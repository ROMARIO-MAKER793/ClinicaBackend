package com.cibertec.denticore.clinica.dto.response;

import lombok.Data;

@Data
public class ElementoOdontogramaDTO {
    private Integer id;
    private String nombre;
    private String categoria;
    private String aplicaA;
    private String colorHex;
}
