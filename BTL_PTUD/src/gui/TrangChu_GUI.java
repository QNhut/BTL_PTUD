package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

import constants.Colors;
import constants.FontStyle;
import constants.Spacings;
import exception.RoundedButton;
import exception.RoundedPanel;

public class TrangChu_GUI extends JPanel {

	private static final Color CARD_ICON_GREEN = new Color(16, 185, 129);
	private static final Color CARD_ICON_ORANGE = new Color(249, 115, 22);
	private static final Color CARD_ICON_TEAL = new Color(20, 184, 166);
	private static final Color CARD_ICON_PURPLE = new Color(139, 92, 246);

	private static final Color DOT_ORANGE = new Color(245, 158, 11);
	private static final Color DOT_BLUE = new Color(59, 130, 246);
	private static final Color DOT_RED = new Color(239, 68, 68);

	public TrangChu_GUI() {
		setLayout(new BorderLayout());
		setBackground(Colors.SECONDARY);

		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		wrapper.setBackground(Colors.SECONDARY);
		wrapper.setBorder(BorderFactory.createEmptyBorder(Spacings.S6, 40, Spacings.S8, 40));

		// ===== TIÊU ĐỀ =====
		JLabel lblTitle = new JLabel("Trang chủ");
		lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
		lblTitle.setForeground(Colors.FOREGROUND);
		lblTitle.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lblSub = new JLabel("Chào mừng bạn đến với hệ thống quản lý bán hàng");
		lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lblSub.setForeground(Colors.MUTED);
		lblSub.setAlignmentX(LEFT_ALIGNMENT);

		wrapper.add(lblTitle);
		wrapper.add(Box.createVerticalStrut(Spacings.S1));
		wrapper.add(lblSub);
		wrapper.add(Box.createVerticalStrut(Spacings.S6));

		// 1. BANNER
		wrapper.add(buildBanner());
		wrapper.add(Box.createVerticalStrut(Spacings.S6));

		// 2. 4 STAT CARDS
		wrapper.add(buildStatRow());
		wrapper.add(Box.createVerticalStrut(Spacings.S6));

		// 3. MIDDLE ROW (Featured Products & Promotions)
		wrapper.add(buildMiddleRow());
		wrapper.add(Box.createVerticalStrut(Spacings.S6));

		// 4. WHY US
		wrapper.add(buildWhyUsRow());
		wrapper.add(Box.createVerticalStrut(Spacings.S6));

		// 5. FOOTER CONSULT
		wrapper.add(buildConsultSection());
		wrapper.add(Box.createVerticalStrut(Spacings.S8));

		JScrollPane scroll = new JScrollPane(wrapper);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, BorderLayout.CENTER);
	}

	// ============================================================
	// 1. BANNER SECTION
	// ============================================================
	private JPanel buildBanner() {
		RoundedPanel banner = new RoundedPanel(1000, 180, 14);
		banner.setLayout(new BorderLayout());
		banner.setBackground(new Color(229, 231, 235)); // Gray-200
		banner.setBorder(BorderFactory.createEmptyBorder(Spacings.S8, Spacings.S8, Spacings.S8, Spacings.S8));
		banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 180));
		banner.setAlignmentX(LEFT_ALIGNMENT);

		JPanel textPanel = new JPanel();
		textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
		textPanel.setOpaque(false);

		JLabel lblT = new JLabel("Sức khỏe là ưu tiên hàng đầu");
		lblT.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
		lblT.setForeground(Colors.FOREGROUND);

		JLabel lblS = new JLabel("PharmaCare - Nhà thuốc tin cậy cho sức khỏe gia đình bạn");
		lblS.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lblS.setForeground(Colors.MUTED);

		RoundedButton btn = new RoundedButton(150, 40, 14, "Tìm hiểu thêm", Color.BLACK);
		btn.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		btn.setForeground(Color.WHITE);

		textPanel.add(lblT);
		textPanel.add(Box.createVerticalStrut(Spacings.S2));
		textPanel.add(lblS);
		textPanel.add(Box.createVerticalStrut(Spacings.S6));
		textPanel.add(btn);

		banner.add(textPanel, BorderLayout.WEST);
		return banner;
	}

	// ============================================================
	// 2. 4 STAT CARDS ROW
	// ============================================================
	private JPanel buildStatRow() {
		JPanel row = new JPanel(new GridLayout(1, 4, Spacings.S4, 0));
		row.setOpaque(false);
		row.setAlignmentX(LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

		row.add(statCard("Tổng doanh thu", "125,430,000đ", CARD_ICON_GREEN));
		row.add(statCard("Đơn hàng hôm nay", "24", CARD_ICON_ORANGE));
		row.add(statCard("Sản phẩm", "342", CARD_ICON_TEAL));
		row.add(statCard("Khách hàng", "156", CARD_ICON_PURPLE));

		return row;
	}

	private JPanel statCard(String label, String value, Color accentColor) {
		JPanel card = createCard(250, 110);
		card.setLayout(new BorderLayout(Spacings.S3, 0));
		card.setBorder(BorderFactory.createEmptyBorder(Spacings.S4, Spacings.S6, Spacings.S4, Spacings.S6));

		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		left.setOpaque(false);

		JLabel lblLabel = new JLabel(label);
		lblLabel.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblLabel.setForeground(Colors.MUTED);

		JLabel lblValue = new JLabel(value);
		lblValue.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
		lblValue.setForeground(Colors.FOREGROUND);

		left.add(lblLabel);
		left.add(Box.createVerticalStrut(Spacings.S2));
		left.add(lblValue);
		card.add(left, BorderLayout.CENTER);

		JLabel dot = new JLabel("\u25CF");
		dot.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
		dot.setForeground(accentColor);
		dot.setVerticalAlignment(SwingConstants.CENTER);
		card.add(dot, BorderLayout.EAST);

		return card;
	}

	// ============================================================
	// 3. MIDDLE ROW (Featured Products & Promotions)
	// ============================================================
	private JPanel buildMiddleRow() {
		JPanel row = new JPanel(new GridLayout(1, 2, Spacings.S6, 0));
		row.setOpaque(false);
		row.setAlignmentX(LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

		// Left: Sản phẩm nổi bật
		JPanel left = createCard(500, 300);
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		left.setBorder(BorderFactory.createEmptyBorder(Spacings.S6, Spacings.S6, Spacings.S6, Spacings.S6));

		JLabel lblT = new JLabel("Sản phẩm nổi bật");
		lblT.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblT.setForeground(Colors.FOREGROUND);
		left.add(lblT);
		left.add(Box.createVerticalStrut(Spacings.S4));

		left.add(productRow("Vitamin C 1000mg", "Tăng cường miễn dịch", "45,000đ"));
		left.add(sep());
		left.add(productRow("Omega-3 Fish Oil", "Sức khỏe tim mạch", "120,000đ"));
		left.add(sep());
		left.add(productRow("Probiotics Pro", "Hệ tiêu hóa khỏe mạnh", "85,000đ"));

		// Right: Khuyến mãi hôm nay
		RoundedPanel right = new RoundedPanel(500, 300, 14);
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.setBackground(new Color(255, 251, 235)); // Amber-50
		right.setBorder(BorderFactory.createEmptyBorder(Spacings.S6, Spacings.S6, Spacings.S6, Spacings.S6));

		JLabel lblR = new JLabel("Khuyến mãi hôm nay");
		lblR.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblR.setForeground(Colors.FOREGROUND);
		right.add(lblR);
		right.add(Box.createVerticalStrut(Spacings.S6));

		right.add(notifRow(DOT_ORANGE, "Mua 2 tặng 1", "Các sản phẩm vitamin C được chọn\nĐến hết ngày hôm nay"));
		right.add(Box.createVerticalStrut(Spacings.S4));
		right.add(notifRow(CARD_ICON_TEAL, "Giảm 20%", "Mỹ phẩm chăm sóc da Hàn Quốc\nÁp dụng trong tuần này"));
		right.add(Box.createVerticalStrut(Spacings.S4));
		right.add(notifRow(DOT_BLUE, "Freeship", "Đơn hàng từ 500,000đ trở lên\nHàng ngày"));

		row.add(left);
		row.add(right);
		return row;
	}

	private JPanel productRow(String name, String desc, String price) {
		JPanel row = new JPanel(new BorderLayout());
		row.setOpaque(false);
		row.setBorder(BorderFactory.createEmptyBorder(Spacings.S3, 0, Spacings.S3, 0));

		JPanel info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		info.setOpaque(false);

		JLabel lblName = new JLabel(name);
		lblName.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblName.setForeground(Colors.FOREGROUND);

		JLabel lblDesc = new JLabel(desc);
		lblDesc.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblDesc.setForeground(Colors.MUTED);

		info.add(lblName);
		info.add(Box.createVerticalStrut(2));
		info.add(lblDesc);

		JLabel lblPrice = new JLabel(price);
		lblPrice.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblPrice.setForeground(Colors.FOREGROUND);

		row.add(info, BorderLayout.CENTER);
		row.add(lblPrice, BorderLayout.EAST);
		return row;
	}

	private JPanel notifRow(Color dotColor, String title, String detail) {
		JPanel row = new JPanel(new BorderLayout(Spacings.S3, 0));
		row.setOpaque(false);

		JLabel dot = new JLabel("\u25CF ");
		dot.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		dot.setForeground(dotColor);
		dot.setVerticalAlignment(SwingConstants.TOP);

		JPanel text = new JPanel();
		text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
		text.setOpaque(false);

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblTitle.setForeground(Colors.FOREGROUND);

		String[] lines = detail.split("\n");
		for (String line : lines) {
			JLabel lblL = new JLabel(line);
			lblL.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
			lblL.setForeground(line.contains("hôm nay") ? CARD_ICON_ORANGE : Colors.MUTED);
			text.add(lblL);
		}

		row.add(dot, BorderLayout.WEST);
		row.add(text, BorderLayout.CENTER);
		return row;
	}

	// ============================================================
	// 4. WHY US ROW
	// ============================================================
	private JPanel buildWhyUsRow() {
		RoundedPanel main = new RoundedPanel(1000, 250, 14);
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		main.setBackground(new Color(243, 244, 246)); // Gray-100
		main.setBorder(BorderFactory.createEmptyBorder(Spacings.S6, Spacings.S6, Spacings.S6, Spacings.S6));
		main.setAlignmentX(LEFT_ALIGNMENT);

		main.add(Box.createVerticalStrut(Spacings.S6));

		JPanel row = new JPanel(new GridLayout(1, 3, Spacings.S6, 0));
		row.setOpaque(false);

		row.add(featureCard("\u2661", "Tư vấn sức khỏe", "Đội ngũ dược sĩ chuyên nghiệp sẵn sàng tư vấn miễn phí"));
		row.add(featureCard("\u26E8", "Sản phẩm chất lượng", "Chỉ bán các sản phẩm từ các hãng dược phẩm uy tín"));
		row.add(featureCard("\uD83D\uDC65", "Giao hàng nhanh", "Giao hàng miễn phí trong vòng 24h tại TP.HCM"));

		main.add(row);
		return main;
	}

	private JPanel featureCard(String icon, String title, String desc) {
		RoundedPanel card = new RoundedPanel(300, 180, 14);
		card.setBackground(Colors.BACKGROUND);
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBorder(BorderFactory.createEmptyBorder(Spacings.S6, Spacings.S6, Spacings.S6, Spacings.S6));

		JLabel lblIcon = new JLabel(icon);
		lblIcon.setFont(FontStyle.font(FontStyle.XXL, FontStyle.NORMAL));
		lblIcon.setForeground(Colors.FOREGROUND);

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblTitle.setForeground(Colors.FOREGROUND);

		JLabel lblDesc = new JLabel("<html><body style='width: 150px'>" + desc + "</body></html>");
		lblDesc.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblDesc.setForeground(Colors.MUTED);

		card.add(lblIcon);
		card.add(Box.createVerticalStrut(Spacings.S4));
		card.add(lblTitle);
		card.add(Box.createVerticalStrut(2));
		card.add(lblDesc);

		return card;
	}

	// ============================================================
	// 5. FOOTER CONSULT SECTION
	// ============================================================
	private JPanel buildConsultSection() {
		RoundedPanel main = new RoundedPanel(1000, 250, 14);
		main.setLayout(new GridLayout(1, 2, Spacings.S6, 0));
		main.setBackground(new Color(229, 231, 235)); // Gray-200
		main.setBorder(BorderFactory.createEmptyBorder(Spacings.S8, Spacings.S8, Spacings.S8, Spacings.S8));
		main.setAlignmentX(LEFT_ALIGNMENT);
		main.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));

		// Left: Consult
		JPanel left = new JPanel();
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
		left.setOpaque(false);

		JLabel lblT = new JLabel("Cần tư vấn sức khỏe?");
		lblT.setFont(FontStyle.font(FontStyle.MD, FontStyle.BOLD));
		lblT.setForeground(Colors.FOREGROUND);

		JLabel lblS = new JLabel("Liên hệ ngay với các dược sĩ chuyên nghiệp của chúng tôi");
		lblS.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblS.setForeground(Colors.MUTED);

		RoundedButton btn = new RoundedButton(220, 40, 14, "Đặt lịch tư vấn miễn phí", Color.BLACK);
		btn.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		btn.setForeground(Color.WHITE);

		JPanel contact = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		contact.setOpaque(false);
		JLabel lblPhoneIcon = new JLabel("\u260E ");
		lblPhoneIcon.setFont(FontStyle.font(FontStyle.MD, FontStyle.NORMAL));
		JLabel lblPhone = new JLabel("<html><b>1900 6789</b><br><font size='2' color='gray'>Thứ 2-7: 8:00-22:00 | CN: 9:00-20:00</font></html>");
		lblPhone.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		contact.add(lblPhoneIcon);
		contact.add(lblPhone);

		left.add(lblT);
		left.add(Box.createVerticalStrut(2));
		left.add(lblS);
		left.add(Box.createVerticalStrut(Spacings.S6));
		left.add(btn);
		left.add(Box.createVerticalStrut(Spacings.S6));
		left.add(contact);

		// Right: Info
		JPanel right = new JPanel();
		right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
		right.setOpaque(false);

		right.add(infoBox("\u2316", "Chi nhánh chính", "123 Đường Nguyễn Huệ, Quận 1, TP.HCM"));
		right.add(Box.createVerticalStrut(Spacings.S4));
		right.add(infoBox("\u23F2", "Giờ hoạt động", "Thứ 2 - Chủ nhật: 8:00 - 22:00"));

		main.add(left);
		main.add(right);
		return main;
	}

	private JPanel infoBox(String icon, String title, String detail) {
		JPanel p = createCard(400, 80);
		p.setLayout(new BorderLayout(Spacings.S4, 0));
		p.setBorder(BorderFactory.createEmptyBorder(Spacings.S4, Spacings.S4, Spacings.S4, Spacings.S4));

		JLabel lblIcon = new JLabel(icon);
		lblIcon.setFont(FontStyle.font(FontStyle.MD, FontStyle.NORMAL));
		
		JPanel text = new JPanel();
		text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
		text.setOpaque(false);

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblTitle.setForeground(Colors.FOREGROUND);

		JLabel lblDetail = new JLabel(detail);
		lblDetail.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblDetail.setForeground(Colors.MUTED);

		text.add(lblTitle);
		text.add(Box.createVerticalStrut(2));
		text.add(lblDetail);

		p.add(lblIcon, BorderLayout.WEST);
		p.add(text, BorderLayout.CENTER);
		return p;
	}

	// ============================================================
	// HELPERS
	// ============================================================
	private JPanel createCard(int w, int h) {
		RoundedPanel card = new RoundedPanel(w, h, 14);
		card.setBackground(Colors.BACKGROUND);
		return card;
	}

	private JSeparator sep() {
		JSeparator s = new JSeparator(SwingConstants.HORIZONTAL);
		s.setForeground(Colors.BORDER_LIGHT);
		s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		s.setAlignmentX(LEFT_ALIGNMENT);
		return s;
	}
}
