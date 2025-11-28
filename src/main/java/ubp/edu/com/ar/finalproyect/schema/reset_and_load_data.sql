-- =============================================
-- COMPLETE DATABASE RESET AND RELOAD SCRIPT
-- =============================================
-- This script performs:
-- 1. Cleans all existing data
-- 2. Resets IDENTITY columns
-- 3. Loads fresh test data
--
-- Execute this script to get a clean database state
-- =============================================

PRINT '========================================';
PRINT 'STARTING COMPLETE DATABASE RESET';
PRINT '========================================';
PRINT '';

-- =============================================
-- STEP 1: CLEAN DATABASE
-- =============================================
PRINT 'STEP 1: Cleaning existing data...';
PRINT '------------------------------------';

-- Delete in correct order to avoid FK violations
DELETE FROM PedidoProducto;
DELETE FROM Pedido;
DELETE FROM HistorialPrecio;
DELETE FROM ProductoProveedor;
DELETE FROM Producto;
DELETE FROM Proveedor;
DELETE FROM EstadoPedido;
DELETE FROM EstadoProducto;
DELETE FROM TipoServicio;

-- Reset auto-increment IDs
DBCC CHECKIDENT ('EstadoProducto', RESEED, 0);
DBCC CHECKIDENT ('TipoServicio', RESEED, 0);
DBCC CHECKIDENT ('Proveedor', RESEED, 0);
DBCC CHECKIDENT ('EstadoPedido', RESEED, 0);
DBCC CHECKIDENT ('Pedido', RESEED, 0);

PRINT 'Database cleaned successfully!';
PRINT '';

-- =============================================
-- STEP 2: INSERT TEST DATA
-- =============================================
PRINT 'STEP 2: Inserting test data...';
PRINT '------------------------------------';

-- 1. ESTADOS DE PRODUCTO
PRINT 'Inserting EstadoProducto...';
INSERT INTO EstadoProducto (nombre, descripcion) VALUES
('Disponible', 'Producto disponible para la venta'),
('Agotado', 'Producto sin stock disponible'),
('Descontinuado', 'Producto descontinuado, no se volverá a vender');

-- 2. TIPOS DE SERVICIO
PRINT 'Inserting TipoServicio...';
INSERT INTO TipoServicio (nombre) VALUES
('REST'),
('SOAP');

-- 3. ESTADOS DE PEDIDO
PRINT 'Inserting EstadoPedido...';
INSERT INTO EstadoPedido (nombre, descripcion) VALUES
('Pendiente', 'Pedido pendiente de confirmación por el proveedor'),
('Confirmado', 'Pedido confirmado por el proveedor'),
('En Preparación', 'Pedido en proceso de preparación'),
('En Tránsito', 'Pedido en camino al supermercado'),
('Entregado', 'Pedido entregado exitosamente'),
('Cancelado', 'Pedido cancelado');

-- 4. PROVEEDORES
PRINT 'Inserting Proveedores...';
INSERT INTO Proveedor (nombre, apiEndpoint, tipoServicio, clientId, apiKey) VALUES
('Distribuidora Central REST', 'http://localhost:8081', 1, 'testclient', 'test-api-key-123'),
('Mayorista Del Sur S.A.', 'https://api.mayoristadelsur.com', 1, 'supermarket001', 'sk_live_abc123xyz789'),
('Proveedor SOAP Ejemplo', 'https://soap.provider.com/service.wsdl', 2, 'soapclient', 'soap_key_456');

-- 5. PRODUCTOS
PRINT 'Inserting Productos...';
-- Almacén
INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual) VALUES
(1001, 'Leche Descremada La Serenísima 1L', 'https://via.placeholder.com/150?text=Leche', 20, 200, 80),
(1002, 'Arroz Gallo Oro 1kg', 'https://via.placeholder.com/150?text=Arroz', 15, 150, 45),
(1003, 'Aceite Cocinero 900ml', 'https://via.placeholder.com/150?text=Aceite', 10, 100, 35),
(1004, 'Azúcar Ledesma 1kg', 'https://via.placeholder.com/150?text=Azucar', 25, 250, 100);

-- Bebidas
INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual) VALUES
(2001, 'Coca Cola 2.25L', 'https://via.placeholder.com/150?text=CocaCola', 30, 300, 120),
(2002, 'Sprite 2.25L', 'https://via.placeholder.com/150?text=Sprite', 20, 200, 75),
(2003, 'Agua Mineral Villavicencio 2L', 'https://via.placeholder.com/150?text=Agua', 40, 400, 180);

-- Limpieza
INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual) VALUES
(3001, 'Detergente Magistral 750ml', 'https://via.placeholder.com/150?text=Detergente', 15, 150, 60),
(3002, 'Lavandina Ayudín 1L', 'https://via.placeholder.com/150?text=Lavandina', 12, 120, 40),
(3003, 'Limpiador Mr. Músculo 500ml', 'https://via.placeholder.com/150?text=Limpiador', 10, 100, 30);

-- Panadería
INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual) VALUES
(4001, 'Pan Lactal Bimbo 450g', 'https://via.placeholder.com/150?text=Pan', 25, 250, 90),
(4002, 'Galletitas Oreo 118g', 'https://via.placeholder.com/150?text=Oreo', 30, 300, 150),
(4003, 'Tostadas Criollitas 120g', 'https://via.placeholder.com/150?text=Tostadas', 20, 200, 85);

-- 6. PRODUCTO-PROVEEDOR
PRINT 'Inserting ProductoProveedor...';
-- Proveedor 1: Almacén y Bebidas
INSERT INTO ProductoProveedor (idProveedor, codigoBarra, estado, codigoBarraProveedor) VALUES
(1, 1001, 1, 1001),  -- Leche
(1, 1002, 1, 1002),  -- Arroz
(1, 1003, 1, 1003),  -- Aceite
(1, 1004, 1, 1004),  -- Azúcar
(1, 2001, 1, 2001),  -- Coca Cola
(1, 2002, 1, 2002),  -- Sprite
(1, 2003, 1, 2003);  -- Agua

-- Proveedor 2: Limpieza y Panadería
INSERT INTO ProductoProveedor (idProveedor, codigoBarra, estado, codigoBarraProveedor) VALUES
(2, 3001, 1, 3001),  -- Detergente
(2, 3002, 1, 3002),  -- Lavandina
(2, 3003, 1, 3003),  -- Limpiador
(2, 4001, 1, 4001),  -- Pan
(2, 4002, 1, 4002),  -- Oreo
(2, 4003, 1, 4003);  -- Tostadas

-- 7. HISTORIAL DE PRECIOS
PRINT 'Inserting HistorialPrecio...';
-- Proveedor 1 - Precios actuales
INSERT INTO HistorialPrecio (codigoBarra, idProveedor, precio, fechaInicio, fechaFin) VALUES
(1001, 1, 1250.00, '2025-11-01 00:00:00', NULL),
(1002, 1, 1800.00, '2025-11-01 00:00:00', NULL),
(1003, 1, 3500.00, '2025-11-01 00:00:00', NULL),
(1004, 1, 1400.00, '2025-11-01 00:00:00', NULL),
(2001, 1, 2200.00, '2025-11-01 00:00:00', NULL),
(2002, 1, 2000.00, '2025-11-01 00:00:00', NULL),
(2003, 1, 950.00, '2025-11-01 00:00:00', NULL);

-- Proveedor 1 - Precios históricos
INSERT INTO HistorialPrecio (codigoBarra, idProveedor, precio, fechaInicio, fechaFin) VALUES
(1001, 1, 1150.00, '2025-10-01 00:00:00', '2025-10-31 23:59:59'),
(1002, 1, 1650.00, '2025-10-01 00:00:00', '2025-10-31 23:59:59'),
(2001, 1, 2050.00, '2025-10-01 00:00:00', '2025-10-31 23:59:59');

-- Proveedor 2 - Precios actuales
INSERT INTO HistorialPrecio (codigoBarra, idProveedor, precio, fechaInicio, fechaFin) VALUES
(3001, 2, 2800.00, '2025-11-01 00:00:00', NULL),
(3002, 2, 1500.00, '2025-11-01 00:00:00', NULL),
(3003, 2, 3200.00, '2025-11-01 00:00:00', NULL),
(4001, 2, 1800.00, '2025-11-01 00:00:00', NULL),
(4002, 2, 2500.00, '2025-11-01 00:00:00', NULL),
(4003, 2, 1200.00, '2025-11-01 00:00:00', NULL);

-- 8. PEDIDOS
PRINT 'Inserting Pedidos and PedidoProducto...';

-- Pedido 1: Pendiente
DECLARE @pedido1 INT;
INSERT INTO Pedido (estado, proveedor, fechaEstimada, fechaEntrega, fechaRegistro, evaluacionEscala)
VALUES (1, 1, '2025-12-05 10:00:00', NULL, GETDATE(), NULL);
SET @pedido1 = SCOPE_IDENTITY();

INSERT INTO PedidoProducto (idPedido, codigoBarra, cantidad) VALUES
(@pedido1, 1001, 50),  -- Leche
(@pedido1, 1002, 30),  -- Arroz
(@pedido1, 2001, 60);  -- Coca Cola

-- Pedido 2: Confirmado
DECLARE @pedido2 INT;
INSERT INTO Pedido (estado, proveedor, fechaEstimada, fechaEntrega, fechaRegistro, evaluacionEscala)
VALUES (2, 2, '2025-12-03 14:00:00', NULL, GETDATE(), NULL);
SET @pedido2 = SCOPE_IDENTITY();

INSERT INTO PedidoProducto (idPedido, codigoBarra, cantidad) VALUES
(@pedido2, 3001, 25),  -- Detergente
(@pedido2, 3002, 20),  -- Lavandina
(@pedido2, 3003, 15);  -- Limpiador

-- Pedido 3: En Tránsito
DECLARE @pedido3 INT;
INSERT INTO Pedido (estado, proveedor, fechaEstimada, fechaEntrega, fechaRegistro, evaluacionEscala)
VALUES (4, 1, '2025-11-30 16:00:00', NULL, DATEADD(day, -2, GETDATE()), NULL);
SET @pedido3 = SCOPE_IDENTITY();

INSERT INTO PedidoProducto (idPedido, codigoBarra, cantidad) VALUES
(@pedido3, 2002, 40),  -- Sprite
(@pedido3, 2003, 80),  -- Agua
(@pedido3, 1004, 35);  -- Azúcar

-- Pedido 4: Entregado
DECLARE @pedido4 INT;
INSERT INTO Pedido (estado, proveedor, fechaEstimada, fechaEntrega, fechaRegistro, evaluacionEscala)
VALUES (5, 2, '2025-11-25 09:00:00', '2025-11-25 11:30:00', DATEADD(day, -5, GETDATE()), 5);
SET @pedido4 = SCOPE_IDENTITY();

INSERT INTO PedidoProducto (idPedido, codigoBarra, cantidad) VALUES
(@pedido4, 4001, 45),  -- Pan
(@pedido4, 4002, 60),  -- Oreo
(@pedido4, 4003, 35);  -- Tostadas

PRINT '';
PRINT '========================================';
PRINT 'DATABASE RESET COMPLETED SUCCESSFULLY!';
PRINT '========================================';
PRINT '';
PRINT 'Summary:';
PRINT '  - Estados Producto: 3';
PRINT '  - Tipos Servicio: 2';
PRINT '  - Estados Pedido: 6';
PRINT '  - Proveedores: 3';
PRINT '  - Productos: 13';
PRINT '  - Asignaciones Producto-Proveedor: 13';
PRINT '  - Historial Precios: 16';
PRINT '  - Pedidos: 4';
PRINT '  - Items en Pedidos: 12';
PRINT '';
PRINT 'Database is ready for testing!';
PRINT '========================================';
