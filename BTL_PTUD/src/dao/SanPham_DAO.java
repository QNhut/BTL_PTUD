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
                + "LEFT JOIN KhuyenMai km ON sp.MaKhuyenMai = km.MaKhuyenMai "
                + "LEFT JOIN Thue t ON sp.MaThue = t.MaThue "
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
                + "sp.NoiSanXuat, sp.MaLoaiSanPham, sp.MaKhuyenMai, sp.MaThue, sp.TrangThai, sp.HinhAnh, sp.DonViTinh, "
                + "lsp.TenLoaiSanPham, lsp.MoTa AS MoTaLoai, "
                + "km.TenKhuyenMai, km.PhanTramGG, km.NgayBatDau, km.NgayKetThuc, km.TrangThai AS TrangThaiKM, "
                + "t.TenThue, t.PhanTramThue, t.MoTa AS MoTaThue "
                + "FROM SanPham sp "
                + "JOIN LoaiSanPham lsp ON sp.MaLoaiSanPham = lsp.MaLoaiSanPham "
                + "LEFT JOIN KhuyenMai km ON sp.MaKhuyenMai = km.MaKhuyenMai "
                + "LEFT JOIN Thue t ON sp.MaThue = t.MaThue "
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
                if (sp.getKhuyenMai() != null && sp.getKhuyenMai().getMaKhuyenMai() != null)
                    stmt.setString(9, sp.getKhuyenMai().getMaKhuyenMai());
                else
                    stmt.setNull(9, java.sql.Types.NVARCHAR);
                if (sp.getThue() != null && sp.getThue().getMaThue() != null)
                    stmt.setString(10, sp.getThue().getMaThue());
                else
                    stmt.setNull(10, java.sql.Types.NVARCHAR);
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
                if (sp.getKhuyenMai() != null && sp.getKhuyenMai().getMaKhuyenMai() != null)
                    stmt.setString(8, sp.getKhuyenMai().getMaKhuyenMai());
                else
                    stmt.setNull(8, java.sql.Types.NVARCHAR);
                if (sp.getThue() != null && sp.getThue().getMaThue() != null)
                    stmt.setString(9, sp.getThue().getMaThue());
                else
                    stmt.setNull(9, java.sql.Types.NVARCHAR);
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

        String maKM = rs.getString("MaKhuyenMai");
        KhuyenMai km = null;
        if (maKM != null) {
            km = new KhuyenMai(
                    maKM,
                    rs.getString("TenKhuyenMai"),
                    rs.getDouble("PhanTramGG"),
                    rs.getTimestamp("NgayBatDau") != null ? rs.getTimestamp("NgayBatDau").toLocalDateTime().toLocalDate() : java.time.LocalDate.now(),
                    rs.getTimestamp("NgayKetThuc") != null ? rs.getTimestamp("NgayKetThuc").toLocalDateTime().toLocalDate() : java.time.LocalDate.now().plusYears(1),
                    rs.getBoolean("TrangThaiKM"));
        }

        String maThue = rs.getString("MaThue");
        Thue thue = null;
        if (maThue != null) {
            thue = new Thue(
                    maThue,
                    rs.getString("TenThue"),
                    rs.getDouble("PhanTramThue"),
                    rs.getString("MoTaThue"));
        }

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
        // DonViTinh nằm trong LoSanPham, không có trong bảng SanPham
        sp.setDonViTinh(null);
        return sp;
    }

    // Sinh mã sản phẩm tự động: SP + YYYY + 3 số (VD: SP2026001)
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

    // ==================== THỐNG KÊ ====================

    public int demTongSanPham() {
        String sql = "SELECT COUNT(*) FROM SanPham WHERE TrangThai = 1";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int demSanPhamKhuyenMai() {
        String sql = "SELECT COUNT(*) FROM SanPham sp "
                   + "JOIN KhuyenMai km ON sp.MaKhuyenMai = km.MaKhuyenMai "
                   + "WHERE sp.TrangThai = 1 AND km.TrangThai = 1 "
                   + "AND km.PhanTramGG > 0 "
                   + "AND CAST(GETDATE() AS DATE) BETWEEN km.NgayBatDau AND km.NgayKetThuc";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public int tongTonKho() {
        String sql = "SELECT ISNULL(SUM(SoLuong), 0) FROM LoSanPham WHERE TrangThai = 1 AND HanSuDung > GETDATE()";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    public java.util.LinkedHashMap<String, Integer> soLuongBanTheoThang(int nam) {
        java.util.LinkedHashMap<String, Integer> map = new java.util.LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) map.put("T" + i, 0);
        String sql = "SELECT MONTH(hd.NgayLap) AS Thang, SUM(ct.SoLuong) AS SL "
                   + "FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon "
                   + "WHERE YEAR(hd.NgayLap) = ? "
                   + "GROUP BY MONTH(hd.NgayLap) ORDER BY Thang";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, nam);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        map.put("T" + rs.getInt("Thang"), rs.getInt("SL"));
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    public ArrayList<Object[]> layDanhSachSPBanChay(String tuNgay, String denNgay) {
        ArrayList<Object[]> rows = new ArrayList<>();
        boolean hasDate = (tuNgay != null && denNgay != null);
        String sql = "SELECT sp.MaSanPham, sp.TenSanPham, lsp.TenLoaiSanPham, "
                   + "SUM(ct.SoLuong) AS SoLuongBan, "
                   + "SUM(ct.SoLuong * ct.DonGia) AS DoanhThu "
                   + "FROM SanPham sp "
                   + "JOIN LoaiSanPham lsp ON sp.MaLoaiSanPham = lsp.MaLoaiSanPham "
                   + "JOIN ChiTietHoaDon ct ON sp.MaSanPham = ct.MaSanPham "
                   + "JOIN HoaDon hd ON ct.MaHoaDon = hd.MaHoaDon "
                   + (hasDate ? "WHERE CAST(hd.NgayLap AS DATE) BETWEEN ? AND ? " : "")
                   + "GROUP BY sp.MaSanPham, sp.TenSanPham, lsp.TenLoaiSanPham "
                   + "ORDER BY SoLuongBan DESC";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (hasDate) {
                    ps.setString(1, tuNgay);
                    ps.setString(2, denNgay);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        rows.add(new Object[]{
                            rs.getString("MaSanPham"),
                            rs.getString("TenSanPham"),
                            rs.getString("TenLoaiSanPham"),
                            rs.getInt("SoLuongBan"),
                            rs.getDouble("DoanhThu")
                        });
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    public ArrayList<Object[]> layDanhSachSPTonKhoThap() {
        ArrayList<Object[]> rows = new ArrayList<>();
        String sql = "SELECT sp.TenSanPham, ISNULL(SUM(ls.SoLuong), 0) AS TonKho "
                   + "FROM SanPham sp "
                   + "LEFT JOIN LoSanPham ls ON sp.MaSanPham = ls.MaSanPham AND ls.TrangThai = 1 AND ls.HanSuDung > GETDATE() "
                   + "WHERE sp.TrangThai = 1 "
                   + "GROUP BY sp.MaSanPham, sp.TenSanPham "
                   + "HAVING ISNULL(SUM(ls.SoLuong), 0) < 50 "
                   + "ORDER BY TonKho ASC";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    int tonKho = rs.getInt("TonKho");
                    String trangThai = tonKho == 0 ? "Hết hàng" : "Sắp hết";
                    rows.add(new Object[]{
                        rs.getString("TenSanPham"),
                        tonKho,
                        trangThai
                    });
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }
}
