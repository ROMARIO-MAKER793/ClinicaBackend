import { CommonModule } from '@angular/common';
import { Component, signal } from '@angular/core';

type VentaSimulada = {
  idCita: number;
  idPaciente: number;
  paciente: string;
  dni: string;
  metodoPago: string;
  montoPagado: number;
  total: number;
  saldo: number;
  fecha: string;
};

@Component({
  selector: 'app-ventas',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './ventas.component.html'
})
export class VentasComponent {
  ventas = signal<VentaSimulada[]>(this.cargarVentas());

  cargarVentas(): VentaSimulada[] {
    return JSON.parse(localStorage.getItem('ventas_simuladas') ?? '[]');
  }

  totalVendido(): number {
    return this.ventas().reduce((acc, venta) => acc + venta.total, 0);
  }

  refrescar(): void {
    this.ventas.set(this.cargarVentas());
  }
}