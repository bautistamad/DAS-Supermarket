package ubp.edu.com.ar.finalproyect.exception;

public class ProductoNotFoundException extends RuntimeException {

    public ProductoNotFoundException(Integer barCode) {
        super("Producto not found with barCode: " + barCode);
    }

    public ProductoNotFoundException(String message) {
        super(message);
    }
}
