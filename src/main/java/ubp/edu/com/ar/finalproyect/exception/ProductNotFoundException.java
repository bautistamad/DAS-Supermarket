package ubp.edu.com.ar.finalproyect.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Integer barCode) {
        super("Product not found with barCode: " + barCode);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
