package ubp.edu.com.ar.finalproyect.domain;

public class Proveedor {

    private Integer id;
    private String name;
    private String apiEndpoint;
    private Integer tipoServicio;
    private String apiKey;

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

    public Proveedor() {
    }

    public Proveedor(Integer id, String name, String apiEndpoint, Integer tipoServicio, String apiKey) {
        this.id = id;
        this.name = name;
        this.apiEndpoint = apiEndpoint;
        this.tipoServicio = tipoServicio;
        this.apiKey = apiKey;
    }
}
