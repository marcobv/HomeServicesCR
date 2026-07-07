package com.homeservices.controller;

import com.homeservices.domain.Usuario;
import com.homeservices.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {
    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "usuario/registro";
    }

    @PostMapping("/guardar")
    public String guardar(@Valid @ModelAttribute Usuario usuario, BindingResult result,
                          RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "usuario/registro";
        }
        if (usuarioService.buscarPorCorreo(usuario.getCorreo()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El correo ya se encuentra registrado.");
            return "redirect:/usuarios/registro";
        }
        usuarioService.guardar(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "La cuenta se creó correctamente.");
        return "redirect:/usuarios/login";
    }

    @GetMapping("/login")
    public String login() {
        return "usuario/login";
    }

    @PostMapping("/login")
    public String procesarLogin(String correo, String password, RedirectAttributes redirectAttributes) {
        var usuario = usuarioService.buscarPorCorreo(correo);
        if (usuario.isPresent() && usuario.get().getPassword().equals(password)) {
            redirectAttributes.addFlashAttribute("mensaje", "Inicio de sesión exitoso. Bienvenido(a) " + usuario.get().getNombreCompleto() + ".");
            String rol = usuario.get().getRol();
            if ("PROVEEDOR".equalsIgnoreCase(rol)) {
                return "redirect:/proveedor/panel";
            }
            if ("ADMIN".equalsIgnoreCase(rol)) {
                return "redirect:/admin/panel";
            }
            return "redirect:/cliente/panel";
        }
        redirectAttributes.addFlashAttribute("error", "Correo o contraseña incorrectos.");
        return "redirect:/usuarios/login";
    }
}
