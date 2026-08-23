package com.homeservices.repository;

import com.homeservices.domain.Calificacion;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CalificacionRepository extends JpaRepository<Calificacion, Long> {

    List<Calificacion> findBySolicitudProveedorIdProveedorAndVerificadoTrueOrderByFechaCreacionDesc(Long idProveedor);

    List<Calificacion> findByReportadoTrueOrderByFechaCreacionDesc();

    Optional<Calificacion> findBySolicitudIdSolicitud(Long idSolicitud);

    boolean existsBySolicitudIdSolicitud(Long idSolicitud);

    List<Calificacion> findBySolicitudClienteIdUsuario(Long idCliente);

    @Query("SELECT AVG(c.puntaje) FROM Calificacion c WHERE c.solicitud.proveedor.idProveedor = :idProveedor AND c.verificado = true")
    Double promedioVerificadoPorProveedor(@Param("idProveedor") Long idProveedor);
}
