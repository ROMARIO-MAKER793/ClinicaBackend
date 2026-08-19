package com.cibertec.denticore.ventas.controllers;


import com.cibertec.denticore.ventas.dto.request.ProcesarVentaRequestDTO;
import com.cibertec.denticore.ventas.dto.response.PrevisualizacionVentaDTO;
import com.cibertec.denticore.ventas.dto.response.VentaListadoDTO;
import com.cibertec.denticore.ventas.services.VentaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/ventas")
@RequiredArgsConstructor
public class VentaApiController {

    private final VentaService ventaService;
  

    @PostMapping("/procesar")
    public ResponseEntity<Void> procesarVenta(
            @RequestBody ProcesarVentaRequestDTO request,
            Authentication authentication
    ) {

        ventaService.procesarVenta(request, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping
    public ResponseEntity<List<VentaListadoDTO>> listarVentas() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }

    @GetMapping("/previsualizacion/{idCita}")
    public ResponseEntity<PrevisualizacionVentaDTO> obtenerPrevisualizacion(
            @PathVariable Integer idCita
    ) {
        return ResponseEntity.ok(
                ventaService.obtenerPrevisualizacion(idCita)
        );
    }
}