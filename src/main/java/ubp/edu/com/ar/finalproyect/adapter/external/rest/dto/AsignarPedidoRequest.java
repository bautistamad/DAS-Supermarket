package ubp.edu.com.ar.finalproyect.adapter.external.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DTO for sending order assignment request to provider
 * Provider expects: {"Pedido": {"productos": [{"codigoBarra": 1, "cantidad": 10}, ...]}}
 */
public class AsignarPedidoRequest {

    @JsonProperty("Pedido")
    private Map<String, Object> Pedido;

    public AsignarPedidoRequest() {
        this.Pedido = new HashMap<>();
    }

    public AsignarPedidoRequest(List<Map<String, Integer>> productos) {
        this.Pedido = new HashMap<>();
        this.Pedido.put("productos", productos);
    }

    public Map<String, Object> getPedido() {
        return Pedido;
    }

    public void setPedido(Map<String, Object> pedido) {
        this.Pedido = pedido;
    }
}
