package gui;

import constants.Colors;
import constants.FontStyle;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.NhanVien_DAO;
import dao.SanPham_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.NhanVien;
import entity.PhuongThucThanhToan;
import entity.SanPham;
import exception.RoundedButton;

import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import service.HoaDon_Service;
import util.MaskUtil;

/**
 * Dialog xác nhận thanh toán cho phiếu đặt thuốc (trạng thái "Chờ thanh toán").
 * Hiển thị thông tin khách hàng, danh sách thuốc kèm giá, tổng kết,
 * cho phép chọn phương thức thanh toán rồi xác nhận.
 */
@SuppressWarnings("serial")
public class XacNhanThanhToanDialog extends JDialog {

    private static final NumberFormat MONEY_FMT = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final String maHoaDon;
    private final String tenKhachHang;
    private final String soDienThoai;
    private final LocalDate ngayNhan;
    private final LocalTime gioNhan;
    private final HoaDon_Service hoaDonService;
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO;
    private final SanPham_DAO sanPhamDAO;
    private final Runnable onSuccess;

    private JComboBox<PhuongThucThanhToan> cboPTTT;
    // Raw data for building the print preview model (col0: tenSP, col1: sl, col2: donGia int)
    private final List<Object[]> rawRows = new ArrayList<>();
    private String tenNhanVien = "—";
    private String thoiGianLap = "";

    // Loyalty points + dynamic summary
    private entity.KhachHang khachHangHienTai;
    private JCheckBox chkApDungDiem;
    private JPanel pnlSummaryCard;
    private JLabel lblSumGiamGia, lblSumConLai;
    private double tamTinhVal, tienThueVal, tienGiamGiaVal, tienCocVal;

    public XacNhanThanhToanDialog(
            Window parent,
            String maHoaDon,
            String tenKhachHang,
            String soDienThoai,
            LocalDate ngayNhan,
            LocalTime gioNhan,
            HoaDon_Service hoaDonService,
            ChiTietHoaDon_DAO chiTietHoaDonDAO,
            SanPham_DAO sanPhamDAO,
            Runnable onSuccess) {
        super(parent, "Xác nhận thanh toán — " + maHoaDon, ModalityType.APPLICATION_MODAL);
        this.maHoaDon = maHoaDon;
        this.tenKhachHang = tenKhachHang;
        this.soDienThoai = soDienThoai;
        this.ngayNhan = ngayNhan;
        this.gioNhan = gioNhan;
        this.hoaDonService = hoaDonService;
        this.chiTietHoaDonDAO = chiTietHoaDonDAO;
        this.sanPhamDAO = sanPhamDAO;
        this.onSuccess = onSuccess;
        initUI();
    }

    // =========================================================
    //  Main UI setup
    // =========================================================
    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        // Load current MaPTTT + NhanVien name + ngayLap for pre-selection and receipt
        HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
        HoaDon hd = hoaDonDAO.layHDTheoMa(maHoaDon);
        String maPTTTHienTai = (hd != null) ? hd.getMaPTTT() : null;
        if (hd != null) {
            if (hd.getNgayLap() != null) {
                thoiGianLap = hd.getNgayLap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
            }
            if (hd.getNhanVien() != null && hd.getNhanVien().getMaNhanVien() != null) {
                NhanVien nv = new NhanVien_DAO().layNVTheoMa(hd.getNhanVien().getMaNhanVien());
                tenNhanVien = (nv != null && nv.getTenNhanVien() != null)
                        ? nv.getTenNhanVien() : hd.getNhanVien().getMaNhanVien();
            }
        }

        // Load ChiTietHoaDon with product names
        List<ChiTietHoaDon> dsCT = chiTietHoaDonDAO.getDSTheoHoaDon(maHoaDon);
        List<Object[]> tableRows = new ArrayList<>();
        double tamTinh = 0;
        int stt = 1;
        for (ChiTietHoaDon ct : dsCT) {
            SanPham sp = sanPhamDAO.laySanPhamTheoMa(ct.getSanPham().getMaSanPham());
            String tenSP = (sp != null) ? sp.getTenSanPham() : ct.getSanPham().getMaSanPham();
            String dvt = (sp != null && sp.getDonViTinh() != null) ? sp.getDonViTinh() : "";
            int sl = ct.getSoLuong();
            double donGia = ct.getDonGia();
            double thanhTien = sl * donGia;
            tamTinh += thanhTien;
            tableRows.add(new Object[]{stt++, tenSP, dvt, sl,
                MONEY_FMT.format((long) donGia) + " đ",
                MONEY_FMT.format((long) thanhTien) + " đ"});
            // Store raw values for print model: {tenSP, soLuong, donGia (int)}
            rawRows.add(new Object[]{tenSP, sl, (int) donGia});
        }

        // Compute tax/discount from HoaDon for separate display
        tienThueVal = (hd != null) ? hd.getTienThue() : 0;
        tienGiamGiaVal = (hd != null) ? hd.getTienGiamGia() : 0;

        // Load customer for loyalty points
        if (soDienThoai != null && !soDienThoai.isBlank()
                && !HoaDon_Service.SDT_KHACH_LE.equals(soDienThoai)) {
            entity.KhachHang kh = new service.KhachHang_Service().layKHTheoSDT(soDienThoai);
            if (kh != null && !HoaDon_Service.SDT_KHACH_LE.equals(kh.getSoDienThoai())) {
                khachHangHienTai = kh;
            }
        }

        // Build UI sections
        double tienCoc = parseTienCocTuGhiChu(hd != null ? hd.getGhiChu() : null);
        tamTinhVal = tamTinh;
        tienCocVal = tienCoc;
        add(buildHeader(), BorderLayout.NORTH);
        add(buildScrollBody(tableRows, tamTinh, tienCoc), BorderLayout.CENTER);
        add(buildSouthPanel(maPTTTHienTai), BorderLayout.SOUTH);

        setSize(920, 740);
        setMinimumSize(new Dimension(800, 580));
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    // =========================================================
    //  Header
    // =========================================================
    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
                new EmptyBorder(16, 20, 16, 16)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel title = new JLabel("Xác nhận thanh toán");
        title.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        title.setForeground(Colors.TEXT_PRIMARY);
        JLabel badge = new JLabel(maHoaDon);
        badge.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        badge.setForeground(Colors.PRIMARY);
        badge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.PRIMARY, 1, true),
                new EmptyBorder(2, 8, 2, 8)));
        left.add(title);
        left.add(badge);
        header.add(left, BorderLayout.WEST);

        JButton btnClose = new JButton("✕");
        btnClose.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnClose.setForeground(Colors.TEXT_SECONDARY);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        header.add(btnClose, BorderLayout.EAST);
        return header;
    }

    // =========================================================
    //  Scrollable body: info cards + product table + summary
    // =========================================================
    private JComponent buildScrollBody(List<Object[]> tableRows, double tamTinh, double tienCoc) {
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        // --- 2-column info cards ---
        JPanel infoRow = new JPanel(new GridLayout(1, 2, 16, 0));
        infoRow.setOpaque(false);
        infoRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));
        infoRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoRow.add(buildInfoCard("Thông tin khách hàng", buildCustomerInfo()));
        infoRow.add(buildInfoCard("Đặt thuốc", buildOrderInfo()));
        body.add(infoRow);
        body.add(Box.createVerticalStrut(20));

        // --- Section title ---
        JLabel sectionLbl = new JLabel("Danh sách thuốc đặt");
        sectionLbl.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        sectionLbl.setForeground(Colors.TEXT_PRIMARY);
        sectionLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(sectionLbl);
        body.add(Box.createVerticalStrut(8));

        // --- Product table ---
        JComponent tbl = buildProductTable(tableRows);
        tbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(tbl);
        body.add(Box.createVerticalStrut(16));

        // --- Summary card ---
        JPanel summaryCard = buildSummaryCard(tamTinh, tienCoc);
        summaryCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(summaryCard);

        JScrollPane scroll = new JScrollPane(body,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Color.WHITE);
        return scroll;
    }

    // =========================================================
    //  Info card helper
    // =========================================================
    private JPanel buildInfoCard(String cardTitle, Object[][] rows) {
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
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Card title row
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel titleLbl = new JLabel(cardTitle);
        titleLbl.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        titleLbl.setForeground(Colors.TEXT_PRIMARY);
        titleRow.add(titleLbl);
        card.add(titleRow);
        card.add(Box.createVerticalStrut(10));

        // Separator
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(Colors.BORDER_LIGHT);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sep);
        card.add(Box.createVerticalStrut(10));

        // Key-value rows
        for (Object[] row : rows) {
            JPanel rowPanel = new JPanel(new BorderLayout(8, 0));
            rowPanel.setOpaque(false);
            rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
            rowPanel.setBorder(new EmptyBorder(2, 0, 2, 0));
            JLabel keyLbl = new JLabel((String) row[0]);
            keyLbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            keyLbl.setForeground(Colors.TEXT_SECONDARY);
            keyLbl.setPreferredSize(new Dimension(130, 22));
            JLabel valLbl = new JLabel("<html>" + (String) row[1] + "</html>");
            valLbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            valLbl.setForeground(Colors.TEXT_PRIMARY);
            rowPanel.add(keyLbl, BorderLayout.WEST);
            rowPanel.add(valLbl, BorderLayout.CENTER);
            card.add(rowPanel);
        }
        return card;
    }

    private Object[][] buildCustomerInfo() {
        return new Object[][]{
            {"Tên khách hàng:", tenKhachHang != null && !tenKhachHang.isBlank() ? tenKhachHang : "Khách lẻ"},
            {"Số điện thoại:", soDienThoai != null && !soDienThoai.isBlank() ? MaskUtil.phone(soDienThoai) : "—"}
        };
    }

    private Object[][] buildOrderInfo() {
        return new Object[][]{
            {"Mã phiếu đặt:", maHoaDon},
            {"Ngày nhận:", ngayNhan != null ? ngayNhan.format(DATE_FMT) : "—"},
            {"Giờ nhận:", gioNhan != null ? gioNhan.format(TIME_FMT) : "—"}
        };
    }

    // =========================================================
    //  Product table
    // =========================================================
    private JComponent buildProductTable(List<Object[]> rows) {
        String[] cols = {"STT", "Tên thuốc", "ĐVT", "SL", "Đơn giá", "Thành tiền"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Object[] row : rows) model.addRow(row);

        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(236, 252, 247));
        table.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        table.setFillsViewportHeight(true);

        // Header style
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setBackground(new Color(248, 249, 251));
        tableHeader.setForeground(Colors.TEXT_PRIMARY);
        tableHeader.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        tableHeader.setReorderingAllowed(false);
        tableHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));
        tableHeader.setPreferredSize(new Dimension(0, 36));

        // Column widths
        int[] widths = {50, 0, 80, 60, 110, 120};
        for (int i = 0; i < widths.length; i++) {
            if (widths[i] > 0) table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        // Center-align STT, ĐVT, SL
        DefaultTableCellRenderer centerR = new DefaultTableCellRenderer();
        centerR.setHorizontalAlignment(SwingConstants.CENTER);
        DefaultTableCellRenderer rightR = new DefaultTableCellRenderer();
        rightR.setHorizontalAlignment(SwingConstants.RIGHT);

        // Row renderer for alternating rows + alignment
        javax.swing.table.TableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value,
                    boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
                lbl.setBorder(new EmptyBorder(0, 12, 0, 12));
                if (!sel) lbl.setBackground(row % 2 == 0 ? Color.WHITE : new Color(249, 250, 251));
                lbl.setForeground(Colors.TEXT_PRIMARY);
                lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
                switch (col) {
                    case 0: case 2: case 3:
                        lbl.setHorizontalAlignment(SwingConstants.CENTER); break;
                    case 4: case 5:
                        lbl.setHorizontalAlignment(SwingConstants.RIGHT); break;
                    default:
                        lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }
                return lbl;
            }
        };
        for (int i = 0; i < cols.length; i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(rowRenderer);
        }

        // Empty-state label
        if (rows.isEmpty()) {
            JPanel empty = new JPanel(new BorderLayout());
            empty.setBackground(Color.WHITE);
            empty.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
            JLabel lbl = new JLabel("Không có mặt hàng nào", SwingConstants.CENTER);
            lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            lbl.setForeground(Colors.TEXT_SECONDARY);
            lbl.setBorder(new EmptyBorder(24, 0, 24, 0));
            empty.add(lbl, BorderLayout.CENTER);
            return empty;
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        sp.setPreferredSize(new Dimension(0, Math.min(rows.size() * 36 + 40, 250)));
        sp.getViewport().setBackground(Color.WHITE);
        return sp;
    }

    // =========================================================
    //  Summary totals card
    // =========================================================
    private JPanel buildSummaryCard(double tamTinh, double tienCoc) {
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
        card.setBorder(new EmptyBorder(14, 20, 14, 20));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        addSummaryRow(card, "Tạm tính", MONEY_FMT.format((long) tamTinh) + " đ", Colors.TEXT_PRIMARY, false);
        addSummaryRow(card, "Thuế (+)",
                tienThueVal > 0 ? "+ " + MONEY_FMT.format((long) tienThueVal) + " đ" : "0 đ",
                Colors.TEXT_SECONDARY, false);
        // Giảm giá (-) — gộp KM + điểm tích lũy, giống HoaDon_GUI
        JPanel rowGiam = new JPanel(new BorderLayout(8, 0));
        rowGiam.setOpaque(false);
        rowGiam.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowGiam.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        rowGiam.setBorder(new EmptyBorder(0, 0, 4, 0));
        JLabel keyGiam = new JLabel("Giảm giá (-)");
        keyGiam.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        keyGiam.setForeground(Colors.TEXT_SECONDARY);
        String initGiam = tienGiamGiaVal > 0
                ? "- " + String.format("%,.0fđ", tienGiamGiaVal) : "0đ";
        lblSumGiamGia = new JLabel(initGiam, SwingConstants.RIGHT);
        lblSumGiamGia.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lblSumGiamGia.setForeground(Colors.SUCCESS_DARK);
        rowGiam.add(keyGiam, BorderLayout.WEST);
        rowGiam.add(lblSumGiamGia, BorderLayout.EAST);
        card.add(rowGiam);
        card.add(Box.createVerticalStrut(4));

        if (tienCoc > 0) {
            addSummaryRow(card, "Đã cọc trước",
                    "- " + MONEY_FMT.format((long) tienCoc) + " đ", Colors.TEXT_SECONDARY, false);
        }

        // Separator line before total
        card.add(Box.createVerticalStrut(6));
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        sep.setForeground(Colors.BORDER_LIGHT);
        sep.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(sep);
        card.add(Box.createVerticalStrut(8));

        // THÀNH TIỀN / CÒN LẠI (dynamic label — updated by updateSummaryConLai)
        double conLai = Math.max(0, tamTinh + tienThueVal - tienCoc);
        String conLaiLabel = tienCoc > 0 ? "CÒN LẠI" : "THÀNH TIỀN";
        JPanel rowConLai = new JPanel(new BorderLayout(8, 0));
        rowConLai.setOpaque(false);
        rowConLai.setAlignmentX(Component.LEFT_ALIGNMENT);
        rowConLai.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        JLabel keyConLai = new JLabel(conLaiLabel);
        keyConLai.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        keyConLai.setForeground(Colors.TEXT_PRIMARY);
        lblSumConLai = new JLabel(String.format("%,.0fđ", conLai), SwingConstants.RIGHT);
        lblSumConLai.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblSumConLai.setForeground(Colors.SUCCESS_DARK);
        rowConLai.add(keyConLai, BorderLayout.WEST);
        rowConLai.add(lblSumConLai, BorderLayout.EAST);
        card.add(rowConLai);

        pnlSummaryCard = card;
        return card;
    }

    private void updateSummaryConLai(int diemSuDung) {
        long tienGiamDiem = (long) diemSuDung * HoaDon_Service.VND_PER_POINT_USE;
        double conLai = Math.max(0, tamTinhVal + tienThueVal - tienCocVal - tienGiamDiem);
        if (lblSumConLai != null) {
            lblSumConLai.setText(String.format("%,.0fđ", conLai));
        }
        if (lblSumGiamGia != null) {
            long combinedGiam = (long) tienGiamGiaVal + tienGiamDiem;
            lblSumGiamGia.setText(combinedGiam > 0
                    ? "- " + String.format("%,.0fđ", (double) combinedGiam) : "0đ");
        }
        if (pnlSummaryCard != null) {
            pnlSummaryCard.revalidate();
            pnlSummaryCard.repaint();
        }
    }

    private static double parseTienCocTuGhiChu(String ghiChu) {
        if (ghiChu == null) return 0;
        final String PREFIX = "Cọc: ";
        int idx = ghiChu.indexOf(PREFIX);
        if (idx < 0) return 0;
        String segment = ghiChu.substring(idx + PREFIX.length());
        int pipeIdx = segment.indexOf(" | ");
        if (pipeIdx >= 0) segment = segment.substring(0, pipeIdx);
        if (segment.startsWith("Không cọc")) return 0;
        // Format: "1,500,000đ (30%)" — extract digits before "đ"
        java.util.regex.Matcher m = java.util.regex.Pattern.compile("([\\d,.]+)đ").matcher(segment);
        if (m.find()) {
            try {
                return Double.parseDouble(m.group(1).replaceAll("[,.]", ""));
            } catch (Exception ignored) {}
        }
        return 0;
    }

    private void addSummaryRow(JPanel parent, String label, String value, Color valueColor, boolean bold) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));

        JLabel keyLbl = new JLabel(label);
        keyLbl.setFont(bold ? FontStyle.font(FontStyle.SM, FontStyle.BOLD) : FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        keyLbl.setForeground(bold ? Colors.TEXT_PRIMARY : Colors.TEXT_SECONDARY);

        JLabel valLbl = new JLabel(value, SwingConstants.RIGHT);
        valLbl.setFont(bold ? FontStyle.font(FontStyle.BASE, FontStyle.BOLD) : FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        valLbl.setForeground(valueColor);

        row.add(keyLbl, BorderLayout.WEST);
        row.add(valLbl, BorderLayout.EAST);
        parent.add(row);
        parent.add(Box.createVerticalStrut(4));
    }

    // =========================================================
    //  South panel: PTTT selector + action buttons
    // =========================================================
    private JPanel buildSouthPanel(String maPTTTHienTai) {
        JPanel south = new JPanel(new BorderLayout(0, 0));
        south.setBackground(Color.WHITE);
        south.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT));

        // PTTT row
        JPanel ptttRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 12));
        ptttRow.setBackground(new Color(248, 250, 252));
        ptttRow.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));

        JLabel ptttLbl = new JLabel("Phương thức thanh toán:");
        ptttLbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        ptttLbl.setForeground(Colors.TEXT_PRIMARY);

        cboPTTT = new JComboBox<>();
        cboPTTT.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        cboPTTT.setPreferredSize(new Dimension(220, 34));

        List<PhuongThucThanhToan> dsPTTT = hoaDonService.getDSPhuongThucThanhToan();
        PhuongThucThanhToan preSelected = null;
        for (PhuongThucThanhToan pt : dsPTTT) {
            cboPTTT.addItem(pt);
            if (pt.getMaPTTT().equals(maPTTTHienTai)) preSelected = pt;
        }
        if (preSelected != null) cboPTTT.setSelectedItem(preSelected);

        cboPTTT.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean sel, boolean focus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, sel, focus);
                if (value instanceof PhuongThucThanhToan) {
                    lbl.setText(((PhuongThucThanhToan) value).getTenPTTT());
                }
                return lbl;
            }
        });

        ptttRow.add(ptttLbl);
        ptttRow.add(cboPTTT);

        // Loyalty points checkbox — only shown if customer has points
        if (khachHangHienTai != null && khachHangHienTai.getDiemTichLuy() > 0) {
            int maxDiem = khachHangHienTai.getDiemTichLuy();
            long tienGiamMax = (long) maxDiem * HoaDon_Service.VND_PER_POINT_USE;
            chkApDungDiem = new JCheckBox("Áp dụng " + maxDiem + " điểm tích lũy (→ giảm "
                    + String.format("%,.0fđ", (double) tienGiamMax) + ")");
            chkApDungDiem.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            chkApDungDiem.setOpaque(false);
            chkApDungDiem.addActionListener(e -> {
                boolean on = chkApDungDiem.isSelected();
                updateSummaryConLai(on ? maxDiem : 0);
            });
            ptttRow.add(chkApDungDiem);
        }

        south.add(ptttRow, BorderLayout.NORTH);

        // Button row
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 12));
        btnRow.setBackground(Color.WHITE);

        RoundedButton btnClose = new RoundedButton(110, 38, 8, "Đóng", Colors.SECONDARY);
        btnClose.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnClose.setForeground(Colors.TEXT_PRIMARY);
        btnClose.addActionListener(e -> dispose());

        RoundedButton btnConfirm = new RoundedButton(220, 38, 8, "Xác nhận thanh toán", Colors.SUCCESS);
        btnConfirm.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(e -> xulyXacNhan());

        btnRow.add(btnClose);
        btnRow.add(btnConfirm);
        south.add(btnRow, BorderLayout.CENTER);

        return south;
    }

    // =========================================================
    //  Confirm action
    // =========================================================
    private void xulyXacNhan() {
        PhuongThucThanhToan pttt = (PhuongThucThanhToan) cboPTTT.getSelectedItem();
        if (pttt == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn phương thức thanh toán.",
                    "Thiếu thông tin", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "<html>Xác nhận thanh toán phiếu <b>" + maHoaDon + "</b><br>"
                + "Phương thức: <b>" + pttt.getTenPTTT() + "</b><br>"
                + "Hóa đơn sẽ chuyển sang trạng thái <b>Đã thanh toán</b>.</html>",
                "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (confirm != JOptionPane.YES_OPTION) return;

        // Confirm payment in DB (update NgayLap → actual time, GhiChu → delay note)
        try {
            // Deduct loyalty points if applied before recording the payment
            if (chkApDungDiem != null && chkApDungDiem.isSelected() && khachHangHienTai != null) {
                hoaDonService.truDiemTichLuy(khachHangHienTai, khachHangHienTai.getDiemTichLuy());
            }
            LocalDateTime ngayGioDuKien = (ngayNhan != null && gioNhan != null)
                    ? ngayNhan.atTime(gioNhan) : null;
            hoaDonService.xacNhanThanhToanCho(maHoaDon, pttt.getMaPTTT(), ngayGioDuKien);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi xác nhận thanh toán: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Đánh dấu tồn kho đã thay đổi → HoaDon_GUI sẽ reload khi user chuyển sang tab Bán hàng
        HoaDon_GUI.stockDirty = true;

        // Build DefaultTableModel for print preview (col0: tenSP, col1: sl, col2: donGia)
        DefaultTableModel previewModel = new DefaultTableModel(
                new String[]{"Tên SP", "SL", "Đơn giá"}, 0);
        for (Object[] row : rawRows) {
            previewModel.addRow(row);
        }

        // Resolve parent Frame before closing this dialog
        Frame parentFrame = null;
        for (Window w = getOwner(); w != null; w = w.getOwner()) {
            if (w instanceof Frame) { parentFrame = (Frame) w; break; }
        }
        if (parentFrame == null && getOwner() instanceof Frame) {
            parentFrame = (Frame) getOwner();
        }

        // Close this dialog and refresh the pre-order table
        dispose();
        if (onSuccess != null) onSuccess.run();

        // Tính tổng tiền từ rawRows để truyền sang preview (dùng cho QR)
        long tongTien = 0;
        for (Object[] row : rawRows) {
            tongTien += (long) ((int) row[1]) * ((int) row[2]);
        }

        // Open invoice preview with Print / Export PDF / Close buttons
        String thoiGian = thoiGianLap.isEmpty()
                ? LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                : thoiGianLap;
        HoaDonPreviewDialog preview = new HoaDonPreviewDialog(
                parentFrame,
                tenKhachHang,
                soDienThoai,
                tenNhanVien,
                thoiGian,
                previewModel,
                pttt.getTenPTTT(),
                tongTien);
        preview.setVisible(true);
    }
}
