package com.homeservices.repository;

import com.homeservices.domain.Calificacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    List<Calificacion> findBySolicitudProveedorIdProveedorAndVerificadoTrueOrderByFechaCreacionDesc(Long idProveedor);

    List<Calificacion> findByReportadoTrueOrderByFechaCreacionDesc();

    Optional<Calificacion> findBySolicitudIdSolicitud(Long idSolicitud);

    boolean existsBySolicitudIdSolicitud(Long idSolicitud);

    List<Calificacion> findBySolicitudClienteIdUsuario(Long idCliente);
}