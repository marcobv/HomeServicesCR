package com.homeservices.service;

import com.homeservices.domain.Servicio;
import com.homeservices.repository.ServicioRepository;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ServicioService {
    private final ServicioRepository servicioRepository;

    public ServicioService(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    @Transactional(readOnly = true)
    public List<Servicio> listarActivos() {
        return servicioRepository.findByActivoTrueOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<Servicio> listarDestacados() {
        return servicioRepository.findTop6ByActivoTrueOrderByProveedorCalificacionPromedioDesc();
    }

    @Transactional(readOnly = true)
    public Servicio obtener(Long id) {
        return servicioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El servicio indicado no existe."));
    }

    @Transactional(readOnly = true)
    public List<Servicio> buscar(String texto, Long idCategoria, String ubicacion,
                                 BigDecimal precioMax, Double calificacionMin) {
        return servicioRepository.buscarServicios(texto, idCategoria, ubicacion, precioMax, calificacionMin);
    }

    @Transactional
    public Servicio guardar(Servicio servicio) {
        if (servicio.getActivo() == null) {
            servicio.setActivo(true);
        }
        return servicioRepository.save(servicio);
    }
    //denisse
    @Transactional(readOnly = true)
    public List<Servicio> listarPorProveedor(Long idProveedor) {
        return servicioRepository
            .findByProveedorIdProveedorAndActivoTrueOrderByNombreAsc(idProveedor);
    }

    @Transactional(readOnly = true)
    public List<Servicio> listarTodosPorProveedor(Long idProveedor) {
        return servicioRepository.findByProveedorIdProveedorOrderByNombreAsc(idProveedor);
    }

    @Transactional
    public void cambiarEstado(Long idServicio, Long idProveedor) {
        Servicio servicio = obtenerDelProveedor(idServicio, idProveedor);
        servicio.setActivo(!Boolean.TRUE.equals(servicio.getActivo()));
        servicioRepository.save(servicio);
    }

    @Transactional(readOnly = true)
    public Servicio obtenerDelProveedor(Long idServicio, Long idProveedor) {
        Servicio servicio = obtener(idServicio);
        if (!servicio.getProveedor().getIdProveedor().equals(idProveedor)) {
            throw new IllegalArgumentException("El servicio no pertenece al proveedor autenticado.");
        }
        return servicio;
    }
}
