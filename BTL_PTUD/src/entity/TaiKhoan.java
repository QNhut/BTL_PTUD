package entity;

import java.time.LocalDate;
import java.util.Objects;

public class TaiKhoan {
	private String tenDangNhap, maKhau;
	private LocalDate ngayTao;
	private boolean trangThaiOnline;
	private NhanVien nhanVien;

	public TaiKhoan() {
	}

	public TaiKhoan(String tenDangNhap) {
		this.tenDangNhap = tenDangNhap;
	}

	public TaiKhoan(String tenDangNhap, String maKhau, LocalDate ngayTao, boolean trangThaiOnline, NhanVien nhanVien) {
		this.tenDangNhap = tenDangNhap;
		this.maKhau = maKhau;
		this.ngayTao = ngayTao;
		this.trangThaiOnline = trangThaiOnline;
		this.nhanVien = nhanVien;
	}

	public String getTenDangNhap() {
		return tenDangNhap;
	}

	public void setTenDangNhap(String tenDangNhap) {
		this.tenDangNhap = tenDangNhap;
	}

	public String getMaKhau() {
		return maKhau;
	}

	public void setMaKhau(String maKhau) {
		this.maKhau = maKhau;
	}

	public LocalDate getNgayTao() {
		return ngayTao;
	}

	public void setNgayTao(LocalDate ngayTao) {
		this.ngayTao = ngayTao;
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

	public Object[] addToTable() {
		return new Object[] {};
	}

}
