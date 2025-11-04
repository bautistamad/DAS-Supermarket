CREATE TABLE EstadoProducto (
                                id INT PRIMARY KEY IDENTITY(1,1),
                                nombre NVARCHAR(255) NOT NULL,
                                descripcion TEXT NULL
);
GO

CREATE TABLE Proveedor (
                           id INT PRIMARY KEY IDENTITY(1,1),
                           nombre NVARCHAR(255) NOT NULL,
                           servicio NVARCHAR(255) NULL,
                           tipoServicio INT NULL,
                           escala INT NULL
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
                                   codigoProducto INT NOT NULL,
                                   fechaActualizacion DATETIME NOT NULL DEFAULT GETDATE(),
                                   estado INT NOT NULL DEFAULT 1,

    -- Foreign Keys
                                   CONSTRAINT FK_ProductoProveedor_Producto
                                       FOREIGN KEY (codigoProducto) REFERENCES Producto(codigoBarra),

                                   CONSTRAINT FK_ProductoProveedor_Proveedor
                                       FOREIGN KEY (idProveedor) REFERENCES Proveedor(id),

                                   CONSTRAINT FK_ProductoProveedor_EstadoProducto
                                       FOREIGN KEY (estado) REFERENCES EstadoProducto(id),

                                   CONSTRAINT PK_ProductoProveedor
                                       PRIMARY KEY (codigoProducto, idProveedor)
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
                        puntuacion TINYINT NULL,
                        fechaCreada DATETIME NOT NULL DEFAULT GETDATE(),
                        fechaEntrega DATETIME NULL,
                        fechaRegistro DATETIME NOT NULL DEFAULT GETDATE(),
                        evaluacion SMALLINT NULL,

    -- Foreign Keys
                        CONSTRAINT FK_Pedido_EstadoPedido
                            FOREIGN KEY (estado) REFERENCES EstadoPedido(id),

                        CONSTRAINT FK_Pedido_Proveedor
                            FOREIGN KEY (proveedor) REFERENCES Proveedor(id)
);
GO
