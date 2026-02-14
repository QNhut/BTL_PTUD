package entity;

import java.util.Objects;

public class LoaiSanPham {
	private String maLoaiSP, tenLoaiSP, moTa;

	public LoaiSanPham() {
	}

	public LoaiSanPham(String maLoaiSP) {
		this.maLoaiSP = maLoaiSP;
	}

	public LoaiSanPham(String maLoaiSP, String tenLoaiSP, String moTa) {
		this.maLoaiSP = maLoaiSP;
		this.tenLoaiSP = tenLoaiSP;
		this.moTa = moTa;
	}

	public String getMaLoaiSP() {
		return maLoaiSP;
	}

	public void setMaLoaiSP(String maLoaiSP) {
		this.maLoaiSP = maLoaiSP;
	}

	public String getTenLoaiSP() {
		return tenLoaiSP;
	}

	public void setTenLoaiSP(String tenLoaiSP) {
		this.tenLoaiSP = tenLoaiSP;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = moTa;
	}

	@Override
	public int hashCode() {
		return Objects.hash(maLoaiSP);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoaiSanPham other = (LoaiSanPham) obj;
		return Objects.equals(maLoaiSP, other.maLoaiSP);
	}

	public Object[] addToTable() {
		return new Object[] {};
	}
}
