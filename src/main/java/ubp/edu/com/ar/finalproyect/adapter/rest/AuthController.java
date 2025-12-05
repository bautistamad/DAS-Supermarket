package ubp.edu.com.ar.finalproyect.adapter.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ubp.edu.com.ar.finalproyect.adapter.rest.dto.LoginRequest;
import ubp.edu.com.ar.finalproyect.adapter.rest.dto.RegisterRequest;
import ubp.edu.com.ar.finalproyect.adapter.rest.dto.UsuarioResponse;
import ubp.edu.com.ar.finalproyect.domain.Usuario;
import ubp.edu.com.ar.finalproyect.service.UsuarioService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * POST /api/auth/login
     * Valida credenciales y retorna datos del usuario
     */
    @PostMapping("/login")
    public ResponseEntity<UsuarioResponse> login(@RequestBody LoginRequest request) {
        Usuario usuario = usuarioService.login(
            request.getUsername(),
            request.getPassword()
        );

        UsuarioResponse response = UsuarioResponse.fromUsuario(usuario);
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/auth/register
     * Crea un nuevo usuario
     */
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@RequestBody RegisterRequest request) {
        Usuario usuario = usuarioService.createUsuario(
            request.getUsername(),
            request.getEmail(),
            request.getPassword()
        );

        UsuarioResponse response = UsuarioResponse.fromUsuario(usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
