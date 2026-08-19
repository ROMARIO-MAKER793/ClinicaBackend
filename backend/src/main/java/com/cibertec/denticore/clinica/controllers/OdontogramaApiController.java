package com.cibertec.denticore.clinica.controllers;

import com.cibertec.denticore.clinica.dto.request.GuardarOdontogramaRequestDTO;
import com.cibertec.denticore.clinica.dto.response.DetalleHistorialDTO;
import com.cibertec.denticore.clinica.dto.response.ElementoOdontogramaDTO;
import com.cibertec.denticore.clinica.services.OdontogramaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/clinica/odontograma")
@RequiredArgsConstructor
public class OdontogramaApiController {

    private final OdontogramaService odontogramaService;

    @PostMapping
    public ResponseEntity<Void> registrarAtencion(
            @RequestBody GuardarOdontogramaRequestDTO dto,
            Principal principal) {
        String dniAutenticado = principal.getName();
        odontogramaService.registrarAtencionClinica(dto, dniAutenticado);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/historial/{idPaciente}")
    public ResponseEntity<List<DetalleHistorialDTO>> obtenerHistorialPaciente(
            @PathVariable Integer idPaciente) {
        return ResponseEntity.ok(odontogramaService.obtenerHistorialPaciente(idPaciente));
    }

    @GetMapping("/elementos")
    public ResponseEntity<List<ElementoOdontogramaDTO>> listarElementosActivos() {
        return ResponseEntity.ok(odontogramaService.listarElementosActivos());
    }
}
