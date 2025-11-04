-- =============================================
-- Test Data for Orders (Pedidos)
-- =============================================


-- Insert EstadoProducto (Product States) if not exists
IF NOT EXISTS (SELECT 1 FROM EstadoProducto WHERE id = 1)
BEGIN
INSERT INTO EstadoProducto (nombre, descripcion) VALUES ('Disponible', 'Producto disponible para venta');
INSERT INTO EstadoProducto (nombre, descripcion) VALUES ('Agotado', 'Producto sin stock disponible');
INSERT INTO EstadoProducto (nombre, descripcion) VALUES ('Descontinuado', 'Producto ya no se comercializa');
END
GO

-- Insert EstadoPedido (Order States)
IF NOT EXISTS (SELECT 1 FROM EstadoPedido WHERE id = 1)
BEGIN
INSERT INTO EstadoPedido (nombre, descripcion) VALUES ('Pendiente', 'Pedido creado, esperando confirmación');
INSERT INTO EstadoPedido (nombre, descripcion) VALUES ('Confirmado', 'Pedido confirmado por el proveedor');
INSERT INTO EstadoPedido (nombre, descripcion) VALUES ('En Preparación', 'Pedido siendo preparado por el proveedor');
INSERT INTO EstadoPedido (nombre, descripcion) VALUES ('En Tránsito', 'Pedido en camino');
INSERT INTO EstadoPedido (nombre, descripcion) VALUES ('Entregado', 'Pedido entregado exitosamente');
INSERT INTO EstadoPedido (nombre, descripcion) VALUES ('Cancelado', 'Pedido cancelado');
END
GO

-- Insert Test Providers (Proveedores) with specific IDs
-- Actual columns: id, nombre, servicio, tipoServicio, escala
IF NOT EXISTS (SELECT 1 FROM Proveedor WHERE id = 1)
BEGIN
        SET IDENTITY_INSERT Proveedor ON;
INSERT INTO Proveedor (id, nombre, servicio, tipoServicio, escala) VALUES (1, 'Distribuidora La Central', 'Distribución de alimentos', 1, 1);
INSERT INTO Proveedor (id, nombre, servicio, tipoServicio, escala) VALUES (2, 'Mayorista Del Norte', 'Mayorista general', 2, 2);
INSERT INTO Proveedor (id, nombre, servicio, tipoServicio, escala) VALUES (3, 'Alimentos Frescos SA', 'Alimentos frescos y congelados', 1, 3);
SET IDENTITY_INSERT Proveedor OFF;
END
GO

-- Insert Test Products - Check each individually
IF NOT EXISTS (SELECT 1 FROM Producto WHERE codigoBarra = 1001)
    INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual) VALUES (1001, 'Arroz Gallo Oro 1kg', 'https://example.com/arroz.jpg', 50, 500, 120);

IF NOT EXISTS (SELECT 1 FROM Producto WHERE codigoBarra = 1002)
    INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual) VALUES (1002, 'Fideos Matarazzo 500g', 'https://example.com/fideos.jpg', 100, 1000, 450);

IF NOT EXISTS (SELECT 1 FROM Producto WHERE codigoBarra = 1003)
    INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual) VALUES (1003, 'Aceite Cocinero 900ml', 'https://example.com/aceite.jpg', 30, 300, 85);

IF NOT EXISTS (SELECT 1 FROM Producto WHERE codigoBarra = 1004)
    INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual) VALUES (1004, 'Azúcar Ledesma 1kg', 'https://example.com/azucar.jpg', 80, 800, 200);

IF NOT EXISTS (SELECT 1 FROM Producto WHERE codigoBarra = 1005)
    INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual) VALUES (1005, 'Yerba Playadito 1kg', 'https://example.com/yerba.jpg', 60, 600, 150);
GO

-- Assign Products to Providers - Check each individually
-- ProductoProveedor columns: idProveedor, codigoProducto, fechaActualizacion, estado
IF NOT EXISTS (SELECT 1 FROM ProductoProveedor WHERE codigoProducto = 1001 AND idProveedor = 1)
    INSERT INTO ProductoProveedor (codigoProducto, idProveedor, estado) VALUES (1001, 1, 1);

IF NOT EXISTS (SELECT 1 FROM ProductoProveedor WHERE codigoProducto = 1002 AND idProveedor = 1)
    INSERT INTO ProductoProveedor (codigoProducto, idProveedor, estado) VALUES (1002, 1, 1);

IF NOT EXISTS (SELECT 1 FROM ProductoProveedor WHERE codigoProducto = 1003 AND idProveedor = 2)
    INSERT INTO ProductoProveedor (codigoProducto, idProveedor, estado) VALUES (1003, 2, 1);

IF NOT EXISTS (SELECT 1 FROM ProductoProveedor WHERE codigoProducto = 1004 AND idProveedor = 2)
    INSERT INTO ProductoProveedor (codigoProducto, idProveedor, estado) VALUES (1004, 2, 1);

IF NOT EXISTS (SELECT 1 FROM ProductoProveedor WHERE codigoProducto = 1005 AND idProveedor = 3)
    INSERT INTO ProductoProveedor (codigoProducto, idProveedor, estado) VALUES (1005, 3, 1);
GO

-- Insert Test Orders (Pedidos) - Check each individually
-- Note: fechaRegistro will use DEFAULT GETDATE() automatically
IF NOT EXISTS (SELECT 1 FROM Pedido WHERE estado = 1 AND proveedor = 1 AND fechaCreada = '2025-01-15 10:30:00')
    INSERT INTO Pedido (estado, proveedor, puntuacion, fechaCreada, fechaEntrega, evaluacion) VALUES (1, 1, NULL, '2025-01-15 10:30:00', NULL, NULL);

IF NOT EXISTS (SELECT 1 FROM Pedido WHERE estado = 2 AND proveedor = 1 AND fechaCreada = '2025-01-20 14:15:00')
    INSERT INTO Pedido (estado, proveedor, puntuacion, fechaCreada, fechaEntrega, evaluacion) VALUES (2, 1, NULL, '2025-01-20 14:15:00', NULL, NULL);

IF NOT EXISTS (SELECT 1 FROM Pedido WHERE estado = 5 AND proveedor = 2 AND fechaCreada = '2025-01-10 09:00:00')
    INSERT INTO Pedido (estado, proveedor, puntuacion, fechaCreada, fechaEntrega, evaluacion) VALUES (5, 2, 5, '2025-01-10 09:00:00', '2025-01-12 16:30:00', 5);

IF NOT EXISTS (SELECT 1 FROM Pedido WHERE estado = 5 AND proveedor = 2 AND fechaCreada = '2025-01-18 11:00:00')
    INSERT INTO Pedido (estado, proveedor, puntuacion, fechaCreada, fechaEntrega, evaluacion) VALUES (5, 2, 4, '2025-01-18 11:00:00', '2025-01-20 10:00:00', 4);

IF NOT EXISTS (SELECT 1 FROM Pedido WHERE estado = 3 AND proveedor = 3 AND fechaCreada = '2025-01-25 08:00:00')
    INSERT INTO Pedido (estado, proveedor, puntuacion, fechaCreada, fechaEntrega, evaluacion) VALUES (3, 3, NULL, '2025-01-25 08:00:00', NULL, NULL);
GO

PRINT 'Test data for Orders loaded successfully!'
GO