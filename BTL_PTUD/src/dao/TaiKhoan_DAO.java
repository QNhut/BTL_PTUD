package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connectDB.ConnectDB;
import entity.NhanVien;
import entity.TaiKhoan;

public class TaiKhoan_DAO {
	private Connection con;

	public TaiKhoan getOnlineTaiKhoan() {
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "select * from TaiKhoan tk join NhanVien nv on tk.MaNhanVien = nv.MaNhanVien "+
					 "where tk.TrangThaiOnline = 1";
		try {
			con = ConnectDB.getInstance().getConnection();
			stmt = con.prepareStatement(sql);
			rs = stmt.executeQuery();
			
			if (rs.next()) {
//				Lấy thông tin nhân viên
				String maNV = rs.getString("MaNhanVien");
				String tenNV = rs.getString("TenNhanVien");
				String sDT = rs.getString("SoDienThoai");
				String cCCD = rs.getString("CCCD");
				String diaChi = rs.getString("DiaChi");
				String gioiTinh = rs.getString("GioiTinh");
				String chucVu = rs.getString("ChucVu");
				String caLam = rs.getString("CaLam");
				String trangThai = rs.getString("TrangThai");
				String hinhAnh = rs.getString("HinhAnh");
				NhanVien nv = new NhanVien(maNV, tenNV, sDT, cCCD, diaChi, gioiTinh, chucVu, caLam, trangThai, hinhAnh);
//				Lấy thông tin tài khoản
				String tenDangNhapNV = rs.getString("TenDangNhap");
				String matKhauNV = rs.getString("MatKhau");
				String vaiTro = rs.getString("VaiTro");
				boolean trangThaiOnline = rs.getInt("TrangThaiOnline") == 1;
				TaiKhoan tk = new TaiKhoan(tenDangNhapNV, matKhauNV, null, trangThaiOnline, nv);
				return tk;
			}
			
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return null;
	}
}
