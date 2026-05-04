package entity;

import java.time.LocalDate;
import java.util.Objects;

public class HoaDon {
    private String maHoaDon;
    private LocalDate ngayLap;
    private double tongTien;
    private int diemTichLuy;
    private String maPTTT;
    private NhanVien nhanVien;
    private KhachHang khachHang;

    public HoaDon(String maHoaDon, LocalDate ngayLap, double tongTien, int diemTichLuy, String maPTTT,
                  NhanVien nhanVien, KhachHang khachHang) {
        setMaHoaDon(maHoaDon);
        setNgayLap(ngayLap);
        setTongTien(tongTien);
        setDiemTichLuy(diemTichLuy);
        setMaPTTT(maPTTT);
        setNhanVien(nhanVien);
        setKhachHang(khachHang);
    }

    public HoaDon(String maHoaDon) {
        setMaHoaDon(maHoaDon);
    }

    public HoaDon() {
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public String getMaHD() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        maHoaDon = normalizeRequired(maHoaDon);
        if (isBlank(maHoaDon)) {
            throw new IllegalArgumentException("Ma hoa don khong duoc de trong");
        }
        this.maHoaDon = maHoaDon;
    }

    public void setMaHD(String maHD) {
        setMaHoaDon(maHD);
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        this.ngayLap = ngayLap == null ? LocalDate.now() : ngayLap;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        if (tongTien < 0) {
            throw new IllegalArgumentException("Tong tien khong duoc am");
        }
        this.tongTien = tongTien;
    }

    public int getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(int diemTichLuy) {
        if (diemTichLuy < 0) {
            throw new IllegalArgumentException("Diem tich luy khong duoc am");
        }
        this.diemTichLuy = diemTichLuy;
    }

    public String getMaPTTT() {
        return maPTTT;
    }

    public String getpTTT() {
        return maPTTT;
    }

    public void setMaPTTT(String maPTTT) {
        this.maPTTT = normalizeRequired(maPTTT);
    }

    public void setpTTT(String pTTT) {
        setMaPTTT(pTTT);
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    @Override
    public int hashCode() {
        return Objects.hash(maHoaDon);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        HoaDon other = (HoaDon) obj;
        return Objects.equals(maHoaDon, other.maHoaDon);
    }

    @Override
    public String toString() {
        return "HoaDon{" +
                "maHoaDon='" + maHoaDon + '\'' +
                ", ngayLap=" + ngayLap +
                ", tongTien=" + tongTien +
                ", diemTichLuy=" + diemTichLuy +
                ", maPTTT='" + maPTTT + '\'' +
                '}';
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private static String normalizeRequired(String value) {
        return value == null ? null : value.trim();
    }
}
