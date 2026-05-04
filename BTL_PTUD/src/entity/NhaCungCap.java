package entity;

import java.util.Objects;
import java.util.regex.Pattern;

public class NhaCungCap {
	private String maNhaCungCap;
	private String tenNhaCungCap;
	private String diaChi;
	private String email;
	private String soDienThoai;
	private String moTa;
	private boolean trangThai;

	private static final Pattern REGEX_SDT = Pattern.compile("\\d{9,12}");
	private static final Pattern REGEX_EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	public NhaCungCap(String maNhaCungCap, String tenNhaCungCap, String diaChi, String email, String soDienThoai,
			String moTa, boolean trangThai) {
		setMaNhaCungCap(maNhaCungCap);
		setTenNhaCungCap(tenNhaCungCap);
		setDiaChi(diaChi);
		setEmail(email);
		setSoDienThoai(soDienThoai);
		setMoTa(moTa);
		setTrangThai(trangThai);
	}

	public NhaCungCap(String maNhaCungCap, String tenNhaCungCap, String diaChi, String soDienThoai, String email) {
		this(maNhaCungCap, tenNhaCungCap, diaChi, email, soDienThoai, null, true);
	}

	public NhaCungCap(String maNhaCungCap) {
		setMaNhaCungCap(maNhaCungCap);
	}

	public NhaCungCap() {
	}

	public String getMaNhaCungCap() {
		return maNhaCungCap;
	}

	public String getMaNCC() {
		return maNhaCungCap;
	}

	public void setMaNhaCungCap(String maNhaCungCap) {
		maNhaCungCap = normalizeRequired(maNhaCungCap);
		if (isBlank(maNhaCungCap)) {
			throw new IllegalArgumentException("Ma nha cung cap khong duoc de trong");
		}
		this.maNhaCungCap = maNhaCungCap;
	}

	public void setMaNCC(String maNCC) {
		setMaNhaCungCap(maNCC);
	}

	public String getTenNhaCungCap() {
		return tenNhaCungCap;
	}

	public String getTenNCC() {
		return tenNhaCungCap;
	}

	public void setTenNhaCungCap(String tenNhaCungCap) {
		tenNhaCungCap = normalizeRequired(tenNhaCungCap);
		if (isBlank(tenNhaCungCap)) {
			throw new IllegalArgumentException("Ten nha cung cap khong duoc de trong");
		}
		this.tenNhaCungCap = tenNhaCungCap;
	}

	public void setTenNCC(String tenNCC) {
		setTenNhaCungCap(tenNCC);
	}

	public String getDiaChi() {
		return diaChi;
	}

	public void setDiaChi(String diaChi) {
		this.diaChi = normalizeOptional(diaChi);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		email = normalizeRequired(email);
		if (isBlank(email) || !REGEX_EMAIL.matcher(email).matches()) {
			throw new IllegalArgumentException("Email khong hop le");
		}
		this.email = email;
	}

	public String getSoDienThoai() {
		return soDienThoai;
	}

	public void setSoDienThoai(String soDienThoai) {
		soDienThoai = normalizeRequired(soDienThoai);
		if (isBlank(soDienThoai) || !REGEX_SDT.matcher(soDienThoai).matches()) {
			throw new IllegalArgumentException("So dien thoai nha cung cap khong hop le");
		}
		this.soDienThoai = soDienThoai;
	}

	public String getMoTa() {
		return moTa;
	}

	public void setMoTa(String moTa) {
		this.moTa = normalizeOptional(moTa);
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	@Override
	public int hashCode() {
		return Objects.hash(maNhaCungCap);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NhaCungCap other = (NhaCungCap) obj;
		return Objects.equals(maNhaCungCap, other.maNhaCungCap);
	}

	@Override
	public String toString() {
		return "NhaCungCap{" + "maNhaCungCap='" + maNhaCungCap + '\'' + ", tenNhaCungCap='" + tenNhaCungCap
				+ '\'' + ", diaChi='" + diaChi + '\'' + ", email='" + email + '\'' + ", soDienThoai='"
				+ soDienThoai + '\'' + ", moTa='" + moTa + '\'' + ", trangThai=" + trangThai + '}';
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