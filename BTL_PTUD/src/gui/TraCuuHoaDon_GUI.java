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
import service.ChiTietHoaDon_Service;
import service.HoaDon_Service;
import service.KhachHang_Service;
import service.NhanVien_Service;
import service.SanPham_Service;

public class TraCuuHoaDon_GUI extends JPanel {

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
    private JSpinner spnTuNgay;
    private JSpinner spnDenNgay;
    private boolean tuNgayActive = false;
    private boolean denNgayActive = false;
    private RoundedButton btnTimKiem;
    private RoundedButton btnXoaLoc;

    // ── Results area ───────────────────────────────────────────
    private JLabel lblSoLuong;
    private StyledTable tblHoaDon;
    private JPanel pairButton;

    // ── Suggestion popup ───────────────────────────────────────
    private SearchSuggestionPopup<KhachHang> khSuggest;
    private SearchSuggestionPopup<NhanVien> nvSuggest;

    private static final String[] COLUMN_NAMES = {
        "Mã hóa đơn", "Khách hàng", "Nhân viên tạo",
        "Thời gian", "Tổng tiền", "Thao tác"
    };

    private static final DateTimeFormatter DATE_FMT
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final NumberFormat MONEY_FMT
            = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    // ═══════════════════════════════════════════════════════════
    public TraCuuHoaDon_GUI() {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Colors.BACKGROUND);

        add(buildTopSection(), BorderLayout.NORTH);
        add(buildResultsPanel(), BorderLayout.CENTER);

        loadData();
    }

    // ─────────────────────────────────────────────────────────
    // TOP SECTION  (header + filter card stacked vertically)
    // ─────────────────────────────────────────────────────────
    private JPanel buildTopSection() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Colors.BACKGROUND);

        // ── Page header ──
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Colors.BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Tra cứu hóa đơn");
        lblTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.TEXT_PRIMARY);

        JLabel lblNote = new JLabel("Tìm kiếm và xem chi tiết hóa đơn trong hệ thống");
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
        //====="Tạo card bo góc, chia 3 vùng: NORTH (tiêu đề), CENTER (tìm kiếm), SOUTH (ngày + nút)"=====
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        //====="NORTH – Tiêu đề card bộ lọc"=====
        JPanel pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        pnlNorth.setOpaque(false);
        pnlNorth.add(buildCardTitleRow());
        card.add(pnlNorth, BorderLayout.NORTH);

        //====="CENTER – Hàng tìm kiếm: pairTimKiem bên trái (WEST), pairTuKhoa giãn rộng (CENTER)"=====
        JPanel pnlCenter = new JPanel();
        pnlCenter.setLayout(new BoxLayout(pnlCenter, BoxLayout.X_AXIS));
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(4, 12, 8, 12));

        // Cặp: label "Tìm kiếm theo" + cboTimKiemTheo – căn trái (WEST)
        JPanel pairTimKiem = new JPanel();
        pairTimKiem.setLayout(new BoxLayout(pairTimKiem, BoxLayout.Y_AXIS));
        pairTimKiem.setOpaque(false);
        JLabel lblTimKiemTheo = fieldLabel("Tìm kiếm theo");
        lblTimKiemTheo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTimKiem.add(lblTimKiemTheo);
        pairTimKiem.add(Box.createVerticalStrut(4));
        cboTimKiemTheo = new RoundedComboBox<>(10);
        cboTimKiemTheo.addItem("Mã hóa đơn");
        cboTimKiemTheo.addItem("Khách hàng");
        cboTimKiemTheo.addItem("Nhân viên tạo");
        cboTimKiemTheo.setPreferredSize(new Dimension(200, 50));
        cboTimKiemTheo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTimKiem.add(cboTimKiemTheo);
        pairTimKiem.setMaximumSize(new Dimension(216, Integer.MAX_VALUE));
        pnlCenter.add(pairTimKiem);
        pnlCenter.add(Box.createHorizontalStrut(16));

        // Cặp: label "Từ khóa" + txtKeyword – giãn rộng (CENTER)
        JPanel pairTuKhoa = new JPanel();
        pairTuKhoa.setLayout(new BoxLayout(pairTuKhoa, BoxLayout.Y_AXIS));
        pairTuKhoa.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        pairTuKhoa.setOpaque(false);
        JLabel lblTuKhoa = fieldLabel("Từ khóa");
        lblTuKhoa.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTuKhoa.add(lblTuKhoa);
        pairTuKhoa.add(Box.createVerticalStrut(4));
        txtKeyword = new RoundedTextField(800, 50, 10, "Nhập mã hóa đơn...");
        txtKeyword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtKeyword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        pairTuKhoa.add(txtKeyword);
        pairTuKhoa.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        pnlCenter.add(pairTuKhoa);

        card.add(pnlCenter, BorderLayout.CENTER);

        //====="SOUTH – Hàng ngày lọc + nút Xóa lọc và Tìm kiếm (nhãn nằm trên spinner)"=====
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        pnlSouth.setOpaque(false);
        pnlSouth.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));

        // Cặp: label "Từ ngày" + spnTuNgay
        JPanel pairTuNgay = new JPanel();
        pairTuNgay.setLayout(new BoxLayout(pairTuNgay, BoxLayout.Y_AXIS));
        pairTuNgay.setOpaque(false);
        JLabel lblTuNgay = fieldLabel("Từ ngày");
        lblTuNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTuNgay.add(lblTuNgay);
        pairTuNgay.add(Box.createVerticalStrut(4));
        spnTuNgay = makeDateSpinner();
        spnTuNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTuNgay.add(spnTuNgay);
        pnlSouth.add(pairTuNgay);
        pnlSouth.add(Box.createHorizontalStrut(10));
        // Cặp: label "Đến ngày" + spnDenNgay
        JPanel pairDenNgay = new JPanel();
        pairDenNgay.setLayout(new BoxLayout(pairDenNgay, BoxLayout.Y_AXIS));
        pairDenNgay.setOpaque(false);
        JLabel lblDenNgay = fieldLabel("Đến ngày");
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

        btnXoaLoc = new RoundedButton(130, 50, 10, "\u2715  Xóa lọc", Colors.SECONDARY);
        btnXoaLoc.setForeground(Colors.TEXT_PRIMARY);
        pairButton.add(btnXoaLoc);

        btnTimKiem = new RoundedButton(150, 50, 10, "\uD83D\uDD0D  Tìm kiếm", Colors.PRIMARY);
        pairButton.add(btnTimKiem);
        pnlSouth.add(pairButton);
        card.add(pnlSouth, BorderLayout.SOUTH);

        //====="Gắn sự kiện cho các thành phần điều khiển bộ lọc"=====
        cboTimKiemTheo.addActionListener(e -> {
            updatePlaceholder();
            updateSuggestionMode();
        });
        txtKeyword.addActionListener(e -> search());
        btnTimKiem.addActionListener(e -> search());
        btnXoaLoc.addActionListener(e -> resetFilter());
        spnTuNgay.addChangeListener(e -> tuNgayActive = true);
        spnDenNgay.addChangeListener(e -> denNgayActive = true);

        // Khởi tạo popup gợi ý
        initSuggestionPopups();

        return card;
    }

    //====="Khởi tạo 2 popup gợi ý: 1 cho Khách hàng, 1 cho Nhân viên"=====
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
                // Hiển thị dòng chính = SDT, dòng phụ = tên (chỉ tham khảo)
                kh -> kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "",
                kh -> kh.getTenKhachHang() != null ? kh.getTenKhachHang() : "",
                // Chỉ lọc theo số điện thoại
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
                // Chỉ lọc theo tên nhân viên
                (nv, kw) -> nv.getTenNhanVien() != null && nv.getTenNhanVien().toLowerCase().contains(kw)
        );
        nvSuggest.setOnSelect(nv -> {
            nvSuggest.setTextSilently(nv.getTenNhanVien());
            search();
        });
        nvSuggest.setEnabled(false);
    }

    //====="Bật popup tương ứng theo loại tìm kiếm; tắt khi chọn Mã hóa đơn"=====
    private void updateSuggestionMode() {
        if (khSuggest == null || nvSuggest == null) {
            return;
        }
        String s = (String) cboTimKiemTheo.getSelectedItem();
        boolean kh = "Khách hàng".equals(s);
        boolean nv = "Nhân viên tạo".equals(s);
        khSuggest.setEnabled(kh);
        nvSuggest.setEnabled(nv);
    }

//====="Tạo dòng tiêu đề với icon và nhãn cho card bộ lọc"=====
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

    // ─────────────────────────────────────────────────────────
    // RESULTS PANEL
    // ─────────────────────────────────────────────────────────
    private JPanel buildResultsPanel() {
        //====="Tạo card kết quả tìm kiếm"=====
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 0));

        //====="Thanh tiêu đề: tên section + số lượng kết quả"=====
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

        lblSoLuong = new JLabel("Tìm thấy 0 hóa đơn");
        lblSoLuong.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSoLuong.setForeground(Colors.PRIMARY);

        bar.add(left, BorderLayout.WEST);
        bar.add(lblSoLuong, BorderLayout.EAST);
        card.add(bar, BorderLayout.NORTH);

        //====="Khởi tạo StyledTable với 6 cột dữ liệu hóa đơn"=====
        tblHoaDon = new StyledTable(COLUMN_NAMES, filteredList);

        //====="Cột 0 – Mã hóa đơn: hiển thị màu PRIMARY dạng link"=====
        tblHoaDon.setColumnRenderer(0, (tbl, val, sel, foc, row, col) -> {
            JLabel lbl = new JLabel();
            lbl.setOpaque(true);
            lbl.setText(val instanceof HoaDon ? ((HoaDon) val).getMaHoaDon() : "");
            lbl.setForeground(Colors.PRIMARY);
            lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
            lbl.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            return lbl;
        });
        tblHoaDon.setColumnWidth(0, 130);

        //====="Cột 1 – Khách hàng: dòng 1 = tên KH, dòng 2 = số điện thoại"=====
        tblHoaDon.setTwoLineColumn(1, 210,
                v -> safeKHName((HoaDon) v),
                v -> safeKHPhone((HoaDon) v));

        //====="Cột 2 – Nhân viên tạo: dòng 1 = tên NV, dòng 2 = mã NV"=====
        tblHoaDon.setTwoLineColumn(2, 190,
                v -> safeNVName((HoaDon) v),
                v -> safeNVMa((HoaDon) v));

        //====="Cột 3 – Thời gian: định dạng dd/MM/yyyy từ ngayLap"=====
        tblHoaDon.setSingleTextColumn(3, 150,
                v -> {
                    LocalDateTime d = ((HoaDon) v).getNgayLap();
                    return d != null ? d.format(DATE_FMT) : "";
                });

        //====="Cột 4 – Tổng tiền: định dạng tiền VN, in đậm"=====
        tblHoaDon.setColumnRenderer(4, (tbl, val, sel, foc, row, col) -> {
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
        tblHoaDon.setColumnWidth(4, 150);

        //====="Cột 5 – Thao tác: nút Chi tiết mở dialog xem hóa đơn"=====
        tblHoaDon.setActionColumn(5, 100);
        tblHoaDon.setActionColumnListener((row, obj) -> moChiTietHoaDon((HoaDon) obj));

        card.add(tblHoaDon, BorderLayout.CENTER);
        return card;
    }

    // ─────────────────────────────────────────────────────────
    // DATA & LOGIC
    // ─────────────────────────────────────────────────────────
    //===="Reload toàn bộ dữ liệu từ DB (gọi từ Main_GUI khi mở lại tab)"=====
    public void refresh() {
        loadData();
    }

    //====="Tải toàn bộ hóa đơn từ DB vào fullList và hiển thị lên bảng"=====
    private void loadData() {
        try {
            ArrayList<HoaDon> ds = hoaDonSV.getDSHoaDon();
            fullList.clear();
            if (ds != null) {
                fullList.addAll(ds);
            }
            filteredList.clear();
            filteredList.addAll(fullList);
            if (tblHoaDon != null) {
                tblHoaDon.refresh();
            }
            updateCountLabel();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //====="Lọc danh sách hóa đơn theo từ khóa và khoảng ngày rồi refresh bảng"=====
    private void search() {
        String loai = (String) cboTimKiemTheo.getSelectedItem();
        String kw = txtKeyword.getText().trim().toLowerCase();
        LocalDateTime tuNgay = tuNgayActive ? spinnerToDate(spnTuNgay) : null;
        LocalDateTime denNgay = denNgayActive ? spinnerToDate(spnDenNgay) : null;
        if (denNgay != null) {
            denNgay = denNgay.withHour(23).withMinute(59).withSecond(59);
        }

        filteredList.clear();
        for (HoaDon hd : fullList) {

            //====="Kiểm tra từ khóa theo loại tìm kiếm đã chọn"=====
            if (!kw.isEmpty() && !matchKeyword(hd, loai, kw)) {
                continue;
            }

            //====="Kiểm tra hóa đơn nằm trong khoảng ngày lọc"=====
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
        tblHoaDon.refresh();
        updateCountLabel();
    }

    //====="So khớp từ khóa với trường tương ứng theo loại tìm kiếm"=====
    private boolean matchKeyword(HoaDon hd, String loai, String kw) {
        if (loai == null) {
            return true;
        }
        switch (loai) {
            case "Mã hóa đơn":
                return hd.getMaHoaDon() != null && hd.getMaHoaDon().toLowerCase().contains(kw);
            case "Khách hàng":
                // Chỉ tìm theo số điện thoại
                return safeKHPhone(hd).toLowerCase().contains(kw);
            case "Nhân viên tạo":
                // Chỉ tìm theo tên nhân viên
                return safeNVName(hd).toLowerCase().contains(kw);
            default:
                return true;
        }
    }

    //====="Xóa toàn bộ điều kiện lọc và hiển thị lại toàn bộ danh sách"=====
    private void resetFilter() {
        txtKeyword.setText("");
        cboTimKiemTheo.setSelectedIndex(0);
        spnTuNgay.setValue(new java.util.Date());
        spnDenNgay.setValue(new java.util.Date());
        tuNgayActive = false;
        denNgayActive = false;
        filteredList.clear();
        filteredList.addAll(fullList);
        tblHoaDon.refresh();
        updateCountLabel();
    }

    //====="Mở dialog hiển thị chi tiết một hóa đơn được chọn"=====
    private void moChiTietHoaDon(HoaDon hd) {
        // Lấy thông tin đầy đủ từ DB
        KhachHang kh = hd.getKhachHang() != null
                ? khachHangSV.layKHTheoMa(hd.getKhachHang().getMaKhachHang()) : null;
        NhanVien nv = hd.getNhanVien() != null
                ? nhanVienSV.layNVTheoMa(hd.getNhanVien().getMaNhanVien()) : null;
        PhuongThucThanhToan pttt = hd.getMaPTTT() != null
                ? hoaDonSV.layPTTTTheoMa(hd.getMaPTTT()) : null;
        List<ChiTietHoaDon> chiTiets = chiTietHoaDonSV.getChiTietTheoHoaDon(hd.getMaHoaDon());

        // Card trái – Thông tin khách hàng
        LinkedHashMap<String, String> leftInfo = new LinkedHashMap<>();
        leftInfo.put("Họ tên", kh != null ? kh.getTenKhachHang() : "Khách lẻ");
        leftInfo.put("SĐT", kh != null && kh.getSoDienThoai() != null ? kh.getSoDienThoai() : "---");
        leftInfo.put("Email", kh != null && kh.getEmail() != null ? kh.getEmail() : "---");
        leftInfo.put("Điểm tích lũy", kh != null ? String.valueOf(kh.getDiemTichLuy()) : "0");

        // Card phải – Thông tin hóa đơn
        DateTimeFormatter dtFmt = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        LinkedHashMap<String, String> rightInfo = new LinkedHashMap<>();
        rightInfo.put("Mã hóa đơn", "<font color='" + toHex(Colors.PRIMARY) + "'>" + hd.getMaHoaDon() + "</font>");
        rightInfo.put("Nhân viên", nv != null
                ? nv.getTenNhanVien() + " (" + nv.getMaNhanVien() + ")" : "---");
        rightInfo.put("Thời gian", hd.getNgayLap() != null ? hd.getNgayLap().format(dtFmt) : "---");
        rightInfo.put("Thanh toán", pttt != null ? pttt.getTenPTTT()
                : (hd.getMaPTTT() != null ? hd.getMaPTTT() : "---"));

        // Bảng sản phẩm
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

        // Tổng kết
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
                "\uD83D\uDCC4",
                "Chi tiết hóa đơn: " + hd.getMaHoaDon(),
                "\uD83D\uDC64", "Thông tin khách hàng", leftInfo,
                "\uD83D\uDCC4", "Thông tin hóa đơn", rightInfo,
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
    //====="Tạo JPanel bo góc 16px, nền trắng, viền BORDER_LIGHT"=====
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

    //====="Tạo JLabel nhãn form in đậm màu TEXT_PRIMARY"=====
    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        l.setForeground(Colors.TEXT_PRIMARY);
        return l;
    }

    //====="Tạo JSpinner kiểu ngày định dạng dd/MM/yyyy"=====
    private JSpinner makeDateSpinner() {
        JSpinner sp = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor ed = new JSpinner.DateEditor(sp, "dd/MM/yyyy");
        sp.setEditor(ed);
        sp.setPreferredSize(new Dimension(160, 42));
        sp.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        return sp;
    }

    //====="Cập nhật placeholder của ô tìm kiếm theo loại được chọn"=====
    private void updatePlaceholder() {
        String s = (String) cboTimKiemTheo.getSelectedItem();
        if (s == null) {
            return;
        }
        switch (s) {
            case "Mã hóa đơn":
                txtKeyword.setPlaceholder("Nhập mã hóa đơn...");
                break;
            case "Khách hàng":
                txtKeyword.setPlaceholder("Nhập tên hoặc SĐT khách hàng...");
                break;
            case "Nhân viên tạo":
                txtKeyword.setPlaceholder("Nhập tên hoặc mã nhân viên...");
                break;
        }
    }

    //====="Cập nhật nhãn hiển thị số lượng hóa đơn tìm được"=====
    private void updateCountLabel() {
        lblSoLuong.setText("Tìm thấy " + filteredList.size() + " hóa đơn");
    }

    //====="Chuyển đổi giá trị JSpinner thành LocalDate"=====
    private LocalDateTime spinnerToDate(JSpinner sp) {
        try {
            java.util.Date d = (java.util.Date) sp.getValue();
            return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }

    //====="Lấy tên khách hàng từ hóa đơn, trả về chuỗi rỗng nếu không có"=====
    private String safeKHName(HoaDon hd) {
        return hd.getKhachHang() != null && hd.getKhachHang().getTenKhachHang() != null
                ? hd.getKhachHang().getTenKhachHang() : "";
    }

    //====="Lấy số điện thoại khách hàng từ hóa đơn, trả về chuỗi rỗng nếu không có"=====
    private String safeKHPhone(HoaDon hd) {
        return hd.getKhachHang() != null && hd.getKhachHang().getSoDienThoai() != null
                ? hd.getKhachHang().getSoDienThoai() : "";
    }

    //====="Lấy tên nhân viên tạo hóa đơn, trả về chuỗi rỗng nếu không có"=====
    private String safeNVName(HoaDon hd) {
        return hd.getNhanVien() != null && hd.getNhanVien().getTenNhanVien() != null
                ? hd.getNhanVien().getTenNhanVien() : "";
    }

    //====="Lấy mã nhân viên tạo hóa đơn, trả về chuỗi rỗng nếu không có"=====
    private String safeNVMa(HoaDon hd) {
        return hd.getNhanVien() != null && hd.getNhanVien().getMaNhanVien() != null
                ? hd.getNhanVien().getMaNhanVien() : "";
    }

}
