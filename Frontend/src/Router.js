import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home';
import Menu from './pages/Menu';
import Cart from './pages/Cart';
import Login from './pages/LoginRegister';
import Reserva from './pages/Reserva';
import Profile from './pages/Profile';
import Page_Admin from './CRUD_PAGES/Page_Admin';
import Clientes from './CRUD_PAGES/clientes';
import Categorias from './CRUD_PAGES/categorias';
import Productos from './CRUD_PAGES/productos';
import Inventario from './CRUD_PAGES/inventario';
import Mesas from './CRUD_PAGES/mesas';
import Reservas from './CRUD_PAGES/reservas';
import Usuarios from './CRUD_PAGES/usuarios';
import Pedidos from './CRUD_PAGES/pedidos'; // Nuevo componente Pedidos
import LibroReclamaciones from './components/LibroReclamaciones';
const AppRouter = () => {
  return (
    <Routes>
      <Route path="/" element={<Home />} />
      <Route path="/menu" element={<Menu />} />
      <Route path="/cart" element={<Cart />} />
      <Route path="/login" element={<Login />} />
      <Route path="/reserva" element={<Reserva />} />
      <Route path="/profile" element={<Profile />} />
      <Route path="/libro" element={<LibroReclamaciones />} />
      
      {/* Rutas del administrador */}
      <Route path="/admin" element={<Page_Admin />}>
        <Route path="clientes" element={<Clientes />} />
        <Route path="categorias" element={<Categorias />} />
        <Route path="productos" element={<Productos />} />
        <Route path="inventario" element={<Inventario />} />
        <Route path="mesas" element={<Mesas />} />
        <Route path="reservas" element={<Reservas />} />
        <Route path="pedidos" element={<Pedidos />} /> {/* Nueva ruta de pedidos */}
        <Route path="usuarios" element={<Usuarios />} />
      </Route>
    </Routes>
  );
};

export default AppRouter;