package gui;

import constants.Colors;
import constants.FontStyle;
import entity.LoaiSanPham;
import entity.SanPham;
import exception.RoundedButton;
import exception.RoundedPanel;
import exception.RoundedTextField;
import exception.RoundedToggleButton;
import service.LoaiSanPham_Service;
import service.SanPham_Service;
import service.SanPham_Service.TonKhoInfo;
import service.SanPham_Service.ThongKe;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SanPham_GUI extends JPanel {

	private static final DecimalFormat PRICE_FMT = new DecimalFormat("#,###");

	private final SanPham_Service spService = new SanPham_Service();
	private final LoaiSanPham_Service lspService = new LoaiSanPham_Service();

	private List<SanPham> dsGoc, dsHienThi;
	private List<LoaiSanPham> dsLoaiSP;
	private Map<String, TonKhoInfo> mapTonKho;

	private JLabel lblTongSP, lblConHang, lblSapHet, lblHetHang, lblCount;
	private JPanel pnlCards;
	private RoundedTextField txtSearch;
	private String filterTon = "ALL";
	private String maLoaiLoc = null;

	public SanPham_GUI() {
		dsLoaiSP = safeCall(() -> lspService.layDanhSachLoaiSanPham(), new ArrayList<>());
		setLayout(new BorderLayout());
		setBackground(Colors.BACKGROUND);

		JPanel main = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				Container p = getParent();
				if (p != null && p.getWidth() > 0) {
					return new Dimension(p.getWidth(), super.getPreferredSize().height);
				}
				return super.getPreferredSize();
			}
		};
		main.setLayout(new BoxLayout(main, BoxLayout.Y_AXIS));
		main.setBackground(Colors.BACKGROUND);
		main.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));

		addSection(main, taoHeader());
		main.add(Box.createVerticalStrut(20));
		addSection(main, taoThongKe());
		main.add(Box.createVerticalStrut(20));
		addSection(main, taoTimKiem());
		main.add(Box.createVerticalStrut(16));
		addSection(main, taoGrid());

		JScrollPane scroll = new JScrollPane(main);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.getViewport().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) { main.revalidate(); }
		});
		add(scroll, BorderLayout.CENTER);

		taiDuLieu();
	}

	private void addSection(JPanel parent, JPanel section) {
		section.setAlignmentX(Component.LEFT_ALIGNMENT);
		parent.add(section);
	}

	// ==================== HEADER ====================
	private JPanel taoHeader() {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

		JPanel left = col(
			label("Sản phẩm thuốc", FontStyle.XXL, FontStyle.BOLD, Colors.FOREGROUND),
			Box.createVerticalStrut(4),
			label("Quản lý danh mục thuốc và lô hàng", FontStyle.SM, FontStyle.NORMAL, Colors.MUTED)
		);
		p.add(left, BorderLayout.WEST);

		RoundedButton btn = roundedBtn("+ Thêm sản phẩm", 170, 40, Colors.PRIMARY, Colors.BACKGROUND);
		JPanel rw = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
		rw.setOpaque(false);
		rw.add(btn);
		p.add(rw, BorderLayout.EAST);
		return p;
	}

	// ==================== THỐNG KÊ ====================
	private JPanel taoThongKe() {
		JPanel p = new JPanel(new GridLayout(1, 4, 16, 0));
		p.setOpaque(false);
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
		p.setPreferredSize(new Dimension(0, 90));

		lblTongSP = new JLabel("0");
		lblConHang = new JLabel("0");
		lblSapHet = new JLabel("0");
		lblHetHang = new JLabel("0");

		p.add(statCard("Tổng sản phẩm", lblTongSP, Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
		p.add(statCard("Còn hàng", lblConHang, Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
		p.add(statCard("Sắp hết hàng", lblSapHet, Colors.BROWN_HOVER, Colors.ACCENT, Colors.ACCENT));
		p.add(statCard("Hết hàng", lblHetHang, Colors.SECONDARY, Colors.DANGER, Colors.DANGER));
		return p;
	}

	private JPanel statCard(String title, JLabel val, Color bg, Color valColor, Color titleColor) {
		RoundedPanel c = new RoundedPanel(200, 80, 16);
		c.setBackground(bg);
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
		c.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
		c.add(leftLabel(title, FontStyle.XS, FontStyle.BOLD, titleColor));
		c.add(Box.createVerticalStrut(6));
		val.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
		val.setForeground(valColor);
		val.setAlignmentX(LEFT_ALIGNMENT);
		c.add(val);
		return c;
	}

	// ==================== TÌM KIẾM ====================
	private JPanel taoTimKiem() {
		JPanel w = new JPanel(new BorderLayout(0, 8));
		w.setOpaque(false);
		w.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

		JPanel row = new JPanel(new BorderLayout(10, 0));
		row.setOpaque(false);
		row.setPreferredSize(new Dimension(0, 40));

		txtSearch = new RoundedTextField(400, 38, 12, "Tìm kiếm theo tên hoặc mã sản phẩm...");
		txtSearch.setPreferredSize(new Dimension(400, 38));
		txtSearch.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) { locVaHienThi(); }
		});
		row.add(txtSearch, BorderLayout.CENTER);

		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		right.setOpaque(false);

		// Dropdown loại SP
		JComboBox<String> cbo = new JComboBox<>();
		cbo.addItem("Tất cả");
		for (LoaiSanPham lsp : dsLoaiSP) cbo.addItem(lsp.getTenLoaiSanPham());
		cbo.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		cbo.setPreferredSize(new Dimension(180, 38));
		cbo.addActionListener(e -> {
			int idx = cbo.getSelectedIndex();
			maLoaiLoc = idx <= 0 ? null : dsLoaiSP.get(idx - 1).getMaLoaiSanPham();
			locVaHienThi();
		});
		right.add(cbo);

		// Filter buttons
		String[] labels = {"Tất cả", "Còn hàng", "Sắp hết", "Hết hàng"};
		String[] keys = {"ALL", "CON_HANG", "SAP_HET", "HET_HANG"};
		ButtonGroup grp = new ButtonGroup();
		for (int i = 0; i < labels.length; i++) {
			final String key = keys[i];
			RoundedToggleButton btn = new RoundedToggleButton(80, 34, 10, labels[i], Colors.PRIMARY);
			btn.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
			btn.setPreferredSize(new Dimension(80, 34));
			if (i == 0) btn.setSelected(true);
			grp.add(btn);
			btn.addActionListener(e -> { filterTon = key; locVaHienThi(); });
			right.add(btn);
		}
		row.add(right, BorderLayout.EAST);
		w.add(row, BorderLayout.CENTER);

		lblCount = new JLabel("Hiển thị 0 / 0 sản phẩm");
		lblCount.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lblCount.setForeground(Colors.MUTED);
		w.add(lblCount, BorderLayout.SOUTH);
		return w;
	}

	// ==================== GRID ====================
	private JPanel taoGrid() {
		pnlCards = new JPanel(new GridLayout(0, 4, 14, 14));
		pnlCards.setBackground(Colors.BACKGROUND);
		JPanel wrap = new JPanel(new BorderLayout());
		wrap.setOpaque(false);
		wrap.add(pnlCards, BorderLayout.CENTER);
		return wrap;
	}

	// ==================== CARD ====================
	private JPanel taoCard(SanPham sp, TonKhoInfo info) {
		RoundedPanel card = new RoundedPanel(240, 350, 14);
		card.setBackground(Colors.BACKGROUND);
		card.setLayout(new BorderLayout());

		JPanel ct = new JPanel();
		ct.setLayout(new BoxLayout(ct, BoxLayout.Y_AXIS));
		ct.setOpaque(false);
		ct.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

		// Top: Mã + Badge
		JPanel top = new JPanel(new BorderLayout());
		top.setOpaque(false);
		top.setMaximumSize(dim(Integer.MAX_VALUE, 22));
		top.setAlignmentX(LEFT_ALIGNMENT);
		top.add(leftLabel(sp.getMaSanPham(), FontStyle.XS, FontStyle.NORMAL, Colors.MUTED), BorderLayout.WEST);
		top.add(badge(info), BorderLayout.EAST);
		ct.add(top);
		ct.add(Box.createVerticalStrut(6));

		// Ảnh
		ct.add(taoAnhPanel(sp));
		ct.add(Box.createVerticalStrut(8));

		// Loại
		String loai = sp.getLoaiSanPham() != null && sp.getLoaiSanPham().getTenLoaiSanPham() != null
				? sp.getLoaiSanPham().getTenLoaiSanPham() : "Chưa phân loại";
		ct.add(leftLabel(loai, FontStyle.XS, FontStyle.NORMAL, Colors.SUCCESS));
		ct.add(Box.createVerticalStrut(2));

		// Tên
		String ten = sp.getTenSanPham();
		if (ten.length() > 28) ten = ten.substring(0, 28) + "...";
		ct.add(leftLabel(ten, FontStyle.SM, FontStyle.BOLD, Colors.TEXT_PRIMARY));
		ct.add(Box.createVerticalStrut(3));

		// Công dụng
		String cd = sp.getCongDung() != null ? sp.getCongDung() : "";
		if (cd.length() > 45) cd = cd.substring(0, 45) + "...";
		JLabel lblCd = new JLabel("<html><div style='width:180px'>" + cd + "</div></html>");
		lblCd.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblCd.setForeground(Colors.TEXT_SECONDARY);
		lblCd.setAlignmentX(LEFT_ALIGNMENT);
		ct.add(lblCd);
		ct.add(Box.createVerticalStrut(6));

		// Giá + Tồn
		JPanel priceRow = new JPanel(new BorderLayout());
		priceRow.setOpaque(false);
		priceRow.setAlignmentX(LEFT_ALIGNMENT);
		priceRow.setMaximumSize(dim(Integer.MAX_VALUE, 20));
		priceRow.add(leftLabel(PRICE_FMT.format(sp.getGiaThanh()) + "đ", FontStyle.SM, FontStyle.BOLD, Colors.SUCCESS), BorderLayout.WEST);
		priceRow.add(leftLabel("Tồn: " + info.tonKho, FontStyle.XS, FontStyle.NORMAL, Colors.MUTED), BorderLayout.EAST);
		ct.add(priceRow);
		ct.add(Box.createVerticalStrut(4));

		// Lô
		ct.add(leftLabel(info.soLo + " lô hàng  |  " + info.loHetHan + " lô hết hạn",
				FontStyle.XS, FontStyle.NORMAL, Colors.MUTED));
		ct.add(Box.createVerticalStrut(8));

		// Buttons
		JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		btns.setOpaque(false);
		btns.setAlignmentX(LEFT_ALIGNMENT);
		btns.setMaximumSize(dim(Integer.MAX_VALUE, 34));
		btns.add(roundedBtn("Xem chi tiết", 110, 30, Colors.PRIMARY, Colors.BACKGROUND));
		btns.add(roundedBtn("Sửa", 40, 30, Colors.SECONDARY, Colors.TEXT_SECONDARY));
		btns.add(roundedBtn("Xoá", 40, 30, Colors.SECONDARY, Colors.DANGER));
		ct.add(btns);

		card.add(ct, BorderLayout.CENTER);
		return card;
	}

	private JPanel taoAnhPanel(SanPham sp) {
		JPanel p = new JPanel(new GridBagLayout());
		p.setBackground(Colors.SECONDARY);
		p.setMaximumSize(dim(Integer.MAX_VALUE, 120));
		p.setPreferredSize(dim(100, 120));
		p.setAlignmentX(LEFT_ALIGNMENT);
		p.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1));

		if (sp.getHinhAnh() != null && !sp.getHinhAnh().trim().isEmpty()) {
			try {
				ImageIcon raw = new ImageIcon("data/img/sanPham/" + sp.getHinhAnh());
				if (raw.getIconWidth() > 0) {
					p.setLayout(new BorderLayout());
					JLabel li = new JLabel(new ImageIcon(raw.getImage().getScaledInstance(180, 110, Image.SCALE_SMOOTH)));
					li.setHorizontalAlignment(SwingConstants.CENTER);
					p.add(li, BorderLayout.CENTER);
					return p;
				}
			} catch (Exception ignored) {}
		}
		JLabel na = new JLabel("Chưa có ảnh");
		na.setForeground(Colors.MUTED);
		na.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		p.add(na);
		return p;
	}

	// ==================== DATA ====================
	private void taiDuLieu() {
		dsGoc = safeCall(() -> spService.layDanhSachSanPham(), new ArrayList<>());
		mapTonKho = spService.tinhTonKhoTatCa(dsGoc);
		locVaHienThi();
	}

	private void locVaHienThi() {
		dsHienThi = spService.timKiem(dsGoc, txtSearch.getText().trim());
		if (maLoaiLoc != null) dsHienThi = spService.locTheoLoai(dsHienThi, maLoaiLoc);
		dsHienThi = spService.locTheoTrangThaiTon(dsHienThi, filterTon, mapTonKho);

		// Thống kê trên dsGoc
		ThongKe tk = spService.tinhThongKe(dsGoc, mapTonKho);
		lblTongSP.setText(String.valueOf(tk.tong));
		lblConHang.setText(String.valueOf(tk.conHang));
		lblSapHet.setText(String.valueOf(tk.sapHet));
		lblHetHang.setText(String.valueOf(tk.hetHang));
		lblCount.setText("Hiển thị " + dsHienThi.size() + " / " + tk.tong + " sản phẩm");

		// Render cards
		pnlCards.removeAll();
		for (SanPham sp : dsHienThi) {
			TonKhoInfo info = mapTonKho.getOrDefault(sp.getMaSanPham(), new TonKhoInfo(0, 0, 0));
			pnlCards.add(taoCard(sp, info));
		}
		pnlCards.revalidate();
		pnlCards.repaint();
	}

	// ==================== UI HELPERS ====================
	private JLabel badge(TonKhoInfo info) {
		Color bg, fg;
		if ("CON_HANG".equals(info.trangThai)) { bg = Colors.GREEN_HOVER; fg = Colors.SUCCESS_DARK; }
		else if ("SAP_HET".equals(info.trangThai)) { bg = Colors.YELLOW_HOVER; fg = Colors.ACCENT; }
		else { bg = Colors.BROWN_HOVER; fg = Colors.DANGER; }
		String text = "CON_HANG".equals(info.trangThai) ? "Còn hàng"
				: "SAP_HET".equals(info.trangThai) ? "Sắp hết" : "Hết hàng";
		JLabel b = new JLabel(text);
		b.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		b.setForeground(fg);
		b.setOpaque(true);
		b.setBackground(bg);
		b.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
		return b;
	}

	private JLabel label(String text, int size, int style, Color color) {
		JLabel l = new JLabel(text);
		l.setFont(FontStyle.font(size, style));
		l.setForeground(color);
		return l;
	}

	private JLabel leftLabel(String text, int size, int style, Color color) {
		JLabel l = label(text, size, style, color);
		l.setAlignmentX(LEFT_ALIGNMENT);
		return l;
	}

	private JPanel col(Component... comps) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setOpaque(false);
		for (Component c : comps) p.add(c);
		return p;
	}

	private RoundedButton roundedBtn(String text, int w, int h, Color bg, Color fg) {
		RoundedButton b = new RoundedButton(w, h, 10, text, bg);
		b.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		b.setForeground(fg);
		return b;
	}

	private Dimension dim(int w, int h) { return new Dimension(w, h); }

	private <T> T safeCall(java.util.concurrent.Callable<T> c, T fallback) {
		try { return c.call(); } catch (Exception e) { e.printStackTrace(); return fallback; }
	}
}
