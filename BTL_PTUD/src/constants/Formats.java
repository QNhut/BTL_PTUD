package constants;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.Locale;

// Định dạng dùng chung toàn ứng dụng.
// - VND: số nguyên có dấu phẩy theo locale vi-VN
// - tinhKhoangNgay: tính khoảng [tuNgay, denNgay] theo năm/tháng/ngày
public final class Formats {

    private Formats() {}

    public static final Locale VI_VN = new Locale("vi", "VN");
    public static final NumberFormat VND = NumberFormat.getNumberInstance(VI_VN);

    // Trả về [tuNgay, denNgay] dạng yyyy-MM-dd dựa trên các tham số được cung cấp.
    // - Có cả tháng + ngày: 1 ngày cụ thể
    // - Chỉ tháng: cả tháng
    // - Chỉ năm: cả năm
    // - Năm null: dùng năm hiện tại
    public static String[] tinhKhoangNgay(Integer nam, Integer thang, Integer ngay) {
        int y = (nam != null) ? nam : LocalDate.now().getYear();
        String tu, den;
        if (ngay != null && thang != null) {
            LocalDate d = LocalDate.of(y, thang, ngay);
            tu = d.toString();
            den = d.toString();
        } else if (thang != null) {
            LocalDate first = LocalDate.of(y, thang, 1);
            tu = first.toString();
            den = first.withDayOfMonth(first.lengthOfMonth()).toString();
        } else {
            tu = y + "-01-01";
            den = y + "-12-31";
        }
        return new String[] { tu, den };
    }
}
