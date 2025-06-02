import React, { useState, useEffect } from 'react';
import Swal from 'sweetalert2';

const Categoria = () => {
  const [categorias, setCategorias] = useState([]);
  const [categoria, setCategoria] = useState({
    id: '',
    nombre: '',
  });
  const [isEditing, setIsEditing] = useState(false);

  // Obtener las categorías del backend
  useEffect(() => {
    const fetchCategorias = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/categorias'); // Ajusta la URL según tu backend
        if (response.ok) {
          const data = await response.json();
          setCategorias(data);
        } else {
          console.error('Error al obtener las categorías.');
        }
      } catch (error) {
        console.error('Error al conectar con el backend:', error);
      }
    };

    fetchCategorias();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCategoria({ ...categoria, [name]: value });
  };

  const handleAddCategoria = async (e) => {
    e.preventDefault();
    if (isEditing) {
      try {
        const response = await fetch(`http://localhost:8080/api/categorias/${categoria.id}`, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(categoria),
        });

        if (response.ok) {
          const updatedCategoria = await response.json();
          setCategorias(
            categorias.map((c) => (c.id === updatedCategoria.id ? updatedCategoria : c))
          );
          Swal.fire('Actualizado', 'La categoría ha sido actualizada.', 'success');
          setIsEditing(false);
        } else {
          Swal.fire('Error', 'No se pudo actualizar la categoría.', 'error');
        }
      } catch (error) {
        Swal.fire('Error', 'Hubo un problema al actualizar la categoría.', 'error');
      }
    } else {
      try {
        const response = await fetch('http://localhost:8080/api/categorias', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify(categoria),
        });

        if (response.ok) {
          const newCategoria = await response.json();
          setCategorias([...categorias, newCategoria]);
          Swal.fire('Agregado', 'La categoría ha sido agregada.', 'success');
        } else {
          Swal.fire('Error', 'No se pudo agregar la categoría.', 'error');
        }
      } catch (error) {
        Swal.fire('Error', 'Hubo un problema al agregar la categoría.', 'error');
      }
    }
    setCategoria({ id: '', nombre: '' });
  };

  const handleEditCategoria = (id) => {
    const categoriaToEdit = categorias.find((c) => c.id === id);
    setCategoria(categoriaToEdit);
    setIsEditing(true);
  };

  const handleDeleteCategoria = async (id) => {
    const confirmDelete = await Swal.fire({
      title: '¿Estás seguro?',
      text: `¿Deseas eliminar la categoría con ID ${id}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
    });

    if (confirmDelete.isConfirmed) {
      try {
        const response = await fetch(`http://localhost:8080/api/categorias/${id}`, {
          method: 'DELETE',
        });

        if (response.ok) {
          setCategorias(categorias.filter((c) => c.id !== id));
          Swal.fire('Eliminado', 'La categoría ha sido eliminada.', 'success');
        } else {
          Swal.fire('Error', 'No se pudo eliminar la categoría.', 'error');
        }
      } catch (error) {
        Swal.fire('Error', 'Hubo un problema al eliminar la categoría.', 'error');
      }
    }
  };

  return (
    <div className="p-4 bg-light rounded shadow-sm">
      <h2 className="mb-4">Gestión de Categorías</h2>

      <form onSubmit={handleAddCategoria} className="mb-4">
        <div className="mb-3">
          <label htmlFor="nombre" className="form-label">Nombre de la Categoría</label>
          <input
            type="text"
            name="nombre"
            id="nombre"
            placeholder="Ej: Clásicos"
            className="form-control"
            value={categoria.nombre}
            onChange={handleChange}
            required
          />
        </div>

        <button type="submit" className="btn btn-primary">
          {isEditing ? 'Actualizar Categoría' : 'Agregar Categoría'}
        </button>
      </form>

      <table className="table table-bordered">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {categorias.map((c) => (
            <tr key={c.id}>
              <td>{c.id}</td>
              <td>{c.nombre}</td>
              <td>
                <button
                  className="btn btn-warning btn-sm me-2"
                  onClick={() => handleEditCategoria(c.id)}
                >
                  Editar
                </button>
                <button
                  className="btn btn-danger btn-sm"
                  onClick={() => handleDeleteCategoria(c.id)}
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

export default Categoria;
