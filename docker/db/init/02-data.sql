-- =============================================================================
-- 1. INSERTAR CATEGORÍAS DE PRUEBA (RUBRO LIBRERÍA / ÚTILES)
-- =============================================================================
INSERT INTO categoria (id, nombre) VALUES 
(1, 'Escritura y Corrección'),
(2, 'Cuadernos y Papelería'),
(3, 'Artículos de Oficina y Descartables');

-- Sincronizar secuencia de categorías
SELECT pg_catalog.setval('categoria_id_seq', 3, true);


-- =============================================================================
-- 2. INSERTAR PRODUCTOS
-- =============================================================================
INSERT INTO producto (id, nombre, precio, stock, estado, categoria_id) VALUES 
(1, 'Bolígrafo Azul Trazo Fino (Caja x50)', 45000, 150, true, 1),
(2, 'Corrector Líquido en Cinta 5mm', 28000, 80, true, 1),
(3, 'Cuaderno Universitario 100 Hojas Raya', 12000, 300, true, 2),
(4, 'Resma de Papel A4 75g (500 Hojas)', 38000, 500, true, 2), -- Ajustado para mantener coherencia de precios
(5, 'Calculadora Científica Estándar', 65000, 45, true, 3);

-- Sincronizar secuencia de productos
SELECT pg_catalog.setval('producto_id_seq', 5, true);


-- =============================================================================
-- 3. INSERTAR CLIENTES
-- =============================================================================
INSERT INTO clientes (id, nombre, documento, direccion, telefono, email) VALUES 
(1, 'Juan Pérez', '4567891', 'Av. Gaspar Rodríguez de Francia', '0985111222', 'juan.perez@email.com'),
(2, 'María Galeano', '3892104', 'Calle Mariscal Estigarribia', '0971333444', 'maria.galeano@email.com');

-- Sincronizar secuencia de clientes
SELECT pg_catalog.setval('clientes_id_seq', 2, true);


-- =============================================================================
-- 4. INSERTAR NOTAS DE ENVÍO (COMPRAS/ENTRADAS DE STOCK) - JUNIO 2026
-- =============================================================================
INSERT INTO nota_envio (id, nro, fecha, proveedor, observacion, procesado, verificado) VALUES 
(1, 1001, '2026-06-10', 'DISTRIBUIDORA CONTINENTAL', 'Lote de stock para temporada de invierno', 1, 1),
(2, 1002, '2026-06-15', 'PAPELERA PARAGUAYA S.A.', 'Reposición mensual de resmas y oficina', 1, 1);

-- Detalles de Notas de Envío
INSERT INTO detalle_nota_envio (id, cantidad, precio, nota_envio_id, producto_id) VALUES 
(1, 100, 35000, 1, 1), -- 100 Cajas de bolígrafos compradas
(2, 50, 20000, 1, 2),  -- 50 Correctores comprados
(3, 200, 30000, 2, 4), -- 200 Resmas A4 compradas (Ajustado el costo proporcionalmente)
(4, 30, 50000, 2, 5);  -- 30 Calculadoras científicas compradas

-- Sincronizar secuencias
SELECT pg_catalog.setval('nota_envio_id_seq', 2, true);
SELECT pg_catalog.setval('detalle_nota_envio_id_seq', 4, true);


-- =============================================================================
-- 5. INSERTAR VENTAS (SALIDAS) - JUNIO 2026
-- =============================================================================
INSERT INTO ventas (id, fecha, cliente_id, procesado, total_gravada, total_iva, total_venta) VALUES 
(1, '2026-06-18', 1, 1, 140909.09, 14090.91, 155000), -- Ajustado el total: (2 * 45mil) + 65mil = 155.000 Gs.
(2, '2026-06-22', 2, 1, 103636.36, 10363.64, 114000); -- Ajustado el total: 3 * 38mil = 114.000 Gs.

-- Detalles de Ventas
-- Venta 1: 2 Cajas de Bolígrafos (45mil c/u) + 1 Calculadora Científica (65mil c/u) = 155.000 Gs.
INSERT INTO detalle_ventas (id, cantidad, precio_unitario, subtotal, iva, gravada, producto_id, venta_id) VALUES 
(1, 2, 45000, 90000, 8181.82, 81818.18, 1, 1),
(2, 1, 65000, 65000, 5909.09, 59090.91, 5, 1);

-- Venta 2: 3 Resmas de Papel A4 (38mil c/u) = 114.000 Gs.
INSERT INTO detalle_ventas (id, cantidad, precio_unitario, subtotal, iva, gravada, producto_id, venta_id) VALUES 
(3, 3, 38000, 114000, 10363.64, 103636.36, 4, 2);

-- Sincronizar secuencias
SELECT pg_catalog.setval('ventas_id_seq', 2, true);
SELECT pg_catalog.setval('detalle_ventas_id_seq', 3, true);