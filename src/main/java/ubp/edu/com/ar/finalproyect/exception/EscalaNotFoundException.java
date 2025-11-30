package ubp.edu.com.ar.finalproyect.exception;

public class EscalaNotFoundException extends RuntimeException {

    public EscalaNotFoundException(String message) {
        super(message);
    }

    public EscalaNotFoundException(Integer idEscala) {
        super("Escala not found with id: " + idEscala);
    }
}
