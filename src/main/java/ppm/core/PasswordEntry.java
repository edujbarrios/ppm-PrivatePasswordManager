package ppm.core;

public class PasswordEntry {
    private String service;
    private String username;
    private String encryptedPassword;

    public PasswordEntry(String service, String username, String encryptedPassword) {
        this.service = service;
        this.username = username;
        this.encryptedPassword = encryptedPassword;
    }

    public String getService() {
        return service;
    }

    public String getUsername() {
        return username;
    }

    public String getEncryptedPassword() {
        return encryptedPassword;
    }
}
