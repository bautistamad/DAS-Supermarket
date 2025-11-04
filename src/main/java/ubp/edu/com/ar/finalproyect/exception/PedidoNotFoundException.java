package ubp.edu.com.ar.finalproyect.exception;

public class PedidoNotFoundException extends RuntimeException {

    public PedidoNotFoundException(Integer id) {
        super("Pedido not found with id: " + id);
    }

    public PedidoNotFoundException(String message) {
        super(message);
    }
}
