package ubp.edu.com.ar.finalproyect.adapter.persistence.proveedor;


public class ProveedorEntity {

    private Integer id;
    private String nombre;
    private String servicio;
    private Integer tipoServicio;
    private Integer escala;

    public ProveedorEntity() {}

    public ProveedorEntity(Integer id, String nombre, String servicio, Integer tipoServicio, Integer escala) {
        this.id = id;
        this.nombre = nombre;
        this.servicio = servicio;
        this.tipoServicio = tipoServicio;
        this.escala = escala;
    }

    // Getters
    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getServicio() {
        return servicio;
    }

    public Integer getTipoServicio() {
        return tipoServicio;
    }

    public Integer getEscala() {
        return escala;
    }

    // Setters (required for BeanPropertyRowMapper)
    public void setId(Integer id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public void setTipoServicio(Integer tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public void setEscala(Integer escala) {
        this.escala = escala;
    }
}
