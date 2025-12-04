package ubp.edu.com.ar.finalproyect.adapter.persistence.proveedor;


public class ProveedorEntity {

    private Integer id;
    private String nombre;
    private String apiEndpoint;
    private Integer tipoServicio;
    private String tipoServicioNombre;
    private String clientId;
    private String apiKey;
    private Double ratingPromedio;

    public ProveedorEntity() {
    }

    public ProveedorEntity(Integer id, String nombre, String apiEndpoint, Integer tipoServicio, String tipoServicioNombre, String clientId, String apiKey) {
        this.id = id;
        this.nombre = nombre;
        this.apiEndpoint = apiEndpoint;
        this.tipoServicio = tipoServicio;
        this.tipoServicioNombre = tipoServicioNombre;
        this.clientId = clientId;
        this.apiKey = apiKey;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public Integer getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(Integer tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getTipoServicioNombre() {
        return tipoServicioNombre;
    }

    public void setTipoServicioNombre(String tipoServicioNombre) {
        this.tipoServicioNombre = tipoServicioNombre;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Double getRatingPromedio() {
        return ratingPromedio;
    }

    public void setRatingPromedio(Double ratingPromedio) {
        this.ratingPromedio = ratingPromedio;
    }
}

