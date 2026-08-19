package com.cibertec.denticore.clinica.dto.response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class DetalleHistorialDTO {

    private Integer numeroPieza;
    private String diagnostico;
    private String estadoTratamiento;
    private LocalDateTime fechaAtencion;
    private String tipoOdontograma;
}
