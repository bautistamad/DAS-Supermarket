package ubp.edu.com.ar.finalproyect.domain;

import java.time.LocalDateTime;

public class ProductoProveedor {

    private Integer idProveedor;
    private Integer codigoBarra;
    private Integer codigoBarraProveedor;
    private Integer estado;
    private LocalDateTime fechaActualizacion;

    // Enriched fields (from JOINs)
    private String productoNombre;
    private String proveedorNombre;

    public ProductoProveedor() {
    }

    public ProductoProveedor(Integer idProveedor, Integer codigoBarra,
                            Integer codigoBarraProveedor, Integer estado) {
        this.idProveedor = idProveedor;
        this.codigoBarra = codigoBarra;
        this.codigoBarraProveedor = codigoBarraProveedor;
        this.estado = estado;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(Integer codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public Integer getCodigoBarraProveedor() {
        return codigoBarraProveedor;
    }

    public void setCodigoBarraProveedor(Integer codigoBarraProveedor) {
        this.codigoBarraProveedor = codigoBarraProveedor;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }
}
