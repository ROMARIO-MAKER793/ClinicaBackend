package com.cibertec.denticore.ventas.dto.request;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class ProcesarVentaRequestDTO {
    private Integer idPaciente;
    private Integer idCita;
    private String tipoDocumento;
    private String metodoPago;
    private BigDecimal montoPagado;
    private List<ItemVentaRequestDTO> itemsCarrito;
}