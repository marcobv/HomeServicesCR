package com.homeservices.repository;

import com.homeservices.domain.Solicitud;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

    List<Solicitud> findByClienteIdUsuarioOrderByFechaCreacionDesc(Long idCliente);

    List<Solicitud> findByProveedorIdProveedorOrderByFechaCreacionDesc(Long idProveedor);

    List<Solicitud> findByProveedorIdProveedorAndEstadoOrderByFechaCreacionDesc(Long idProveedor, String estado);

    long countByEstado(String estado);

    long countByProveedorIdProveedorAndEstado(Long idProveedor, String estado);
}