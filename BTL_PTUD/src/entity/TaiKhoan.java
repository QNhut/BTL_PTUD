package entity;

import java.util.Objects;

public class TaiKhoan {
	private String tenDangNhap;
	private String matKhau;
	private boolean trangThaiOnline;
	private NhanVien nhanVien;

	public TaiKhoan(String tenDangNhap, String matKhau, boolean trangThaiOnline, NhanVien nhanVien) {
		setTenDangNhap(tenDangNhap);
		setMatKhau(matKhau);
		setTrangThaiOnline(trangThaiOnline);
		setNhanVien(nhanVien);
	}

	public TaiKhoan(String tenDangNhap, String matKhau, NhanVien nhanVien) {
		setTenDangNhap(tenDangNhap);
		setMatKhau(matKhau);
		setTrangThaiOnline(false);
		setNhanVien(nhanVien);
	}

	public TaiKhoan(String tenDangNhap, boolean trangThaiOnline, NhanVien nhanVien) {
		setTenDangNhap(tenDangNhap);
		setTrangThaiOnline(trangThaiOnline);
		setNhanVien(nhanVien);
	}

	public TaiKhoan(String tenDangNhap) {
		setTenDangNhap(tenDangNhap);
	}

	public TaiKhoan() {
	}

	public String getTenDangNhap() {
		return tenDangNhap;
	}

	public void setTenDangNhap(String tenDangNhap) {
		tenDangNhap = normalizeRequired(tenDangNhap);
		if (isBlank(tenDangNhap)) {
			throw new IllegalArgumentException("Ten dang nhap khong duoc de trong");
		}
		this.tenDangNhap = tenDangNhap;
	}

	public String getMatKhau() {
		return matKhau;
	}

	public void setMatKhau(String matKhau) {
		if (isBlank(matKhau)) {
			throw new IllegalArgumentException("Mat khau khong duoc de trong");
		}
		this.matKhau = matKhau;
	}

	public boolean isTrangThaiOnline() {
		return trangThaiOnline;
	}

	public void setTrangThaiOnline(boolean trangThaiOnline) {
		this.trangThaiOnline = trangThaiOnline;
	}

	public NhanVien getNhanVien() {
		return nhanVien;
	}

	public void setNhanVien(NhanVien nhanVien) {
		this.nhanVien = nhanVien;
	}

	@Override
	public int hashCode() {
		return Objects.hash(tenDangNhap);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TaiKhoan other = (TaiKhoan) obj;
		return Objects.equals(tenDangNhap, other.tenDangNhap);
	}

	@Override
	public String toString() {
		return "TaiKhoan{" + "tenDangNhap='" + tenDangNhap + '\'' + ", trangThaiOnline=" + trangThaiOnline
				+ ", nhanVien=" + nhanVien + '}';
	}

	private boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private String normalizeRequired(String value) {
		return value == null ? null : value.trim();
	}

}
