package ubp.edu.com.ar.finalproyect.domain;

public class PedidoProducto {

    private Integer idPedido;
    private Integer codigoBarra;
    private Integer cantidad;

    // Product information
    private String productoNombre;
    private String productoImagen;

    public PedidoProducto() {
    }

    public PedidoProducto(Integer idPedido, Integer codigoBarra, Integer cantidad, Float precio) {
        this.idPedido = idPedido;
        this.codigoBarra = codigoBarra;
        this.cantidad = cantidad;
    }

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public Integer getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(Integer codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public String getProductoImagen() {
        return productoImagen;
    }

    public void setProductoImagen(String productoImagen) {
        this.productoImagen = productoImagen;
    }
}
