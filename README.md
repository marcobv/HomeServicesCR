# HomeServices CR — proyecto final

Aplicación web académica para buscar, publicar, contratar y calificar servicios para el hogar en Costa Rica. Utiliza Spring Boot, Spring MVC, Thymeleaf, JPA/Hibernate, MySQL y Bootstrap, y puede abrirse directamente como proyecto Maven en NetBeans.

## Funcionalidades

- Registro e inicio de sesión para clientes y proveedores.
- Sesiones y autorización por roles `CLIENTE`, `PROVEEDOR` y `ADMIN`.
- Edición del perfil personal y recuperación académica de contraseña.
- Búsqueda por palabra, categoría, ubicación, precio y calificación.
- Perfil público del proveedor, catálogo y comentarios verificados.
- Solicitudes con validación de disponibilidad, fecha y choque de horario.
- Flujo controlado: pendiente, aceptada/rechazada y finalizada.
- Calificación de servicios finalizados y reporte de comentarios.
- Panel del proveedor para perfil profesional, servicios y horarios.
- Panel administrativo para usuarios, categorías, proveedores, solicitudes y moderación.
- Contraseñas nuevas protegidas con PBKDF2; las cuentas antiguas continúan funcionando y se actualizan automáticamente después de un inicio de sesión válido.

## Requisitos

- JDK 17
- NetBeans con soporte Maven, o Maven 3.9+
- MySQL 8 compatible (preparado para la base compartida en Aiven)

## Ejecución en NetBeans

1. Descomprima el proyecto.
2. En NetBeans seleccione **File > Open Project** y abra esta carpeta.
3. Revise `src/main/resources/application.properties` y confirme la conexión autorizada a la base de datos.
4. Ejecute **Clean and Build**.
5. Ejecute `HomeServicesCrApplication`.
6. Abra `http://localhost:8080`.

También puede ejecutarse desde una terminal:

```bash
mvn clean test
mvn spring-boot:run
```

## Usuarios de demostración

| Rol | Correo | Contraseña |
|---|---|---|
| Cliente | `marco.demo@homeservices.cr` | `123456` |
| Proveedor | `proveedor.demo@homeservices.cr` | `123456` |
| Administrador | `admin@homeservices.cr` | `123456` |

Los registros se encuentran en `database/creaHomeServices.sql`. El script recrea las tablas, por lo que debe ejecutarse solamente para reinicializar una base de pruebas.

## Despliegue gratuito en Render

El `Dockerfile` compila la aplicación en Java 17 y escucha el puerto indicado por Render mediante `${PORT}`. Para producción se recomienda configurar en Render las variables de conexión a Aiven y mantener las credenciales fuera del repositorio. La aplicación enlaza el servidor a `0.0.0.0`, requisito para que Render detecte el puerto.

## Verificación antes de entregar

```bash
mvn clean test
mvn clean package -DskipTests
```

La carpeta `target` no forma parte del proyecto fuente ni del ZIP de entrega.
