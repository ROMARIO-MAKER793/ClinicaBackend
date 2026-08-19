import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Plus, Search, UserPlus, X, LucideAngularModule } from 'lucide-angular';
import { ToastService } from '../../../core/services/toast.service';
import { UsuarioService } from '../../../core/services/usuario.service';
import { AuthService } from '../../../core/services/auth.service';
import { PacienteActivo, RegistroPacienteRequest } from '../../../core/models/api.model';

@Component({
  selector: 'app-pacientes',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, LucideAngularModule],
  templateUrl: './pacientes.component.html'
})
export class PacientesComponent {
  private readonly usuarioService = inject(UsuarioService);
  private readonly authService = inject(AuthService);
  private readonly fb = inject(FormBuilder);
  private readonly toastService = inject(ToastService);

  readonly Plus = Plus;
  readonly Search = Search;
  readonly UserPlus = UserPlus;
  readonly X = X;

  pacientes = signal<PacienteActivo[]>([]);
  cargando = signal(false);
  guardando = signal(false);
  error = signal('');
  modalAbierto = signal(false);

  pacienteForm = this.fb.group({
    dni: ['', Validators.required],
    nombres: ['', Validators.required],
    apellidos: ['', Validators.required],
    correo: ['', [Validators.required, Validators.email]],
    password: ['', Validators.required],
    grupoSanguineo: [''],
    fechaNacimiento: ['', Validators.required]
  });

  ngOnInit(): void {
    this.listarPacientes();
  }

  listarPacientes(): void {
    this.cargando.set(true);
    this.error.set('');

    this.usuarioService.listarPacientesActivos().subscribe({
      next: (data) => {
        this.pacientes.set(data);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar pacientes.');
        this.toastService.error('No se pudo cargar pacientes.');
        this.cargando.set(false);
      }
    });
  }

  abrirModal(): void {
    this.modalAbierto.set(true);
    this.error.set('');
  }

  cerrarModal(): void {
    this.modalAbierto.set(false);
    this.pacienteForm.reset();
    this.guardando.set(false);
  }

  registrarPaciente(): void {
  if (this.pacienteForm.invalid) {
    this.pacienteForm.markAllAsTouched();
    this.error.set('Complete correctamente los datos del paciente.');
    this.toastService.warning('Complete correctamente los datos del paciente.');
    return;
  }

  this.guardando.set(true);
  this.error.set('');

  const request = this.pacienteForm.getRawValue() as RegistroPacienteRequest;

  this.authService.registrarPaciente(request).subscribe({
    next: () => {
      this.guardando.set(false);
      this.toastService.success('Paciente registrado correctamente.');
      this.cerrarModal();
      this.listarPacientes();
    },
    error: () => {
      this.guardando.set(false);
      this.error.set('No se pudo registrar el paciente.');
      this.toastService.error('No se pudo registrar el paciente.');
    }
  });
}
}