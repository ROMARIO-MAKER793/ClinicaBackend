import { Injectable, signal } from '@angular/core';

export type ToastTipo = 'success' | 'error' | 'warning' | 'info';

export interface ToastMessage {
  mensaje: string;
  tipo: ToastTipo;
}

@Injectable({
  providedIn: 'root'
})
export class ToastService {
  toast = signal<ToastMessage | null>(null);

  mostrar(mensaje: string, tipo: ToastTipo = 'info'): void {
    this.toast.set({ mensaje, tipo });

    setTimeout(() => {
      this.toast.set(null);
    }, 3000);
  }

  success(mensaje: string): void {
    this.mostrar(mensaje, 'success');
  }

  error(mensaje: string): void {
    this.mostrar(mensaje, 'error');
  }

  warning(mensaje: string): void {
    this.mostrar(mensaje, 'warning');
  }

  info(mensaje: string): void {
    this.mostrar(mensaje, 'info');
  }

  cerrar(): void {
    this.toast.set(null);
  }
}