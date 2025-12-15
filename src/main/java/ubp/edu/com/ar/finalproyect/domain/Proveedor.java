package ubp.edu.com.ar.finalproyect.domain;

public class Proveedor {

    private Integer id;
    private String name;
    private String apiEndpoint;
    private Integer tipoServicio;
    private String tipoServicioNombre;
    private String clientId;
    private String apiKey;
    private Double ratingPromedio;
    private Boolean activo;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getApiEndpoint() {
        return apiEndpoint;
    }

    public void setApiEndpoint(String apiEndpoint) {
        this.apiEndpoint = apiEndpoint;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public Integer getTipoServicio() {
        return tipoServicio;
    }

    public void setTipoServicio(Integer tipoServicio) {
        this.tipoServicio = tipoServicio;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }

    public Proveedor() {
    }

    public Proveedor(Integer id, String name, String apiEndpoint, Integer tipoServicio, String tipoServicioNombre, String clientId, String apiKey) {
        this.id = id;
        this.name = name;
        this.apiEndpoint = apiEndpoint;
        this.tipoServicio = tipoServicio;
        this.tipoServicioNombre = tipoServicioNombre;
        this.clientId = clientId;
        this.apiKey = apiKey;
    }

    public Proveedor(Integer id, String name, String apiEndpoint, Integer tipoServicio, String tipoServicioNombre, String clientId, String apiKey, Double ratingPromedio) {
        this.id = id;
        this.name = name;
        this.apiEndpoint = apiEndpoint;
        this.tipoServicio = tipoServicio;
        this.tipoServicioNombre = tipoServicioNombre;
        this.clientId = clientId;
        this.apiKey = apiKey;
        this.ratingPromedio = ratingPromedio;
    }
}
