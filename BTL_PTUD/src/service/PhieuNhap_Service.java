package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.text.SimpleDateFormat;

import dao.ChiTietPhieuNhap_DAO;
import dao.PhieuNhap_DAO;
import dao.SanPham_DAO;
import entity.ChiTietPhieuNhap;
import entity.KeSanPham;
import entity.LoSanPham;
import entity.NhanVien;
import entity.PhieuNhap;
import entity.SanPham;

public class PhieuNhap_Service {
	private static final DateTimeFormatter[] DINH_DANG_NGAY = new DateTimeFormatter[] {
			DateTimeFormatter.ofPattern("d/M/yyyy"),
			DateTimeFormatter.ofPattern("d-M-yyyy"),
			DateTimeFormatter.ISO_LOCAL_DATE };

	private final SanPham_DAO sanPhamDAO = new SanPham_DAO();
	private final PhieuNhap_DAO phieuNhapDAO = new PhieuNhap_DAO();
	private final ChiTietPhieuNhap_DAO chiTietDAO = new ChiTietPhieuNhap_DAO();
	private final LoSanPham_Service loSanPhamService = new LoSanPham_Service();

	public static class DuLieuNhapHang {
		private final List<ChiTietPhieuNhap> chiTietPhieuNhaps;
		private final String tenNhaCungCap;
		private final LocalDate ngayNhap;
		private final String ghiChu;

		public DuLieuNhapHang(List<ChiTietPhieuNhap> chiTietPhieuNhaps, String tenNhaCungCap, LocalDate ngayNhap,
				String ghiChu) {
			this.chiTietPhieuNhaps = chiTietPhieuNhaps;
			this.tenNhaCungCap = tenNhaCungCap;
			this.ngayNhap = ngayNhap;
			this.ghiChu = ghiChu;
		}

		public List<ChiTietPhieuNhap> getChiTietPhieuNhaps() {
			return chiTietPhieuNhaps;
		}

		public String getTenNhaCungCap() {
			return tenNhaCungCap;
		}

		public LocalDate getNgayNhap() {
			return ngayNhap;
		}

		public String getGhiChu() {
			return ghiChu;
		}
	}

	public List<ChiTietPhieuNhap> taiDanhSachNhapHang(File file) {
		return taiDuLieuNhapHang(file).getChiTietPhieuNhaps();
	}

	public DuLieuNhapHang taiDuLieuNhapHang(File file) {
		if (file == null || !file.exists() || !file.isFile()) {
			throw new IllegalArgumentException("Không tìm thấy file cần tải");
		}

		String tenFile = file.getName().toLowerCase(Locale.ROOT);

		try {
			if (tenFile.endsWith(".csv")) {
				return docFileCsv(file);
			}
			if (tenFile.endsWith(".xlsx") || tenFile.endsWith(".xls")) {
				return docFileExcel(file);
			}
		} catch (IOException e) {
			throw new IllegalArgumentException("Không đọc được file: " + e.getMessage(), e);
		}

		throw new IllegalArgumentException("Chỉ hỗ trợ file Excel (.xlsx, .xls) và CSV (.csv)");
	}

	private DuLieuNhapHang docFileCsv(File file) throws IOException {
		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
			List<ChiTietPhieuNhap> rows = new ArrayList<ChiTietPhieuNhap>();
			Map<String, String> thongTinPhieu = new LinkedHashMap<String, String>();
			Map<String, Integer> chiSoCot = null;
			String line;
			while ((line = reader.readLine()) != null) {
				if (line.trim().isEmpty()) {
					continue;
				}

				List<String> values = tachDongCsv(line);
				if (chiSoCot == null) {
					chiSoCot = taoChiSoCotNeuHopLe(values);
					if (chiSoCot == null) {
						luuThongTinPhieu(thongTinPhieu, values);
					}
					continue;
				}

				if (!laDongDuLieuHopLe(values, chiSoCot)) {
					continue;
				}
				rows.add(taoChiTietPhieuNhap(values, chiSoCot));
			}

			if (chiSoCot == null) {
				throw new IllegalArgumentException("Không tìm thấy dòng tiêu đề hợp lệ trong file nhập");
			}
			return taoDuLieuNhapHang(rows, thongTinPhieu);
		}
	}

	private DuLieuNhapHang docFileExcel(File file) throws IOException {
		try (FileInputStream inputStream = new FileInputStream(file);
				Workbook workbook = WorkbookFactory.create(inputStream)) {
			Sheet sheet = workbook.getNumberOfSheets() > 0 ? workbook.getSheetAt(0) : null;
			if (sheet == null) {
				return new DuLieuNhapHang(new ArrayList<ChiTietPhieuNhap>(), null, null, "");
			}

			DataFormatter formatter = new DataFormatter();
			List<ChiTietPhieuNhap> rows = new ArrayList<ChiTietPhieuNhap>();
			Map<String, String> thongTinPhieu = new LinkedHashMap<String, String>();
			Map<String, Integer> chiSoCot = null;

			for (int i = sheet.getFirstRowNum(); i <= sheet.getLastRowNum(); i++) {
				Row row = sheet.getRow(i);
				if (row == null) {
					continue;
				}

				List<String> values = docDongExcel(row, formatter);
				if (laDongTrong(values)) {
					continue;
				}
				if (chiSoCot == null) {
					chiSoCot = taoChiSoCotNeuHopLe(values);
					if (chiSoCot == null) {
						luuThongTinPhieu(thongTinPhieu, values);
					}
					continue;
				}
				if (!laDongDuLieuHopLe(values, chiSoCot)) {
					continue;
				}
				rows.add(taoChiTietPhieuNhap(values, chiSoCot));
			}

			if (chiSoCot == null) {
				throw new IllegalArgumentException("Không tìm thấy dòng tiêu đề hợp lệ trong file nhập");
			}
			return taoDuLieuNhapHang(rows, thongTinPhieu);
		} catch (Exception e) {
			throw new IllegalArgumentException("Không đọc được file Excel: " + e.getMessage(), e);
		}
	}

	private DuLieuNhapHang taoDuLieuNhapHang(List<ChiTietPhieuNhap> rows, Map<String, String> thongTinPhieu) {
		String tenNhaCungCap = layThongTinPhieu(thongTinPhieu, "NhaCungCap", "DonViGiao", "NhaCungCapGiao");
		LocalDate ngayNhap = parseNgayNhap(layThongTinPhieu(thongTinPhieu, "NgayNhap", "NgayGiao", "NgayLap"));
		String ghiChu = taoGhiChu(thongTinPhieu);
		return new DuLieuNhapHang(rows, tenNhaCungCap, ngayNhap, ghiChu);
	}

	private ChiTietPhieuNhap taoChiTietPhieuNhap(List<String> values, Map<String, Integer> chiSoCot) {
		SanPham sanPham = timSanPham(values, chiSoCot);
		int soLuong = parseSoLuong(layGiaTriBatBuoc(values, chiSoCot, "soluong"));
		String donVi = layGiaTriBatBuoc(values, chiSoCot, "donvitinh");
		double gia = parseGia(layGiaTriBatBuoc(values, chiSoCot, "gia"));
		return new ChiTietPhieuNhap(null, taoSanPhamHienThi(sanPham, donVi), soLuong, gia);
	}

	private SanPham taoSanPhamHienThi(SanPham sanPhamGoc, String donViTinh) {
		SanPham sanPham = new SanPham();
		sanPham.setMaSanPham(sanPhamGoc.getMaSanPham());
		sanPham.setTenSanPham(sanPhamGoc.getTenSanPham());
		sanPham.setDonViTinh(donViTinh);
		return sanPham;
	}

	private SanPham timSanPham(List<String> values, Map<String, Integer> chiSoCot) {
		String maSanPham = layGiaTri(values, chiSoCot, "masanpham");
		if (maSanPham == null || maSanPham.isEmpty()) {
			throw new IllegalArgumentException("Thiếu mã sản phẩm trong file nhập");
		}

		SanPham sanPham = sanPhamDAO.laySanPhamTheoMa(maSanPham.trim());
		if (sanPham == null) {
			throw new IllegalArgumentException("Không tìm thấy sản phẩm với mã: " + maSanPham);
		}
		return sanPham;
	}

	private Map<String, Integer> taoChiSoCot(List<String> tieuDe) {
		Map<String, Integer> chiSoCot = new HashMap<String, Integer>();
		for (int i = 0; i < tieuDe.size(); i++) {
			String tenCot = chuanHoaKhoaThongTin(tieuDe.get(i));
			if (tenCot.equals("masanpham") || tenCot.equals("masp")) {
				chiSoCot.put("masanpham", i);
			}
			if (tenCot.equals("soluong") || tenCot.equals("sl")) {
				chiSoCot.put("soluong", i);
			}
			if (tenCot.equals("donvitinh") || tenCot.equals("dvt")) {
				chiSoCot.put("donvitinh", i);
			}
			if (tenCot.equals("gianhap") || tenCot.equals("dongia") || tenCot.equals("gia")) {
				chiSoCot.put("gia", i);
			}
		}

		if (!chiSoCot.containsKey("masanpham") || !chiSoCot.containsKey("soluong")
				|| !chiSoCot.containsKey("donvitinh") || !chiSoCot.containsKey("gia")) {
			throw new IllegalArgumentException("File phải có đúng các cột MaSanPham, SoLuong, DonViTinh, GiaNhap");
		}

		return chiSoCot;
	}

	private Map<String, Integer> taoChiSoCotNeuHopLe(List<String> tieuDe) {
		try {
			return taoChiSoCot(tieuDe);
		} catch (IllegalArgumentException e) {
			return null;
		}
	}

	private void luuThongTinPhieu(Map<String, String> thongTinPhieu, List<String> values) {
		if (values == null || values.isEmpty()) {
			return;
		}

		String[] thongTin = tachThongTinPhieu(values);
		if (thongTin == null) {
			return;
		}

		String key = thongTin[0];
		if (key.isEmpty()) {
			return;
		}

		String value = thongTin[1];
		if (value.isEmpty()) {
			return;
		}
		thongTinPhieu.put(key, value);
	}

	private String[] tachThongTinPhieu(List<String> values) {
		String key = values.get(0) == null ? "" : values.get(0).trim();
		String value = ghepGiaTriThongTin(values);
		if (!key.isEmpty() && !value.isEmpty()) {
			return new String[] { key, value };
		}

		if (key.contains(":")) {
			String[] parts = key.split(":", 2);
			String tachKey = parts[0].trim();
			String tachValue = parts.length > 1 ? parts[1].trim() : "";
			if (!tachKey.isEmpty() && !tachValue.isEmpty()) {
				return new String[] { tachKey, tachValue };
			}
		}
		return null;
	}

	private String ghepGiaTriThongTin(List<String> values) {
		StringBuilder builder = new StringBuilder();
		for (int i = 1; i < values.size(); i++) {
			String value = values.get(i);
			if (value == null || value.trim().isEmpty()) {
				continue;
			}
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(value.trim());
		}
		return builder.toString();
	}

	private String layThongTinPhieu(Map<String, String> thongTinPhieu, String... keys) {
		for (String keyCanTim : keys) {
			for (Map.Entry<String, String> entry : thongTinPhieu.entrySet()) {
				if (laKhoaThongTin(entry.getKey(), keyCanTim)) {
					return entry.getValue();
				}
			}
		}
		return null;
	}

	private boolean laKhoaThongTin(String keyTrongFile, String keyCanTim) {
		if (keyTrongFile == null || keyCanTim == null) {
			return false;
		}
		return chuanHoaKhoaThongTin(keyTrongFile).equals(chuanHoaKhoaThongTin(keyCanTim));
	}

	private String chuanHoaKhoaThongTin(String value) {
		if (value == null) {
			return "";
		}
		String normalized = Normalizer.normalize(value.trim(), Normalizer.Form.NFD);
		normalized = normalized.replaceAll("\\p{M}+", "");
		normalized = normalized.replace('đ', 'd');
		normalized = normalized.replace('Đ', 'D');
		return normalized.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
	}

	private LocalDate parseNgayNhap(String value) {
		if (value == null || value.trim().isEmpty()) {
			return null;
		}

		String ngayNhap = value.trim();
		for (DateTimeFormatter dinhDangNgay : DINH_DANG_NGAY) {
			try {
				return LocalDate.parse(ngayNhap, dinhDangNgay);
			} catch (DateTimeParseException e) {
				// Thu tiep dinh dang khac.
			}
		}
		return null;
	}

	private String taoGhiChu(Map<String, String> thongTinPhieu) {
		String ghiChu = layThongTinPhieu(thongTinPhieu, "GhiChu");
		return ghiChu == null ? "" : ghiChu.trim();
	}

	private List<String> tachDongCsv(String line) {
		List<String> values = new ArrayList<String>();
		StringBuilder current = new StringBuilder();
		boolean insideQuotes = false;

		for (int i = 0; i < line.length(); i++) {
			char ch = line.charAt(i);
			if (ch == '"') {
				insideQuotes = !insideQuotes;
			} else if (ch == ',' && !insideQuotes) {
				values.add(current.toString().trim());
				current.setLength(0);
			} else {
				current.append(ch);
			}
		}

		values.add(current.toString().trim());
		return values;
	}

	private List<String> docDongExcel(Row row, DataFormatter formatter) {
		List<String> values = new ArrayList<String>();
		short lastCellNum = row.getLastCellNum();
		for (int i = 0; i < lastCellNum; i++) {
			Cell cell = row.getCell(i, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
			values.add(cell == null ? "" : formatter.formatCellValue(cell).trim());
		}
		return values;
	}

	private boolean laDongTrong(List<String> values) {
		for (String value : values) {
			if (value != null && !value.trim().isEmpty()) {
				return false;
			}
		}
		return true;
	}

	private boolean laDongDuLieuHopLe(List<String> values, Map<String, Integer> chiSoCot) {
		return coGiaTri(values, chiSoCot, "masanpham")
				&& coGiaTri(values, chiSoCot, "soluong")
				&& coGiaTri(values, chiSoCot, "donvitinh")
				&& coGiaTri(values, chiSoCot, "gia");
	}

	private boolean coGiaTri(List<String> values, Map<String, Integer> chiSoCot, String key) {
		String value = layGiaTri(values, chiSoCot, key);
		return value != null && !value.isEmpty();
	}

	private String layGiaTri(List<String> values, Map<String, Integer> chiSoCot, String key) {
		Integer index = chiSoCot.get(key);
		if (index == null || index.intValue() >= values.size()) {
			return null;
		}
		String value = values.get(index.intValue());
		return value == null ? null : value.trim();
	}

	private String layGiaTriBatBuoc(List<String> values, Map<String, Integer> chiSoCot, String key) {
		String value = layGiaTri(values, chiSoCot, key);
		if (value == null || value.isEmpty()) {
			throw new IllegalArgumentException("Thiếu dữ liệu ở cột " + key);
		}
		return value;
	}

	private int parseSoLuong(String value) {
		try {
			double parsed = Double.parseDouble(chuanHoaSo(value));
			return (int) parsed;
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Số lượng không hợp lệ: " + value);
		}
	}

	private double parseGia(String value) {
		try {
			return Double.parseDouble(chuanHoaSo(value));
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException("Giá không hợp lệ: " + value);
		}
	}

	private String chuanHoaSo(String value) {
		String normalized = value.trim().replace(" ", "");
		if (normalized.contains(",") && normalized.contains(".")) {
			return normalized.replace(",", "");
		}
		return normalized.replace(',', '.');
	}

	// ==================== LƯU PHIẾU NHẬP ====================

	// Sinh mã phiếu nhập tự động: PN + YYYY + 3 số (PN2026001).
	// Tham số {@code prefix} chỉ giữ để tương thích ngược, không sử dụng.
	public String sinhMaPhieuNhap(String prefix) {
		return phieuNhapDAO.sinhMaTuDong();
	}

	// Sinh mã phiếu nhập tự động: PN + YYYY + 3 số.
	public String sinhMaPhieuNhap() {
		return phieuNhapDAO.sinhMaTuDong();
	}

	// @deprecated Giữ để tương thích, hãy gọi {@link #sinhMaPhieuNhap()}.
	@Deprecated
	public String sinhPrefixHomNay() {
		return "PN" + java.time.LocalDate.now().getYear();
	}

	// Lưu phiếu nhập + toàn bộ chi tiết + tạo lô sản phẩm.
	// @param pn           Phiếu nhập đã điền mã, ngày, NCC, NV
	// @param dsChiTiet    Danh sách chi tiết (từ bảng GUI)
	// @param keSP         Kệ mặc định để đặt lô
	// @param taoMaLoFn    Hàm sinh mã lô theo index (có thể null → tự sinh)
	// @return số lô tạo thành công; -1 nếu không lưu được phiếu nhập
	public int luuPhieuNhapVaChiTiet(PhieuNhap pn, List<ChiTietPhieuNhap> dsChiTiet, KeSanPham keSP) {
		if (!phieuNhapDAO.taoPhieuNhap(pn)) {
			return -1;
		}
		int soLoTao = 0;
		for (int i = 0; i < dsChiTiet.size(); i++) {
			ChiTietPhieuNhap ct = dsChiTiet.get(i);
			ct.setPhieuNhap(pn);
			chiTietDAO.them(ct);
			try {
				String maLo = loSanPhamService.sinhMaLoTuDong();
				LoSanPham lo = loSanPhamService.taoLoTuPhieuNhap(ct, pn, maLo, keSP);
				loSanPhamService.luuLo(lo);
				soLoTao++;
			} catch (Exception ex) {
				System.err.println("Lỗi tạo lô cho dòng " + i + ": " + ex.getMessage());
			}
		}
		return soLoTao;
	}
}
