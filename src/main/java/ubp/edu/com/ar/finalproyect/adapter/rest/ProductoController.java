package ubp.edu.com.ar.finalproyect.adapter.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ubp.edu.com.ar.finalproyect.domain.Producto;
import ubp.edu.com.ar.finalproyect.service.ProductoService;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // POST /api/products - Create a new Producto
    @PostMapping
    public ResponseEntity<Producto> save(@RequestBody Producto producto) {
        Producto created = productoService.createProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/products/{barCode} - Get product by barCode
    @GetMapping("/{barCode}")
    public ResponseEntity<Producto> getProduct(@PathVariable Integer barCode) {
        Producto producto = productoService.getProducto(barCode);
        return ResponseEntity.ok(producto);
    }

    // GET /api/products - Get all products
    @GetMapping
    public ResponseEntity<List<Producto>> getAllProducts() {
        return ResponseEntity.ok(productoService.getAllProductos());
    }

    // DELETE /api/products/{barCode}
    @DeleteMapping("/{barCode}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer barCode) {
        productoService.deleteProducto(barCode);
        return ResponseEntity.noContent().build();
    }

    // GET /api/products/provider/{id} - Get products by provider ID
    @GetMapping("/proveedor/{id}")
    public ResponseEntity<List<Producto>> getProductsByProvider(@PathVariable Integer id) {
        List<Producto> productos = productoService.getProductoByProveedor(id);
        return ResponseEntity.ok(productos);
    }

}
