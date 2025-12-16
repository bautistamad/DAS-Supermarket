package ubp.edu.com.ar.finalproyect.adapter.external.rest.dto;

import com.google.gson.annotations.SerializedName;

import java.time.LocalDateTime;

public class EstimacionPedidoDTO {

    @SerializedName("fechaEstimada")
    private LocalDateTime fechaEstimada;

    @SerializedName("precioEstimadoTotal")
    private Float precioEstimadoTotal;

    @SerializedName("productosJson")
    private String productosJson;

    public EstimacionPedidoDTO() {
    }

    public LocalDateTime getFechaEstimada() {
        return fechaEstimada;
    }

    public void setFechaEstimada(LocalDateTime fechaEstimada) {
        this.fechaEstimada = fechaEstimada;
    }

    public Float getPrecioEstimadoTotal() {
        return precioEstimadoTotal;
    }

    public void setPrecioEstimadoTotal(Float precioEstimadoTotal) {
        this.precioEstimadoTotal = precioEstimadoTotal;
    }

    public String getProductosJson() {
        return productosJson;
    }

    public void setProductosJson(String productosJson) {
        this.productosJson = productosJson;
    }

    @Override
    public String toString() {
        return "EstimacionPedidoDTO{" +
                "fechaEstimada=" + fechaEstimada +
                ", precioEstimadoTotal=" + precioEstimadoTotal +
                ", productosJson='" + productosJson + '\'' +
                '}';
    }
}
