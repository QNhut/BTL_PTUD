package service;

import java.text.Normalizer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * Quy đổi đơn vị tính cho dược phẩm.
 * 
 * Bảng quy đổi mặc định: 1 Vỉ = 10 Viên 1 Hộp = 10 Vỉ = 100 Viên 1 Hộp = 10 Gói
 * / 10 Ống 1 Bịch = 10 Gói 1 Chai = 1 Lọ
 * 
 * Có thể bổ sung thêm qua boSungQuyDoi() và boSungBienThe().
 */
public class DonViTinhConverter {

	private final Map<String, String> bienThe; // "vien" → "Viên"
	private final Map<String, Map<String, Double>> graph; // "Hộp" → {"Viên": 100, "Vỉ": 10}

	public DonViTinhConverter() {
		bienThe = taoBienThe();
		graph = taoGraphQuyDoi();
	}

	// ==================== API CHÍNH ====================

	/** Chuẩn hóa tên đơn vị: "vien" → "Viên", "hop" → "Hộp" */
	public String chuanHoa(String donVi) {
		String chuan = bienThe.get(toKey(donVi));
		return chuan != null ? chuan : donVi.trim();
	}

	/** Hai đơn vị có tương đương không? */
	public boolean tuongDuong(String a, String b) {
		return chuanHoa(a).equalsIgnoreCase(chuanHoa(b));
	}

	/** Lấy hệ số quy đổi: layHeSoQuyDoi("Hộp", "Viên") = 100.0 */
	public double layHeSoQuyDoi(String donViGoc, String donViDich) {
		String goc = chuanHoa(donViGoc);
		String dich = chuanHoa(donViDich);
		if (goc.equalsIgnoreCase(dich))
			return 1.0;

		Double heSo = timHeSo(goc, dich, new HashSet<String>());
		if (heSo == null) {
			throw new IllegalArgumentException("Không quy đổi được: " + donViGoc + " → " + donViDich);
		}
		return heSo;
	}

	/** Quy đổi số lượng: chuanHoaSoLuong(5, "Hộp", "Viên") = 500 */
	public double chuanHoaSoLuong(double soLuong, String donViGoc, String donViDich) {
		return soLuong * layHeSoQuyDoi(donViGoc, donViDich);
	}

	/** Quy đổi đơn giá (nghịch đảo): chuanHoaDonGia(50000, "Hộp", "Viên") = 500 */
	public double chuanHoaDonGia(double donGia, String donViGoc, String donViDich) {
		return donGia / layHeSoQuyDoi(donViGoc, donViDich);
	}

	/** Kiểm tra 2 đơn vị có thể quy đổi qua lại */
	public boolean coTheQuyDoi(String donViA, String donViB) {
		if (donViA == null || donViB == null)
			return true;
		if (tuongDuong(donViA, donViB))
			return true;
		return timHeSo(chuanHoa(donViA), chuanHoa(donViB), new HashSet<String>()) != null;
	}

	/** Mô tả: "1 Hộp = 100 Viên" */
	public String moTa(String donViGoc, String donViDich) {
		double hs = layHeSoQuyDoi(donViGoc, donViDich);
		String hsStr = (hs == Math.floor(hs)) ? String.valueOf((long) hs) : String.format("%.2f", hs);
		return "1 " + chuanHoa(donViGoc) + " = " + hsStr + " " + chuanHoa(donViDich);
	}

	// ==================== MỞ RỘNG ====================

	/** Bổ sung biến thể tên: boSungBienThe("tablet", "Viên") */
	public void boSungBienThe(String bienTheMoi, String donViChuan) {
		bienThe.put(toKey(bienTheMoi), donViChuan.trim());
	}

	/** Bổ sung quy đổi: boSungQuyDoi("Thùng", "Hộp", 24) → 1 Thùng = 24 Hộp */
	public void boSungQuyDoi(String donViLon, String donViNho, double heSo) {
		if (heSo <= 0)
			throw new IllegalArgumentException("Hệ số phải > 0");
		dangKy(chuanHoa(donViLon), chuanHoa(donViNho), heSo);
	}

	// ==================== DỮ LIỆU MẶC ĐỊNH ====================

	private Map<String, String> taoBienThe() {
		Map<String, String> m = new HashMap<String, String>();
		nhom(m, "Viên", "vien", "viên", "v", "capsule", "cap", "tablet", "tab");
		nhom(m, "Vỉ", "vi", "vỉ", "blister");
		nhom(m, "Hộp", "hop", "hộp", "box");
		nhom(m, "Hũ", "hu", "hũ", "jar", "cup");
		nhom(m, "Túi", "tui", "túi", "bag", "pouch");
		nhom(m, "Gói", "goi", "gói", "pack", "packet", "sachet");
		nhom(m, "Bịch", "bich", "bịch");
		nhom(m, "Chai", "chai", "bottle");
		nhom(m, "Lọ", "lo", "lọ");
		nhom(m, "Ống", "ong", "ống", "ampoule");
		nhom(m, "Tuýp", "tuyp", "tuýp", "tube");
		nhom(m, "Miếng", "mieng", "miếng", "piece", "pcs");
		nhom(m, "ml", "ml", "millilitre", "milliliter");
		nhom(m, "g", "g", "gram", "grams");
		return m;
	}

	private Map<String, Map<String, Double>> taoGraphQuyDoi() {
		Map<String, Map<String, Double>> g = new HashMap<String, Map<String, Double>>();

		// Thuốc viên: 1 Vỉ = 10 Viên, 1 Hộp = 10 Vỉ = 100 Viên
		dangKy(g, "Vỉ", "Viên", 10);
		dangKy(g, "Hộp", "Vỉ", 10);
		dangKy(g, "Hộp", "Viên", 100);

		// Gói / Bịch: 1 Hộp = 10 Gói, 1 Bịch = 10 Gói
		dangKy(g, "Hộp", "Gói", 10);
		dangKy(g, "Bịch", "Gói", 10);

		// Ống: 1 Hộp = 10 Ống
		dangKy(g, "Hộp", "Ống", 10);

		// Chai = Lọ
		dangKy(g, "Chai", "Lọ", 1);

		return g;
	}

	// ==================== INTERNAL ====================

	private void nhom(Map<String, String> m, String chuan, String... ten) {
		m.put(toKey(chuan), chuan);
		for (String t : ten)
			m.put(toKey(t), chuan);
	}

	private void dangKy(String lon, String nho, double heSo) {
		dangKy(graph, lon, nho, heSo);
	}

	private void dangKy(Map<String, Map<String, Double>> g, String lon, String nho, double heSo) {
		g.computeIfAbsent(lon, k -> new HashMap<String, Double>()).put(nho, heSo);
		g.computeIfAbsent(nho, k -> new HashMap<String, Double>()).put(lon, 1.0 / heSo);
	}

	/** Tìm hệ số quy đổi bằng BFS/DFS qua graph (hỗ trợ quy đổi gián tiếp) */
	private Double timHeSo(String goc, String dich, Set<String> daDuyet) {
		if (goc.equalsIgnoreCase(dich))
			return 1.0;
		if (!daDuyet.add(goc))
			return null;

		Map<String, Double> ke = graph.get(goc);
		if (ke == null)
			return null;

		// Trực tiếp
		Double trucTiep = ke.get(dich);
		if (trucTiep != null)
			return trucTiep;

		// Gián tiếp (qua đơn vị trung gian)
		for (Map.Entry<String, Double> e : ke.entrySet()) {
			Double heSoCon = timHeSo(e.getKey(), dich, daDuyet);
			if (heSoCon != null)
				return e.getValue() * heSoCon;
		}
		return null;
	}

	/** Chuẩn hóa key: bỏ dấu, lowercase */
	private static String toKey(String donVi) {
		if (donVi == null || donVi.trim().isEmpty()) {
			throw new IllegalArgumentException("Đơn vị tính không được để trống");
		}
		String s = Normalizer.normalize(donVi.trim(), Normalizer.Form.NFD);
		return s.replaceAll("\\p{M}+", "").toLowerCase(Locale.ROOT);
	}
}