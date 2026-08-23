package com.homeservices.controller;

import com.homeservices.config.SessionKeys;
import com.homeservices.domain.Calificacion;
import com.homeservices.domain.Solicitud;
import com.homeservices.dto.HorarioDisponible;
import com.homeservices.service.CalificacionService;
import com.homeservices.service.ProveedorService;
import com.homeservices.service.ServicioService;
import com.homeservices.service.SolicitudService;
import com.homeservices.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {

    private static final int DIAS_VISIBLES = 60;
    private static final DateTimeFormatter VALOR_FECHA = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter VALOR_HORA = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter FECHA_CORTA = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final Locale ESPANOL = new Locale("es", "CR");

    private final SolicitudService solicitudService;
    private final ServicioService servicioService;
    private final UsuarioService usuarioService;
    private final ProveedorService proveedorService;
    private final CalificacionService calificacionService;

    public SolicitudController(SolicitudService solicitudService,
                               ServicioService servicioService,
                               UsuarioService usuarioService,
                               ProveedorService proveedorService,
                               CalificacionService calificacionService) {
        this.solicitudService = solicitudService;
        this.servicioService = servicioService;
        this.usuarioService = usuarioService;
        this.proveedorService = proveedorService;
        this.calificacionService = calificacionService;
    }

    @GetMapping("/nueva/{idServicio}")
    public String nueva(@PathVariable Long idServicio, Model model, HttpSession session) {
        var servicio = servicioService.obtener(idServicio);
        var solicitud = new Solicitud();
        solicitud.setServicio(servicio);
        solicitud.setProveedor(servicio.getProveedor());
        solicitud.setCliente(clienteActual(session));
        model.addAttribute("solicitud", solicitud);
        model.addAttribute("servicio", servicio);
        model.addAttribute("horariosDisponibles",
                construirHorariosDisponibles(servicio.getProveedor().getIdProveedor()));
        return "solicitud/nueva";
    }

    @PostMapping("/guardar")
    public String guardar(Solicitud solicitud,
                          @RequestParam(required = false) String horarioSeleccionado,
                          HttpSession session,
                          RedirectAttributes redirectAttributes) {
        var servicio = servicioService.obtener(solicitud.getServicio().getIdServicio());
        try {
            asignarHorarioSeleccionado(solicitud, horarioSeleccionado);
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/solicitudes/nueva/" + servicio.getIdServicio();
        }
        if (solicitud.getFechaServicio() == null || solicitud.getFechaServicio().isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar una fecha actual o futura.");
            return "redirect:/solicitudes/nueva/" + servicio.getIdServicio();
        }
        boolean horarioOfrecido = construirHorariosDisponibles(servicio.getProveedor().getIdProveedor()).stream()
                .anyMatch(horario -> horario.getFecha().equals(solicitud.getFechaServicio())
                        && horario.getHora().equals(solicitud.getHoraServicio()));
        if (!horarioOfrecido) {
            redirectAttributes.addFlashAttribute("error",
                    "El horario seleccionado ya no está disponible. Por favor seleccione otro espacio.");
            return "redirect:/solicitudes/nueva/" + servicio.getIdServicio();
        }
        if (!proveedorService.estaDisponible(servicio.getProveedor().getIdProveedor(),
                solicitud.getFechaServicio(), solicitud.getHoraServicio())) {
            redirectAttributes.addFlashAttribute("error", "El proveedor no está disponible en la fecha y hora seleccionadas.");
            return "redirect:/solicitudes/nueva/" + servicio.getIdServicio();
        }
        if (solicitudService.horarioOcupado(servicio.getProveedor().getIdProveedor(),
                solicitud.getFechaServicio(), solicitud.getHoraServicio())) {
            redirectAttributes.addFlashAttribute("error", "Ese horario ya tiene una solicitud pendiente o aceptada.");
            return "redirect:/solicitudes/nueva/" + servicio.getIdServicio();
        }
        solicitud.setIdSolicitud(null);
        solicitud.setServicio(servicio);
        solicitud.setProveedor(servicio.getProveedor());
        solicitud.setCliente(clienteActual(session));
        solicitud.setEstado("PENDIENTE");
        solicitudService.guardar(solicitud);
        redirectAttributes.addFlashAttribute("mensaje", "La solicitud fue registrada correctamente.");
        return "redirect:/cliente/panel";
    }

    @PostMapping("/{idSolicitud}/estado")
    public String actualizarEstado(@PathVariable Long idSolicitud,
                                   String estado,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        try {
            var proveedor = proveedorService.obtenerPorUsuario((Long) session.getAttribute(SessionKeys.USER_ID));
            solicitudService.actualizarEstado(idSolicitud, proveedor.getIdProveedor(), estado);
            redirectAttributes.addFlashAttribute("mensaje", "El estado de la solicitud fue actualizado.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/proveedor/panel";
    }

    @GetMapping("/{idSolicitud}/calificar")
    public String calificar(@PathVariable Long idSolicitud,
                            HttpSession session,
                            Model model,
                            RedirectAttributes redirectAttributes) {
        var solicitud = solicitudService.obtener(idSolicitud);
        if (!solicitud.getCliente().getIdUsuario().equals(clienteActual(session).getIdUsuario())) {
            return accesoNoAutorizado(redirectAttributes);
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
                                      HttpSession session,
                                      RedirectAttributes redirectAttributes) {
        if (calificacion.getSolicitud() == null || calificacion.getSolicitud().getIdSolicitud() == null) {
            redirectAttributes.addFlashAttribute("error", "No fue posible identificar la solicitud a calificar.");
            return "redirect:/cliente/panel";
        }
        var solicitud = solicitudService.obtener(calificacion.getSolicitud().getIdSolicitud());
        if (!solicitud.getCliente().getIdUsuario().equals(clienteActual(session).getIdUsuario())) {
            return accesoNoAutorizado(redirectAttributes);
        }
        if (!estadoEs(solicitud, "FINALIZADA") || calificacionService.existePorSolicitud(solicitud.getIdSolicitud())) {
            redirectAttributes.addFlashAttribute("error", "La solicitud no puede ser calificada.");
            return "redirect:/cliente/panel";
        }
        if (calificacion.getPuntaje() == null || calificacion.getPuntaje() < 1 || calificacion.getPuntaje() > 5
                || calificacion.getComentario() == null || calificacion.getComentario().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Debe indicar un puntaje entre 1 y 5 y escribir un comentario.");
            return "redirect:/solicitudes/" + solicitud.getIdSolicitud() + "/calificar";
        }
        calificacion.setIdCalificacion(null);
        calificacion.setSolicitud(solicitud);
        calificacion.setVerificado(true);
        calificacion.setReportado(false);
        calificacionService.guardar(calificacion);
        redirectAttributes.addFlashAttribute("mensaje", "Gracias por publicar una calificación verificada.");
        return "redirect:/cliente/panel";
    }

    @PostMapping("/calificaciones/{id}/reportar")
    public String reportarComentario(@PathVariable Long id,
                                     Long idProveedor,
                                     RedirectAttributes redirectAttributes) {
        calificacionService.reportar(id);
        redirectAttributes.addFlashAttribute("mensaje", "El comentario fue enviado a revisión administrativa.");
        return "redirect:/servicios/proveedor/" + idProveedor;
    }

    private com.homeservices.domain.Usuario clienteActual(HttpSession session) {
        return usuarioService.obtenerRequerido((Long) session.getAttribute(SessionKeys.USER_ID));
    }

    private boolean estadoEs(Solicitud solicitud, String estado) {
        return solicitud.getEstado() != null && solicitud.getEstado().equalsIgnoreCase(estado);
    }

    private String accesoNoAutorizado(RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", "No puede modificar solicitudes de otro cliente.");
        return "redirect:/cliente/panel";
    }

    private List<HorarioDisponible> construirHorariosDisponibles(Long idProveedor) {
        LocalDate hoy = LocalDate.now();
        LocalDate fechaFinal = hoy.plusDays(DIAS_VISIBLES);
        LocalDateTime ahora = LocalDateTime.now();
        Set<String> ocupados = new HashSet<>();
        for (Solicitud solicitud : solicitudService.listarHorariosOcupados(idProveedor, hoy, fechaFinal)) {
            if (solicitud.getFechaServicio() != null && solicitud.getHoraServicio() != null) {
                ocupados.add(claveHorario(solicitud.getFechaServicio(), solicitud.getHoraServicio()));
            }
        }

        List<HorarioDisponible> horarios = new ArrayList<>();
        Set<String> agregados = new HashSet<>();
        var disponibilidades = proveedorService.obtenerDisponibilidad(idProveedor);

        for (LocalDate fecha = hoy; !fecha.isAfter(fechaFinal); fecha = fecha.plusDays(1)) {
            String dia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, ESPANOL);
            for (var disponibilidad : disponibilidades) {
                if (disponibilidad.getDiaSemana() == null
                        || !dia.equalsIgnoreCase(disponibilidad.getDiaSemana())
                        || disponibilidad.getHoraInicio() == null
                        || disponibilidad.getHoraFin() == null) {
                    continue;
                }
                for (LocalTime hora = disponibilidad.getHoraInicio();
                     !hora.plusHours(1).isAfter(disponibilidad.getHoraFin());
                     hora = hora.plusHours(1)) {
                    String clave = claveHorario(fecha, hora);
                    if (!LocalDateTime.of(fecha, hora).isAfter(ahora)
                            || ocupados.contains(clave)
                            || !agregados.add(clave)) {
                        continue;
                    }
                    horarios.add(new HorarioDisponible(
                            fecha,
                            hora,
                            clave,
                            etiquetaHorario(fecha, hora)));
                }
            }
        }

        horarios.sort(Comparator.comparing(HorarioDisponible::getFecha)
                .thenComparing(HorarioDisponible::getHora));
        return horarios;
    }

    private void asignarHorarioSeleccionado(Solicitud solicitud, String horarioSeleccionado) {
        if (horarioSeleccionado == null || horarioSeleccionado.isBlank()) {
            throw new IllegalArgumentException("Debe seleccionar uno de los horarios disponibles.");
        }
        String[] partes = horarioSeleccionado.split("\\|", -1);
        if (partes.length != 2) {
            throw new IllegalArgumentException("El horario seleccionado no es válido.");
        }
        try {
            solicitud.setFechaServicio(LocalDate.parse(partes[0], VALOR_FECHA));
            solicitud.setHoraServicio(LocalTime.parse(partes[1], VALOR_HORA));
        } catch (java.time.format.DateTimeParseException ex) {
            throw new IllegalArgumentException("El horario seleccionado no es válido.");
        }
    }

    private String claveHorario(LocalDate fecha, LocalTime hora) {
        return fecha.format(VALOR_FECHA) + "|" + hora.format(VALOR_HORA);
    }

    private String etiquetaHorario(LocalDate fecha, LocalTime hora) {
        String dia = fecha.getDayOfWeek().getDisplayName(TextStyle.FULL, ESPANOL);
        dia = Character.toUpperCase(dia.charAt(0)) + dia.substring(1);
        return dia + " " + fecha.format(FECHA_CORTA)
                + " · " + hora.format(VALOR_HORA)
                + " a " + hora.plusHours(1).format(VALOR_HORA);
    }
}
