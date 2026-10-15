package com.example.pethealth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class PasswordUtils {

    private PasswordUtils() {
    }

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
