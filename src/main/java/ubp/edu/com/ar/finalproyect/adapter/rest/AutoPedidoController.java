package ubp.edu.com.ar.finalproyect.adapter.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ubp.edu.com.ar.finalproyect.service.AutoPedidoService;

import java.util.Map;

@RestController
@RequestMapping("/api/pedidos")
public class AutoPedidoController {

    private static final Logger logger = LoggerFactory.getLogger(AutoPedidoController.class);
    private final AutoPedidoService autoPedidoService;

    public AutoPedidoController(AutoPedidoService autoPedidoService) {
        this.autoPedidoService = autoPedidoService;
    }

    /**
     * Generate automatic orders for products below minimum stock using optimized two-stage algorithm
     *
     * Algorithm:
     * STAGE 0: Synchronize prices from all active providers
     * STAGE 1: Greedy assignment by best price (select cheapest provider for each product)
     * STAGE 2: Consolidate small orders (move single-item orders to larger providers)
     * STAGE 3: Create final orders in the database
     *
     * This optimized approach:
     * - Minimizes costs by selecting the best price for each product
     * - Reduces logistics complexity by consolidating small orders
     * - Creates MULTIPLE orders (one per provider) instead of a single order
     *
     * POST /api/pedidos/auto-generar
     *
     * @return Map with order details, statistics, and result
     */
    @PostMapping("/auto-generar")
    public ResponseEntity<Map<String, Object>> generarPedidoAutomatico() {
        logger.info("Manual trigger: generating OPTIMIZED automatic orders for low-stock products");

        Map<String, Object> response = autoPedidoService.generarPedidoAutomaticoOptimizado();

        logger.info("Optimized automatic order generation completed: {}", response.get("mensaje"));

        return ResponseEntity.ok(response);
    }
}
