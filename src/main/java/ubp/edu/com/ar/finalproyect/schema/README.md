# Database Scripts

Este directorio contiene todos los scripts SQL necesarios para el proyecto.

## 📁 Archivos

### 1. **schema.sql**
Crea todas las tablas de la base de datos.
- Define estructura de tablas
- Establece foreign keys
- Define constraints

### 2. **store_procedures.sql**
Crea todos los stored procedures necesarios.
- 19 stored procedures en total
- CRUD completo para todas las entidades
- Operaciones especializadas

### 3. **reset_and_load_data.sql** ⭐ RECOMENDADO
Script completo que limpia y recarga la base de datos con datos de prueba.

**Ejecuta este archivo para obtener una base de datos limpia lista para usar.**

### 4. **clean_database.sql**
Solo limpia los datos, no recarga.

### 5. **test_data.sql**
Solo datos de prueba (sin limpieza previa).

---

## 🚀 Cómo Usar

### Opción 1: Reset Completo (Recomendado)

**Ejecuta un solo archivo:**

```sql
-- Ejecuta en SQL Server Management Studio o Azure Data Studio
-- Este archivo limpia y recarga todo
:r reset_and_load_data.sql
```

O desde la línea de comandos:

```bash
sqlcmd -S localhost -d SupermarketDB -i reset_and_load_data.sql
```

### Opción 2: Paso a Paso

Si prefieres control manual:

```sql
-- 1. Crear estructura
:r schema.sql

-- 2. Crear stored procedures
:r store_procedures.sql

-- 3. Limpiar datos (opcional)
:r clean_database.sql

-- 4. Cargar datos de prueba
:r test_data.sql
```

---

## 📊 Datos de Prueba Incluidos

### Estados
- **EstadoProducto:** Disponible, Agotado, Descontinuado
- **EstadoPedido:** Pendiente, Confirmado, En Preparación, En Tránsito, Entregado, Cancelado
- **TipoServicio:** REST, SOAP

### Proveedores (3)
1. **Distribuidora Central REST** (localhost:8081)
   - clientId: `testclient`
   - apiKey: `test-api-key-123`
   - Tipo: REST

2. **Mayorista Del Sur S.A.** (API remota)
   - clientId: `supermarket001`
   - apiKey: `sk_live_abc123xyz789`
   - Tipo: REST

3. **Proveedor SOAP Ejemplo** (SOAP)
   - clientId: `soapclient`
   - apiKey: `soap_key_456`
   - Tipo: SOAP

### Productos (13) - Códigos de Barra de 4 dígitos

#### Almacén (4 productos)
- **1001** - Leche Descremada La Serenísima 1L - $1,250
- **1002** - Arroz Gallo Oro 1kg - $1,800
- **1003** - Aceite Cocinero 900ml - $3,500
- **1004** - Azúcar Ledesma 1kg - $1,400

#### Bebidas (3 productos)
- **2001** - Coca Cola 2.25L - $2,200
- **2002** - Sprite 2.25L - $2,000
- **2003** - Agua Mineral Villavicencio 2L - $950

#### Limpieza (3 productos)
- **3001** - Detergente Magistral 750ml - $2,800
- **3002** - Lavandina Ayudín 1L - $1,500
- **3003** - Limpiador Mr. Músculo 500ml - $3,200

#### Panadería (3 productos)
- **4001** - Pan Lactal Bimbo 450g - $1,800
- **4002** - Galletitas Oreo 118g - $2,500
- **4003** - Tostadas Criollitas 120g - $1,200

### Pedidos (4)

1. **Pedido #1** - Estado: Pendiente
   - Proveedor: Distribuidora Central
   - Productos: Leche (50), Arroz (30), Coca Cola (60)
   - Fecha estimada: 2025-12-05

2. **Pedido #2** - Estado: Confirmado
   - Proveedor: Mayorista Del Sur
   - Productos: Detergente (25), Lavandina (20), Limpiador (15)
   - Fecha estimada: 2025-12-03

3. **Pedido #3** - Estado: En Tránsito
   - Proveedor: Distribuidora Central
   - Productos: Sprite (40), Agua (80), Azúcar (35)
   - Fecha estimada: 2025-11-30

4. **Pedido #4** - Estado: Entregado ✅
   - Proveedor: Mayorista Del Sur
   - Productos: Pan (45), Oreo (60), Tostadas (35)
   - Evaluación: 5/5 ⭐

### Historial de Precios
- 13 precios actuales (fechaFin = NULL)
- 3 precios históricos de Octubre 2025

---

## ⚠️ Notas Importantes

### Configuración de Proveedores

El **Proveedor 1** está configurado para apuntar a `http://localhost:8081`.

**Si vas a probar la integración REST:**
1. Levanta un servidor mock en el puerto 8081, o
2. Cambia el `apiEndpoint` del Proveedor 1 en el script

**Para cambiar el endpoint:**
```sql
-- Opción A: Editar antes de ejecutar el script
-- Busca la línea con "http://localhost:8081" y cámbiala

-- Opción B: Actualizar después de cargar
UPDATE Proveedor
SET apiEndpoint = 'http://tu-servidor:puerto'
WHERE id = 1;
```

### Health Check Automático

Al crear proveedores desde la API (`POST /api/proveedores`), el sistema hace **health check automático**.

Los proveedores del script SQL se insertan **directamente en la BD**, por lo que:
- ❌ No pasan por validación del health check
- ✅ Se crean exitosamente aunque el endpoint no esté disponible
- ⚠️ Para testing, asegúrate de tener el servidor mock corriendo

---

## 🔄 Orden de Ejecución de Scripts

Si ejecutas manualmente (opción 2):

```
1. schema.sql              (Crea tablas)
2. store_procedures.sql    (Crea SPs)
3. clean_database.sql      (Opcional - Limpia datos)
4. test_data.sql           (Carga datos de prueba)
```

**O simplemente:**
```
reset_and_load_data.sql    (Todo en uno)
```

---

## 📝 Verificación

Después de ejecutar los scripts, verifica:

```sql
-- Contar registros
SELECT 'Proveedores' AS Tabla, COUNT(*) AS Total FROM Proveedor
UNION ALL
SELECT 'Productos', COUNT(*) FROM Producto
UNION ALL
SELECT 'Pedidos', COUNT(*) FROM Pedido
UNION ALL
SELECT 'Items en Pedidos', COUNT(*) FROM PedidoProducto
UNION ALL
SELECT 'Historial Precios', COUNT(*) FROM HistorialPrecio;

-- Ver proveedores con sus credenciales
SELECT id, nombre, apiEndpoint, clientId, apiKey, tipoServicio
FROM Proveedor;

-- Ver pedidos con su estado
SELECT
    p.id,
    p.estado,
    ep.nombre AS estadoNombre,
    pr.nombre AS proveedorNombre,
    p.fechaEstimada
FROM Pedido p
INNER JOIN EstadoPedido ep ON p.estado = ep.id
INNER JOIN Proveedor pr ON p.proveedor = pr.id
ORDER BY p.id;
```

---

## 🎯 Testing con la API

### Crear un nuevo proveedor (con health check):
```bash
curl -X POST http://localhost:8080/api/proveedores \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Nuevo Proveedor",
    "apiEndpoint": "http://localhost:8081",
    "tipoServicio": 1,
    "clientId": "testclient",
    "apiKey": "test-api-key-123"
  }'
```

### Cancelar un pedido:
```bash
curl -X POST http://localhost:8080/api/pedidos/1/cancelar
```

### Obtener productos con precios:
```bash
curl "http://localhost:8080/api/productos/1001?history=true"
```

---

**Última actualización:** 2025-11-27
