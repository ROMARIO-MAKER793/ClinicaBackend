package com.cibertec.denticore.clinica.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class GuardarOdontogramaRequestDTO {

    private Integer idCita;
    private String motivoConsulta;
    private String notasClinicas;
    private String tipoOdontograma;
    private List<DetalleOdontogramaDTO> detalles;
}
