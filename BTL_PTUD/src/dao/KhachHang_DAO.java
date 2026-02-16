package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.KhachHang;

public class KhachHang_DAO {
	private Connection con = null;
	private ArrayList<KhachHang> dsKH;

	public KhachHang_DAO() {
		dsKH = new ArrayList<KhachHang>();
	}

//	Đọc từ bảng KhachHang
	public ArrayList<KhachHang> getDSKhachHang() {
		dsKH.clear();
		String sql = "select * from KhachHang order by MaKH";
		try {
			con = ConnectDB.getInstance().getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				String maKH = rs.getString("MaKH");
				if(maKH.equals("KH000")) {
					continue; 
				}
				String tenKH = rs.getString("TenKH");
				int namSinh = rs.getInt("NamSinh");
				String sdt = rs.getString("SoDienThoai");
				String gioiTinh = rs.getString("GioiTinh");
				String diaChi = rs.getString("DiaChi");
				diaChi = (diaChi == null) ? "" : diaChi;
				int diemTichLuy = rs.getInt("DiemTichLuy");
				dsKH.add(new KhachHang(maKH, tenKH, namSinh, sdt, gioiTinh, diaChi, diemTichLuy));
			}
			rs.close();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dsKH;
	}

//	Lấy khách hàng từ số điện thoại
	public KhachHang layKHTheoSDT(String sdt) {
		KhachHang kh = null;
		try {
			con = ConnectDB.getInstance().getConnection();
			String sql = "select * from KhachHang where SoDienThoai = ?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, sdt);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String maKH = rs.getString("MaKH");
				String tenKH = rs.getString("TenKH");
				int namSinh = rs.getInt("NamSinh");
				String gioiTinh = rs.getString("GioiTinh");
				String diaChi = rs.getString("DiaChi");
				diaChi = (diaChi == null) ? "" : diaChi;
				int diemTichLuy = rs.getInt("DiemTichLuy");

				kh = new KhachHang(maKH, tenKH, namSinh, sdt, gioiTinh, diaChi, diemTichLuy);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return kh;
	}

//	Lấy khách hàng từ mã KH
	public KhachHang layKHTheoMaKH(String maKH) {
		KhachHang kh = null;
		try {
			con = ConnectDB.getInstance().getConnection();
			String sql = "select * from KhachHang where MaKH = ?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maKH);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String tenKH = rs.getString("TenKH");
				int namSinh = rs.getInt("NamSinh");
				String sdt = rs.getString("SoDienThoai");
				String gioiTinh = rs.getString("GioiTinh");
				String diaChi = rs.getString("DiaChi");
				diaChi = (diaChi == null) ? "" : diaChi;
				int diemTichLuy = rs.getInt("DiemTichLuy");

				kh = new KhachHang(maKH, tenKH, namSinh, sdt, gioiTinh, diaChi, diemTichLuy);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return kh;
	}
	
	public boolean themKhachHang(KhachHang kh) {
		con = ConnectDB.getInstance().getConnection();
		PreparedStatement stmt = null;
		int n = 0;
		String sql = "insert into KhachHang(MaKH, TenKH, NamSinh, Sdt, GioiTinh, DiaChi) values(?,?,?,?,?,?)";
		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, kh.getMaKH());
			stmt.setString(2, kh.getTenKH());
			if (kh.getNamSinh() != 0) {
				stmt.setInt(3, kh.getNamSinh());
			} else {
				stmt.setNull(3, Types.INTEGER);
			}
			stmt.setString(4, kh.getSoDienThoai());
			stmt.setString(5, kh.getGioiTinh());
			stmt.setString(6, kh.getDiaChi());
			n = stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n > 0;
	}

	public boolean updateKhachHang(KhachHang kh) {
		String sql = "UPDATE KhachHang SET TenKH = ?, NamSinh = ?, Sdt = ?, GioiTinh = ?, DiaChi = ?, DiemTichLuy = ? WHERE MaKH = ?";
		int n = 0;
		Connection con = ConnectDB.getInstance().getConnection();

		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, kh.getTenKH());

			if (kh.getNamSinh() == 0) {
				stmt.setNull(2, Types.INTEGER);
			} else {
				stmt.setInt(2, kh.getNamSinh());
			}

			stmt.setString(3, kh.getSoDienThoai());
			stmt.setString(4, kh.getGioiTinh());

			if (kh.getDiaChi() == null || kh.getDiaChi().isEmpty()) {
				stmt.setNull(5, Types.NVARCHAR);
			} else {
				stmt.setString(5, kh.getDiaChi());
			}

			stmt.setInt(6, kh.getDiemTichLuy());
			stmt.setString(7, kh.getMaKH());

			n = stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n > 0;
	}

	// Xóa khách hàng
	public boolean xoaKhachHang(String maKH) {
		String sql = "DELETE FROM KhachHang WHERE MaKH = ?";
		int n = 0;
		Connection con = ConnectDB.getInstance().getConnection();

		try (PreparedStatement stmt = con.prepareStatement(sql)) {

			stmt.setString(1, maKH);
			n = stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n > 0;
	}

}
