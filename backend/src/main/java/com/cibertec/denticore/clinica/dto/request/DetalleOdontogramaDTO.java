package com.cibertec.denticore.clinica.dto.request;

import lombok.Data;

@Data
public class DetalleOdontogramaDTO {

    private Integer idElementoClinico;
    private Integer numeroPieza;
    private String diagnostico;
    private String estadoTratamiento;
}
