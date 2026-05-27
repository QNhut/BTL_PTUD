package gui;

import constants.Colors;
import constants.FontStyle;
import entity.HoaDon;
import entity.KhachHang;
import entity.LoSanPham;
import entity.NhanVien;
import entity.SanPham;
import exception.ProductTableRenderer;
import exception.RoundedButton;
import exception.RoundedPanel;
import exception.RoundedTextField;
import exception.RoundedToggleButton;
import entity.LoaiSanPham;
import service.HoaDon_Service;
import service.KhachHang_Service;
import service.LoaiSanPham_Service;
import service.LoSanPham_Service;
import service.SanPham_Service;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

@SuppressWarnings("serial")
public class DatTruoc_GUI extends JPanel {

	private static final DecimalFormat PRICE_FMT = new DecimalFormat("#,###");
	private static final int ACTION_ROW_H = 56;
	private static final int CART_ROW_H = 44;
	private static final LocalTime GIO_MO_CUA_THU_2_7 = LocalTime.of(8, 0);
	private static final LocalTime GIO_DONG_CUA_THU_2_7 = LocalTime.of(22, 0);
	private static final LocalTime GIO_MO_CUA_CHU_NHAT = LocalTime.of(9, 0);
	private static final LocalTime GIO_DONG_CUA_CHU_NHAT = LocalTime.of(20, 0);

	private static final String[] PRODUCT_COLS = { "Sản phẩm", "Giá bán", "Tồn kho", "Trạng thái", "Thao tác" };
	private static final String[] CART_COLS = { "Sản phẩm", "SL", "Thành tiền", "" };

	private final SanPham_Service spService = new SanPham_Service();
	private final KhachHang_Service khService = new KhachHang_Service();
	private final LoSanPham_Service loService = new LoSanPham_Service();
	private final LoaiSanPham_Service loaiSPService = new LoaiSanPham_Service();

	private List<SanPham> dsGoc = new ArrayList<>();
	private List<SanPham> dsHienThi = new ArrayList<>();
	private Map<String, SanPham_Service.TonKhoInfo> mapTonKho = new HashMap<>();
	private Map<String, String> mapNgayGanNhat = new HashMap<>();

	private final List<CartItem> cartItems = new ArrayList<>();
	private int depositPct = 0;
	private boolean isOldCustomer = true;
	private String selectedMaLoai = null;

	private RoundedTextField txtSearch;
	private JButton btnLoaiFilter;
	private DefaultTableModel productModel;
	private JTable tblProduct;
	private JDateChooser dtcNgayNhan;
	private JComboBox<String> cboGioNhan;
	private JRadioButton rdoKhachCu, rdoKhachMoi;
	private JPanel pnlKhachCu, pnlKhachMoi;

	private RoundedTextField txtSDTKhachCu;
	private JLabel lblKhachCuStatus;
	private KhachHang khachHangCu = null;
	private RoundedTextField txtTenKhachMoi;
	private RoundedTextField txtSDTKhachMoi;
	private DefaultTableModel cartModel;
	private JTable tblCart;
	private JLabel lblCartCount, lblTongSP, lblTongDonVi, lblTongTien;
	private RoundedButton btnXacNhan;
	private JLabel lblHint;
	private JTextArea txtGhiChu;
	private RoundedToggleButton btnDeposit0;
	private final HoaDon_Service hoaDonService = new HoaDon_Service();
	private final NhanVien nhanVien;

	private static final class CartItem {

		final String maSP, tenSP, donVi;
		final double gia;
		int qty;

		CartItem(SanPham sp) {
			maSP = sp.getMaSanPham();
			tenSP = sp.getTenSanPham();
			donVi = (sp.getDonViTinh() != null && !sp.getDonViTinh().isEmpty()) ? sp.getDonViTinh() : "Hộp";
			gia = sp.coKhuyenMai() ? sp.getGiaSauKM() : sp.getGiaThanh();
			qty = 1;
		}

		double thanhTien() {
			return gia * qty;
		}
	}

	public DatTruoc_GUI() {
		this(null);
	}

	public DatTruoc_GUI(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
		setLayout(new BorderLayout(0, 12));
		setBackground(Colors.BACKGROUND);
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		add(buildHeaderPanel(), BorderLayout.NORTH);

		JPanel pnlMain = new JPanel(new BorderLayout(12, 0));
		pnlMain.setOpaque(false);

		JPanel pnlLeft = new JPanel();
		pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
		pnlLeft.setOpaque(false);

		JPanel productPanel = buildProductPanel();
		productPanel.setPreferredSize(new Dimension(0, 360));
		productPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));
		productPanel.setBackground(Colors.BACKGROUND);
		pnlLeft.add(productPanel);
		pnlLeft.add(Box.createVerticalStrut(6));
		pnlLeft.add(buildBottomPanel());

		pnlMain.add(pnlLeft, BorderLayout.CENTER);

		JPanel formRight = buildFormPanel();
		formRight.setPreferredSize(new Dimension(450, 0));
		pnlMain.add(formRight, BorderLayout.EAST);

		add(pnlMain, BorderLayout.CENTER);

		loadDataBackground();
	}

	// =========================================================================
	// HEADER PANEL
	// =========================================================================
	private JPanel buildHeaderPanel() {
		JPanel hdr = new JPanel();
		hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
		hdr.setOpaque(false);
		hdr.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));

		JLabel lblTitle = new JLabel("Đặt trước thuốc");
		lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
		lblTitle.setForeground(Colors.FOREGROUND);
		lblTitle.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lblSub = new JLabel("Chọn sản phẩm và điền thông tin để tạo phiếu");
		lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lblSub.setForeground(Colors.MUTED);
		lblSub.setAlignmentX(LEFT_ALIGNMENT);

		hdr.add(lblTitle);
		hdr.add(Box.createVerticalStrut(2));
		hdr.add(lblSub);
		return hdr;
	}

	// =========================================================================
	// PRODUCT PANEL (left column)
	// =========================================================================
	private JPanel buildProductPanel() {
		JPanel p = new JPanel(new BorderLayout(0, 8));
		p.setOpaque(false);

		txtSearch = new RoundedTextField(0, 38, 12, "Tìm thuốc để đặt trước...");
		txtSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				filterAndDisplay();
			}
		});

		// ── Category filter popup ──────────────────────────────────────────
		java.util.List<LoaiSanPham> dsLoai = loaiSPService.layDanhSachLoaiSanPham();
		JPopupMenu popupLoai = new JPopupMenu();
		popupLoai.setBackground(Colors.BACKGROUND);
		popupLoai.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1),
				BorderFactory.createEmptyBorder(4, 0, 4, 0)));

		JMenuItem itemTatCa = new JMenuItem("Tất cả loại");
		styleMenuItem(itemTatCa);
		itemTatCa.addActionListener(ev -> {
			selectedMaLoai = null;
			btnLoaiFilter.setText("▼ Tất cả loại");
			filterAndDisplay();
		});
		popupLoai.add(itemTatCa);

		for (LoaiSanPham loai : dsLoai) {
			JMenuItem item = new JMenuItem(loai.getTenLoaiSP());
			styleMenuItem(item);
			item.addActionListener(ev -> {
				selectedMaLoai = loai.getMaLoaiSP();
				String label = loai.getTenLoaiSP();
				btnLoaiFilter.setText("▼ " + (label.length() > 14 ? label.substring(0, 14) + "…" : label));
				filterAndDisplay();
			});
			popupLoai.add(item);
		}

		btnLoaiFilter = new RoundedButton(150, 38, 12, "▼ Tất cả loại", Colors.SECONDARY);
		btnLoaiFilter.setForeground(Colors.TEXT_PRIMARY);
		btnLoaiFilter.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		btnLoaiFilter.setMinimumSize(new Dimension(140, 38));
		btnLoaiFilter.setPreferredSize(new Dimension(150, 38));
		btnLoaiFilter.setMaximumSize(new Dimension(160, 38));
		btnLoaiFilter.addActionListener(e -> {
			popupLoai.setPreferredSize(new Dimension(180, popupLoai.getPreferredSize().height));
			popupLoai.show(btnLoaiFilter, 0, btnLoaiFilter.getHeight() + 4);
		});

		JPanel searchRow = new JPanel();
		searchRow.setLayout(new BoxLayout(searchRow, BoxLayout.X_AXIS));
		searchRow.setOpaque(false);
		searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
		searchRow.add(txtSearch);
		searchRow.add(Box.createHorizontalStrut(8));
		searchRow.add(btnLoaiFilter);

		p.add(searchRow, BorderLayout.NORTH);

		productModel = new DefaultTableModel(PRODUCT_COLS, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return c == 4;
			}
		};
		tblProduct = new JTable(productModel);
		ProductTableRenderer.apply(tblProduct);
		tblProduct.setRowHeight(56);

		tblProduct.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		int[] colW = { 180, 110, 65, 100, 130 };
		for (int i = 0; i < colW.length; i++) {
			tblProduct.getColumnModel().getColumn(i).setPreferredWidth(colW[i]);
		}

		tblProduct.getColumnModel().getColumn(0).setCellRenderer(new ProductNameRdr());
		tblProduct.getColumnModel().getColumn(1).setCellRenderer(new PriceRdr());
		tblProduct.getColumnModel().getColumn(2).setCellRenderer(new StockRdr());
		tblProduct.getColumnModel().getColumn(3).setCellRenderer(new StatusRdr());
		TableColumn ac = tblProduct.getColumnModel().getColumn(4);
		ac.setCellRenderer(new ProductActionRenderer());
		ac.setCellEditor(new ProductActionEditor());

		JScrollPane scroll = new JScrollPane(tblProduct);
		scroll.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
		scroll.getViewport().setBackground(Colors.BACKGROUND);
		p.add(scroll, BorderLayout.CENTER);
		return p;
	}


	private JPanel buildFormPanel() {
		JPanel outer = new JPanel(new BorderLayout());
		outer.setBackground(Colors.BACKGROUND);
		outer.setLayout(new BoxLayout(outer, BoxLayout.Y_AXIS));

		JPanel content = new JPanel();
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
		content.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1),
				BorderFactory.createEmptyBorder(12, 12, 12, 12)));
		content.setBackground(Colors.SECONDARY);

		content.add(buildSectionThoiGian());
		content.add(vgap(6));
		content.add(buildSectionKhachHang());
		content.add(vgap(6));
		content.add(buildSectionDatCoc());
		content.add(vgap(6));
		content.add(buildSectionGhiChu());
		content.add(vgap(10));

		btnXacNhan = new RoundedButton(Integer.MAX_VALUE, 46, 12, "Xác nhận đặt trước", Colors.PRIMARY);
		btnXacNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
		btnXacNhan.setAlignmentX(LEFT_ALIGNMENT);
		btnXacNhan.setEnabled(false);
		btnXacNhan.setForeground(Color.WHITE);
		btnXacNhan.addActionListener(e -> xuLyXacNhan());
		content.add(btnXacNhan);
		content.add(vgap(6));

		lblHint = new JLabel("Vui lòng chọn ngày nhận hàng");
		lblHint.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblHint.setForeground(Colors.MUTED);
		lblHint.setAlignmentX(LEFT_ALIGNMENT);
		content.add(lblHint);
		content.add(vgap(8));
		content.add(Box.createVerticalGlue());
	
		outer.add(content);
		outer.add(Box.createVerticalStrut(270));
		return outer;
	}

	// =========================================================================
	// BOTTOM PANEL (full-width cart table + confirm)
	// =========================================================================
	private JPanel buildBottomPanel() {
		JPanel outer = new RoundedPanel(0, 0, 12);
		outer.setLayout(new BorderLayout());
		outer.setBackground(Colors.BACKGROUND);
		outer.setMinimumSize(new Dimension(0, 300));
		outer.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE)); // fills remaining height

		JPanel cartSection = buildSectionCart();
		outer.add(cartSection, BorderLayout.CENTER);
		return outer;
	}

	// Thời gian nhận hàng
	private JPanel buildSectionThoiGian() {
		JPanel p = sectionPanel("THỜI GIAN NHẬN HÀNG");
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));

		Box row = Box.createHorizontalBox();
		row.setAlignmentX(Component.LEFT_ALIGNMENT);

		// ================= NGÀY NHẬN =================
		JPanel colNgay = labeledCol("Ngày nhận *");
		colNgay.setLayout(new BoxLayout(colNgay, BoxLayout.Y_AXIS));
		colNgay.setOpaque(false);

		dtcNgayNhan = new JDateChooser();
		dtcNgayNhan.setDateFormatString("dd/MM/yyyy");
		dtcNgayNhan.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		dtcNgayNhan.setDate(new Date());

		dtcNgayNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		dtcNgayNhan.setPreferredSize(new Dimension(220, 30));
		dtcNgayNhan.setAlignmentX(Component.LEFT_ALIGNMENT);

		JComponent dateEditor = dtcNgayNhan.getDateEditor().getUiComponent();

		if (dateEditor instanceof JTextField) {
			JTextField dateText = (JTextField) dateEditor;

			dateText.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));

			dateText.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Colors.BORDER, 1),
					BorderFactory.createEmptyBorder(2, 8, 2, 8)));
		}

		dtcNgayNhan.addPropertyChangeListener("date", e -> {
			capNhatKhungGioNhan();
			updateConfirmButton();
		});

		colNgay.add(dtcNgayNhan);

		// ================= GIỜ NHẬN =================
		JPanel colGio = labeledCol("Giờ nhận");
		colGio.setLayout(new BoxLayout(colGio, BoxLayout.Y_AXIS));
		colGio.setOpaque(false);

		cboGioNhan = new JComboBox<>();
		cboGioNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		cboGioNhan.setPreferredSize(new Dimension(150, 30));
		cboGioNhan.setAlignmentX(Component.LEFT_ALIGNMENT);
		cboGioNhan.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		cboGioNhan.addActionListener(e -> updateConfirmButton());

		capNhatKhungGioNhan();

		colGio.add(cboGioNhan);

		// ================= ADD =================
		row.add(colNgay);
		row.add(Box.createHorizontalStrut(16)); // khoảng cách
		row.add(colGio);

		p.add(row);
		p.add(Box.createVerticalStrut(8));

		return p;
	}

	// ── Thông tin khách hàng ─────────────────────────────────────────────────
	private JPanel buildSectionKhachHang() {
		JPanel p = sectionPanel("THÔNG TIN KHÁCH HÀNG");

		rdoKhachCu = new JRadioButton("Khách cũ");
		rdoKhachCu.setSelected(true);
		rdoKhachCu.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		rdoKhachCu.setOpaque(false);
		rdoKhachMoi = new JRadioButton("Khách mới");
		rdoKhachMoi.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		rdoKhachMoi.setOpaque(false);
		ButtonGroup bg = new ButtonGroup();
		bg.add(rdoKhachCu);
		bg.add(rdoKhachMoi);
		rdoKhachCu.addActionListener(e -> {
			isOldCustomer = true;
			switchCustomerPanel();
		});
		rdoKhachMoi.addActionListener(e -> {
			isOldCustomer = false;
			switchCustomerPanel();
		});
		JPanel pnlToggle = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		pnlToggle.setOpaque(false);
		pnlToggle.setAlignmentX(LEFT_ALIGNMENT);
		pnlToggle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
		pnlToggle.add(rdoKhachCu);
		pnlToggle.add(rdoKhachMoi);
		p.add(pnlToggle);
		p.add(vgap(8));

		// ── Khách cũ: tìm theo số điện thoại ──────────────────────────────
		pnlKhachCu = new JPanel();
		pnlKhachCu.setLayout(new BoxLayout(pnlKhachCu, BoxLayout.Y_AXIS));
		pnlKhachCu.setOpaque(false);
		pnlKhachCu.setAlignmentX(LEFT_ALIGNMENT);

		JLabel lblSDTCu = new JLabel("Số điện thoại khách hàng");
		lblSDTCu.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblSDTCu.setForeground(Colors.TEXT_SECONDARY);
		lblSDTCu.setAlignmentX(LEFT_ALIGNMENT);

		txtSDTKhachCu = new RoundedTextField(0, 32, 10, "Nhập 10 số để tìm khách hàng...");
		txtSDTKhachCu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		txtSDTKhachCu.setAlignmentX(LEFT_ALIGNMENT);
		txtSDTKhachCu.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				timKhachHangCu();
			}
		});
		txtSDTKhachCu.addActionListener(e -> timKhachHangCu());

		lblKhachCuStatus = new JLabel(" ");
		lblKhachCuStatus.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblKhachCuStatus.setAlignmentX(LEFT_ALIGNMENT);
		lblKhachCuStatus.setVisible(false);

		pnlKhachCu.add(lblSDTCu);
		pnlKhachCu.add(vgap(4));
		pnlKhachCu.add(txtSDTKhachCu);
		pnlKhachCu.add(vgap(3));
		pnlKhachCu.add(lblKhachCuStatus);

		// ── Khách mới: nhập tên + SDT ─────────────────────────────────────
		pnlKhachMoi = new JPanel();
		pnlKhachMoi.setLayout(new BoxLayout(pnlKhachMoi, BoxLayout.Y_AXIS));
		pnlKhachMoi.setOpaque(false);
		pnlKhachMoi.setAlignmentX(LEFT_ALIGNMENT);

		txtTenKhachMoi = new RoundedTextField(0, 30, 10, "Họ và tên khách hàng");
		txtTenKhachMoi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		txtTenKhachMoi.setAlignmentX(LEFT_ALIGNMENT);
		txtSDTKhachMoi = new RoundedTextField(0, 30, 10, "Số điện thoại (10 chữ số)");
		txtSDTKhachMoi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		txtSDTKhachMoi.setAlignmentX(LEFT_ALIGNMENT);

		pnlKhachMoi.add(txtTenKhachMoi);
		pnlKhachMoi.add(vgap(6));
		pnlKhachMoi.add(txtSDTKhachMoi);
		pnlKhachMoi.setVisible(false);

		p.add(pnlKhachCu);
		p.add(pnlKhachMoi);
		return p;
	}

	private void switchCustomerPanel() {
		pnlKhachCu.setVisible(isOldCustomer);
		pnlKhachMoi.setVisible(!isOldCustomer);
		revalidate();
		repaint();
	}

	// ── Đặt cọc ──────────────────────────────────────────────────────────────
	private JPanel buildSectionDatCoc() {
		JPanel p = sectionPanel("ĐẶT CỌC");

		JPanel btns = new JPanel(new GridLayout(1, 5, 6, 0));
		btns.setBackground(Colors.BACKGROUND);
		btns.setOpaque(true);
		btns.setAlignmentX(LEFT_ALIGNMENT);
		btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		btns.setMinimumSize(new Dimension(0, 32));
		btns.setPreferredSize(new Dimension(0, 32));

		String[] labels = { "0%", "20%", "30%", "50%", "100%" };
		int[] values = { 0, 20, 30, 50, 100 };
		ButtonGroup bgD = new ButtonGroup();

		for (int i = 0; i < labels.length; i++) {
			final int val = values[i];
			RoundedToggleButton btn = new RoundedToggleButton(60, 32, 16, labels[i], Colors.PRIMARY);
			btn.setSelected(val == 0);
			if (val == 0) {
				btnDeposit0 = btn;
			}
			bgD.add(btn);
			btn.addActionListener(e -> depositPct = val);
			btns.add(btn);
		}
		p.add(btns);
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 95));
		return p;
	}

	// ── Ghi chú chung ────────────────────────────────────────────────────────
	private JPanel buildSectionGhiChu() {
		JPanel p = sectionPanel("GHI CHÚ CHUNG");

		txtGhiChu = new JTextArea(3, 0);
		txtGhiChu.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		txtGhiChu.setLineWrap(true);
		txtGhiChu.setWrapStyleWord(true);
		txtGhiChu.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
		txtGhiChu.setText("Ghi chú (toa thuốc, yêu cầu đặc biệt...)");
		txtGhiChu.setForeground(Colors.MUTED);
		txtGhiChu.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if ("Ghi chú (toa thuốc, yêu cầu đặc biệt...)".equals(txtGhiChu.getText())) {
					txtGhiChu.setText("");
					txtGhiChu.setForeground(Colors.TEXT_PRIMARY);
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				if (txtGhiChu.getText().trim().isEmpty()) {
					txtGhiChu.setText("Ghi chú (toa thuốc, yêu cầu đặc biệt...)");
					txtGhiChu.setForeground(Colors.MUTED);
				}
			}
		});

		JPanel box = new JPanel(new BorderLayout());
		box.setBackground(Colors.BACKGROUND);
		box.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1));
		box.add(txtGhiChu, BorderLayout.CENTER);
		box.setAlignmentX(LEFT_ALIGNMENT);
		box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
		p.add(box);
		p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
		return p;
	}

	// ── Chi tiết thuốc đặt — JTable ──────────────────────────────────────────
	private JPanel buildSectionCart() {
		JPanel p = new JPanel(new BorderLayout(0, 8));
		p.setBackground(Colors.BACKGROUND);
		p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1),
				BorderFactory.createEmptyBorder(12, 14, 14, 14)));

		// Header
		JPanel hdr = new JPanel(new BorderLayout(6, 0));
		hdr.setOpaque(false);

		JLabel lblT = new JLabel("Chi tiết thuốc đặt");
		lblT.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
		lblT.setForeground(Colors.TEXT_PRIMARY);

		lblCartCount = new JLabel("0 SP");
		lblCartCount.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		lblCartCount.setForeground(Colors.SUCCESS_DARK);
		lblCartCount.setOpaque(true);
		lblCartCount.setBackground(Colors.SUCCESS_LIGHT);
		lblCartCount.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

		hdr.add(lblT, BorderLayout.WEST);
		hdr.add(lblCartCount, BorderLayout.EAST);

		// Cart JTable
		cartModel = new DefaultTableModel(CART_COLS, 0) {
			@Override
			public boolean isCellEditable(int r, int c) {
				return c == 1 || c == 3;
			}

			@Override
			public Class<?> getColumnClass(int c) {
				return Object.class;
			}
		};

		tblCart = new JTable(cartModel);
		tblCart.setRowHeight(CART_ROW_H);
		tblCart.setShowGrid(false);
		tblCart.setIntercellSpacing(new Dimension(0, 0));
		tblCart.setBackground(Colors.BACKGROUND);
		tblCart.setSelectionBackground(Colors.PRIMARY_LIGHT);
		tblCart.setFocusable(false);
		tblCart.setFillsViewportHeight(false);
		tblCart.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		styleCartHeader();

		// Full-width layout: product name expands, fixed widths for qty/price/del
		tblCart.getColumnModel().getColumn(0).setPreferredWidth(400);
		tblCart.getColumnModel().getColumn(1).setPreferredWidth(100);
		tblCart.getColumnModel().getColumn(1).setMaxWidth(120);
		tblCart.getColumnModel().getColumn(2).setPreferredWidth(160);
		tblCart.getColumnModel().getColumn(2).setMaxWidth(200);
		tblCart.getColumnModel().getColumn(3).setPreferredWidth(40);
		tblCart.getColumnModel().getColumn(3).setMaxWidth(50);

		tblCart.getColumnModel().getColumn(0).setCellRenderer(new CartProductRdr());
		tblCart.getColumnModel().getColumn(1).setCellRenderer(new CartQtyRdr());
		tblCart.getColumnModel().getColumn(1).setCellEditor(new CartQtyEditor());
		tblCart.getColumnModel().getColumn(2).setCellRenderer(new CartTotalRdr());
		tblCart.getColumnModel().getColumn(3).setCellRenderer(new CartDelRdr());
		tblCart.getColumnModel().getColumn(3).setCellEditor(new CartDelEditor());

		JScrollPane cartScroll = new JScrollPane(tblCart);
		cartScroll.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
		cartScroll.getViewport().setBackground(Colors.BACKGROUND);
		cartScroll.setPreferredSize(new Dimension(0, 200));

		// Summary
		JPanel sum = new JPanel(new GridLayout(2, 1, 0, 4));
		sum.setBackground(Colors.BACKGROUND);
		sum.setBorder(
				BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT),
						BorderFactory.createEmptyBorder(8, 0, 0, 0)));

		JPanel r1 = new JPanel(new BorderLayout());
		r1.setOpaque(false);
		lblTongSP = mkLabel("0 sản phẩm", false);
		lblTongDonVi = mkLabel("0 đơn vị", false);
		r1.add(lblTongSP, BorderLayout.WEST);
		r1.add(lblTongDonVi, BorderLayout.EAST);

		JPanel r2 = new JPanel(new BorderLayout());
		r2.setOpaque(false);
		JLabel lblTL = mkLabel("Tổng cộng", true);
		lblTL.setForeground(Colors.TEXT_PRIMARY);
		lblTongTien = mkLabel("0đ", true);
		lblTongTien.setForeground(Colors.SUCCESS_DARK);
		r2.add(lblTL, BorderLayout.WEST);
		r2.add(lblTongTien, BorderLayout.EAST);

		sum.add(r1);
		sum.add(r2);

		p.add(hdr, BorderLayout.NORTH);
		p.add(cartScroll, BorderLayout.CENTER);
		p.add(sum, BorderLayout.SOUTH);
		return p;
	}

	private void styleCartHeader() {
		JTableHeader h = tblCart.getTableHeader();
		h.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		h.setBackground(Colors.SECONDARY);
		h.setForeground(Colors.TEXT_SECONDARY);
		h.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));
		h.setReorderingAllowed(false);
		h.setResizingAllowed(false);
		h.setPreferredSize(new Dimension(0, 30));
	}

	// =========================================================================
	// CART MANAGEMENT
	// =========================================================================
	private void addToCart(SanPham sp) {
		for (CartItem ci : cartItems) {
			if (ci.maSP.equals(sp.getMaSanPham())) {
				ci.qty++;
				refreshCart();
				return;
			}
		}
		cartItems.add(new CartItem(sp));
		refreshCart();
		tblProduct.repaint();
	}

	private void removeFromCart(int row) {
		if (row >= 0 && row < cartItems.size()) {
			cartItems.remove(row);
			refreshCart();
			tblProduct.repaint();
		}
	}

	private void removeFromCartByMa(String maSP) {
		cartItems.removeIf(ci -> ci.maSP.equals(maSP));
		refreshCart();
		tblProduct.repaint();
	}

	private void refreshCart() {
		cartModel.setRowCount(0);
		for (CartItem ci : cartItems) {
			cartModel.addRow(new Object[] { ci, ci, ci, ci });
		}
		int totalQty = cartItems.stream().mapToInt(ci -> ci.qty).sum();
		double total = cartItems.stream().mapToDouble(CartItem::thanhTien).sum();
		lblCartCount.setText(cartItems.size() + " SP");
		lblTongSP.setText(cartItems.size() + " sản phẩm");
		lblTongDonVi.setText(totalQty + " đơn vị");
		lblTongTien.setText(PRICE_FMT.format(total) + "đ");
		updateConfirmButton();
	}

	private boolean isInCart(String maSP) {
		return cartItems.stream().anyMatch(ci -> ci.maSP.equals(maSP));
	}

	private void updateConfirmButton() {
		if (btnXacNhan == null) return;
		boolean hasDate = dtcNgayNhan != null && dtcNgayNhan.getDate() != null;
		boolean hasItems = !cartItems.isEmpty();
		boolean ok = hasDate && hasItems;
		btnXacNhan.setEnabled(ok);
		if (!hasItems) {
			lblHint.setText("Vui lòng thêm sản phẩm vào giỏ hàng");
		} else if (!hasDate) {
			lblHint.setText("Vui lòng nhập ngày nhận hàng");
		} else {
			lblHint.setText("");
		}
	}

	private void capNhatKhungGioNhan() {
		if (cboGioNhan == null) return;
		LocalDate ngayNhan = dtcNgayNhan != null && dtcNgayNhan.getDate() != null
				? dtcNgayNhan.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
				: LocalDate.now();
		LocalTime gioMoCua = laChuNhat(ngayNhan) ? GIO_MO_CUA_CHU_NHAT : GIO_MO_CUA_THU_2_7;
		LocalTime gioDongCua = laChuNhat(ngayNhan) ? GIO_DONG_CUA_CHU_NHAT : GIO_DONG_CUA_THU_2_7;

		// Lưu lại giờ đang chọn (nếu có) để giữ nguyên khi ngày thay đổi
		String prevSelected = (String) cboGioNhan.getSelectedItem();

		cboGioNhan.removeAllItems();
		LocalTime slot = gioMoCua;
		while (!slot.isAfter(gioDongCua)) {
			cboGioNhan.addItem(slot.format(DateTimeFormatter.ofPattern("HH:mm")));
			slot = slot.plusMinutes(30);
		}

		// Chọn giờ gần nhất với giờ hiện tại nếu là hôm nay, ngược lại giữ lựa chọn cũ
		String target = prevSelected;
		if (target == null || ngayNhan.equals(LocalDate.now())) {
			target = gioGanNhatHopLe(gioMoCua, gioDongCua);
		}
		cboGioNhan.setSelectedItem(target);
		if (cboGioNhan.getSelectedIndex() < 0) {
			cboGioNhan.setSelectedIndex(0);
		}

		cboGioNhan.setToolTipText(laChuNhat(ngayNhan) ? "Chỉ nhận từ 09:00 đến 20:00 vào Chủ nhật"
				: "Chỉ nhận từ 08:00 đến 22:00 từ Thứ 2 đến Thứ 7");
	}

	/** Trả về slot 30 phút gần nhất >= giờ hiện tại, nằm trong [gioMoCua, gioDongCua]. */
	private static String gioGanNhatHopLe(LocalTime gioMoCua, LocalTime gioDongCua) {
		LocalTime now = LocalTime.now();
		// Làm tròn lên 30 phút gần nhất
		int minute = now.getMinute();
		int roundedMinute = (minute == 0) ? 0 : ((minute / 30) + 1) * 30;
		LocalTime candidate;
		if (roundedMinute >= 60) {
			candidate = LocalTime.of(now.getHour() + 1, 0);
		} else {
			candidate = LocalTime.of(now.getHour(), roundedMinute);
		}
		if (candidate.isBefore(gioMoCua)) candidate = gioMoCua;
		if (candidate.isAfter(gioDongCua)) candidate = gioMoCua;
		return candidate.format(DateTimeFormatter.ofPattern("HH:mm"));
	}

	private static boolean laChuNhat(LocalDate ngay) {
		return ngay != null && ngay.getDayOfWeek().getValue() == 7;
	}

	private boolean gioNhanHopLe() {
		if (dtcNgayNhan == null || dtcNgayNhan.getDate() == null || cboGioNhan == null
				|| cboGioNhan.getSelectedItem() == null) {
			return false;
		}
		LocalDate ngayNhan = dtcNgayNhan.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		LocalTime gioNhan = LocalTime.parse((String) cboGioNhan.getSelectedItem(),
				DateTimeFormatter.ofPattern("HH:mm"));
		LocalTime gioMoCua = laChuNhat(ngayNhan) ? GIO_MO_CUA_CHU_NHAT : GIO_MO_CUA_THU_2_7;
		LocalTime gioDongCua = laChuNhat(ngayNhan) ? GIO_DONG_CUA_CHU_NHAT : GIO_DONG_CUA_THU_2_7;
		return !gioNhan.isBefore(gioMoCua) && !gioNhan.isAfter(gioDongCua);
	}

	// =========================================================================
	// DATA LOADING
	// =========================================================================
	// Được gọi từ Main_GUI khi chuyển sang tab Đặt trước — tải lại danh sách sản phẩm.
	public void refresh() {
		loadDataBackground();
	}

	private void loadDataBackground() {
		new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() {
				dsGoc = spService.layDanhSachSanPham();
				mapTonKho = spService.tinhTonKhoTatCa(dsGoc);

				List<LoSanPham> dsLo = loService.getDSLoSanPham();
				Map<String, LocalDate> nearest = new HashMap<>();
				for (LoSanPham lo : dsLo) {
					if (lo.getSanPham() == null || lo.getHanSuDung() == null) {
						continue;
					}
					String ma = lo.getSanPham().getMaSanPham();
					LocalDate c = nearest.get(ma);
					if (c == null || lo.getHanSuDung().isBefore(c)) {
						nearest.put(ma, lo.getHanSuDung());
					}
				}
				DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d/M/yyyy");
				nearest.forEach((k, v) -> mapNgayGanNhat.put(k, v.format(fmt)));
				return null;
			}

			@Override
			protected void done() {
				dsHienThi = new ArrayList<>(dsGoc);
				renderProductTable();
			}
		}.execute();
	}

	private void filterAndDisplay() {
		String kw = txtSearch.getText().trim();
		dsHienThi = kw.isEmpty() ? new ArrayList<>(dsGoc) : spService.timKiem(dsGoc, kw);
		if (selectedMaLoai != null) {
			List<SanPham> filtered = new ArrayList<>();
			for (SanPham sp : dsHienThi) {
				if (sp.getLoaiSP() != null && selectedMaLoai.equals(sp.getLoaiSP().getMaLoaiSP())) {
					filtered.add(sp);
				}
			}
			dsHienThi = filtered;
		}
		renderProductTable();
	}

	private void timKhachHangCu() {
		if (txtSDTKhachCu == null) {
			return;
		}
		String sdt = txtSDTKhachCu.getText().trim();
		if (sdt.isEmpty()) {
			lblKhachCuStatus.setVisible(false);
			khachHangCu = null;
			return;
		}
		if (!sdt.matches("\\d{10}")) {
			lblKhachCuStatus.setText("✗ Số điện thoại phải có đúng 10 chữ số");
			lblKhachCuStatus.setForeground(Colors.DANGER);
			lblKhachCuStatus.setVisible(true);
			khachHangCu = null;
			return;
		}
		KhachHang kh = khService.layKHTheoSDT(sdt);
		if (kh != null && kh.isTrangThai()) {
			khachHangCu = kh;
			lblKhachCuStatus.setText("✓ " + kh.getTenKhachHang());
			lblKhachCuStatus.setForeground(Colors.SUCCESS_DARK);
		} else {
			khachHangCu = null;
			lblKhachCuStatus.setText("✗ Không tìm thấy khách hàng");
			lblKhachCuStatus.setForeground(Colors.DANGER);
		}
		lblKhachCuStatus.setVisible(true);
	}

	private void renderProductTable() {
		productModel.setRowCount(0);
		for (SanPham sp : dsHienThi) {
			SanPham_Service.TonKhoInfo info = mapTonKho.getOrDefault(sp.getMaSanPham(),
					new SanPham_Service.TonKhoInfo(0, 0, 0));
			productModel.addRow(buildProductRow(sp, info));
		}
	}

	private Object[] buildProductRow(SanPham sp, SanPham_Service.TonKhoInfo info) {
		return new Object[] { new Object[] { sp.getMaSanPham(), sp.getTenSanPham() }, sp, info.tonKho, info.trangThai,
				sp.getMaSanPham() };
	}

	// =========================================================================
	// XÁC NHẬN
	// =========================================================================
	private void xuLyXacNhan() {
		if (nhanVien == null) {
			JOptionPane.showMessageDialog(this, "Không xác định được nhân viên lập phiếu đặt trước.", "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (cartItems.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Vui lòng thêm ít nhất 1 sản phẩm.", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		Date ngayNhan = dtcNgayNhan != null ? dtcNgayNhan.getDate() : null;
		if (ngayNhan == null) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày nhận hàng.", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		LocalDate ngayNhanLD = ngayNhan.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		if (ngayNhanLD.isBefore(LocalDate.now())) {
			JOptionPane.showMessageDialog(this, "Ngày nhận hàng phải là hôm nay hoặc trong tương lai.", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (!gioNhanHopLe()) {
			JOptionPane.showMessageDialog(this,
					"Giờ nhận phải nằm trong giờ hoạt động: Thứ 2-7 từ 08:00 đến 22:00, Chủ nhật từ 09:00 đến 20:00.",
					"Thông báo", JOptionPane.WARNING_MESSAGE);
			return;
		}

		String tenKH = null;
		String sdtKH = null;
		if (isOldCustomer) {
			if (khachHangCu == null) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập số điện thoại để tìm khách hàng.", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			tenKH = khachHangCu.getTenKhachHang();
			sdtKH = khachHangCu.getSoDienThoai();
		} else {
			tenKH = txtTenKhachMoi != null ? txtTenKhachMoi.getText().trim() : "";
			sdtKH = txtSDTKhachMoi != null ? txtSDTKhachMoi.getText().trim() : "";
			if (tenKH == null || tenKH.isBlank()) {
				JOptionPane.showMessageDialog(this, "Vui lòng nhập tên khách hàng.", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (sdtKH == null || !sdtKH.matches("\\d{10}")) {
				JOptionPane.showMessageDialog(this, "Số điện thoại phải có đúng 10 chữ số.", "Thông báo",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
		}

		List<HoaDon_Service.CartItem> items = new ArrayList<>();
		for (CartItem ci : cartItems) {
			SanPham sp = findSP(ci.maSP);
			if (sp != null && ci.qty > 0) {
				items.add(new HoaDon_Service.CartItem(sp, ci.qty, ci.gia, sp.getGiaThanh()));
			}
		}
		if (items.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Không có sản phẩm hợp lệ để tạo phiếu đặt trước.", "Thông báo",
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		String loiTonKho = hoaDonService.kiemTraTonKho(items);
		if (loiTonKho != null) {
			// Tồn kho chưa đủ — cho phép đặt trước, xác nhận sẽ bị chặn cho đến khi có đủ
			// hàng.
			int ok = JOptionPane.showConfirmDialog(this,
					loiTonKho + "\n\nĐơn đặt trước vẫn sẽ được tạo.\n"
							+ "Xác nhận nhận hàng chỉ thực hiện được sau khi nhập đủ hàng về kho.",
					"Cảnh báo tồn kho", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE);
			if (ok != JOptionPane.OK_OPTION) {
				return;
			}
		}

		// Chuẩn bị dữ liệu trước khi tạo phiếu
		String ngay = new SimpleDateFormat("dd/MM/yyyy").format(ngayNhan);
		String gio = cboGioNhan != null && cboGioNhan.getSelectedItem() != null
				? (String) cboGioNhan.getSelectedItem() : "--:--";
		double total = cartItems.stream().mapToDouble(CartItem::thanhTien).sum();
		double coc = total * depositPct / 100.0;
		String sCoc = depositPct == 0 ? "Không cọc" : PRICE_FMT.format(coc) + "đ (" + depositPct + "%)";
		String userNote = (txtGhiChu != null) ? txtGhiChu.getText().trim() : "";
		if ("Ghi chú (toa thuốc, yêu cầu đặc biệt...)".equals(userNote)) {
			userNote = "";
		}
		StringBuilder ghiChuSB = new StringBuilder("Dự kiến nhận: ").append(ngay).append(" ").append(gio)
				.append(" | Cọc: ").append(sCoc);
		if (!userNote.isEmpty()) {
			ghiChuSB.append(" | Ghi chú: ").append(userNote);
		}

		String maHD = hoaDonService.sinhMaHoaDon();
		HoaDon hdDat;
		try {
			hdDat = hoaDonService.taoHoaDonChoThanhToan(maHD, tenKH, sdtKH, nhanVien, items, null, 0,
					ghiChuSB.toString());
		} catch (RuntimeException ex) {
			JOptionPane.showMessageDialog(this, "Không thể tạo phiếu đặt trước: " + ex.getMessage(), "Lỗi",
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		HoaDon_GUI.stockDirty = true;
		Window owner = SwingUtilities.getWindowAncestor(this);
		new SuccessDialog(owner, hdDat.getMaHoaDon(), tenKH, sdtKH, ngay, gio, total, sCoc).setVisible(true);
		resetSauDatTruoc();
	}

	private void resetSauDatTruoc() {
		cartItems.clear();
		refreshCart();
		if (txtSDTKhachCu != null) {
			txtSDTKhachCu.setText("");
		}
		if (lblKhachCuStatus != null) {
			lblKhachCuStatus.setVisible(false);
		}
		khachHangCu = null;
		if (txtTenKhachMoi != null) {
			txtTenKhachMoi.setText("");
		}
		if (txtSDTKhachMoi != null) {
			txtSDTKhachMoi.setText("");
		}
		depositPct = 0;
		if (btnDeposit0 != null) {
			btnDeposit0.setSelected(true);
		}
		if (txtGhiChu != null) {
			txtGhiChu.setText("Ghi chú (toa thuốc, yêu cầu đặc biệt...)");
			txtGhiChu.setForeground(Colors.MUTED);
		}
		rdoKhachCu.setSelected(true);
		isOldCustomer = true;
		switchCustomerPanel();
		if (dtcNgayNhan != null) {
			dtcNgayNhan.setDate(new Date());
		}
		capNhatKhungGioNhan(); // tự chọn giờ gần nhất khi reset
	}

	// =========================================================================
	// INNER — PRODUCT TABLE COLUMN RENDERERS
	// =========================================================================
	/**
	 * Col 0: Tên + mã SP (no image)
	 */
	@SuppressWarnings("serial")
	private class ProductNameRdr extends JPanel implements TableCellRenderer {

		ProductNameRdr() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row,
				int col) {
			removeAll();
			setBackground(sel ? Colors.PRIMARY_LIGHT : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
			setBorder(
					BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
							BorderFactory.createEmptyBorder(8, 12, 8, 10)));
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			if (val instanceof Object[]) {
				Object[] d = (Object[]) val;
				String maSP = d.length > 0 && d[0] != null ? d[0].toString() : "";
				String tenSP = d.length > 1 && d[1] != null ? d[1].toString() : "";
				JLabel lMa = new JLabel(maSP);
				lMa.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
				lMa.setForeground(Colors.MUTED);
				if (tenSP.length() > 32) {
					tenSP = tenSP.substring(0, 32) + "…";
				}
				JLabel lTen = new JLabel(tenSP);
				lTen.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
				lTen.setForeground(Colors.TEXT_PRIMARY);
				add(lMa);
				add(Box.createVerticalStrut(2));
				add(lTen);
			}
			return this;
		}
	}

	/**
	 * Col 1: Giá bán (́+ gạch nếu có KM)
	 */
	@SuppressWarnings("serial")
	private class PriceRdr extends JPanel implements TableCellRenderer {

		PriceRdr() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row,
				int col) {
			removeAll();
			setBackground(sel ? Colors.PRIMARY_LIGHT : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
			setBorder(
					BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
							BorderFactory.createEmptyBorder(8, 10, 8, 8)));
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			if (val instanceof SanPham) {
				SanPham sp = (SanPham) val;
				double giaSale = sp.coKhuyenMai() ? sp.getGiaSauKM() : sp.getGiaThanh();
				JLabel lSale = new JLabel(PRICE_FMT.format(giaSale) + "đ");
				lSale.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
				lSale.setForeground(Colors.ACCENT);
				lSale.setAlignmentX(Component.LEFT_ALIGNMENT);
				add(lSale);
				if (sp.coKhuyenMai()) {
					JLabel lGoc = new JLabel("<html><s>" + PRICE_FMT.format(sp.getGiaThanh()) + "đ</s></html>");
					lGoc.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
					lGoc.setForeground(Colors.MUTED);
					lGoc.setAlignmentX(Component.LEFT_ALIGNMENT);
					add(lGoc);
				}
			}
			return this;
		}
	}

	/**
	 * Col 2: Tồn kho
	 */
	@SuppressWarnings("serial")
	private class StockRdr extends JPanel implements TableCellRenderer {

		StockRdr() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row,
				int col) {
			removeAll();
			setBackground(sel ? Colors.PRIMARY_LIGHT : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));
			setLayout(new FlowLayout(FlowLayout.LEFT, 10, 18));
			if (val instanceof Number) {
				int ton = ((Number) val).intValue();
				JLabel l = new JLabel(String.valueOf(ton));
				l.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
				l.setForeground(ton > 0 ? Colors.TEXT_PRIMARY : Colors.DANGER);
				add(l);
			}
			return this;
		}
	}

	/**
	 * Col 3: Trạng thái badge
	 */
	@SuppressWarnings("serial")
	private class StatusRdr extends JPanel implements TableCellRenderer {

		StatusRdr() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row,
				int col) {
			removeAll();
			setBackground(sel ? Colors.PRIMARY_LIGHT : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));
			setLayout(new FlowLayout(FlowLayout.LEFT, 10, 17));
			String tt = val != null ? val.toString() : "HET_HANG";
			String text;
			Color bg, fg;
			if ("CON_HANG".equals(tt)) {
				text = "Còn hàng";
				bg = Colors.GREEN_HOVER;
				fg = Colors.SUCCESS_DARK;
			} else if ("SAP_HET".equals(tt)) {
				text = "Sắp hết";
				bg = Colors.YELLOW_HOVER;
				fg = Colors.ACCENT;
			} else {
				text = "Hết hàng";
				bg = Colors.BROWN_HOVER;
				fg = Colors.DANGER;
			}
			JLabel badge = new JLabel(text);
			badge.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
			badge.setForeground(fg);
			badge.setOpaque(true);
			badge.setBackground(bg);
			badge.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
			add(badge);
			return this;
		}
	}

	// =========================================================================
	// INNER — PRODUCT TABLE ACTION COLUMN
	// =========================================================================
	@SuppressWarnings("serial")
	private class ProductActionRenderer extends JPanel implements TableCellRenderer {

		private final JButton btnAdd = actionBtn("+ Thêm", Colors.PRIMARY, Colors.BACKGROUND);
		private final JButton btnRem = actionBtn("Bỏ chọn", Colors.SECONDARY, Colors.DANGER);

		ProductActionRenderer() {
			setLayout(new FlowLayout(FlowLayout.CENTER, 4, (ACTION_ROW_H - 30) / 2));
			setOpaque(true);
			add(btnAdd);
			add(btnRem);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row,
				int col) {
			setBackground(sel ? Colors.PRIMARY_LIGHT : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));
			boolean in = isInCart(val != null ? val.toString() : "");
			btnAdd.setVisible(!in);
			btnRem.setVisible(in);
			return this;
		}
	}

	@SuppressWarnings("serial")
	private class ProductActionEditor extends AbstractCellEditor implements TableCellEditor {

		private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, (ACTION_ROW_H - 30) / 2));
		private final JButton btnAdd = actionBtn("+ Thêm", Colors.PRIMARY, Colors.BACKGROUND);
		private final JButton btnRem = actionBtn("Bỏ chọn", Colors.SECONDARY, Colors.DANGER);
		private String maSP = "";

		ProductActionEditor() {
			panel.setOpaque(true);
			panel.add(btnAdd);
			panel.add(btnRem);
			btnAdd.addActionListener(e -> {
				fireEditingStopped();
				SanPham sp = findSP(maSP);
				if (sp != null) {
					addToCart(sp);
				}
			});
			btnRem.addActionListener(e -> {
				fireEditingStopped();
				removeFromCartByMa(maSP);
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable t, Object val, boolean sel, int row, int col) {
			maSP = val != null ? val.toString() : "";
			boolean in = isInCart(maSP);
			panel.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
			btnAdd.setVisible(!in);
			btnRem.setVisible(in);
			return panel;
		}

		@Override
		public Object getCellEditorValue() {
			return maSP;
		}
	}

	// =========================================================================
	// INNER — CART TABLE RENDERERS
	// =========================================================================
	/**
	 * Col 0: Tên sản phẩm + đơn giá
	 */
	@SuppressWarnings("serial")
	private class CartProductRdr extends JPanel implements TableCellRenderer {

		CartProductRdr() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row,
				int col) {
			removeAll();
			setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
			setBorder(
					BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
							BorderFactory.createEmptyBorder(6, 10, 6, 4)));
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			if (val instanceof CartItem) {
				CartItem ci = (CartItem) val;
				String ten = ci.tenSP.length() > 24 ? ci.tenSP.substring(0, 24) + "…" : ci.tenSP;
				JLabel lTen = new JLabel(ten);
				lTen.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
				lTen.setForeground(Colors.TEXT_PRIMARY);
				JLabel lGia = new JLabel(PRICE_FMT.format(ci.gia) + "đ/" + ci.donVi);
				lGia.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
				lGia.setForeground(Colors.MUTED);
				add(lTen);
				add(lGia);
			}
			return this;
		}
	}

	/**
	 * Col 1: Số lượng (render only)
	 */
	@SuppressWarnings("serial")
	private class CartQtyRdr extends JPanel implements TableCellRenderer {

		CartQtyRdr() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row,
				int col) {
			removeAll();
			setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));
			setLayout(new FlowLayout(FlowLayout.CENTER, 2, (CART_ROW_H - 24) / 2));
			if (val instanceof CartItem) {
				CartItem ci = (CartItem) val;
				JLabel m = qtyLabel("−");
				JLabel qty = new JLabel(String.valueOf(ci.qty), SwingConstants.CENTER);
				qty.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
				qty.setPreferredSize(new Dimension(28, 22));
				JLabel pls = qtyLabel("+");
				add(m);
				add(qty);
				add(pls);
			}
			return this;
		}

		private JLabel qtyLabel(String txt) {
			JLabel l = new JLabel(txt, SwingConstants.CENTER);
			l.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
			l.setForeground(Colors.TEXT_SECONDARY);
			l.setOpaque(true);
			l.setBackground(Colors.SECONDARY);
			l.setPreferredSize(new Dimension(22, 22));
			return l;
		}
	}

	/**
	 * Col 2: Thành tiền
	 */
	@SuppressWarnings("serial")
	private class CartTotalRdr extends JPanel implements TableCellRenderer {

		CartTotalRdr() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row,
				int col) {
			removeAll();
			setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
			setBorder(
					BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
							BorderFactory.createEmptyBorder(0, 4, 0, 8)));
			setLayout(new FlowLayout(FlowLayout.RIGHT, 0, (CART_ROW_H - 18) / 2));
			if (val instanceof CartItem) {
				JLabel l = new JLabel(PRICE_FMT.format(((CartItem) val).thanhTien()) + "đ");
				l.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
				l.setForeground(Colors.SUCCESS_DARK);
				add(l);
			}
			return this;
		}
	}

	/**
	 * Col 3: Xóa
	 */
	@SuppressWarnings("serial")
	private class CartDelRdr extends JPanel implements TableCellRenderer {

		CartDelRdr() {
			setOpaque(true);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object val, boolean sel, boolean focus, int row,
				int col) {
			removeAll();
			setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
			setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));
			setLayout(new FlowLayout(FlowLayout.CENTER, 0, (CART_ROW_H - 18) / 2));
			JLabel ic = new JLabel("✕");
			ic.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
			ic.setForeground(Colors.MUTED);
			add(ic);
			return this;
		}
	}

	// =========================================================================
	// INNER — CART TABLE EDITORS
	// =========================================================================
	/**
	 * Col 1: +/- quantity editor
	 */
	@SuppressWarnings("serial")
	private class CartQtyEditor extends AbstractCellEditor implements TableCellEditor {

		private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, (CART_ROW_H - 24) / 2));
		private final JButton btnM = smallEditBtn("−");
		private final JLabel lblQ = new JLabel("1", SwingConstants.CENTER);
		private final JButton btnP = smallEditBtn("+");
		private int currentRow = -1;

		CartQtyEditor() {
			panel.setOpaque(true);
			lblQ.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
			lblQ.setPreferredSize(new Dimension(28, 22));
			panel.add(btnM);
			panel.add(lblQ);
			panel.add(btnP);

			btnM.addActionListener(e -> {
				if (currentRow < 0 || currentRow >= cartItems.size()) {
					return;
				}
				CartItem ci = cartItems.get(currentRow);
				if (ci.qty > 1) {
					ci.qty--;
					lblQ.setText(String.valueOf(ci.qty));
					refreshSummary();
				}
			});
			btnP.addActionListener(e -> {
				if (currentRow < 0 || currentRow >= cartItems.size()) {
					return;
				}
				CartItem ci = cartItems.get(currentRow);
				ci.qty++;
				lblQ.setText(String.valueOf(ci.qty));
				refreshSummary();
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable t, Object val, boolean sel, int row, int col) {
			currentRow = row;
			panel.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
			if (val instanceof CartItem) {
				lblQ.setText(String.valueOf(((CartItem) val).qty));
			}
			return panel;
		}

		@Override
		public Object getCellEditorValue() {
			return (currentRow >= 0 && currentRow < cartItems.size()) ? cartItems.get(currentRow) : null;
		}
	}

	/**
	 * Col 3: Delete editor
	 */
	@SuppressWarnings("serial")
	private class CartDelEditor extends AbstractCellEditor implements TableCellEditor {

		private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, (CART_ROW_H - 22) / 2));
		private final JButton btnDel = new JButton("✕");
		private int currentRow = -1;

		CartDelEditor() {
			panel.setOpaque(true);
			btnDel.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
			btnDel.setForeground(Colors.DANGER);
			btnDel.setBorderPainted(false);
			btnDel.setContentAreaFilled(false);
			btnDel.setFocusPainted(false);
			btnDel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			panel.add(btnDel);
			btnDel.addActionListener(e -> {
				fireEditingStopped();
				removeFromCart(currentRow);
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable t, Object val, boolean sel, int row, int col) {
			currentRow = row;
			panel.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
			return panel;
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}
	}

	// =========================================================================
	// UTILITIES
	// =========================================================================
	/**
	 * Refresh summary labels without rebuilding table rows (used by quantity
	 * editor).
	 */
	private void refreshSummary() {
		int totalQty = cartItems.stream().mapToInt(ci -> ci.qty).sum();
		double total = cartItems.stream().mapToDouble(CartItem::thanhTien).sum();
		lblCartCount.setText(cartItems.size() + " SP");
		lblTongSP.setText(cartItems.size() + " sản phẩm");
		lblTongDonVi.setText(totalQty + " đơn vị");
		lblTongTien.setText(PRICE_FMT.format(total) + "đ");
		tblCart.repaint();
	}

	private SanPham findSP(String maSP) {
		for (SanPham sp : dsGoc) {
			if (sp.getMaSanPham().equals(maSP)) {
				return sp;
			}
		}
		return null;
	}

	private static Component vgap(int h) {
		return Box.createVerticalStrut(h);
	}

	private static JLabel mkLabel(String text, boolean bold) {
		JLabel l = new JLabel(text);
		l.setFont(FontStyle.font(bold ? FontStyle.SM : FontStyle.XS, bold ? FontStyle.BOLD : FontStyle.NORMAL));
		l.setForeground(Colors.MUTED);
		return l;
	}

	private JPanel sectionPanel(String title) {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBackground(Colors.BACKGROUND);
		p.setAlignmentX(LEFT_ALIGNMENT);
		p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1),
				BorderFactory.createEmptyBorder(8, 12, 10, 12)));
		JLabel l = new JLabel(title);
		l.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		l.setForeground(Colors.TEXT_SECONDARY);
		l.setAlignmentX(LEFT_ALIGNMENT);
		p.add(l);
		p.add(vgap(6));
		return p;
	}

	private JPanel labeledCol(String label) {
		JPanel col = new JPanel();
		col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
		col.setOpaque(false);
		JLabel l = new JLabel(label);
		l.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		l.setForeground(Colors.TEXT_SECONDARY);
		l.setAlignmentX(LEFT_ALIGNMENT);
		col.add(l);
		col.add(vgap(4));
		return col;
	}

	private static JButton actionBtn(String text, Color bg, Color fg) {
		JButton b = new JButton(text);
		b.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		b.setForeground(fg);
		b.setBackground(bg);
		b.setOpaque(true);
		b.setFocusPainted(false);
		b.setBorderPainted(false);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		b.setPreferredSize(new Dimension(90, 30));
		return b;
	}

	private static JButton smallEditBtn(String text) {
		JButton b = new JButton(text);
		b.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		b.setBackground(Colors.SECONDARY);
		b.setForeground(Colors.TEXT_PRIMARY);
		b.setOpaque(true);
		b.setBorderPainted(false);
		b.setFocusPainted(false);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		b.setPreferredSize(new Dimension(24, 22));
		b.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				b.setBackground(Colors.BORDER_LIGHT);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				b.setBackground(Colors.SECONDARY);
			}
		});
		return b;
	}

	private static void styleMenuItem(JMenuItem item) {
		item.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		item.setForeground(Colors.TEXT_PRIMARY);
		item.setBackground(Colors.BACKGROUND);
		item.setBorder(BorderFactory.createEmptyBorder(7, 14, 7, 28));
		item.setCursor(new Cursor(Cursor.HAND_CURSOR));
		item.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				item.setBackground(Colors.PRIMARY_LIGHT);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				item.setBackground(Colors.BACKGROUND);
			}
		});
	}

	// =========================================================================
	// INNER — SuccessDialog: hiển thị xác nhận đặt trước thành công
	// =========================================================================
	@SuppressWarnings("serial")
	private static final class SuccessDialog extends JDialog {

		SuccessDialog(Window parent, String maHD, String tenKH, String sdt,
				String ngay, String gio, double total, String sCoc) {
			super(parent, "Đặt trước thành công", ModalityType.APPLICATION_MODAL);
			setLayout(new BorderLayout());
			getContentPane().setBackground(Color.WHITE);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			add(buildHeader(), BorderLayout.NORTH);
			add(buildBody(maHD, tenKH, sdt, ngay, gio, sCoc), BorderLayout.CENTER);
			add(buildSummaryFooter(total), BorderLayout.SOUTH);
			setSize(640, 440);
			setResizable(false);
			setLocationRelativeTo(parent);
		}

		private JPanel buildHeader() {
			JPanel p = new JPanel(new BorderLayout());
			p.setBackground(Colors.SUCCESS);
			p.setBorder(BorderFactory.createEmptyBorder(18, 22, 18, 22));

			JPanel txt = new JPanel();
			txt.setOpaque(false);
			txt.setLayout(new BoxLayout(txt, BoxLayout.Y_AXIS));
			JLabel lblTitle = new JLabel("Đặt trước thành công!");
			lblTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
			lblTitle.setForeground(Color.WHITE);
			JLabel lblSub = new JLabel("Phiếu đặt đang chờ khách hàng đến nhận và thanh toán.");
			lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
			lblSub.setForeground(new Color(209, 250, 229));
			txt.add(lblTitle);
			txt.add(Box.createVerticalStrut(3));
			txt.add(lblSub);

			p.add(txt, BorderLayout.WEST);
			return p;
		}

		private JPanel buildBody(String maHD, String tenKH, String sdt,
				String ngay, String gio, String sCoc) {
			JPanel body = new JPanel(new BorderLayout());
			body.setBackground(Color.WHITE);
			body.setBorder(BorderFactory.createEmptyBorder(16, 20, 8, 20));

			JPanel cardsRow = new JPanel(new GridLayout(1, 2, 12, 0));
			cardsRow.setOpaque(false);
			cardsRow.add(infoCard("Khách hàng", new String[][]{
					{"Tên khách", tenKH != null && !tenKH.isBlank() ? tenKH : "Khách lẻ"},
					{"Số điện thoại", sdt != null && !sdt.isBlank() ? sdt : "—"}
			}));
			cardsRow.add(infoCard("Phiếu đặt", new String[][]{
					{"Mã phiếu", maHD},
					{"Ngày nhận", ngay},
					{"Giờ nhận", gio},
					{"Đặt cọc", sCoc},
					{"Trạng thái", HoaDon.TRANG_THAI_CHO_THANH_TOAN}
			}));
			body.add(cardsRow, BorderLayout.CENTER);
			return body;
		}

		private JPanel infoCard(String title, String[][] rows) {
			JPanel card = new JPanel() {
				@Override
				protected void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setColor(new Color(245, 247, 250));
					g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
					g2.dispose();
				}
			};
			card.setOpaque(false);
			card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
			card.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

			JLabel lbl = new JLabel(title);
			lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
			lbl.setForeground(Colors.TEXT_PRIMARY);
			lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
			card.add(lbl);
			card.add(Box.createVerticalStrut(8));

			JSeparator sep = new JSeparator();
			sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
			sep.setForeground(Colors.BORDER_LIGHT);
			sep.setAlignmentX(Component.LEFT_ALIGNMENT);
			card.add(sep);
			card.add(Box.createVerticalStrut(6));

			for (String[] r : rows) {
				JPanel rowP = new JPanel(new BorderLayout(8, 0));
				rowP.setOpaque(false);
				rowP.setAlignmentX(Component.LEFT_ALIGNMENT);
				rowP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 24));
				JLabel k = new JLabel(r[0] + ":");
				k.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
				k.setForeground(Colors.TEXT_SECONDARY);
				k.setPreferredSize(new Dimension(88, 22));
				JLabel v = new JLabel(r[1] != null ? r[1] : "—");
				v.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
				v.setForeground(Colors.TEXT_PRIMARY);
				rowP.add(k, BorderLayout.WEST);
				rowP.add(v, BorderLayout.CENTER);
				card.add(rowP);
				card.add(Box.createVerticalStrut(2));
			}
			return card;
		}

		private JPanel buildSummaryFooter(double total) {
			JPanel outer = new JPanel(new BorderLayout());
			outer.setBackground(Color.WHITE);
			outer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT));

			JPanel sum = new JPanel();
			sum.setLayout(new BoxLayout(sum, BoxLayout.Y_AXIS));
			sum.setBackground(new Color(248, 250, 252));
			sum.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
			addSumRow(sum, "Tổng cộng", PRICE_FMT.format((long) total) + " đ", Colors.TEXT_PRIMARY, false);
			addSumRow(sum, "THÀNH TIỀN", PRICE_FMT.format((long) total) + " đ", Colors.SUCCESS_DARK, true);

			JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
			btnPanel.setBackground(Color.WHITE);
			RoundedButton btn = new RoundedButton(140, 38, 8, "Đóng", Colors.SUCCESS);
			btn.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
			btn.setForeground(Color.WHITE);
			btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			btn.addActionListener(e -> dispose());
			btnPanel.add(btn);

			outer.add(sum, BorderLayout.NORTH);
			outer.add(btnPanel, BorderLayout.CENTER);
			return outer;
		}

		private static void addSumRow(JPanel p, String label, String value, Color color, boolean bold) {
			JPanel row = new JPanel(new BorderLayout(8, 0));
			row.setOpaque(false);
			row.setAlignmentX(Component.LEFT_ALIGNMENT);
			row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
			JLabel k = new JLabel(label);
			k.setFont(bold ? FontStyle.font(FontStyle.SM, FontStyle.BOLD)
					: FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
			k.setForeground(bold ? Colors.TEXT_PRIMARY : Colors.TEXT_SECONDARY);
			JLabel v = new JLabel(value, SwingConstants.RIGHT);
			v.setFont(bold ? FontStyle.font(FontStyle.BASE, FontStyle.BOLD)
					: FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
			v.setForeground(color);
			row.add(k, BorderLayout.WEST);
			row.add(v, BorderLayout.EAST);
			p.add(row);
			p.add(Box.createVerticalStrut(3));
		}
	}

}
