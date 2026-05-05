package service;

import java.util.ArrayList;
import java.util.List;

import dao.NhaCungCap_DAO;
import entity.NhaCungCap;

public class NhaCungCap_Service {

    private final NhaCungCap_DAO nhaCungCapDAO = new NhaCungCap_DAO();

    public ArrayList<NhaCungCap> getDSNhaCungCap() {
        return nhaCungCapDAO.getDSNhaCungCap();
    }

    public int getSoLuongNCC() {
        return nhaCungCapDAO.getDSNhaCungCap().size();
    }

    public int getSoLuongDangHopTac() {
        int count = 0;
        for (NhaCungCap ncc : nhaCungCapDAO.getDSNhaCungCap()) {
            if (ncc.isTrangThai()) {
                count++;
            }
        }
        return count;
    }

    public int getSoLuongNgungHopTac() {
        int count = 0;
        for (NhaCungCap ncc : nhaCungCapDAO.getDSNhaCungCap()) {
            if (!ncc.isTrangThai()) {
                count++;
            }
        }
        return count;
    }

    public ArrayList<NhaCungCap> timKiem(ArrayList<NhaCungCap> ds, String keyword) {
        ArrayList<NhaCungCap> result = new ArrayList<>();
        if (keyword == null || keyword.trim().isEmpty()) {
            result.addAll(ds);
            return result;
        }
        String kw = keyword.trim().toLowerCase();
        for (NhaCungCap ncc : ds) {
            boolean matchTen = ncc.getTenNhaCungCap() != null && ncc.getTenNhaCungCap().toLowerCase().contains(kw);
            boolean matchMa = ncc.getMaNhaCungCap() != null && ncc.getMaNhaCungCap().toLowerCase().contains(kw);
            boolean matchSDT = ncc.getSoDienThoai() != null && ncc.getSoDienThoai().contains(kw);
            if (matchTen || matchMa || matchSDT) {
                result.add(ncc);
            }
        }
        return result;
    }

    public boolean themNCC(NhaCungCap ncc) {
        return nhaCungCapDAO.themNCC(ncc);
    }

    public boolean capNhatNCC(NhaCungCap ncc) {
        return nhaCungCapDAO.updateNCC(ncc);
    }

    public boolean xoaNCC(String maNCC) {
        return nhaCungCapDAO.xoaNCC(maNCC);
    }

    public List<String> layDanhSachTenNhaCungCap() {
        List<String> tenNhaCungCaps = new ArrayList<String>();
        for (NhaCungCap nhaCungCap : nhaCungCapDAO.getDSNhaCungCap()) {
            if (nhaCungCap == null || nhaCungCap.getTenNhaCungCap() == null) {
                continue;
            }

            String tenNhaCungCap = nhaCungCap.getTenNhaCungCap().trim();
            if (tenNhaCungCap.isEmpty() || tenNhaCungCaps.contains(tenNhaCungCap)) {
                continue;
            }
            tenNhaCungCaps.add(tenNhaCungCap);
        }
        return tenNhaCungCaps;
    }

    public NhaCungCap layNhaCungCapTheoTen(String ten) {
        if (ten == null || ten.trim().isEmpty()) {
            return null;
        }
        String canTim = ten.trim();
        for (NhaCungCap ncc : nhaCungCapDAO.getDSNhaCungCap()) {
            if (ncc.getTenNhaCungCap() != null && ncc.getTenNhaCungCap().trim().equalsIgnoreCase(canTim)) {
                return ncc;
            }
        }
        return null;
    }
}
