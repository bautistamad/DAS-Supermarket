-- =============================================
-- DATABASE INITIALIZATION SCRIPT
-- =============================================
-- This script performs:
-- 1. Cleans all existing data
-- 2. Resets IDENTITY columns
-- 3. Loads essential catalog data (Estados, TipoServicio)
--
-- Note: Products, Providers, Orders, etc. should be created via frontend
-- =============================================

PRINT '========================================';
PRINT 'STARTING DATABASE INITIALIZATION';
PRINT '========================================';
PRINT '';

-- =============================================
-- STEP 1: CLEAN DATABASE
-- =============================================
PRINT 'STEP 1: Cleaning existing data...';
PRINT '------------------------------------';

-- Delete in correct order to avAoid FK violations
DELETE FROM PedidoProducto;
DELETE FROM Pedido;
DELETE FROM HistorialPrecio;
DELETE FROM ProductoProveedor;
DELETE FROM Escala;
DELETE FROM Producto;
DELETE FROM Proveedor;
DELETE FROM EstadoPedido;
DELETE FROM EstadoProducto;
DELETE FROM TipoServicio;

-- Reset auto-increment IDs (only tables with IDENTITY columns)
-- Note: RESEED to 0 makes the next insert use ID = 1
DBCC CHECKIDENT ('EstadoProducto', RESEED, 0);
DBCC CHECKIDENT ('TipoServicio', RESEED, 0);
DBCC CHECKIDENT ('Proveedor', RESEED, 0);
DBCC CHECKIDENT ('EstadoPedido', RESEED, 0);
DBCC CHECKIDENT ('Pedido', RESEED, 0);
DBCC CHECKIDENT ('Escala', RESEED, 0);
-- Note: Producto uses codigoBarra as PK (not IDENTITY), so no CHECKIDENT needed

PRINT 'Database cleaned successfully!';
PRINT '';

-- =============================================
-- STEP 2: INSERT CATALOG DATA
-- =============================================
PRINT 'STEP 2: Inserting catalog data...';
PRINT '------------------------------------';

-- 1. ESTADOS DE PRODUCTO (Required catalog)
PRINT 'Inserting EstadoProducto...';
INSERT INTO EstadoProducto (nombre, descripcion) VALUES
('Disponible', 'Producto disponible para la venta'),
('Agotado', 'Producto sin stock disponible'),
('Descontinuado', 'Producto descontinuado, no se volverá a vender');

-- 2. TIPOS DE SERVICIO (Required catalog)
PRINT 'Inserting TipoServicio...';
INSERT INTO TipoServicio (nombre) VALUES
('REST'),
('SOAP');

-- 3. ESTADOS DE PEDIDO (Required catalog)
PRINT 'Inserting EstadoPedido...';
INSERT INTO EstadoPedido (nombre, descripcion) VALUES
('Pendiente', 'Pedido pendiente de confirmacion por el proveedor'),
('En Proceso', 'Pedido confirmado y en proceso de preparacion'),
('Enviado', 'Pedido despachado y en camino al supermercado'),
('Entregado', 'Pedido recibido por el supermercado'),
('Cancelado', 'Pedido cancelado por el cliente o proveedor');

PRINT '';
PRINT '========================================';
PRINT 'DATABASE INITIALIZATION COMPLETED!';
PRINT '========================================';
PRINT '';
PRINT 'Summary:';
PRINT '  - Estados Producto: 3';
PRINT '  - Tipos Servicio: 2';
PRINT '  - Estados Pedido: 5';
PRINT '';
PRINT 'Catalog data loaded successfully!';
PRINT 'Products, Providers, and Orders should be created via frontend.';
PRINT '========================================';
