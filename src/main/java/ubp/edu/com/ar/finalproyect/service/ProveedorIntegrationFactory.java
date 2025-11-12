package ubp.edu.com.ar.finalproyect.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ubp.edu.com.ar.finalproyect.domain.TipoServicio;
import ubp.edu.com.ar.finalproyect.port.ProveedorIntegration;

@Component
public class ProveedorIntegrationFactory {

    private final ProveedorIntegration restProveedorAdapter;
    private final ProveedorIntegration soapProveedorAdapter;

    @Autowired
    public ProveedorIntegrationFactory(
            ProveedorIntegration restProveedorAdapter,
            ProveedorIntegration soapProveedorAdapter) {
        this.restProveedorAdapter = restProveedorAdapter;
        this.soapProveedorAdapter = soapProveedorAdapter;
    }

    public ProveedorIntegration getAdapter(Integer tipoServicio) {
        if (tipoServicio == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }

        return switch (tipoServicio) {
            case 1 -> restProveedorAdapter;
            case 2 -> soapProveedorAdapter;
            default -> throw new IllegalArgumentException(
                    "Unsupported service type: " + tipoServicio +
                            ". Supported types are: 1 (REST), 2 (SOAP)"
            );
        };
    }

    public ProveedorIntegration getAdapter(TipoServicio tipoServicio) {
        if (tipoServicio == null) {
            throw new IllegalArgumentException("Service type cannot be null");
        }
        return getAdapter(tipoServicio.getValue());
    }
}
