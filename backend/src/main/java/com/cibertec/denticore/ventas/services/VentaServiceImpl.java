package com.cibertec.denticore.ventas.services;

import com.cibertec.denticore.catalogo.entities.ItemCatalogo;
import com.cibertec.denticore.catalogo.repositories.ItemCatalogoRepository;
import com.cibertec.denticore.clinica.entities.Odontograma;
import com.cibertec.denticore.clinica.repositories.OdontogramaRepository;
import com.cibertec.denticore.crm.entities.Cita;
import com.cibertec.denticore.crm.enums.EstadoCita;
import com.cibertec.denticore.crm.repositories.CitaRepository;
import com.cibertec.denticore.security.entities.Paciente;
import com.cibertec.denticore.security.entities.Usuario;
import com.cibertec.denticore.security.repositories.PacienteRepository;
import com.cibertec.denticore.security.repositories.UsuarioRepository;
import com.cibertec.denticore.ventas.dto.request.ItemVentaRequestDTO;
import com.cibertec.denticore.ventas.dto.request.ProcesarVentaRequestDTO;
import com.cibertec.denticore.ventas.dto.response.ItemPrevisualizacionVentaDTO;
import com.cibertec.denticore.ventas.dto.response.PrevisualizacionVentaDTO;
import com.cibertec.denticore.ventas.dto.response.VentaListadoDTO;
import com.cibertec.denticore.ventas.entities.DetalleTransaccion;
import com.cibertec.denticore.ventas.entities.TransaccionComercial;
import com.cibertec.denticore.ventas.repositories.TransaccionComercialRepository;
import com.cibertec.denticore.clinica.entities.DetalleOdontograma;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {

    private final TransaccionComercialRepository transaccionRepository;
    private final PacienteRepository pacienteRepository;
    private final CitaRepository citaRepository;
    private final ItemCatalogoRepository itemCatalogoRepository;
    private final UsuarioRepository usuarioRepository;
    private final OdontogramaRepository odontogramaRepository;

    @Override
    @Transactional
    public void procesarVenta(ProcesarVentaRequestDTO dto, String dniAutenticado) {
        Usuario creadoPor = usuarioRepository.findByDni(dniAutenticado)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Paciente paciente = pacienteRepository.findById(dto.getIdPaciente())
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado"));

        Cita cita = citaRepository.findById(dto.getIdCita())
                .orElseThrow(() -> new RuntimeException("Cita no encontrada"));

        if (dto.getItemsCarrito() == null || dto.getItemsCarrito().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un item.");
        }

        BigDecimal total = calcularTotal(dto.getItemsCarrito());
        BigDecimal montoPagado = dto.getMontoPagado() != null ? dto.getMontoPagado() : BigDecimal.ZERO;

        TransaccionComercial transaccion = new TransaccionComercial();
        transaccion.setPaciente(paciente);
        transaccion.setCita(cita);
        transaccion.setCreadoPor(creadoPor);
        transaccion.setTipoDocumento(dto.getTipoDocumento() != null ? dto.getTipoDocumento() : "ReciboInterno");
        transaccion.setSerie("R001");
        transaccion.setCorrelativo(transaccionRepository.countBySerie("R001") + 1);
        transaccion.setSubTotal(total);
        transaccion.setIgv(BigDecimal.ZERO);
        transaccion.setTotal(total);
        transaccion.setMontoPagado(montoPagado);
        transaccion.setMetodoPago(dto.getMetodoPago() != null ? dto.getMetodoPago() : "Efectivo");
        transaccion.setEstadoPago(calcularEstadoPago(total, montoPagado));

        for (ItemVentaRequestDTO itemDto : dto.getItemsCarrito()) {
            ItemCatalogo itemCatalogo = itemCatalogoRepository.findById(itemDto.getIdItemCatalogo())
                    .orElseThrow(() -> new RuntimeException("Item de catálogo no encontrado"));

            DetalleTransaccion detalle = new DetalleTransaccion();
            detalle.setTransaccion(transaccion);
            detalle.setItemCatalogo(itemCatalogo);
            detalle.setCantidad(itemDto.getCantidad());
            detalle.setPrecioAplicado(itemDto.getPrecioAplicado());
            detalle.setNumeroPieza(itemDto.getNumeroPieza());

            transaccion.getDetalles().add(detalle);
        }

        transaccionRepository.save(transaccion);

        cita.setEstado(EstadoCita.FINALIZADA);
        citaRepository.save(cita);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VentaListadoDTO> listarVentas() {
        return transaccionRepository.findAll()
                .stream()
                .map(this::mapearVenta)
                .toList();
    }

    private BigDecimal calcularTotal(List<ItemVentaRequestDTO> items) {
        return items.stream()
                .map(item -> item.getPrecioAplicado().multiply(BigDecimal.valueOf(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String calcularEstadoPago(BigDecimal total, BigDecimal montoPagado) {
        if (montoPagado.compareTo(BigDecimal.ZERO) <= 0) {
            return "Pendiente";
        }

        if (montoPagado.compareTo(total) >= 0) {
            return "Pagado";
        }

        return "Parcial";
    }

    private VentaListadoDTO mapearVenta(TransaccionComercial venta) {
        VentaListadoDTO dto = new VentaListadoDTO();
        dto.setId(venta.getId());
        dto.setIdCita(venta.getCita() != null ? venta.getCita().getId() : null);
        dto.setIdPaciente(venta.getPaciente().getUsuario().getId());
        dto.setPaciente(
                venta.getPaciente().getUsuario().getNombres() + " " +
                venta.getPaciente().getUsuario().getApellidos()
        );
        dto.setDni(venta.getPaciente().getUsuario().getDni());
        dto.setTipoDocumento(venta.getTipoDocumento());
        dto.setSerie(venta.getSerie());
        dto.setCorrelativo(venta.getCorrelativo());
        dto.setFechaEmision(venta.getFechaEmision());
        dto.setTotal(venta.getTotal());
        dto.setMontoPagado(venta.getMontoPagado());
        dto.setSaldoPendiente(venta.getSaldoPendiente());
        dto.setMetodoPago(venta.getMetodoPago());
        dto.setEstadoPago(venta.getEstadoPago());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public PrevisualizacionVentaDTO obtenerPrevisualizacion(Integer idCita) {
        Odontograma odontograma = odontogramaRepository.findByAtencionClinicaCitaId(idCita)
            .orElseThrow(() -> new RuntimeException("No existe odontograma registrado para esta cita"));

    Cita cita = odontograma.getAtencionClinica().getCita();
    Usuario usuarioPaciente = cita.getPaciente().getUsuario();

    PrevisualizacionVentaDTO dto = new PrevisualizacionVentaDTO();
    dto.setIdCita(cita.getId());
    dto.setIdPaciente(usuarioPaciente.getId());
    dto.setPaciente(usuarioPaciente.getNombres() + " " + usuarioPaciente.getApellidos());
    dto.setDni(usuarioPaciente.getDni());

    List<ItemPrevisualizacionVentaDTO> items = new ArrayList<>();

    agregarConsultaDental(items);

    odontograma.getDetalles().stream()
            .filter(detalle -> "Tratamiento".equalsIgnoreCase(detalle.getElementoClinico().getCategoria()))
            .filter(detalle -> "Realizado".equalsIgnoreCase(detalle.getEstadoTratamiento()))
            .forEach(detalle -> agregarItemTratamiento(items, detalle));

    dto.setItems(items);

    BigDecimal total = items.stream()
            .map(item -> item.getPrecioAplicado().multiply(BigDecimal.valueOf(item.getCantidad())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    dto.setTotal(total);

    return dto;
    }


    private void agregarConsultaDental(List<ItemPrevisualizacionVentaDTO> items) {
    ItemCatalogo consulta = buscarItemPorNombre("Consulta dental");

    ItemPrevisualizacionVentaDTO item = new ItemPrevisualizacionVentaDTO();
    item.setIdItemCatalogo(consulta.getId());
    item.setDescripcion(consulta.getNombre());
    item.setCantidad(1);
    item.setPrecioAplicado(consulta.getCostoReferencial());
    item.setNumeroPieza(null);

    items.add(item);
}

private void agregarItemTratamiento(
        List<ItemPrevisualizacionVentaDTO> items,
        DetalleOdontograma detalle
) {
    ItemCatalogo itemCatalogo = resolverItemCatalogo(detalle.getElementoClinico().getNombre());

    ItemPrevisualizacionVentaDTO item = new ItemPrevisualizacionVentaDTO();
    item.setIdItemCatalogo(itemCatalogo.getId());
    item.setDescripcion(itemCatalogo.getNombre() + " - Pieza " + detalle.getNumeroPieza());
    item.setCantidad(1);
    item.setPrecioAplicado(itemCatalogo.getCostoReferencial());
    item.setNumeroPieza(detalle.getNumeroPieza());

    items.add(item);
}

private ItemCatalogo resolverItemCatalogo(String elementoClinico) {
    String nombre = elementoClinico.toLowerCase();

    if (nombre.contains("curación") || nombre.contains("restauración")) {
        return buscarItemPorNombre("Curación dental");
    }

    if (nombre.contains("limpieza") || nombre.contains("profilaxis")) {
        return buscarItemPorNombre("Limpieza dental");
    }

    if (nombre.contains("endodoncia")) {
        return buscarItemPorNombre("Tratamiento de conducto");
    }

    if (nombre.contains("extracción")) {
        return buscarItemPorNombre("Consulta dental");
    }

    return buscarItemPorNombre("Consulta dental");
}

    private ItemCatalogo buscarItemPorNombre(String nombre) {
        return itemCatalogoRepository.findByActivoTrue()
                .stream()
                .filter(item -> item.getNombre().equalsIgnoreCase(nombre))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No existe item de catálogo: " + nombre));
    }


}