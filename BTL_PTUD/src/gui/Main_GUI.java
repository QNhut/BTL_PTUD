package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.*;

import constants.Colors;
import constants.FontStyle;
import entity.TaiKhoan;
import exception.MenuBarPanel;
import exception.RoundedButton;
import gui.TaiKhoan_GUI;
import service.TaiKhoan_Service;
//import gui.DangKy_GUI;

public class Main_GUI extends JFrame implements ActionListener {

	private JPanel pHeader;
	private JPanel pMainHeader;
	private JPanel pLogo;
	private JPanel pDangXuat;

	private JLabel lblLogo;
	private JLabel lblNhanVienTK;
	private JLabel lblVaiTroTK;
	private JButton btnDangXuat;

	private MenuBarPanel menuBarPanel;

	private JPanel contentPanel;
	private CardLayout cardLayout;
	private TaiKhoan_Service taiKhoanService;
	private String currentToken;
	private TaiKhoan taiKhoanDangNhap;
	private Timer authTimer;
	private boolean daDangXuat = false;
	private JLabel lblSubLogo;

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

		setTitle("Quản lý cửa hàng tiện lợi");
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

		// ===== MENU BÊN TRÁI =====
		menuBarPanel = new MenuBarPanel(this);
		pHeader.add(menuBarPanel, BorderLayout.CENTER);

		pDangXuat = new JPanel();
		pDangXuat.setLayout(new BoxLayout(pDangXuat, BoxLayout.Y_AXIS));
		pDangXuat.setPreferredSize(new Dimension(0, 120));
		pDangXuat.setOpaque(true);
		pDangXuat.setBackground(Colors.BACKGROUND);
		pDangXuat.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT),
				BorderFactory.createEmptyBorder(12, 0, 16, 0)));

		String tenHienThi = "Nhân viên";
		if (taiKhoanDangNhap != null && taiKhoanDangNhap.getNhanVien() != null) {
			tenHienThi = taiKhoanDangNhap.getNhanVien().getTenNV();
		}

		String vaiTroHienThi = "Nhân viên";
		if (taiKhoanDangNhap != null && taiKhoanDangNhap.getNhanVien() != null
				&& taiKhoanDangNhap.getNhanVien().getVaiTro() != null
				&& !taiKhoanDangNhap.getNhanVien().getVaiTro().trim().isEmpty()) {
			vaiTroHienThi = taiKhoanDangNhap.getNhanVien().getVaiTro();
		}

		JPanel accountCard = new JPanel();
		accountCard.setLayout(new BorderLayout(12, 0));
		accountCard.setBackground(Colors.BACKGROUND);
		accountCard.setAlignmentX(Component.CENTER_ALIGNMENT);
		accountCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 74));
		accountCard.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1),
				BorderFactory.createEmptyBorder(8, 10, 8, 10)));

		String linkAnhNhanVien = null;
		if (taiKhoanDangNhap != null && taiKhoanDangNhap.getNhanVien() != null) {
			linkAnhNhanVien = taiKhoanDangNhap.getNhanVien().getLinkAnh();
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

		btnDangXuat = new RoundedButton(220, 38, 12, "Đăng xuất", Colors.ACCENT);
		btnDangXuat.setForeground(Colors.BACKGROUND);
		btnDangXuat.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		btnDangXuat.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnDangXuat.addActionListener(this);

		userInfoPanel.add(lblNhanVienTK);
		userInfoPanel.add(Box.createVerticalStrut(2));
		userInfoPanel.add(lblVaiTroTK);

		accountCard.add(lblUserIcon, BorderLayout.WEST);
		accountCard.add(userInfoPanel, BorderLayout.CENTER);

		pDangXuat.add(accountCard);
		pDangXuat.add(Box.createVerticalStrut(12));
		pDangXuat.add(btnDangXuat);
		pHeader.add(pDangXuat, BorderLayout.SOUTH);

		// ===== CONTENT =====
		cardLayout = new CardLayout();
		contentPanel = new JPanel(cardLayout);
		contentPanel.setBackground(Color.WHITE);
		add(contentPanel, BorderLayout.CENTER);

//		Danh mục
		contentPanel.add(createEmptyPage("NhanVien"), "NhanVien");
		contentPanel.add(createEmptyPage("KhachHang"), "KhachHang");
		contentPanel.add(createEmptyPage("SanPham"), "SanPham");
		contentPanel.add(createEmptyPage("NhaCungCap"), "NhaCungCap");
		contentPanel.add(createEmptyPage("KhuyenMai"), "KhuyenMai");

//		Xử lý
		contentPanel.add(createEmptyPage("BanHang"), "BanHang");
		contentPanel.add(new NhapHang_GUI(), "NhapHang");
		contentPanel.add(createEmptyPage("DoiHang"), "DoiHang");
		contentPanel.add(createEmptyPage("TraHang"), "TraHang");

//		Tra cứu
		contentPanel.add(createEmptyPage("TraCuuHoaDon"), "TraCuuHoaDon");
		contentPanel.add(createEmptyPage("TraCuuPhieuNhap"), "TraCuuPhieuNhap");
		contentPanel.add(createEmptyPage("TraCuuDoiHang"), "TraCuuDoiHang");
		contentPanel.add(createEmptyPage("TraCuuTraHang"), "TraCuuTraHang");

//		Thống kê
		contentPanel.add(createEmptyPage("ThongKeDoanhThu"), "ThongKeDoanhThu");
		contentPanel.add(createEmptyPage("ThongKeKhachHang"), "ThongKeKhachHang");
		contentPanel.add(createEmptyPage("ThongKeSanPham"), "ThongKeSanPham");

//		Cài đặt
		contentPanel.add(new TaiKhoan_GUI(), "CaiDat");

		cardLayout.show(contentPanel, "NhapHang");

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				dongUngDungGiuSession();
			}
		});

		batDauTheoDoiToken();
	}

	private JPanel createEmptyPage(String name) {
		JPanel p = new JPanel(new GridBagLayout());
		JLabel lbl = new JLabel("PAGE: " + name);
		lbl.setFont(new Font("Segoe UI", Font.BOLD, 30));
		p.add(lbl);
		return p;
	}

	public void showPage(String pageName) {
		cardLayout.show(contentPanel, pageName);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnDangXuat) {
			int confirm = JOptionPane.showConfirmDialog(this, "Bạn có chắc chắn muốn đăng xuất?", "Xác nhận",
					JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.YES_OPTION) {
				dangXuatVaTroVeDangNhap(true);
			}
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

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new DangNhap_GUI().setVisible(true);
		});
	}
}
