import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Swal from 'sweetalert2';
import { useNavigate } from 'react-router-dom';
import '../assets/styles/Reserva.css';

const Reserva = () => {
  const [userData, setUserData] = useState({
    id: '',
    nombre: '',
    apellido: '',
    correo: '',
    telefono: '',
  });
  const [formData, setFormData] = useState({
    fecha: '',
    hora: '',
    mesa: '',
    observaciones: '',
  });
  const [mesas, setMesas] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    const usuario = JSON.parse(localStorage.getItem('usuario'));
    if (!usuario) {
      Swal.fire({
        title: '¡No estás logeado!',
        text: 'Para hacer una reserva, necesitas iniciar sesión.',
        icon: 'warning',
        confirmButtonText: 'Aceptar',
      }).then(() => {
        navigate('/login');
      });
      return; 
    }

    setUserData({
      id: usuario.id,
      nombre: usuario.nombre,
      apellido: usuario.apellido,
      correo: usuario.email,
      telefono: usuario.telefono,
    });

    const fetchMesas = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/mesas');
        setMesas(response.data);
      } catch (error) {
        console.error('Error al obtener las mesas:', error);
        Swal.fire({
          title: 'Error',
          text: 'No se pudieron cargar las mesas.',
          icon: 'error',
          confirmButtonText: 'Aceptar',
        });
      }
    };
    fetchMesas();

    const currentDate = new Date();
    const formattedDate = currentDate.toISOString().split('T')[0];
    setFormData(prevState => ({
      ...prevState,
      fecha: formattedDate,
      hora: formatTo12HourFormat(currentDate),
    }));
  }, [navigate]);

  const formatTo12HourFormat = (date) => {
    let hours = date.getHours();
    const minutes = date.getMinutes();
    const ampm = hours >= 12 ? 'PM' : 'AM';
    hours = hours % 12;
    hours = hours ? hours : 12;
    const minutesFormatted = minutes < 10 ? `0${minutes}` : minutes;
    return `${hours}:${minutesFormatted} ${ampm}`;
  };

  const generateAvailableHours = () => {
    const hours = [];
    const currentDate = new Date();
    const currentHour = currentDate.getHours();
    const currentMinute = currentDate.getMinutes();

    for (let h = 11; h <= 24; h++) {
      for (let m = 0; m < 60; m += 15) {
        const ampm = h >= 12 ? 'PM' : 'AM';
        const formattedHour = `${h > 12 ? h - 12 : h}:${m === 0 ? '00' : m} ${ampm}`;

        if (h > currentHour || (h === currentHour && m >= currentMinute)) {
          hours.push(formattedHour);
        }
      }
    }
    return hours;
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevState) => ({
      ...prevState,
      [name]: value,
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    const selectedDate = new Date(formData.fecha);
    const timeParts = formData.hora.split(" "); 
    const hourMinute = timeParts[0].split(":");
    const ampm = timeParts[1];

    let hours = parseInt(hourMinute[0], 10);
    const minutes = hourMinute[1];

   
    if (ampm === "PM" && hours !== 12) {
      hours += 12;
    } else if (ampm === "AM" && hours === 12) {
      hours = 0; 
    }

    selectedDate.setHours(hours);
    selectedDate.setMinutes(minutes);
    selectedDate.setSeconds(0);


    selectedDate.setDate(selectedDate.getDate() + 1);
    const localDate = new Date(selectedDate.getTime() - selectedDate.getTimezoneOffset() * 60000);
    const formattedDateTime = localDate.toISOString().slice(0, -1); 
    
    const selectedMesaId = parseInt(formData.mesa, 10);
    
    
    
    const reservaData = {
      clienteId: userData.id,  
      fecha: formattedDateTime, 
      mesa: { id: formData.mesa },  // Aquí se envía el id de la mesa
      observaciones: formData.observaciones,
    };
    


    try {
      const response = await axios.post('http://localhost:8080/api/reservas', reservaData);

      // Después de una reserva exitosa, actualizar el estado de la mesa
      const mesaSeleccionada = mesas.find((mesa) => mesa.numero === selectedMesaId);

      if (mesaSeleccionada) {
          await axios.put(`http://localhost:8080/api/mesas/${mesaSeleccionada.id}`, {
              estado: 'Ocupado',
          });
      }

      Swal.fire({
        title: 'Reserva realizada',
        text: 'Tu reserva ha sido confirmada exitosamente.',
        icon: 'success',
        confirmButtonText: 'Aceptar',
      }).then(() => {
        window.location.reload();
      });
    } catch (error) {
      console.error(error);
      Swal.fire({
        title: 'Error',
        text: 'Hubo un problema al crear la reserva.',
        icon: 'error',
        confirmButtonText: 'Aceptar',
      });
    }
  };

  return (
    <div className="reserva-page">
      <div className="container reserva-container">
        <h2 className="text-center mb-4">Reserva tu Mesa</h2>
        <p className="text-center text-muted">Por favor, completa los datos para realizar la reserva</p>

        <form onSubmit={handleSubmit}>
          <div className="mb-3">
            <label htmlFor="nombre" className="form-label">Nombre:</label>
            <input
              type="text"
              className="form-control"
              id="nombre"
              name="nombre"
              value={userData.nombre}
              readOnly
            />
          </div>

          <div className="mb-3">
            <label htmlFor="apellido" className="form-label">Apellido:</label>
            <input
              type="text"
              className="form-control"
              id="apellido"
              name="apellido"
              value={userData.apellido}
              readOnly
            />
          </div>

          <div className="mb-3">
            <label htmlFor="correo" className="form-label">Correo:</label>
            <input
              type="email"
              className="form-control"
              id="correo"
              name="correo"
              value={userData.correo}
              readOnly
            />
          </div>

          <div className="mb-3">
            <label htmlFor="telefono" className="form-label">Teléfono:</label>
            <input
              type="text"
              className="form-control"
              id="telefono"
              name="telefono"
              value={userData.telefono}
              readOnly
            />
          </div>

          <div className="mb-3">
            <label htmlFor="fecha" className="form-label">Fecha de la reserva:</label>
            <input
              type="date"
              className="form-control"
              id="fecha"
              name="fecha"
              value={formData.fecha}
              onChange={handleInputChange}
              min={formData.fecha}
              required
            />
          </div>

          <div className="mb-3">
            <label htmlFor="hora" className="form-label">Hora de la reserva:</label>
            <select
              className="form-control"
              id="hora"
              name="hora"
              value={formData.hora}
              onChange={handleInputChange}
              required
            >
              {generateAvailableHours().map((hour, index) => (
                <option key={index} value={hour}>{hour}</option>
              ))}
            </select>
          </div>

          <div className="mb-3">
  <label htmlFor="mesa" className="form-label">Mesa:</label>
  <select
    className="form-control"
    id="mesa"
    name="mesa"
    value={formData.mesa}
    onChange={handleInputChange}
    required
  >
    <option value="">Selecciona una mesa</option>
    {mesas
      .filter((mesa) => mesa.estado === 'Libre') // Filtramos solo las mesas libres
      .map((mesa) => (
        <option key={mesa.id} value={mesa.id}>
          Mesa {mesa.numero} (Capacidad: {mesa.capacidad})
        </option>
      ))}
  </select>
</div>


          <div className="mb-3">
            <label htmlFor="observaciones" className="form-label">Observaciones (opcional):</label>
            <textarea
              className="form-control"
              id="observaciones"
              name="observaciones"
              rows="3"
              value={formData.observaciones}
              onChange={handleInputChange}
            ></textarea>
          </div>

          <button type="submit" className="btn btn-primary w-100">
            Confirmar Reserva
          </button>
        </form>
      </div>
    </div>
  );
};

export default Reserva;

