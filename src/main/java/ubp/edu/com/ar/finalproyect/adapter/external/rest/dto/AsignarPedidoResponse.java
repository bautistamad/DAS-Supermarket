package ubp.edu.com.ar.finalproyect.adapter.external.rest.dto;

import com.google.gson.annotations.SerializedName;

import java.util.Map;


public class AsignarPedidoResponse {

    @SerializedName("Pedido")
    private Map<String, Object> Pedido;

    public AsignarPedidoResponse() {
    }

    public Map<String, Object> getPedido() {
        return Pedido;
    }

    public void setPedido(Map<String, Object> pedido) {
        this.Pedido = pedido;
    }
}
