package com.homeservices.controller;

import com.homeservices.domain.Categoria;
import com.homeservices.service.CalificacionService;
import com.homeservices.service.CategoriaService;
import com.homeservices.service.ProveedorService;
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
@RequestMapping("/admin")
public class AdminController {

    private final UsuarioService usuarioService;
    private final CategoriaService categoriaService;
    private final ProveedorService proveedorService;
    private final SolicitudService solicitudService;
    private final CalificacionService calificacionService;

    public AdminController(UsuarioService usuarioService, CategoriaService categoriaService,
            ProveedorService proveedorService, SolicitudService solicitudService,
            CalificacionService calificacionService) {
        this.usuarioService = usuarioService;
        this.categoriaService = categoriaService;
        this.proveedorService = proveedorService;
        this.solicitudService = solicitudService;
        this.calificacionService = calificacionService;
    }

    @GetMapping("/panel")
    public String panel(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("proveedores", proveedorService.listarTodos());
        model.addAttribute("solicitudes", solicitudService.listarTodas());
        model.addAttribute("comentariosReportados", calificacionService.listarReportadas());
        return "admin/panel-admin";
    }

    @GetMapping("/categorias")
    public String categorias(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodas());
        model.addAttribute("categoria", new Categoria());
        return "admin/categorias";
    }

    @PostMapping("/categorias/guardar")
    public String guardarCategoria(Categoria categoria, RedirectAttributes redirectAttributes) {
        categoriaService.guardar(categoria);
        redirectAttributes.addFlashAttribute("mensaje", "La categoría fue guardada correctamente.");
        return "redirect:/admin/categorias";
    }

    @GetMapping("/categorias/desactivar/{id}")
    public String desactivarCategoria(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        categoriaService.desactivar(id);
        redirectAttributes.addFlashAttribute("mensaje", "La categoría fue desactivada.");
        return "redirect:/admin/categorias";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        return "admin/usuarios";
    }

    @GetMapping("/proveedores")
    public String proveedores(Model model) {
        model.addAttribute("proveedores", proveedorService.listarTodos());
        return "admin/proveedores";
    }

    @GetMapping("/solicitudes")
    public String solicitudes(Model model) {
        model.addAttribute("solicitudes", solicitudService.listarTodas());
        return "admin/solicitudes";
    }

    @GetMapping("/reportes")
    public String reportes(Model model) {
        model.addAttribute("comentariosReportados", calificacionService.listarReportadas());
        return "admin/reportes";
    }
}
