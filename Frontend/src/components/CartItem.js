import React, { useState, useEffect } from 'react';

const CartItem = ({ item, onRemove, onQuantityChange }) => {
  const [imageSrc, setImageSrc] = useState('');

  useEffect(() => {
    // Asegurarse de que 'item.nombre' está definido y es una cadena válida
    const imageName = item.nombre ? item.nombre.replace(/\s+/g, '').toLowerCase() : '';
    
    // Intentar cargar la imagen o asignar la imagen predeterminada
    try {
      const imagePath = require(`../assets/images/${imageName}.png`);
      setImageSrc(imagePath);
    } catch (error) {
      // Imagen predeterminada en caso de error
      setImageSrc(require('../assets/images/default-image.png'));
    }
  }, [item.nombre]);

  return (
    <li className="cart-card">
      <img src={imageSrc} alt={item.nombre} className="cart-image" />
      <div className="cart-info">
        <div>
          {item.nombre} - s/.{item.precio ? item.precio.toFixed(2) : '0.00'} x {item.quantity}
        </div>
        <div className="button-cart">
          <button onClick={() => onQuantityChange(item.id, 1)}>+</button>
          <button onClick={() => onQuantityChange(item.id, -1)} disabled={item.quantity <= 1}>-</button>
          <button onClick={() => onRemove(item.id)}>x</button>
        </div>
      </div>
    </li>
  );
};

export default CartItem;
