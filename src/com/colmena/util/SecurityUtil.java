package com.colmena.util;

import org.mindrot.jbcrypt.BCrypt;

public class SecurityUtil {
    
    // Genera un hash de una contraseña usando BCrypt
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }
    
    // Verifica si una contraseña coincide con un hash
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        if (hashedPassword == null || !hashedPassword.startsWith("$2a$") && !hashedPassword.startsWith("$2b$")) {
            return false; // Hash inválido
        }
        
        try {
            return BCrypt.checkpw(plainTextPassword, hashedPassword);
        } catch (Exception e) {
            return false; // Error al comprobar
        }
    }
}