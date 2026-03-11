CREATE DATABASE  IF NOT EXISTS `timepoint` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `timepoint`;
-- MySQL dump 10.13  Distrib 8.0.38, for Win64 (x86_64)
--
-- Host: localhost    Database: timepoint
-- ------------------------------------------------------
-- Server version	8.0.39

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
-- Table structure for table `registro`
--

DROP TABLE IF EXISTS `registro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `registro` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `usuarios_id` int NOT NULL,
                            `data_registro` date NOT NULL,
                            `horario_chegada` time DEFAULT NULL,
                            `horario_saida_almoco` time DEFAULT NULL,
                            `horario_volta_almoco` time DEFAULT NULL,
                            `horario_saida` time DEFAULT NULL,
                            `criado_em` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                            `atualizado_em` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                            PRIMARY KEY (`id`),
                            UNIQUE KEY `unique_ponto` (`id`,`data_registro`),
                            KEY `usuarios_id` (`usuarios_id`),
                            CONSTRAINT `registro_ibfk_1` FOREIGN KEY (`usuarios_id`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `registro`
--

LOCK TABLES `registro` WRITE;
/*!40000 ALTER TABLE `registro` DISABLE KEYS */;
/*!40000 ALTER TABLE `registro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
                            `id` int NOT NULL AUTO_INCREMENT,
                            `nome` varchar(100) DEFAULT NULL,
                            `email` varchar(100) DEFAULT NULL,
                            `cargo` varchar(100) DEFAULT NULL,
                            `turno` varchar(50) DEFAULT NULL,
                            `senha` varchar(255) DEFAULT NULL,
                            PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'João Silva','joao.silva@timepoint.com','Funcionário','Manhã','senha123'),
                              (2,'Maria Santos','maria.santos@timepoint.com','Funcionário','Noite','senha456')
        ,(3,'Carlos Oliveira','carlos.oliveira@timepoint.com','Gerente','Manhã','senha789')
        ,(4,'Ana Costa','ana.costa@timepoint.com','Funcionário','Manhã','senhaabc'),
                              (5,'Pedro Pereira','pedro.pereira@timepoint.com','Gerente','Noite','senhadef'),
                              (6,'Julia Martins','julia.martins@timepoint.com','Funcionário','Noite','senha012'),
                              (7,'Roberto Alves','roberto.alves@timepoint.com','Funcionário','Manhã','senha345');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;


INSERT INTO `registro`
(usuarios_id, data_registro, horario_chegada, horario_saida_almoco, horario_volta_almoco, horario_saida)
VALUES
    (1, '2026-03-11', '08:02:00', '12:00:00', '13:05:00', '17:03:00'),  -- João Silva
    (2, '2026-03-11', '18:01:00', '22:00:00', '23:00:00', '00:05:00'),  -- Maria Santos (noite)
    (3, '2026-03-11', '08:15:00', '12:10:00', '13:00:00', '17:20:00'),  -- Carlos Oliveira (gerente)
    (4, '2026-03-11', '08:00:00', '12:00:00', '13:00:00', '17:00:00'),  -- Ana Costa
    (5, '2026-03-11', '18:05:00', '22:15:00', '23:10:00', NULL),        -- Pedro Pereira (ainda no trabalho)
    (6, '2026-03-11', '18:30:00', NULL,        NULL,        NULL),       -- Julia Martins (só chegada)
    (7, '2026-03-11', '08:10:00', '12:05:00', '13:10:00', '17:15:00'); -- Roberto Alves


UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-10 21:12:18

select * from usuarios;
select * from registro;

