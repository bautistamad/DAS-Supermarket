package ubp.edu.com.ar.finalproyect.adapter.persistence.product;


public class ProductEntity {

    private Integer codigoBarra;
    private String nombre;
    private String imagen;
    private Integer stockMinimo;
    private Integer stockMaximo;
    private Integer stockActual;

    public ProductEntity() {};

    public ProductEntity(Integer codigoBarra, String nombre, String imagen,
                         Integer stockMinimo, Integer stockMaximo, Integer stockActual) {
        this.codigoBarra = codigoBarra;
        this.nombre = nombre;
        this.imagen = imagen;
        this.stockMinimo = stockMinimo;
        this.stockMaximo = stockMaximo;
        this.stockActual = stockActual;
    }

    public Integer getCodigoBarra() { return codigoBarra; }
    public void setCodigoBarra(Integer codigoBarra) { this.codigoBarra = codigoBarra; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getImagen() { return imagen; }
    public void setImagen(String imagen) { this.imagen = imagen; }
    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }
    public Integer getStockMaximo() { return stockMaximo; }
    public void setStockMaximo(Integer stockMaximo) { this.stockMaximo = stockMaximo; }
    public Integer getStockActual() { return stockActual; }
    public void setStockActual(Integer stockActual) { this.stockActual = stockActual; }

}
