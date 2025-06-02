import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; 
import Swal from 'sweetalert2';
import '../assets/styles/login.css';

const LoginRegisterCliente = () => {
  const navigate = useNavigate(); 
  const [isRegister, setIsRegister] = useState(false); 
  const [newCliente, setNewCliente] = useState({
    nombre: '',
    apellido: '',
    dni: '',
    email: '',
    telefono: '',
    contrasena: '',
  });

  const [loginData, setLoginData] = useState({
    email: '',
    contrasena: '',
    rol: 'Cliente',  // Valor inicial de rol
  });

  useEffect(() => {
    const usuario = JSON.parse(localStorage.getItem('usuario'));
    console.log('Usuario en localStorage:', usuario);
    if (usuario && usuario.isActive) {
      navigate('/');  // Redirige a la página principal si ya está logueado
    }
  }, [navigate]);

  const toggleForm = () => {
    setIsRegister(!isRegister);
  };

  const validateLoginData = () => {
    return loginData.email.trim() !== '' && loginData.contrasena.trim() !== '';
  };

  const validateNewCliente = () => {
    return (
      newCliente.nombre.trim() !== '' &&
      newCliente.apellido.trim() !== '' &&
      newCliente.dni.trim().length === 8 &&
      newCliente.email.trim() !== '' &&
      newCliente.telefono.trim().length === 9 &&
      validatePassword(newCliente.contrasena)
    );
  };

  const validatePassword = (password) => {
    const hasUpperCase = /[A-Z]/.test(password);
    const hasNumber = /[0-9]/.test(password);
    const hasMinLength = password.length >= 8;
    return hasUpperCase && hasNumber && hasMinLength;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    const updater = isRegister ? setNewCliente : setLoginData;
    updater((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleLoginSubmit = async (e) => {
    e.preventDefault();
    try {
        const response = await fetch('http://localhost:8080/api/clientes/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({
                email: loginData.email,  // Aquí usamos `loginData.email` como el nombre de usuario
                contrasena: loginData.contrasena  // Aquí usamos la contraseña tal cual
            }),
        });

        if (response.ok) {
            const usuario = await response.json();
            
            Swal.fire({
                title: '¡Bienvenido!',
                text: `Inicio de sesión exitoso, ${usuario.nombre}.`,
                icon: 'success',
                confirmButtonText: 'Aceptar',
            });

            // Guardar los datos del usuario en localStorage
            localStorage.setItem('usuario', JSON.stringify(usuario));

            // Redirigir al cliente o admin según el rol
            if (usuario.rol === 'Administrador' || usuario.rol === 'Trabajador') {
                navigate('/admin');  // Redirigir a la sección de administración (para ambos roles)
            } else {
                navigate('/');  // Redirigir al cliente a la página principal
            }
            window.location.reload();
        } else {
            Swal.fire({
                title: 'Error',
                text: 'Usuario o contraseña incorrectos.',
                icon: 'error',
                confirmButtonText: 'Intentar de nuevo',
            });
        }
    } catch (error) {
        Swal.fire({
            title: 'Error',
            text: 'Hubo un problema al iniciar sesión.',
            icon: 'error',
            confirmButtonText: 'Intentar de nuevo',
        });
    }
};


const handleRegisterSubmit = async (e) => {
  e.preventDefault();
  if (!validateNewCliente()) {
      Swal.fire({
          title: 'Error',
          text: 'Por favor, verifica los datos ingresados.',
          icon: 'error',
          confirmButtonText: 'Aceptar',
      });
      return;
  }

  try {
      const response = await fetch('http://localhost:8080/api/clientes', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(newCliente),  // Enviar datos completos del cliente
      });

      if (response.ok) {
          Swal.fire({
              title: '¡Registro exitoso!',
              text: 'Te has registrado correctamente.',
              icon: 'success',
              confirmButtonText: 'Aceptar',
          });
          setNewCliente({
              nombre: '',
              apellido: '',
              dni: '',
              email: '',
              telefono: '',
              contrasena: '',
          });
          toggleForm();
      } else if (response.status === 409) {
          Swal.fire({
              title: 'Error',
              text: 'Ya existe un cliente con este correo electrónico.',
              icon: 'error',
              confirmButtonText: 'Aceptar',
          });
      } else {
          Swal.fire({
              title: 'Error',
              text: 'Hubo un problema al registrarte. Intenta de nuevo.',
              icon: 'error',
              confirmButtonText: 'Intentar de nuevo',
          });
      }
  } catch (error) {
      Swal.fire({
          title: 'Error',
          text: 'Hubo un problema al registrarte.',
          icon: 'error',
          confirmButtonText: 'Intentar de nuevo',
      });
  }
};


  return (
    <div className="login-register-container">
      {isRegister ? (
        <div className="register-box">
          <h2>Registro</h2>
          <form onSubmit={handleRegisterSubmit}>
            <div className="form-group">
              <label>Nombre:</label>
              <input type="text" name="nombre" value={newCliente.nombre} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Apellido:</label>
              <input type="text" name="apellido" value={newCliente.apellido} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>DNI:</label>
              <input type="text" name="dni" value={newCliente.dni} onChange={handleChange} maxLength="8" required />
            </div>
            <div className="form-group">
              <label>Email:</label>
              <input type="email" name="email" value={newCliente.email} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Teléfono:</label>
              <input type="text" name="telefono" value={newCliente.telefono} onChange={handleChange} maxLength="9" required />
            </div>
            <div className="form-group">
              <label>Contraseña:</label>
              <input type="password" name="contrasena" value={newCliente.contrasena} onChange={handleChange} required />
            </div>
            <button type="submit" className="register-button">Registrarse</button>
          </form>
          <p>
            ¿Ya tienes cuenta?{' '}
            <span onClick={toggleForm} className="toggle-link">
              Inicia Sesión
            </span>
          </p>
        </div>
      ) : (
        <div className="login-box">
          <h2>Iniciar Sesión</h2>
          <form onSubmit={handleLoginSubmit}>
            <div className="form-group">
              <label>Email:</label>
              <input type="email" name="email" value={loginData.email} onChange={handleChange} required />
            </div>
            <div className="form-group">
              <label>Contraseña:</label>
              <input type="password" name="contrasena" value={loginData.contrasena} onChange={handleChange} required />
            </div>
            <button type="submit" className="login-button">Iniciar Sesión</button>
          </form>
          <p>
            ¿No tienes cuenta?{' '}
            <span onClick={toggleForm} className="toggle-link">
              Regístrate
            </span>
          </p>
        </div>
      )}
    </div>
  );
};

export default LoginRegisterCliente;
