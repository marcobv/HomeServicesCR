package com.homeservices.repository;

import com.homeservices.domain.Proveedor;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    List<Proveedor> findByVerificadoTrueOrderByCalificacionPromedioDesc();
}
