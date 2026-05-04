package entity;

import java.util.Objects;
import java.util.regex.Pattern;

public class KhachHang {
	private String maKhachHang;
	private String tenKhachHang;
	private String soDienThoai;
	private String email;
	private boolean gioiTinh;
	private int diemTichLuy;
	private boolean trangThai;

	private static final Pattern REGEX_SDT = Pattern.compile("\\d{10}");
	private static final Pattern REGEX_EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	public KhachHang(String maKhachHang, String tenKhachHang, String soDienThoai, String email, boolean gioiTinh,
			int diemTichLuy, boolean trangThai) {
		setMaKhachHang(maKhachHang);
		setTenKhachHang(tenKhachHang);
		setSoDienThoai(soDienThoai);
		setEmail(email);
		setGioiTinh(gioiTinh);
		setDiemTichLuy(diemTichLuy);
		setTrangThai(trangThai);
	}

	public KhachHang(String maKhachHang) {
		setMaKhachHang(maKhachHang);
	}

	public KhachHang() {
	}

	public String getMaKhachHang() {
		return maKhachHang;
	}

	public String getMaKH() {
		return maKhachHang;
	}

	public String getTenKhachHang() {
		return tenKhachHang;
	}

	public String getTenKH() {
		return tenKhachHang;
	}

	public void setTenKhachHang(String tenKhachHang) {
		tenKhachHang = normalizeRequired(tenKhachHang);
		if (isBlank(tenKhachHang)) {
			throw new IllegalArgumentException("Ten khach hang khong duoc de trong");
		}
		this.tenKhachHang = tenKhachHang;
	}

	public void setTenKH(String tenKH) {
		setTenKhachHang(tenKH);
	}

	public String getSoDienThoai() {
		return soDienThoai;
	}

	public void setSoDienThoai(String soDienThoai) {
		soDienThoai = normalizeRequired(soDienThoai);
		if (isBlank(soDienThoai) || !REGEX_SDT.matcher(soDienThoai).matches()) {
			throw new IllegalArgumentException("So dien thoai phai gom 10 chu so");
		}
		this.soDienThoai = soDienThoai;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		email = normalizeOptional(email);
		if (!isBlank(email) && !REGEX_EMAIL.matcher(email).matches()) {
			throw new IllegalArgumentException("Email khong hop le");
		}
		this.email = email;
	}

	public boolean isGioiTinh() {
		return gioiTinh;
	}

	public void setGioiTinh(boolean gioiTinh) {
		this.gioiTinh = gioiTinh;
	}

	public int getDiemTichLuy() {
		return diemTichLuy;
	}

	public void setDiemTichLuy(int diemTichLuy) {
		if (diemTichLuy < 0) {
			throw new IllegalArgumentException("Diem tich luy khong duoc am");
		}
		this.diemTichLuy = diemTichLuy;
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}

	public void setMaKhachHang(String maKhachHang) {
		maKhachHang = normalizeRequired(maKhachHang);
		if (isBlank(maKhachHang)) {
			throw new IllegalArgumentException("Ma khach hang khong duoc de trong");
		}
		this.maKhachHang = maKhachHang;
	}

	public void setMaKH(String maKH) {
		setMaKhachHang(maKH);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maKhachHang);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KhachHang other = (KhachHang) obj;
		return Objects.equals(maKhachHang, other.maKhachHang);
	}

	@Override
	public String toString() {
		return "KhachHang{" + "maKhachHang='" + maKhachHang + '\'' + ", tenKhachHang='" + tenKhachHang + '\''
				+ ", soDienThoai='" + maskPhone(soDienThoai) + '\'' + ", email='" + email + '\'' + ", gioiTinh="
				+ gioiTinh + ", diemTichLuy=" + diemTichLuy + ", trangThai=" + trangThai + '}';
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

	private static String maskPhone(String value) {
		if (isBlank(value) || value.length() < 4) {
			return value;
		}
		return "******" + value.substring(value.length() - 4);
	}

}
