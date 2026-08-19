import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ToastService, ToastTipo } from '../../../core/services/toast.service';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.component.html'
})
export class ToastComponent {
  readonly toastService = inject(ToastService);

  obtenerClase(tipo: ToastTipo): string {
    const clases: Record<ToastTipo, string> = {
      success: 'bg-green-600 text-white',
      error: 'bg-red-600 text-white',
      warning: 'bg-amber-500 text-white',
      info: 'bg-primary text-white'
    };

    return clases[tipo];
  }
}