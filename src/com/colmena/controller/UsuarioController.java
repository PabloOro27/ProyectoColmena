// src/com/colmena/controller/UsuarioController.java
package com.colmena.controller;

import com.colmena.data.ConexionBDD;
import com.colmena.model.Categoria;
import com.colmena.model.Cliente;
import com.colmena.model.Producto;
import com.colmena.model.Rol;
import com.colmena.model.Usuario;
import com.colmena.util.SecurityUtil;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import org.mindrot.jbcrypt.BCrypt;

public class UsuarioController {
    
    // Metodo para validar las credenciales del usuario
    public Usuario validarUsuario(String nombreUsuario, String passwordIn) {
        Usuario usuario = null;
        Rol rol = null;
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
         try {
            // Obtener conexión
            conn = ConexionBDD.getInstancia().establecerConexion();
            if (conn == null) {                
                return null;
            }
                        
            String sql = "SELECT u.id, u.nombre, apellido, email, password, u.idRol, u.activo, r.Nombre as nombreRol " +
                        "FROM USUARIO u " + 
                        "INNER JOIN ROL r on u.idRol = r.id  " + 
                        "WHERE u.nombre = ?  AND activo = 1";
            
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
                        usuario.setIdRol(rs.getInt("idRol"));
                        usuario.setActivo(rs.getBoolean("activo"));

                        //setear rol 
                        rol = new Rol();
                        rol.setId(rs.getInt("idRol"));
                        rol.setNombre(rs.getString("nombreRol"));

                        usuario.setRol(rol);
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
    
    // Metodo para generar un hash BCrypt
    public String generarHashBCrypt(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt(12));
    }
    
    // Metodo para comparar una contraseña con un hash BCrypt
    public boolean verificarPassword(String password, String hashedPassword) {
        try {
            return BCrypt.checkpw(password, hashedPassword);
        } catch (Exception e) {
            return false;
        }
    }

    // Metodo para cambiar la contraseña de un usuario
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
    
    // Metodo para crear un nuevo usuario con contraseña encriptada
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
            String sql = "INSERT INTO USUARIO (nombre, apellido, email, password, idRol, activo) " +
                       "VALUES (?, ?, ?, ?, ?, ?)";
            
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, usuario.getNombre());
            pstmt.setString(2, usuario.getApellido());
            pstmt.setString(3, usuario.getEmail());
            pstmt.setString(4, hashedPassword);
            pstmt.setInt(5, usuario.getRol().getId());
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

    // Metodo para traer roles 
    public List<Rol> obtenerRoles(){
        List<Rol> roles = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();

            String sql = "SELECT id, Nombre" + 
                        "FROM ROL";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Rol rol = new Rol();
                rol.setId(rs.getInt("id"));                         
                rol.setNombre(rs.getString(sql));

                roles.add(rol);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener Roles: " + e.getMessage(),
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

        return roles;
    }

    // Metodo para obtener todos los usuarios
    public List<Usuario> obtenerUsuarios()
    {
        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = ConexionBDD.getInstancia().establecerConexion();

            String sql = "SELECT u.id, u.nombre, apellido, email, password, u.idRol, u.activo, r.Nombre as nombreRol " +
                        "FROM USUARIO u  " + 
                        "INNER JOIN ROL r on u.idRol = r.id  " +
                        "WHERE activo = 1 " + 
                        "ORDER BY u.id DESC";

            pstmt = conn.prepareStatement(sql);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellido(rs.getString("apellido"));
                usuario.setEmail(rs.getString("email"));
                usuario.setPassword(rs.getString("password"));
                usuario.setIdRol(rs.getInt("idRol"));
                usuario.setActivo(rs.getBoolean("activo"));

                Rol rol = new Rol();
                rol.setId(rs.getInt(rs.getInt("idRol")));
                rol.setNombre(rs.getString("nombreRol"));
                
                usuario.setRol(rol);
                usuarios.add(usuario);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al obtener los usuarios: " + e.getMessage(),
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

        return usuarios;
    }

    // Metodo para busqueda de Usuarios
    public List<Usuario> buscarUsuario(String textoBusqueda) {
        List<Usuario> usuarios = new ArrayList<>();
        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        
        try {
            conn = ConexionBDD.getInstancia().establecerConexion();
            
            String sql = "SELECT u.id, u.nombre, apellido, email, password, u.idRol, u.activo, r.Nombre as nombreRol " +
                        "FROM USUARIO u  " + 
                        "INNER JOIN ROL r on u.idRol = r.id  " +
                        "WHERE u.nombre LIKE ? OR u.apellido LIKE ? " +
                        "ORDER BY u.nombre, u.apellido";
            
            pstmt = conn.prepareStatement(sql);
            String parametro = "%" + textoBusqueda + "%";
            pstmt.setString(1, parametro);
            pstmt.setString(2, parametro);
            
            rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setApellido(rs.getString("apellido"));
                usuario.setEmail(rs.getString("email"));
                usuario.setPassword(rs.getString("password"));
                usuario.setIdRol(rs.getInt("idRol"));
                usuario.setActivo(rs.getBoolean("activo"));
                                
                Rol rol = new Rol();
                rol.setId(rs.getInt(rs.getInt("idRol")));
                rol.setNombre(rs.getString("nombreRol"));
                
                usuario.setRol(rol);
                usuarios.add(usuario);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al buscar usuarios: " + e.getMessage(),
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
        
        return usuarios;
    }

    // Método para actualizar datos básicos del usuario (agregar a UsuarioController.java)
public boolean actualizarUsuario(Usuario usuario) {
    Connection conn = null;
    PreparedStatement pstmt = null;
    boolean resultado = false;
    
    try {
        // Obtener conexión
        conn = ConexionBDD.getInstancia().establecerConexion();
        if (conn == null) {
            return false;
        }
        
        // SQL para actualizar datos básicos del usuario
        String sql = "UPDATE USUARIO SET nombre = ?, apellido = ?, email = ?, idRol = ?, activo = ? WHERE id = ?";
        
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, usuario.getNombre());
        pstmt.setString(2, usuario.getApellido());
        pstmt.setString(3, usuario.getEmail());
        pstmt.setInt(4, usuario.getRol().getId());
        pstmt.setBoolean(5, usuario.isActivo());
        pstmt.setInt(6, usuario.getId());
        
        int filasAfectadas = pstmt.executeUpdate();
        resultado = (filasAfectadas > 0);
        
        if (resultado) {
            System.out.println("Usuario actualizado correctamente: " + usuario.getNombre() + " " + usuario.getApellido());
        }
        
    } catch (SQLException e) {
        System.err.println("Error SQL al actualizar usuario: " + e.getMessage());
        JOptionPane.showMessageDialog(null, 
            "Error al actualizar usuario: " + e.getMessage(),
            "Error de Base de Datos", 
            JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        System.err.println("Error general al actualizar usuario: " + e.getMessage());
        JOptionPane.showMessageDialog(null, 
            "Error inesperado al actualizar usuario: " + e.getMessage(),
            "Error", 
            JOptionPane.ERROR_MESSAGE);
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

// Método mejorado para cambiar contraseña (opcional: mejora del que ya tienes)
public boolean cambiarPasswordUsuario(int usuarioId, String nuevaPassword) {
    Connection conn = null;
    PreparedStatement pstmt = null;
    boolean resultado = false;
    
    try {
        // Obtener conexión
        conn = ConexionBDD.getInstancia().establecerConexion();
        if (conn == null) {
            return false;
        }
        
        // Encriptar la nueva contraseña
        String hashedPassword = SecurityUtil.hashPassword(nuevaPassword);
        
        // Actualizar la contraseña en la base de datos
        String sql = "UPDATE USUARIO SET password = ? WHERE id = ? AND activo = 1";
        
        pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, hashedPassword);
        pstmt.setInt(2, usuarioId);
        
        int filasAfectadas = pstmt.executeUpdate();
        resultado = (filasAfectadas > 0);
        
        if (resultado) {
            System.out.println("Contraseña actualizada correctamente para usuario ID: " + usuarioId);
        } else {
            System.out.println("No se pudo actualizar la contraseña. Verifique que el usuario existe y esté activo.");
        }
        
    } catch (SQLException e) {
        System.err.println("Error SQL al cambiar contraseña: " + e.getMessage());
        JOptionPane.showMessageDialog(null, 
            "Error al cambiar contraseña: " + e.getMessage(),
            "Error de Base de Datos", 
            JOptionPane.ERROR_MESSAGE);
    } catch (Exception e) {
        System.err.println("Error general al cambiar contraseña: " + e.getMessage());
        JOptionPane.showMessageDialog(null, 
            "Error inesperado al cambiar contraseña: " + e.getMessage(),
            "Error", 
            JOptionPane.ERROR_MESSAGE);
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

// Método para obtener un usuario por ID (útil para recargar datos después de editar)
public Usuario obtenerUsuarioPorId(int id) {
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
        
        String sql = "SELECT id, nombre, apellido, email, idRol, activo " +
                    "FROM USUARIO " +
                    "WHERE id = ?";
        
        pstmt = conn.prepareStatement(sql);
        pstmt.setInt(1, id);
        
        rs = pstmt.executeQuery();
        
        if (rs.next()) {
            usuario = new Usuario();
            usuario.setId(rs.getInt("id"));
            usuario.setNombre(rs.getString("nombre"));
            usuario.setApellido(rs.getString("apellido"));
            usuario.setEmail(rs.getString("email"));
            usuario.setIdRol(rs.getInt("idRol"));
            usuario.setActivo(rs.getBoolean("activo"));
        }
        
    } catch (SQLException e) {
        System.err.println("Error SQL al obtener usuario: " + e.getMessage());
        JOptionPane.showMessageDialog(null, 
            "Error al obtener usuario: " + e.getMessage(),
            "Error de Base de Datos", 
            JOptionPane.ERROR_MESSAGE);
    } finally {
        // Cerrar recursos
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

}