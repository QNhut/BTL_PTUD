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

    /** Sinh mã hóa đơn tự động: HD + YYYY + 4 số (VD: HD20260001) */
    public String sinhMaTuDong() {
        String prefix = "HD";
        int nam = LocalDate.now().getYear();
        String pattern = prefix + nam;
        String sql = "SELECT MAX(MaHoaDon) FROM HoaDon WHERE MaHoaDon LIKE ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pattern + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String maxMa = rs.getString(1);
                        if (maxMa != null && maxMa.length() >= pattern.length() + 4) {
                            int stt = Integer.parseInt(maxMa.substring(pattern.length())) + 1;
                            return pattern + String.format("%04d", stt);
                        }
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return pattern + "0001";
    }
}
