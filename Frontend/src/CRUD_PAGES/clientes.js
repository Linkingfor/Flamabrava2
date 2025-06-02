import React, { useState, useEffect } from 'react';
import Swal from 'sweetalert2';

const Clientes = () => {
  const [clientes, setClientes] = useState([]);

  // Obtener los clientes desde el backend
  useEffect(() => {
    const fetchClientes = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/clientes'); // Cambia la URL según tu configuración
        if (response.ok) {
          const data = await response.json();
          setClientes(data);
        } else {
          console.error('Error al obtener los clientes.');
        }
      } catch (error) {
        console.error('Error al conectar con el backend:', error);
      }
    };

    fetchClientes();
  }, []);

  // Eliminar cliente
  const handleDeleteCliente = async (id) => {
    const confirmDelete = await Swal.fire({
      title: '¿Estás seguro?',
      text: `¿Deseas eliminar al cliente con ID ${id}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
    });

    if (confirmDelete.isConfirmed) {
      try {
        const response = await fetch(`http://localhost:8080/api/clientes/${id}`, {
          method: 'DELETE',
        });

        if (response.ok) {
          setClientes(clientes.filter((cliente) => cliente.id !== id));
          Swal.fire('Eliminado', 'El cliente ha sido eliminado correctamente.', 'success');
        } else {
          Swal.fire('Error', 'No se pudo eliminar el cliente.', 'error');
        }
      } catch (error) {
        Swal.fire('Error', 'Hubo un problema al eliminar el cliente.', 'error');
      }
    }
  };

  return (
    <div className="p-4 bg-light rounded shadow-sm">
      <h2 className="mb-4">Gestión de Clientes</h2>

      <table className="table table-bordered">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>DNI</th>
            <th>Correo</th>
            <th>Teléfono</th>
            <th>Activo</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {clientes.map((cliente) => (
            <tr key={cliente.id}>
              <td>{cliente.id}</td>
              <td>{cliente.nombre}</td>
              <td>{cliente.dni}</td>
              <td>{cliente.email}</td>
              <td>{cliente.telefono}</td>
              <td>{cliente.isActive ? 'Sí' : 'No'}</td>
              <td>
                <button
                  className="btn btn-danger btn-sm"
                  onClick={() => handleDeleteCliente(cliente.id)}
                >
                  Eliminar
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default Clientes;
