package com.flamabrava.repository;

import com.flamabrava.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    // Para listar cada Pedido junto con su DetallePedido + Producto
    @Query("""
      SELECT DISTINCT p
      FROM Pedido p
      LEFT JOIN FETCH p.detalle d
      LEFT JOIN FETCH d.producto pr
      """)
    List<Pedido> findAllWithDetalle();

    // Para obtener un Pedido (por ID) junto con su detalle
    @Query("""
      SELECT p
      FROM Pedido p
      LEFT JOIN FETCH p.detalle d
      LEFT JOIN FETCH d.producto pr
      WHERE p.id = :id
      """)
    Optional<Pedido> findByIdWithDetalle(@Param("id") Integer id);

}
