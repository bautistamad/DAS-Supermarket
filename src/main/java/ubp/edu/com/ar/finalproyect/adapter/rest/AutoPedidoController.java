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
     * Generate automatic order for products below minimum stock
     *
     * Logic:
     * - Find products where actualStock <= stockMinimo
     * - Compare all providers (REST + SOAP)
     * - Select by lowest total price, then by highest rating (if tie)
     * - Create ONE order to the best provider
     *
     * POST /api/pedidos/auto-generar
     *
     * @return Map with order details and result
     */
    @PostMapping("/auto-generar")
    public ResponseEntity<Map<String, Object>> generarPedidoAutomatico() {
        logger.info("Manual trigger: generating automatic orders for low-stock products");

        Map<String, Object> response = autoPedidoService.generarPedidoAutomatico();

        logger.info("Automatic order generation completed: {}", response.get("mensaje"));

        return ResponseEntity.ok(response);
    }
}
