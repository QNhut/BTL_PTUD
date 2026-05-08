package entity;

import java.util.Objects;

public class KeSanPham {
    private String maKeSanPham;
    private String tenKeSanPham;
    private String viTri;
    private String moTa;

    public KeSanPham(String maKeSanPham, String tenKeSanPham, String viTri, String moTa) {
        setMaKeSanPham(maKeSanPham);
        setTenKeSanPham(tenKeSanPham);
        setViTri(viTri);
        setMoTa(moTa);
    }

    public KeSanPham(String maKeSanPham) {
        setMaKeSanPham(maKeSanPham);
    }

    public KeSanPham() {
    }

    public String getMaKeSanPham() {
        return maKeSanPham;
    }

    public void setMaKeSanPham(String maKeSanPham) {
        maKeSanPham = normalizeRequired(maKeSanPham);
        if (isBlank(maKeSanPham)) {
            throw new IllegalArgumentException("Ma ke san pham khong duoc de trong");
        }
        this.maKeSanPham = maKeSanPham;
    }

    public String getTenKeSanPham() {
        return tenKeSanPham;
    }

    public void setTenKeSanPham(String tenKeSanPham) {
        tenKeSanPham = normalizeRequired(tenKeSanPham);
        if (isBlank(tenKeSanPham)) {
            throw new IllegalArgumentException("Ten ke san pham khong duoc de trong");
        }
        this.tenKeSanPham = tenKeSanPham;
    }

    public String getViTri() {
        return viTri;
    }

    public void setViTri(String viTri) {
        viTri = normalizeRequired(viTri);
        if (isBlank(viTri)) {
            throw new IllegalArgumentException("Vi tri khong duoc de trong");
        }
        this.viTri = viTri;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = normalizeOptional(moTa);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maKeSanPham);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        KeSanPham other = (KeSanPham) obj;
        return Objects.equals(maKeSanPham, other.maKeSanPham);
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
