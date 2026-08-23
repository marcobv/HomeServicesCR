package com.homeservices.controller;

import com.homeservices.config.SessionKeys;
import com.homeservices.domain.Usuario;
import com.homeservices.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
        if (usuario.getPassword() == null || usuario.getPassword().length() < 6) {
            result.rejectValue("password", "password.corta", "La contraseña debe tener al menos 6 caracteres.");
        }
        if (result.hasErrors()) {
            return "usuario/registro";
        }
        if (usuarioService.buscarPorCorreo(usuario.getCorreo()).isPresent()) {
            redirectAttributes.addFlashAttribute("error", "El correo ya se encuentra registrado.");
            return "redirect:/usuarios/registro";
        }
        usuario.setRol("PROVEEDOR".equalsIgnoreCase(usuario.getRol()) ? "PROVEEDOR" : "CLIENTE");
        usuario.setIdUsuario(null);
        usuarioService.guardar(usuario);
        redirectAttributes.addFlashAttribute("mensaje", "La cuenta se creó correctamente. Ya puede iniciar sesión.");
        return "redirect:/usuarios/login";
    }

    @GetMapping("/login")
    public String login() {
        return "usuario/login";
    }

    @PostMapping("/login")
    public String procesarLogin(@RequestParam String correo,
                                @RequestParam String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        var usuario = usuarioService.autenticar(correo, password);
        if (usuario.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Correo o contraseña incorrectos, o la cuenta está inactiva.");
            return "redirect:/usuarios/login";
        }

        Usuario autenticado = usuario.get();
        usuarioService.actualizarPasswordLegada(autenticado, password);
        session.setAttribute(SessionKeys.USER_ID, autenticado.getIdUsuario());
        session.setAttribute(SessionKeys.USER_NAME, autenticado.getNombreCompleto());
        session.setAttribute(SessionKeys.USER_ROLE, autenticado.getRol().toUpperCase());
        redirectAttributes.addFlashAttribute("mensaje", "Inicio de sesión exitoso. Bienvenido(a) " + autenticado.getNombreCompleto() + ".");

        return switch (autenticado.getRol().toUpperCase()) {
            case "PROVEEDOR" -> "redirect:/proveedor/panel";
            case "ADMIN" -> "redirect:/admin/panel";
            default -> "redirect:/cliente/panel";
        };
    }

    @PostMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("mensaje", "La sesión se cerró correctamente.");
        return "redirect:/inicio";
    }

    @GetMapping("/perfil")
    public String perfil(HttpSession session, Model model) {
        model.addAttribute("usuario", usuarioService.obtenerRequerido(requireUserId(session)));
        return "usuario/perfil";
    }

    @PostMapping("/perfil")
    public String actualizarPerfil(@RequestParam String nombreCompleto,
                                   @RequestParam(required = false) String telefono,
                                   @RequestParam(required = false) String ubicacion,
                                   HttpSession session,
                                   RedirectAttributes redirectAttributes) {
        if (nombreCompleto == null || nombreCompleto.isBlank()) {
            redirectAttributes.addFlashAttribute("error", "El nombre es obligatorio.");
            return "redirect:/usuarios/perfil";
        }
        usuarioService.actualizarPerfil(requireUserId(session), nombreCompleto, telefono, ubicacion);
        session.setAttribute(SessionKeys.USER_NAME, nombreCompleto.trim());
        redirectAttributes.addFlashAttribute("mensaje", "El perfil fue actualizado correctamente.");
        return "redirect:/usuarios/perfil";
    }

    @GetMapping("/recuperar")
    public String recuperar() {
        return "usuario/recuperar";
    }

    @PostMapping("/recuperar")
    public String restablecer(@RequestParam String correo,
                              @RequestParam String nuevaPassword,
                              RedirectAttributes redirectAttributes) {
        if (nuevaPassword == null || nuevaPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("error", "La nueva contraseña debe tener al menos 6 caracteres.");
            return "redirect:/usuarios/recuperar";
        }
        if (!usuarioService.restablecerPassword(correo, nuevaPassword)) {
            redirectAttributes.addFlashAttribute("error", "No existe una cuenta activa con ese correo.");
            return "redirect:/usuarios/recuperar";
        }
        redirectAttributes.addFlashAttribute("mensaje", "La contraseña fue actualizada. Ya puede iniciar sesión.");
        return "redirect:/usuarios/login";
    }

    private Long requireUserId(HttpSession session) {
        Object id = session.getAttribute(SessionKeys.USER_ID);
        if (id instanceof Long value) {
            return value;
        }
        throw new IllegalStateException("No existe una sesión activa.");
    }
}
