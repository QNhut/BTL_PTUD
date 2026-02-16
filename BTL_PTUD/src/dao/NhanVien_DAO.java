package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.NhanVien;
public class NhanVien_DAO {
	private Connection con;
	private ArrayList<NhanVien> dsNV;

	public NhanVien_DAO() {
		dsNV = new ArrayList<NhanVien>();
	}

//	Lấy nhân viên từ mã 
	public NhanVien layNVTheoMa(String maNV) {
		NhanVien nv = null;
		try {
			con = ConnectDB.getInstance().getConnection();
			String sql = "select * from NhanVien where MaNhanVien = ?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maNV);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String tenNV = rs.getString("TenNhanVien");
				String sDT = rs.getString("SoDienThoai");
				String cCCD = rs.getString("CCCD");
				String diaChi = rs.getString("DiaChi");
				String gioiTinh = rs.getString("GioiTinh");
				String chucVu = rs.getString("ChucVu");
				String caLam = rs.getString("CaLam");
				String trangThai = rs.getString("TrangThai");
				String hinhAnh = rs.getString("HinhAnh");

				diaChi = (diaChi == null) ? "" : diaChi;
				hinhAnh = (hinhAnh == null) ? "" : hinhAnh;

				nv = new NhanVien(maNV, tenNV, sDT, cCCD, diaChi, gioiTinh, chucVu, caLam, trangThai, hinhAnh);
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
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("select * from NhanVien ORDER BY MaNhanVien");
			while (rs.next()) {
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

				diaChi = (diaChi == null) ? "" : diaChi;
				hinhAnh = (hinhAnh == null) ? "" : hinhAnh;

				dsNV.add(new NhanVien(maNV, tenNV, sDT, cCCD, diaChi, gioiTinh, chucVu, caLam, trangThai, hinhAnh));
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
			String sql = "insert into NhanVien (MaNhanVien, TenNhanVien, GioiTinh, SoDienThoai, CCCD, DiaChi, ChucVu, CaLam, TrangThai, HinhAnh) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, nv.getMaNhanVien());
			stmt.setString(2, nv.getTenNhanVien());
			stmt.setString(3, nv.getGioiTinh());
			stmt.setString(4, nv.getSoDienThoai());
			stmt.setString(5, nv.getcCCD());

			if (nv.getDiaChi() == null || nv.getDiaChi().isEmpty()) {
				stmt.setNull(6, Types.NVARCHAR);
			} else {
				stmt.setString(6, nv.getDiaChi());
			}

			stmt.setString(7, nv.getChucVu());
			stmt.setString(8, nv.getCaLam());
			stmt.setString(9, nv.getTrangThai());

			if (nv.getHinhAnh() == null || nv.getHinhAnh().isEmpty()) {
				stmt.setNull(10, Types.NVARCHAR);
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
			String sql = "UPDATE NhanVien SET TenNhanVien=?, GioiTinh=?, SoDienThoai=?, CCCD=?, DiaChi=?, ChucVu=?, CaLam=?, TrangThai=?, HinhAnh=? WHERE MaNhanVien=?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, nv.getTenNhanVien());
			stmt.setString(2, nv.getGioiTinh());
			stmt.setString(3, nv.getSoDienThoai());
			stmt.setString(4, nv.getcCCD());

			if (nv.getDiaChi() == null || nv.getDiaChi().isEmpty()) {
				stmt.setNull(5, Types.NVARCHAR);
			} else {
				stmt.setString(5, nv.getDiaChi());
			}

			stmt.setString(6, nv.getChucVu());
			stmt.setString(7, nv.getCaLam());
			stmt.setString(8, nv.getTrangThai());

			if (nv.getHinhAnh() == null || nv.getHinhAnh().isEmpty()) {
				stmt.setNull(9, Types.NVARCHAR);
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
	public boolean xoaNhanVien(String maNV) {
		int n = 0;
		try {
			con = ConnectDB.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("DELETE FROM NhanVien WHERE MaNhanVien = ?");
			stmt.setString(1, maNV);
			n = stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n > 0;
	}
}	
