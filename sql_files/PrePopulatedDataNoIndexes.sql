CREATE DATABASE  IF NOT EXISTS `serwis_ogloszeniowy` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `serwis_ogloszeniowy`;
-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: serwis_ogloszeniowy
-- ------------------------------------------------------
-- Server version	8.0.30

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `authorities`
--

DROP TABLE IF EXISTS `authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `authorities` (
  `role_id` int NOT NULL AUTO_INCREMENT,
  `authority` varchar(45) NOT NULL,
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authorities`
--

LOCK TABLES `authorities` WRITE;
/*!40000 ALTER TABLE `authorities` DISABLE KEYS */;
INSERT INTO `authorities` VALUES (1,'ROLE_ADMIN'),(2,'ROLE_USER');
/*!40000 ALTER TABLE `authorities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `block_list`
--

DROP TABLE IF EXISTS `block_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `block_list` (
  `block_id` int NOT NULL AUTO_INCREMENT,
  `blocking_user_id` int NOT NULL,
  `blocked_user_id` int NOT NULL,
  PRIMARY KEY (`block_id`),
  KEY `blocking_user_id_idx` (`blocking_user_id`),
  CONSTRAINT `blocking_user_id` FOREIGN KEY (`blocking_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `block_list`
--

LOCK TABLES `block_list` WRITE;
/*!40000 ALTER TABLE `block_list` DISABLE KEYS */;
INSERT INTO `block_list` VALUES (4,4,2);
/*!40000 ALTER TABLE `block_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `messages`
--

DROP TABLE IF EXISTS `messages`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `messages` (
  `message_id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `receiver_id` int NOT NULL,
  `message_date` datetime NOT NULL,
  `message_content` longtext NOT NULL,
  PRIMARY KEY (`message_id`),
  KEY `sender_id_idx` (`sender_id`),
  KEY `sender_receiver_idx` (`sender_id`,`receiver_id`),
  CONSTRAINT `sender_id` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `messages`
--

LOCK TABLES `messages` WRITE;
/*!40000 ALTER TABLE `messages` DISABLE KEYS */;
INSERT INTO `messages` VALUES (1,2,3,'2023-01-06 02:04:34','Awesome shoes, what is their size?'),(2,4,3,'2023-01-06 17:49:02','Hello, I have a question about offer X'),(3,4,3,'2023-01-06 18:01:53','Do you speak polish sir?'),(4,3,4,'2023-01-06 18:04:42','Of course, what is your question sir?'),(5,4,2,'2023-01-06 18:13:27','This offer is a joke! This plot of land does not exist!!!!');
/*!40000 ALTER TABLE `messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `offer_report`
--

DROP TABLE IF EXISTS `offer_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `offer_report` (
  `id_offer_report` int NOT NULL AUTO_INCREMENT,
  `offer_report_desc` longtext NOT NULL,
  `reported_offer_id` int NOT NULL,
  PRIMARY KEY (`id_offer_report`),
  KEY `reported_offer_id_idx` (`reported_offer_id`),
  CONSTRAINT `reported_offer_id` FOREIGN KEY (`reported_offer_id`) REFERENCES `offers` (`offer_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `offer_report`
--

LOCK TABLES `offer_report` WRITE;
/*!40000 ALTER TABLE `offer_report` DISABLE KEYS */;
INSERT INTO `offer_report` VALUES (2,'These shoes have been stolen recently!!',1);
/*!40000 ALTER TABLE `offer_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `offers`
--

DROP TABLE IF EXISTS `offers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `offers` (
  `offer_id` int NOT NULL AUTO_INCREMENT,
  `date_of_creation` datetime NOT NULL,
  `description` longtext NOT NULL,
  `is_active` tinyint(1) NOT NULL,
  `title` varchar(45) NOT NULL,
  `price` float NOT NULL,
  `user_fk` int NOT NULL,
  PRIMARY KEY (`offer_id`),
  KEY `user_fk_idx` (`user_fk`),
  CONSTRAINT `user_fk` FOREIGN KEY (`user_fk`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `offers`
--

LOCK TABLES `offers` WRITE;
/*!40000 ALTER TABLE `offers` DISABLE KEYS */;
INSERT INTO `offers` VALUES (1,'2023-01-06 01:57:18','Lahti shoes, unused, undamaged. Available in Poznań Area.',1,'Lahti shoes - winter edition',220.5,3),(2,'2023-01-06 01:59:00','Grey skirt, modest, very low price. High quality material!',1,'Skirt - gray',77,3),(4,'2023-01-06 02:01:29','Professional barbell. Very durable!.',1,'Barbell',45,2),(5,'2023-01-06 02:02:18','Newly purchased spade. I am selling it since it is not useful for me anymore.',1,'Spade!!!!',15,2),(6,'2023-01-06 02:14:38','White toyota, brand-new, without a course',1,'Toyota Yaris',50000,4);
/*!40000 ALTER TABLE `offers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `subscriptions`
--

DROP TABLE IF EXISTS `subscriptions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `subscriptions` (
  `sub_id` int NOT NULL AUTO_INCREMENT,
  `subbed_offer_id` int NOT NULL,
  `subbing_user_id` int NOT NULL,
  `date_of_sub` date NOT NULL,
  PRIMARY KEY (`sub_id`),
  KEY `subbed_offer_id_idx` (`subbed_offer_id`),
  KEY `subbing_user_id_idx` (`subbing_user_id`),
  CONSTRAINT `subbed_offer_id` FOREIGN KEY (`subbed_offer_id`) REFERENCES `offers` (`offer_id`),
  CONSTRAINT `subbing_user_id` FOREIGN KEY (`subbing_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `subscriptions`
--

LOCK TABLES `subscriptions` WRITE;
/*!40000 ALTER TABLE `subscriptions` DISABLE KEYS */;
INSERT INTO `subscriptions` VALUES (2,2,4,'2023-01-06');
/*!40000 ALTER TABLE `subscriptions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_auth`
--

DROP TABLE IF EXISTS `user_auth`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_auth` (
  `user_id_auth` int NOT NULL,
  `role_id_auth` int NOT NULL,
  KEY `user_id_auth_idx` (`user_id_auth`),
  KEY `role_id_auth_idx` (`role_id_auth`),
  CONSTRAINT `role_id_auth` FOREIGN KEY (`role_id_auth`) REFERENCES `authorities` (`role_id`),
  CONSTRAINT `user_id_auth` FOREIGN KEY (`user_id_auth`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_auth`
--

LOCK TABLES `user_auth` WRITE;
/*!40000 ALTER TABLE `user_auth` DISABLE KEYS */;
INSERT INTO `user_auth` VALUES (1,1),(2,2),(3,2),(4,2),(8,2);
/*!40000 ALTER TABLE `user_auth` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_details`
--

DROP TABLE IF EXISTS `user_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_details` (
  `user_id` int NOT NULL,
  `name` varchar(45) NOT NULL,
  `surname` varchar(45) NOT NULL,
  `email` varchar(45) NOT NULL,
  `city` varchar(45) NOT NULL,
  `phone_number` varchar(15) NOT NULL,
  `last_login` datetime NOT NULL,
  KEY `user_id_idx` (`user_id`),
  CONSTRAINT `user_id` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_details`
--

LOCK TABLES `user_details` WRITE;
/*!40000 ALTER TABLE `user_details` DISABLE KEYS */;
INSERT INTO `user_details` VALUES (1,'Adrian','Kowalski','admin@email.com','Krakow','455143434','2023-01-06 20:24:02'),(2,'Patryk','Młodawski','PatrykM@gmail.com','Poznań','690741829','2023-01-06 20:27:17'),(3,'Mark','Rothko','MarkR@gmail.com','New York','5056756930','2023-01-06 19:38:28'),(4,'Bartosz','Kowalski','BartoszKK@gmail.com','Warszawa','212097341','2023-01-06 19:55:14'),(8,'Marysia','Andrzejewska','MarysiaA@gmail.com','Łódź','761892451','2023-01-06 20:07:04');
/*!40000 ALTER TABLE `user_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_report`
--

DROP TABLE IF EXISTS `user_report`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_report` (
  `report_id` int NOT NULL AUTO_INCREMENT,
  `user_report_desc` longtext NOT NULL,
  `reported_user_id` int NOT NULL,
  `reporting_user_id` int NOT NULL,
  PRIMARY KEY (`report_id`),
  KEY `reported_user_id_idx` (`reported_user_id`),
  CONSTRAINT `reported_user_id` FOREIGN KEY (`reported_user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_report`
--

LOCK TABLES `user_report` WRITE;
/*!40000 ALTER TABLE `user_report` DISABLE KEYS */;
INSERT INTO `user_report` VALUES (1,'This user has posted an offer content of which does not exist!!!!',2,4);
/*!40000 ALTER TABLE `user_report` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `password` varchar(70) NOT NULL,
  `enabled` tinyint(1) NOT NULL,
  `reset_code` varchar(70) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username_idx` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','$2a$10$IVre2YLrZ0YVViZoLJQJkOdKouGkiQ7nkoM/R8rYw7J07Mh8gs3om',1,'$2a$12$GVj669uTkpO5aCExWjPdh.PttifE/NM.DjhXhp3FHC6dnmwJz4mtW'),(2,'PatrykM','$2a$10$it24pfJUKFyeQofcJshTfeA1E/WvdcVriVCwc13XZ6sAqgLWv7/HS',1,'$2a$10$g7OadcfzbGD2defo7e1Jf.SrilOo5OUQ.UONqUSamQI7ZsS.eGhXy'),(3,'MarkR','$2a$10$UcxwmKGPRn02KgVsBjPTmuwO3q8bbjSOY0uPFKH3Y0xRqS736kaoW',1,'$2a$10$qmRbPx8VAHXkUc6Bwg1Yo.L6H5usZII4B8pAwhtBLrlFvx9KniDG6'),(4,'BartoszK','$2a$10$fdNU4Y4NePiAcOboIyQCFu.QSX3LGksUNCAzPbRceIWzMU6WYOL76',1,'$2a$10$PC8oR367DmG34p4ghVecLeFT0qx3vrzzrqDwFv7SWc3r.QM5suciO'),(8,'MarysiaA','$2a$10$7w42Kv47UWZAkWlJkEtk/u9BbhaGP3ncPNLMayGvbRRXyYpH/IF5G',1,'$2a$10$.lrfpoXLbQaLJwpQKsNmHeFfYjRtpPjxC58Viz86pqghf466BIvRC');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-01-06 21:28:48
