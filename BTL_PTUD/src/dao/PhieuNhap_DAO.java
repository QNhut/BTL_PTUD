package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;

import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhap;

public class PhieuNhap_DAO {
	private Connection con;
	private ArrayList<PhieuNhap> dsPhieuNhap;

	public PhieuNhap_DAO() {
		dsPhieuNhap = new ArrayList<PhieuNhap>();
	}

	public ArrayList<PhieuNhap> getDSPhieuNhap() {
		dsPhieuNhap.clear();
		Statement statement = null;
		String sql = "select * from PhieuNhap";
		try {
			con = connectDB.ConnectDB.getInstance().getConnection();
			statement = con.createStatement();
			ResultSet rs = statement.executeQuery(sql);

			while (rs.next()) {
				String maPN = rs.getString("MaPhieuNhap");
				LocalDate ngayNhap = rs.getDate("NgayNhap").toLocalDate();
				String maNCC = rs.getString("MaNCC");
				NhaCungCap_DAO ncc_DAO = new NhaCungCap_DAO();
				NhaCungCap nhaCungCap = ncc_DAO.layNCCTheoMaNCC(maNCC);
				String maNV = rs.getString("MaNhanVien");
				NhanVien_DAO nv_DAO = new NhanVien_DAO();
				NhanVien nhanVien = nv_DAO.layNVTheoMa(maNV);
				String ghiChu = rs.getString("GhiChu");
				ghiChu = (ghiChu == null) ? "" : ghiChu;

				dsPhieuNhap.add(new PhieuNhap(maPN, nhaCungCap, nhanVien, ghiChu));
			}
			rs.close();
			statement.close();

		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return dsPhieuNhap;
	}
	
	public PhieuNhap layPNTheoMa(String maPN) {
		PhieuNhap pn = null;
		try {
			con = connectDB.ConnectDB.getInstance().getConnection();
			String sql = "select * from PhieuNhap where MaPhieuNhap = ?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maPN);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				LocalDate ngayNhap = rs.getDate("NgayNhap").toLocalDate();
				String maNCC = rs.getString("MaNCC");
				NhaCungCap_DAO ncc_DAO = new NhaCungCap_DAO();
				NhaCungCap nhaCungCap = ncc_DAO.layNCCTheoMaNCC(maNCC);
				String maNV = rs.getString("MaNhanVien");
				NhanVien_DAO nv_DAO = new NhanVien_DAO();
				NhanVien nhanVien = nv_DAO.layNVTheoMa(maNV);
				String ghiChu = rs.getString("GhiChu");
				ghiChu = (ghiChu == null) ? "" : ghiChu;

				pn = new PhieuNhap(maPN, nhaCungCap, nhanVien, ghiChu);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return pn;
	}

//	Tạo phiếu nhập
	public boolean taoPhieuNhap(PhieuNhap pn) {
		PreparedStatement stmt = null;
		int n = 0;
		String sql = "insert into PhieuNhap values(?,?,?,?,?)";
		try {
			con = connectDB.ConnectDB.getInstance().getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, pn.getMaPhieuNhap());
			stmt.setString(3, pn.getnCC().getMaNCC());
			stmt.setString(4, pn.getNhanVien().getMaNhanVien());

			if (pn.getGhiChu() == null || pn.getGhiChu().isEmpty()) {
				stmt.setNull(5, Types.NVARCHAR);
			} else {
				stmt.setString(5, pn.getGhiChu());
			}
			n = stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n > 0;
	}

//	Cập nhật phiếu nhập
	public boolean capNhatPhieuNhap(PhieuNhap pn) {
		PreparedStatement stmt = null;
		int n = 0;
		String sql = "update PhieuNhap set NgayNhap = ?, MaNCC = ?, MaNhanVien = ?, GhiChu = ? where MaPhieuNhap = ?";
		try {
			con = connectDB.ConnectDB.getInstance().getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(2, pn.getnCC().getMaNCC());
			stmt.setString(3, pn.getNhanVien().getMaNhanVien());

			if (pn.getGhiChu() == null || pn.getGhiChu().isEmpty()) {
				stmt.setNull(4, Types.NVARCHAR);
			} else {
				stmt.setString(4, pn.getGhiChu());
			}
			stmt.setString(5, pn.getMaPhieuNhap());
			n = stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n > 0;
	}
	
	public boolean xoaPhieuNhap(String maPN) {
		PreparedStatement stmt = null;
		int n = 0;
		String sql = "delete from PhieuNhap where MaPhieuNhap = ?";
		try {
			con = connectDB.ConnectDB.getInstance().getConnection();
			stmt = con.prepareStatement(sql);
			stmt.setString(1, maPN);
			n = stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n > 0;
	}

}
