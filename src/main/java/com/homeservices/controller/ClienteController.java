package com.homeservices.controller;

import com.homeservices.service.SolicitudService;
import com.homeservices.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cliente")
public class ClienteController {
    private final SolicitudService solicitudService;
    private final UsuarioService usuarioService;

    public ClienteController(SolicitudService solicitudService, UsuarioService usuarioService) {
        this.solicitudService = solicitudService;
        this.usuarioService = usuarioService;
    }

    @GetMapping("/panel")
    public String panel(Model model) {
        var cliente = usuarioService.obtenerClienteDemo();
        model.addAttribute("cliente", cliente);
        model.addAttribute("solicitudes", solicitudService.listarPorCliente(cliente.getIdUsuario()));
        return "solicitud/panel-cliente";
    }
}
