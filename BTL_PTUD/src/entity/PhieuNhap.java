package entity;

import java.time.LocalDate;
import java.util.Objects;

public class PhieuNhap {
    private String maPhieuNhap;
    private LocalDate ngayNhap;
    private NhaCungCap nhaCungCap;
    private NhanVien nhanVien;
    private String ghiChu;

    public PhieuNhap(String maPhieuNhap, LocalDate ngayNhap, NhaCungCap nhaCungCap, NhanVien nhanVien, String ghiChu) {
        setMaPhieuNhap(maPhieuNhap);
        setNgayNhap(ngayNhap);
        setNhaCungCap(nhaCungCap);
        setNhanVien(nhanVien);
        setGhiChu(ghiChu);
    }

    public PhieuNhap(String maPhieuNhap, NhaCungCap nhaCungCap, NhanVien nhanVien, String ghiChu) {
        this(maPhieuNhap, LocalDate.now(), nhaCungCap, nhanVien, ghiChu);
    }

    public PhieuNhap(String maPhieuNhap) {
        setMaPhieuNhap(maPhieuNhap);
    }

    public PhieuNhap() {
    }

    public String getMaPhieuNhap() {
        return maPhieuNhap;
    }

    public String getMaPN() {
        return maPhieuNhap;
    }

    public void setMaPhieuNhap(String maPhieuNhap) {
        maPhieuNhap = normalizeRequired(maPhieuNhap);
        if (isBlank(maPhieuNhap)) {
            throw new IllegalArgumentException("Ma phieu nhap khong duoc de trong");
        }
        this.maPhieuNhap = maPhieuNhap;
    }

    public void setMaPN(String maPN) {
        setMaPhieuNhap(maPN);
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap == null ? LocalDate.now() : ngayNhap;
    }

    public NhaCungCap getNhaCungCap() {
        return nhaCungCap;
    }

    public NhaCungCap getnCC() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public void setnCC(NhaCungCap nCC) {
        this.nhaCungCap = nCC;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = normalizeOptional(ghiChu);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPhieuNhap);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PhieuNhap other = (PhieuNhap) obj;
        return Objects.equals(maPhieuNhap, other.maPhieuNhap);
    }

    @Override
    public String toString() {
        return "PhieuNhap{" +
                "maPhieuNhap='" + maPhieuNhap + '\'' +
                ", ngayNhap=" + ngayNhap +
                ", ghiChu='" + ghiChu + '\'' +
                '}';
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
