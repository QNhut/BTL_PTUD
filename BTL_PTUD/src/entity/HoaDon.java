package entity;

import java.time.LocalDate;
import java.util.Objects;

public class HoaDon {
	private String maHD;
	private LocalDate ngayLap;
	private double tongTien;
	private int diemTichLuy;
	private String pTTT;
	private NhanVien nhanVien;
	private KhachHang khachHang;

	public HoaDon() {
	}

	public HoaDon(String maHD) {
		this.maHD = maHD;
	}

	public HoaDon(String maHD, LocalDate ngayLap, double tongTien, int diemTichLuy, String pTTT, NhanVien nhanVien,
			KhachHang khachHang) {
		this.maHD = maHD;
		this.ngayLap = ngayLap;
		this.tongTien = tongTien;
		this.diemTichLuy = diemTichLuy;
		this.pTTT = pTTT;
		this.nhanVien = nhanVien;
		this.khachHang = khachHang;
	}

	public String getMaHD() {
		return maHD;
	}

	public void setMaHD(String maHD) {
		this.maHD = maHD;
	}

	public LocalDate getNgayLap() {
		return ngayLap;
	}

	public void setNgayLap(LocalDate ngayLap) {
		this.ngayLap = ngayLap;
	}

	public double getTongTien() {
		return tongTien;
	}

	public void setTongTien(double tongTien) {
		this.tongTien = tongTien;
	}

	public int getDiemTichLuy() {
		return diemTichLuy;
	}

	public void setDiemTichLuy(int diemTichLuy) {
		this.diemTichLuy = diemTichLuy;
	}

	public String getpTTT() {
		return pTTT;
	}

	public void setpTTT(String pTTT) {
		this.pTTT = pTTT;
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
		return Objects.hash(maHD);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		HoaDon other = (HoaDon) obj;
		return Objects.equals(maHD, other.maHD);
	}

	public Object[] addToTable() {
		return new Object[] {};
	}

}
