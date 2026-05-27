package util;

/**
 * Utility class for masking sensitive personal data in the UI.
 * Raw data remains unmasked in memory/DB; masking is applied only at display time.
 */
public final class MaskUtil {

    private MaskUtil() {}

    /**
     * Masks a phone number, showing only the last 3 digits.
     * Example: "0912345678" → "*******678"
     */
    public static String phone(String s) {
        if (s == null || s.isBlank()) return s;
        s = s.trim();
        int visible = Math.min(3, s.length());
        return "*".repeat(s.length() - visible) + s.substring(s.length() - visible);
    }

    /**
     * Masks an email address, showing only the first character of the local part.
     * Example: "user@gmail.com" → "u***@gmail.com"
     */
    public static String email(String s) {
        if (s == null || s.isBlank()) return s;
        int at = s.indexOf('@');
        if (at <= 0) return "***";
        String local = s.substring(0, at);
        String domain = s.substring(at);
        if (local.length() <= 1) return local + "***" + domain;
        return local.charAt(0) + "*".repeat(local.length() - 1) + domain;
    }

    /**
     * Masks a CCCD (national ID), showing only the first 3 and last 2 digits.
     * Example: "012345678901" → "012*******01"
     */
    public static String cccd(String s) {
        if (s == null || s.isBlank()) return s;
        s = s.trim();
        if (s.length() <= 5) return "*".repeat(s.length());
        return s.substring(0, 3) + "*".repeat(s.length() - 5) + s.substring(s.length() - 2);
    }

    /**
     * Masks an address, showing only the first 8 characters.
     * Example: "123 Nguyễn Văn Linh, Q.7" → "123 Nguy***"
     */
    public static String address(String s) {
        if (s == null || s.isBlank()) return s;
        int show = Math.min(8, s.length());
        return s.substring(0, show) + (s.length() > show ? "***" : "");
    }
}
