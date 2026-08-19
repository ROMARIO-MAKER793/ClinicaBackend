import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Calendar, Clock, Form, LucideAngularModule, RefreshCcw, Search, UserCheck } from 'lucide-angular';
import { AuthService } from '../../../core/services/auth.service';
import { CitaService } from '../../../core/services/cita.service';
import { Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { UsuarioService } from '../../../core/services/usuario.service';
import { AgendarCitaRequest,CitaListadoDTO,EstadoCita,OdontologoActivo,PacienteActivo } from '../../../core/models/api.model';
import { ToastService } from '../../../core/services/toast.service';
import { ConfirmService } from '../../../core/services/confirm.service';


@Component({
  selector: 'app-citas',
  standalone: true,
  imports: [CommonModule, LucideAngularModule,FormsModule],
  templateUrl: './citas.component.html'
})
export class CitasComponent {

    private readonly toastService = inject(ToastService);
    private readonly router = inject(Router);
    private readonly authService = inject(AuthService);
    rolActual = this.authService.rol;   

    private readonly citaService = inject(CitaService);
    private readonly confirmService = inject(ConfirmService);


    readonly Calendar = Calendar;
    readonly Clock = Clock;
    readonly UserCheck = UserCheck;
    readonly RefreshCcw = RefreshCcw;
    readonly Search = Search;

    private readonly usuarioService = inject(UsuarioService);

    pacientes = signal<PacienteActivo[]>([]);
    odontologos = signal<OdontologoActivo[]>([]);
    modalNuevaCita = signal(false);

    nuevaCita: AgendarCitaRequest = {
      idPaciente: 0,
      idOdontologo: 0,
      fechaHora: '',
      canalOrigen: 'RECEPCION',
      montoAdelanto: 0,
      referenciaAdelanto: ''
    };



    citas = signal<CitaListadoDTO[]>([]);
    cargando = signal(false);
    error = signal('');

    ngOnInit(): void {
      this.listarCitas();
      this.cargarCombos();
    }

  listarCitas(): void {
    this.cargando.set(true);
    this.error.set('');

    const request$ = this.rolActual() === 'ODONTOLOGO'
    ? this.citaService.listarMisCitas(undefined, 0, 50)
    : this.citaService.listarAgendaDiaria(undefined, 0, 50);

    request$.subscribe({
      next: (response) => {
        const citasFiltradas = this.rolActual() === 'ODONTOLOGO'
        ? response.content.filter(cita => cita.estado === 'EN_SALA' ||cita.estado === 'EN_CURSO')
        : response.content;

        this.citas.set(citasFiltradas);
        this.cargando.set(false);
      },
      error: () => {
        this.error.set('No se pudo cargar la agenda de citas.');
        this.cargando.set(false);
      }
    });
  }

  

  atenderCita(cita: CitaListadoDTO): void {
  if (this.hayCitaEnCurso()) {
    this.toastService.warning('Ya existe una atención en curso. Finaliza el odontograma antes de atender otro paciente.');
    return;
  }

  this.confirmService.abrir({
    titulo: 'Iniciar atención',
    mensaje: `¿Deseas iniciar la atención de ${cita.pacienteNombreCompleto}?`,
    textoConfirmar: 'Sí, atender',
    textoCancelar: 'Cancelar',
    tipo: 'info',
    onConfirmar: () => {
      this.citaService.actualizarEstado(cita.idCita, 'EN_CURSO').subscribe({
        next: () => {
          this.toastService.success('Atención iniciada correctamente.');
          this.listarCitas();
        },
        error: () => {
          this.toastService.error('No se pudo iniciar la atención.');
          this.error.set('No se pudo iniciar la atención.');
        }
      });
    }
  });
}

  cambiarEstado(idCita: number, estado: EstadoCita): void {
  this.confirmService.abrir({
    titulo: 'Cambiar estado',
    mensaje: `¿Deseas cambiar la cita a "${this.textoEstado(estado)}"?`,
    textoConfirmar: 'Sí, cambiar',
    textoCancelar: 'Cancelar',
    tipo: 'warning',
    onConfirmar: () => {
      this.citaService.actualizarEstado(idCita, estado).subscribe({
        next: () => {
          this.toastService.success('Estado actualizado correctamente.');
          this.listarCitas();
        },
        error: () => {
          this.toastService.error('No se pudo actualizar el estado.');
          this.error.set('No se pudo actualizar el estado de la cita.');
        }
      });
    }
  });
}

  totalPorEstado(estado: EstadoCita): number {
    return this.citas().filter(cita => cita.estado === estado).length;
  }

  claseEstado(estado: EstadoCita): string {
  const clases: Record<EstadoCita, string> = {
    PENDIENTE: 'bg-orange-50 text-status-warning border-orange-200',
    CONFIRMADA: 'bg-cyan-50 text-primary border-cyan-200',
    EN_SALA: 'bg-amber-50 text-status-warning border-amber-200',
    EN_CURSO: 'bg-purple-50 text-purple-600 border-purple-200',
    ATENDIDA: 'bg-green-50 text-status-success border-green-200',
    FINALIZADA: 'bg-slate-100 text-slate-600 border-slate-200',
    CANCELADA: 'bg-red-50 text-status-error border-red-200'
  };

  return clases[estado];
}

    textoEstado(estado: EstadoCita): string {
      const textos: Record<EstadoCita, string> = {
        PENDIENTE: 'Pendiente',
        CONFIRMADA: 'Confirmada',
        EN_SALA: 'En sala',
        EN_CURSO: 'En curso',
        ATENDIDA: 'Atendida',
        FINALIZADA: 'Finalizada',
        CANCELADA: 'Cancelada'
      };

      return textos[estado];
    } 


    cargarCombos(): void {
  this.usuarioService.listarPacientesActivos().subscribe({
    next: (data) => this.pacientes.set(data),
    error: () => this.error.set('No se pudo cargar pacientes.')
  });

  this.usuarioService.listarOdontologosActivos().subscribe({
    next: (data) => this.odontologos.set(data),
    error: () => this.error.set('No se pudo cargar odontólogos.')
  });
}

abrirModalNuevaCita(): void {
  this.modalNuevaCita.set(true);
}

cerrarModalNuevaCita(): void {
  this.modalNuevaCita.set(false);
}

registrarCita(): void {
  if (!this.nuevaCita.idPaciente || !this.nuevaCita.idOdontologo || !this.nuevaCita.fechaHora) {
    this.error.set('Completa paciente, odontólogo y fecha/hora.');
    return;
  }

  this.citaService.agendar(this.nuevaCita).subscribe({
    next: () => {

      this.toastService.success('Cita registrada correctamente');
      this.cerrarModalNuevaCita();
      this.nuevaCita = {
        idPaciente: 0,
        idOdontologo: 0,
        fechaHora: '',
        canalOrigen: 'RECEPCION',
        montoAdelanto: 0,
        referenciaAdelanto: ''
      };
      this.listarCitas();
    },
    error: () => this.toastService.error('No se pudo registrar la cita.')
  });
  }

  irOdontograma(cita: CitaListadoDTO): void {
  this.router.navigate(['/admin/odontograma', cita.idCita], {
    queryParams: {
      idPaciente: cita.idPaciente,
      paciente: cita.pacienteNombreCompleto,
      dni: cita.pacienteDni
    }
  });
}

    irFacturacion(cita: CitaListadoDTO): void {
  this.router.navigate(['/admin/facturacion', cita.idCita], {
    queryParams: {
      idPaciente: cita.idPaciente,
      paciente: cita.pacienteNombreCompleto,
      dni: cita.pacienteDni
    }
  });
} 

  hayCitaEnCurso(): boolean {
  return this.citas().some(cita => cita.estado === 'EN_CURSO');
}

}