package gui;

import constants.Colors;
import constants.FontStyle;
import entity.KhachHang;
import entity.LoSanPham;
import entity.SanPham;
import exception.ProductTableRenderer;
import exception.RoundedButton;
import exception.RoundedComboBox;
import exception.RoundedTextField;
import exception.RoundedToggleButton;
import service.KhachHang_Service;
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

public class DatTruoc_GUI extends JPanel {

    // ==================== CONSTANTS ====================
    private static final DecimalFormat PRICE_FMT = new DecimalFormat("#,###");
    private static final int ACTION_ROW_H = 56;
    private static final int CART_ROW_H = 44;
    private static final LocalTime GIO_MO_CUA_THU_2_7 = LocalTime.of(8, 0);
    private static final LocalTime GIO_DONG_CUA_THU_2_7 = LocalTime.of(22, 0);
    private static final LocalTime GIO_MO_CUA_CHU_NHAT = LocalTime.of(9, 0);
    private static final LocalTime GIO_DONG_CUA_CHU_NHAT = LocalTime.of(20, 0);

    private static final String[] PRODUCT_COLS = {
        "Sản phẩm", "Giá bán", "Tồn kho", "Trạng thái", "Thao tác"
    };
    private static final String[] CART_COLS = {"Sản phẩm", "SL", "Thành tiền", ""};

    // ==================== SERVICES ====================
    private final SanPham_Service spService = new SanPham_Service();
    private final KhachHang_Service khService = new KhachHang_Service();
    private final LoSanPham_Service loService = new LoSanPham_Service();

    // ==================== DATA ====================
    private List<SanPham> dsGoc = new ArrayList<>();
    private List<SanPham> dsHienThi = new ArrayList<>();
    private Map<String, SanPham_Service.TonKhoInfo> mapTonKho = new HashMap<>();
    private Map<String, String> mapNgayGanNhat = new HashMap<>();

    private final List<CartItem> cartItems = new ArrayList<>();
    private int depositPct = 0;
    private boolean isOldCustomer = true;

    // ==================== WIDGETS ====================
    private RoundedTextField txtSearch;
    private DefaultTableModel productModel;
    private JTable tblProduct;
    private JDateChooser dtcNgayNhan;
    private JSpinner spnGioNhan;
    private RoundedToggleButton btnKhachCu, btnKhachMoi;
    private JPanel pnlKhachCu, pnlKhachMoi;
    @SuppressWarnings("rawtypes")
    private JComboBox cmbKhachHang;
    private DefaultTableModel cartModel;
    private JTable tblCart;
    private JLabel lblCartCount, lblTongSP, lblTongDonVi, lblTongTien;
    private RoundedButton btnXacNhan;
    private JLabel lblHint;

    // ==================== CART ITEM ====================
    private static final class CartItem {

        final String maSP, tenSP, donVi;
        final double gia;
        int qty;

        CartItem(SanPham sp) {
            maSP = sp.getMaSanPham();
            tenSP = sp.getTenSanPham();
            donVi = (sp.getDonViTinh() != null && !sp.getDonViTinh().isEmpty())
                    ? sp.getDonViTinh() : "Hộp";
            gia = sp.coKhuyenMai() ? sp.getGiaSauKM() : sp.getGiaThanh();
            qty = 1;
        }

        double thanhTien() {
            return gia * qty;
        }
    }

    // ==================== CONSTRUCTOR ====================
    public DatTruoc_GUI() {
        setLayout(new BorderLayout(14, 0));
        setBackground(Colors.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 20));

        add(buildLeftPanel(), BorderLayout.CENTER);
        JPanel right = buildRightPanel();
        right.setPreferredSize(new Dimension(500, 0));
        add(right, BorderLayout.EAST);

        loadDataBackground();
    }

    // =========================================================================
    // LEFT PANEL
    // =========================================================================
    private JPanel buildLeftPanel() {
        JPanel p = new JPanel(new BorderLayout(0, 12));
        p.setOpaque(false);

        JPanel hdr = new JPanel();
        hdr.setLayout(new BoxLayout(hdr, BoxLayout.Y_AXIS));
        hdr.setOpaque(false);

        JLabel lblTitle = new JLabel("Đặt Trước Thuốc");
        lblTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.TEXT_PRIMARY);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Chọn sản phẩm và điền thông tin để tạo phiếu");
        lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSub.setForeground(Colors.TEXT_SECONDARY);
        lblSub.setAlignmentX(LEFT_ALIGNMENT);

        hdr.add(lblTitle);
        hdr.add(Box.createVerticalStrut(2));
        hdr.add(lblSub);
        hdr.add(Box.createVerticalStrut(10));

        txtSearch = new RoundedTextField(370, 42, 12, "Tìm thuốc để đặt trước...");
        txtSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        txtSearch.setAlignmentX(LEFT_ALIGNMENT);
        txtSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterAndDisplay();
            }
        });
        JPanel sw = new JPanel(new BorderLayout());
        sw.setOpaque(false);
        sw.add(txtSearch, BorderLayout.CENTER);
        hdr.add(sw);
        p.add(hdr, BorderLayout.NORTH);

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
        int[] colW = {180, 110, 65, 110, 120};
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

    // =========================================================================
    // RIGHT PANEL
    // =========================================================================
    private JPanel buildRightPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(Colors.SECONDARY);
        outer.setBorder(BorderFactory.createMatteBorder(0, 1, 0, 0, Colors.BORDER_LIGHT));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(Colors.SECONDARY);
        content.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        content.add(buildSectionThoiGian());
        content.add(vgap(10));
        content.add(buildSectionKhachHang());
        content.add(vgap(10));
        content.add(buildSectionDatCoc());
        content.add(vgap(10));
        content.add(buildSectionGhiChu());
        content.add(vgap(10));
        content.add(buildSectionCart());
        content.add(vgap(16));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.getViewport().setBackground(Colors.SECONDARY);
        outer.add(scroll, BorderLayout.CENTER);

        // Sticky confirm bar
        JPanel bar = new JPanel();
        bar.setLayout(new BoxLayout(bar, BoxLayout.Y_AXIS));
        bar.setBackground(Colors.BACKGROUND);
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(12, 14, 12, 14)
        ));

        btnXacNhan = new RoundedButton(300, 46, 12, "Xác nhận đặt trước", Colors.PRIMARY);
        btnXacNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        btnXacNhan.setAlignmentX(LEFT_ALIGNMENT);
        btnXacNhan.setEnabled(false);
        btnXacNhan.addActionListener(e -> xuLyXacNhan());
        bar.add(btnXacNhan);
        bar.add(vgap(5));

        lblHint = new JLabel("Vui lòng chọn ngày nhận hàng");
        lblHint.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lblHint.setForeground(Colors.MUTED);
        lblHint.setAlignmentX(LEFT_ALIGNMENT);
        bar.add(lblHint);

        outer.add(bar, BorderLayout.SOUTH);
        return outer;
    }

    // ── Thời gian nhận hàng ──────────────────────────────────────────────────
    private JPanel buildSectionThoiGian() {
        JPanel p = sectionPanel("THỜI GIAN NHẬN HÀNG");

        JPanel row = new JPanel(new GridLayout(1, 2, 10, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 68));

        JPanel colNgay = labeledCol("Ngày nhận *");
        dtcNgayNhan = new JDateChooser();
        dtcNgayNhan.setDateFormatString("dd/MM/yyyy");
        dtcNgayNhan.setAlignmentX(LEFT_ALIGNMENT);
        dtcNgayNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        dtcNgayNhan.setPreferredSize(new Dimension(0, 38));
        dtcNgayNhan.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        dtcNgayNhan.setDate(new Date());
        JComponent dateEditor = dtcNgayNhan.getDateEditor().getUiComponent() instanceof JComponent
                ? (JComponent) dtcNgayNhan.getDateEditor().getUiComponent()
                : null;
        if (dateEditor instanceof JTextField) {
            JTextField dateText = (JTextField) dateEditor;
            dateText.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            dateText.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Colors.BORDER, 1),
                    BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        }
        dtcNgayNhan.addPropertyChangeListener("date", e -> {
            capNhatKhungGioNhan();
            updateConfirmButton();
        });
        colNgay.add(dtcNgayNhan);

        JPanel colGio = labeledCol("Giờ nhận");
        SpinnerDateModel gioNhanModel = new SpinnerDateModel();
        gioNhanModel.setCalendarField(Calendar.MINUTE);
        spnGioNhan = new JSpinner(gioNhanModel);
        spnGioNhan.setAlignmentX(LEFT_ALIGNMENT);
        spnGioNhan.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        spnGioNhan.setPreferredSize(new Dimension(0, 38));
        spnGioNhan.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        JSpinner.DateEditor gioEditor = new JSpinner.DateEditor(spnGioNhan, "HH:mm");
        spnGioNhan.setEditor(gioEditor);
        gioEditor.getTextField().setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        gioEditor.getTextField().setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                BorderFactory.createEmptyBorder(4, 10, 4, 10)));
        spnGioNhan.addChangeListener(e -> capNhatKhungGioNhan());
        spnGioNhan.setValue(java.util.Date.from(LocalTime.of(9, 0)
                .atDate(LocalDate.now())
                .atZone(ZoneId.systemDefault())
                .toInstant()));
        capNhatKhungGioNhan();
        colGio.add(spnGioNhan);

        row.add(colNgay);
        row.add(colGio);
        p.add(row);
        return p;
    }

    // ── Thông tin khách hàng ─────────────────────────────────────────────────
    @SuppressWarnings({"unchecked", "rawtypes"})
    private JPanel buildSectionKhachHang() {
        JPanel p = sectionPanel("THÔNG TIN KHÁCH HÀNG");

        JPanel toggle = new JPanel(new GridLayout(1, 2, 0, 0));
        toggle.setOpaque(false);
        toggle.setAlignmentX(LEFT_ALIGNMENT);
        toggle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        toggle.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1));

        btnKhachCu = new RoundedToggleButton(0, 38, 6, "Khách cũ", Colors.PRIMARY);
        btnKhachMoi = new RoundedToggleButton(0, 38, 6, "Khách mới", Colors.PRIMARY);
        btnKhachCu.setSelected(true);
        ButtonGroup bg = new ButtonGroup();
        bg.add(btnKhachCu);
        bg.add(btnKhachMoi);
        btnKhachCu.addActionListener(e -> {
            isOldCustomer = true;
            switchCustomerPanel();
        });
        btnKhachMoi.addActionListener(e -> {
            isOldCustomer = false;
            switchCustomerPanel();
        });
        toggle.add(btnKhachCu);
        toggle.add(btnKhachMoi);
        p.add(toggle);
        p.add(vgap(8));

        pnlKhachCu = new JPanel(new BorderLayout());
        pnlKhachCu.setOpaque(false);
        pnlKhachCu.setAlignmentX(LEFT_ALIGNMENT);
        pnlKhachCu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        cmbKhachHang = new RoundedComboBox(10);
        cmbKhachHang.addItem(null);
        cmbKhachHang.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(
                        list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    lbl.setText("\u2014\u2014 Chọn khách hàng \u2014\u2014");
                    lbl.setForeground(Colors.MUTED);
                } else if (value instanceof KhachHang) {
                    KhachHang kh = (KhachHang) value;
                    lbl.setText(kh.getTenKhachHang() + "  ·  " + kh.getSoDienThoai());
                    lbl.setForeground(Colors.TEXT_PRIMARY);
                }
                return lbl;
            }
        });
        cmbKhachHang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        pnlKhachCu.add(cmbKhachHang, BorderLayout.CENTER);

        pnlKhachMoi = new JPanel();
        pnlKhachMoi.setLayout(new BoxLayout(pnlKhachMoi, BoxLayout.Y_AXIS));
        pnlKhachMoi.setOpaque(false);
        pnlKhachMoi.setAlignmentX(LEFT_ALIGNMENT);

        RoundedTextField txtTen = new RoundedTextField(0, 36, 10, "Họ và tên khách hàng");
        txtTen.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtTen.setAlignmentX(LEFT_ALIGNMENT);
        RoundedTextField txtSDT = new RoundedTextField(0, 36, 10, "Số điện thoại (10 chữ số)");
        txtSDT.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        txtSDT.setAlignmentX(LEFT_ALIGNMENT);

        pnlKhachMoi.add(txtTen);
        pnlKhachMoi.add(vgap(6));
        pnlKhachMoi.add(txtSDT);
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
        btns.setOpaque(false);
        btns.setAlignmentX(LEFT_ALIGNMENT);
        btns.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));

        String[] labels = {"0%", "20%", "30%", "50%", "100%"};
        int[] values = {0, 20, 30, 50, 100};
        ButtonGroup bgD = new ButtonGroup();

        for (int i = 0; i < labels.length; i++) {
            final int val = values[i];
            RoundedToggleButton btn
                    = new RoundedToggleButton(0, 36, 16, labels[i], Colors.PRIMARY);
            btn.setSelected(val == 0);
            bgD.add(btn);
            btn.addActionListener(e -> depositPct = val);
            btns.add(btn);
        }
        p.add(btns);
        return p;
    }

    // ── Ghi chú chung ────────────────────────────────────────────────────────
    private JPanel buildSectionGhiChu() {
        JPanel p = sectionPanel("GHI CHÚ CHUNG");

        JTextArea txt = new JTextArea(3, 0);
        txt.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        txt.setLineWrap(true);
        txt.setWrapStyleWord(true);
        txt.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        txt.setText("Ghi chú (toa thuốc, yêu cầu đặc biệt...)");
        txt.setForeground(Colors.MUTED);
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if ("Ghi chú (toa thuốc, yêu cầu đặc biệt...)".equals(txt.getText())) {
                    txt.setText("");
                    txt.setForeground(Colors.TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txt.getText().trim().isEmpty()) {
                    txt.setText("Ghi chú (toa thuốc, yêu cầu đặc biệt...)");
                    txt.setForeground(Colors.MUTED);
                }
            }
        });

        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Colors.BACKGROUND);
        box.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1));
        box.add(txt, BorderLayout.CENTER);
        box.setAlignmentX(LEFT_ALIGNMENT);
        box.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        p.add(box);
        return p;
    }

    // ── Chi tiết thuốc đặt — JTable ──────────────────────────────────────────
    private JPanel buildSectionCart() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Colors.BACKGROUND);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(12, 14, 14, 14)
        ));

        // Header
        JPanel hdr = new JPanel(new BorderLayout(6, 0));
        hdr.setOpaque(false);
        hdr.setAlignmentX(LEFT_ALIGNMENT);
        hdr.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));

        JLabel lblT = new JLabel("Chi tiết thuốc đặt");
        lblT.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
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
        styleCartHeader();

        int[] cw = {160, 82, 96, 32};
        for (int i = 0; i < cw.length; i++) {
            tblCart.getColumnModel().getColumn(i).setPreferredWidth(cw[i]);
        }

        tblCart.getColumnModel().getColumn(0).setCellRenderer(new CartProductRdr());
        tblCart.getColumnModel().getColumn(1).setCellRenderer(new CartQtyRdr());
        tblCart.getColumnModel().getColumn(1).setCellEditor(new CartQtyEditor());
        tblCart.getColumnModel().getColumn(2).setCellRenderer(new CartTotalRdr());
        tblCart.getColumnModel().getColumn(3).setCellRenderer(new CartDelRdr());
        tblCart.getColumnModel().getColumn(3).setCellEditor(new CartDelEditor());

        JScrollPane cartScroll = new JScrollPane(tblCart);
        cartScroll.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        cartScroll.getViewport().setBackground(Colors.BACKGROUND);
        cartScroll.setAlignmentX(LEFT_ALIGNMENT);
        cartScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));
        cartScroll.setPreferredSize(new Dimension(0, 220));

        // Summary
        JPanel sum = new JPanel(new GridLayout(2, 1, 0, 4));
        sum.setBackground(Colors.BACKGROUND);
        sum.setAlignmentX(LEFT_ALIGNMENT);
        sum.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        sum.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(8, 0, 0, 0)
        ));

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

        p.add(hdr);
        p.add(vgap(8));
        p.add(cartScroll);
        p.add(vgap(8));
        p.add(sum);
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
            cartModel.addRow(new Object[]{ci, ci, ci, ci});
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
        if (spnGioNhan == null || !(spnGioNhan.getModel() instanceof SpinnerDateModel)) {
            return;
        }

        LocalDate ngayNhan = dtcNgayNhan != null && dtcNgayNhan.getDate() != null
                ? dtcNgayNhan.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                : LocalDate.now();

        LocalTime gioMoCua = laChuNhat(ngayNhan) ? GIO_MO_CUA_CHU_NHAT : GIO_MO_CUA_THU_2_7;
        LocalTime gioDongCua = laChuNhat(ngayNhan) ? GIO_DONG_CUA_CHU_NHAT : GIO_DONG_CUA_THU_2_7;

        Date minTime = taoDateTheoNgayVaGio(ngayNhan, gioMoCua);
        Date maxTime = taoDateTheoNgayVaGio(ngayNhan, gioDongCua);
        SpinnerDateModel model = (SpinnerDateModel) spnGioNhan.getModel();
        model.setStart(minTime);
        model.setEnd(maxTime);

        Date currentValue = (Date) spnGioNhan.getValue();
        LocalTime currentTime = currentValue.toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        if (currentTime.isBefore(gioMoCua)) {
            spnGioNhan.setValue(minTime);
        } else if (currentTime.isAfter(gioDongCua)) {
            spnGioNhan.setValue(maxTime);
        } else {
            spnGioNhan.setValue(taoDateTheoNgayVaGio(ngayNhan, currentTime.withSecond(0).withNano(0)));
        }

        spnGioNhan.setToolTipText(laChuNhat(ngayNhan)
                ? "Chỉ nhận từ 09:00 đến 20:00 vào Chủ nhật"
                : "Chỉ nhận từ 08:00 đến 22:00 từ Thứ 2 đến Thứ 7");
    }

    private static boolean laChuNhat(LocalDate ngay) {
        return ngay != null && ngay.getDayOfWeek().getValue() == 7;
    }

    private static Date taoDateTheoNgayVaGio(LocalDate ngay, LocalTime gio) {
        return Date.from(gio.atDate(ngay).atZone(ZoneId.systemDefault()).toInstant());
    }

    private boolean gioNhanHopLe() {
        if (dtcNgayNhan == null || dtcNgayNhan.getDate() == null || spnGioNhan == null) {
            return false;
        }

        LocalDate ngayNhan = dtcNgayNhan.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalTime gioNhan = ((Date) spnGioNhan.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalTime();
        LocalTime gioMoCua = laChuNhat(ngayNhan) ? GIO_MO_CUA_CHU_NHAT : GIO_MO_CUA_THU_2_7;
        LocalTime gioDongCua = laChuNhat(ngayNhan) ? GIO_DONG_CUA_CHU_NHAT : GIO_DONG_CUA_THU_2_7;
        return !gioNhan.isBefore(gioMoCua) && !gioNhan.isAfter(gioDongCua);
    }

    // =========================================================================
    // DATA LOADING
    // =========================================================================
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

                List<KhachHang> dsKH = khService.getDSKhachHang();
                SwingUtilities.invokeLater(() -> loadCustomers(dsKH));
                return null;
            }

            @Override
            protected void done() {
                dsHienThi = new ArrayList<>(dsGoc);
                renderProductTable();
            }
        }.execute();
    }

    @SuppressWarnings("unchecked")
    private void loadCustomers(List<KhachHang> dsKH) {
        cmbKhachHang.removeAllItems();
        cmbKhachHang.addItem(null);
        for (KhachHang kh : dsKH) {
            if (kh.isTrangThai()) {
                cmbKhachHang.addItem(kh);
            }
        }
    }

    private void filterAndDisplay() {
        String kw = txtSearch.getText().trim();
        dsHienThi = kw.isEmpty() ? new ArrayList<>(dsGoc) : spService.timKiem(dsGoc, kw);
        renderProductTable();
    }

    private void renderProductTable() {
        productModel.setRowCount(0);
        for (SanPham sp : dsHienThi) {
            SanPham_Service.TonKhoInfo info
                    = mapTonKho.getOrDefault(sp.getMaSanPham(), new SanPham_Service.TonKhoInfo(0, 0, 0));
            productModel.addRow(buildProductRow(sp, info));
        }
    }

    private Object[] buildProductRow(SanPham sp, SanPham_Service.TonKhoInfo info) {
        return new Object[]{
            new Object[]{sp.getMaSanPham(), sp.getTenSanPham()},
            sp, info.tonKho, info.trangThai, sp.getMaSanPham()
        };
    }

    // =========================================================================
    // XÁC NHẬN
    // =========================================================================
    private void xuLyXacNhan() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng thêm ít nhất 1 sản phẩm.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Date ngayNhan = dtcNgayNhan != null ? dtcNgayNhan.getDate() : null;
        if (ngayNhan == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập ngày nhận hàng.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!gioNhanHopLe()) {
            JOptionPane.showMessageDialog(this,
                    "Giờ nhận phải nằm trong giờ hoạt động: Thứ 2-7 từ 08:00 đến 22:00, Chủ nhật từ 09:00 đến 20:00.",
                    "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        String ngay = new SimpleDateFormat("dd/MM/yyyy").format(ngayNhan);
        String gio = spnGioNhan != null
                ? new SimpleDateFormat("HH:mm").format((Date) spnGioNhan.getValue())
                : "--:--";
        double total = cartItems.stream().mapToDouble(CartItem::thanhTien).sum();
        double coc = total * depositPct / 100.0;
        String sCoc = depositPct == 0 ? "Không cọc"
                : PRICE_FMT.format(coc) + "đ (" + depositPct + "%)";
        JOptionPane.showMessageDialog(this,
                "Đặt trước thành công!\nNgày nhận : " + ngay + " " + gio
                + "\nTổng cộng : " + PRICE_FMT.format(total) + "đ"
                + "\nĐặt cọc   : " + sCoc,
                "Xác nhận", JOptionPane.INFORMATION_MESSAGE);
    }

    // =========================================================================
    // INNER — PRODUCT TABLE COLUMN RENDERERS
    // =========================================================================
    /**
     * Col 0: Tên + mã SP (no image)
     */
    private class ProductNameRdr extends JPanel implements TableCellRenderer {

        ProductNameRdr() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean focus, int row, int col) {
            removeAll();
            setBackground(sel ? Colors.PRIMARY_LIGHT
                    : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
                    BorderFactory.createEmptyBorder(8, 12, 8, 10)
            ));
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
    private class PriceRdr extends JPanel implements TableCellRenderer {

        PriceRdr() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean focus, int row, int col) {
            removeAll();
            setBackground(sel ? Colors.PRIMARY_LIGHT
                    : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
                    BorderFactory.createEmptyBorder(8, 10, 8, 8)
            ));
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
    private class StockRdr extends JPanel implements TableCellRenderer {

        StockRdr() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean focus, int row, int col) {
            removeAll();
            setBackground(sel ? Colors.PRIMARY_LIGHT
                    : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
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
    private class StatusRdr extends JPanel implements TableCellRenderer {

        StatusRdr() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean focus, int row, int col) {
            removeAll();
            setBackground(sel ? Colors.PRIMARY_LIGHT
                    : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
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
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean focus, int row, int col) {
            setBackground(sel ? Colors.PRIMARY_LIGHT
                    : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));
            boolean in = isInCart(val != null ? val.toString() : "");
            btnAdd.setVisible(!in);
            btnRem.setVisible(in);
            return this;
        }
    }

    private class ProductActionEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 4, (ACTION_ROW_H - 30) / 2));
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
        public Component getTableCellEditorComponent(JTable t, Object val,
                boolean sel, int row, int col) {
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
    private class CartProductRdr extends JPanel implements TableCellRenderer {

        CartProductRdr() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean focus, int row, int col) {
            removeAll();
            setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
                    BorderFactory.createEmptyBorder(6, 10, 6, 4)
            ));
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
            if (val instanceof CartItem) {
                CartItem ci = (CartItem) val;
                String ten = ci.tenSP.length() > 24 ? ci.tenSP.substring(0, 24) + "\u2026" : ci.tenSP;
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
    private class CartQtyRdr extends JPanel implements TableCellRenderer {

        CartQtyRdr() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean focus, int row, int col) {
            removeAll();
            setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));
            setLayout(new FlowLayout(FlowLayout.CENTER, 2, (CART_ROW_H - 24) / 2));
            if (val instanceof CartItem) {
                CartItem ci = (CartItem) val;
                JLabel m = qtyLabel("\u2212");
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
    private class CartTotalRdr extends JPanel implements TableCellRenderer {

        CartTotalRdr() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean focus, int row, int col) {
            removeAll();
            setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
                    BorderFactory.createEmptyBorder(0, 4, 0, 8)
            ));
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
    private class CartDelRdr extends JPanel implements TableCellRenderer {

        CartDelRdr() {
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val,
                boolean sel, boolean focus, int row, int col) {
            removeAll();
            setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, (CART_ROW_H - 18) / 2));
            JLabel ic = new JLabel("\u2715");
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
    private class CartQtyEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 2, (CART_ROW_H - 24) / 2));
        private final JButton btnM = smallEditBtn("\u2212");
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
        public Component getTableCellEditorComponent(JTable t, Object val,
                boolean sel, int row, int col) {
            currentRow = row;
            panel.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            if (val instanceof CartItem) {
                lblQ.setText(String.valueOf(((CartItem) val).qty));
            }
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return (currentRow >= 0 && currentRow < cartItems.size())
                    ? cartItems.get(currentRow) : null;
        }
    }

    /**
     * Col 3: Delete editor
     */
    private class CartDelEditor extends AbstractCellEditor implements TableCellEditor {

        private final JPanel panel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 0, (CART_ROW_H - 22) / 2));
        private final JButton btnDel = new JButton("\u2715");
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
        public Component getTableCellEditorComponent(JTable t, Object val,
                boolean sel, int row, int col) {
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
        l.setFont(FontStyle.font(
                bold ? FontStyle.SM : FontStyle.XS,
                bold ? FontStyle.BOLD : FontStyle.NORMAL));
        l.setForeground(Colors.MUTED);
        return l;
    }

    private JPanel sectionPanel(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Colors.BACKGROUND);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(12, 14, 14, 14)
        ));
        JLabel l = new JLabel(title);
        l.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        l.setForeground(Colors.TEXT_SECONDARY);
        l.setAlignmentX(LEFT_ALIGNMENT);
        p.add(l);
        p.add(vgap(10));
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
        b.setPreferredSize(new Dimension(80, 30));
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

}
