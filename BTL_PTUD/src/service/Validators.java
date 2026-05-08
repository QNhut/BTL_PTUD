package service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Function;
import java.util.regex.Pattern;

// T\u1eadp h\u1ee3p c\u00e1c h\u00e0m validate cho form nh\u1eadp li\u1ec7u.
// Quy \u01b0\u1edbc: m\u1ed7i validator nh\u1eadn v\u00e0o gi\u00e1 tr\u1ecb chu\u1ed7i, tr\u1ea3 v\u1ec1
// - {@code null} n\u1ebfu h\u1ee3p l\u1ec7
// - chu\u1ed7i th\u00f4ng b\u00e1o l\u1ed7i (ti\u1ebfng Vi\u1ec7t) n\u1ebfu sai.
public final class Validators {

    private Validators() {}

    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // T\u00ean ng\u01b0\u1eddi: ch\u1eef c\u00e1i (k\u1ec3 c\u1ea3 ti\u1ebfng Vi\u1ec7t c\u00f3 d\u1ea5u) + kho\u1ea3ng tr\u1eafng
    private static final Pattern PT_TEN_NGUOI = Pattern.compile(
            "^[\\p{L}][\\p{L} .'\\-]{0,79}$");
    // S\u0110T VN: 10 s\u1ed1, b\u1eaft \u0111\u1ea7u 0
    private static final Pattern PT_SDT_VN = Pattern.compile("^0\\d{9}$");
    // Email
    private static final Pattern PT_EMAIL = Pattern.compile(
            "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");

    // B\u1eaft bu\u1ed9c nh\u1eadp (kh\u00f4ng \u0111\u01b0\u1ee3c r\u1ed7ng).
    public static String required(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Kh\u00f4ng \u0111\u01b0\u1ee3c \u0111\u1ec3 tr\u1ed1ng";
        }
        return null;
    }

    // T\u00ean ng\u01b0\u1eddi: ch\u1ec9 ch\u1eef + d\u1ea5u + kho\u1ea3ng tr\u1eafng, 1-80 k\u00fd t\u1ef1.
    public static String tenNguoi(String value) {
        String r = required(value);
        if (r != null) return r;
        if (!PT_TEN_NGUOI.matcher(value.trim()).matches()) {
            return "T\u00ean ch\u1ec9 ch\u1ee9a ch\u1eef c\u00e1i v\u00e0 kho\u1ea3ng tr\u1eafng (1-80 k\u00fd t\u1ef1)";
        }
        return null;
    }

    // S\u1ed1 \u0111i\u1ec7n tho\u1ea1i Vi\u1ec7t Nam: 10 s\u1ed1, b\u1eaft \u0111\u1ea7u b\u1eb1ng 0.
    public static String soDienThoai(String value) {
        String r = required(value);
        if (r != null) return r;
        if (!PT_SDT_VN.matcher(value.trim()).matches()) {
            return "S\u0110T ph\u1ea3i g\u1ed3m 10 s\u1ed1 v\u00e0 b\u1eaft \u0111\u1ea7u b\u1eb1ng 0";
        }
        return null;
    }

    // Email h\u1ee3p l\u1ec7.
    public static String email(String value) {
        String r = required(value);
        if (r != null) return r;
        if (!PT_EMAIL.matcher(value.trim()).matches()) {
            return "Email kh\u00f4ng \u0111\u00fang \u0111\u1ecbnh d\u1ea1ng (vd: ten@domain.com)";
        }
        return null;
    }

    // Email kh\u00f4ng b\u1eaft bu\u1ed9c, n\u1ebfu c\u00f3 ph\u1ea3i \u0111\u00fang.
    public static String emailOptional(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return email(value);
    }

    // Ng\u00e0y theo \u0111\u1ecbnh d\u1ea1ng dd/MM/yyyy.
    public static String ngay(String value) {
        String r = required(value);
        if (r != null) return r;
        try {
            LocalDate.parse(value.trim(), FMT_DATE);
            return null;
        } catch (DateTimeParseException ex) {
            return "Ng\u00e0y kh\u00f4ng \u0111\u00fang \u0111\u1ecbnh d\u1ea1ng dd/MM/yyyy";
        }
    }

    // S\u1ed1 nguy\u00ean d\u01b0\u01a1ng (>=0).
    public static String soNguyenKhongAm(String value) {
        String r = required(value);
        if (r != null) return r;
        try {
            long n = Long.parseLong(value.trim());
            if (n < 0) return "Gi\u00e1 tr\u1ecb ph\u1ea3i >= 0";
            return null;
        } catch (NumberFormatException ex) {
            return "Ph\u1ea3i l\u00e0 s\u1ed1 nguy\u00ean h\u1ee3p l\u1ec7";
        }
    }

    // S\u1ed1 nguy\u00ean d\u01b0\u01a1ng > 0.
    public static String soNguyenDuong(String value) {
        String r = required(value);
        if (r != null) return r;
        try {
            long n = Long.parseLong(value.trim());
            if (n <= 0) return "Gi\u00e1 tr\u1ecb ph\u1ea3i l\u1edbn h\u01a1n 0";
            return null;
        } catch (NumberFormatException ex) {
            return "Ph\u1ea3i l\u00e0 s\u1ed1 nguy\u00ean h\u1ee3p l\u1ec7";
        }
    }

    // S\u1ed1 th\u1ef1c d\u01b0\u01a1ng > 0.
    public static String soThucDuong(String value) {
        String r = required(value);
        if (r != null) return r;
        try {
            double n = Double.parseDouble(value.trim());
            if (n <= 0) return "Gi\u00e1 tr\u1ecb ph\u1ea3i l\u1edbn h\u01a1n 0";
            return null;
        } catch (NumberFormatException ex) {
            return "Ph\u1ea3i l\u00e0 s\u1ed1 h\u1ee3p l\u1ec7";
        }
    }

    // CCCD Vi\u1ec7t Nam: \u0111\u00fang 12 ch\u1eef s\u1ed1.
    public static String cccd(String value) {
        String r = required(value);
        if (r != null) return r;
        if (!value.trim().matches("\\d{12}")) {
            return "CCCD ph\u1ea3i g\u1ed3m \u0111\u00fang 12 ch\u1eef s\u1ed1";
        }
        return null;
    }

    // Ph\u1ea7n tr\u0103m trong kho\u1ea3ng [0, 100].
    public static String phanTram(String value) {
        String r = required(value);
        if (r != null) return r;
        try {
            double n = Double.parseDouble(value.trim());
            if (n < 0 || n > 100) return "Ph\u1ea7n tr\u0103m ph\u1ea3i trong kho\u1ea3ng 0 - 100";
            return null;
        } catch (NumberFormatException ex) {
            return "Ph\u1ea3i l\u00e0 s\u1ed1 h\u1ee3p l\u1ec7";
        }
    }

    // K\u1ebft h\u1ee3p nhi\u1ec1u validator: tr\u1ea3 v\u1ec1 l\u1ed7i \u0111\u1ea7u ti\u00ean g\u1eb7p ph\u1ea3i.
    @SafeVarargs
    public static String chain(String value, Function<String, String>... validators) {
        for (Function<String, String> v : validators) {
            String err = v.apply(value);
            if (err != null) return err;
        }
        return null;
    }
}
