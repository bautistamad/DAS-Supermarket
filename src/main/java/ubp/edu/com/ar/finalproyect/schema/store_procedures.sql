-- =============================================
-- Add idPedidoProveedor column if it doesn't exist
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Pedido') AND name = 'idPedidoProveedor')
BEGIN
    ALTER TABLE Pedido ADD idPedidoProveedor INT NULL;
    PRINT 'Column idPedidoProveedor added to Pedido table successfully.';
END
GO

-- =============================================
-- Add ratingPromedio column to Proveedor table
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Proveedor') AND name = 'ratingPromedio')
BEGIN
    ALTER TABLE Proveedor ADD ratingPromedio DECIMAL(3,2) NULL;
    PRINT 'Column ratingPromedio added to Proveedor table successfully.';
END
GO

-- =============================================
-- Add activo column to Proveedor table
-- Default is 0 (inactive) - must be enabled by user
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.columns WHERE object_id = OBJECT_ID('Proveedor') AND name = 'activo')
BEGIN
    ALTER TABLE Proveedor ADD activo BIT DEFAULT 0;
    PRINT 'Column activo added to Proveedor table successfully.';
END
GO

-- =============================================
-- Trigger to update provider rating when order is evaluated
-- =============================================
CREATE OR ALTER TRIGGER trg_update_proveedor_rating
ON Pedido
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Only process if evaluacionEscala was updated
    IF UPDATE(evaluacionEscala)
    BEGIN
        -- Update rating for affected providers
        UPDATE Proveedor
        SET ratingPromedio = (
            SELECT CAST(AVG(CAST(e.escalaInt AS DECIMAL(5,2))) AS DECIMAL(3,2))
            FROM Pedido p
            INNER JOIN Escala e ON p.evaluacionEscala = e.idEscala
            WHERE p.proveedor = Proveedor.id
              AND p.estado = 4  -- Only delivered orders
              AND p.evaluacionEscala IS NOT NULL
              AND e.escalaInt IS NOT NULL
        )
        WHERE id IN (SELECT DISTINCT proveedor FROM inserted);
    END
END
GO

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

    SELECT
        p.codigoBarra,
        p.nombre,
        p.imagen,
        p.stockMinimo,
        p.stockMaximo,
        p.stockActual,
        p.estado,
        ep.nombre AS estadoNombre,
        ep.descripcion AS estadoDescripcion
    FROM Producto p
    LEFT JOIN EstadoProducto ep ON p.estado = ep.id
    ORDER BY p.nombre;
END
go

-- =============================================
-- Find All Providers
-- =============================================
CREATE OR ALTER PROCEDURE sp_find_all_providers
AS
BEGIN
    SET NOCOUNT ON;

    SELECT p.id, p.nombre, p.apiEndpoint, p.tipoServicio, ts.nombre AS tipoServicioNombre, p.clientId, p.apiKey, p.ratingPromedio, p.activo
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

    SELECT p.codigoBarra, p.nombre, p.imagen, p.stockMinimo, p.stockMaximo, p.stockActual, p.estado, ep.nombre AS estadoNombre, ep.descripcion AS estadoDescripcion
    FROM Producto p
    LEFT JOIN EstadoProducto ep ON p.estado = ep.id
    WHERE p.codigoBarra = @codigoBarra;
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

BEGIN TRANSACTION;
BEGIN TRY
DELETE FROM ProductoProveedor
WHERE codigoBarra = @codigoBarra;

DELETE FROM Producto
WHERE codigoBarra = @codigoBarra;

COMMIT TRANSACTION;
END TRY
BEGIN CATCH
ROLLBACK TRANSACTION;
        THROW; -- Re-lanza el error original
END CATCH
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

    IF EXISTS (SELECT 1 FROM Pedido
               WHERE proveedor = @id
               AND estado IN (1, 2, 3))
BEGIN
        RAISERROR('No se puede eliminar: El proveedor tiene pedidos en curso (no finalizados).', 16, 1);
        RETURN;
    END

    BEGIN TRANSACTION;
    BEGIN TRY

    DELETE FROM ProductoProveedor
    WHERE idProveedor = @id;


    DELETE FROM Pedido
    WHERE proveedor = @id;


    DELETE FROM Proveedor
    WHERE id = @id;

    COMMIT TRANSACTION;
    PRINT 'Proveedor y todo su historial de pedidos han sido eliminados permanentemente.';

    END TRY
    BEGIN CATCH
    ROLLBACK TRANSACTION;
            -- Relanzar el error para que la aplicación lo detecte
            THROW;
    END CATCH
END
GO

-- =============================================
-- Find Proveedor by ID
-- =============================================
CREATE OR ALTER PROCEDURE sp_find_provider_by_id
@id INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT p.id, p.nombre, p.apiEndpoint, p.tipoServicio, ts.nombre AS tipoServicioNombre, p.clientId, p.apiKey, p.ratingPromedio, p.activo
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
        p.estado,
        ep.nombre AS estadoNombre,
        ep.descripcion AS estadoDescripcion
    FROM ProductoProveedor pp
             INNER JOIN Producto p ON pp.codigoBarra = p.codigoBarra
             LEFT JOIN EstadoProducto ep ON p.estado = ep.id
    WHERE pp.idProveedor = @idProveedor
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
            INSERT INTO Producto (codigoBarra, nombre, imagen, stockMinimo, stockMaximo, stockActual, estado)
            VALUES (@codigoBarra, @nombre, @imagen, @stockMinimo, @stockMaximo, @stockActual,1);
        END

    -- Return the saved producto
    SELECT p.codigoBarra, p.nombre, p.imagen, p.stockMinimo, p.stockMaximo, p.stockActual, p.estado, ep.nombre AS estadoNombre, ep.descripcion AS estadoDescripcion
    FROM Producto p
    LEFT JOIN EstadoProducto ep ON p.estado = ep.id
    WHERE p.codigoBarra = @codigoBarra;
END
go

-- =============================================
-- Update Producto
-- =============================================
CREATE OR ALTER PROCEDURE sp_update_product
    @codigoBarra INT,
    @nombre NVARCHAR(255),
    @imagen NVARCHAR(500),
    @stockMinimo INT,
    @stockMaximo INT,
    @stockActual INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate producto exists
    IF NOT EXISTS (SELECT 1 FROM Producto WHERE codigoBarra = @codigoBarra)
        BEGIN
            RAISERROR('Producto with codigoBarra %d does not exist.', 16, 1, @codigoBarra);
            RETURN;
        END

    -- Validate stock constraints
    IF @stockMinimo >= @stockMaximo
        BEGIN
            RAISERROR('stockMinimo (%d) must be less than stockMaximo (%d).', 16, 1, @stockMinimo, @stockMaximo);
            RETURN;
        END

    -- UPDATE producto
    UPDATE Producto
    SET nombre = @nombre,
        imagen = @imagen,
        stockMinimo = @stockMinimo,
        stockMaximo = @stockMaximo,
        stockActual = @stockActual
    WHERE codigoBarra = @codigoBarra;

    -- Return the updated producto
    SELECT p.codigoBarra, p.nombre, p.imagen, p.stockMinimo, p.stockMaximo, p.stockActual, p.estado, ep.nombre AS estadoNombre, ep.descripcion AS estadoDescripcion
    FROM Producto p
    LEFT JOIN EstadoProducto ep ON p.estado = ep.id
    WHERE p.codigoBarra = @codigoBarra;
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
    @apiKey NVARCHAR(255),
    @activo BIT = NULL
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
                apiKey = @apiKey,
                activo = CASE WHEN @activo IS NOT NULL THEN @activo ELSE activo END
            WHERE id = @id;
        END
    ELSE
        BEGIN
            -- INSERT
            INSERT INTO Proveedor (nombre, apiEndpoint, tipoServicio, clientId, apiKey, activo)
            VALUES (@nombre, @apiEndpoint, @tipoServicio, @clientId, @apiKey, COALESCE(@activo, 0));

            SET @id = SCOPE_IDENTITY();
        END

    -- Return the saved proveedor
    SELECT p.id, p.nombre, p.apiEndpoint, p.tipoServicio, ts.nombre AS tipoServicioNombre, p.clientId, p.apiKey, p.ratingPromedio, p.activo
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
    @codigoBarraProveedor INT
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
                codigoBarraProveedor = @codigoBarraProveedor
            WHERE idProveedor = @idProveedor
              AND codigoBarra = @codigoProducto;

            PRINT 'Relationship updated.';
        END
    ELSE
        BEGIN
            -- INSERT new relationship
            INSERT INTO ProductoProveedor (idProveedor, codigoBarra, codigoBarraProveedor, fechaActualizacion)
            VALUES (@idProveedor, @codigoProducto, @codigoBarraProveedor, GETDATE());

            PRINT 'Relationship created.';
        END

    -- Return the relationship with details
    SELECT pp.idProveedor, pp.codigoBarra, pp.codigoBarraProveedor, pp.fechaActualizacion,
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
-- Unassign producto from proveedor
-- =============================================
CREATE OR ALTER PROCEDURE sp_unassign_product_from_provider
    @idProveedor INT,
    @codigoProducto INT
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

    -- Check if relationship exists
    IF NOT EXISTS (SELECT 1 FROM ProductoProveedor
                   WHERE idProveedor = @idProveedor
                     AND codigoBarra = @codigoProducto)
        BEGIN
            RAISERROR('Product-Provider relationship does not exist.', 16, 1);
            RETURN;
        END

    -- Delete the relationship
    DELETE FROM ProductoProveedor
    WHERE idProveedor = @idProveedor
      AND codigoBarra = @codigoProducto;

    PRINT 'Relationship deleted.';
END
go

-- =============================================
-- Create pedido
-- =============================================
CREATE OR ALTER PROCEDURE sp_create_pedido
    @estado SMALLINT,
    @proveedor SMALLINT,
    @fechaEstimada DATETIME = NULL,
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

    INSERT INTO Pedido (estado, proveedor, fechaEstimada, fechaEntrega, evaluacionEscala)
    VALUES (@estado, @proveedor, @fechaEstimada, @fechaEntrega, @evaluacionEscala);

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
        p.idPedidoProveedor,
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
        p.idPedidoProveedor,
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
    @idPedidoProveedor INT = NULL,
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
        idPedidoProveedor = CASE
            WHEN @idPedidoProveedor IS NOT NULL THEN @idPedidoProveedor
            ELSE idPedidoProveedor
        END,
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
        p.idPedidoProveedor,
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
-- Sync precio from external provider
-- =============================================
CREATE OR ALTER PROCEDURE sp_sync_precio_from_proveedor
    @codigoBarra INT,
    @precio FLOAT,
    @idProveedor INT
AS
BEGIN
    SET NOCOUNT ON;

    DECLARE @now DATETIME = GETDATE();
    DECLARE @precioActual FLOAT;
    DECLARE @action NVARCHAR(50);

    BEGIN TRANSACTION;
    BEGIN TRY

        -- Validate product exists
        IF NOT EXISTS (SELECT 1 FROM Producto WHERE codigoBarra = @codigoBarra)
        BEGIN
            ROLLBACK TRANSACTION;
            SELECT
                @codigoBarra AS codigoBarra,
                'ERROR' AS status,
                'PRODUCT_NOT_FOUND' AS action,
                'Producto no existe' AS errorMessage;
            RETURN;
        END

        -- Get current price for this product-provider combination
        SELECT @precioActual = precio
        FROM HistorialPrecio
        WHERE codigoBarra = @codigoBarra
          AND idProveedor = @idProveedor
          AND fechaFin IS NULL;

        -- Check if price changed or doesn't exist
        IF @precioActual IS NULL OR @precioActual != @precio
        BEGIN
            -- Close previous price if exists
            IF @precioActual IS NOT NULL
            BEGIN
                UPDATE HistorialPrecio
                SET fechaFin = @now
                WHERE codigoBarra = @codigoBarra
                  AND idProveedor = @idProveedor
                  AND fechaFin IS NULL;

                SET @action = 'PRICE_UPDATED';
            END
            ELSE
            BEGIN
                SET @action = 'PRICE_CREATED';
            END

            -- Insert new price record
            INSERT INTO HistorialPrecio (codigoBarra, idProveedor, precio, fechaInicio, fechaFin)
            VALUES (@codigoBarra, @idProveedor, @precio, @now, NULL);

            COMMIT TRANSACTION;

            SELECT
                @codigoBarra AS codigoBarra,
                'SUCCESS' AS status,
                @action AS action,
                @precioActual AS precioAnterior,
                @precio AS precioNuevo;
        END
        ELSE
        BEGIN
            -- Price unchanged
            COMMIT TRANSACTION;

            SELECT
                @codigoBarra AS codigoBarra,
                'SUCCESS' AS status,
                'PRICE_UNCHANGED' AS action,
                @precio AS precioActual;
        END

    END TRY
    BEGIN CATCH
        ROLLBACK TRANSACTION;

        SELECT
            @codigoBarra AS codigoBarra,
            'ERROR' AS status,
            'EXCEPTION' AS action,
            ERROR_MESSAGE() AS errorMessage;
    END CATCH
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
        p.imagen AS productoImagen,
        prodprov.codigoBarraProveedor
    FROM PedidoProducto pp
             INNER JOIN Producto p ON pp.codigoBarra = p.codigoBarra
             INNER JOIN Pedido ped ON pp.idPedido = ped.id
             LEFT JOIN ProductoProveedor prodprov ON pp.codigoBarra = prodprov.codigoBarra
                AND ped.proveedor = prodprov.idProveedor
    WHERE pp.idPedido = @idPedido
    ORDER BY p.nombre;
END
GO


-- =============================================
-- Add product to pedido
-- =============================================
CREATE OR ALTER PROCEDURE sp_add_product_to_pedido
    @idPedido INT,
    @codigoBarra INT,
    @cantidad INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate pedido exists
    IF NOT EXISTS (SELECT 1 FROM Pedido WHERE id = @idPedido)
        BEGIN
            RAISERROR('Pedido with id %d does not exist.', 16, 1, @idPedido);
            RETURN;
        END

    -- Validate producto exists
    IF NOT EXISTS (SELECT 1 FROM Producto WHERE codigoBarra = @codigoBarra)
        BEGIN
            RAISERROR('Producto with codigoBarra %d does not exist.', 16, 1, @codigoBarra);
            RETURN;
        END

    -- Validate cantidad
    IF @cantidad <= 0
        BEGIN
            RAISERROR('Cantidad must be greater than 0, got: %d', 16, 1, @cantidad);
            RETURN;
        END

    -- Check if product already exists in pedido
    IF EXISTS (SELECT 1 FROM PedidoProducto WHERE idPedido = @idPedido AND codigoBarra = @codigoBarra)
        BEGIN
            -- Update cantidad
            UPDATE PedidoProducto
            SET cantidad = @cantidad
            WHERE idPedido = @idPedido AND codigoBarra = @codigoBarra;
        END
    ELSE
        BEGIN
            -- Insert new product
            INSERT INTO PedidoProducto (idPedido, codigoBarra, cantidad)
            VALUES (@idPedido, @codigoBarra, @cantidad);
        END

    -- Return the product
    SELECT
        pp.idPedido,
        pp.codigoBarra,
        pp.cantidad,
        p.nombre AS productoNombre,
        p.imagen AS productoImagen
    FROM PedidoProducto pp
             INNER JOIN Producto p ON pp.codigoBarra = p.codigoBarra
    WHERE pp.idPedido = @idPedido AND pp.codigoBarra = @codigoBarra;
END
GO


-- =============================================
-- ESCALA (Rating Scale) Stored Procedures - SIMPLIFIED
-- =============================================

-- =============================================
-- sp_save_escala - Create or update scale mapping
-- Allows NULL escalaInt for unmapped scales from provider
-- =============================================
CREATE OR ALTER PROCEDURE sp_save_escala
    @idEscala INT OUTPUT,
    @idProveedor INT,
    @escalaInt SMALLINT = NULL,
    @escalaExt VARCHAR(50),
    @descripcionExt VARCHAR(255) = NULL
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate proveedor exists
    IF NOT EXISTS (SELECT 1 FROM Proveedor WHERE id = @idProveedor)
        BEGIN
            RAISERROR('Proveedor with id %d does not exist.', 16, 1, @idProveedor);
            RETURN;
        END

    -- Validate internal scale range (only if not NULL)
    IF @escalaInt IS NOT NULL AND (@escalaInt < 1 OR @escalaInt > 5)
        BEGIN
            RAISERROR('Internal scale must be between 1 and 5, got: %d', 16, 1, @escalaInt);
            RETURN;
        END

    -- Check if mapping exists
    IF @idEscala IS NOT NULL AND @idEscala > 0 AND EXISTS (SELECT 1 FROM Escala WHERE idEscala = @idEscala)
        BEGIN
            -- UPDATE
            UPDATE Escala
            SET idProveedor = @idProveedor,
                escalaInt = @escalaInt,
                escalaExt = @escalaExt,
                descripcionExt = @descripcionExt
            WHERE idEscala = @idEscala;
        END
    ELSE
        BEGIN
            -- INSERT
            INSERT INTO Escala (idProveedor, escalaInt, escalaExt, descripcionExt)
            VALUES (@idProveedor, @escalaInt, @escalaExt, @descripcionExt);

            SET @idEscala = SCOPE_IDENTITY();
        END

    -- Return the saved escala
    SELECT idEscala, idProveedor, escalaInt, escalaExt, descripcionExt
    FROM Escala
    WHERE idEscala = @idEscala;
END
GO

-- =============================================
-- sp_find_escalas_by_proveedor - Get all mappings for a provider
-- =============================================
CREATE OR ALTER PROCEDURE sp_find_escalas_by_proveedor
    @idProveedor INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate proveedor exists
    IF NOT EXISTS (SELECT 1 FROM Proveedor WHERE id = @idProveedor)
        BEGIN
            RAISERROR('Proveedor with id %d does not exist.', 16, 1, @idProveedor);
            RETURN;
        END

    SELECT idEscala, idProveedor, escalaInt, escalaExt, descripcionExt
    FROM Escala
    WHERE idProveedor = @idProveedor
    ORDER BY escalaInt ASC;
END
GO

-- =============================================
-- sp_find_escala_by_internal - Find external value from internal (1-5)
-- Used when sending ratings TO provider (convert 5 -> "Excelente")
-- =============================================
CREATE OR ALTER PROCEDURE sp_find_escala_by_internal
    @idProveedor INT,
    @escalaInt SMALLINT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT TOP 1 idEscala, idProveedor, escalaInt, escalaExt, descripcionExt
    FROM Escala
    WHERE idProveedor = @idProveedor
      AND escalaInt = @escalaInt
    ORDER BY idEscala ASC;
END
GO

-- =============================================
-- sp_update_pedido_evaluacion - Update order rating with validation
-- =============================================
CREATE OR ALTER PROCEDURE sp_update_pedido_evaluacion
    @idPedido INT,
    @idEscala INT
AS
BEGIN
    SET NOCOUNT ON;

    -- Validate pedido exists
    IF NOT EXISTS (SELECT 1 FROM Pedido WHERE id = @idPedido)
        BEGIN
            RAISERROR('Pedido with id %d does not exist.', 16, 1, @idPedido);
            RETURN;
        END

    -- Validate escala exists
    IF NOT EXISTS (SELECT 1 FROM Escala WHERE idEscala = @idEscala)
        BEGIN
            RAISERROR('Escala with id %d does not exist.', 16, 1, @idEscala);
            RETURN;
        END

    -- Validate pedido state is "Entregado" (4)
    DECLARE @estadoId INT;
    SELECT @estadoId = estado FROM Pedido WHERE id = @idPedido;

    IF @estadoId != 4
        BEGIN
            RAISERROR('Pedido must be in Entregado state to be rated. Current state: %d', 16, 1, @estadoId);
            RETURN;
        END

    -- Update evaluation
    UPDATE Pedido
    SET evaluacionEscala = @idEscala
    WHERE id = @idPedido;

    -- Return updated pedido
    SELECT p.id, p.estado, p.proveedor, p.fechaEstimada, p.fechaEntrega,
           p.fechaRegistro, p.evaluacionEscala,
           ep.nombre AS estadoNombre,
           ep.descripcion AS estadoDescripcion,
           pr.nombre AS proveedorNombre
    FROM Pedido p
             LEFT JOIN EstadoPedido ep ON p.estado = ep.id
             LEFT JOIN Proveedor pr ON p.proveedor = pr.id
    WHERE p.id = @idPedido;
END
GO

-- =============================================
-- Usuario Stored Procedures
-- =============================================

-- =============================================
-- Save Usuario (Create)
-- =============================================
CREATE OR ALTER PROCEDURE sp_save_usuario
    @username NVARCHAR(100),
    @email NVARCHAR(255),
    @passwordHash NVARCHAR(255)
AS
BEGIN
    SET NOCOUNT ON;

    -- Validar que no exista el username
    IF EXISTS (SELECT 1 FROM Usuario WHERE username = @username)
    BEGIN
        RAISERROR('Username already exists', 16, 1);
        RETURN;
    END

    -- Validar que no exista el email
    IF EXISTS (SELECT 1 FROM Usuario WHERE email = @email)
    BEGIN
        RAISERROR('Email already exists', 16, 1);
        RETURN;
    END

    -- Insertar
    INSERT INTO Usuario (username, email, passwordHash, fechaCreacion, fechaActualizacion)
    VALUES (@username, @email, @passwordHash, GETDATE(), GETDATE());

    -- Retornar el usuario creado (sin password)
    SELECT id, username, email, fechaCreacion, fechaActualizacion
    FROM Usuario
    WHERE id = SCOPE_IDENTITY();
END
GO

-- =============================================
-- Find Usuario by Username
-- =============================================
CREATE OR ALTER PROCEDURE sp_find_usuario_by_username
    @username NVARCHAR(100)
AS
BEGIN
    SET NOCOUNT ON;

    SELECT id, username, email, passwordHash, fechaCreacion, fechaActualizacion
    FROM Usuario
    WHERE username = @username;
END
GO

-- =============================================
-- Find All Usuarios
-- =============================================
CREATE OR ALTER PROCEDURE sp_find_all_usuarios
AS
BEGIN
    SET NOCOUNT ON;

    SELECT id, username, email, fechaCreacion, fechaActualizacion
    FROM Usuario
    ORDER BY fechaCreacion DESC;
END
GO

-- =============================================
-- Find Products with Low Stock (below minimum)
-- Returns products where actualStock <= stockMinimo
-- Excludes products already in active orders (estado 1, 2, 3)
-- Used for automatic order generation
-- =============================================
CREATE OR ALTER PROCEDURE sp_find_productos_bajo_stock
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        p.codigoBarra,
        p.nombre,
        p.imagen,
        p.stockMinimo,
        p.stockMaximo,
        p.stockActual,
        p.estado,
        ep.nombre AS estadoNombre,
        ep.descripcion AS estadoDescripcion
    FROM Producto p
    LEFT JOIN EstadoProducto ep ON p.estado = ep.id
    WHERE p.stockActual <= p.stockMinimo
      AND p.stockMaximo > p.stockActual  -- Only products that can be restocked
      -- Exclude products already in active orders (Pendiente, En Proceso, Enviado)
      AND p.codigoBarra NOT IN (
          SELECT DISTINCT pp.codigoBarra
          FROM PedidoProducto pp
          INNER JOIN Pedido ped ON pp.idPedido = ped.id
          WHERE ped.estado IN (1, 2, 3)  -- 1=Pendiente, 2=En Proceso, 3=Enviado
      )
    ORDER BY p.stockActual ASC;  -- Lowest stock first
END
GO

-- =============================================
-- Find ProductoProveedor mapping
-- Used to check if provider has a specific product
-- =============================================
CREATE OR ALTER PROCEDURE sp_find_producto_proveedor
    @idProveedor INT,
    @codigoBarra INT
AS
BEGIN
    SET NOCOUNT ON;

    SELECT
        pp.idProveedor,
        pp.codigoBarra,
        pp.codigoBarraProveedor,
        pp.fechaActualizacion,
        p.nombre AS productoNombre,
        pr.nombre AS proveedorNombre
    FROM ProductoProveedor pp
    LEFT JOIN Producto p ON pp.codigoBarra = p.codigoBarra
    LEFT JOIN Proveedor pr ON pp.idProveedor = pr.id
    WHERE pp.idProveedor = @idProveedor
      AND pp.codigoBarra = @codigoBarra;
END
GO

-- =============================================
-- Trigger to update product stock when order is delivered (estado = 4)
-- When an order status changes to 4 (Entregado), update stockActual
-- by adding the quantities of all products in that order
-- =============================================
CREATE OR ALTER TRIGGER trg_update_stock_on_pedido_delivered
ON Pedido
AFTER UPDATE
AS
BEGIN
    SET NOCOUNT ON;

    -- Only process if estado was updated
    IF UPDATE(estado)
    BEGIN
        -- Update stock for all products in orders that just transitioned to estado = 4
        UPDATE Producto
        SET stockActual = stockActual + pp.cantidad
        FROM Producto p
        INNER JOIN PedidoProducto pp ON p.codigoBarra = pp.codigoBarra
        INNER JOIN inserted i ON pp.idPedido = i.id
        INNER JOIN deleted d ON i.id = d.id
        WHERE i.estado = 4  -- New estado is "Entregado"
          AND d.estado != 4  -- Old estado was not "Entregado" (to avoid double-updates)
    END
END
GO
