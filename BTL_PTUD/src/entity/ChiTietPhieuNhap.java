package entity;

import java.util.Objects;

public class ChiTietPhieuNhap {
    private PhieuNhap phieuNhap;
    private SanPham sanPham;
    private int soLuong;
    private double giaNhap;

    public ChiTietPhieuNhap(PhieuNhap phieuNhap, SanPham sanPham, int soLuong, double giaNhap) {
        setPhieuNhap(phieuNhap);
        setSanPham(sanPham);
        setSoLuong(soLuong);
        setGiaNhap(giaNhap);
    }

    public ChiTietPhieuNhap() {
    }

    public PhieuNhap getPhieuNhap() {
        return phieuNhap;
    }

    public void setPhieuNhap(PhieuNhap phieuNhap) {
        this.phieuNhap = phieuNhap;
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

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        if (giaNhap < 0) {
            throw new IllegalArgumentException("Gia nhap khong duoc am");
        }
        this.giaNhap = giaNhap;
    }

    @Override
    public int hashCode() {
        String maPhieuNhap = phieuNhap == null ? null : phieuNhap.getMaPhieuNhap();
        String maSanPham = sanPham == null ? null : sanPham.getMaSanPham();
        return Objects.hash(maPhieuNhap, maSanPham);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ChiTietPhieuNhap other = (ChiTietPhieuNhap) obj;
        String thisMaPN = phieuNhap == null ? null : phieuNhap.getMaPhieuNhap();
        String thisMaSP = sanPham == null ? null : sanPham.getMaSanPham();
        String otherMaPN = other.phieuNhap == null ? null : other.phieuNhap.getMaPhieuNhap();
        String otherMaSP = other.sanPham == null ? null : other.sanPham.getMaSanPham();
        return Objects.equals(thisMaPN, otherMaPN) && Objects.equals(thisMaSP, otherMaSP);
    }
}
