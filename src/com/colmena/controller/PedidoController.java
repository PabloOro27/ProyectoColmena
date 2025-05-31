package com.colmena.controller;

import com.colmena.data.ConexionBDD;
import com.colmena.model.Cliente;
import com.colmena.model.DetallePedido;
import com.colmena.model.Pedido;
import com.colmena.model.Producto;
import com.colmena.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class PedidoController {

    // Obtiene todos los pedidos
    public List<Pedido> obtenerPedidos(int clienteId) {
        List<Pedido> pedidos = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();

            String sql = "SELECT p.id, p.cliente_id, p.fecha_pedido, p.estado, p.total, " +
                    "p.saldo_pagado, p.metodo_pago, p.notas, " +
                    "c.nombre as cliente_nombre, c.apellido as cliente_apellido, " +
                    "u.id as usuario_id, u.nombre as usuario_nombre, u.apellido as usuario_apellido " +
                    "FROM PEDIDO p " +
                    "INNER JOIN CLIENTE c ON p.cliente_id = c.id " +
                    "LEFT JOIN USUARIO u ON c.usuario_id = u.id ";

            // Filtro por cliente
            if (clienteId > 0) {
                sql += "WHERE p.cliente_id = ? ";
            }

            sql += "ORDER BY p.fecha_pedido DESC";

            pstmt = conn.prepareStatement(sql);

            if (clienteId > 0) {
                pstmt.setInt(1, clienteId);
            }

            rs = pstmt.executeQuery();

            while (rs.next()) {
                Pedido pedido = new Pedido();
                pedido.setId(rs.getInt("id"));
                pedido.setFecha(rs.getDate("fecha_pedido"));
                pedido.setEstado(rs.getString("estado"));
                pedido.setTotal(rs.getDouble("total"));
                pedido.setSaldoPagado(rs.getDouble("saldo_pagado"));

                // Crear y asignar el cliente
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("cliente_id"));
                cliente.setNombre(rs.getString("cliente_nombre"));
                cliente.setApellido(rs.getString("cliente_apellido"));

                // Si el cliente tiene un usuario asignado
                int usuarioId = rs.getInt("usuario_id");
                if (!rs.wasNull()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(usuarioId);
                    usuario.setNombre(rs.getString("usuario_nombre"));
                    usuario.setApellido(rs.getString("usuario_apellido"));
                    cliente.setUsuario(usuario);
                }

                pedido.setCliente(cliente);

                // Cargar detalles del pedido
                List<DetallePedido> detalles = obtenerDetallesPedido(pedido.getId());
                pedido.setDetalles(detalles);

                pedidos.add(pedido);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener pedidos: " + e.getMessage(),
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

        return pedidos;
    }

    // Obtiene un pedido por su ID
    public Pedido obtenerPedidoPorId(int pedidoId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Pedido pedido = null;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();

            String sql = "SELECT p.id, p.cliente_id, p.fecha_pedido, p.estado, p.total, " +
                    "p.saldo_pagado, p.metodo_pago, p.notas, " +
                    "c.nombre as cliente_nombre, c.apellido as cliente_apellido, " +
                    "u.id as usuario_id, u.nombre as usuario_nombre, u.apellido as usuario_apellido " +
                    "FROM PEDIDO p " +
                    "INNER JOIN CLIENTE c ON p.cliente_id = c.id " +
                    "LEFT JOIN USUARIO u ON c.usuario_id = u.id " +
                    "WHERE p.id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, pedidoId);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                pedido = new Pedido();
                pedido.setId(rs.getInt("id"));
                pedido.setFecha(rs.getDate("fecha_pedido"));
                pedido.setEstado(rs.getString("estado"));
                pedido.setTotal(rs.getDouble("total"));

                // Crear y asignar el cliente
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("cliente_id"));
                cliente.setNombre(rs.getString("cliente_nombre"));
                cliente.setApellido(rs.getString("cliente_apellido"));

                // Si el cliente tiene un usuario asignado
                int usuarioId = rs.getInt("usuario_id");
                if (!rs.wasNull()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(usuarioId);
                    usuario.setNombre(rs.getString("usuario_nombre"));
                    usuario.setApellido(rs.getString("usuario_apellido"));
                    cliente.setUsuario(usuario);
                }

                pedido.setCliente(cliente);

                // Cargar detalles del pedido
                List<DetallePedido> detalles = obtenerDetallesPedido(pedido.getId());
                pedido.setDetalles(detalles);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener pedido: " + e.getMessage(),
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

        return pedido;
    }

    // Obtiene los detalles de un pedido
    private List<DetallePedido> obtenerDetallesPedido(int pedidoId) {
        List<DetallePedido> detalles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();

            String sql = "SELECT dp.id, dp.producto_id, dp.cantidad, dp.precio_unitario, dp.subtotal, " +
                    "p.codigo, p.nombre as producto_nombre, p.descripcion as producto_descripcion, " +
                    "c.id as categoria_id, c.nombre as categoria_nombre " +
                    "FROM DETALLE_PEDIDO dp " +
                    "INNER JOIN PRODUCTO p ON dp.producto_id = p.id " +
                    "INNER JOIN CATEGORIA c ON p.categoria_id = c.id " +
                    "WHERE dp.pedido_id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, pedidoId);

            rs = pstmt.executeQuery();

            while (rs.next()) {
                // Crear el producto
                Producto producto = new Producto();
                producto.setId(rs.getInt("producto_id"));
                producto.setCodigo(rs.getString("codigo"));
                producto.setNombre(rs.getString("producto_nombre"));
                producto.setDescripcion(rs.getString("producto_descripcion"));

                // Crear y asignar categoría al producto
                com.colmena.model.Categoria categoria = new com.colmena.model.Categoria();
                categoria.setId(rs.getInt("categoria_id"));
                categoria.setNombre(rs.getString("categoria_nombre"));
                producto.setCategoria(categoria);

                // Crear el detalle
                DetallePedido detalle = new DetallePedido(
                        producto,
                        rs.getInt("cantidad"),
                        rs.getDouble("precio_unitario"));
                detalle.setId(rs.getInt("id"));

                detalles.add(detalle);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener detalles del pedido: " + e.getMessage());
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

        return detalles;
    }

    // Crea un nuevo pedido
    public int crearPedido(Pedido pedido) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        int pedidoId = -1;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();
            conn.setAutoCommit(false); // Iniciar transacción

            // Insertar pedido
            String sql = "INSERT INTO PEDIDO (cliente_id, fecha_pedido, estado, total, saldo_pagado, metodo_pago, notas) "
                    +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setInt(1, pedido.getCliente().getId());

            // Si la fecha es null, usar la fecha actual
            if (pedido.getFecha() != null) {
                pstmt.setDate(2, pedido.getFecha());
            } else {
                pstmt.setDate(2, new Date(System.currentTimeMillis()));
            }

            pstmt.setString(3, pedido.getEstado());
            pstmt.setDouble(4, pedido.getTotal());
            pstmt.setDouble(5, 0.0); // Saldo pagado inicial es 0
            pstmt.setString(6, "Efectivo"); // Método de pago por defecto
            pstmt.setString(7, "");

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    pedidoId = generatedKeys.getInt(1);
                    pedido.setId(pedidoId);

                    // Insertar detalles del pedido
                    if (insertarDetallesPedido(conn, pedido)) {
                        conn.commit(); // Confirmar transacción
                    } else {
                        pedidoId = -1;
                        conn.rollback(); // Revertir transacción
                    }
                }
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

            JOptionPane.showMessageDialog(null, "Error al crear pedido: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null)
                    generatedKeys.close();
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

        return pedidoId;
    }

    // Inserta los detalles de un pedido
    private boolean insertarDetallesPedido(Connection conn, Pedido pedido) throws SQLException {
        PreparedStatement pstmt = null;
        boolean resultado = true;

        try {
            String sql = "INSERT INTO DETALLE_PEDIDO (pedido_id, producto_id, cantidad, precio_unitario, subtotal) " +
                    "VALUES (?, ?, ?, ?, ?)";

            pstmt = conn.prepareStatement(sql);

            for (DetallePedido detalle : pedido.getDetalles()) {
                pstmt.setInt(1, pedido.getId());
                pstmt.setInt(2, detalle.getProducto().getId());
                pstmt.setInt(3, detalle.getCantidad());
                pstmt.setDouble(4, detalle.getPrecioUnitario());
                pstmt.setDouble(5, detalle.getSubtotal());

                int filasAfectadas = pstmt.executeUpdate();
                if (filasAfectadas <= 0) {
                    resultado = false;
                    break;
                }

                // Actualizar el stock del producto
                if (!actualizarStockProducto(conn, detalle.getProducto().getId(), detalle.getCantidad())) {
                    resultado = false;
                    break;
                }
            }
        } finally {
            if (pstmt != null)
                pstmt.close();
        }

        return resultado;
    }

    // Actualiza el stock de un producto al realizar un pedido
    private boolean actualizarStockProducto(Connection conn, int productoId, int cantidad) throws SQLException {
        PreparedStatement pstmt = null;
        boolean resultado = false;

        try {
            String sql = "UPDATE PRODUCTO SET stock = stock - ? WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, cantidad);
            pstmt.setInt(2, productoId);

            int filasAfectadas = pstmt.executeUpdate();
            resultado = (filasAfectadas > 0);
        } finally {
            if (pstmt != null)
                pstmt.close();
        }

        return resultado;
    }

    // Actualiza el estado de un pedido
    public boolean actualizarEstadoPedido(int pedidoId, String nuevoEstado) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean resultado = false;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();

            String sql = "UPDATE PEDIDO SET estado = ? WHERE id = ?";

            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nuevoEstado);
            pstmt.setInt(2, pedidoId);

            int filasAfectadas = pstmt.executeUpdate();
            resultado = (filasAfectadas > 0);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar estado del pedido: " + e.getMessage(),
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

    // Registra un pago para un pedido
    public boolean registrarPago(int pedidoId, double monto, String metodoPago, String referencia) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean resultado = false;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();
            conn.setAutoCommit(false); // Iniciar transacción

            // Insertar en el historial de pagos
            String sqlHistorial = "INSERT INTO HISTORIAL_PAGO (pedido_id, fecha_pago, monto, metodo_pago, referencia) "
                    +
                    "VALUES (?, GETDATE(), ?, ?, ?)";

            pstmt = conn.prepareStatement(sqlHistorial);
            pstmt.setInt(1, pedidoId);
            pstmt.setDouble(2, monto);
            pstmt.setString(3, metodoPago);
            pstmt.setString(4, referencia);

            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas > 0) {
                conn.commit();
                resultado = true;

                try {
                    pstmt.close();
                    String sqlVerificar = "SELECT total, saldo_pagado FROM PEDIDO WHERE id = ?";
                    pstmt = conn.prepareStatement(sqlVerificar);
                    pstmt.setInt(1, pedidoId);
                    ResultSet rs = pstmt.executeQuery();

                    if (rs.next()) {
                        double total = rs.getDouble("total");
                        double saldoPagado = rs.getDouble("saldo_pagado");

                        if (saldoPagado >= total) {
                            rs.close();
                            pstmt.close();

                            actualizarEstadoPedido(pedidoId, "Pagado");
                        } else {
                            rs.close();
                        }
                    }
                } catch (SQLException ex) {
                    System.err.println("Error al verificar saldo: " + ex.getMessage());
                }
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

            JOptionPane.showMessageDialog(null, "Error al registrar pago: " + e.getMessage(),
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
}