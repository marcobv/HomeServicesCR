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
        return servicioRepository.findById(id).orElse(new Servicio());
    }

    @Transactional(readOnly = true)
    public List<Servicio> buscar(String texto, Long idCategoria, String ubicacion, BigDecimal precioMax) {
        return servicioRepository.buscarServicios(texto, idCategoria, ubicacion, precioMax);
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
}
