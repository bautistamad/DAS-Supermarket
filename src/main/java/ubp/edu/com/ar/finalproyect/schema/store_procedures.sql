-- =============================================
-- Find All Products
-- =============================================
CREATE PROCEDURE sp_find_all_products
    AS
BEGIN
    SET NOCOUNT ON;

SELECT codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual
FROM Producto
ORDER BY nombre;
END
go

-- =============================================
-- Find All Providers
-- =============================================
CREATE PROCEDURE sp_find_all_providers
    AS
BEGIN
    SET NOCOUNT ON;

SELECT id, nombre, servicio, tipoServicio, escala
FROM Proveedor
ORDER BY nombre;
END
go

-- =============================================
-- Find Products by Barcode
-- =============================================

CREATE PROCEDURE sp_find_product_by_barcode
    @codigoBarra INT
AS
BEGIN
    SET NOCOUNT ON;

SELECT codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual
FROM Producto
WHERE codigoBarra = @codigoBarra;
END
go

-- =============================================
-- Delete Product by Barcode
-- =============================================
CREATE PROCEDURE sp_delete_product
    @codigoBarra INT
AS
BEGIN
    SET NOCOUNT ON;

DELETE FROM Producto
WHERE codigoBarra = @codigoBarra;
END

-- =============================================
-- Delete Provider by ID
-- =============================================
CREATE PROCEDURE sp_delete_provider
    @id INT
AS
BEGIN
    SET NOCOUNT ON;

DELETE FROM Proveedor
WHERE id = @id;
END
go

-- =============================================
-- Find Provider by ID
-- =============================================
CREATE PROCEDURE sp_find_provider_by_id
    @id INT
AS
BEGIN
    SET NOCOUNT ON;

SELECT id, nombre, servicio, tipoServicio, escala
FROM Proveedor
WHERE id = @id;
END
go

-- =============================================
-- Get products by provider
-- =============================================

CREATE   PROCEDURE sp_get_products_by_provider
    @idProveedor INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate that provider exists
    IF NOT EXISTS (SELECT 1 FROM Proveedor WHERE id = @idProveedor)
BEGIN
            RAISERROR('Provider with id %d does not exist.', 16, 1, @idProveedor);
            RETURN;
END
    -- Return all products for this provider with estado information
SELECT
    p.codigoBarra,
    p.nombre,
    p.imagen,
    p.stockMinimo,
    p.stockMaximo,
    p.stockActual,
    pp.fechaActualizacion,
    pp.estado,
    ep.nombre AS estadoNombre,
    ep.descripcion AS estadoDescripcion
FROM ProductoProveedor pp
         INNER JOIN Producto p ON pp.codigoProducto = p.codigoBarra
         LEFT JOIN EstadoProducto ep ON pp.estado = ep.id
WHERE pp.idProveedor = @idProveedor
--       AND pp.estado = 1 Only active products (estado = 1)
ORDER BY p.nombre;
END
go


-- =============================================
-- Save Product (INSERT or UPDATE)
-- =============================================
CREATE PROCEDURE sp_save_product
    @codigoBarra INT,
    @nombre NVARCHAR(255),
    @imagen NVARCHAR(500),
    @stockMinimo INT,
    @stockMaximo INT,
    @stockActual INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Check if product exists
    IF EXISTS (SELECT 1 FROM Producto WHERE codigoBarra = @codigoBarra)
BEGIN
            -- UPDATE
UPDATE Producto
SET nombre = @nombre,
    imagen = @imagen,
    stockMinimo = @stockMinimo,
    stockMaximo = @stockMaximo,
    stockActual = @stockActual
WHERE codigoBarra = @codigoBarra;
END
ELSE
BEGIN
            -- INSERT
INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual)
VALUES (@codigoBarra, @nombre, @imagen, @stockMinimo, @stockMaximo, @stockActual);
END

    -- Return the saved product
SELECT codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual
FROM Producto
WHERE codigoBarra = @codigoBarra;
END
go


-- =============================================
-- Save Provider (INSERT or UPDATE)
-- =============================================
CREATE PROCEDURE sp_save_provider
    @id INT OUTPUT,
    @nombre NVARCHAR(255),
    @servicio NVARCHAR(255),
    @tipoServicio NVARCHAR(100),
    @escala NVARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;

    -- Check if provider exists
    IF @id IS NOT NULL AND @id > 0 AND EXISTS (SELECT 1 FROM Proveedor WHERE id = @id)
BEGIN
            -- UPDATE
UPDATE Proveedor
SET nombre = @nombre,
    servicio = @servicio,
    tipoServicio = @tipoServicio,
    escala = @escala
WHERE id = @id;
END
ELSE
BEGIN
            -- INSERT
INSERT INTO Proveedor (nombre, servicio, tipoServicio, escala)
VALUES (@nombre, @servicio, @tipoServicio, @escala);

SET @id = SCOPE_IDENTITY();
END

    -- Return the saved provider
SELECT id, nombre, servicio, tipoServicio, escala
FROM Proveedor
WHERE id = @id;
END
go

-- =============================================
-- Assign product to provider
-- =============================================
CREATE PROCEDURE sp_assign_product_to_provider
    @idProveedor INT,
        @codigoProducto INT,
        @estado INT = 1
    AS
BEGIN
        SET NOCOUNT ON;

        -- Validate that provider exists
        IF NOT EXISTS (SELECT 1 FROM Proveedor WHERE id = @idProveedor)
BEGIN
                RAISERROR('Provider with id %d does not exist.', 16, 1, @idProveedor);
                RETURN;
END

        -- Validate that product exists
        IF NOT EXISTS (SELECT 1 FROM Producto WHERE codigoBarra = @codigoProducto)
BEGIN
                RAISERROR('Product with barcode %d does not exist.', 16, 1, @codigoProducto);
                RETURN;
END

        -- Check if relationship already exists
        IF EXISTS (SELECT 1 FROM ProductoProveedor
                   WHERE idProveedor = @idProveedor
                     AND codigoProducto = @codigoProducto)
BEGIN
                -- UPDATE existing relationship
UPDATE ProductoProveedor
SET fechaActualizacion = GETDATE(),
    estado = @estado
WHERE idProveedor = @idProveedor
  AND codigoProducto = @codigoProducto;

PRINT 'Relationship updated.';
END
ELSE
BEGIN
                -- INSERT new relationship
INSERT INTO ProductoProveedor (idProveedor, codigoProducto, fechaActualizacion, estado)
VALUES (@idProveedor, @codigoProducto, GETDATE(), @estado);

PRINT 'Relationship created.';
END

        -- Return the relationship with details
SELECT pp.idProveedor, pp.codigoProducto, pp.fechaActualizacion, pp.estado,
       p.nombre AS productoNombre,
       pr.nombre AS proveedorNombre
FROM ProductoProveedor pp
         INNER JOIN Producto p ON pp.codigoProducto = p.codigoBarra
         INNER JOIN Proveedor pr ON pp.idProveedor = pr.id
WHERE pp.idProveedor = @idProveedor
  AND pp.codigoProducto = @codigoProducto;
END
go







