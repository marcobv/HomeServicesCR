package com.homeservices.service;

import com.homeservices.domain.Calificacion;
import com.homeservices.repository.CalificacionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CalificacionService {

    private final CalificacionRepository calificacionRepository;

    public CalificacionService(CalificacionRepository calificacionRepository) {
        this.calificacionRepository = calificacionRepository;
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
        return calificacionRepository.save(calificacion);
    }

    @Transactional
    public void reportar(Long idCalificacion) {
        calificacionRepository.findById(idCalificacion).ifPresent(calificacion -> {
            calificacion.setReportado(true);
            calificacionRepository.save(calificacion);
        });
    }
}
