package com.cibertec.denticore.crm.services;

import com.cibertec.denticore.crm.dto.request.ActualizarEstadoDTO;
import com.cibertec.denticore.crm.dto.request.CitaRequestDTO;
import com.cibertec.denticore.crm.dto.response.CitaListadoDTO;
import com.cibertec.denticore.crm.dto.response.CitaResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

public interface CitaService {

    CitaResponseDTO programarCita(CitaRequestDTO dto, String dniUsuarioAutenticado);

    Page<CitaListadoDTO> listarAgendaDiaria(LocalDate fecha, Pageable pageable);

    CitaResponseDTO actualizarEstado(Integer idCita, ActualizarEstadoDTO dto);

    Page<CitaListadoDTO> listarMisCitas(
        String dniUsuarioAutenticado,
        LocalDate fecha,
        Pageable pageable);

    Page<CitaListadoDTO> listarMisCitasPaciente(
        String dniUsuarioAutenticado,
        Pageable pageable);

    
}
