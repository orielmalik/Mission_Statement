package com.example.missionstatement.Tools;

public class AutoKey {
    public static  String encryptAutoKey(String plaintext, String keyword) {
        StringBuilder ciphertext = new StringBuilder();
        plaintext = plaintext.toUpperCase();
        keyword = keyword.toUpperCase() + plaintext;

        for (int i = 0; i < plaintext.length(); i++) {
            char plainChar = plaintext.charAt(i);
            char keyChar = keyword.charAt(i);

            if (Character.isLetter(plainChar)) {
                int encryptedChar = (plainChar + keyChar - 2 * 'A') % 26 + 'A';
                ciphertext.append((char) encryptedChar);
            } else {
                ciphertext.append(plainChar);
            }
        }

        return ciphertext.toString();
    }

    public static String decryptAutoKey(String ciphertext, String keyword) {
        StringBuilder decryptedText = new StringBuilder();
        keyword = keyword.toUpperCase();

        for (int i = 0; i < ciphertext.length(); i++) {
            char cipherChar = ciphertext.charAt(i);
            char keyChar = keyword.charAt(i);

            if (Character.isLetter(cipherChar)) {
                int decryptedChar = (cipherChar - keyChar + 26) % 26 + 'A';
                decryptedText.append((char) decryptedChar);
                keyword += (char) decryptedChar;
            } else {
                decryptedText.append(cipherChar);
                keyword += cipherChar;
            }
        }

        return decryptedText.toString();
    }
}


