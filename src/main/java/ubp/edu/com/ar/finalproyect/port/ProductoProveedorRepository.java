package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.ProductoProveedor;

public interface ProductoProveedorRepository {
    ProductoProveedor assign(ProductoProveedor assignment);

    void unassign(Integer codigoBarra, Integer idProveedor);

    /**
     * Find producto-proveedor mapping
     * Used to check if provider has a specific product and get provider's barcode
     * @param idProveedor Provider ID
     * @param codigoBarra Product barcode
     * @return ProductoProveedor mapping or null if not found
     */
    ProductoProveedor findByProveedorAndProducto(Integer idProveedor, Integer codigoBarra);
}
