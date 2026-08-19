package com.cibertec.denticore.clinica.services;

import com.cibertec.denticore.clinica.dto.request.GuardarOdontogramaRequestDTO;
import com.cibertec.denticore.clinica.dto.response.DetalleHistorialDTO;
import com.cibertec.denticore.clinica.dto.response.ElementoOdontogramaDTO;

import java.util.List;

public interface OdontogramaService {

    void registrarAtencionClinica(GuardarOdontogramaRequestDTO dto, String dniAutenticado);

    List<DetalleHistorialDTO> obtenerHistorialPaciente(Integer idPaciente);

    List<ElementoOdontogramaDTO> listarElementosActivos();
}
