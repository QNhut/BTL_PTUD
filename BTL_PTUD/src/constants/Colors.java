package constants;
import java.awt.Color;

public final class Colors {

    private Colors() {}

    public static final Color PRIMARY = hex("#18A67A");
    public static final Color ACCENT = hex("#ED5A2D");
    public static final Color SUCCESS = hex("#16A34A");
    public static final Color DANGER = hex("#EF4444");

    public static final Color BACKGROUND = Color.WHITE;
    public static final Color FOREGROUND = hex("#212121");
    public static final Color MUTED = hex("#737373");
    public static final Color BORDER = hex("#E0E0E0");
    public static final Color SECONDARY = hex("#F2F2F2");

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
