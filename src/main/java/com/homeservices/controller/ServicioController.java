package com.homeservices.controller;

import com.homeservices.service.CalificacionService;
import com.homeservices.service.CategoriaService;
import com.homeservices.service.ProveedorService;
import com.homeservices.service.ServicioService;
import java.math.BigDecimal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/servicios")
public class ServicioController {
    private final ServicioService servicioService;
    private final CategoriaService categoriaService;
    private final ProveedorService proveedorService;
    private final CalificacionService calificacionService;

    public ServicioController(ServicioService servicioService, CategoriaService categoriaService,
                              ProveedorService proveedorService, CalificacionService calificacionService) {
        this.servicioService = servicioService;
        this.categoriaService = categoriaService;
        this.proveedorService = proveedorService;
        this.calificacionService = calificacionService;
    }

    @GetMapping("/resultados")
    public String resultados(@RequestParam(required = false) String texto,
                             @RequestParam(required = false) Long categoriaId,
                             @RequestParam(required = false) String ubicacion,
                             @RequestParam(required = false) BigDecimal precioMax,
                             Model model) {
        model.addAttribute("servicios", servicioService.buscar(texto, categoriaId, ubicacion, precioMax));
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("texto", texto);
        model.addAttribute("categoriaId", categoriaId);
        model.addAttribute("ubicacion", ubicacion);
        model.addAttribute("precioMax", precioMax);
        return "servicio/resultados";
    }

    @GetMapping("/proveedor/{idProveedor}")
    public String perfilProveedor(@PathVariable Long idProveedor, Model model) {
        model.addAttribute("proveedor", proveedorService.obtener(idProveedor));
        model.addAttribute("disponibilidades", proveedorService.obtenerDisponibilidad(idProveedor));
        model.addAttribute("calificaciones", calificacionService.listarPorProveedor(idProveedor));
        return "servicio/perfil-proveedor";
    }
}
