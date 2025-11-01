-- =====================================================
-- MIGRATION SCRIPT: Add Report Participants and Enhanced Report Fields
-- Fecha: 2025-10-28
-- Descripción: Migración para mejorar la tabla novelty_reports
--              y agregar la tabla report_participants
-- =====================================================

USE `mydb`;

-- =====================================================
-- Step 1: Add new columns to novelty_reports
-- =====================================================

-- Agregar work_start_date si no existe
ALTER TABLE `novelty_reports`
ADD COLUMN IF NOT EXISTS `work_start_date` DATETIME NULL COMMENT 'Fecha real de inicio del trabajo'
AFTER `observations`;

-- Agregar work_end_date si no existe
ALTER TABLE `novelty_reports`
ADD COLUMN IF NOT EXISTS `work_end_date` DATETIME NULL COMMENT 'Fecha real de finalización del trabajo'
AFTER `work_start_date`;

-- Agregar resolution_status si no existe
ALTER TABLE `novelty_reports`
ADD COLUMN IF NOT EXISTS `resolution_status` ENUM('COMPLETADA', 'NO_COMPLETADA', 'CERRADA') NOT NULL DEFAULT 'COMPLETADA' 
COMMENT 'Estado resultante de la novedad tras el reporte'
AFTER `work_end_date`;

-- =====================================================
-- Step 2: Add index for resolution_status
-- =====================================================

-- Crear índice si no existe
SET @dbname = DATABASE();
SET @tablename = 'novelty_reports';
SET @indexname = 'idx_novelty_reports_resolution_status';
SET @preparedStatement = (SELECT IF(
  (
    SELECT COUNT(*) FROM INFORMATION_SCHEMA.STATISTICS
    WHERE
      (table_name = @tablename)
      AND (table_schema = @dbname)
      AND (index_name = @indexname)
  ) > 0,
  'SELECT 1',
  CONCAT('CREATE INDEX ', @indexname, ' ON ', @tablename, ' (resolution_status)')
));
PREPARE alterIfNotExists FROM @preparedStatement;
EXECUTE alterIfNotExists;
DEALLOCATE PREPARE alterIfNotExists;

-- =====================================================
-- Step 3: Create report_participants table if not exists
-- =====================================================

CREATE TABLE IF NOT EXISTS `report_participants` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `report_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `added_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_report_participants_report_id` (`report_id` ASC),
  INDEX `idx_report_participants_user_id` (`user_id` ASC),
  UNIQUE INDEX `unique_report_user` (`report_id`, `user_id`),
  CONSTRAINT `fk_report_participants_report`
    FOREIGN KEY (`report_id`)
    REFERENCES `novelty_reports` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_report_participants_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;

-- =====================================================
-- Step 4: Migration notes
-- =====================================================

-- NOTAS IMPORTANTES:
-- 1. Los reportes existentes tendrán resolution_status = 'COMPLETADA' por defecto
-- 2. Los campos work_start_date y work_end_date serán NULL para reportes existentes
-- 3. No se crean report_participants para reportes existentes (debe hacerse manualmente si es necesario)
-- 4. Se han removido los campos materials_used, solution_type, resolution_time_hours y role_description

SELECT 'Migration completed successfully!' as status;
