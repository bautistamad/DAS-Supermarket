package ubp.edu.com.ar.finalproyect.service;

import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.Usuario;
import ubp.edu.com.ar.finalproyect.exception.InvalidCredentialsException;
import ubp.edu.com.ar.finalproyect.port.UsuarioRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    /**
     * Crea un nuevo usuario con password hasheado
     */
    public Usuario createUsuario(String username, String email, String password) {
        // Validar inputs
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }

        logger.info("Creating new user: {}", username);

        // Hashear password con BCrypt
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());

        // Crear usuario

        Usuario usuario = new Usuario();
        usuario.setUsername(username);
        usuario.setEmail(email);
        usuario.setPasswordHash(passwordHash);

        return repository.save(usuario);
    }

    /**
     * Valida credenciales y retorna el usuario si son correctas
     */
    public Usuario login(String username, String password) {
        if (username == null || password == null) {
            throw new IllegalArgumentException("Username and password are required");
        }

        logger.info("Login attempt for user: {}", username);

        // Buscar usuario
        Optional<Usuario> usuarioOpt = repository.findByUsername(username);

        if (usuarioOpt.isEmpty()) {
            logger.warn("Login failed: user not found - {}", username);
            throw new InvalidCredentialsException("Invalid username or password");
        }

        Usuario usuario = usuarioOpt.get();

        // Verificar password con BCrypt
        boolean passwordMatches = BCrypt.checkpw(password, usuario.getPasswordHash());

        if (!passwordMatches) {
            logger.warn("Login failed: invalid password for user - {}", username);
            throw new InvalidCredentialsException("Invalid username or password");
        }

        logger.info("Login successful for user: {}", username);
        return usuario;
    }

    /**
     * Busca usuario por username
     */
    public Optional<Usuario> getUsuarioByUsername(String username) {
        return repository.findByUsername(username);
    }

    /**
     * Lista todos los usuarios (para admin futuro)
     */
    public List<Usuario> getAllUsuarios() {
        return repository.findAll();
    }
}
