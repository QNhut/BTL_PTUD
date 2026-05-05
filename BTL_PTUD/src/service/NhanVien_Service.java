package service;

import dao.NhanVien_DAO;
import entity.NhanVien;
import java.util.ArrayList;

public class NhanVien_Service {

    private final NhanVien_DAO nhanVienDao;

    public NhanVien_Service() {
        this.nhanVienDao = new NhanVien_DAO();
        // TODO Auto-generated constructor stub
    }

//Hàm lấy danh sách nhân viên
    public ArrayList<NhanVien> getDSNhanVien() {
        ArrayList<NhanVien> list = nhanVienDao.getDSNhanVien();
        return list;
    }

//Hàm lấy Nhân viên theo mã nhân viên
    public NhanVien layNVTheoMa(String maNhanVien) {
        return nhanVienDao.layNVTheoMa(maNhanVien);
    }

//Hàm thêm nhân viên
    public boolean themNhanVien(NhanVien nv) {
        return nhanVienDao.themNhanVien(nv);
    }

//Hàm update nhân viên
    public boolean updateNhanVien(NhanVien nv) {
        return nhanVienDao.updateNhanVien(nv);
    }

//Hàm xóa nhân viên    
    public boolean xoaNhanVien(String maNhanVien) {
        return nhanVienDao.xoaNhanVien(maNhanVien);
    }

//Hàm lấy danh sách chức vụ
    public ArrayList<entity.ChucVu> getDSChucVu() {
        return nhanVienDao.getDSChucVu();
    }

    //Hàm Lấy tổng số nhân viên
    public int getSoLuongNhanVien() {
        ArrayList<NhanVien> list = nhanVienDao.getDSNhanVien();
        return list.size();
    }

//Hàm lấy tổng nhân viên đang hoạt động
    public int getSoLuongNhanVienOnline() {
        ArrayList<NhanVien> list = nhanVienDao.getDSNhanVien();
        int count = 0;
        for (NhanVien i : list) {
            if (i.isTrangThai() == true) {
                count++;
            }
        }
        return count;
    }

//Hàm lấy tổng nhân viên đang hoạt động
    public int getSoLuongNhanVienOffline() {
        ArrayList<NhanVien> list = nhanVienDao.getDSNhanVien();
        int count = 0;
        for (NhanVien i : list) {
            if (i.isTrangThai() == false) {
                count++;
            }
        }
        return count;
    }

    /**
     * Tìm kiếm nhân viên trên danh sách cục bộ (không gọi lại DB). - Nếu
     * keyword khớp chính xác mã NV → tra ngay qua DAO O(1). - Fallback: linear
     * scan O(n) theo mã, tên, SĐT, email.
     */
    public ArrayList<NhanVien> timKiem(ArrayList<NhanVien> dsnv, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return new ArrayList<>(dsnv);
        }
        String kw = keyword.trim();
        String kwUpper = kw.toUpperCase();
        String kwLower = kw.toLowerCase();
        // Exact code match → single-result shortcut via DAO
        if (kwUpper.startsWith("NV")) {
            NhanVien found = nhanVienDao.layNVTheoMa(kwUpper);
            if (found != null) {
                ArrayList<NhanVien> result = new ArrayList<>();
                result.add(found);
                return result;
            }
        }
        // Linear scan theo mã, tên, SĐT, email — hỗ trợ không dấu
        String kwNorm = normalize(kw);
        ArrayList<NhanVien> result = new ArrayList<>();
        for (NhanVien nv : dsnv) {
            if ((nv.getMaNhanVien() != null && nv.getMaNhanVien().toUpperCase().contains(kwUpper))
                    || (nv.getTenNhanVien() != null && normalize(nv.getTenNhanVien()).contains(kwNorm))
                    || (nv.getSoDienThoai() != null && nv.getSoDienThoai().contains(kw))
                    || (nv.getEmail() != null && normalize(nv.getEmail()).contains(kwNorm))) {
                result.add(nv);
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
