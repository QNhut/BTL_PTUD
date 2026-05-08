package entity;

import java.util.Objects;

public class Thue {
	private String maThue;
	private String tenThue;
	private double phanTramThue;
	private String moTa;

	public Thue(String maThue, String tenThue, double phanTramThue, String moTa) {
		setMaThue(maThue);
		setTenThue(tenThue);
		setPhanTramThue(phanTramThue);
		setMoTa(moTa);
	}

	public Thue(String maThue) {
		setMaThue(maThue);
	}

	public Thue() {
	}

	public String getMaThue() {
		return maThue;
	}

	public void setMaThue(String maThue) {
		maThue = normalizeRequired(maThue);
		if (isBlank(maThue)) {
			throw new IllegalArgumentException("Ma thue khong duoc de trong");
		}
		this.maThue = maThue;
	}

	public String getTenThue() {
		return tenThue;
	}

	public void setTenThue(String tenThue) {
		tenThue = normalizeRequired(tenThue);
		if (isBlank(tenThue)) {
			throw new IllegalArgumentException("Ten thue khong duoc de trong");
		}
		this.tenThue = tenThue;
	}

	public double getPhanTramThue() {
		return phanTramThue;
	}

	public void setPhanTramThue(double phanTramThue) {
		if (phanTramThue < 0) {
			throw new IllegalArgumentException("Phan tram thue khong duoc am");
		}
		this.phanTramThue = phanTramThue;
	}

	public String getMoTa() {
		return moTa;
	}

	public String getGhiChu() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = normalizeOptional(moTa);
	}

	public void setGhiChu(String ghiChu) {
		setMoTa(ghiChu);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maThue);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Thue other = (Thue) obj;
		return Objects.equals(maThue, other.maThue);
	}

	@Override
	public String toString() {
		return "Thue{" + "maThue='" + maThue + '\'' + ", tenThue='" + tenThue + '\'' + ", phanTramThue="
				+ phanTramThue + ", moTa='" + moTa + '\'' + '}';
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
