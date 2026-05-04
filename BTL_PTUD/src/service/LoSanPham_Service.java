package service;

import java.time.LocalDate;
import java.util.List;

import dao.LoSanPham_DAO;
import dao.SanPham_DAO;
import entity.ChiTietPhieuNhap;
import entity.KeSanPham;
import entity.LoSanPham;
import entity.PhieuNhap;
import entity.SanPham;

/**
 * Service quản lý lô sản phẩm.
 * 
 * Luồng chính:
 *   1. Nhập hàng → taoLoTuPhieuNhap() → quy đổi đơn vị + tính HSD → lưu DB
 *   2. Bán hàng  → truKho() → FIFO theo HSD → trừ từ lô cũ nhất
 */
public class LoSanPham_Service {

	private final LoSanPham_DAO loDAO = new LoSanPham_DAO();
	private final SanPham_DAO spDAO = new SanPham_DAO();
	private final DonViTinhConverter converter = new DonViTinhConverter();

	// ==================== NHẬP HÀNG ====================

	/**
	 * Tạo lô sản phẩm từ chi tiết phiếu nhập.
	 * - Quy đổi số lượng từ đơn vị file (Hộp/Vỉ/Thùng) → đơn vị chuẩn SP
	 * - Tính HSD = ngayNhap + sanPham.hanSuDung (tháng)
	 * 
	 * Ví dụ: Nhập 5 Hộp Paracetamol (đơn vị chuẩn: Viên)
	 *   → soLuong = 5 × 100 = 500 Viên
	 *   → hanSuDung = ngayNhap + 24 tháng
	 */
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

	/**
	 * Lưu lô sản phẩm vào DB
	 */
	public boolean luuLo(LoSanPham lo) {
		return loDAO.them(lo);
	}

	// ==================== BÁN HÀNG (TRỪ KHO) ====================

	/**
	 * Trừ kho theo FIFO (lô cũ nhất trước, sắp hết hạn trước).
	 * 
	 * Ví dụ: Bán 20 Viên Paracetamol
	 *   - Lô 1 (HSD gần): còn 15 → trừ 15, còn lại 0
	 *   - Lô 2 (HSD xa): còn 500 → trừ 5, còn lại 495
	 * 
	 * @param maSP       Mã sản phẩm
	 * @param soLuongBan Số lượng bán (đơn vị bán)
	 * @param donViBan   Đơn vị bán (Viên, Vỉ, Gói...)
	 * @return true nếu trừ đủ, false nếu không đủ hàng
	 */
	public boolean truKho(String maSP, int soLuongBan, String donViBan) {
		SanPham sp = spDAO.laySanPhamTheoMa(maSP);
		if (sp == null) return false;

		// Quy đổi số lượng bán → đơn vị chuẩn
		int canTru = quyDoi(soLuongBan, donViBan, sp.getDonViTinh());

		// Lấy lô còn hạn, sắp xếp FIFO (HSD gần trước)
		List<LoSanPham> dsLo = layLoConHanFIFO(maSP);

		// Kiểm tra đủ hàng không
		int tongTon = 0;
		for (LoSanPham lo : dsLo) {
			tongTon += quyDoi(lo.getSoLuong(), lo.getDonViTinh(), sp.getDonViTinh());
		}
		if (tongTon < canTru) return false;

		// Trừ FIFO
		int conLai = canTru;
		for (LoSanPham lo : dsLo) {
			if (conLai <= 0) break;
			int tonLo = quyDoi(lo.getSoLuong(), lo.getDonViTinh(), sp.getDonViTinh());
			if (tonLo <= conLai) {
				lo.setSoLuong(0);
				conLai -= tonLo;
			} else {
				lo.setSoLuong(tonLo - conLai);
				lo.setDonViTinh(sp.getDonViTinh()); // đã quy đổi
				conLai = 0;
			}
			// TODO: cập nhật lo vào DB (cần thêm method update trong DAO)
		}
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

	/**
	 * Tính HSD: ngày nhập + số tháng.
	 * Nếu hanSuDungThang = null hoặc 0 → không giới hạn (return null)
	 */
	private LocalDate tinhHanSuDung(LocalDate ngayNhap, Integer hanSuDungThang) {
		if (hanSuDungThang == null || hanSuDungThang <= 0) return null;
		return ngayNhap.plusMonths(hanSuDungThang);
	}

	/**
	 * Lấy danh sách lô còn hạn, sắp xếp FIFO (HSD gần nhất trước)
	 */
	private List<LoSanPham> layLoConHanFIFO(String maSP) {
		List<LoSanPham> tatCa = loDAO.getDSLoSanPham();
		List<LoSanPham> conHan = new java.util.ArrayList<LoSanPham>();
		LocalDate homNay = LocalDate.now();

		for (LoSanPham lo : tatCa) {
			if (lo.getSanPham() == null) continue;
			if (!maSP.equals(lo.getSanPham().getMaSanPham())) continue;
			if (lo.getSoLuong() <= 0 || !lo.isTrangThai()) continue;
			if (lo.getHanSuDung() != null && lo.getHanSuDung().isBefore(homNay)) continue;
			conHan.add(lo);
		}

		// Sắp xếp: HSD gần nhất trước (null = vô hạn → đưa cuối)
		conHan.sort((a, b) -> {
			if (a.getHanSuDung() == null && b.getHanSuDung() == null) return 0;
			if (a.getHanSuDung() == null) return 1;
			if (b.getHanSuDung() == null) return -1;
			return a.getHanSuDung().compareTo(b.getHanSuDung());
		});
		return conHan;
	}
}
