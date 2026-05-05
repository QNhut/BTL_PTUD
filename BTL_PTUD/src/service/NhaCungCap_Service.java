package service;

import dao.NhaCungCap_DAO;
import entity.NhaCungCap;
import java.text.Normalizer;
import java.util.ArrayList;

public class NhaCungCap_Service {

    private final NhaCungCap_DAO nhaCungCapDao;

    public NhaCungCap_Service() {
        this.nhaCungCapDao = new NhaCungCap_DAO();
    }

    public ArrayList<NhaCungCap> getDSNhaCungCap() {
        return nhaCungCapDao.getDSNhaCungCap();
    }

    public NhaCungCap layNCCTheoMa(String maNCC) {
        return nhaCungCapDao.layNCCTheoMaNCC(maNCC);
    }

    public boolean themNCC(NhaCungCap ncc) {
        return nhaCungCapDao.themNCC(ncc);
    }

    public boolean capNhatNCC(NhaCungCap ncc) {
        return nhaCungCapDao.updateNCC(ncc);
    }

    public boolean xoaNCC(String maNCC) {
        return nhaCungCapDao.xoaNCC(maNCC);
    }

    public int getSoLuongNCC() {
        return getDSNhaCungCap().size();
    }

    /**
     * Đang hợp tác: trangThai = true
     */
    public int getSoLuongDangHopTac() {
        int count = 0;
        for (NhaCungCap ncc : getDSNhaCungCap()) {
            if (ncc.isTrangThai()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Ngừng hợp tác: trangThai = false
     */
    public int getSoLuongNgungHopTac() {
        int count = 0;
        for (NhaCungCap ncc : getDSNhaCungCap()) {
            if (!ncc.isTrangThai()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Tìm kiếm trong danh sách: nếu keyword bắt đầu bằng "NCC" tra theo mã, nếu
     * 10 chữ số tra theo SĐT, còn lại tìm kiếm không dấu theo tên/địa
     * chỉ/email.
     */
    public ArrayList<NhaCungCap> timKiem(ArrayList<NhaCungCap> ds, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return new ArrayList<>(ds);
        }
        String kw = keyword.trim();
        ArrayList<NhaCungCap> result = new ArrayList<>();

        if (kw.toUpperCase().startsWith("NCC")) {
            NhaCungCap found = layNCCTheoMa(kw.toUpperCase());
            if (found != null) {
                result.add(found);
            }
            return result;
        }

        if (kw.matches("\\d{10}")) {
            for (NhaCungCap ncc : ds) {
                if (kw.equals(ncc.getSoDienThoai())) {
                    result.add(ncc);
                    return result;
                }
            }
        }

        String kwNorm = normalize(kw);
        for (NhaCungCap ncc : ds) {
            if (normalize(ncc.getTenNhaCungCap()).contains(kwNorm)
                    || ncc.getMaNhaCungCap().toUpperCase().contains(kw.toUpperCase())
                    || (ncc.getDiaChi() != null && normalize(ncc.getDiaChi()).contains(kwNorm))
                    || (ncc.getEmail() != null && ncc.getEmail().toLowerCase().contains(kw.toLowerCase()))
                    || (ncc.getSoDienThoai() != null && ncc.getSoDienThoai().contains(kw))) {
                result.add(ncc);
            }
        }
        return result;
    }

    public static String normalize(String s) {
        if (s == null) {
            return "";
        }
        String normalized = Normalizer.normalize(s, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
    }
}
