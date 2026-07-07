package com.homeservices.repository;

import com.homeservices.domain.Disponibilidad;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    List<Disponibilidad> findByProveedorIdProveedorAndDisponibleTrueOrderByDiaSemanaAscHoraInicioAsc(Long idProveedor);
}
