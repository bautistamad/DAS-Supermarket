package ubp.edu.com.ar.finalproyect.adapter.persistence.proveedor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Proveedor")
public class ProviderEntity {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "servicio")
    private String servicio;

    @Column(name = "tipoServicio")
    private Integer tipoServicio;

    @Column(name = "escala")
    private Integer escala;

    public ProviderEntity() {}

    public ProviderEntity(Integer id, String nombre, String servicio, Integer tipoServicio, Integer escala) {
        this.id = id;
        this.nombre = nombre;
        this.servicio = servicio;
        this.tipoServicio = tipoServicio;
        this.escala = escala;
    }


    public String getServicio() {
        return servicio;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getId() {
        return id;
    }

    public Integer getTipoServicio() {
        return tipoServicio;
    }

    public Integer getEscala() {
        return escala;
    }
}
