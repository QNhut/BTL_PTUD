package entity;

import java.util.Objects;
import java.util.regex.Pattern;

public class NhanVien {
    private String maNhanVien;
    private String tenNhanVien;
    private boolean gioiTinh;
    private String soDienThoai;
    private String email;
    private String cCCD;
    private String diaChi;
    private String hinhAnh;
    private boolean trangThai;
    private ChucVu chucVu;

    private static final Pattern REGEX_SDT = Pattern.compile("\\d{10}");
    private static final Pattern REGEX_CCCD = Pattern.compile("\\d{12}");
    private static final Pattern REGEX_EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    public NhanVien(String maNhanVien, String tenNhanVien, boolean gioiTinh, String soDienThoai, String diaChi,
                    String email, String cCCD, ChucVu chucVu, String hinhAnh, boolean trangThai) {
        setMaNhanVien(maNhanVien);
        setTenNhanVien(tenNhanVien);
        setGioiTinh(gioiTinh);
        setSoDienThoai(soDienThoai);
        setDiaChi(diaChi);
        setEmail(email);
        setCCCD(cCCD);
        setChucVu(chucVu);
        setHinhAnh(hinhAnh);
        setTrangThai(trangThai);
    }

    public NhanVien(String maNhanVien) {
        setMaNhanVien(maNhanVien);
    }

    public NhanVien() {
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        maNhanVien = normalizeRequired(maNhanVien);
        if (isBlank(maNhanVien)) {
            throw new IllegalArgumentException("Ma nhan vien khong duoc de trong");
        }
        this.maNhanVien = maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        tenNhanVien = normalizeRequired(tenNhanVien);
        if (isBlank(tenNhanVien)) {
            throw new IllegalArgumentException("Ten nhan vien khong duoc de trong");
        }
        this.tenNhanVien = tenNhanVien;
    }

    public boolean isGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(boolean gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        soDienThoai = normalizeRequired(soDienThoai);
        if (isBlank(soDienThoai) || !REGEX_SDT.matcher(soDienThoai).matches()) {
            throw new IllegalArgumentException("So dien thoai phai gom 10 chu so");
        }
        this.soDienThoai = soDienThoai;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        email = normalizeOptional(email);
        if (!isBlank(email) && !REGEX_EMAIL.matcher(email).matches()) {
            throw new IllegalArgumentException("Email khong hop le");
        }
        this.email = email;
    }

    public String getCCCD() {
        return cCCD;
    }

    public void setCCCD(String cCCD) {
        cCCD = normalizeRequired(cCCD);
        if (isBlank(cCCD) || !REGEX_CCCD.matcher(cCCD).matches()) {
            throw new IllegalArgumentException("CCCD phai gom 12 chu so");
        }
        this.cCCD = cCCD;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = normalizeOptional(diaChi);
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = normalizeOptional(hinhAnh);
    }

    public boolean isTrangThai() {
        return trangThai;
    }

    public void setTrangThai(boolean trangThai) {
        this.trangThai = trangThai;
    }

    public ChucVu getChucVu() {
        return chucVu;
    }

    public void setChucVu(ChucVu chucVu) {
        this.chucVu = chucVu;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maNhanVien);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NhanVien other = (NhanVien) obj;
        return Objects.equals(maNhanVien, other.maNhanVien);
    }

    @Override
    public String toString() {
        return "NhanVien{"
                + "maNhanVien='" + maNhanVien + '\''
                + ", tenNhanVien='" + tenNhanVien + '\''
                + ", gioiTinh=" + gioiTinh
                + ", soDienThoai='" + maskPhone(soDienThoai) + '\''
                + ", diaChi='" + diaChi + '\''
                + ", email='" + email + '\''
                + ", cCCD='" + maskCCCD(cCCD) + '\''
                + ", hinhAnh='" + hinhAnh + '\''
                + ", trangThai=" + trangThai
                + ", chucVu=" + chucVu
                + '}';
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String normalizeRequired(String value) {
        return value == null ? null : value.trim();
    }

    private static String normalizeOptional(String value) {
        if (value == null) return null;
        String normalized = value.trim();
        return normalized.isEmpty() ? null : normalized;
    }

    private static String maskPhone(String value) {
        if (isBlank(value) || value.length() < 4) return value;
        return "******" + value.substring(value.length() - 4);
    }

    private static String maskCCCD(String value) {
        if (isBlank(value) || value.length() < 4) return value;
        return "********" + value.substring(value.length() - 4);
    }
}