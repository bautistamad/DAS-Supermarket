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

    @PostMapping("/auto-generar")
    public ResponseEntity<Map<String, Object>> generarPedidoAutomatico() {
        logger.info("Manual trigger: generating OPTIMIZED automatic orders for low-stock products");

        Map<String, Object> response = autoPedidoService.generarPedidoAutomaticoOptimizado();

        logger.info("Optimized automatic order generation completed: {}", response.get("mensaje"));

        return ResponseEntity.ok(response);
    }
}
