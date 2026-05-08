package dao;

import ConnectDB.ConnectDB;
import entity.KhachHang;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class KhachHang_DAO {

    private Connection con;

    // Lấy danh sách khách hàng
    public ArrayList<KhachHang> getDSKhachHang() {
        ArrayList<KhachHang> dsKH = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang ORDER BY MaKhachHang";

        try {
            con = ConnectDB.getInstance().getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                KhachHang kh = mapKhachHang(rs);
                dsKH.add(kh);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsKH;
    }

    // Map ResultSet → Object
    private KhachHang mapKhachHang(ResultSet rs) throws SQLException {
        String maKH = rs.getString("MaKhachHang");
        String tenKH = rs.getString("TenKhachHang");
        String sdt = rs.getString("SoDienThoai");
        String email = rs.getString("Email");
        boolean gioiTinh = rs.getBoolean("GioiTinh");
        int diem = rs.getInt("DiemTichLuy");
        boolean trangThai = rs.getBoolean("TrangThai");

        return new KhachHang(maKH, tenKH, sdt, email, gioiTinh, diem, trangThai);
    }

    // Lấy theo SĐT
    public KhachHang layKHTheoSDT(String sdt) {
        String sql = "SELECT * FROM KhachHang WHERE SoDienThoai = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, sdt);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapKhachHang(rs);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Lấy theo mã
    public KhachHang layKHTheoMa(String maKH) {
        String sql = "SELECT * FROM KhachHang WHERE MaKhachHang = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maKH);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapKhachHang(rs);
            }

            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Thêm khách hàng
    public boolean themKhachHang(KhachHang kh) {
        String sql = "INSERT INTO KhachHang VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, kh.getMaKhachHang());
            stmt.setString(2, kh.getTenKhachHang());
            stmt.setString(3, kh.getSoDienThoai());
            stmt.setString(4, kh.getEmail());
            stmt.setBoolean(5, kh.isGioiTinh());
            stmt.setInt(6, kh.getDiemTichLuy());
            stmt.setBoolean(7, kh.isTrangThai());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Cập nhật
    public boolean updateKhachHang(KhachHang kh) {
        String sql = "UPDATE KhachHang SET TenKhachHang=?, SoDienThoai=?, Email=?, GioiTinh=?, DiemTichLuy=?, TrangThai=? WHERE MaKhachHang=?";

        try {
            con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);

            stmt.setString(1, kh.getTenKhachHang());
            stmt.setString(2, kh.getSoDienThoai());
            stmt.setString(3, kh.getEmail());
            stmt.setBoolean(4, kh.isGioiTinh());
            stmt.setInt(5, kh.getDiemTichLuy());
            stmt.setBoolean(6, kh.isTrangThai());
            stmt.setString(7, kh.getMaKhachHang());

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Xóa
    public boolean xoaKhachHang(String maKH) {
        String sql = "DELETE FROM KhachHang WHERE MaKhachHang=?";

        try {
            con = ConnectDB.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maKH);

            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    //===== Sinh mã khách hàng tự động: KH + YYYY + 3 số (VD: KH2026001) =====
    public String sinhMaTuDong() {
        String prefix = "KH";
        int nam = java.time.LocalDate.now().getYear();
        String pattern = prefix + nam;
        String sql = "SELECT MAX(MaKhachHang) FROM KhachHang WHERE MaKhachHang LIKE ?";
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
                            } catch (NumberFormatException ignored) {
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return pattern + "001";
    }

    // ============================================================
    // THỐNG KÊ KHÁCH HÀNG
    // ============================================================

    // Đếm tổng KH đang hoạt động trong hệ thống
    public int demTongKH() {
        String sql = "SELECT COUNT(*) FROM KhachHang WHERE TrangThai = 1";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Đếm số KH có phát sinh giao dịch trong khoảng thời gian
    public int demKHCoGiaoDich(String tuNgay, String denNgay) {
        String sql = "SELECT COUNT(DISTINCT MaKhachHang) FROM HoaDon "
                + "WHERE CAST(NgayLap AS DATE) BETWEEN ? AND ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, tuNgay);
                ps.setString(2, denNgay);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Đếm KH mới (có hóa đơn đầu tiên trong khoảng thời gian)
    public int demKHMoi(String tuNgay, String denNgay) {
        String sql = "SELECT COUNT(DISTINCT hd.MaKhachHang) FROM HoaDon hd "
                + "WHERE CAST(hd.NgayLap AS DATE) BETWEEN ? AND ? "
                + "AND hd.MaKhachHang NOT IN ("
                + "  SELECT DISTINCT MaKhachHang FROM HoaDon WHERE CAST(NgayLap AS DATE) < ?"
                + ")";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, tuNgay);
                ps.setString(2, denNgay);
                ps.setString(3, tuNgay);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Tổng doanh thu từ KH trong khoảng thời gian
    public double doanhThuKH(String tuNgay, String denNgay) {
        String sql = "SELECT ISNULL(SUM(ct.SoLuong * ct.DonGia), 0) "
                + "FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon "
                + "WHERE CAST(hd.NgayLap AS DATE) BETWEEN ? AND ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, tuNgay);
                ps.setString(2, denNgay);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getDouble(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Đếm KH có >= threshold đơn hàng (dùng tính tỉ lệ giữ chân)
    public int demKHCoNhieuDon(int threshold) {
        String sql = "SELECT COUNT(*) FROM ("
                + "  SELECT MaKhachHang FROM HoaDon GROUP BY MaKhachHang HAVING COUNT(*) >= ?"
                + ") sub";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, threshold);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Đếm tổng KH đã từng mua hàng
    public int demKHDaMua() {
        String sql = "SELECT COUNT(DISTINCT MaKhachHang) FROM HoaDon";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    // Danh sách KH kèm ngày lập gần nhất, số đơn, tổng chi tiêu, phân loại.
    // Nếu tuNgay/denNgay != null: chỉ lấy KH có giao dịch trong khoảng đó.
    // Trả về: [MaKH, TenKH, SDT, NgayLapGanNhat(String), SoDon, TongChiTieu, PhanLoai]
    public ArrayList<Object[]> layDanhSachKHThongKe(String tuNgay, String denNgay) {
        ArrayList<Object[]> rows = new ArrayList<>();
        boolean hasDate = (tuNgay != null && denNgay != null);

        String sql = "SELECT kh.MaKhachHang, kh.TenKhachHang, kh.SoDienThoai, "
                + "MAX(hd.NgayLap) AS NgayLapGanNhat, "
                + "COUNT(hd.MaHoaDon) AS SoDon, "
                + "ISNULL(SUM(ct.SoLuong * ct.DonGia), 0) AS TongChiTieu, "
                + "CASE "
                + "  WHEN COUNT(hd.MaHoaDon) >= 5 THEN N'Thường xuyên' "
                + "  WHEN COUNT(hd.MaHoaDon) >= 2 THEN N'Tiềm năng' "
                + "  ELSE N'Khách hàng mới' "
                + "END AS PhanLoai "
                + "FROM KhachHang kh "
                + "INNER JOIN HoaDon hd ON kh.MaKhachHang = hd.MaKhachHang "
                + (hasDate ? "AND CAST(hd.NgayLap AS DATE) BETWEEN ? AND ? " : "")
                + "LEFT JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon "
                + "WHERE kh.TrangThai = 1 "
                + "GROUP BY kh.MaKhachHang, kh.TenKhachHang, kh.SoDienThoai "
                + "ORDER BY TongChiTieu DESC";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                if (hasDate) {
                    ps.setString(1, tuNgay);
                    ps.setString(2, denNgay);
                }
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        java.sql.Timestamp ts = rs.getTimestamp("NgayLapGanNhat");
                        String ngayLap = "";
                        if (ts != null) {
                            ngayLap = ts.toLocalDateTime()
                                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
                        }
                        rows.add(new Object[]{
                            rs.getString("MaKhachHang"),
                            rs.getString("TenKhachHang"),
                            rs.getString("SoDienThoai"),
                            ngayLap,
                            rs.getInt("SoDon"),
                            rs.getDouble("TongChiTieu"),
                            rs.getString("PhanLoai")
                        });
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return rows;
    }

    // Thống kê phân loại KH cho Pie chart: {loại -> số lượng}
    public java.util.LinkedHashMap<String, Integer> thongKePhanLoai() {
        java.util.LinkedHashMap<String, Integer> map = new java.util.LinkedHashMap<>();
        String sql = "SELECT PhanLoai, COUNT(*) AS SoLuong FROM ("
                + "  SELECT CASE "
                + "    WHEN COUNT(hd.MaHoaDon) >= 5 THEN N'Thường xuyên' "
                + "    WHEN COUNT(hd.MaHoaDon) >= 2 THEN N'Tiềm năng' "
                + "    ELSE N'Khách hàng mới' "
                + "  END AS PhanLoai "
                + "  FROM KhachHang kh "
                + "  LEFT JOIN HoaDon hd ON kh.MaKhachHang = hd.MaKhachHang "
                + "  WHERE kh.TrangThai = 1 "
                + "  GROUP BY kh.MaKhachHang"
                + ") sub GROUP BY PhanLoai";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
                while (rs.next()) {
                    map.put(rs.getString("PhanLoai"), rs.getInt("SoLuong"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    // Doanh thu theo tháng trong năm cho bar chart: {tháng -> doanh thu}
    public java.util.LinkedHashMap<String, Double> doanhThuTheoThang(int nam) {
        java.util.LinkedHashMap<String, Double> map = new java.util.LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) map.put("T" + i, 0.0);
        String sql = "SELECT MONTH(hd.NgayLap) AS Thang, SUM(ct.SoLuong * ct.DonGia) AS DT "
                + "FROM HoaDon hd JOIN ChiTietHoaDon ct ON hd.MaHoaDon = ct.MaHoaDon "
                + "WHERE YEAR(hd.NgayLap) = ? "
                + "GROUP BY MONTH(hd.NgayLap) ORDER BY Thang";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, nam);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        map.put("T" + rs.getInt("Thang"), rs.getDouble("DT"));
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }

    // Số lượng KH theo loại theo tháng cho line chart xu hướng
    public java.util.LinkedHashMap<String, int[]> xuHuongKHTheoThang(int nam) {
        // key = "T1".."T12", value = [thuongXuyen, khachMoi, tiemNang]
        java.util.LinkedHashMap<String, int[]> map = new java.util.LinkedHashMap<>();
        for (int i = 1; i <= 12; i++) map.put("T" + i, new int[]{0, 0, 0});

        String sql = "SELECT MONTH(hd.NgayLap) AS Thang, "
                + "SUM(CASE WHEN sub.PhanLoai = N'Thường xuyên' THEN 1 ELSE 0 END) AS TX, "
                + "SUM(CASE WHEN sub.PhanLoai = N'Khách hàng mới' THEN 1 ELSE 0 END) AS KM, "
                + "SUM(CASE WHEN sub.PhanLoai = N'Tiềm năng' THEN 1 ELSE 0 END) AS TN "
                + "FROM HoaDon hd "
                + "INNER JOIN ("
                + "  SELECT kh.MaKhachHang, CASE "
                + "    WHEN COUNT(h2.MaHoaDon) >= 5 THEN N'Thường xuyên' "
                + "    WHEN COUNT(h2.MaHoaDon) >= 2 THEN N'Tiềm năng' "
                + "    ELSE N'Khách hàng mới' "
                + "  END AS PhanLoai "
                + "  FROM KhachHang kh LEFT JOIN HoaDon h2 ON kh.MaKhachHang = h2.MaKhachHang "
                + "  GROUP BY kh.MaKhachHang"
                + ") sub ON hd.MaKhachHang = sub.MaKhachHang "
                + "WHERE YEAR(hd.NgayLap) = ? "
                + "GROUP BY MONTH(hd.NgayLap) ORDER BY Thang";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, nam);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        int thang = rs.getInt("Thang");
                        map.put("T" + thang, new int[]{rs.getInt("TX"), rs.getInt("KM"), rs.getInt("TN")});
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return map;
    }
}
