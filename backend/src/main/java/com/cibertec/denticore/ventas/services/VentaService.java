package com.cibertec.denticore.ventas.services;

import com.cibertec.denticore.ventas.dto.response.PrevisualizacionVentaDTO;
import com.cibertec.denticore.ventas.dto.request.ProcesarVentaRequestDTO;
import com.cibertec.denticore.ventas.dto.response.VentaListadoDTO;
import java.util.List;

public interface VentaService {
    void procesarVenta(ProcesarVentaRequestDTO dto, String dniAutenticado);

    List<VentaListadoDTO> listarVentas();

    PrevisualizacionVentaDTO obtenerPrevisualizacion(Integer idCita);
}
