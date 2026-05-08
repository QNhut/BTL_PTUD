package service;

import dao.ChiTietHoaDon_DAO;
import dao.LoSanPham_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import java.util.List;

public class ChiTietHoaDon_Service {

    private final ChiTietHoaDon_DAO chiTietDAO = new ChiTietHoaDon_DAO();
    private final LoSanPham_DAO loSanPhamDAO = new LoSanPham_DAO();

    //===="Lưu tất cả chi tiết hóa đơn và giảm tồn kho FEFO cho từng dòng"=====
    public void luuChiTietVaGiamTonKho(HoaDon hd, List<HoaDon_Service.CartItem> items) {
        for (HoaDon_Service.CartItem item : items) {
            ChiTietHoaDon ct = new ChiTietHoaDon(
                    hd,
                    item.getSanPham(),
                    item.getSoLuong(),
                    item.getDonGia()
            );
            ct.setGiaGoc(item.getGiaGoc());
            if (!chiTietDAO.them(ct, item.getGiaGoc())) {
                throw new RuntimeException(
                        "Lỗi khi lưu chi tiết hóa đơn: " + item.getSanPham().getTenSP());
            }
            loSanPhamDAO.giamSoLuongTheoSanPham(
                    item.getSanPham().getMaSanPham(),
                    item.getSoLuong()
            );
        }
    }

    //===="Lấy danh sách chi tiết theo mã hóa đơn"=====
    public List<ChiTietHoaDon> getChiTietTheoHoaDon(String maHoaDon) {
        return chiTietDAO.getDSTheoHoaDon(maHoaDon);
    }
}
