import React, { useState, useEffect } from 'react';
import { Link, Outlet, useLocation, useNavigate } from 'react-router-dom';

const Layout = () => {
  const location = useLocation();
  const [userRole, setUserRole] = useState('');
  const navigate = useNavigate();

  // Estilos para el layout
  const styles = {
    sidebar: {
      backgroundColor: '#343a40',
      color: '#fff',
      height: '100vh',
      padding: '20px',
      width: '250px',
    },
    title: {
      color: '#f8f9fa',
      fontSize: '1.5rem',
      fontWeight: 'bold',
      marginBottom: '20px',
      textTransform: 'uppercase',
    },
    navLink: {
      padding: '10px',
      borderRadius: '5px',
      color: '#fff',
      textDecoration: 'none',
      display: 'flex',
      alignItems: 'center',
      transition: 'background-color 0.3s ease, transform 0.2s ease',
    },
    navLinkActive: {
      backgroundColor: '#6c757d',
    },
    navIcon: {
      fontSize: '1.2rem',
      marginRight: '10px',
    },
    logoutButton: {
      marginTop: '20px',
      padding: '5px 10px',
      backgroundColor: '#dc3545',
      color: '#fff',
      border: 'none',
      borderRadius: '5px',
      cursor: 'pointer',
      fontWeight: 'bold',
      fontSize: '0.9rem',
    },
  };

  useEffect(() => {
    const usuario = JSON.parse(localStorage.getItem('usuario'));
    if (usuario) {
      setUserRole(usuario.rol); // Almacenar el rol del usuario (Administrador o Trabajador)
    }
  }, []);

  const handleLogout = () => {
    // Eliminar los datos del usuario del localStorage
    localStorage.removeItem('usuario');
    
    // Redirigir al login
    navigate('/');
  };

  return (
    <div className="d-flex">
      <div style={styles.sidebar}>
        <h4 style={styles.title}>
          <i className="bi bi-gear-fill" style={styles.navIcon}></i>Gestión
        </h4>

        <nav className="nav flex-column">
          {/* Enlace de Clientes, visible solo para Administradores */}
          {userRole === 'Administrador' && (
            <Link
              to="/admin/clientes"
              className="nav-link"
              style={{
                ...styles.navLink,
                ...(location.pathname === '/admin/clientes' ? styles.navLinkActive : {}),
              }}
            >
              <i className="bi bi-person-fill" style={styles.navIcon}></i>Clientes
            </Link>
          )}

          {/* Enlace de Categorías, visible para todos */}
          <Link
            to="/admin/categorias"
            className="nav-link"
            style={{
              ...styles.navLink,
              ...(location.pathname === '/admin/categorias' ? styles.navLinkActive : {}),
            }}
          >
            <i className="bi bi-tag-fill" style={styles.navIcon}></i>Categorías
          </Link>

          {/* Enlace de Productos, visible para todos */}
          <Link
            to="/admin/productos"
            className="nav-link"
            style={{
              ...styles.navLink,
              ...(location.pathname === '/admin/productos' ? styles.navLinkActive : {}),
            }}
          >
            <i className="bi bi-box-seam" style={styles.navIcon}></i>Productos
          </Link>

          {/* Enlace de Inventario, visible para todos */}
          <Link
            to="/admin/inventario"
            className="nav-link"
            style={{
              ...styles.navLink,
              ...(location.pathname === '/admin/inventario' ? styles.navLinkActive : {}),
            }}
          >
            <i className="bi bi-boxes" style={styles.navIcon}></i>Inventario
          </Link>

          {/* Enlace de Mesas, visible para todos */}
          <Link
            to="/admin/mesas"
            className="nav-link"
            style={{
              ...styles.navLink,
              ...(location.pathname === '/admin/mesas' ? styles.navLinkActive : {}),
            }}
          >
            <i className="bi bi-table" style={styles.navIcon}></i>Mesas
          </Link>

          {/* Enlace de Reservas, visible para todos */}
          <Link
            to="/admin/reservas"
            className="nav-link"
            style={{
              ...styles.navLink,
              ...(location.pathname === '/admin/reservas' ? styles.navLinkActive : {}),
            }}
          >
            <i className="bi bi-calendar-check-fill" style={styles.navIcon}></i>Reservas
          </Link>

          {/* Enlace de Pedidos, visible para todos */}
          <Link
            to="/admin/pedidos"
            className="nav-link"
            style={{
              ...styles.navLink,
              ...(location.pathname === '/admin/pedidos' ? styles.navLinkActive : {}),
            }}
          >
            <i className="bi bi-cart-fill" style={styles.navIcon}></i>Pedidos
          </Link>

          {/* Enlace de Usuarios, visible solo para Administradores */}
          {userRole === 'Administrador' && (
            <Link
              to="/admin/usuarios"
              className="nav-link"
              style={{
                ...styles.navLink,
                ...(location.pathname === '/admin/usuarios' ? styles.navLinkActive : {}),
              }}
            >
              <i className="bi bi-people-fill" style={styles.navIcon}></i>Usuarios
            </Link>
          )}

          {/* Cerrar sesión - Este botón es visible para todos */}
          <button style={styles.logoutButton} onClick={handleLogout}>
            Cerrar Sesión
          </button>
        </nav>
      </div>

      <div className="flex-grow-1 p-4">
        <Outlet />
      </div>
    </div>
  );
};

export default Layout;
