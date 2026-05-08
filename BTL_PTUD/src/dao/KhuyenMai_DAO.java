package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.KhuyenMai;

public class KhuyenMai_DAO {
    private Connection con;

    public ArrayList<KhuyenMai> getDSKhuyenMai() {
        ArrayList<KhuyenMai> ds = new ArrayList<KhuyenMai>();
        String sql = "SELECT MaKhuyenMai, TenKhuyenMai, PhanTramGG, NgayBatDau, NgayKetThuc, TrangThai FROM KhuyenMai ORDER BY MaKhuyenMai";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    ds.add(mapKhuyenMai(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public KhuyenMai layTheoMa(String maKhuyenMai) {
        String sql = "SELECT MaKhuyenMai, TenKhuyenMai, PhanTramGG, NgayBatDau, NgayKetThuc, TrangThai FROM KhuyenMai WHERE MaKhuyenMai = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maKhuyenMai);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return mapKhuyenMai(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean them(KhuyenMai km) {
        String sql = "INSERT INTO KhuyenMai (MaKhuyenMai, TenKhuyenMai, PhanTramGG, NgayBatDau, NgayKetThuc, TrangThai) VALUES (?, ?, ?, ?, ?, ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, km.getMaKhuyenMai());
                ps.setString(2, km.getTenKhuyenMai());
                ps.setDouble(3, km.getPhanTramGG());
                ps.setTimestamp(4, Timestamp.valueOf(km.getNgayBatDau().atStartOfDay()));
                ps.setTimestamp(5, Timestamp.valueOf(km.getNgayKetThuc().atStartOfDay()));
                ps.setBoolean(6, km.isTrangThai());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean capNhat(KhuyenMai km) {
        String sql = "UPDATE KhuyenMai SET TenKhuyenMai = ?, PhanTramGG = ?, NgayBatDau = ?, NgayKetThuc = ?, TrangThai = ? WHERE MaKhuyenMai = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, km.getTenKhuyenMai());
                ps.setDouble(2, km.getPhanTramGG());
                ps.setTimestamp(3, Timestamp.valueOf(km.getNgayBatDau().atStartOfDay()));
                ps.setTimestamp(4, Timestamp.valueOf(km.getNgayKetThuc().atStartOfDay()));
                ps.setBoolean(5, km.isTrangThai());
                ps.setString(6, km.getMaKhuyenMai());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoa(String maKhuyenMai) {
        String sql = "DELETE FROM KhuyenMai WHERE MaKhuyenMai = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maKhuyenMai);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private KhuyenMai mapKhuyenMai(ResultSet rs) throws SQLException {
        return new KhuyenMai(
                rs.getString("MaKhuyenMai"),
                rs.getString("TenKhuyenMai"),
                rs.getDouble("PhanTramGG"),
                rs.getTimestamp("NgayBatDau").toLocalDateTime().toLocalDate(),
                rs.getTimestamp("NgayKetThuc").toLocalDateTime().toLocalDate(),
                rs.getBoolean("TrangThai"));
    }

    // Trả về map maKhuyenMai → số sản phẩm đang áp dụng, load 1 lần
    public java.util.Map<String, Integer> getDemSanPhamTheoKM() {
        String sql = "SELECT MaKhuyenMai, COUNT(*) AS SoLuong FROM SanPham GROUP BY MaKhuyenMai";
        java.util.Map<String, Integer> map = new java.util.HashMap<>();
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    map.put(rs.getString("MaKhuyenMai"), rs.getInt("SoLuong"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    // Áp dụng (gán) khuyến mãi cho 1 sản phẩm
    public boolean apDungChoSanPham(String maSP, String maKM) {
        String sql = "UPDATE SanPham SET MaKhuyenMai = ? WHERE MaSanPham = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maKM);
                ps.setString(2, maSP);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Gỡ (huỷ) khuyến mãi khỏi 1 sản phẩm bằng cách set MaKhuyenMai = NULL
    public boolean goKhuyenMaiKhoiSanPham(String maSP) {
        String sql = "UPDATE SanPham SET MaKhuyenMai = NULL WHERE MaSanPham = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maSP);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Gỡ khuyến mãi khỏi TẤT CẢ sản phẩm đang áp dụng {@code maKM}. Trả về số SP bị gỡ.
    public int goKhuyenMaiKhoiTatCa(String maKM) {
        String sql = "UPDATE SanPham SET MaKhuyenMai = NULL WHERE MaKhuyenMai = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maKM);
                return ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Sinh mã khuyến mãi tự động: KM + YYYY + 3 số (VD: KM2026001)
    public String sinhMaTuDong() {
        String prefix = "KM";
        int nam = java.time.LocalDate.now().getYear();
        String pattern = prefix + nam;
        String sql = "SELECT MAX(MaKhuyenMai) FROM KhuyenMai WHERE MaKhuyenMai LIKE ?";
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
        } catch (SQLException e) { e.printStackTrace(); }
        return pattern + "001";
    }
}
