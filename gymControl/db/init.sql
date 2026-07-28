-- =====================================================================
-- init.sql — GymControl
-- Script de inicialización de base de datos (PostgreSQL)
--
-- Este archivo está pensado para montarse en el contenedor de Postgres
-- en /docker-entrypoint-initdb.d/init.sql, para que se ejecute
-- AUTOMÁTICAMENTE la primera vez que se crea el volumen de datos.
--
-- Crea el esquema completo (tablas + relaciones) reflejando las
-- estrategias de herencia usadas en las entidades JPA:
--   - Persona  -> @Inheritance(JOINED): persona / cliente / empleado / instructor
--   - Membresia -> @Inheritance(SINGLE_TABLE) + @DiscriminatorColumn: una sola tabla
-- Y agrega datos de prueba (seed data) para las capturas de pantalla.
-- =====================================================================

-- ---------------------------------------------------------------------
-- 1. PERSONA (tabla base, herencia JOINED)
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS persona (
    id        BIGSERIAL PRIMARY KEY,
    nombre    VARCHAR(150) NOT NULL,
    telefono  VARCHAR(20),
    email     VARCHAR(150),
    direccion VARCHAR(200),
    dtype     VARCHAR(20)  -- discriminador de herencia JOINED: CLIENTE | EMPLEADO | INSTRUCTOR
);

-- Cliente extiende Persona (tabla hija: comparte el mismo id)
CREATE TABLE IF NOT EXISTS cliente (
    id              BIGINT PRIMARY KEY REFERENCES persona(id) ON DELETE CASCADE,
    fecha_registro  DATE NOT NULL DEFAULT CURRENT_DATE,
    estado          VARCHAR(20) NOT NULL DEFAULT 'Active'
);

-- Empleado extiende Persona
CREATE TABLE IF NOT EXISTS empleado (
    id       BIGINT PRIMARY KEY REFERENCES persona(id) ON DELETE CASCADE,
    cargo    VARCHAR(100),
    salario  NUMERIC(10,2)
);

-- Instructor extiende Empleado (herencia de 2 niveles)
CREATE TABLE IF NOT EXISTS instructor (
    id            BIGINT PRIMARY KEY REFERENCES empleado(id) ON DELETE CASCADE,
    especialidad  VARCHAR(100),
    horario       VARCHAR(100),
    puntuacion    VARCHAR(10) DEFAULT '5.0',
    clientes      INTEGER DEFAULT 0,
    estatus       VARCHAR(20) DEFAULT 'Active',
    activo        BOOLEAN DEFAULT TRUE,
    image_url     VARCHAR(300)
);

-- ---------------------------------------------------------------------
-- 2. MEMBRESIA (tabla única, herencia SINGLE_TABLE + discriminador)
--    tipo_membresia: 'MENSUAL' | 'ANUAL' | 'VIP'
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS membresia (
    id                 BIGSERIAL PRIMARY KEY,
    tipo_membresia     VARCHAR(20) NOT NULL,
    precio             NUMERIC(10,2) NOT NULL,
    fecha_inicio       DATE NOT NULL,
    fecha_vencimiento  DATE,
    estado             VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
    cliente_id         BIGINT REFERENCES cliente(id) ON DELETE CASCADE
);

-- ---------------------------------------------------------------------
-- 3. PRODUCTO
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS producto (
    id         BIGSERIAL PRIMARY KEY,
    nombre     VARCHAR(150) NOT NULL,
    categoria  VARCHAR(100),
    precio     NUMERIC(10,2) NOT NULL,
    stock      INTEGER NOT NULL DEFAULT 0
);

-- ---------------------------------------------------------------------
-- 4. PAGO
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS pago (
    id              BIGSERIAL PRIMARY KEY,
    nombre_cliente  VARCHAR(150),
    concepto        VARCHAR(150),
    metodo          VARCHAR(30),
    monto           NUMERIC(10,2) NOT NULL,
    fecha           DATE NOT NULL DEFAULT CURRENT_DATE,
    estado          VARCHAR(20)
);

-- =====================================================================
-- DATOS DE PRUEBA (seed data)
-- =====================================================================

-- Personas (2 clientes + 1 instructor + 1 empleado de recepción)
INSERT INTO persona (id, nombre, telefono, email, direccion, dtype) VALUES
    (1, 'Ana Torres',      '0991234567', 'ana.torres@mail.com',        'Av. Universitaria 123', 'CLIENTE'),
    (2, 'Luis Fernández',  '0987654321', 'luis.fernandez@mail.com',    'Calle Bolívar 456',     'CLIENTE'),
    (3, 'Carlos Vega',     '0976543210', 'carlos.vega@gymcontrol.ec',  'Av. Gran Colombia 789', 'INSTRUCTOR'),
    (4, 'María Jiménez',   '0965432109', 'maria.jimenez@gymcontrol.ec','Calle Sucre 321',       'EMPLEADO')
ON CONFLICT (id) DO NOTHING;

-- Reinicia la secuencia para que los próximos INSERT de la app no choquen con estos ids fijos
SELECT setval(pg_get_serial_sequence('persona', 'id'), (SELECT MAX(id) FROM persona));

-- Clientes
INSERT INTO cliente (id, fecha_registro, estado) VALUES
    (1, CURRENT_DATE - INTERVAL '90 days', 'Active'),
    (2, CURRENT_DATE - INTERVAL '30 days', 'Active')
ON CONFLICT (id) DO NOTHING;

-- Empleados (Carlos e Instructor, María recepción)
INSERT INTO empleado (id, cargo, salario) VALUES
    (3, 'Entrenador Senior', 850.00),
    (4, 'Recepcionista',     500.00)
ON CONFLICT (id) DO NOTHING;

-- Instructor (solo Carlos)
INSERT INTO instructor (id, especialidad, horario, puntuacion, clientes, estatus, activo, image_url) VALUES
    (3, 'Musculación y Crossfit', 'Lunes a Viernes 06:00-14:00', '4.8', 12, 'Active', TRUE,
     'https://images.unsplash.com/photo-1518611012118-696072aa579a')
ON CONFLICT (id) DO NOTHING;

-- Membresías (una de cada tipo, incluida una vencida para probar esActiva()/renovar())
INSERT INTO membresia (tipo_membresia, precio, fecha_inicio, fecha_vencimiento, estado, cliente_id) VALUES
    ('MENSUAL', 25.00,  CURRENT_DATE - INTERVAL '10 days',  CURRENT_DATE + INTERVAL '20 days',  'ACTIVA',  1),
    ('ANUAL',   220.00, CURRENT_DATE - INTERVAL '60 days',  CURRENT_DATE + INTERVAL '305 days', 'ACTIVA',  2),
    ('VIP',     45.00,  CURRENT_DATE - INTERVAL '400 days', CURRENT_DATE - INTERVAL '35 days',  'VENCIDA', 1);

-- Productos
INSERT INTO producto (nombre, categoria, precio, stock) VALUES
    ('Proteína Whey 1kg',            'Suplementos', 35.50, 20),
    ('Guantes de Entrenamiento',     'Accesorios',  12.00, 15),
    ('Botella Deportiva 750ml',      'Accesorios',  8.00,  40),
    ('Creatina Monohidratada 300g',  'Suplementos', 18.75, 25);

-- Pagos
INSERT INTO pago (nombre_cliente, concepto, metodo, monto, fecha, estado) VALUES
    ('Ana Torres',     'Membresía Mensual',  'Card',     25.00, CURRENT_DATE - INTERVAL '10 days', 'Paid'),
    ('Luis Fernández', 'Membresía Anual',    'Transfer', 220.00, CURRENT_DATE - INTERVAL '60 days', 'Paid'),
    ('Ana Torres',     'Proteína Whey 1kg',  'Cash',     35.50, CURRENT_DATE - INTERVAL '5 days',  'Paid');
