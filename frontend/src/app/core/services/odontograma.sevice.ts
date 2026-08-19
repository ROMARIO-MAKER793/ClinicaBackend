import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ElementoOdontograma } from '../models/api.model';
import { environment } from '../../../environments/environments';
import { DetalleHistorial, RegistrarAtencionRequest } from '../models/api.model';

@Injectable({
  providedIn: 'root'
})
export class OdontogramaService {
  private readonly apiUrl = `${environment.apiBaseUrl}/clinica/odontograma`;

  constructor(private readonly http: HttpClient) {}

  registrarAtencion(request: RegistrarAtencionRequest): Observable<void> {
    return this.http.post<void>(this.apiUrl, request);
  }

  obtenerHistorialPaciente(idPaciente: number): Observable<DetalleHistorial[]> {
    return this.http.get<DetalleHistorial[]>(`${this.apiUrl}/historial/${idPaciente}`);
  }

  listarElementosActivos(): Observable<ElementoOdontograma[]> {
  return this.http.get<ElementoOdontograma[]>(`${this.apiUrl}/elementos`);
  }
}