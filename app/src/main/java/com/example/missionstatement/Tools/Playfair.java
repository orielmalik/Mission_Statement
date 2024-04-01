package com.example.missionstatement.Tools;

public class Playfair {
    private static final char PAD_CHAR = 'X';

    public static String Playfair_encrypt(String plaintext, String key) {
        char[][] matrix = createPlayfairMatrix(key);
        plaintext = prepareText(plaintext);

        StringBuilder ciphertext = new StringBuilder();

        for (int i = 0; i < plaintext.length(); i += 2) {
            if(i==plaintext.length()-1) {break;}
            char char1 = plaintext.charAt(i);
            char char2 = plaintext.charAt(i + 1);

            int[] pos1 = findPosition(matrix, char1);
            int[] pos2 = findPosition(matrix, char2);

            if (pos1[0] == pos2[0]) { // Same row
                ciphertext.append(matrix[pos1[0]][(pos1[1] + 1) % 5]);
                ciphertext.append(matrix[pos2[0]][(pos2[1] + 1) % 5]);
            } else if (pos1[1] == pos2[1]) { // Same column
                ciphertext.append(matrix[(pos1[0] + 1) % 5][pos1[1]]);
                ciphertext.append(matrix[(pos2[0] + 1) % 5][pos2[1]]);
            } else {
                ciphertext.append(matrix[pos1[0]][pos2[1]]);
                ciphertext.append(matrix[pos2[0]][pos1[1]]);
            }
        }

        return ciphertext.toString();
    }

    public static String Playfair_decrypt(String ciphertext, String key) {
        char[][] matrix = createPlayfairMatrix(key);

        StringBuilder decryptedText = new StringBuilder();

        for (int i = 0; i < ciphertext.length(); i += 2) {
            char char1 = ciphertext.charAt(i);
            char char2 = ciphertext.charAt(i + 1);

            int[] pos1 = findPosition(matrix, char1);
            int[] pos2 = findPosition(matrix, char2);

            if (pos1[0] == pos2[0]) { // Same row
                decryptedText.append(matrix[pos1[0]][(pos1[1] - 1 + 5) % 5]);
                decryptedText.append(matrix[pos2[0]][(pos2[1] - 1 + 5) % 5]);
            } else if (pos1[1] == pos2[1]) { // Same column
                decryptedText.append(matrix[(pos1[0] - 1 + 5) % 5][pos1[1]]);
                decryptedText.append(matrix[(pos2[0] - 1 + 5) % 5][pos2[1]]);
            } else {
                decryptedText.append(matrix[pos1[0]][pos2[1]]);
                decryptedText.append(matrix[pos2[0]][pos1[1]]);
            }
        }
        String a=decryptedText.toString();
        return a;
    }

    private static char[][] createPlayfairMatrix(String key) {
        String keyWithoutDuplicates = removeDuplicates(key + "ABCDEFGHIJKLMNOPQRSTUVWXYZ");

        char[][] matrix = new char[5][5];
        int k = 0;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                matrix[i][j] = keyWithoutDuplicates.charAt(k);
                k++;
            }
        }

        return matrix;
    }

    private static String prepareText(String input) {
        StringBuilder preparedText = new StringBuilder();
        input = input.toUpperCase().replaceAll("[^A-Z]", "");

        for (int i = 0; i < input.length(); i += 2) {
            char char1 = input.charAt(i);
            char char2 = (i + 1 < input.length()) ? input.charAt(i + 1) : PAD_CHAR;

            if (char1 == char2) {
                preparedText.append(char1);
            } else {
                preparedText.append(char1);
                preparedText.append(char2);
            }
        }

        return preparedText.toString();
    }

    private static int[] findPosition(char[][] matrix, char target) {
        int[] position = new int[2];

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (matrix[i][j] == target) {
                    position[0] = i;
                    position[1] = j;
                    return position;
                }
            }
        }

        return position;
    }

    private static String removeDuplicates(String input) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            if (result.indexOf(String.valueOf(input.charAt(i))) == -1) {
                result.append(input.charAt(i));
            }
        }
        return result.toString();
    }


}
