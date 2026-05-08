package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.KeSanPham;
import entity.LoSanPham;
import entity.PhieuNhap;
import entity.SanPham;

public class LoSanPham_DAO {
    private Connection con;

    public ArrayList<LoSanPham> getDSLoSanPham() {
        ArrayList<LoSanPham> ds = new ArrayList<LoSanPham>();
        String sql = "SELECT MaLoSanPham, MaSanPham, MaPhieuNhap, MaKeSanPham, SoLuong, DonViTinh, HanSuDung, TrangThai FROM LoSanPham ORDER BY MaLoSanPham";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    try {
                        LoSanPham lo = new LoSanPham();
                        lo.setMaLoSanPham(rs.getString("MaLoSanPham"));
                        lo.setSanPham(new SanPham(rs.getString("MaSanPham")));
                        String maPN = rs.getString("MaPhieuNhap");
                        if (maPN != null && !maPN.trim().isEmpty())
                            lo.setPhieuNhap(new PhieuNhap(maPN));
                        String maKe = rs.getString("MaKeSanPham");
                        if (maKe != null && !maKe.trim().isEmpty())
                            lo.setKeSanPham(new KeSanPham(maKe));
                        lo.setSoLuong(rs.getInt("SoLuong"));
                        lo.setDonViTinh(rs.getString("DonViTinh"));
                        Date hsd = rs.getDate("HanSuDung");
                        lo.setHanSuDung(hsd == null ? null : hsd.toLocalDate());
                        lo.setTrangThai(rs.getBoolean("TrangThai"));
                        ds.add(lo);
                    } catch (Exception rowEx) {
                        rowEx.printStackTrace(); // log lỗi từng hàng nhưng tiếp tục
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean them(LoSanPham lo) {
        String sql = "INSERT INTO LoSanPham (MaLoSanPham, MaSanPham, MaPhieuNhap, MaKeSanPham, SoLuong, DonViTinh, HanSuDung, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, lo.getMaLoSanPham());
                ps.setString(2, lo.getSanPham().getMaSanPham());
                ps.setString(3, lo.getPhieuNhap().getMaPhieuNhap());
                ps.setString(4, lo.getKeSanPham().getMaKeSanPham());
                ps.setInt(5, lo.getSoLuong());
                ps.setString(6, lo.getDonViTinh());
                if (lo.getHanSuDung() == null) {
                    ps.setDate(7, null);
                } else {
                    ps.setDate(7, Date.valueOf(lo.getHanSuDung()));
                }
                ps.setBoolean(8, lo.isTrangThai());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int layTongSoLuongTonTheoMaSanPham(String maSanPham) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            return 0;
        }

        String sql = "SELECT COALESCE(SUM(SoLuong), 0) AS TongSoLuong "
                + "FROM LoSanPham WHERE MaSanPham = ? AND TrangThai = ? AND (HanSuDung IS NULL OR HanSuDung >= ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            if (con == null) {
                return 0;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maSanPham.trim());
                ps.setBoolean(2, true);
                ps.setDate(3, Date.valueOf(LocalDate.now()));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("TongSoLuong");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy tất cả lô theo mã sản phẩm (kể cả hết hạn), sắp xếp HSD tăng dần.
    // Chỉ lấy dữ liệu trực tiếp từ bảng LoSanPham – Service sẽ enrich thêm.
    public ArrayList<LoSanPham> layTheoMaSanPham(String maSanPham) {
        ArrayList<LoSanPham> ds = new ArrayList<>();
        String sql = "SELECT MaLoSanPham, MaSanPham, MaPhieuNhap, MaKeSanPham, SoLuong, DonViTinh, HanSuDung, TrangThai "
                + "FROM LoSanPham WHERE MaSanPham = ? ORDER BY HanSuDung ASC";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maSanPham);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        try {
                            LoSanPham lo = new LoSanPham();
                            lo.setMaLoSanPham(rs.getString("MaLoSanPham"));
                            lo.setSanPham(new SanPham(rs.getString("MaSanPham")));
                            String maPN = rs.getString("MaPhieuNhap");
                            if (maPN != null && !maPN.trim().isEmpty())
                                lo.setPhieuNhap(new PhieuNhap(maPN));
                            String maKe = rs.getString("MaKeSanPham");
                            if (maKe != null && !maKe.trim().isEmpty())
                                lo.setKeSanPham(new KeSanPham(maKe));
                            lo.setSoLuong(rs.getInt("SoLuong"));
                            lo.setDonViTinh(rs.getString("DonViTinh"));
                            Date hsd = rs.getDate("HanSuDung");
                            lo.setHanSuDung(hsd == null ? null : hsd.toLocalDate());
                            lo.setTrangThai(rs.getBoolean("TrangThai"));
                            ds.add(lo);
                        } catch (Exception rowEx) {
                            rowEx.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    // Giảm số lượng tồn kho theo FEFO (lô gần hết hạn nhất bị trừ trước).
    // Chỉ trừ các lô còn TrangThai=true và chưa hết hạn.
    public void giamSoLuongTheoSanPham(String maSanPham, int soLuongCan) {
        ArrayList<LoSanPham> dsLo = layTheoMaSanPham(maSanPham);
        int conLai = soLuongCan;
        String sqlCapNhat = "UPDATE LoSanPham SET SoLuong = ?, TrangThai = ? WHERE MaLoSanPham = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sqlCapNhat)) {
                for (LoSanPham lo : dsLo) {
                    if (conLai <= 0) break;
                    if (!lo.isTrangThai()) continue;
                    if (lo.getHanSuDung() != null && lo.getHanSuDung().isBefore(LocalDate.now())) continue;
                    int tru = Math.min(lo.getSoLuong(), conLai);
                    int soLuongMoi = lo.getSoLuong() - tru;
                    ps.setInt(1, soLuongMoi);
                    ps.setBoolean(2, soLuongMoi > 0);
                    ps.setString(3, lo.getMaLoSanPham());
                    ps.executeUpdate();
                    conLai -= tru;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi giảm tồn kho lô sản phẩm: " + e.getMessage());
        }
    }

    // Cập nhật kệ chứa của một lô sản phẩm.
    public boolean capNhatKe(String maLoSanPham, String maKeSanPham) {
        String sql = "UPDATE LoSanPham SET MaKeSanPham = ? WHERE MaLoSanPham = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maKeSanPham);
                ps.setString(2, maLoSanPham);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Sinh mã lô sản phẩm tự động: LSP + YYYY + 3 số (VD: LSP2026001)
    public String sinhMaTuDong() {
        String prefix = "LSP";
        int nam = LocalDate.now().getYear();
        String pattern = prefix + nam;
        String sql = "SELECT MAX(MaLoSanPham) FROM LoSanPham WHERE MaLoSanPham LIKE ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pattern + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String maxMa = rs.getString(1);
                        if (maxMa != null && maxMa.length() > pattern.length()) {
                            try {
                                int stt = Integer.parseInt(maxMa.substring(pattern.length())) + 1;
                                return pattern + String.format("%03d", stt);
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pattern + "0001";
    }
}
