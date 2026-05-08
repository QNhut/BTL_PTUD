package service;

import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.LoaiSanPham_DAO;
import dao.SanPham_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.LoaiSanPham;
import entity.SanPham;
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
        if (hd == null) return "Hóa đơn không tồn tại.";
        
        java.time.LocalDate ngayLap = hd.getNgayLap();
        java.time.LocalDate hienTai = java.time.LocalDate.now();
        long soNgay = java.time.temporal.ChronoUnit.DAYS.between(ngayLap, hienTai);
        
        if (soNgay > 7) {
            return "Đã quá hạn đổi trả (7 ngày kể từ ngày lập hóa đơn).";
        }
        return "OK";
    }

    /**
     * Tính toán chênh lệch tiền
     * @return giá trị dương nếu khách phải trả thêm, âm nếu hoàn tiền cho khách
     */
    public double tinhToanChenhLech(List<ChiTietHoaDon> itemsReturn, List<ChiTietHoaDon> itemsNew) {
        double tongTra = 0;
        for (ChiTietHoaDon ct : itemsReturn) tongTra += ct.getSoLuong() * ct.getDonGia();
        
        double tongMoi = 0;
        for (ChiTietHoaDon ct : itemsNew) tongMoi += ct.getSoLuong() * ct.getDonGia();
        
        return tongMoi - tongTra;
    }
    public boolean thucHienGiaoDichDoiHang(String maHD, List<ChiTietHoaDon> itemsReturn, List<ChiTietHoaDon> itemsNew, String lyDoChinh) {
        try {
            // 1. Xử lý nhập lại hàng trả vào kho
            for (ChiTietHoaDon item : itemsReturn) {
                if (item.getSoLuong() <= 0) continue;
                String queryGetLot = "SELECT TOP 1 MaLoSanPham FROM LoSanPham WHERE MaSanPham = ? ORDER BY HanSuDung DESC";
                try (java.sql.PreparedStatement psLot = ConnectDB.ConnectDB.getInstance().getConnection().prepareStatement(queryGetLot)) {
                    psLot.setString(1, item.getSanPham().getMaSanPham());
                    try (java.sql.ResultSet rs = psLot.executeQuery()) {
                        if (rs.next()) {
                            String maLo = rs.getString(1);
                            String updateLot = "UPDATE LoSanPham SET SoLuong = SoLuong + ? WHERE MaLoSanPham = ?";
                            try (java.sql.PreparedStatement psUpdate = ConnectDB.ConnectDB.getInstance().getConnection().prepareStatement(updateLot)) {
                                psUpdate.setInt(1, item.getSoLuong());
                                psUpdate.setString(2, maLo);
                                psUpdate.executeUpdate();
                            }
                        }
                    }
                }
            }

            // 2. Xử lý xuất hàng mới từ kho
            for (ChiTietHoaDon item : itemsNew) {
                if (item.getSoLuong() <= 0) continue;
                int remaining = item.getSoLuong();
                String queryGetLots = "SELECT MaLoSanPham, SoLuong FROM LoSanPham WHERE MaSanPham = ? AND TrangThai = 1 AND SoLuong > 0 ORDER BY HanSuDung ASC";
                try (java.sql.PreparedStatement psLot = ConnectDB.ConnectDB.getInstance().getConnection().prepareStatement(queryGetLots)) {
                    psLot.setString(1, item.getSanPham().getMaSanPham());
                    try (java.sql.ResultSet rs = psLot.executeQuery()) {
                        while (rs.next() && remaining > 0) {
                            String maLo = rs.getString("MaLoSanPham");
                            int soLuong = rs.getInt("SoLuong");
                            int deduct = Math.min(soLuong, remaining);
                            
                            String updateLot = "UPDATE LoSanPham SET SoLuong = SoLuong - ? WHERE MaLoSanPham = ?";
                            try (java.sql.PreparedStatement psUpdate = ConnectDB.ConnectDB.getInstance().getConnection().prepareStatement(updateLot)) {
                                psUpdate.setInt(1, deduct);
                                psUpdate.setString(2, maLo);
                                psUpdate.executeUpdate();
                            }
                            remaining -= deduct;
                        }
                        if (remaining > 0) {
                            throw new Exception("Không đủ số lượng trong kho cho sản phẩm: " + item.getSanPham().getTenSP());
                        }
                    }
                }
            }

            // 3. TÌNH HUỐNG: Lưu vết giao dịch (Tùy chọn)

            // 4. Cập nhật hóa đơn cũ
            if (maHD != null && !maHD.isEmpty()) {
                // Trừ sản phẩm trả lại
                for (ChiTietHoaDon item : itemsReturn) {
                    if (item.getSoLuong() <= 0) continue;
                    String sqlGetQty = "SELECT SoLuong FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaSanPham = ?";
                    int currentQty = 0;
                    try (java.sql.PreparedStatement ps = ConnectDB.ConnectDB.getInstance().getConnection().prepareStatement(sqlGetQty)) {
                        ps.setString(1, maHD);
                        ps.setString(2, item.getSanPham().getMaSanPham());
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) {
                                currentQty = rs.getInt("SoLuong");
                            }
                        }
                    }
                    int newQty = currentQty - item.getSoLuong();
                    if (newQty <= 0) {
                        String sqlDel = "DELETE FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaSanPham = ?";
                        try (java.sql.PreparedStatement ps = ConnectDB.ConnectDB.getInstance().getConnection().prepareStatement(sqlDel)) {
                            ps.setString(1, maHD);
                            ps.setString(2, item.getSanPham().getMaSanPham());
                            ps.executeUpdate();
                        }
                    } else {
                        String sqlUpd = "UPDATE ChiTietHoaDon SET SoLuong = ? WHERE MaHoaDon = ? AND MaSanPham = ?";
                        try (java.sql.PreparedStatement ps = ConnectDB.ConnectDB.getInstance().getConnection().prepareStatement(sqlUpd)) {
                            ps.setInt(1, newQty);
                            ps.setString(2, maHD);
                            ps.setString(3, item.getSanPham().getMaSanPham());
                            ps.executeUpdate();
                        }
                    }
                }

                // Thêm/cập nhật sản phẩm mới
                for (ChiTietHoaDon item : itemsNew) {
                    if (item.getSoLuong() <= 0) continue;
                    boolean exists = false;
                    String sqlCheck = "SELECT SoLuong FROM ChiTietHoaDon WHERE MaHoaDon = ? AND MaSanPham = ?";
                    try (java.sql.PreparedStatement ps = ConnectDB.ConnectDB.getInstance().getConnection().prepareStatement(sqlCheck)) {
                        ps.setString(1, maHD);
                        ps.setString(2, item.getSanPham().getMaSanPham());
                        try (java.sql.ResultSet rs = ps.executeQuery()) {
                            if (rs.next()) exists = true;
                        }
                    }
                    if (exists) {
                        String sqlUpd = "UPDATE ChiTietHoaDon SET SoLuong = SoLuong + ?, DonGia = ? WHERE MaHoaDon = ? AND MaSanPham = ?";
                        try (java.sql.PreparedStatement ps = ConnectDB.ConnectDB.getInstance().getConnection().prepareStatement(sqlUpd)) {
                            ps.setInt(1, item.getSoLuong());
                            ps.setDouble(2, item.getDonGia());
                            ps.setString(3, maHD);
                            ps.setString(4, item.getSanPham().getMaSanPham());
                            ps.executeUpdate();
                        }
                    } else {
                        String sqlIns = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaSanPham, SoLuong, DonGia) VALUES (?, ?, ?, ?)";
                        try (java.sql.PreparedStatement ps = ConnectDB.ConnectDB.getInstance().getConnection().prepareStatement(sqlIns)) {
                            ps.setString(1, maHD);
                            ps.setString(2, item.getSanPham().getMaSanPham());
                            ps.setInt(3, item.getSoLuong());
                            ps.setDouble(4, item.getDonGia());
                            ps.executeUpdate();
                        }
                    }
                }
            }

            return true;
        } catch (Exception e) {
            System.err.println("Lỗi xử lý đổi hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<SanPham> getAllSanPham() {
        return sanPhamDAO.getDSSanPham();
    }

    public List<LoaiSanPham> getAllLoaiSanPham() {
        return loaiSanPhamDAO.getDSLoaiSanPham();
    }
}
