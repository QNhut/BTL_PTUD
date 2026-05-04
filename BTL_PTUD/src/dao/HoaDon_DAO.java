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
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;

public class HoaDon_DAO {
    private Connection con;

    public ArrayList<HoaDon> getDSHoaDon() {
        ArrayList<HoaDon> dsHD = new ArrayList<HoaDon>();
        String sql = "SELECT MaHoaDon, NgayLap, MaKhachHang, MaNhanVien, MaPTTT FROM HoaDon ORDER BY MaHoaDon";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement statement = con.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    dsHD.add(mapHoaDon(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsHD;
    }

    public boolean taoHoaDon(HoaDon hd) {
        String sql = "INSERT INTO HoaDon (MaHoaDon, NgayLap, MaKhachHang, MaNhanVien, MaPTTT) VALUES (?, ?, ?, ?, ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, hd.getMaHoaDon());
                stmt.setDate(2, Date.valueOf(hd.getNgayLap()));
                stmt.setString(3, hd.getKhachHang().getMaKhachHang());
                stmt.setString(4, hd.getNhanVien().getMaNhanVien());
                stmt.setString(5, hd.getMaPTTT());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public HoaDon layHDTheoMa(String maHD) {
        String sql = "SELECT MaHoaDon, NgayLap, MaKhachHang, MaNhanVien, MaPTTT FROM HoaDon WHERE MaHoaDon = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maHD);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapHoaDon(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean xoaHoaDon(String maHD) {
        String sql = "DELETE FROM HoaDon WHERE MaHoaDon = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maHD);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private HoaDon mapHoaDon(ResultSet rs) throws SQLException {
        String maHoaDon = rs.getString("MaHoaDon");
        LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate();
        KhachHang khachHang = new KhachHang(rs.getString("MaKhachHang"));
        NhanVien nhanVien = new NhanVien(rs.getString("MaNhanVien"));
        String maPTTT = rs.getString("MaPTTT");
        return new HoaDon(maHoaDon, ngayLap, 0, 0, maPTTT, nhanVien, khachHang);
    }
}
