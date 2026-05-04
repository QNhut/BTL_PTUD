package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.Thue;

public class Thue_DAO {
    private Connection con;

    public ArrayList<Thue> getDSThue() {
        ArrayList<Thue> ds = new ArrayList<Thue>();
        String sql = "SELECT MaThue, TenThue, PhanTramThue, MoTa FROM Thue ORDER BY MaThue";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    ds.add(new Thue(rs.getString("MaThue"), rs.getString("TenThue"), rs.getDouble("PhanTramThue"), rs.getString("MoTa")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ds;
    }

    public Thue layTheoMa(String maThue) {
        String sql = "SELECT MaThue, TenThue, PhanTramThue, MoTa FROM Thue WHERE MaThue = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maThue);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return new Thue(rs.getString("MaThue"), rs.getString("TenThue"), rs.getDouble("PhanTramThue"), rs.getString("MoTa"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean them(Thue thue) {
        String sql = "INSERT INTO Thue (MaThue, TenThue, PhanTramThue, MoTa) VALUES (?, ?, ?, ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, thue.getMaThue());
                ps.setString(2, thue.getTenThue());
                ps.setDouble(3, thue.getPhanTramThue());
                ps.setString(4, thue.getMoTa());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean capNhat(Thue thue) {
        String sql = "UPDATE Thue SET TenThue = ?, PhanTramThue = ?, MoTa = ? WHERE MaThue = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, thue.getTenThue());
                ps.setDouble(2, thue.getPhanTramThue());
                ps.setString(3, thue.getMoTa());
                ps.setString(4, thue.getMaThue());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean xoa(String maThue) {
        String sql = "DELETE FROM Thue WHERE MaThue = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maThue);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
