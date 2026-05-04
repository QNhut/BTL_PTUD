package entity;

import java.util.Objects;

public class LoaiSanPham {
	private String maLoaiSanPham;
	private String tenLoaiSanPham;
	private String moTa;

	public LoaiSanPham(String maLoaiSanPham, String tenLoaiSanPham, String moTa) {
		setMaLoaiSanPham(maLoaiSanPham);
		setTenLoaiSanPham(tenLoaiSanPham);
		setMoTa(moTa);
	}

	public LoaiSanPham(String maLoaiSanPham) {
		setMaLoaiSanPham(maLoaiSanPham);
	}

	public LoaiSanPham() {
	}

	public String getMaLoaiSanPham() {
		return maLoaiSanPham;
	}

	public String getMaLoaiSP() {
		return maLoaiSanPham;
	}

	public void setMaLoaiSanPham(String maLoaiSanPham) {
		maLoaiSanPham = normalizeRequired(maLoaiSanPham);
		if (isBlank(maLoaiSanPham)) {
			throw new IllegalArgumentException("Ma loai san pham khong duoc de trong");
		}
		this.maLoaiSanPham = maLoaiSanPham;
	}

	public void setMaLoaiSP(String maLoaiSP) {
		setMaLoaiSanPham(maLoaiSP);
	}

	public String getTenLoaiSanPham() {
		return tenLoaiSanPham;
	}

	public String getTenLoaiSP() {
		return tenLoaiSanPham;
	}

	public void setTenLoaiSanPham(String tenLoaiSanPham) {
		tenLoaiSanPham = normalizeRequired(tenLoaiSanPham);
		if (isBlank(tenLoaiSanPham)) {
			throw new IllegalArgumentException("Ten loai san pham khong duoc de trong");
		}
		this.tenLoaiSanPham = tenLoaiSanPham;
	}

	public void setTenLoaiSP(String tenLoaiSP) {
		setTenLoaiSanPham(tenLoaiSP);
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = normalizeOptional(moTa);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maLoaiSanPham);
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
		return Objects.equals(maLoaiSanPham, other.maLoaiSanPham);
	}

	@Override
	public String toString() {
		return "LoaiSanPham{" + "maLoaiSanPham='" + maLoaiSanPham + '\'' + ", tenLoaiSanPham='"
				+ tenLoaiSanPham + '\'' + ", moTa='" + moTa + '\'' + '}';
	}

	private static boolean isBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	private static String normalizeRequired(String value) {
		return value == null ? null : value.trim();
	}

	private static String normalizeOptional(String value) {
		if (value == null) {
			return null;
		}
		String normalized = value.trim();
		return normalized.isEmpty() ? null : normalized;
	}

}
