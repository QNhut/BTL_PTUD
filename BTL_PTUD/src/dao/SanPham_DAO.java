package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.KhuyenMai;
import entity.LoaiSanPham;
import entity.SanPham;
import entity.Thue;

public class SanPham_DAO {
    private Connection con;

    public ArrayList<SanPham> getDSSanPham() {
        ArrayList<SanPham> dsSanPham = new ArrayList<SanPham>();
        String sql = "SELECT sp.MaSanPham, sp.TenSanPham, sp.CongDung, sp.ThanhPhan, sp.HanSuDung, sp.GiaThanh, "
                + "sp.NoiSanXuat, sp.MaLoaiSanPham, sp.MaKhuyenMai, sp.MaThue, sp.TrangThai, sp.HinhAnh, "
                + "lsp.TenLoaiSanPham, lsp.MoTa AS MoTaLoai, "
                + "km.TenKhuyenMai, km.PhanTramGG, km.NgayBatDau, km.NgayKetThuc, km.TrangThai AS TrangThaiKM, "
                + "t.TenThue, t.PhanTramThue, t.MoTa AS MoTaThue "
                + "FROM SanPham sp "
                + "JOIN LoaiSanPham lsp ON sp.MaLoaiSanPham = lsp.MaLoaiSanPham "
                + "JOIN KhuyenMai km ON sp.MaKhuyenMai = km.MaKhuyenMai "
                + "JOIN Thue t ON sp.MaThue = t.MaThue "
                + "ORDER BY sp.MaSanPham";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement statement = con.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    dsSanPham.add(mapSanPham(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsSanPham;
    }

    public SanPham laySanPhamTheoMa(String maSP) {
        String sql = "SELECT sp.MaSanPham, sp.TenSanPham, sp.CongDung, sp.ThanhPhan, sp.HanSuDung, sp.GiaThanh, "
                + "sp.NoiSanXuat, sp.MaLoaiSanPham, sp.MaKhuyenMai, sp.MaThue, sp.TrangThai, sp.HinhAnh, "
                + "lsp.TenLoaiSanPham, lsp.MoTa AS MoTaLoai, "
                + "km.TenKhuyenMai, km.PhanTramGG, km.NgayBatDau, km.NgayKetThuc, km.TrangThai AS TrangThaiKM, "
                + "t.TenThue, t.PhanTramThue, t.MoTa AS MoTaThue "
                + "FROM SanPham sp "
                + "JOIN LoaiSanPham lsp ON sp.MaLoaiSanPham = lsp.MaLoaiSanPham "
                + "JOIN KhuyenMai km ON sp.MaKhuyenMai = km.MaKhuyenMai "
                + "JOIN Thue t ON sp.MaThue = t.MaThue "
                + "WHERE sp.MaSanPham = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maSP);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapSanPham(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean themSanPham(SanPham sp) {
        String sql = "INSERT INTO SanPham (MaSanPham, TenSanPham, CongDung, ThanhPhan, HanSuDung, GiaThanh, NoiSanXuat, MaLoaiSanPham, MaKhuyenMai, MaThue, TrangThai, HinhAnh) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, sp.getMaSanPham());
                stmt.setString(2, sp.getTenSanPham());
                stmt.setString(3, sp.getCongDung());
                stmt.setString(4, sp.getThanhPhan());
                stmt.setInt(5, sp.getHanSuDungThang());
                stmt.setDouble(6, sp.getGiaThanh());
                stmt.setString(7, sp.getNoiSanXuat());
                stmt.setString(8, sp.getLoaiSanPham().getMaLoaiSanPham());
                stmt.setString(9, sp.getKhuyenMai().getMaKhuyenMai());
                stmt.setString(10, sp.getThue().getMaThue());
                stmt.setBoolean(11, sp.isTrangThai());
                stmt.setString(12, sp.getHinhAnh());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateSanPham(SanPham sp) {
        String sql = "UPDATE SanPham SET TenSanPham = ?, CongDung = ?, ThanhPhan = ?, HanSuDung = ?, GiaThanh = ?, NoiSanXuat = ?, "
                + "MaLoaiSanPham = ?, MaKhuyenMai = ?, MaThue = ?, TrangThai = ?, HinhAnh = ? WHERE MaSanPham = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, sp.getTenSanPham());
                stmt.setString(2, sp.getCongDung());
                stmt.setString(3, sp.getThanhPhan());
                stmt.setInt(4, sp.getHanSuDungThang());
                stmt.setDouble(5, sp.getGiaThanh());
                stmt.setString(6, sp.getNoiSanXuat());
                stmt.setString(7, sp.getLoaiSanPham().getMaLoaiSanPham());
                stmt.setString(8, sp.getKhuyenMai().getMaKhuyenMai());
                stmt.setString(9, sp.getThue().getMaThue());
                stmt.setBoolean(10, sp.isTrangThai());
                stmt.setString(11, sp.getHinhAnh());
                stmt.setString(12, sp.getMaSanPham());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoaSanPham(String maSP) {
        String sql = "DELETE FROM SanPham WHERE MaSanPham = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maSP);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private SanPham mapSanPham(ResultSet rs) throws SQLException {
        LoaiSanPham loai = new LoaiSanPham(
                rs.getString("MaLoaiSanPham"),
                rs.getString("TenLoaiSanPham"),
                rs.getString("MoTaLoai"));

        KhuyenMai km = new KhuyenMai(
                rs.getString("MaKhuyenMai"),
                rs.getString("TenKhuyenMai"),
                rs.getDouble("PhanTramGG"),
                rs.getTimestamp("NgayBatDau").toLocalDateTime().toLocalDate(),
                rs.getTimestamp("NgayKetThuc").toLocalDateTime().toLocalDate(),
                rs.getBoolean("TrangThaiKM"));

        Thue thue = new Thue(
                rs.getString("MaThue"),
                rs.getString("TenThue"),
                rs.getDouble("PhanTramThue"),
                rs.getString("MoTaThue"));

        SanPham sp = new SanPham();
        sp.setMaSanPham(rs.getString("MaSanPham"));
        sp.setTenSanPham(rs.getString("TenSanPham"));
        sp.setCongDung(rs.getString("CongDung"));
        sp.setThanhPhan(rs.getString("ThanhPhan"));
        sp.setHanSuDungThang(rs.getInt("HanSuDung"));
        sp.setGiaThanh(rs.getDouble("GiaThanh"));
        sp.setNoiSanXuat(rs.getString("NoiSanXuat"));
        sp.setLoaiSanPham(loai);
        sp.setKhuyenMai(km);
        sp.setThue(thue);
        sp.setTrangThai(rs.getBoolean("TrangThai"));
        sp.setHinhAnh(rs.getString("HinhAnh"));
        return sp;
    }

    /** Sinh mã sản phẩm tự động: SP + YYYY + 3 số (VD: SP2026001) */
    public String sinhMaTuDong() {
        String prefix = "SP";
        int nam = java.time.LocalDate.now().getYear();
        String pattern = prefix + nam;
        String sql = "SELECT MAX(MaSanPham) FROM SanPham WHERE MaSanPham LIKE ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (java.sql.PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pattern + "%");
                try (java.sql.ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String maxMa = rs.getString(1);
                        if (maxMa != null && maxMa.length() >= pattern.length() + 3) {
                            int stt = Integer.parseInt(maxMa.substring(pattern.length())) + 1;
                            return pattern + String.format("%03d", stt);
                        }
                    }
                }
            }
        } catch (java.sql.SQLException e) { e.printStackTrace(); }
        return pattern + "001";
    }
}
