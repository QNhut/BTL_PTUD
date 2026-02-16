package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.NhaCungCap;
import entity.NhanVien;

public class NhaCungCap_DAO {
	private Connection con = null;
	private ArrayList<NhaCungCap> dsNCC;

	public NhaCungCap_DAO() {
		dsNCC = new ArrayList<NhaCungCap>();
	}

	public ArrayList<NhaCungCap> getDSNhaCungCap() {
		dsNCC.clear();
		String sql = "select * from NhaCungCap order by MaNCC";
		try {
			con = ConnectDB.getInstance().getConnection();
			Statement statement = con.createStatement();
			ResultSet rs = statement.executeQuery(sql);
			while (rs.next()) {
				String maNCC = rs.getString("MaNCC");
				String tenNCC = rs.getString("TenNCC");
				String diaChi = rs.getString("DiaChi");
				String sDT = rs.getString("SoDienThoai");
				String email = rs.getString("Email");
				diaChi = (diaChi == null) ? "" : diaChi;
				sDT = (sDT == null) ? "" : sDT;
				email = (email == null) ? "" : email;
				dsNCC.add(new NhaCungCap(maNCC, tenNCC, diaChi, sDT, email));
			}

		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return dsNCC;
	}
	
	public NhaCungCap layNCCTheoMaNCC(String maNCC) {
		NhaCungCap ncc = null;
		try {
			con = ConnectDB.getInstance().getConnection();
			String sql = "select * from NhaCungCap where MaNCC = ?";
			PreparedStatement stmt = con.prepareStatement(sql);
			stmt.setString(1, maNCC);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				String tenNCC = rs.getString("TenNCC");
				String diaChi = rs.getString("DiaChi");
				String sDT = rs.getString("SoDienThoai");
				String email = rs.getString("Email");
				diaChi = (diaChi == null) ? "" : diaChi;
				sDT = (sDT == null) ? "" : sDT;
				email = (email == null) ? "" : email;
				dsNCC.add(new NhaCungCap(maNCC, tenNCC, diaChi, sDT, email));

				ncc = new NhaCungCap(maNCC, tenNCC, diaChi, sDT, email);
				}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ncc;
	}

//	Thêm nhà cung cấp
	public boolean themNCC(NhaCungCap ncc) {
		con = ConnectDB.getInstance().getConnection();
		PreparedStatement stmt = null;
		int n = 0;
		String sql = "insert into NhaCungCap values(?,?,?,?,?)";
		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, ncc.getMaNCC());
			stmt.setString(2, ncc.getTenNCC());
			if (ncc.getDiaChi() == null || ncc.getDiaChi().isEmpty()) {
				stmt.setNull(3, Types.NVARCHAR);
			} else {
				stmt.setString(3, ncc.getDiaChi());
			}
			if (ncc.getSoDienThoai() == null || ncc.getSoDienThoai().isEmpty()) {
				stmt.setNull(4, Types.NVARCHAR);
			} else {
				stmt.setString(4, ncc.getSoDienThoai());
			}

			if (ncc.getEmail() == null || ncc.getEmail().isEmpty()) {
				stmt.setNull(5, Types.NVARCHAR);
			} else {
				stmt.setString(5, ncc.getEmail());
			}
			n = stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n > 0;
	}

//	Cập nhật nhà cung cấp
	public boolean updateNCC(NhaCungCap ncc) {
		con = ConnectDB.getInstance().getConnection();
		PreparedStatement stmt = null;
		int n = 0;
		String sql = "update NhaCungCap set TenNCC = ?, DiaChi = ?, SoDienThoai = ?, Email = ? where MaNCC = ?";
		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, ncc.getTenNCC());
			if (ncc.getDiaChi() == null || ncc.getDiaChi().isEmpty()) {
				stmt.setNull(2, Types.NVARCHAR);
			} else {
				stmt.setString(2, ncc.getDiaChi());
			}

			if (ncc.getSoDienThoai() == null || ncc.getSoDienThoai().isEmpty()) {
				stmt.setNull(3, Types.NVARCHAR);
			} else {
				stmt.setString(3, ncc.getSoDienThoai());
			}

			if (ncc.getEmail() == null || ncc.getEmail().isEmpty()) {
				stmt.setNull(4, Types.NVARCHAR);
			} else {
				stmt.setString(4, ncc.getEmail());
			}

			stmt.setString(5, ncc.getMaNCC());
			n = stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n > 0;
	}

//	Xoá nhà cung cấp
	public boolean xoaNCC(String maNCC) {
		con = ConnectDB.getInstance().getConnection();
		PreparedStatement stmt = null;
		int n = 0;
		String sql = "delete from NhaCungCap where MaNCC = ?";
		try {
			stmt = con.prepareStatement(sql);
			stmt.setString(1, maNCC);
			n = stmt.executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return n > 0;
	}
}
