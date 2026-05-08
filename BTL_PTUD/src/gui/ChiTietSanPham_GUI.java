package gui;

import constants.Colors;
import constants.FontStyle;
import entity.KeSanPham;
import entity.LoSanPham;
import entity.LoaiSanPham;
import entity.SanPham;
import exception.RoundedButton;
import exception.RoundedComboBox;
import exception.RoundedPanel;
import exception.RoundedTextField;
import service.ImageCache;
import service.ImageUpload_Service;
import service.LoSanPham_Service;
import service.LoaiSanPham_Service;
import service.SanPham_Service;
import service.SanPham_Service.TonKhoInfo;
import service.Validators;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

// Dialog chi tiết / thêm / chỉnh sửa sản phẩm.
// Cách dùng: ChiTietSanPham_GUI.moChiTiet(parentFrame, sanPham, tonKhoInfo);
// ChiTietSanPham_GUI.moThemMoi(parentFrame, () -> refresh.run());
public class ChiTietSanPham_GUI extends JDialog {

	// ── Services ──────────────────────────────────────────────
	private final SanPham_Service spService = new SanPham_Service();
	private final LoSanPham_Service loService = new LoSanPham_Service();
	private final LoaiSanPham_Service lspService = new LoaiSanPham_Service();
	private final ImageUpload_Service imgUpload = ImageUpload_Service.getInstance();
	private final ImageCache imgCache = ImageCache.getInstance();

	// ── Data ──────────────────────────────────────────────────
	private SanPham sanPham;
	private TonKhoInfo tonKhoInfo;
	private List<LoSanPham> dsLo;
	private boolean cheDoXem;
	private Runnable onSaved;

	// ── Tab control ──────────────────────────────────────────
	private JLabel[] tabLabels;
	private JPanel contentHolder;
	private int currentTab = 0;
	private JScrollPane scrLoHang;

	// ── Upload state ──────────────────────────────────────────
	private String tenFileTam = null;
	private String maSinhSan = null;

	// ── Form fields ───────────────────────────────────────────
	private JLabel lblAnhPreview;
	private RoundedTextField txtTen, txtGia;
	private RoundedComboBox<String> cboDonVi;
	private JTextArea txaMoTa, txaThanhPhan, txaCongDung, txaCachDung, txaChongChiDinh;
	private RoundedComboBox<String> cboDanhMuc;
	private JLabel errTen, errGia;
	private List<LoaiSanPham> dsLoaiSP;

	private static final DecimalFormat PRICE_FMT = new DecimalFormat("#,###");
	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("d/M/yyyy");

	// ── Entry points ──────────────────────────────────────────

	public static void moChiTiet(Window parent, SanPham sp, TonKhoInfo info) {
		new ChiTietSanPham_GUI(parent, sp, info, true, null).setVisible(true);
	}

	public static void moChinhSua(Window parent, SanPham sp, TonKhoInfo info, Runnable onSaved) {
		new ChiTietSanPham_GUI(parent, sp, info, false, onSaved).setVisible(true);
	}

	public static void moThemMoi(Window parent, Runnable onSaved) {
		new ChiTietSanPham_GUI(parent, null, null, false, onSaved).setVisible(true);
	}

	// ── Constructor ───────────────────────────────────────────
	private ChiTietSanPham_GUI(Window parent, SanPham sp, TonKhoInfo info, boolean cheDoXem, Runnable onSaved) {
		super(parent, cheDoXem ? "Chi tiết sản phẩm" : (sp == null ? "Thêm sản phẩm thuốc" : "Chỉnh sửa sản phẩm"),
				ModalityType.APPLICATION_MODAL);
		this.sanPham = sp;
		this.tonKhoInfo = info != null ? info : new TonKhoInfo(0, 0, 0);
		this.cheDoXem = cheDoXem;
		this.onSaved = onSaved;
		this.dsLoaiSP = lspService.layDanhSachLoaiSanPham();
		if (sp != null)
			dsLo = loService.layDanhSachLoTheoSP(sp.getMaSanPham());
		// Sinh mã sẵn cho thêm mới
		if (sp == null)
			this.maSinhSan = spService.sinhMaSanPhamMoi();

		setSize(800, 700);
		setMinimumSize(new Dimension(800, 640));
		setLocationRelativeTo(parent);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		getContentPane().setBackground(Colors.BACKGROUND);
		getContentPane().setLayout(new BorderLayout());

		getContentPane().add(taoHeader(), BorderLayout.NORTH);
		getContentPane().add(taoTabPanel(), BorderLayout.CENTER);
		getContentPane().add(taoFooter(), BorderLayout.SOUTH);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				huyFileTam();
			}
		});
	}

	// ══════════════════════════════════════════════════════════
	// HEADER
	// ══════════════════════════════════════════════════════════
	private JPanel taoHeader() {
		JPanel p = new JPanel(new BorderLayout(12, 0));
		p.setBackground(Colors.BACKGROUND);
		p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
				BorderFactory.createEmptyBorder(16, 20, 16, 20)));

		// Icon tròn bên trái
		JLabel iconLbl = new JLabel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Colors.SUCCESS_LIGHT);
				g2.fillOval(0, 0, 55, 55);
				g2.setColor(Colors.SUCCESS);
				g2.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
				String sym = cheDoXem ? "i" : "+";
				FontMetrics fm = g2.getFontMetrics();
				g2.drawString(sym, (55 - fm.stringWidth(sym)) / 2, (55 + fm.getAscent() - fm.getDescent()) / 2);
				g2.dispose();
			}
		};
		iconLbl.setPreferredSize(new Dimension(55, 55));

		if (cheDoXem && sanPham != null) {
			// Xem: dùng thumbnail thay icon
			JLabel imgLbl = new JLabel();
			imgLbl.setPreferredSize(new Dimension(56, 56));
			imgLbl.setHorizontalAlignment(SwingConstants.CENTER);
			imgLbl.setOpaque(true);
			imgLbl.setBackground(Colors.SECONDARY);
			imgLbl.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
			String fn = sanPham.getHinhAnh();
			if (fn != null && !fn.isBlank()) {
				ImageIcon ic = imgCache.getImage(fn, 52, 52, icon -> {
					imgLbl.setIcon(icon);
					imgLbl.setText(null);
				});
				if (ic != null)
					imgLbl.setIcon(ic);
			}
			p.add(imgLbl, BorderLayout.WEST);
		} else {
			p.add(iconLbl, BorderLayout.WEST);
		}

		// Thông tin giữa
		JPanel info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		info.setOpaque(false);

		if (cheDoXem && sanPham != null) {
			info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

			// ── META ─────────────────────────
			String meta = sanPham.getMaSanPham()
					+ (sanPham.getLoaiSanPham() != null ? "  •  " + sanPham.getLoaiSanPham().getTenLoaiSanPham() : "");

			JLabel lblMeta = new JLabel(meta);
			lblMeta.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
			lblMeta.setForeground(Colors.MUTED);
			lblMeta.setAlignmentX(Component.LEFT_ALIGNMENT);
			info.add(lblMeta);

			info.add(Box.createVerticalStrut(4));

			// ── TÊN SẢN PHẨM ─────────────────
			JLabel lblTen = new JLabel(sanPham.getTenSanPham());
			lblTen.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
			lblTen.setForeground(Colors.TEXT_PRIMARY);
			lblTen.setAlignmentX(Component.LEFT_ALIGNMENT);
			info.add(lblTen);

			// ── CÔNG DỤNG ────────────────────
			String cd = sanPham.getCongDung();
			if (cd != null && !cd.isBlank()) {
				if (cd.length() > 60)
					cd = cd.substring(0, 60) + "...";

				JLabel lblCd = new JLabel(cd);
				lblCd.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
				lblCd.setForeground(Colors.TEXT_SECONDARY);
				lblCd.setAlignmentX(Component.LEFT_ALIGNMENT);

				info.add(Box.createVerticalStrut(2));
				info.add(lblCd);
			}

			info.add(Box.createVerticalStrut(6));

			// ── PRICE ROW (BOXLAYOUT X) ──────
			JPanel priceRow = new JPanel();
			priceRow.setLayout(new BoxLayout(priceRow, BoxLayout.X_AXIS));
			priceRow.setOpaque(false);
			priceRow.setAlignmentX(Component.LEFT_ALIGNMENT);

			// Giá (có gạch ngang giá gốc khi có KM)
			String giaHtml;
			if (sanPham.coKhuyenMai()) {
				giaHtml = "<html><span style='color:#ED5A2D;'>"
						+ PRICE_FMT.format(sanPham.getGiaSauKM()) + "đ</span> "
						+ "<span style='color:#9CA3AF; text-decoration:line-through; font-size:smaller;'>"
						+ PRICE_FMT.format(sanPham.getGiaThanh()) + "đ</span></html>";
			} else {
				giaHtml = PRICE_FMT.format(sanPham.getGiaThanh()) + "đ";
			}
			JLabel lblGia = new JLabel(giaHtml);
			lblGia.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
			lblGia.setForeground(Colors.SUCCESS);
			priceRow.add(lblGia);

			// Đơn vị
			if (sanPham.getDonViTinh() != null) {
				priceRow.add(Box.createHorizontalStrut(6));

				JLabel lblDonVi = new JLabel("/ " + sanPham.getDonViTinh());
				lblDonVi.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
				lblDonVi.setForeground(Colors.MUTED);

				priceRow.add(lblDonVi);
			}

			// Badge tồn kho
			priceRow.add(Box.createHorizontalStrut(10));
			priceRow.add(badge("Tồn: " + tonKhoInfo.tonKho, Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK));

			// đẩy sang trái hết
			priceRow.add(Box.createHorizontalGlue());

			info.add(priceRow);
		} else {
			// ── TIÊU ĐỀ ─────────────────────
			String tieuDe = sanPham == null ? "Thêm sản phẩm thuốc" : "Chỉnh sửa sản phẩm";

			JLabel lbl = new JLabel(tieuDe);
			lbl.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
			lbl.setForeground(Colors.TEXT_PRIMARY);
			lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
			info.add(lbl);

			// ── SUB TEXT ────────────────────
			info.add(Box.createVerticalStrut(4));

			JLabel sub = new JLabel("Nhập đầy đủ thông tin sản phẩm");
			sub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
			sub.setForeground(Colors.MUTED);
			sub.setAlignmentX(Component.LEFT_ALIGNMENT);
			info.add(sub);

			// ── MÃ SẢN PHẨM (BADGE) ─────────
			String maHT = sanPham != null ? sanPham.getMaSanPham() : maSinhSan;

			if (maHT != null) {
				info.add(Box.createVerticalStrut(8));

				JPanel rowMa = new JPanel();
				rowMa.setLayout(new BoxLayout(rowMa, BoxLayout.X_AXIS));
				rowMa.setOpaque(false);
				rowMa.setAlignmentX(Component.LEFT_ALIGNMENT);

				JLabel lblMa = new JLabel("Mã: " + maHT);
				lblMa.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
				lblMa.setForeground(Colors.PRIMARY);
				lblMa.setOpaque(true);
				lblMa.setBackground(Colors.SUCCESS_LIGHT);

				// bo đẹp hơn + padding chuẩn
				lblMa.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT),
						BorderFactory.createEmptyBorder(4, 10, 4, 10)));

				rowMa.add(lblMa);
				rowMa.add(Box.createHorizontalGlue());

				info.add(rowMa);
			}
		}
		p.add(info, BorderLayout.CENTER);

		JButton btnClose = new JButton("x");
		btnClose.setFont(new Font("SansSerif", Font.PLAIN, 22));
		btnClose.setForeground(Colors.DANGER);
		btnClose.setFocusPainted(false);
		btnClose.setBorderPainted(false);
		btnClose.setContentAreaFilled(false);
		btnClose.setPreferredSize(new Dimension(55, 55));
		btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnClose.addActionListener(e -> {
			huyFileTam();
			dispose();
		});
		JPanel closeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		closeWrap.setOpaque(false);
		closeWrap.add(btnClose);
		p.add(closeWrap, BorderLayout.EAST);
		return p;
	}

	// ══════════════════════════════════════════════════════════
	// TAB PANEL
	// ══════════════════════════════════════════════════════════
	private JPanel taoTabPanel() {
		JPanel container = new JPanel(new BorderLayout());
		container.setBackground(Colors.BACKGROUND);

		String[] tabNames;
		JPanel[] tabContents = new JPanel[2];

		if (cheDoXem) {
			tabContents[0] = taoTabThongTinXem();
			tabContents[1] = taoTabLoHang();
			int canhBao = dsLo != null ? loService.demLoCanhBao(dsLo) : 0;
			String loLabel = "Lô hàng (" + (dsLo != null ? dsLo.size() : 0) + ")" + (canhBao > 0 ? " !" : "");
			tabNames = new String[] { "Thông tin thuốc", loLabel };
		} else {
			tabContents[0] = taoTabThongTinCoban();
			tabContents[1] = taoTabChiTietThuoc();
			tabNames = new String[] { "Thông tin cơ bản", "Chi tiết thuốc" };
		}

		JLabel[] tl = new JLabel[tabContents.length];
		this.tabLabels = tl;

		JPanel tabBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		tabBar.setBackground(Colors.BACKGROUND);
		tabBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));

		CardLayout cl = new CardLayout();
		JPanel ch = new JPanel(cl);
		this.contentHolder = ch;
		ch.setBackground(Colors.BACKGROUND);

		for (int i = 0; i < tabContents.length; i++) {
			JScrollPane scr = new JScrollPane(tabContents[i]);
			scr.setBorder(null);
			scr.getVerticalScrollBar().setUnitIncrement(12);
			scr.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

			if (cheDoXem && i == 1)
				scrLoHang = scr;

			ch.add(scr, String.valueOf(i));
		}

		for (int i = 0; i < tabNames.length; i++) {
			final int idx = i;
			JLabel lbl = new JLabel(tabNames[i]);
			lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
			lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
			lbl.setBorder(BorderFactory.createEmptyBorder(12, 18, 12, 18));
			tl[i] = lbl;
			tabBar.add(lbl);

			lbl.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					chuyenTab(idx);
				}
			});
		}

		// Default tab 0
		tl[0].setForeground(Colors.PRIMARY);
		tl[0].setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Colors.PRIMARY),
				BorderFactory.createEmptyBorder(12, 18, 10, 18)));

		for (int j = 1; j < tl.length; j++) {
			tl[j].setForeground(Colors.MUTED);
		}

		// Hiển thị tab đầu tiên đúng chuẩn CardLayout
		cl.show(ch, "0");

		container.add(tabBar, BorderLayout.NORTH);
		container.add(ch, BorderLayout.CENTER);
		return container;
	}

	// ══════════════════════════════════════════════════════════
	// TAB XEM – Thong tin thuoc
	// ══════════════════════════════════════════════════════════
	private JPanel taoTabThongTinXem() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBackground(Colors.BACKGROUND);
		p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		if (sanPham == null)
			return p;

		p.add(infoCard("Thành phần", sanPham.getThanhPhan(), Colors.PRIMARY));
		p.add(Box.createVerticalStrut(10));
		p.add(infoCard("Công dụng - Chỉ định", sanPham.getCongDung(), Colors.PRIMARY));
		p.add(Box.createVerticalStrut(10));
		p.add(infoCard("Cách dùng - Liều dùng", sanPham.getNoiSanXuat(), Colors.PRIMARY));
		p.add(Box.createVerticalStrut(10));
		p.add(infoCard("Chống chỉ định", null, Colors.DANGER));
		p.add(Box.createVerticalGlue());
		return p;
	}

	private JPanel infoCard(String title, String content, Color titleColor) {
		RoundedPanel card = new RoundedPanel(450, 90, 40);
		card.setBackground(Colors.BACKGROUND);
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));
		card.setAlignmentX(Component.LEFT_ALIGNMENT);

		JLabel lblTitle = new JLabel(title);
		lblTitle.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblTitle.setForeground(titleColor);
		lblTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
		card.add(lblTitle);
		card.add(Box.createVerticalStrut(8));

		String text = (content != null && !content.isBlank()) ? content : "(Chưa có thông tin)";
		JLabel lblContent = new JLabel("<html><p style='width:380px'>" + text + "</p></html>");
		lblContent.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lblContent.setForeground(Colors.TEXT_SECONDARY);
		lblContent.setAlignmentX(Component.LEFT_ALIGNMENT);
		card.add(lblContent);
		return card;
	}

	// ══════════════════════════════════════════════════════════
	// TAB XEM – Lo hang
	// ══════════════════════════════════════════════════════════
	private JPanel taoTabLoHang() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBackground(Colors.BACKGROUND);
		p.setBorder(BorderFactory.createEmptyBorder(16, 20, 20, 20));

		if (dsLo == null || dsLo.isEmpty()) {
			JLabel none = new JLabel("Chưa có lô hàng nào");
			none.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
			none.setForeground(Colors.MUTED);
			none.setAlignmentX(Component.LEFT_ALIGNMENT);
			p.add(none);
			return p;
		}

		int soHetHan = 0, soSapHet = 0;
		for (LoSanPham lo : dsLo) {
			String tt = LoSanPham_Service.trangThaiLo(lo.getHanSuDung());
			if (LoSanPham_Service.TrangThaiLo.HET_HAN.equals(tt))
				soHetHan++;
			else if (LoSanPham_Service.TrangThaiLo.SAP_HET_HAN.equals(tt))
				soSapHet++;
		}
		if (soHetHan > 0 || soSapHet > 0) {
			p.add(taoBannerCanhBao(soHetHan, soSapHet));
			p.add(Box.createVerticalStrut(12));
		}

		List<KeSanPham> dsKe = loService.layTatCaKe();
		for (LoSanPham lo : dsLo) {
			p.add(taoLoCard(lo, dsKe));
			p.add(Box.createVerticalStrut(8));
		}
		p.add(Box.createVerticalGlue());
		return p;
	}

	private JPanel taoBannerCanhBao(int soHetHan, int soSapHet) {
		JPanel banner = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 8));
		banner.setBackground(Colors.WARNING_BG);
		banner.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Colors.WARNING_BD, 1),
				BorderFactory.createEmptyBorder(4, 10, 4, 10)));
		banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
		banner.setAlignmentX(Component.LEFT_ALIGNMENT);
		StringBuilder sb = new StringBuilder();
		if (soHetHan > 0)
			sb.append(soHetHan).append(" lô đã hết hạn");
		if (soSapHet > 0) {
			if (sb.length() > 0)
				sb.append(", ");
			sb.append(soSapHet).append(" lô sắp hết hạn");
		}
		sb.append(" -- Cần xử lý ngay!");
		JLabel msg = new JLabel("[!] " + sb);
		msg.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		msg.setForeground(Colors.WARNING_FG);
		banner.add(msg);
		return banner;
	}

	private JPanel taoLoCard(LoSanPham lo, List<KeSanPham> dsKe) {
		String tt = LoSanPham_Service.trangThaiLo(lo.getHanSuDung());
		Color bg, border;
		switch (tt) {
		case LoSanPham_Service.TrangThaiLo.HET_HAN:
			bg = Colors.DANGER_LIGHT;
			border = Colors.DANGER_BORDER;
			break;
		case LoSanPham_Service.TrangThaiLo.SAP_HET_HAN:
			bg = Colors.WARNING_BG;
			border = Colors.WARNING_BD;
			break;
		default:
			bg = Colors.BACKGROUND;
			border = Colors.BORDER_LIGHT;
		}

		// Dùng JPanel custom thay RoundedPanel để chiều cao tự co giãn theo nội dung
		final Color cardBg = bg;
		final Color cardBorder = border;
		JPanel card = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(cardBg);
				g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
				g2.setColor(cardBorder);
				g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 12, 12);
				g2.dispose();
			}
		};
		card.setOpaque(false);
		card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
		card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));
		card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		card.setAlignmentX(Component.LEFT_ALIGNMENT);

		// ── Header: Mã lô (nổi bật) + Badge trạng thái ──────────
		JPanel headerRow = new JPanel(new BorderLayout(0, 0));
		headerRow.setOpaque(false);
		headerRow.setAlignmentX(Component.LEFT_ALIGNMENT);
		JLabel lblMaLo = new JLabel(lo.getMaLoSanPham());
		lblMaLo.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
		lblMaLo.setForeground(Colors.TEXT_PRIMARY);
		headerRow.add(lblMaLo, BorderLayout.WEST);
		JPanel badgeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
		badgeWrap.setOpaque(false);
		badgeWrap.add(taoBadgeLo(tt));
		headerRow.add(badgeWrap, BorderLayout.EAST);
		card.add(headerRow);
		card.add(Box.createVerticalStrut(8));

		// ── Đường kẻ ngang ───────────────────────────────────────
		JSeparator sep = new JSeparator(SwingConstants.HORIZONTAL);
		sep.setAlignmentX(Component.LEFT_ALIGNMENT);
		sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
		card.add(sep);
		card.add(Box.createVerticalStrut(8));

		// ── Thông tin: 2 cột, mỗi hàng 2 cặp label–giá trị ──────
		JPanel infoBox = new JPanel();
		infoBox.setLayout(new BoxLayout(infoBox, BoxLayout.Y_AXIS));
		infoBox.setOpaque(false);
		infoBox.setAlignmentX(Component.LEFT_ALIGNMENT);
		infoBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		// Hàng 1: Hạn sử dụng | Số lượng
		String hsdText = lo.getHanSuDung() != null ? lo.getHanSuDung().format(DATE_FMT) : "Không giới hạn";
		Long ncl = LoSanPham_Service.soNgayConLai(lo.getHanSuDung());
		JLabel lblHsd = boldLbl(hsdText);
		if (ncl != null) {
			if (ncl < 0)
				lblHsd.setForeground(Colors.DANGER);
			else if (ncl <= 90)
				lblHsd.setForeground(Colors.ACCENT);
		}
		String dvt = lo.getDonViTinh();
		String slText = lo.getSoLuong() + (dvt != null && !dvt.isBlank() ? " " + dvt : "");
		infoBox.add(makeInfoRow("Hạn sử dụng", lblHsd, "Số lượng", boldLbl(slText)));
		infoBox.add(Box.createVerticalStrut(5));

		// Hàng 2: Ngày nhập | Nhà cung cấp
		String ngayNhap = (lo.getPhieuNhap() != null && lo.getPhieuNhap().getNgayNhap() != null)
				? lo.getPhieuNhap().getNgayNhap().format(DATE_FMT)
				: "--";
		String tenNCC = "--";
		if (lo.getPhieuNhap() != null && lo.getPhieuNhap().getNhaCungCap() != null) {
			String t = lo.getPhieuNhap().getNhaCungCap().getTenNhaCungCap();
			tenNCC = (t != null && !t.isBlank()) ? t : "--";
			if (tenNCC.length() > 22)
				tenNCC = tenNCC.substring(0, 22) + "…";
		}
		infoBox.add(makeInfoRow("Ngày nhập", boldLbl(ngayNhap), "Nhà cung cấp", boldLbl(tenNCC)));
		infoBox.add(Box.createVerticalStrut(5));

		// Hàng 3: Kệ chứa | Nơi sản xuất
		String tenKe = "Chưa xếp";
		if (lo.getKeSanPham() != null) {
			String tk = lo.getKeSanPham().getTenKeSanPham();
			String vt = lo.getKeSanPham().getViTri();
			tenKe = (tk != null ? tk : lo.getKeSanPham().getMaKeSanPham())
					+ (vt != null && !vt.isBlank() ? " (" + vt + ")" : "");
		}
		String nsx = sanPham != null ? sanPham.getNoiSanXuat() : null;
		infoBox.add(makeInfoRow("Kệ chứa", boldLbl(tenKe), "Nơi sản xuất",
				boldLbl(nsx != null && !nsx.isBlank() ? nsx : "--")));
		infoBox.add(Box.createVerticalStrut(5));

		// Hàng 4: Mã phiếu nhập (toàn chiều rộng)
		infoBox.add(makeInfoRow("Mã phiếu nhập",
				boldLbl(lo.getPhieuNhap() != null ? lo.getPhieuNhap().getMaPhieuNhap() : "--"), null, null));

		card.add(infoBox);

		// ── Chuyển kệ ─────────────────────────────────────────────
		if (dsKe != null && !dsKe.isEmpty()) {
			card.add(Box.createVerticalStrut(8));
			JSeparator sep2 = new JSeparator(SwingConstants.HORIZONTAL);
			sep2.setAlignmentX(Component.LEFT_ALIGNMENT);
			sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
			card.add(sep2);
			card.add(Box.createVerticalStrut(6));

			// Inline form (ẩn mặc định) – BoxLayout X_AXIS để tự điều chỉnh kích thước
			JPanel inlineForm = new JPanel();
			inlineForm.setLayout(new BoxLayout(inlineForm, BoxLayout.X_AXIS));
			inlineForm.setOpaque(false);
			inlineForm.setAlignmentX(Component.LEFT_ALIGNMENT);
			inlineForm.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
			inlineForm.setVisible(false);

			JLabel lblKeNew = new JLabel("Kệ mới:");
			lblKeNew.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
			lblKeNew.setForeground(Colors.TEXT_SECONDARY);
			lblKeNew.setMaximumSize(new Dimension(70, 30));
			inlineForm.add(lblKeNew);
			inlineForm.add(Box.createHorizontalStrut(6));

			RoundedComboBox<String> cboKe = new RoundedComboBox<>();
			for (KeSanPham ke : dsKe) {
				String lbl = ke.getTenKeSanPham() != null ? ke.getTenKeSanPham() : ke.getMaKeSanPham();
				if (ke.getViTri() != null && !ke.getViTri().isBlank())
					lbl += " (" + ke.getViTri() + ")";
				cboKe.addItem(lbl);
			}
			if (lo.getKeSanPham() != null && lo.getKeSanPham().getMaKeSanPham() != null) {
				String maCur = lo.getKeSanPham().getMaKeSanPham();
				for (int i = 0; i < dsKe.size(); i++) {
					if (maCur.equals(dsKe.get(i).getMaKeSanPham())) {
						cboKe.setSelectedIndex(i);
						break;
					}
				}
			}
			cboKe.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
			cboKe.setPreferredSize(new Dimension(300, 30));
			cboKe.setMaximumSize(new Dimension(350, 30));
			inlineForm.add(cboKe);
			inlineForm.add(Box.createHorizontalStrut(8));

			RoundedButton btnLuuKe = roundBtn("Lưu", 80, 30, Colors.PRIMARY, Colors.BACKGROUND);
			btnLuuKe.setMaximumSize(new Dimension(80, 30));
			inlineForm.add(btnLuuKe);
			inlineForm.add(Box.createHorizontalStrut(6));
			RoundedButton btnHuyKe = roundBtn("Hủy", 80, 30, Colors.SECONDARY, Colors.TEXT_PRIMARY);
			btnHuyKe.setMaximumSize(new Dimension(80, 30));
			inlineForm.add(btnHuyKe);
			inlineForm.add(Box.createHorizontalGlue());

			// Nút toggle – BoxLayout X_AXIS
			JPanel btnToggleRow = new JPanel();
			btnToggleRow.setLayout(new BoxLayout(btnToggleRow, BoxLayout.X_AXIS));
			btnToggleRow.setOpaque(false);
			btnToggleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
			btnToggleRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
			RoundedButton btnToggle = roundBtn("▼ Chuyển kệ", 200, 28, Colors.SECONDARY, Colors.PRIMARY);
			btnToggle.setMaximumSize(new Dimension(200, 28));
			btnToggleRow.add(btnToggle);
			btnToggleRow.add(Box.createHorizontalGlue());
			card.add(btnToggleRow);
			card.add(inlineForm);

			btnToggle.addActionListener(e -> {
				boolean showing = inlineForm.isVisible();
				inlineForm.setVisible(!showing);
				btnToggle.setText(showing ? "▼ Chuyển kệ" : "▲ Thu lại");
				card.revalidate();
				card.repaint();
			});
			btnHuyKe.addActionListener(e -> {
				inlineForm.setVisible(false);
				btnToggle.setText("▼ Chuyển kệ");
				card.revalidate();
				card.repaint();
			});
			btnLuuKe.addActionListener(e -> {
				int idx = cboKe.getSelectedIndex();
				if (idx < 0 || idx >= dsKe.size())
					return;
				KeSanPham keChon = dsKe.get(idx);
				boolean ok = loService.chuyenKe(lo.getMaLoSanPham(), keChon.getMaKeSanPham());
				if (ok) {
					JOptionPane.showMessageDialog(ChiTietSanPham_GUI.this,
							"Đã chuyển lô " + lo.getMaLoSanPham() + " sang " + keChon.getTenKeSanPham(), "Thành công",
							JOptionPane.INFORMATION_MESSAGE);
					taiLaiTabLoHang();
				} else {
					JOptionPane.showMessageDialog(ChiTietSanPham_GUI.this, "Chuyển kệ thất bại, vui lòng thử lại",
							"Lỗi", JOptionPane.ERROR_MESSAGE);
				}
			});
		}

		return card;
	}

	private void taiLaiTabLoHang() {
		dsLo = loService.layDanhSachLoTheoSP(sanPham.getMaSanPham());
		if (scrLoHang != null) {
			scrLoHang.setViewportView(taoTabLoHang());
			scrLoHang.revalidate();
			scrLoHang.repaint();
		}
		if (tabLabels != null && tabLabels.length > 1) {
			List<LoSanPham> ds = dsLo != null ? dsLo : new java.util.ArrayList<>();
			int canhBao = loService.demLoCanhBao(ds);
			String loLabel = "Lô hàng (" + ds.size() + ")" + (canhBao > 0 ? " !" : "");
			tabLabels[1].setText(loLabel);
		}
	}

	private JLabel taoBadgeLo(String tt) {
		String text;
		Color bg, fg;
		switch (tt) {
		case LoSanPham_Service.TrangThaiLo.HET_HAN:
			text = "Hết hạn";
			bg = Colors.DANGER;
			fg = Color.WHITE;
			break;
		case LoSanPham_Service.TrangThaiLo.SAP_HET_HAN:
			text = "Sắp hết hạn";
			bg = new Color(0xFB923C);
			fg = Color.WHITE;
			break;
		default:
			text = "Còn hạn";
			bg = Colors.SUCCESS;
			fg = Color.WHITE;
		}
		JLabel b = new JLabel(text);
		b.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		b.setForeground(fg);
		b.setOpaque(true);
		b.setBackground(bg);
		b.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
		return b;
	}

	// TAB THEM/SUA – Thong tin co ban
	private JPanel taoTabThongTinCoban() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBackground(Colors.BACKGROUND);
		p.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

		Font f = FontStyle.font(FontStyle.SM, FontStyle.NORMAL);

		// ================= TOP: ẢNH + INFO =================
		JPanel topRow = new JPanel();
		topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
		topRow.setOpaque(false);
		topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

		// ── ẢNH ──
		lblAnhPreview = new JLabel("Tải ảnh", SwingConstants.CENTER);
		lblAnhPreview.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblAnhPreview.setForeground(Colors.MUTED);
		lblAnhPreview.setOpaque(true);
		lblAnhPreview.setBackground(Colors.SECONDARY);
		lblAnhPreview.setPreferredSize(new Dimension(150, 200));
		lblAnhPreview.setMaximumSize(new Dimension(150, 200));
		lblAnhPreview.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
		lblAnhPreview.setCursor(new Cursor(Cursor.HAND_CURSOR));

		lblAnhPreview.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				chonAnh();
			}
		});

		// Load existing image in edit mode
		if (sanPham != null) {
			String fn = sanPham.getHinhAnh();
			if (fn != null && !fn.isBlank()) {
				ImageIcon ic = imgCache.getImage(fn, 146, 196, icon -> {
					lblAnhPreview.setIcon(icon);
					lblAnhPreview.setText(null);
				});
				if (ic != null) {
					lblAnhPreview.setIcon(ic);
					lblAnhPreview.setText(null);
				}
			}
		}

		topRow.add(lblAnhPreview);
		topRow.add(Box.createRigidArea(new Dimension(20, 0)));

		// ================= INFO (TẤT CẢ NẰM ĐÂY) =================
		JPanel info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		info.setOpaque(false);
		info.setAlignmentX(Component.LEFT_ALIGNMENT);

		// ── TÊN ──
		info.add(fieldLabel("Tên sản phẩm *"));
		info.add(Box.createRigidArea(new Dimension(0, 4)));

		txtTen = field("VD: Paracetamol 500mg", sanPham != null ? sanPham.getTenSanPham() : null);
		txtTen.setFont(f);
		txtTen.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		txtTen.setAlignmentX(Component.LEFT_ALIGNMENT);

		info.add(txtTen);
		errTen = errLabelSP();
		info.add(errTen);
		info.add(Box.createRigidArea(new Dimension(0, 12)));
		info.add(fieldLabel("Danh mục"));
		info.add(Box.createRigidArea(new Dimension(0, 4)));

		cboDanhMuc = new RoundedComboBox<>();
		String curLoai = sanPham != null && sanPham.getLoaiSanPham() != null
				? sanPham.getLoaiSanPham().getTenLoaiSanPham()
				: null;

		int selIdx = 0;
		for (int i = 0; i < dsLoaiSP.size(); i++) {
			cboDanhMuc.addItem(dsLoaiSP.get(i).getTenLoaiSanPham());
			if (dsLoaiSP.get(i).getTenLoaiSanPham().equals(curLoai))
				selIdx = i;
		}
		cboDanhMuc.setSelectedIndex(selIdx);
		cboDanhMuc.setFont(f);
		cboDanhMuc.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		cboDanhMuc.setAlignmentX(Component.LEFT_ALIGNMENT);

		info.add(cboDanhMuc);
		info.add(Box.createRigidArea(new Dimension(0, 12)));

		// ── ĐƠN VỊ ──
		info.add(fieldLabel("Đơn vị tính"));
		info.add(Box.createRigidArea(new Dimension(0, 4)));

		cboDonVi = new RoundedComboBox<>();
		for (String dv : new String[] { "Viên", "Vỉ", "Hộp", "Hũ", "Túi", "Gói", "Bịch", "Chai", "Lọ", "Ống", "Tuýp",
				"Miếng", "ml", "g" }) {
			cboDonVi.addItem(dv);
		}

		if (sanPham != null && sanPham.getDonViTinh() != null) {
			String dv = sanPham.getDonViTinh().trim();
			for (int i = 0; i < cboDonVi.getItemCount(); i++) {
				if (cboDonVi.getItemAt(i).equalsIgnoreCase(dv)) {
					cboDonVi.setSelectedIndex(i);
					break;
				}
			}
		}

		cboDonVi.setFont(f);
		cboDonVi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		cboDonVi.setAlignmentX(Component.LEFT_ALIGNMENT);

		info.add(cboDonVi);
		info.add(Box.createRigidArea(new Dimension(0, 12)));

		// ── GIÁ ──
		info.add(fieldLabel("Giá bán (đ) *"));
		info.add(Box.createRigidArea(new Dimension(0, 4)));

		JPanel giaRow = new JPanel();
		giaRow.setLayout(new BoxLayout(giaRow, BoxLayout.X_AXIS));
		giaRow.setOpaque(false);
		giaRow.setAlignmentX(Component.LEFT_ALIGNMENT);

		txtGia = field("VD: 25000", sanPham != null ? String.valueOf((long) sanPham.getGiaThanh()) : null);
		txtGia.setFont(f);
		txtGia.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

		JLabel lblD = new JLabel("đ");
		lblD.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblD.setForeground(Colors.MUTED);

		giaRow.add(txtGia);
		giaRow.add(Box.createRigidArea(new Dimension(8, 0)));
		giaRow.add(lblD);

		info.add(giaRow);
		errGia = errLabelSP();
		info.add(errGia);

		// add info vào top
		topRow.add(info);

		p.add(topRow);
		p.add(Box.createRigidArea(new Dimension(0, 16)));

		// ================= MÔ TẢ =================
		p.add(fieldLabel("Mô tả ngắn *"));
		p.add(Box.createRigidArea(new Dimension(0, 6)));

		txaMoTa = new JTextArea(4, 0);
		configTextArea(txaMoTa, "Mô tả công dụng chính của thuốc...", sanPham != null ? sanPham.getCongDung() : null);

		txaMoTa.setFont(f);

		JScrollPane scr = new JScrollPane(txaMoTa);
		scr.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
		scr.setAlignmentX(Component.LEFT_ALIGNMENT);
		scr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));

		p.add(scr);

		p.add(Box.createVerticalGlue());

		return p;
	}

	// ══════════════════════════════════════════════════════════
	// TAB THEM/SUA – Chi tiet thuoc
	// ══════════════════════════════════════════════════════════
	private JPanel taoTabChiTietThuoc() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBackground(Colors.BACKGROUND);
		p.setBorder(BorderFactory.createEmptyBorder(16, 24, 20, 24));

		String[] titles = { "Thành phần hoạt chất", "Công dụng - Chỉ định", "Cách dùng - Liều dùng", "Chống chỉ định" };
		String[] ph = { "VD: Paracetamol 500mg, tá dược...", "Mô tả các chỉ định điều trị...",
				"VD: Người lớn 1-2 viên/lần, 3 lần/ngày...", "VD: Mẫn cảm, suy gan nặng..." };
		String[] vals = { sanPham != null ? sanPham.getThanhPhan() : null,
				sanPham != null ? sanPham.getCongDung() : null, sanPham != null ? sanPham.getNoiSanXuat() : null,
				null };
		txaThanhPhan = new JTextArea(3, 0);
		txaCongDung = new JTextArea(3, 0);
		txaCachDung = new JTextArea(3, 0);
		txaChongChiDinh = new JTextArea(3, 0);
		JTextArea[] fields = { txaThanhPhan, txaCongDung, txaCachDung, txaChongChiDinh };

		for (int i = 0; i < titles.length; i++) {
			p.add(fieldLabel(titles[i]));
			configTextArea(fields[i], ph[i], vals[i]);
			JScrollPane scr = new JScrollPane(fields[i]);
			scr.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
			scr.setAlignmentX(Component.LEFT_ALIGNMENT);
			scr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
			p.add(scr);
			p.add(Box.createVerticalStrut(14));
		}
		p.add(Box.createVerticalGlue());
		return p;
	}

	// TAB SWITCHING
	private void chuyenTab(int idx) {
		currentTab = idx;
		for (int j = 0; j < tabLabels.length; j++) {
			boolean sel = (j == idx);
			tabLabels[j].setForeground(sel ? Colors.PRIMARY : Colors.MUTED);
			tabLabels[j].setBorder(BorderFactory.createCompoundBorder(
					BorderFactory.createMatteBorder(0, 0, sel ? 2 : 0, 0, Colors.PRIMARY),
					BorderFactory.createEmptyBorder(12, 18, sel ? 10 : 12, 18)));
		}
		((CardLayout) contentHolder.getLayout()).show(contentHolder, String.valueOf(idx));
		contentHolder.revalidate();
		contentHolder.repaint();
		// Cập nhật footer
		capNhatFooter();
	}

	// ══════════════════════════════════════════════════════════
	// FOOTER
	// ══════════════════════════════════════════════════════════
	private JPanel pnlFooter;

	private JPanel taoFooter() {
		pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
		pnlFooter.setBackground(Colors.BACKGROUND);
		pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT));
		capNhatFooter();
		return pnlFooter;
	}

	private void capNhatFooter() {
		if (pnlFooter == null)
			return;
		pnlFooter.removeAll();

		if (cheDoXem) {
			RoundedButton btnDong = roundBtn("Đóng", 90, 38, Colors.SECONDARY, Colors.TEXT_PRIMARY);
			btnDong.addActionListener(e -> dispose());
			pnlFooter.add(btnDong);
			RoundedButton btnSua = roundBtn("Chỉnh sửa", 120, 38, Colors.PRIMARY, Colors.BACKGROUND);
			btnSua.addActionListener(e -> {
				dispose();
				moChinhSua(getOwner(), sanPham, tonKhoInfo, onSaved);
			});
			pnlFooter.add(btnSua);
		} else {
			// Nút Hủy
			RoundedButton btnHuy = roundBtn("Hủy", 80, 38, Colors.SECONDARY, Colors.TEXT_PRIMARY);
			btnHuy.addActionListener(e -> {
				huyFileTam();
				dispose();
			});
			pnlFooter.add(btnHuy);

			if (currentTab == 0) {
				// Tab 1: Tiếp theo
				RoundedButton btnNext = roundBtn("Tiếp theo", 120, 38, Colors.PRIMARY, Colors.BACKGROUND);
				btnNext.addActionListener(e -> {
					// Validate tab 1 trước khi chuyển
					String ten = txtTen != null ? txtTen.getText().trim() : "";
					String errMsgTen = Validators.required(ten);
					if (errMsgTen != null) {
						if (errTen != null) { errTen.setText(errMsgTen); errTen.setVisible(true); }
						if (txtTen != null) { txtTen.setInvalid(true); txtTen.requestFocusInWindow(); txtTen.selectAll(); }
						return;
					}
					if (errTen != null) errTen.setVisible(false);
					if (txtTen != null) txtTen.setInvalid(false);

					String giaStr = txtGia != null ? txtGia.getText().trim().replace(",", "") : "";
					String errMsgGia = Validators.soThucDuong(giaStr);
					if (errMsgGia != null) {
						if (errGia != null) { errGia.setText(errMsgGia); errGia.setVisible(true); }
						if (txtGia != null) { txtGia.setInvalid(true); txtGia.requestFocusInWindow(); txtGia.selectAll(); }
						return;
					}
					if (errGia != null) errGia.setVisible(false);
					if (txtGia != null) txtGia.setInvalid(false);

					chuyenTab(1);
				});
				pnlFooter.add(btnNext);
			} else {
				// Tab 2: Lưu / Thêm mới
				RoundedButton btnLuu = roundBtn(sanPham == null ? "Thêm mới" : "Lưu", 120, 38, Colors.PRIMARY,
						Colors.BACKGROUND);
				btnLuu.addActionListener(e -> luuSanPham());
				pnlFooter.add(btnLuu);
			}
		}
		pnlFooter.revalidate();
		pnlFooter.repaint();
	}

	// ══════════════════════════════════════════════════════════
	// ACTIONS
	// ══════════════════════════════════════════════════════════
	private void chonAnh() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileFilter(new FileNameExtensionFilter("Ảnh (PNG, JPG, JPEG, GIF)", "png", "jpg", "jpeg", "gif"));
		chooser.setDialogTitle("Chọn ảnh sản phẩm");
		if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION)
			return;
		try {
			huyFileTam();
			tenFileTam = imgUpload.chuanBiUpload(chooser.getSelectedFile());
			ImageIcon preview = imgUpload.taiPreview(tenFileTam, 84, 84);
			if (preview != null) {
				lblAnhPreview.setIcon(preview);
				lblAnhPreview.setText(null);
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, "Không đọc được ảnh: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void luuSanPham() {
		String ten = txtTen != null ? txtTen.getText().trim() : "";
		String errMsgTen = Validators.required(ten);
		if (errMsgTen != null) {
			if (errTen != null) { errTen.setText(errMsgTen); errTen.setVisible(true); }
			if (txtTen != null) { txtTen.setInvalid(true); txtTen.requestFocusInWindow(); txtTen.selectAll(); }
			return;
		}
		if (errTen != null) errTen.setVisible(false);
		if (txtTen != null) txtTen.setInvalid(false);

		String giaStr = txtGia != null ? txtGia.getText().trim().replace(",", "") : "";
		String errMsgGia = Validators.soThucDuong(giaStr);
		if (errMsgGia != null) {
			if (errGia != null) { errGia.setText(errMsgGia); errGia.setVisible(true); }
			if (txtGia != null) { txtGia.setInvalid(true); txtGia.requestFocusInWindow(); txtGia.selectAll(); }
			return;
		}
		double gia = Double.parseDouble(giaStr);
		if (errGia != null) errGia.setVisible(false);
		if (txtGia != null) txtGia.setInvalid(false);

		// Xác nhận lưu ảnh tạm
		String tenAnh = sanPham != null ? sanPham.getHinhAnh() : null;
		if (tenFileTam != null) {
			try {
				String maSP = sanPham != null ? sanPham.getMaSanPham()
						: (maSinhSan != null ? maSinhSan : "SP_" + System.currentTimeMillis());
				String ext = tenFileTam.contains(".") ? tenFileTam.substring(tenFileTam.lastIndexOf('.')) : ".png";
				tenAnh = maSP.toLowerCase().replace(" ", "_") + ext;
				imgUpload.xacNhanLuu(tenFileTam, tenAnh);
				tenFileTam = null;
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(this, "Lỗi lưu ảnh: " + ex.getMessage(), "Lỗi",
						JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		SanPham sp = sanPham != null ? sanPham : new SanPham();
		sp.setTenSanPham(ten);
		sp.setGiaThanh(gia);
		if (cboDonVi != null)
			sp.setDonViTinh((String) cboDonVi.getSelectedItem());
		sp.setHinhAnh(tenAnh);
		if (txaThanhPhan != null && !txaThanhPhan.getForeground().equals(Colors.MUTED))
			sp.setThanhPhan(txaThanhPhan.getText().trim());
		if (txaCongDung != null && !txaCongDung.getForeground().equals(Colors.MUTED))
			sp.setCongDung(txaCongDung.getText().trim());
		else if (txaMoTa != null && !txaMoTa.getForeground().equals(Colors.MUTED))
			sp.setCongDung(txaMoTa.getText().trim()); // fallback nếu chưa qua tab 2
		if (txaCachDung != null && !txaCachDung.getForeground().equals(Colors.MUTED))
			sp.setNoiSanXuat(txaCachDung.getText().trim());
		if (cboDanhMuc != null && !dsLoaiSP.isEmpty()) {
			int idx = cboDanhMuc.getSelectedIndex();
			if (idx >= 0 && idx < dsLoaiSP.size())
				sp.setLoaiSanPham(dsLoaiSP.get(idx));
		}

		boolean ok;
		if (sanPham == null) {
			String maMoi = maSinhSan != null ? maSinhSan : spService.sinhMaSanPhamMoi();
			sp.setMaSanPham(maMoi);
			ok = spService.themSanPham(sp);
		} else {
			ok = spService.updateSanPham(sp);
		}

		if (ok) {
			if (onSaved != null)
				onSaved.run();
			dispose();
		} else {
			JOptionPane.showMessageDialog(this, "Lưu không thành công, vui lòng thử lại", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	private void huyFileTam() {
		if (tenFileTam != null) {
			imgUpload.huyUpload(tenFileTam);
			tenFileTam = null;
		}
	}

	// ══════════════════════════════════════════════════════════
	// UI HELPERS
	// ══════════════════════════════════════════════════════════
	private JPanel col() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setOpaque(false);
		return p;
	}

	private JLabel errLabelSP() {
		JLabel l = new JLabel();
		l.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		l.setForeground(Colors.DANGER);
		l.setAlignmentX(Component.LEFT_ALIGNMENT);
		l.setVisible(false);
		return l;
	}

	private JLabel smLbl(String text) {
		JLabel l = new JLabel(text);
		l.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		l.setForeground(Colors.MUTED);
		return l;
	}

	private JLabel boldLbl(String text) {
		JLabel l = new JLabel(text);
		l.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		l.setForeground(Colors.TEXT_PRIMARY);
		return l;
	}

	// Tạo panel BoxLayout X_AXIS: [label cố định 100px] [strut 4] [value fill]
	private JPanel makeInfoPair(String label, JLabel value) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.setOpaque(false);
		p.setAlignmentX(Component.LEFT_ALIGNMENT);
		JLabel lbl = smLbl(label);
		lbl.setPreferredSize(new Dimension(100, 18));
		lbl.setMinimumSize(new Dimension(100, 18));
		lbl.setMaximumSize(new Dimension(100, 18));
		value.setMinimumSize(new Dimension(0, 20));
		value.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		p.add(lbl);
		p.add(Box.createHorizontalStrut(4));
		p.add(value);
		return p;
	}

	// Hàng 2 cặp: GridLayout(1,2,16,0) mỗi ô là makeInfoPair; nếu lbl2==null thì 1
	// cặp full width
	private JPanel makeInfoRow(String lbl1, JLabel val1, String lbl2, JLabel val2) {
		if (lbl2 == null)
			return makeInfoPair(lbl1, val1);
		JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
		row.setOpaque(false);
		row.setAlignmentX(Component.LEFT_ALIGNMENT);
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		row.add(makeInfoPair(lbl1, val1));
		row.add(makeInfoPair(lbl2, val2));
		return row;
	}

	private JLabel fieldLabel(String text) {
		JLabel l = new JLabel(text);
		l.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		l.setForeground(Colors.TEXT_PRIMARY);
		l.setAlignmentX(Component.LEFT_ALIGNMENT);
		l.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));
		return l;
	}

	private RoundedTextField field(String placeholder, String value) {
		RoundedTextField f = new RoundedTextField(300, 38, 10, placeholder);
		f.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		if (value != null && !value.isBlank())
			f.setText(value);
		return f;
	}

	private void configTextArea(JTextArea ta, String placeholder, String value) {
		ta.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		ta.setForeground(Colors.TEXT_PRIMARY);
		ta.setBackground(Colors.BACKGROUND);
		ta.setLineWrap(true);
		ta.setWrapStyleWord(true);
		ta.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
		if (value != null && !value.isBlank()) {
			ta.setText(value);
		} else {
			ta.setText(placeholder);
			ta.setForeground(Colors.MUTED);
			ta.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent e) {
					if (ta.getForeground().equals(Colors.MUTED)) {
						ta.setText("");
						ta.setForeground(Colors.TEXT_PRIMARY);
					}
				}

				@Override
				public void focusLost(FocusEvent e) {
					if (ta.getText().isBlank()) {
						ta.setText(placeholder);
						ta.setForeground(Colors.MUTED);
					}
				}
			});
		}
	}

	private JLabel badge(String text, Color bg, Color fg) {
		JLabel b = new JLabel(text);
		b.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		b.setForeground(fg);
		b.setOpaque(true);
		b.setBackground(bg);
		b.setBorder(BorderFactory.createEmptyBorder(3, 10, 3, 10));
		return b;
	}

	private RoundedButton roundBtn(String text, int w, int h, Color bg, Color fg) {
		RoundedButton b = new RoundedButton(w, h, 10, text, bg);
		b.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		b.setForeground(fg);
		return b;
	}
}
