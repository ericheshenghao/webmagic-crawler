/*
SQLyog Ultimate v12.08 (64 bit)
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

/*Table structure for table `sys_soundfile` */

DROP TABLE IF EXISTS `sys_soundfile`;

CREATE TABLE `sys_soundfile` (
  `id` int(8) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `name` varchar(200) NOT NULL COMMENT '文件名',
  `ext` varchar(20) NOT NULL COMMENT '扩展名',
  `size` varchar(200) NOT NULL COMMENT '大小',
  `path` varchar(200) NOT NULL COMMENT '路径',
  `classification` varchar(100) NOT NULL COMMENT '类型',
  `description` longtext NOT NULL COMMENT '描述',
  `url` varchar(100) NOT NULL COMMENT '原始链接',
  `statu` tinyint(4) NOT NULL COMMENT '状态',
  `location` varchar(100) DEFAULT NULL COMMENT '地图位置',
  `cover` varchar(100) DEFAULT NULL COMMENT '封面图',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4359 DEFAULT CHARSET=utf8;

/*Table structure for table `sys_soundfile_tag` */

DROP TABLE IF EXISTS `sys_soundfile_tag`;

CREATE TABLE `sys_soundfile_tag` (
  `fileId` int(8) NOT NULL,
  `tagId` int(8) NOT NULL,
  KEY `fileId` (`fileId`),
  KEY `tagId` (`tagId`),
  CONSTRAINT `sys_soundfile_tag_ibfk_1` FOREIGN KEY (`fileId`) REFERENCES `sys_soundfile` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `sys_soundfile_tag_ibfk_2` FOREIGN KEY (`tagId`) REFERENCES `sys_tag` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Table structure for table `sys_tag` */

DROP TABLE IF EXISTS `sys_tag`;

CREATE TABLE `sys_tag` (
  `id` int(8) NOT NULL AUTO_INCREMENT,
  `name` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8021 DEFAULT CHARSET=utf8;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
