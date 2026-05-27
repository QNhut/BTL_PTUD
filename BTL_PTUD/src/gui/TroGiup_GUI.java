package gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

import constants.ApiConfig;
import constants.Colors;
import constants.FontStyle;
import constants.Spacings;
import exception.RoundedButton;
import exception.RoundedPanel;
import service.GeminiChatService;

@SuppressWarnings("serial")
public class TroGiup_GUI extends JPanel {

	private static final Color TAG_BLUE = new Color(59, 130, 246, 30);
	private static final Color TAG_BLUE_TEXT = new Color(59, 130, 246);
	private static final Color TAG_GREEN = new Color(34, 197, 94, 30);
	private static final Color TAG_GREEN_TEXT = new Color(34, 197, 94);
	private static final Color TAG_ORANGE = new Color(245, 158, 11, 30);
	private static final Color TAG_ORANGE_TEXT = new Color(245, 158, 11);
	private static final Color TAG_PURPLE = new Color(139, 92, 246, 30);
	private static final Color TAG_PURPLE_TEXT = new Color(139, 92, 246);

	// ---- Chat state ----
	private JPanel messagesPanel;
	private JScrollPane msgScroll;
	private JTextField chatInput;
	private RoundedButton sendBtn;
	private RoundedButton toggleBtn;   // giữ tham chiếu để nút × có thể reset lại text
	private JLabel typingLabel;
	private final GeminiChatService geminiService = new GeminiChatService();

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

		JLabel lblSub = new JLabel("Hướng dẫn sử dụng & hỗ trợ nhanh");
		lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lblSub.setForeground(Colors.MUTED);

		// Chat panel – ẩn lúc đầu, bật/tắt bằng toggle button
		JPanel chatPanel = buildChatPanel();
		chatPanel.setVisible(false);

		toggleBtn = new RoundedButton(148, 38, 19, "💬  Trợ lý AI", Colors.PRIMARY);
		toggleBtn.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		toggleBtn.setForeground(Color.WHITE);
		toggleBtn.addActionListener(e -> {
			boolean show = !chatPanel.isVisible();
			chatPanel.setVisible(show);
			toggleBtn.setText(show ? "\u2715  Đóng chat" : "💬  Trợ lý AI");
			revalidate();
			repaint();
		});

		// Header row: tiêu đề bên trái, toggle button bên phải
		JPanel titleRow = new JPanel(new BorderLayout());
		titleRow.setOpaque(false);
		titleRow.setAlignmentX(LEFT_ALIGNMENT);
		titleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 58));

		JPanel titleText = new JPanel();
		titleText.setLayout(new BoxLayout(titleText, BoxLayout.Y_AXIS));
		titleText.setOpaque(false);
		titleText.add(lblTitle);
		titleText.add(Box.createVerticalStrut(Spacings.S1));
		titleText.add(lblSub);

		titleRow.add(titleText, BorderLayout.WEST);
		titleRow.add(toggleBtn, BorderLayout.EAST);

		wrapper.add(titleRow);
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
		add(chatPanel, BorderLayout.EAST);
	}

	// ============================================================
	// 1. CONTACT ROW
	// ============================================================
	private JPanel buildContactRow() {
		JPanel row = new JPanel(new GridLayout(1, 3, Spacings.S4, 0));
		row.setOpaque(false);
		row.setAlignmentX(LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

		row.add(contactCard("✆", TAG_GREEN_TEXT, "Hotline", "1900-xxxx"));
		row.add(contactCard("✉", TAG_BLUE_TEXT, "Email", "support@example.com"));
		row.add(contactCard("☝", TAG_PURPLE_TEXT, "Live Chat", "Trực tuyến 24/7"));

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
			"Vào Danh mục → Sản phẩm → Thêm mới. Điền tên, giá, tồn kho rồi nhấn Lưu.",
			"Vào mục Xử lý → Bán hàng. Chọn sản phẩm, khách hàng và nhấn Thanh toán.",
			"Vào Thống kê → Doanh thu. Chọn khoảng thời gian để xem biểu đồ và số liệu.",
			"Vào Tra cứu → Khách hàng để xem, sửa hoặc thêm mới thông tin khách hàng.",
			"Vào Xử lý → Nhập hàng. Chọn nhà cung cấp và lập phiếu nhập kho."
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

		JLabel lblArrow = new JLabel(expanded ? "▲" : "▼");
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
				lblArrow.setText(show ? "▲" : "▼");
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

		RoundedButton btn = new RoundedButton(120, 40, 10, "Chat ngay ↗", Colors.BACKGROUND);
		btn.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		btn.setForeground(Colors.FOREGROUND);
		btn.setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));

		foot.add(lblMsg, BorderLayout.WEST);
		foot.add(btn, BorderLayout.EAST);

		return foot;
	}

	// ============================================================
	// 5. CHAT PANEL (Gemini AI)
	// ============================================================
	private JPanel buildChatPanel() {
		JPanel chatPanel = new JPanel(new BorderLayout());
		chatPanel.setBackground(Colors.BACKGROUND);
		chatPanel.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Colors.BORDER));
		chatPanel.setPreferredSize(new Dimension(470, 0));
		chatPanel.setMinimumSize(new Dimension(400, 0));

		// ---- Header ----
		JPanel header = new JPanel(new BorderLayout(8, 0));
		header.setBackground(Colors.PRIMARY);
		header.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

		JPanel headerLeft = new JPanel();
		headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
		headerLeft.setOpaque(false);

		JLabel lblTitle = new JLabel("Trợ lý AI");
		lblTitle.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblTitle.setForeground(Color.WHITE);

		String providerLabel = "GROQ".equals(ApiConfig.PROVIDER)
				? "● Groq / " + ApiConfig.GROQ_MODEL
				: "● Gemini / " + ApiConfig.GEMINI_MODEL;
		JLabel lblStatus = new JLabel(providerLabel);
		lblStatus.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblStatus.setForeground(new Color(255, 255, 255, 180));

		headerLeft.add(lblTitle);
		headerLeft.add(Box.createVerticalStrut(2));
		headerLeft.add(lblStatus);

		// Nút xóa lịch sử
		JButton clearBtn = new JButton("↺");
		clearBtn.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
		clearBtn.setForeground(new Color(255, 255, 255, 200));
		clearBtn.setContentAreaFilled(false);
		clearBtn.setBorderPainted(false);
		clearBtn.setFocusPainted(false);
		clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		clearBtn.setToolTipText("Xóa lịch sử hội thoại");

		// Nút × đóng chat
		JButton closeBtn = new JButton("×");
		closeBtn.setFont(new Font("Arial", Font.BOLD, 18));
		closeBtn.setForeground(Color.WHITE);
		closeBtn.setContentAreaFilled(false);
		closeBtn.setBorderPainted(false);
		closeBtn.setFocusPainted(false);
		closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		closeBtn.setToolTipText("Đóng chat");
		closeBtn.addActionListener(e -> {
			chatPanel.setVisible(false);
			if (toggleBtn != null) toggleBtn.setText("💬  Trợ lý AI");
			revalidate();
			repaint();
		});

		// Panel nút bên phải header
		JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 2, 0));
		rightBtns.setOpaque(false);
		rightBtns.add(clearBtn);
		rightBtns.add(closeBtn);

		header.add(headerLeft, BorderLayout.WEST);
		header.add(rightBtns, BorderLayout.EAST);

		// ---- Messages area ----
		messagesPanel = new JPanel();
		messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
		messagesPanel.setBackground(new Color(249, 250, 251));
		messagesPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

		msgScroll = new JScrollPane(messagesPanel);
		msgScroll.setBorder(null);
		msgScroll.setBackground(new Color(249, 250, 251));
		msgScroll.getVerticalScrollBar().setUnitIncrement(16);
		msgScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// ---- Typing indicator ----
		typingLabel = new JLabel("   AI đang trả lời...");
		typingLabel.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		typingLabel.setForeground(Colors.MUTED);
		typingLabel.setVisible(false);
		typingLabel.setBorder(new EmptyBorder(4, 16, 4, 8));

		// ---- Input area ----
		JPanel inputPanel = new JPanel(new BorderLayout(8, 0));
		inputPanel.setBackground(Colors.BACKGROUND);
		inputPanel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER),
				new EmptyBorder(8, 10, 8, 10)));

		chatInput = new JTextField();
		chatInput.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
		chatInput.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Colors.BORDER),
				new EmptyBorder(7, 10, 7, 10)));
		chatInput.setBackground(new Color(249, 250, 251));
		chatInput.putClientProperty("JTextField.placeholderText", "Nhập câu hỏi...");

		sendBtn = new RoundedButton(80, 38, 8, "Gửi", Colors.PRIMARY);
		sendBtn.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
		sendBtn.setForeground(Color.WHITE);

		inputPanel.add(chatInput, BorderLayout.CENTER);
		inputPanel.add(sendBtn, BorderLayout.EAST);

		// ---- API key warning (nếu chưa cấu hình) ----
		if (ApiConfig.GEMINI_API_KEY.equals("YOUR_API_KEY_HERE")) {
			addApiKeyWarning();
		} else {
			addAIMessage("Xin chào! Tôi là trợ lý AI của hệ thống quản lý bán hàng.\n"
					+ "Hãy hỏi tôi bất kỳ điều gì về cách sử dụng phần mềm nhé!");
		}

		// ---- Action listeners ----
		ActionListener sendAction = e -> sendMessage();
		sendBtn.addActionListener(sendAction);
		chatInput.addActionListener(sendAction);

		clearBtn.addActionListener(e -> {
			geminiService.clearHistory();
			messagesPanel.removeAll();
			addAIMessage("Đã xóa lịch sử. Tôi có thể giúp gì cho bạn?");
			messagesPanel.revalidate();
			messagesPanel.repaint();
		});

		// ---- Assemble ----
		JPanel south = new JPanel(new BorderLayout());
		south.setBackground(Colors.BACKGROUND);
		south.add(typingLabel, BorderLayout.NORTH);
		south.add(inputPanel, BorderLayout.CENTER);

		chatPanel.add(header, BorderLayout.NORTH);
		chatPanel.add(msgScroll, BorderLayout.CENTER);
		chatPanel.add(south, BorderLayout.SOUTH);

		return chatPanel;
	}

	/** Hiển thị cảnh báo chưa cấu hình API key. */
	private void addApiKeyWarning() {
		JPanel warn = new JPanel(new BorderLayout(0, 6));
		warn.setBackground(new Color(254, 243, 199));
		warn.setBorder(new EmptyBorder(12, 14, 12, 14));
		warn.setAlignmentX(LEFT_ALIGNMENT);
		warn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

		JLabel icon = new JLabel("⚠ Chưa cấu hình API Key");
		icon.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		icon.setForeground(new Color(146, 64, 14));

		JLabel desc = new JLabel(
				"<html>Mở <b>constants/ApiConfig.java</b><br>"
				+ "và dán Gemini API key vào<br>"
				+ "hằng <b>GEMINI_API_KEY</b>.</html>");
		desc.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		desc.setForeground(new Color(120, 53, 15));

		warn.add(icon, BorderLayout.NORTH);
		warn.add(desc, BorderLayout.CENTER);

		messagesPanel.add(warn);
		messagesPanel.add(Box.createVerticalStrut(8));
	}

	/** Thêm bong bóng tin nhắn của AI (bên trái). */
	private void addAIMessage(String text) {
		addMessageBubble(text, false);
	}

	/** Thêm bong bóng tin nhắn của người dùng (bên phải). */
	private void addUserMessage(String text) {
		addMessageBubble(text, true);
	}

	private void addMessageBubble(String text, boolean isUser) {
		// Escape HTML đặc biệt rồi chuyển \n → <br>
		String safe = text
				.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\n", "<br>");

		// HTML style width=330px → Swing tính đúng chiều cao khi wrap
		JLabel bubble = new JLabel(
				"<html><body style='width:330px;font-family:Arial;font-size:13pt;'>"
				+ safe + "</body></html>");
		bubble.setOpaque(true);

		if (isUser) {
			bubble.setBackground(Colors.PRIMARY);
			bubble.setForeground(Color.WHITE);
			bubble.setBorder(new EmptyBorder(8, 12, 8, 12));
		} else {
			bubble.setBackground(Color.WHITE);
			bubble.setForeground(Colors.FOREGROUND);
			bubble.setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createLineBorder(Colors.BORDER_LIGHT),
					new EmptyBorder(8, 12, 8, 12)));
		}

		// BorderLayout: bubble gắn EAST (user) hoặc WEST (AI) → không overlap
		JPanel row = new JPanel(new BorderLayout());
		row.setOpaque(false);
		row.setAlignmentX(LEFT_ALIGNMENT);
		row.add(bubble, isUser ? BorderLayout.EAST : BorderLayout.WEST);

		// Giới hạn chiều cao row để BoxLayout.Y_AXIS không giãn ra
		int prefH = bubble.getPreferredSize().height;
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(prefH, 36)));

		messagesPanel.add(row);
		messagesPanel.add(Box.createVerticalStrut(6));
		messagesPanel.revalidate();
		messagesPanel.repaint();
		scrollToBottom();
	}

	/** Chuyển lỗi kỹ thuật thành thông báo thân thiện. */
	private static String friendlyError(String msg) {
		if (msg.contains("API_KEY_NOT_SET"))
			return "⚠ Chưa cấu hình API key.\nMở constants/ApiConfig.java và cập nhật key.";
		if (msg.contains("429")) {
			// Trích thời gian retry nếu có: "RETRY_AFTER=28.6s"
			String wait = "";
			if (msg.contains("RETRY_AFTER=")) {
				try {
					String raw = msg.substring(msg.indexOf("RETRY_AFTER=") + 12);
					double sec = Double.parseDouble(raw.replace("s", "").trim());
					wait = " (~" + (int) Math.ceil(sec) + "s)";
				} catch (Exception ignored) {}
			}
			return "⚠ Đã vượt giới hạn API miễn phí.\nVui lòng thử lại sau vài giây" + wait + ".";
		}
		if (msg.contains("401") || msg.contains("403"))
			return "⚠ API key không hợp lệ hoặc không có quyền.\nKiểm tra lại key trong ApiConfig.java.";
		if (msg.contains("400"))
			return "⚠ Yêu cầu không hợp lệ. Thử lại với câu hỏi khác.";
		if (msg.contains("500") || msg.contains("503"))
			return "⚠ Server Google AI tạm thời lỗi. Thử lại sau.";
		if (msg.toLowerCase().contains("timed out") || msg.toLowerCase().contains("timeout"))
			return "⚠ Hết thời gian chờ. Kiểm tra kết nối mạng.";
		if (msg.toLowerCase().contains("network") || msg.toLowerCase().contains("connection refused"))
			return "⚠ Lỗi mạng. Kiểm tra kết nối internet.";
		return "⚠ Lỗi không xác định. Vui lòng thử lại.";
	}

	private void scrollToBottom() {
		SwingUtilities.invokeLater(() -> {
			JScrollBar bar = msgScroll.getVerticalScrollBar();
			bar.setValue(bar.getMaximum());
		});
	}

	/** Gửi tin nhắn đến AI và hiển thị phản hồi. */
	private void sendMessage() {
		String text = chatInput.getText().trim();
		if (text.isEmpty()) return;

		// Kiểm tra API key theo provider hiện tại
		boolean keyMissing = "GROQ".equals(ApiConfig.PROVIDER)
				? ApiConfig.GROQ_API_KEY.equals("YOUR_GROQ_KEY_HERE")
				: ApiConfig.GEMINI_API_KEY.equals("YOUR_API_KEY_HERE");
		if (keyMissing) {
			String provider = "GROQ".equals(ApiConfig.PROVIDER) ? "Groq" : "Gemini";
			JOptionPane.showMessageDialog(this,
					"Bạn chưa nhập " + provider + " API key.\n"
					+ "Mở file constants/ApiConfig.java và cập nhật key.",
					"Thiếu API Key", JOptionPane.WARNING_MESSAGE);
			return;
		}

		chatInput.setText("");
		chatInput.setEnabled(false);
		sendBtn.setEnabled(false);
		addUserMessage(text);

		typingLabel.setVisible(true);
		scrollToBottom();

		// Gọi API trên luồng nền, tránh đóng băng UI
		new SwingWorker<String, Void>() {
			@Override
			protected String doInBackground() throws Exception {
				// Phát hiện ý định → lấy dữ liệu thực từ DB làm ngữ cảnh cho AI
				String dataCtx = service.ChatDataContext.buildContext(text);
				String fullMsg;
				if (dataCtx.isEmpty()) {
					fullMsg = text;
				} else {
					fullMsg = "[DỮ LIỆU HỆ THỐNG - dùng để trả lời, không cần đề cập nguồn gốc]\n"
							+ dataCtx.trim()
							+ "\n[HẾT DỮ LIỆU]\n\nCâu hỏi: " + text;
				}
				return geminiService.chat(fullMsg);
			}

			@Override
			protected void done() {
				typingLabel.setVisible(false);
				chatInput.setEnabled(true);
				sendBtn.setEnabled(true);
				chatInput.requestFocus();

				try {
					String reply = get();
					addAIMessage(reply);
				} catch (Exception ex) {
					Throwable root = ex.getCause() != null ? ex.getCause() : ex;
					String msg = root.getMessage() != null ? root.getMessage() : "";
					addAIMessage(friendlyError(msg));
				}
			}
		}.execute();
	}

}

