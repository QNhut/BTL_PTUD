package entity;

import java.util.Objects;

public class PhuongThucThanhToan {
    private String maPTTT;
    private String tenPTTT;
    private String moTa;
    private boolean trangThai;

    public PhuongThucThanhToan(String maPTTT, String tenPTTT, String moTa, boolean trangThai) {
        setMaPTTT(maPTTT);
        setTenPTTT(tenPTTT);
        setMoTa(moTa);
        setTrangThai(trangThai);
    }

    public PhuongThucThanhToan(String maPTTT) {
        setMaPTTT(maPTTT);
    }

    public PhuongThucThanhToan() {
    }

    public String getMaPTTT() {
        return maPTTT;
    }

    public void setMaPTTT(String maPTTT) {
        maPTTT = normalizeRequired(maPTTT);
        if (isBlank(maPTTT)) {
            throw new IllegalArgumentException("Ma phuong thuc thanh toan khong duoc de trong");
        }
        this.maPTTT = maPTTT;
    }

    public String getTenPTTT() {
        return tenPTTT;
    }

    public void setTenPTTT(String tenPTTT) {
        tenPTTT = normalizeRequired(tenPTTT);
        if (isBlank(tenPTTT)) {
            throw new IllegalArgumentException("Ten phuong thuc thanh toan khong duoc de trong");
        }
        this.tenPTTT = tenPTTT;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = normalizeOptional(moTa);
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maPTTT);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PhuongThucThanhToan other = (PhuongThucThanhToan) obj;
        return Objects.equals(maPTTT, other.maPTTT);
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
