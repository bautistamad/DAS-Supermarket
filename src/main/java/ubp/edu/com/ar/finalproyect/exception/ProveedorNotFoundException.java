package ubp.edu.com.ar.finalproyect.exception;

public class ProveedorNotFoundException extends RuntimeException {

    public ProveedorNotFoundException(Integer id) {
        super("Proveedor not found with id: " + id);
    }

    public ProveedorNotFoundException(String message) {
        super(message);
    }
}
