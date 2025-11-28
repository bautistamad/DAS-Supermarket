-- =============================================
-- TEST DATA FOR SupermarketProyect
-- =============================================
-- Clean and organized test data for development
-- Last Updated: 2025-11-27
-- =============================================

-- =============================================
-- 1. ESTADOS DE PRODUCTO
-- =============================================
INSERT INTO EstadoProducto (nombre, descripcion) VALUES
('Disponible', 'Producto disponible para la venta'),
('Agotado', 'Producto sin stock disponible'),
('Descontinuado', 'Producto descontinuado, no se volverá a vender');

-- =============================================
-- 2. TIPOS DE SERVICIO
-- =============================================
INSERT INTO TipoServicio (nombre) VALUES
('REST'),
('SOAP');

-- =============================================
-- 3. ESTADOS DE PEDIDO
-- =============================================
INSERT INTO EstadoPedido (nombre, descripcion) VALUES
('Pendiente', 'Pedido pendiente de confirmación por el proveedor'),
('Confirmado', 'Pedido confirmado por el proveedor'),
('En Preparación', 'Pedido en proceso de preparación'),
('En Tránsito', 'Pedido en camino al supermercado'),
('Entregado', 'Pedido entregado exitosamente'),
('Cancelado', 'Pedido cancelado');

-- =============================================
-- 4. PROVEEDORES (SUPPLIERS)
-- =============================================
-- Nota: Estos proveedores son ejemplos. Cambiar apiEndpoint y credenciales según tu entorno.

-- Proveedor REST Local (para testing)
INSERT INTO Proveedor (nombre, apiEndpoint, tipoServicio, clientId, apiKey) VALUES
('Distribuidora Central REST', 'http://localhost:8081', 1, 'testclient', 'test-api-key-123');

-- Proveedor REST Remoto (ejemplo)
INSERT INTO Proveedor (nombre, apiEndpoint, tipoServicio, clientId, apiKey) VALUES
('Mayorista Del Sur S.A.', 'https://api.mayoristadelsur.com', 1, 'supermarket001', 'sk_live_abc123xyz789');

-- Proveedor SOAP (ejemplo - cuando esté disponible)
INSERT INTO Proveedor (nombre, apiEndpoint, tipoServicio, clientId, apiKey) VALUES
('Proveedor SOAP Ejemplo', 'https://soap.provider.com/service.wsdl', 2, 'soapclient', 'soap_key_456');

-- =============================================
-- 5. PRODUCTOS
-- =============================================
-- Productos de Almacén
INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual) VALUES
(1001, 'Leche Descremada La Serenísima 1L', 'https://via.placeholder.com/150?text=Leche', 20, 200, 80),
(1002, 'Arroz Gallo Oro 1kg', 'https://via.placeholder.com/150?text=Arroz', 15, 150, 45),
(1003, 'Aceite Cocinero 900ml', 'https://via.placeholder.com/150?text=Aceite', 10, 100, 35),
(1004, 'Azúcar Ledesma 1kg', 'https://via.placeholder.com/150?text=Azucar', 25, 250, 100);

-- Productos de Bebidas
INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual) VALUES
(2001, 'Coca Cola 2.25L', 'https://via.placeholder.com/150?text=CocaCola', 30, 300, 120),
(2002, 'Sprite 2.25L', 'https://via.placeholder.com/150?text=Sprite', 20, 200, 75),
(2003, 'Agua Mineral Villavicencio 2L', 'https://via.placeholder.com/150?text=Agua', 40, 400, 180);

-- Productos de Limpieza
INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual) VALUES
(3001, 'Detergente Magistral 750ml', 'https://via.placeholder.com/150?text=Detergente', 15, 150, 60),
(3002, 'Lavandina Ayudín 1L', 'https://via.placeholder.com/150?text=Lavandina', 12, 120, 40),
(3003, 'Limpiador Mr. Músculo 500ml', 'https://via.placeholder.com/150?text=Limpiador', 10, 100, 30);

-- Productos de Panadería
INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual) VALUES
(4001, 'Pan Lactal Bimbo 450g', 'https://via.placeholder.com/150?text=Pan', 25, 250, 90),
(4002, 'Galletitas Oreo 118g', 'https://via.placeholder.com/150?text=Oreo', 30, 300, 150),
(4003, 'Tostadas Criollitas 120g', 'https://via.placeholder.com/150?text=Tostadas', 20, 200, 85);

-- =============================================
-- 6. ASIGNACIÓN PRODUCTO-PROVEEDOR
-- =============================================
-- Proveedor 1 (Distribuidora Central) - Productos de Almacén y Bebidas
INSERT INTO ProductoProveedor (idProveedor, codigoBarra, estado, codigoBarraProveedor) VALUES
(1, 1001, 1, 1001),  -- Leche
(1, 1002, 1, 1002),  -- Arroz
(1, 1003, 1, 1003),  -- Aceite
(1, 1004, 1, 1004),  -- Azúcar
(1, 2001, 1, 2001),  -- Coca Cola
(1, 2002, 1, 2002),  -- Sprite
(1, 2003, 1, 2003);  -- Agua

-- Proveedor 2 (Mayorista Del Sur) - Productos de Limpieza y Panadería
INSERT INTO ProductoProveedor (idProveedor, codigoBarra, estado, codigoBarraProveedor) VALUES
(2, 3001, 1, 3001),  -- Detergente
(2, 3002, 1, 3002),  -- Lavandina
(2, 3003, 1, 3003),  -- Limpiador
(2, 4001, 1, 4001),  -- Pan
(2, 4002, 1, 4002),  -- Oreo
(2, 4003, 1, 4003);  -- Tostadas

-- =============================================
-- 7. HISTORIAL DE PRECIOS
-- =============================================
-- Proveedor 1 - Precios actuales (fechaFin = NULL significa precio vigente)
INSERT INTO HistorialPrecio (codigoBarra, idProveedor, precio, fechaInicio, fechaFin) VALUES
(1001, 1, 1250.00, '2025-11-01 00:00:00', NULL),  -- Leche
(1002, 1, 1800.00, '2025-11-01 00:00:00', NULL),  -- Arroz
(1003, 1, 3500.00, '2025-11-01 00:00:00', NULL),  -- Aceite
(1004, 1, 1400.00, '2025-11-01 00:00:00', NULL),  -- Azúcar
(2001, 1, 2200.00, '2025-11-01 00:00:00', NULL),  -- Coca Cola
(2002, 1, 2000.00, '2025-11-01 00:00:00', NULL),  -- Sprite
(2003, 1, 950.00, '2025-11-01 00:00:00', NULL);   -- Agua

-- Proveedor 1 - Precios históricos (Octubre 2025)
INSERT INTO HistorialPrecio (codigoBarra, idProveedor, precio, fechaInicio, fechaFin) VALUES
(1001, 1, 1150.00, '2025-10-01 00:00:00', '2025-10-31 23:59:59'),  -- Leche (precio anterior)
(1002, 1, 1650.00, '2025-10-01 00:00:00', '2025-10-31 23:59:59'),  -- Arroz (precio anterior)
(2001, 1, 2050.00, '2025-10-01 00:00:00', '2025-10-31 23:59:59');  -- Coca Cola (precio anterior)

-- Proveedor 2 - Precios actuales
INSERT INTO HistorialPrecio (codigoBarra, idProveedor, precio, fechaInicio, fechaFin) VALUES
(3001, 2, 2800.00, '2025-11-01 00:00:00', NULL),  -- Detergente
(3002, 2, 1500.00, '2025-11-01 00:00:00', NULL),  -- Lavandina
(3003, 2, 3200.00, '2025-11-01 00:00:00', NULL),  -- Limpiador
(4001, 2, 1800.00, '2025-11-01 00:00:00', NULL),  -- Pan
(4002, 2, 2500.00, '2025-11-01 00:00:00', NULL),  -- Oreo
(4003, 2, 1200.00, '2025-11-01 00:00:00', NULL);  -- Tostadas

-- =============================================
-- 8. PEDIDOS DE EJEMPLO
-- =============================================
-- Pedido 1: Pendiente - Proveedor 1 (Almacén + Bebidas)
DECLARE @pedido1 INT;
INSERT INTO Pedido (estado, proveedor, fechaEstimada, fechaEntrega, fechaRegistro, evaluacionEscala)
VALUES (1, 1, '2025-12-05 10:00:00', NULL, GETDATE(), NULL);
SET @pedido1 = SCOPE_IDENTITY();

INSERT INTO PedidoProducto (idPedido, codigoBarra, cantidad) VALUES
(@pedido1, 1001, 50),  -- 50 unidades de Leche
(@pedido1, 1002, 30),  -- 30 unidades de Arroz
(@pedido1, 2001, 60);  -- 60 unidades de Coca Cola

-- Pedido 2: Confirmado - Proveedor 2 (Limpieza)
DECLARE @pedido2 INT;
INSERT INTO Pedido (estado, proveedor, fechaEstimada, fechaEntrega, fechaRegistro, evaluacionEscala)
VALUES (2, 2, '2025-12-03 14:00:00', NULL, GETDATE(), NULL);
SET @pedido2 = SCOPE_IDENTITY();

INSERT INTO PedidoProducto (idPedido, codigoBarra, cantidad) VALUES
(@pedido2, 3001, 25),  -- 25 unidades de Detergente
(@pedido2, 3002, 20),  -- 20 unidades de Lavandina
(@pedido2, 3003, 15);  -- 15 unidades de Limpiador

-- Pedido 3: En Tránsito - Proveedor 1 (Bebidas + Almacén)
DECLARE @pedido3 INT;
INSERT INTO Pedido (estado, proveedor, fechaEstimada, fechaEntrega, fechaRegistro, evaluacionEscala)
VALUES (4, 1, '2025-11-30 16:00:00', NULL, DATEADD(day, -2, GETDATE()), NULL);
SET @pedido3 = SCOPE_IDENTITY();

INSERT INTO PedidoProducto (idPedido, codigoBarra, cantidad) VALUES
(@pedido3, 2002, 40),  -- 40 unidades de Sprite
(@pedido3, 2003, 80),  -- 80 unidades de Agua
(@pedido3, 1004, 35);  -- 35 unidades de Azúcar

-- Pedido 4: Entregado - Proveedor 2 (Panadería)
DECLARE @pedido4 INT;
INSERT INTO Pedido (estado, proveedor, fechaEstimada, fechaEntrega, fechaRegistro, evaluacionEscala)
VALUES (5, 2, '2025-11-25 09:00:00', '2025-11-25 11:30:00', DATEADD(day, -5, GETDATE()), 5);
SET @pedido4 = SCOPE_IDENTITY();

INSERT INTO PedidoProducto (idPedido, codigoBarra, cantidad) VALUES
(@pedido4, 4001, 45),  -- 45 unidades de Pan
(@pedido4, 4002, 60),  -- 60 unidades de Oreo
(@pedido4, 4003, 35);  -- 35 unidades de Tostadas

-- =============================================
-- RESUMEN DE DATOS DE PRUEBA
-- =============================================
-- Estados Producto: 3 registros
-- Tipos Servicio: 2 registros (REST, SOAP)
-- Estados Pedido: 6 registros
-- Proveedores: 3 registros (2 REST, 1 SOAP)
-- Productos: 13 registros
-- Asignaciones Producto-Proveedor: 13 registros
-- Historial Precios: 16 registros (10 actuales, 6 históricos)
-- Pedidos: 4 registros (Pendiente, Confirmado, En Tránsito, Entregado)
-- Productos en Pedidos: 12 líneas
-- =============================================

PRINT 'Test data inserted successfully!';
PRINT 'Total Proveedores: 3';
PRINT 'Total Productos: 13';
PRINT 'Total Pedidos: 4';
PRINT 'Ready for testing!';
