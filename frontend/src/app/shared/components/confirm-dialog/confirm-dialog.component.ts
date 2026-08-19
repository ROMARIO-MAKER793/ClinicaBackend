import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { ConfirmService } from '../../../core/services/confirm.service';

@Component({
  selector: 'app-confirm-dialog',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './confirm-dialog.component.html'
})
export class ConfirmDialogComponent {
  readonly confirmService = inject(ConfirmService);

  claseBoton(tipo?: string): string {
    if (tipo === 'danger') {
      return 'bg-status-error text-white hover:bg-red-700';
    }

    return 'bg-primary text-white hover:bg-primary-hover';
  }
}