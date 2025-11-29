package ubp.edu.com.ar.finalproyect.adapter.external.rest.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductoProveedorDTO {

    @JsonProperty("codigoBarra")
    private Integer codigoBarra;

    @JsonProperty("nombre")
    private String nombre;

    @JsonProperty("precio")
    private Float precio;

    public ProductoProveedorDTO() {
    }

    public ProductoProveedorDTO(Integer codigoBarra, String nombre, Float precio) {
        this.codigoBarra = codigoBarra;
        this.nombre = nombre;
        this.precio = precio;
    }

    public Integer getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(Integer codigoBarra) {
        this.codigoBarra = codigoBarra;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Float getPrecio() {
        return precio;
    }

    public void setPrecio(Float precio) {
        this.precio = precio;
    }

    @Override
    public String toString() {
        return "ProductoProveedorDTO{" +
                "codigoBarra=" + codigoBarra +
                ", nombre='" + nombre + '\'' +
                ", precio=" + precio +
                '}';
    }
}
