package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.ProductoProveedor;

public interface ProductoProveedorRepository {
    ProductoProveedor assign(ProductoProveedor assignment);

    void unassign(Integer codigoBarra, Integer idProveedor);
}
