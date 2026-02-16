package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.HoaDon;
import entity.KhachHang;
import entity.LoaiSanPham;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhap;
import entity.SanPham;

public class HoaDon_DAO {
	private Connection con;
	private ArrayList<HoaDon> dsHD;
	private NhanVien_DAO nv_DAO = new NhanVien_DAO();
	private KhachHang_DAO kh_DAO = new KhachHang_DAO();


	public HoaDon_DAO() {
		dsHD = new ArrayList<HoaDon>();
	}

//	Đọc dữ liệu từ bảng HoaDon
	public ArrayList<HoaDon> getDSHoaDon() {
		dsHD.clear();
		String sql = "Select * from HoaDon order by MaHD";
		try {
			con = ConnectDB.getInstance().getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				String maHD = rs.getString("MaHD");
				String maNV = rs.getString("MaNV");
				String maKH = rs.getString("MaKH");
				Date ngayLapSQL = rs.getDate("NgayLap");
				double tongTien = rs.getDouble("TongTien");
				int diemTichLuy = rs.getInt("DiemTichLuy");
				String PTTT = rs.getString("PTTT");

				NhanVien nv = nv_DAO.layNVTheoMa(maNV);
				KhachHang kh = kh_DAO.layKHTheoMaKH(maKH);
				
				dsHD.add(new HoaDon(maHD, ngayLapSQL.toLocalDate(), tongTien, diemTichLuy, PTTT, nv, kh));
			}
			rs.close();
			statement.close();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dsHD;
	}

//	Tạo hoá đơn
	public boolean taoHoaDon(HoaDon hd) {
		boolean kq = false;
		try {
			con = ConnectDB.getInstance().getConnection();
			String sql = "insert into HoaDon (MaHD, NgayLap, TongTien, PhuongThucThanhToan, MaNV, MaKH) "
					+ "values (?, ?, ?, ?, ?, ?)";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, hd.getMaHD());
			stmt.setDate(2, Date.valueOf(hd.getNgayLap()));
			stmt.setDouble(3, hd.getTongTien());
			stmt.setString(4, hd.getpTTT());
			stmt.setString(5, hd.getNhanVien().getMaNhanVien());
			stmt.setString(6, hd.getKhachHang().getMaKH());

			int n = stmt.executeUpdate();
			if (n > 0) {
				kq = true;
			}
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return kq;
	}

	public HoaDon layHDTheoMa(String maHD) {
		HoaDon hd = null;
		try {
			con = ConnectDB.getInstance().getConnection();
			String sql = "select * from HoaDon where MaHD = ?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maHD);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {

				String maNV = rs.getString("MaNV");
				String maKH = rs.getString("MaKH");
				Date ngayLapSQL = rs.getDate("NgayLap");
				double tongTien = rs.getDouble("TongTien");
				int diemTichLuy = rs.getInt("DiemTichLuy");
				String PTTT = rs.getString("PTTT");

				NhanVien nv = nv_DAO.layNVTheoMa(maNV);
				KhachHang kh = kh_DAO.layKHTheoMaKH(maKH);
				
				hd = new HoaDon(maHD, ngayLapSQL.toLocalDate(), tongTien, diemTichLuy, PTTT, nv, kh);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return hd;
	}

//	Xoá hoá đơn theo mã
	public boolean xoaHoaDon(String maHD) {
		boolean ketQua = false;
		try {
			Connection con = ConnectDB.getInstance().getConnection();

			String sqlCT = "delete from ChiTietHoaDon where MaHD = ?";
			PreparedStatement psCT = con.prepareStatement(sqlCT);
			psCT.setString(1, maHD);
			psCT.executeUpdate();
			psCT.close();

			String sqlHD = "delete from HoaDon where MaHD = ?";
			PreparedStatement psHD = con.prepareStatement(sqlHD);
			psHD.setString(1, maHD);
			int row = psHD.executeUpdate();
			psHD.close();
			ketQua = (row > 0);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return ketQua;
	}
}
