package ubp.edu.com.ar.finalproyect.adapter.persistence.product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Producto")
public class ProductEntity {

    @Id
    @Column(name = "codigoBarra")
    private Integer codigoBarra;

    @Column(name = "nombre", nullable = false)
    private String nombre;

    @Column(name = "imagen")
    private String imagen;

    @Column(name = "stockMinimo", nullable = false)
    private Integer stockMinimo;

    @Column(name = "stockMaximo", nullable = false)
    private Integer stockMaximo;

    @Column(name = "stockActual")
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
