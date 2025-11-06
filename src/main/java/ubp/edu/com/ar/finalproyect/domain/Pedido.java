package ubp.edu.com.ar.finalproyect.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Pedido {

    private Integer id;
    private Integer estadoId;
    private Integer proveedorId;
    private Integer puntuacion;
    private LocalDateTime fechaCreada;
    private LocalDateTime fechaEntrega;
    private Integer evaluacion;
    private LocalDateTime fechaRegistro;

    // Enriched fields from JOINs
    private String estadoNombre;
    private String estadoDescripcion;
    private String proveedorNombre;

    // Products in this order
    private List<PedidoProducto> productos = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(Integer id, Integer estadoId, Integer proveedorId, Integer puntuacion,
                  LocalDateTime fechaCreada, LocalDateTime fechaEntrega,
                  Integer evaluacion, LocalDateTime fechaRegistro) {
        this.id = id;
        this.estadoId = estadoId;
        this.proveedorId = proveedorId;
        this.puntuacion = puntuacion;
        this.fechaCreada = fechaCreada;
        this.fechaEntrega = fechaEntrega;
        this.evaluacion = evaluacion;
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }

    public Integer getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Integer proveedorId) {
        this.proveedorId = proveedorId;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public LocalDateTime getFechaCreada() {
        return fechaCreada;
    }

    public void setFechaCreada(LocalDateTime fechaCreada) {
        this.fechaCreada = fechaCreada;
    }

    public LocalDateTime getFechaEntrega() {
        return fechaEntrega;
    }

    public void setFechaEntrega(LocalDateTime fechaEntrega) {
        this.fechaEntrega = fechaEntrega;
    }

    public Integer getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Integer evaluacion) {
        this.evaluacion = evaluacion;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    public String getEstadoDescripcion() {
        return estadoDescripcion;
    }

    public void setEstadoDescripcion(String estadoDescripcion) {
        this.estadoDescripcion = estadoDescripcion;
    }

    public String getProveedorNombre() {
        return proveedorNombre;
    }

    public void setProveedorNombre(String proveedorNombre) {
        this.proveedorNombre = proveedorNombre;
    }

    public List<PedidoProducto> getProductos() {
        return productos;
    }

    public void setProductos(List<PedidoProducto> productos) {
        this.productos = productos;
    }
}
