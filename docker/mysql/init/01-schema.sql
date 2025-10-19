-- =====================================================
-- EBSA NEXUS - SCHEMA COMPLETO CON CREW MANAGEMENT
-- Actualizado con BIGINT UNSIGNED para compatibilidad JPA
-- =====================================================

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- =====================================================
-- Crear Schema
-- =====================================================
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `mydb`;

-- =====================================================
-- Table: roles
-- =====================================================
CREATE TABLE IF NOT EXISTS `roles` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` MEDIUMTEXT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC)
) ENGINE = InnoDB;

-- =====================================================
-- Table: work_roles
-- =====================================================
CREATE TABLE IF NOT EXISTS `work_roles` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` MEDIUMTEXT NULL,
  `type` ENUM('intern', 'extern') NOT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `name_UNIQUE` (`name` ASC)
) ENGINE = InnoDB;

-- =====================================================
-- Table: users
-- =====================================================
CREATE TABLE IF NOT EXISTS `users` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `uuid` VARCHAR(45) NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `email` VARCHAR(60) NOT NULL,
  `pwd_hash` VARCHAR(256) NOT NULL,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `role_id` BIGINT UNSIGNED NOT NULL,
  `work_role_id` BIGINT UNSIGNED NULL,
  `work_type` ENUM('intern', 'extern') NULL,
  `document_number` VARCHAR(45) NULL,
  `phone` VARCHAR(45) NOT NULL,
  `active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `last_login` DATETIME NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `uuid_UNIQUE` (`uuid` ASC),
  UNIQUE INDEX `email_UNIQUE` (`email` ASC),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  INDEX `role_id_idx` (`role_id` ASC),
  INDEX `work_role_id_idx` (`work_role_id` ASC),
  CONSTRAINT `fk_users_roles`
    FOREIGN KEY (`role_id`)
    REFERENCES `roles` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_users_work_roles`
    FOREIGN KEY (`work_role_id`)
    REFERENCES `work_roles` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;

-- =====================================================
-- Table: areas
-- =====================================================
CREATE TABLE IF NOT EXISTS `areas` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `code` ENUM('FACTURACION', 'CARTERA', 'PERDIDAS') NOT NULL,
  `name` VARCHAR(45) NULL,
  `description` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `code_UNIQUE` (`code` ASC)
) ENGINE = InnoDB;

-- =====================================================
-- Table: Location
-- =====================================================
CREATE TABLE IF NOT EXISTS `Location` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `id_location` BIGINT UNSIGNED NULL,
  `name` VARCHAR(45) NULL,
  `details` VARCHAR(45) NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_location_parent_idx` (`id_location` ASC),
  CONSTRAINT `fk_location_parent`
    FOREIGN KEY (`id_location`)
    REFERENCES `Location` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;

-- =====================================================
-- Table: novelties
-- =====================================================
CREATE TABLE IF NOT EXISTS `novelties` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `novelty_uuid` VARCHAR(45) NULL,
  `area_id` BIGINT UNSIGNED NOT NULL,
  `status_id` ENUM('Por_asignar', 'En_curso', 'completado', 'cerrado') NOT NULL,
  `reported_by` BIGINT UNSIGNED NOT NULL,
  `title` VARCHAR(100) NULL,
  `description` TEXT NULL,
  `severity` ENUM('minor', 'major', 'critical') NOT NULL DEFAULT 'minor',
  `priority` ENUM('low', 'medium', 'high', 'critical') NOT NULL DEFAULT 'medium',
  `account_number` VARCHAR(45) NULL,
  `meter_number` VARCHAR(45) NULL,
  `active_reading` VARCHAR(45) NULL,
  `reactive_reading` VARCHAR(45) NULL,
  `town` VARCHAR(45) NULL,
  `is_offline` TINYINT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `acknowledged_at` DATETIME NULL,
  `closed_at` DATETIME NULL,
  `id_location` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_novelties_area_idx` (`area_id` ASC),
  INDEX `fk_novelties_users_idx` (`reported_by` ASC),
  INDEX `fk_novelties_location_idx` (`id_location` ASC),
  CONSTRAINT `fk_novelties_area`
    FOREIGN KEY (`area_id`)
    REFERENCES `areas` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_novelties_users`
    FOREIGN KEY (`reported_by`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_novelties_location`
    FOREIGN KEY (`id_location`)
    REFERENCES `Location` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;

-- =====================================================
-- Table: extern_novelties
-- =====================================================
CREATE TABLE IF NOT EXISTS `extern_novelties` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `number_meter_installed` VARCHAR(45) NULL,
  `active_reading_installed` VARCHAR(45) NULL,
  `reactive_reading_installed` VARCHAR(45) NULL,
  `digits_installed` VARCHAR(45) NULL,
  `calibration_certificate` VARCHAR(45) NULL,
  `calibration_date` DATETIME NULL,
  `laboratory_change_date` DATETIME NULL,
  `id_novelties` BIGINT UNSIGNED NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `fk_extern_novelties_novelties_idx` (`id_novelties` ASC),
  CONSTRAINT `fk_extern_novelties_novelties`
    FOREIGN KEY (`id_novelties`)
    REFERENCES `novelties` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;

-- =====================================================
-- CREW MANAGEMENT MODULE - TABLES
-- =====================================================

-- Table: crews
-- =====================================================
CREATE TABLE IF NOT EXISTS `crews` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(100) NOT NULL,
  `description` VARCHAR(255) NULL,
  `created_by` BIGINT UNSIGNED NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'DISPONIBLE',
  `deleted_at` DATETIME NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_crews_status` (`status` ASC),
  INDEX `idx_crews_created_by` (`created_by` ASC),
  INDEX `idx_crews_deleted_at` (`deleted_at` ASC),
  CONSTRAINT `fk_crews_created_by`
    FOREIGN KEY (`created_by`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;

-- =====================================================
-- Table: crew_members
-- =====================================================
CREATE TABLE IF NOT EXISTS `crew_members` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `crew_id` BIGINT UNSIGNED NOT NULL,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `is_leader` TINYINT(1) NOT NULL DEFAULT 0,
  `joined_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `left_at` DATETIME NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_crew_members_crew_id` (`crew_id` ASC),
  INDEX `idx_crew_members_user_id` (`user_id` ASC),
  INDEX `idx_crew_members_is_leader` (`is_leader` ASC),
  INDEX `idx_crew_members_left_at` (`left_at` ASC),
  CONSTRAINT `fk_crew_members_crew`
    FOREIGN KEY (`crew_id`)
    REFERENCES `crews` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_crew_members_user`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;

-- =====================================================
-- Table: novelty_assignments
-- =====================================================
CREATE TABLE IF NOT EXISTS `novelty_assignments` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `novelty_id` BIGINT UNSIGNED NOT NULL,
  `crew_id` BIGINT UNSIGNED NOT NULL,
  `assigned_by` BIGINT UNSIGNED NOT NULL,
  `status` VARCHAR(20) NOT NULL DEFAULT 'ASIGNADO',
  `notes` TEXT NULL,
  `assigned_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `started_at` DATETIME NULL,
  `completed_at` DATETIME NULL,
  `cancelled_at` DATETIME NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `idx_assignments_novelty_id` (`novelty_id` ASC),
  INDEX `idx_assignments_crew_id` (`crew_id` ASC),
  INDEX `idx_assignments_status` (`status` ASC),
  INDEX `idx_assignments_assigned_by` (`assigned_by` ASC),
  CONSTRAINT `fk_assignments_novelty`
    FOREIGN KEY (`novelty_id`)
    REFERENCES `novelties` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_assignments_crew`
    FOREIGN KEY (`crew_id`)
    REFERENCES `crews` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_assignments_assigned_by`
    FOREIGN KEY (`assigned_by`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;

-- =====================================================
-- Table: reports (placeholder for future use)
-- =====================================================
CREATE TABLE IF NOT EXISTS `reports` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `report_uuid` VARCHAR(45) NULL,
  `title` VARCHAR(100) NULL,
  `description` TEXT NULL,
  `created_by` BIGINT UNSIGNED NOT NULL,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_reports_users_idx` (`created_by` ASC),
  CONSTRAINT `fk_reports_users`
    FOREIGN KEY (`created_by`)
    REFERENCES `users` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION
) ENGINE = InnoDB;

-- =====================================================
-- Table: notifications (placeholder for future use)
-- =====================================================
CREATE TABLE IF NOT EXISTS `notifications` (
  `id` BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT UNSIGNED NOT NULL,
  `title` VARCHAR(100) NULL,
  `message` TEXT NULL,
  `read` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` DATETIME NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  INDEX `fk_notifications_users_idx` (`user_id` ASC),
  CONSTRAINT `fk_notifications_users`
    FOREIGN KEY (`user_id`)
    REFERENCES `users` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
) ENGINE = InnoDB;

-- =====================================================
-- Restaurar configuraciones originales
-- =====================================================
SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- =====================================================
-- Fin del schema
-- =====================================================
