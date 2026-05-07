package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import constants.Colors;
import constants.FontStyle;
import constants.Spacings;
import entity.TaiKhoan;
import service.TaiKhoan_Service;
import exception.RoundedButton;

public class DoiMatKhau_GUI extends JFrame implements ActionListener {

    private JPasswordField txtMatKhauHienTai, txtMatKhauMoi, txtXacNhanMatKhau;
    private RoundedButton btnDoiMatKhau, btnHuy;
    private JCheckBox chkHienMK;
    private JLabel errHienTai, errMoi, errXacNhan;
    private TaiKhoan_Service taiKhoanService;
    private String currentToken;

    public DoiMatKhau_GUI(TaiKhoan_Service taiKhoanService, String token) {
        this.taiKhoanService = taiKhoanService;
        this.currentToken = token;
        
        setTitle("Đổi mật khẩu");
        setSize(500, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel main = new JPanel();
        main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
        main.setBackground(Colors.BACKGROUND);
        main.setBorder(BorderFactory.createEmptyBorder(Spacings.S6, Spacings.S6, Spacings.S6, Spacings.S6));

        // Header
        JLabel lblTitle = new JLabel("Đổi mật khẩu");
        lblTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblTitle.setForeground(Colors.FOREGROUND);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Cập nhật mật khẩu để bảo vệ tài khoản của bạn");
        lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSub.setForeground(Colors.MUTED);
        lblSub.setAlignmentX(LEFT_ALIGNMENT);

        main.add(lblTitle);
        main.add(Box.createVerticalStrut(2));
        main.add(lblSub);
        main.add(Box.createVerticalStrut(Spacings.S4));

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(Colors.BORDER_LIGHT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        main.add(sep);
        main.add(Box.createVerticalStrut(Spacings.S6));

        // Mật khẩu hiện tại
        txtMatKhauHienTai = new JPasswordField();
        stylePasswordField(txtMatKhauHienTai);
        main.add(fieldGroup("Mật khẩu hiện tại", txtMatKhauHienTai));
        errHienTai = errLabel();
        main.add(errHienTai);
        main.add(Box.createVerticalStrut(Spacings.S4));

        // Mật khẩu mới
        txtMatKhauMoi = new JPasswordField();
        stylePasswordField(txtMatKhauMoi);
        main.add(fieldGroup("Mật khẩu mới", txtMatKhauMoi));
        errMoi = errLabel();
        main.add(errMoi);
        main.add(Box.createVerticalStrut(Spacings.S1));

        JLabel hint = new JLabel("Ít nhất 8 ký tự, bao gồm chữ hoa, chữ thường và số");
        hint.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        hint.setForeground(Colors.MUTED);
        hint.setAlignmentX(LEFT_ALIGNMENT);
        main.add(hint);
        main.add(Box.createVerticalStrut(Spacings.S4));

        // Xác nhận mật khẩu
        txtXacNhanMatKhau = new JPasswordField();
        stylePasswordField(txtXacNhanMatKhau);
        main.add(fieldGroup("Xác nhận mật khẩu mới", txtXacNhanMatKhau));
        errXacNhan = errLabel();
        main.add(errXacNhan);
        main.add(Box.createVerticalStrut(Spacings.S3));

        // Clear errors on typing
        addClearOnType(txtMatKhauHienTai, errHienTai);
        addClearOnType(txtMatKhauMoi, errMoi);
        addClearOnType(txtXacNhanMatKhau, errXacNhan);

        // Checkbox hiện mật khẩu
        chkHienMK = new JCheckBox("Hiện mật khẩu");
        chkHienMK.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        chkHienMK.setForeground(Colors.TEXT_SECONDARY);
        chkHienMK.setOpaque(false);
        chkHienMK.setAlignmentX(LEFT_ALIGNMENT);
        chkHienMK.addActionListener(ev -> {
            char echo = chkHienMK.isSelected() ? (char) 0 : '\u2022';
            txtMatKhauHienTai.setEchoChar(echo);
            txtMatKhauMoi.setEchoChar(echo);
            txtXacNhanMatKhau.setEchoChar(echo);
        });
        main.add(chkHienMK);
        main.add(Box.createVerticalStrut(Spacings.S6));

        // Buttons
        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT, Spacings.S3, 0));
        btns.setOpaque(false);
        btns.setAlignmentX(LEFT_ALIGNMENT);

        btnHuy = new RoundedButton(100, 35, 10, "Hủy", Colors.BACKGROUND);
        btnHuy.setForeground(Colors.TEXT_PRIMARY);
        btnHuy.setBorder(BorderFactory.createLineBorder(Colors.BORDER));
        btnHuy.addActionListener(this);

        btnDoiMatKhau = new RoundedButton(140, 35, 10, "Đổi mật khẩu", Colors.PRIMARY);
        btnDoiMatKhau.addActionListener(this);

        btns.add(btnHuy);
        btns.add(btnDoiMatKhau);
        main.add(btns);

        setContentPane(main);
    }

    private void stylePasswordField(JPasswordField f) {
        f.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        f.setForeground(Colors.FOREGROUND);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(Spacings.S2, Spacings.S3, Spacings.S2, Spacings.S3)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        f.setAlignmentX(LEFT_ALIGNMENT);
    }

    private JPanel fieldGroup(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lbl.setForeground(Colors.FOREGROUND);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        field.setAlignmentX(LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(Spacings.S2));
        p.add(field);
        return p;
    }

    private JLabel errLabel() {
        JLabel l = new JLabel();
        l.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        l.setForeground(Colors.DANGER);
        l.setAlignmentX(LEFT_ALIGNMENT);
        l.setVisible(false);
        return l;
    }

    private void addClearOnType(JPasswordField field, JLabel errLbl) {
        field.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { errLbl.setVisible(false); }
            public void removeUpdate(DocumentEvent e) { errLbl.setVisible(false); }
            public void changedUpdate(DocumentEvent e) { errLbl.setVisible(false); }
        });
    }

    private void showErr(JLabel errLbl, String msg) {
        errLbl.setText("✗ " + msg);
        errLbl.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnDoiMatKhau) {
            TaiKhoan tk = taiKhoanService.layTaiKhoanTheoToken(currentToken);
            if (tk == null) {
                JOptionPane.showMessageDialog(this, "Phiên đăng nhập hết hạn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                dispose();
                return;
            }

            String mkHienTai = new String(txtMatKhauHienTai.getPassword()).trim();
            String mkMoi = new String(txtMatKhauMoi.getPassword()).trim();
            String xacNhan = new String(txtXacNhanMatKhau.getPassword()).trim();

            boolean valid = true;
            if (mkHienTai.isEmpty()) { showErr(errHienTai, "Vui lòng nhập mật khẩu hiện tại"); valid = false; }
            if (mkMoi.isEmpty()) { showErr(errMoi, "Vui lòng nhập mật khẩu mới"); valid = false; }
            if (xacNhan.isEmpty()) { showErr(errXacNhan, "Vui lòng xác nhận mật khẩu mới"); valid = false; }
            if (!valid) return;

            if (!mkMoi.equals(xacNhan)) {
                showErr(errXacNhan, "Xác nhận không khớp với mật khẩu mới");
                return;
            }

            if (!taiKhoanService.xacThucMatKhau(tk.getTenDangNhap(), mkHienTai)) {
                showErr(errHienTai, "Mật khẩu hiện tại không đúng");
                return;
            }

            if (taiKhoanService.doiMatKhau(tk.getTenDangNhap(), mkMoi)) {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Đổi mật khẩu thất bại!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }

        } else if (src == btnHuy) {
            dispose();
        }
    }
}
