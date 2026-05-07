package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import constants.Colors;
import constants.FontStyle;
import constants.Spacings;
import entity.NhanVien;
import entity.TaiKhoan;
import exception.RoundedButton;
import exception.RoundedPanel;
import exception.RoundedPasswordField;
import exception.RoundedTextField;
import service.NhanVien_Service;
import service.TaiKhoan_Service;

public class TaiKhoan_GUI extends JPanel implements ActionListener {

    private static final Color ACCENT = new Color(37, 99, 235);
    private static final int FIELD_H = 40;

    private JTextField txtTenDangNhap, txtHoTen, txtEmail, txtSoDienThoai, txtDiaChi, txtChucVu, txtCCCD;
    private JPasswordField txtMatKhau;
    private JRadioButton rbNam, rbNu;
    private JButton btnLuuThayDoi, btnDoiMatKhau;
    private JLabel errThongTin;
    private TaiKhoan_Service taiKhoanService;
    private String currentToken;
    private NhanVien_Service nhanVienService;

    public TaiKhoan_GUI(TaiKhoan_Service taiKhoanService, String token) {
        this.taiKhoanService = taiKhoanService;
        this.currentToken = token;
        this.nhanVienService = new NhanVien_Service();

        TaiKhoan tk = taiKhoanService.layTaiKhoanTheoToken(token);
        NhanVien nv = (tk != null) ? tk.getNhanVien() : null;

        setLayout(new BorderLayout());
        setBackground(Colors.SECONDARY);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Colors.SECONDARY);
        wrapper.setBorder(BorderFactory.createEmptyBorder(Spacings.S8, 40, Spacings.S8, 40));

        // ===== TIÊU ĐỀ =====
        JLabel lblTitle = new JLabel("Tài khoản");
        lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.FOREGROUND);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Quản lý thông tin tài khoản của bạn");
        lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSub.setForeground(Colors.MUTED);
        lblSub.setAlignmentX(LEFT_ALIGNMENT);

        wrapper.add(lblTitle);
        wrapper.add(Box.createVerticalStrut(Spacings.S1));
        wrapper.add(lblSub);
        wrapper.add(Box.createVerticalStrut(Spacings.S6));

        // ===== KHỞI TẠO TẤT CẢ FIELDS =====
        txtTenDangNhap = new RoundedTextField(0, FIELD_H, 12, "");
        txtTenDangNhap.setText(tk != null ? tk.getTenDangNhap() : "");
        txtTenDangNhap.setEditable(false);
        txtTenDangNhap.setBackground(Colors.SECONDARY);

        txtHoTen = new RoundedTextField(0, FIELD_H, 12, "");
        txtHoTen.setText(nv != null ? nv.getTenNhanVien() : "");

        txtCCCD = new RoundedTextField(0, FIELD_H, 12, "");
        txtCCCD.setText(nv != null && nv.getCCCD() != null ? nv.getCCCD() : "");

        txtChucVu = new RoundedTextField(0, FIELD_H, 12, "");
        txtChucVu.setText(nv != null && nv.getChucVu() != null ? nv.getChucVu().getTenChucVu() : "");
        txtChucVu.setEditable(false);
        txtChucVu.setBackground(Colors.SECONDARY);

        txtEmail = new RoundedTextField(0, FIELD_H, 12, "");
        txtEmail.setText(nv != null && nv.getEmail() != null ? nv.getEmail() : "");

        txtSoDienThoai = new RoundedTextField(0, FIELD_H, 12, "");
        txtSoDienThoai.setText(nv != null ? nv.getSoDienThoai() : "");

        txtDiaChi = new RoundedTextField(0, FIELD_H, 12, "");
        txtDiaChi.setText(nv != null && nv.getDiaChi() != null ? nv.getDiaChi() : "");

        txtMatKhau = new RoundedPasswordField(0, FIELD_H, 12, "");
        txtMatKhau.setText(tk != null && tk.getMatKhau() != null ? tk.getMatKhau() : "");
        txtMatKhau.setEditable(false);
        txtMatKhau.setBackground(Colors.SECONDARY);

        rbNam = new JRadioButton("Nam");
        rbNu  = new JRadioButton("Nữ");
        if (nv != null && !nv.isGioiTinh()) rbNu.setSelected(true);
        else rbNam.setSelected(true);

        JLabel lblStatus = new JLabel(
                nv != null && nv.isTrangThai() ? "Đang làm việc" : "Offline");
        lblStatus.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblStatus.setForeground(Colors.FOREGROUND);

        // ===== FORM LAYOUT =====
        JPanel formThongTin = new JPanel(new GridBagLayout());
        formThongTin.setOpaque(false);
        formThongTin.setAlignmentX(LEFT_ALIGNMENT);

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.weightx = 0.5;

        // Row 0: Tên đăng nhập | Họ và tên
        g.gridx = 0; g.gridy = 0;
        g.insets = new Insets(0, 0, Spacings.S4, Spacings.S3);
        formThongTin.add(fieldGroup("Tên đăng nhập", txtTenDangNhap), g);

        g.gridx = 1;
        g.insets = new Insets(0, Spacings.S3, Spacings.S4, 0);
        formThongTin.add(fieldGroup("Họ và tên", txtHoTen), g);

        // Row 1: CCCD | Chức vụ
        g.gridx = 0; g.gridy = 1;
        g.insets = new Insets(0, 0, Spacings.S4, Spacings.S3);
        formThongTin.add(fieldGroup("Căn cước công dân", txtCCCD), g);

        g.gridx = 1;
        g.insets = new Insets(0, Spacings.S3, Spacings.S4, 0);
        formThongTin.add(fieldGroup("Chức vụ", txtChucVu), g);

        // Row 2: Email | Số điện thoại
        g.gridx = 0; g.gridy = 2;
        g.insets = new Insets(0, 0, Spacings.S4, Spacings.S3);
        formThongTin.add(fieldGroup("Email", txtEmail), g);

        g.gridx = 1;
        g.insets = new Insets(0, Spacings.S3, Spacings.S4, 0);
        formThongTin.add(fieldGroup("Số điện thoại", txtSoDienThoai), g);

        // Row 3: Giới tính | Mật khẩu
        g.gridx = 0; g.gridy = 3;
        g.insets = new Insets(0, 0, Spacings.S4, Spacings.S3);
        formThongTin.add(fieldGroup("Giới tính", rbNam, rbNu), g);

        g.gridx = 1;
        g.insets = new Insets(0, Spacings.S3, Spacings.S4, 0);
        formThongTin.add(fieldGroup("Mật khẩu", txtMatKhau), g);

        // Row 4: Trạng thái | Địa chỉ
        g.gridx = 0; g.gridy = 4;
        g.insets = new Insets(0, 0, 0, Spacings.S3);
        formThongTin.add(fieldGroup("Trạng thái", lblStatus), g);

        g.gridx = 1;
        g.insets = new Insets(0, Spacings.S3, 0, 0);
        formThongTin.add(fieldGroup("Địa chỉ", txtDiaChi), g);

        // ===== BUTTONS =====
        btnLuuThayDoi = accentBtn("Lưu thay đổi");
        btnDoiMatKhau = accentBtn("Đổi mật khẩu");

        errThongTin = new JLabel();
        errThongTin.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        errThongTin.setForeground(Colors.DANGER);
        errThongTin.setVisible(false);

        JPanel btnsThongTin = new JPanel();
        btnsThongTin.setLayout(new BoxLayout(btnsThongTin, BoxLayout.Y_AXIS));
        btnsThongTin.setOpaque(false);
        btnsThongTin.setAlignmentX(LEFT_ALIGNMENT);
        btnsThongTin.add(errThongTin);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, Spacings.S3, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnDoiMatKhau);
        btnRow.add(btnLuuThayDoi);
        btnsThongTin.add(btnRow);

        wrapper.add(buildCard("Thông tin cá nhân", "Cập nhật thông tin cá nhân của bạn", formThongTin, btnsThongTin));
        wrapper.add(Box.createVerticalStrut(Spacings.S6));

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    // ============================================================
    // HELPER: Build Card chung
    // ============================================================
    private JPanel buildCard(String title, String subtitle, JPanel content, JPanel buttons) {
        Dimension cSize = content.getPreferredSize();
        int totalH = cSize.height + 200; // Ước tính chiều cao

        RoundedPanel card = new RoundedPanel(1000, totalH, 14);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Colors.BACKGROUND);
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createEmptyBorder(Spacings.S6, Spacings.S6, Spacings.S6, Spacings.S6));

        JPanel h = new JPanel();
        h.setLayout(new BoxLayout(h, BoxLayout.Y_AXIS));
        h.setOpaque(false);
        h.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblT = new JLabel(title);
        lblT.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblT.setForeground(Colors.FOREGROUND);

        JLabel lblS = new JLabel(subtitle);
        lblS.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblS.setForeground(Colors.MUTED);

        h.add(lblT);
        h.add(Box.createVerticalStrut(2));
        h.add(lblS);

        card.add(h);
        card.add(Box.createVerticalStrut(Spacings.S4));

        JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
        sep.setForeground(Colors.BORDER_LIGHT);
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setAlignmentX(LEFT_ALIGNMENT);
        card.add(sep);
        card.add(Box.createVerticalStrut(Spacings.S6));

        content.setAlignmentX(LEFT_ALIGNMENT);
        card.add(content);
        card.add(Box.createVerticalStrut(Spacings.S6));

        buttons.setAlignmentX(LEFT_ALIGNMENT);
        card.add(buttons);

        Dimension pref = card.getPreferredSize();
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, pref.height));
        card.setHeight(pref.height);

        return card;
    }
    
    // (styledField đã được thay thế bằng RoundedTextField trực tiếp)

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

    private JPanel fieldGroup(String label, JRadioButton... radios) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lbl.setForeground(Colors.FOREGROUND);
        lbl.setAlignmentX(LEFT_ALIGNMENT);

        ButtonGroup group = new ButtonGroup();
        JPanel radioPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, Spacings.S3, 0));
        radioPanel.setOpaque(false);
        radioPanel.setAlignmentX(LEFT_ALIGNMENT);
        for (JRadioButton rb : radios) {
            rb.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            rb.setForeground(Colors.FOREGROUND);
            rb.setOpaque(false);
            group.add(rb);
            radioPanel.add(rb);
        }

        p.add(lbl);
        p.add(Box.createVerticalStrut(Spacings.S2));
        p.add(radioPanel);
        return p;
    }

    // HELPER: Buttons
    private JButton accentBtn(String text) {
        RoundedButton btn = new RoundedButton(160, 40, 12, text, ACCENT);
        btn.setForeground(Color.WHITE);
        btn.addActionListener(this);
        return btn;
    }

    private JButton outlineBtn(String text) {
        JButton btn = new JButton(text);
        btn.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        btn.setForeground(Colors.FOREGROUND);
        btn.setBackground(Colors.BACKGROUND);
        btn.setFocusPainted(false);
        btn.setOpaque(true);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(Spacings.S2 + 1, Spacings.S6, Spacings.S2 + 1, Spacings.S6)));
        btn.addActionListener(this);
        return btn;
    }

    // XỬ LÝ SỰ KIỆN
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == btnLuuThayDoi) {
            TaiKhoan tk = taiKhoanService.layTaiKhoanTheoToken(currentToken);
            if (tk == null || tk.getNhanVien() == null) {
                JOptionPane.showMessageDialog(this, "Phiên đăng nhập hết hạn!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            NhanVien nv = tk.getNhanVien();

            String hoTen  = txtHoTen.getText().trim();
            String email  = txtEmail.getText().trim();
            String sdt    = txtSoDienThoai.getText().trim();
            String diaChi = txtDiaChi.getText().trim();
            String cccd   = txtCCCD.getText().trim();
            boolean gioiTinh = rbNam.isSelected();

            if (hoTen.isEmpty() || sdt.isEmpty()) {
                errThongTin.setText("✗ Vui lòng nhập đầy đủ họ tên và số điện thoại!");
                errThongTin.setVisible(true);
                return;
            }

            try {
                nv.setTenNhanVien(hoTen);
                nv.setSoDienThoai(cccd);
                nv.setEmail(email.isEmpty() ? null : email);
                nv.setDiaChi(diaChi.isEmpty() ? null : diaChi);
                nv.setGioiTinh(gioiTinh);
                nv.setCCCD(cccd);

                if (nhanVienService.updateNhanVien(nv)) {
                    errThongTin.setVisible(false);
                    JOptionPane.showMessageDialog(this,
                            "Cập nhật thông tin thành công!", "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Cập nhật thất bại!", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                errThongTin.setText("✗ " + ex.getMessage());
                errThongTin.setVisible(true);
            }

        } else if (src == btnDoiMatKhau) {
            new DoiMatKhau_GUI(taiKhoanService, currentToken).setVisible(true);
        }
    }
}