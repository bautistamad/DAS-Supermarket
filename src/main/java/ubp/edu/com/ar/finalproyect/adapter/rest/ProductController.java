package ubp.edu.com.ar.finalproyect.adapter.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ubp.edu.com.ar.finalproyect.domain.Product;
import ubp.edu.com.ar.finalproyect.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // Post /api/prpducts = Create a new Product
    @PostMapping
    public ResponseEntity<Product> save(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/products/{barCode} - Get product by barCode
    @GetMapping("/{barCode}")
    public ResponseEntity<Product> getProduct(@PathVariable Integer barCode) {
        return productService.getProduct(barCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/product - Get all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    // DELETE /api/product/{bardCode}
    @DeleteMapping("/{barCode}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer barCode) {
        productService.deleteProduct(barCode);
        return ResponseEntity.noContent().build();
    }

}
