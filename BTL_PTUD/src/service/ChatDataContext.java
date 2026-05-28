package service;

import ConnectDB.ConnectDB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Xây dựng ngữ cảnh dữ liệu thực từ DB để gửi kèm cho AI.
 *
 * Luôn bao gồm snapshot tổng quan cửa hàng (hôm nay, tháng, tồn kho, hóa đơn
 * gần nhất). Bổ sung thêm dữ liệu chi tiết khi phát hiện từ khóa.
 */
public final class ChatDataContext {

	private ChatDataContext() {
	}

	private static final DateTimeFormatter DT_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	/**
	 * Luôn trả về snapshot sống của cửa hàng + dữ liệu chi tiết theo từ khóa.
	 */
	public static String buildContext(String question) {
		String q = normalize(question);
		StringBuilder ctx = new StringBuilder();

		// Phần 1: Snapshot tổng quan cửa hàng tại thời điểm hỏi (luôn có)
		ctx.append(buildLiveSnapshot());

		// Phần 2: Chi tiết hóa đơn nếu hỏi về mã hóa đơn cụ thể
		String hdId = extractHoaDonId(question);
		if (hdId != null) {
			ctx.append(queryInvoiceDetail(hdId));
		}

		// Phần 3: Danh sách hóa đơn gần nhất nếu hỏi chung chung về hóa đơn
		if (q.contains("danh sach hoa don") || q.contains("tat ca hoa don") || q.contains("list hoa don")
				|| q.contains("xem hoa don")) {
			ctx.append(queryRecentInvoices(20));
		}

		// Phần 4: Doanh thu theo từng ngày trong tháng nếu hỏi về doanh thu hoặc biểu
		// đồ
		if (q.contains("doanh thu theo ngay") || q.contains("tung ngay") || q.contains("chi tiet thang")
				|| q.contains("bieu do")) {
			ctx.append(queryMonthDailyBreakdown());
		}

		// Phần 5: Top sản phẩm bán chạy nếu hỏi về sản phẩm bán chạy, phổ biến
		if (q.contains("ban chay") || q.contains("nhieu nhat") || q.contains("top san pham")
				|| q.contains("san pham ban") || q.contains("pho bien") || q.contains("hang ban nhieu")) {
			ctx.append(queryTopProducts(10));
		}

		// Phần 6: Top khách hàng chi tiêu nhiều nhất nếu hỏi về khách hàng thân thiết,
		// mua nhiều
		if ((q.contains("khach hang") || q.contains("khach"))
				&& (q.contains("top") || q.contains("nhieu") || q.contains("than thiet") || q.contains("lich su")
						|| q.contains("mua nhieu") || q.contains("chi tieu"))) {
			ctx.append(queryTopCustomers(10));
		}

		// Phần 7: Toàn bộ tồn kho chi tiết nếu hỏi về tồn kho, hết hàng, nhập hàng, con
		// lại
		if (q.contains("het hang") || q.contains("sap het") || q.contains("ton kho") || q.contains("con lai")
				|| q.contains("nhap hang") || q.contains("kho")) {
			ctx.append(queryFullStockStatus());
		}

		return ctx.toString();
	}

	// Phần xây dựng snapshot tổng quan cửa hàng tại thời điểm hỏi (luôn có, cập
	// nhật trực tiếp từ DB)
	private static String buildLiveSnapshot() {
		LocalDate today = LocalDate.now();
		int y = today.getYear(), m = today.getMonthValue(), d = today.getDayOfMonth();
		StringBuilder sb = new StringBuilder();
		sb.append("=== DỮ LIỆU THỰC TẾ CỬA HÀNG (cập nhật lúc ").append(today.format(DATE_FMT)).append(") ===\n");

		Connection con = ConnectDB.getInstance().getConnection();
		try {

			// Hôm nay: số hóa đơn đã thanh toán, chờ thanh toán, doanh thu hôm nay
			String sqlToday = "SELECT "
					+ "SUM(CASE WHEN hd.TrangThai = N'Đã thanh toán' THEN 1 ELSE 0 END) AS DaThanhToan, "
					+ "SUM(CASE WHEN hd.TrangThai = N'Chờ thanh toán' THEN 1 ELSE 0 END) AS ChoThanhToan, "
					+ "COALESCE(SUM(CASE WHEN hd.TrangThai = N'Đã thanh toán' "
					+ "THEN c.TongTien ELSE 0 END), 0) AS DoanhThu " + "FROM HoaDon hd "
					+ "LEFT JOIN (SELECT MaHoaDon, SUM(SoLuong*DonGia) AS TongTien "
					+ "FROM ChiTietHoaDon GROUP BY MaHoaDon) c ON hd.MaHoaDon = c.MaHoaDon "
					+ "WHERE YEAR(hd.NgayLap)=? AND MONTH(hd.NgayLap)=? AND DAY(hd.NgayLap)=?";
			try (PreparedStatement ps = con.prepareStatement(sqlToday)) {
				ps.setInt(1, y);
				ps.setInt(2, m);
				ps.setInt(3, d);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						sb.append("\nHÔM NAY (").append(today.format(DATE_FMT)).append("):\n");
						sb.append("  Hóa đơn đã thanh toán : ").append(rs.getInt("DaThanhToan")).append("\n");
						sb.append("  Hóa đơn chờ thanh toán: ").append(rs.getInt("ChoThanhToan")).append("\n");
						sb.append("  Doanh thu hôm nay     : ").append(fmt(rs.getDouble("DoanhThu"))).append(" VND\n");
					}
				}
			}

			// Tháng này: số hóa đơn đã thanh toán, tổng doanh thu tháng này
			String sqlMonth = "SELECT COUNT(*) AS SoHD, " + "COALESCE(SUM(c.TongTien), 0) AS DoanhThu "
					+ "FROM HoaDon hd " + "LEFT JOIN (SELECT MaHoaDon, SUM(SoLuong*DonGia) AS TongTien "
					+ "FROM ChiTietHoaDon GROUP BY MaHoaDon) c ON hd.MaHoaDon = c.MaHoaDon "
					+ "WHERE YEAR(hd.NgayLap)=? AND MONTH(hd.NgayLap)=? " + "AND hd.TrangThai = N'Đã thanh toán'";
			try (PreparedStatement ps = con.prepareStatement(sqlMonth)) {
				ps.setInt(1, y);
				ps.setInt(2, m);
				try (ResultSet rs = ps.executeQuery()) {
					if (rs.next()) {
						sb.append("\nTHÁNG ").append(m).append("/").append(y).append(":\n");
						sb.append("  Tổng hóa đơn  : ").append(rs.getInt("SoHD")).append("\n");
						sb.append("  Tổng doanh thu: ").append(fmt(rs.getDouble("DoanhThu"))).append(" VND\n");
					}
				}
			}

			// Toàn bộ: số hóa đơn đã thanh toán, tổng doanh thu từ trước đến nay
			String sqlAll = "SELECT COUNT(*) AS TongHD, " + "COALESCE(SUM(c.TongTien), 0) AS TongDT "
					+ "FROM HoaDon hd " + "LEFT JOIN (SELECT MaHoaDon, SUM(SoLuong*DonGia) AS TongTien "
					+ "FROM ChiTietHoaDon GROUP BY MaHoaDon) c ON hd.MaHoaDon = c.MaHoaDon "
					+ "WHERE hd.TrangThai = N'Đã thanh toán'";
			try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sqlAll)) {
				if (rs.next()) {
					sb.append("\nTOÀN BỘ (từ trước đến nay):\n");
					sb.append("  Tổng hóa đơn  : ").append(rs.getInt("TongHD")).append("\n");
					sb.append("  Tổng doanh thu: ").append(fmt(rs.getDouble("TongDT"))).append(" VND\n");
				}
			}

			// Tồn kho: tổng sản phẩm đang bán, tổng số lượng tồn kho
			String sqlStock = "SELECT COUNT(DISTINCT sp.MaSanPham) AS TongSP, " + "SUM(ls.SoLuong) AS TongTon "
					+ "FROM SanPham sp " + "LEFT JOIN LoSanPham ls ON sp.MaSanPham = ls.MaSanPham "
					+ "WHERE sp.TrangThai = 1";
			try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sqlStock)) {
				if (rs.next()) {
					sb.append("\nKHO HÀNG:\n");
					sb.append("  Tổng sản phẩm đang bán: ").append(rs.getInt("TongSP")).append("\n");
					sb.append("  Tổng số lượng tồn kho : ").append(rs.getInt("TongTon")).append("\n");
				}
			}

			// Sản phẩm sắp hết hàng (< 20) - top 10
			String sqlLowStock = "SELECT TOP 10 sp.TenSanPham, COALESCE(SUM(ls.SoLuong),0) AS TonKho "
					+ "FROM SanPham sp " + "LEFT JOIN LoSanPham ls ON sp.MaSanPham = ls.MaSanPham "
					+ "WHERE sp.TrangThai = 1 " + "GROUP BY sp.MaSanPham, sp.TenSanPham "
					+ "HAVING COALESCE(SUM(ls.SoLuong), 0) < 20 " + "ORDER BY TonKho ASC";
			try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sqlLowStock)) {
				boolean hasLow = false;
				StringBuilder low = new StringBuilder();
				while (rs.next()) {
					if (!hasLow) {
						low.append("  Sắp hết hàng (< 20)  :\n");
						hasLow = true;
					}
					low.append("    - ").append(rs.getString("TenSanPham")).append(" (còn ").append(rs.getInt("TonKho"))
							.append(")\n");
				}
				if (hasLow)
					sb.append(low);
			}

			// Khách hàng: tổng khách hàng đang hoạt động (trạng thái = 1)
			String sqlKH = "SELECT COUNT(*) AS TongKH FROM KhachHang WHERE TrangThai = 1";
			try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sqlKH)) {
				if (rs.next()) {
					sb.append("\nKHÁCH HÀNG:\n");
					sb.append("  Tổng khách hàng đang hoạt động: ").append(rs.getInt("TongKH")).append("\n");
				}
			}

			// 5 hóa đơn gần nhất (có cả khách hàng và tổng tiền)
			String sqlRecent = "SELECT TOP 5 hd.MaHoaDon, hd.NgayLap, hd.TrangThai, "
					+ "COALESCE(kh.TenKhachHang, hd.MaKhachHang) AS TenKH, "
					+ "COALESCE(SUM(c.SoLuong*c.DonGia), 0) AS TongTien " + "FROM HoaDon hd "
					+ "LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang "
					+ "LEFT JOIN ChiTietHoaDon c ON hd.MaHoaDon = c.MaHoaDon "
					+ "GROUP BY hd.MaHoaDon, hd.NgayLap, hd.TrangThai, kh.TenKhachHang, hd.MaKhachHang "
					+ "ORDER BY hd.NgayLap DESC";
			try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sqlRecent)) {
				sb.append("\n5 HÓA ĐƠN GẦN NHẤT:\n");
				while (rs.next()) {
					sb.append(String.format("  %-12s | %-16s | %-18s | %-22s | %s VND%n", rs.getString("MaHoaDon"),
							rs.getTimestamp("NgayLap").toLocalDateTime().format(DATE_FMT), rs.getString("TrangThai"),
							rs.getString("TenKH"), fmt(rs.getDouble("TongTien"))));
				}
			}

		} catch (Exception e) {
			sb.append("(Lỗi tải dữ liệu snapshot: ").append(e.getMessage()).append(")\n");
		}

		sb.append("\n");
		return sb.toString();
	}

	// Phần xây dựng chi tiết hóa đơn nếu phát hiện mã hóa đơn trong câu hỏi (truy
	// vấn trực tiếp từ DB)
	private static String queryInvoiceDetail(String maHD) {
		String id2 = maHD.replace("HD-", "HD");
		StringBuilder sb = new StringBuilder();
		String sql = "SELECT hd.MaHoaDon, hd.NgayLap, hd.TrangThai, hd.GhiChu, "
				+ "COALESCE(kh.TenKhachHang, hd.MaKhachHang) AS TenKH, "
				+ "COALESCE(nv.TenNhanVien, hd.MaNhanVien) AS TenNV, " + "COALESCE(pt.TenPTTT, hd.MaPTTT) AS TenPTTT, "
				+ "COALESCE(SUM(c.SoLuong * c.DonGia), 0) AS TongTien " + "FROM HoaDon hd "
				+ "LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang "
				+ "LEFT JOIN NhanVien nv ON hd.MaNhanVien = nv.MaNhanVien "
				+ "LEFT JOIN PhuongThucThanhToan pt ON hd.MaPTTT = pt.MaPTTT "
				+ "LEFT JOIN ChiTietHoaDon c ON hd.MaHoaDon = c.MaHoaDon " + "WHERE hd.MaHoaDon = ? OR hd.MaHoaDon = ? "
				+ "GROUP BY hd.MaHoaDon, hd.NgayLap, hd.TrangThai, hd.GhiChu, "
				+ "kh.TenKhachHang, hd.MaKhachHang, nv.TenNhanVien, hd.MaNhanVien, " + "pt.TenPTTT, hd.MaPTTT";
		Connection con = ConnectDB.getInstance().getConnection();
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, maHD);
			ps.setString(2, id2);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					sb.append("\n--- CHI TIẾT HÓA ĐƠN ").append(rs.getString("MaHoaDon")).append(" ---\n");
					sb.append("Ngày lập   : ").append(rs.getTimestamp("NgayLap").toLocalDateTime().format(DT_FMT))
							.append("\n");
					sb.append("Trạng thái : ").append(rs.getString("TrangThai")).append("\n");
					sb.append("Khách hàng : ").append(rs.getString("TenKH")).append("\n");
					sb.append("Nhân viên  : ").append(rs.getString("TenNV")).append("\n");
					sb.append("Thanh toán : ").append(rs.getString("TenPTTT")).append("\n");
					sb.append("Tổng tiền  : ").append(fmt(rs.getDouble("TongTien"))).append(" VND\n");
					String ghiChu = rs.getString("GhiChu");
					if (ghiChu != null && !ghiChu.isBlank())
						sb.append("Ghi chú    : ").append(ghiChu).append("\n");

					String sqlItems = "SELECT sp.TenSanPham, ct.SoLuong, ct.DonGia "
							+ "FROM ChiTietHoaDon ct JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham "
							+ "WHERE ct.MaHoaDon = ? OR ct.MaHoaDon = ?";
					try (PreparedStatement ps2 = con.prepareStatement(sqlItems)) {
						ps2.setString(1, maHD);
						ps2.setString(2, id2);
						try (ResultSet rs2 = ps2.executeQuery()) {
							sb.append("Sản phẩm:\n");
							while (rs2.next()) {
								sb.append("  - ").append(rs2.getString("TenSanPham")).append(" x")
										.append(rs2.getInt("SoLuong")).append(" @ ")
										.append(fmt(rs2.getDouble("DonGia"))).append(" VND\n");
							}
						}
					}
				} else {
					sb.append("\n(Không tìm thấy hóa đơn ").append(maHD).append(")\n");
				}
			}
		} catch (Exception e) {
			sb.append("\n(Lỗi truy vấn hóa đơn: ").append(e.getMessage()).append(")\n");
		}
		return sb.toString();
	}

	// Phần xây dựng danh sách hóa đơn gần nhất nếu phát hiện hỏi chung chung về hóa
	// đơn (truy vấn trực tiếp từ DB)
	private static String queryRecentInvoices(int limit) {
		StringBuilder sb = new StringBuilder();
		String sql = "SELECT TOP (?) hd.MaHoaDon, hd.NgayLap, hd.TrangThai, "
				+ "COALESCE(kh.TenKhachHang, hd.MaKhachHang) AS TenKH, "
				+ "COALESCE(SUM(c.SoLuong * c.DonGia), 0) AS TongTien " + "FROM HoaDon hd "
				+ "LEFT JOIN KhachHang kh ON hd.MaKhachHang = kh.MaKhachHang "
				+ "LEFT JOIN ChiTietHoaDon c ON hd.MaHoaDon = c.MaHoaDon "
				+ "GROUP BY hd.MaHoaDon, hd.NgayLap, hd.TrangThai, kh.TenKhachHang, hd.MaKhachHang "
				+ "ORDER BY hd.NgayLap DESC";
		Connection con = ConnectDB.getInstance().getConnection();
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, limit);
			try (ResultSet rs = ps.executeQuery()) {
				sb.append("\n--- ").append(limit).append(" HÓA ĐƠN GẦN NHẤT ---\n");
				while (rs.next()) {
					sb.append(String.format("  %-12s | %-16s | %-18s | %-22s | %s VND%n", rs.getString("MaHoaDon"),
							rs.getTimestamp("NgayLap").toLocalDateTime().format(DATE_FMT), rs.getString("TrangThai"),
							rs.getString("TenKH"), fmt(rs.getDouble("TongTien"))));
				}
			}
		} catch (Exception e) {
			sb.append("\n(Lỗi truy vấn danh sách hóa đơn: ").append(e.getMessage()).append(")\n");
		}
		return sb.toString();
	}

	// Phần xây dựng doanh thu theo từng ngày trong tháng nếu phát hiện hỏi về doanh
	// thu hoặc biểu đồ (truy vấn trực tiếp từ DB)
	private static String queryMonthDailyBreakdown() {
		LocalDate today = LocalDate.now();
		int y = today.getYear(), m = today.getMonthValue();
		StringBuilder sb = new StringBuilder();
		String sql = "SELECT DAY(hd.NgayLap) AS Ngay, COUNT(*) AS SoHD, "
				+ "COALESCE(SUM(c.SoLuong*c.DonGia), 0) AS DoanhThu "
				+ "FROM HoaDon hd LEFT JOIN ChiTietHoaDon c ON hd.MaHoaDon = c.MaHoaDon "
				+ "WHERE YEAR(hd.NgayLap)=? AND MONTH(hd.NgayLap)=? AND hd.TrangThai = N'Đã thanh toán' "
				+ "GROUP BY DAY(hd.NgayLap) ORDER BY Ngay";
		Connection con = ConnectDB.getInstance().getConnection();
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, y);
			ps.setInt(2, m);
			try (ResultSet rs = ps.executeQuery()) {
				sb.append("\n--- DOANH THU TỪNG NGÀY THÁNG ").append(m).append("/").append(y).append(" ---\n");
				while (rs.next()) {
					sb.append(String.format("  Ngày %02d/%02d: %3d HĐ — %s VND%n", rs.getInt("Ngay"), m,
							rs.getInt("SoHD"), fmt(rs.getDouble("DoanhThu"))));
				}
			}
		} catch (Exception e) {
			sb.append("\n(Lỗi truy vấn: ").append(e.getMessage()).append(")\n");
		}
		return sb.toString();
	}

	// Phần xây dựng top sản phẩm bán chạy nếu phát hiện hỏi về sản phẩm bán chạy,
	// phổ biến (truy vấn trực tiếp từ DB)
	private static String queryTopProducts(int limit) {
		StringBuilder sb = new StringBuilder();
		String sql = "SELECT TOP (?) sp.TenSanPham, SUM(ct.SoLuong) AS TongSL, "
				+ "SUM(ct.SoLuong*ct.DonGia) AS DoanhThu " + "FROM ChiTietHoaDon ct "
				+ "JOIN SanPham sp ON ct.MaSanPham = sp.MaSanPham " + "JOIN HoaDon hd ON ct.MaHoaDon = hd.MaHoaDon "
				+ "WHERE hd.TrangThai = N'Đã thanh toán' " + "GROUP BY sp.TenSanPham ORDER BY TongSL DESC";
		Connection con = ConnectDB.getInstance().getConnection();
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, limit);
			try (ResultSet rs = ps.executeQuery()) {
				sb.append("\n--- TOP ").append(limit).append(" SẢN PHẨM BÁN CHẠY ---\n");
				int rank = 1;
				while (rs.next()) {
					sb.append(String.format("  %2d. %-30s %5d cái — %s VND%n", rank++, rs.getString("TenSanPham"),
							rs.getInt("TongSL"), fmt(rs.getDouble("DoanhThu"))));
				}
			}
		} catch (Exception e) {
			sb.append("\n(Lỗi: ").append(e.getMessage()).append(")\n");
		}
		return sb.toString();
	}

	// Phần xây dựng top khách hàng chi tiêu nhiều nhất nếu phát hiện hỏi về khách
	// hàng thân thiết, mua nhiều (truy vấn trực tiếp từ DB)
	private static String queryTopCustomers(int limit) {
		StringBuilder sb = new StringBuilder();
		String sql = "SELECT TOP (?) kh.TenKhachHang, kh.SoDienThoai, " + "COUNT(hd.MaHoaDon) AS SoLanMua, "
				+ "COALESCE(SUM(c.SoLuong*c.DonGia), 0) AS TongChiTieu " + "FROM KhachHang kh "
				+ "JOIN HoaDon hd ON kh.MaKhachHang = hd.MaKhachHang "
				+ "LEFT JOIN ChiTietHoaDon c ON hd.MaHoaDon = c.MaHoaDon " + "WHERE hd.TrangThai = N'Đã thanh toán' "
				+ "GROUP BY kh.TenKhachHang, kh.SoDienThoai ORDER BY TongChiTieu DESC";
		Connection con = ConnectDB.getInstance().getConnection();
		try (PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, limit);
			try (ResultSet rs = ps.executeQuery()) {
				sb.append("\n--- TOP ").append(limit).append(" KHÁCH HÀNG CHI TIÊU NHIỀU NHẤT ---\n");
				int rank = 1;
				while (rs.next()) {
					sb.append(String.format("  %2d. %-25s (%s) — %d lần — %s VND%n", rank++,
							rs.getString("TenKhachHang"), rs.getString("SoDienThoai"), rs.getInt("SoLanMua"),
							fmt(rs.getDouble("TongChiTieu"))));
				}
			}
		} catch (Exception e) {
			sb.append("\n(Lỗi: ").append(e.getMessage()).append(")\n");
		}
		return sb.toString();
	}

	// Phần xây dựng toàn bộ tồn kho chi tiết nếu phát hiện hỏi về tồn kho, hết
	// hàng, nhập hàng, con lại (truy vấn trực tiếp từ DB)
	private static String queryFullStockStatus() {
		StringBuilder sb = new StringBuilder();
		String sql = "SELECT sp.TenSanPham, loai.TenLoaiSanPham, "
				+ "COALESCE(SUM(ls.SoLuong), 0) AS TonKho, sp.GiaThanh " + "FROM SanPham sp "
				+ "LEFT JOIN LoaiSanPham loai ON sp.MaLoaiSanPham = loai.MaLoaiSanPham "
				+ "LEFT JOIN LoSanPham ls ON sp.MaSanPham = ls.MaSanPham " + "WHERE sp.TrangThai = 1 "
				+ "GROUP BY sp.MaSanPham, sp.TenSanPham, loai.TenLoaiSanPham, sp.GiaThanh " + "ORDER BY TonKho ASC";
		Connection con = ConnectDB.getInstance().getConnection();
		try (Statement st = con.createStatement(); ResultSet rs = st.executeQuery(sql)) {
			sb.append("\n--- TỒN KHO TOÀN BỘ SẢN PHẨM ---\n");
			while (rs.next()) {
				sb.append(String.format("  %-30s | %-20s | còn %4d | giá %s VND%n", rs.getString("TenSanPham"),
						rs.getString("TenLoaiSanPham"), rs.getInt("TonKho"), fmt(rs.getDouble("GiaThanh"))));
			}
		} catch (Exception e) {
			sb.append("\n(Lỗi truy vấn tồn kho: ").append(e.getMessage()).append(")\n");
		}
		return sb.toString();
	}

	// Hàm phụ: Chuẩn hóa câu hỏi (loại bỏ dấu, chuyển về chữ thường, xóa ký tự đặc
	// biệt) để dễ dàng phát hiện từ khóa và mã hóa đơn
	private static String normalize(String s) {
		if (s == null)
			return "";
		return s.toLowerCase().replaceAll("[áàảãạăắằẳẵặâấầẩẫậ]", "a").replaceAll("[éèẻẽẹêếềểễệ]", "e")
				.replaceAll("[íìỉĩị]", "i").replaceAll("[óòỏõọôốồổỗộơớờởỡợ]", "o").replaceAll("[úùủũụưứừửữự]", "u")
				.replaceAll("[ýỳỷỹỵ]", "y").replaceAll("[đ]", "d").replaceAll("[^a-z0-9\\s\\-]", " ")
				.replaceAll("\\s+", " ").trim();
	}

	private static String extractHoaDonId(String text) {
		Matcher m = Pattern.compile("(?i)\\bHD[-]?(\\d+)\\b").matcher(text);
		return m.find() ? "HD-" + m.group(1) : null;
	}

	private static String fmt(double v) {
		return String.format(Locale.US, "%,.0f", v);
	}
}
