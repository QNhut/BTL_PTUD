package gui;

import constants.Colors;
import constants.FontStyle;
import entity.ChiTietPhieuNhap;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhap;
import entity.SanPham;
import exception.RoundedButton;
import exception.RoundedComboBox;
import exception.RoundedTextField;
import exception.StyledTable;

import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import service.NhaCungCap_Service;
import service.NhanVien_Service;
import service.PhieuNhap_Service;
import service.SanPham_Service;
import util.AsyncLoader;

@SuppressWarnings("serial")
public class TraCuuPhieuNhap_GUI extends JPanel {

    // ── Services / Data ────────────────────────────────────────
    private final PhieuNhap_Service phieuNhapSV = new PhieuNhap_Service();
    private final NhaCungCap_Service nhaCungCapSV = new NhaCungCap_Service();
    private final NhanVien_Service nhanVienSV = new NhanVien_Service();
    private final SanPham_Service sanPhamSV = new SanPham_Service();
    private final ArrayList<PhieuNhap> fullList = new ArrayList<>();
    private final ArrayList<PhieuNhap> filteredList = new ArrayList<>();

    // ── Filter controls ────────────────────────────────────────
    private RoundedComboBox<String> cboTimKiemTheo;
    private RoundedTextField txtKeyword;
    private JDateChooser dtcTuNgay;
    private JDateChooser dtcDenNgay;
    private RoundedButton btnTimKiem;
    private RoundedButton btnXoaLoc;

    // ── Results area ───────────────────────────────────────────
    private JLabel lblSoLuong;
    private StyledTable tblPhieuNhap;
    private JPanel pairButton;

    // ── Suggestion popup ───────────────────────────────────────
    private SearchSuggestionPopup<NhaCungCap> nccSuggest;
    private SearchSuggestionPopup<NhanVien> nvSuggest;

    private static final String[] COLUMN_NAMES = {
        "Mã phiếu nhập", "Nhà cung cấp", "Nhân viên lập",
        "Ngày nhập", "Ghi chú", "Thao tác"
    };

    private static final DateTimeFormatter DATE_FMT
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // ═══════════════════════════════════════════════════════════
    public TraCuuPhieuNhap_GUI() {
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

        JLabel lblTitle = new JLabel("Tra cứu phiếu nhập");
        lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.FOREGROUND);

        JLabel lblNote = new JLabel("Tìm kiếm và xem chi tiết phiếu nhập hàng trong hệ thống");
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

        //===== NORTH – Tiêu đề =====
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
        cboTimKiemTheo.addItem("Mã phiếu nhập");
        cboTimKiemTheo.addItem("Nhà cung cấp");
        cboTimKiemTheo.addItem("Nhân viên lập");
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
        txtKeyword = new RoundedTextField(650, 44, 10, "Nhập mã phiếu nhập...");
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

    //=====Khởi tạo popup gợi ý cho NCC và NV lập=====
    private void initSuggestionPopups() {
        nccSuggest = new SearchSuggestionPopup<>(txtKeyword);
        nccSuggest.setSource(
                () -> {
                    java.util.LinkedHashMap<String, NhaCungCap> map = new java.util.LinkedHashMap<>();
                    for (PhieuNhap pn : fullList) {
                        NhaCungCap ncc = pn.getNhaCungCap();
                        if (ncc != null && ncc.getMaNhaCungCap() != null) {
                            map.putIfAbsent(ncc.getMaNhaCungCap(), ncc);
                        }
                    }
                    return map.values();
                },
                ncc -> ncc.getTenNhaCungCap() != null ? ncc.getTenNhaCungCap() : "",
                ncc -> ncc.getMaNhaCungCap() != null ? ncc.getMaNhaCungCap() : "",
                (ncc, kw) -> (ncc.getTenNhaCungCap() != null && ncc.getTenNhaCungCap().toLowerCase().contains(kw))
                || (ncc.getMaNhaCungCap() != null && ncc.getMaNhaCungCap().toLowerCase().contains(kw))
        );
        nccSuggest.setOnSelect(ncc -> {
            nccSuggest.setTextSilently(ncc.getTenNhaCungCap());
            search();
        });
        nccSuggest.setEnabled(false);

        nvSuggest = new SearchSuggestionPopup<>(txtKeyword);
        nvSuggest.setSource(
                () -> {
                    java.util.LinkedHashMap<String, NhanVien> map = new java.util.LinkedHashMap<>();
                    for (PhieuNhap pn : fullList) {
                        NhanVien nv = pn.getNhanVien();
                        if (nv != null && nv.getMaNhanVien() != null) {
                            map.putIfAbsent(nv.getMaNhanVien(), nv);
                        }
                    }
                    return map.values();
                },
                nv -> nv.getTenNhanVien() != null ? nv.getTenNhanVien() : "",
                nv -> nv.getMaNhanVien() != null ? nv.getMaNhanVien() : "",
                (nv, kw) -> (nv.getTenNhanVien() != null && nv.getTenNhanVien().toLowerCase().contains(kw))
                || (nv.getMaNhanVien() != null && nv.getMaNhanVien().toLowerCase().contains(kw))
        );
        nvSuggest.setOnSelect(nv -> {
            nvSuggest.setTextSilently(nv.getTenNhanVien());
            search();
        });
        nvSuggest.setEnabled(false);
    }

    //=====Bật popup theo loại tìm kiếm=====
    private void updateSuggestionMode() {
        if (nccSuggest == null || nvSuggest == null) {
            return;
        }
        String s = (String) cboTimKiemTheo.getSelectedItem();
        nccSuggest.setEnabled("Nhà cung cấp".equals(s));
        nvSuggest.setEnabled("Nhân viên lập".equals(s));
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

        lblSoLuong = new JLabel("Tìm thấy 0 phiếu nhập");
        lblSoLuong.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSoLuong.setForeground(Colors.PRIMARY);

        bar.add(left, BorderLayout.WEST);
        bar.add(lblSoLuong, BorderLayout.EAST);
        card.add(bar, BorderLayout.NORTH);

        tblPhieuNhap = new StyledTable(COLUMN_NAMES, filteredList);

        //===== Cột 0 – Mã phiếu nhập =====
        tblPhieuNhap.setColumnRenderer(0, (tbl, val, sel, foc, row, col) -> {
            JLabel lbl = new JLabel();
            lbl.setOpaque(true);
            lbl.setText(val instanceof PhieuNhap ? ((PhieuNhap) val).getMaPhieuNhap() : "");
            lbl.setForeground(Colors.PRIMARY);
            lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
            lbl.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            return lbl;
        });
        tblPhieuNhap.setColumnWidth(0, 150);

        //===== Cột 1 – Nhà cung cấp: dòng 1 = tên NCC, dòng 2 = mã NCC =====
        tblPhieuNhap.setTwoLineColumn(1, 210,
                v -> safeNCCName((PhieuNhap) v),
                v -> safeNCCMa((PhieuNhap) v));

        //===== Cột 2 – Nhân viên lập: dòng 1 = tên NV, dòng 2 = mã NV =====
        tblPhieuNhap.setTwoLineColumn(2, 190,
                v -> safeNVName((PhieuNhap) v),
                v -> safeNVMa((PhieuNhap) v));

        //===== Cột 3 – Ngày nhập =====
        tblPhieuNhap.setSingleTextColumn(3, 130,
                v -> {
                    LocalDate d = ((PhieuNhap) v).getNgayNhap();
                    return d != null ? d.format(DATE_FMT) : "";
                });

        //===== Cột 4 – Ghi chú =====
        tblPhieuNhap.setSingleTextColumn(4, 200,
                v -> {
                    String gc = ((PhieuNhap) v).getGhiChu();
                    return gc != null ? gc : "";
                });

        //===== Cột 5 – Thao tác =====
        tblPhieuNhap.setActionColumn(5, 100);
        tblPhieuNhap.setActionColumnListener((row, obj) -> moChiTietPhieuNhap((PhieuNhap) obj));

        card.add(tblPhieuNhap, BorderLayout.CENTER);
        return card;
    }

    // ─────────────────────────────────────────────────────────
    // DATA & LOGIC
    // ─────────────────────────────────────────────────────────
    //===="Reload toàn bộ dữ liệu từ DB (gọi từ Main_GUI khi mở lại tab)"=====
    public void refresh() {
        loadData();
    }

    private void loadData() {
        try {
            ArrayList<PhieuNhap> ds = phieuNhapSV.getDSPhieuNhap();
            fullList.clear();
            if (ds != null) {
                // Cache để tránh query lại nhiều lần cùng 1 NCC/NV
                java.util.HashMap<String, NhaCungCap> cacheNCC = new java.util.HashMap<>();
                java.util.HashMap<String, NhanVien> cacheNV = new java.util.HashMap<>();
                for (PhieuNhap pn : ds) {
                    NhaCungCap ncc = pn.getNhaCungCap();
                    if (ncc != null && ncc.getMaNhaCungCap() != null && ncc.getTenNhaCungCap() == null) {
                        NhaCungCap full = cacheNCC.computeIfAbsent(
                                ncc.getMaNhaCungCap(), nhaCungCapSV::layNCCTheoMa);
                        if (full != null) {
                            pn.setNhaCungCap(full);
                        }
                    }
                    NhanVien nv = pn.getNhanVien();
                    if (nv != null && nv.getMaNhanVien() != null && nv.getTenNhanVien() == null) {
                        NhanVien full = cacheNV.computeIfAbsent(
                                nv.getMaNhanVien(), nhanVienSV::layNVTheoMa);
                        if (full != null) {
                            pn.setNhanVien(full);
                        }
                    }
                }
                fullList.addAll(ds);
            }
            filteredList.clear();
            filteredList.addAll(fullList);
            tblPhieuNhap.refresh();
            updateCountLabel();
            search();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void search() {
        String loai = (String) cboTimKiemTheo.getSelectedItem();
        String kw = txtKeyword.getText().trim().toLowerCase();
        LocalDate tuNgay = dtcTuNgay.getDate() != null
                ? dtcTuNgay.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate denNgay = dtcDenNgay.getDate() != null
                ? dtcDenNgay.getDate().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate() : null;

        if (tuNgay != null && denNgay != null && tuNgay.isAfter(denNgay)) {
            dtcTuNgay.setDate(null);
            dtcDenNgay.setDate(null);
            tuNgay = null;
            denNgay = null;
        }
        filteredList.clear();
        for (PhieuNhap pn : fullList) {
            if (!kw.isEmpty() && !matchKeyword(pn, loai, kw)) {
                continue;
            }
            LocalDate ngay = pn.getNgayNhap();
            if (ngay != null) {
                if (tuNgay != null && ngay.isBefore(tuNgay)) {
                    continue;
                }
                if (denNgay != null && ngay.isAfter(denNgay)) {
                    continue;
                }
            }
            filteredList.add(pn);
        }
        tblPhieuNhap.refresh();
        updateCountLabel();
    }

    private boolean matchKeyword(PhieuNhap pn, String loai, String kw) {
        if (loai == null) {
            return true;
        }
        switch (loai) {
            case "Mã phiếu nhập":
                return pn.getMaPhieuNhap() != null && pn.getMaPhieuNhap().toLowerCase().contains(kw);
            case "Nhà cung cấp":
                return safeNCCName(pn).toLowerCase().contains(kw)
                        || safeNCCMa(pn).toLowerCase().contains(kw);
            case "Nhân viên lập":
                return safeNVName(pn).toLowerCase().contains(kw)
                        || safeNVMa(pn).toLowerCase().contains(kw);
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
        tblPhieuNhap.refresh();
        updateCountLabel();
    }

    private static final NumberFormat MONEY_FMT
            = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private void moChiTietPhieuNhap(PhieuNhap pn) {
        // Tải dữ liệu chi tiết
        NhaCungCap ncc = pn.getNhaCungCap() != null
                ? nhaCungCapSV.layNCCTheoMa(pn.getNhaCungCap().getMaNhaCungCap()) : null;
        NhanVien nv = pn.getNhanVien() != null
                ? nhanVienSV.layNVTheoMa(pn.getNhanVien().getMaNhanVien()) : null;
        ArrayList<ChiTietPhieuNhap> chiTiets = phieuNhapSV.getChiTietTheoPhieuNhap(pn.getMaPhieuNhap());

        // Card trái – Thông tin nhà cung cấp
        LinkedHashMap<String, String> leftInfo = new LinkedHashMap<>();
        leftInfo.put("Tên NCC", ncc != null ? ncc.getTenNhaCungCap() : "---");
        leftInfo.put("Mã NCC", ncc != null ? ncc.getMaNhaCungCap() : "---");
        leftInfo.put("SĐT", ncc != null && ncc.getSoDienThoai() != null ? ncc.getSoDienThoai() : "---");
        leftInfo.put("Email", ncc != null && ncc.getEmail() != null ? ncc.getEmail() : "---");
        leftInfo.put("Địa chỉ", ncc != null && ncc.getDiaChi() != null ? ncc.getDiaChi() : "---");

        // Card phải – Thông tin phiếu nhập
        LinkedHashMap<String, String> rightInfo = new LinkedHashMap<>();
        rightInfo.put("Mã phiếu nhập", "<font color='" + toHex(Colors.PRIMARY) + "'>" + pn.getMaPhieuNhap() + "</font>");
        rightInfo.put("Nhân viên", nv != null
                ? nv.getTenNhanVien() + " (" + nv.getMaNhanVien() + ")" : "---");
        rightInfo.put("Ngày nhập", pn.getNgayNhap() != null ? pn.getNgayNhap().format(DATE_FMT) : "---");
        rightInfo.put("Ghi chú", pn.getGhiChu() != null && !pn.getGhiChu().isBlank() ? pn.getGhiChu() : "---");

        // Bảng sản phẩm
        String[] cols = {"STT", "Mã SP", "Tên sản phẩm", "ĐVT", "SL", "Đơn giá nhập", "Thành tiền"};
        List<Object[]> rows = new ArrayList<>();
        int stt = 1;
        double tongTien = 0;
        int tongSL = 0;
        for (ChiTietPhieuNhap ct : chiTiets) {
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
            double thanhTien = ct.getSoLuong() * ct.getGiaNhap();
            tongTien += thanhTien;
            tongSL += ct.getSoLuong();
            rows.add(new Object[]{stt++, maSP, tenSP, dvt, ct.getSoLuong(),
                MONEY_FMT.format(ct.getGiaNhap()) + " đ",
                MONEY_FMT.format(thanhTien) + " đ"});
        }

        // Tổng kết
        List<ChiTietDialog.SummaryRow> summary = new ArrayList<>();
        summary.add(new ChiTietDialog.SummaryRow("Tổng số mặt hàng",
                String.valueOf(chiTiets.size()), null, false));
        summary.add(new ChiTietDialog.SummaryRow("Tổng số lượng",
                String.valueOf(tongSL), null, false));
        summary.add(new ChiTietDialog.SummaryRow("Tổng tiền nhập",
                MONEY_FMT.format(tongTien) + " đ", Colors.PRIMARY, true));

        new ChiTietDialog(
                SwingUtilities.getWindowAncestor(this),
                "PN",
                "Chi tiết phiếu nhập: " + pn.getMaPhieuNhap(),
                "NCC", "Thông tin nhà cung cấp", leftInfo,
                "PN", "Thông tin phiếu nhập", rightInfo,
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
            case "Mã phiếu nhập":
                txtKeyword.setPlaceholder("Nhập mã phiếu nhập...");
                break;
            case "Nhà cung cấp":
                txtKeyword.setPlaceholder("Nhập tên hoặc mã nhà cung cấp...");
                break;
            case "Nhân viên lập":
                txtKeyword.setPlaceholder("Nhập tên hoặc mã nhân viên...");
                break;
        }
    }

    private void updateCountLabel() {
        lblSoLuong.setText("Tìm thấy " + filteredList.size() + " phiếu nhập");
    }

    private String safeNCCName(PhieuNhap pn) {
        return pn.getNhaCungCap() != null && pn.getNhaCungCap().getTenNhaCungCap() != null
                ? pn.getNhaCungCap().getTenNhaCungCap() : "";
    }

    private String safeNCCMa(PhieuNhap pn) {
        return pn.getNhaCungCap() != null && pn.getNhaCungCap().getMaNhaCungCap() != null
                ? pn.getNhaCungCap().getMaNhaCungCap() : "";
    }

    private String safeNVName(PhieuNhap pn) {
        return pn.getNhanVien() != null && pn.getNhanVien().getTenNhanVien() != null
                ? pn.getNhanVien().getTenNhanVien() : "";
    }

    private String safeNVMa(PhieuNhap pn) {
        return pn.getNhanVien() != null && pn.getNhanVien().getMaNhanVien() != null
                ? pn.getNhanVien().getMaNhanVien() : "";
    }
}
