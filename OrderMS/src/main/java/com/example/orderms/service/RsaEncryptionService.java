package com.example.orderms.service;

import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import java.security.*;
import java.util.Base64;

/**
 * Servicio para el cifrado y descifrado de datos utilizando el algoritmo RSA.
 * Este servicio genera un par de claves RSA (pública y privada) al momento de la instanciación.
 */
@Service
public class RsaEncryptionService {

    private KeyPair keyPair;

    /**
     * Constructor que inicializa el servicio generando un nuevo par de claves RSA de 2048 bits.
     *
     * @throws NoSuchAlgorithmException Si el algoritmo RSA no está disponible.
     */
    public RsaEncryptionService() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(2048);
        this.keyPair = keyGen.generateKeyPair();
    }

    /**
     * Cifra una cadena de datos utilizando la clave pública RSA.
     *
     * @param data La cadena de datos a cifrar.
     * @return La cadena de datos cifrada y codificada en Base64.
     * @throws Exception Si ocurre un error durante el cifrado.
     */
    public String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    /**
     * Descifra una cadena de datos cifrada utilizando la clave privada RSA.
     *
     * @param encryptedData La cadena de datos cifrada y codificada en Base64.
     * @return La cadena de datos original descifrada.
     * @throws Exception Si ocurre un error durante el descifrado.
     */
    public String decrypt(String encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

    /**
     * Devuelve la clave pública del par de claves RSA.
     *
     * @return La clave pública.
     */
    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }
}
