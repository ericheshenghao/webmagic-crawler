/*
SQLyog Ultimate v12.09 (64 bit)
MySQL - 5.7.29 : Database - sound
*********************************************************************
*/


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`sound` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `sound`;


/*Table structure for table `sound_file` */

DROP TABLE IF EXISTS `sound_file`;

CREATE TABLE `sound_file` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) NOT NULL,
  `ext` varchar(20) NOT NULL,
  `size` varchar(200) NOT NULL,
  `path` varchar(200) NOT NULL,
  `classification` varchar(100) NOT NULL,
  `description` longtext NOT NULL,
  `url` varchar(100) NOT NULL,
  `statu` tinyint(4) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=684 DEFAULT CHARSET=utf8;

/*Table structure for table `tag` */

DROP TABLE IF EXISTS `tag`;

CREATE TABLE `tag` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2023 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

/*Table structure for table `file_has_tag` */

DROP TABLE IF EXISTS `file_has_tag`;

CREATE TABLE `file_has_tag` (
  `fileId` int(8) NOT NULL,
  `tagId` int(8) NOT NULL,
  KEY `fileId` (`fileId`),
  KEY `tagId` (`tagId`),
  CONSTRAINT `file_has_tag_ibfk_1` FOREIGN KEY (`fileId`) REFERENCES `sound_file` (`id`) ,
  CONSTRAINT `file_has_tag_ibfk_2` FOREIGN KEY (`tagId`) REFERENCES `tag` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

