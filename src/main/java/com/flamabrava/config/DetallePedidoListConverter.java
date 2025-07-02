package com.flamabrava.config;

import com.flamabrava.model.DetallePedido;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Converter
public class DetallePedidoListConverter
     implements AttributeConverter<List<DetallePedido>, String> {

    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<DetallePedido> detalle) {
        if (detalle == null || detalle.isEmpty()) {
            return "[]";
        }
        try {
            return mapper.writeValueAsString(detalle);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("No se pudo serializar detallePedido", e);
        }
    }

    @Override
    public List<DetallePedido> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isBlank()) {
            return Collections.emptyList();
        }
        try {
            JavaType type = mapper.getTypeFactory()
                .constructCollectionType(List.class, DetallePedido.class);
            return mapper.readValue(dbData, type);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo deserializar detallePedido", e);
        }
    }
}
