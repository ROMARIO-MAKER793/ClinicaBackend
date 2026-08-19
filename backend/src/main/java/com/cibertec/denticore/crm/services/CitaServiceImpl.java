package com.cibertec.denticore.crm.services;

import com.cibertec.denticore.crm.dto.request.ActualizarEstadoDTO;
import com.cibertec.denticore.crm.dto.request.CitaRequestDTO;
import com.cibertec.denticore.crm.dto.response.CitaListadoDTO;
import com.cibertec.denticore.crm.dto.response.CitaResponseDTO;
import com.cibertec.denticore.crm.entities.Cita;
import com.cibertec.denticore.crm.enums.EstadoCita;
import com.cibertec.denticore.crm.repositories.CitaRepository;
import com.cibertec.denticore.security.entities.Odontologo;
import com.cibertec.denticore.security.entities.Paciente;
import com.cibertec.denticore.security.repositories.OdontologoRepository;
import com.cibertec.denticore.security.repositories.PacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class CitaServiceImpl implements CitaService {

    private final CitaRepository citaRepository;
    private final PacienteRepository pacienteRepository;
    private final OdontologoRepository odontologoRepository;

    @Override
    @Transactional
    public CitaResponseDTO programarCita(CitaRequestDTO dto, String dniUsuarioAutenticado) {
        boolean existeColision = citaRepository.existsByOdontologoAndFechaHoraAndEstadoOcupado(
                dto.getIdOdontologo(), dto.getFechaHora());

        if (existeColision) {
            throw new IllegalStateException("El odontólogo ya tiene una cita reservada en este horario.");
        }

        Paciente paciente = obtenerPaciente(dto, dniUsuarioAutenticado);

        Odontologo odontologo = odontologoRepository.findById(dto.getIdOdontologo())
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setOdontologo(odontologo);
        cita.setFechaHora(dto.getFechaHora());
        cita.setEstado(EstadoCita.PENDIENTE);
        cita.setCanalOrigen(dto.getCanalOrigen());
        cita.setMontoAdelanto(dto.getMontoAdelanto() != null ? dto.getMontoAdelanto() : BigDecimal.ZERO);
        cita.setReferenciaAdelanto(dto.getReferenciaAdelanto());

        Cita guardada = citaRepository.save(cita);

        return mapearRespuesta(guardada, "Cita programada correctamente");
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CitaListadoDTO> listarAgendaDiaria(LocalDate fecha, Pageable pageable) {
        LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
        LocalDateTime inicio = fechaConsulta.atStartOfDay();
        LocalDateTime fin = fechaConsulta.atTime(LocalTime.MAX);

        Page<Cita> citas = citaRepository.findByFechaHoraBetweenOrderByFechaHoraAsc(inicio, fin, pageable);

        return citas.map(this::mapearCitaListado);
    }

    @Override
    @Transactional
    public CitaResponseDTO actualizarEstado(Integer idCita, ActualizarEstadoDTO dto) {
        Cita cita = citaRepository.findById(idCita)
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        cita.setEstado(dto.getEstado());

        Cita actualizada = citaRepository.save(cita);

        return mapearRespuesta(actualizada, "Estado de cita actualizado");
    }

    private Paciente obtenerPaciente(CitaRequestDTO dto, String dniUsuarioAutenticado) {
        if (dto.getIdPaciente() != null) {
            return pacienteRepository.findById(dto.getIdPaciente())
                    .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));
        }

        return pacienteRepository.findByUsuarioDni(dniUsuarioAutenticado)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado para el usuario autenticado"));
    }

    private CitaListadoDTO mapearCitaListado(Cita cita) {
        CitaListadoDTO dto = new CitaListadoDTO();
        dto.setIdCita(cita.getId());
        dto.setFechaHora(cita.getFechaHora());
        dto.setIdPaciente(cita.getPaciente().getUsuario().getId());
        dto.setPacienteNombreCompleto(
                cita.getPaciente().getUsuario().getNombres() + " " + cita.getPaciente().getUsuario().getApellidos());
        dto.setPacienteDni(cita.getPaciente().getUsuario().getDni());
        dto.setIdOdontologo(cita.getOdontologo().getUsuario().getId());
        dto.setOdontologoNombre(
                cita.getOdontologo().getUsuario().getNombres() + " " + cita.getOdontologo().getUsuario().getApellidos());
        dto.setEstado(cita.getEstado().name());
        dto.setMontoAdelanto(cita.getMontoAdelanto());
        return dto;
    }

    private CitaResponseDTO mapearRespuesta(Cita cita, String mensaje) {
        CitaResponseDTO dto = new CitaResponseDTO();
        dto.setId(cita.getId());
        dto.setEstado(cita.getEstado().name());
        dto.setFechaHora(cita.getFechaHora());
        dto.setMensaje(mensaje);
        return dto;
    }

    @Override
@Transactional(readOnly = true)
public Page<CitaListadoDTO> listarMisCitas(
        String dniUsuarioAutenticado,
        LocalDate fecha,
        Pageable pageable) {

    LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
    LocalDateTime inicio = fechaConsulta.atStartOfDay();
    LocalDateTime fin = fechaConsulta.atTime(LocalTime.MAX);

    Odontologo odontologo = odontologoRepository
            .findByUsuarioDni(dniUsuarioAutenticado)
            .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

     return citaRepository
            .findAgendaDiariaByOdontologo(
                    odontologo.getIdUsuario(),
                    inicio,
                    fin,
                    pageable)
            .map(this::mapearCitaListado);
    }


    @Override
@Transactional(readOnly = true)
public Page<CitaListadoDTO> listarMisCitasPaciente(
        String dniUsuarioAutenticado,
        Pageable pageable) {

    Paciente paciente = pacienteRepository
            .findByUsuarioDni(dniUsuarioAutenticado)
            .orElseThrow(() ->
                    new RuntimeException("Paciente no encontrado"));

    return citaRepository
            .findByPaciente(
                    paciente.getIdUsuario(),
                    pageable)
            .map(this::mapearCitaListado);
}
}
