package com.cibertec.denticore.clinica.services;

import com.cibertec.denticore.clinica.dto.request.DetalleOdontogramaDTO;
import com.cibertec.denticore.clinica.dto.request.GuardarOdontogramaRequestDTO;
import com.cibertec.denticore.clinica.dto.response.DetalleHistorialDTO;
import com.cibertec.denticore.clinica.dto.response.ElementoOdontogramaDTO;
import com.cibertec.denticore.clinica.entities.AtencionClinica;
import com.cibertec.denticore.clinica.entities.DetalleOdontograma;
import com.cibertec.denticore.clinica.entities.ElementoOdontograma;
import com.cibertec.denticore.clinica.entities.HistoriaClinica;
import com.cibertec.denticore.clinica.entities.Odontograma;
import com.cibertec.denticore.clinica.repositories.AtencionClinicaRepository;
import com.cibertec.denticore.clinica.repositories.DetalleOdontogramaRepository;
import com.cibertec.denticore.clinica.repositories.ElementoOdontogramaRepository;
import com.cibertec.denticore.clinica.repositories.OdontogramaRepository;
import com.cibertec.denticore.crm.entities.Cita;
import com.cibertec.denticore.crm.enums.EstadoCita;
import com.cibertec.denticore.crm.repositories.CitaRepository;
import com.cibertec.denticore.security.entities.Usuario;
import com.cibertec.denticore.security.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OdontogramaServiceImpl implements OdontogramaService {

    private final CitaRepository citaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AtencionClinicaRepository atencionClinicaRepository;
    private final OdontogramaRepository odontogramaRepository;
    private final ElementoOdontogramaRepository elementoOdontogramaRepository;
    private final DetalleOdontogramaRepository detalleOdontogramaRepository;

    @Override
    @Transactional
    public void registrarAtencionClinica(GuardarOdontogramaRequestDTO dto, String dniAutenticado) {
        Usuario creadoPor = usuarioRepository.findByDni(dniAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Cita cita = citaRepository.findById(dto.getIdCita())
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (cita.getEstado() != EstadoCita.EN_CURSO) {
            throw new IllegalStateException("La cita debe estar EN_CURSO para registrar la atención.");
        }

        HistoriaClinica historiaClinica = cita.getPaciente().getHistoriaClinica();
        if (historiaClinica == null) {
            throw new RuntimeException("El paciente no tiene historia clínica registrada");
        }

        AtencionClinica atencion = new AtencionClinica();
        atencion.setHistoriaClinica(historiaClinica);
        atencion.setCita(cita);
        atencion.setMotivoConsulta(dto.getMotivoConsulta());
        atencion.setNotasClinicas(dto.getNotasClinicas());
        atencion.setCreadoPor(creadoPor);

        AtencionClinica atencionGuardada = atencionClinicaRepository.save(atencion);

        Odontograma odontograma = new Odontograma();
        odontograma.setAtencionClinica(atencionGuardada);
        odontograma.setTipo(dto.getTipoOdontograma());
        odontograma.setCreadoPor(creadoPor);

        for (DetalleOdontogramaDTO detalleDto : dto.getDetalles()) {
            ElementoOdontograma elemento = elementoOdontogramaRepository.findById(detalleDto.getIdElementoClinico())
                    .orElseThrow(() -> new RuntimeException("Elemento clínico no encontrado"));

            DetalleOdontograma detalle = new DetalleOdontograma();
            detalle.setOdontograma(odontograma);
            detalle.setElementoClinico(elemento);
            detalle.setNumeroPieza(detalleDto.getNumeroPieza());
            detalle.setDiagnostico(detalleDto.getDiagnostico());
            detalle.setEstadoTratamiento(detalleDto.getEstadoTratamiento());

            odontograma.getDetalles().add(detalle);
        }

        odontogramaRepository.save(odontograma);

        cita.setEstado(EstadoCita.ATENDIDA);
        citaRepository.save(cita);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DetalleHistorialDTO> obtenerHistorialPaciente(Integer idPaciente) {
        return detalleOdontogramaRepository.findHistorialByPacienteId(idPaciente).stream()
                .map(this::mapearHistorial)
                .toList();
    }

    
    private DetalleHistorialDTO mapearHistorial(DetalleOdontograma detalle) {
        DetalleHistorialDTO dto = new DetalleHistorialDTO();
        dto.setNumeroPieza(detalle.getNumeroPieza());
        dto.setDiagnostico(detalle.getDiagnostico());
        dto.setEstadoTratamiento(detalle.getEstadoTratamiento());
        dto.setFechaAtencion(detalle.getOdontograma().getAtencionClinica().getFechaAtencion());
        dto.setTipoOdontograma(detalle.getOdontograma().getTipo());
        return dto;
    }

        @Override
        @Transactional(readOnly = true)
        public List<ElementoOdontogramaDTO> listarElementosActivos() {
            return elementoOdontogramaRepository.findByActivoTrueOrderByCategoriaAscNombreAsc()
                    .stream()
                    .map(this::mapearElemento)
                    .toList();
        }

        private ElementoOdontogramaDTO mapearElemento(ElementoOdontograma elemento) {
            ElementoOdontogramaDTO dto = new ElementoOdontogramaDTO();
            dto.setId(elemento.getId());
            dto.setNombre(elemento.getNombre());
            dto.setCategoria(elemento.getCategoria());
            dto.setAplicaA(elemento.getAplicaA());
            dto.setColorHex(elemento.getColorHex());
            return dto;
        }
}
