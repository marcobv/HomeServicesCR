package com.homeservices.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class AppErrorController implements org.springframework.boot.web.servlet.error.ErrorController {

    @GetMapping("/acceso-denegado")
    public String accesoDenegado(Model model) {
        model.addAttribute("codigo", 403);
        model.addAttribute("titulo", "Acceso denegado");
        model.addAttribute("detalle", "Su cuenta no tiene permisos para acceder a esta sección.");
        return "error/error";
    }

    @RequestMapping("/error")
    public String error(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int codigo = status == null ? 500 : Integer.parseInt(status.toString());
        model.addAttribute("codigo", codigo);
        model.addAttribute("titulo", codigo == 404 ? "Página no encontrada" : "No fue posible completar la solicitud");
        model.addAttribute("detalle", codigo == 404
                ? "La dirección solicitada no existe."
                : "Ocurrió un error inesperado. Regrese al inicio e inténtelo nuevamente.");
        return "error/error";
    }
}
