BEGIN;

-- =========================================================
-- LIMPIEZA DE DATOS
-- =========================================================

TRUNCATE TABLE
    ventas.detalle_transaccion,
    ventas.transaccion_comercial,
    clinica.detalle_odontograma,
    clinica.odontograma,
    clinica.atencion_clinica,
    clinica.historia_clinica,
    clinica.elemento_odontograma,
    crm.cita,
    crm.lead_contacto,
    catalogo.odontologo_especialidad,
    catalogo.item_catalogo,
    catalogo.especialidad,
    seguridad.paciente,
    seguridad.odontologo,
    seguridad.usuario_rol,
    seguridad.usuario,
    seguridad.rol
RESTART IDENTITY CASCADE;


-- =========================================================
-- ROLES
-- =========================================================

INSERT INTO seguridad.rol (nombre, activo) VALUES
('ADMIN', TRUE),
('ODONTOLOGO', TRUE),
('PACIENTE', TRUE);


-- =========================================================
-- USUARIOS
-- Hash BCrypt ya validado en tu proyecto
-- =========================================================

INSERT INTO seguridad.usuario
(nombre_usuario, dni, nombres, apellidos, correo, password_hash, activo)
VALUES
(
    'admin',
    '12345678',
    'Administrador',
    'Sistema',
    'admin@clinica.com',
    '$2a$10$t92jVELPZuE/G5Zts.v0DuK8MDPlVvBMWbt752RfIQj9Ybn8ApEde',
    TRUE
),
(
    'doctor01',
    '87654321',
    'Carlos',
    'Ramirez',
    'doctor01@clinica.com',
    '$2a$10$t92jVELPZuE/G5Zts.v0DuK8MDPlVvBMWbt752RfIQj9Ybn8ApEde',
    TRUE
),
(
    'doctor02',
    '87654322',
    'Lucia',
    'Fernandez',
    'doctor02@clinica.com',
    '$2a$10$t92jVELPZuE/G5Zts.v0DuK8MDPlVvBMWbt752RfIQj9Ybn8ApEde',
    TRUE
),
(
    'paciente01',
    '11223344',
    'Maria',
    'Lopez',
    'maria@gmail.com',
    '$2a$10$t92jVELPZuE/G5Zts.v0DuK8MDPlVvBMWbt752RfIQj9Ybn8ApEde',
    TRUE
),
(
    'paciente02',
    '22334455',
    'Juan',
    'Perez',
    'juan@gmail.com',
    '$2a$10$t92jVELPZuE/G5Zts.v0DuK8MDPlVvBMWbt752RfIQj9Ybn8ApEde',
    TRUE
);


-- =========================================================
-- ROLES DE USUARIO
-- =========================================================

INSERT INTO seguridad.usuario_rol (id_usuario, id_rol, activo)
SELECT u.id, r.id, TRUE
FROM seguridad.usuario u
JOIN seguridad.rol r ON
       (u.nombre_usuario = 'admin' AND r.nombre = 'ADMIN')
    OR (u.nombre_usuario IN ('doctor01', 'doctor02')
        AND r.nombre = 'ODONTOLOGO')
    OR (u.nombre_usuario IN ('paciente01', 'paciente02')
        AND r.nombre = 'PACIENTE');


-- =========================================================
-- ODONTÓLOGOS
-- =========================================================

INSERT INTO seguridad.odontologo (id_usuario, cop, activo)
SELECT id, 'COP-001', TRUE
FROM seguridad.usuario
WHERE nombre_usuario = 'doctor01';

INSERT INTO seguridad.odontologo (id_usuario, cop, activo)
SELECT id, 'COP-002', TRUE
FROM seguridad.usuario
WHERE nombre_usuario = 'doctor02';


-- =========================================================
-- PACIENTES
-- =========================================================

INSERT INTO seguridad.paciente
(id_usuario, grupo_sanguineo, alergias, fecha_nacimiento, activo)
SELECT
    id,
    'O+',
    'Ninguna',
    DATE '1998-05-12',
    TRUE
FROM seguridad.usuario
WHERE nombre_usuario = 'paciente01';

INSERT INTO seguridad.paciente
(id_usuario, grupo_sanguineo, alergias, fecha_nacimiento, activo)
SELECT
    id,
    'A+',
    'Penicilina',
    DATE '1995-08-20',
    TRUE
FROM seguridad.usuario
WHERE nombre_usuario = 'paciente02';


-- =========================================================
-- ESPECIALIDADES
-- =========================================================

INSERT INTO catalogo.especialidad (nombre, activo) VALUES
('Odontología General', TRUE),
('Ortodoncia', TRUE),
('Endodoncia', TRUE);


-- =========================================================
-- RELACIÓN ODONTÓLOGO - ESPECIALIDAD
-- =========================================================

INSERT INTO catalogo.odontologo_especialidad
(id_odontologo, id_especialidad)
SELECT o.id_usuario, e.id
FROM seguridad.odontologo o
JOIN seguridad.usuario u
    ON u.id = o.id_usuario
JOIN catalogo.especialidad e
    ON e.nombre = 'Odontología General'
WHERE u.nombre_usuario = 'doctor01';

INSERT INTO catalogo.odontologo_especialidad
(id_odontologo, id_especialidad)
SELECT o.id_usuario, e.id
FROM seguridad.odontologo o
JOIN seguridad.usuario u
    ON u.id = o.id_usuario
JOIN catalogo.especialidad e
    ON e.nombre = 'Endodoncia'
WHERE u.nombre_usuario = 'doctor02';


-- =========================================================
-- CITAS DE PRUEBA
-- Estados alineados con EstadoCita.java y PostgreSQL
-- =========================================================

INSERT INTO crm.cita
(
    id_paciente,
    id_odontologo,
    fecha_hora,
    estado,
    canal_origen,
    monto_adelanto,
    referencia_adelanto
)
VALUES
(
    (SELECT id FROM seguridad.usuario
     WHERE nombre_usuario = 'paciente01'),

    (SELECT id FROM seguridad.usuario
     WHERE nombre_usuario = 'doctor01'),

    CURRENT_TIMESTAMP + INTERVAL '1 day',
    'CONFIRMADA',
    'APP-IOS',
    20.00,
    'PRUEBA-IOS-001'
),
(
    (SELECT id FROM seguridad.usuario
     WHERE nombre_usuario = 'paciente02'),

    (SELECT id FROM seguridad.usuario
     WHERE nombre_usuario = 'doctor02'),

    CURRENT_TIMESTAMP + INTERVAL '2 day',
    'PENDIENTE',
    'APP-IOS',
    0.00,
    NULL
);

COMMIT;