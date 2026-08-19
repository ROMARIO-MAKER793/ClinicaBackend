import { Injectable, signal } from '@angular/core';

export interface ConfirmData {
  titulo: string;
  mensaje: string;
  textoConfirmar?: string;
  textoCancelar?: string;
  tipo?: 'warning' | 'danger' | 'info';
  onConfirmar: () => void;
}

@Injectable({
  providedIn: 'root'
})
export class ConfirmService {
  confirmacion = signal<ConfirmData | null>(null);

  abrir(data: ConfirmData): void {
    this.confirmacion.set({
      textoConfirmar: 'Confirmar',
      textoCancelar: 'Cancelar',
      tipo: 'warning',
      ...data
    });
  }

  cerrar(): void {
    this.confirmacion.set(null);
  }

  confirmar(): void {
    const data = this.confirmacion();

    if (!data) return;

    data.onConfirmar();
    this.cerrar();
  }
}