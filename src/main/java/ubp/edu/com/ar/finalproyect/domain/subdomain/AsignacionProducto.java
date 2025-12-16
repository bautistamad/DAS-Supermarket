package ubp.edu.com.ar.finalproyect.domain.subdomain;

public class AsignacionProducto {
    private Integer codigoBarra;
    private Integer codigoBarraProveedor;
    private Integer cantidad;
    private Float precio;
    private String productoNombre;

    public AsignacionProducto(Integer codigoBarra, Integer codigoBarraProveedor,
                              Integer cantidad, Float precio, String productoNombre) {
        this.codigoBarra = codigoBarra;
        this.codigoBarraProveedor = codigoBarraProveedor;
        this.cantidad = cantidad;
        this.precio = precio;
        this.productoNombre = productoNombre;
    }

    public Float getCostoTotal() {
        return precio * cantidad;
    }

    public Integer getCodigoBarra() {
        return codigoBarra;
    }

    public Integer getCodigoBarraProveedor() {
        return codigoBarraProveedor;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public Float getPrecio() {
        return precio;
    }

    public String getProductoNombre() {
        return productoNombre;
    }
}
