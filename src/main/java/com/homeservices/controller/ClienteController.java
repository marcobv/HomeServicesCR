package com.homeservices.controller;

import com.homeservices.domain.Solicitud;
import com.homeservices.service.CalificacionService;
import com.homeservices.service.SolicitudService;
import com.homeservices.service.UsuarioService;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController {

    private final SolicitudService solicitudService;
    private final UsuarioService usuarioService;
    private final CalificacionService calificacionService;

    public ClienteController(SolicitudService solicitudService,
                             UsuarioService usuarioService,
                             CalificacionService calificacionService) {
        this.solicitudService = solicitudService;
        this.usuarioService = usuarioService;
        this.calificacionService = calificacionService;
    }

    @GetMapping("/panel")
    public String panel(Model model) {
        var cliente = usuarioService.obtenerClienteDemo();
        var solicitudes = solicitudService.listarPorCliente(cliente.getIdUsuario());

        Set<Long> solicitudesCalificadas = calificacionService.listarPorCliente(cliente.getIdUsuario())
                .stream()
                .filter(calificacion -> calificacion.getSolicitud() != null)
                .map(calificacion -> calificacion.getSolicitud().getIdSolicitud())
                .collect(Collectors.toSet());

        long solicitudesActivas = solicitudes.stream()
                .filter(solicitud -> !estadoEs(solicitud, "FINALIZADA") && !estadoEs(solicitud, "RECHAZADA"))
                .count();

        long serviciosCompletados = solicitudes.stream()
                .filter(solicitud -> estadoEs(solicitud, "FINALIZADA"))
                .count();

        long pendientesCalificar = solicitudes.stream()
                .filter(solicitud -> estadoEs(solicitud, "FINALIZADA"))
                .filter(solicitud -> !solicitudesCalificadas.contains(solicitud.getIdSolicitud()))
                .count();

        model.addAttribute("cliente", cliente);
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("solicitudesCalificadas", solicitudesCalificadas);
        model.addAttribute("solicitudesActivas", solicitudesActivas);
        model.addAttribute("serviciosCompletados", serviciosCompletados);
        model.addAttribute("pendientesCalificar", pendientesCalificar);

        return "solicitud/panel-cliente";
    }

    private boolean estadoEs(Solicitud solicitud, String estado) {
        return solicitud.getEstado() != null && solicitud.getEstado().equalsIgnoreCase(estado);
    }
}