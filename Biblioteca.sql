-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema Biblioteca
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema Biblioteca
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `Biblioteca` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci ;
USE `Biblioteca` ;

-- -----------------------------------------------------
-- Table `Biblioteca`.`Categoria`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Biblioteca`.`Categoria` (
  `id_categoria` BIGINT NOT NULL AUTO_INCREMENT,
  `categoria` VARCHAR(255) NULL DEFAULT NULL,
  PRIMARY KEY (`id_categoria`),
  UNIQUE INDEX `UK_127vx1lnbo4eoufh8lawbeq1r` (`categoria` ASC) VISIBLE)
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Biblioteca`.`Cuenta`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Biblioteca`.`Cuenta` (
  `id` BIGINT NOT NULL,
  `apellido` VARCHAR(255) NOT NULL,
  `contraseña` VARCHAR(255) NULL DEFAULT NULL,
  `correo` VARCHAR(255) NOT NULL,
  `estado_cuenta` VARCHAR(255) NOT NULL,
  `nombre` VARCHAR(255) NOT NULL,
  `telefono` VARCHAR(255) NOT NULL,
  `tipo_cuenta` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Biblioteca`.`Libro`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Biblioteca`.`Libro` (
  `isbn` BIGINT NOT NULL,
  `autor` VARCHAR(255) NULL DEFAULT NULL,
  `estado_libro` VARCHAR(255) NULL DEFAULT NULL,
  `libros_disponibles` INT NOT NULL,
  `libros_total` INT NOT NULL,
  `titulo` VARCHAR(255) NULL DEFAULT NULL,
  `ubicacion` VARCHAR(255) NULL DEFAULT NULL,
  `categoria` BIGINT NOT NULL,
  PRIMARY KEY (`isbn`),
  INDEX `FKbgtxsrqnsvpwi34fqyass0ijp` (`categoria` ASC) VISIBLE,
  CONSTRAINT `FKbgtxsrqnsvpwi34fqyass0ijp`
    FOREIGN KEY (`categoria`)
    REFERENCES `Biblioteca`.`Categoria` (`id_categoria`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Biblioteca`.`Prestamo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Biblioteca`.`Prestamo` (
  `id_prestamo` BIGINT NOT NULL AUTO_INCREMENT,
  `estado_prestamo` VARCHAR(255) NULL DEFAULT NULL,
  `fecha_inicio_prestamo` DATE NOT NULL,
  `fecha_relativa_devolucion` DATE NOT NULL,
  `observaciones` VARCHAR(255) NULL DEFAULT NULL,
  `id_cuenta` BIGINT NOT NULL,
  PRIMARY KEY (`id_prestamo`),
  INDEX `FK3cki25m3e8lydu254334ityyc` (`id_cuenta` ASC) VISIBLE,
  CONSTRAINT `FK3cki25m3e8lydu254334ityyc`
    FOREIGN KEY (`id_cuenta`)
    REFERENCES `Biblioteca`.`Cuenta` (`id`))
ENGINE = InnoDB
AUTO_INCREMENT = 12
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Biblioteca`.`Devolucion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Biblioteca`.`Devolucion` (
  `id_devolucion` BIGINT NOT NULL AUTO_INCREMENT,
  `fecha_devolucion_real` DATE NULL DEFAULT NULL,
  `observaciones` VARCHAR(255) NULL DEFAULT NULL,
  `prestamo` BIGINT NOT NULL,
  PRIMARY KEY (`id_devolucion`),
  INDEX `FKepqea815pvst59d8l5r4dur1w` (`prestamo` ASC) VISIBLE,
  CONSTRAINT `FKepqea815pvst59d8l5r4dur1w`
    FOREIGN KEY (`prestamo`)
    REFERENCES `Biblioteca`.`Prestamo` (`id_prestamo`))
ENGINE = InnoDB
AUTO_INCREMENT = 7
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Biblioteca`.`Detalledevolucion`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Biblioteca`.`Detalledevolucion` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `ejemplares_devueltos` INT NOT NULL,
  `id_devolucion` BIGINT NOT NULL,
  `id_libro` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FKorse1gw62wodr4rsbqg09h6rh` (`id_devolucion` ASC) VISIBLE,
  INDEX `FKhtc8x2fjgfugf737mfpw6jguf` (`id_libro` ASC) VISIBLE,
  CONSTRAINT `FKhtc8x2fjgfugf737mfpw6jguf`
    FOREIGN KEY (`id_libro`)
    REFERENCES `Biblioteca`.`Libro` (`isbn`),
  CONSTRAINT `FKorse1gw62wodr4rsbqg09h6rh`
    FOREIGN KEY (`id_devolucion`)
    REFERENCES `Biblioteca`.`Devolucion` (`id_devolucion`))
ENGINE = InnoDB
AUTO_INCREMENT = 8
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


-- -----------------------------------------------------
-- Table `Biblioteca`.`Detalleprestamo`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `Biblioteca`.`Detalleprestamo` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `ejemplares_prestados` INT NOT NULL,
  `id_libro` BIGINT NOT NULL,
  `id_prestamo` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  INDEX `FK5j34x8uadul04yg6234nnfcfe` (`id_libro` ASC) VISIBLE,
  INDEX `FKhtnuylqdnr03ctpko5w44gq6n` (`id_prestamo` ASC) VISIBLE,
  CONSTRAINT `FK5j34x8uadul04yg6234nnfcfe`
    FOREIGN KEY (`id_libro`)
    REFERENCES `Biblioteca`.`Libro` (`isbn`),
  CONSTRAINT `FKhtnuylqdnr03ctpko5w44gq6n`
    FOREIGN KEY (`id_prestamo`)
    REFERENCES `Biblioteca`.`Prestamo` (`id_prestamo`))
ENGINE = InnoDB
AUTO_INCREMENT = 15
DEFAULT CHARACTER SET = utf8mb4
COLLATE = utf8mb4_0900_ai_ci;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
