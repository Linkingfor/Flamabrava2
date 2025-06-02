import React, { useEffect, useState } from 'react';
import { Navbar, Nav, Dropdown } from 'react-bootstrap';
import '../assets/styles/style.css';
import loginImage from '../assets/images/login.png';
import Swal from 'sweetalert2';

const Header = () => {
  const [usuario, setUsuario] = useState(null);

  useEffect(() => {
    const storedUsuario = JSON.parse(localStorage.getItem('usuario'));
    if (storedUsuario && storedUsuario.isActive) {
      setUsuario(storedUsuario);
    }
  }, []);

  const handleLogout = () => {
    fetch('http://localhost:8080/api/clientes/logout', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email: usuario.email }),
    })
      .then((response) => {
        if (response.ok) {
          Swal.fire({
            title: '¡Sesión cerrada!',
            text: 'Has cerrado sesión con éxito.',
            icon: 'success',
            confirmButtonText: 'Aceptar',
          }).then(() => {
            localStorage.removeItem('usuario');
            setUsuario(null);
            window.location.href = '/';
          });
        }
      })
      .catch((error) => {
        console.error('Error al cerrar sesión:', error);
      });
  };

  return (
    <Navbar bg="dark" variant="dark" expand="lg" className="custom-navbar">
      <Navbar.Brand href="/" className="navbar-logo">
        <img
          src="/images/logo.jpg"
          width="70"
          height="70"
          className="d-inline-block align-top"
          alt="Logo Pollería Flama Brava"
          style={{
            borderRadius: '50%',
            boxShadow: '0px 0px 2px rgba(0,0,0,0.5)',
          }}
        />
      </Navbar.Brand>
      <Navbar.Toggle aria-controls="basic-navbar-nav" />
      <Navbar.Collapse id="basic-navbar-nav">
        <Nav className="ms-auto navbar-links">
          <Nav.Link href="/" className="nav-text">Inicio</Nav.Link>
          <Nav.Link href="/menu" className="nav-text">Menú</Nav.Link>
          <Nav.Link href="/reserva" className="nav-text">Reserva</Nav.Link>
          {usuario ? (
            <Dropdown align="end" className="nav-user-dropdown">
              <Dropdown.Toggle variant="success" id="dropdown-basic">
                <img 
                  src={loginImage} 
                  alt="Login"
                  style={{
                    width: '45px',
                    height: '45px',
                    borderRadius: '100%',
                  }}
                />
              </Dropdown.Toggle>
              <Dropdown.Menu>
                {(usuario?.rol === 'Administrador' || usuario?.rol === 'Trabajador') && (
                  <Dropdown.Item href="/admin">Administrar</Dropdown.Item>
                )}
                <Dropdown.Item href="/profile">Mi Perfil</Dropdown.Item>
                <Dropdown.Item onClick={handleLogout}>Cerrar Sesión</Dropdown.Item>
              </Dropdown.Menu>
            </Dropdown>
          ) : (
            <Nav.Link href="/login" className="nav-text">Iniciar Sesión</Nav.Link>
          )}
        </Nav>
      </Navbar.Collapse>
    </Navbar>
  );
};

export default Header;
