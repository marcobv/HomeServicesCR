package com.homeservices.controller;

import com.homeservices.service.CategoriaService;
import com.homeservices.service.ServicioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    private final CategoriaService categoriaService;
    private final ServicioService servicioService;

    public HomeController(CategoriaService categoriaService, ServicioService servicioService) {
        this.categoriaService = categoriaService;
        this.servicioService = servicioService;
    }

    @GetMapping({"/", "/inicio"})
    public String inicio(Model model) {
        model.addAttribute("categorias", categoriaService.listarActivas());
        model.addAttribute("serviciosDestacados", servicioService.listarDestacados());
        return "index";
    }
}
