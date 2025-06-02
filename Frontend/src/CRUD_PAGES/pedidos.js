import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

const Pedidos = () => {
  const [pedidos, setPedidos] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const fetchPedidos = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/pedidos');
        if (response.ok) {
          const data = await response.json();
          setPedidos(data);
        } else {
          console.error('Error al obtener los pedidos.');
        }
      } catch (error) {
        console.error('Error al conectar con la API de pedidos:', error);
      }
    };

    fetchPedidos();
  }, []);

  const handleViewDetails = (id) => {
    navigate(`/pedidos/${id}`);
  };

  const handleDeletePedido = async (id) => {
    try {
      const response = await fetch(`http://localhost:8080/api/pedidos/${id}`, {
        method: 'DELETE',
      });

      if (response.ok) {
        setPedidos(pedidos.filter(pedido => pedido.id !== id));
      } else {
        console.error("No se pudo eliminar el pedido");
      }
    } catch (error) {
      console.error('Error al eliminar el pedido:', error);
    }
  };

  const handleChangeEstado = async (id) => {
    const pedidoActualizado = pedidos.find(pedido => pedido.id === id);
    if (pedidoActualizado.estado === 'Pendiente') {
      try {
        const response = await fetch(`http://localhost:8080/api/pedidos/${id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            ...pedidoActualizado,
            estado: 'Finalizado',
          }),
        });

        if (response.ok) {
          setPedidos(pedidos.map(pedido =>
            pedido.id === id ? { ...pedido, estado: 'Finalizado' } : pedido
          ));
        } else {
          console.error('Error al cambiar el estado del pedido');
        }
      } catch (error) {
        console.error('Error al conectar con la API para cambiar el estado:', error);
      }
    }
  };

  return (
    <div className="p-4 bg-light rounded shadow-sm">
      <h2 className="mb-4">Gestión de Pedidos</h2>
      
      {pedidos.length > 0 ? (
        <table className="table table-bordered">
          <thead>
            <tr>
              <th>ID</th>
              <th>Cliente</th>
              <th>Teléfono</th>
              <th>Productos</th>
              <th>Total</th>
              <th>Estado</th>
              <th>Fecha</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {pedidos.map((pedido) => (
              <tr key={pedido.id}>
                <td>{pedido.id}</td>
                <td>{pedido.cliente?.nombre || 'Cliente no especificado'}</td>
                <td>{pedido.cliente?.telefono || 'No especificado'}</td>
                
                <td>
                  {pedido.productos && pedido.productos.length > 0 ? (
                    pedido.productos.map((producto, index) => {
                      const cantidad = producto.cantidad && producto.cantidad > 0 ? producto.cantidad : 1;

                      return [...Array(cantidad)].map((_, idx) => (
                        <div key={`${index}-${idx}`}>
                          {producto.nombre} (Cantidad: 1)
                        </div>
                      ));
                    })
                  ) : (
                    <span>No hay productos</span>
                  )}
                </td>

                <td>S/ {pedido.total?.toFixed(2)}</td>
                <td>{pedido.estado}</td>
                <td>{new Date(pedido.fechaPedido).toLocaleString()}</td>
                <td>
                  <button 
                    className="btn btn-info btn-sm me-2" 
                    onClick={() => handleChangeEstado(pedido.id)}
                    disabled={pedido.estado !== 'Pendiente'}
                  >
                    Estado
                  </button>
                  <button className="btn btn-danger btn-sm" onClick={() => handleDeletePedido(pedido.id)}>
                    Eliminar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      ) : (
        <p>No hay pedidos para mostrar.</p>
      )}
    </div>
  );
};

export default Pedidos;
