import { Component , OnInit} from '@angular/core';
import { inject } from '@angular/core';
import { Router , RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { ToastComponent } from '../../shared/components/toast/toast.component';
import { ConfirmDialogComponent } from '../../shared/components/confirm-dialog/confirm-dialog.component';
import { ConfirmService } from '../../core/services/confirm.service';
import { ToastService } from '../../core/services/toast.service';

import {
  BarChart3,
  Calendar,
  DollarSign,
  LayoutDashboard,
  LogOut,
  LucideAngularModule,
  Plus,
  Search,
  Settings,
  Sparkles,
  Stethoscope,
  UserCog,
  Users
} from 'lucide-angular';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, LucideAngularModule, ToastComponent, ConfirmDialogComponent],
  templateUrl: './admin-layout.component.html'
})
export class AdminLayoutComponent  implements OnInit{
  readonly Sparkles = Sparkles;
  readonly LayoutDashboard = LayoutDashboard;
  readonly Users = Users;
  readonly Calendar = Calendar;
  readonly Stethoscope = Stethoscope;
  readonly DollarSign = DollarSign;
  readonly UserCog = UserCog;
  readonly BarChart3 = BarChart3;
  readonly Settings = Settings;
  readonly LogOut = LogOut;
  readonly Search = Search;
  readonly Plus = Plus;

  ngOnInit(): void {
  if (this.esOdontologo() && this.router.url === '/admin/dashboard') {
    this.router.navigate(['/admin/citas']);
  }
}

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  private readonly confirmService = inject(ConfirmService);
  private readonly toastService = inject(ToastService);

  cerrarSesion(): void {
  this.confirmService.abrir({
    titulo: 'Cerrar sesión',
    mensaje: '¿Seguro que deseas cerrar tu sesión?',
    textoConfirmar: 'Sí, cerrar',
    textoCancelar: 'Cancelar',
    tipo: 'danger',
    onConfirmar: () => this.ejecutarCerrarSesion()
  });
}

private ejecutarCerrarSesion(): void {
  this.authService.logoutBackend().subscribe({
    next: () => {
      this.authService.cerrarSesion();
      this.toastService.success('Sesión cerrada correctamente.');
      this.router.navigate(['/login']);
    },
    error: () => {
      this.authService.cerrarSesion();
      this.toastService.warning('Sesión local cerrada.');
      this.router.navigate(['/login']);
    }
  });
}   

    

        rolActual = this.authService.rol;

        esAdmin(): boolean {
          return this.rolActual() === 'ADMIN';
        }

        esOdontologo(): boolean {
          return this.rolActual() === 'ODONTOLOGO';
        }




}