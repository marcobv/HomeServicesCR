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
    public Solicitud obtener(Long id) {
        return solicitudRepository.findById(id).orElse(new Solicitud());
    }

    @Transactional
    public Solicitud guardar(Solicitud solicitud) {
        if (solicitud.getEstado() == null || solicitud.getEstado().isBlank()) {
            solicitud.setEstado("PENDIENTE");
        }
        return solicitudRepository.save(solicitud);
    }

    @Transactional
    public void actualizarEstado(Long idSolicitud, String estado) {
        solicitudRepository.findById(idSolicitud).ifPresent(solicitud -> {
            solicitud.setEstado(estado);
            solicitudRepository.save(solicitud);
        });
    }
}