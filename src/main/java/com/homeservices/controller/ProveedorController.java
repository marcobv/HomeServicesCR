package com.homeservices.controller;

import com.homeservices.domain.Servicio;
import com.homeservices.service.CategoriaService;
import com.homeservices.service.ProveedorService;
import com.homeservices.service.ServicioService;
import com.homeservices.service.SolicitudService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    public String panel(Model model) {
        var proveedores = proveedorService.listarTodos();
        var proveedor = proveedores.isEmpty() ? null : proveedores.get(0);

        model.addAttribute("proveedor", proveedor);
        model.addAttribute("solicitudes", proveedor == null ? java.util.List.of() : solicitudService.listarPendientesPorProveedor(proveedor.getIdProveedor()));
        model.addAttribute("solicitudesNuevas", proveedor == null ? 0 : solicitudService.contarPendientesPorProveedor(proveedor.getIdProveedor()));
        model.addAttribute("servicios", servicioService.listarActivos());

        return "proveedor/panel-proveedor";
    }

    @GetMapping("/servicios/nuevo")
    public String nuevoServicio(Model model) {
        model.addAttribute("servicio", new Servicio());
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("proveedores", proveedorService.listarTodos());
        return "proveedor/servicio-form";
    }

    @PostMapping("/servicios/guardar")
    public String guardarServicio(Servicio servicio,
                                  Long idCategoria,
                                  Long idProveedor,
                                  RedirectAttributes redirectAttributes) {
        servicio.setCategoria(categoriaService.obtener(idCategoria));
        servicio.setProveedor(proveedorService.obtener(idProveedor));
        servicioService.guardar(servicio);
        redirectAttributes.addFlashAttribute("mensaje", "El servicio fue guardado correctamente.");
        return "redirect:/proveedor/panel";
    }
}