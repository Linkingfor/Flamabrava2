import React, { useEffect, useState } from 'react';
import { initMercadoPago } from '@mercadopago/sdk-react'
initMercadoPago('TEST-fbbb2029-988e-48db-ba25-ff9b5846db15');


const PaymentPage = () => {
  const [loading, setLoading] = useState(true);
  const [paymentUrl, setPaymentUrl] = useState(''); 

  useEffect(() => {
    const fetchPreference = async () => {
      try {
        const response = await fetch('http://localhost:8080/api/pago/crear-preferencia', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            items: [
              { name: 'Pollo', price: 30.0, quantity: 1 },
              { name: 'Anticucho', price: 25.0, quantity: 1 },
            ],
          }),
        });

        if (response.ok) {
          const data = await response.text();
          setPaymentUrl(data); 
          setLoading(false);
        } else {
          console.error('Error al crear la preferencia');
          setLoading(false);
        }
      } catch (error) {
        console.error('Error al cargar la preferencia:', error);
        setLoading(false);
      }
    };

    fetchPreference();
  }, []);

  useEffect(() => {

    if (paymentUrl) {
      window.location.href = paymentUrl;  
    }
  }, [paymentUrl]);

  return (
    <div style={styles.container}>
      {loading ? (
        <div style={styles.loader}>
          <h1 style={styles.text}>Redirigiendo al flujo de pago...</h1>
          <div style={styles.spinner}></div>
        </div>
      ) : (
        <h2 style={styles.text}>Si no redirige automáticamente, verifica tu conexión.</h2>
      )}
    </div>
  );
};

const styles = {
  container: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    height: '100vh',
    backgroundColor: '#121212',
    color: '#fff',
    textAlign: 'center',
  },
  loader: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
  },
  text: {
    marginBottom: '20px',
    fontSize: '20px',
  },
  spinner: {
    border: '8px solid #f3f3f3',
    borderTop: '8px solid #ff5722',
    borderRadius: '50%',
    width: '50px',
    height: '50px',
    animation: 'spin 1s linear infinite',
  },
};

export default PaymentPage;
