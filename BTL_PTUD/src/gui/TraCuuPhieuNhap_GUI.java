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
import service.NhaCungCap_Service;
import service.NhanVien_Service;
import service.PhieuNhap_Service;
import service.SanPham_Service;

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
    private JSpinner spnTuNgay;
    private JSpinner spnDenNgay;
    private boolean tuNgayActive = false;
    private boolean denNgayActive = false;
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
        "M\u00e3 phi\u1ebfu nh\u1eadp", "Nh\u00e0 cung c\u1ea5p", "Nh\u00e2n vi\u00ean l\u1eadp",
        "Ng\u00e0y nh\u1eadp", "Ghi ch\u00fa", "Thao t\u00e1c"
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

        JLabel lblTitle = new JLabel("Tra c\u1ee9u phi\u1ebfu nh\u1eadp");
        lblTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.TEXT_PRIMARY);

        JLabel lblNote = new JLabel("T\u00ecm ki\u1ebfm v\u00e0 xem chi ti\u1ebft phi\u1ebfu nh\u1eadp h\u00e0ng trong h\u1ec7 th\u1ed1ng");
        lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNote.setForeground(Colors.TEXT_SECONDARY);

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

        //===== NORTH – Ti\u00eau \u0111\u1ec1 =====
        JPanel pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        pnlNorth.setOpaque(false);
        pnlNorth.add(buildCardTitleRow());
        card.add(pnlNorth, BorderLayout.NORTH);

        //===== CENTER – T\u00ecm ki\u1ebfm =====
        JPanel pnlCenter = new JPanel();
        pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.X_AXIS));
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(4, 12, 8, 12));

        JPanel pairTimKiem = new JPanel();
        pairTimKiem.setLayout(new BoxLayout(pairTimKiem, BoxLayout.Y_AXIS));
        pairTimKiem.setOpaque(false);
        JLabel lblTimKiemTheo = fieldLabel("T\u00ecm ki\u1ebfm theo");
        lblTimKiemTheo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTimKiem.add(lblTimKiemTheo);
        pairTimKiem.add(Box.createVerticalStrut(4));
        cboTimKiemTheo = new RoundedComboBox<>(10);
        cboTimKiemTheo.addItem("M\u00e3 phi\u1ebfu nh\u1eadp");
        cboTimKiemTheo.addItem("Nh\u00e0 cung c\u1ea5p");
        cboTimKiemTheo.addItem("Nh\u00e2n vi\u00ean l\u1eadp");
        cboTimKiemTheo.setPreferredSize(new Dimension(200, 50));
        cboTimKiemTheo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTimKiem.add(cboTimKiemTheo);
        pairTimKiem.setMaximumSize(new Dimension(216, Integer.MAX_VALUE));
        pnlCenter.add(pairTimKiem);
        pnlCenter.add(Box.createHorizontalStrut(16));

        JPanel pairTuKhoa = new JPanel();
        pairTuKhoa.setLayout(new BoxLayout(pairTuKhoa, BoxLayout.Y_AXIS));
        pairTuKhoa.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        pairTuKhoa.setOpaque(false);
        JLabel lblTuKhoa = fieldLabel("T\u1eeb kh\u00f3a");
        lblTuKhoa.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTuKhoa.add(lblTuKhoa);
        pairTuKhoa.add(Box.createVerticalStrut(4));
        txtKeyword = new RoundedTextField(800, 50, 10, "Nh\u1eadp m\u00e3 phi\u1ebfu nh\u1eadp...");
        txtKeyword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtKeyword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pairTuKhoa.add(txtKeyword);
        pairTuKhoa.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        pnlCenter.add(pairTuKhoa);
        card.add(pnlCenter, BorderLayout.CENTER);

        //===== SOUTH – Ng\u00e0y + n\u00fat =====
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlSouth.setOpaque(false);
        pnlSouth.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));

        JPanel pairTuNgay = new JPanel();
        pairTuNgay.setLayout(new BoxLayout(pairTuNgay, BoxLayout.Y_AXIS));
        pairTuNgay.setOpaque(false);
        JLabel lblTuNgay = fieldLabel("T\u1eeb ng\u00e0y");
        lblTuNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTuNgay.add(lblTuNgay);
        pairTuNgay.add(Box.createVerticalStrut(4));
        spnTuNgay = makeDateSpinner();
        spnTuNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTuNgay.add(spnTuNgay);
        pnlSouth.add(pairTuNgay);
        pnlSouth.add(Box.createHorizontalStrut(10));

        JPanel pairDenNgay = new JPanel();
        pairDenNgay.setLayout(new BoxLayout(pairDenNgay, BoxLayout.Y_AXIS));
        pairDenNgay.setOpaque(false);
        JLabel lblDenNgay = fieldLabel("\u0110\u1ebfn ng\u00e0y");
        lblDenNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairDenNgay.add(lblDenNgay);
        pairDenNgay.add(Box.createVerticalStrut(4));
        spnDenNgay = makeDateSpinner();
        spnDenNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairDenNgay.add(spnDenNgay);
        pnlSouth.add(pairDenNgay);
        pnlSouth.add(Box.createHorizontalStrut(345));

        pairButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pairButton.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        pairButton.setBackground(Colors.BACKGROUND);
        btnXoaLoc = new RoundedButton(130, 50, 10, "\u2715  X\u00f3a l\u1ecdc", Colors.SECONDARY);
        btnXoaLoc.setForeground(Colors.TEXT_PRIMARY);
        pairButton.add(btnXoaLoc);
        btnTimKiem = new RoundedButton(150, 50, 10, "\uD83D\uDD0D  T\u00ecm ki\u1ebfm", Colors.PRIMARY);
        pairButton.add(btnTimKiem);
        pnlSouth.add(pairButton);
        card.add(pnlSouth, BorderLayout.SOUTH);

        cboTimKiemTheo.addActionListener(e -> {
            updatePlaceholder();
            updateSuggestionMode();
        });
        txtKeyword.addActionListener(e -> search());
        btnTimKiem.addActionListener(e -> search());
        btnXoaLoc.addActionListener(e -> resetFilter());
        spnTuNgay.addChangeListener(e -> tuNgayActive = true);
        spnDenNgay.addChangeListener(e -> denNgayActive = true);

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
        JLabel icon = new JLabel("\uD83D\uDD0D");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        icon.setForeground(Colors.PRIMARY);
        JLabel title = new JLabel("B\u1ed9 l\u1ecdc t\u00ecm ki\u1ebfm");
        title.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        title.setForeground(Colors.PRIMARY);
        row.add(icon);
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
        JLabel iconDoc = new JLabel("\uD83D\uDCC4");
        iconDoc.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        JLabel lblResultTitle = new JLabel("K\u1ebft qu\u1ea3 t\u00ecm ki\u1ebfm");
        lblResultTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblResultTitle.setForeground(Colors.TEXT_PRIMARY);
        left.add(iconDoc);
        left.add(lblResultTitle);

        lblSoLuong = new JLabel("T\u00ecm th\u1ea5y 0 phi\u1ebfu nh\u1eadp");
        lblSoLuong.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSoLuong.setForeground(Colors.PRIMARY);

        bar.add(left, BorderLayout.WEST);
        bar.add(lblSoLuong, BorderLayout.EAST);
        card.add(bar, BorderLayout.NORTH);

        tblPhieuNhap = new StyledTable(COLUMN_NAMES, filteredList);

        //===== C\u1ed9t 0 – M\u00e3 phi\u1ebfu nh\u1eadp =====
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

        //===== C\u1ed9t 1 – Nh\u00e0 cung c\u1ea5p: d\u00f2ng 1 = t\u00ean NCC, d\u00f2ng 2 = m\u00e3 NCC =====
        tblPhieuNhap.setTwoLineColumn(1, 210,
                v -> safeNCCName((PhieuNhap) v),
                v -> safeNCCMa((PhieuNhap) v));

        //===== C\u1ed9t 2 – Nh\u00e2n vi\u00ean l\u1eadp: d\u00f2ng 1 = t\u00ean NV, d\u00f2ng 2 = m\u00e3 NV =====
        tblPhieuNhap.setTwoLineColumn(2, 190,
                v -> safeNVName((PhieuNhap) v),
                v -> safeNVMa((PhieuNhap) v));

        //===== C\u1ed9t 3 – Ng\u00e0y nh\u1eadp =====
        tblPhieuNhap.setSingleTextColumn(3, 130,
                v -> {
                    LocalDate d = ((PhieuNhap) v).getNgayNhap();
                    return d != null ? d.format(DATE_FMT) : "";
                });

        //===== C\u1ed9t 4 – Ghi ch\u00fa =====
        tblPhieuNhap.setSingleTextColumn(4, 200,
                v -> {
                    String gc = ((PhieuNhap) v).getGhiChu();
                    return gc != null ? gc : "";
                });

        //===== C\u1ed9t 5 – Thao t\u00e1c =====
        tblPhieuNhap.setActionColumn(5, 100);
        tblPhieuNhap.setActionColumnListener((row, obj) -> moChiTietPhieuNhap((PhieuNhap) obj));

        card.add(tblPhieuNhap, BorderLayout.CENTER);
        return card;
    }

    // ─────────────────────────────────────────────────────────
    // DATA & LOGIC
    // ─────────────────────────────────────────────────────────
    private void loadData() {
        try {
            ArrayList<PhieuNhap> ds = phieuNhapSV.getDSPhieuNhap();
            fullList.clear();
            if (ds != null) {
                fullList.addAll(ds);
            }
            filteredList.clear();
            filteredList.addAll(fullList);
            tblPhieuNhap.refresh();
            updateCountLabel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void search() {
        String loai = (String) cboTimKiemTheo.getSelectedItem();
        String kw = txtKeyword.getText().trim().toLowerCase();
        LocalDate tuNgay = tuNgayActive ? spinnerToDate(spnTuNgay) : null;
        LocalDate denNgay = denNgayActive ? spinnerToDate(spnDenNgay) : null;

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
            case "M\u00e3 phi\u1ebfu nh\u1eadp":
                return pn.getMaPhieuNhap() != null && pn.getMaPhieuNhap().toLowerCase().contains(kw);
            case "Nh\u00e0 cung c\u1ea5p":
                return safeNCCName(pn).toLowerCase().contains(kw)
                        || safeNCCMa(pn).toLowerCase().contains(kw);
            case "Nh\u00e2n vi\u00ean l\u1eadp":
                return safeNVName(pn).toLowerCase().contains(kw)
                        || safeNVMa(pn).toLowerCase().contains(kw);
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
                "\uD83D\uDCE6",
                "Chi tiết phiếu nhập: " + pn.getMaPhieuNhap(),
                "\uD83C\uDFEC", "Thông tin nhà cung cấp", leftInfo,
                "\uD83D\uDCC4", "Thông tin phiếu nhập", rightInfo,
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
        return l;
    }

    private JSpinner makeDateSpinner() {
        JSpinner sp = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor ed = new JSpinner.DateEditor(sp, "dd/MM/yyyy");
        sp.setEditor(ed);
        sp.setPreferredSize(new Dimension(160, 42));
        sp.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        return sp;
    }

    private void updatePlaceholder() {
        String s = (String) cboTimKiemTheo.getSelectedItem();
        if (s == null) {
            return;
        }
        switch (s) {
            case "M\u00e3 phi\u1ebfu nh\u1eadp":
                txtKeyword.setPlaceholder("Nh\u1eadp m\u00e3 phi\u1ebfu nh\u1eadp...");
                break;
            case "Nh\u00e0 cung c\u1ea5p":
                txtKeyword.setPlaceholder("Nh\u1eadp t\u00ean ho\u1eb7c m\u00e3 nh\u00e0 cung c\u1ea5p...");
                break;
            case "Nh\u00e2n vi\u00ean l\u1eadp":
                txtKeyword.setPlaceholder("Nh\u1eadp t\u00ean ho\u1eb7c m\u00e3 nh\u00e2n vi\u00ean...");
                break;
        }
    }

    private void updateCountLabel() {
        lblSoLuong.setText("T\u00ecm th\u1ea5y " + filteredList.size() + " phi\u1ebfu nh\u1eadp");
    }

    private LocalDate spinnerToDate(JSpinner sp) {
        try {
            java.util.Date d = (java.util.Date) sp.getValue();
            return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        } catch (Exception e) {
            return null;
        }
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
