package com.homeservices.repository;

import com.homeservices.domain.Proveedor;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
    List<Proveedor> findByVerificadoTrueOrderByCalificacionPromedioDesc();
    Optional<Proveedor> findByUsuarioIdUsuario(Long idUsuario);
}
