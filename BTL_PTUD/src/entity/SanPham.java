package entity;

import java.util.Objects;

public class SanPham {
	private String maSP, tenSP;
	private NhaCungCap nCC;
	private LoaiSanPham loaiSP;
	private double giaBan;
	private int soLuong;
	private String donViTinh, hanSuDung, hinhAnh;

	public SanPham() {
	}

	public SanPham(String maSP) {
		this.maSP = maSP;
	}

	public SanPham(String maSP, String tenSP, NhaCungCap nCC, LoaiSanPham loaiSP, double giaBan, int soLuong,
			String donViTinh, String hanSuDung, String hinhAnh) {
		this.maSP = maSP;
		this.tenSP = tenSP;
		this.nCC = nCC;
		this.loaiSP = loaiSP;
		this.giaBan = giaBan;
		this.soLuong = soLuong;
		this.donViTinh = donViTinh;
		this.hanSuDung = hanSuDung;
		this.hinhAnh = hinhAnh;
	}

	public String getMaSP() {
		return maSP;
	}

	public void setMaSP(String maSP) {
		this.maSP = maSP;
	}

	public String getTenSP() {
		return tenSP;
	}

	public void setTenSP(String tenSP) {
		this.tenSP = tenSP;
	}

	public NhaCungCap getnCC() {
		return nCC;
	}

	public void setnCC(NhaCungCap nCC) {
		this.nCC = nCC;
	}

	public LoaiSanPham getLoaiSP() {
		return loaiSP;
	}

	public void setLoaiSP(LoaiSanPham loaiSP) {
		this.loaiSP = loaiSP;
	}

	public double getGiaBan() {
		return giaBan;
	}

	public void setGiaBan(double giaBan) {
		this.giaBan = giaBan;
	}

	public int getSoLuong() {
		return soLuong;
	}

	public void setSoLuong(int soLuong) {
		this.soLuong = soLuong;
	}

	public String getDonViTinh() {
		return donViTinh;
	}

	public void setDonViTinh(String donViTinh) {
		this.donViTinh = donViTinh;
	}

	public String getHanSuDung() {
		return hanSuDung;
	}

	public void setHanSuDung(String hanSuDung) {
		this.hanSuDung = hanSuDung;
	}

	public String getHinhAnh() {
		return hinhAnh;
	}

	public void setHinhAnh(String hinhAnh) {
		this.hinhAnh = hinhAnh;
	}

	@Override
	public boolean equals(Object o) {
	    if (this == o) return true;
	    if (!(o instanceof SanPham)) return false;

	    SanPham sp = (SanPham) o;
	    return this.getTenSP().equals(sp.getTenSP());
	}

	@Override
	public int hashCode() {
	    return getTenSP().hashCode();
	}

}
