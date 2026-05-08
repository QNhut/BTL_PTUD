package service;

import java.time.LocalDate;
import java.util.List;

import dao.KeSanPham_DAO;
import dao.LoSanPham_DAO;
import dao.NhaCungCap_DAO;
import dao.PhieuNhap_DAO;
import dao.SanPham_DAO;
import entity.ChiTietPhieuNhap;
import entity.KeSanPham;
import entity.LoSanPham;
import entity.NhaCungCap;
import entity.PhieuNhap;
import entity.SanPham;

// Service quản lý lô sản phẩm.
// Luồng chính:
// 1. Nhập hàng → taoLoTuPhieuNhap() → quy đổi đơn vị + tính HSD → lưu DB
// 2. Bán hàng  → truKho() → FIFO theo HSD → trừ từ lô cũ nhất
public class LoSanPham_Service {

	private final LoSanPham_DAO loDAO = new LoSanPham_DAO();
	private final SanPham_DAO spDAO = new SanPham_DAO();
	private final DonViTinhConverter converter = new DonViTinhConverter();
	private final PhieuNhap_DAO pnDAO = new PhieuNhap_DAO();
	private final KeSanPham_DAO keDAO = new KeSanPham_DAO();
	private final NhaCungCap_DAO nccDAO = new NhaCungCap_DAO();

	// Lấy toàn bộ lô sản phẩm (qua DAO).
	public List<LoSanPham> getDSLoSanPham() {
		return loDAO.getDSLoSanPham();
	}

	// ==================== NHẬP HÀNG ====================

	// Tạo lô sản phẩm từ chi tiết phiếu nhập.
	// - Quy đổi số lượng từ đơn vị file (Hộp/Vỉ/Thùng) → đơn vị chuẩn SP
	// - Tính HSD = ngayNhap + sanPham.hanSuDung (tháng)
	// Ví dụ: Nhập 5 Hộp Paracetamol (đơn vị chuẩn: Viên)
	// → soLuong = 5 × 100 = 500 Viên
	// → hanSuDung = ngayNhap + 24 tháng
	public LoSanPham taoLoTuPhieuNhap(ChiTietPhieuNhap ct, PhieuNhap phieuNhap,
			String maLoSP, KeSanPham keSP) {

		SanPham spFile = ct.getSanPham();
		SanPham spDB = spDAO.laySanPhamTheoMa(spFile.getMaSanPham());
		if (spDB == null) {
			throw new IllegalArgumentException("Không tìm thấy SP: " + spFile.getMaSanPham());
		}

		// Quy đổi: đơn vị nhập → đơn vị chuẩn SP
		String donViNhap = spFile.getDonViTinh();  // từ file Excel
		String donViChuan = spDB.getDonViTinh();    // từ DB
		int soLuongQuyDoi = quyDoi(ct.getSoLuong(), donViNhap, donViChuan);

		// Tính HSD: ngày nhập + số tháng hạn sử dụng
		LocalDate ngayNhap = phieuNhap.getNgayNhap() != null ? phieuNhap.getNgayNhap() : LocalDate.now();
		int hanThang = spDB.getHanSuDungThang();
		LocalDate hanSuDung = tinhHanSuDung(ngayNhap, hanThang > 0 ? hanThang : null);

		return new LoSanPham(maLoSP, spDB, phieuNhap, keSP,
				soLuongQuyDoi, donViChuan, hanSuDung, true);
	}

	// Lưu lô sản phẩm vào DB
	public boolean luuLo(LoSanPham lo) {
		return loDAO.them(lo);
	}

	// ==================== BÁN HÀNG (TRỪ KHO) ====================

	// Trừ kho theo FIFO (lô cũ nhất trước, sắp hết hạn trước).
	// Ví dụ: Bán 20 Viên Paracetamol
	// - Lô 1 (HSD gần): còn 15 → trừ 15, còn lại 0
	// - Lô 2 (HSD xa): còn 500 → trừ 5, còn lại 495
	// @param maSP       Mã sản phẩm
	// @param soLuongBan Số lượng bán (đơn vị bán)
	// @param donViBan   Đơn vị bán (Viên, Vỉ, Gói...)
	// @return true nếu trừ đủ, false nếu không đủ hàng
	public boolean truKho(String maSP, int soLuongBan, String donViBan) {
		SanPham sp = spDAO.laySanPhamTheoMa(maSP);
		if (sp == null) return false;

		// Quy đổi số lượng bán → đơn vị chuẩn
		int canTru = quyDoi(soLuongBan, donViBan, sp.getDonViTinh());

		// Kiểm tra đủ hàng không
		int tongTon = loDAO.layTongSoLuongTonTheoMaSanPham(maSP);
		if (tongTon < canTru) return false;

		// Trừ kho FEFO trực tiếp xuống DB
		loDAO.giamSoLuongTheoSanPham(maSP, canTru);
		return true;
	}

	// ==================== HELPERS ====================

	private int quyDoi(int soLuong, String donViGoc, String donViDich) {
		if (donViGoc == null || donViDich == null) return soLuong;
		try {
			return (int) Math.round(soLuong * converter.layHeSoQuyDoi(donViGoc, donViDich));
		} catch (IllegalArgumentException e) {
			return soLuong; // Không quy đổi được → giữ nguyên
		}
	}

	// Tính HSD: ngày nhập + số tháng.
	// Nếu hanSuDungThang = null hoặc 0 → không giới hạn (return null)
	private LocalDate tinhHanSuDung(LocalDate ngayNhap, Integer hanSuDungThang) {
		if (hanSuDungThang == null || hanSuDungThang <= 0) return null;
		return ngayNhap.plusMonths(hanSuDungThang);
	}

	// ==================== CHI TIẾT SẢN PHẨM ====================

	public static class TrangThaiLo {
		public static final String HET_HAN    = "HET_HAN";
		public static final String SAP_HET_HAN = "SAP_HET_HAN"; // trong vòng 90 ngày
		public static final String CON_HAN    = "CON_HAN";
	}

	// Số ngày còn lại đến hết hạn; null nếu HSD không xác định
	public static Long soNgayConLai(LocalDate hanSuDung) {
		if (hanSuDung == null) return null;
		return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), hanSuDung);
	}

	// Trạng thái lô: HET_HAN / SAP_HET_HAN (≤90 ngày) / CON_HAN
	public static String trangThaiLo(LocalDate hanSuDung) {
		if (hanSuDung == null) return TrangThaiLo.CON_HAN;
		long ngay = java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), hanSuDung);
		if (ngay < 0)  return TrangThaiLo.HET_HAN;
		if (ngay <= 90) return TrangThaiLo.SAP_HET_HAN;
		return TrangThaiLo.CON_HAN;
	}

	// Lấy tất cả lô của một sản phẩm (kể cả hết hạn).
	// Enrich thêm thông tin PhieuNhap (ngày nhập, NCC) và KeSanPham (tên kệ, vị trí).
	public List<LoSanPham> layDanhSachLoTheoSP(String maSanPham) {
		if (maSanPham == null || maSanPham.isBlank()) return new java.util.ArrayList<>();
		List<LoSanPham> ds = loDAO.layTheoMaSanPham(maSanPham);
		for (LoSanPham lo : ds) {
			// Enrich PhieuNhap: lấy ngayNhap và NCC
			try {
				if (lo.getPhieuNhap() != null) {
					PhieuNhap pn = pnDAO.layPNTheoMa(lo.getPhieuNhap().getMaPhieuNhap());
					if (pn != null) {
						// Load tên NCC
						if (pn.getNhaCungCap() != null) {
							try {
								NhaCungCap ncc = nccDAO.layNCCTheoMaNCC(pn.getNhaCungCap().getMaNhaCungCap());
								if (ncc != null) pn.setNhaCungCap(ncc);
							} catch (Exception ignored) {}
						}
						lo.setPhieuNhap(pn);
					}
				}
			} catch (Exception ignored) {}
			// Enrich KeSanPham: lấy tên kệ và vị trí
			try {
				if (lo.getKeSanPham() != null) {
					KeSanPham ke = keDAO.layTheoMa(lo.getKeSanPham().getMaKeSanPham());
					if (ke != null) lo.setKeSanPham(ke);
				}
			} catch (Exception ignored) {}
		}
		return ds;
	}

	// Lấy tất cả kệ sản phẩm
	public List<KeSanPham> layTatCaKe() {
		return keDAO.getDSKeSanPham();
	}

	// Chuyển lô sang kệ khác
	public boolean chuyenKe(String maLoSanPham, String maKeSanPham) {
		return loDAO.capNhatKe(maLoSanPham, maKeSanPham);
	}

	// Đếm số lô hết hạn + sắp hết hạn trong danh sách
	public int demLoCanhBao(List<LoSanPham> dsLo) {
		int count = 0;
		for (LoSanPham lo : dsLo) {
			String tt = trangThaiLo(lo.getHanSuDung());
			if (TrangThaiLo.HET_HAN.equals(tt) || TrangThaiLo.SAP_HET_HAN.equals(tt)) count++;
		}
		return count;
	}

	// Sinh mã lô tự động — dùng cho PhieuNhap_Service
	public String sinhMaLoTuDong() {
		return loDAO.sinhMaTuDong();
	}
}
