package constants;

public class Spacings {
	private Spacings() {}

    //Spacing scale
	//vd: layout.setHgap(Spacings.S1)
    public static final int S1 = 4;
    public static final int S2 = 8;
    public static final int S3 = 12;
    public static final int S4 = 16;
    public static final int S6 = 24;
    public static final int S8 = 32;

    //Radius
    //vd: new RoundButton("Login", Radius.R_MD);
    public static final int R_SM   = 4;
    public static final int R_MD   = 8;
    public static final int R_LG   = 12; // panel, đăng nhập, chức năng đã chọn
    public static final int R_XL   = 16;

    // Layout
    //vd: sidebar.setPreferredSize(new Dimension(Layout.SIDEBAR_WIDTH, getHeight()));
    public static final int SIDEBAR_WIDTH = 256;
    public static final int CARD_MAX_WIDTH = 448;
}
