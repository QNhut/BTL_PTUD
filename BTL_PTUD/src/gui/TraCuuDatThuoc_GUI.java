package gui;

import constants.Colors;
import constants.FontStyle;
import exception.RoundedButton;
import exception.RoundedComboBox;
import exception.RoundedTextField;
import exception.StyledTable;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class TraCuuDatThuoc_GUI extends JPanel {

    private static final class DatThuocRecord {

        private final String maPhieuDat;
        private final String tenKhachHang;
        private final String soDienThoai;
        private final LocalDate ngayNhan;
        private final LocalTime gioNhan;
        private final boolean daNhan;
        private final double tienCoc;
        private final String ghiChu;
        private final List<Object[]> chiTietThuoc;

        private DatThuocRecord(String maPhieuDat, String tenKhachHang, String soDienThoai,
                LocalDate ngayNhan, LocalTime gioNhan, boolean daNhan,
                double tienCoc, String ghiChu, List<Object[]> chiTietThuoc) {
            this.maPhieuDat = maPhieuDat;
            this.tenKhachHang = tenKhachHang;
            this.soDienThoai = soDienThoai;
            this.ngayNhan = ngayNhan;
            this.gioNhan = gioNhan;
            this.daNhan = daNhan;
            this.tienCoc = tienCoc;
            this.ghiChu = ghiChu;
            this.chiTietThuoc = chiTietThuoc != null ? chiTietThuoc : new ArrayList<>();
        }

        private int tongSoLuong() {
            int total = 0;
            for (Object[] row : chiTietThuoc) {
                if (row != null && row.length > 3 && row[3] instanceof Number) {
                    total += ((Number) row[3]).intValue();
                }
            }
            return total;
        }
    }

    private static final String[] COLUMN_NAMES = {
        "Mã phiếu đặt", "Khách hàng", "Ngày nhận", "Giờ nhận",
        "Trạng thái", "Tiền cọc", "Thao tác"
    };

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");
    private static final NumberFormat MONEY_FMT = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private final ArrayList<DatThuocRecord> fullList = new ArrayList<>();
    private final ArrayList<DatThuocRecord> filteredList = new ArrayList<>();

    private RoundedComboBox<String> cboTimKiemTheo;
    private RoundedTextField txtKeyword;
    private JSpinner spnTuNgay;
    private JSpinner spnDenNgay;
    private boolean tuNgayActive = false;
    private boolean denNgayActive = false;
    private RoundedButton btnTimKiem;
    private RoundedButton btnXoaLoc;

    private JLabel lblSoLuong;
    private StyledTable tblDatThuoc;
    private JPanel pairButton;

    public TraCuuDatThuoc_GUI() {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Colors.BACKGROUND);

        add(buildTopSection(), BorderLayout.NORTH);
        add(buildResultsPanel(), BorderLayout.CENTER);

        loadData();
    }

    private JPanel buildTopSection() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Colors.BACKGROUND);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Colors.BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Tra cứu đặt thuốc");
        lblTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.TEXT_PRIMARY);

        JLabel lblNote = new JLabel("Tìm kiếm và xem chi tiết các phiếu đặt thuốc");
        lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNote.setForeground(Colors.TEXT_SECONDARY);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(lblNote);

        pnl.add(header);
        pnl.add(buildFilterCard());
        return pnl;
    }

    private JPanel buildFilterCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        pnlNorth.setOpaque(false);
        pnlNorth.add(buildCardTitleRow());
        card.add(pnlNorth, BorderLayout.NORTH);

        JPanel pnlFields = new JPanel();
        pnlFields.setLayout(new BoxLayout(pnlFields, BoxLayout.X_AXIS));
        pnlFields.setOpaque(false);
        pnlFields.setBorder(BorderFactory.createEmptyBorder(4, 12, 12, 12));

        JPanel pairTimKiem = createFieldPair();
        JLabel lblTimKiemTheo = fieldLabel("Tìm kiếm theo");
        pairTimKiem.add(lblTimKiemTheo);
        pairTimKiem.add(Box.createVerticalStrut(4));
        cboTimKiemTheo = new RoundedComboBox<>(10);
        cboTimKiemTheo.addItem("Mã phiếu đặt");
        cboTimKiemTheo.addItem("Khách hàng");
        cboTimKiemTheo.addItem("Số điện thoại");
        cboTimKiemTheo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cboTimKiemTheo.setMaximumSize(new java.awt.Dimension(200, 44));
        pairTimKiem.add(cboTimKiemTheo);
        pnlFields.add(pairTimKiem);
        pnlFields.add(Box.createHorizontalStrut(10));

        JPanel pairTuKhoa = createFieldPair();
        JLabel lblTuKhoa = fieldLabel("Từ khóa");
        pairTuKhoa.add(lblTuKhoa);
        pairTuKhoa.add(Box.createVerticalStrut(4));
        txtKeyword = new RoundedTextField(800, 44, 10, "Nhập mã phiếu đặt...");
        txtKeyword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtKeyword.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 44));
        pairTuKhoa.add(txtKeyword);
        pnlFields.add(pairTuKhoa);
        pnlFields.add(Box.createHorizontalStrut(10));

        JPanel pairTuNgay = createFieldPair();
        JLabel lblTuNgay = fieldLabel("Từ ngày");
        pairTuNgay.add(lblTuNgay);
        pairTuNgay.add(Box.createVerticalStrut(4));
        spnTuNgay = makeDateSpinner();
        spnTuNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        spnTuNgay.setMaximumSize(new java.awt.Dimension(200, 44));
        pairTuNgay.add(spnTuNgay);
        pnlFields.add(pairTuNgay);
        pnlFields.add(Box.createHorizontalStrut(10));

        JPanel pairDenNgay = createFieldPair();
        JLabel lblDenNgay = fieldLabel("Đến ngày");
        pairDenNgay.add(lblDenNgay);
        pairDenNgay.add(Box.createVerticalStrut(4));
        spnDenNgay = makeDateSpinner();
        spnDenNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        spnDenNgay.setMaximumSize(new java.awt.Dimension(200, 44));
        pairDenNgay.add(spnDenNgay);
        pnlFields.add(pairDenNgay);

        card.add(pnlFields, BorderLayout.CENTER);

        pairButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pairButton.setOpaque(false);
        pairButton.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        btnXoaLoc = new RoundedButton(130, 44, 10, "\u2715  Xóa lọc", Colors.SECONDARY);
        btnXoaLoc.setForeground(Colors.TEXT_PRIMARY);
        pairButton.add(btnXoaLoc);
        btnTimKiem = new RoundedButton(150, 44, 10, "\uD83D\uDD0D  Tìm kiếm", Colors.PRIMARY);
        pairButton.add(btnTimKiem);
        card.add(pairButton, BorderLayout.SOUTH);

        cboTimKiemTheo.addActionListener(e -> updatePlaceholder());
        txtKeyword.addActionListener(e -> search());
        btnTimKiem.addActionListener(e -> search());
        btnXoaLoc.addActionListener(e -> resetFilter());
        spnTuNgay.addChangeListener(e -> tuNgayActive = true);
        spnDenNgay.addChangeListener(e -> denNgayActive = true);

        return card;
    }

    private JPanel createFieldPair() {
        JPanel pair = new JPanel();
        pair.setLayout(new BoxLayout(pair, BoxLayout.Y_AXIS));
        pair.setOpaque(false);
        pair.setAlignmentY(Component.TOP_ALIGNMENT);
        return pair;
    }

    private JPanel buildCardTitleRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);
        JLabel icon = new JLabel("\uD83D\uDD0D");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        icon.setForeground(Colors.PRIMARY);
        JLabel title = new JLabel("Bộ lọc tìm kiếm");
        title.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        title.setForeground(Colors.PRIMARY);
        row.add(icon);
        row.add(title);
        return row;
    }

    private JPanel buildResultsPanel() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 0));

        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        JLabel iconDoc = new JLabel("\uD83D\uDCC4");
        iconDoc.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        JLabel lblResultTitle = new JLabel("Kết quả tìm kiếm");
        lblResultTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblResultTitle.setForeground(Colors.TEXT_PRIMARY);
        left.add(iconDoc);
        left.add(lblResultTitle);

        lblSoLuong = new JLabel("Tìm thấy 0 phiếu đặt");
        lblSoLuong.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSoLuong.setForeground(Colors.PRIMARY);

        bar.add(left, BorderLayout.WEST);
        bar.add(lblSoLuong, BorderLayout.EAST);
        card.add(bar, BorderLayout.NORTH);

        tblDatThuoc = new StyledTable(COLUMN_NAMES, filteredList);

        tblDatThuoc.setColumnRenderer(0, (tbl, val, sel, foc, row, col) -> {
            JLabel lbl = new JLabel();
            DatThuocRecord rec = (DatThuocRecord) val;
            lbl.setOpaque(true);
            lbl.setText(rec.maPhieuDat);
            lbl.setForeground(Colors.PRIMARY);
            lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
            lbl.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            return lbl;
        });
        tblDatThuoc.setColumnWidth(0, 130);

        tblDatThuoc.setTwoLineColumn(1, 220,
                v -> ((DatThuocRecord) v).tenKhachHang,
                v -> ((DatThuocRecord) v).soDienThoai);

        tblDatThuoc.setSingleTextColumn(2, 130,
                v -> ((DatThuocRecord) v).ngayNhan != null ? ((DatThuocRecord) v).ngayNhan.format(DATE_FMT) : "");

        tblDatThuoc.setSingleTextColumn(3, 110,
                v -> ((DatThuocRecord) v).gioNhan != null ? ((DatThuocRecord) v).gioNhan.format(TIME_FMT) : "");

        tblDatThuoc.setBadgeColumn(4, 130,
                v -> ((DatThuocRecord) v).daNhan,
                "Đã nhận", "Chờ nhận");

        tblDatThuoc.setColumnRenderer(5, (tbl, val, sel, foc, row, col) -> {
            JLabel lbl = new JLabel();
            DatThuocRecord rec = (DatThuocRecord) val;
            lbl.setOpaque(true);
            lbl.setText(MONEY_FMT.format(rec.tienCoc) + " đ");
            lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            lbl.setForeground(Colors.TEXT_PRIMARY);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
            lbl.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            return lbl;
        });
        tblDatThuoc.setColumnWidth(5, 150);

        tblDatThuoc.setActionColumn(6, 100);
        tblDatThuoc.setActionColumnListener((row, obj) -> moChiTietDatThuoc((DatThuocRecord) obj));

        card.add(tblDatThuoc, BorderLayout.CENTER);
        return card;
    }

    public void refresh() {
        loadData();
    }

    private void loadData() {
        fullList.clear();
        // Tạm dùng dữ liệu mẫu cho đến khi module Đặt trước có entity/DAO/service riêng.
        fullList.addAll(buildTempData());

        filteredList.clear();
        filteredList.addAll(fullList);
        if (tblDatThuoc != null) {
            tblDatThuoc.refresh();
        }
        updateCountLabel();
    }

    private List<DatThuocRecord> buildTempData() {
        List<DatThuocRecord> records = new ArrayList<>();
        records.add(new DatThuocRecord(
                "DT001",
                "Nguyễn Văn An",
                "0901234567",
                LocalDate.now().plusDays(1),
                LocalTime.of(10, 0),
                false,
                150000,
                "Khách cần chuẩn bị sẵn thuốc trước 10h.",
                Arrays.asList(
                        new Object[]{1, "Paracetamol 500mg", "Hộp", 2},
                        new Object[]{2, "Vitamin C", "Tuýp", 1}
                )
        ));
        records.add(new DatThuocRecord(
                "DT002",
                "Trần Thị Bình",
                "0912345678",
                LocalDate.now().plusDays(2),
                LocalTime.of(15, 30),
                true,
                80000,
                "Đã gọi xác nhận và khách sẽ đến nhận buổi chiều.",
                Arrays.asList(
                        new Object[]{1, "Omega 3", "Hộp", 1},
                        new Object[]{2, "Siro ho", "Chai", 2}
                )
        ));
        return records;
    }

    private void search() {
        String loai = (String) cboTimKiemTheo.getSelectedItem();
        String kw = txtKeyword.getText().trim().toLowerCase();
        LocalDateTime tuNgay = tuNgayActive ? spinnerToDate(spnTuNgay) : null;
        LocalDateTime denNgay = denNgayActive ? spinnerToDate(spnDenNgay) : null;
        if (denNgay != null) {
            denNgay = denNgay.withHour(23).withMinute(59).withSecond(59);
        }

        filteredList.clear();
        for (DatThuocRecord rec : fullList) {
            if (!kw.isEmpty() && !matchKeyword(rec, loai, kw)) {
                continue;
            }

            LocalDateTime ngayNhan = rec.ngayNhan != null ? rec.ngayNhan.atStartOfDay() : null;
            if (ngayNhan != null) {
                if (tuNgay != null && ngayNhan.isBefore(tuNgay)) {
                    continue;
                }
                if (denNgay != null && ngayNhan.isAfter(denNgay)) {
                    continue;
                }
            }
            filteredList.add(rec);
        }

        tblDatThuoc.refresh();
        updateCountLabel();
    }

    private boolean matchKeyword(DatThuocRecord rec, String loai, String kw) {
        if (loai == null) {
            return true;
        }
        switch (loai) {
            case "Mã phiếu đặt":
                return rec.maPhieuDat != null && rec.maPhieuDat.toLowerCase().contains(kw);
            case "Khách hàng":
                return rec.tenKhachHang != null && rec.tenKhachHang.toLowerCase().contains(kw);
            case "Số điện thoại":
                return rec.soDienThoai != null && rec.soDienThoai.toLowerCase().contains(kw);
            default:
                return true;
        }
    }

    private void resetFilter() {
        txtKeyword.setText("");
        cboTimKiemTheo.setSelectedIndex(0);
        spnTuNgay.setValue(new java.util.Date());
        spnDenNgay.setValue(new java.util.Date());
        tuNgayActive = false;
        denNgayActive = false;
        filteredList.clear();
        filteredList.addAll(fullList);
        tblDatThuoc.refresh();
        updateCountLabel();
        updatePlaceholder();
    }

    private void moChiTietDatThuoc(DatThuocRecord rec) {
        LinkedHashMap<String, String> leftInfo = new LinkedHashMap<>();
        leftInfo.put("Họ tên", rec.tenKhachHang != null ? rec.tenKhachHang : "---");
        leftInfo.put("SĐT", rec.soDienThoai != null ? rec.soDienThoai : "---");
        leftInfo.put("Ghi chú", rec.ghiChu != null && !rec.ghiChu.isBlank() ? rec.ghiChu : "---");

        LinkedHashMap<String, String> rightInfo = new LinkedHashMap<>();
        rightInfo.put("Mã phiếu đặt", rec.maPhieuDat);
        rightInfo.put("Ngày nhận", rec.ngayNhan != null ? rec.ngayNhan.format(DATE_FMT) : "---");
        rightInfo.put("Giờ nhận", rec.gioNhan != null ? rec.gioNhan.format(TIME_FMT) : "---");
        rightInfo.put("Trạng thái", rec.daNhan ? "Đã nhận" : "Chờ nhận");

        String[] cols = {"STT", "Tên thuốc", "Đơn vị", "SL"};
        List<Object[]> rows = new ArrayList<>(rec.chiTietThuoc);

        List<ChiTietDialog.SummaryRow> summary = new ArrayList<>();
        summary.add(new ChiTietDialog.SummaryRow("Số mặt hàng",
                String.valueOf(rows.size()), null, false));
        summary.add(new ChiTietDialog.SummaryRow("Tổng số lượng",
                String.valueOf(rec.tongSoLuong()), null, false));
        summary.add(new ChiTietDialog.SummaryRow("Tiền cọc",
                MONEY_FMT.format(rec.tienCoc) + " đ", Colors.PRIMARY, true));

        new ChiTietDialog(
                SwingUtilities.getWindowAncestor(this),
                "\uD83D\uDC8A",
                "Chi tiết phiếu đặt: " + rec.maPhieuDat,
                "\uD83D\uDC64", "Thông tin khách hàng", leftInfo,
                "\uD83D\uDCC4", "Thông tin phiếu đặt", rightInfo,
                "Danh sách thuốc đặt",
                cols, rows, new int[]{0, 3},
                summary
        ).setVisible(true);
    }

    private JPanel createCard() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
                g2.setColor(Colors.BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBackground(Color.WHITE);
        return p;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        l.setForeground(Colors.TEXT_PRIMARY);
        l.setHorizontalAlignment(SwingConstants.LEFT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, l.getPreferredSize().height));
        return l;
    }

    private JSpinner makeDateSpinner() {
        JSpinner sp = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor ed = new JSpinner.DateEditor(sp, "dd/MM/yyyy");
        sp.setEditor(ed);
        sp.setPreferredSize(new java.awt.Dimension(160, 42));
        sp.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        return sp;
    }

    private void updatePlaceholder() {
        String s = (String) cboTimKiemTheo.getSelectedItem();
        if (s == null) {
            return;
        }
        switch (s) {
            case "Mã phiếu đặt":
                txtKeyword.setPlaceholder("Nhập mã phiếu đặt...");
                break;
            case "Khách hàng":
                txtKeyword.setPlaceholder("Nhập tên khách hàng...");
                break;
            case "Số điện thoại":
                txtKeyword.setPlaceholder("Nhập số điện thoại...");
                break;
            default:
                break;
        }
    }

    private void updateCountLabel() {
        lblSoLuong.setText("Tìm thấy " + filteredList.size() + " phiếu đặt");
    }

    private LocalDateTime spinnerToDate(JSpinner sp) {
        try {
            java.util.Date d = (java.util.Date) sp.getValue();
            return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }
}
