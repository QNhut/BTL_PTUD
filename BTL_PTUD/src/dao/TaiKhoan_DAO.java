package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import ConnectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

public class TaiKhoan_DAO {
	// Xac thuc dang nhap va tra ve thong tin tai khoan
	public TaiKhoan login(String tenDangNhap, String matKhau) {
		String sql = "select * from TaiKhoan tk join NhanVien nv on tk.MaNhanVien = nv.MaNhanVien "
				+ "where tk.TenDangNhap = ? and tk.MatKhau = ?";
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
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	public TaiKhoan layTaiKhoanTheoTenDangNhap(String tenDangNhap) {
		String sql = "select * from TaiKhoan tk join NhanVien nv on tk.MaNhanVien = nv.MaNhanVien "
				+ "where tk.TenDangNhap = ?";
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
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean capNhatTrangThaiOnline(String tenDangNhap, boolean online) {
		String sql = "update TaiKhoan set TrangThaiOnline = ? where TenDangNhap = ?";
		try {
			Connection con = ConnectDB.getInstance().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setBoolean(1, online);
				stmt.setString(2, tenDangNhap);
				return stmt.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean doiMatKhau(String tenDangNhap, String matKhauMoi) {
		String sql = "update TaiKhoan set MatKhau = ? where TenDangNhap = ?";
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
		NhanVien nv = mapNhanVien(rs);
		String tenDangNhapNV = rs.getString("TenDangNhap");
		String matKhauNV = rs.getString("MatKhau");
		boolean trangThaiOnline = rs.getBoolean("TrangThaiOnline");
		return new TaiKhoan(tenDangNhapNV, matKhauNV, trangThaiOnline, nv);
	}

	private NhanVien mapNhanVien(ResultSet rs) throws SQLException {
		String maNV = rs.getString("MaNhanVien");
		String tenNV = rs.getString("TenNhanVien");
		String sDT = rs.getString("SoDienThoai");
		String cCCD = rs.getString("CCCD");
		String diaChi = rs.getString("DiaChi");
		String gioiTinhText = rs.getString("GioiTinh");
		String chucVu = rs.getString("ChucVu");
		String caLam = rs.getString("CaLam");
		String hinhAnh = rs.getString("HinhAnh");
		boolean gioiTinh = parseGioiTinh(gioiTinhText);
		return new NhanVien(maNV, tenNV, gioiTinh, sDT, diaChi, null, cCCD, chucVu, 0.0, caLam, hinhAnh);
	}

	private boolean parseGioiTinh(String gioiTinhText) {
		if (gioiTinhText == null) {
			return true;
		}
		return gioiTinhText.trim().equalsIgnoreCase("Nam");
	}
}
