package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.NhaCungCap;

public class NhaCungCap_DAO {
	private Connection con = null;

	public ArrayList<NhaCungCap> getDSNhaCungCap() {
		ArrayList<NhaCungCap> dsNCC = new ArrayList<NhaCungCap>();
		String sql = "SELECT MaNhaCungCap, TenNhaCungCap, DiaChi, Email, SoDienThoai, MoTa, TrangThai FROM NhaCungCap ORDER BY MaNhaCungCap";
		try {
			con = ConnectDB.getInstance().getConnection();
			try (Statement statement = con.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
				while (rs.next()) {
					dsNCC.add(mapNhaCungCap(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dsNCC;
	}

	public NhaCungCap layNCCTheoMaNCC(String maNCC) {
		String sql = "SELECT MaNhaCungCap, TenNhaCungCap, DiaChi, Email, SoDienThoai, MoTa, TrangThai FROM NhaCungCap WHERE MaNhaCungCap = ?";
		try {
			con = ConnectDB.getInstance().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, maNCC);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						return mapNhaCungCap(rs);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean themNCC(NhaCungCap ncc) {
		String sql = "INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, DiaChi, Email, SoDienThoai, MoTa, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?)";
		try {
			con = ConnectDB.getInstance().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, ncc.getMaNhaCungCap());
				stmt.setString(2, ncc.getTenNhaCungCap());
				stmt.setString(3, ncc.getDiaChi());
				stmt.setString(4, ncc.getEmail());
				stmt.setString(5, ncc.getSoDienThoai());
				stmt.setString(6, ncc.getMoTa());
				stmt.setBoolean(7, ncc.isTrangThai());
				return stmt.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean updateNCC(NhaCungCap ncc) {
		String sql = "UPDATE NhaCungCap SET TenNhaCungCap = ?, DiaChi = ?, Email = ?, SoDienThoai = ?, MoTa = ?, TrangThai = ? WHERE MaNhaCungCap = ?";
		try {
			con = ConnectDB.getInstance().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, ncc.getTenNhaCungCap());
				stmt.setString(2, ncc.getDiaChi());
				stmt.setString(3, ncc.getEmail());
				stmt.setString(4, ncc.getSoDienThoai());
				stmt.setString(5, ncc.getMoTa());
				stmt.setBoolean(6, ncc.isTrangThai());
				stmt.setString(7, ncc.getMaNhaCungCap());
				return stmt.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean xoaNCC(String maNCC) {
		String sql = "DELETE FROM NhaCungCap WHERE MaNhaCungCap = ?";
		try {
			con = ConnectDB.getInstance().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, maNCC);
				return stmt.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private NhaCungCap mapNhaCungCap(ResultSet rs) throws SQLException {
		String maNCC = rs.getString("MaNhaCungCap");
		String tenNCC = rs.getString("TenNhaCungCap");
		String diaChi = rs.getString("DiaChi");
		String email = rs.getString("Email");
		String soDienThoai = rs.getString("SoDienThoai");
		String moTa = rs.getString("MoTa");
		boolean trangThai = rs.getBoolean("TrangThai");
		return new NhaCungCap(maNCC, tenNCC, diaChi, email, soDienThoai, moTa, trangThai);
	}
}
