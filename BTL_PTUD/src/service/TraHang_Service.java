package service;

import entity.ChiTietHoaDon;
import entity.HoaDon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class TraHang_Service {

    private final dao.HoaDon_DAO hoaDonDAO = new dao.HoaDon_DAO();

    public HoaDon getHoaDonByMa(String maHD) {
        return hoaDonDAO.layHDTheoMa(maHD);
    }

    public String checkDieuKienTraHang(HoaDon hd) {
        if (hd == null) {
            return "Hóa đơn không tồn tại.";
        }

        String tt = hd.getTrangThai();
        if (HoaDon.TRANG_THAI_CHO_THANH_TOAN.equals(tt)) {
            return "Hóa đơn đang chờ thanh toán, chưa thể trả hàng.";
        }
        if (HoaDon.TRANG_THAI_TRA_HANG.equals(tt) || HoaDon.TRANG_THAI_DOI_HANG.equals(tt)) {
            return "Hóa đơn đã được xử lý (" + tt + "), không thể trả hàng tiếp.";
        }

        LocalDateTime ngayLap = hd.getNgayLap();
        if (ngayLap == null) {
            return "Hóa đơn không có ngày lập hợp lệ.";
        }
        long soNgay = ChronoUnit.DAYS.between(ngayLap.toLocalDate(), LocalDate.now());
        if (soNgay > 7) {
            return "Đã quá hạn đổi trả (7 ngày kể từ ngày lập hóa đơn).";
        }
        return "OK";
    }

    /**
     * Xử lý trả hàng trong 1 transaction: 1) Cộng lại số lượng vào một lô còn
     * hạn (FEFO ngược: lô có Hạn sử dụng xa nhất). 2) Trừ số lượng đã trả trong
     * ChiTietHoaDon (xoá dòng nếu về 0). Nếu bất kỳ bước nào lỗi → rollback
     * toàn bộ.
     */
    public boolean thucHienTraHang(String maHD, List<ChiTietHoaDon> itemsReturn, String lyDo) {
        if (maHD == null || maHD.isEmpty() || itemsReturn == null || itemsReturn.isEmpty()) {
            return false;
        }
        Connection con = null;
        boolean prevAuto = true;
        try {
            con = ConnectDB.ConnectDB.getInstance().getConnection();
            prevAuto = con.getAutoCommit();
            con.setAutoCommit(false);

            // 1) Cộng lại kho
            String sqlGetLot = "SELECT TOP 1 MaLoSanPham FROM LoSanPham "
                    + "WHERE MaSanPham = ? AND TrangThai = 1 "
                    + "ORDER BY CASE WHEN HanSuDung IS NULL THEN 1 ELSE 0 END, HanSuDung DESC";
            // TrangThai = 1: đảm bảo lô được active lại sau khi hoàn hàng
            String sqlUpdLot = "UPDATE LoSanPham SET SoLuong = SoLuong + ?, TrangThai = 1 WHERE MaLoSanPham = ?";
            for (ChiTietHoaDon item : itemsReturn) {
                if (item.getSoLuong() <= 0) {
                    continue;
                }
                String maLo = null;
                try (PreparedStatement ps = con.prepareStatement(sqlGetLot)) {
                    ps.setString(1, item.getSanPham().getMaSanPham());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            maLo = rs.getString(1);
                        }
                    }
                }
                if (maLo == null) {
                    // Không còn lô active → fallback: chọn bất kỳ lô của SP
                    try (PreparedStatement ps = con.prepareStatement(
                            "SELECT TOP 1 MaLoSanPham FROM LoSanPham WHERE MaSanPham = ? ORDER BY HanSuDung DESC")) {
                        ps.setString(1, item.getSanPham().getMaSanPham());
                        try (ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                maLo = rs.getString(1);
                            }
                        }
                    }
                }
                if (maLo == null) {
                    throw new RuntimeException("Không tìm thấy lô cho SP " + item.getSanPham().getMaSanPham());
                }
                try (PreparedStatement ps = con.prepareStatement(sqlUpdLot)) {
                    ps.setInt(1, item.getSoLuong());
                    ps.setString(2, maLo);
                    ps.executeUpdate();
                }
            }

            // 2) Trừ số lượng trong ChiTietHoaDon
            String sqlGetQty = "SELECT SoLuong FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaSanPham = ?";
            String sqlDel = "DELETE FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaSanPham = ?";
            String sqlUpd = "UPDATE ChiTietHoaDon SET SoLuong = ? WHERE MaHoaDon = ? AND MaSanPham = ?";
            for (ChiTietHoaDon item : itemsReturn) {
                if (item.getSoLuong() <= 0) {
                    continue;
                }
                int currentQty = 0;
                try (PreparedStatement ps = con.prepareStatement(sqlGetQty)) {
                    ps.setString(1, maHD);
                    ps.setString(2, item.getSanPham().getMaSanPham());
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            currentQty = rs.getInt("SoLuong");
                        }
                    }
                }
                if (currentQty <= 0) {
                    throw new RuntimeException("SP " + item.getSanPham().getMaSanPham()
                            + " không có trong hoá đơn " + maHD);
                }
                if (item.getSoLuong() > currentQty) {
                    throw new RuntimeException("Số lượng trả (" + item.getSoLuong()
                            + ") vượt quá SL còn lại (" + currentQty + ") của SP "
                            + item.getSanPham().getMaSanPham());
                }
                int newQty = currentQty - item.getSoLuong();
                if (newQty == 0) {
                    try (PreparedStatement ps = con.prepareStatement(sqlDel)) {
                        ps.setString(1, maHD);
                        ps.setString(2, item.getSanPham().getMaSanPham());
                        ps.executeUpdate();
                    }
                } else {
                    try (PreparedStatement ps = con.prepareStatement(sqlUpd)) {
                        ps.setInt(1, newQty);
                        ps.setString(2, maHD);
                        ps.setString(3, item.getSanPham().getMaSanPham());
                        ps.executeUpdate();
                    }
                }
            }

            // 3) Cập nhật trạng thái hóa đơn → "Trả hàng" và lưu lý do vào GhiChu
            try (PreparedStatement ps = con.prepareStatement(
                    "UPDATE HoaDon SET TrangThai = ?, GhiChu = ? WHERE MaHoaDon = ?")) {
                ps.setNString(1, entity.HoaDon.TRANG_THAI_TRA_HANG);
                String ghiChu = (lyDo != null && !lyDo.isBlank())
                        ? "[Trả hàng] " + lyDo.trim() : "[Trả hàng]";
                ps.setNString(2, ghiChu);
                ps.setString(3, maHD);
                ps.executeUpdate();
            }

            con.commit();
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi xử lý trả hàng: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (Exception ignored) {
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(prevAuto);
                } catch (Exception ignored) {
                }
            }
        }
    }
}
