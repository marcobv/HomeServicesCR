package com.homeservices.repository;

import com.homeservices.domain.Solicitud;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByClienteIdUsuarioOrderByFechaCreacionDesc(Long idCliente);

    List<Solicitud> findByProveedorIdProveedorOrderByFechaCreacionDesc(Long idProveedor);

    List<Solicitud> findByProveedorIdProveedorAndEstadoOrderByFechaCreacionDesc(Long idProveedor, String estado);

    long countByEstado(String estado);

    long countByProveedorIdProveedorAndEstado(Long idProveedor, String estado);

    boolean existsByProveedorIdProveedorAndFechaServicioAndHoraServicioAndEstadoIn(
            Long idProveedor, LocalDate fechaServicio, LocalTime horaServicio, List<String> estados);

    List<Solicitud> findByProveedorIdProveedorAndFechaServicioBetweenAndEstadoIn(
            Long idProveedor, LocalDate fechaInicio, LocalDate fechaFin, List<String> estados);
}
