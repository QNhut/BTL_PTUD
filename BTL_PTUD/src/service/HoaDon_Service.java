package service;

import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.PhuongThucThanhToan_DAO;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;
import entity.PhuongThucThanhToan;
import entity.SanPham;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HoaDon_Service {

    // SĐT mặc định cho khách lẻ (không có thông tin liên lạc).
    public static final String SDT_KHACH_LE = "0000000000";
    // Tỉ lệ tích điểm: cứ 10.000đ thanh toán → 1 điểm.
    public static final int VND_PER_POINT_EARN = 10000;
    // Giá trị quy đổi mỗi điểm khi sử dụng: 1 điểm = 1.000đ.
    public static final int VND_PER_POINT_USE = 1000;

    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private final PhuongThucThanhToan_DAO pttDAO = new PhuongThucThanhToan_DAO();
    private final SanPham_Service sanPhamService = new SanPham_Service();
    private final ChiTietHoaDon_Service chiTietHoaDonService = new ChiTietHoaDon_Service();

    // ===== Inner DTO đại diện một dòng trong giỏ hàng =====
    public static class CartItem {

        private final SanPham sanPham;
        private final int soLuong;
        // Đơn giá thực bán = giá sau khuyến mãi.
        private final double donGia;
        // Giá gốc trước khi áp dụng khuyến mãi (nếu không có KM thì = donGia).
        private final double giaGoc;

        public CartItem(SanPham sanPham, int soLuong, double donGia) {
            this(sanPham, soLuong, donGia, sanPham != null ? sanPham.getGiaThanh() : donGia);
        }

        public CartItem(SanPham sanPham, int soLuong, double donGia, double giaGoc) {
            this.sanPham = sanPham;
            this.soLuong = soLuong;
            this.donGia = donGia;
            this.giaGoc = giaGoc;
        }

        public SanPham getSanPham() { return sanPham; }
        public int getSoLuong() { return soLuong; }
        public double getDonGia() { return donGia; }
        public double getGiaGoc() { return giaGoc; }

        // Thành tiền 1 dòng (sau KM, trước thuế).
        public double getThanhTien() { return soLuong * donGia; }

        // Tiền giảm trên 1 dòng do khuyến mãi sản phẩm.
        public double getTienGiamGia() { return Math.max(0, soLuong * (giaGoc - donGia)); }

        // % thuế của dòng (lấy từ sản phẩm). 0 nếu không có.
        public double getPhanTramThue() {
            if (sanPham == null || sanPham.getThue() == null) return 0;
            return sanPham.getThue().getPhanTramThue();
        }

        // Tiền thuế 1 dòng = thanhTien * %thue / 100.
        public double getTienThue() {
            return getThanhTien() * getPhanTramThue() / 100.0;
        }
    }

    // Tổng hợp các con số tiền của 1 hóa đơn.
    public static class HoaDonSummary {
        public final double tienHang;       // sum(qty * donGia) — sau KM, trước thuế
        public final double tienThue;       // sum(line tax)
        public final double tienGiamGia;    // sum(qty * (giaGoc - donGia))
        public final int diemSuDung;        // điểm khách dùng
        public final double tienGiamTuDiem; // diemSuDung * 1000
        public final double thanhTien;      // tienHang + tienThue - tienGiamTuDiem
        public final int diemTichLuyMoi;    // điểm cộng thêm cho KH

        public HoaDonSummary(double tienHang, double tienThue, double tienGiamGia,
                             int diemSuDung, double thanhTien, int diemTichLuyMoi) {
            this.tienHang = tienHang;
            this.tienThue = tienThue;
            this.tienGiamGia = tienGiamGia;
            this.diemSuDung = diemSuDung;
            this.tienGiamTuDiem = diemSuDung * (double) VND_PER_POINT_USE;
            this.thanhTien = thanhTien;
            this.diemTichLuyMoi = diemTichLuyMoi;
        }
    }

    // Tính breakdown cho danh sách CartItem + số điểm muốn dùng.
    public HoaDonSummary tinhTongKet(List<CartItem> items, int diemSuDung) {
        double tienHang = 0, tienThue = 0, tienGiamGia = 0;
        if (items != null) {
            for (CartItem it : items) {
                tienHang += it.getThanhTien();
                tienThue += it.getTienThue();
                tienGiamGia += it.getTienGiamGia();
            }
        }
        if (diemSuDung < 0) diemSuDung = 0;
        double tienGiamTuDiem = diemSuDung * (double) VND_PER_POINT_USE;
        // Không cho phép điểm dùng vượt quá (tienHang + tienThue) để tránh thanhTien âm
        double maxGiamDiem = tienHang + tienThue;
        if (tienGiamTuDiem > maxGiamDiem) {
            tienGiamTuDiem = maxGiamDiem;
            diemSuDung = (int) (tienGiamTuDiem / VND_PER_POINT_USE);
        }
        double thanhTien = Math.max(0, tienHang + tienThue - tienGiamTuDiem);
        // Điểm tích lũy mới chỉ tính trên thành tiền thực trả
        int diemTichLuyMoi = (int) (thanhTien / VND_PER_POINT_EARN);
        return new HoaDonSummary(tienHang, tienThue, tienGiamGia, diemSuDung, thanhTien, diemTichLuyMoi);
    }

    //===="Sinh mã hóa đơn tự động từ DAO"=====
    public String sinhMaHoaDon() {
        return hoaDonDAO.sinhMaTuDong();
    }

    //===="Tạo toàn bộ hóa đơn: xử lý KH, lưu HĐ, chi tiết HĐ và giảm tồn kho"=====
    public HoaDon taoHoaDon(String maHD, String tenKhachHang, String soDienThoai,
            NhanVien nhanVien, List<CartItem> items, String maPTTT) {
        return taoHoaDon(maHD, tenKhachHang, soDienThoai, nhanVien, items, maPTTT, 0);
    }

    // Tạo hóa đơn với điểm tích lũy được sử dụng (1 điểm = 1.000đ).
    // Tự xử lý: lookup/tạo KH, kiểm tra điểm, tính breakdown, lưu, giảm tồn kho, cập nhật điểm.
    public HoaDon taoHoaDon(String maHD, String tenKhachHang, String soDienThoai,
            NhanVien nhanVien, List<CartItem> items, String maPTTT, int diemSuDung) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống, không thể tạo hóa đơn.");
        }
        if (nhanVien == null) {
            throw new IllegalArgumentException("Không xác định được nhân viên lập hóa đơn.");
        }
        if (diemSuDung < 0) diemSuDung = 0;

        // 1. Tìm hoặc tạo khách hàng (member nếu có SĐT, khách lẻ mặc định nếu không)
        KhachHang kh = timHoacTaoKhachHang(tenKhachHang, soDienThoai);

        // 2. Khách lẻ thì không được dùng điểm
        if (laKhachLe(kh)) {
            diemSuDung = 0;
        } else {
            KhachHang khFresh = khachHangDAO.layKHTheoSDT(kh.getSoDienThoai());
            if (khFresh != null && diemSuDung > khFresh.getDiemTichLuy()) {
                throw new IllegalArgumentException("Số điểm sử dụng (" + diemSuDung
                        + ") vượt quá điểm hiện có của khách hàng (" + khFresh.getDiemTichLuy() + ").");
            }
        }

        // 3. PTTT
        String maPTTTFinal = (maPTTT != null && !maPTTT.isBlank()) ? maPTTT : layMaPTTTMacDinh();

        // 4. Tính breakdown
        HoaDonSummary sum = tinhTongKet(items, diemSuDung);

        // 5. Tạo HoaDon entity
        HoaDon hd = new HoaDon(maHD, LocalDateTime.now(), sum.tienHang, sum.diemTichLuyMoi, maPTTTFinal, nhanVien, kh);
        hd.setTienHang(sum.tienHang);
        hd.setTienThue(sum.tienThue);
        hd.setTienGiamGia(sum.tienGiamGia);
        hd.setDiemSuDung(sum.diemSuDung);
        hd.setThanhTien(sum.thanhTien);

        // 6. Lưu HĐ
        if (!hoaDonDAO.taoHoaDon(hd)) {
            throw new RuntimeException("Không thể lưu hóa đơn vào cơ sở dữ liệu.");
        }

        // 7. Lưu chi tiết HĐ + giảm tồn kho
        chiTietHoaDonService.luuChiTietVaGiamTonKho(hd, items);

        // 8. Cập nhật điểm KH: trừ điểm dùng, cộng điểm tích lũy mới (chỉ với khách thành viên)
        if (!laKhachLe(kh)) {
            capNhatDiemKhachHang(kh, sum.diemTichLuyMoi, sum.diemSuDung);
        }

        return hd;
    }

    // Khách lẻ = SĐT mặc định "0000000000".
    public static boolean laKhachLe(KhachHang kh) {
        return kh != null && SDT_KHACH_LE.equals(kh.getSoDienThoai());
    }

    //===="Tìm KH theo SĐT; nếu không có thì tạo mới với thông tin nhập vào"=====
    private KhachHang timHoacTaoKhachHang(String tenKH, String sdt) {
        boolean sdtHopLe = sdt != null && sdt.matches("\\d{10}");

        if (sdtHopLe) {
            KhachHang existing = khachHangDAO.layKHTheoSDT(sdt);
            if (existing != null) {
                return existing;
            }
            // Tạo KH mới với SĐT này
            String maKH = khachHangDAO.sinhMaTuDong();
            String ten = (tenKH != null && !tenKH.isBlank()) ? tenKH.trim() : "Khách lẻ";
            KhachHang kh = new KhachHang(maKH, ten, sdt, null, true, 0, true);
            khachHangDAO.themKhachHang(kh);
            return kh;
        }

        // Không có SĐT hợp lệ → dùng KH khách lẻ mặc định (SĐT 0000000000)
        KhachHang khachLe = khachHangDAO.layKHTheoSDT(SDT_KHACH_LE);
        if (khachLe == null) {
            String maKH = khachHangDAO.sinhMaTuDong();
            khachLe = new KhachHang(maKH, "Khách lẻ", SDT_KHACH_LE, null, true, 0, true);
            khachHangDAO.themKhachHang(khachLe);
        }
        return khachLe;
    }

    //===="Lấy mã phương thức thanh toán đầu tiên (mặc định: tiền mặt)"=====
    private String layMaPTTTMacDinh() {
        List<PhuongThucThanhToan> ds = pttDAO.getDSPhuongThuc();
        if (ds != null && !ds.isEmpty()) {
            return ds.get(0).getMaPTTT();
        }
        return "PTTT01";
    }

    //===="Tính điểm tích lũy: cứ 10.000đ được 1 điểm"=====
    private int tinhDiemTichLuy(double tongTien) {
        return (int) (tongTien / VND_PER_POINT_EARN);
    }

    // Cập nhật điểm khách hàng: trừ điểm đã sử dụng, sau đó cộng điểm tích lũy mới.
    private void capNhatDiemKhachHang(KhachHang kh, int diemThem, int diemDung) {
        if (kh == null) return;
        if (diemThem <= 0 && diemDung <= 0) return;
        KhachHang khMoi = khachHangDAO.layKHTheoSDT(kh.getSoDienThoai());
        if (khMoi == null) return;
        int diemMoi = khMoi.getDiemTichLuy() - diemDung + diemThem;
        if (diemMoi < 0) diemMoi = 0;
        khMoi.setDiemTichLuy(diemMoi);
        khachHangDAO.updateKhachHang(khMoi);
    }

    //===="Cộng điểm tích lũy vào tài khoản khách hàng và lưu DB (legacy)"=====
    private void capNhatDiemKhachHang(KhachHang kh, int diemThem) {
        capNhatDiemKhachHang(kh, diemThem, 0);
    }

    //===="Lấy danh sách hóa đơn đã lưu"=====
    public ArrayList<HoaDon> getDSHoaDon() {
        return hoaDonDAO.getDSHoaDon();
    }
    // ==================== THỐNG KÊ DOANH THU ====================

    // DTO tổng hợp các chỉ số thống kê cho summary cards
    public static class ThongKeTongHop {
        public final double doanhThuKy;
        public final double tongDoanhThu;
        public final int soGiaoDich;
        public final double doanhThuTrungBinh;

        public ThongKeTongHop(double doanhThuKy, double tongDoanhThu,
                              int soGiaoDich, double doanhThuTrungBinh) {
            this.doanhThuKy = doanhThuKy;
            this.tongDoanhThu = tongDoanhThu;
            this.soGiaoDich = soGiaoDich;
            this.doanhThuTrungBinh = doanhThuTrungBinh;
        }
    }

    // Lấy tổng hợp thống kê cho summary cards.
    // Tính toán doanh thu kỳ, tổng DT, số giao dịch, DT trung bình.
    public ThongKeTongHop layThongKeTongHop(Integer nam, Integer thang, Integer ngay,
                                             LocalDate tuNgay, LocalDate denNgay) {
        double dtKy = hoaDonDAO.tinhDoanhThuKy(nam, thang, ngay, tuNgay, denNgay);
        double tongDT = hoaDonDAO.tinhTongDoanhThu();
        int soGD = hoaDonDAO.demSoGiaoDich(nam, thang, ngay, tuNgay, denNgay);
        double dtTB = soGD > 0 ? dtKy / soGD : 0;
        return new ThongKeTongHop(dtKy, tongDT, soGD, dtTB);
    }

    // Lấy dữ liệu biểu đồ cột theo 3 mức:
    // - Chỉ có năm → 12 tháng
    // - Có năm + tháng → theo ngày trong tháng
    // - Có năm + tháng + ngày → 24 giờ
    public LinkedHashMap<String, Double> layDuLieuBieuDoCot(Integer nam, Integer thang, Integer ngay) {
        if (ngay != null && thang != null && nam != null) {
            return hoaDonDAO.thongKeTheoGio(nam, thang, ngay);
        }
        if (thang != null && nam != null) {
            return hoaDonDAO.thongKeTheoNgay(nam, thang);
        }
        int barNam = nam != null ? nam : LocalDate.now().getYear();
        return hoaDonDAO.thongKeTheoThang(barNam);
    }

    // Lấy dữ liệu xu hướng (line chart) theo 3 mức tương tự.
    public LinkedHashMap<String, Double> layDuLieuXuHuong(Integer nam, Integer thang, Integer ngay,
                                                          LocalDate tuNgay, LocalDate denNgay) {
        // Nếu có ngày cụ thể → dùng biểu đồ theo giờ
        if (ngay != null && thang != null && nam != null) {
            return hoaDonDAO.thongKeTheoGio(nam, thang, ngay);
        }

        LocalDate lineFrom = tuNgay;
        LocalDate lineTo = denNgay;
        if (lineFrom == null || lineTo == null) {
            int y = nam != null ? nam : LocalDate.now().getYear();
            if (thang != null) {
                lineFrom = LocalDate.of(y, thang, 1);
                lineTo = lineFrom.withDayOfMonth(lineFrom.lengthOfMonth());
            } else {
                lineFrom = LocalDate.of(y, 1, 1);
                lineTo = LocalDate.of(y, 12, 31);
            }
        }
        LinkedHashMap<String, Double> result = hoaDonDAO.xuHuongTheoNgay(lineFrom, lineTo);
        if (result.isEmpty()) {
            int y = nam != null ? nam : LocalDate.now().getYear();
            result = hoaDonDAO.thongKeTheoThang(y);
        }
        return result;
    }

    // Lấy danh sách hóa đơn theo kỳ (đã JOIN tên PTTT và DonGiaTB)
    public ArrayList<Object[]> layDanhSachTheoKy(Integer nam, Integer thang, Integer ngay,
            LocalDate tuNgay, LocalDate denNgay) {
        return hoaDonDAO.layDanhSachTheoKy(nam, thang, ngay, tuNgay, denNgay);
    }
    //===""=""Lấy danh sách phương thức thanh toán đang hoạt động"====="
    public List<PhuongThucThanhToan> getDSPhuongThucThanhToan() {
        List<PhuongThucThanhToan> ds = pttDAO.getDSPhuongThuc();
        List<PhuongThucThanhToan> active = new ArrayList<>();
        for (PhuongThucThanhToan pt : ds) {
            if (pt.isTrangThai()) {
                active.add(pt);
            }
        }
        return active;
    }

    //===="Kiểm tra tồn kho đủ cho từng sản phẩm trong giỏ hàng (dùng quy đổi đơn vị)"=====
    public String kiemTraTonKho(List<CartItem> items) {
        List<SanPham> dsSP = new ArrayList<>();
        for (CartItem item : items) {
            dsSP.add(item.getSanPham());
        }
        Map<String, SanPham_Service.TonKhoInfo> tonKhoMap = sanPhamService.tinhTonKhoTatCa(dsSP);
        for (CartItem item : items) {
            SanPham_Service.TonKhoInfo info = tonKhoMap.get(item.getSanPham().getMaSanPham());
            int ton = (info != null) ? info.tonKho : 0;
            if (ton < item.getSoLuong()) {
                return "Sản phẩm \"" + item.getSanPham().getTenSP()
                        + "\" không đủ tồn kho (còn " + ton + ", cần " + item.getSoLuong() + ").";
            }
        }
        return null; // null = tất cả đều đủ
    }
}
