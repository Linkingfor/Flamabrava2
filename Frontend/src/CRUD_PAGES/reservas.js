import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Swal from 'sweetalert2';

const ReservaCrud = () => {
  const [reservas, setReservas] = useState([]);
  const [clientes, setClientes] = useState({}); // Almacenar los datos del cliente por id

  // Fetch de las reservas
  useEffect(() => {
    const fetchReservas = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/reservas');
        setReservas(response.data);
      } catch (error) {
        console.error('Error al obtener las reservas:', error);
        Swal.fire({
          title: 'Error',
          text: 'No se pudieron cargar las reservas.',
          icon: 'error',
          confirmButtonText: 'Aceptar',
        });
      }
    };

    fetchReservas();
  }, []);

  // Fetch de los datos de los clientes
  useEffect(() => {
    const fetchClientes = async () => {
      let clienteData = {};

      if (reservas.length > 0) {
        for (let reserva of reservas) {
          const clienteId = reserva.clienteId;

          if (clienteId) {
            try {
              const response = await axios.get(`http://localhost:8080/api/clientes/${clienteId}`);
              clienteData[clienteId] = response.data;
            } catch (error) {
              console.error(`Error al obtener el cliente con id ${clienteId}:`, error);
            }
          }
        }

        setClientes(clienteData); // Actualizamos el estado con los datos de los clientes
      }
    };

    fetchClientes();
  }, [reservas]);

  // Función para eliminar una reserva
  const handleDelete = async (id) => {
    const confirmDelete = await Swal.fire({
      title: '¿Estás seguro?',
      text: `¿Deseas eliminar esta reserva?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
    });

    if (confirmDelete.isConfirmed) {
      try {
        // Eliminar la reserva desde el backend
        await axios.delete(`http://localhost:8080/api/reservas/${id}`);
        
        // Actualizar el estado de las reservas para reflejar los cambios sin necesidad de recargar la página
        setReservas(reservas.filter((reserva) => reserva.id !== id));

        Swal.fire('Eliminado', 'La reserva ha sido eliminada correctamente.', 'success');
      } catch (error) {
        Swal.fire('Error', 'No se pudo eliminar la reserva.', 'error');
      }
    }
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h2 style={{ textAlign: 'center', marginBottom: '20px' }}>Lista de Reservas</h2>

      <table
        style={{
          width: '100%',
          borderCollapse: 'collapse',
          marginTop: '20px',
        }}
      >
        <thead>
          <tr style={{ backgroundColor: '#f4f4f4' }}>
            <th style={{ padding: '10px', border: '1px solid #ddd' }}>Nombre Cliente</th>
            <th style={{ padding: '10px', border: '1px solid #ddd' }}>Apellido Cliente</th>
            <th style={{ padding: '10px', border: '1px solid #ddd' }}>Teléfono Cliente</th>
            <th style={{ padding: '10px', border: '1px solid #ddd' }}>Correo Cliente</th>
            <th style={{ padding: '10px', border: '1px solid #ddd' }}>Número de Mesa</th>
            <th style={{ padding: '10px', border: '1px solid #ddd' }}>Observaciones</th>
            <th style={{ padding: '10px', border: '1px solid #ddd' }}>Fecha de Reserva</th>
            <th style={{ padding: '10px', border: '1px solid #ddd' }}>Acciones</th> {/* Nueva columna para eliminar */}
          </tr>
        </thead>
        <tbody>
          {reservas.map((reserva) => {
            const cliente = clientes[reserva.clienteId]; // Obtener los datos del cliente usando clienteId

            return (
              <tr key={reserva.id}>
                <td style={{ padding: '10px', border: '1px solid #ddd' }}>
                  {cliente ? cliente.nombre : 'Cargando...'}
                </td>
                <td style={{ padding: '10px', border: '1px solid #ddd' }}>
                  {cliente ? cliente.apellido : 'Cargando...'}
                </td>
                <td style={{ padding: '10px', border: '1px solid #ddd' }}>
                  {cliente ? cliente.telefono : 'Cargando...'}
                </td>
                <td style={{ padding: '10px', border: '1px solid #ddd' }}>
                  {cliente ? cliente.email : 'Cargando...'}
                </td>
                <td style={{ padding: '10px', border: '1px solid #ddd' }}>
                  {reserva.mesa ? reserva.mesa.numero : 'No disponible'}
                </td>
                <td style={{ padding: '10px', border: '1px solid #ddd' }}>
                  {reserva.observaciones || 'No hay comentarios'}
                </td>
                <td style={{ padding: '10px', border: '1px solid #ddd' }}>
                  {new Date(reserva.fecha).toLocaleString('en-US', { timeZone: 'UTC' })}
                </td>
                <td style={{ padding: '10px', border: '1px solid #ddd' }}>
                  {/* Botón para eliminar la reserva */}
                  <button
                    onClick={() => handleDelete(reserva.id)}
                    style={{
                      backgroundColor: '#d33',
                      color: 'white',
                      border: 'none',
                      padding: '5px 10px',
                      cursor: 'pointer',
                    }}
                  >
                    Eliminar
                  </button>
                </td>
              </tr>
            );
          })}
        </tbody>
      </table>
    </div>
  );
};

export default ReservaCrud;
