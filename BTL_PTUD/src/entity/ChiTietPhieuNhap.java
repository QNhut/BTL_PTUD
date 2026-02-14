package entity;

import java.util.Objects;

public class ChiTietPhieuNhap {
	private PhieuNhap phieuNhap;
	private SanPham sanPham;
	private int soLuong;
	private double donGia;

	public ChiTietPhieuNhap() {
		super();
	}

	public ChiTietPhieuNhap(PhieuNhap phieuNhap, SanPham sanPham) {
		super();
		this.phieuNhap = phieuNhap;
		this.sanPham = sanPham;
	}

	public ChiTietPhieuNhap(PhieuNhap phieuNhap, SanPham sanPham, int soLuong, double donGia) {
		super();
		this.phieuNhap = phieuNhap;
		this.sanPham = sanPham;
		this.soLuong = soLuong;
		this.donGia = donGia;
	}

	public PhieuNhap getPhieuNhap() {
		return phieuNhap;
	}

	public void setPhieuNhap(PhieuNhap phieuNhap) {
		this.phieuNhap = phieuNhap;
	}

	public SanPham getSanPham() {
		return sanPham;
	}

	public void setSanPham(SanPham sanPham) {
		this.sanPham = sanPham;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public double getDonGia() {
		return donGia;
	}

	public void setDonGia(double donGia) {
		this.donGia = donGia;
	}

	@Override
	public int hashCode() {
		return Objects.hash(phieuNhap, sanPham);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChiTietPhieuNhap other = (ChiTietPhieuNhap) obj;
		return Objects.equals(phieuNhap, other.phieuNhap) && Objects.equals(sanPham, other.sanPham);
	}

	public Object[] addToTable() {
		return new Object[] {};
	}

}
