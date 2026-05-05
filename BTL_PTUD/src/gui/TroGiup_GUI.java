package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

import constants.Colors;
import constants.FontStyle;
import constants.Spacings;
import exception.RoundedButton;
import exception.RoundedPanel;

public class TroGiup_GUI extends JPanel {

	private static final Color TAG_BLUE = new Color(59, 130, 246, 30);
	private static final Color TAG_BLUE_TEXT = new Color(59, 130, 246);
	private static final Color TAG_GREEN = new Color(34, 197, 94, 30);
	private static final Color TAG_GREEN_TEXT = new Color(34, 197, 94);
	private static final Color TAG_ORANGE = new Color(245, 158, 11, 30);
	private static final Color TAG_ORANGE_TEXT = new Color(245, 158, 11);
	private static final Color TAG_PURPLE = new Color(139, 92, 246, 30);
	private static final Color TAG_PURPLE_TEXT = new Color(139, 92, 246);

	public TroGiup_GUI() {
		setLayout(new BorderLayout());
		setBackground(Colors.SECONDARY);

		JPanel wrapper = new JPanel();
		wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
		wrapper.setBackground(Colors.SECONDARY);
		wrapper.setBorder(BorderFactory.createEmptyBorder(Spacings.S6, 40, Spacings.S8, 40));

		// ===== TIÊU ĐỀ =====
		JLabel lblTitle = new JLabel("Trợ giúp");
		lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
		lblTitle.setForeground(Colors.FOREGROUND);
		lblTitle.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lblSub = new JLabel("Hướng dẫn sử dụng & hỗ trợ nhanh");
		lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lblSub.setForeground(Colors.MUTED);
		lblSub.setAlignmentX(LEFT_ALIGNMENT);

		wrapper.add(lblTitle);
		wrapper.add(Box.createVerticalStrut(Spacings.S1));
		wrapper.add(lblSub);
		wrapper.add(Box.createVerticalStrut(Spacings.S6));

		// 1. CONTACT CARDS
		wrapper.add(buildContactRow());
		wrapper.add(Box.createVerticalStrut(Spacings.S6));

		// 3. FAQ SECTION
		wrapper.add(buildFaqSection());
		wrapper.add(Box.createVerticalStrut(Spacings.S6));

		// 4. FOOTER
		wrapper.add(buildFooter());
		wrapper.add(Box.createVerticalGlue());

		JScrollPane scroll = new JScrollPane(wrapper);
		scroll.setBorder(null);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll, BorderLayout.CENTER);
	}

	// ============================================================
	// 1. CONTACT ROW
	// ============================================================
	private JPanel buildContactRow() {
		JPanel row = new JPanel(new GridLayout(1, 3, Spacings.S4, 0));
		row.setOpaque(false);
		row.setAlignmentX(LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

		row.add(contactCard("\u2706", TAG_GREEN_TEXT, "Hotline", "1900-xxxx"));
		row.add(contactCard("\u2709", TAG_BLUE_TEXT, "Email", "support@example.com"));
		row.add(contactCard("\u261D", TAG_PURPLE_TEXT, "Live Chat", "Trực tuyến 24/7"));

		return row;
	}

	private JPanel contactCard(String icon, Color iconColor, String title, String detail) {
		RoundedPanel card = new RoundedPanel(300, 100, 14);
		card.setBackground(Colors.BACKGROUND);
		card.setLayout(new BorderLayout(Spacings.S4, 0));
		card.setBorder(BorderFactory.createEmptyBorder(Spacings.S4, Spacings.S4, Spacings.S4, Spacings.S4));

		// Icon circle
		RoundedPanel iconCircle = new RoundedPanel(50, 50, 25);
		iconCircle.setBackground(new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), 30));
		iconCircle.setLayout(new GridBagLayout());
		JLabel lblIcon = new JLabel(icon);
		lblIcon.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
		lblIcon.setForeground(iconColor);
		iconCircle.add(lblIcon);

		JPanel text = new JPanel();
		text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
		text.setOpaque(false);

		JLabel lblT = new JLabel(title);
		lblT.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblT.setForeground(Colors.FOREGROUND);

		JLabel lblD = new JLabel(detail);
		lblD.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblD.setForeground(Colors.MUTED);

		text.add(Box.createVerticalGlue());
		text.add(lblT);
		text.add(Box.createVerticalStrut(2));
		text.add(lblD);
		text.add(Box.createVerticalGlue());

		card.add(iconCircle, BorderLayout.WEST);
		card.add(text, BorderLayout.CENTER);

		return card;
	}



	// ============================================================
	// 3. FAQ SECTION
	// ============================================================
	private JPanel buildFaqSection() {
		JPanel main = new JPanel();
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		main.setOpaque(false);
		main.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lblT = new JLabel("CÂU HỎI THƯỜNG GẶP");
		lblT.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblT.setForeground(Colors.MUTED);
		main.add(lblT);
		main.add(Box.createVerticalStrut(Spacings.S4));

		String[] questions = {
			"Làm thế nào để thêm sản phẩm mới?",
			"Cách tạo hóa đơn bán hàng?",
			"Làm sao xem báo cáo doanh thu?",
			"Cách quản lý khách hàng?",
			"Làm thế nào để nhập hàng từ nhà cung cấp?"
		};

		String[] tags = {"Sản phẩm", "Hóa đơn", "Báo cáo", "Khách hàng", "Kho hàng"};
		Color[] tagColors = {TAG_BLUE_TEXT, TAG_GREEN_TEXT, TAG_ORANGE_TEXT, TAG_PURPLE_TEXT, TAG_BLUE_TEXT};
		Color[] tagBgs = {TAG_BLUE, TAG_GREEN, TAG_ORANGE, TAG_PURPLE, TAG_BLUE};

		String[] answers = {
			"Vào Danh mục \u2192 Sản phẩm \u2192 Thêm mới. Điền tên, giá, tồn kho rồi nhấn Lưu.",
			"Vào mục Xử lý \u2192 Bán hàng. Chọn sản phẩm, khách hàng và nhấn Thanh toán.",
			"Vào Thống kê \u2192 Doanh thu. Chọn khoảng thời gian để xem biểu đồ và số liệu.",
			"Vào Tra cứu \u2192 Khách hàng để xem, sửa hoặc thêm mới thông tin khách hàng.",
			"Vào Xử lý \u2192 Nhập hàng. Chọn nhà cung cấp và lập phiếu nhập kho."
		};

		for (int i = 0; i < questions.length; i++) {
			main.add(faqItem(questions[i], answers[i], tags[i], tagColors[i], tagBgs[i], false));
			main.add(Box.createVerticalStrut(Spacings.S2));
		}

		return main;
	}

	private JPanel faqItem(String q, String a, String tag, Color tagColor, Color tagBg, boolean expanded) {
		RoundedPanel container = new RoundedPanel(1000, expanded ? 120 : 60, 10);
		container.setBackground(Colors.BACKGROUND);
		container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
		container.setAlignmentX(LEFT_ALIGNMENT);
		container.setBorder(BorderFactory.createEmptyBorder(Spacings.S4, Spacings.S4, Spacings.S4, Spacings.S4));

		// Header: Q + Tag + Arrow
		JPanel header = new JPanel(new BorderLayout(Spacings.S4, 0));
		header.setOpaque(false);
		header.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lblQ = new JLabel(q);
		lblQ.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblQ.setForeground(Colors.FOREGROUND);

		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, Spacings.S3, 0));
		right.setOpaque(false);

		// Tag badge
		RoundedPanel badge = new RoundedPanel(80, 24, 12);
		badge.setBackground(tagBg);
		badge.setLayout(new GridBagLayout());
		JLabel lblTag = new JLabel(tag);
		lblTag.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		lblTag.setForeground(tagColor);
		badge.add(lblTag);

		JLabel lblArrow = new JLabel(expanded ? "\u25B2" : "\u25BC");
		lblArrow.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblArrow.setForeground(Colors.MUTED);

		right.add(badge);
		right.add(lblArrow);

		header.add(lblQ, BorderLayout.WEST);
		header.add(right, BorderLayout.EAST);

		// Answer
		JLabel lblA = new JLabel(a);
		lblA.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lblA.setForeground(Colors.FOREGROUND);
		lblA.setBorder(BorderFactory.createEmptyBorder(Spacings.S4, 0, 0, 0));
		lblA.setVisible(expanded);

		container.add(header);
		container.add(lblA);

		// Click logic
		MouseAdapter toggle = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				boolean show = !lblA.isVisible();
				lblA.setVisible(show);
				lblArrow.setText(show ? "\u25B2" : "\u25BC");
				container.setHeight(show ? 120 : 60);
			}
		};
		container.addMouseListener(toggle);
		container.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		return container;
	}

	// ============================================================
	// 4. FOOTER
	// ============================================================
	private JPanel buildFooter() {
		RoundedPanel foot = new RoundedPanel(1000, 80, 14);
		foot.setBackground(new Color(243, 244, 246)); // Gray-100
		foot.setLayout(new BorderLayout());
		foot.setBorder(BorderFactory.createEmptyBorder(Spacings.S4, Spacings.S6, Spacings.S4, Spacings.S6));
		foot.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
		foot.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lblMsg = new JLabel("Không tìm thấy câu trả lời? Liên hệ đội hỗ trợ ngay.");
		lblMsg.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lblMsg.setForeground(Colors.FOREGROUND);

		RoundedButton btn = new RoundedButton(120, 40, 10, "Chat ngay \u2197", Colors.BACKGROUND);
		btn.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		btn.setForeground(Colors.FOREGROUND);
		btn.setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));

		foot.add(lblMsg, BorderLayout.WEST);
		foot.add(btn, BorderLayout.EAST);

		return foot;
	}

	// ============================================================
	// HELPERS
	// ============================================================
	private JSeparator sep() {
		JSeparator s = new JSeparator(SwingConstants.HORIZONTAL);
		s.setForeground(Colors.BORDER_LIGHT);
		s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		s.setAlignmentX(LEFT_ALIGNMENT);
		return s;
	}
}
