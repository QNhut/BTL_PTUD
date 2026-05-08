package service;

import dao.KhuyenMai_DAO;
import entity.KhuyenMai;
import java.time.LocalDate;
import java.util.ArrayList;

public class KhuyenMai_Service {

    private final KhuyenMai_DAO khuyenMaiDao;

    public KhuyenMai_Service() {
        this.khuyenMaiDao = new KhuyenMai_DAO();
    }

    public ArrayList<KhuyenMai> getDSKhuyenMai() {
        return khuyenMaiDao.getDSKhuyenMai();
    }

    public KhuyenMai layTheoMa(String maKM) {
        return khuyenMaiDao.layTheoMa(maKM);
    }

    public boolean themKhuyenMai(KhuyenMai km) {
        return khuyenMaiDao.them(km);
    }

    public boolean capNhatKhuyenMai(KhuyenMai km) {
        return khuyenMaiDao.capNhat(km);
    }

    public boolean xoaKhuyenMai(String maKM) {
        return khuyenMaiDao.xoa(maKM);
    }

    public int getSoLuongKhuyenMai() {
        return getDSKhuyenMai().size();
    }

    /**
     * Khuyến mãi đang diễn ra: trangThai=true VÀ hôm nay nằm trong [ngayBatDau,
     * ngayKetThuc]
     */
    public int getSoLuongDangHoatDong() {
        LocalDate today = LocalDate.now();
        int count = 0;
        for (KhuyenMai km : getDSKhuyenMai()) {
            if (km.isTrangThai()
                    && !today.isBefore(km.getNgayBatDau())
                    && !today.isAfter(km.getNgayKetThuc())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Sắp diễn ra: trangThai=true VÀ hôm nay < ngayBatDau
     */
    public int getSoLuongSapDienRa() {
        LocalDate today = LocalDate.now();
        int count = 0;
        for (KhuyenMai km : getDSKhuyenMai()) {
            if (km.isTrangThai() && today.isBefore(km.getNgayBatDau())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Đã kết thúc: trangThai=false HOẶC hôm nay > ngayKetThuc
     */
    public int getSoLuongDaKetThuc() {
        LocalDate today = LocalDate.now();
        int count = 0;
        for (KhuyenMai km : getDSKhuyenMai()) {
            if (!km.isTrangThai() || today.isAfter(km.getNgayKetThuc())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Trả về chuỗi trạng thái hiển thị dựa trên ngày: "Đang diễn ra", "Sắp diễn
     * ra", "Đã kết thúc"
     */
    public String getTrangThaiHienThi(KhuyenMai km) {
        LocalDate today = LocalDate.now();
        if (!km.isTrangThai() || today.isAfter(km.getNgayKetThuc())) {
            return "Đã kết thúc";
        }
        if (today.isBefore(km.getNgayBatDau())) {
            return "Sắp diễn ra";
        }
        return "Đang diễn ra";
    }

    /**
     * Kiểm tra khuyến mãi đang hoạt động thực tế (dùng cho badge table)
     */
    public boolean isDangHoatDong(KhuyenMai km) {
        LocalDate today = LocalDate.now();
        return km.isTrangThai()
                && !today.isBefore(km.getNgayBatDau())
                && !today.isAfter(km.getNgayKetThuc());
    }

    /**
     * Tìm kiếm trên danh sách cục bộ — không gọi lại DB. - Nếu keyword khớp
     * chính xác mã KM → tra ngay qua DAO O(1). - Fallback: linear scan theo mã,
     * tên — hỗ trợ không dấu.
     */
    public ArrayList<KhuyenMai> timKiem(ArrayList<KhuyenMai> ds, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>(ds);
        }
        String kw = keyword.trim();
        String kwUpper = kw.toUpperCase();

        if (kwUpper.startsWith("KM")) {
            KhuyenMai found = khuyenMaiDao.layTheoMa(kwUpper);
            if (found != null) {
                ArrayList<KhuyenMai> result = new ArrayList<>();
                result.add(found);
                return result;
            }
        }

        String kwNorm = normalize(kw);
        ArrayList<KhuyenMai> result = new ArrayList<>();
        for (KhuyenMai km : ds) {
            if ((km.getMaKhuyenMai() != null && km.getMaKhuyenMai().toUpperCase().contains(kwUpper))
                    || (km.getTenKhuyenMai() != null && normalize(km.getTenKhuyenMai()).contains(kwNorm))) {
                result.add(km);
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
