package ubp.edu.com.ar.finalproyect.domain.subdomain;

import java.util.HashMap;
import java.util.Map;

public class EstadisticasGeneracion {
    private int totalProductosProcesados;
    private int productosAsignados;
    private int productosNoAsignados;
    private int totalProveedoresUtilizados;
    private int totalPedidosCreados;
    private int pedidosConsolidados;
    private int pedidosUnitariosInevitables;

    public EstadisticasGeneracion() {}

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("totalProductosProcesados", totalProductosProcesados);
        map.put("productosAsignados", productosAsignados);
        map.put("productosNoAsignados", productosNoAsignados);
        map.put("totalProveedoresUtilizados", totalProveedoresUtilizados);
        map.put("totalPedidosCreados", totalPedidosCreados);
        map.put("pedidosConsolidados", pedidosConsolidados);
        map.put("pedidosUnitariosInevitables", pedidosUnitariosInevitables);
        return map;
    }

    public int getTotalProductosProcesados() {
        return totalProductosProcesados;
    }

    public void setTotalProductosProcesados(int totalProductosProcesados) {
        this.totalProductosProcesados = totalProductosProcesados;
    }

    public int getProductosAsignados() {
        return productosAsignados;
    }

    public void setProductosAsignados(int productosAsignados) {
        this.productosAsignados = productosAsignados;
    }

    public int getProductosNoAsignados() {
        return productosNoAsignados;
    }

    public void setProductosNoAsignados(int productosNoAsignados) {
        this.productosNoAsignados = productosNoAsignados;
    }

    public int getTotalProveedoresUtilizados() {
        return totalProveedoresUtilizados;
    }

    public void setTotalProveedoresUtilizados(int totalProveedoresUtilizados) {
        this.totalProveedoresUtilizados = totalProveedoresUtilizados;
    }

    public int getTotalPedidosCreados() {
        return totalPedidosCreados;
    }

    public void setTotalPedidosCreados(int totalPedidosCreados) {
        this.totalPedidosCreados = totalPedidosCreados;
    }

    public int getPedidosConsolidados() {
        return pedidosConsolidados;
    }

    public void setPedidosConsolidados(int pedidosConsolidados) {
        this.pedidosConsolidados = pedidosConsolidados;
    }

    public int getPedidosUnitariosInevitables() {
        return pedidosUnitariosInevitables;
    }

    public void setPedidosUnitariosInevitables(int pedidosUnitariosInevitables) {
        this.pedidosUnitariosInevitables = pedidosUnitariosInevitables;
    }

    public void incrementarProductosAsignados() {
        this.productosAsignados++;
    }

    public void incrementarProductosNoAsignados() {
        this.productosNoAsignados++;
    }

    public void incrementarPedidosConsolidados() {
        this.pedidosConsolidados++;
    }

    public void incrementarPedidosUnitariosInevitables() {
        this.pedidosUnitariosInevitables++;
    }

    public void incrementarTotalPedidosCreados() {
        this.totalPedidosCreados++;
    }
}
