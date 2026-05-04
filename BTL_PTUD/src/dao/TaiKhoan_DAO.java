package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;

import ConnectDB.ConnectDB;
import entity.ChucVu;
import entity.NhanVien;
import entity.TaiKhoan;

public class TaiKhoan_DAO {

    private static final String SQL_SELECT_TAI_KHOAN = "SELECT "
            + "tk.TenTaiKhoan, tk.MatKhau, tk.NgayTao, tk.TrangThai AS TrangThaiTaiKhoan, "
            + "nv.MaNhanVien, nv.TenNhanVien, nv.GioiTinh, nv.SoDienThoai, nv.Email, nv.CCCD, nv.DiaChi, "
            + "nv.TrangThai AS TrangThaiNhanVien, nv.HinhAnh, "
            + "cv.MaChucVu AS CV_MaChucVu, cv.TenChucVu, cv.MoTa AS CV_MoTa "
            + "FROM TaiKhoan tk "
            + "JOIN NhanVien nv ON tk.MaNhanVien = nv.MaNhanVien "
            + "LEFT JOIN ChucVu cv ON nv.MaChucVu = cv.MaChucVu ";

    // Xác thực đăng nhập và trả về thông tin tài khoản
    public TaiKhoan login(String tenDangNhap, String matKhau) {
        String sql = SQL_SELECT_TAI_KHOAN + "WHERE tk.TenTaiKhoan = ? AND tk.MatKhau = ?";
        try {
            Connection con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, tenDangNhap);
                stmt.setString(2, matKhau);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapTaiKhoan(rs);
                    }
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy tài khoản theo tên đăng nhập
    public TaiKhoan layTaiKhoanTheoTenDangNhap(String tenDangNhap) {
        String sql = SQL_SELECT_TAI_KHOAN + "WHERE tk.TenTaiKhoan = ?";
        try {
            Connection con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, tenDangNhap);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapTaiKhoan(rs);
                    }
                }
            }
        } catch (SQLException | IllegalArgumentException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Cập nhật trạng thái online
    public boolean capNhatTrangThai(String tenDangNhap, boolean trangThai) {
        String sql = "UPDATE TaiKhoan SET TrangThai = ? WHERE TenTaiKhoan = ?";
        try {
            Connection con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setBoolean(1, trangThai);
                stmt.setString(2, tenDangNhap);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Đổi mật khẩu
    public boolean doiMatKhau(String tenDangNhap, String matKhauMoi) {
        String sql = "UPDATE TaiKhoan SET MatKhau = ? WHERE TenTaiKhoan = ?";
        try {
            Connection con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, matKhauMoi);
                stmt.setString(2, tenDangNhap);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private TaiKhoan mapTaiKhoan(ResultSet rs) throws SQLException {
        String tenDangNhap = rs.getString("TenTaiKhoan");
        String matKhau     = rs.getString("MatKhau");
        boolean trangThai  = rs.getBoolean("TrangThaiTaiKhoan");

        LocalDate ngayTao = null;
        Timestamp ts = rs.getTimestamp("NgayTao");
        if (ts != null) {
            ngayTao = ts.toLocalDateTime().toLocalDate();
        }

        NhanVien nv = mapNhanVien(rs);
        return new TaiKhoan(tenDangNhap, matKhau, ngayTao, trangThai, nv);
    }

    private NhanVien mapNhanVien(ResultSet rs) throws SQLException {
        String maNhanVien  = rs.getString("MaNhanVien");
        String tenNhanVien = rs.getString("TenNhanVien");
        boolean gioiTinh   = rs.getBoolean("GioiTinh");
        String soDienThoai = rs.getString("SoDienThoai");
        String email       = rs.getString("Email");
        String cccd        = rs.getString("CCCD");
        String diaChi      = rs.getString("DiaChi");
        boolean trangThaiNV = rs.getBoolean("TrangThaiNhanVien");
        String hinhAnh     = rs.getString("HinhAnh");

        ChucVu chucVu = null;
        String maChucVu = rs.getString("CV_MaChucVu");
        if (maChucVu != null) {
            String tenChucVu = rs.getString("TenChucVu");
            String moTa      = rs.getString("CV_MoTa");
            chucVu = new ChucVu(maChucVu, tenChucVu, moTa);
        }

        return new NhanVien(maNhanVien, tenNhanVien, gioiTinh, soDienThoai,
                            diaChi, email, cccd, chucVu, hinhAnh, trangThaiNV);
    }
}