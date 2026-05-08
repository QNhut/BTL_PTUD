package service;

import entity.ChiTietHoaDon;
import java.util.List;

public class TraHang_Service {

    public boolean thucHienTraHang(String maHD, List<ChiTietHoaDon> itemsReturn, String lyDo) {
        try {
            // 1. Nhập lại hàng trả vào kho (cộng số lượng)
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

            // 2. Cập nhật hóa đơn cũ (trừ số lượng trong ChiTietHoaDon)
            if (maHD != null && !maHD.isEmpty()) {
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
            }

            return true;
        } catch (Exception e) {
            System.err.println("Lỗi xử lý trả hàng: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
