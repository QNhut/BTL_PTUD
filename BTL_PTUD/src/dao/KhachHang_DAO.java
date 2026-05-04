package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.KhachHang;

public class KhachHang_DAO {
    private Connection con;

    // Lấy danh sách khách hàng
    public ArrayList<KhachHang> getDSKhachHang() {
        ArrayList<KhachHang> dsKH = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang ORDER BY MaKhachHang";

        try {
            con = ConnectDB.getInstance().getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                KhachHang kh = mapKhachHang(rs);
                dsKH.add(kh);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsKH;
    }

    // Map ResultSet → Object
    private KhachHang mapKhachHang(ResultSet rs) throws SQLException {
        String maKH = rs.getString("MaKhachHang");
        String tenKH = rs.getString("TenKhachHang");
        String sdt = rs.getString("SoDienThoai");
        String email = rs.getString("Email");
        boolean gioiTinh = rs.getBoolean("GioiTinh");
        int diem = rs.getInt("DiemTichLuy");
        boolean trangThai = rs.getBoolean("TrangThai");

        return new KhachHang(maKH, tenKH, sdt, email, gioiTinh, diem, trangThai);
    }

    // Lấy theo SĐT
    public KhachHang layKHTheoSDT(String sdt) {
        String sql = "SELECT * FROM KhachHang WHERE SoDienThoai = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, sdt);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapKhachHang(rs);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy theo mã
    public KhachHang layKHTheoMa(String maKH) {
        String sql = "SELECT * FROM KhachHang WHERE MaKhachHang = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maKH);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapKhachHang(rs);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm khách hàng
    public boolean themKhachHang(KhachHang kh) {
        String sql = "INSERT INTO KhachHang VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, kh.getMaKhachHang());
            stmt.setString(2, kh.getTenKhachHang());
            stmt.setString(3, kh.getSoDienThoai());
            stmt.setString(4, kh.getEmail());
            stmt.setBoolean(5, kh.isGioiTinh());
            stmt.setInt(6, kh.getDiemTichLuy());
            stmt.setBoolean(7, kh.isTrangThai());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật
    public boolean updateKhachHang(KhachHang kh) {
        String sql = "UPDATE KhachHang SET TenKhachHang=?, SoDienThoai=?, Email=?, GioiTinh=?, DiemTichLuy=?, TrangThai=? WHERE MaKhachHang=?";

        try {
            con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, kh.getTenKhachHang());
            stmt.setString(2, kh.getSoDienThoai());
            stmt.setString(3, kh.getEmail());
            stmt.setBoolean(4, kh.isGioiTinh());
            stmt.setInt(5, kh.getDiemTichLuy());
            stmt.setBoolean(6, kh.isTrangThai());
            stmt.setString(7, kh.getMaKhachHang());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa
    public boolean xoaKhachHang(String maKH) {
        String sql = "DELETE FROM KhachHang WHERE MaKhachHang=?";

        try {
            con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maKH);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}