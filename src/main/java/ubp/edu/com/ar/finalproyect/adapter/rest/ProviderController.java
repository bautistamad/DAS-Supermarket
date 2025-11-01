package ubp.edu.com.ar.finalproyect.adapter.rest;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ubp.edu.com.ar.finalproyect.domain.Provider;
import ubp.edu.com.ar.finalproyect.service.ProviderService;

import java.util.List;

@RestController
@RequestMapping("/api/providers")
public class ProviderController {

    private final ProviderService providerService;

    public ProviderController(ProviderService providerService) {
        this.providerService = providerService;
    }

    // POST /api/providers = Create a new Provider

    @PostMapping
    public ResponseEntity<Provider> saveProvider(@RequestBody Provider provider) {
        Provider created = providerService.createProvider(provider);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // GET /api/providers/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Provider> getProvider(@PathVariable Integer id) {
        return providerService.getProvider(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/providers

    @GetMapping
    public ResponseEntity<List<Provider>> getAllProviders() {
        return ResponseEntity.ok(providerService.getAllProviders());
    }

    // DELETE /api/products/{id}
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteProvider(@PathVariable Integer id) {
        providerService.deleteProvider(id);
        return ResponseEntity.noContent().build();
    }
}
