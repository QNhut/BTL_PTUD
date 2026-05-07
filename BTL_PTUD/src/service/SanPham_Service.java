package service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dao.KhuyenMai_DAO;
import dao.LoSanPham_DAO;
import dao.SanPham_DAO;
import dao.Thue_DAO;
import entity.KhuyenMai;
import entity.LoSanPham;
import entity.SanPham;
import entity.Thue;

public class SanPham_Service {
	public static final int NGUONG_SAP_HET = 50;

	private final SanPham_DAO sanPhamDAO = new SanPham_DAO();
	private final LoSanPham_DAO loSanPhamDAO = new LoSanPham_DAO();
	private final KhuyenMai_DAO khuyenMaiDAO = new KhuyenMai_DAO();
	private final Thue_DAO thueDAO = new Thue_DAO();
	private final DonViTinhConverter converter = new DonViTinhConverter();

	// ==================== DTO tồn kho ====================
	public static class TonKhoInfo {
		public final int tonKho;
		public final int soLo;
		public final int loHetHan;
		public final String trangThai; 

		public TonKhoInfo(int tonKho, int soLo, int loHetHan) {
			this.tonKho = tonKho;
			this.soLo = soLo;
			this.loHetHan = loHetHan;
			if (tonKho <= 0) this.trangThai = "HET_HANG";
			else if (tonKho < NGUONG_SAP_HET) this.trangThai = "SAP_HET";
			else this.trangThai = "CON_HANG";
		}
	}

	// ==================== CRUD ====================
	public List<SanPham> layDanhSachSanPham() { return sanPhamDAO.getDSSanPham(); }
	public SanPham laySanPhamTheoMa(String ma) { return sanPhamDAO.laySanPhamTheoMa(ma); }

	// ==================== TÌM KIẾM & LỌC ====================
	public List<SanPham> timKiem(List<SanPham> ds, String tuKhoa) {
		if (tuKhoa == null || tuKhoa.trim().isEmpty()) return ds;
		String kw = tuKhoa.trim().toLowerCase();
		List<SanPham> kq = new ArrayList<SanPham>();
		for (SanPham sp : ds) {
			if (sp.getTenSanPham().toLowerCase().contains(kw)
					|| sp.getMaSanPham().toLowerCase().contains(kw)) {
				kq.add(sp);
			}
		}
		return kq;
	}

	public List<SanPham> locTheoLoai(List<SanPham> ds, String maLoai) {
		if (maLoai == null || maLoai.trim().isEmpty()) return ds;
		List<SanPham> kq = new ArrayList<SanPham>();
		for (SanPham sp : ds) {
			if (sp.getLoaiSanPham() != null && maLoai.equals(sp.getLoaiSanPham().getMaLoaiSanPham())) {
				kq.add(sp);
			}
		}
		return kq;
	}

	public List<SanPham> locTheoTrangThaiTon(List<SanPham> ds, String filter, Map<String, TonKhoInfo> mapTon) {
		if ("ALL".equals(filter)) return ds;
		List<SanPham> kq = new ArrayList<SanPham>();
		for (SanPham sp : ds) {
			TonKhoInfo info = mapTon.get(sp.getMaSanPham());
			if (info != null && filter.equals(info.trangThai)) {
				kq.add(sp);
			}
		}
		return kq;
	}

	// ==================== TỒN KHO (1 lần query) ====================
	/**
	 * Tính tồn kho cho TẤT CẢ sản phẩm trong 1 lần query.
	 * Trả về Map: maSP → TonKhoInfo
	 */
	public Map<String, TonKhoInfo> tinhTonKhoTatCa(List<SanPham> dsSanPham) {
		List<LoSanPham> dsLo = loSanPhamDAO.getDSLoSanPham();
		LocalDate homNay = LocalDate.now();

		// Map SP → đơn vị chuẩn
		Map<String, String> donViMap = new HashMap<String, String>();
		for (SanPham sp : dsSanPham) {
			donViMap.put(sp.getMaSanPham(), sp.getDonViTinh());
		}

		// Tính toán cho từng SP
		Map<String, Integer> tonMap = new HashMap<String, Integer>();
		Map<String, Integer> loConHanMap = new HashMap<String, Integer>();
		Map<String, Integer> loHetHanMap = new HashMap<String, Integer>();

		for (LoSanPham lo : dsLo) {
			if (lo.getSanPham() == null) continue;
			String maSP = lo.getSanPham().getMaSanPham();

			boolean hetHan = lo.getHanSuDung() != null && lo.getHanSuDung().isBefore(homNay);
			if (hetHan) {
				loHetHanMap.merge(maSP, 1, Integer::sum);
			} else if (lo.isTrangThai() && lo.getSoLuong() > 0) {
				// Quy đổi về đơn vị chuẩn SP
				String donViChuan = donViMap.get(maSP);
				int sl = quyDoi(lo.getSoLuong(), lo.getDonViTinh(), donViChuan);
				tonMap.merge(maSP, sl, Integer::sum);
				loConHanMap.merge(maSP, 1, Integer::sum);
			}
		}

		// Build kết quả
		Map<String, TonKhoInfo> result = new HashMap<String, TonKhoInfo>();
		for (SanPham sp : dsSanPham) {
			String ma = sp.getMaSanPham();
			int ton = tonMap.getOrDefault(ma, 0);
			int soLo = loConHanMap.getOrDefault(ma, 0);
			int hetHan = loHetHanMap.getOrDefault(ma, 0);
			result.put(ma, new TonKhoInfo(ton, soLo, hetHan));
		}
		return result;
	}

	// ==================== QUY ĐỔI ĐƠN VỊ ====================
	public int quyDoi(int soLuong, String donViGoc, String donViDich) {
		if (donViGoc == null || donViDich == null
				|| donViGoc.trim().isEmpty() || donViDich.trim().isEmpty()) {
			return soLuong;
		}
		try {
			return (int) Math.round(soLuong * converter.layHeSoQuyDoi(donViGoc, donViDich));
		} catch (IllegalArgumentException e) {
			return soLuong;
		}
	}

	// ==================== ĐẾM THỐNG KÊ ====================
	public static class ThongKe {
		public final int tong, conHang, sapHet, hetHang;
		public ThongKe(int tong, int conHang, int sapHet, int hetHang) {
			this.tong = tong; this.conHang = conHang;
			this.sapHet = sapHet; this.hetHang = hetHang;
		}
	}

	public ThongKe tinhThongKe(List<SanPham> ds, Map<String, TonKhoInfo> mapTon) {
		int con = 0, sap = 0, het = 0;
		for (SanPham sp : ds) {
			TonKhoInfo info = mapTon.get(sp.getMaSanPham());
			if (info == null || "HET_HANG".equals(info.trangThai)) het++;
			else if ("SAP_HET".equals(info.trangThai)) sap++;
			else con++;
		}
		return new ThongKe(ds.size(), con, sap, het);
	}

	// ==================== TỒN KHO (1 SP) ====================
	/** Trả về tổng số lượng còn tồn (chưa hết hạn) của 1 sản phẩm — dùng cho HoaDon_GUI */
	public int layTonKho(String maSP) {
		return loSanPhamDAO.layTongSoLuongTonTheoMaSanPham(maSP);
	}

	// ── CRUD ─────────────────────────────────────────────────

	/**
	 * Thêm sản phẩm mới. Tự động gán KhuyenMai/Thue mặc định (record đầu tiên từ DB)
	 * nếu sp không có để tránh NPE trong DAO.
	 */
	public boolean themSanPham(SanPham sp) {
		if (sp.getKhuyenMai() == null) {
			KhuyenMai km = layKhuyenMaiMacDinh();
			if (km == null) throw new IllegalStateException("Không có khuyến mãi nào trong DB");
			sp.setKhuyenMai(km);
		}
		if (sp.getThue() == null) {
			Thue thue = layThueMacDinh();
			if (thue == null) throw new IllegalStateException("Không có thuế nào trong DB");
			sp.setThue(thue);
		}
		return sanPhamDAO.themSanPham(sp);
	}

	private KhuyenMai layKhuyenMaiMacDinh() {
		java.util.ArrayList<KhuyenMai> ds = khuyenMaiDAO.getDSKhuyenMai();
		return ds.isEmpty() ? null : ds.get(0);
	}

	private Thue layThueMacDinh() {
		java.util.ArrayList<Thue> ds = thueDAO.getDSThue();
		return ds.isEmpty() ? null : ds.get(0);
	}

	public boolean capNhatSanPham(SanPham sp) { return sanPhamDAO.updateSanPham(sp); }

	public boolean xoaSanPham(String maSP) { return sanPhamDAO.xoaSanPham(maSP); }

	public String sinhMaSanPhamMoi() { return sanPhamDAO.sinhMaTuDong(); }
}
