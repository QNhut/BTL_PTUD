package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.LoaiSanPham;

public class LoaiSanPham_DAO {
	private Connection con;
	private ArrayList<LoaiSanPham> dsLoaiSP;

	public LoaiSanPham_DAO() {
		dsLoaiSP = new ArrayList<LoaiSanPham>();
	}

//  Đọc dữ liệu từ bản LoaiSanPham
	public ArrayList<LoaiSanPham> getDSLoaiSP() {
		dsLoaiSP.clear();
		String sql = "Select * from LoaiSanPham";
		try {
			con = ConnectDB.getInstance().getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				String maLoaiSP = rs.getString("MaLoaiSP");
				String tenLoaiSP = rs.getString("TenLoaiSP");
				String moTa = rs.getString("MoTa");
				moTa = (moTa == null) ? "" : moTa;
				dsLoaiSP.add(new LoaiSanPham(maLoaiSP, tenLoaiSP, moTa));
			}

		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return dsLoaiSP;
	}

//	Lấy tên loại sản phẩm dựa trên mã
	public String layTenLoaiSP(String maLoaiSP) {
		String tenLoaiSP = "";
		String sql = "select TenLoaiSP from LoaiSanPham where MaLoaiSP = ?";
		try {
			con = ConnectDB.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maLoaiSP);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				tenLoaiSP = rs.getString("TenLoaiSP");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return tenLoaiSP;
	}
	
	public LoaiSanPham layLoaiSPTheoMaLSP(String maLoaiSP) {
		String sql = "select * from LoaiSanPham where MaLoaiSP = ?";
		LoaiSanPham lsp = null;
		try {
			con = ConnectDB.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maLoaiSP);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String tenLoaiSP = rs.getString("TenLoaiSP");
				String moTa = rs.getString("MoTa");
				moTa = (moTa == null) ? "" : moTa;
				
				lsp = new LoaiSanPham(maLoaiSP, tenLoaiSP, moTa);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return lsp;
	}
}
