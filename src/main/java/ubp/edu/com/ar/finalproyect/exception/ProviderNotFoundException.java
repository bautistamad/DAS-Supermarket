package ubp.edu.com.ar.finalproyect.exception;

public class ProviderNotFoundException extends RuntimeException {

    public ProviderNotFoundException(Integer id) {
        super("Provider not found with id: " + id);
    }

    public ProviderNotFoundException(String message) {
        super(message);
    }
}
