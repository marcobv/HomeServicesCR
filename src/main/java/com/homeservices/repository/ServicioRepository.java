package com.homeservices.repository;

import com.homeservices.domain.Servicio;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ServicioRepository extends JpaRepository<Servicio, Long> {

    List<Servicio> findByActivoTrueOrderByNombreAsc();
    List<Servicio> findTop6ByActivoTrueOrderByProveedorCalificacionPromedioDesc();
//Denisse
    List<Servicio> findByProveedorIdProveedorAndActivoTrueOrderByNombreAsc(Long idProveedor);
    @Query("SELECT s FROM Servicio s " +
           "WHERE s.activo = true " +
           "AND (:texto IS NULL OR :texto = '' OR LOWER(s.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "     OR LOWER(s.descripcion) LIKE LOWER(CONCAT('%', :texto, '%')) " +
           "     OR LOWER(s.categoria.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))) " +
           "AND (:idCategoria IS NULL OR s.categoria.idCategoria = :idCategoria) " +
           "AND (:ubicacion IS NULL OR :ubicacion = '' OR LOWER(s.proveedor.ubicacion) LIKE LOWER(CONCAT('%', :ubicacion, '%'))) " +
           "AND (:precioMax IS NULL OR s.precioReferencia <= :precioMax) " +
           "ORDER BY s.proveedor.calificacionPromedio DESC, s.nombre ASC")
    List<Servicio> buscarServicios(@Param("texto") String texto,
                                   @Param("idCategoria") Long idCategoria,
                                   @Param("ubicacion") String ubicacion,
                                   @Param("precioMax") BigDecimal precioMax);
}
