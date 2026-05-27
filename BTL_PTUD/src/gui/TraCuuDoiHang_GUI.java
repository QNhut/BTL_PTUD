package gui;

import constants.Colors;
import constants.FontStyle;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhuongThucThanhToan;
import entity.SanPham;
import exception.RoundedButton;
import exception.RoundedComboBox;
import exception.RoundedTextField;
import exception.StyledTable;

import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import service.ChiTietHoaDon_Service;
import service.HoaDon_Service;
import service.KhachHang_Service;
import service.NhanVien_Service;
import service.SanPham_Service;
import util.AsyncLoader;
import util.MaskUtil;

@SuppressWarnings("serial")
public class TraCuuDoiHang_GUI extends JPanel {

    // ── Services / Data ────────────────────────────────────────
    private final HoaDon_Service hoaDonSV = new HoaDon_Service();
    private final KhachHang_Service khachHangSV = new KhachHang_Service();
    private final NhanVien_Service nhanVienSV = new NhanVien_Service();
    private final ChiTietHoaDon_Service chiTietHoaDonSV = new ChiTietHoaDon_Service();
    private final SanPham_Service sanPhamSV = new SanPham_Service();
    private final ArrayList<HoaDon> fullList = new ArrayList<>();
    private final ArrayList<HoaDon> filteredList = new ArrayList<>();

    // ── Filter controls ────────────────────────────────────────
    private RoundedComboBox<String> cboTimKiemTheo;
    private RoundedTextField txtKeyword;
    private JDateChooser dtcTuNgay;
    private JDateChooser dtcDenNgay;
    private RoundedButton btnTimKiem;
    private RoundedButton btnXoaLoc;

    // ── Results area ───────────────────────────────────────────
    private JLabel lblSoLuong;
    private StyledTable tblDoiHang;
    private JPanel pairButton;

    // ── Suggestion popup ───────────────────────────────────────
    private SearchSuggestionPopup<KhachHang> khSuggest;
    private SearchSuggestionPopup<NhanVien> nvSuggest;

    private static final String[] COLUMN_NAMES = {
        "Mã hóa đơn gốc", "Khách hàng", "Nhân viên xử lý",
        "Thời gian", "Tổng tiền", "Thao tác"
    };

    private static final DateTimeFormatter DATE_FMT
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat MONEY_FMT
            = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    // ═══════════════════════════════════════════════════════════
    public TraCuuDoiHang_GUI() {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Colors.BACKGROUND);

        add(buildTopSection(), BorderLayout.NORTH);
        add(buildResultsPanel(), BorderLayout.CENTER);

        loadData();
    }

    // ─────────────────────────────────────────────────────────
    // TOP SECTION
    // ─────────────────────────────────────────────────────────
    private JPanel buildTopSection() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Colors.BACKGROUND);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Colors.BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Tra cứu đổi hàng");
        lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.FOREGROUND);

        JLabel lblNote = new JLabel("Tìm kiếm và xem chi tiết phiếu đổi hàng trong hệ thống");
        lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNote.setForeground(Colors.MUTED);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(lblNote);

        pnl.add(header);
        pnl.add(buildFilterCard());
        return pnl;
    }

    // ─────────────────────────────────────────────────────────
    // FILTER CARD
    // ─────────────────────────────────────────────────────────
    private JPanel buildFilterCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        pnlNorth.setOpaque(false);
        pnlNorth.add(buildCardTitleRow());
        card.add(pnlNorth, BorderLayout.NORTH);

        // ===Hang tat ca fields: 4 cot deu nhau===
        JPanel pnlFields = new JPanel();
        pnlFields.setLayout(new BoxLayout(pnlFields, BoxLayout.X_AXIS));
        pnlFields.setOpaque(false);
        pnlFields.setBorder(BorderFactory.createEmptyBorder(4, 12, 12, 12));

        JPanel pairTimKiem = new JPanel();
        pairTimKiem.setLayout(new BoxLayout(pairTimKiem, BoxLayout.Y_AXIS));
        pairTimKiem.setOpaque(false);
        pairTimKiem.setAlignmentY(Component.TOP_ALIGNMENT);
        JLabel lblTimKiemTheo = fieldLabel("Tìm kiếm theo");
        lblTimKiemTheo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTimKiem.add(lblTimKiemTheo);
        pairTimKiem.add(Box.createVerticalStrut(4));
        cboTimKiemTheo = new RoundedComboBox<>(10);
        cboTimKiemTheo.addItem("Mã phiếu đổi");
        cboTimKiemTheo.addItem("Khách hàng");
        cboTimKiemTheo.addItem("Nhân viên xử lý");
        cboTimKiemTheo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cboTimKiemTheo.setMaximumSize(new Dimension(250, 44));
        pairTimKiem.add(cboTimKiemTheo);
        pnlFields.add(pairTimKiem);
        pnlFields.add(Box.createHorizontalStrut(10));

        JPanel pairTuKhoa = new JPanel();
        pairTuKhoa.setLayout(new BoxLayout(pairTuKhoa, BoxLayout.Y_AXIS));
        pairTuKhoa.setOpaque(false);
        pairTuKhoa.setAlignmentY(Component.TOP_ALIGNMENT);
        JLabel lblTuKhoa = fieldLabel("Từ khóa");
        lblTuKhoa.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTuKhoa.add(lblTuKhoa);
        pairTuKhoa.add(Box.createVerticalStrut(4));
        txtKeyword = new RoundedTextField(650, 44, 10, "Nhập mã phiếu đổi...");
        txtKeyword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtKeyword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        pairTuKhoa.add(txtKeyword);
        pnlFields.add(pairTuKhoa);
        pnlFields.add(Box.createHorizontalStrut(10));

        JPanel pairTuNgay = new JPanel();
        pairTuNgay.setLayout(new BoxLayout(pairTuNgay, BoxLayout.Y_AXIS));
        pairTuNgay.setOpaque(false);
        pairTuNgay.setAlignmentY(Component.TOP_ALIGNMENT);
        JLabel lblTuNgay = fieldLabel("Từ ngày");
        lblTuNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTuNgay.add(lblTuNgay);
        pairTuNgay.add(Box.createVerticalStrut(4));
        dtcTuNgay = new JDateChooser();
        dtcTuNgay.setDateFormatString("dd/MM/yyyy");
        dtcTuNgay.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        dtcTuNgay.setDate(new java.util.Date());
        dtcTuNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        dtcTuNgay.setMaximumSize(new Dimension(250, 44));
        pairTuNgay.add(dtcTuNgay);
        pnlFields.add(pairTuNgay);
        pnlFields.add(Box.createHorizontalStrut(10));

        JPanel pairDenNgay = new JPanel();
        pairDenNgay.setLayout(new BoxLayout(pairDenNgay, BoxLayout.Y_AXIS));
        pairDenNgay.setOpaque(false);
        pairDenNgay.setAlignmentY(Component.TOP_ALIGNMENT);
        JLabel lblDenNgay = fieldLabel("Đến ngày");
        lblDenNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairDenNgay.add(lblDenNgay);
        pairDenNgay.add(Box.createVerticalStrut(4));
        dtcDenNgay = new JDateChooser();
        dtcDenNgay.setDateFormatString("dd/MM/yyyy");
        dtcDenNgay.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        dtcDenNgay.setDate(new java.util.Date());
        dtcDenNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        dtcDenNgay.setMaximumSize(new Dimension(250, 44));
        pairDenNgay.add(dtcDenNgay);
        pnlFields.add(pairDenNgay);

        card.add(pnlFields, BorderLayout.CENTER);

        // ===Hang nut bam can phai===
        pairButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pairButton.setOpaque(false);
        pairButton.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        btnXoaLoc = new RoundedButton(130, 44, 10, "Xóa lọc", Colors.SECONDARY);
        btnXoaLoc.setForeground(Colors.TEXT_PRIMARY);
        pairButton.add(btnXoaLoc);
        btnTimKiem = new RoundedButton(150, 44, 10, "Tìm kiếm", Colors.PRIMARY);
        pairButton.add(btnTimKiem);
        card.add(pairButton, BorderLayout.SOUTH);

        cboTimKiemTheo.addActionListener(e -> {
            updatePlaceholder();
            updateSuggestionMode();
        });
        txtKeyword.addActionListener(e -> search());
        btnTimKiem.addActionListener(e -> search());
        btnXoaLoc.addActionListener(e -> resetFilter());

        initSuggestionPopups();

        return card;
    }

    private void initSuggestionPopups() {
        khSuggest = new SearchSuggestionPopup<>(txtKeyword);
        khSuggest.setSource(
                () -> {
                    java.util.LinkedHashMap<String, KhachHang> map = new java.util.LinkedHashMap<>();
                    for (HoaDon hd : fullList) {
                        KhachHang kh = hd.getKhachHang();
                        if (kh != null && kh.getMaKhachHang() != null) {
                            map.putIfAbsent(kh.getMaKhachHang(), kh);
                        }
                    }
                    return map.values();
                },
                kh -> kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "",
                kh -> kh.getTenKhachHang() != null ? kh.getTenKhachHang() : "",
                (kh, kw) -> kh.getSoDienThoai() != null && kh.getSoDienThoai().toLowerCase().contains(kw)
        );
        khSuggest.setOnSelect(kh -> {
            khSuggest.setTextSilently(kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "");
            search();
        });
        khSuggest.setEnabled(false);

        nvSuggest = new SearchSuggestionPopup<>(txtKeyword);
        nvSuggest.setSource(
                () -> {
                    java.util.LinkedHashMap<String, NhanVien> map = new java.util.LinkedHashMap<>();
                    for (HoaDon hd : fullList) {
                        NhanVien nv = hd.getNhanVien();
                        if (nv != null && nv.getMaNhanVien() != null) {
                            map.putIfAbsent(nv.getMaNhanVien(), nv);
                        }
                    }
                    return map.values();
                },
                nv -> nv.getTenNhanVien() != null ? nv.getTenNhanVien() : "",
                nv -> nv.getMaNhanVien() != null ? nv.getMaNhanVien() : "",
                (nv, kw) -> nv.getTenNhanVien() != null && nv.getTenNhanVien().toLowerCase().contains(kw)
        );
        nvSuggest.setOnSelect(nv -> {
            nvSuggest.setTextSilently(nv.getTenNhanVien());
            search();
        });
        nvSuggest.setEnabled(false);
    }

    private void updateSuggestionMode() {
        if (khSuggest == null || nvSuggest == null) {
            return;
        }
        String s = (String) cboTimKiemTheo.getSelectedItem();
        boolean kh = "Khách hàng".equals(s);
        boolean nv = "Nhân viên xử lý".equals(s);
        khSuggest.setEnabled(kh);
        nvSuggest.setEnabled(nv);
    }

    private JPanel buildCardTitleRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);
        JLabel title = new JLabel("Bộ lọc tìm kiếm");
        title.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        title.setForeground(Colors.PRIMARY);
        row.add(title);
        return row;
    }

    // ─────────────────────────────────────────────────────────
    // RESULTS PANEL
    // ─────────────────────────────────────────────────────────
    private JPanel buildResultsPanel() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 0));

        JPanel bar = new JPanel(new BorderLayout());
        bar.setOpaque(false);
        bar.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        left.setOpaque(false);
        JLabel lblResultTitle = new JLabel("Kết quả tìm kiếm");
        lblResultTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblResultTitle.setForeground(Colors.TEXT_PRIMARY);
        left.add(lblResultTitle);

        lblSoLuong = new JLabel("Tìm thấy 0 phiếu đổi");
        lblSoLuong.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSoLuong.setForeground(Colors.PRIMARY);

        bar.add(left, BorderLayout.WEST);
        bar.add(lblSoLuong, BorderLayout.EAST);
        card.add(bar, BorderLayout.NORTH);

        tblDoiHang = new StyledTable(COLUMN_NAMES, filteredList);

        tblDoiHang.setColumnRenderer(0, (tbl, val, sel, foc, row, col) -> {
            JLabel lbl = new JLabel();
            lbl.setOpaque(true);
            lbl.setText(val instanceof HoaDon ? ((HoaDon) val).getMaHoaDon() : "");
            lbl.setForeground(Colors.PRIMARY);
            lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
            lbl.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            return lbl;
        });
        tblDoiHang.setColumnWidth(0, 150);

        tblDoiHang.setTwoLineColumn(1, 210,
                v -> safeKHName((HoaDon) v),
                v -> MaskUtil.phone(safeKHPhone((HoaDon) v)));

        tblDoiHang.setTwoLineColumn(2, 190,
                v -> safeNVName((HoaDon) v),
                v -> safeNVMa((HoaDon) v));

        tblDoiHang.setSingleTextColumn(3, 150,
                v -> {
                    LocalDateTime d = ((HoaDon) v).getNgayLap();
                    return d != null ? d.format(DATE_FMT) : "";
                });

        tblDoiHang.setColumnRenderer(4, (tbl, val, sel, foc, row, col) -> {
            JLabel lbl = new JLabel();
            lbl.setOpaque(true);
            lbl.setText(val instanceof HoaDon
                    ? MONEY_FMT.format(((HoaDon) val).getTongTien()) + " đ" : "");
            lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            lbl.setForeground(Colors.TEXT_PRIMARY);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
            lbl.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            return lbl;
        });
        tblDoiHang.setColumnWidth(4, 150);

        tblDoiHang.setActionColumn(5, 100);
        tblDoiHang.setActionColumnListener((row, obj) -> moChiTiet((HoaDon) obj));

        card.add(tblDoiHang, BorderLayout.CENTER);
        return card;
    }

    // ─────────────────────────────────────────────────────────
    // DATA & LOGIC
    // ─────────────────────────────────────────────────────────
    public void refresh() {
        AsyncLoader.run(
            () -> hoaDonSV.layDSTheoTrangThai(HoaDon.TRANG_THAI_DOI_HANG),
            ds -> {
                fullList.clear();
                if (ds != null) fullList.addAll(ds);
                filteredList.clear();
                filteredList.addAll(fullList);
                if (tblDoiHang != null) tblDoiHang.refresh();
                updateCountLabel();
                if (tblDoiHang != null) search();
            }
        );
    }

    private void loadData() {
        refresh();
    }

    private void search() {
        String loai = (String) cboTimKiemTheo.getSelectedItem();
        String kw = txtKeyword.getText().trim().toLowerCase();
        LocalDateTime tuNgay = dtcTuNgay.getDate() != null
                ? dtcTuNgay.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null;
        LocalDateTime denNgay = dtcDenNgay.getDate() != null
                ? dtcDenNgay.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime() : null;
        if (tuNgay != null && denNgay != null && tuNgay.toLocalDate().isAfter(denNgay.toLocalDate())) {
            dtcTuNgay.setDate(null);
            dtcDenNgay.setDate(null);
            tuNgay = null;
            denNgay = null;
        }
        if (tuNgay != null) {
            tuNgay = tuNgay.toLocalDate().atStartOfDay();
        }
        if (denNgay != null) {
            denNgay = denNgay.toLocalDate().atTime(23, 59, 59);
        }

        filteredList.clear();
        for (HoaDon hd : fullList) {
            if (!kw.isEmpty() && !matchKeyword(hd, loai, kw)) {
                continue;
            }
            LocalDateTime ngay = hd.getNgayLap();
            if (ngay != null) {
                if (tuNgay != null && ngay.isBefore(tuNgay)) {
                    continue;
                }
                if (denNgay != null && ngay.isAfter(denNgay)) {
                    continue;
                }
            }
            filteredList.add(hd);
        }
        tblDoiHang.refresh();
        updateCountLabel();
    }

    private boolean matchKeyword(HoaDon hd, String loai, String kw) {
        if (loai == null) {
            return true;
        }
        switch (loai) {
            case "Mã phiếu đổi":
                return hd.getMaHoaDon() != null && hd.getMaHoaDon().toLowerCase().contains(kw);
            case "Khách hàng":
                return safeKHPhone(hd).toLowerCase().contains(kw);
            case "Nhân viên xử lý":
                return safeNVName(hd).toLowerCase().contains(kw);
            default:
                return true;
        }
    }

    private void resetFilter() {
        txtKeyword.setText("");
        cboTimKiemTheo.setSelectedIndex(0);
        dtcTuNgay.setDate(new java.util.Date());
        dtcDenNgay.setDate(new java.util.Date());
        filteredList.clear();
        filteredList.addAll(fullList);
        tblDoiHang.refresh();
        updateCountLabel();
    }

    private void moChiTiet(HoaDon hd) {
        KhachHang kh = hd.getKhachHang() != null
                ? khachHangSV.layKHTheoMa(hd.getKhachHang().getMaKhachHang()) : null;
        NhanVien nv = hd.getNhanVien() != null
                ? nhanVienSV.layNVTheoMa(hd.getNhanVien().getMaNhanVien()) : null;
        PhuongThucThanhToan pttt = hd.getMaPTTT() != null
                ? hoaDonSV.layPTTTTheoMa(hd.getMaPTTT()) : null;
        List<ChiTietHoaDon> chiTiets = chiTietHoaDonSV.getChiTietTheoHoaDon(hd.getMaHoaDon());

        LinkedHashMap<String, String> leftInfo = new LinkedHashMap<>();
        leftInfo.put("Họ tên", kh != null ? kh.getTenKhachHang() : "Khách lẻ");
        leftInfo.put("ĐT", kh != null && kh.getSoDienThoai() != null ? MaskUtil.phone(kh.getSoDienThoai()) : "---");
        leftInfo.put("Email", kh != null && kh.getEmail() != null ? MaskUtil.email(kh.getEmail()) : "---");
        leftInfo.put("Điểm tích lũy", kh != null ? String.valueOf(kh.getDiemTichLuy()) : "0");

        DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        LinkedHashMap<String, String> rightInfo = new LinkedHashMap<>();
        rightInfo.put("Mã phiếu đổi", "<font color='" + toHex(Colors.PRIMARY) + "'>" + hd.getMaHoaDon() + "</font>");
        rightInfo.put("Nhân viên", nv != null
                ? nv.getTenNhanVien() + " (" + nv.getMaNhanVien() + ")" : "---");
        rightInfo.put("Thời gian", hd.getNgayLap() != null ? hd.getNgayLap().format(dtFmt) : "---");
        rightInfo.put("Thanh toán", pttt != null ? pttt.getTenPTTT()
                : (hd.getMaPTTT() != null ? hd.getMaPTTT() : "---"));

        String[] cols = {"STT", "Mã SP", "Tên sản phẩm", "ĐVT", "SL", "Đơn giá", "Thành tiền"};
        List<Object[]> rows = new ArrayList<>();
        int stt = 1;
        for (ChiTietHoaDon ct : chiTiets) {
            SanPham sp = ct.getSanPham();
            if (sp != null && (sp.getTenSanPham() == null || sp.getDonViTinh() == null)) {
                SanPham full = sanPhamSV.laySanPhamTheoMa(sp.getMaSanPham());
                if (full != null) {
                    sp = full;
                }
            }
            String maSP = sp != null ? sp.getMaSanPham() : "";
            String tenSP = sp != null && sp.getTenSanPham() != null ? sp.getTenSanPham() : "";
            String dvt = sp != null && sp.getDonViTinh() != null ? sp.getDonViTinh() : "";
            double thanhTienDong = ct.getSoLuong() * ct.getDonGia();
            rows.add(new Object[]{stt++, maSP, tenSP, dvt, ct.getSoLuong(),
                MONEY_FMT.format(ct.getDonGia()) + " đ",
                MONEY_FMT.format(thanhTienDong) + " đ"});
        }

        List<ChiTietDialog.SummaryRow> summary = new ArrayList<>();
        double tienHang = hd.getTienHang() > 0 ? hd.getTienHang() : hd.getTongTien();
        double tienGiam = hd.getTienGiamGia();
        double tienThue = hd.getTienThue();
        double diemQuyTien = hd.getDiemSuDung() * 1000.0;
        double thanhTien = hd.getThanhTien() > 0 ? hd.getThanhTien()
                : Math.max(0, tienHang + tienThue - tienGiam - diemQuyTien);

        summary.add(new ChiTietDialog.SummaryRow("Tạm tính",
                MONEY_FMT.format(tienHang) + " đ", null, false));
        if (tienGiam > 0) {
            summary.add(new ChiTietDialog.SummaryRow("Giảm giá",
                    "-" + MONEY_FMT.format(tienGiam) + " đ", new Color(220, 53, 69), false));
        }
        if (tienThue > 0) {
            summary.add(new ChiTietDialog.SummaryRow("Thuế VAT",
                    MONEY_FMT.format(tienThue) + " đ", null, false));
        }
        if (diemQuyTien > 0) {
            summary.add(new ChiTietDialog.SummaryRow(
                    "Điểm sử dụng (" + hd.getDiemSuDung() + " điểm)",
                    "-" + MONEY_FMT.format(diemQuyTien) + " đ", new Color(220, 53, 69), false));
        }
        summary.add(new ChiTietDialog.SummaryRow("Tổng thanh toán",
                MONEY_FMT.format(thanhTien) + " đ", Colors.PRIMARY, true));

        new ChiTietDialog(
                SwingUtilities.getWindowAncestor(this),
                "DH",
                "Chi tiết phiếu đổi hàng: " + hd.getMaHoaDon(),
                "KH", "Thông tin khách hàng", leftInfo,
                "DH", "Thông tin phiếu đổi", rightInfo,
                "Danh sách sản phẩm",
                cols, rows, new int[]{0, 4, 5, 6},
                summary
        ).setVisible(true);
    }

    private String toHex(Color c) {
        return String.format("#%02x%02x%02x", c.getRed(), c.getGreen(), c.getBlue());
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────
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
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, l.getPreferredSize().height));
        return l;
    }

    private void updatePlaceholder() {
        String s = (String) cboTimKiemTheo.getSelectedItem();
        if (s == null) {
            return;
        }
        switch (s) {
            case "Mã phiếu đổi":
                txtKeyword.setPlaceholder("Nhập mã phiếu đổi...");
                break;
            case "Khách hàng":
                txtKeyword.setPlaceholder("Nhập tên hoặc SĐT khách hàng...");
                break;
            case "Nhân viên xử lý":
                txtKeyword.setPlaceholder("Nhập tên hoặc mã nhân viên...");
                break;
        }
    }

    private void updateCountLabel() {
        lblSoLuong.setText("Tìm thấy " + filteredList.size() + " phiếu đổi");
    }

    private String safeKHName(HoaDon hd) {
        return hd.getKhachHang() != null && hd.getKhachHang().getTenKhachHang() != null
                ? hd.getKhachHang().getTenKhachHang() : "Khách vãng lai";
    }

    private String safeKHPhone(HoaDon hd) {
        return hd.getKhachHang() != null && hd.getKhachHang().getSoDienThoai() != null
                ? hd.getKhachHang().getSoDienThoai() : "Không có";
    }

    private String safeNVName(HoaDon hd) {
        return hd.getNhanVien() != null && hd.getNhanVien().getTenNhanVien() != null
                ? hd.getNhanVien().getTenNhanVien() : "";
    }

    private String safeNVMa(HoaDon hd) {
        return hd.getNhanVien() != null && hd.getNhanVien().getMaNhanVien() != null
                ? hd.getNhanVien().getMaNhanVien() : "";
    }
}
