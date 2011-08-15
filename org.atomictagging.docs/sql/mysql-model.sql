SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL';

DROP SCHEMA IF EXISTS `atomictagging` ;
CREATE SCHEMA IF NOT EXISTS `atomictagging` DEFAULT CHARACTER SET utf8 ;
USE `atomictagging` ;

-- -----------------------------------------------------
-- Table `atomictagging`.`atoms`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `atomictagging`.`atoms` ;

CREATE  TABLE IF NOT EXISTS `atomictagging`.`atoms` (
  `atomid` INT NOT NULL AUTO_INCREMENT ,
  `data` TEXT NOT NULL ,
  PRIMARY KEY (`atomid`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `atomictagging`.`tags`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `atomictagging`.`tags` ;

CREATE  TABLE IF NOT EXISTS `atomictagging`.`tags` (
  `tagid` INT NOT NULL AUTO_INCREMENT ,
  `tag` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`tagid`) ,
  UNIQUE INDEX `tag_UNIQUE` (`tag` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `atomictagging`.`molecules`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `atomictagging`.`molecules` ;

CREATE  TABLE IF NOT EXISTS `atomictagging`.`molecules` (
  `moleculeid` INT NOT NULL AUTO_INCREMENT ,
  PRIMARY KEY (`moleculeid`) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `atomictagging`.`molecule_has_tags`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `atomictagging`.`molecule_has_tags` ;

CREATE  TABLE IF NOT EXISTS `atomictagging`.`molecule_has_tags` (
  `molecules_moleculeid` INT NOT NULL ,
  `tags_tagid` INT NOT NULL ,
  PRIMARY KEY (`molecules_moleculeid`, `tags_tagid`) ,
  INDEX `fk_molecules_has_tags_molecules1` (`molecules_moleculeid` ASC) ,
  INDEX `fk_molecules_has_tags_tags1` (`tags_tagid` ASC) ,
  CONSTRAINT `fk_molecules_has_tags_molecules1`
    FOREIGN KEY (`molecules_moleculeid` )
    REFERENCES `atomictagging`.`molecules` (`moleculeid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_molecules_has_tags_tags1`
    FOREIGN KEY (`tags_tagid` )
    REFERENCES `atomictagging`.`tags` (`tagid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `atomictagging`.`molecule_has_atoms`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `atomictagging`.`molecule_has_atoms` ;

CREATE  TABLE IF NOT EXISTS `atomictagging`.`molecule_has_atoms` (
  `molecules_moleculeid` INT NOT NULL ,
  `atoms_atomid` INT NOT NULL ,
  PRIMARY KEY (`molecules_moleculeid`, `atoms_atomid`) ,
  INDEX `fk_molecules_has_atoms_molecules1` (`molecules_moleculeid` ASC) ,
  INDEX `fk_molecules_has_atoms_atoms1` (`atoms_atomid` ASC) ,
  CONSTRAINT `fk_molecules_has_atoms_molecules1`
    FOREIGN KEY (`molecules_moleculeid` )
    REFERENCES `atomictagging`.`molecules` (`moleculeid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_molecules_has_atoms_atoms1`
    FOREIGN KEY (`atoms_atomid` )
    REFERENCES `atomictagging`.`atoms` (`atomid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `atomictagging`.`types`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `atomictagging`.`types` ;

CREATE  TABLE IF NOT EXISTS `atomictagging`.`types` (
  `typeid` INT NOT NULL AUTO_INCREMENT ,
  `type` VARCHAR(255) NOT NULL ,
  PRIMARY KEY (`typeid`) ,
  UNIQUE INDEX `type_UNIQUE` (`type` ASC) )
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `atomictagging`.`atom_has_types`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `atomictagging`.`atom_has_types` ;

CREATE  TABLE IF NOT EXISTS `atomictagging`.`atom_has_types` (
  `atoms_atomid` INT NOT NULL ,
  `types_typeid` INT NOT NULL ,
  PRIMARY KEY (`atoms_atomid`, `types_typeid`) ,
  INDEX `fk_atoms_has_types_atoms1` (`atoms_atomid` ASC) ,
  INDEX `fk_atoms_has_types_types1` (`types_typeid` ASC) ,
  CONSTRAINT `fk_atoms_has_types_atoms1`
    FOREIGN KEY (`atoms_atomid` )
    REFERENCES `atomictagging`.`atoms` (`atomid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_atoms_has_types_types1`
    FOREIGN KEY (`types_typeid` )
    REFERENCES `atomictagging`.`types` (`typeid` )
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;



SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
