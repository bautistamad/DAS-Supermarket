package ubp.edu.com.ar.finalproyect.domain;

import java.time.LocalDateTime;
import java.util.List;

public class Producto {

    private Integer codigoBarra;
    private String nombre;
    private String imagen;
    private Integer minStock;
    private Integer maxStock;
    private Integer actualStock;

    // Fields from ProductoProveedor (only populated when querying by provider)
    private LocalDateTime updateDate;
    private Integer estadoId;
    private String estadoNombre;
    private String estadoDescripcion;

    // Fields from HistorialPrecio
    private List<HistorialPrecio> precios;

    public Producto() {
    }

    public Producto(Producto producto) {
        this.codigoBarra = producto.getCodigoBarra();
        this.nombre = producto.getNombre();
        this.imagen = producto.getImage();
        this.actualStock = producto.getActualStock();
        this.minStock = producto.getMinStock();
        this.maxStock = producto.getMaxStock();
        this.updateDate = producto.getUpdateDate();
        this.estadoId = producto.getEstadoId();
        this.estadoNombre = producto.getEstadoNombre();
        this.estadoDescripcion = producto.getEstadoDescripcion();
    }

    public List<HistorialPrecio> getPrecios() {
        return precios;
    }

    public void setPrecios(List<HistorialPrecio> precios) {
        this.precios = precios;
    }

    public Producto(Integer codigoBarra, String nombre, String imagen,
                    Integer minStock, Integer maxStock, Integer actualStock) {
        this.codigoBarra = codigoBarra;
        this.nombre = nombre;
        this.imagen = imagen;
        this.minStock = minStock;
        this.maxStock = maxStock;
        this.actualStock = actualStock;
        this.precios = null;
    }

    public boolean needsRestock(Integer currentStock) {
        return currentStock <= this.minStock;
    }

    public Integer getCodigoBarra() {
        return codigoBarra;
    }

    public void setCodigoBarra(Integer barCode) {
        this.codigoBarra = barCode;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String name) {
        this.nombre = name;
    }

    public String getImage() {
        return imagen;
    }

    public void setImage(String image) {
        this.imagen = image;
    }

    public Integer getMinStock() {
        return minStock;
    }

    public void setMinStock(Integer minStock) {
        this.minStock = minStock;
    }

    public Integer getMaxStock() {
        return maxStock;
    }

    public void setMaxStock(Integer maxStock) {
        this.maxStock = maxStock;
    }

    public Integer getActualStock() {
        return actualStock;
    }

    public void setActualStock(Integer actualStock) {
        this.actualStock = actualStock;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    public Integer getEstadoId() {
        return estadoId;
    }

    public void setEstadoId(Integer estadoId) {
        this.estadoId = estadoId;
    }

    public String getEstadoNombre() {
        return estadoNombre;
    }

    public void setEstadoNombre(String estadoNombre) {
        this.estadoNombre = estadoNombre;
    }

    public String getEstadoDescripcion() {
        return estadoDescripcion;
    }

    public void setEstadoDescripcion(String estadoDescripcion) {
        this.estadoDescripcion = estadoDescripcion;
    }

    @Override
    public String toString() {
        return "Producto [barCode=" + codigoBarra + ", name=" + nombre + ", image=" + imagen + ", minStock=" + minStock + ", maxStock=" + maxStock + ", currentStock=" + actualStock + ", estadoNombre=" + estadoNombre + "]";
    }

}
