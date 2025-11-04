package ubp.edu.com.ar.finalproyect.adapter.persistence.pedido;

import java.time.LocalDateTime;

public class PedidoEntity {
    private Integer id;
    private Integer estado;
    private Integer proveedor;
    private Integer puntuacion;
    private LocalDateTime fechaCreada;
    private LocalDateTime fechaEntrega;
    private LocalDateTime fechaRegistro;
    private Integer evaluacion;

    // Enriched fields from JOINs
    private String estadoNombre;
    private String estadoDescripcion;
    private String proveedorNombre;

    public PedidoEntity() {
    }

    public PedidoEntity(Integer id, Integer estado, Integer proveedor, Integer puntuacion,
                        LocalDateTime fechaCreada, LocalDateTime fechaEntrega,
                        LocalDateTime fechaRegistro, Integer evaluacion) {
        this.id = id;
        this.estado = estado;
        this.proveedor = proveedor;
        this.puntuacion = puntuacion;
        this.fechaCreada = fechaCreada;
        this.fechaEntrega = fechaEntrega;
        this.fechaRegistro = fechaRegistro;
        this.evaluacion = evaluacion;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public Integer getProveedor() {
        return proveedor;
    }

    public void setProveedor(Integer proveedor) {
        this.proveedor = proveedor;
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

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public Integer getEvaluacion() {
        return evaluacion;
    }

    public void setEvaluacion(Integer evaluacion) {
        this.evaluacion = evaluacion;
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
}
