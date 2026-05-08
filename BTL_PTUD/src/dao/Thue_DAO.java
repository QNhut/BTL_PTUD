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

    // Sinh mã thuế tự động: TH + YYYY + 3 số (VD: TH2026001)
    public String sinhMaTuDong() {
        String prefix = "TH";
        int nam = java.time.LocalDate.now().getYear();
        String pattern = prefix + nam;
        String sql = "SELECT MAX(MaThue) FROM Thue WHERE MaThue LIKE ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pattern + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String maxMa = rs.getString(1);
                        if (maxMa != null && maxMa.length() > pattern.length()) {
                            try {
                                int stt = Integer.parseInt(maxMa.substring(pattern.length())) + 1;
                                return pattern + String.format("%03d", stt);
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return pattern + "001";
    }

    // Áp dụng (gán) thuế cho TẤT CẢ sản phẩm thuộc các loại được chọn.
    // Trả về số sản phẩm bị ảnh hưởng. Truyền {@code maThue == null} để gỡ thuế.
    public int apDungChoLoaiSanPham(String maThue, java.util.List<String> dsMaLoai) {
        if (dsMaLoai == null || dsMaLoai.isEmpty()) return 0;
        StringBuilder sb = new StringBuilder("UPDATE SanPham SET MaThue = ? WHERE MaLoaiSanPham IN (");
        for (int i = 0; i < dsMaLoai.size(); i++) {
            sb.append(i == 0 ? "?" : ",?");
        }
        sb.append(")");
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sb.toString())) {
                if (maThue == null) ps.setNull(1, java.sql.Types.NVARCHAR);
                else ps.setString(1, maThue);
                for (int i = 0; i < dsMaLoai.size(); i++) {
                    ps.setString(i + 2, dsMaLoai.get(i));
                }
                return ps.executeUpdate();
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Đếm số SP đang gắn theo từng mã thuế (loại bỏ NULL).
    public java.util.Map<String, Integer> getDemSanPhamTheoThue() {
        String sql = "SELECT MaThue, COUNT(*) AS SoLuong FROM SanPham WHERE MaThue IS NOT NULL GROUP BY MaThue";
        java.util.Map<String, Integer> map = new java.util.HashMap<>();
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    map.put(rs.getString("MaThue"), rs.getInt("SoLuong"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    // Trả về danh sách MaLoaiSanPham đang được gán {@code maThue}.
    public java.util.List<String> layMaLoaiDangApDung(String maThue) {
        java.util.List<String> ds = new java.util.ArrayList<>();
        if (maThue == null) return ds;
        String sql = "SELECT DISTINCT MaLoaiSanPham FROM SanPham WHERE MaThue = ? AND MaLoaiSanPham IS NOT NULL";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maThue);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) ds.add(rs.getString(1));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return ds;
    }
}
