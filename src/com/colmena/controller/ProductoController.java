package com.colmena.controller;

import com.colmena.data.ConexionBDD;
import com.colmena.model.Categoria;
import com.colmena.model.Producto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ProductoController {

    public List<Producto> obtenerProductos() {
        List<Producto> productos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();

            String sql = "SELECT p.id, p.codigo, p.nombre, p.descripcion, p.precio, p.stock, " +
                    "p.categoria_id, c.nombre as categoria_nombre, c.descripcion as categoria_descripcion, " +
                    "p.activo " +
                    "FROM PRODUCTO p " +
                    "INNER JOIN CATEGORIA c ON p.categoria_id = c.id " +
                    "WHERE p.activo = 1 " +
                    "ORDER BY p.nombre";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Producto producto = new Producto();
                producto.setId(rs.getInt("id"));
                producto.setCodigo(rs.getString("codigo"));
                producto.setNombre(rs.getString("nombre"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setStock(rs.getInt("stock"));
                producto.setActivo(rs.getBoolean("activo"));

                // Crear y asignar la categoría
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("categoria_id"));
                categoria.setNombre(rs.getString("categoria_nombre"));
                categoria.setDescripcion(rs.getString("categoria_descripcion"));

                producto.setCategoria(categoria);

                productos.add(producto);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener productos: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }

        return productos;
    }

    public boolean actualizarStock(int productoId, int nuevoStock, int usuarioId, String nota) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean resultado = false;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();
            conn.setAutoCommit(false); // Iniciar transacción

            // stock actual
            String sqlObtenerStock = "SELECT stock FROM PRODUCTO WHERE id = ?";
            pstmt = conn.prepareStatement(sqlObtenerStock);
            pstmt.setInt(1, productoId);
            ResultSet rs = pstmt.executeQuery();

            int stockActual = 0;
            if (rs.next()) {
                stockActual = rs.getInt("stock");
            } else {
                throw new SQLException("No se encontró el producto con ID: " + productoId);
            }

            rs.close();
            pstmt.close();

            // Actualizamos el stock
            String sqlActualizarStock = "UPDATE PRODUCTO SET stock = ? WHERE id = ?";
            pstmt = conn.prepareStatement(sqlActualizarStock);
            pstmt.setInt(1, nuevoStock);
            pstmt.setInt(2, productoId);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                pstmt.close();

                // Registramos el movimiento en el historial
                String sqlHistorial = "INSERT INTO PRODUCTO_HISTORIAL " +
                        "(producto_id, stock_anterior, stock_nuevo, tipo_movimiento, usuario_id, nota) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";

                pstmt = conn.prepareStatement(sqlHistorial);
                pstmt.setInt(1, productoId);
                pstmt.setInt(2, stockActual);
                pstmt.setInt(3, nuevoStock);
                pstmt.setString(4, nuevoStock > stockActual ? "Entrada" : "Salida");
                pstmt.setInt(5, usuarioId);
                pstmt.setString(6, nota);

                pstmt.executeUpdate();

                conn.commit(); // Confirmar transacción
                resultado = true;
            } else {
                conn.rollback(); // Revertir transacción
            }

        } catch (SQLException e) {
            try {
                if (conn != null)
                    conn.rollback(); // Revertir transacción en caso de error
            } catch (SQLException ex) {
                System.err.println("Error al hacer rollback: " + ex.getMessage());
            }

            JOptionPane.showMessageDialog(null, "Error al actualizar stock: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true); // Restaurar autocommit
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }

        return resultado;
    }

    public List<Categoria> obtenerCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();

            String sql = "SELECT id, nombre, descripcion FROM CATEGORIA ORDER BY nombre";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("id"));
                categoria.setNombre(rs.getString("nombre"));
                categoria.setDescripcion(rs.getString("descripcion"));

                categorias.add(categoria);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener categorías: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }

        return categorias;
    }

    public boolean guardarProducto(Producto producto) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean resultado = false;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();

            String sql = "INSERT INTO PRODUCTO (codigo, nombre, descripcion, precio, stock, categoria_id, activo) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, producto.getCodigo());
            pstmt.setString(2, producto.getNombre());
            pstmt.setString(3, producto.getDescripcion());
            pstmt.setDouble(4, producto.getPrecio());
            pstmt.setInt(5, producto.getStock());
            pstmt.setInt(6, producto.getCategoria().getId());
            pstmt.setBoolean(7, producto.isActivo());

            int filasAfectadas = pstmt.executeUpdate();
            resultado = (filasAfectadas > 0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar el producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }

        return resultado;
    }

    public boolean actualizarProducto(Producto producto) {

        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean resultado = false;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();

            String sql = "UPDATE PRODUCTO SET nombre = ?, descripcion = ?, precio = ?, " +
                    "categoria_id = ?, activo = ? WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, producto.getNombre());
            pstmt.setString(2, producto.getDescripcion());
            pstmt.setDouble(3, producto.getPrecio());
            pstmt.setInt(4, producto.getCategoria().getId());
            pstmt.setBoolean(5, producto.isActivo());
            pstmt.setInt(6, producto.getId());

            int filasAfectadas = pstmt.executeUpdate();
            resultado = (filasAfectadas > 0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar el producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }

        return resultado;
    }

    public Producto obtenerProductoPorId(int id) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Producto producto = null;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();

            String sql = "SELECT p.id, p.codigo, p.nombre, p.descripcion, p.precio, p.stock, " +
                    "p.categoria_id, c.nombre as categoria_nombre, c.descripcion as categoria_descripcion, " +
                    "p.activo " +
                    "FROM PRODUCTO p " +
                    "INNER JOIN CATEGORIA c ON p.categoria_id = c.id " +
                    "WHERE p.id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                producto = new Producto();
                producto.setId(rs.getInt("id"));
                producto.setCodigo(rs.getString("codigo"));
                producto.setNombre(rs.getString("nombre"));
                producto.setDescripcion(rs.getString("descripcion"));
                producto.setPrecio(rs.getDouble("precio"));
                producto.setStock(rs.getInt("stock"));
                producto.setActivo(rs.getBoolean("activo"));

                // Crear y asignar la categoría
                Categoria categoria = new Categoria();
                categoria.setId(rs.getInt("categoria_id"));
                categoria.setNombre(rs.getString("categoria_nombre"));
                categoria.setDescripcion(rs.getString("categoria_descripcion"));

                producto.setCategoria(categoria);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener el producto: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (rs != null)
                    rs.close();
                if (pstmt != null)
                    pstmt.close();
                if (conn != null)
                    conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }

        return producto;
    }
}