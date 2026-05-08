package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import ConnectDB.ConnectDB;
import entity.KeSanPham;
import entity.LoSanPham;
import entity.PhieuNhap;
import entity.SanPham;

public class LoSanPham_DAO {

    private Connection con;

    public ArrayList<LoSanPham> getDSLoSanPham() {
        ArrayList<LoSanPham> ds = new ArrayList<LoSanPham>();
        String sql = "SELECT MaLoSanPham, MaSanPham, MaPhieuNhap, MaKeSanPham, SoLuong, DonViTinh, HanSuDung, TrangThai FROM LoSanPham ORDER BY MaLoSanPham";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
                while (rs.next()) {
                    try {
                        LoSanPham lo = new LoSanPham();
                        lo.setMaLoSanPham(rs.getString("MaLoSanPham"));
                        lo.setSanPham(new SanPham(rs.getString("MaSanPham")));
                        String maPN = rs.getString("MaPhieuNhap");
                        if (maPN != null && !maPN.trim().isEmpty()) {
                            lo.setPhieuNhap(new PhieuNhap(maPN));
                        }
                        String maKe = rs.getString("MaKeSanPham");
                        if (maKe != null && !maKe.trim().isEmpty()) {
                            lo.setKeSanPham(new KeSanPham(maKe));
                        }
                        lo.setSoLuong(rs.getInt("SoLuong"));
                        lo.setDonViTinh(rs.getString("DonViTinh"));
                        Date hsd = rs.getDate("HanSuDung");
                        lo.setHanSuDung(hsd == null ? null : hsd.toLocalDate());
                        lo.setTrangThai(rs.getBoolean("TrangThai"));
                        ds.add(lo);
                    } catch (Exception rowEx) {
                        rowEx.printStackTrace(); // log lỗi từng hàng nhưng tiếp tục
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    public boolean them(LoSanPham lo) {
        String sql = "INSERT INTO LoSanPham (MaLoSanPham, MaSanPham, MaPhieuNhap, MaKeSanPham, SoLuong, DonViTinh, HanSuDung, TrangThai) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, lo.getMaLoSanPham());
                ps.setString(2, lo.getSanPham().getMaSanPham());
                ps.setString(3, lo.getPhieuNhap().getMaPhieuNhap());
                ps.setString(4, lo.getKeSanPham().getMaKeSanPham());
                ps.setInt(5, lo.getSoLuong());
                ps.setString(6, lo.getDonViTinh());
                if (lo.getHanSuDung() == null) {
                    ps.setDate(7, null);
                } else {
                    ps.setDate(7, Date.valueOf(lo.getHanSuDung()));
                }
                ps.setBoolean(8, lo.isTrangThai());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int layTongSoLuongTonTheoMaSanPham(String maSanPham) {
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            return 0;
        }

        String sql = "SELECT COALESCE(SUM(SoLuong), 0) AS TongSoLuong "
                + "FROM LoSanPham WHERE MaSanPham = ? AND TrangThai = ? AND (HanSuDung IS NULL OR HanSuDung >= ?)";
        try {
            con = ConnectDB.getInstance().getConnection();
            if (con == null) {
                return 0;
            }
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maSanPham.trim());
                ps.setBoolean(2, true);
                ps.setDate(3, Date.valueOf(LocalDate.now()));
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("TongSoLuong");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // Lấy tất cả lô theo mã sản phẩm (kể cả hết hạn), sắp xếp HSD tăng dần.
    // Lưu ý: để ưu tiên trừ lô "sắp hết hạn nhất" trước (FEFO),
    //   các lô có HSD = NULL phải được xếp SAU CÙNG (mặc định SQL Server xếp NULL lên trước).
    //   Tách tie-breaker theo MaLoSanPham để thứ tự ổn định khi 2 lô cùng HSD.
    // Chỉ lấy dữ liệu trực tiếp từ bảng LoSanPham – Service sẽ enrich thêm.
    public ArrayList<LoSanPham> layTheoMaSanPham(String maSanPham) {
        ArrayList<LoSanPham> ds = new ArrayList<>();
        String sql = "SELECT MaLoSanPham, MaSanPham, MaPhieuNhap, MaKeSanPham, SoLuong, DonViTinh, HanSuDung, TrangThai "
                + "FROM LoSanPham WHERE MaSanPham = ? "
                + "ORDER BY CASE WHEN HanSuDung IS NULL THEN 1 ELSE 0 END, HanSuDung ASC, MaLoSanPham ASC";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maSanPham);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        try {
                            LoSanPham lo = new LoSanPham();
                            lo.setMaLoSanPham(rs.getString("MaLoSanPham"));
                            lo.setSanPham(new SanPham(rs.getString("MaSanPham")));
                            String maPN = rs.getString("MaPhieuNhap");
                            if (maPN != null && !maPN.trim().isEmpty()) {
                                lo.setPhieuNhap(new PhieuNhap(maPN));
                            }
                            String maKe = rs.getString("MaKeSanPham");
                            if (maKe != null && !maKe.trim().isEmpty()) {
                                lo.setKeSanPham(new KeSanPham(maKe));
                            }
                            lo.setSoLuong(rs.getInt("SoLuong"));
                            lo.setDonViTinh(rs.getString("DonViTinh"));
                            Date hsd = rs.getDate("HanSuDung");
                            lo.setHanSuDung(hsd == null ? null : hsd.toLocalDate());
                            lo.setTrangThai(rs.getBoolean("TrangThai"));
                            ds.add(lo);
                        } catch (Exception rowEx) {
                            rowEx.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ds;
    }

    // Giảm số lượng tồn kho theo FEFO (lô gần hết hạn nhất bị trừ trước).
    // Chỉ trừ các lô còn TrangThai=true và chưa hết hạn.
    // - UPDATE atomic dạng "SoLuong = SoLuong - ? WHERE SoLuong >= ?" để chống race condition
    //   khi 2 hóa đơn cùng trừ 1 lô song song.
    // - Kiểm tra rowsAffected để bảo đảm thật sự trừ được; nếu không đủ tồn → throw để
    //   transaction phía service rollback.
    public void giamSoLuongTheoSanPham(String maSanPham, int soLuongCan) {
        if (soLuongCan <= 0) {
            return;
        }
        ArrayList<LoSanPham> dsLo = layTheoMaSanPham(maSanPham);
        LocalDate homNay = LocalDate.now();

        // Lọc lô hợp lệ (còn hàng, còn hiệu lực, chưa hết hạn) và sắp xếp FEFO chặt:
        //   HSD gần nhất → HSD xa hơn → lô không HSD (xếp sau cùng vì coi như "không hết hạn").
        // Tie-breaker theo MaLoSanPham để thứ tự ổn định khi 2 lô cùng HSD.
        // Sắp xếp lại ở tầng Java để không phụ thuộc vào thuật tính NULLs-first của SQL Server.
        ArrayList<LoSanPham> hopLe = new ArrayList<>();
        for (LoSanPham lo : dsLo) {
            if (!lo.isTrangThai()) {
                continue;
            }
            if (lo.getSoLuong() <= 0) {
                continue;
            }
            if (lo.getHanSuDung() != null && lo.getHanSuDung().isBefore(homNay)) {
                continue;
            }
            hopLe.add(lo);
        }
        hopLe.sort((a, b) -> {
            LocalDate ha = a.getHanSuDung();
            LocalDate hb = b.getHanSuDung();
            if (ha == null && hb == null) {
                return safe(a.getMaLoSanPham()).compareTo(safe(b.getMaLoSanPham()));
            }
            if (ha == null) {
                return 1;   // lô không HSD xếp sau

                        }if (hb == null) {
                return -1;
            }
            int c = ha.compareTo(hb);   // HSD sớm hơn xếp trước
            return c != 0 ? c : safe(a.getMaLoSanPham()).compareTo(safe(b.getMaLoSanPham()));
        });

        int conLai = soLuongCan;
        // Atomic: chỉ trừ khi lô còn đủ số lượng; đồng thời cập nhật TrangThai = 0 nếu hết.
        String sqlCapNhat
                = "UPDATE LoSanPham SET SoLuong = SoLuong - ?, "
                + "TrangThai = CASE WHEN SoLuong - ? <= 0 THEN 0 ELSE 1 END "
                + "WHERE MaLoSanPham = ? AND SoLuong >= ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sqlCapNhat)) {
                for (LoSanPham lo : hopLe) {
                    if (conLai <= 0) {
                        break;
                    }
                    int tru = Math.min(lo.getSoLuong(), conLai);
                    if (tru <= 0) {
                        continue;
                    }
                    ps.setInt(1, tru);
                    ps.setInt(2, tru);
                    ps.setString(3, lo.getMaLoSanPham());
                    ps.setInt(4, tru);
                    int rows = ps.executeUpdate();
                    if (rows == 0) {
                        // Lô đã bị trừ bởi giao dịch khác giữa lúc ta đọc snapshot → bỏ qua, thử lô tiếp theo
                        continue;
                    }
                    conLai -= tru;
                }
            }
            if (conLai > 0) {
                throw new RuntimeException("Không đủ tồn kho để trừ cho sản phẩm "
                        + maSanPham + " (còn thiếu " + conLai + ")");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi giảm tồn kho lô sản phẩm: " + e.getMessage(), e);
        }
    }

    private static String safe(String s) {
        return s == null ? "" : s;
    }

    // Cập nhật kệ chứa của một lô sản phẩm.
    public boolean capNhatKe(String maLoSanPham, String maKeSanPham) {
        String sql = "UPDATE LoSanPham SET MaKeSanPham = ? WHERE MaLoSanPham = ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, maKeSanPham);
                ps.setString(2, maLoSanPham);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Sinh mã lô sản phẩm tự động: LSP + YYYY + 3 số (VD: LSP2026001)
    public String sinhMaTuDong() {
        String prefix = "LSP";
        int nam = LocalDate.now().getYear();
        String pattern = prefix + nam;
        String sql = "SELECT MAX(MaLoSanPham) FROM LoSanPham WHERE MaLoSanPham LIKE ?";
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
        return pattern + "0001";
    }

    // Sinh mã lô cơ sở cho preview — LO + YYYY + XXX (lấy số cuối lô hiện có + 1)
    public String sinhMaLoBase() {
        String prefix = "LO";
        int nam = LocalDate.now().getYear();
        String pattern = prefix + nam;
        String sql = "SELECT MAX(MaLoSanPham) FROM LoSanPham WHERE MaLoSanPham LIKE ?";
        try {
            con = ConnectDB.getInstance().getConnection();
            try (PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, pattern + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        String maxMa = rs.getString(1);
                        if (maxMa != null && maxMa.length() > pattern.length()) {
                            try {
                                String phan = maxMa.substring(pattern.length());
                                if (phan.contains("-")) {
                                    phan = phan.substring(0, phan.indexOf("-"));
                                }
                                int stt = Integer.parseInt(phan) + 1;
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
}
