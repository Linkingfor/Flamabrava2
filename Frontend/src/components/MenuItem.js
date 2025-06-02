import React from 'react';

const MenuItem = ({ product, addToCart }) => {
  // Asegurarse de que el nombre del producto sea compatible con la imagen
  const productName = product.nombre?.toLowerCase().replace(/\s+/g, '-');  // Convertir el nombre en minúsculas y reemplazar espacios por guiones

  // Intentamos cargar la imagen correspondiente o una imagen predeterminada en caso de error
  let imagePath;
  try {
    imagePath = require(`../assets/images/${productName}.png`);
  } catch (error) {
    imagePath = require(`../assets/images/default-image.png`);  // Imagen predeterminada
  }

  return (
    <div className="menu-card">
      <img src={imagePath.default || imagePath} alt={product.nombre} className="product-image" />
      <div className="product-details">
        <h3>{product.nombre}</h3>
        <p>s/.{product.precio ? product.precio.toFixed(2) : '0.00'}</p>  {/* Mostrar el precio con validación */}
        <button onClick={() => addToCart(product)}>Añadir al Carrito</button>  {/* Al hacer clic, pasamos el producto a la función */}
      </div>
    </div>
  );
};

export default MenuItem;
