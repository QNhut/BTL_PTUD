package entity;

import java.util.Objects;

public class PhieuNhap {
	private String maPhieuNhap;
	private NhaCungCap nCC;
	private NhanVien nhanVien;
	private String ghiChu;

	public PhieuNhap() {
	}

	public PhieuNhap(String maPhieuNhap) {
		this.maPhieuNhap = maPhieuNhap;
	}

	public PhieuNhap(String maPhieuNhap, NhaCungCap nCC, NhanVien nhanVien, String ghiChu) {
		this.maPhieuNhap = maPhieuNhap;
		this.nCC = nCC;
		this.nhanVien = nhanVien;
		this.ghiChu = ghiChu;
	}

	public String getMaPhieuNhap() {
		return maPhieuNhap;
	}

	public void setMaPhieuNhap(String maPhieuNhap) {
		this.maPhieuNhap = maPhieuNhap;
	}

	public NhaCungCap getnCC() {
		return nCC;
	}

	public void setnCC(NhaCungCap nCC) {
		this.nCC = nCC;
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
		this.ghiChu = ghiChu;
	}

	@Override
	public int hashCode() {
		return Objects.hash(maPhieuNhap);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PhieuNhap other = (PhieuNhap) obj;
		return Objects.equals(maPhieuNhap, other.maPhieuNhap);
	}

	public Object[] addToTable() {
		return new Object[] {};
	}

}
