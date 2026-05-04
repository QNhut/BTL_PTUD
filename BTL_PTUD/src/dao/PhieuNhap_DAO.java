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
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhap;

public class PhieuNhap_DAO {
    private Connection con;

    public ArrayList<PhieuNhap> getDSPhieuNhap() {
        ArrayList<PhieuNhap> dsPhieuNhap = new ArrayList<PhieuNhap>();
        String sql = "SELECT MaPhieuNhap, NgayNhap, MaNhanVien, MaNhaCungCap FROM PhieuNhap ORDER BY MaPhieuNhap";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement statement = con.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    dsPhieuNhap.add(mapPhieuNhap(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsPhieuNhap;
    }

    public PhieuNhap layPNTheoMa(String maPN) {
        String sql = "SELECT MaPhieuNhap, NgayNhap, MaNhanVien, MaNhaCungCap FROM PhieuNhap WHERE MaPhieuNhap = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maPN);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapPhieuNhap(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean taoPhieuNhap(PhieuNhap pn) {
        String sql = "INSERT INTO PhieuNhap (MaPhieuNhap, NgayNhap, MaNhanVien, MaNhaCungCap) VALUES (?, ?, ?, ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, pn.getMaPhieuNhap());
                stmt.setDate(2, Date.valueOf(pn.getNgayNhap()));
                stmt.setString(3, pn.getNhanVien().getMaNhanVien());
                stmt.setString(4, pn.getNhaCungCap().getMaNhaCungCap());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean capNhatPhieuNhap(PhieuNhap pn) {
        String sql = "UPDATE PhieuNhap SET NgayNhap = ?, MaNhanVien = ?, MaNhaCungCap = ? WHERE MaPhieuNhap = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setDate(1, Date.valueOf(pn.getNgayNhap()));
                stmt.setString(2, pn.getNhanVien().getMaNhanVien());
                stmt.setString(3, pn.getNhaCungCap().getMaNhaCungCap());
                stmt.setString(4, pn.getMaPhieuNhap());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoaPhieuNhap(String maPN) {
        String sql = "DELETE FROM PhieuNhap WHERE MaPhieuNhap = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maPN);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private PhieuNhap mapPhieuNhap(ResultSet rs) throws SQLException {
        String maPhieuNhap = rs.getString("MaPhieuNhap");
        LocalDate ngayNhap = rs.getDate("NgayNhap").toLocalDate();
        NhanVien nhanVien = new NhanVien(rs.getString("MaNhanVien"));
        NhaCungCap nhaCungCap = new NhaCungCap(rs.getString("MaNhaCungCap"));
        return new PhieuNhap(maPhieuNhap, ngayNhap, nhaCungCap, nhanVien, null);
    }

    /**
     * Sinh mã phiếu nhập tự động: PN + YYYY + 4 số (VD: PN20260001)
     */
    public String sinhMaTuDong() {
        String prefix = "PN";
        int nam = LocalDate.now().getYear();
        String pattern = prefix + nam; // PN2026
        String sql = "SELECT MAX(MaPhieuNhap) FROM PhieuNhap WHERE MaPhieuNhap LIKE ?";
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pattern + "0001";
    }
}
