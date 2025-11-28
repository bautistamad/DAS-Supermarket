-- =============================================
-- Get price history by product
-- =============================================
CREATE OR ALTER PROCEDURE sp_get_precio_history_by_product
@codigoBarra INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate product exists
    IF NOT EXISTS (SELECT 1 FROM Producto WHERE codigoBarra = @codigoBarra)
        BEGIN
            RAISERROR('Producto with codigoBarra %d does not exist.', 16, 1, @codigoBarra);
            RETURN;
        END

    SELECT
        hp.codigoBarra,
        hp.idProveedor,
        pr.nombre AS proveedorNombre,
        hp.precio,
        hp.fechaInicio,
        hp.fechaFin,
        p.nombre AS productoNombre
    FROM HistorialPrecio hp
             INNER JOIN Producto p ON hp.codigoBarra = p.codigoBarra
             INNER JOIN Proveedor pr ON hp.idProveedor = pr.id
    WHERE hp.codigoBarra = @codigoBarra
    ORDER BY hp.fechaInicio DESC;
END
GO


-- =============================================
-- Get current price by product and provider
-- =============================================
CREATE OR ALTER PROCEDURE sp_get_current_precio_by_product_provider
    @codigoBarra INT,
    @idProveedor INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate product exists
    IF NOT EXISTS (SELECT 1 FROM Producto WHERE codigoBarra = @codigoBarra)
        BEGIN
            RAISERROR('Producto with codigoBarra %d does not exist.', 16, 1, @codigoBarra);
            RETURN;
        END

    -- Validate provider exists
    IF NOT EXISTS (SELECT 1 FROM Proveedor WHERE id = @idProveedor)
        BEGIN
            RAISERROR('Proveedor with id %d does not exist.', 16, 1, @idProveedor);
            RETURN;
        END

    SELECT TOP 1
        hp.codigoBarra,
        hp.idProveedor,
        pr.nombre AS proveedorNombre,
        hp.precio,
        hp.fechaInicio,
        hp.fechaFin,
        p.nombre AS productoNombre
    FROM HistorialPrecio hp
             INNER JOIN Producto p ON hp.codigoBarra = p.codigoBarra
             INNER JOIN Proveedor pr ON hp.idProveedor = pr.id
    WHERE hp.codigoBarra = @codigoBarra
      AND hp.idProveedor = @idProveedor
      AND hp.fechaFin IS NULL
    ORDER BY hp.fechaInicio DESC;
END
GO


-- =============================================
-- Find All Products
-- =============================================
CREATE OR ALTER PROCEDURE sp_find_all_products
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
CREATE OR ALTER PROCEDURE sp_find_all_providers
AS
BEGIN
    SET NOCOUNT ON;

    SELECT p.id, p.nombre, p.apiEndpoint, p.tipoServicio, ts.nombre AS tipoServicioNombre, p.clientId, p.apiKey
    FROM Proveedor p
    LEFT JOIN TipoServicio ts ON p.tipoServicio = ts.id
    ORDER BY p.nombre;
END
go

-- =============================================
-- Find Products by Barcode
-- =============================================

CREATE OR ALTER PROCEDURE sp_find_product_by_barcode
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
-- Delete Producto by Barcode
-- =============================================
CREATE OR ALTER PROCEDURE sp_delete_product
@codigoBarra INT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM Producto
    WHERE codigoBarra = @codigoBarra;
END
GO

-- =============================================
-- Delete Proveedor by ID
-- =============================================
CREATE OR ALTER PROCEDURE sp_delete_provider
@id INT
AS
BEGIN
    SET NOCOUNT ON;

    DELETE FROM Proveedor
    WHERE id = @id;
END
go

-- =============================================
-- Find Proveedor by ID
-- =============================================
CREATE OR ALTER PROCEDURE sp_find_provider_by_id
@id INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT p.id, p.nombre, p.apiEndpoint, p.tipoServicio, ts.nombre AS tipoServicioNombre, p.clientId, p.apiKey
    FROM Proveedor p
    LEFT JOIN TipoServicio ts ON p.tipoServicio = ts.id
    WHERE p.id = @id;
END
go

-- =============================================
-- Get productos by proveedor
-- =============================================

CREATE OR ALTER PROCEDURE sp_get_products_by_provider
@idProveedor INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate that proveedor exists
    IF NOT EXISTS (SELECT 1 FROM Proveedor WHERE id = @idProveedor)
        BEGIN
            RAISERROR('Proveedor with id %d does not exist.', 16, 1, @idProveedor);
            RETURN;
        END
    -- Return all productos for this proveedor with estado information
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
             INNER JOIN Producto p ON pp.codigoBarra = p.codigoBarra -- Corregido: Asumí que la columna era codigoBarra, no codigoProducto
             LEFT JOIN EstadoProducto ep ON pp.estado = ep.id
    WHERE pp.idProveedor = @idProveedor
--       AND pp.estado = 1 Only active productos (estado = 1)
    ORDER BY p.nombre;
END
go


-- =============================================
-- Save Producto (INSERT or UPDATE)
-- =============================================
CREATE OR ALTER PROCEDURE sp_save_product
    @codigoBarra INT,
    @nombre NVARCHAR(255),
    @imagen NVARCHAR(500),
    @stockMinimo INT,
    @stockMaximo INT,
    @stockActual INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Check if producto exists
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

    -- Return the saved producto
    SELECT codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual
    FROM Producto
    WHERE codigoBarra = @codigoBarra;
END
go


-- =============================================
-- Save Proveedor (INSERT or UPDATE)
-- =============================================
CREATE OR ALTER PROCEDURE sp_save_provider
    @id INT OUTPUT,
    @nombre NVARCHAR(255),
    @apiEndpoint NVARCHAR(255),
    @tipoServicio INT,
    @clientId NVARCHAR(255),
    @apiKey NVARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;

    -- Check if proveedor exists
    IF @id IS NOT NULL AND @id > 0 AND EXISTS (SELECT 1 FROM Proveedor WHERE id = @id)
        BEGIN
            -- UPDATE
            UPDATE Proveedor
            SET nombre = @nombre,
                apiEndpoint = @apiEndpoint,
                tipoServicio = @tipoServicio,
                clientId = @clientId,
                apiKey = @apiKey
            WHERE id = @id;
        END
    ELSE
        BEGIN
            -- INSERT
            INSERT INTO Proveedor (nombre, apiEndpoint, tipoServicio, clientId, apiKey)
            VALUES (@nombre, @apiEndpoint, @tipoServicio, @clientId, @apiKey);

            SET @id = SCOPE_IDENTITY();
        END

    -- Return the saved proveedor
    SELECT p.id, p.nombre, p.apiEndpoint, p.tipoServicio, ts.nombre AS tipoServicioNombre, p.clientId, p.apiKey
    FROM Proveedor p
    LEFT JOIN TipoServicio ts ON p.tipoServicio = ts.id
    WHERE p.id = @id;
END
go

-- =============================================
-- Assign producto to proveedor
-- =============================================
CREATE OR ALTER PROCEDURE sp_assign_product_to_provider
    @idProveedor INT,
    @codigoProducto INT,
    @codigoBarraProveedor INT,
    @estado INT = 1
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate that proveedor exists
    IF NOT EXISTS (SELECT 1 FROM Proveedor WHERE id = @idProveedor)
        BEGIN
            RAISERROR('Proveedor with id %d does not exist.', 16, 1, @idProveedor);
            RETURN;
        END

    -- Validate that producto exists
    IF NOT EXISTS (SELECT 1 FROM Producto WHERE codigoBarra = @codigoProducto)
        BEGIN
            RAISERROR('Producto with barcode %d does not exist.', 16, 1, @codigoProducto);
            RETURN;
        END

    -- Check if relationship already exists
    IF EXISTS (SELECT 1 FROM ProductoProveedor
               WHERE idProveedor = @idProveedor
                 AND codigoBarra = @codigoProducto)
        BEGIN
            -- UPDATE existing relationship
            UPDATE ProductoProveedor
            SET fechaActualizacion = GETDATE(),
                estado = @estado,
                codigoBarraProveedor = @codigoBarraProveedor
            WHERE idProveedor = @idProveedor
              AND codigoBarra = @codigoProducto;

            PRINT 'Relationship updated.';
        END
    ELSE
        BEGIN
            -- INSERT new relationship
            INSERT INTO ProductoProveedor (idProveedor, codigoBarra, codigoBarraProveedor, fechaActualizacion, estado)
            VALUES (@idProveedor, @codigoProducto, @codigoBarraProveedor, GETDATE(), @estado);

            PRINT 'Relationship created.';
        END

    -- Return the relationship with details
    SELECT pp.idProveedor, pp.codigoBarra, pp.codigoBarraProveedor, pp.fechaActualizacion, pp.estado,
           p.nombre AS productoNombre,
           pr.nombre AS proveedorNombre
    FROM ProductoProveedor pp
             INNER JOIN Producto p ON pp.codigoBarra = p.codigoBarra
             INNER JOIN Proveedor pr ON pp.idProveedor = pr.id
    WHERE pp.idProveedor = @idProveedor
      AND pp.codigoBarra = @codigoProducto;
END
go

-- =============================================
-- Create pedido
-- =============================================
CREATE OR ALTER PROCEDURE sp_create_pedido
    @estado SMALLINT,
    @proveedor SMALLINT,
    @fechaEntrega DATETIME = NULL,
    @evaluacionEscala SMALLINT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate proveedor exists
    IF NOT EXISTS (SELECT 1 FROM Proveedor WHERE id = @proveedor)
        BEGIN
            RAISERROR('Proveedor with id %d does not exist.', 16, 1, @proveedor);
            RETURN;
        END

    -- Validate estado exists
    IF NOT EXISTS (SELECT 1 FROM EstadoPedido WHERE id = @estado)
        BEGIN
            RAISERROR('EstadoPedido with id %d does not exist.', 16, 1, @estado);
            RETURN;
        END

    DECLARE @newId INT; -- Cambiado de SMALLINT a INT para coincidir con Pedido.id (IDENTITY)

    INSERT INTO Pedido (estado, proveedor, fechaEntrega, evaluacionEscala)
    VALUES (@estado, @proveedor, @fechaEntrega, @evaluacionEscala);

    SET @newId = SCOPE_IDENTITY();

    -- Return the created pedido with estado and proveedor info
    SELECT
        p.id,
        p.estado,
        ep.nombre AS estadoNombre,
        ep.descripcion AS estadoDescripcion,
        p.proveedor,
        pr.nombre AS proveedorNombre,
        p.fechaEstimada,
        p.fechaEntrega,
        p.fechaRegistro,
        p.evaluacionEscala
    FROM Pedido p
             INNER JOIN EstadoPedido ep ON p.estado = ep.id
             INNER JOIN Proveedor pr ON p.proveedor = pr.id
    WHERE p.id = @newId;
END
GO


-- =============================================
-- Return pedido by id WITH products
-- =============================================
CREATE OR ALTER PROCEDURE sp_find_pedido_by_id
@id INT -- Cambiado de SMALLINT a INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        p.id,
        p.estado,
        ep.nombre AS estadoNombre,
        ep.descripcion AS estadoDescripcion,
        p.proveedor,
        pr.nombre AS proveedorNombre,
        p.fechaEstimada,
        p.fechaEntrega,
        p.fechaRegistro,
        p.evaluacionEscala,
        -- Product information from PedidoProducto
        pp.codigoBarra,
        pp.cantidad,
        prod.nombre AS productoNombre,
        prod.imagen AS productoImagen
    FROM Pedido p
             INNER JOIN EstadoPedido ep ON p.estado = ep.id
             INNER JOIN Proveedor pr ON p.proveedor = pr.id
             LEFT JOIN PedidoProducto pp ON p.id = pp.idPedido
             LEFT JOIN Producto prod ON pp.codigoBarra = prod.codigoBarra
    WHERE p.id = @id;
END
GO


-- =============================================
-- Find all pedidos WITH products
-- =============================================
CREATE OR ALTER PROCEDURE sp_find_all_pedidos
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        p.id,
        p.estado,
        ep.nombre AS estadoNombre,
        ep.descripcion AS estadoDescripcion,
        p.proveedor,
        pr.nombre AS proveedorNombre,
        p.fechaEstimada,
        p.fechaEntrega,
        p.fechaRegistro,
        p.evaluacionEscala,
        -- Product information from PedidoProducto
        pp.codigoBarra,
        pp.cantidad,
        prod.nombre AS productoNombre,
        prod.imagen AS productoImagen
    FROM Pedido p
             INNER JOIN EstadoPedido ep ON p.estado = ep.id
             INNER JOIN Proveedor pr ON p.proveedor = pr.id
             LEFT JOIN PedidoProducto pp ON p.id = pp.idPedido
             LEFT JOIN Producto prod ON pp.codigoBarra = prod.codigoBarra
    ORDER BY p.fechaEstimada DESC, pp.codigoBarra;
END
GO


-- =============================================
-- Update pedido
-- =============================================
CREATE OR ALTER PROCEDURE sp_update_pedido
    @id INT, -- Cambiado de SMALLINT a INT
    @estado INT, -- Cambiado de SMALLINT a INT
    @fechaEntrega DATETIME = NULL,
    @evaluacionEscala SMALLINT = NULL
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate pedido exists
    IF NOT EXISTS (SELECT 1 FROM Pedido WHERE id = @id)
        BEGIN
            RAISERROR('Pedido with id %d does not exist.', 16, 1, @id);
            RETURN;
        END

    -- Validate estado exists
    IF NOT EXISTS (SELECT 1 FROM EstadoPedido WHERE id = @estado)
        BEGIN
            RAISERROR('EstadoPedido with id %d does not exist.', 16, 1, @estado);
            RETURN;
        END

    UPDATE Pedido
    SET estado = @estado,
        fechaEntrega = @fechaEntrega,
        evaluacionEscala = @evaluacionEscala
    WHERE id = @id;

-- Return the updated pedido
    SELECT
        p.id,
        p.estado,
        ep.nombre AS estadoNombre,
        ep.descripcion AS estadoDescripcion,
        p.proveedor,
        pr.nombre AS proveedorNombre,
        p.fechaEstimada,
        p.fechaEntrega,
        p.fechaRegistro,
        p.evaluacionEscala
    FROM Pedido p
             INNER JOIN EstadoPedido ep ON p.estado = ep.id
             INNER JOIN Proveedor pr ON p.proveedor = pr.id
    WHERE p.id = @id;
END
GO


-- =============================================
-- Delete pedido
-- =============================================
CREATE OR ALTER PROCEDURE sp_delete_pedido
@id INT -- Cambiado de SMALLINT a INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate pedido exists
    IF NOT EXISTS (SELECT 1 FROM Pedido WHERE id = @id)
        BEGIN
            RAISERROR('Pedido with id %d does not exist.', 16, 1, @id);
            RETURN;
        END

    DELETE FROM Pedido WHERE id = @id;
END
GO



-- =============================================
-- Find pedidos by proveedor
-- =============================================
CREATE OR ALTER PROCEDURE sp_find_pedidos_by_proveedor
@proveedorId INT -- Cambiado de SMALLINT a INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate proveedor exists
    IF NOT EXISTS (SELECT 1 FROM Proveedor WHERE id = @proveedorId)
        BEGIN
            RAISERROR('Proveedor with id %d does not exist.', 16, 1, @proveedorId);
            RETURN;
        END

    SELECT
        p.id,
        p.estado,
        ep.nombre AS estadoNombre,
        ep.descripcion AS estadoDescripcion,
        p.proveedor,
        pr.nombre AS proveedorNombre,
        p.fechaEstimada,
        p.fechaEntrega,
        p.fechaRegistro,
        p.evaluacionEscala
    FROM Pedido p
             INNER JOIN EstadoPedido ep ON p.estado = ep.id
             INNER JOIN Proveedor pr ON p.proveedor = pr.id
    WHERE p.proveedor = @proveedorId
    ORDER BY p.fechaEstimada DESC;
END
GO


-- =============================================
-- Get products by pedido
-- =============================================
CREATE OR ALTER PROCEDURE sp_get_products_by_pedido
@idPedido INT -- Cambiado de SMALLINT a INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate pedido exists
    IF NOT EXISTS (SELECT 1 FROM Pedido WHERE id = @idPedido)
        BEGIN
            RAISERROR('Pedido with id %d does not exist.', 16, 1, @idPedido);
            RETURN;
        END

    SELECT
        pp.idPedido,
        pp.codigoBarra,
        pp.cantidad,
        p.nombre AS productoNombre,
        p.imagen AS productoImagen
    FROM PedidoProducto pp
             INNER JOIN Producto p ON pp.codigoBarra = p.codigoBarra
    WHERE pp.idPedido = @idPedido
    ORDER BY p.nombre;
END
GO