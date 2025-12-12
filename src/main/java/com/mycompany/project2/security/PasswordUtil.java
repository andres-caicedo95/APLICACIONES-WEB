package com.mycompany.project2.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {
    
    /**
     * Genera un hash BCrypt para una contraseña en texto plano.
     * Este método debe usarse al registrar un nuevo usuario o cambiar una contraseña.
     */
    public static String hashPassword(String plainTextPassword) {
        // Genera una sal (salt) y hashea la contraseña.
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(10)); // Se recomienda usar un factor de costo (10)
    }

    /**
     * Verifica una contraseña en texto plano contra un hash almacenado.
     * Este método se usa durante el inicio de sesión.
     * @param plainTextPassword La contraseña ingresada por el usuario.
     * @param hashedPassword El hash almacenado en la base de datos.
     * @return true si coinciden, false en caso contrario.
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        
        // **CORRECCIÓN CRÍTICA:** Manejo de hashes inválidos o nulos.
        // Si el hash es nulo, vacío o demasiado corto (no es BCrypt), prevenimos el error
        // 'Invalid salt version' y retornamos falso inmediatamente.
        if (hashedPassword == null || hashedPassword.isEmpty() || hashedPassword.length() < 60) {
            return false;
        }
        
        // Ejecuta la verificación de BCrypt de manera segura
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}