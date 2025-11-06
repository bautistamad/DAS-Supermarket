package ubp.edu.com.ar.finalproyect.service;

import org.springframework.stereotype.Service;
import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.PedidoProducto;
import ubp.edu.com.ar.finalproyect.exception.PedidoNotFoundException;
import ubp.edu.com.ar.finalproyect.port.PedidoRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class PedidoService {

    private final PedidoRepository repository;

    public PedidoService(PedidoRepository repository) {
        this.repository = repository;
    }

    public Pedido createPedido(Pedido pedido) {
        // Validate input
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido cannot be null");
        }
        if (pedido.getEstadoId() == null) {
            throw new IllegalArgumentException("Pedido estado cannot be null");
        }
        if (pedido.getProveedorId() == null) {
            throw new IllegalArgumentException("Pedido proveedor cannot be null");
        }

        return repository.save(pedido);
    }

    public Pedido updatePedido(Pedido pedido) {
        // Validate input
        if (pedido == null) {
            throw new IllegalArgumentException("Pedido cannot be null");
        }
        if (pedido.getId() == null) {
            throw new IllegalArgumentException("Pedido id cannot be null");
        }
        if (pedido.getEstadoId() == null) {
            throw new IllegalArgumentException("Pedido estado cannot be null");
        }

        // Check if pedido exists
        repository.findById(pedido.getId())
                .orElseThrow(() -> new PedidoNotFoundException(pedido.getId()));

        return repository.update(pedido);
    }

    public Pedido getPedido(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Pedido id cannot be null");
        }
        return repository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
    }

    public List<Pedido> getAllPedidos() {
        return repository.findAll();
    }

    public void deletePedido(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("Pedido id cannot be null");
        }
        repository.findById(id)
                .orElseThrow(() -> new PedidoNotFoundException(id));
        repository.deleteById(id);
    }

    public List<Pedido> getPedidosByProveedor(Integer providerId) {

        List<Pedido> pedidos = new ArrayList<>();

        if (providerId == null) {
            throw new IllegalArgumentException("Proveedor id cannot be null");
        }

        pedidos = repository.findByProviderId(providerId);

        for (Pedido pedido : pedidos) {
            List<PedidoProducto> pedidosProducto = getProductosByPedido(pedido.getId());
            pedido.setProductos(pedidosProducto);
        }

        return pedidos;
    }

    public List<PedidoProducto> getProductosByPedido(Integer pedidoId) {
        if (pedidoId == null) {
            throw new IllegalArgumentException("Pedido id cannot be null");
        }
        // Validate pedido exists
        repository.findById(pedidoId)
                .orElseThrow(() -> new PedidoNotFoundException(pedidoId));
        return repository.findProductsByPedidoId(pedidoId);
    }
}
