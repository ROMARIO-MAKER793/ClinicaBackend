package com.cibertec.denticore.ventas.dto.response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class VentaListadoDTO {
    private Integer id;
    private Integer idCita;
    private Integer idPaciente;
    private String paciente;
    private String dni;
    private String tipoDocumento;
    private String serie;
    private Integer correlativo;
    private LocalDateTime fechaEmision;
    private BigDecimal total;
    private BigDecimal montoPagado;
    private BigDecimal saldoPendiente;
    private String metodoPago;
    private String estadoPago;
}