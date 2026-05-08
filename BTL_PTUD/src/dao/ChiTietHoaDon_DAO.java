package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.SanPham;

public class ChiTietHoaDon_DAO {
    private Connection con;

    public ArrayList<ChiTietHoaDon> getDSTheoHoaDon(String maHoaDon) {
        ArrayList<ChiTietHoaDon> ds = new ArrayList<ChiTietHoaDon>();
        String sql = "SELECT MaHoaDon, MaSanPham, SoLuong, DonGia, COALESCE(GiaGoc, DonGia) AS GiaGoc FROM ChiTietHoaDon WHERE MaHoaDon = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maHoaDon);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ChiTietHoaDon ct = new ChiTietHoaDon(
                                new HoaDon(rs.getString("MaHoaDon")),
                                new SanPham(rs.getString("MaSanPham")),
                                rs.getInt("SoLuong"),
                                rs.getDouble("DonGia"));
                        ct.setGiaGoc(rs.getDouble("GiaGoc"));
                        ds.add(ct);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean them(ChiTietHoaDon ct) {
        return them(ct, ct.getDonGia());
    }

    // Lưu chi tiết kèm giá gốc (trước khi áp dụng khuyến mãi).
    public boolean them(ChiTietHoaDon ct, double giaGoc) {
        String sql = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, DonGia, GiaGoc) VALUES (?, ?, ?, ?, ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, ct.getHoaDon().getMaHoaDon());
                ps.setString(2, ct.getSanPham().getMaSanPham());
                ps.setInt(3, ct.getSoLuong());
                ps.setDouble(4, ct.getDonGia());
                ps.setDouble(5, giaGoc);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoa(String maHoaDon, String maSanPham) {
        String sql = "DELETE FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaSanPham = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maHoaDon);
                ps.setString(2, maSanPham);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
