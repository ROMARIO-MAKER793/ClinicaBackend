package com.cibertec.denticore.crm.controllers;

import com.cibertec.denticore.crm.dto.request.ActualizarEstadoDTO;
import com.cibertec.denticore.crm.dto.request.CitaRequestDTO;
import com.cibertec.denticore.crm.dto.response.CitaListadoDTO;
import com.cibertec.denticore.crm.dto.response.CitaResponseDTO;
import com.cibertec.denticore.crm.services.CitaService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.time.LocalDate;

@RestController
@RequestMapping("/citas")
@RequiredArgsConstructor
public class CitaApiController {

    private final CitaService citaService;

    @PostMapping
    public ResponseEntity<CitaResponseDTO> programarCita(
            @RequestBody CitaRequestDTO dto,
            Principal principal) {
        String dniUsuarioAutenticado = principal.getName();
        CitaResponseDTO response = citaService.programarCita(dto, dniUsuarioAutenticado);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/agenda-diaria")
    public ResponseEntity<Page<CitaListadoDTO>> listarAgendaDiaria(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageRequest pageRequest = PageRequest.of(page, size);
        return ResponseEntity.ok(citaService.listarAgendaDiaria(fecha, pageRequest));
    }

    @PutMapping("/{id}/estado")
    public ResponseEntity<CitaResponseDTO> actualizarEstado(
            @PathVariable Integer id,
            @RequestBody ActualizarEstadoDTO dto) {
        return ResponseEntity.ok(citaService.actualizarEstado(id, dto));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<CitaResponseDTO> handleIllegalStateException(IllegalStateException ex) {
        CitaResponseDTO errorResponse = new CitaResponseDTO();
        errorResponse.setMensaje(ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @GetMapping("/mis-citas")
    public ResponseEntity<Page<CitaListadoDTO>> listarMisCitas(
        Principal principal,
        @RequestParam(required = false)
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate fecha,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    PageRequest pageRequest = PageRequest.of(page, size);

    return ResponseEntity.ok(
            citaService.listarMisCitas(
                    principal.getName(),
                    fecha,
                    pageRequest));
    }


    @GetMapping("/mis-citas-paciente")
public ResponseEntity<Page<CitaListadoDTO>> listarMisCitasPaciente(
        Principal principal,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

    PageRequest pageRequest = PageRequest.of(page, size);

    return ResponseEntity.ok(
            citaService.listarMisCitasPaciente(
                    principal.getName(),
                    pageRequest));
}
}
