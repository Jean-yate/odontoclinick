-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 10-12-2025 a las 21:04:07
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `odontoclinic`
--

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `categoria_producto`
--

CREATE TABLE `categoria_producto` (
  `id_categoria` int(11) NOT NULL,
  `nombre_categoria` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `categoria_producto`
--

INSERT INTO `categoria_producto` (`id_categoria`, `nombre_categoria`, `descripcion`) VALUES
(1, 'Materiales Dentales', 'Resinas, amalgamas, cementos, etc.'),
(2, 'Instrumental', 'Herramientas e instrumentos dentales'),
(3, 'Medicamentos', 'Analgésicos, antibióticos, anestésicos'),
(4, 'Insumos Desechables', 'Guantes, mascarillas, gasas, jeringas'),
(5, 'Equipos', 'Equipos médicos y dentales');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cita`
--

CREATE TABLE `cita` (
  `id_cita` int(11) NOT NULL,
  `id_paciente` int(11) NOT NULL,
  `id_doctor` int(11) NOT NULL,
  `fecha_hora` datetime NOT NULL,
  `id_estado_cita` int(11) NOT NULL DEFAULT 1,
  `notas_paciente` text DEFAULT NULL,
  `notas_doctor` text DEFAULT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp(),
  `fecha_actualizacion` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `cita`
--

INSERT INTO `cita` (`id_cita`, `id_paciente`, `id_doctor`, `fecha_hora`, `id_estado_cita`, `notas_paciente`, `notas_doctor`, `fecha_creacion`, `fecha_actualizacion`) VALUES
(1, 1, 1, '2024-12-16 09:00:00', 1, 'Primera consulta de ortodoncia', NULL, '2025-12-08 18:55:34', '2025-12-08 18:55:34'),
(2, 2, 2, '2024-12-16 15:00:00', 2, NULL, NULL, '2025-12-08 18:55:34', '2025-12-08 18:55:34'),
(3, 3, 3, '2024-12-17 10:00:00', 1, 'Control de encías', NULL, '2025-12-08 18:55:34', '2025-12-08 18:55:34'),
(4, 1, 2, '2024-12-18 14:30:00', 1, 'Dolor en molar superior', NULL, '2025-12-08 18:55:34', '2025-12-08 18:55:34'),
(5, 2, 1, '2024-12-19 08:30:00', 2, NULL, NULL, '2025-12-08 18:55:34', '2025-12-08 18:55:34'),
(6, 1, 1, '2024-12-01 09:00:00', 4, NULL, 'Paciente requiere ortodoncia. Evaluación inicial completada.', '2025-12-08 18:55:35', '2025-12-08 18:55:35'),
(7, 2, 3, '2024-12-05 11:00:00', 4, NULL, 'Limpieza dental realizada. Encías en buen estado.', '2025-12-08 18:55:35', '2025-12-08 18:55:35'),
(8, 3, 2, '2024-12-08 16:00:00', 4, NULL, 'Endodoncia completada exitosamente.', '2025-12-08 18:55:35', '2025-12-08 18:55:35');

--
-- Disparadores `cita`
--
DELIMITER $$
CREATE TRIGGER `trg_cita_update_timestamp` BEFORE UPDATE ON `cita` FOR EACH ROW BEGIN
  SET NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `trg_validar_cita_duplicada` BEFORE INSERT ON `cita` FOR EACH ROW BEGIN
  DECLARE cita_existe INT;
  
  SELECT COUNT(*) INTO cita_existe
  FROM cita
  WHERE id_doctor = NEW.id_doctor
    AND DATE(fecha_hora) = DATE(NEW.fecha_hora)
    AND TIME(fecha_hora) = TIME(NEW.fecha_hora)
    AND id_estado_cita NOT IN (5, 6); 
  
  IF cita_existe > 0 THEN
    SIGNAL SQLSTATE '45000'
    SET MESSAGE_TEXT = 'Ya existe una cita para este doctor en ese horario';
  END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `cita_tratamiento`
--

CREATE TABLE `cita_tratamiento` (
  `id_cita_tratamiento` int(11) NOT NULL,
  `id_cita` int(11) NOT NULL,
  `id_tratamiento` int(11) NOT NULL,
  `costo_aplicado` decimal(10,2) NOT NULL,
  `observaciones` text DEFAULT NULL,
  `completado` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `cita_tratamiento`
--

INSERT INTO `cita_tratamiento` (`id_cita_tratamiento`, `id_cita`, `id_tratamiento`, `costo_aplicado`, `observaciones`, `completado`) VALUES
(1, 6, 1, 80000.00, 'Primera limpieza del paciente', 1),
(2, 7, 1, 80000.00, 'Limpieza anual', 1),
(3, 8, 5, 400000.00, 'Endodoncia en molar #16', 1),
(4, 1, 6, 250000.00, 'Primera consulta de ortodoncia', 0),
(5, 2, 5, 400000.00, NULL, 0),
(6, 3, 10, 200000.00, 'Cuadrante superior derecho', 0);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `especialidad`
--

CREATE TABLE `especialidad` (
  `id_especialidad` int(11) NOT NULL,
  `nombre_especialidad` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `especialidad`
--

INSERT INTO `especialidad` (`id_especialidad`, `nombre_especialidad`, `descripcion`) VALUES
(1, 'Ortodoncia', 'Corrección de dientes y mordida'),
(2, 'Endodoncia', 'Tratamiento de conductos radiculares'),
(3, 'Periodoncia', 'Tratamiento de encías y tejidos de soporte'),
(4, 'Odontopediatría', 'Odontología para niños'),
(5, 'Cirugía Oral', 'Extracciones y procedimientos quirúrgicos'),
(6, 'Estética Dental', 'Blanqueamiento, carillas y estética');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estado`
--

CREATE TABLE `estado` (
  `id_estado` int(11) NOT NULL,
  `nombre_estado` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `estado`
--

INSERT INTO `estado` (`id_estado`, `nombre_estado`) VALUES
(1, 'Activo'),
(2, 'Inactivo'),
(3, 'Suspendido');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `estado_cita`
--

CREATE TABLE `estado_cita` (
  `id_estado_cita` int(11) NOT NULL,
  `nombre_estado` varchar(50) NOT NULL,
  `color` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `estado_cita`
--

INSERT INTO `estado_cita` (`id_estado_cita`, `nombre_estado`, `color`) VALUES
(1, 'Programada', '#FFA500'),
(2, 'Confirmada', '#0000FF'),
(3, 'En curso', '#FFFF00'),
(4, 'Completada', '#008000'),
(5, 'Cancelada', '#FF0000'),
(6, 'No asistió', '#808080');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `historial_medico`
--

CREATE TABLE `historial_medico` (
  `id_historial` int(11) NOT NULL,
  `id_paciente` int(11) NOT NULL,
  `id_cita` int(11) DEFAULT NULL,
  `id_doctor` int(11) NOT NULL,
  `fecha` date NOT NULL,
  `diagnostico` text NOT NULL,
  `tratamiento_realizado` text NOT NULL,
  `observaciones` text DEFAULT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `historial_medico`
--

INSERT INTO `historial_medico` (`id_historial`, `id_paciente`, `id_cita`, `id_doctor`, `fecha`, `diagnostico`, `tratamiento_realizado`, `observaciones`, `fecha_creacion`) VALUES
(1, 1, 6, 1, '2024-12-01', 'Maloclusión clase II. Apiñamiento dental moderado', 'Evaluación ortodóntica. Se recomienda tratamiento con brackets', 'Paciente motivado. Buena higiene oral', '2025-12-08 18:55:35'),
(2, 2, 7, 3, '2024-12-05', 'Gingivitis leve en sector anterior', 'Profilaxis dental. Educación en técnica de cepillado', 'Se programa control en 6 meses', '2025-12-08 18:55:35'),
(3, 3, 8, 2, '2024-12-08', 'Necrosis pulpar en molar #16. Caries profunda', 'Endodoncia completada. Obturación temporal colocada', 'Paciente reporta disminución del dolor. Se programa para corona', '2025-12-08 18:55:35');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `horario`
--

CREATE TABLE `horario` (
  `id_horario` int(11) NOT NULL,
  `id_doctor` int(11) NOT NULL,
  `dia_semana` enum('Lunes','Martes','Miércoles','Jueves','Viernes','Sábado','Domingo') NOT NULL,
  `hora_inicio` time NOT NULL,
  `hora_fin` time NOT NULL,
  `duracion_cita_minutos` int(11) NOT NULL DEFAULT 30,
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `horario`
--

INSERT INTO `horario` (`id_horario`, `id_doctor`, `dia_semana`, `hora_inicio`, `hora_fin`, `duracion_cita_minutos`, `activo`) VALUES
(1, 1, 'Lunes', '08:00:00', '12:00:00', 30, 1),
(2, 1, 'Martes', '08:00:00', '12:00:00', 30, 1),
(3, 1, 'Miércoles', '14:00:00', '18:00:00', 30, 1),
(4, 1, 'Jueves', '08:00:00', '12:00:00', 30, 1),
(5, 1, 'Viernes', '08:00:00', '12:00:00', 30, 1),
(6, 2, 'Lunes', '14:00:00', '18:00:00', 45, 1),
(7, 2, 'Martes', '14:00:00', '18:00:00', 45, 1),
(8, 2, 'Miércoles', '08:00:00', '12:00:00', 45, 1),
(9, 2, 'Jueves', '14:00:00', '18:00:00', 45, 1),
(10, 2, 'Viernes', '14:00:00', '18:00:00', 45, 1),
(11, 3, 'Lunes', '08:00:00', '17:00:00', 30, 1),
(12, 3, 'Miércoles', '08:00:00', '17:00:00', 30, 1),
(13, 3, 'Viernes', '08:00:00', '17:00:00', 30, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `medico`
--

CREATE TABLE `medico` (
  `id_doctor` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `id_especialidad` int(11) NOT NULL,
  `licencia_medica` varchar(50) DEFAULT NULL,
  `anos_experiencia` int(11) DEFAULT 0,
  `fecha_ingreso` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `medico`
--

INSERT INTO `medico` (`id_doctor`, `id_usuario`, `id_especialidad`, `licencia_medica`, `anos_experiencia`, `fecha_ingreso`) VALUES
(1, 2, 1, 'MED-12345', 8, '2020-01-15'),
(2, 3, 2, 'MED-23456', 12, '2018-06-20'),
(3, 4, 3, 'MED-34567', 5, '2021-03-10');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `metodo_pago`
--

CREATE TABLE `metodo_pago` (
  `id_metodo_pago` int(11) NOT NULL,
  `nombre_metodo` varchar(50) NOT NULL,
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `metodo_pago`
--

INSERT INTO `metodo_pago` (`id_metodo_pago`, `nombre_metodo`, `activo`) VALUES
(1, 'Efectivo', 1),
(2, 'Tarjeta de crédito', 1),
(3, 'Tarjeta de débito', 1),
(4, 'Transferencia bancaria', 1),
(5, 'PSE', 1),
(6, 'Nequi', 1),
(7, 'Daviplata', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `movimiento_inventario`
--

CREATE TABLE `movimiento_inventario` (
  `id_movimiento` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `tipo_movimiento` enum('ENTRADA','SALIDA','AJUSTE') NOT NULL,
  `cantidad` int(11) NOT NULL,
  `stock_anterior` int(11) NOT NULL,
  `stock_nuevo` int(11) NOT NULL,
  `motivo` varchar(255) DEFAULT NULL,
  `id_usuario` int(11) NOT NULL,
  `fecha_movimiento` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `paciente`
--

CREATE TABLE `paciente` (
  `id_paciente` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `fecha_nacimiento` date DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `eps` varchar(100) DEFAULT NULL,
  `rh` varchar(5) DEFAULT NULL,
  `alergias` text DEFAULT NULL,
  `enfermedades_preexistentes` text DEFAULT NULL,
  `contacto_emergencia_nombre` varchar(100) DEFAULT NULL,
  `contacto_emergencia_telefono` varchar(15) DEFAULT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `paciente`
--

INSERT INTO `paciente` (`id_paciente`, `id_usuario`, `fecha_nacimiento`, `direccion`, `eps`, `rh`, `alergias`, `enfermedades_preexistentes`, `contacto_emergencia_nombre`, `contacto_emergencia_telefono`, `fecha_registro`) VALUES
(1, 8, '1990-05-15', 'Calle 100 #45-67, Bogotá', 'Sanitas', 'O+', 'Penicilina', 'Hipertensión', 'María Ramírez', '3101234567', '2025-12-08 18:55:33'),
(2, 9, '1985-09-22', 'Carrera 7 #85-34, Bogotá', 'Compensar', 'A+', NULL, NULL, 'Pedro Herrera', '3102345678', '2025-12-08 18:55:33'),
(3, 10, '1995-12-08', 'Avenida 68 #25-90, Bogotá', 'Sura', 'B-', 'Ibuprofeno', 'Diabetes tipo 2', 'Ana Mendoza', '3103456789', '2025-12-08 18:55:33');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `pago`
--

CREATE TABLE `pago` (
  `id_pago` int(11) NOT NULL,
  `id_cita` int(11) NOT NULL,
  `fecha_pago` datetime NOT NULL DEFAULT current_timestamp(),
  `monto` decimal(10,2) NOT NULL,
  `id_metodo_pago` int(11) NOT NULL,
  `referencia` varchar(100) DEFAULT NULL,
  `notas` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `pago`
--

INSERT INTO `pago` (`id_pago`, `id_cita`, `fecha_pago`, `monto`, `id_metodo_pago`, `referencia`, `notas`) VALUES
(1, 6, '2024-12-01 10:30:00', 80000.00, 1, NULL, 'Pago completo en efectivo'),
(2, 7, '2024-12-05 12:00:00', 80000.00, 2, 'REF-12345', 'Tarjeta débito'),
(3, 8, '2024-12-08 17:30:00', 200000.00, 4, 'TRANS-98765', 'Abono 50% del tratamiento');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `producto`
--

CREATE TABLE `producto` (
  `id_producto` int(11) NOT NULL,
  `codigo_producto` varchar(50) DEFAULT NULL,
  `nombre_producto` varchar(255) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `id_categoria` int(11) NOT NULL,
  `precio_compra` decimal(10,2) NOT NULL,
  `precio_venta` decimal(10,2) NOT NULL,
  `stock_actual` int(11) NOT NULL DEFAULT 0,
  `stock_minimo` int(11) NOT NULL DEFAULT 5,
  `fecha_vencimiento` date DEFAULT NULL,
  `activo` tinyint(1) DEFAULT 1,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `producto`
--

INSERT INTO `producto` (`id_producto`, `codigo_producto`, `nombre_producto`, `descripcion`, `id_categoria`, `precio_compra`, `precio_venta`, `stock_actual`, `stock_minimo`, `fecha_vencimiento`, `activo`, `fecha_creacion`) VALUES
(1, 'PROD-001', 'Resina Fotocurable A2', 'Resina compuesta tono A2', 1, 45000.00, 65000.00, 15, 5, '2026-06-30', 1, '2025-12-08 18:55:36'),
(2, 'PROD-002', 'Anestesia Lidocaína 2%', 'Anestésico local con epinefrina', 3, 8000.00, 12000.00, 50, 10, '2025-12-31', 1, '2025-12-08 18:55:36'),
(3, 'PROD-003', 'Guantes de Nitrilo Talla M', 'Caja por 100 unidades', 4, 25000.00, 35000.00, 20, 5, NULL, 1, '2025-12-08 18:55:36'),
(4, 'PROD-004', 'Brackets Metálicos', 'Kit completo 20 piezas', 2, 120000.00, 180000.00, 8, 3, NULL, 1, '2025-12-08 18:55:36'),
(5, 'PROD-005', 'Cemento Dental', 'Cemento de ionómero de vidrio', 1, 35000.00, 50000.00, 12, 5, '2026-03-15', 1, '2025-12-08 18:55:36'),
(6, 'PROD-006', 'Jeringa Triple 3ml', 'Jeringas desechables estériles', 4, 500.00, 800.00, 200, 50, '2027-01-01', 1, '2025-12-08 18:55:36'),
(7, 'PROD-007', 'Hilo Dental 50m', 'Hilo dental encerado', 4, 3000.00, 5000.00, 30, 10, NULL, 1, '2025-12-08 18:55:36'),
(8, 'PROD-008', 'Pasta Profiláctica', 'Pasta para profilaxis sabor menta', 1, 18000.00, 28000.00, 10, 3, '2025-09-30', 1, '2025-12-08 18:55:36'),
(9, 'PROD-009', 'Gasas Estériles 10x10cm', 'Paquete de 100 unidades', 4, 15000.00, 22000.00, 25, 10, NULL, 1, '2025-12-08 18:55:36'),
(10, 'PROD-010', 'Espejo Dental #5', 'Espejo bucal acero inoxidable', 2, 8000.00, 15000.00, 30, 5, NULL, 1, '2025-12-08 18:55:36');

--
-- Disparadores `producto`
--
DELIMITER $$
CREATE TRIGGER `trg_producto_stock_update` AFTER UPDATE ON `producto` FOR EACH ROW BEGIN
  IF OLD.stock_actual != NEW.stock_actual THEN
    INSERT INTO movimiento_inventario (
      id_producto, 
      tipo_movimiento, 
      cantidad, 
      stock_anterior, 
      stock_nuevo, 
      motivo, 
      id_usuario
    ) VALUES (
      NEW.id_producto,
      IF(NEW.stock_actual > OLD.stock_actual, 'ENTRADA', 'SALIDA'),
      ABS(NEW.stock_actual - OLD.stock_actual),
      OLD.stock_actual,
      NEW.stock_actual,
      'Actualización automática de stock',
      1 
    );
  END IF;
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol`
--

CREATE TABLE `rol` (
  `id_rol` int(11) NOT NULL,
  `nombre_rol` varchar(50) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`id_rol`, `nombre_rol`, `descripcion`) VALUES
(1, 'Administrador', 'Acceso total al sistema'),
(2, 'Doctor', 'Gestión de citas y pacientes'),
(3, 'Secretaria', 'Gestión administrativa y citas'),
(4, 'Paciente', 'Acceso limitado a datos propios');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `secretaria`
--

CREATE TABLE `secretaria` (
  `id_secretaria` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `fecha_ingreso` date DEFAULT NULL,
  `turno` enum('Mañana','Tarde','Completo') DEFAULT 'Completo'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `secretaria`
--

INSERT INTO `secretaria` (`id_secretaria`, `id_usuario`, `fecha_ingreso`, `turno`) VALUES
(1, 5, '2022-02-01', 'Mañana'),
(2, 6, '2021-08-15', 'Tarde'),
(3, 7, '2023-01-10', 'Completo');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tratamiento`
--

CREATE TABLE `tratamiento` (
  `id_tratamiento` int(11) NOT NULL,
  `codigo` varchar(20) DEFAULT NULL,
  `nombre_tratamiento` varchar(255) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `costo_base` decimal(10,2) NOT NULL,
  `duracion_estimada_minutos` int(11) DEFAULT 30,
  `activo` tinyint(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `tratamiento`
--

INSERT INTO `tratamiento` (`id_tratamiento`, `codigo`, `nombre_tratamiento`, `descripcion`, `costo_base`, `duracion_estimada_minutos`, `activo`) VALUES
(1, 'TRAT-001', 'Limpieza Dental', 'Profilaxis y eliminación de placa bacteriana', 80000.00, 30, 1),
(2, 'TRAT-002', 'Blanqueamiento Dental', 'Blanqueamiento con lámpara LED', 350000.00, 60, 1),
(3, 'TRAT-003', 'Resina Dental', 'Restauración con resina fotocurable', 120000.00, 45, 1),
(4, 'TRAT-004', 'Extracción Simple', 'Extracción de pieza dental', 100000.00, 30, 1),
(5, 'TRAT-005', 'Endodoncia', 'Tratamiento de conducto', 400000.00, 90, 1),
(6, 'TRAT-006', 'Ortodoncia (mensualidad)', 'Control y ajuste de brackets', 250000.00, 30, 1),
(7, 'TRAT-007', 'Corona Dental', 'Corona de porcelana', 800000.00, 60, 1),
(8, 'TRAT-008', 'Implante Dental', 'Implante de titanio', 2500000.00, 120, 1),
(9, 'TRAT-009', 'Tratamiento de Conducto', 'Endodoncia completa', 450000.00, 90, 1),
(10, 'TRAT-010', 'Limpieza Profunda (Cuadrante)', 'Raspaje y alisado radicular', 200000.00, 45, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tratamiento_producto`
--

CREATE TABLE `tratamiento_producto` (
  `id_tratamiento_producto` int(11) NOT NULL,
  `id_tratamiento` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `cantidad_requerida` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `tratamiento_producto`
--

INSERT INTO `tratamiento_producto` (`id_tratamiento_producto`, `id_tratamiento`, `id_producto`, `cantidad_requerida`) VALUES
(1, 1, 8, 1.00),
(2, 1, 3, 2.00),
(3, 1, 7, 1.00),
(4, 3, 1, 0.50),
(5, 3, 2, 1.00),
(6, 3, 3, 2.00),
(7, 5, 2, 2.00),
(8, 5, 5, 1.00),
(9, 5, 3, 4.00),
(10, 5, 9, 5.00);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL,
  `nombre_usuario` varchar(50) NOT NULL,
  `nombre` varchar(50) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `telefono` varchar(15) DEFAULT NULL,
  `contrasena` varchar(255) NOT NULL,
  `id_rol` int(11) NOT NULL,
  `id_estado` int(11) NOT NULL DEFAULT 1,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp(),
  `fecha_actualizacion` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `nombre_usuario`, `nombre`, `apellidos`, `correo`, `telefono`, `contrasena`, `id_rol`, `id_estado`, `fecha_creacion`, `fecha_actualizacion`) VALUES
(1, 'admin', 'Carlos', 'Administrador', 'admin@odontoclinic.com', '3001234567', 'admin123', 1, 1, '2025-12-08 18:55:32', '2025-12-08 18:55:32'),
(2, 'dr.martinez', 'Ana María', 'Martínez López', 'ana.martinez@odontoclinic.com', '3001111111', 'doctor123', 2, 1, '2025-12-08 18:55:32', '2025-12-08 18:55:32'),
(3, 'dr.gomez', 'Luis Fernando', 'Gómez Pérez', 'luis.gomez@odontoclinic.com', '3002222222', 'doctor123', 2, 1, '2025-12-08 18:55:32', '2025-12-08 18:55:32'),
(4, 'dra.rodriguez', 'María Camila', 'Rodríguez Silva', 'maria.rodriguez@odontoclinic.com', '3003333333', 'doctor123', 2, 1, '2025-12-08 18:55:32', '2025-12-08 18:55:32'),
(5, 'sec.laura', 'Laura', 'García Moreno', 'laura.garcia@odontoclinic.com', '3004444444', 'secretaria123', 3, 1, '2025-12-08 18:55:32', '2025-12-08 18:55:32'),
(6, 'sec.andrea', 'Andrea', 'López Hernández', 'andrea.lopez@odontoclinic.com', '3005555555', 'secretaria123', 3, 1, '2025-12-08 18:55:32', '2025-12-08 18:55:32'),
(7, 'sec.carolina', 'Carolina', 'Sánchez Torres', 'carolina.sanchez@odontoclinic.com', '3006666666', 'secretaria123', 3, 1, '2025-12-08 18:55:32', '2025-12-08 18:55:32'),
(8, 'pac.juan', 'Juan Carlos', 'Ramírez Vargas', 'juan.ramirez@email.com', '3007777777', 'paciente123', 4, 1, '2025-12-08 18:55:33', '2025-12-08 18:55:33'),
(9, 'pac.sofia', 'Sofía', 'Herrera Castro', 'sofia.herrera@email.com', '3008888888', 'paciente123', 4, 1, '2025-12-08 18:55:33', '2025-12-08 18:55:33'),
(10, 'pac.diego', 'Diego Andrés', 'Mendoza Ruiz', 'diego.mendoza@email.com', '3009999999', 'paciente123', 4, 1, '2025-12-08 18:55:33', '2025-12-08 18:55:33');

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `categoria_producto`
--
ALTER TABLE `categoria_producto`
  ADD PRIMARY KEY (`id_categoria`),
  ADD UNIQUE KEY `nombre_categoria` (`nombre_categoria`);

--
-- Indices de la tabla `cita`
--
ALTER TABLE `cita`
  ADD PRIMARY KEY (`id_cita`),
  ADD KEY `idx_paciente` (`id_paciente`),
  ADD KEY `idx_doctor` (`id_doctor`),
  ADD KEY `idx_fecha` (`fecha_hora`),
  ADD KEY `idx_estado` (`id_estado_cita`);

--
-- Indices de la tabla `cita_tratamiento`
--
ALTER TABLE `cita_tratamiento`
  ADD PRIMARY KEY (`id_cita_tratamiento`),
  ADD UNIQUE KEY `uk_cita_tratamiento` (`id_cita`,`id_tratamiento`),
  ADD KEY `idx_cita` (`id_cita`),
  ADD KEY `idx_tratamiento` (`id_tratamiento`);

--
-- Indices de la tabla `especialidad`
--
ALTER TABLE `especialidad`
  ADD PRIMARY KEY (`id_especialidad`),
  ADD UNIQUE KEY `nombre_especialidad` (`nombre_especialidad`);

--
-- Indices de la tabla `estado`
--
ALTER TABLE `estado`
  ADD PRIMARY KEY (`id_estado`),
  ADD UNIQUE KEY `nombre_estado` (`nombre_estado`);

--
-- Indices de la tabla `estado_cita`
--
ALTER TABLE `estado_cita`
  ADD PRIMARY KEY (`id_estado_cita`),
  ADD UNIQUE KEY `nombre_estado` (`nombre_estado`);

--
-- Indices de la tabla `historial_medico`
--
ALTER TABLE `historial_medico`
  ADD PRIMARY KEY (`id_historial`),
  ADD KEY `idx_paciente` (`id_paciente`),
  ADD KEY `idx_cita` (`id_cita`),
  ADD KEY `idx_doctor` (`id_doctor`),
  ADD KEY `idx_fecha` (`fecha`);

--
-- Indices de la tabla `horario`
--
ALTER TABLE `horario`
  ADD PRIMARY KEY (`id_horario`),
  ADD KEY `idx_doctor` (`id_doctor`),
  ADD KEY `idx_dia` (`dia_semana`);

--
-- Indices de la tabla `medico`
--
ALTER TABLE `medico`
  ADD PRIMARY KEY (`id_doctor`),
  ADD UNIQUE KEY `id_usuario` (`id_usuario`),
  ADD KEY `idx_usuario` (`id_usuario`),
  ADD KEY `idx_especialidad` (`id_especialidad`);

--
-- Indices de la tabla `metodo_pago`
--
ALTER TABLE `metodo_pago`
  ADD PRIMARY KEY (`id_metodo_pago`),
  ADD UNIQUE KEY `nombre_metodo` (`nombre_metodo`);

--
-- Indices de la tabla `movimiento_inventario`
--
ALTER TABLE `movimiento_inventario`
  ADD PRIMARY KEY (`id_movimiento`),
  ADD KEY `idx_producto` (`id_producto`),
  ADD KEY `idx_fecha` (`fecha_movimiento`),
  ADD KEY `idx_usuario` (`id_usuario`);

--
-- Indices de la tabla `paciente`
--
ALTER TABLE `paciente`
  ADD PRIMARY KEY (`id_paciente`),
  ADD UNIQUE KEY `id_usuario` (`id_usuario`),
  ADD KEY `idx_usuario` (`id_usuario`),
  ADD KEY `idx_fecha_nacimiento` (`fecha_nacimiento`);

--
-- Indices de la tabla `pago`
--
ALTER TABLE `pago`
  ADD PRIMARY KEY (`id_pago`),
  ADD KEY `idx_cita` (`id_cita`),
  ADD KEY `idx_fecha` (`fecha_pago`),
  ADD KEY `idx_metodo` (`id_metodo_pago`);

--
-- Indices de la tabla `producto`
--
ALTER TABLE `producto`
  ADD PRIMARY KEY (`id_producto`),
  ADD UNIQUE KEY `codigo_producto` (`codigo_producto`),
  ADD KEY `idx_categoria` (`id_categoria`),
  ADD KEY `idx_codigo` (`codigo_producto`),
  ADD KEY `idx_stock` (`stock_actual`);

--
-- Indices de la tabla `rol`
--
ALTER TABLE `rol`
  ADD PRIMARY KEY (`id_rol`),
  ADD UNIQUE KEY `nombre_rol` (`nombre_rol`);

--
-- Indices de la tabla `secretaria`
--
ALTER TABLE `secretaria`
  ADD PRIMARY KEY (`id_secretaria`),
  ADD UNIQUE KEY `id_usuario` (`id_usuario`),
  ADD KEY `idx_usuario` (`id_usuario`);

--
-- Indices de la tabla `tratamiento`
--
ALTER TABLE `tratamiento`
  ADD PRIMARY KEY (`id_tratamiento`),
  ADD UNIQUE KEY `codigo` (`codigo`),
  ADD KEY `idx_codigo` (`codigo`),
  ADD KEY `idx_activo` (`activo`);

--
-- Indices de la tabla `tratamiento_producto`
--
ALTER TABLE `tratamiento_producto`
  ADD PRIMARY KEY (`id_tratamiento_producto`),
  ADD UNIQUE KEY `uk_tratamiento_producto` (`id_tratamiento`,`id_producto`),
  ADD KEY `idx_tratamiento` (`id_tratamiento`),
  ADD KEY `idx_producto` (`id_producto`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `nombre_usuario` (`nombre_usuario`),
  ADD UNIQUE KEY `correo` (`correo`),
  ADD KEY `idx_rol` (`id_rol`),
  ADD KEY `idx_estado` (`id_estado`),
  ADD KEY `idx_correo` (`correo`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `categoria_producto`
--
ALTER TABLE `categoria_producto`
  MODIFY `id_categoria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `cita`
--
ALTER TABLE `cita`
  MODIFY `id_cita` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `cita_tratamiento`
--
ALTER TABLE `cita_tratamiento`
  MODIFY `id_cita_tratamiento` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `especialidad`
--
ALTER TABLE `especialidad`
  MODIFY `id_especialidad` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `estado`
--
ALTER TABLE `estado`
  MODIFY `id_estado` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `estado_cita`
--
ALTER TABLE `estado_cita`
  MODIFY `id_estado_cita` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT de la tabla `historial_medico`
--
ALTER TABLE `historial_medico`
  MODIFY `id_historial` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `horario`
--
ALTER TABLE `horario`
  MODIFY `id_horario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=14;

--
-- AUTO_INCREMENT de la tabla `medico`
--
ALTER TABLE `medico`
  MODIFY `id_doctor` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `metodo_pago`
--
ALTER TABLE `metodo_pago`
  MODIFY `id_metodo_pago` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT de la tabla `movimiento_inventario`
--
ALTER TABLE `movimiento_inventario`
  MODIFY `id_movimiento` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT de la tabla `paciente`
--
ALTER TABLE `paciente`
  MODIFY `id_paciente` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `pago`
--
ALTER TABLE `pago`
  MODIFY `id_pago` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `producto`
--
ALTER TABLE `producto`
  MODIFY `id_producto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `rol`
--
ALTER TABLE `rol`
  MODIFY `id_rol` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT de la tabla `secretaria`
--
ALTER TABLE `secretaria`
  MODIFY `id_secretaria` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tratamiento`
--
ALTER TABLE `tratamiento`
  MODIFY `id_tratamiento` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `tratamiento_producto`
--
ALTER TABLE `tratamiento_producto`
  MODIFY `id_tratamiento_producto` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `producto`
--
ALTER TABLE `producto`
  ADD CONSTRAINT `fk_producto_categoria` FOREIGN KEY (`id_categoria`) REFERENCES `categoria_producto` (`id_categoria`);

--
-- Filtros para la tabla `secretaria`
--
ALTER TABLE `secretaria`
  ADD CONSTRAINT `fk_secretaria_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE;

--
-- Filtros para la tabla `tratamiento_producto`
--
ALTER TABLE `tratamiento_producto`
  ADD CONSTRAINT `fk_trat_prod_producto` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`),
  ADD CONSTRAINT `fk_trat_prod_tratamiento` FOREIGN KEY (`id_tratamiento`) REFERENCES `tratamiento` (`id_tratamiento`) ON DELETE CASCADE;

--
-- Filtros para la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD CONSTRAINT `fk_usuario_estado` FOREIGN KEY (`id_estado`) REFERENCES `estado` (`id_estado`),
  ADD CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
