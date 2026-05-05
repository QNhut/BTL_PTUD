package service;

import dao.KhachHang_DAO;
import entity.KhachHang;
import java.util.ArrayList;

public class KhachHang_Service {

    private final KhachHang_DAO khachHangDao;

    public KhachHang_Service() {
        this.khachHangDao = new KhachHang_DAO();
    }

    public ArrayList<KhachHang> getDSKhachHang() {
        return khachHangDao.getDSKhachHang();
    }

    public KhachHang layKHTheoMa(String maKhachHang) {
        return khachHangDao.layKHTheoMa(maKhachHang);
    }

    public KhachHang layKHTheoSDT(String sdt) {
        return khachHangDao.layKHTheoSDT(sdt);
    }

    public boolean themKhachHang(KhachHang kh) {
        if (kh.getTenKhachHang() == null || kh.getTenKhachHang().isBlank()) {
            throw new IllegalArgumentException("Tên khách hàng không được để trống.");
        }
        if (kh.getSoDienThoai() == null || !kh.getSoDienThoai().matches("\\d{10}")) {
            throw new IllegalArgumentException("Số điện thoại phải có đúng 10 chữ số.");
        }
        return khachHangDao.themKhachHang(kh);
    }

    public boolean updateKhachHang(KhachHang kh) {
        return khachHangDao.updateKhachHang(kh);
    }

    public boolean xoaKhachHang(String maKhachHang) {
        return khachHangDao.xoaKhachHang(maKhachHang);
    }

    public int getSoLuongKhachHang() {
        return khachHangDao.getDSKhachHang().size();
    }

    public int getSoLuongKhachHangHoatDong() {
        int count = 0;
        for (KhachHang kh : khachHangDao.getDSKhachHang()) {
            if (kh.isTrangThai()) {
                count++;
            }
        }
        return count;
    }

    public int getSoLuongKhachHangNgungHoatDong() {
        int count = 0;
        for (KhachHang kh : khachHangDao.getDSKhachHang()) {
            if (!kh.isTrangThai()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Tìm kiếm khách hàng trên danh sách cục bộ (không gọi lại DB). - Nếu
     * keyword khớp chính xác mã KH → tra qua DAO O(1). - Nếu keyword là 10 chữ
     * số → tra theo SĐT O(1). - Fallback: linear scan O(n) theo mã, tên, SĐT.
     */
    public ArrayList<KhachHang> timKiem(ArrayList<KhachHang> dskh, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>(dskh);
        }
        String kw = keyword.trim();
        String kwUpper = kw.toUpperCase();
        String kwLower = kw.toLowerCase();
        // Exact code match → single-result shortcut
        if (kwUpper.startsWith("KH")) {
            KhachHang found = khachHangDao.layKHTheoMa(kwUpper);
            if (found != null) {
                ArrayList<KhachHang> result = new ArrayList<>();
                result.add(found);
                return result;
            }
        }
        // Exact SĐT match (10 chữ số)
        if (kw.matches("\\d{10}")) {
            KhachHang found = khachHangDao.layKHTheoSDT(kw);
            if (found != null) {
                ArrayList<KhachHang> result = new ArrayList<>();
                result.add(found);
                return result;
            }
        }
        // Linear scan theo mã, tên, SĐT — hỗ trợ không dấu
        String kwNorm = normalize(kw);
        ArrayList<KhachHang> result = new ArrayList<>();
        for (KhachHang kh : dskh) {
            if ((kh.getMaKhachHang() != null && kh.getMaKhachHang().toUpperCase().contains(kwUpper))
                    || (kh.getTenKhachHang() != null && normalize(kh.getTenKhachHang()).contains(kwNorm))
                    || (kh.getSoDienThoai() != null && kh.getSoDienThoai().contains(kw))) {
                result.add(kh);
            }
        }
        return result;
    }

    /**
     * Chuẩn hóa chuỗi: bỏ dấu, viết thường để so sánh không phân biệt dấu.
     */
    public static String normalize(String s) {
        if (s == null) {
            return "";
        }
        return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }
}
