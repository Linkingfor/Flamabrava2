import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Swal from 'sweetalert2';

const UsuarioCrud = () => {
  const [newUsuario, setNewUsuario] = useState({
    nombre: '',
    password: '',
    rol: 'Trabajador', // Valor por defecto
  });

  const [usuarios, setUsuarios] = useState([]); // Estado para almacenar los usuarios
  const [loading, setLoading] = useState(true); // Estado para controlar la carga de datos

  // Obtener la lista de usuarios desde el backend
  useEffect(() => {
    const fetchUsuarios = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/usuarios');
        // Filtrar los usuarios para mostrar solo los que tienen el rol "Trabajador"
        const trabajadores = response.data.filter(usuario => usuario.rol === 'Trabajador');
        setUsuarios(trabajadores);
        setLoading(false);
      } catch (error) {
        console.error("Error al obtener los usuarios:", error);
        setLoading(false);
      }
    };
    fetchUsuarios();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setNewUsuario((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Validar los datos del formulario
    if (newUsuario.nombre.trim() === '' || newUsuario.password.trim() === '') {
      Swal.fire({
        title: 'Error',
        text: 'Por favor, ingresa todos los datos correctamente.',
        icon: 'error',
        confirmButtonText: 'Aceptar',
      });
      return;
    }

    try {
      const response = await axios.post('http://localhost:8080/api/usuarios', newUsuario);
      if (response.status === 201) {
        Swal.fire({
          title: 'Usuario creado exitosamente',
          text: `Se ha creado el usuario ${response.data.nombre}`,
          icon: 'success',
          confirmButtonText: 'Aceptar',
        });
        // Limpiar el formulario
        setNewUsuario({
          nombre: '',
          password: '',
          rol: 'Trabajador',
        });
        // Actualizar la lista de usuarios (solo los trabajadores)
        setUsuarios([...usuarios, response.data]);
      }
    } catch (error) {
      console.error(error);
      Swal.fire({
        title: 'Error',
        text: 'Hubo un problema al crear el usuario.',
        icon: 'error',
        confirmButtonText: 'Intentar de nuevo',
      });
    }
  };

  const handleDelete = async (id) => {
    try {
      const response = await axios.delete(`http://localhost:8080/api/usuarios/${id}`);
      if (response.status === 204) {
        Swal.fire({
          title: 'Usuario eliminado',
          text: 'El usuario ha sido eliminado correctamente.',
          icon: 'success',
          confirmButtonText: 'Aceptar',
        });
        // Eliminar el usuario de la lista en el frontend
        setUsuarios(usuarios.filter(usuario => usuario.id !== id));
      }
    } catch (error) {
      console.error(error);
      Swal.fire({
        title: 'Error',
        text: 'Hubo un problema al eliminar el usuario.',
        icon: 'error',
        confirmButtonText: 'Intentar de nuevo',
      });
    }
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'Arial, sans-serif' }}>
      <h2 style={{ textAlign: 'center', marginBottom: '20px' }}>Crear Usuario</h2>
      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Correo:</label>
          <input
            type="email"
            name="nombre"
            value={newUsuario.nombre}
            onChange={handleChange}
            required
            placeholder="Correo del usuario"
          />
        </div>
        <div className="form-group">
          <label>Contraseña:</label>
          <input
            type="password"
            name="password"
            value={newUsuario.password}
            onChange={handleChange}
            required
            placeholder="Contraseña del usuario"
          />
        </div>
        <div className="form-group">
          <label>Rol:</label>
          <select
            name="rol"
            value={newUsuario.rol}
            onChange={handleChange}
            required
            disabled
          >
            <option value="Trabajador">Trabajador</option>
          </select>
        </div>
        <button type="submit" className="submit-button">Crear Trabajador</button>
      </form>

      {/* Tabla de Usuarios */}
      <h2 style={{ textAlign: 'center', marginTop: '40px' }}>Lista de Usuarios</h2>
      {loading ? (
        <p>Loading...</p>
      ) : (
        <table style={{ width: '100%', borderCollapse: 'collapse', marginTop: '20px' }}>
          <thead>
            <tr style={{ backgroundColor: '#f4f4f4' }}>
              <th style={{ padding: '10px', border: '1px solid #ddd' }}>Correo</th>
              <th style={{ padding: '10px', border: '1px solid #ddd' }}>Rol</th>
              <th style={{ padding: '10px', border: '1px solid #ddd' }}>Acciones</th>
            </tr>
          </thead>
          <tbody>
            {usuarios.map((usuario) => (
              <tr key={usuario.id}>
                <td style={{ padding: '10px', border: '1px solid #ddd' }}>{usuario.nombre}</td>
                <td style={{ padding: '10px', border: '1px solid #ddd' }}>{usuario.rol}</td>
                <td style={{ padding: '10px', border: '1px solid #ddd' }}>
                  <button
                    style={{
                      padding: '5px 10px',
                      backgroundColor: '#d9534f',
                      color: 'white',
                      border: 'none',
                      cursor: 'pointer',
                    }}
                    onClick={() => handleDelete(usuario.id)}
                  >
                    Eliminar
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
};

export default UsuarioCrud;
