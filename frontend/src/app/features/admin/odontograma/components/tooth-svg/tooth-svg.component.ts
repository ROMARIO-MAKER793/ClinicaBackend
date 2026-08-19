import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output, signal } from '@angular/core';

export type SuperficieDental = 'Oclusal' | 'Vestibular' | 'Lingual' | 'Mesial' | 'Distal';

export interface SeleccionDental {
  pieza: number;
  superficie: SuperficieDental;
}

@Component({
  selector: 'app-tooth-svg',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tooth-svg.component.html'
})
export class ToothSvgComponent {
  @Input({ required: true }) pieza!: number;
  @Input() seleccionado = false;
  @Input() superficiesMarcadas: Partial<Record<SuperficieDental, string>> = {};

  @Output() seleccionar = new EventEmitter<SeleccionDental>();

  superficieHover = signal<SuperficieDental | null>(null);

  seleccionarSuperficie(superficie: SuperficieDental): void {
    this.seleccionar.emit({
      pieza: this.pieza,
      superficie
    });
  }

  colorSuperficie(superficie: SuperficieDental): string {
  if (this.superficiesMarcadas[superficie]) {
    return this.superficiesMarcadas[superficie]!;
  }

  return '#fcfaf5';
}

bordeSuperficie(superficie: SuperficieDental): string {
  if (this.superficieHover() === superficie || this.seleccionado) {
    return '#2563eb';
  }

  return '#eadecc';
}
}