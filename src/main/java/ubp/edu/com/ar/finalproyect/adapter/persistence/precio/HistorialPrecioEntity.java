package ubp.edu.com.ar.finalproyect.adapter.persistence.precio;

import java.time.LocalDateTime;

public class HistorialPrecioEntity {

    private Integer codigoBarra;
    private Integer idProveedor;
    private Float precio;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;

    // Enriched fields from JOINs
    private String proveedorNombre;
    private String productoNombre;

    public HistorialPrecioEntity() {
    }

    public HistorialPrecioEntity(Integer codigoBarra, Integer idProveedor, Float precio,
                                 LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.codigoBarra = codigoBarra;
        this.idProveedor = idProveedor;
        this.precio = precio;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }

    public Integer getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(Integer codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public LocalDateTime getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(LocalDateTime fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }

    public String getProductoNombre() {
        return productoNombre;
    }

    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }
}
