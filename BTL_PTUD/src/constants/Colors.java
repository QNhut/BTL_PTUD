package constants;
import java.awt.Color;

public final class Colors {
	// Khi dùng thì gọi Colors.__ Vd: lbTieuDe.setForeground(Colors.PRIMARY)
    private Colors() {}

    public static final Color PRIMARY = hex("#18A67A"); // Nút chính, sidebar
    public static final Color ACCENT = hex("#ED5A2D"); // highlight, nút được chọn
    public static final Color SUCCESS = hex("#16A34A"); // tổng tiền, trạng thái còn hàng, đã thanh toán
    public static final Color DANGER = hex("#EF4444"); // cảnh báo

    public static final Color BACKGROUND = Color.WHITE; // Nền
    public static final Color FOREGROUND = hex("#212121"); // Chữ
    public static final Color MUTED = hex("#737373"); // Text phụ
    public static final Color BORDER = hex("#E0E0E0");
    public static final Color SECONDARY = hex("#F2F2F2"); // Nền phụ, hover

    public static Color hex(String hex) {
        return Color.decode(hex);
    }

    public static Color opacity(Color c, float o) {
        return new Color(
                c.getRed(),
                c.getGreen(),
                c.getBlue(),
                Math.round(o * 255)
        );
    }
}
