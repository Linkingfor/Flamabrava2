import React from 'react';
import { FaFacebook, FaInstagram, FaTiktok, FaWhatsapp } from 'react-icons/fa';
import { Container, Row, Col } from 'react-bootstrap';

const Footer = () => {
  return (
    <footer className="bg-dark text-light py-5">
      <Container>
        <Row>
          {/* Servicios */}
          <Col md={4}>
            <h5 className="text-uppercase text-warning">Servicios</h5>

            <a href="/libro" className="text-light">
  <strong>Libro de Reclamaciones</strong>
</a>
<h5 className="text-uppercase text-warning">Backend</h5>
<a href="http://localhost:8080/" className="text-light">
  <strong>Acceso al backend</strong>
</a>


          </Col>

          <Col md={4}>
            <div className="d-flex align-items-center mt-3">
              <span className="me-2">Síguenos en:</span>
              <a href="https://www.facebook.com/FlamaBrava" target="_blank" rel="noreferrer" className="mx-2">
                <FaFacebook size={40} />
              </a>
              <a href="https://www.instagram.com/flamabrava/" target="_blank" rel="noreferrer" className="mx-2">
                <FaInstagram size={40} />
              </a>
              <a href="https://www.tiktok.com/@flamabrava?lang=es" target="_blank" rel="noreferrer" className="mx-2">
                <FaTiktok size={40} />
              </a>
              <a href="https://wa.me/949570583" target="_blank" rel="noreferrer" className="mx-2">
                <FaWhatsapp size={40} />
              </a>
            </div>
          </Col>
        </Row>

        <hr className="my-4" />
        <div className="text-center">
          <p className="mb-0">&copy; 2024 Pollería Flama Brava. Todos los derechos reservados.</p>
        </div>
      </Container>
    </footer>
  );
};

export default Footer;