package gui;

import constants.Colors;
import constants.FontStyle;
import entity.LoSanPham;
import entity.LoaiSanPham;
import entity.SanPham;
import exception.ProductActionEditor;
import exception.ProductTableRenderer;
import exception.RoundedButton;
import exception.RoundedPanel;
import exception.RoundedTextField;
import exception.RoundedToggleButton;
import service.ImageCache;
import service.LoaiSanPham_Service;
import service.LoSanPham_Service;
import service.SanPham_Service;
import service.SanPham_Service.TonKhoInfo;
import service.SanPham_Service.ThongKe;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SanPham_GUI extends JPanel {

	private static final DecimalFormat PRICE_FMT = new DecimalFormat("#,###");

	private final SanPham_Service spService = new SanPham_Service();
	private final LoSanPham_Service loSPService = new LoSanPham_Service();
	private final LoaiSanPham_Service lspService = new LoaiSanPham_Service();

	private List<SanPham> dsGoc, dsHienThi;
	private List<LoaiSanPham> dsLoaiSP;
	private Map<String, TonKhoInfo> mapTonKho;

	// UI chung
	private JLabel lblTongSP, lblConHang, lblSapHet, lblHetHang, lblCount;
	private RoundedTextField txtSearch;
	private String filterTon = "ALL";
	private String maLoaiLoc = null;

	// Chế độ hiển thị: true = Card Grid, false = Table List
	private boolean cheDoCard = true;
	private JButton btnToggleView;
	private RoundedToggleButton btnFilterSapHet, btnFilterHetHang;

	// Card Grid
	private JPanel pnlCards;

	// Table List
	private JTable tblSanPham;
	private DefaultTableModel tableModel;
	private JScrollPane scrTable;

	// Container giữ view (chuyển đổi giữa card / table)
	private JPanel pnlViewContainer;

	// Pagination (card mode)
	private static final int PAGE_SIZE = 20;
	private int currentPage = 0;
	private JPanel pnlPagination;

	// Cache lot data (tránh query DB 200 lần)
	private List<LoSanPham> dsLoSanPhamCache;
	private Map<String, String> mapNgayGanNhat;
	private final ImageCache imgCache = ImageCache.getInstance();

	private static final String[] TABLE_COLUMNS = { "Sản phẩm", "Danh mục", "Giá bán", "Tồn kho", "Thông tin lô hàng",
			"Trạng thái", "Thao tác" };

	public SanPham_GUI() {
		dsLoaiSP = safeCall(() -> lspService.layDanhSachLoaiSanPham(), new ArrayList<>());
		setLayout(new BorderLayout());
		setBackground(Colors.BACKGROUND);

		JPanel main = new JPanel() {
			@Override
			public Dimension getPreferredSize() {
				Container p = getParent();
				if (p != null && p.getWidth() > 0)
					return new Dimension(p.getWidth(), super.getPreferredSize().height);
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

		// View container — chứa card hoặc table
		pnlViewContainer = new JPanel(new BorderLayout());
		pnlViewContainer.setOpaque(false);
		pnlViewContainer.setAlignmentX(Component.LEFT_ALIGNMENT);
		pnlViewContainer.add(taoGridView(), BorderLayout.CENTER);
		main.add(pnlViewContainer);

		JScrollPane scroll = new JScrollPane(main);
		scroll.setBorder(null);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(16);
		scroll.getViewport().addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				main.revalidate();
			}
		});
		add(scroll, BorderLayout.CENTER);

		taiDuLieu();
	}

	private void addSection(JPanel parent, JPanel section) {
		section.setAlignmentX(Component.LEFT_ALIGNMENT);
		parent.add(section);
	}

	//  TOGGLE VIEW
	private void toggleView() {
		cheDoCard = !cheDoCard;
		capNhatNutToggle();

		pnlViewContainer.removeAll();
		if (cheDoCard) {
			pnlViewContainer.add(taoGridView(), BorderLayout.CENTER);
		} else {
			pnlViewContainer.add(taoTableView(), BorderLayout.CENTER);
		}
		locVaHienThi();
		pnlViewContainer.revalidate();
		pnlViewContainer.repaint();
	}

	private void capNhatNutToggle() {
		if (btnToggleView == null)
			return;
		if (cheDoCard) {
			btnToggleView.setText("Bảng");
			btnToggleView.setToolTipText("Chuyển sang dạng bảng");
		} else {
			btnToggleView.setText("Lưới");
			btnToggleView.setToolTipText("Chuyển sang dạng lưới");
		}
	}

	//  HEADER 
	private JPanel taoHeader() {
		JPanel p = new JPanel(new BorderLayout());
		p.setOpaque(false);
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));

		JPanel left = col(label("Sản phẩm thuốc", FontStyle.XXL, FontStyle.BOLD, Colors.FOREGROUND),
				Box.createVerticalStrut(4),
				label("Quản lý danh mục thuốc và lô hàng", FontStyle.SM, FontStyle.NORMAL, Colors.MUTED));
		p.add(left, BorderLayout.WEST);

		RoundedButton btn = roundedBtn("+ Thêm sản phẩm", 170, 40, Colors.PRIMARY, Colors.BACKGROUND);
		btn.addActionListener(e -> ChiTietSanPham_GUI.moThemMoi(
				(java.awt.Window) SwingUtilities.getWindowAncestor(SanPham_GUI.this),
				() -> taiDuLieu()));
		JPanel rw = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 8));
		rw.setOpaque(false);
		rw.add(btn);
		p.add(rw, BorderLayout.EAST);
		return p;
	}

	//  THỐNG KÊ
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

	// TÌM KIẾM
	private JPanel taoTimKiem() {
		JPanel w = new JPanel(new BorderLayout(0, 8));
		w.setOpaque(false);
		w.setAlignmentX(LEFT_ALIGNMENT);
		w.setMaximumSize(new Dimension(Integer.MAX_VALUE, 62)); // cố định chiều cao tổng

		JPanel row = new JPanel(new BorderLayout(10, 0));
		row.setOpaque(false);
		row.setPreferredSize(new Dimension(200, 38));
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38)); // cố định chiều cao row

		txtSearch = new RoundedTextField(200, 38, 12, "Tìm kiếm theo tên hoặc mã sản phẩm...");
		txtSearch.setPreferredSize(new Dimension(200, 38));
		txtSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
		txtSearch.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				locVaHienThi();
			}
		});

		// Bọc trong GridBagLayout để không bị kéo giãn dọc
		JPanel searchWrap = new JPanel(new GridBagLayout());
		searchWrap.setOpaque(false);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.ipady = 0;
		searchWrap.add(txtSearch, gbc);
		row.add(searchWrap, BorderLayout.CENTER);

		JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
		right.setOpaque(false);

		// Dropdown loại SP
		JComboBox<String> cbo = new JComboBox<>();
		cbo.addItem("Tất cả");
		for (LoaiSanPham lsp : dsLoaiSP)
			cbo.addItem(lsp.getTenLoaiSanPham());
		cbo.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		cbo.setPreferredSize(new Dimension(180, 38));
		cbo.addActionListener(e -> {
			int idx = cbo.getSelectedIndex();
			maLoaiLoc = idx <= 0 ? null : dsLoaiSP.get(idx - 1).getMaLoaiSanPham();
			locVaHienThi();
		});
		right.add(cbo);

		// Filter buttons
		String[] labels = { "Tất cả", "Còn hàng", "Sắp hết", "Hết hàng" };
		String[] keys = { "ALL", "CON_HANG", "SAP_HET", "HET_HANG" };
		ButtonGroup grp = new ButtonGroup();
		for (int i = 0; i < labels.length; i++) {
			final String key = keys[i];
			RoundedToggleButton btn = new RoundedToggleButton(108, 34, 10, labels[i], Colors.PRIMARY);
			btn.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
			btn.setPreferredSize(new Dimension(108, 34));
			if (i == 0)
				btn.setSelected(true);
			grp.add(btn);
			btn.addActionListener(e -> {
				filterTon = key;
				locVaHienThi();
			});
			right.add(btn);
			if (i == 2) btnFilterSapHet = btn;
			if (i == 3) btnFilterHetHang = btn;
		}

		// NÚT TOGGLE VIEW (cuối cùng thanh tìm kiếm)
		btnToggleView = new JButton();
		btnToggleView.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		btnToggleView.setPreferredSize(new Dimension(70, 34));
		btnToggleView.setFocusPainted(false);
		btnToggleView.setBorderPainted(false);
		btnToggleView.setContentAreaFilled(true);
		btnToggleView.setOpaque(true);
		btnToggleView.setBackground(Colors.SECONDARY);
		btnToggleView.setForeground(Colors.TEXT_PRIMARY);
		btnToggleView.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btnToggleView.addActionListener(e -> toggleView());
		btnToggleView.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				btnToggleView.setBackground(Colors.BORDER_LIGHT);
			}

			public void mouseExited(MouseEvent e) {
				btnToggleView.setBackground(Colors.SECONDARY);
			}
		});
		right.add(btnToggleView);
		capNhatNutToggle();

		row.add(right, BorderLayout.EAST);
		w.add(row, BorderLayout.CENTER);

		lblCount = new JLabel("Hiển thị 0 / 0 sản phẩm");
		lblCount.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lblCount.setForeground(Colors.MUTED);
		w.add(lblCount, BorderLayout.SOUTH);
		return w;
	}

	// CHẾ ĐỘ 1: CARD GRID
	private JPanel taoGridView() {
		pnlCards = new JPanel(new GridLayout(0, 4, 14, 14));
		pnlCards.setBackground(Colors.BACKGROUND);

		// Pagination bar
		pnlPagination = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
		pnlPagination.setOpaque(false);
		pnlPagination.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

		JPanel wrap = new JPanel(new BorderLayout(0, 10));
		wrap.setOpaque(false);
		wrap.add(pnlCards, BorderLayout.CENTER);
		wrap.add(pnlPagination, BorderLayout.SOUTH);
		return wrap;
	}

	private void capNhatPagination() {
		if (pnlPagination == null)
			return;
		pnlPagination.removeAll();

		int totalPages = Math.max(1, (int) Math.ceil((double) dsHienThi.size() / PAGE_SIZE));
		if (totalPages <= 1) {
			pnlPagination.revalidate();
			pnlPagination.repaint();
			return;
		}

		// Nút Trước
		RoundedButton btnPrev = roundedBtn("← Trước", 90, 32, Colors.SECONDARY, Colors.TEXT_PRIMARY);
		btnPrev.setEnabled(currentPage > 0);
		btnPrev.addActionListener(e -> {
			currentPage--;
			renderCards();
			capNhatLblCount();
		});
		pnlPagination.add(btnPrev);

		// Page numbers (hiện tối đa 7 trang)
		int start = Math.max(0, currentPage - 3);
		int end = Math.min(totalPages, start + 7);
		for (int i = start; i < end; i++) {
			final int pg = i;
			Color bg = (i == currentPage) ? Colors.PRIMARY : Colors.SECONDARY;
			Color fg = (i == currentPage) ? Colors.BACKGROUND : Colors.TEXT_PRIMARY;
			RoundedButton btnPage = roundedBtn(String.valueOf(i + 1), 50, 32, bg, fg);
			btnPage.addActionListener(e -> {
				currentPage = pg;
				renderCards();
				capNhatLblCount();
			});
			pnlPagination.add(btnPage);
		}

		// Nút Sau
		RoundedButton btnNext = roundedBtn("Sau →", 90, 32, Colors.SECONDARY, Colors.TEXT_PRIMARY);
		btnNext.setEnabled(currentPage < totalPages - 1);
		btnNext.addActionListener(e -> {
			currentPage++;
			renderCards();
			capNhatLblCount();
		});
		pnlPagination.add(btnNext);

		pnlPagination.revalidate();
		pnlPagination.repaint();
	}

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
				? sp.getLoaiSanPham().getTenLoaiSanPham()
				: "Chưa phân loại";
		ct.add(leftLabel(loai, FontStyle.XS, FontStyle.NORMAL, Colors.SUCCESS));
		ct.add(Box.createVerticalStrut(2));

		// Tên
		String ten = sp.getTenSanPham();
		if (ten.length() > 28)
			ten = ten.substring(0, 28) + "...";
		ct.add(leftLabel(ten, FontStyle.SM, FontStyle.BOLD, Colors.TEXT_PRIMARY));
		ct.add(Box.createVerticalStrut(3));

		// Công dụng
		String cd = sp.getCongDung() != null ? sp.getCongDung() : "";
		if (cd.length() > 45)
			cd = cd.substring(0, 45) + "...";
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
		priceRow.setMaximumSize(dim(Integer.MAX_VALUE, 24));
		String giaHtml;
		if (sp.coKhuyenMai()) {
			giaHtml = "<html><span style='color:#ED5A2D; font-weight:bold;'>"
					+ PRICE_FMT.format(sp.getGiaSauKM()) + "đ</span> "
					+ "<span style='color:#9CA3AF; text-decoration:line-through; font-size:smaller;'>"
					+ PRICE_FMT.format(sp.getGiaThanh()) + "đ</span></html>";
		} else {
			giaHtml = PRICE_FMT.format(sp.getGiaThanh()) + "đ";
		}
		priceRow.add(leftLabel(giaHtml, FontStyle.SM, FontStyle.BOLD, Colors.SUCCESS),
				BorderLayout.WEST);
		priceRow.add(leftLabel("Tồn: " + info.tonKho, FontStyle.XS, FontStyle.NORMAL, Colors.MUTED), BorderLayout.EAST);
		ct.add(priceRow);
		ct.add(Box.createVerticalStrut(4));

		// Lô
		ct.add(leftLabel(info.soLo + " lô hàng  |  " + info.loHetHan + " lô hết hạn", FontStyle.XS, FontStyle.NORMAL,
				Colors.MUTED));
		ct.add(Box.createVerticalStrut(8));

		// Buttons
		JPanel btns = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
		btns.setOpaque(false);
		btns.setAlignmentX(LEFT_ALIGNMENT);
		btns.setMaximumSize(dim(Integer.MAX_VALUE, 34));
		RoundedButton btnXemCT = roundedBtn("Chi tiết", 100, 30, Colors.PRIMARY, Colors.BACKGROUND);
		btnXemCT.addActionListener(e -> ChiTietSanPham_GUI.moChiTiet(
				(java.awt.Window) SwingUtilities.getWindowAncestor(SanPham_GUI.this),
				sp, mapTonKho.get(sp.getMaSanPham())));
		btns.add(btnXemCT);
		RoundedButton btnSua = roundedBtn("Sửa", 60, 30, Colors.SECONDARY, Colors.TEXT_SECONDARY);
		btnSua.addActionListener(e -> ChiTietSanPham_GUI.moChinhSua(
				(java.awt.Window) SwingUtilities.getWindowAncestor(SanPham_GUI.this),
				sp, mapTonKho.get(sp.getMaSanPham()), () -> taiDuLieu()));
		btns.add(btnSua);
		RoundedButton btnXoa = roundedBtn("Xoá", 60, 30, Colors.SECONDARY, Colors.DANGER);
		btnXoa.addActionListener(e -> xuLyXoa(sp));
		btns.add(btnXoa);
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

		String fileName = sp.getHinhAnh();
		if (fileName != null && !fileName.trim().isEmpty()) {
			JLabel li = new JLabel();
			li.setHorizontalAlignment(SwingConstants.CENTER);

			// Lấy từ cache (sync) hoặc load background
			ImageIcon cached = imgCache.getImage(fileName, 180, 110, icon -> {
				if (li.isDisplayable()) {
					li.setIcon(icon);
					li.setText(null);
					p.revalidate();
					p.repaint();
				}
			});

			if (cached != null) {
				li.setIcon(cached);
			} else {
				// Placeholder trống trong khi chờ load
				li.setText("");
			}
			p.setLayout(new BorderLayout());
			p.add(li, BorderLayout.CENTER);
			return p;
		}

		JLabel na = new JLabel("Chưa có ảnh");
		na.setForeground(Colors.MUTED);
		na.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		p.add(na);
		return p;
	}

	// CHẾ ĐỘ 2: TABLE LIST
	private JPanel taoTableView() {
		tableModel = new DefaultTableModel(TABLE_COLUMNS, 0) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return col == 6; // Chỉ cột THAO TÁC
			}
		};

		tblSanPham = new JTable(tableModel);
		ProductTableRenderer.apply(tblSanPham);

		// Action editor cho cột THAO TÁC
		tblSanPham.getColumnModel().getColumn(6).setCellEditor(new ProductActionEditor(tblSanPham, (action, row) -> {
			if (row < 0 || row >= dsHienThi.size())
				return;
			SanPham sp = dsHienThi.get(row);
			switch (action) {
			case "XEM":
			case "CHI_TIET":
				ChiTietSanPham_GUI.moChiTiet(
					(java.awt.Window) SwingUtilities.getWindowAncestor(SanPham_GUI.this),
					sp, mapTonKho.get(sp.getMaSanPham()));
				break;
			case "SUA":
				ChiTietSanPham_GUI.moChinhSua(
					(java.awt.Window) SwingUtilities.getWindowAncestor(SanPham_GUI.this),
					sp, mapTonKho.get(sp.getMaSanPham()), () -> taiDuLieu());
				break;
			case "XOA":
				xuLyXoa(sp);
				break;
			}
		}));

		scrTable = new JScrollPane(tblSanPham);
		scrTable.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
		scrTable.getViewport().setBackground(Colors.BACKGROUND);

		JPanel wrap = new JPanel(new BorderLayout());
		wrap.setOpaque(false);
		wrap.add(scrTable, BorderLayout.CENTER);
		return wrap;
	}

	private Object[] taoRowData(SanPham sp, TonKhoInfo info) {
		String imgPath = "";
		if (sp.getHinhAnh() != null && !sp.getHinhAnh().trim().isEmpty())
			imgPath = sp.getHinhAnh(); // chỉ filename, ProductTableRenderer tự resolve path

		String danhMuc = sp.getLoaiSanPham() != null && sp.getLoaiSanPham().getTenLoaiSanPham() != null
				? sp.getLoaiSanPham().getTenLoaiSanPham()
				: "Chưa phân loại";

		int sapHetLo = ("SAP_HET".equals(info.trangThai)) ? 1 : 0;
		String ngayGanNhat = layNgayGanNhat(sp.getMaSanPham());

		return new Object[] { new Object[] { sp.getMaSanPham(), sp.getTenSanPham(), imgPath }, danhMuc,
				sp, info.tonKho, new Object[] { info.soLo, info.loHetHan, sapHetLo, ngayGanNhat },
				info.trangThai, null };
	}

	private String layNgayGanNhat(String maSP) {
		if (mapNgayGanNhat != null)
			return mapNgayGanNhat.getOrDefault(maSP, "");
		return "";
	}

	// Tính trước ngày gần nhất cho tất cả SP (1 lần query thay vì N lần)
	private void tinhNgayGanNhatTatCa() {
		mapNgayGanNhat = new HashMap<>();
		if (dsLoSanPhamCache == null) {
			dsLoSanPhamCache = safeCall(() -> loSPService.getDSLoSanPham(), new ArrayList<>());
		}
		Map<String, LocalDate> map = new HashMap<>();
		for (LoSanPham lo : dsLoSanPhamCache) {
			if (lo.getSanPham() == null || lo.getHanSuDung() == null)
				continue;
			String ma = lo.getSanPham().getMaSanPham();
			LocalDate cur = map.get(ma);
			if (cur == null || lo.getHanSuDung().isBefore(cur))
				map.put(ma, lo.getHanSuDung());
		}
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d/M/yyyy");
		for (Map.Entry<String, LocalDate> e : map.entrySet()) {
			mapNgayGanNhat.put(e.getKey(), e.getValue().format(fmt));
		}
	}

	// ==================== DATA ====================
	private void xuLyXoa(SanPham sp) {
		int confirm = JOptionPane.showConfirmDialog(this,
			"Ban co chac muon xoa san pham:\n" + sp.getTenSanPham() + " (" + sp.getMaSanPham() + ") ?",
			"Xac nhan xoa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
		if (confirm != JOptionPane.YES_OPTION) return;

		boolean ok = spService.xoaSanPham(sp.getMaSanPham());
		if (ok) {
			JOptionPane.showMessageDialog(this, "Da xoa san pham thanh cong", "Thanh cong",
				JOptionPane.INFORMATION_MESSAGE);
			taiDuLieu();
		} else {
			JOptionPane.showMessageDialog(this, "Khong the xoa san pham nay\n(co the da co lo hang hoac hoa don lien quan)",
				"Loi", JOptionPane.ERROR_MESSAGE);
		}
	}

	public void refresh() {
		taiDuLieu();
	}

	private void taiDuLieu() {
		dsGoc = safeCall(() -> spService.layDanhSachSanPham(), new ArrayList<>());
		mapTonKho = spService.tinhTonKhoTatCa(dsGoc);
		dsLoSanPhamCache = null; // reset cache lot
		tinhNgayGanNhatTatCa(); // 1 query cho tất cả

		// Preload ảnh background
		List<String> imgFiles = new ArrayList<>();
		for (SanPham sp : dsGoc) {
			if (sp.getHinhAnh() != null && !sp.getHinhAnh().trim().isEmpty())
				imgFiles.add(sp.getHinhAnh());
		}
		imgCache.preload(imgFiles, 180, 110);
		imgCache.preload(imgFiles, 46, 46); // cho chế độ bảng

		locVaHienThi();
	}

	private void locVaHienThi() {
		dsHienThi = spService.timKiem(dsGoc, txtSearch.getText().trim());
		if (maLoaiLoc != null)
			dsHienThi = spService.locTheoLoai(dsHienThi, maLoaiLoc);
		dsHienThi = spService.locTheoTrangThaiTon(dsHienThi, filterTon, mapTonKho);

		// Thống kê trên dsGoc
		ThongKe tk = spService.tinhThongKe(dsGoc, mapTonKho);
		lblTongSP.setText(String.valueOf(tk.tong));
		lblConHang.setText(String.valueOf(tk.conHang));
		lblSapHet.setText(String.valueOf(tk.sapHet));
		lblHetHang.setText(String.valueOf(tk.hetHang));


		if (cheDoCard) {
			renderCards();
		} else {
			renderTable();
		}

		capNhatLblCount();
	}

	private void capNhatLblCount() {
		int total = dsHienThi.size();
		ThongKe tk = spService.tinhThongKe(dsGoc, mapTonKho);
		if (cheDoCard) {
			int from = Math.min(currentPage * PAGE_SIZE + 1, total);
			int to = Math.min((currentPage + 1) * PAGE_SIZE, total);
			lblCount.setText(
					"Hiển thị " + from + "-" + to + " / " + total + " sản phẩm (trang " + (currentPage + 1) + ")");
		} else {
			lblCount.setText("Hiển thị " + total + " / " + tk.tong + " sản phẩm");
		}
	}

	private void renderCards() {
		if (pnlCards == null)
			return;
		pnlCards.removeAll();

		// Chỉ render PAGE_SIZE card cho trang hiện tại
		int from = currentPage * PAGE_SIZE;
		int to = Math.min(from + PAGE_SIZE, dsHienThi.size());
		for (int i = from; i < to; i++) {
			SanPham sp = dsHienThi.get(i);
			TonKhoInfo info = mapTonKho.getOrDefault(sp.getMaSanPham(), new TonKhoInfo(0, 0, 0));
			pnlCards.add(taoCard(sp, info));
		}
		pnlCards.revalidate();
		pnlCards.repaint();
		capNhatPagination();
	}

	private void renderTable() {
		if (tableModel == null)
			return;
		tableModel.setRowCount(0);
		for (SanPham sp : dsHienThi) {
			TonKhoInfo info = mapTonKho.getOrDefault(sp.getMaSanPham(), new TonKhoInfo(0, 0, 0));
			tableModel.addRow(taoRowData(sp, info));
		}
	}

	// UI HELPERS
	private JLabel badge(TonKhoInfo info) {
		Color bg, fg;
		if ("CON_HANG".equals(info.trangThai)) {
			bg = Colors.GREEN_HOVER;
			fg = Colors.SUCCESS_DARK;
		} else if ("SAP_HET".equals(info.trangThai)) {
			bg = Colors.YELLOW_HOVER;
			fg = Colors.ACCENT;
		} else {
			bg = Colors.BROWN_HOVER;
			fg = Colors.DANGER;
		}
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
		for (Component c : comps)
			p.add(c);
		return p;
	}

	private RoundedButton roundedBtn(String text, int w, int h, Color bg, Color fg) {
		RoundedButton b = new RoundedButton(w, h, 10, text, bg);
		b.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		b.setForeground(fg);
		return b;
	}

	private Dimension dim(int w, int h) {
		return new Dimension(w, h);
	}

	private <T> T safeCall(java.util.concurrent.Callable<T> c, T fallback) {
		try {
			return c.call();
		} catch (Exception e) {
			e.printStackTrace();
			return fallback;
		}
	}
}
