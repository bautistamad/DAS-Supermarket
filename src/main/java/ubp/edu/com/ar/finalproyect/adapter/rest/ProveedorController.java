package ubp.edu.com.ar.finalproyect.adapter.rest;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ubp.edu.com.ar.finalproyect.domain.Proveedor;
import ubp.edu.com.ar.finalproyect.service.ProveedorIntegrationService;
import ubp.edu.com.ar.finalproyect.service.ProveedorService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/proveedores")
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final ProveedorIntegrationService integrationService;

    public ProveedorController(ProveedorService proveedorService,
                              ProveedorIntegrationService integrationService) {
        this.proveedorService = proveedorService;
        this.integrationService = integrationService;
    }

    // POST /api/proveedores = Create a new Proveedor

    @PostMapping
    public ResponseEntity<Proveedor> saveProveedor(@RequestBody Proveedor proveedor) {
        Proveedor created = proveedorService.createProveedor(proveedor);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/proveedores/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Proveedor> getProveedor(@PathVariable Integer id) {
        return proveedorService.getProveedor(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    // GET /api/proveedores
    @GetMapping
    public ResponseEntity<List<Proveedor>> getAllProveedores() {
        return ResponseEntity.ok(proveedorService.getAllProveedores());
    }

    // DELETE /api/products/{id}
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProveedor(@PathVariable Integer id) {
        proveedorService.deleteProveedor(id);
        return ResponseEntity.noContent().build();
    }

    // POST /api/proveedores/{id}/sync - Sync products and prices from external provider
    @PostMapping("/{id}/sync")
    public ResponseEntity<Map<String, Integer>> syncProductos(@PathVariable Integer id) {
        Map<String, Integer> result = integrationService.syncProductosFromProveedor(id);
        return ResponseEntity.ok(result);
    }
}
