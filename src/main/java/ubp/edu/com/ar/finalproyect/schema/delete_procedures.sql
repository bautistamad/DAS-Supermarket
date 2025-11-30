-- =============================================
-- DELETE ALL STORED PROCEDURES
-- =============================================
-- This script drops all stored procedures in the correct order
-- Execute this before recreating procedures
-- =============================================

PRINT 'Dropping all stored procedures...';

-- =============================================
-- PRECIO (Price History) - 3 procedures
-- =============================================
DROP PROCEDURE IF EXISTS sp_get_precio_history_by_product;
DROP PROCEDURE IF EXISTS sp_get_current_precio_by_product_provider;
DROP PROCEDURE IF EXISTS sp_sync_precio_from_proveedor;

-- =============================================
-- PRODUCTO (Products) - 5 procedures
-- =============================================
DROP PROCEDURE IF EXISTS sp_find_all_products;
DROP PROCEDURE IF EXISTS sp_find_product_by_barcode;
DROP PROCEDURE IF EXISTS sp_delete_product;
DROP PROCEDURE IF EXISTS sp_save_product;
DROP PROCEDURE IF EXISTS sp_get_products_by_provider;

-- =============================================
-- PROVEEDOR (Providers) - 5 procedures
-- =============================================
DROP PROCEDURE IF EXISTS sp_find_all_providers;
DROP PROCEDURE IF EXISTS sp_find_provider_by_id;
DROP PROCEDURE IF EXISTS sp_delete_provider;
DROP PROCEDURE IF EXISTS sp_save_provider;
DROP PROCEDURE IF EXISTS sp_assign_product_to_provider;

-- =============================================
-- PEDIDO (Orders) - 7 procedures
-- =============================================
DROP PROCEDURE IF EXISTS sp_create_pedido;
DROP PROCEDURE IF EXISTS sp_find_pedido_by_id;
DROP PROCEDURE IF EXISTS sp_find_all_pedidos;
DROP PROCEDURE IF EXISTS sp_update_pedido;
DROP PROCEDURE IF EXISTS sp_delete_pedido;
DROP PROCEDURE IF EXISTS sp_find_pedidos_by_proveedor;
DROP PROCEDURE IF EXISTS sp_get_products_by_pedido;

-- =============================================
-- ESCALA (Rating Scales) - 4 procedures
-- =============================================
DROP PROCEDURE IF EXISTS sp_save_escala;
DROP PROCEDURE IF EXISTS sp_find_escalas_by_proveedor;
DROP PROCEDURE IF EXISTS sp_find_escala_by_internal;
DROP PROCEDURE IF EXISTS sp_update_pedido_evaluacion;

GO

PRINT 'All stored procedures dropped successfully!';
PRINT 'Total procedures dropped: 24';
PRINT '  - Precio: 3';
PRINT '  - Producto: 5';
PRINT '  - Proveedor: 5';
PRINT '  - Pedido: 7';
PRINT '  - Escala: 4';
