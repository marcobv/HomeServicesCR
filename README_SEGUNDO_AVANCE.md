# HomeServices CR - Base para segundo avance

Proyecto base para la implementación parcial del sitio web HomeServices CR.

## Objetivo del ZIP

Este proyecto deja una base funcional para que el grupo pueda trabajar en GitHub y completar el segundo avance. Incluye arquitectura MVC, conexión a MySQL/Aiven, páginas Thymeleaf, Bootstrap mediante WebJars y un script SQL inicial.

## Configuración de base de datos

La base creada en Aiven se llama:

```text
homeservices_cr
```

El archivo principal está en:

```text
src/main/resources/application.properties
```

Debe actualizarse la contraseña:

```properties
spring.datasource.password=AQUI_PASSWORD_AIVEN
```

No subir contraseñas reales a GitHub. Para compartir el formato existe:

```text
src/main/resources/application.properties.example
```

## Script SQL

Ejecutar en MySQL Workbench o en el cliente conectado a Aiven:

```text
database/creaHomeServices.sql
```

El script crea las tablas principales y datos de prueba:

- usuario
- categoria
- proveedor
- servicio
- disponibilidad
- solicitud
- calificacion

## Rutas principales

```text
/                         Página de inicio
/inicio                   Página de inicio
/usuarios/registro        Registro de usuario
/usuarios/login           Inicio de sesión básico
/servicios/resultados     Búsqueda y filtros
/servicios/proveedor/{id} Perfil del proveedor
/solicitudes/nueva/{id}   Solicitud de servicio
/cliente/panel            Panel del cliente
/proveedor/panel          Panel del proveedor
/admin/panel              Panel administrativo
/admin/categorias         Administración de categorías
```

## Usuarios demo

Después de ejecutar el script SQL:

```text
Cliente:     marco.demo@homeservices.cr      / 123456
Proveedor:   proveedor.demo@homeservices.cr  / 123456
Admin:       admin@homeservices.cr           / 123456
```

## Historias de usuario cubiertas como base

Esta base deja avanzadas o funcionales las siguientes historias del primer avance:

- GC-01 Registro de usuario
- GC-02 Inicio de sesión
- BS-01 Buscar servicios
- BS-02 Filtrar resultados
- BS-03 Ver perfil del proveedor
- BS-04 Consultar disponibilidad
- CT-01 Solicitar servicio
- CT-02 Seguimiento de solicitud
- CT-03 Historial de contrataciones
- CV-01 Calificar proveedor
- CV-02 Comentario verificado
- CV-03 Ver puntuación general
- PR-02 Publicar servicio
- AD-01 Administrar usuarios, consulta básica
- AD-03 Administrar categorías

## Distribución sugerida del trabajo

Marco puede subir la base inicial con estos módulos:

- Proyecto Spring Boot MVC
- Entidades, repositorios, servicios y controladores base
- Conexión a Aiven
- Script SQL
- Inicio, resultados, perfil, solicitud y paneles

Compañero/a 1 puede trabajar en:

- Mejorar calificaciones
- Reporte de comentarios
- Panel del cliente
- Evidencia visual del flujo cliente

Compañero/a 2 puede trabajar en:

- Panel del proveedor
- Gestión de disponibilidad
- CRUD de categorías
- Funciones administrativas

## Notas importantes

- Bootstrap se carga mediante WebJars, no por CDN.
- El proyecto usa Spring Boot, Thymeleaf, JPA/Hibernate y MySQL.
- La seguridad es básica para efectos de avance académico; si se desea, luego puede agregarse Spring Security.
- Antes de entregar el ZIP final, eliminar la carpeta `target`.
