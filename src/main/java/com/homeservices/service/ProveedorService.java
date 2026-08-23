package com.homeservices.service;

import com.homeservices.domain.Disponibilidad;
import com.homeservices.domain.Proveedor;
import com.homeservices.repository.DisponibilidadRepository;
import com.homeservices.repository.ProveedorRepository;
import java.util.List;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;
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
        return proveedorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El proveedor indicado no existe."));
    }

    @Transactional(readOnly = true)
    public Proveedor obtenerPorUsuario(Long idUsuario) {
        return proveedorRepository.findByUsuarioIdUsuario(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException("El usuario no tiene un perfil de proveedor."));
    }

    @Transactional
    public Proveedor guardar(Proveedor proveedor) {
        return proveedorRepository.save(proveedor);
    }

    @Transactional(readOnly = true)
    public List<Disponibilidad> obtenerDisponibilidad(Long idProveedor) {
        return disponibilidadRepository.findByProveedorIdProveedorAndDisponibleTrueOrderByDiaSemanaAscHoraInicioAsc(idProveedor);
    }

    @Transactional(readOnly = true)
    public List<Disponibilidad> obtenerTodaDisponibilidad(Long idProveedor) {
        return disponibilidadRepository.findByProveedorIdProveedorOrderByDiaSemanaAscHoraInicioAsc(idProveedor);
    }

    @Transactional
    public void guardarDisponibilidad(Long idProveedor, Disponibilidad disponibilidad) {
        if (disponibilidad.getHoraInicio() == null || disponibilidad.getHoraFin() == null
                || !disponibilidad.getHoraFin().isAfter(disponibilidad.getHoraInicio())) {
            throw new IllegalArgumentException("La hora final debe ser posterior a la hora inicial.");
        }
        disponibilidad.setProveedor(obtener(idProveedor));
        if (disponibilidad.getDisponible() == null) {
            disponibilidad.setDisponible(true);
        }
        disponibilidadRepository.save(disponibilidad);
    }

    @Transactional
    public void eliminarDisponibilidad(Long idProveedor, Long idDisponibilidad) {
        disponibilidadRepository.findById(idDisponibilidad).ifPresent(disponibilidad -> {
            if (!disponibilidad.getProveedor().getIdProveedor().equals(idProveedor)) {
                throw new IllegalArgumentException("El horario no pertenece al proveedor autenticado.");
            }
            disponibilidadRepository.delete(disponibilidad);
        });
    }

    @Transactional(readOnly = true)
    public boolean estaDisponible(Long idProveedor, LocalDate fecha, LocalTime hora) {
        if (fecha == null || hora == null) {
            return false;
        }
        Map<java.time.DayOfWeek, String> dias = Map.of(
                java.time.DayOfWeek.MONDAY, "Lunes",
                java.time.DayOfWeek.TUESDAY, "Martes",
                java.time.DayOfWeek.WEDNESDAY, "Miércoles",
                java.time.DayOfWeek.THURSDAY, "Jueves",
                java.time.DayOfWeek.FRIDAY, "Viernes",
                java.time.DayOfWeek.SATURDAY, "Sábado",
                java.time.DayOfWeek.SUNDAY, "Domingo");
        String dia = dias.get(fecha.getDayOfWeek());
        return obtenerDisponibilidad(idProveedor).stream()
                .filter(d -> dia.equalsIgnoreCase(d.getDiaSemana()))
                .anyMatch(d -> !hora.isBefore(d.getHoraInicio()) && hora.isBefore(d.getHoraFin()));
    }

    @Transactional
    public void actualizarPerfil(Long idProveedor, String especialidad, String experiencia) {
        Proveedor proveedor = obtener(idProveedor);
        proveedor.setEspecialidad(especialidad == null ? null : especialidad.trim());
        proveedor.setExperiencia(experiencia == null ? null : experiencia.trim());
        proveedorRepository.save(proveedor);
    }

    @Transactional
    public void cambiarVerificacion(Long idProveedor) {
        Proveedor proveedor = obtener(idProveedor);
        proveedor.setVerificado(!Boolean.TRUE.equals(proveedor.getVerificado()));
        proveedorRepository.save(proveedor);
    }
}
