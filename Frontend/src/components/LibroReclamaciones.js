import React, { useState } from 'react';
import { Container, Form, Button } from 'react-bootstrap';
import Swal from 'sweetalert2';

const LibroReclamaciones = () => {
  const [formData, setFormData] = useState({
    nombre: '',
    correo: '',
    tipoReclamo: '',
    detalle: '',
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    console.log('Reclamo enviado:', formData);

    // Mostrar SweetAlert
    Swal.fire({
      title: '¡Gracias!',
      text: 'Su reclamo ha sido enviado correctamente. Lo atenderemos lo antes posible.',
      icon: 'success',
      confirmButtonText: 'Aceptar',
    });

    // Limpiar los campos del formulario
    setFormData({ nombre: '', correo: '', tipoReclamo: '', detalle: '' });
  };

  return (
    <Container className="py-5">
      <h2 className="text-center text-warning">Libro de Reclamaciones</h2>
      <p className="text-center text-muted">
        Complete el formulario a continuación para registrar su reclamo o sugerencia.
      </p>
      <Form onSubmit={handleSubmit}>
        <Form.Group className="mb-3" controlId="nombre">
          <Form.Label>Nombre completo</Form.Label>
          <Form.Control
            type="text"
            name="nombre"
            value={formData.nombre}
            onChange={handleChange}
            placeholder="Ingrese su nombre completo"
            required
          />
        </Form.Group>

        <Form.Group className="mb-3" controlId="correo">
          <Form.Label>Correo electrónico</Form.Label>
          <Form.Control
            type="email"
            name="correo"
            value={formData.correo}
            onChange={handleChange}
            placeholder="Ingrese su correo electrónico"
            required
          />
        </Form.Group>

        <Form.Group className="mb-3" controlId="tipoReclamo">
          <Form.Label>Tipo de Reclamo</Form.Label>
          <Form.Select
            name="tipoReclamo"
            value={formData.tipoReclamo}
            onChange={handleChange}
            required
          >
            <option value="">Seleccione una opción</option>
            <option value="Producto">Producto</option>
            <option value="Servicio">Servicio</option>
            <option value="Otro">Otro</option>
          </Form.Select>
        </Form.Group>

        <Form.Group className="mb-3" controlId="detalle">
          <Form.Label>Detalles del Reclamo</Form.Label>
          <Form.Control
            as="textarea"
            name="detalle"
            value={formData.detalle}
            onChange={handleChange}
            placeholder="Describa su reclamo o sugerencia"
            rows={4}
            required
          />
        </Form.Group>

        <Button variant="warning" type="submit" className="w-100">
          Enviar Reclamo
        </Button>
      </Form>
    </Container>
  );
};

export default LibroReclamaciones;
