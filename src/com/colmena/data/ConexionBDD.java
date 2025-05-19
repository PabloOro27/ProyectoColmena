package com.colmena.data; // namespace 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class ConexionBDD {    
    private static ConexionBDD instancia;
    private Connection conectar = null;
    
    // Datos de conexión
    private  String usuario = "admin";
    private  String contrasenia = "admin123";
    private  String bd = "Colmena";  
    private  String ip = "localhost";
    private  String puerto = "1433";

    private String cadena = "jdbc:sqlserver://" + ip + ":" + puerto+";" +
            "databaseName=" + bd +";" +
            "encrypt=true;" +
            "trustServerCertificate=true;" +
            "user="+ usuario +";" +
            "password="+ contrasenia +";";
        
    private ConexionBDD() {
    }
        
    public static ConexionBDD getInstancia() {
        if (instancia == null) {
            instancia = new ConexionBDD();
        }
        return instancia;
    }
        
    public Connection establecerConexion() {
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            System.out.println("Driver de SQL Server encontrado correctamente");
                        
            conectar = DriverManager.getConnection(cadena, usuario, contrasenia);
                        
            if (System.getProperty("dev.mode") != null) {
                JOptionPane.showMessageDialog(null, "La conexión a Colmena fue exitosa");
            }
            
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "Error: Driver SQL Server no encontrado\n" + e.getMessage(), 
                                        "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error de conexión a la base de datos Colmena\n" + e.getMessage(), 
                                        "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error general: " + e.getMessage(), 
                                        "Error de Conexión", JOptionPane.ERROR_MESSAGE);
        }
        
        return conectar;
    }
    
    public void cerrarConexion() {
        if (conectar != null) {
            try {
                conectar.close();
                System.out.println("Conexión cerrada correctamente");
            } catch (SQLException e) {
                System.err.println("Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
        
    public boolean estaConectado() {
        try {
            return conectar != null && !conectar.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}