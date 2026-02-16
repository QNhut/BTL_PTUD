package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.LoaiSanPham;
import entity.NhaCungCap;
import entity.SanPham;

public class SanPham_DAO {
	private Connection con;
	private ArrayList<SanPham> dsSanPham;
	private NhaCungCap_DAO ncc_DAO = new NhaCungCap_DAO();
	private LoaiSanPham_DAO loaiSP_DAO = new LoaiSanPham_DAO();

	public SanPham_DAO() {
		dsSanPham = new ArrayList<SanPham>();
	}

//	Đọc dữ liệu từ bảng SanPham
	public ArrayList<SanPham> getDSSanPham() {
		// TODO Auto-generated method stub
		dsSanPham.clear();
		Statement statement = null;
		String sql = "Select * from SanPham";
		try {
			con = ConnectDB.getInstance().getConnection();
			statement = con.createStatement();
			ResultSet rs = statement.executeQuery(sql);

			while (rs.next()) {
				String maSP = rs.getString("MaSP");
				String tenSP = rs.getString("TenSP");
				String maNhaCC = rs.getString("MaNCC");
				String maLoaiSP = rs.getString("MaLoaiSP");
				double giaBan = rs.getDouble("GiaBan");
				int soLuongTon = rs.getInt("SoLuong");
				String donViTinh = rs.getString("DonViTinh");
				String hanSuDung = rs.getString("HanSuDung");
				String hinhAnh = rs.getString("HinhAnh");
				soLuongTon = (soLuongTon == 0) ? 0 : soLuongTon;
				hanSuDung = (hanSuDung == null) ? "" : hanSuDung;
				hinhAnh = (hinhAnh == null) ? "" : hinhAnh;
				
				NhaCungCap ncc = ncc_DAO.layNCCTheoMaNCC(maNhaCC);
				LoaiSanPham lsp = loaiSP_DAO.layLoaiSPTheoMaLSP(maLoaiSP);
				
				dsSanPham.add(new SanPham(maSP, tenSP, ncc,lsp, giaBan, soLuongTon, donViTinh, hanSuDung, hinhAnh));
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return dsSanPham;
	}
	
	public SanPham laySanPhamTheoMa(String maSP) {
		SanPham sp = null;
		String sql = "select * from SanPham where MaSP = ?";
		try {
			con = ConnectDB.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maSP);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String tenSP = rs.getString("TenSP");
				String maNhaCC = rs.getString("MaNCC");
				String maLoaiSP = rs.getString("MaLoaiSP");
				NhaCungCap_DAO ncc_DAO = new NhaCungCap_DAO();
				LoaiSanPham_DAO loaiSP_DAO = new LoaiSanPham_DAO();
				double giaBan = rs.getDouble("GiaBan");
				int soLuongTon = rs.getInt("SoLuong");
				String donViTinh = rs.getString("DonViTinh");
				String hanSuDung = rs.getString("HanSuDung");
				String hinhAnh = rs.getString("HinhAnh");
				soLuongTon = (soLuongTon == 0) ? 0 : soLuongTon;
				hanSuDung = (hanSuDung == null) ? "" : hanSuDung;
				hinhAnh = (hinhAnh == null) ? "" : hinhAnh;
				
				NhaCungCap ncc = ncc_DAO.layNCCTheoMaNCC(maNhaCC);
				LoaiSanPham lsp = loaiSP_DAO.layLoaiSPTheoMaLSP(maLoaiSP);

				sp = new SanPham(maSP, tenSP, ncc, lsp, giaBan, soLuongTon, donViTinh, hanSuDung, hinhAnh);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sp;
	}

//	Thêm sản phẩm 
	public boolean themSanPham(SanPham sp) {
		// TODO Auto-generated method stub
		int n = 0;
		String sql = "insert into SanPham values(?,?,?,?,?,?,?,?,?)";
		con = ConnectDB.getInstance().getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, sp.getMaSP());
			stmt.setString(2, sp.getTenSP());
			stmt.setString(3, sp.getnCC().getMaNCC());
			stmt.setString(4, sp.getLoaiSP().getMaLoaiSP());
			stmt.setDouble(5, sp.getGiaBan());
			stmt.setInt(6, sp.getSoLuong());
			stmt.setString(7, sp.getDonViTinh());

			if (sp.getHanSuDung() == null || sp.getHanSuDung().isEmpty()) {
				stmt.setNull(8, Types.NVARCHAR);
			} else {
				stmt.setString(8, sp.getHanSuDung());
			}

			if (sp.getHinhAnh() == null || sp.getHinhAnh().isEmpty()) {
				stmt.setNull(9, Types.NVARCHAR);
			} else {
				stmt.setString(9, sp.getHinhAnh());
			}

			n = stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return n > 0;
	}

//	Cập nhật sản phẩm
	public boolean updateSanPham(SanPham sp) {
		// TODO Auto-generated method stub
		int n = 0;
		String sql = "update SanPham set TenSP = ?, MaNCC = ?, MaLoaiSP = ?, GiaBan = ?, SoLuong = ?, DonViTinh = ?, HanSuDung = ?, HinhAnh = ? where MaSP = ?";
		con = ConnectDB.getInstance().getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, sp.getTenSP());
			stmt.setString(2, sp.getnCC().getMaNCC());
			stmt.setString(3, sp.getLoaiSP().getMaLoaiSP());
			stmt.setDouble(4, sp.getGiaBan());
			stmt.setInt(5, sp.getSoLuong());
			stmt.setString(6, sp.getDonViTinh());
			if (sp.getHanSuDung() == null || sp.getHanSuDung().isEmpty()) {
				stmt.setNull(7, Types.NVARCHAR);
			} else {
				stmt.setString(7, sp.getHanSuDung());
			}
			if (sp.getHinhAnh() == null || sp.getHinhAnh().isEmpty()) {
				stmt.setNull(8, Types.NVARCHAR);
			} else {
				stmt.setString(8, sp.getHinhAnh());
			}
			stmt.setString(9, sp.getMaSP());
			n = stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n > 0;
	}

//	Xóa sản phẩm
	public boolean xoaSanPham(String maSP) {
		// TODO Auto-generated method stub
		int n = 0;
		String sql = "delete from SanPham where MaSP = ?";
		con = ConnectDB.getInstance().getConnection();
		PreparedStatement stmt = null;
		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, maSP);
			n = stmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n > 0;
	}
}
