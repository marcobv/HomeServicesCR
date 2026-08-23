package com.homeservices.service;

import com.homeservices.domain.Usuario;
import com.homeservices.domain.Proveedor;
import com.homeservices.repository.ProveedorRepository;
import com.homeservices.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final ProveedorRepository proveedorRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, ProveedorRepository proveedorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.proveedorRepository = proveedorRepository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario obtener(Long id) {
        return usuarioRepository.findById(id).orElse(new Usuario());
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepository.findByCorreoIgnoreCase(correo == null ? "" : correo.trim());
    }

    @Transactional
    public Usuario guardar(Usuario usuario) {
        if (usuario.getRol() == null || usuario.getRol().isBlank()) {
            usuario.setRol("CLIENTE");
        }
        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }
        if (usuario.getIdUsuario() == null || !PasswordHasher.isEncoded(usuario.getPassword())) {
            usuario.setPassword(PasswordHasher.encode(usuario.getPassword()));
        }
        Usuario guardado = usuarioRepository.save(usuario);
        if ("PROVEEDOR".equalsIgnoreCase(guardado.getRol())
                && proveedorRepository.findByUsuarioIdUsuario(guardado.getIdUsuario()).isEmpty()) {
            Proveedor proveedor = new Proveedor();
            proveedor.setNombre(guardado.getNombreCompleto());
            proveedor.setUbicacion(guardado.getUbicacion());
            proveedor.setTelefono(guardado.getTelefono());
            proveedor.setEspecialidad("Pendiente de completar");
            proveedor.setExperiencia("Perfil pendiente de completar por el proveedor.");
            proveedor.setVerificado(false);
            proveedor.setUsuario(guardado);
            proveedorRepository.save(proveedor);
        }
        return guardado;
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> autenticar(String correo, String password) {
        Optional<Usuario> encontrado = buscarPorCorreo(correo);
        if (encontrado.isEmpty() || !Boolean.TRUE.equals(encontrado.get().getActivo())) {
            return Optional.empty();
        }
        Usuario usuario = encontrado.get();
        boolean valido = PasswordHasher.matches(password, usuario.getPassword());
        return valido ? Optional.of(usuario) : Optional.empty();
    }

    @Transactional
    public void actualizarPerfil(Long idUsuario, String nombre, String telefono, String ubicacion) {
        Usuario usuario = obtenerRequerido(idUsuario);
        usuario.setNombreCompleto(nombre.trim());
        usuario.setTelefono(telefono == null ? null : telefono.trim());
        usuario.setUbicacion(ubicacion == null ? null : ubicacion.trim());
        usuarioRepository.save(usuario);
        proveedorRepository.findByUsuarioIdUsuario(idUsuario).ifPresent(proveedor -> {
            proveedor.setNombre(usuario.getNombreCompleto());
            proveedor.setTelefono(usuario.getTelefono());
            proveedor.setUbicacion(usuario.getUbicacion());
            proveedorRepository.save(proveedor);
        });
    }

    @Transactional
    public boolean restablecerPassword(String correo, String nuevaPassword) {
        Optional<Usuario> encontrado = buscarPorCorreo(correo);
        if (encontrado.isEmpty() || !Boolean.TRUE.equals(encontrado.get().getActivo())) {
            return false;
        }
        Usuario usuario = encontrado.get();
        usuario.setPassword(PasswordHasher.encode(nuevaPassword));
        usuarioRepository.save(usuario);
        return true;
    }

    @Transactional
    public void cambiarEstado(Long idUsuario) {
        Usuario usuario = obtenerRequerido(idUsuario);
        usuario.setActivo(!Boolean.TRUE.equals(usuario.getActivo()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public void actualizarPasswordLegada(Usuario usuario, String passwordPlano) {
        if (!PasswordHasher.isEncoded(usuario.getPassword())) {
            usuario.setPassword(PasswordHasher.encode(passwordPlano));
            usuarioRepository.save(usuario);
        }
    }

    public Usuario obtenerRequerido(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El usuario indicado no existe."));
    }

}
