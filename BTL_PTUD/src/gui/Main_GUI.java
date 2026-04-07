package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

import exception.MenuBarPanel;
//import gui.DangKy_GUI;

public class Main_GUI extends JFrame implements ActionListener {

	private JPanel pHeader;
	private JPanel pMainHeader;
	private JPanel pLogo;
	private JPanel pDangXuat;

	private JLabel lblLogo;
	private JLabel lblNhanVienTK;
	private JButton btnDangXuat;

	private MenuBarPanel menuBarPanel;

	private JPanel contentPanel;
	private CardLayout cardLayout;
	private Component LapHoaDon_GUI ;

	public Main_GUI() {

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
		pMainHeader.setPreferredSize(new Dimension(0, 90));
		pMainHeader.setBackground(new Color(141, 141, 141));
		pHeader.add(pMainHeader, BorderLayout.NORTH);

		pLogo = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pLogo.setOpaque(false);
		lblLogo = new JLabel("LOGO");
		lblLogo.setForeground(Color.WHITE);
		lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 20));
		pLogo.add(lblLogo);
		pMainHeader.add(pLogo, BorderLayout.WEST);

		// ===== MENU BÊN TRÁI =====
		menuBarPanel = new MenuBarPanel(this);
		pHeader.add(menuBarPanel, BorderLayout.CENTER);

		pDangXuat = new JPanel();
		pDangXuat.setLayout(new BoxLayout(pDangXuat, BoxLayout.Y_AXIS));
		pDangXuat.setPreferredSize(new Dimension(0, 70));
		pDangXuat.setOpaque(false);

		lblNhanVienTK = new JLabel("Nhân viên");
		lblNhanVienTK.setForeground(Color.WHITE);
		lblNhanVienTK.setAlignmentX(Component.CENTER_ALIGNMENT);

		btnDangXuat = new JButton("Đăng xuất");
		btnDangXuat.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnDangXuat.addActionListener(this);

		pDangXuat.add(lblNhanVienTK);
		pDangXuat.add(Box.createVerticalStrut(30));
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
		

		//	Xử lý
		contentPanel.add(new gui.LapHoaDon_GUI(	), "BanHang");

		contentPanel.add(createEmptyPage("DoiHang"), "DoiHang");
		contentPanel.add(createEmptyPage("TraHang"), "TraHang");
		
//		Tra cứu
		contentPanel.add(createEmptyPage("TraCuuHoaDon"), "TraCuuHoaDon");
		contentPanel.add(createEmptyPage("TraCuuPhieuNhap"), "TraCuuPhieuNhap");
		contentPanel.add(createEmptyPage("TraCuuDoiHang"),"TraCuuDoiHang");
		contentPanel.add(createEmptyPage("TraCuuTraHang"), "TraCuuTraHang");
		
//		Thống kê
		contentPanel.add(createEmptyPage("ThongKeDoanhThu"), "ThongKeDoanhThu");
		contentPanel.add(createEmptyPage("ThongKeKhachHang"), "ThongKeKhachHang");
		contentPanel.add(createEmptyPage("ThongKeSanPham"), "ThongKeSanPham");
		
		


		cardLayout.show(contentPanel, "NhapHang");
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
				dispose();
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new Main_GUI().setVisible(true);
		});
	}
}
