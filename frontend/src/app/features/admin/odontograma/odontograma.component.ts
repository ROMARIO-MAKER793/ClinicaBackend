import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ConfirmService } from '../../../core/services/confirm.service';
import { ToastService } from '../../../core/services/toast.service';
import { ToothSvgComponent, SeleccionDental } from './components/tooth-svg/tooth-svg.component';
import { OdontogramaService } from '../../../core/services/odontograma.sevice';
import {
  DetalleHistorial,
  ElementoOdontograma,
  RegistrarAtencionRequest
} from '../../../core/models/api.model';

@Component({
  selector: 'app-odontograma',
  standalone: true,
  imports: [CommonModule, FormsModule, ToothSvgComponent],
  templateUrl: './odontograma.component.html'
})
export class OdontogramaComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly odontogramaService = inject(OdontogramaService);
  private readonly confirmService = inject(ConfirmService);
  private readonly toastService = inject(ToastService);

  idCita = Number(
    this.route.snapshot.paramMap.get('idCita') ??
    this.route.snapshot.paramMap.get('id')
  );

  idPaciente = Number(this.route.snapshot.queryParamMap.get('idPaciente'));
  paciente = this.route.snapshot.queryParamMap.get('paciente') ?? 'Paciente no identificado';
  dni = this.route.snapshot.queryParamMap.get('dni') ?? '-';

  historial = signal<DetalleHistorial[]>([]);
  elementos = signal<ElementoOdontograma[]>([]);

  cargando = signal(false);
  guardando = signal(false);
  error = signal('');
  exito = signal('');

  piezaSeleccionada = signal<number | null>(null);
  superficieSeleccionada = signal<string | null>(null);

  piezasSuperioresDerecha = [18, 17, 16, 15, 14, 13, 12, 11];
  piezasSuperioresIzquierda = [21, 22, 23, 24, 25, 26, 27, 28];
  piezasInferioresDerecha = [48, 47, 46, 45, 44, 43, 42, 41];
  piezasInferioresIzquierda = [31, 32, 33, 34, 35, 36, 37, 38];

  request: RegistrarAtencionRequest = {
    idCita: this.idCita,
    motivoConsulta: '',
    notasClinicas: '',
    tipoOdontograma: 'Inicial',
    detalles: []
  };

  ngOnInit(): void {
    this.cargarHistorial();
    this.cargarElementos();
  }

  cargarHistorial(): void {
    if (!this.idPaciente) {
      return;
    }

    this.cargando.set(true);

    this.odontogramaService.obtenerHistorialPaciente(this.idPaciente).subscribe({
      next: (data) => {
        this.historial.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar el historial clínico.');
        this.cargando.set(false);
      }
    });
  }

  cargarElementos(): void {
    this.odontogramaService.listarElementosActivos().subscribe({
      next: (data) => this.elementos.set(data),
      error: () => this.error.set('No se pudo cargar los elementos clínicos.')
    });
  }

    seleccionarDental(data: SeleccionDental): void {
      this.piezaSeleccionada.set(data.pieza);
      this.superficieSeleccionada.set(data.superficie);

      const primerElemento = this.elementosPorTipo()[0];
      this.elementoSeleccionadoId.set(primerElemento?.id ?? null);
    }

  agregarDetalle(): void {
    const primerElemento = this.elementos()[0];

    this.request.detalles.push({
      idElementoClinico: primerElemento?.id ?? 0,
      numeroPieza: this.piezaSeleccionada() ?? 0,
      diagnostico: this.superficieSeleccionada()
        ? `[${this.superficieSeleccionada()}] `
        : '',
      estadoTratamiento: 'Pendiente'
    });
  }

  eliminarDetalle(index: number): void {
    this.request.detalles.splice(index, 1);
  }

      guardarAtencion(): void {
      this.error.set('');
      this.exito.set('');

      if (!this.request.motivoConsulta.trim()) {
        this.error.set('Ingresa el motivo de consulta.');
        this.toastService.warning('Ingresa el motivo de consulta.');
        return;
      }

      if (this.request.detalles.length === 0) {
        this.error.set('Agrega al menos un detalle clínico.');
        this.toastService.warning('Agrega al menos un detalle clínico.');
        return;
      }

      const detalleInvalido = this.request.detalles.some(
        detalle =>
          !detalle.idElementoClinico ||
          !detalle.numeroPieza ||
          !detalle.diagnostico.trim()
      );

      if (detalleInvalido) {
        this.error.set('Revisa los detalles clínicos antes de guardar.');
        this.toastService.warning('Revisa los detalles clínicos antes de guardar.');
        return;
      }

      this.confirmService.abrir({
        titulo: 'Guardar atención',
        mensaje: '¿Seguro que deseas registrar esta atención clínica?',
        textoConfirmar: 'Sí, guardar',
        textoCancelar: 'Cancelar',
        tipo: 'info',
        onConfirmar: () => this.ejecutarGuardarAtencion()
      });
    }

    private ejecutarGuardarAtencion(): void {
      this.guardando.set(true);

      this.odontogramaService.registrarAtencion(this.request).subscribe({
        next: () => {
          this.guardando.set(false);
          this.toastService.success('Atención clínica registrada correctamente.');
          this.router.navigate(['/admin/citas']);
        },
        error: () => {
          this.guardando.set(false);
          this.error.set('No se pudo guardar la atención. Verifica que la cita esté EN_CURSO.');
          this.toastService.error('No se pudo guardar la atención clínica.');
        }
      });
    }

  volver(): void {
    this.router.navigate(['/admin/citas']);
  }

   //Odontograma
    tipoRegistro = signal<'Diagnostico' | 'Tratamiento'>('Diagnostico');
  elementoSeleccionadoId = signal<number | null>(null);
  observacionDetalle = signal('');
  estadoDetalle = signal('Pendiente');

elementosPorTipo(): ElementoOdontograma[] {
  return this.elementos().filter(
    elemento => elemento.categoria === this.tipoRegistro()
  );
}

nombreElemento(idElemento: number): string {
  return this.elementos().find(elemento => elemento.id === idElemento)?.nombre ?? 'Elemento no encontrado';
}

colorElemento(idElemento: number): string {
  return this.elementos().find(elemento => elemento.id === idElemento)?.colorHex ?? '#64748b';
}

agregarDetalleDesdePanel(): void {
  this.error.set('');

  if (!this.piezaSeleccionada() || !this.superficieSeleccionada()) {
    this.error.set('Selecciona una pieza y una superficie dental.');
    return;
  }

  if (!this.elementoSeleccionadoId()) {
    this.error.set('Selecciona un elemento clínico.');
    return;
  }

  if (!this.observacionDetalle().trim()) {
    this.error.set('Ingresa una observación clínica.');
    return;
  }

  this.request.detalles.push({
    idElementoClinico: this.elementoSeleccionadoId()!,
    numeroPieza: this.piezaSeleccionada()!,
    diagnostico: `[${this.superficieSeleccionada()}] ${this.observacionDetalle().trim()}`,
    estadoTratamiento: this.estadoDetalle()
  });

  this.observacionDetalle.set('');
  this.estadoDetalle.set('Pendiente');
  }
}