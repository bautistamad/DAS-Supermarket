package ubp.edu.com.ar.finalproyect.adapter.rest.dto;

import ubp.edu.com.ar.finalproyect.domain.Usuario;

import java.time.LocalDateTime;

public class UsuarioResponse {
    private Integer id;
    private String username;
    private String email;
    private LocalDateTime fechaCreacion;

    // Constructor desde Usuario domain
    public static UsuarioResponse fromUsuario(Usuario usuario) {
        UsuarioResponse response = new UsuarioResponse();
        response.setId(usuario.getId());
        response.setUsername(usuario.getUsername());
        response.setEmail(usuario.getEmail());
        response.setFechaCreacion(usuario.getFechaCreacion());
        return response;
    }

    // Getters y setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
}
