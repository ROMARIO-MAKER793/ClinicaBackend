import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environments';
import { ProcesarVentaRequest , VentaListado , PrevisualizacionVenta} from '../models/api.model';


@Injectable({
  providedIn: 'root'
})
export class VentaService {

  private readonly apiUrl = `${environment.apiBaseUrl}/ventas`;

  constructor(private readonly http: HttpClient) {}

  procesar(request: ProcesarVentaRequest): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/procesar`, request);
  }

  listarVentas():Observable<VentaListado[]>{
    return this.http.get<VentaListado[]>(this.apiUrl);
  }

  obtenerPrevisualizacion(idCita: number): Observable<PrevisualizacionVenta> {
      return this.http.get<PrevisualizacionVenta>(
       `${this.apiUrl}/previsualizacion/${idCita}`
    );
  }
}