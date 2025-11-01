-- =====================================================
-- ANALYTICS OPTIMIZATION - ADD INDEXES
-- Script para optimizar las consultas de analytics
-- =====================================================

USE `mydb`;

-- =====================================================
-- Índices para optimizar queries de analytics
-- =====================================================

-- Índice compuesto para filtrado por fecha y área
CREATE INDEX IF NOT EXISTS idx_novelties_created_at_area 
ON novelties(created_at, area_id);

-- Índice compuesto para filtrado por fecha y estado
CREATE INDEX IF NOT EXISTS idx_novelties_created_at_status 
ON novelties(created_at, status);

-- Índice para completed_at (usado en cálculo de tiempo de resolución)
CREATE INDEX IF NOT EXISTS idx_novelties_completed_at 
ON novelties(completed_at);

-- Índice compuesto para novelty_assignments
CREATE INDEX IF NOT EXISTS idx_assignments_crew_novelty 
ON novelty_assignments(assigned_crew_id, novelty_id);

-- Índice para crew_members activos
CREATE INDEX IF NOT EXISTS idx_crew_members_active 
ON crew_members(crew_id, user_id, left_at);

-- Índice para novelty_reports por usuario
CREATE INDEX IF NOT EXISTS idx_novelty_reports_generated_user 
ON novelty_reports(generated_by, created_at);

-- Índice compuesto para report_participants
CREATE INDEX IF NOT EXISTS idx_report_participants_user_report 
ON report_participants(user_id, report_id);

-- Índice para usuarios activos con work_role
CREATE INDEX IF NOT EXISTS idx_users_active_work_role 
ON users(active, work_role_id);

-- Índice para filtrado por municipio
-- Ya existe: idx_novelties_municipality

-- =====================================================
-- Estadísticas de tablas (opcional, mejora el plan de ejecución)
-- =====================================================

ANALYZE TABLE novelties;
ANALYZE TABLE novelty_assignments;
ANALYZE TABLE crew_members;
ANALYZE TABLE novelty_reports;
ANALYZE TABLE report_participants;
ANALYZE TABLE users;
ANALYZE TABLE crews;

-- =====================================================
-- Verificación de índices creados
-- =====================================================

SELECT 
    TABLE_NAME,
    INDEX_NAME,
    COLUMN_NAME,
    SEQ_IN_INDEX
FROM INFORMATION_SCHEMA.STATISTICS
WHERE TABLE_SCHEMA = 'mydb'
  AND TABLE_NAME IN ('novelties', 'novelty_assignments', 'crew_members', 'novelty_reports', 'report_participants')
ORDER BY TABLE_NAME, INDEX_NAME, SEQ_IN_INDEX;

-- =====================================================
-- Fin del script de optimización
-- =====================================================
