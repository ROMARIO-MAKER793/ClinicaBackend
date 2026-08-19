package com.cibertec.denticore.ventas.dto.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ItemVentaRequestDTO {
    
    private Integer idItemCatalogo;
    private Integer cantidad;
    private BigDecimal precioAplicado;
    private Integer numeroPieza;
}
