# VetTurno — Agenda Digital de Veterinaria Huellitas

## 1. Historia del Proyecto

Veterinaria Huellitas es un centro médico veterinario enfocado en brindar atención de calidad a los animales de compañía. Debido a su rápido crecimiento en el número de pacientes y consultas diarias, la gestión manual de horarios generaba conflictos y solapamientos en las agendas de los médicos veterinarios. **VetTurno** fue desarrollado como la agenda digital centralizada para organizar el registro de propietarios, mascotas, profesionales y citas médicas de forma confiable, ordenada y protegida.

---

## 2. Alcance del Sistema

### Lo que incluye
* **Seguridad y Control de Acceso:** Autenticación y autorización basada en tokens JWT *stateless* y control de roles (`USER` y `ADMIN`).
* **Gestión de Pacientes y Clientes:** Registro y consulta de propietarios y sus mascotas.
* **Gestión del Cuerpo Médico:** Registro de veterinarios (restringido a administradores) y consulta de profesionales.
* **Agendamiento de Citas:** Programación de citas médicas exclusivamente en fechas futuras, impidiendo la duplicidad de horarios para un mismo veterinario.
* **Consultas de Agenda:** Consulta general de citas y filtrado específico de turnos por veterinario.
* **Validación de Datos:** Validación declarativa mediante Bean Validation en todos los puntos de entrada.
* **Manejo Centralizado de Errores:** Respuestas de error estandarizadas mediante `ApiError` y `@RestControllerAdvice`.
* **Documentación Interactiva:** Interfaz Swagger UI / OpenAPI 3 con soporte integrado para autorización Bearer JWT.
* **Persistencia Relacional:** Almacenamiento persistente en base de datos MySQL mediante Spring Data JPA.

### Lo que NO incluye
* Gestión de historias clínicas o expedientes médicos.
* Pasarelas de pago, facturación o cobros.
* Recordatorios automatizados por SMS o correo electrónico.
* Interfaces gráficas web o aplicaciones móviles cliente.
* Empaquetado en Docker ni despliegue en plataformas en la nube.

---

## 3. Tecnologías Utilizadas

* **Lenguaje:** Java 17
* **Framework:** Spring Boot 4.1.2 (con Spring Data JPA, Spring Security, Bean Validation, Spring Web MVC)
* **Gestor de Dependencias:** Apache Maven
* **Base de Datos:** MySQL Server 8+
* **Seguridad JWT:** JJWT (`jjwt-api`, `jjwt-impl`, `jjwt-jackson` versión `0.12.6`)
* **Documentación de API:** SpringDoc OpenAPI 3 (`springdoc-openapi-starter-webmvc-ui` versión `2.8.5`)

---

## 4. Arquitectura por Capas

El proyecto implementa una arquitectura en capas desacoplada y orientada al dominio dentro del paquete base `com.huellitas.vetturno`:

* **`model`:** Entidades JPA (`Usuario`, `Propietario`, `Mascota`, `Veterinario`, `Cita`) y enumeraciones (`Rol`). Representan las tablas de MySQL.
* **`repository`:** Interfaces que extienden `JpaRepository` para el acceso a datos y consultas derivadas de Spring Data JPA.
* **`service`:** Capa de lógica de negocio y aplicación transaccional (`@Transactional`). Aplica reglas, valida existencias y orquesta operaciones.
* **`controller`:** Controladores REST (`@RestController`) que exponen los endpoints, reciben DTOs validados con `@Valid` y devuelven `ResponseEntity`.
* **`dto`:** Clases Request y DTO de transferencia de datos. Garantizan que nunca se expongan entidades de base de datos ni se generen referencias circulares.
* **`security`:** Componentes de autenticación JWT (`JwtService`, `JwtAuthFilter`, `UsuarioDetailsService`, `SecurityConfig`).
* **`config`:** Configuración de la documentación interactiva OpenAPI/Swagger (`OpenApiConfig`).
* **`exception`:** Estructura de error (`ApiError`), excepciones personalizadas de negocio y manejador global (`GlobalExceptionHandler`).

---

## 5. Modelo de Datos

### Tabla de Entidades y Atributos

| Entidad | Tabla en MySQL | Atributos Principales | Relaciones y Restricciones |
|---|---|---|---|
| **Usuario** | `usuarios` | `id` (PK, Auto), `email` (Unique, Not Null), `password` (BCrypt), `rol` (STRING) | `rol`: Enum (`USER`, `ADMIN`). |
| **Propietario** | `propietarios` | `id` (PK, Auto), `nombre` (Not Null), `telefono` (Not Null), `email` (Nullable) | Relación 1 a N con Mascotas (gestionada unidireccionalmente). |
| **Mascota** | `mascotas` | `id` (PK, Auto), `nombre` (Not Null), `especie` (Not Null), `raza` (Nullable) | `@ManyToOne` hacia `Propietario` (`propietario_id`, FK, Not Null). |
| **Veterinario** | `veterinarios` | `id` (PK, Auto), `nombre` (Not Null), `especialidad` (Not Null) | Profesional asignable a turnos. |
| **Cita** | `citas` | `id` (PK, Auto), `fecha_hora` (Not Null), `motivo` (Not Null) | `@ManyToOne` hacia `Mascota` (`mascota_id`, FK) y `@ManyToOne` hacia `Veterinario` (`veterinario_id`, FK). |

---

## 6. Endpoints de la API y Control de Acceso

### Tabla de Endpoints

| Método | Endpoint | Descripción | Acceso Requerido |
|---|---|---|---|
| `POST` | `/api/auth/register` | Registro de nuevos usuarios (asigna rol USER) | **Público** |
| `POST` | `/api/auth/login` | Inicio de sesión y generación de token JWT | **Público** |
| `GET` | `/swagger-ui/**`, `/v3/api-docs/**` | Documentación interactiva Swagger UI | **Público** |
| `GET` | `/api/propietarios` | Listado completo de propietarios | **USER / ADMIN** |
| `POST` | `/api/propietarios` | Registro de un nuevo propietario | **USER / ADMIN** |
| `GET` | `/api/mascotas` | Listado de mascotas con datos básicos de su dueño | **USER / ADMIN** |
| `POST` | `/api/mascotas` | Registro de una mascota asociada a un propietario | **USER / ADMIN** |
| `GET` | `/api/veterinarios` | Listado de médicos veterinarios disponibles | **USER / ADMIN** |
| `POST` | `/api/veterinarios` | Creación de un veterinario | **Solo ADMIN** |
| `GET` | `/api/citas` | Agenda general de citas médicas | **USER / ADMIN** |
| `POST` | `/api/citas` | Agendamiento de cita futura sin cruce de horario | **USER / ADMIN** |
| `GET` | `/api/citas/veterinario/{id}` | Consulta de citas asignadas a un veterinario específico | **USER / ADMIN** |

---

## 7. Gestión de Roles y Promoción de Administradores

* **Rol `USER`:** Asignado automáticamente a todo usuario registrado desde el endpoint público `POST /api/auth/register`. Puede consultar y gestionar propietarios, mascotas y citas médicas.
* **Rol `ADMIN`:** Rol administrativo con privilegios exclusivos para dar de alta médicos veterinarios (`POST /api/veterinarios`).

### Procedimiento seguro para promover el primer ADMIN
Para evitar riesgos de escalada de privilegios, la API no expone ningún endpoint público para crear administradores. La promoción se realiza de forma directa en el motor relacional MySQL:

1. Registra un usuario común vía `POST /api/auth/register`:
   ```json
   {
     "email": "admin@huellitas.com",
     "password": "PasswordSegura123"
   }
   ```
2. Conéctate a tu cliente MySQL y ejecuta:
   ```sql
   USE vetturno;
   UPDATE usuarios SET rol = 'ADMIN' WHERE email = 'admin@huellitas.com';
   ```
3. Vuelve a iniciar sesión mediante `POST /api/auth/login`. El nuevo token JWT emitido contendrá el rol `ADMIN` para acceder a los endpoints restringidos.

---

## 8. Configuración y Variables de Entorno

La aplicación utiliza variables de entorno para evitar la exposición de credenciales y secretos en el código fuente.

### Variables requeridas

| Variable | Descripción | Ejemplo Referencial (No Real) |
|---|---|---|
| `DB_USER` | Nombre de usuario con permisos sobre MySQL | `tu_usuario_mysql` |
| `DB_PASSWORD` | Contraseña del usuario de MySQL | `tu_contrasena_mysql` |
| `JWT_SECRET` | Clave secreta para firmar tokens (mínimo 32 caracteres) | `ClaveSecretaDeAlMenos32CaracteresLongitud!!` |

### Cómo configurar y ejecutar

#### En Windows (PowerShell)
```powershell
$env:DB_USER="tu_usuario_mysql"
$env:DB_PASSWORD="tu_contrasena_mysql"
$env:JWT_SECRET="ClaveSecretaDeAlMenos32CaracteresLongitud!!"

# Compilar y empaquetar
./mvnw.cmd clean package -DskipTests

# Iniciar servidor
./mvnw.cmd spring-boot:run
```

#### En macOS / Linux (Bash)
```bash
export DB_USER="tu_usuario_mysql"
export DB_PASSWORD="tu_contrasena_mysql"
export JWT_SECRET="ClaveSecretaDeAlMenos32CaracteresLongitud!!"

# Compilar y empaquetar
./mvnw clean package -DskipTests

# Iniciar servidor
./mvnw spring-boot:run
```

---

## 9. Orden Recomendado del Flujo de Uso

1. **Registrar usuario:** Enviar `POST /api/auth/register` para crear el usuario inicial.
2. **Iniciar sesión:** Enviar `POST /api/auth/login` y copiar el token retornado en la respuesta.
3. **Promover administrador:** Ejecutar el `UPDATE` en MySQL para el usuario administrador.
4. **Abrir Swagger UI:** Ingresar a `http://localhost:8080/swagger-ui/index.html`.
5. **Autenticarse en Swagger:** Pulsar el botón verde **Authorize**, pegar el token JWT en el campo y pulsar *Authorize*.
6. **Registrar Veterinario:** Enviar `POST /api/veterinarios` con el token de ADMIN.
7. **Registrar Propietario:** Enviar `POST /api/propietarios`.
8. **Registrar Mascota:** Enviar `POST /api/mascotas` asociando el `propietarioId` obtenido.
9. **Programar Cita:** Enviar `POST /api/citas` indicando una fecha futura.
10. **Consultar Turnos:** Consultar la agenda general en `GET /api/citas` o por veterinario en `GET /api/citas/veterinario/{id}`.

---

## 10. Matriz de Pruebas

Para revisar el detalle de los 15 escenarios de prueba funcionales, de seguridad y de persistencia, consulta la matriz completa en:
* [Plan y Matriz de Pruebas (docs/pruebas.md)](docs/pruebas.md)

---

## 11. Errores Frecuentes y Soluciones

* **Estado HTTP 401 Unauthorized:**
  * *Causa:* Petición a un endpoint protegido sin incluir el token o con un token malformado/expirado.
  * *Solución:* Incluir la cabecera `Authorization: Bearer <token>` válida generada tras el login.
* **Estado HTTP 403 Forbidden:**
  * *Causa:* Petición autenticada pero con rol insuficiente (ejemplo: usuario con rol `USER` intentando ejecutar `POST /api/veterinarios`).
  * *Solución:* Asegurarse de que el usuario haya sido promovido a `ADMIN` en la base de datos y haya iniciado sesión nuevamente para renovar su token.
* **Estado HTTP 400 Bad Request por validación:**
  * *Causa:* Envío de campos en blanco, correos sin formato válido o fechas de cita que no son futuras.
  * *Solución:* Revisar el mapa de `errores` devuelto en el cuerpo de la respuesta `ApiError` y corregir los campos indicados.
* **Estado HTTP 400 Bad Request por colisión de horario:**
  * *Causa:* Intento de agendar una cita para un veterinario que ya tiene un turno reservado en esa misma fecha y hora.
  * *Solución:* Seleccionar otro horario o asignar otro profesional disponible.
* **Error de escape de comillas en PowerShell al usar `curl.exe`:**
  * *Causa:* El intérprete de PowerShell retira las barras invertidas `\"` antes de pasarlas a `curl.exe`, corrompiendo el JSON.
  * *Solución:* Utilizar la interfaz gráfica de **Swagger UI** o emplear el comando nativo `Invoke-RestMethod` de PowerShell.

---

## 12. Evidencias

El archivo que contiene las evidencias de cada una de las 7 partes se encuentra en:
* [Archivo de Evidencias](docs/evidencias/EVIDENCIAS.md)

---

## 13. Matriz de pruebas manuales

La matriz de pruebas manuales se encuentra en:
* [Archivo de matriz](docs/matriz/matriz.md)


