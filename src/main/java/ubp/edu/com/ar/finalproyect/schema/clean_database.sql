-- =============================================
-- CLEAN DATABASE SCRIPT
-- =============================================
-- This script deletes all data from tables
-- Preserves table structure and stored procedures
-- Execute BEFORE running test_data.sql
-- =============================================

PRINT 'Starting database cleanup...';

-- =============================================
-- 1. Delete all records (respecting FK constraints)
-- =============================================

-- Delete in correct order to avoid FK violations
DELETE FROM PedidoProducto;
PRINT 'Deleted PedidoProducto records';

DELETE FROM Pedido;
PRINT 'Deleted Pedido records';

DELETE FROM HistorialPrecio;
PRINT 'Deleted HistorialPrecio records';

DELETE FROM ProductoProveedor;
PRINT 'Deleted ProductoProveedor records';

DELETE FROM Producto;
PRINT 'Deleted Producto records';

DELETE FROM Proveedor;
PRINT 'Deleted Proveedor records';

DELETE FROM EstadoPedido;
PRINT 'Deleted EstadoPedido records';

DELETE FROM EstadoProducto;
PRINT 'Deleted EstadoProducto records';

DELETE FROM TipoServicio;
PRINT 'Deleted TipoServicio records';

-- =============================================
-- 2. Reset IDENTITY columns
-- =============================================

-- Reset auto-increment IDs to start from 1
DBCC CHECKIDENT ('EstadoProducto', RESEED, 0);
DBCC CHECKIDENT ('TipoServicio', RESEED, 0);
DBCC CHECKIDENT ('Proveedor', RESEED, 0);
DBCC CHECKIDENT ('EstadoPedido', RESEED, 0);
DBCC CHECKIDENT ('Pedido', RESEED, 0);

PRINT 'Reset IDENTITY columns';

-- =============================================
-- Summary
-- =============================================
PRINT '================================';
PRINT 'Database cleanup completed!';
PRINT 'All data deleted successfully';
PRINT 'IDENTITY columns reset to 0';
PRINT 'Ready to insert test data';
PRINT '================================';
