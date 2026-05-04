package entity;

import java.util.Objects;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private SanPham sanPham;
    private int soLuong;
    private double donGia;

    public ChiTietHoaDon(HoaDon hoaDon, SanPham sanPham, int soLuong, double donGia) {
        setHoaDon(hoaDon);
        setSanPham(sanPham);
        setSoLuong(soLuong);
        setDonGia(donGia);
    }

    public ChiTietHoaDon() {
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        if (soLuong < 0) {
            throw new IllegalArgumentException("So luong khong duoc am");
        }
        this.soLuong = soLuong;
    }

    public double getDonGia() {
        return donGia;
    }

    public void setDonGia(double donGia) {
        if (donGia < 0) {
            throw new IllegalArgumentException("Don gia khong duoc am");
        }
        this.donGia = donGia;
    }

    @Override
    public int hashCode() {
        String maHoaDon = hoaDon == null ? null : hoaDon.getMaHoaDon();
        String maSanPham = sanPham == null ? null : sanPham.getMaSanPham();
        return Objects.hash(maHoaDon, maSanPham);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ChiTietHoaDon other = (ChiTietHoaDon) obj;
        String thisMaHD = hoaDon == null ? null : hoaDon.getMaHoaDon();
        String thisMaSP = sanPham == null ? null : sanPham.getMaSanPham();
        String otherMaHD = other.hoaDon == null ? null : other.hoaDon.getMaHoaDon();
        String otherMaSP = other.sanPham == null ? null : other.sanPham.getMaSanPham();
        return Objects.equals(thisMaHD, otherMaHD) && Objects.equals(thisMaSP, otherMaSP);
    }
}
