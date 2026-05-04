package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.KeSanPham;

public class KeSanPham_DAO {
    private Connection con;

    public ArrayList<KeSanPham> getDSKeSanPham() {
        ArrayList<KeSanPham> ds = new ArrayList<KeSanPham>();
        String sql = "SELECT MaKeSanPham, TenKeSanPham, ViTri, MoTa FROM KeSanPham ORDER BY MaKeSanPham";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    ds.add(new KeSanPham(rs.getString("MaKeSanPham"), rs.getString("TenKeSanPham"), rs.getString("ViTri"), rs.getString("MoTa")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public KeSanPham layTheoMa(String maKeSanPham) {
        String sql = "SELECT MaKeSanPham, TenKeSanPham, ViTri, MoTa FROM KeSanPham WHERE MaKeSanPham = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maKeSanPham);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new KeSanPham(rs.getString("MaKeSanPham"), rs.getString("TenKeSanPham"), rs.getString("ViTri"), rs.getString("MoTa"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
