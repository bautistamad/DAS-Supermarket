package ubp.edu.com.ar.finalproyect.exception;

public class UsuarioNotFoundException extends RuntimeException {
    public UsuarioNotFoundException(String username) {
        super("Usuario with username '" + username + "' not found");
    }
}
