// src/com/colmena/controller/ClienteController.java
package com.colmena.controller;

import com.colmena.data.ConexionBDD;
import com.colmena.model.Cliente;
import com.colmena.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;

public class ClienteController {
    
    // Obtiene todos los clientes activos
    public List<Cliente> obtenerClientes() {
        List<Cliente> clientes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBDD.getInstancia().establecerConexion();
            
            String sql = "SELECT c.id, c.nombre, c.apellido, c.telefono, c.email, c.direccion, " +
                        "c.saldo_pendiente, c.fecha_registro, c.usuario_id, u.nombre as usuario_nombre, " +
                        "u.apellido as usuario_apellido " +
                        "FROM CLIENTE c " +
                        "LEFT JOIN USUARIO u ON c.usuario_id = u.id " +
                        "ORDER BY c.nombre, c.apellido";
            
            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setEmail(rs.getString("email"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setSaldoPendiente(rs.getDouble("saldo_pendiente"));
                cliente.setFechaRegistro(rs.getDate("fecha_registro"));
                                
                int usuarioId = rs.getInt("usuario_id");
                if (!rs.wasNull()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(usuarioId);
                    usuario.setNombre(rs.getString("usuario_nombre"));
                    usuario.setApellido(rs.getString("usuario_apellido"));
                    cliente.setUsuario(usuario);
                }
                
                clientes.add(cliente);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener clientes: " + e.getMessage(),
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
        
        return clientes;
    }
    
    // Guarda un nuevo cliente y retorna el ID generado
    public int guardarCliente(Cliente cliente) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet generatedKeys = null;
        int clienteId = -1;
        
        try {
            conn = ConexionBDD.getInstancia().establecerConexion();
            
            String sql = "INSERT INTO CLIENTE (nombre, apellido, telefono, email, direccion, usuario_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getEmail());
            pstmt.setString(5, cliente.getDireccion());
            
            // Si hay un usuario asignado
            if (cliente.getUsuario() != null) {
                pstmt.setInt(6, cliente.getUsuario().getId());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            int filasAfectadas = pstmt.executeUpdate();
            
            if (filasAfectadas > 0) {
                generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    clienteId = generatedKeys.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al guardar cliente: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return clienteId;
    }
    
    // Actualiza un cliente existente
    public boolean actualizarCliente(Cliente cliente) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean resultado = false;
        
        try {
            conn = ConexionBDD.getInstancia().establecerConexion();
            
            String sql = "UPDATE CLIENTE SET nombre = ?, apellido = ?, telefono = ?, " +
                        "email = ?, direccion = ?, usuario_id = ? WHERE id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, cliente.getNombre());
            pstmt.setString(2, cliente.getApellido());
            pstmt.setString(3, cliente.getTelefono());
            pstmt.setString(4, cliente.getEmail());
            pstmt.setString(5, cliente.getDireccion());
            
            // Si hay un usuario asignado
            if (cliente.getUsuario() != null) {
                pstmt.setInt(6, cliente.getUsuario().getId());
            } else {
                pstmt.setNull(6, java.sql.Types.INTEGER);
            }
            
            pstmt.setInt(7, cliente.getId());
            
            int filasAfectadas = pstmt.executeUpdate();
            resultado = (filasAfectadas > 0);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al actualizar cliente: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return resultado;
    }
    
    // Eliminacion de cliente 
    public boolean desactivarCliente(int clienteId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean resultado = false;
        
        try {
            conn = ConexionBDD.getInstancia().establecerConexion();
                        
            String sql = "UPDATE CLIENTE SET activo = 0 WHERE id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, clienteId);
            
            int filasAfectadas = pstmt.executeUpdate();
            resultado = (filasAfectadas > 0);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al desactivar cliente: " + e.getMessage(),
                                         "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return resultado;
    }
    
    // Busca un cliente por su ID
    public Cliente obtenerClientePorId(int clienteId) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        Cliente cliente = null;
        
        try {
            conn = ConexionBDD.getInstancia().establecerConexion();
            
            String sql = "SELECT c.id, c.nombre, c.apellido, c.telefono, c.email, c.direccion, " +
                        "c.saldo_pendiente, c.fecha_registro, c.usuario_id, u.nombre as usuario_nombre, " +
                        "u.apellido as usuario_apellido " +
                        "FROM CLIENTE c " +
                        "LEFT JOIN USUARIO u ON c.usuario_id = u.id " +
                        "WHERE c.id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, clienteId);
            
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setEmail(rs.getString("email"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setSaldoPendiente(rs.getDouble("saldo_pendiente"));
                cliente.setFechaRegistro(rs.getDate("fecha_registro"));
                                
                int usuarioId = rs.getInt("usuario_id");
                if (!rs.wasNull()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(usuarioId);
                    usuario.setNombre(rs.getString("usuario_nombre"));
                    usuario.setApellido(rs.getString("usuario_apellido"));
                    cliente.setUsuario(usuario);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener cliente: " + e.getMessage(),
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
        
        return cliente;
    }
    
    // Busca clientes por nombre o teléfono
    public List<Cliente> buscarClientes(String textoBusqueda) {
        List<Cliente> clientes = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBDD.getInstancia().establecerConexion();
            
            String sql = "SELECT c.id, c.nombre, c.apellido, c.telefono, c.email, c.direccion, " +
                        "c.saldo_pendiente, c.fecha_registro, c.usuario_id, u.nombre as usuario_nombre, " +
                        "u.apellido as usuario_apellido " +
                        "FROM CLIENTE c " +
                        "LEFT JOIN USUARIO u ON c.usuario_id = u.id " +
                        "WHERE c.nombre LIKE ? OR c.apellido LIKE ? OR c.telefono LIKE ? " +
                        "ORDER BY c.nombre, c.apellido";
            
            pstmt = conn.prepareStatement(sql);
            String parametro = "%" + textoBusqueda + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);
            pstmt.setString(3, parametro);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setNombre(rs.getString("nombre"));
                cliente.setApellido(rs.getString("apellido"));
                cliente.setTelefono(rs.getString("telefono"));
                cliente.setEmail(rs.getString("email"));
                cliente.setDireccion(rs.getString("direccion"));
                cliente.setSaldoPendiente(rs.getDouble("saldo_pendiente"));
                cliente.setFechaRegistro(rs.getDate("fecha_registro"));
                                
                int usuarioId = rs.getInt("usuario_id");
                if (!rs.wasNull()) {
                    Usuario usuario = new Usuario();
                    usuario.setId(usuarioId);
                    usuario.setNombre(rs.getString("usuario_nombre"));
                    usuario.setApellido(rs.getString("usuario_apellido"));
                    cliente.setUsuario(usuario);
                }
                
                clientes.add(cliente);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar clientes: " + e.getMessage(),
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
        
        return clientes;
    }
}