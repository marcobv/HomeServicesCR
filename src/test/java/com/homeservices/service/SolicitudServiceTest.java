package com.homeservices.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.homeservices.domain.Proveedor;
import com.homeservices.domain.Solicitud;
import com.homeservices.repository.SolicitudRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SolicitudServiceTest {

    private SolicitudRepository repository;
    private SolicitudService service;
    private Solicitud solicitud;

    @BeforeEach
    void preparar() {
        repository = mock(SolicitudRepository.class);
        service = new SolicitudService(repository);
        Proveedor proveedor = new Proveedor();
        proveedor.setIdProveedor(7L);
        solicitud = new Solicitud();
        solicitud.setIdSolicitud(20L);
        solicitud.setProveedor(proveedor);
        solicitud.setEstado("PENDIENTE");
        when(repository.findById(20L)).thenReturn(Optional.of(solicitud));
    }

    @Test
    void permiteAceptarYFinalizarEnOrden() {
        service.actualizarEstado(20L, 7L, "ACEPTADA");
        assertEquals("ACEPTADA", solicitud.getEstado());

        service.actualizarEstado(20L, 7L, "FINALIZADA");
        assertEquals("FINALIZADA", solicitud.getEstado());
        verify(repository, times(2)).save(solicitud);
    }

    @Test
    void impideSaltarDePendienteAFinalizada() {
        assertThrows(IllegalArgumentException.class,
                () -> service.actualizarEstado(20L, 7L, "FINALIZADA"));
    }

    @Test
    void impideModificarSolicitudesDeOtroProveedor() {
        assertThrows(IllegalArgumentException.class,
                () -> service.actualizarEstado(20L, 99L, "ACEPTADA"));
    }
}
