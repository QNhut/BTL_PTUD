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

	// Sinh mã nhà cung cấp tự động: NCC + YYYY + 3 số (VD: NCC2026001)
	public String sinhMaTuDong() {
		String prefix = "NCC";
		int nam = java.time.LocalDate.now().getYear();
		String pattern = prefix + nam;
		String sql = "SELECT MAX(MaNhaCungCap) FROM NhaCungCap WHERE MaNhaCungCap LIKE ?";
		try {
			con = ConnectDB.getInstance().getConnection();
			try (PreparedStatement ps = con.prepareStatement(sql)) {
				ps.setString(1, pattern + "%");
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						String maxMa = rs.getString(1);
						if (maxMa != null && maxMa.length() > pattern.length()) {
							try {
								int stt = Integer.parseInt(maxMa.substring(pattern.length())) + 1;
								return pattern + String.format("%03d", stt);
							} catch (NumberFormatException ignored) {}
						}
					}
				}
			}
		} catch (SQLException e) { e.printStackTrace(); }
		return pattern + "001";
	}
}
