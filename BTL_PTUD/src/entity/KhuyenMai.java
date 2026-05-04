package entity;

import java.time.LocalDate;
import java.util.Objects;

public class KhuyenMai {
	private String maKhuyenMai;
	private String tenKhuyenMai;
	private double phanTramGG;
	private LocalDate ngayBatDau;
	private LocalDate ngayKetThuc;
	private boolean trangThai;

	public KhuyenMai(String maKhuyenMai, String tenKhuyenMai, double phanTramGG, LocalDate ngayBatDau,
			LocalDate ngayKetThuc, boolean trangThai) {
		setMaKhuyenMai(maKhuyenMai);
		setTenKhuyenMai(tenKhuyenMai);
		setPhanTramGG(phanTramGG);
		setNgayBatDau(ngayBatDau);
		setNgayKetThuc(ngayKetThuc);
		setTrangThai(trangThai);
	}

	public KhuyenMai(String maKhuyenMai) {
		setMaKhuyenMai(maKhuyenMai);
	}

	public KhuyenMai() {
	}

	public String getMaKhuyenMai() {
		return maKhuyenMai;
	}

	public void setMaKhuyenMai(String maKhuyenMai) {
		maKhuyenMai = normalizeRequired(maKhuyenMai);
		if (isBlank(maKhuyenMai)) {
			throw new IllegalArgumentException("Ma khuyen mai khong duoc de trong");
		}
		this.maKhuyenMai = maKhuyenMai;
	}

	public String getTenKhuyenMai() {
		return tenKhuyenMai;
	}

	public void setTenKhuyenMai(String tenKhuyenMai) {
		tenKhuyenMai = normalizeRequired(tenKhuyenMai);
		if (isBlank(tenKhuyenMai)) {
			throw new IllegalArgumentException("Ten khuyen mai khong duoc de trong");
		}
		this.tenKhuyenMai = tenKhuyenMai;
	}

	public double getPhanTramGG() {
		return phanTramGG;
	}

	public double getPhanTramGiamGia() {
		return phanTramGG;
	}

	public void setPhanTramGG(double phanTramGG) {
		if (phanTramGG < 0) {
			throw new IllegalArgumentException("Phan tram giam gia khong duoc am");
		}
		this.phanTramGG = phanTramGG;
	}

	public void setPhanTramGiamGia(double phanTramGiamGia) {
		setPhanTramGG(phanTramGiamGia);
	}

	public LocalDate getNgayBatDau() {
		return ngayBatDau;
	}

	public void setNgayBatDau(LocalDate ngayBatDau) {
		if (ngayBatDau == null) {
			this.ngayBatDau = LocalDate.now();
			return;
		}
		this.ngayBatDau = ngayBatDau;
	}

	public LocalDate getNgayKetThuc() {
		return ngayKetThuc;
	}

	public void setNgayKetThuc(LocalDate ngayKetThuc) {
		if (ngayKetThuc == null) {
			this.ngayKetThuc = this.ngayBatDau;
			return;
		}
		if (this.ngayBatDau != null && ngayKetThuc.isBefore(this.ngayBatDau)) {
			throw new IllegalArgumentException("Ngay ket thuc khong duoc truoc ngay bat dau");
		}
		this.ngayKetThuc = ngayKetThuc;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public int hashCode() {
		return Objects.hash(maKhuyenMai);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KhuyenMai other = (KhuyenMai) obj;
		return Objects.equals(maKhuyenMai, other.maKhuyenMai);
	}

	@Override
	public String toString() {
		return "KhuyenMai{" + "maKhuyenMai='" + maKhuyenMai + '\'' + ", tenKhuyenMai='" + tenKhuyenMai + '\''
				+ ", phanTramGG=" + phanTramGG + ", ngayBatDau=" + ngayBatDau + ", ngayKetThuc=" + ngayKetThuc
				+ ", trangThai=" + trangThai + '}';
	}

	private static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private static String normalizeRequired(String value) {
		return value == null ? null : value.trim();
	}

}
