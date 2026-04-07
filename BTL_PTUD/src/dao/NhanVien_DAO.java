package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.NhanVien;
public class NhanVien_DAO {
	private Connection con;
	private ArrayList<NhanVien> dsNV;

	public NhanVien_DAO() {
		dsNV = new ArrayList<NhanVien>();
	}

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
				String gioiTinhText = rs.getString("GioiTinh");
				String chucVu = rs.getString("ChucVu");
				String caLam = rs.getString("CaLam");
				String hinhAnh = rs.getString("HinhAnh");
				boolean gioiTinh = parseGioiTinh(gioiTinhText);

				nv = new NhanVien(maNV, tenNV, gioiTinh, sDT, diaChi, null, cCCD, chucVu, 0.0, caLam, hinhAnh);
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
				String gioiTinhText = rs.getString("GioiTinh");
				String chucVu = rs.getString("ChucVu");
				String caLam = rs.getString("CaLam");
				String hinhAnh = rs.getString("HinhAnh");
				boolean gioiTinh = parseGioiTinh(gioiTinhText);

				dsNV.add(new NhanVien(maNV, tenNV, gioiTinh, sDT, diaChi, null, cCCD, chucVu, 0.0, caLam, hinhAnh));
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
			stmt.setString(1, nv.getMaNV());
			stmt.setString(2, nv.getTenNV());
			stmt.setString(3, nv.isGioiTinh() ? "Nam" : "Nu");
			stmt.setString(4, nv.getSoDienThoai());
			stmt.setString(5, nv.getcCCD());

			if (nv.getDiaChi() == null || nv.getDiaChi().isEmpty()) {
				stmt.setNull(6, Types.NVARCHAR);
			} else {
				stmt.setString(6, nv.getDiaChi());
			}

			stmt.setString(7, nv.getVaiTro());
			stmt.setString(8, nv.getCaLam());
			stmt.setString(9, "Dang lam");

			if (nv.getLinkAnh() == null || nv.getLinkAnh().isEmpty()) {
				stmt.setNull(10, Types.NVARCHAR);
			} else {
				stmt.setString(10, nv.getLinkAnh());
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
			stmt.setString(1, nv.getTenNV());
			stmt.setString(2, nv.isGioiTinh() ? "Nam" : "Nu");
			stmt.setString(3, nv.getSoDienThoai());
			stmt.setString(4, nv.getcCCD());

			if (nv.getDiaChi() == null || nv.getDiaChi().isEmpty()) {
				stmt.setNull(5, Types.NVARCHAR);
			} else {
				stmt.setString(5, nv.getDiaChi());
			}

			stmt.setString(6, nv.getVaiTro());
			stmt.setString(7, nv.getCaLam());
			stmt.setString(8, "Dang lam");

			if (nv.getLinkAnh() == null || nv.getLinkAnh().isEmpty()) {
				stmt.setNull(9, Types.NVARCHAR);
			} else {
				stmt.setString(9, nv.getLinkAnh());
			}

			stmt.setString(10, nv.getMaNV());
			n = stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n > 0;
	}

	private boolean parseGioiTinh(String gioiTinhText) {
		if (gioiTinhText == null) {
			return true;
		}
		return gioiTinhText.trim().equalsIgnoreCase("Nam");
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
