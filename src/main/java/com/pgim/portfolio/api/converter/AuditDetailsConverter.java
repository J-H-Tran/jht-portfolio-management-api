package com.pgim.portfolio.api.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pgim.portfolio.domain.entity.audit.AuditDetails;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AuditDetailsConverter implements AttributeConverter<AuditDetails, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(AuditDetails attribute) {
        if (attribute == null) {
            System.err.println("AuditDetailsConverter: attribute is null");
            return null;
        }
        try {
            System.err.println("AuditDetailsConverter: converting to JSON: " + attribute);
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            System.err.println("AuditDetailsConverter error: " + e.getMessage());
            throw new IllegalArgumentException("Failed to convert AuditDetails to JSON string", e);
        }
    }

    @Override
    public AuditDetails convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            System.err.println("AuditDetailsConverter: dbData is null or empty");
            return null;
        }
        try {
            System.err.println("AuditDetailsConverter: converting from JSON: " + dbData);
            return objectMapper.readValue(dbData, AuditDetails.class);
        } catch (Exception e) {
            System.err.println("AuditDetailsConverter error: " + e.getMessage());
            throw new IllegalArgumentException("Failed to convert JSON string to AuditDetails", e);
        }
    }
}