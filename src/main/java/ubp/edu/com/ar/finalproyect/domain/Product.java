package ubp.edu.com.ar.finalproyect.domain;

public class  Product {

    private Integer barCode;
    private String name;
    private String image;
    private Integer minStock;
    private Integer maxStock;
    private Integer currentStock;

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

    @Override
    public String toString() {
        return "Product [barCode=" + barCode + ", name=" + name + ", image=" + image + ", minStock=" + minStock + ", maxStock=" + maxStock + ", currentStock=" + currentStock + "]";
    }

}
