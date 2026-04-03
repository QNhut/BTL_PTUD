package constants;
import java.awt.Color;

public final class Colors {
    private Colors() {}

    // ===== CORE COLORS =====
    public static final Color PRIMARY = hex("#18A67A");   // Nút chính
    public static final Color ACCENT = hex("#ED5A2D");    // Highlight
    public static final Color SUCCESS = hex("#16A34A");   // Trạng thái OK
    public static final Color DANGER = hex("#EF4444");    // Cảnh báo

    // ===== BACKGROUND =====
    public static final Color BACKGROUND = Color.WHITE;   // Nền chính
    public static final Color SECONDARY = hex("#F2F2F2"); // Nền phụ (hover)

    // NEW (dùng cho selected card, badge)
    public static final Color SUCCESS_LIGHT = hex("#D1FAE5"); // xanh nhạt
    public static final Color PRIMARY_LIGHT = hex("#ECFDF5"); // nền selected card

    // ===== TEXT =====
    public static final Color FOREGROUND = hex("#212121"); // text chính

    // NEW (chuẩn theo UI bạn đang dùng)
    public static final Color TEXT_PRIMARY = hex("#111827");   // (17,24,39)
    public static final Color TEXT_SECONDARY = hex("#6B7280");
    public static final Color TEXT_LOGIN = hex("#0099FF");// (107,114,128)

    public static final Color MUTED = hex("#737373"); 

    // ===== BORDER =====
    public static final Color BORDER = hex("#E0E0E0");

    // NEW (border nhẹ hơn)
    public static final Color BORDER_LIGHT = hex("#E5E7EB"); 

    // ===== SPECIAL =====
    // Badge / trạng thái đã thêm
    public static final Color SUCCESS_DARK = hex("#065F46");

    // Button riêng 
    public static final Color PRIMARY_BUTTON = hex("#00A86B");
    
    // ===== UTILS =====
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