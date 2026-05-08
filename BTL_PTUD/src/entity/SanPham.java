package entity;

import java.time.LocalDate;
import java.util.Objects;

public class SanPham {
	private String maSanPham;
	private String tenSanPham;
	private String congDung;
	private String thanhPhan;
	private Integer hanSuDung;
	private double giaThanh;
	private String noiSanXuat;
	private LoaiSanPham loaiSanPham;
	private KhuyenMai khuyenMai;
	private Thue thue;
	private NhaCungCap nhaCungCap;
	private String donViTinh;
	private boolean trangThai;
	private String hinhAnh;

	public SanPham(String maSanPham, String tenSanPham, String maLoaiSanPham, String congDung, String thanhPhan,
			int hanSuDung, double giaThanh, boolean trangThai, String hinhAnh) {
		setMaSanPham(maSanPham);
		setTenSanPham(tenSanPham);
		setLoaiSP(new LoaiSanPham(maLoaiSanPham));
		setCongDung(congDung);
		setThanhPhan(thanhPhan);
		setHanSuDungThang(hanSuDung);
		setGiaThanh(giaThanh);
		setTrangThai(trangThai);
		setHinhAnh(hinhAnh);
	}

	public SanPham(String maSanPham, String tenSanPham, NhaCungCap nhaCungCap, LoaiSanPham loaiSanPham,
			double giaThanh, String donViTinh, String hanSuDung, String hinhAnh) {
		setMaSanPham(maSanPham);
		setTenSanPham(tenSanPham);
		setNhaCungCap(nhaCungCap);
		setLoaiSP(loaiSanPham);
		setGiaThanh(giaThanh);
		setDonViTinh(donViTinh);
		setHanSuDung(hanSuDung);
		setHinhAnh(hinhAnh);
	}

	public SanPham(String maSanPham) {
		setMaSanPham(maSanPham);
	}

	public SanPham() {
	}

	public String getMaSanPham() {
		return maSanPham;
	}

	public String getMaSP() {
		return maSanPham;
	}

	public void setMaSanPham(String maSanPham) {
		maSanPham = normalizeRequired(maSanPham);
		if (isBlank(maSanPham)) {
			throw new IllegalArgumentException("Ma san pham khong duoc de trong");
		}
		this.maSanPham = maSanPham;
	}

	public void setMaSP(String maSP) {
		setMaSanPham(maSP);
	}

	public String getTenSanPham() {
		return tenSanPham;
	}

	public String getTenSP() {
		return tenSanPham;
	}

	public void setTenSanPham(String tenSanPham) {
		tenSanPham = normalizeRequired(tenSanPham);
		if (isBlank(tenSanPham)) {
			throw new IllegalArgumentException("Ten san pham khong duoc de trong");
		}
		this.tenSanPham = tenSanPham;
	}

	public void setTenSP(String tenSP) {
		setTenSanPham(tenSP);
	}

	public LoaiSanPham getLoaiSanPham() {
		return loaiSanPham;
	}

	public LoaiSanPham getLoaiSP() {
		return loaiSanPham;
	}

	public void setLoaiSanPham(LoaiSanPham loaiSanPham) {
		this.loaiSanPham = loaiSanPham;
	}

	public void setLoaiSP(LoaiSanPham loaiSP) {
		this.loaiSanPham = loaiSP;
	}

	public String getCongDung() {
		return congDung;
	}

	public void setCongDung(String congDung) {
		this.congDung = normalizeOptional(congDung);
	}

	public String getThanhPhan() {
		return thanhPhan;
	}

	public void setThanhPhan(String thanhPhan) {
		this.thanhPhan = normalizeOptional(thanhPhan);
	}

	public int getHanSuDungThang() {
		return hanSuDung == null ? 0 : hanSuDung;
	}

	public void setHanSuDungThang(int hanSuDung) {
		if (hanSuDung <= 0) {
			throw new IllegalArgumentException("Han su dung phai lon hon 0");
		}
		this.hanSuDung = hanSuDung;
	}

	public String getHanSuDung() {
		return hanSuDung == null ? null : String.valueOf(hanSuDung);
	}

	public void setHanSuDung(String hanSuDung) {
		hanSuDung = normalizeOptional(hanSuDung);
		if (isBlank(hanSuDung)) {
			this.hanSuDung = null;
			return;
		}
		try {
			setHanSuDungThang(Integer.parseInt(hanSuDung));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Han su dung phai la so nguyen duong");
		}
	}

	public double getGiaThanh() {
		return giaThanh;
	}

	public double getGiaBan() {
		return giaThanh;
	}

	public void setGiaThanh(double giaThanh) {
		if (giaThanh < 0) {
			throw new IllegalArgumentException("Gia ban khong duoc am");
		}
		this.giaThanh = giaThanh;
	}

	public void setGiaBan(double giaBan) {
		setGiaThanh(giaBan);
	}

	public String getNoiSanXuat() {
		return noiSanXuat;
	}

	public void setNoiSanXuat(String noiSanXuat) {
		this.noiSanXuat = normalizeOptional(noiSanXuat);
	}

	public KhuyenMai getKhuyenMai() {
		return khuyenMai;
	}

	public void setKhuyenMai(KhuyenMai khuyenMai) {
		this.khuyenMai = khuyenMai;
	}

	// ==================== GIÁ + KHUYẾN MÃI ====================

	// Khuyến mãi của SP có đang hiệu lực hôm nay không.
	public boolean coKhuyenMai() {
		if (khuyenMai == null || !khuyenMai.isTrangThai()) return false;
		if (khuyenMai.getPhanTramGG() <= 0) return false;
		LocalDate today = LocalDate.now();
		if (khuyenMai.getNgayBatDau() != null && today.isBefore(khuyenMai.getNgayBatDau())) return false;
		if (khuyenMai.getNgayKetThuc() != null && today.isAfter(khuyenMai.getNgayKetThuc())) return false;
		return true;
	}

	// Phần trăm KM hiệu lực; 0 nếu không có.
	public double getPhanTramGiam() {
		return coKhuyenMai() ? khuyenMai.getPhanTramGG() : 0.0;
	}

	// Giá bán cuối cùng sau khi áp dụng KM hiệu lực; trả về giá gốc nếu không có
	// KM. Làm tròn xuống đồng (Math.floor).
	public double getGiaSauKM() {
		double pct = getPhanTramGiam();
		if (pct <= 0) return giaThanh;
		return Math.floor(giaThanh * (100 - pct) / 100.0);
	}

	public Thue getThue() {
		return thue;
	}

	public void setThue(Thue thue) {
		this.thue = thue;
	}

	public NhaCungCap getNhaCungCap() {
		return nhaCungCap;
	}

	public NhaCungCap getnCC() {
		return nhaCungCap;
	}

	public void setNhaCungCap(NhaCungCap nhaCungCap) {
		this.nhaCungCap = nhaCungCap;
	}

	public void setnCC(NhaCungCap nCC) {
		this.nhaCungCap = nCC;
	}

	public String getDonViTinh() {
		return donViTinh;
	}

	public void setDonViTinh(String donViTinh) {
		this.donViTinh = normalizeOptional(donViTinh);
	}

	public boolean isTrangThai() {
		return trangThai;
	}

	public void setTrangThai(boolean trangThai) {
		this.trangThai = trangThai;
	}
	
	public String getHinhAnh() {
		return hinhAnh;
	}

	public String getLinkAnh() {
		return hinhAnh;
	}

	public void setHinhAnh(String hinhAnh) {
		this.hinhAnh = normalizeOptional(hinhAnh);
	}

	public void setLinkAnh(String linkAnh) {
		setHinhAnh(linkAnh);
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

	@Override
	public int hashCode() {
		return Objects.hash(maSanPham);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SanPham other = (SanPham) obj;
		return Objects.equals(maSanPham, other.maSanPham);
	}

	@Override
	public String toString() {
		return "SanPham{" + "maSanPham='" + maSanPham + '\'' + ", tenSanPham='" + tenSanPham + '\''
				+ ", giaThanh=" + giaThanh + ", hanSuDung=" + hanSuDung + ", trangThai=" + trangThai + '}';
	}

}
