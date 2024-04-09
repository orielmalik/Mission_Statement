package com.example.missionstatement.Tools;
import android.util.Base64;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.HashMap;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

public class CryptoUtils {

    private static final int pswdIterations = 10;
    private static final int keySize = 128;
    private static final String cypherInstance = "AES/CBC/PKCS5Padding";
    private static final String secretKeyInstance = "PBKDF2WithHmacSHA1";
    private static final String plainText = "sampleText";
    private static final String AESSalt = "exampleSalt";
    private static final String initializationVector = "8119745113154120";

    public static String encrypt(String textToEncrypt) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(getRaw(plainText, AESSalt), "AES");
        Cipher cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, new IvParameterSpec(initializationVector.getBytes()));
        byte[] encrypted = cipher.doFinal(textToEncrypt.getBytes());
        return Base64.encodeToString(encrypted, Base64.DEFAULT);
    }

    public static String decrypt(String textToDecrypt) throws Exception {
        byte[] encryted_bytes = Base64.decode(textToDecrypt, Base64.DEFAULT);
        SecretKeySpec skeySpec = new SecretKeySpec(getRaw(plainText, AESSalt), "AES");
        Cipher cipher = Cipher.getInstance(cypherInstance);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, new IvParameterSpec(initializationVector.getBytes()));
        byte[] decrypted = cipher.doFinal(encryted_bytes);
        return new String(decrypted, "UTF-8");
    }

    private static byte[] getRaw(String plainText, String salt) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance(secretKeyInstance);
            KeySpec spec = new PBEKeySpec(plainText.toCharArray(), salt.getBytes(), pswdIterations, keySize);
            return factory.generateSecret(spec).getEncoded();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static HashMap<String,String> encryptHuman(HashMap<String,String>mdeatils){
        HashMap<String,String>deatils=mdeatils;
        deatils.forEach((key, value) -> {
            // בדיקה אם הערך הוא String
            if (value instanceof String) {
                // שינוי הערך
                try {
                    String strValue=CryptoUtils.encrypt((String) value.trim());
                    deatils.put(key, strValue);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });
        return  deatils;
    }  public static HashMap<String,String> decryptHuman(HashMap<String,String>mdeatils){
        HashMap<String,String>deatils=mdeatils;
        deatils.forEach((key, value) -> {
            // בדיקה אם הערך הוא String
            if (value instanceof String) {
                // שינוי הערך
                try {
                    String strValue=CryptoUtils.decrypt((String) value.trim());
                    deatils.put(key, strValue);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        });
        return  deatils;
    }


}
