import React, { useState, useEffect } from 'react';
import Swal from 'sweetalert2';

const Producto = () => {
  const [productos, setProductos] = useState([]);
  const [categorias, setCategorias] = useState([]);
  const [producto, setProducto] = useState({
    id: '',
    nombre: '',
    precio: '',
    stock: '',
    categoriaId: '',
  });
  const [isEditing, setIsEditing] = useState(false);

  useEffect(() => {
    const fetchProductos = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/productos');
        if (response.ok) {
          const data = await response.json();
          setProductos(data);
        } else {
          console.error('Error al obtener los productos.');
        }
      } catch (error) {
        console.error('Error al conectar con el backend:', error);
      }
    };

    const fetchCategorias = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/categorias');
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

    fetchProductos();
    fetchCategorias();
  }, []);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProducto({ ...producto, [name]: value });
  };

  const handleAddProducto = async (e) => {
    e.preventDefault();

    if (producto.categoriaId === "") {
      Swal.fire('Error', 'Debes seleccionar una categoría.', 'error');
      return;
    }

    try {
      if (isEditing) {
        const response = await fetch(`http://localhost:8080/api/productos/${producto.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            id: producto.id,
            nombre: producto.nombre,
            precio: producto.precio,
            stock: producto.stock,
            categoria: { id: producto.categoriaId },
          }),
        });

        if (response.ok) {
          const updatedProducto = await response.json();
          setProductos(
            productos.map((p) => (p.id === updatedProducto.id ? updatedProducto : p))
          );
          Swal.fire('Actualizado', 'El producto ha sido actualizado correctamente.', 'success');
        } else {
          Swal.fire('Error', 'No se pudo actualizar el producto.', 'error');
        }
      } else {
        const response = await fetch('http://localhost:8080/api/productos', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            nombre: producto.nombre,
            precio: producto.precio,
            stock: producto.stock,
            categoria: { id: producto.categoriaId },
          }),
        });

        if (response.ok) {
          const newProducto = await response.json();
          setProductos([...productos, newProducto]);
          Swal.fire('Agregado', 'El producto ha sido agregado correctamente.', 'success');
        } else {
          Swal.fire('Error', 'No se pudo agregar el producto.', 'error');
        }
      }
    } catch (error) {
      Swal.fire('Error', 'Hubo un problema al procesar la solicitud.', 'error');
    }

    setProducto({ id: '', nombre: '', precio: '', stock: '', categoriaId: '' });
    setIsEditing(false);
  };

  const handleEditProducto = (id) => {
    const productoToEdit = productos.find((p) => p.id === id);
    setProducto({
      id: productoToEdit.id,
      nombre: productoToEdit.nombre,
      precio: productoToEdit.precio,
      stock: productoToEdit.stock,
      categoriaId: productoToEdit.categoria?.id || '',
    });
    setIsEditing(true);
  };

  const handleDeleteProducto = async (id) => {
    const confirmDelete = await Swal.fire({
      title: '¿Estás seguro?',
      text: `¿Deseas eliminar el producto con ID ${id}?`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#d33',
      cancelButtonColor: '#3085d6',
      confirmButtonText: 'Sí, eliminar',
      cancelButtonText: 'Cancelar',
    });

    if (confirmDelete.isConfirmed) {
      try {
        const response = await fetch(`http://localhost:8080/api/productos/${id}`, {
          method: 'DELETE',
        });

        if (response.ok) {
          setProductos(productos.filter((p) => p.id !== id));
          Swal.fire('Eliminado', 'El producto ha sido eliminado.', 'success');
        } else {
          Swal.fire('Error', 'No se pudo eliminar el producto.', 'error');
        }
      } catch (error) {
        Swal.fire('Error', 'Hubo un problema al eliminar el producto.', 'error');
      }
    }
  };

  return (
    <div className="p-4 bg-light rounded shadow-sm">
      <h2 className="mb-4">Gestión de Productos</h2>

      <form onSubmit={handleAddProducto} className="mb-4">
        <div className="mb-3">
          <label htmlFor="nombre" className="form-label">Nombre del Producto</label>
          <input
            type="text"
            name="nombre"
            id="nombre"
            placeholder="Ej: Pollo Asado"
            className="form-control"
            value={producto.nombre}
            onChange={handleChange}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="precio" className="form-label">Precio</label>
          <input
            type="number"
            name="precio"
            id="precio"
            className="form-control"
            value={producto.precio}
            onChange={handleChange}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="stock" className="form-label">Stock</label>
          <input
            type="number"
            name="stock"
            id="stock"
            className="form-control"
            value={producto.stock}
            onChange={handleChange}
            required
          />
        </div>
        <div className="mb-3">
          <label htmlFor="categoriaId" className="form-label">Categoría</label>
          <select
            name="categoriaId"
            id="categoriaId"
            className="form-control"
            value={producto.categoriaId}
            onChange={handleChange}
            required
          >
            <option value="">Seleccione una categoría</option>
            {categorias.map((categoria) => (
              <option key={categoria.id} value={categoria.id}>
                {categoria.nombre}
              </option>
            ))}
          </select>
        </div>

        <button type="submit" className="btn btn-primary">
          {isEditing ? 'Actualizar Producto' : 'Agregar Producto'}
        </button>
      </form>

      <table className="table table-bordered">
        <thead>
          <tr>
            <th>ID</th>
            <th>Nombre</th>
            <th>Precio</th>
            <th>Stock</th>
            <th>Categoría</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          {productos.map((p) => (
            <tr key={p.id}>
              <td>{p.id}</td>
              <td>{p.nombre}</td>
              <td>{p.precio}</td>
<td className="text-center">
  <span
    style={{
      display: 'inline-block',
      width: '25px',
      height: '25px',
      borderRadius: '50%',
      backgroundColor: p.stock >= 8 ? '#28a745' : '#dc3545',
    }}
  ></span>
</td>


              <td>{p.categoria ? p.categoria.nombre : 'No asignada'}</td>
              <td>
                <button
                  className="btn btn-warning btn-sm me-2"
                  onClick={() => handleEditProducto(p.id)}
                >
                  Editar
                </button>
                <button
                  className="btn btn-danger btn-sm"
                  onClick={() => handleDeleteProducto(p.id)}
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

export default Producto;
