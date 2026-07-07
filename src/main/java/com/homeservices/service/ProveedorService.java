package com.homeservices.service;

import com.homeservices.domain.Disponibilidad;
import com.homeservices.domain.Proveedor;
import com.homeservices.repository.DisponibilidadRepository;
import com.homeservices.repository.ProveedorRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProveedorService {
    private final ProveedorRepository proveedorRepository;
    private final DisponibilidadRepository disponibilidadRepository;

    public ProveedorService(ProveedorRepository proveedorRepository, DisponibilidadRepository disponibilidadRepository) {
        this.proveedorRepository = proveedorRepository;
        this.disponibilidadRepository = disponibilidadRepository;
    }

    @Transactional(readOnly = true)
    public List<Proveedor> listarTodos() {
        return proveedorRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Proveedor> listarVerificados() {
        return proveedorRepository.findByVerificadoTrueOrderByCalificacionPromedioDesc();
    }

    @Transactional(readOnly = true)
    public Proveedor obtener(Long id) {
        return proveedorRepository.findById(id).orElse(new Proveedor());
    }

    @Transactional
    public Proveedor guardar(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Transactional(readOnly = true)
    public List<Disponibilidad> obtenerDisponibilidad(Long idProveedor) {
        return disponibilidadRepository.findByProveedorIdProveedorAndDisponibleTrueOrderByDiaSemanaAscHoraInicioAsc(idProveedor);
    }
}
