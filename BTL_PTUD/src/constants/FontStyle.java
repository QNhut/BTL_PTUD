package constants;
import java.awt.*;
public class FontStyle {
	public static final String FONT_FAMILY = "Arial";

    public static final int XS = 12;
    public static final int SM = 14;
    public static final int BASE = 16;
    public static final int LG = 18;
    public static final int XL = 24;
    public static final int XXL = 30;

    public static final int NORMAL = Font.PLAIN;
    public static final int BOLD = Font.BOLD;

    public static Font font(int size, int style) {
        return new Font(FONT_FAMILY, style, size);
    }
}
