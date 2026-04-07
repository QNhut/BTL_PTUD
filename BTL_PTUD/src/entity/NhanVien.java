package entity;

import java.util.Objects;
import java.util.regex.Pattern;

public class NhanVien {
	private String maNV;
	private String tenNV;
	private boolean gioiTinh;
	private String sDT;
	private String diaChi;
	private String email;
	private String cCCD;
	private String vaiTro;
	private double tienLuong;
	private String caLam;
	private String linkAnh;

	private static final Pattern REGEX_SDT = Pattern.compile("\\d{10}");
	private static final Pattern REGEX_CCCD = Pattern.compile("\\d{12}");
	private static final Pattern REGEX_EMAIL = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

	public NhanVien(String maNV, String tenNV, boolean gioiTinh, String sDT, String diaChi, String email, String cCCD,
			String vaiTro, double tienLuong, String caLam, String linkAnh) {
		setMaNV(maNV);
		setTenNV(tenNV);
		setGioiTinh(gioiTinh);
		setsDT(sDT);
		setDiaChi(diaChi);
		setEmail(email);
		setcCCD(cCCD);
		setVaiTro(vaiTro);
		setTienLuong(tienLuong);
		setCaLam(caLam);
		setLinkAnh(linkAnh);
	}

	public NhanVien(String maNV) {
		setMaNV(maNV);
	}

	public NhanVien() {
	}

	public String getMaNV() {
		return maNV;
	}

	public void setMaNV(String maNV) {
		maNV = normalizeRequired(maNV);
		if (isBlank(maNV)) {
			throw new IllegalArgumentException("Ma nhan vien khong duoc de trong");
		}
		this.maNV = maNV;
	}

	public String getTenNV() {
		return tenNV;
	}

	public void setTenNV(String tenNV) {
		tenNV = normalizeRequired(tenNV);
		if (isBlank(tenNV)) {
			throw new IllegalArgumentException("Ten nhan vien khong duoc de trong");
		}
		this.tenNV = tenNV;
	}

	public boolean isGioiTinh() {
		return gioiTinh;
	}

	public void setGioiTinh(boolean gioiTinh) {
		this.gioiTinh = gioiTinh;
	}

	public String getsDT() {
		return sDT;
	}

	public String getSoDienThoai() {
		return sDT;
	}

	public void setsDT(String sDT) {
		sDT = normalizeRequired(sDT);
		if (isBlank(sDT) || !REGEX_SDT.matcher(sDT).matches()) {
			throw new IllegalArgumentException("So dien thoai phai gom 10 chu so");
		}
		this.sDT = sDT;
	}

	public void setSoDienThoai(String soDienThoai) {
		setsDT(soDienThoai);
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
		email = normalizeOptional(email);
		if (!isBlank(email) && !REGEX_EMAIL.matcher(email).matches()) {
			throw new IllegalArgumentException("Email khong hop le");
		}
		this.email = email;
	}

	public String getcCCD() {
		return cCCD;
	}

	public String getCCCD() {
		return cCCD;
	}

	public void setcCCD(String cCCD) {
		cCCD = normalizeRequired(cCCD);
		if (isBlank(cCCD) || !REGEX_CCCD.matcher(cCCD).matches()) {
			throw new IllegalArgumentException("CCCD phai gom 12 chu so");
		}
		this.cCCD = cCCD;
	}

	public void setCCCD(String cccd) {
		setcCCD(cccd);
	}

	public String getVaiTro() {
		return vaiTro;
	}

	public void setVaiTro(String vaiTro) {
		this.vaiTro = normalizeOptional(vaiTro);
	}

	public double getTienLuong() {
		return tienLuong;
	}

	public void setTienLuong(double tienLuong) {
		if (tienLuong < 0) {
			throw new IllegalArgumentException("Tien luong khong duoc am");
		}
		this.tienLuong = tienLuong;
	}

	public String getCaLam() {
		return caLam;
	}

	public void setCaLam(String caLam) {
		this.caLam = normalizeOptional(caLam);
	}

	public String getLinkAnh() {
		return linkAnh;
	}

	public void setLinkAnh(String linkAnh) {
		this.linkAnh = normalizeOptional(linkAnh);
	}

	@Override
	public int hashCode() {
		return Objects.hash(maNV);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NhanVien other = (NhanVien) obj;
		return Objects.equals(maNV, other.maNV);
	}

	@Override
	public String toString() {
		return "NhanVien{" + "maNV='" + maNV + '\'' + ", tenNV='" + tenNV + '\'' + ", gioiTinh=" + gioiTinh
				+ ", sDT='" + maskPhone(sDT) + '\'' + ", diaChi='" + diaChi + '\'' + ", email='" + email + '\''
				+ ", cCCD='" + maskCCCD(cCCD) + '\'' + ", vaiTro='" + vaiTro + '\'' + ", tienLuong=" + tienLuong
				+ ", caLam='" + caLam + '\'' + ", linkAnh='" + linkAnh + '\'' + '}';
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

	private static String maskCCCD(String value) {
		if (isBlank(value) || value.length() < 4) {
			return value;
		}
		return "********" + value.substring(value.length() - 4);
	}

}
