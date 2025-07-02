package com.flamabrava.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class StripeService {

    @Value("${stripe.api.key.secret}")
    private String stripeSecretKey;

    @Value("${stripe.api.key.publishable}")
    private String stripePublishableKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
    }

    /**
     * Crea una sesión de Checkout minimal sin metadata.
     */
    public Session createCheckoutSession(
            long precioEnCentavos,
            String descripcion,
            String successUrl,
            String cancelUrl
    ) throws StripeException {
        return createCheckoutSession(precioEnCentavos, descripcion, successUrl, cancelUrl, Map.of(), List.of());
    }

    /**
     * Crea una sesión de Checkout con metadata y line items detallados.
     *
     * @param precioEnCentavos  monto en centavos (ej. 1500)
     * @param descripcion       descripción genérica (se usa si no hay detalles)
     * @param successUrl        URL de éxito (ya con ?pedidoId=…)
     * @param cancelUrl         URL de cancelación
     * @param metadata          map con metadata para la sesión
     * @param items             lista de DetallePedido para line items
     */
    public Session createCheckoutSession(
            long precioEnCentavos,
            String descripcion,
            String successUrl,
            String cancelUrl,
            Map<String, String> metadata,
            List<com.flamabrava.model.DetallePedido> items
    ) throws StripeException {

        SessionCreateParams.Builder builder = SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl + "&session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl(cancelUrl);

        // Adjuntar metadata si existe
        if (metadata != null && !metadata.isEmpty()) {
            builder.putAllMetadata(metadata);
        }

        // Si hay detalles, agregar cada producto como line item
        if (items != null && !items.isEmpty()) {
            for (com.flamabrava.model.DetallePedido d : items) {
                long unitAmount = d.getProducto().getPrecio()
                        .multiply(BigDecimal.valueOf(100))
                        .longValue();

                SessionCreateParams.LineItem lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity(d.getCantidad().longValue())
                        .setPriceData(
                            SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("pen")
                                .setUnitAmount(unitAmount)
                                .setProductData(
                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(d.getProducto().getNombre())
                                        .build()
                                )
                                .build()
                        )
                        .build();

                builder.addLineItem(lineItem);
            }
        } else {
            // fallback a un único line item con descripción genérica
            SessionCreateParams.LineItem fallback = SessionCreateParams.LineItem.builder()
                    .setQuantity(1L)
                    .setPriceData(
                        SessionCreateParams.LineItem.PriceData.builder()
                            .setCurrency("pen")
                            .setUnitAmount(precioEnCentavos)
                            .setProductData(
                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                    .setName(descripcion)
                                    .build()
                            )
                            .build()
                    )
                    .build();
            builder.addLineItem(fallback);
        }

        return Session.create(builder.build());
    }

    /**
     * Devuelve tu clave pública para Stripe.js en el frontend
     */
    public String getPublishableKey() {
        return stripePublishableKey;
    }
}
