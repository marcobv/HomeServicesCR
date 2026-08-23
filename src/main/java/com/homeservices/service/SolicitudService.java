package com.homeservices.service;

import com.homeservices.domain.Solicitud;
import com.homeservices.repository.SolicitudRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SolicitudService {

    private final SolicitudRepository solicitudRepository;

    public SolicitudService(SolicitudRepository solicitudRepository) {
        this.solicitudRepository = solicitudRepository;
    }

    @Transactional(readOnly = true)
    public List<Solicitud> listarTodas() {
        return solicitudRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Solicitud> listarPorCliente(Long idCliente) {
        return solicitudRepository.findByClienteIdUsuarioOrderByFechaCreacionDesc(idCliente);
    }

    @Transactional(readOnly = true)
    public List<Solicitud> listarPorProveedor(Long idProveedor) {
        return solicitudRepository.findByProveedorIdProveedorOrderByFechaCreacionDesc(idProveedor);
    }

    @Transactional(readOnly = true)
    public List<Solicitud> listarPendientesPorProveedor(Long idProveedor) {
        return solicitudRepository.findByProveedorIdProveedorAndEstadoOrderByFechaCreacionDesc(idProveedor, "PENDIENTE");
    }

    @Transactional(readOnly = true)
    public long contarPendientesPorProveedor(Long idProveedor) {
        return solicitudRepository.countByProveedorIdProveedorAndEstado(idProveedor, "PENDIENTE");
    }

    @Transactional(readOnly = true)
    public boolean horarioOcupado(Long idProveedor, java.time.LocalDate fecha, java.time.LocalTime hora) {
        return solicitudRepository.existsByProveedorIdProveedorAndFechaServicioAndHoraServicioAndEstadoIn(
                idProveedor, fecha, hora, List.of("PENDIENTE", "ACEPTADA"));
    }

    @Transactional(readOnly = true)
    public List<Solicitud> listarHorariosOcupados(Long idProveedor,
                                                  java.time.LocalDate fechaInicio,
                                                  java.time.LocalDate fechaFin) {
        return solicitudRepository.findByProveedorIdProveedorAndFechaServicioBetweenAndEstadoIn(
                idProveedor, fechaInicio, fechaFin, List.of("PENDIENTE", "ACEPTADA"));
    }

    @Transactional(readOnly = true)
    public Solicitud obtener(Long id) {
        return solicitudRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La solicitud indicada no existe."));
    }

    @Transactional
    public Solicitud guardar(Solicitud solicitud) {
        if (solicitud.getEstado() == null || solicitud.getEstado().isBlank()) {
            solicitud.setEstado("PENDIENTE");
        }
        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public void actualizarEstado(Long idSolicitud, Long idProveedor, String estado) {
        Solicitud solicitud = obtener(idSolicitud);
        if (!solicitud.getProveedor().getIdProveedor().equals(idProveedor)) {
            throw new IllegalArgumentException("La solicitud no pertenece al proveedor autenticado.");
        }
        String actual = solicitud.getEstado() == null ? "PENDIENTE" : solicitud.getEstado().toUpperCase();
        String nuevo = estado == null ? "" : estado.toUpperCase();
        boolean transicionValida = ("PENDIENTE".equals(actual)
                && ("ACEPTADA".equals(nuevo) || "RECHAZADA".equals(nuevo)))
                || ("ACEPTADA".equals(actual) && "FINALIZADA".equals(nuevo));
        if (!transicionValida) {
            throw new IllegalArgumentException("La transición de estado solicitada no es válida.");
        }
        solicitud.setEstado(nuevo);
        solicitudRepository.save(solicitud);
    }
}
