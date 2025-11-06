package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.Pedido;
import ubp.edu.com.ar.finalproyect.domain.PedidoProducto;

import java.util.List;
import java.util.Optional;

public interface PedidoRepository {
    Pedido save(Pedido pedido);
    Pedido update(Pedido pedido);
    Optional<Pedido> findById(Integer id);
    List<Pedido> findAll();
    void deleteById(Integer id);
    List<Pedido> findByProviderId(Integer providerId);
    List<PedidoProducto> findProductsByPedidoId(Integer pedidoId);
}
