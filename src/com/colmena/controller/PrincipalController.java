// src/com/colmena/controller/DashboardController.java
package com.colmena.controller;

import com.colmena.data.ConexionBDD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;

public class PrincipalController {

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> estadisticas = new HashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBDD.getInstancia().establecerConexion();
            
            // Total de productos
            String sqlProductos = "SELECT COUNT(*) as total, COUNT(CASE WHEN stock > 0 THEN 1 END) as con_stock FROM PRODUCTO WHERE activo = 1";
            pstmt = conn.prepareStatement(sqlProductos);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                estadisticas.put("totalProductos", rs.getInt("total"));
                estadisticas.put("productosConStock", rs.getInt("con_stock"));
            }
            
            rs.close();
            pstmt.close();
            
            // Total de categorías
            String sqlCategorias = "SELECT COUNT(*) as total FROM CATEGORIA";
            pstmt = conn.prepareStatement(sqlCategorias);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                estadisticas.put("totalCategorias", rs.getInt("total"));
            }
            
            rs.close();
            pstmt.close();
            
            // Total de clientes
            String sqlClientes = "SELECT COUNT(*) as total FROM CLIENTE";
            pstmt = conn.prepareStatement(sqlClientes);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                estadisticas.put("totalClientes", rs.getInt("total"));
            }
            
            rs.close();
            pstmt.close();
            
            // Ventas (pedidos) del día
            String sqlVentasHoy = "SELECT COUNT(*) as total, SUM(total) as monto_total " +
                                 "FROM PEDIDO " +
                                 "WHERE CAST(fecha_pedido AS DATE) = CAST(GETDATE() AS DATE)";
            pstmt = conn.prepareStatement(sqlVentasHoy);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                estadisticas.put("ventasHoy", rs.getInt("total"));
                double montoTotal = rs.getDouble("monto_total");
                estadisticas.put("montoTotalHoy", montoTotal);
            }
            
            rs.close();
            pstmt.close();
            
            // Productos con bajo stock (menos de 10 unidades)
            String sqlBajoStock = "SELECT COUNT(*) as total FROM PRODUCTO WHERE stock < 10 AND activo = 1";
            pstmt = conn.prepareStatement(sqlBajoStock);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                estadisticas.put("productosBajoStock", rs.getInt("total"));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener estadísticas: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return estadisticas;
    }
       
    public Map<String, Integer> obtenerProductosMasVendidos() {
        Map<String, Integer> productosMasVendidos = new HashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBDD.getInstancia().establecerConexion();
            
            String sql = "SELECT TOP 5 p.nombre, SUM(dp.cantidad) as cantidad_vendida " +
                        "FROM DETALLE_PEDIDO dp " +
                        "JOIN PRODUCTO p ON dp.producto_id = p.id " +
                        "JOIN PEDIDO ped ON dp.pedido_id = ped.id " +
                        "WHERE ped.fecha_pedido >= DATEADD(MONTH, -1, GETDATE()) " + // Último mes
                        "GROUP BY p.nombre " +
                        "ORDER BY cantidad_vendida DESC";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                productosMasVendidos.put(rs.getString("nombre"), rs.getInt("cantidad_vendida"));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener productos más vendidos: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return productosMasVendidos;
    }
        
    public Map<String, Double> obtenerVentasPorCategoria() {
        Map<String, Double> ventasPorCategoria = new HashMap<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBDD.getInstancia().establecerConexion();
            
            String sql = "SELECT c.nombre, SUM(dp.subtotal) as monto_total " +
                        "FROM DETALLE_PEDIDO dp " +
                        "JOIN PRODUCTO p ON dp.producto_id = p.id " +
                        "JOIN CATEGORIA c ON p.categoria_id = c.id " +
                        "JOIN PEDIDO ped ON dp.pedido_id = ped.id " +
                        "WHERE ped.fecha_pedido >= DATEADD(MONTH, -1, GETDATE()) " + // Último mes
                        "GROUP BY c.nombre " +
                        "ORDER BY monto_total DESC";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                ventasPorCategoria.put(rs.getString("nombre"), rs.getDouble("monto_total"));
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener ventas por categoría: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return ventasPorCategoria;
    }
}