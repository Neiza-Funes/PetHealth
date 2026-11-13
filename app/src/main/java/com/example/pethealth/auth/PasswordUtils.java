package com.example.pethealth.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilidad sencilla para generar el hash SHA-256 utilizado por PetHealth.
 * Se mantiene como una medida académica básica para evitar almacenar contraseñas en texto plano.
 */
public final class PasswordUtils {

    private PasswordUtils() {
    }

    /**
     * Genera una representación SHA-256 de la contraseña recibida.
     *
     * @param password contraseña ingresada por el usuario
     * @return hash hexadecimal SHA-256
     * @throws IllegalStateException si SHA-256 no está disponible en el entorno
     */
    public static String hashPassword(String password) {
        String valor = password == null ? "" : password;

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(valor.getBytes(StandardCharsets.UTF_8));
            StringBuilder resultado = new StringBuilder();

            for (byte b : hash) {
                resultado.append(String.format("%02x", b & 0xff));
            }

            return resultado.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 no está disponible", e);
        }
    }
}
