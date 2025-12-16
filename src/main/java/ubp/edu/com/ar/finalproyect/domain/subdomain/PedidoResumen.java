package ubp.edu.com.ar.finalproyect.domain.subdomain;

public class PedidoResumen {
    private Integer pedidoId;
    private String proveedorNombre;
    private Integer proveedorId;
    private Integer cantidadProductos;
    private Float costoTotal;
    private Double ratingProveedor;

    public PedidoResumen() {}

    public Integer getPedidoId() {
        return pedidoId;
    }

    public void setPedidoId(Integer pedidoId) {
        this.pedidoId = pedidoId;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Integer proveedorId) {
        this.proveedorId = proveedorId;
    }

    public Integer getCantidadProductos() {
        return cantidadProductos;
    }

    public void setCantidadProductos(Integer cantidadProductos) {
        this.cantidadProductos = cantidadProductos;
    }

    public Float getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(Float costoTotal) {
        this.costoTotal = costoTotal;
    }

    public Double getRatingProveedor() {
        return ratingProveedor;
    }

    public void setRatingProveedor(Double ratingProveedor) {
        this.ratingProveedor = ratingProveedor;
    }
}
