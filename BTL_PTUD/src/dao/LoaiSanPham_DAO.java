package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.LoaiSanPham;

public class LoaiSanPham_DAO {
	private Connection con;

	public ArrayList<LoaiSanPham> getDSLoaiSanPham() {
		ArrayList<LoaiSanPham> dsLoaiSP = new ArrayList<LoaiSanPham>();
		String sql = "SELECT MaLoaiSanPham, TenLoaiSanPham, MoTa FROM LoaiSanPham ORDER BY MaLoaiSanPham";
		try {
			con = ConnectDB.getInstance().getConnection();
			try (Statement statement = con.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
				while (rs.next()) {
					dsLoaiSP.add(mapLoaiSanPham(rs));
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dsLoaiSP;
	}

	public String layTenLoaiSP(String maLoaiSP) {
		LoaiSanPham lsp = layLoaiSPTheoMaLSP(maLoaiSP);
		return lsp == null ? "" : lsp.getTenLoaiSanPham();
	}

	public LoaiSanPham layLoaiSPTheoMaLSP(String maLoaiSP) {
		String sql = "SELECT MaLoaiSanPham, TenLoaiSanPham, MoTa FROM LoaiSanPham WHERE MaLoaiSanPham = ?";
		try {
			con = ConnectDB.getInstance().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, maLoaiSP);
				try (ResultSet rs = stmt.executeQuery()) {
					if (rs.next()) {
						return mapLoaiSanPham(rs);
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean themLoaiSanPham(LoaiSanPham lsp) {
		String sql = "INSERT INTO LoaiSanPham (MaLoaiSanPham, TenLoaiSanPham, MoTa) VALUES (?, ?, ?)";
		try {
			con = ConnectDB.getInstance().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, lsp.getMaLoaiSanPham());
				stmt.setString(2, lsp.getTenLoaiSanPham());
				stmt.setString(3, lsp.getMoTa());
				return stmt.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean capNhatLoaiSanPham(LoaiSanPham lsp) {
		String sql = "UPDATE LoaiSanPham SET TenLoaiSanPham = ?, MoTa = ? WHERE MaLoaiSanPham = ?";
		try {
			con = ConnectDB.getInstance().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, lsp.getTenLoaiSanPham());
				stmt.setString(2, lsp.getMoTa());
				stmt.setString(3, lsp.getMaLoaiSanPham());
				return stmt.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean xoaLoaiSanPham(String maLoaiSanPham) {
		String sql = "DELETE FROM LoaiSanPham WHERE MaLoaiSanPham = ?";
		try {
			con = ConnectDB.getInstance().getConnection();
			try (PreparedStatement stmt = con.prepareStatement(sql)) {
				stmt.setString(1, maLoaiSanPham);
				return stmt.executeUpdate() > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private LoaiSanPham mapLoaiSanPham(ResultSet rs) throws SQLException {
		String maLoai = rs.getString("MaLoaiSanPham");
		String tenLoai = rs.getString("TenLoaiSanPham");
		String moTa = rs.getString("MoTa");
		return new LoaiSanPham(maLoai, tenLoai, moTa);
	}
}
