package service;

import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.LoaiSanPham_DAO;
import dao.SanPham_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.LoaiSanPham;
import entity.SanPham;
import java.time.LocalDateTime;
import java.util.List;

public class DoiHang_Service {

    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
    private final SanPham_DAO sanPhamDAO = new SanPham_DAO();
    private final LoaiSanPham_DAO loaiSanPhamDAO = new LoaiSanPham_DAO();

    /**
     * Tìm hóa đơn theo mã
     */
    public HoaDon getHoaDonByMa(String maHD) {
        return hoaDonDAO.layHDTheoMa(maHD);
    }

    /**
     * Lấy chi tiết hóa đơn
     */
    public List<ChiTietHoaDon> getChiTietHoaDon(String maHD) {
        List<ChiTietHoaDon> ds = chiTietHoaDonDAO.getDSTheoHoaDon(maHD);
        for (ChiTietHoaDon ct : ds) {
            SanPham fullSP = sanPhamDAO.laySanPhamTheoMa(ct.getSanPham().getMaSanPham());
            if (fullSP != null) {
                ct.setSanPham(fullSP);
            }
        }
        return ds;
    }

    /**
     * Kiểm tra điều kiện đổi hàng (ví dụ: trong vòng 7 ngày)
     */
    public String checkDieuKienDoiHang(HoaDon hd) {
        if (hd == null) {
            return "Hóa đơn không tồn tại.";
        }

        // Chặn theo trạng thái — không cho đổi hàng trên phiếu chờ thanh toán
        // hoặc đã từng đổi/trả.
        String tt = hd.getTrangThai();
        if (HoaDon.TRANG_THAI_CHO_THANH_TOAN.equals(tt)) {
            return "Hóa đơn đang chờ thanh toán, chưa thể đổi hàng.";
        }
        if (HoaDon.TRANG_THAI_DOI_HANG.equals(tt) || HoaDon.TRANG_THAI_TRA_HANG.equals(tt)) {
            return "Hóa đơn đã được xử lý (" + tt + "), không thể đổi hàng tiếp.";
        }

        LocalDateTime ngayLap = hd.getNgayLap();
        if (ngayLap == null) {
            return "Hóa đơn không có ngày lập hợp lệ.";
        }
        // Cả 2 đầu phải cùng kiểu temporal — đưa về LocalDate để tính theo ngày.
        java.time.LocalDate ngayLapDate = ngayLap.toLocalDate();
        java.time.LocalDate hienTai = java.time.LocalDate.now();
        long soNgay = java.time.temporal.ChronoUnit.DAYS.between(ngayLapDate, hienTai);

        if (soNgay > 7) {
            return "Đã quá hạn đổi trả (7 ngày kể từ ngày lập hóa đơn).";
        }
        return "OK";
    }

    /**
     * Tính toán chênh lệch tiền
     *
     * @return giá trị dương nếu khách phải trả thêm, âm nếu hoàn tiền cho khách
     */
    public double tinhToanChenhLech(List<ChiTietHoaDon> itemsReturn, List<ChiTietHoaDon> itemsNew) {
        double tongTra = 0;
        for (ChiTietHoaDon ct : itemsReturn) {
            tongTra += ct.getSoLuong() * ct.getDonGia();
        }

        double tongMoi = 0;
        for (ChiTietHoaDon ct : itemsNew) {
            tongMoi += ct.getSoLuong() * ct.getDonGia();
        }

        return tongMoi - tongTra;
    }

    public boolean thucHienGiaoDichDoiHang(String maHD, List<ChiTietHoaDon> itemsReturn, List<ChiTietHoaDon> itemsNew, String lyDoChinh) {
        return thucHienGiaoDichDoiHang(maHD, itemsReturn, itemsNew, lyDoChinh, null);
    }

    public boolean thucHienGiaoDichDoiHang(String maHD, List<ChiTietHoaDon> itemsReturn, List<ChiTietHoaDon> itemsNew, String lyDoChinh, String maPTTT) {
        java.sql.Connection con = null;
        boolean prevAuto = true;
        try {
            con = ConnectDB.ConnectDB.getInstance().getConnection();
            prevAuto = con.getAutoCommit();
            con.setAutoCommit(false);

            // 1. Nhập lại hàng trả vào kho (cộng vào lô có HSD xa nhất)
            String queryGetLot = "SELECT TOP 1 MaLoSanPham FROM LoSanPham WHERE MaSanPham = ? "
                    + "ORDER BY CASE WHEN HanSuDung IS NULL THEN 1 ELSE 0 END, HanSuDung DESC";
            String updateLotAdd = "UPDATE LoSanPham SET SoLuong = SoLuong + ?, TrangThai = 1 WHERE MaLoSanPham = ?";
            for (ChiTietHoaDon item : itemsReturn) {
                if (item.getSoLuong() <= 0) continue;
                String maLo = null;
                try (java.sql.PreparedStatement psLot = con.prepareStatement(queryGetLot)) {
                    psLot.setString(1, item.getSanPham().getMaSanPham());
                    try (java.sql.ResultSet rs = psLot.executeQuery()) {
                        if (rs.next()) maLo = rs.getString(1);
                    }
                }
                if (maLo == null) {
                    throw new Exception("Không tìm thấy lô để hoàn trả cho SP "
                            + item.getSanPham().getMaSanPham());
                }
                try (java.sql.PreparedStatement psUpdate = con.prepareStatement(updateLotAdd)) {
                    psUpdate.setInt(1, item.getSoLuong());
                    psUpdate.setString(2, maLo);
                    psUpdate.executeUpdate();
                }
            }

            // 2. Xuất hàng mới từ kho (FEFO)
            String queryGetLots = "SELECT MaLoSanPham, SoLuong FROM LoSanPham "
                    + "WHERE MaSanPham = ? AND TrangThai = 1 AND SoLuong > 0 "
                    + "ORDER BY CASE WHEN HanSuDung IS NULL THEN 1 ELSE 0 END, HanSuDung ASC";
            String updateLotSub = "UPDATE LoSanPham SET SoLuong = SoLuong - ?, "
                    + "TrangThai = CASE WHEN SoLuong - ? <= 0 THEN 0 ELSE 1 END "
                    + "WHERE MaLoSanPham = ?";
            for (ChiTietHoaDon item : itemsNew) {
                if (item.getSoLuong() <= 0) continue;
                int remaining = item.getSoLuong();
                java.util.LinkedHashMap<String, Integer> deductPlan = new java.util.LinkedHashMap<>();
                try (java.sql.PreparedStatement psLot = con.prepareStatement(queryGetLots)) {
                    psLot.setString(1, item.getSanPham().getMaSanPham());
                    try (java.sql.ResultSet rs = psLot.executeQuery()) {
                        while (rs.next() && remaining > 0) {
                            String maLo = rs.getString("MaLoSanPham");
                            int soLuong = rs.getInt("SoLuong");
                            int deduct = Math.min(soLuong, remaining);
                            deductPlan.put(maLo, deduct);
                            remaining -= deduct;
                        }
                    }
                }
                if (remaining > 0) {
                    throw new Exception("Không đủ số lượng trong kho cho sản phẩm: "
                            + item.getSanPham().getTenSP());
                }
                for (java.util.Map.Entry<String, Integer> e : deductPlan.entrySet()) {
                    try (java.sql.PreparedStatement psUpdate = con.prepareStatement(updateLotSub)) {
                        psUpdate.setInt(1, e.getValue());
                        psUpdate.setInt(2, e.getValue());
                        psUpdate.setString(3, e.getKey());
                        psUpdate.executeUpdate();
                    }
                }
            }

            // 3. Cập nhật ChiTietHoaDon (đúng tên cột: MaHoaDon, MaSanPham, GiaGoc)
            if (maHD != null && !maHD.isEmpty()) {
                // Trừ sản phẩm trả lại
                String sqlGetQty = "SELECT SoLuong FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaSanPham = ?";
                String sqlDel = "DELETE FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaSanPham = ?";
                String sqlUpdQty = "UPDATE ChiTietHoaDon SET SoLuong = ? WHERE MaHoaDon = ? AND MaSanPham = ?";
                for (ChiTietHoaDon item : itemsReturn) {
                    if (item.getSoLuong() <= 0) continue;
                    int currentQty = 0;
                    try (java.sql.PreparedStatement ps = con.prepareStatement(sqlGetQty)) {
                        ps.setString(1, maHD);
                        ps.setString(2, item.getSanPham().getMaSanPham());
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) currentQty = rs.getInt("SoLuong");
                        }
                    }
                    int newQty = currentQty - item.getSoLuong();
                    if (newQty <= 0) {
                        try (java.sql.PreparedStatement ps = con.prepareStatement(sqlDel)) {
                            ps.setString(1, maHD);
                            ps.setString(2, item.getSanPham().getMaSanPham());
                            ps.executeUpdate();
                        }
                    } else {
                        try (java.sql.PreparedStatement ps = con.prepareStatement(sqlUpdQty)) {
                            ps.setInt(1, newQty);
                            ps.setString(2, maHD);
                            ps.setString(3, item.getSanPham().getMaSanPham());
                            ps.executeUpdate();
                        }
                    }
                }

                // Thêm/cập nhật sản phẩm mới
                String sqlCheck = "SELECT SoLuong FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaSanPham = ?";
                String sqlUpdAdd = "UPDATE ChiTietHoaDon SET SoLuong = SoLuong + ?, DonGia = ? WHERE MaHoaDon = ? AND MaSanPham = ?";
                String sqlIns = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, DonGia, GiaGoc) VALUES (?, ?, ?, ?, ?)";
                for (ChiTietHoaDon item : itemsNew) {
                    if (item.getSoLuong() <= 0) continue;
                    boolean exists = false;
                    try (java.sql.PreparedStatement ps = con.prepareStatement(sqlCheck)) {
                        ps.setString(1, maHD);
                        ps.setString(2, item.getSanPham().getMaSanPham());
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) exists = true;
                        }
                    }
                    if (exists) {
                        try (java.sql.PreparedStatement ps = con.prepareStatement(sqlUpdAdd)) {
                            ps.setInt(1, item.getSoLuong());
                            ps.setDouble(2, item.getDonGia());
                            ps.setString(3, maHD);
                            ps.setString(4, item.getSanPham().getMaSanPham());
                            ps.executeUpdate();
                        }
                    } else {
                        try (java.sql.PreparedStatement ps = con.prepareStatement(sqlIns)) {
                            ps.setString(1, maHD);
                            ps.setString(2, item.getSanPham().getMaSanPham());
                            ps.setInt(3, item.getSoLuong());
                            ps.setDouble(4, item.getDonGia());
                            double giaGoc = item.getGiaGoc() > 0 ? item.getGiaGoc() : item.getDonGia();
                            ps.setDouble(5, giaGoc);
                            ps.executeUpdate();
                        }
                    }
                }

                // 4. Cập nhật trạng thái hóa đơn → "Đổi hàng" và lưu lý do vào GhiChu
                String ghiChu = (lyDoChinh != null && !lyDoChinh.isBlank())
                        ? "[Đổi hàng] " + lyDoChinh.trim() : "[Đổi hàng]";
                if (maPTTT != null && !maPTTT.isBlank()) {
                    try (java.sql.PreparedStatement ps = con.prepareStatement(
                            "UPDATE HoaDon SET TrangThai = ?, GhiChu = ?, MaPTTT = ? WHERE MaHoaDon = ?")) {
                        ps.setNString(1, HoaDon.TRANG_THAI_DOI_HANG);
                        ps.setNString(2, ghiChu);
                        ps.setString(3, maPTTT);
                        ps.setString(4, maHD);
                        ps.executeUpdate();
                    }
                } else {
                    try (java.sql.PreparedStatement ps = con.prepareStatement(
                            "UPDATE HoaDon SET TrangThai = ?, GhiChu = ? WHERE MaHoaDon = ?")) {
                        ps.setNString(1, HoaDon.TRANG_THAI_DOI_HANG);
                        ps.setNString(2, ghiChu);
                        ps.setString(3, maHD);
                        ps.executeUpdate();
                    }
                }
            }

            con.commit();
            return true;
        } catch (Exception e) {
            System.err.println("Lỗi xử lý đổi hàng: " + e.getMessage());
            e.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (Exception ignored) {}
            }
            return false;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(prevAuto); } catch (Exception ignored) {}
            }
        }
    }

    public List<SanPham> getAllSanPham() {
        return sanPhamDAO.getDSSanPham();
    }

    public List<LoaiSanPham> getAllLoaiSanPham() {
        return loaiSanPhamDAO.getDSLoaiSanPham();
    }
}
