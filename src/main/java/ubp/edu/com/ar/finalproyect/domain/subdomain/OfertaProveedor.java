package ubp.edu.com.ar.finalproyect.domain.subdomain;

public class OfertaProveedor {
    private Integer proveedorId;
    private String proveedorNombre;
    private Integer codigoBarraProveedor;
    private Float precio;
    private Double rating;

    public OfertaProveedor(Integer proveedorId, String proveedorNombre,
                           Integer codigoBarraProveedor, Float precio, Double rating) {
        this.proveedorId = proveedorId;
        this.proveedorNombre = proveedorNombre;
        this.codigoBarraProveedor = codigoBarraProveedor;
        this.precio = precio;
        this.rating = rating != null ? rating : 0.0;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public Integer getCodigoBarraProveedor() {
        return codigoBarraProveedor;
    }

    public Float getPrecio() {
        return precio;
    }

    public Double getRating() {
        return rating;
    }
}
