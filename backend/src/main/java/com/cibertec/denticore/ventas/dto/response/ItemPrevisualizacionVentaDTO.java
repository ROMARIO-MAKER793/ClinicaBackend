package com.cibertec.denticore.ventas.dto.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemPrevisualizacionVentaDTO {
    private Integer idItemCatalogo;
    private String descripcion;
    private Integer cantidad;
    private BigDecimal precioAplicado;
    private Integer numeroPieza;
}