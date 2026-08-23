package com.homeservices.service;

import com.homeservices.domain.Calificacion;
import com.homeservices.repository.CalificacionRepository;
import com.homeservices.repository.ProveedorRepository;
import com.homeservices.repository.SolicitudRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;
    private final ProveedorRepository proveedorRepository;
    private final SolicitudRepository solicitudRepository;

    public CalificacionService(CalificacionRepository calificacionRepository,
                               ProveedorRepository proveedorRepository,
                               SolicitudRepository solicitudRepository) {
        this.calificacionRepository = calificacionRepository;
        this.proveedorRepository = proveedorRepository;
        this.solicitudRepository = solicitudRepository;
    }

    @Transactional(readOnly = true)
    public List<Calificacion> listarPorProveedor(Long idProveedor) {
        return calificacionRepository.findBySolicitudProveedorIdProveedorAndVerificadoTrueOrderByFechaCreacionDesc(idProveedor);
    }

    @Transactional(readOnly = true)
    public List<Calificacion> listarReportadas() {
        return calificacionRepository.findByReportadoTrueOrderByFechaCreacionDesc();
    }

    @Transactional(readOnly = true)
    public List<Calificacion> listarPorCliente(Long idCliente) {
        return calificacionRepository.findBySolicitudClienteIdUsuario(idCliente);
    }

    @Transactional(readOnly = true)
    public Optional<Calificacion> buscarPorSolicitud(Long idSolicitud) {
        return calificacionRepository.findBySolicitudIdSolicitud(idSolicitud);
    }

    @Transactional(readOnly = true)
    public boolean existePorSolicitud(Long idSolicitud) {
        return calificacionRepository.existsBySolicitudIdSolicitud(idSolicitud);
    }

    @Transactional
    public Calificacion guardar(Calificacion calificacion) {
        Calificacion guardada = calificacionRepository.save(calificacion);
        actualizarIndicadores(guardada.getSolicitud().getProveedor().getIdProveedor());
        return guardada;
    }

    @Transactional
    public void reportar(Long idCalificacion) {
        calificacionRepository.findById(idCalificacion).ifPresent(calificacion -> {
            calificacion.setReportado(true);
            calificacionRepository.save(calificacion);
        });
    }

    @Transactional
    public void resolverReporte(Long idCalificacion, boolean ocultar) {
        Calificacion calificacion = calificacionRepository.findById(idCalificacion)
                .orElseThrow(() -> new IllegalArgumentException("El comentario indicado no existe."));
        calificacion.setReportado(false);
        if (ocultar) {
            calificacion.setVerificado(false);
        }
        calificacionRepository.save(calificacion);
        actualizarIndicadores(calificacion.getSolicitud().getProveedor().getIdProveedor());
    }

    private void actualizarIndicadores(Long idProveedor) {
        proveedorRepository.findById(idProveedor).ifPresent(proveedor -> {
            Double promedio = calificacionRepository.promedioVerificadoPorProveedor(idProveedor);
            proveedor.setCalificacionPromedio(promedio == null ? 0.0 : Math.round(promedio * 10.0) / 10.0);
            proveedor.setServiciosCompletados((int) solicitudRepository
                    .countByProveedorIdProveedorAndEstado(idProveedor, "FINALIZADA"));
            proveedorRepository.save(proveedor);
        });
    }
}
