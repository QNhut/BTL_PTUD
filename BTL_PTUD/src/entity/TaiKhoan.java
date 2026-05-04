package entity;

import java.time.LocalDate;
import java.util.Objects;

public class TaiKhoan {
	private String tenDangNhap;
	private String matKhau;
	private LocalDate ngayTao;
	private boolean trangThai;
	private NhanVien nhanVien;

	public TaiKhoan(String tenDangNhap, String matKhau, LocalDate ngayTao, boolean trangThai, NhanVien nhanVien) {
		setTenDangNhap(tenDangNhap);
		setMatKhau(matKhau);
		setNgayTao(ngayTao);
		setTrangThai(trangThai);
		setNhanVien(nhanVien);
	}

	public TaiKhoan(String tenDangNhap, String matKhau, LocalDate ngayTao, NhanVien nhanVien) {
		setTenDangNhap(tenDangNhap);
		setMatKhau(matKhau);
		setNgayTao(ngayTao);
		setTrangThai(false);
		setNhanVien(nhanVien);
	}

	public TaiKhoan(String tenDangNhap, boolean trangThai, NhanVien nhanVien) {
		setTenDangNhap(tenDangNhap);
		setTrangThai(trangThai);
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

	public String getTenTaiKhoan() {
		return tenDangNhap;
	}

	public void setTenDangNhap(String tenDangNhap) {
		tenDangNhap = normalizeRequired(tenDangNhap);
		if (isBlank(tenDangNhap)) {
			throw new IllegalArgumentException("Ten dang nhap khong duoc de trong");
		}
		this.tenDangNhap = tenDangNhap;
	}

	public void setTenTaiKhoan(String tenTaiKhoan) {
		setTenDangNhap(tenTaiKhoan);
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

	public LocalDate getNgayTao() {
		return ngayTao;
	}

	public void setNgayTao(LocalDate ngayTao) {
		if (ngayTao == null || ngayTao.isAfter(LocalDate.now())) {
			this.ngayTao = LocalDate.now();
		} else {
			this.ngayTao = ngayTao;
		}
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
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
		return "TaiKhoan{" + "tenDangNhap='" + tenDangNhap + '\'' + ", trangThaiOnline=" + trangThai + ", nhanVien="
				+ nhanVien + '}';
	}

	private static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private static String normalizeRequired(String value) {
		return value == null ? null : value.trim();
	}

}
