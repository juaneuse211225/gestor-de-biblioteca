package com.tecno.biblioteca.util;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.KeyGeneratorSpi;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class AESutil {

    // generador de clave secreta
    public static SecretKey Generar_LLave_Secreta(int i) throws Exception {
        KeyGenerator generador = KeyGenerator.getInstance("AES");
        generador.init(i);
        return generador.generateKey();
    }

    public static void Guardar_Clave(SecretKey llave, String ruta) throws IOException {
        byte[] codigo = llave.getEncoded();
        String codigollave = Base64.getEncoder().encodeToString(codigo);
        try (FileOutputStream fos = new FileOutputStream(ruta)) {
            fos.write(codigollave.getBytes());
        }
    }

    public static SecretKey Cargar_LLave(String ruta) throws IOException {
        File archivo = new File(ruta);
        byte[] codigollave = new byte[(int) archivo.length()];
        try (FileInputStream fis = new FileInputStream(archivo)) {
            fis.read(codigollave);
        }
        String keyString = new String(codigollave);
        byte[] llave_decodificada = Base64.getDecoder().decode(codigollave);
        return new SecretKeySpec(llave_decodificada, 0, llave_decodificada.length, "AES");
    }

    public static String encriptar(String str, SecretKey llave) throws Exception {
        Cipher cp = Cipher.getInstance("AES");
        cp.init(Cipher.ENCRYPT_MODE, llave);
        byte[] encriptado = cp.doFinal(str.getBytes());
        return Base64.getEncoder().encodeToString(encriptado);

    }

    public static String desepcritar(String str, SecretKey llave) throws Exception {
        Cipher cp = Cipher.getInstance("AES");
        cp.init(Cipher.DECRYPT_MODE, llave);
        byte[] desencritado = cp.doFinal(Base64.getDecoder().decode(str));

        return new String(desencritado);
    }
}
