-- =====================================================
-- EBSA NEXUS - DATOS INICIALES CON CREW MANAGEMENT
-- =====================================================

USE `mydb`;

SET FOREIGN_KEY_CHECKS=0;

-- =====================================================
-- INSERTAR ROLES DEL SISTEMA
-- =====================================================
INSERT INTO `roles` (`name`, `description`, `created_at`, `updated_at`) VALUES
('ADMIN', 'Administrador del sistema con acceso completo', NOW(), NOW()),
('JEFE_AREA', 'Jefe de area con permisos de gestion', NOW(), NOW()),
('SUPERVISOR', 'Supervisor de cuadrillas y operaciones', NOW(), NOW()),
('TRABAJADOR', 'Trabajador del sistema', NOW(), NOW()),
('LIDER_CUADRILLA', 'Líder de cuadrilla de trabajo', NOW(), NOW());

-- =====================================================
-- INSERTAR WORK ROLES - PERSONAL INTERNO
-- =====================================================
INSERT INTO `work_roles` (`name`, `description`, `type`, `created_at`, `updated_at`) VALUES
('Desarrollador', 'Desarrollador de software', 'intern', NOW(), NOW()),
('Administrador', 'Administrador de sistemas', 'intern', NOW(), NOW()),
('Coordinacion de distribucion', 'Responsable de coordinar la distribucion electrica', 'intern', NOW(), NOW()),
('Coordinador comercial', 'Coordinador del area comercial', 'intern', NOW(), NOW()),
('Jefe de Cuadrilla', 'Lider de cuadrilla de trabajo', 'intern', NOW(), NOW()),
('Liniero', 'Tecnico de lineas electricas', 'intern', NOW(), NOW()),
('Electricista', 'Electricista general', 'intern', NOW(), NOW()),
('Operario', 'Operario de mantenimiento', 'intern', NOW(), NOW());

-- =====================================================
-- INSERTAR WORK ROLES - PERSONAL EXTERNO
-- =====================================================
INSERT INTO `work_roles` (`name`, `description`, `type`, `created_at`, `updated_at`) VALUES
('Consultor', 'Consultor externo', 'extern', NOW(), NOW()),
('Auxiliares', 'Personal auxiliar de soporte', 'extern', NOW(), NOW()),
('Aforadores', 'Personal encargado de mediciones', 'extern', NOW(), NOW()),
('Genicos', 'Personal tecnico especializado', 'extern', NOW(), NOW()),
('Liniero Externo', 'Tecnico de lineas electricas externo', 'extern', NOW(), NOW()),
('Contratista', 'Contratista externo', 'extern', NOW(), NOW());

-- =====================================================
-- INSERTAR ÁREAS
-- =====================================================
INSERT INTO `areas` (`code`, `name`, `description`) VALUES
('FACTURACION', 'Facturación', 'Área de facturación y cobros'),
('CARTERA', 'Cartera', 'Área de gestión de cartera'),
('PERDIDAS', 'Pérdidas', 'Área de control de pérdidas');

-- =====================================================
-- INSERTAR USUARIOS DEL SISTEMA
-- =====================================================

-- Usuario Administrador
INSERT INTO `users` (
    `uuid`, 
    `username`,
    `email`, 
    `pwd_hash`, 
    `first_name`, 
    `last_name`, 
    `role_id`, 
    `work_role_id`,
    `work_type`, 
    `document_number`, 
    `phone`, 
    `active`, 
    `created_at`, 
    `updated_at`
) VALUES (
    'admin-uuid-001', 
    'admin',
    'admin@ebsa.com.co', 
    '$2a$12$KO2D8u/O9SDu99spxwIfRuMdCKpVoG8Ol8TwqUWK0cQPTjUDeFC2e',
    'Administrador',
    'Sistema',
    1,
    2,
    'intern',
    '12345678',
    '+57-300-1234567',
    1,
    NOW(),
    NOW()
);

-- Usuario Supervisor
INSERT INTO `users` (
    `uuid`, 
    `username`,
    `email`, 
    `pwd_hash`, 
    `first_name`, 
    `last_name`, 
    `role_id`, 
    `work_role_id`,
    `work_type`, 
    `document_number`, 
    `phone`, 
    `active`, 
    `created_at`, 
    `updated_at`
) VALUES (
    'supervisor-uuid-001', 
    'supervisor',
    'supervisor@ebsa.com.co', 
    '$2a$12$KO2D8u/O9SDu99spxwIfRuMdCKpVoG8Ol8TwqUWK0cQPTjUDeFC2e',
    'Carlos',
    'Supervisor',
    3,
    3,
    'intern',
    '87654321',
    '+57-300-2345678',
    1,
    NOW(),
    NOW()
);

-- Líder de Cuadrilla 1
INSERT INTO `users` (
    `uuid`, 
    `username`,
    `email`, 
    `pwd_hash`, 
    `first_name`, 
    `last_name`, 
    `role_id`, 
    `work_role_id`,
    `work_type`, 
    `document_number`, 
    `phone`, 
    `active`, 
    `created_at`, 
    `updated_at`
) VALUES (
    'leader1-uuid-001', 
    'jperez',
    'juan.perez@ebsa.com.co', 
    '$2a$12$KO2D8u/O9SDu99spxwIfRuMdCKpVoG8Ol8TwqUWK0cQPTjUDeFC2e',
    'Juan',
    'Pérez',
    5,
    5,
    'intern',
    '11111111',
    '+57-310-1111111',
    1,
    NOW(),
    NOW()
);

-- Liniero 1
INSERT INTO `users` (
    `uuid`, 
    `username`,
    `email`, 
    `pwd_hash`, 
    `first_name`, 
    `last_name`, 
    `role_id`, 
    `work_role_id`,
    `work_type`, 
    `document_number`, 
    `phone`, 
    `active`, 
    `created_at`, 
    `updated_at`
) VALUES (
    'worker1-uuid-001', 
    'mgarcia',
    'miguel.garcia@ebsa.com.co', 
    '$2a$12$KO2D8u/O9SDu99spxwIfRuMdCKpVoG8Ol8TwqUWK0cQPTjUDeFC2e',
    'Miguel',
    'García',
    4,
    6,
    'intern',
    '22222222',
    '+57-310-2222222',
    1,
    NOW(),
    NOW()
);

-- Liniero 2
INSERT INTO `users` (
    `uuid`, 
    `username`,
    `email`, 
    `pwd_hash`, 
    `first_name`, 
    `last_name`, 
    `role_id`, 
    `work_role_id`,
    `work_type`, 
    `document_number`, 
    `phone`, 
    `active`, 
    `created_at`, 
    `updated_at`
) VALUES (
    'worker2-uuid-001', 
    'alopez',
    'andres.lopez@ebsa.com.co', 
    '$2a$12$KO2D8u/O9SDu99spxwIfRuMdCKpVoG8Ol8TwqUWK0cQPTjUDeFC2e',
    'Andrés',
    'López',
    4,
    6,
    'intern',
    '33333333',
    '+57-310-3333333',
    1,
    NOW(),
    NOW()
);

-- Líder de Cuadrilla 2
INSERT INTO `users` (
    `uuid`, 
    `username`,
    `email`, 
    `pwd_hash`, 
    `first_name`, 
    `last_name`, 
    `role_id`, 
    `work_role_id`,
    `work_type`, 
    `document_number`, 
    `phone`, 
    `active`, 
    `created_at`, 
    `updated_at`
) VALUES (
    'leader2-uuid-001', 
    'lmartinez',
    'laura.martinez@ebsa.com.co', 
    '$2a$12$KO2D8u/O9SDu99spxwIfRuMdCKpVoG8Ol8TwqUWK0cQPTjUDeFC2e',
    'Laura',
    'Martínez',
    5,
    5,
    'intern',
    '44444444',
    '+57-310-4444444',
    1,
    NOW(),
    NOW()
);

-- Electricista 1
INSERT INTO `users` (
    `uuid`, 
    `username`,
    `email`, 
    `pwd_hash`, 
    `first_name`, 
    `last_name`, 
    `role_id`, 
    `work_role_id`,
    `work_type`, 
    `document_number`, 
    `phone`, 
    `active`, 
    `created_at`, 
    `updated_at`
) VALUES (
    'worker3-uuid-001', 
    'prodriguez',
    'pedro.rodriguez@ebsa.com.co', 
    '$2a$12$KO2D8u/O9SDu99spxwIfRuMdCKpVoG8Ol8TwqUWK0cQPTjUDeFC2e',
    'Pedro',
    'Rodríguez',
    4,
    7,
    'intern',
    '55555555',
    '+57-310-5555555',
    1,
    NOW(),
    NOW()
);

-- Operario 1
INSERT INTO `users` (
    `uuid`, 
    `username`,
    `email`, 
    `pwd_hash`, 
    `first_name`, 
    `last_name`, 
    `role_id`, 
    `work_role_id`,
    `work_type`, 
    `document_number`, 
    `phone`, 
    `active`, 
    `created_at`, 
    `updated_at`
) VALUES (
    'worker4-uuid-001', 
    'cgomez',
    'carlos.gomez@ebsa.com.co', 
    '$2a$12$KO2D8u/O9SDu99spxwIfRuMdCKpVoG8Ol8TwqUWK0cQPTjUDeFC2e',
    'Carlos',
    'Gómez',
    4,
    8,
    'intern',
    '66666666',
    '+57-310-6666666',
    1,
    NOW(),
    NOW()
);

-- =====================================================
-- INSERTAR CUADRILLAS
-- =====================================================

-- Cuadrilla Alpha - Mantenimiento Preventivo
INSERT INTO `crews` (`name`, `description`, `created_by`, `status`, `created_at`, `updated_at`) VALUES
('Cuadrilla Alpha', 'Cuadrilla especializada en mantenimiento preventivo de infraestructura eléctrica', 1, 'DISPONIBLE', NOW(), NOW());

-- Cuadrilla Beta - Emergencias
INSERT INTO `crews` (`name`, `description`, `created_by`, `status`, `created_at`, `updated_at`) VALUES
('Cuadrilla Beta', 'Cuadrilla de respuesta rápida para emergencias y fallas críticas', 1, 'DISPONIBLE', NOW(), NOW());

-- Cuadrilla Gamma - Instalaciones
INSERT INTO `crews` (`name`, `description`, `created_by`, `status`, `created_at`, `updated_at`) VALUES
('Cuadrilla Gamma', 'Cuadrilla especializada en nuevas instalaciones y expansión de red', 2, 'DISPONIBLE', NOW(), NOW());

-- =====================================================
-- INSERTAR MIEMBROS DE CUADRILLAS
-- =====================================================

-- Cuadrilla Alpha (ID: 1)
-- Líder: Juan Pérez (user_id: 3)
INSERT INTO `crew_members` (`crew_id`, `user_id`, `is_leader`, `joined_at`) VALUES
(1, 3, 1, NOW());

-- Miembro: Miguel García (user_id: 4)
INSERT INTO `crew_members` (`crew_id`, `user_id`, `is_leader`, `joined_at`) VALUES
(1, 4, 0, NOW());

-- Miembro: Andrés López (user_id: 5)
INSERT INTO `crew_members` (`crew_id`, `user_id`, `is_leader`, `joined_at`) VALUES
(1, 5, 0, NOW());

-- Cuadrilla Beta (ID: 2)
-- Líder: Laura Martínez (user_id: 6)
INSERT INTO `crew_members` (`crew_id`, `user_id`, `is_leader`, `joined_at`) VALUES
(2, 6, 1, NOW());

-- Miembro: Pedro Rodríguez (user_id: 7)
INSERT INTO `crew_members` (`crew_id`, `user_id`, `is_leader`, `joined_at`) VALUES
(2, 7, 0, NOW());

-- Cuadrilla Gamma (ID: 3)
-- Miembro: Carlos Gómez (user_id: 8) - Sin líder asignado aún
INSERT INTO `crew_members` (`crew_id`, `user_id`, `is_leader`, `joined_at`) VALUES
(3, 8, 0, NOW());

-- =====================================================
-- ACTUALIZAR NOVEDADES CON CUADRILLAS ASIGNADAS
-- =====================================================

-- Asignar Cuadrilla Alpha a Novedad 1
UPDATE `novelties` SET `crew_id` = 1 WHERE `id` = 1;

-- Asignar Cuadrilla Beta a Novedad 2
UPDATE `novelties` SET `crew_id` = 2 WHERE `id` = 2;

-- Asignar Cuadrilla Gamma a Novedad 3
UPDATE `novelties` SET `crew_id` = 3 WHERE `id` = 3;

-- =====================================================
-- INSERTAR UBICACIONES DE EJEMPLO
-- =====================================================
INSERT INTO `Location` (`name`, `details`) VALUES
('Zona Norte', 'Sector residencial norte'),
('Zona Sur', 'Sector comercial sur'),
('Zona Centro', 'Centro histórico'),
('Zona Oriental', 'Sector industrial oriental');

-- =====================================================
-- INSERTAR NOVEDADES DE EJEMPLO
-- =====================================================

-- Novedad 1 - Error de lectura (SIN crew_id inicialmente)
INSERT INTO `novelties` (
    `area_id`,
    `reason`,
    `account_number`,
    `meter_number`,
    `active_reading`,
    `reactive_reading`,
    `municipality`,
    `address`,
    `description`,
    `observations`,
    `status`,
    `created_by`,
    `created_at`
) VALUES (
    1,
    'ERROR_LECTURA',
    'ACC-00001',
    'MTR-00001',
    1234.56,
    789.01,
    'Tunja',
    'Zona Norte - Transformador Principal',
    'Transformador presenta sobrecalentamiento y ruidos anormales. Se requiere revisión urgente del medidor.',
    'Prioridad alta',
    'CREADA',
    1,
    NOW()
);

-- Novedad 2 - Actualización de datos (SIN crew_id inicialmente)
INSERT INTO `novelties` (
    `area_id`,
    `reason`,
    `account_number`,
    `meter_number`,
    `active_reading`,
    `reactive_reading`,
    `municipality`,
    `address`,
    `description`,
    `observations`,
    `status`,
    `created_by`,
    `created_at`
) VALUES (
    2,
    'ACTUALIZACION_DATOS',
    'ACC-00002',
    'MTR-00002',
    5678.90,
    234.56,
    'Tunja',
    'Sector Comercial - Calle 19',
    'Revisión programada de red eléctrica en sector comercial. Actualización de datos de consumo requerida.',
    'Revisión programada',
    'CREADA',
    2,
    NOW()
);

-- Novedad 3 - Otro motivo (SIN crew_id inicialmente)
INSERT INTO `novelties` (
    `area_id`,
    `reason`,
    `account_number`,
    `meter_number`,
    `active_reading`,
    `reactive_reading`,
    `municipality`,
    `address`,
    `description`,
    `observations`,
    `status`,
    `created_by`,
    `created_at`
) VALUES (
    1,
    'OTROS',
    'ACC-00003',
    'MTR-00003',
    901.23,
    456.78,
    'Tunja',
    'Nueva Instalación - Barrio Sur',
    'Cliente solicita instalación de medidor bifásico. Nueva instalación requerida en barrio residencial.',
    'Nueva instalación',
    'CREADA',
    2,
    NOW()
);

-- =====================================================
-- Reactivar restricciones de claves foráneas
-- =====================================================
SET FOREIGN_KEY_CHECKS=1;

-- =====================================================
-- VERIFICAR INSERCIONES
-- =====================================================
SELECT '=====================================' as '';
SELECT '=== RESUMEN DE DATOS INICIALES ===' as '';
SELECT '=====================================' as '';

SELECT 'ROLES DEL SISTEMA' as 'Tabla', COUNT(*) as 'Total' FROM `roles`
UNION ALL
SELECT 'WORK ROLES', COUNT(*) FROM `work_roles`
UNION ALL
SELECT 'ÁREAS', COUNT(*) FROM `areas`
UNION ALL
SELECT 'USUARIOS', COUNT(*) FROM `users`
UNION ALL
SELECT 'CUADRILLAS', COUNT(*) FROM `crews`
UNION ALL
SELECT 'MIEMBROS DE CUADRILLAS', COUNT(*) FROM `crew_members`
UNION ALL
SELECT 'UBICACIONES', COUNT(*) FROM `Location`
UNION ALL
SELECT 'NOVEDADES', COUNT(*) FROM `novelties`;

SELECT '=====================================' as '';
SELECT '=== DETALLE DE CUADRILLAS ===' as '';
SELECT '=====================================' as '';

SELECT 
    c.id as 'ID',
    c.name as 'Cuadrilla',
    c.status as 'Estado',
    COUNT(cm.id) as 'Miembros',
    SUM(cm.is_leader) as 'Líderes'
FROM crews c
LEFT JOIN crew_members cm ON c.id = cm.crew_id AND cm.left_at IS NULL
GROUP BY c.id, c.name, c.status;

SELECT '=====================================' as '';
SELECT '=== USUARIOS Y SUS ROLES ===' as '';
SELECT '=====================================' as '';

SELECT 
    u.username as 'Usuario',
    CONCAT(u.first_name, ' ', u.last_name) as 'Nombre Completo',
    r.name as 'Rol Sistema',
    wr.name as 'Rol Trabajo',
    u.work_type as 'Tipo'
FROM users u
LEFT JOIN roles r ON u.role_id = r.id
LEFT JOIN work_roles wr ON u.work_role_id = wr.id
ORDER BY u.id;

SELECT '=====================================' as '';
SELECT '=== ¡DATOS CARGADOS EXITOSAMENTE! ===' as '';
SELECT '=====================================' as '';
