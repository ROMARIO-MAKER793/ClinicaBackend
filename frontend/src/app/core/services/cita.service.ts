import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environments';
import {
  ActualizarEstadoDTO,
  AgendarCitaRequest,
  CitaListadoDTO,
  CitaResponseDTO,
  EstadoCita,
  PageResponse
} from '../models/api.model';

@Injectable({
  providedIn: 'root'
})
export class CitaService {
  private readonly apiUrl = `${environment.apiBaseUrl}/citas`;

  constructor(private readonly http: HttpClient) {}

  agendar(request: AgendarCitaRequest): Observable<CitaResponseDTO> {
    return this.http.post<CitaResponseDTO>(this.apiUrl, request);
  }

  listarAgendaDiaria(fecha?: string, page = 0, size = 20): Observable<PageResponse<CitaListadoDTO>> {
    let params = new HttpParams()
      .set('page', page)
      .set('size', size);

    if (fecha) {
      params = params.set('fecha', fecha);
    }

    return this.http.get<PageResponse<CitaListadoDTO>>(`${this.apiUrl}/agenda-diaria`, { params });
  }

  actualizarEstado(idCita: number, estado: EstadoCita): Observable<CitaResponseDTO> {
    const request: ActualizarEstadoDTO = { estado };

    return this.http.put<CitaResponseDTO>(`${this.apiUrl}/${idCita}/estado`, request);
  }

  listarMisCitas(fecha?: string, page = 0, size = 20): Observable<PageResponse<CitaListadoDTO>> {
  let params = new HttpParams()
    .set('page', page)
    .set('size', size);

  if (fecha) {
    params = params.set('fecha', fecha);
  }

  return this.http.get<PageResponse<CitaListadoDTO>>(`${this.apiUrl}/mis-citas`, { params });
 }
}