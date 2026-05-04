package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.ChucVu;
import entity.NhanVien;

public class NhanVien_DAO {
    private Connection con;
    private ArrayList<NhanVien> dsNV;

    public NhanVien_DAO() {
        dsNV = new ArrayList<NhanVien>();
    }

    // Lấy nhân viên theo mã
    public NhanVien layNVTheoMa(String maNhanVien) {
        NhanVien nv = null;
        try {
            con = ConnectDB.getInstance().getConnection();
            String sql = "SELECT nv.*, cv.MaChucVu, cv.TenChucVu, cv.MoTa "
                       + "FROM NhanVien nv "
                       + "LEFT JOIN ChucVu cv ON nv.MaChucVu = cv.MaChucVu "
                       + "WHERE nv.MaNhanVien = ?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maNhanVien);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nv = mapResultSetToNhanVien(rs);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nv;
    }

    // Lấy tất cả nhân viên
    public ArrayList<NhanVien> getDSNhanVien() {
        dsNV.clear();
        try {
            con = ConnectDB.getInstance().getConnection();
            String sql = "SELECT nv.*, cv.MaChucVu, cv.TenChucVu, cv.MoTa "
                       + "FROM NhanVien nv "
                       + "LEFT JOIN ChucVu cv ON nv.MaChucVu = cv.MaChucVu "
                       + "ORDER BY nv.MaNhanVien";
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                dsNV.add(mapResultSetToNhanVien(rs));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsNV;
    }

    // Thêm nhân viên
    public boolean themNhanVien(NhanVien nv) {
        int n = 0;
        try {
            con = ConnectDB.getInstance().getConnection();
            String sql = "INSERT INTO NhanVien (MaNhanVien, TenNhanVien, GioiTinh, SoDienThoai, email, CCCD, DiaChi, TrangThai, MaChucVu, HinhAnh) "
                       + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, nv.getMaNhanVien());
            stmt.setString(2, nv.getTenNhanVien());
            stmt.setBoolean(3, nv.isGioiTinh());
            stmt.setString(4, nv.getSoDienThoai());
            stmt.setString(5, nv.getEmail());
            stmt.setString(6, nv.getCCCD());

            if (nv.getDiaChi() == null) {
                stmt.setNull(7, Types.NVARCHAR);
            } else {
                stmt.setString(7, nv.getDiaChi());
            }

            stmt.setBoolean(8, nv.isTrangThai());

            if (nv.getChucVu() == null) {
                stmt.setNull(9, Types.VARCHAR);
            } else {
                stmt.setString(9, nv.getChucVu().getMaChucVu());
            }

            if (nv.getHinhAnh() == null) {
                stmt.setNull(10, Types.VARCHAR);
            } else {
                stmt.setString(10, nv.getHinhAnh());
            }

            n = stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // Sửa nhân viên
    public boolean updateNhanVien(NhanVien nv) {
        int n = 0;
        try {
            con = ConnectDB.getInstance().getConnection();
            String sql = "UPDATE NhanVien SET TenNhanVien=?, GioiTinh=?, SoDienThoai=?, email=?, CCCD=?, DiaChi=?, TrangThai=?, MaChucVu=?, HinhAnh=? "
                       + "WHERE MaNhanVien=?";
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, nv.getTenNhanVien());
            stmt.setBoolean(2, nv.isGioiTinh());
            stmt.setString(3, nv.getSoDienThoai());
            stmt.setString(4, nv.getEmail());
            stmt.setString(5, nv.getCCCD());

            if (nv.getDiaChi() == null) {
                stmt.setNull(6, Types.NVARCHAR);
            } else {
                stmt.setString(6, nv.getDiaChi());
            }

            stmt.setBoolean(7, nv.isTrangThai());

            if (nv.getChucVu() == null) {
                stmt.setNull(8, Types.VARCHAR);
            } else {
                stmt.setString(8, nv.getChucVu().getMaChucVu());
            }

            if (nv.getHinhAnh() == null) {
                stmt.setNull(9, Types.VARCHAR);
            } else {
                stmt.setString(9, nv.getHinhAnh());
            }

            stmt.setString(10, nv.getMaNhanVien());
            n = stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // Xóa nhân viên
    public boolean xoaNhanVien(String maNhanVien) {
        int n = 0;
        try {
            con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement("DELETE FROM NhanVien WHERE MaNhanVien = ?");
            stmt.setString(1, maNhanVien);
            n = stmt.executeUpdate();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return n > 0;
    }

    // ── private helpers ──────────────────────────────────────────────────────────
    private NhanVien mapResultSetToNhanVien(ResultSet rs) throws SQLException {
        String maNhanVien  = rs.getString("MaNhanVien");
        String tenNhanVien = rs.getString("TenNhanVien");
        boolean gioiTinh   = rs.getBoolean("GioiTinh");
        String soDienThoai = rs.getString("SoDienThoai");
        String email       = rs.getString("email");
        String cccd        = rs.getString("CCCD");
        String diaChi      = rs.getString("DiaChi");
        boolean trangThai  = rs.getBoolean("TrangThai");
        String hinhAnh     = rs.getString("HinhAnh");

        ChucVu chucVu = null;
        String maChucVu = rs.getString("MaChucVu");
        if (maChucVu != null) {
            String tenChucVu = rs.getString("TenChucVu");
            String moTa      = rs.getString("MoTa");
            chucVu = new ChucVu(maChucVu, tenChucVu, moTa);
        }

        return new NhanVien(maNhanVien, tenNhanVien, gioiTinh, soDienThoai,
                            diaChi, email, cccd, chucVu, hinhAnh, trangThai);
    }
}