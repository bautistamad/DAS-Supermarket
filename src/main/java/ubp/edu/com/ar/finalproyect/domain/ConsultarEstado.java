package ubp.edu.com.ar.finalproyect.domain;

public class ConsultarEstado {

    private Integer idPedido    ;
    private String estado;

    public Integer getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(Integer idPedido) {
        this.idPedido = idPedido;
    }

    public String getNombreEstado() {
        return estado;
    }

    public void setNombreEstado(String estado) {
        this.estado = estado;
    }
}
