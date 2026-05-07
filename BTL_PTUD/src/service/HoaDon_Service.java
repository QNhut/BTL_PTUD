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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class HoaDon_Service {

    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private final PhuongThucThanhToan_DAO pttDAO = new PhuongThucThanhToan_DAO();
    private final SanPham_Service sanPhamService = new SanPham_Service();
    private final ChiTietHoaDon_Service chiTietHoaDonService = new ChiTietHoaDon_Service();

    // ===== Inner DTO đại diện một dòng trong giỏ hàng =====
    public static class CartItem {

        private final SanPham sanPham;
        private final int soLuong;
        private final double donGia;

        public CartItem(SanPham sanPham, int soLuong, double donGia) {
            this.sanPham = sanPham;
            this.soLuong = soLuong;
            this.donGia = donGia;
        }

        public SanPham getSanPham() {
            return sanPham;
        }

        public int getSoLuong() {
            return soLuong;
        }

        public double getDonGia() {
            return donGia;
        }

        //===="Tính thành tiền của 1 dòng giỏ hàng"=====
        public double getThanhTien() {
            return soLuong * donGia;
        }
    }

    //===="Sinh mã hóa đơn tự động từ DAO"=====
    public String sinhMaHoaDon() {
        return hoaDonDAO.sinhMaTuDong();
    }

    //===="Tạo toàn bộ hóa đơn: xử lý KH, lưu HĐ, chi tiết HĐ và giảm tồn kho"=====
    public HoaDon taoHoaDon(String maHD, String tenKhachHang, String soDienThoai,
            NhanVien nhanVien, List<CartItem> items, String maPTTT) {
        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Giỏ hàng trống, không thể tạo hóa đơn.");
        }
        if (nhanVien == null) {
            throw new IllegalArgumentException("Không xác định được nhân viên lập hóa đơn.");
        }

        // 1. Tìm hoặc tạo khách hàng
        KhachHang kh = timHoacTaoKhachHang(tenKhachHang, soDienThoai);

        // 2. Xác định phương thức thanh toán
        String maPTTTFinal = (maPTTT != null && !maPTTT.isBlank()) ? maPTTT : layMaPTTTMacDinh();

        // 3. Tính tổng tiền
        double tongTien = 0;
        for (CartItem item : items) {
            tongTien += item.getThanhTien();
        }

        // 4. Tính điểm tích lũy cho KH
        int diemThem = tinhDiemTichLuy(tongTien);

        // 5. Tạo đối tượng HoaDon
        HoaDon hd = new HoaDon(maHD, LocalDate.now(), tongTien, diemThem, maPTTTFinal, nhanVien, kh);

        // 6. Lưu hóa đơn vào DB
        if (!hoaDonDAO.taoHoaDon(hd)) {
            throw new RuntimeException("Không thể lưu hóa đơn vào cơ sở dữ liệu.");
        }

        // 7. Lưu chi tiết hóa đơn và giảm tồn kho (delegate sang ChiTietHoaDon_Service)
        chiTietHoaDonService.luuChiTietVaGiamTonKho(hd, items);

        // 8. Cộng điểm tích lũy cho khách hàng
        capNhatDiemKhachHang(kh, diemThem);

        return hd;
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
        final String SDT_KHACH_LE = "0000000000";
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
        return (int) (tongTien / 10000);
    }

    //===="Cộng điểm tích lũy vào tài khoản khách hàng và lưu DB"=====
    private void capNhatDiemKhachHang(KhachHang kh, int diemThem) {
        if (kh == null || diemThem <= 0) {
            return;
        }
        // Lấy lại KH từ DB để đảm bảo điểm hiện tại chính xác
        KhachHang khMoi = khachHangDAO.layKHTheoSDT(kh.getSoDienThoai());
        if (khMoi == null) {
            return;
        }
        khMoi.setDiemTichLuy(khMoi.getDiemTichLuy() + diemThem);
        khachHangDAO.updateKhachHang(khMoi);
    }

    //===="Lấy danh sách hóa đơn đã lưu"=====
    public ArrayList<HoaDon> getDSHoaDon() {
        return hoaDonDAO.getDSHoaDon();
    }
    // ==================== THỐNG KÊ (delegate to DAO) ====================

    public double tinhDoanhThuKy(Integer nam, Integer thang, Integer ngay,
            LocalDate tuNgay, LocalDate denNgay) {
        return hoaDonDAO.tinhDoanhThuKy(nam, thang, ngay, tuNgay, denNgay);
    }

    public double tinhTongDoanhThu() {
        return hoaDonDAO.tinhTongDoanhThu();
    }

    public int demSoGiaoDich(Integer nam, Integer thang, Integer ngay,
            LocalDate tuNgay, LocalDate denNgay) {
        return hoaDonDAO.demSoGiaoDich(nam, thang, ngay, tuNgay, denNgay);
    }

    public LinkedHashMap<String, Double> thongKeTheoThang(int nam) {
        return hoaDonDAO.thongKeTheoThang(nam);
    }

    public LinkedHashMap<String, Double> thongKeTheoNgay(int nam, int thang) {
        return hoaDonDAO.thongKeTheoNgay(nam, thang);
    }

    public LinkedHashMap<String, Double> xuHuongTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        return hoaDonDAO.xuHuongTheoNgay(tuNgay, denNgay);
    }

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
