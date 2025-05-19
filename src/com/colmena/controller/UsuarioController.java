// src/com/colmena/controller/UsuarioController.java
package com.colmena.controller;

import com.colmena.data.ConexionBDD;
import com.colmena.model.Usuario;
import com.colmena.util.SecurityUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;

import org.mindrot.jbcrypt.BCrypt;

public class UsuarioController {
    
    // Método para validar las credenciales del usuario
    public Usuario validarUsuario(String nombreUsuario, String passwordIn) {
        Usuario usuario = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
         try {
            // Obtener conexión
            conn = ConexionBDD.getInstancia().establecerConexion();
            if (conn == null) {                
                return null;
            }
                        
            String sql = "SELECT id, nombre, apellido, email, password, rol, activo " +
                        "FROM USUARIO " +
                        "WHERE nombre = ?  AND activo = 1";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, nombreUsuario);            
                    
            rs = pstmt.executeQuery();
            
            // verificamos la contraseña
            if (rs.next()) {                
                String hashedPassword = rs.getString("password");                                
                                
                try {
                    boolean bcryptMatch = BCrypt.checkpw(passwordIn, hashedPassword);                    
                    
                    if (bcryptMatch) {                        
                        usuario = new Usuario();
                        usuario.setId(rs.getInt("id"));
                        usuario.setNombre(rs.getString("nombre"));
                        usuario.setApellido(rs.getString("apellido"));
                        usuario.setEmail(rs.getString("email"));
                        usuario.setRol(rs.getString("rol"));
                        usuario.setActivo(rs.getBoolean("activo"));
                    } else {
                        JOptionPane.showMessageDialog(null, "Contraseña incorrecta.");
                    }
                } catch (Exception e) {                    
                                        
                    JOptionPane.showMessageDialog(null, "Error al verificar las contraseñas");                  
                }
            } else {
                JOptionPane.showMessageDialog(null, "Usuario no encontrado.");
            }
            
        } catch (SQLException e) {            
            JOptionPane.showMessageDialog(null, "Error al validar usuario: " + e.getMessage(),
                                        "Error de Autenticación", JOptionPane.ERROR_MESSAGE);
        } finally {            
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return usuario;
    }
    
    // Método para generar un hash BCrypt
    public String generarHashBCrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    
    // Método para comparar una contraseña con un hash BCrypt
    public boolean verificarPassword(String password, String hashedPassword) {
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    // Método para cambiar la contraseña de un usuario
    public boolean cambiarPassword(int usuarioId, String nuevaPassword) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean resultado = false;
        
        try {
            // Obtener conexión
            conn = ConexionBDD.getInstancia().establecerConexion();
            
            // Encriptar la nueva contraseña
            String hashedPassword = SecurityUtil.hashPassword(nuevaPassword);
            
            // Actualizar la contraseña en la base de datos
            String sql = "UPDATE USUARIO SET password = ? WHERE id = ?";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, hashedPassword);
            pstmt.setInt(2, usuarioId);
            
            int filasAfectadas = pstmt.executeUpdate();
            resultado = (filasAfectadas > 0);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cambiar contraseña: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Cerrar recursos
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return resultado;
    }
    
    // Método para crear un nuevo usuario con contraseña encriptada
    public boolean crearUsuario(Usuario usuario, String password) {
        Connection conn = null;
        PreparedStatement pstmt = null;
        boolean resultado = false;
        
        try {
            // Obtener conexión
            conn = ConexionBDD.getInstancia().establecerConexion();
            
            // Encriptar la contraseña
            String hashedPassword = SecurityUtil.hashPassword(password);
            
            // Insertar el nuevo usuario
            String sql = "INSERT INTO USUARIO (nombre, apellido, email, password, rol, activo) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, hashedPassword);
            pstmt.setString(5, usuario.getRol());
            pstmt.setBoolean(6, usuario.isActivo());
            
            int filasAfectadas = pstmt.executeUpdate();
            resultado = (filasAfectadas > 0);
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al crear usuario: " + e.getMessage(),
                                        "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Cerrar recursos
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error al cerrar recursos: " + e.getMessage());
            }
        }
        
        return resultado;
    }
}