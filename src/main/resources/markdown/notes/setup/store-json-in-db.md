# Storing JSON in a MySQL Column with JPA/Hibernate

## Overview
This project demonstrates how to store a complex Java object (POJO) as a JSON column in a MySQL database using JPA/Hibernate. The example uses an `AuditDetails` POJO, which is serialized/deserialized to/from a MySQL `JSON` column via a JPA `AttributeConverter`.

## Key Steps

### 1. Entity Setup
- The entity (`TradeAudit`) has a field:
  ```java
  @Convert(converter = AuditDetailsConverter.class)
  @Column(nullable = false, columnDefinition = "JSON")
  private AuditDetails details;
  ```
- The `AuditDetails` class must be a regular POJO with getters/setters. Lombok's `@Data`, `@NoArgsConstructor`, and `@AllArgsConstructor` are recommended. If you want to use a builder, add `@Builder` as well.

### 2. AttributeConverter Implementation
- Implement a JPA `AttributeConverter<AuditDetails, String>` that uses Jackson's `ObjectMapper` to serialize/deserialize the POJO to/from JSON.
- Example:
  ```java
  @Converter(autoApply = true)
  public class AuditDetailsConverter implements AttributeConverter<AuditDetails, String> {
      private static final ObjectMapper objectMapper = new ObjectMapper();
      ...
  }
  ```

### 3. MySQL Table Definition
- The column should be defined as `JSON` in the DDL:
  ```sql
  details JSON NOT NULL
  ```

## Common Pitfalls

### 1. Missing No-Args Constructor
- **Symptom:**
    - `Cannot construct instance of ... (no Creators, like default constructor, exist)`
- **Solution:**
    - Add `@NoArgsConstructor` (Lombok) or a public no-args constructor to your POJO.

### 2. Serialization/Deserialization Errors
- **Symptom:**
    - `Error attempting to apply AttributeConverter`
    - `Data truncation: Invalid JSON text` (MySQL error)
- **Solution:**
    - Ensure the POJO is serializable by Jackson (public getters/setters, no-args constructor).
    - Validate that the JSON string is valid before saving.
    - Use `@AllArgsConstructor` and `@NoArgsConstructor` for full compatibility.

### 3. Incorrect Column Definition
- **Symptom:**
    - MySQL errors about data type or truncation.
- **Solution:**
    - Use `columnDefinition = "JSON"` in your JPA entity.
    - Ensure your MySQL version supports the `JSON` type (MySQL 5.7+).

### 4. Builder Pattern Usage
- **Symptom:**
    - `Cannot resolve method 'builder'` or builder not working.
- **Solution:**
    - Add Lombok's `@Builder` to your POJO.
    - Example:
      ```java
      @Builder
      public class AuditDetails { ... }
      ```

## Troubleshooting Solutions Used
- Added `@NoArgsConstructor` and `@AllArgsConstructor` to `AuditDetails` to resolve Jackson deserialization issues.
- Used a static `ObjectMapper` in the converter for thread safety and performance.
- Ensured the entity field uses the converter and the correct column definition.
- Validated the JSON before saving to avoid MySQL errors.

## Example POJO
```java
@Data
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditDetails {
    private String note;
    private String referenceId;
}
```

## Example Converter
```java
@Converter(autoApply = true)
public class AuditDetailsConverter implements AttributeConverter<AuditDetails, String> {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    ...
}
```

## Summary
- Always ensure your POJO is compatible with Jackson (no-args constructor, getters/setters).
- Use a JPA AttributeConverter for JSON serialization.
- Use the correct MySQL column type and JPA column definition.
- Add troubleshooting logs to your converter for easier debugging.