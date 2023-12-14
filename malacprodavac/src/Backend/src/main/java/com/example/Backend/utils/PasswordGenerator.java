package com.example.Backend.utils;

import java.security.*;

public class PasswordGenerator {

    private static final String PASSWORD_CHARACTERS = "abcdefghijklmnopqrstuvwxyz1234567890";
    private static final int PASSWORD_LENGTH = 7;

    public static String generatePassword() {
        return generateRandomPassword();
    }

    private static String generateRandomPassword() {
        char[] password = new char[PASSWORD_LENGTH];

        char[] allCharacters = PASSWORD_CHARACTERS.toCharArray();
        SecureRandom r = new SecureRandom();

        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            password[i] = allCharacters[r.nextInt(allCharacters.length)];
        }
        return new String(password);
    }

}
