package com.duc.chatting.utilities;

import android.util.Base64;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class AESCryptoService {
    private static final byte[] encryptionKey = {
            9, 115, 51, 86, 105, 4, -31, -23, -68, 88, 17, 20, 3, -105, 119, -53
    };

    private static final String AES = "AES";

    private static final SecretKeySpec secretKeySpec = new SecretKeySpec(encryptionKey, AES);

    private static final Cipher cipher;
    private static final Cipher decipher;

    static {
        Cipher tmpCipher = null;
        Cipher tmpDecipher = null;
        try {
            tmpCipher = Cipher.getInstance(AES);
            tmpDecipher = Cipher.getInstance(AES);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        cipher = tmpCipher;
        decipher = tmpDecipher;
    }

    public static String encrypt(String input) {
        try {
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] encryptedBytes = cipher.doFinal(input.getBytes(StandardCharsets.UTF_8));
            return Base64.encodeToString(encryptedBytes, Base64.NO_WRAP);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String decrypt(String encryptedInput) {
        try {
            decipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] decodedBytes = Base64.decode(encryptedInput, Base64.NO_WRAP);
            byte[] decryptedBytes = decipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
