-- HomeServices CR - Script inicial de base de datos
-- Ejecutar en MySQL Workbench contra la base homeservices_cr de Aiven o contra MySQL local.

CREATE DATABASE IF NOT EXISTS homeservices_cr CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE homeservices_cr;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS calificacion;
DROP TABLE IF EXISTS solicitud;
DROP TABLE IF EXISTS disponibilidad;
DROP TABLE IF EXISTS servicio;
DROP TABLE IF EXISTS proveedor;
DROP TABLE IF EXISTS categoria;
DROP TABLE IF EXISTS usuario;
SET FOREIGN_KEY_CHECKS = 1;

CREATE TABLE usuario (
    id_usuario BIGINT NOT NULL AUTO_INCREMENT,
    nombre_completo VARCHAR(120) NOT NULL,
    correo VARCHAR(120) NOT NULL UNIQUE,
    password VARCHAR(120) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    telefono VARCHAR(30),
    ubicacion VARCHAR(120),
    activo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id_usuario)
);

CREATE TABLE categoria (
    id_categoria BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(80) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activo BOOLEAN DEFAULT TRUE,
    PRIMARY KEY (id_categoria)
);

CREATE TABLE proveedor (
    id_proveedor BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    especialidad VARCHAR(120),
    ubicacion VARCHAR(120),
    experiencia VARCHAR(500),
    telefono VARCHAR(30),
    verificado BOOLEAN DEFAULT FALSE,
    calificacion_promedio DOUBLE DEFAULT 0,
    servicios_completados INT DEFAULT 0,
    id_usuario BIGINT,
    PRIMARY KEY (id_proveedor),
    CONSTRAINT fk_proveedor_usuario FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE servicio (
    id_servicio BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(120) NOT NULL,
    descripcion VARCHAR(600),
    precio_referencia DECIMAL(10,2),
    activo BOOLEAN DEFAULT TRUE,
    id_categoria BIGINT NOT NULL,
    id_proveedor BIGINT NOT NULL,
    PRIMARY KEY (id_servicio),
    CONSTRAINT fk_servicio_categoria FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    CONSTRAINT fk_servicio_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor)
);

CREATE TABLE disponibilidad (
    id_disponibilidad BIGINT NOT NULL AUTO_INCREMENT,
    dia_semana VARCHAR(20) NOT NULL,
    hora_inicio TIME,
    hora_fin TIME,
    disponible BOOLEAN DEFAULT TRUE,
    id_proveedor BIGINT NOT NULL,
    PRIMARY KEY (id_disponibilidad),
    CONSTRAINT fk_disponibilidad_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor)
);

CREATE TABLE solicitud (
    id_solicitud BIGINT NOT NULL AUTO_INCREMENT,
    id_cliente BIGINT NOT NULL,
    id_proveedor BIGINT NOT NULL,
    id_servicio BIGINT NOT NULL,
    fecha_servicio DATE,
    hora_servicio TIME,
    direccion VARCHAR(255),
    descripcion_trabajo VARCHAR(800),
    estado VARCHAR(30) DEFAULT 'PENDIENTE',
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_solicitud),
    CONSTRAINT fk_solicitud_cliente FOREIGN KEY (id_cliente) REFERENCES usuario(id_usuario),
    CONSTRAINT fk_solicitud_proveedor FOREIGN KEY (id_proveedor) REFERENCES proveedor(id_proveedor),
    CONSTRAINT fk_solicitud_servicio FOREIGN KEY (id_servicio) REFERENCES servicio(id_servicio)
);

CREATE TABLE calificacion (
    id_calificacion BIGINT NOT NULL AUTO_INCREMENT,
    id_solicitud BIGINT NOT NULL UNIQUE,
    puntaje INT,
    comentario VARCHAR(600),
    verificado BOOLEAN DEFAULT TRUE,
    reportado BOOLEAN DEFAULT FALSE,
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_calificacion),
    CONSTRAINT fk_calificacion_solicitud FOREIGN KEY (id_solicitud) REFERENCES solicitud(id_solicitud)
);

INSERT INTO usuario (id_usuario, nombre_completo, correo, password, rol, telefono, ubicacion, activo) VALUES
(1, 'Marco Demo', 'marco.demo@homeservices.cr', '123456', 'CLIENTE', '8888-0001', 'San José', TRUE),
(2, 'Carlos Méndez', 'proveedor.demo@homeservices.cr', '123456', 'PROVEEDOR', '8888-0002', 'Heredia', TRUE),
(3, 'Andrea Vargas', 'andrea@homeservices.cr', '123456', 'PROVEEDOR', '8888-0003', 'Alajuela', TRUE),
(4, 'Administrador HomeServices', 'admin@homeservices.cr', '123456', 'ADMIN', '8888-0004', 'San José', TRUE);

INSERT INTO categoria (id_categoria, nombre, descripcion, activo) VALUES
(1, 'Plomería', 'Reparación de fugas, instalación de tuberías y mantenimiento general.', TRUE),
(2, 'Electricidad', 'Instalaciones eléctricas, revisión de cableado y emergencias.', TRUE),
(3, 'Limpieza', 'Servicios de limpieza residencial y comercial.', TRUE),
(4, 'Jardinería', 'Mantenimiento de jardines, poda y diseño básico.', TRUE),
(5, 'Computación', 'Soporte técnico, mantenimiento e instalación de equipos.', TRUE),
(6, 'Aire acondicionado', 'Instalación, limpieza y mantenimiento de equipos.', TRUE);

INSERT INTO proveedor (id_proveedor, nombre, especialidad, ubicacion, experiencia, telefono, verificado, calificacion_promedio, servicios_completados, id_usuario) VALUES
(1, 'Carlos Méndez', 'Electricista residencial', 'Heredia', 'Profesional con más de 8 años de experiencia en instalaciones, mantenimiento preventivo y atención de emergencias eléctricas.', '8888-0002', TRUE, 4.9, 126, 2),
(2, 'Andrea Vargas', 'Jardinería y mantenimiento', 'Alajuela', 'Especialista en mantenimiento de zonas verdes, poda, limpieza de jardines y recuperación de espacios exteriores.', '8888-0003', TRUE, 4.8, 87, 3),
(3, 'CleanHouse CR', 'Limpieza residencial', 'San José', 'Equipo dedicado a limpieza profunda de hogares, oficinas pequeñas y apartamentos.', '8888-0005', TRUE, 4.7, 98, NULL);

INSERT INTO servicio (id_servicio, nombre, descripcion, precio_referencia, activo, id_categoria, id_proveedor) VALUES
(1, 'Revisión eléctrica residencial', 'Diagnóstico general de instalación eléctrica y revisión de breakers.', 20000, TRUE, 2, 1),
(2, 'Instalación de lámparas', 'Instalación y prueba de lámparas o luminarias residenciales.', 18000, TRUE, 2, 1),
(3, 'Mantenimiento de jardín', 'Corte de césped, limpieza de zonas verdes y poda básica.', 22000, TRUE, 4, 2),
(4, 'Limpieza profunda', 'Limpieza general para casas o apartamentos con enfoque en cocina y baños.', 28000, TRUE, 3, 3),
(5, 'Soporte técnico básico', 'Revisión de computadora, instalación de programas y limpieza lógica.', 25000, TRUE, 5, 1),
(6, 'Cambio de grifería', 'Reemplazo de grifería y revisión de fugas menores.', 18000, TRUE, 1, 3);

INSERT INTO disponibilidad (dia_semana, hora_inicio, hora_fin, disponible, id_proveedor) VALUES
('Lunes', '08:00:00', '12:00:00', TRUE, 1),
('Martes', '13:00:00', '17:00:00', TRUE, 1),
('Miércoles', '09:00:00', '12:00:00', TRUE, 1),
('Jueves', '08:00:00', '11:00:00', TRUE, 2),
('Viernes', '13:00:00', '16:00:00', TRUE, 2),
('Sábado', '08:00:00', '12:00:00', TRUE, 3);

INSERT INTO solicitud (id_solicitud, id_cliente, id_proveedor, id_servicio, fecha_servicio, hora_servicio, direccion, descripcion_trabajo, estado, fecha_creacion) VALUES
(1, 1, 1, 1, '2026-07-15', '10:00:00', 'Heredia centro', 'Revisar apagones intermitentes en la sala.', 'ACEPTADA', NOW()),
(2, 1, 3, 4, '2026-07-18', '08:00:00', 'San José, Sabana', 'Limpieza profunda del apartamento.', 'PENDIENTE', NOW()),
(3, 1, 2, 3, '2026-07-02', '14:00:00', 'Alajuela centro', 'Mantenimiento general del jardín.', 'FINALIZADA', NOW());

INSERT INTO calificacion (id_solicitud, puntaje, comentario, verificado, reportado, fecha_creacion) VALUES
(3, 5, 'Excelente atención y puntualidad. El jardín quedó muy ordenado.', TRUE, FALSE, NOW());
