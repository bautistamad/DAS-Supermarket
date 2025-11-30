package ubp.edu.com.ar.finalproyect.adapter.persistence.escala;

public class EscalaEntity {

    private Integer idEscala;
    private Integer idProveedor;
    private Integer escalaInt;
    private String escalaExt;
    private String descripcionExt;

    // Constructors
    public EscalaEntity() {}

    public EscalaEntity(Integer idEscala, Integer idProveedor, Integer escalaInt,
                       String escalaExt, String descripcionExt) {
        this.idEscala = idEscala;
        this.idProveedor = idProveedor;
        this.escalaInt = escalaInt;
        this.escalaExt = escalaExt;
        this.descripcionExt = descripcionExt;
    }

    // Getters and Setters
    public Integer getIdEscala() {
        return idEscala;
    }

    public void setIdEscala(Integer idEscala) {
        this.idEscala = idEscala;
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public Integer getEscalaInt() {
        return escalaInt;
    }

    public void setEscalaInt(Integer escalaInt) {
        this.escalaInt = escalaInt;
    }

    public String getEscalaExt() {
        return escalaExt;
    }

    public void setEscalaExt(String escalaExt) {
        this.escalaExt = escalaExt;
    }

    public String getDescripcionExt() {
        return descripcionExt;
    }

    public void setDescripcionExt(String descripcionExt) {
        this.descripcionExt = descripcionExt;
    }
}
