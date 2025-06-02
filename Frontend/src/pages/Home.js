// src/pages/Home.js
import React, { useEffect, useState } from 'react';
import Swal from 'sweetalert2';
import { useNavigate } from 'react-router-dom';
import { Carousel, Container, Card, Row, Col } from 'react-bootstrap';
import 'bootstrap/dist/css/bootstrap.min.css';
import { FaFacebook, FaInstagram, FaTiktok, FaWhatsapp } from 'react-icons/fa';
import flamaBrava from '../assets/images/flama-brava.jpg';
import pollo from '../assets/images/pollo.png';
import parrilla from '../assets/images/parrillafamiliar.png';
import alitasBBQ from '../assets/images/alitasbbq.png';
import alitas from '../assets/images/alitas.jpg';
import ubicacion from '../assets/images/ubicacion.jpg';
import '../assets/styles/style.css';

const Home = () => {
  const [userData, setUserData] = useState({});
  const navigate = useNavigate();

  useEffect(() => {
    // Verificar si el usuario está logeado
    const usuario = JSON.parse(localStorage.getItem('usuario'));
    
    if (!usuario) {
      // Si el usuario no está logeado, mostrar el mensaje cada 40 segundos
      const intervalId = setInterval(() => {
        Swal.fire({
          title: '¡Puedes crear una cuenta en nuestra pagina WEB!',
          text: 'Recuerda que para hacer reservas y comprar nececitas una cuenta.',
          confirmButtonText: 'Iniciar sesion', // Botón que llevará al login
          icon: 'info',
          position: 'bottom-left',  // Posición en la esquina inferior izquierda
          showConfirmButton: true, // Mostrar el botón "Ir al Login"
          timer: 8000, // Duración de la alerta en milisegundos (8 segundos)
          timerProgressBar: true, // Barra de progreso
        }).then((result) => {
          if (result.isConfirmed) {
            // Redirigir al login solo después de que el usuario haga clic en el botón
            navigate('/login');
          }
        });
      }, 40000); // Mostrar la alerta cada 40 segundos

      // Limpiar el intervalo cuando el componente se desmonte
      return () => clearInterval(intervalId);
    }


    // Si el usuario está logeado, cargamos sus datos
    setUserData({
      id: usuario.id,
      nombre: usuario.nombre,
      apellido: usuario.apellido,
      correo: usuario.email,
      telefono: usuario.telefono,
    });
  }, []);
  return (
    <div className="home-page">
      <Carousel className="carousel-container">
        <Carousel.Item>
          <img className="d-block w-100 carousel-image" src={flamaBrava} alt="Flama Brava" />
          <Carousel.Caption></Carousel.Caption>
        </Carousel.Item>
        <Carousel.Item>
          <img className="d-block w-100 carousel-image" src={pollo} alt="Pollo a la Brasa" />
          <Carousel.Caption></Carousel.Caption>
        </Carousel.Item>
        <Carousel.Item>
          <img className="d-block w-100 carousel-image" src={parrilla} alt="Parrilla Mixta" />
          <Carousel.Caption></Carousel.Caption>
        </Carousel.Item>
      </Carousel>
      <Container className="text-center mt-5">
        <h2>¡Bienvenido a Pollería Flama Brava!</h2>
        <p>
          En Flama Brava nos dedicamos a ofrecerte los mejores platos con la calidad y el sabor que nos caracteriza. 
          Nuestra pasión por la cocina se refleja en cada bocado, utilizando siempre ingredientes frescos y de primera. 
          ¡Ven a disfrutar de una experiencia gastronómica única!
        </p>
      </Container>
      <Container className="my-5">
        <h2 className="text-center">Nuestros Platos Estrella</h2>
        <Row className="mt-4">
          <Col md={4}>
            <Card className="h-100">
              <Card.Img variant="top" src={alitasBBQ} alt="Alitas BBQ" className="card-image" />
              <Card.Body>
                <Card.Title>Alitas BBQ</Card.Title>
                <Card.Text>
                  Crujientes alitas bañadas en nuestra inigualable salsa BBQ.
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4}>
            <Card className="h-100">
              <Card.Img variant="top" src={pollo} alt="Pollo a la Brasa" className="card-image" />
              <Card.Body>
                <Card.Title>Pollo a la Brasa</Card.Title>
                <Card.Text>
                  Nuestro plato insignia, preparado con una receta secreta y asado a la perfección.
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
          <Col md={4}>
            <Card className="h-100">
              <Card.Img variant="top" src={alitas} alt="Alitas Picantes" className="card-image" />
              <Card.Body>
                <Card.Title>Alitas Acevichadas</Card.Title>
                <Card.Text>
                  Deliciosas alitas acevichadas con nuestra nueva receta.
                </Card.Text>
              </Card.Body>
            </Card>
          </Col>
        </Row>
      </Container>

      <Container className="ubicacion-section my-5">
        <h2 className="ubicacion-title">UBÍCANOS EN NUESTRO LOCAL</h2>
        <div className="ubicacion-content d-flex flex-column align-items-center">
          <img src={ubicacion} alt="Ubicación" className="ubicacion-image mb-3" />
          <div className="ubicacion-info text-center">
            <p>Encuéntranos en nuestra pollería ubicada en [Av.Inca Garcilazo de la vega F-13 Ica, Perú
            ].</p>
            <a href="https://www.google.com/maps/place/Flama+Brava/@-14.1519497,-75.6956046,17.05z/data=!4m9!1m2!2m1!1sAv.Inca+Garcilazo+de+la+vega+F-13+Ica,+Per%C3%BA!3m5!1s0x91111f80507d03f1:0xb9bfa14c4c7e4a4d!8m2!3d-14.1507976!4d-75.6921743!16s%2Fg%2F11qm5q4tq2?entry=ttu&g_ep=EgoyMDI0MTAyMy4wIKXMDSoASAFQAw%3D%3D" target="_blank" rel="noopener noreferrer" className="ubicacion-link">
              Ver en Google Maps
            </a>
          </div>
        </div>
      </Container>
    </div>
  );
};

export default Home;
