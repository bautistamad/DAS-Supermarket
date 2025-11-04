package ubp.edu.com.ar.finalproyect.domain;

public class Proveedor {

    private Integer id;
    private String name;
    private String service;
    private Integer serviceType;
    private Integer scale;

    public Proveedor() {}

    public Proveedor(Integer id, String name, String service, Integer serviceType, Integer scale) {
        this.id = id;
        this.name = name;
        this.service = service;
        this.serviceType = serviceType;
        this.scale = scale;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public Integer getServiceType() {
        return serviceType;
    }

    public void setServiceType(Integer serviceType) {
        this.serviceType = serviceType;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }
}
