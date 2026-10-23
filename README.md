# VetTurno

Veterinaria Huellitas es una clínica comprometida con el cuidado y bienestar de las mascotas que, frente al constante aumento de sus pacientes, requiere digitalizar la organización de sus consultas. VetTurno nace como su agenda digital centralizada para gestionar de manera confiable, ágil y ordenada el registro de propietarios, pacientes, médicos veterinarios y citas médicas sin conflictos de horario.

## Tecnologías

- **Java 17**
- **Spring Boot 4.1.2**
- **Apache Maven**
- **MySQL**
- **Spring Data JPA**
- **Spring Security & JJWT**
- **Bean Validation (Hibernate Validator)**
- **OpenAPI 3 / Swagger UI (springdoc-openapi)**

## Variables de Entorno Requeridas

Para iniciar la aplicación, deben configurarse las siguientes variables de entorno:
- `DB_USER`: Usuario de acceso a MySQL.
- `DB_PASSWORD`: Contraseña de acceso a MySQL.
- `JWT_SECRET`: Clave secreta para la firma y validación de tokens JWT.

## Cómo ejecutar

### Windows (PowerShell / CMD)
```powershell
./mvnw.cmd spring-boot:run
```

### macOS / Linux
```bash
./mvnw spring-boot:run
```
