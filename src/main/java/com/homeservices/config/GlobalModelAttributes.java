package com.homeservices.config;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute("sesionUsuarioId")
    public Object usuarioId(HttpSession session) {
        return session.getAttribute(SessionKeys.USER_ID);
    }

    @ModelAttribute("sesionUsuarioNombre")
    public Object usuarioNombre(HttpSession session) {
        return session.getAttribute(SessionKeys.USER_NAME);
    }

    @ModelAttribute("sesionUsuarioRol")
    public Object usuarioRol(HttpSession session) {
        return session.getAttribute(SessionKeys.USER_ROLE);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ModelAndView recursoNoEncontrado(IllegalArgumentException ex) {
        ModelAndView view = new ModelAndView("error/error");
        view.setStatus(HttpStatus.NOT_FOUND);
        view.addObject("codigo", 404);
        view.addObject("titulo", "Recurso no encontrado");
        view.addObject("detalle", ex.getMessage());
        return view;
    }
}
