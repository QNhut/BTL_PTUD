package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.ChiTietPhieuNhap;
import entity.PhieuNhap;
import entity.SanPham;

public class ChiTietPhieuNhap_DAO {
    private Connection con;

    public ArrayList<ChiTietPhieuNhap> getDSTheoPhieuNhap(String maPhieuNhap) {
        ArrayList<ChiTietPhieuNhap> ds = new ArrayList<ChiTietPhieuNhap>();
        String sql = "SELECT MaPhieuNhap, MaSanPham, SoLuong, GiaNhap FROM ChiTietPhieuNhap WHERE MaPhieuNhap = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maPhieuNhap);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        ChiTietPhieuNhap ct = new ChiTietPhieuNhap(
                                new PhieuNhap(rs.getString("MaPhieuNhap")),
                                new SanPham(rs.getString("MaSanPham")),
                                rs.getInt("SoLuong"),
                                rs.getDouble("GiaNhap"));
                        ds.add(ct);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean them(ChiTietPhieuNhap ct) {
        String sql = "INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaSanPham, SoLuong, GiaNhap) VALUES (?, ?, ?, ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, ct.getPhieuNhap().getMaPhieuNhap());
                ps.setString(2, ct.getSanPham().getMaSanPham());
                ps.setInt(3, ct.getSoLuong());
                ps.setDouble(4, ct.getGiaNhap());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoa(String maPhieuNhap, String maSanPham) {
        String sql = "DELETE FROM ChiTietPhieuNhap WHERE MaPhieuNhap = ? AND MaSanPham = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maPhieuNhap);
                ps.setString(2, maSanPham);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
