CREATE TABLE EstadoProducto (
                                id INT PRIMARY KEY IDENTITY(1,1),
                                nombre NVARCHAR(255) NOT NULL,
                                descripcion NVARCHAR(255) NULL
);
GO

CREATE TABLE TipoServicio (
                              id INT PRIMARY KEY IDENTITY(1,1),
                              nombre NVARCHAR(255) NOT NULL
);
GO

CREATE TABLE Proveedor (
                           id INT PRIMARY KEY IDENTITY(1,1),
                           nombre NVARCHAR(255) NOT NULL,
                           apiEndpoint NVARCHAR(255) NULL,
                           tipoServicio INT NULL,
                           clientId NVARCHAR(255) NULL,
                           apiKey NVARCHAR(255) NULL,

                           CONSTRAINT FK_Proveedor_TipoServicio
                               FOREIGN KEY (tipoServicio) REFERENCES TipoServicio(id)
);
GO

CREATE TABLE Producto (
                          codigoBarra INT PRIMARY KEY,
                          nombre NVARCHAR(255) NOT NULL,
                          imagen NVARCHAR(500) NULL,
                          stockMinimo INT NOT NULL DEFAULT 0,
                          stockMaximo INT NOT NULL DEFAULT 0,
                          stockActual INT NOT NULL DEFAULT 0
);
GO

CREATE TABLE ProductoProveedor (
                                idProveedor INT NOT NULL,
                                codigoBarra INT NOT NULL,
                                fechaActualizacion DATETIME NOT NULL DEFAULT GETDATE(),
                                estado INT NOT NULL DEFAULT 1,
                                codigoBarraProveedor INT NOT NULL
    -- Foreign Keys
                                   CONSTRAINT FK_ProductoProveedor_Producto
                                       FOREIGN KEY (codigoBarra) REFERENCES Producto(codigoBarra),

                                   CONSTRAINT FK_ProductoProveedor_Proveedor
                                       FOREIGN KEY (idProveedor) REFERENCES Proveedor(id),

                                   CONSTRAINT FK_ProductoProveedor_EstadoProducto
                                       FOREIGN KEY (estado) REFERENCES EstadoProducto(id),

                                   CONSTRAINT PK_ProductoProveedor
                                       PRIMARY KEY (codigoBarra, idProveedor)
);
GO

CREATE TABLE EstadoPedido (
                              id INT PRIMARY KEY IDENTITY(1,1),
                              nombre NVARCHAR(255) NOT NULL,
                              descripcion TEXT NULL
);
GO

CREATE TABLE Pedido (
                        id INT PRIMARY KEY IDENTITY(1,1),
                        estado INT NOT NULL,
                        proveedor INT NOT NULL,
                        idPedidoProveedor INT NULL,  -- ID del pedido en el sistema del proveedor
                        fechaEstimada DATETIME NOT NULL DEFAULT GETDATE(),
                        fechaEntrega DATETIME NULL,
                        fechaRegistro DATETIME NOT NULL DEFAULT GETDATE(),
                        evaluacionEscala SMALLINT NULL,

    -- Foreign Keys
                        CONSTRAINT FK_Pedido_EstadoPedido
                            FOREIGN KEY (estado) REFERENCES EstadoPedido(id),

                        CONSTRAINT FK_Pedido_Proveedor
                            FOREIGN KEY (proveedor) REFERENCES Proveedor(id)
);
GO

-- =============================================
-- Create PedidoProducto Table if doesn't exist
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'PedidoProducto')
BEGIN
CREATE TABLE PedidoProducto (
                                idPedido INT NOT NULL,
                                codigoBarra INT NOT NULL,
                                cantidad INT NOT NULL,
                                CONSTRAINT PK_PedidoProducto PRIMARY KEY (idPedido, codigoBarra),
                                CONSTRAINT FK_PedidoProducto_Pedido FOREIGN KEY (idPedido) REFERENCES Pedido(id) ON DELETE CASCADE,
                                CONSTRAINT FK_PedidoProducto_Producto FOREIGN KEY (codigoBarra) REFERENCES Producto(codigoBarra) ON DELETE CASCADE,
                                CONSTRAINT CHK_PedidoProducto_Cantidad CHECK (cantidad > 0)
);
PRINT 'PedidoProducto table created successfully.';
END
ELSE
BEGIN
    PRINT 'PedidoProducto table already exists.';
END
GO

-- =============================================
-- Create HistorialPrecio Table
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'HistorialPrecio')
BEGIN
CREATE TABLE HistorialPrecio (
                                 codigoBarra INT NOT NULL,
                                 idProveedor INT NOT NULL,
                                 precio FLOAT NOT NULL,
                                 fechaInicio DATETIME NOT NULL DEFAULT GETDATE(),
                                 fechaFin DATETIME NULL,
                                 CONSTRAINT PK_HistorialPrecio PRIMARY KEY (codigoBarra, idProveedor, fechaInicio),
                                 CONSTRAINT FK_HistorialPrecio_Producto FOREIGN KEY (codigoBarra) REFERENCES Producto(codigoBarra) ON DELETE CASCADE,
                                 CONSTRAINT FK_HistorialPrecio_Proveedor FOREIGN KEY (idProveedor) REFERENCES Proveedor(id) ON DELETE CASCADE,
                                 CONSTRAINT CHK_HistorialPrecio_Precio CHECK (precio >= 0),
                                 CONSTRAINT CHK_HistorialPrecio_Fechas CHECK (fechaFin IS NULL OR fechaFin > fechaInicio)
);
PRINT 'HistorialPrecio table created successfully.';
END
ELSE
BEGIN
    PRINT 'HistorialPrecio table already exists.';
END
GO

-- =============================================
-- Create Escala Table (Rating Scale Mappings)
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Escala')
BEGIN
CREATE TABLE Escala (
                        idEscala INT PRIMARY KEY IDENTITY(1,1),
                        idProveedor INT NOT NULL,
                        escalaInt SMALLINT NULL,
                        escalaExt VARCHAR(50) NOT NULL,
                        descripcionExt VARCHAR(255) NULL,

                        -- Foreign Keys
                        CONSTRAINT FK_Escala_Proveedor
                            FOREIGN KEY (idProveedor) REFERENCES Proveedor(id) ON DELETE CASCADE,

                        -- Constraints
                        CONSTRAINT CHK_Escala_Internal
                            CHECK (escalaInt IS NULL OR escalaInt BETWEEN 1 AND 5)
);

-- Indexes for faster lookups
CREATE INDEX IX_Escala_Proveedor ON Escala(idProveedor);
CREATE INDEX IX_Escala_Internal ON Escala(idProveedor, escalaInt);
CREATE INDEX IX_Escala_External ON Escala(idProveedor, escalaExt);

PRINT 'Escala table created successfully.';
END
ELSE
BEGIN
    PRINT 'Escala table already exists.';
END
GO

-- =============================================
-- Usuario table for authentication
-- =============================================
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'Usuario')
BEGIN
CREATE TABLE Usuario (
    id INT PRIMARY KEY IDENTITY(1,1),
    username NVARCHAR(100) NOT NULL UNIQUE,
    email NVARCHAR(255) NOT NULL UNIQUE,
    passwordHash NVARCHAR(255) NOT NULL,
    fechaCreacion DATETIME DEFAULT GETDATE(),
    fechaActualizacion DATETIME DEFAULT GETDATE(),

    CONSTRAINT CHK_Usuario_Username CHECK (LEN(username) >= 3),
    CONSTRAINT CHK_Usuario_Email CHECK (email LIKE '%@%.%')
);

-- Indexes for faster lookups
CREATE INDEX IX_Usuario_Username ON Usuario(username);
CREATE INDEX IX_Usuario_Email ON Usuario(email);

PRINT 'Usuario table created successfully.';
END
ELSE
BEGIN
    PRINT 'Usuario table already exists.';
END
GO

