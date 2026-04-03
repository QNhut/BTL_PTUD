package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.*;

import constants.Colors;
import constants.FontStyle;
import constants.Spacings;

public class TaiKhoan_GUI extends JPanel implements ActionListener {

	private JTextField txtTenDangNhap, txtHoTen, txtEmail, txtSoDienThoai;
	private JPasswordField txtMatKhauHienTai, txtMatKhauMoi, txtXacNhanMatKhau;
	private JButton btnLuuThayDoi, btnHuyThongTin, btnDoiMatKhau, btnHuyMatKhau;

	public TaiKhoan_GUI() {
		setLayout(new BorderLayout());
		setBackground(Colors.SECONDARY);

		// ===== SCROLLABLE CONTENT =====
		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		wrapper.setBackground(Colors.SECONDARY);
		wrapper.setBorder(BorderFactory.createEmptyBorder(Spacings.S8, 40, Spacings.S8, 40));

		// ===== TIÊU ĐỀ =====
		JPanel pTieuDe = new JPanel();
		pTieuDe.setLayout(new BoxLayout(pTieuDe, BoxLayout.Y_AXIS));
		pTieuDe.setBackground(Colors.SECONDARY);
		pTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel lblTieuDe = new JLabel("Tài khoản");
		lblTieuDe.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
		lblTieuDe.setForeground(Colors.FOREGROUND);
		lblTieuDe.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel lblMoTa = new JLabel("Quản lý thông tin tài khoản của bạn");
		lblMoTa.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lblMoTa.setForeground(Colors.MUTED);
		lblMoTa.setAlignmentX(Component.LEFT_ALIGNMENT);

		pTieuDe.add(lblTieuDe);
		pTieuDe.add(Box.createVerticalStrut(Spacings.S1));
		pTieuDe.add(lblMoTa);

		wrapper.add(pTieuDe);
		wrapper.add(Box.createVerticalStrut(Spacings.S6));

		// ===== CARD: THÔNG TIN CÁ NHÂN =====
		JPanel cardThongTin = createCard();

		JLabel lblThongTinTitle = new JLabel("Thông tin cá nhân");
		lblThongTinTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
		lblThongTinTitle.setForeground(Colors.FOREGROUND);
		lblThongTinTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		cardThongTin.add(lblThongTinTitle);
		cardThongTin.add(Box.createVerticalStrut(Spacings.S6));

		// Tên đăng nhập
		txtTenDangNhap = new JTextField("admin");
		txtTenDangNhap.setEditable(false);
		txtTenDangNhap.setBackground(Colors.SECONDARY);
		addFormField(cardThongTin, "Tên đăng nhập", txtTenDangNhap);

		// Họ và tên
		txtHoTen = new JTextField("Quản trị viên");
		addFormField(cardThongTin, "Họ và tên", txtHoTen);

		// Email
		txtEmail = new JTextField("admin@example.com");
		addFormField(cardThongTin, "Email", txtEmail);

		// Số điện thoại
		txtSoDienThoai = new JTextField("0123456789");
		addFormField(cardThongTin, "Số điện thoại", txtSoDienThoai);

		// Buttons
		JPanel pBtnThongTin = new JPanel(new FlowLayout(FlowLayout.LEFT, Spacings.S3, 0));
		pBtnThongTin.setOpaque(false);
		pBtnThongTin.setAlignmentX(Component.LEFT_ALIGNMENT);

		btnLuuThayDoi = createPrimaryButton("Lưu thay đổi");
		btnHuyThongTin = createSecondaryButton("Hủy");

		pBtnThongTin.add(btnLuuThayDoi);
		pBtnThongTin.add(btnHuyThongTin);
		cardThongTin.add(Box.createVerticalStrut(Spacings.S4));
		cardThongTin.add(pBtnThongTin);

		wrapper.add(cardThongTin);
		wrapper.add(Box.createVerticalStrut(Spacings.S6));

		// ===== CARD: ĐỔI MẬT KHẨU =====
		JPanel cardMatKhau = createCard();

		JLabel lblMatKhauTitle = new JLabel("Đổi mật khẩu");
		lblMatKhauTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
		lblMatKhauTitle.setForeground(Colors.FOREGROUND);
		lblMatKhauTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		cardMatKhau.add(lblMatKhauTitle);
		cardMatKhau.add(Box.createVerticalStrut(Spacings.S6));

		// Mật khẩu hiện tại
		txtMatKhauHienTai = new JPasswordField();
		addFormField(cardMatKhau, "Mật khẩu hiện tại", txtMatKhauHienTai);

		// Mật khẩu mới
		txtMatKhauMoi = new JPasswordField();
		addFormField(cardMatKhau, "Mật khẩu mới", txtMatKhauMoi);

		// Xác nhận mật khẩu mới
		txtXacNhanMatKhau = new JPasswordField();
		addFormField(cardMatKhau, "Xác nhận mật khẩu mới", txtXacNhanMatKhau);

		// Buttons
		JPanel pBtnMatKhau = new JPanel(new FlowLayout(FlowLayout.LEFT, Spacings.S3, 0));
		pBtnMatKhau.setOpaque(false);
		pBtnMatKhau.setAlignmentX(Component.LEFT_ALIGNMENT);

		btnDoiMatKhau = createPrimaryButton("Đổi mật khẩu");
		btnHuyMatKhau = createSecondaryButton("Hủy");

		pBtnMatKhau.add(btnDoiMatKhau);
		pBtnMatKhau.add(btnHuyMatKhau);
		cardMatKhau.add(Box.createVerticalStrut(Spacings.S4));
		cardMatKhau.add(pBtnMatKhau);

		wrapper.add(cardMatKhau);
		wrapper.add(Box.createVerticalGlue());

		// ===== SCROLL PANE =====
		JScrollPane scrollPane = new JScrollPane(wrapper);
		scrollPane.setBorder(null);
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scrollPane, BorderLayout.CENTER);
	}

	// ===== TẠO CARD CÓ VIỀN =====
	private JPanel createCard() {
		JPanel card = new JPanel();
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBackground(Colors.BACKGROUND);
		card.setAlignmentX(Component.LEFT_ALIGNMENT);
		card.setMaximumSize(new Dimension(650, Integer.MAX_VALUE));

		Border lineBorder = BorderFactory.createLineBorder(Colors.BORDER, 1);
		Border padding = BorderFactory.createEmptyBorder(Spacings.S6, Spacings.S6, Spacings.S6, Spacings.S6);
		card.setBorder(BorderFactory.createCompoundBorder(lineBorder, padding));

		return card;
	}

	// ===== THÊM TRƯỜNG FORM =====
	private void addFormField(JPanel parent, String labelText, JTextField textField) {
		JLabel lbl = new JLabel(labelText);
		lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lbl.setForeground(Colors.FOREGROUND);
		lbl.setAlignmentX(Component.LEFT_ALIGNMENT);

		textField.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		textField.setForeground(Colors.FOREGROUND);
		textField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Colors.BORDER, 1),
				BorderFactory.createEmptyBorder(Spacings.S2, Spacings.S3, Spacings.S2, Spacings.S3)));
		textField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
		textField.setAlignmentX(Component.LEFT_ALIGNMENT);

		parent.add(lbl);
		parent.add(Box.createVerticalStrut(Spacings.S2));
		parent.add(textField);
		parent.add(Box.createVerticalStrut(Spacings.S4));
	}

	// ===== NÚT CHÍNH (XANH LÁ) =====
	private JButton createPrimaryButton(String text) {
		JButton btn = new JButton(text);
		btn.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		btn.setForeground(Color.WHITE);
		btn.setBackground(Colors.PRIMARY);
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setOpaque(true);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.setBorder(BorderFactory.createEmptyBorder(Spacings.S2, Spacings.S4, Spacings.S2, Spacings.S4));
		btn.addActionListener(this);
		return btn;
	}

	// ===== NÚT PHỤ (TRẮNG) =====
	private JButton createSecondaryButton(String text) {
		JButton btn = new JButton(text);
		btn.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		btn.setForeground(Colors.FOREGROUND);
		btn.setBackground(Colors.BACKGROUND);
		btn.setFocusPainted(false);
		btn.setBorderPainted(true);
		btn.setOpaque(true);
		btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btn.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Colors.BORDER, 1),
				BorderFactory.createEmptyBorder(Spacings.S2, Spacings.S4, Spacings.S2, Spacings.S4)));
		btn.addActionListener(this);
		return btn;
	}

	// ===== XỬ LÝ SỰ KIỆN =====
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();

		if (src == btnLuuThayDoi) {
			String hoTen = txtHoTen.getText().trim();
			String email = txtEmail.getText().trim();
			String sdt = txtSoDienThoai.getText().trim();

			if (hoTen.isEmpty() || email.isEmpty() || sdt.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			JOptionPane.showMessageDialog(this, "Cập nhật thông tin thành công!", "Thành công",
					JOptionPane.INFORMATION_MESSAGE);

		} else if (src == btnHuyThongTin) {
			txtHoTen.setText("");
			txtEmail.setText("");
			txtSoDienThoai.setText("");

		} else if (src == btnDoiMatKhau) {
			String mkHienTai = new String(txtMatKhauHienTai.getPassword()).trim();
			String mkMoi = new String(txtMatKhauMoi.getPassword()).trim();
			String xacNhan = new String(txtXacNhanMatKhau.getPassword()).trim();

			if (mkHienTai.isEmpty() || mkMoi.isEmpty() || xacNhan.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập đầy đủ thông tin!", "Lỗi",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			if (!mkMoi.equals(xacNhan)) {
				JOptionPane.showMessageDialog(this, "Mật khẩu mới và xác nhận không khớp!", "Lỗi",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			JOptionPane.showMessageDialog(this, "Đổi mật khẩu thành công!", "Thành công",
					JOptionPane.INFORMATION_MESSAGE);

			txtMatKhauHienTai.setText("");
			txtMatKhauMoi.setText("");
			txtXacNhanMatKhau.setText("");

		} else if (src == btnHuyMatKhau) {
			txtMatKhauHienTai.setText("");
			txtMatKhauMoi.setText("");
			txtXacNhanMatKhau.setText("");
		}
	}
}
