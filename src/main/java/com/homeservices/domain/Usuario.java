package com.homeservices.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;

@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;

    @NotBlank
    @Column(name = "nombre_completo", nullable = false, length = 120)
    private String nombreCompleto;

    @Email
    @NotBlank
    @Column(name = "correo", nullable = false, unique = true, length = 120)
    private String correo;

    @NotBlank
    @Column(name = "password", nullable = false, length = 120)
    private String password;

    @Column(name = "rol", nullable = false, length = 30)
    private String rol = "CLIENTE";

    @Column(name = "telefono", length = 30)
    private String telefono;

    @Column(name = "ubicacion", length = 120)
    private String ubicacion;

    @Column(name = "activo")
    private Boolean activo = true;

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public Boolean getActivo() { return activo; }
    public void setActivo(Boolean activo) { this.activo = activo; }
}
