package ubp.edu.com.ar.finalproyect.domain;

public class EscalaDefinicion {

    private String valor;         // Scale value from provider (e.g., "Excelente", "Bueno", "Regular")
    private String descripcion;   // Description of what this value means

    // Constructors
    public EscalaDefinicion() {}

    public EscalaDefinicion(String valor, String descripcion) {
        this.valor = valor;
        this.descripcion = descripcion;
    }

    // Getters and Setters
    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "EscalaDefinicion{" +
                "valor='" + valor + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
