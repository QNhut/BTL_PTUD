package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import dao.Thue_DAO;
import entity.Thue;

public class Thue_Service {

    private final Thue_DAO thueDao = new Thue_DAO();

    public ArrayList<Thue> getDSThue() {
        return thueDao.getDSThue();
    }

    public Thue layTheoMa(String maThue) {
        return thueDao.layTheoMa(maThue);
    }

    public boolean themThue(Thue t) {
        return thueDao.them(t);
    }

    public boolean capNhatThue(Thue t) {
        return thueDao.capNhat(t);
    }

    public boolean xoaThue(String maThue) {
        return thueDao.xoa(maThue);
    }

    public String sinhMaThue() {
        return thueDao.sinhMaTuDong();
    }

    // Áp dụng thuế cho danh sách mã loại SP (truyền {@code null} để gỡ thuế).
    public int apDungChoLoaiSanPham(String maThue, List<String> dsMaLoai) {
        return thueDao.apDungChoLoaiSanPham(maThue, dsMaLoai);
    }

    // Map maThue → số SP đang áp dụng
    public Map<String, Integer> getDemSanPhamTheoThue() {
        return thueDao.getDemSanPhamTheoThue();
    }

    // Danh sách mã loại đang gắn 1 mức thuế
    public List<String> layMaLoaiDangApDung(String maThue) {
        return thueDao.layMaLoaiDangApDung(maThue);
    }

    public int getSoLuongThue() {
        return getDSThue().size();
    }

    // Tìm theo mã hoặc tên (không dấu, không phân biệt hoa thường).
    public ArrayList<Thue> timKiem(ArrayList<Thue> ds, String keyword) {
        if (keyword == null || keyword.isBlank()) return new ArrayList<>(ds);
        String kw = keyword.trim();
        String kwUpper = kw.toUpperCase();
        String kwNorm = normalize(kw);
        ArrayList<Thue> result = new ArrayList<>();
        for (Thue t : ds) {
            if ((t.getMaThue() != null && t.getMaThue().toUpperCase().contains(kwUpper))
                    || (t.getTenThue() != null && normalize(t.getTenThue()).contains(kwNorm))) {
                result.add(t);
            }
        }
        return result;
    }

    public static String normalize(String s) {
        if (s == null) return "";
        return java.text.Normalizer.normalize(s, java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase();
    }
}
