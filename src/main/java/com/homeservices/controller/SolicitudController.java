package com.homeservices.controller;

import com.homeservices.domain.Calificacion;
import com.homeservices.domain.Solicitud;
import com.homeservices.service.CalificacionService;
import com.homeservices.service.ServicioService;
import com.homeservices.service.SolicitudService;
import com.homeservices.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final ServicioService servicioService;
    private final UsuarioService usuarioService;
    private final CalificacionService calificacionService;

    public SolicitudController(SolicitudService solicitudService,
                               ServicioService servicioService,
                               UsuarioService usuarioService,
                               CalificacionService calificacionService) {
        this.solicitudService = solicitudService;
        this.servicioService = servicioService;
        this.usuarioService = usuarioService;
        this.calificacionService = calificacionService;
    }

    @GetMapping("/nueva/{idServicio}")
    public String nueva(@PathVariable Long idServicio, Model model) {
        var servicio = servicioService.obtener(idServicio);

        var solicitud = new Solicitud();
        solicitud.setServicio(servicio);
        solicitud.setProveedor(servicio.getProveedor());
        solicitud.setCliente(usuarioService.obtenerClienteDemo());

        model.addAttribute("solicitud", solicitud);
        model.addAttribute("servicio", servicio);

        return "solicitud/nueva";
    }

    @PostMapping("/guardar")
    public String guardar(Solicitud solicitud, RedirectAttributes redirectAttributes) {
        var servicio = servicioService.obtener(solicitud.getServicio().getIdServicio());

        solicitud.setServicio(servicio);
        solicitud.setProveedor(servicio.getProveedor());
        solicitud.setCliente(usuarioService.obtenerClienteDemo());

        solicitudService.guardar(solicitud);

        redirectAttributes.addFlashAttribute("mensaje", "La solicitud fue registrada correctamente.");
        return "redirect:/cliente/panel";
    }

    @PostMapping("/{idSolicitud}/estado")
    public String actualizarEstado(@PathVariable Long idSolicitud,
                                   String estado,
                                   RedirectAttributes redirectAttributes) {
        solicitudService.actualizarEstado(idSolicitud, estado);

        redirectAttributes.addFlashAttribute("mensaje", "El estado de la solicitud fue actualizado.");
        return "redirect:/proveedor/panel";
    }

    @GetMapping("/{idSolicitud}/calificar")
    public String calificar(@PathVariable Long idSolicitud,
                            Model model,
                            RedirectAttributes redirectAttributes) {

        var solicitud = solicitudService.obtener(idSolicitud);

        if (solicitud.getIdSolicitud() == null) {
            redirectAttributes.addFlashAttribute("error", "La solicitud indicada no existe.");
            return "redirect:/cliente/panel";
        }

        if (!estadoEs(solicitud, "FINALIZADA")) {
            redirectAttributes.addFlashAttribute("error", "Solo se pueden calificar solicitudes finalizadas.");
            return "redirect:/cliente/panel";
        }

        if (calificacionService.existePorSolicitud(idSolicitud)) {
            redirectAttributes.addFlashAttribute("mensaje", "Esta solicitud ya fue calificada anteriormente.");
            return "redirect:/cliente/panel";
        }

        var calificacion = new Calificacion();
        calificacion.setSolicitud(solicitud);

        model.addAttribute("calificacion", calificacion);
        model.addAttribute("solicitud", solicitud);

        return "solicitud/calificar";
    }

    @PostMapping("/calificar")
    public String guardarCalificacion(Calificacion calificacion,
                                      RedirectAttributes redirectAttributes) {

        if (calificacion.getSolicitud() == null || calificacion.getSolicitud().getIdSolicitud() == null) {
            redirectAttributes.addFlashAttribute("error", "No fue posible identificar la solicitud a calificar.");
            return "redirect:/cliente/panel";
        }

        Long idSolicitud = calificacion.getSolicitud().getIdSolicitud();
        var solicitud = solicitudService.obtener(idSolicitud);

        if (solicitud.getIdSolicitud() == null) {
            redirectAttributes.addFlashAttribute("error", "La solicitud indicada no existe.");
            return "redirect:/cliente/panel";
        }

        if (!estadoEs(solicitud, "FINALIZADA")) {
            redirectAttributes.addFlashAttribute("error", "Solo se pueden calificar solicitudes finalizadas.");
            return "redirect:/cliente/panel";
        }

        if (calificacionService.existePorSolicitud(idSolicitud)) {
            redirectAttributes.addFlashAttribute("mensaje", "Esta solicitud ya fue calificada anteriormente.");
            return "redirect:/cliente/panel";
        }

        calificacion.setSolicitud(solicitud);
        calificacion.setVerificado(true);
        calificacion.setReportado(false);

        calificacionService.guardar(calificacion);

        redirectAttributes.addFlashAttribute("mensaje", "Gracias por publicar una calificación verificada.");
        return "redirect:/cliente/panel";
    }

    private boolean estadoEs(Solicitud solicitud, String estado) {
        return solicitud.getEstado() != null && solicitud.getEstado().equalsIgnoreCase(estado);
    }
}