package com.homeservices.controller;

import com.homeservices.config.SessionKeys;
import com.homeservices.domain.Disponibilidad;
import com.homeservices.domain.Servicio;
import com.homeservices.service.CategoriaService;
import com.homeservices.service.ProveedorService;
import com.homeservices.service.ServicioService;
import com.homeservices.service.SolicitudService;
import jakarta.servlet.http.HttpSession;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/proveedor")
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final SolicitudService solicitudService;
    private final ServicioService servicioService;
    private final CategoriaService categoriaService;

    public ProveedorController(ProveedorService proveedorService,
                               SolicitudService solicitudService,
                               ServicioService servicioService,
                               CategoriaService categoriaService) {
        this.proveedorService = proveedorService;
        this.solicitudService = solicitudService;
        this.servicioService = servicioService;
        this.categoriaService = categoriaService;
    }

    @GetMapping("/panel")
    public String panel(Model model, HttpSession session) {
        var proveedor = actual(session);
        var solicitudes = solicitudService.listarPorProveedor(proveedor.getIdProveedor());
        model.addAttribute("proveedor", proveedor);
        model.addAttribute("solicitudes", solicitudes);
        model.addAttribute("solicitudesNuevas", solicitudes.stream()
                .filter(s -> "PENDIENTE".equalsIgnoreCase(s.getEstado())).count());
        model.addAttribute("servicios", servicioService.listarTodosPorProveedor(proveedor.getIdProveedor()));
        model.addAttribute("disponibilidades", proveedorService.obtenerTodaDisponibilidad(proveedor.getIdProveedor()));
        model.addAttribute("nuevaDisponibilidad", new Disponibilidad());
        return "proveedor/panel-proveedor";
    }

    @GetMapping("/servicios/nuevo")
    public String nuevoServicio(Model model) {
        prepararFormulario(model, new Servicio());
        return "proveedor/servicio-form";
    }

    @GetMapping("/servicios/{id}/editar")
    public String editarServicio(@PathVariable Long id, Model model, HttpSession session) {
        var proveedor = actual(session);
        prepararFormulario(model, servicioService.obtenerDelProveedor(id, proveedor.getIdProveedor()));
        return "proveedor/servicio-form";
    }

    @PostMapping("/servicios/guardar")
    public String guardarServicio(Servicio servicio,
                                  @RequestParam Long idCategoria,
                                  HttpSession session,
                                  RedirectAttributes redirectAttributes) {
        var proveedor = actual(session);
        if (servicio.getIdServicio() != null) {
            servicioService.obtenerDelProveedor(servicio.getIdServicio(), proveedor.getIdProveedor());
        }
        if (servicio.getPrecioReferencia() == null
                || servicio.getPrecioReferencia().compareTo(BigDecimal.ZERO) <= 0) {
            redirectAttributes.addFlashAttribute("error", "El precio debe ser mayor que cero.");
            return servicio.getIdServicio() == null
                    ? "redirect:/proveedor/servicios/nuevo"
                    : "redirect:/proveedor/servicios/" + servicio.getIdServicio() + "/editar";
        }
        servicio.setCategoria(categoriaService.obtener(idCategoria));
        servicio.setProveedor(proveedor);
        servicioService.guardar(servicio);
        redirectAttributes.addFlashAttribute("mensaje", "El servicio fue guardado correctamente.");
        return "redirect:/proveedor/panel";
    }

    @PostMapping("/servicios/{id}/estado")
    public String cambiarEstadoServicio(@PathVariable Long id,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        var proveedor = actual(session);
        servicioService.cambiarEstado(id, proveedor.getIdProveedor());
        redirectAttributes.addFlashAttribute("mensaje", "El estado del servicio fue actualizado.");
        return "redirect:/proveedor/panel";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@RequestParam String especialidad,
                                   @RequestParam String experiencia,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        var proveedor = actual(session);
        proveedorService.actualizarPerfil(proveedor.getIdProveedor(), especialidad, experiencia);
        redirectAttributes.addFlashAttribute("mensaje", "El perfil profesional fue actualizado.");
        return "redirect:/proveedor/panel";
    }

    @PostMapping("/disponibilidad/guardar")
    public String guardarDisponibilidad(Disponibilidad disponibilidad,
                                        HttpSession session,
                                        RedirectAttributes redirectAttributes) {
        try {
            proveedorService.guardarDisponibilidad(actual(session).getIdProveedor(), disponibilidad);
            redirectAttributes.addFlashAttribute("mensaje", "El horario fue agregado correctamente.");
        } catch (IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
        }
        return "redirect:/proveedor/panel";
    }

    @PostMapping("/disponibilidad/{id}/eliminar")
    public String eliminarDisponibilidad(@PathVariable Long id,
                                         HttpSession session,
                                         RedirectAttributes redirectAttributes) {
        proveedorService.eliminarDisponibilidad(actual(session).getIdProveedor(), id);
        redirectAttributes.addFlashAttribute("mensaje", "El horario fue eliminado.");
        return "redirect:/proveedor/panel";
    }

    private void prepararFormulario(Model model, Servicio servicio) {
        model.addAttribute("servicio", servicio);
        model.addAttribute("categorias", categoriaService.listarActivas());
    }

    private com.homeservices.domain.Proveedor actual(HttpSession session) {
        Long idUsuario = (Long) session.getAttribute(SessionKeys.USER_ID);
        return proveedorService.obtenerPorUsuario(idUsuario);
    }
}
