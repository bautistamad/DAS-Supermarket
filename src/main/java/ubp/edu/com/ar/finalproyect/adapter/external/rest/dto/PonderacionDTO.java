package ubp.edu.com.ar.finalproyect.adapter.external.rest.dto;

public class PonderacionDTO {
    private Integer id;
    private Integer puntuacion;
    private String descripcion;

    public PonderacionDTO() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(Integer puntuacion) {
        this.puntuacion = puntuacion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "PonderacionDTO{" +
                "id=" + id +
                ", puntuacion=" + puntuacion +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
