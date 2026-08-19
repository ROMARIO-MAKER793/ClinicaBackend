package com.cibertec.denticore.ventas.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class PrevisualizacionVentaDTO {
    private Integer idCita;
    private Integer idPaciente;
    private String paciente;
    private String dni;
    private BigDecimal total = BigDecimal.ZERO;
    private List<ItemPrevisualizacionVentaDTO> items = new ArrayList<>();
}