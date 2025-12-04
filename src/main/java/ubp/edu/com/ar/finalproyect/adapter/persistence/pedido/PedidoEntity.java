package ubp.edu.com.ar.finalproyect.adapter.persistence.pedido;

import java.time.LocalDateTime;

public class PedidoEntity {
    private Integer id;
    private Integer estado;
    private Integer proveedor;
    private Integer idPedidoProveedor;  // ID del pedido en el sistema del proveedor
    private Integer puntuacion;
    private LocalDateTime fechaEstimada;
    private LocalDateTime fechaEntrega;
    private LocalDateTime fechaRegistro;
    private Integer evaluacionEscala;

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
        this.fechaEstimada = fechaCreada;
        this.fechaEntrega = fechaEntrega;
        this.fechaRegistro = fechaRegistro;
        this.evaluacionEscala = evaluacion;
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

    public Integer getIdPedidoProveedor() {
        return idPedidoProveedor;
    }

    public void setIdPedidoProveedor(Integer idPedidoProveedor) {
        this.idPedidoProveedor = idPedidoProveedor;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public LocalDateTime getFechaEstimada() {
        return fechaEstimada;
    }

    public void setFechaEstimada(LocalDateTime fechaEstimada) {
        this.fechaEstimada = fechaEstimada;
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

    public Integer getEvaluacionEscala() {
        return evaluacionEscala;
    }

    public void setEvaluacionEscala(Integer evaluacionEscala) {
        this.evaluacionEscala = evaluacionEscala;
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
