package ppm.core;

import ppm.utils.EncryptionUtil;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.nio.file.*;
import java.io.*;

public class PasswordManagerCore {
    private final List<PasswordEntry> entries;
    private final EncryptionUtil encryptionUtil;
    private static final String STORAGE_FILE = "passwords.txt";

    public PasswordManagerCore(String key) {
        if (key == null || key.isEmpty()) {
            key = generateSecureKey();
        }
        this.encryptionUtil = new EncryptionUtil(key);
        this.entries = new ArrayList<>();
        loadEntries();
    }

    private void loadEntries() {
        try {
            List<String> lines = Files.readAllLines(Paths.get(STORAGE_FILE));
            for (String line : lines) {
                String[] parts = line.split(";", 3);
                if (parts.length == 3) {
                    String service = parts[0];
                    String username = parts[1];
                    String encryptedPassword = parts[2];
                    entries.add(new PasswordEntry(service, username, encryptedPassword));
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading entries: " + e.getMessage());
        }
    }

    public void saveEntries() {
        try (PrintWriter writer = new PrintWriter(STORAGE_FILE)) {
            for (PasswordEntry entry : entries) {
                writer.println(entry.getService() + ";" + entry.getUsername() + ";" + entry.getEncryptedPassword());
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error saving entries: " + e.getMessage());
        }
    }

    private String generateSecureKey() {
        SecureRandom random = new SecureRandom();
        byte[] keyBytes = new byte[16]; // 128 bits.
        random.nextBytes(keyBytes);
        return Base64.getEncoder().encodeToString(keyBytes);
    }

    public void addEntry(String service, String username, String password) {
        String encryptedPassword = encryptionUtil.encrypt(password);
        entries.add(new PasswordEntry(service, username, encryptedPassword));
        saveEntries(); // Save entries after adding a new one
    }

    public void deleteEntry(int index) {
        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
            saveEntries(); // Save entries after deleting one
        }
    }

    public List<PasswordEntry> getEntries() {
        return new ArrayList<>(entries);
    }

    public String decryptPassword(String encryptedPassword) {
        return encryptionUtil.decrypt(encryptedPassword);
    }
}
