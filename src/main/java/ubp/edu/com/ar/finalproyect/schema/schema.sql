CREATE TABLE EstadoProducto (
                                id SMALLINT PRIMARY KEY IDENTITY(1,1),
                                nombre VARCHAR(255) NOT NULL,
                                descripcion TEXT NULL
);
GO

CREATE TABLE Proveedor (
                           id INT PRIMARY KEY IDENTITY(1,1),
                           nombre VARCHAR(255) NOT NULL,
                           telefono VARCHAR(50) NULL,
                           email VARCHAR(255) NULL,
                           direccion VARCHAR(500) NULL,
                           fechaCreacion DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE Producto (
                          codigoBarra INT PRIMARY KEY,
                          nombre VARCHAR(255) NOT NULL,
                          imagen VARCHAR(500) NULL,
                          stockMinimo INT NOT NULL DEFAULT 0,
                          stockMaximo INT NOT NULL DEFAULT 0,
                          stockActual INT NOT NULL DEFAULT 0,
                          fechaCreacion DATETIME DEFAULT GETDATE()
);
GO

CREATE TABLE ProductoProveedor (
                                   id INT PRIMARY KEY IDENTITY(1,1),
                                   codigoProducto INT NOT NULL,
                                   idProveedor INT NOT NULL,
                                   fechaActualizacion DATETIME NOT NULL DEFAULT GETDATE(),
                                   estado SMALLINT NOT NULL DEFAULT 1,
                                   precioCompra DECIMAL(10, 2) NULL,

    -- Foreign Keys
                                   CONSTRAINT FK_ProductoProveedor_Producto
                                       FOREIGN KEY (codigoProducto) REFERENCES Producto(codigoBarra),

                                   CONSTRAINT FK_ProductoProveedor_Proveedor
                                       FOREIGN KEY (idProveedor) REFERENCES Proveedor(id),

                                   CONSTRAINT FK_ProductoProveedor_EstadoProducto
                                       FOREIGN KEY (estado) REFERENCES EstadoProducto(id),

                                   CONSTRAINT UQ_ProductoProveedor_Producto_Proveedor
                                       UNIQUE (codigoProducto, idProveedor)
);
GO