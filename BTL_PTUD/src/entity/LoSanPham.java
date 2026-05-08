package entity;

import java.time.LocalDate;
import java.util.Objects;

public class LoSanPham {
    private String maLoSanPham;
    private SanPham sanPham;
    private PhieuNhap phieuNhap;
    private KeSanPham keSanPham;
    private int soLuong;
    private String donViTinh;
    private LocalDate hanSuDung;
    private boolean trangThai;

    public LoSanPham(String maLoSanPham, SanPham sanPham, PhieuNhap phieuNhap, KeSanPham keSanPham,
                     int soLuong, String donViTinh, LocalDate hanSuDung, boolean trangThai) {
        setMaLoSanPham(maLoSanPham);
        setSanPham(sanPham);
        setPhieuNhap(phieuNhap);
        setKeSanPham(keSanPham);
        setSoLuong(soLuong);
        setDonViTinh(donViTinh);
        setHanSuDung(hanSuDung);
        setTrangThai(trangThai);
    }

    public LoSanPham(String maLoSanPham) {
        setMaLoSanPham(maLoSanPham);
    }

    public LoSanPham() {
    }

    public String getMaLoSanPham() {
        return maLoSanPham;
    }

    public void setMaLoSanPham(String maLoSanPham) {
        maLoSanPham = normalizeRequired(maLoSanPham);
        if (isBlank(maLoSanPham)) {
            throw new IllegalArgumentException("Ma lo san pham khong duoc de trong");
        }
        this.maLoSanPham = maLoSanPham;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public PhieuNhap getPhieuNhap() {
        return phieuNhap;
    }

    public void setPhieuNhap(PhieuNhap phieuNhap) {
        this.phieuNhap = phieuNhap;
    }

    public KeSanPham getKeSanPham() {
        return keSanPham;
    }

    public void setKeSanPham(KeSanPham keSanPham) {
        this.keSanPham = keSanPham;
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

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = normalizeOptional(donViTinh);
    }

    public LocalDate getHanSuDung() {
        return hanSuDung;
    }

    public void setHanSuDung(LocalDate hanSuDung) {
        this.hanSuDung = hanSuDung;
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maLoSanPham);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        LoSanPham other = (LoSanPham) obj;
        return Objects.equals(maLoSanPham, other.maLoSanPham);
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String normalizeRequired(String value) {
        return value == null ? null : value.trim();
    }

    private static String normalizeOptional(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }
}
