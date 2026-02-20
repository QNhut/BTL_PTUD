package constants;
import java.awt.*;
public class FontStyle {
	//vd: lblTitle.setFont(FontStyle.FONT_FAMILY)
	public static final String FONT_FAMILY = "Arial";

    public static final int XS = 12; // chú thích
    public static final int SM = 14; // text phụ
    public static final int BASE = 16; // nội dung chính
    public static final int LG = 18; // sub heading
    public static final int XL = 24; // tiêu đề 
    public static final int XXL = 30; // tiêu đề lớn

    public static final int NORMAL = Font.PLAIN;
    public static final int BOLD = Font.BOLD;

    public static Font font(int size, int style) {
        return new Font(FONT_FAMILY, style, size);
    }
}
