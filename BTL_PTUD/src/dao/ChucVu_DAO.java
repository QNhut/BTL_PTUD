package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.ChucVu;

public class ChucVu_DAO {
    private Connection con;

    public ArrayList<ChucVu> getDSChucVu() {
        ArrayList<ChucVu> ds = new ArrayList<ChucVu>();
        String sql = "SELECT MaChucVu, TenChucVu, MoTa FROM ChucVu ORDER BY MaChucVu";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    ds.add(new ChucVu(rs.getString("MaChucVu"), rs.getString("TenChucVu"), rs.getString("MoTa")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public ChucVu layTheoMa(String maChucVu) {
        String sql = "SELECT MaChucVu, TenChucVu, MoTa FROM ChucVu WHERE MaChucVu = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maChucVu);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new ChucVu(rs.getString("MaChucVu"), rs.getString("TenChucVu"), rs.getString("MoTa"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean them(ChucVu chucVu) {
        String sql = "INSERT INTO ChucVu (MaChucVu, TenChucVu, MoTa) VALUES (?, ?, ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, chucVu.getMaChucVu());
                ps.setString(2, chucVu.getTenChucVu());
                ps.setString(3, chucVu.getMoTa());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean capNhat(ChucVu chucVu) {
        String sql = "UPDATE ChucVu SET TenChucVu = ?, MoTa = ? WHERE MaChucVu = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, chucVu.getTenChucVu());
                ps.setString(2, chucVu.getMoTa());
                ps.setString(3, chucVu.getMaChucVu());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoa(String maChucVu) {
        String sql = "DELETE FROM ChucVu WHERE MaChucVu = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maChucVu);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
