-- =============================================
-- VERIFICATION SCRIPT
-- =============================================
-- Run this script to verify base catalog data exists
-- =============================================

PRINT '========================================';
PRINT 'DATABASE VERIFICATION REPORT';
PRINT '========================================';
PRINT '';

-- Check TipoServicio
PRINT '1. TipoServicio (Service Types):';
PRINT '------------------------------------';
IF EXISTS (SELECT 1 FROM TipoServicio)
BEGIN
    SELECT id, nombre FROM TipoServicio;
    PRINT '';
END
ELSE
BEGIN
    PRINT '⚠️  WARNING: TipoServicio table is EMPTY!';
    PRINT '';
END

-- Check EstadoProducto
PRINT '2. EstadoProducto (Product States):';
PRINT '------------------------------------';
IF EXISTS (SELECT 1 FROM EstadoProducto)
BEGIN
    SELECT id, nombre, descripcion FROM EstadoProducto;
    PRINT '';
END
ELSE
BEGIN
    PRINT '⚠️  WARNING: EstadoProducto table is EMPTY!';
    PRINT '';
END

-- Check EstadoPedido
PRINT '3. EstadoPedido (Order States):';
PRINT '------------------------------------';
IF EXISTS (SELECT 1 FROM EstadoPedido)
BEGIN
    SELECT id, nombre, descripcion FROM EstadoPedido;
    PRINT '';
END
ELSE
BEGIN
    PRINT '⚠️  WARNING: EstadoPedido table is EMPTY!';
    PRINT '';
END

-- Summary counts
PRINT '========================================';
PRINT 'SUMMARY:';
PRINT '------------------------------------';
SELECT 'TipoServicio' AS Tabla, COUNT(*) AS Total FROM TipoServicio
UNION ALL
SELECT 'EstadoProducto', COUNT(*) FROM EstadoProducto
UNION ALL
SELECT 'EstadoPedido', COUNT(*) FROM EstadoPedido
UNION ALL
SELECT 'Proveedor', COUNT(*) FROM Proveedor
UNION ALL
SELECT 'Producto', COUNT(*) FROM Producto
UNION ALL
SELECT 'Pedido', COUNT(*) FROM Pedido;

PRINT '';
PRINT '========================================';
PRINT 'END OF VERIFICATION REPORT';
PRINT '========================================';
