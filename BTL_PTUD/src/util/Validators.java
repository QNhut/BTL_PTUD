package util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.function.Function;
import java.util.regex.Pattern;

// Tập hợp các hàm validate cho form nhập liệu.
// Quy ước: mỗi validator nhận vào giá trị chuỗi, trả về
// - {@code null} nếu hợp lệ
// - chuỗi thông báo lỗi (tiếng Việt) nếu sai.
public final class Validators {

    private Validators() {}

    private static final DateTimeFormatter FMT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Tên người: chữ cái (kể cả tiếng Việt có dấu) + khoảng trắng
    private static final Pattern PT_TEN_NGUOI = Pattern.compile(
            "^[\\p{L}][\\p{L} .'\\-]{0,79}$");
    // SĐT VN: 10 số, bắt đầu 0
    private static final Pattern PT_SDT_VN = Pattern.compile("^0\\d{9}$");
    // Email
    private static final Pattern PT_EMAIL = Pattern.compile(
            "^[A-Za-z0-9._%+\\-]+@[A-Za-z0-9.\\-]+\\.[A-Za-z]{2,}$");

    // Bắt buộc nhập (không được rỗng).
    public static String required(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "Không được để trống";
        }
        return null;
    }

    // Tên người: chỉ chữ + dấu + khoảng trắng, 1-80 ký tự.
    public static String tenNguoi(String value) {
        String r = required(value);
        if (r != null) return r;
        if (!PT_TEN_NGUOI.matcher(value.trim()).matches()) {
            return "Tên chỉ chứa chữ cái và khoảng trắng (1-80 ký tự)";
        }
        return null;
    }

    // Số điện thoại Việt Nam: 10 số, bắt đầu bằng 0.
    public static String soDienThoai(String value) {
        String r = required(value);
        if (r != null) return r;
        if (!PT_SDT_VN.matcher(value.trim()).matches()) {
            return "SĐT phải gồm 10 số và bắt đầu bằng 0";
        }
        return null;
    }

    // Email hợp lệ.
    public static String email(String value) {
        String r = required(value);
        if (r != null) return r;
        if (!PT_EMAIL.matcher(value.trim()).matches()) {
            return "Email không đúng định dạng (vd: ten@domain.com)";
        }
        return null;
    }

    // Email không bắt buộc, nếu có phải đúng.
    public static String emailOptional(String value) {
        if (value == null || value.trim().isEmpty()) return null;
        return email(value);
    }

    // Ngày theo định dạng dd/MM/yyyy.
    public static String ngay(String value) {
        String r = required(value);
        if (r != null) return r;
        try {
            LocalDate.parse(value.trim(), FMT_DATE);
            return null;
        } catch (DateTimeParseException ex) {
            return "Ngày không đúng định dạng dd/MM/yyyy";
        }
    }

    // Số nguyên dương (>=0).
    public static String soNguyenKhongAm(String value) {
        String r = required(value);
        if (r != null) return r;
        try {
            long n = Long.parseLong(value.trim());
            if (n < 0) return "Giá trị phải >= 0";
            return null;
        } catch (NumberFormatException ex) {
            return "Phải là số nguyên hợp lệ";
        }
    }

    // Số nguyên dương > 0.
    public static String soNguyenDuong(String value) {
        String r = required(value);
        if (r != null) return r;
        try {
            long n = Long.parseLong(value.trim());
            if (n <= 0) return "Giá trị phải lớn hơn 0";
            return null;
        } catch (NumberFormatException ex) {
            return "Phải là số nguyên hợp lệ";
        }
    }

    // Số thực dương > 0.
    public static String soThucDuong(String value) {
        String r = required(value);
        if (r != null) return r;
        try {
            double n = Double.parseDouble(value.trim());
            if (n <= 0) return "Giá trị phải lớn hơn 0";
            return null;
        } catch (NumberFormatException ex) {
            return "Phải là số hợp lệ";
        }
    }

    // CCCD Việt Nam: đúng 12 chữ số.
    public static String cccd(String value) {
        String r = required(value);
        if (r != null) return r;
        if (!value.trim().matches("\\d{12}")) {
            return "CCCD phải gồm đúng 12 chữ số";
        }
        return null;
    }

    // Phần trăm trong khoảng [0, 100].
    public static String phanTram(String value) {
        String r = required(value);
        if (r != null) return r;
        try {
            double n = Double.parseDouble(value.trim());
            if (n < 0 || n > 100) return "Phần trăm phải trong khoảng 0 - 100";
            return null;
        } catch (NumberFormatException ex) {
            return "Phải là số hợp lệ";
        }
    }

    // Kết hợp nhiều validator: trả về lỗi đầu tiên gặp phải.
    @SafeVarargs
    public static String chain(String value, Function<String, String>... validators) {
        for (Function<String, String> v : validators) {
            String err = v.apply(value);
            if (err != null) return err;
        }
        return null;
    }
}
