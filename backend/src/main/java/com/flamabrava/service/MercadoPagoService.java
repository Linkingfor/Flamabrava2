package com.flamabrava.service;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.*;
import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.resources.payment.Payment;
import com.mercadopago.resources.preference.Preference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@CrossOrigin(origins = "https://polleriaflamabrava.netlify.app")
public class MercadoPagoService {

        @Value("${mercadopago.access.token}")
        private String accessToken;

        public String createPreference(List<Map<String, Object>> productos) {
                try {
                        MercadoPagoConfig.setAccessToken(accessToken);

                        List<PreferenceItemRequest> items = new ArrayList<>();
                        for (Map<String, Object> producto : productos) {
                                String name = (String) producto.get("name");
                                Integer quantity = (Integer) producto.get("quantity");
                                Object priceObj = producto.get("price");

                                BigDecimal price = parsePrice(priceObj);

                                validateProductData(name, quantity, price);

                                PreferenceItemRequest item = PreferenceItemRequest.builder()
                                                .title(name)
                                                .quantity(quantity)
                                                .unitPrice(price)
                                                .currencyId("PEN")
                                                .build();
                                items.add(item);
                        }

                        PreferencePayerRequest payer = createPayer("Nair", "Villegas", "naircito12339@gmail.com");

                        PreferenceBackUrlsRequest backUrls = createBackUrls();

                        PreferenceRequest preferenceRequest = PreferenceRequest.builder()
                                        .items(items)
                                        .payer(payer)
                                        .backUrls(backUrls)
                                        .autoReturn("approved")
                                        .build();

                        Preference preference = new PreferenceClient().create(preferenceRequest);

                        return preference.getInitPoint();

                } catch (Exception e) {
                        e.printStackTrace();
                        return "Error al generar la preferencia: " + e.getMessage();
                }
        }

        private void validateProductData(String name, Integer quantity, BigDecimal price) {
                if (name == null || name.isEmpty()) {
                        throw new IllegalArgumentException("El nombre del producto no puede estar vacío.");
                }
                if (quantity == null || quantity <= 0) {
                        throw new IllegalArgumentException("La cantidad del producto debe ser mayor a 0.");
                }
                if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                        throw new IllegalArgumentException("El precio del producto debe ser mayor a 0.");
                }
        }

        private BigDecimal parsePrice(Object priceObj) {
                if (priceObj instanceof Integer) {
                        return new BigDecimal((Integer) priceObj);
                } else if (priceObj instanceof Double) {
                        return new BigDecimal((Double) priceObj);
                }
                return BigDecimal.ZERO;
        }

        private PreferencePayerRequest createPayer(String name, String surname, String email) {
                return PreferencePayerRequest.builder()
                                .name(name)
                                .surname(surname)
                                .email(email)
                                .build();
        }

        private PreferenceBackUrlsRequest createBackUrls() {
                return PreferenceBackUrlsRequest.builder()
                                .success("https://www.success.com")
                                .failure("http://www.failure.com")
                                .pending("http://www.pending.com")
                                .build();
        }

        public String checkPaymentStatus(String paymentId) {
                try {
                        Long paymentIdLong = Long.parseLong(paymentId);

                        PaymentClient paymentClient = new PaymentClient();
                        Payment payment = paymentClient.get(paymentIdLong);
                        String status = payment.getStatus();

                        switch (status) {
                                case "approved":
                                        return "Pago aprobado. ¡Gracias por tu compra!";
                                case "pending":
                                        return "El pago está pendiente. Por favor, espera a la confirmación.";
                                case "rejected":
                                        return "El pago fue rechazado. Intenta de nuevo o usa otro método de pago.";
                                case "cancelled":
                                        return "El pago fue cancelado.";
                                default:
                                        return "Estado del pago desconocido.";
                        }

                } catch (NumberFormatException e) {
                        return "Error al convertir el ID del pago a Long: " + e.getMessage();
                } catch (Exception e) {
                        e.printStackTrace();
                        return "Error al verificar el estado del pago: " + e.getMessage();
                }
        }
}
