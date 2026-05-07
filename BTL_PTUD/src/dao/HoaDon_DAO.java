package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import ConnectDB.ConnectDB;
import entity.HoaDon;
import entity.KhachHang;
import entity.NhanVien;

public class HoaDon_DAO {
    private Connection con;

    // SQL chung để SELECT HoaDon kèm TongTien (tính từ ChiTietHoaDon) và DiemTichLuy (từ KhachHang)
    private static final String SELECT_HOA_DON =
        "SELECT hd.MaHoaDon, hd.NgayLap, hd.MaKhachHang, hd.MaNhanVien, hd.MaPTTT, " +
        "COALESCE((SELECT SUM(c.SoLuong * c.DonGia) FROM ChiTietHoaDon c WHERE c.MaHoaDon = hd.MaHoaDon), 0) AS TongTien, " +
        "COALESCE(kh.DiemTichLuy, 0) AS DiemTichLuy " +
        "FROM HoaDon hd LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang";

    public ArrayList<HoaDon> getDSHoaDon() {
        ArrayList<HoaDon> dsHD = new ArrayList<HoaDon>();
        String sql = SELECT_HOA_DON + " ORDER BY hd.MaHoaDon";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement statement = con.createStatement(); ResultSet rs = statement.executeQuery(sql)) {
                while (rs.next()) {
                    dsHD.add(mapHoaDon(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsHD;
    }

    public boolean taoHoaDon(HoaDon hd) {
        // TongTien và DiemTichLuy không có trong bảng HoaDon;
        // TongTien tính qua ChiTietHoaDon, DiemTichLuy lưu trong KhachHang.
        String sql = "INSERT INTO HoaDon (MaHoaDon, NgayLap, MaKhachHang, MaNhanVien, MaPTTT) VALUES (?, ?, ?, ?, ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, hd.getMaHoaDon());
                stmt.setDate(2, Date.valueOf(hd.getNgayLap()));
                stmt.setString(3, hd.getKhachHang().getMaKhachHang());
                stmt.setString(4, hd.getNhanVien().getMaNhanVien());
                stmt.setString(5, hd.getMaPTTT());
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public HoaDon layHDTheoMa(String maHD) {
        String sql = SELECT_HOA_DON + " WHERE hd.MaHoaDon = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maHD);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return mapHoaDon(rs);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean xoaHoaDon(String maHD) {
        String sql = "DELETE FROM HoaDon WHERE MaHoaDon = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maHD);
                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private HoaDon mapHoaDon(ResultSet rs) throws SQLException {
        String maHoaDon = rs.getString("MaHoaDon");
        LocalDate ngayLap = rs.getDate("NgayLap").toLocalDate();
        KhachHang khachHang = new KhachHang(rs.getString("MaKhachHang"));
        NhanVien nhanVien = new NhanVien(rs.getString("MaNhanVien"));
        String maPTTT = rs.getString("MaPTTT");
        double tongTien = rs.getDouble("TongTien");
        int diemTichLuy = rs.getInt("DiemTichLuy");
        return new HoaDon(maHoaDon, ngayLap, tongTien, diemTichLuy, maPTTT, nhanVien, khachHang);
    }

    /** Sinh mã hóa đơn tự động: HD + YYYY + 4 số (VD: HD20260001) */
    public String sinhMaTuDong() {
        String prefix = "HD";
        int nam = LocalDate.now().getYear();
        String pattern = prefix + nam;
        String sql = "SELECT MAX(MaHoaDon) FROM HoaDon WHERE MaHoaDon LIKE ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pattern + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String maxMa = rs.getString(1);
                        if (maxMa != null && maxMa.length() >= pattern.length() + 4) {
                            int stt = Integer.parseInt(maxMa.substring(pattern.length())) + 1;
                            return pattern + String.format("%04d", stt);
                        }
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return pattern + "0001";
    }

    // ==================== THỐNG KÊ DOANH THU ====================

    /**
     * Tính doanh thu trong kỳ (lọc liên hợp: năm/tháng/ngày hoặc khoảng ngày).
     * Truyền null cho tham số nào không cần lọc.
     */
    public double tinhDoanhThuKy(Integer nam, Integer thang, Integer ngay,
            LocalDate tuNgay, LocalDate denNgay) {
        StringBuilder sql = new StringBuilder(
                "SELECT COALESCE(SUM(c.SoLuong * c.DonGia), 0) "
                + "FROM HoaDon hd LEFT JOIN ChiTietHoaDon c ON hd.MaHoaDon = c.MaHoaDon WHERE 1=1");
        ArrayList<Object> params = new ArrayList<>();
        appendBoLocThoiGian(sql, params, nam, thang, ngay, tuNgay, denNgay);
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getDouble(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Tổng doanh thu toàn thời gian */
    public double tinhTongDoanhThu() {
        String sql = "SELECT COALESCE(SUM(c.SoLuong * c.DonGia), 0) "
                + "FROM HoaDon hd LEFT JOIN ChiTietHoaDon c ON hd.MaHoaDon = c.MaHoaDon";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /** Đếm số giao dịch trong kỳ */
    public int demSoGiaoDich(Integer nam, Integer thang, Integer ngay,
            LocalDate tuNgay, LocalDate denNgay) {
        StringBuilder sql = new StringBuilder(
                "SELECT COUNT(*) FROM HoaDon hd WHERE 1=1");
        ArrayList<Object> params = new ArrayList<>();
        appendBoLocThoiGian(sql, params, nam, thang, ngay, tuNgay, denNgay);
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getInt(1);
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Doanh thu theo từng tháng trong năm.
     * @return LinkedHashMap: "Tháng 1" → "Tháng 12" → doanh thu
     */
    public LinkedHashMap<String, Double> thongKeTheoThang(int nam) {
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        for (int t = 1; t <= 12; t++) result.put("Tháng " + t, 0.0);
        String sql = "SELECT MONTH(hd.NgayLap) AS Thang, COALESCE(SUM(c.SoLuong * c.DonGia), 0) AS DT "
                + "FROM HoaDon hd LEFT JOIN ChiTietHoaDon c ON hd.MaHoaDon = c.MaHoaDon "
                + "WHERE YEAR(hd.NgayLap) = ? GROUP BY MONTH(hd.NgayLap) ORDER BY Thang";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, nam);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.put("Tháng " + rs.getInt("Thang"), rs.getDouble("DT"));
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    /**
     * Doanh thu theo từng ngày trong tháng.
     * @return LinkedHashMap: "01" .. "31" → doanh thu
     */
    public LinkedHashMap<String, Double> thongKeTheoNgay(int nam, int thang) {
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        String sql = "SELECT DAY(hd.NgayLap) AS Ngay, COALESCE(SUM(c.SoLuong * c.DonGia), 0) AS DT "
                + "FROM HoaDon hd LEFT JOIN ChiTietHoaDon c ON hd.MaHoaDon = c.MaHoaDon "
                + "WHERE YEAR(hd.NgayLap) = ? AND MONTH(hd.NgayLap) = ? "
                + "GROUP BY DAY(hd.NgayLap) ORDER BY Ngay";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setInt(1, nam);
                ps.setInt(2, thang);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.put(String.format("%02d", rs.getInt("Ngay")), rs.getDouble("DT"));
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    /**
     * Xu hướng doanh thu 30 ngày gần nhất (hoặc khoảng tùy chọn).
     */
    public LinkedHashMap<String, Double> xuHuongTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        LinkedHashMap<String, Double> result = new LinkedHashMap<>();
        String sql = "SELECT CONVERT(varchar, hd.NgayLap, 23) AS Ngay, COALESCE(SUM(c.SoLuong * c.DonGia), 0) AS DT "
                + "FROM HoaDon hd LEFT JOIN ChiTietHoaDon c ON hd.MaHoaDon = c.MaHoaDon "
                + "WHERE hd.NgayLap >= ? AND hd.NgayLap <= ? "
                + "GROUP BY CONVERT(varchar, hd.NgayLap, 23) ORDER BY Ngay";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setDate(1, Date.valueOf(tuNgay));
                ps.setDate(2, Date.valueOf(denNgay));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) result.put(rs.getString("Ngay"), rs.getDouble("DT"));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    /**
     * Lấy danh sách hóa đơn trong kỳ — dùng cho bảng chi tiết trong ThongKe GUI.
     * Mỗi Object[]: maHD, ngayLap, maNV, maKH, sốGiao Dịch (luôn 1), tongTien, maPTTT, diemTichLuy
     */
    public ArrayList<Object[]> layDanhSachTheoKy(Integer nam, Integer thang, Integer ngay,
            LocalDate tuNgay, LocalDate denNgay) {
        ArrayList<Object[]> result = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT hd.MaHoaDon, hd.NgayLap, "
                + "COALESCE(nv.TenNhanVien, hd.MaNhanVien) AS TenNhanVien, "
                + "COALESCE(kh.TenKhachHang, hd.MaKhachHang) AS TenKhachHang, "
                + "COALESCE(SUM(c.SoLuong), 0) AS TongSoLuong, "
                + "COALESCE(SUM(c.SoLuong * c.DonGia), 0) AS TongTien, "
                + "hd.MaPTTT "
                + "FROM HoaDon hd "
                + "LEFT JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien "
                + "LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang "
                + "LEFT JOIN ChiTietHoaDon c ON hd.MaHoaDon = c.MaHoaDon "
                + "WHERE 1=1");
        ArrayList<Object> params = new ArrayList<>();
        appendBoLocThoiGian(sql, params, nam, thang, ngay, tuNgay, denNgay);
        sql.append(" GROUP BY hd.MaHoaDon, hd.NgayLap, nv.TenNhanVien, hd.MaNhanVien, kh.TenKhachHang, hd.MaKhachHang, hd.MaPTTT");
        sql.append(" ORDER BY hd.NgayLap DESC");
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
                for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        result.add(new Object[] {
                            rs.getString("MaHoaDon"),
                            rs.getDate("NgayLap").toLocalDate(),
                            rs.getString("TenNhanVien"),
                            rs.getString("TenKhachHang"),
                            rs.getInt("TongSoLuong"),
                            "-",
                            rs.getString("MaPTTT"),
                            rs.getDouble("TongTien")
                        });
                    }
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return result;
    }

    /** Append điều kiện thời gian vào SQL động (dùng alias hd. cho JOIN queries) */
    private void appendBoLocThoiGian(StringBuilder sql, ArrayList<Object> params,
            Integer nam, Integer thang, Integer ngay,
            LocalDate tuNgay, LocalDate denNgay) {
        if (tuNgay != null && denNgay != null) {
            sql.append(" AND hd.NgayLap >= ? AND hd.NgayLap <= ?");
            params.add(Date.valueOf(tuNgay));
            params.add(Date.valueOf(denNgay));
        } else {
            if (nam != null)   { sql.append(" AND YEAR(hd.NgayLap) = ?");  params.add(nam); }
            if (thang != null) { sql.append(" AND MONTH(hd.NgayLap) = ?"); params.add(thang); }
            if (ngay != null)  { sql.append(" AND DAY(hd.NgayLap) = ?");   params.add(ngay); }
        }
    }
}
