import { Routes } from '@angular/router';
import { authGuard } from './core/auth/auth.guard';
import { roleGuard } from './core/auth/role.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/landing/landing.component').then(m => m.LandingComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./features/auth/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'portal',
    canActivate: [authGuard, roleGuard],
    data: { rolesPermitidos: ['PACIENTE'] },
    loadComponent: () => import('./features/portal/portal-layout.component').then(m => m.PortalLayoutComponent),
    children: [
      { path: 'citas', loadComponent: () => import('./features/portal/citas/citas.component').then(m => m.CitasComponent) },
      { path: 'historial', loadComponent: () => import('./features/portal/historial/historial.component').then(m => m.HistorialComponent) },
      { path: '', redirectTo: 'citas', pathMatch: 'full' }
    ]
  },
  {
    path: 'admin',
    canActivate: [authGuard, roleGuard],
    data: { rolesPermitidos: ['ADMIN', 'ODONTOLOGO'] },
    loadComponent: () => import('./features/admin/admin-layout.component').then(m => m.AdminLayoutComponent),
    children: [
      { path: 'dashboard', loadComponent: () => import('./features/admin/dashboard/dashboard.component').then(m => m.DashboardComponent) },
      {
        path: 'citas',
        loadComponent: () => import('./features/portal/citas/citas.component').then(m => m.CitasComponent)
      },
      { path: 'pacientes', loadComponent: () => import('./features/admin/pacientes/pacientes.component').then(m => m.PacientesComponent) },
      { path: 'odontograma/:idCita', loadComponent: () => import('./features/admin/odontograma/odontograma.component').then(m => m.OdontogramaComponent) },
      { path: 'facturacion/:idCita', loadComponent: () => import('./features/admin/facturacion/facturacion.component').then(m => m.FacturacionComponent) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' },
      {
        path: 'ventas',
        loadComponent: () => import('./features/admin/ventas/ventas.component').then(m => m.VentasComponent)
      },
    ]
  },
  {
    path: '**',
    redirectTo: ''
  }
];