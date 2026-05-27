package gui;

import constants.Colors;
import constants.FontStyle;
import entity.TaiKhoan;
import exception.MenuBarPanel;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;
import service.TaiKhoan_Service;

@SuppressWarnings("serial")
public class Main_GUI extends JFrame {

    private JPanel pHeader;
    private JPanel pMainHeader;
    private JPanel pLogo;
    private JPanel pDangXuat;

    private JLabel lblLogo;
    private JLabel lblNhanVienTK;
    private JLabel lblVaiTroTK;

    private MenuBarPanel menuBarPanel;

    private JPanel contentPanel;
    private CardLayout cardLayout;
    private TaiKhoan_Service taiKhoanService;
    private String currentToken;
    private TaiKhoan taiKhoanDangNhap;
    private Timer authTimer;
    private boolean daDangXuat = false;
    private JLabel lblSubLogo;
    private ThongKeDoanhThu_GUI thongKeDoanhThuGUI;
    private ThongKeKhachHang_GUI thongKeKhachHangGUI;
    private ThongKeSanPham_GUI thongKeSanPhamGUI;
    private SanPham_GUI sanPhamGUI;
    private NhanVien_GUI nhanVienGUI;
    private KhachHang_GUI khachHangGUI;
    private NhaCungCap_GUI nhaCungCapGUI;
    private KhuyenMai_GUI khuyenMaiGUI;
    private Thue_GUI thueGUI;
    private HoaDon_GUI hoaDonGUI;
    private TraCuuHoaDon_GUI traCuuHoaDonGUI;
    private TraCuuPhieuNhap_GUI traCuuPhieuNhapGUI;
    private TraCuuDatThuoc_GUI traCuuDatThuocGUI;
    private TraCuuDoiHang_GUI traCuuDoiHangGUI;
    private TraCuuTraHang_GUI traCuuTraHangGUI;
    private DatTruoc_GUI datTruocGUI;
    private TrangChu_GUI trangChuGUI;
    private NhapHang_GUI nhapHangGUI;
    private DoiHang_GUI doiHangGUI;
    private TraHang_GUI traHangGUI;

    public Main_GUI() {
        this(new TaiKhoan_Service(), null);
    }

    public Main_GUI(TaiKhoan_Service taiKhoanService, String token) {
        this.taiKhoanService = taiKhoanService;
        this.currentToken = token;
        if (this.taiKhoanService != null && this.currentToken != null) {
            this.taiKhoanDangNhap = this.taiKhoanService.layTaiKhoanTheoToken(this.currentToken);
        }
        if (this.taiKhoanDangNhap == null) {
            JOptionPane.showMessageDialog(null, "Phiên đăng nhập không hợp lệ hoặc đã hết hạn. Vui lòng đăng nhập lại.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            SwingUtilities.invokeLater(() -> new DangNhap_GUI().setVisible(true));
            dispose();
            return;
        }

        setTitle("Quản lý bán hàng");
        setSize(1450, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ===== HEADER =====
        pHeader = new JPanel(new BorderLayout());
        pHeader.setPreferredSize(new Dimension(300, 0));
        pHeader.setBackground(Color.PINK);
        add(pHeader, BorderLayout.WEST);

        pMainHeader = new JPanel(new BorderLayout());
        pMainHeader.setPreferredSize(new Dimension(0, 80));
//		pMainHeader.setBackground(new Color(141, 141, 141));
        pMainHeader.setBackground(Color.WHITE);
        pMainHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));
        pHeader.add(pMainHeader, BorderLayout.NORTH);

        pMainHeader.add(pLogo = new JPanel(), BorderLayout.WEST);
        pLogo.setLayout(new BoxLayout(pLogo, BoxLayout.Y_AXIS));
        pLogo.setOpaque(false);
        pLogo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        lblLogo = new JLabel("Hệ thống quản lý");
        lblLogo.setForeground(Colors.FOREGROUND);
        lblLogo.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        pLogo.add(lblLogo);
        pLogo.add(Box.createVerticalStrut(10));
        pLogo.add(lblSubLogo = new JLabel("Phần mềm bán hàng"));
        lblSubLogo.setForeground(Colors.MUTED);
        lblSubLogo.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));

        pLogo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        pLogo.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                showPage("TrangChu");
            }
        });

        // ===== MENU BÊN TRÁI =====
        menuBarPanel = new MenuBarPanel(this);
        pHeader.add(menuBarPanel, BorderLayout.CENTER);

        pDangXuat = new JPanel();
        pDangXuat.setLayout(new BoxLayout(pDangXuat, BoxLayout.Y_AXIS));
        pDangXuat.setPreferredSize(new Dimension(0, 90));
        pDangXuat.setOpaque(true);
        pDangXuat.setBackground(Colors.BACKGROUND);
        pDangXuat.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(12, 0, 16, 0)));

        String tenHienThi = "Nhân viên";
        if (taiKhoanDangNhap != null && taiKhoanDangNhap.getNhanVien() != null) {
            tenHienThi = taiKhoanDangNhap.getNhanVien().getTenNhanVien();
        }

        String vaiTroHienThi = "Nhân viên";
        if (taiKhoanDangNhap != null && taiKhoanDangNhap.getNhanVien() != null
                && taiKhoanDangNhap.getNhanVien().getChucVu() != null
                && taiKhoanDangNhap.getNhanVien().getChucVu().getTenChucVu() != null
                && !taiKhoanDangNhap.getNhanVien().getChucVu().getTenChucVu().trim().isEmpty()) {
            vaiTroHienThi = taiKhoanDangNhap.getNhanVien().getChucVu().getTenChucVu();
        }

        JPanel accountCard = new JPanel();
        accountCard.setLayout(new BorderLayout(12, 0));
        accountCard.setBackground(Colors.BACKGROUND);
        accountCard.setAlignmentX(Component.CENTER_ALIGNMENT);
        accountCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
        accountCard.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));

        String linkAnhNhanVien = null;
        if (taiKhoanDangNhap != null && taiKhoanDangNhap.getNhanVien() != null) {
            linkAnhNhanVien = taiKhoanDangNhap.getNhanVien().getHinhAnh();
        }
        ImageIcon userAvatar = loadNhanVienAvatar(linkAnhNhanVien, 28, 28);
        if (userAvatar == null) {
            userAvatar = loadIcon("data/img/icons/people.png", 28, 28);
        }
        userAvatar = toCircularIcon(userAvatar, 32);
        JLabel lblUserIcon = new JLabel(userAvatar);
        lblUserIcon.setPreferredSize(new Dimension(48, 48));
        lblUserIcon.setHorizontalAlignment(SwingConstants.CENTER);
        lblUserIcon.setVerticalAlignment(SwingConstants.CENTER);

        JPanel userInfoPanel = new JPanel();
        userInfoPanel.setOpaque(false);
        userInfoPanel.setLayout(new BoxLayout(userInfoPanel, BoxLayout.Y_AXIS));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));

        lblNhanVienTK = new JLabel(tenHienThi);
        lblNhanVienTK.setForeground(Colors.TEXT_PRIMARY);
        lblNhanVienTK.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblNhanVienTK.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblVaiTroTK = new JLabel(vaiTroHienThi);
        lblVaiTroTK.setForeground(Colors.TEXT_SECONDARY);
        lblVaiTroTK.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblVaiTroTK.setAlignmentX(Component.LEFT_ALIGNMENT);

        userInfoPanel.add(lblNhanVienTK);
        userInfoPanel.add(Box.createVerticalStrut(2));
        userInfoPanel.add(lblVaiTroTK);

        accountCard.add(lblUserIcon, BorderLayout.WEST);
        accountCard.add(userInfoPanel, BorderLayout.CENTER);

        pDangXuat.add(accountCard);
        pHeader.add(pDangXuat, BorderLayout.SOUTH);

        // ===== CONTENT =====
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(Color.WHITE);
        add(contentPanel, BorderLayout.CENTER);

//		Các panel được khởi tạo lười (lazy) trong showPage() để tránh đóng băng UI khi khởi động.
//		Hệ thống và trang chủ khởi tạo ngay vì nhẹ / cần sẵn sàng tức thì.
        contentPanel.add(new TaiKhoan_GUI(taiKhoanService, token), "TaiKhoan");
        trangChuGUI = new TrangChu_GUI(this::showPage);
        contentPanel.add(trangChuGUI, "TrangChu");
        contentPanel.add(new TroGiup_GUI(), "TroGiup");

        cardLayout.show(contentPanel, "TrangChu");

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dongUngDungGiuSession();
            }
        });

        batDauTheoDoiToken();
    }

    public void showPage(String pageName) {
        if ("Thoat".equals(pageName)) {
            xacNhanDangXuat();
            return;
        }
        // Đổi mật khẩu — mở dialog riêng, không dùng CardLayout
        if ("DoiMatKhau".equals(pageName)) {
            new DoiMatKhau_GUI(taiKhoanService, currentToken).setVisible(true);
            return;
        }

        // Lazy-init: tạo panel lần đầu, các lần sau chỉ refresh
        switch (pageName) {
            case "TrangChu":
                if (trangChuGUI != null) { trangChuGUI.refresh(); }
                break;
            case "SanPham":
                if (sanPhamGUI == null) { sanPhamGUI = new SanPham_GUI(); contentPanel.add(sanPhamGUI, "SanPham"); }
                else { sanPhamGUI.refresh(); }
                break;
            case "NhanVien":
                if (nhanVienGUI == null) { nhanVienGUI = new NhanVien_GUI(); contentPanel.add(nhanVienGUI, "NhanVien"); }
                else { nhanVienGUI.refresh(); }
                break;
            case "KhachHang":
                if (khachHangGUI == null) { khachHangGUI = new KhachHang_GUI(); contentPanel.add(khachHangGUI, "KhachHang"); }
                else { khachHangGUI.refresh(); }
                break;
            case "NhaCungCap":
                if (nhaCungCapGUI == null) { nhaCungCapGUI = new NhaCungCap_GUI(); contentPanel.add(nhaCungCapGUI, "NhaCungCap"); }
                else { nhaCungCapGUI.refresh(); }
                break;
            case "KhuyenMai":
                if (khuyenMaiGUI == null) { khuyenMaiGUI = new KhuyenMai_GUI(); contentPanel.add(khuyenMaiGUI, "KhuyenMai"); }
                else { khuyenMaiGUI.refresh(); }
                break;
            case "Thue":
                if (thueGUI == null) { thueGUI = new Thue_GUI(); contentPanel.add(thueGUI, "Thue"); }
                else { thueGUI.refresh(); }
                break;
            case "BanHang":
                if (hoaDonGUI == null) {
                    hoaDonGUI = new HoaDon_GUI(taiKhoanDangNhap != null ? taiKhoanDangNhap.getNhanVien() : null);
                    contentPanel.add(hoaDonGUI, "BanHang");
                } else { hoaDonGUI.refresh(); }
                break;
            case "NhapHang":
                if (nhapHangGUI == null) { nhapHangGUI = new NhapHang_GUI(taiKhoanDangNhap != null ? taiKhoanDangNhap.getNhanVien() : null); contentPanel.add(nhapHangGUI, "NhapHang"); }
                else { nhapHangGUI.refresh(); }
                break;
            case "DoiHang":
                if (doiHangGUI == null) { doiHangGUI = new DoiHang_GUI(); contentPanel.add(doiHangGUI, "DoiHang"); }
                else { doiHangGUI.refresh(); }
                break;
            case "TraHang":
                if (traHangGUI == null) { traHangGUI = new TraHang_GUI(); contentPanel.add(traHangGUI, "TraHang"); }
                else { traHangGUI.refresh(); }
                break;
            case "DatTruoc":
                if (datTruocGUI == null) {
                    datTruocGUI = new DatTruoc_GUI(taiKhoanDangNhap != null ? taiKhoanDangNhap.getNhanVien() : null);
                    contentPanel.add(datTruocGUI, "DatTruoc");
                } else { datTruocGUI.refresh(); }
                break;
            case "TraCuuHoaDon":
                if (traCuuHoaDonGUI == null) { traCuuHoaDonGUI = new TraCuuHoaDon_GUI(); contentPanel.add(traCuuHoaDonGUI, "TraCuuHoaDon"); }
                else { traCuuHoaDonGUI.refresh(); }
                break;
            case "TraCuuPhieuNhap":
                if (traCuuPhieuNhapGUI == null) { traCuuPhieuNhapGUI = new TraCuuPhieuNhap_GUI(); contentPanel.add(traCuuPhieuNhapGUI, "TraCuuPhieuNhap"); }
                else { traCuuPhieuNhapGUI.refresh(); }
                break;
            case "TraCuuDoiHang":
                if (traCuuDoiHangGUI == null) { traCuuDoiHangGUI = new TraCuuDoiHang_GUI(); contentPanel.add(traCuuDoiHangGUI, "TraCuuDoiHang"); }
                else { traCuuDoiHangGUI.refresh(); }
                break;
            case "TraCuuTraHang":
                if (traCuuTraHangGUI == null) { traCuuTraHangGUI = new TraCuuTraHang_GUI(); contentPanel.add(traCuuTraHangGUI, "TraCuuTraHang"); }
                else { traCuuTraHangGUI.refresh(); }
                break;
            case "TraCuuDatThuoc":
                if (traCuuDatThuocGUI == null) { traCuuDatThuocGUI = new TraCuuDatThuoc_GUI(); contentPanel.add(traCuuDatThuocGUI, "TraCuuDatThuoc"); }
                else { traCuuDatThuocGUI.refresh(); }
                break;
            case "ThongKeDoanhThu":
                if (thongKeDoanhThuGUI == null) { thongKeDoanhThuGUI = new ThongKeDoanhThu_GUI(); contentPanel.add(thongKeDoanhThuGUI, "ThongKeDoanhThu"); }
                else { thongKeDoanhThuGUI.refresh(); }
                break;
            case "ThongKeKhachHang":
                if (thongKeKhachHangGUI == null) { thongKeKhachHangGUI = new ThongKeKhachHang_GUI(); contentPanel.add(thongKeKhachHangGUI, "ThongKeKhachHang"); }
                else { thongKeKhachHangGUI.refresh(); }
                break;
            case "ThongKeSanPham":
                if (thongKeSanPhamGUI == null) { thongKeSanPhamGUI = new ThongKeSanPham_GUI(); contentPanel.add(thongKeSanPhamGUI, "ThongKeSanPham"); }
                else { thongKeSanPhamGUI.refresh(); }
                break;
            default:
                break;
        }

        cardLayout.show(contentPanel, pageName);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void xacNhanDangXuat() {
        int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận",
                JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            dangXuatVaTroVeDangNhap(true);
        }
    }

    private void batDauTheoDoiToken() {
        authTimer = new Timer(30_000, e -> {
            if (taiKhoanService == null || currentToken == null) {
                dangXuatVaTroVeDangNhap(true);
                return;
            }
            TaiKhoan tk = taiKhoanService.layTaiKhoanTheoToken(currentToken);
            if (tk == null) {
                JOptionPane.showMessageDialog(this, "Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.", "Thông báo",
                        JOptionPane.WARNING_MESSAGE);
                dangXuatVaTroVeDangNhap(true);
            } else {
                taiKhoanDangNhap = tk;
            }
        });
        authTimer.start();
    }

    private void dangXuatVaTroVeDangNhap(boolean moLaiDangNhap) {
        if (daDangXuat) {
            return;
        }
        daDangXuat = true;

        if (authTimer != null) {
            authTimer.stop();
        }
        if (taiKhoanService != null && currentToken != null) {
            taiKhoanService.dangXuat(currentToken);
        }
        if (moLaiDangNhap) {
            SwingUtilities.invokeLater(() -> new DangNhap_GUI().setVisible(true));
        }
        dispose();
    }

    private void dongUngDungGiuSession() {
        if (authTimer != null) {
            authTimer.stop();
        }
    }

    private ImageIcon loadIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        if (icon.getIconWidth() <= 0 || icon.getIconHeight() <= 0) {
            return null;
        }
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private ImageIcon loadNhanVienAvatar(String linkAnh, int width, int height) {
        if (linkAnh == null || linkAnh.trim().isEmpty()) {
            return null;
        }

        String normalized = linkAnh.trim();
        ImageIcon icon = loadIcon(normalized, width, height);
        if (icon != null) {
            return icon;
        }

        if (!normalized.startsWith("data/")) {
            String fromDataImg = "data/img/" + normalized;
            icon = loadIcon(fromDataImg, width, height);
            if (icon != null) {
                return icon;
            }

            String fromDataRoot = "data/" + normalized;
            icon = loadIcon(fromDataRoot, width, height);
            if (icon != null) {
                return icon;
            }
        }

        File file = new File(normalized);
        if (file.exists()) {
            return loadIcon(file.getPath(), width, height);
        }

        return null;
    }

    private ImageIcon toCircularIcon(ImageIcon sourceIcon, int size) {
        if (sourceIcon == null || size <= 0) {
            return sourceIcon;
        }

        BufferedImage output = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = output.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setClip(new Ellipse2D.Float(0, 0, size, size));
        g2.drawImage(sourceIcon.getImage(), 0, 0, size, size, null);
        g2.dispose();

        return new ImageIcon(output);
    }

    // Entry point duy nhất là DangNhap_GUI.main()
    // Không cần main() ở đây vì DangNhap_GUI đã xử lý
    // ConnectDB, LookAndFeel và auto-login.
}
