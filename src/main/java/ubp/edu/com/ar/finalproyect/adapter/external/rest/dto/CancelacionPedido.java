package ubp.edu.com.ar.finalproyect.adapter.external.rest.dto;

import com.google.gson.annotations.SerializedName;

public class CancelacionPedido {
    private String idPedido;
    private String estado;

    @SerializedName("descripcion")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isCancelled() {
        return estado != null && !"null".equalsIgnoreCase(estado);
    }
}
