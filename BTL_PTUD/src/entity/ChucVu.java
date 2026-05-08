package entity;

import java.util.Objects;

public class ChucVu {
	private String maChucVu;
	private String tenChucVu;
	private String moTa;

	public ChucVu(String maChucVu, String tenChucVu, String moTa) {
		setMaChucVu(maChucVu);
		setTenChucVu(tenChucVu);
		setMoTa(moTa);
	}

	public ChucVu(String maChucVu) {
		setMaChucVu(maChucVu);
	}

	public ChucVu() {
	}

	public String getMaChucVu() {
		return maChucVu;
	}

	public void setMaChucVu(String maChucVu) {
		maChucVu = normalizeRequired(maChucVu);
		if (isBlank(maChucVu)) {
			throw new IllegalArgumentException("Ma chuc vu khong duoc de trong");
		}
		this.maChucVu = maChucVu;
	}

	public String getTenChucVu() {
		return tenChucVu;
	}

	public void setTenChucVu(String tenChucVu) {
		tenChucVu = normalizeRequired(tenChucVu);
		if (isBlank(tenChucVu)) {
			throw new IllegalArgumentException("Ten chuc vu khong duoc de trong");
		}
		this.tenChucVu = tenChucVu;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = normalizeOptional(moTa);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maChucVu);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChucVu other = (ChucVu) obj;
		return Objects.equals(maChucVu, other.maChucVu);
	}

	@Override
	public String toString() {
		return "ChucVu{" + "maChucVu='" + maChucVu + '\'' + ", tenChucVu='" + tenChucVu + '\'' + ", moTa='"
				+ moTa + '\'' + '}';
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
