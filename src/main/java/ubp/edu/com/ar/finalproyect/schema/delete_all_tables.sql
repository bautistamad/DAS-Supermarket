-- 1. Drop the "child" tables (that reference other tables) first.
-- These have FKs pointing to Pedido, Producto, Proveedor, etc.
DROP TABLE IF EXISTS dbo.PedidoProducto;
DROP TABLE IF EXISTS dbo.HistorialPrecio;
DROP TABLE IF EXISTS dbo.ProductoProveedor;

-- 2. Drop the 'Pedido' table.
-- It was a parent to PedidoProducto, but a child of EstadoPedido and Proveedor.
DROP TABLE IF EXISTS dbo.Pedido;

-- 3. Drop the "parent" or base tables.
-- Now that no other tables reference them, they can be safely dropped.
DROP TABLE IF EXISTS dbo.Producto;
DROP TABLE IF EXISTS dbo.Proveedor;
DROP TABLE IF EXISTS dbo.EstadoProducto;
DROP TABLE IF EXISTS dbo.EstadoPedido;
GO