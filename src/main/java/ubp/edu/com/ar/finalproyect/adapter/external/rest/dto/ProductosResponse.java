package ubp.edu.com.ar.finalproyect.adapter.external.rest.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductosResponse {

    @SerializedName("productos")
    private List<ProductoProveedorDTO> productos;

    public ProductosResponse() {
    }

    public ProductosResponse(List<ProductoProveedorDTO> productos) {
        this.productos = productos;
    }

    public List<ProductoProveedorDTO> getProductos() {
        return productos;
    }

    public void setProductos(List<ProductoProveedorDTO> productos) {
        this.productos = productos;
    }

    @Override
    public String toString() {
        return "ProductosResponse{" +
                "productos=" + productos +
                '}';
    }
}
