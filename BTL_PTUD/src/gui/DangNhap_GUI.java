package gui;

import ConnectDB.ConnectDB;
import constants.Colors;
import constants.FontStyle;
import constants.Spacings;
import entity.TaiKhoan;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import javax.swing.*;
import service.TaiKhoan_Service;

public class DangNhap_GUI extends JFrame implements ActionListener {

    private JLabel lblNen, lblTitle1, lblTitle2, lblUser, lblPWD, lblTitle3, lblYC;
    private JPanel pWest, pCenter, pInput;
    private Font fo, fo1;
    private JTextField txtUser;
    private JPasswordField pwdUser;
    private JButton btnDangNhap;
    private Box bUser, bPWD, bDN, bCheck, bButton, bYC;
    private JCheckBox chkShowPWD;

    private TaiKhoan_Service tkService;

    {
        ConnectDB.getInstance().connect();
        tkService = new TaiKhoan_Service();
    }
    private String currentToken;
    private char defaultEchoChar;
    private JSeparator sepWest;
    private JLabel lblSubWest;
    private JLabel lblCopy;
    private JLabel lblSubTitle;
    private JSeparator cardSep;
    private JLabel lblUserTitle;
    private JLabel lblPWDTitle;
    private JLabel lblVersion;
    private Box centerRow;
    private JPanel loginCard;

    public DangNhap_GUI() {
        super("Đăng nhập");

//		Phần này sẽ có 2 khu vực chính: bên trái là phần giới thiệu, bên phải là form đăng nhập		
        ImageIcon nen = new ImageIcon("data/img/icons/Logo.png");
        Image img = nen.getImage();
        img = img.getScaledInstance(130, 130, Image.SCALE_SMOOTH);
        nen = new ImageIcon(img);
        lblNen = new JLabel(nen);
        lblNen.setAlignmentX(CENTER_ALIGNMENT);
        lblNen.setHorizontalAlignment(SwingConstants.CENTER);

//		Phần bên trái (WEST) với màu nền và thông tin giới thiệu
        add(pWest = new JPanel(), BorderLayout.WEST);
        pWest.setLayout(new BoxLayout(pWest, BoxLayout.Y_AXIS));
        pWest.setBackground(Colors.PRIMARY);
        pWest.setPreferredSize(new Dimension(300, 0));
        pWest.setBorder(BorderFactory.createEmptyBorder(0, 20, 20, 20));

        pWest.add(Box.createVerticalGlue());
        pWest.add(lblNen);

        pWest.add(Box.createVerticalStrut(18));
        pWest.add(lblTitle1 = new JLabel("HAPPY HEALTH"));
        lblTitle1.setFont(fo = FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        lblTitle1.setForeground(Color.WHITE);
        lblTitle1.setAlignmentX(CENTER_ALIGNMENT);

        pWest.add(Box.createVerticalStrut(6));
        pWest.add(lblTitle2 = new JLabel("PHARMACY"));
        lblTitle2.setFont(fo);
        lblTitle2.setForeground(Colors.hex("#FEE498"));
        lblTitle2.setAlignmentX(CENTER_ALIGNMENT);
        pWest.add(Box.createVerticalStrut(16));

        pWest.add(sepWest = new JSeparator(SwingConstants.HORIZONTAL));
        sepWest.setMaximumSize(new Dimension(160, 1));
        sepWest.setForeground(new Color(255, 255, 255, 60));
        sepWest.setAlignmentX(CENTER_ALIGNMENT);
        pWest.add(Box.createVerticalStrut(14));

        pWest.add(lblSubWest = new JLabel("<html><center>Hệ thống quản lý<br/>dược phẩm & nhân viên</center></html>"));
        lblSubWest.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSubWest.setForeground(Colors.opacity(Color.WHITE, 0.7f));
        lblSubWest.setAlignmentX(CENTER_ALIGNMENT);
        lblSubWest.setHorizontalAlignment(SwingConstants.CENTER);
        pWest.add(Box.createVerticalGlue());

        pWest.add(lblCopy = new JLabel("© 2025 Happy Health"));
        lblCopy.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lblCopy.setForeground(Colors.opacity(Color.WHITE, 0.35f));
        lblCopy.setAlignmentX(CENTER_ALIGNMENT);

//		Phần bên phải (CENTER) với form đăng nhập
        add(pCenter = new JPanel(), BorderLayout.CENTER);
        pCenter.setLayout(new BoxLayout(pCenter, BoxLayout.Y_AXIS));
        pCenter.setBackground(Colors.BACKGROUND);

        pCenter.add(Box.createVerticalGlue());
        pCenter.add(centerRow = Box.createHorizontalBox());
        pCenter.add(Box.createVerticalGlue());

        centerRow.add(Box.createHorizontalGlue());
        centerRow.add(loginCard = new JPanel());
        centerRow.add(Box.createHorizontalGlue());

        loginCard.setPreferredSize(new Dimension(460, 500));
        loginCard.setBackground(Colors.BACKGROUND);
        loginCard.setBorder(BorderFactory.createEmptyBorder(34, 36, 32, 36));
        loginCard.setLayout(new BoxLayout(loginCard, BoxLayout.Y_AXIS));

//		Phần form đăng nhập với tiêu đề, input và button
        loginCard.add(lblTitle3 = new JLabel("Đăng nhập hệ thống"));
        lblTitle3.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTitle3.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblTitle3.setForeground(Colors.TEXT_PRIMARY);
        loginCard.add(Box.createVerticalStrut(4));

        loginCard.add(lblSubTitle = new JLabel("Nhập thông tin tài khoản để tiếp tục"));
        lblSubTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSubTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        lblSubTitle.setForeground(Colors.TEXT_SECONDARY);
        loginCard.add(Box.createVerticalStrut(Spacings.S3));

        loginCard.add(cardSep = new JSeparator(SwingConstants.HORIZONTAL));
        cardSep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        cardSep.setForeground(Colors.BORDER_LIGHT);
        loginCard.add(Box.createVerticalStrut(Spacings.S6));

//		Phần input với label, icon và field nhập liệu cho username và password, cùng checkbox hiện mật khẩu
        loginCard.add(lblUserTitle = new JLabel("Tên đăng nhập"));
        lblUserTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblUserTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblUserTitle.setForeground(Colors.TEXT_PRIMARY);
        loginCard.add(Box.createVerticalStrut(5));

        loginCard.add(bUser = new Box(BoxLayout.X_AXIS));
        bUser.setAlignmentX(Component.LEFT_ALIGNMENT);
        bUser.add(lblUser = new JLabel("👤 "));
        lblUser.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        lblUser.setPreferredSize(new Dimension(30, 0));
        bUser.add(txtUser = new JTextField(24));
        styleInputField(txtUser);
        loginCard.add(Box.createVerticalStrut(Spacings.S3));

        loginCard.add(lblPWDTitle = new JLabel("Mật khẩu"));
        lblPWDTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblPWDTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblPWDTitle.setForeground(Colors.TEXT_PRIMARY);
        loginCard.add(Box.createVerticalStrut(5));

        loginCard.add(bPWD = new Box(BoxLayout.X_AXIS));
        bPWD.setAlignmentX(Component.LEFT_ALIGNMENT);
        bPWD.add(lblPWD = new JLabel("🔒 "));
        lblPWD.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        lblPWD.setPreferredSize(new Dimension(30, 0));
        bPWD.add(pwdUser = new JPasswordField(24));
        styleInputField(pwdUser);
        pwdUser.addActionListener(this);
        loginCard.add(Box.createVerticalStrut(Spacings.S2));

        loginCard.add(bCheck = new Box(BoxLayout.X_AXIS));
        bCheck.setAlignmentX(Component.LEFT_ALIGNMENT);
        bCheck.add(chkShowPWD = new JCheckBox("Hiện mật khẩu"));
        chkShowPWD.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        chkShowPWD.setForeground(Colors.TEXT_SECONDARY);
        chkShowPWD.setBackground(Colors.BACKGROUND);
        bCheck.add(Box.createHorizontalGlue());
        loginCard.add(Box.createVerticalStrut(Spacings.S2));

        loginCard.add(bYC = new Box(BoxLayout.X_AXIS));
        bYC.setAlignmentX(Component.LEFT_ALIGNMENT);
        bYC.add(lblYC = new JLabel(" "));
        bYC.add(Box.createHorizontalGlue());
        lblYC.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblYC.setForeground(Colors.DANGER);
        loginCard.add(Box.createVerticalStrut(Spacings.S3));

        loginCard.add(bButton = new Box(BoxLayout.X_AXIS));
        bButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        bButton.add(btnDangNhap = new JButton("Đăng nhập"));
        btnDangNhap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnDangNhap.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setBackground(Colors.PRIMARY_BUTTON);
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnDangNhap.setBorder(BorderFactory.createEmptyBorder(11, 22, 11, 22));
        loginCard.add(Box.createVerticalStrut(Spacings.S6));

        loginCard.add(new JSeparator());
        loginCard.add(Box.createVerticalStrut(Spacings.S2));
        loginCard.add(lblVersion = new JLabel("Phiên bản 1.0.0 — Happy Health Pharmacy"));
        lblVersion.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblVersion.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lblVersion.setForeground(Colors.MUTED);

//		Cài đặt chung cho frame
        setSize(720, 460);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        ConnectDB.getInstance().connect();
        btnDangNhap.addActionListener(this);
        chkShowPWD.addActionListener(this);
        defaultEchoChar = pwdUser.getEchoChar();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        ConnectDB.getInstance().connect();
        SwingUtilities.invokeLater(() -> {
            TaiKhoan_Service tkService = new TaiKhoan_Service();
            String savedToken = tkService.layTokenDaLuu();
            if (savedToken != null && tkService.tokenHopLe(savedToken)) {
                new Main_GUI(tkService, savedToken).setVisible(true);
            } else {
                new DangNhap_GUI().setVisible(true);
            }
        });
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(chkShowPWD)) {
            if (chkShowPWD.isSelected()) {
                pwdUser.setEchoChar((char) 0);
            } else {
                pwdUser.setEchoChar(defaultEchoChar);
            }
        }
        if (o.equals(btnDangNhap)) {
            if (ConnectDB.getConnection() == null) {
                ConnectDB.getInstance().connect();
            }
            if (ConnectDB.getConnection() == null) {
                lblYC.setText("Không thể kết nối cơ sở dữ liệu!");
                return;
            }

            String user = txtUser.getText().trim();
            String pass = new String(pwdUser.getPassword()).trim();
            if (user.isEmpty()) {
                lblYC.setText("Vui lòng nhập tài khoản!");
                txtUser.requestFocus();
                return;
            }
            if (pass.isEmpty()) {
                lblYC.setText("Vui lòng nhập mật khẩu!");
                pwdUser.requestFocus();
                return;
            }

			String token = tkService.dangNhap(user, pass);
			if (token != null) {
				TaiKhoan taiKhoan = tkService.layTaiKhoanTheoToken(token);
				if (taiKhoan == null) {
					lblYC.setText("Phiên đăng nhập không hợp lệ!");
					return;
				}
				currentToken = token;
				JOptionPane.showMessageDialog(this, "Xin chào " + taiKhoan.getNhanVien().getTenNhanVien());
				Main_GUI mainFram = new Main_GUI(tkService, currentToken);
				mainFram.setVisible(true);
				this.dispose();
			} else {
				lblYC.setText("Sai tài khoản hoặc mật khẩu!");
				txtUser.requestFocus();
				return;
			}
		}
	}



    public String getCurrentToken() {
        return currentToken;
    }

    private void styleInputField(JTextField field) {
        field.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        field.setPreferredSize(new Dimension(0, 40));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        field.setBackground(Colors.BACKGROUND);
        field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));

        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Colors.PRIMARY, 2),
                        BorderFactory.createEmptyBorder(3, 9, 3, 9)));
            }

            @Override
            public void focusLost(FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Colors.BORDER, 1),
                        BorderFactory.createEmptyBorder(4, 10, 4, 10)));
            }
        });
    }
}
