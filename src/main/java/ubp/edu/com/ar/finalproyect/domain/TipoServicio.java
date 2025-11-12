package ubp.edu.com.ar.finalproyect.domain;

public enum TipoServicio {

    REST(1, "REST"),

    SOAP(2, "SOAP");

    private final Integer value;
    private final String nombre;

    TipoServicio(Integer value, String nombre) {
        this.value = value;
        this.nombre = nombre;
    }

    public Integer getValue() {
        return value;
    }

    public String getNombre() {
        return nombre;
    }

    public static TipoServicio fromValue(Integer value) {
        if (value == null) {
            throw new IllegalArgumentException("TipoServicio value cannot be null");
        }

        for (TipoServicio tipo : TipoServicio.values()) {
            if (tipo.value.equals(value)) {
                return tipo;
            }
        }

        throw new IllegalArgumentException(
            "Invalid TipoServicio value: " + value +
            ". Valid values are: 1 (REST), 2 (SOAP)"
        );
    }

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }

        for (TipoServicio tipo : TipoServicio.values()) {
            if (tipo.value.equals(value)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public String toString() {
        return nombre + " (" + value + ")";
    }
}
