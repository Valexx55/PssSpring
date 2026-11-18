CREATE DATABASE IF NOT EXISTS bd_spring_batch
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bd_spring_batch;

CREATE TABLE IF NOT EXISTS clientes_origen (
  id INT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL,
  localidad VARCHAR(100) NOT NULL
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS clientes_procesados (
  id INT PRIMARY KEY,
  nombre VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL,
  localidad VARCHAR(100) NOT NULL,
  activo BOOLEAN NOT NULL DEFAULT TRUE
) ENGINE=InnoDB;

INSERT INTO clientes_origen (id, nombre, email, localidad) VALUES
(1, 'Ana López', 'cliente01@ejemplo.es', 'Madrid'),
(2, 'Luis García', 'cliente02@ejemplo.es', 'Parla'),
(3, 'Marta Pérez', 'cliente03@ejemplo.es', 'Getafe'),
(4, 'Jorge Martín', 'cliente04@ejemplo.es', 'Leganés'),
(5, 'Elena Ruiz', 'cliente05@ejemplo.es', 'Fuenlabrada'),
(6, 'Pablo Sanz', 'cliente06@ejemplo.es', 'Móstoles'),
(7, 'Sara Gómez', 'cliente07@ejemplo.es', 'Alcalá de Henares'),
(8, 'David Romero', 'cliente08@ejemplo.es', 'Madrid'),
(9, 'Lucía Torres', 'cliente09@ejemplo.es', 'Parla'),
(10, 'Carlos Díaz', 'cliente10@ejemplo.es', 'Getafe'),
(11, 'Nuria Vega', 'cliente11@ejemplo.es', 'Madrid'),
(12, 'Raúl Molina', 'cliente12@ejemplo.es', 'Parla'),
(13, 'Irene Castro', 'cliente13@ejemplo.es', 'Getafe'),
(14, 'Mario León', 'cliente14@ejemplo.es', 'Leganés'),
(15, 'Clara Moreno', 'cliente15@ejemplo.es', 'Fuenlabrada'),
(16, 'Óscar Gil', 'cliente16@ejemplo.es', 'Móstoles'),
(17, 'Paula Navarro', 'cliente17@ejemplo.es', 'Alcalá de Henares'),
(18, 'Adrián Ramos', 'cliente18@ejemplo.es', 'Madrid'),
(19, 'Alba Sánchez', 'cliente19@ejemplo.es', 'Parla'),
(20, 'Diego Vidal', 'cliente20@ejemplo.es', 'Getafe'),
(21, 'Eva Serrano', 'cliente21@ejemplo.es', 'Madrid'),
(22, 'Hugo Ortega', 'cliente22@ejemplo.es', 'Parla'),
(23, 'Laura Medina', 'cliente23@ejemplo.es', 'Getafe'),
(24, 'Iván Flores', 'cliente24@ejemplo.es', 'Leganés'),
(25, 'Noa Herrera', 'cliente25@ejemplo.es', 'Fuenlabrada'),
(26, 'Sergio Rubio', 'cliente26@ejemplo.es', 'Móstoles'),
(27, 'Celia Santos', 'cliente27@ejemplo.es', 'Alcalá de Henares'),
(28, 'Bruno Cano', 'cliente28@ejemplo.es', 'Madrid'),
(29, 'Alicia Pardo', 'cliente29@ejemplo.es', 'Parla'),
(30, 'Marcos Blanco', 'cliente30@ejemplo.es', 'Getafe')
ON DUPLICATE KEY UPDATE
 nombre=VALUES(nombre), email=VALUES(email),
 localidad=VALUES(localidad);
