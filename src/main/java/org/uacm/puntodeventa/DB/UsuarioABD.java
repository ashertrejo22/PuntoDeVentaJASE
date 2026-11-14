package org.uacm.puntodeventa.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioABD {
    
    public static boolean existeUsuario(String nombre) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Usuario WHERE nombre = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.getInt(1) > 0;
            }
        }
    }

    public static void actualizarContrasena(String nombre, String nuevaContrasena) throws SQLException {
        String sql = "UPDATE Usuario SET contrasena = ? WHERE nombre = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nuevaContrasena);
            pstmt.setString(2, nombre);
            pstmt.executeUpdate();
        }
    }

    public static boolean validarCredenciales(String nombre, String contrasena) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Usuario WHERE nombre = ? AND contrasena = ?";
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, contrasena);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.getInt(1) > 0;
            }
        }
    }
    

    public static int[] obtenerDatosUsuario(String nombre, String contrasena) throws SQLException {
        String sql = "SELECT id_usuario FROM Usuario WHERE nombre = ? AND contrasena = ?";
        
        try (Connection conn = ConexionBD.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, nombre);
            pstmt.setString(2, contrasena);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id_usuario");
                    
                    int nivel = nombre.equalsIgnoreCase("Administrador") ? 1 : 2;
                    
                    return new int[]{id, nivel};
                }
            }
        }
        return null; // Retorna null si las credenciales son incorrectas
    }
}