package ubp.edu.com.ar.finalproyect.port;

import ubp.edu.com.ar.finalproyect.domain.Escala;

import java.util.List;
import java.util.Optional;


public interface EscalaRepository {


    Escala save(Escala escala);


    List<Escala> saveAll(List<Escala> escalas);


    List<Escala> findByProveedor(Integer idProveedor);


    Optional<Escala> findByInternal(Integer idProveedor, Integer escalaInt);


    void updatePedidoEvaluacion(Integer idPedido, Integer idEscala);
}
