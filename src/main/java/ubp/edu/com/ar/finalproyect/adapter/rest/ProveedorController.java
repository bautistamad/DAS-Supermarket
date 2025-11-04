package ubp.edu.com.ar.finalproyect.adapter.rest;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ubp.edu.com.ar.finalproyect.domain.Proveedor;
import ubp.edu.com.ar.finalproyect.service.ProveedorService;

import java.util.List;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;

    public ProveedorController(ProveedorService proveedorService) {
        this.proveedorService = proveedorService;
    }

    // POST /api/providers = Create a new Proveedor

    @PostMapping
    public ResponseEntity<Proveedor> saveProvider(@RequestBody Proveedor proveedor) {
        Proveedor created = proveedorService.createProveedor(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/providers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> getProvider(@PathVariable Integer id) {
        return proveedorService.getProveedor(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/providers
    @GetMapping
    public ResponseEntity<List<Proveedor>> getAllProviders() {
        return ResponseEntity.ok(proveedorService.getAllProveedores());
    }

    // DELETE /api/products/{id}
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Integer id) {
        proveedorService.deleteProveedor(id);
        return ResponseEntity.noContent().build();
    }
}
