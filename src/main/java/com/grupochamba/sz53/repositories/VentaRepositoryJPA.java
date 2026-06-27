package com.grupochamba.sz53.repositories;

import com.grupochamba.sz53.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface VentaRepositoryJPA extends JpaRepository<Venta, Long> {

    // La interfaz mapea automáticamente las columnas gracias a los alias de la Query
    public interface ResumenMensualProducto {
        Long getProductoId();
        String getProductoNombre();
        Integer getCantidadComprada();
        Integer getCantidadVendida();
        Integer getStockActual();
    }

    @Query("SELECT p.id AS productoId, p.nombre AS productoNombre, p.stock AS stockActual, " +
           "  COALESCE((SELECT SUM(dne.cantidad) FROM DetalleNotaEnvio dne " +
           "            WHERE dne.producto = p " +
           "            AND dne.notaEnvio.procesado = 1 " +
           "            AND YEAR(dne.notaEnvio.fecha) = :anio " +
           "            AND MONTH(dne.notaEnvio.fecha) = :mes), 0) AS cantidadComprada, " +
           "  COALESCE((SELECT CAST(SUM(dv.cantidad) AS integer) FROM DetalleVenta dv " +
           "            WHERE dv.producto = p " +
           "            AND dv.venta.procesado = 1 " +
           "            AND YEAR(dv.venta.fecha) = :anio " +
           "            AND MONTH(dv.venta.fecha) = :mes), 0) AS cantidadVendida " +
           "FROM Producto p " +
           "WHERE (SELECT SUM(dne.cantidad) FROM DetalleNotaEnvio dne " +
           "       WHERE dne.producto = p AND dne.notaEnvio.procesado = 1 " +
           "       AND YEAR(dne.notaEnvio.fecha) = :anio AND MONTH(dne.notaEnvio.fecha) = :mes) IS NOT NULL " +
           "   OR (SELECT SUM(dv.cantidad) FROM DetalleVenta dv " +
           "       WHERE dv.producto = p AND dv.venta.procesado = 1 " +
           "       AND YEAR(dv.venta.fecha) = :anio AND MONTH(dv.venta.fecha) = :mes) IS NOT NULL " +
           "ORDER BY p.nombre ASC")
    List<ResumenMensualProducto> obtenerResumenMensual(@Param("anio") int anio, @Param("mes") int mes);
}