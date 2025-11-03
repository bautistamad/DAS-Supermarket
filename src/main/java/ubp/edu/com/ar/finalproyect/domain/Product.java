package ubp.edu.com.ar.finalproyect.domain;

import java.time.LocalDateTime;

public class  Product {

    private Integer barCode;
    private String name;
    private String image;
    private Integer minStock;
    private Integer maxStock;
    private Integer currentStock;

    // Fields from ProductoProveedor (only populated when querying by provider)
    private LocalDateTime updateDate;
    private Integer estadoId;
    private String estadoNombre;
    private String estadoDescripcion;

    public Product () {
    }

    public Product(Product product) {}

    public Product(Integer barcode, String name, String image,
                   Integer minStock, Integer maxStock) {
        this.barCode = barcode;
        this.name = name;
        this.image = image;
        this.minStock = minStock;
        this.maxStock = maxStock;
        this.currentStock = currentStock;
    }

    public boolean needsRestock(Integer currentStock) {
        return currentStock <= this.minStock;
    }

    public Integer getBarCode() {
        return barCode;
    }

    public void setBarCode(Integer barCode) {
        this.barCode = barCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
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

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
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
        return "Product [barCode=" + barCode + ", name=" + name + ", image=" + image + ", minStock=" + minStock + ", maxStock=" + maxStock + ", currentStock=" + currentStock + ", estadoNombre=" + estadoNombre + "]";
    }

}
