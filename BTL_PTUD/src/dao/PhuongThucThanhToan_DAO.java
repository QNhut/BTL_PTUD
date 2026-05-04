package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.PhuongThucThanhToan;

public class PhuongThucThanhToan_DAO {
    private Connection con;

    public ArrayList<PhuongThucThanhToan> getDSPhuongThuc() {
        ArrayList<PhuongThucThanhToan> ds = new ArrayList<PhuongThucThanhToan>();
        String sql = "SELECT MaPTTT, TenPTTT, MoTa, TrangThai FROM PhuongThucThanhToan ORDER BY MaPTTT";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    ds.add(new PhuongThucThanhToan(
                            rs.getString("MaPTTT"),
                            rs.getString("TenPTTT"),
                            rs.getString("MoTa"),
                            rs.getBoolean("TrangThai")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public PhuongThucThanhToan layTheoMa(String maPTTT) {
        String sql = "SELECT MaPTTT, TenPTTT, MoTa, TrangThai FROM PhuongThucThanhToan WHERE MaPTTT = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maPTTT);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new PhuongThucThanhToan(
                                rs.getString("MaPTTT"),
                                rs.getString("TenPTTT"),
                                rs.getString("MoTa"),
                                rs.getBoolean("TrangThai"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
