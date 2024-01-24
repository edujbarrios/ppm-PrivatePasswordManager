package ppm.utils;

import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class EncryptionUtil {
    private static final String ALGORITHM = "AES";
    private final Key key;

    public EncryptionUtil(String keyStr) {
        byte[] keyBytes = Base64.getDecoder().decode(keyStr);
        this.key = new SecretKeySpec(keyBytes, ALGORITHM);
    }

    public String encrypt(String valueToEnc) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encryptedValue = cipher.doFinal(valueToEnc.getBytes());
            return Base64.getEncoder().encodeToString(encryptedValue);
        } catch (Exception e) {
            throw new RuntimeException("Error during encryption", e);
        }
    }

    public String decrypt(String encryptedValue) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = Base64.getDecoder().decode(encryptedValue);
            byte[] decryptedValue = cipher.doFinal(decodedValue);
            return new String(decryptedValue);
        } catch (Exception e) {
            throw new RuntimeException("Error during decryption", e);
        }
    }
}

