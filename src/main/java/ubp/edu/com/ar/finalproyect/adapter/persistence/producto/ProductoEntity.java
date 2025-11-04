package ubp.edu.com.ar.finalproyect.adapter.persistence.producto;


import java.time.LocalDateTime;

public class ProductoEntity {

    private Integer codigoBarra;
    private String nombre;
    private String imagen;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private Integer stockActual;

    // Producto State Fields
    private LocalDateTime fechaActualizacion;
    private Integer estado;

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getEstadoDescripcion() {
        return estadoDescripcion;
    }

    public void setEstadoDescripcion(String estadoDescripcion) {
        this.estadoDescripcion = estadoDescripcion;
    }

    private String estadoNombre;
    private String estadoDescripcion;

    public ProductoEntity() {};

    public ProductoEntity(Integer codigoBarra, String nombre, String imagen,
                          Integer stockMinimo, Integer stockMaximo, Integer stockActual) {
        this.codigoBarra = codigoBarra;
        this.nombre = nombre;
        this.imagen = imagen;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.stockActual = stockActual;
    }

    public Integer getCodigoBarra() { return codigoBarra; }
    public void setCodigoBarra(Integer codigoBarra) { this.codigoBarra = codigoBarra; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public Integer getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }

}
