import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ItemPrevisualizacionVenta, ProcesarVentaRequest } from '../../../core/models/api.model';
import { VentaService } from '../../../core/services/venta.service';
import { ToastService } from '../../../core/services/toast.service';
import { ConfirmService } from '../../../core/services/confirm.service';
import { ItemCarrito } from '../../../core/models/api.model';

@Component({
  selector: 'app-facturacion',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './facturacion.component.html'
})
export class FacturacionComponent {

  private readonly ventaService = inject(VentaService);
  private readonly toastService = inject(ToastService);
  private readonly confirmService = inject(ConfirmService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  idCita = Number(
    this.route.snapshot.paramMap.get('idCita') ??
    this.route.snapshot.paramMap.get('id')
  );

  idPaciente = Number(this.route.snapshot.queryParamMap.get('idPaciente'));
  paciente = this.route.snapshot.queryParamMap.get('paciente') ?? 'Paciente no identificado';
  dni = this.route.snapshot.queryParamMap.get('dni') ?? '-';

  metodoPago = 'Efectivo';
  montoPagado = 0;
  procesando = signal(false);
  error = signal('');

  itemsCarrito = signal<ItemPrevisualizacionVenta[]>([]);

  total(): number {
    return this.itemsCarrito().reduce(
      (acc, item) => acc + item.cantidad * item.precioAplicado,
      0
    );
  }

    ngOnInit(): void {
    this.cargarPrevisualizacion();
  }

  cargarPrevisualizacion(): void {
    this.error.set('');

    this.ventaService.obtenerPrevisualizacion(this.idCita).subscribe({
      next: (data) => {
        this.idPaciente = data.idPaciente;
        this.paciente = data.paciente;
        this.dni = data.dni;
        this.itemsCarrito.set(data.items);
        this.montoPagado = data.total;
      },
      error: () => {
        this.error.set('No se pudo cargar la previsualización de venta.');
        this.toastService.error('No se pudo cargar la previsualización de venta.');
      }
    });
  }

  saldo(): number {
    return Math.max(this.total() - Number(this.montoPagado || 0), 0);
  }

  registrarPago(): void {
    this.error.set('');

    if (this.itemsCarrito().length === 0) {
      this.error.set('No hay ítems para facturar.');
      this.toastService.warning('No hay ítems para facturar.');
      return;
    }

    if (!this.montoPagado || Number(this.montoPagado) <= 0) {
      this.error.set('Ingresa un monto pagado válido.');
      this.toastService.warning('Ingresa un monto pagado válido.');
      return;
    }

    this.confirmService.abrir({
      titulo: 'Registrar pago',
      mensaje: '¿Seguro que deseas registrar este pago?',
      textoConfirmar: 'Sí, registrar',
      textoCancelar: 'Cancelar',
      tipo: 'info',
      onConfirmar: () => this.ejecutarRegistrarPago()
    });
  }

  private ejecutarRegistrarPago(): void {
    this.procesando.set(true);

    this.ventaService.procesar({
      idPaciente: this.idPaciente,
      idCita: this.idCita,
      tipoDocumento: 'ReciboInterno',
      metodoPago: this.metodoPago,
      montoPagado: Number(this.montoPagado),
      itemsCarrito: this.itemsCarrito().map(item =>({
        idItemCatalogo:item.idItemCatalogo,
        cantidad:item.cantidad,
        precioAplicado:item.precioAplicado,
        numeroPieza:item.numeroPieza
      }))
    }).subscribe({
      next: () => {
        this.procesando.set(false);
        this.toastService.success('Pago registrado correctamente.');
        this.router.navigate(['/admin/citas']);
      },
      error: () => {
        this.procesando.set(false);
        this.error.set('No se pudo registrar el pago.');
        this.toastService.error('No se pudo registrar el pago.');
      }
    });
  }

  volver(): void {
    this.router.navigate(['/admin/citas']);
  }
}