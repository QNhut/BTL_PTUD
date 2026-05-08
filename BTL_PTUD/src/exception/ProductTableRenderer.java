package exception;

import java.awt.*;
import java.text.DecimalFormat;
import javax.swing.*;
import javax.swing.table.*;

import constants.Colors;
import constants.FontStyle;
import service.ImageCache;

// Custom JTable renderer cho bảng sản phẩm dạng list (như hình thiết kế).
// Cách dùng: String[] columns = {"SẢN PHẨM", "DANH MỤC", "GIÁ BÁN", "TỒN KHO",
// "LÔ HÀNG", "TRẠNG THÁI", "THAO TÁC"}; DefaultTableModel model = new
// DefaultTableModel(columns, 0); JTable table = new JTable(model);
// ProductTableRenderer.apply(table);
// // Thêm dòng: model.addRow(new Object[]{ new Object[]{"SP001", "Paracetamol
// 500mg", "path/to/img.png"}, // col 0: SẢN PHẨM "Giảm đau - Hạ sốt", // col 1:
// DANH MỤC 25000.0, // col 2: GIÁ BÁN 450, // col 3: TỒN KHO new Object[]{3, 1,
// 1, "15/1/2026"}, // col 4: LÔ HÀNG {soLo, hetHan, sapHet, ngayGanNhat}
// "CON_HANG", // col 5: TRẠNG THÁI null // col 6: THAO TÁC });
public class ProductTableRenderer extends JPanel implements TableCellRenderer {

	private static final DecimalFormat PRICE_FMT = new DecimalFormat("#,###");
	private static final int ROW_HEIGHT = 80;

	private final int columnIndex;
	private JTable parentTable; // set lazily on first render

	private ProductTableRenderer(int columnIndex) {
		this.columnIndex = columnIndex;
		setOpaque(true);
	}

	// Áp dụng renderer cho tất cả cột của bảng sản phẩm.
	public static void apply(JTable table) {
		table.setRowHeight(ROW_HEIGHT);
		table.setShowGrid(false);
		table.setIntercellSpacing(new Dimension(0, 0));
		table.setBackground(Colors.BACKGROUND);
		table.setSelectionBackground(Colors.PRIMARY_LIGHT);
		table.setSelectionForeground(Colors.TEXT_PRIMARY);
		table.getTableHeader().setReorderingAllowed(false);
		table.getTableHeader().setDefaultRenderer(new HeaderRenderer());

		// Column widths
		int[] widths = { 220, 160, 100, 80, 200, 110, 210 };
		for (int i = 0; i < Math.min(widths.length, table.getColumnCount()); i++) {
			TableColumn col = table.getColumnModel().getColumn(i);
			col.setPreferredWidth(widths[i]);
			col.setCellRenderer(new ProductTableRenderer(i));
		}

		// Không cho edit
		if (table.getModel() instanceof DefaultTableModel) {
			// Override ở model
		}
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {

		parentTable = table; // cập nhật để callback repaint sử dụng
		removeAll();
		setBackground(isSelected ? Colors.PRIMARY_LIGHT : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
		setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));

		switch (columnIndex) {
		case 0:
			return renderSanPham(value, isSelected, row);
		case 1:
			return renderDanhMuc(value, isSelected, row);
		case 2:
			return renderGia(value, isSelected, row);
		case 3:
			return renderTonKho(value, isSelected, row);
		case 4:
			return renderLoHang(value, isSelected, row);
		case 5:
			return renderTrangThai(value, isSelected, row);
		case 6:
			return renderThaoTac(isSelected, row);
		default:
			return this;
		}
	}

	// ==================== COL 0: SẢN PHẨM ====================
	private JPanel renderSanPham(Object value, boolean isSelected, int row) {
		JPanel p = basePanel(isSelected, row);
		p.setLayout(new BorderLayout(10, 0));
		p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
				BorderFactory.createEmptyBorder(8, 12, 8, 8)));

		// Ảnh thumbnail
		JLabel imgLabel = new JLabel();
		imgLabel.setPreferredSize(new Dimension(50, 50));
		imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
		imgLabel.setVerticalAlignment(SwingConstants.CENTER);
		imgLabel.setOpaque(true);
		imgLabel.setBackground(Colors.SECONDARY);

		String maSP = "", tenSP = "", hinhAnh = "";
		if (value instanceof Object[]) {
			Object[] data = (Object[]) value;
			if (data.length > 0 && data[0] != null)
				maSP = data[0].toString();
			if (data.length > 1 && data[1] != null)
				tenSP = data[1].toString();
			if (data.length > 2 && data[2] != null)
				hinhAnh = data[2].toString();
		}

		// Load ảnh từ cache
		if (!hinhAnh.isEmpty()) {
			String fileName = hinhAnh;
			if (hinhAnh.contains("/"))
				fileName = hinhAnh.substring(hinhAnh.lastIndexOf('/') + 1);

			ImageIcon cached = ImageCache.getInstance().getCached(fileName, 46, 46);
			if (cached != null) {
				imgLabel.setIcon(cached);
			} else {
				// Load nền, khi xong repaint bảng
				ImageCache.getInstance().getImage(fileName, 46, 46, icon -> {
					if (parentTable != null && parentTable.isDisplayable())
						parentTable.repaint();
				});
			}
		} else {
			imgLabel.setText("SP");
			imgLabel.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
			imgLabel.setForeground(Colors.MUTED);
		}

		p.add(imgLabel, BorderLayout.WEST);

		// Mã + Tên SP
		JPanel info = new JPanel();
		info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
		info.setOpaque(false);
		info.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));

		JLabel lblMa = new JLabel(maSP);
		lblMa.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		lblMa.setForeground(Colors.MUTED);
		info.add(lblMa);
		info.add(Box.createVerticalStrut(2));

		// Tên SP cắt ngắn nếu dài
		if (tenSP.length() > 24)
			tenSP = tenSP.substring(0, 24) + "...";
		JLabel lblTen = new JLabel(tenSP);
		lblTen.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblTen.setForeground(Colors.TEXT_PRIMARY);
		info.add(lblTen);

		p.add(info, BorderLayout.CENTER);
		return p;
	}

	// ==================== COL 1: DANH MỤC ====================
	private JPanel renderDanhMuc(Object value, boolean isSelected, int row) {
		JPanel p = cellPanel(isSelected, row);
		String text = value != null ? value.toString() : "";
		JLabel lbl = new JLabel(text);
		lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lbl.setForeground(Colors.SUCCESS);
		p.add(lbl);
		return p;
	}

	// ==================== COL 2: GIÁ BÁN ====================
	private JPanel renderGia(Object value, boolean isSelected, int row) {
		JPanel p = cellPanel(isSelected, row);
		p.setLayout(new javax.swing.BoxLayout(p, javax.swing.BoxLayout.Y_AXIS));

		double giaGoc = 0, giaSale = 0;
		boolean coKM = false;
		if (value instanceof entity.SanPham) {
			entity.SanPham sp = (entity.SanPham) value;
			giaGoc = sp.getGiaThanh();
			giaSale = sp.getGiaSauKM();
			coKM = sp.coKhuyenMai();
		} else if (value instanceof Number) {
			giaGoc = ((Number) value).doubleValue();
			giaSale = giaGoc;
		}

		JLabel lblSale = new JLabel(PRICE_FMT.format(giaSale) + "đ");
		lblSale.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblSale.setForeground(Colors.ACCENT);
		lblSale.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
		p.add(lblSale);

		if (coKM) {
			JLabel lblGoc = new JLabel("<html><span style='text-decoration:line-through;'>"
					+ PRICE_FMT.format(giaGoc) + "đ</span></html>");
			lblGoc.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
			lblGoc.setForeground(Colors.MUTED);
			lblGoc.setAlignmentX(java.awt.Component.LEFT_ALIGNMENT);
			p.add(lblGoc);
		}
		return p;
	}

	// ==================== COL 3: TỒN KHO ====================
	private JPanel renderTonKho(Object value, boolean isSelected, int row) {
		JPanel p = cellPanel(isSelected, row);
		int ton = 0;
		if (value instanceof Number)
			ton = ((Number) value).intValue();
		JLabel lbl = new JLabel(String.valueOf(ton));
		lbl.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
		lbl.setForeground(Colors.TEXT_PRIMARY);
		p.add(lbl);
		return p;
	}

	// ==================== COL 4: LÔ HÀNG ====================
	private JPanel renderLoHang(Object value, boolean isSelected, int row) {
		JPanel p = basePanel(isSelected, row);
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));

		int soLo = 0, hetHan = 0, sapHet = 0;
		String ngayGanNhat = "";

		if (value instanceof Object[]) {
			Object[] data = (Object[]) value;
			if (data.length > 0 && data[0] instanceof Number)
				soLo = ((Number) data[0]).intValue();
			if (data.length > 1 && data[1] instanceof Number)
				hetHan = ((Number) data[1]).intValue();
			if (data.length > 2 && data[2] instanceof Number)
				sapHet = ((Number) data[2]).intValue();
			if (data.length > 3 && data[3] != null)
				ngayGanNhat = data[3].toString();
		}

		// Dòng 1: "X lô"
		JLabel lblLo = new JLabel(soLo + " lô");
		lblLo.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		lblLo.setForeground(Colors.TEXT_PRIMARY);
		p.add(lblLo);

		// Dòng 2: "Gần nhất: dd/MM/yyyy"
		if (!ngayGanNhat.isEmpty()) {
			JLabel lblNgay = new JLabel("Gần nhất: " + ngayGanNhat);
			lblNgay.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
			lblNgay.setForeground(Colors.MUTED);
			p.add(lblNgay);
		}

		// Dòng 3: cảnh báo
		StringBuilder warn = new StringBuilder();
		if (hetHan > 0)
			warn.append("! ").append(hetHan).append(" hết hạn");
		if (sapHet > 0) {
			if (warn.length() > 0)
				warn.append(" - ");
			warn.append(sapHet).append(" sắp hết");
		}
		if (warn.length() > 0) {
			JLabel lblWarn = new JLabel(warn.toString());
			lblWarn.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
			lblWarn.setForeground(Colors.ACCENT);
			p.add(lblWarn);
		}

		return p;
	}

	// ==================== COL 5: TRẠNG THÁI ====================
	private JPanel renderTrangThai(Object value, boolean isSelected, int row) {
		JPanel p = cellPanel(isSelected, row);

		String tt = value != null ? value.toString() : "HET_HANG";
		String text;
		Color bg, fg;
		if ("CON_HANG".equals(tt)) {
			text = "Còn hàng";
			bg = Colors.GREEN_HOVER;
			fg = Colors.SUCCESS_DARK;
		} else if ("SAP_HET".equals(tt)) {
			text = "Sắp hết";
			bg = Colors.YELLOW_HOVER;
			fg = Colors.ACCENT;
		} else {
			text = "Hết hàng";
			bg = Colors.BROWN_HOVER;
			fg = Colors.DANGER;
		}

		JLabel badge = new JLabel(text);
		badge.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		badge.setForeground(fg);
		badge.setOpaque(true);
		badge.setBackground(bg);
		badge.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
		p.add(badge);
		return p;
	}

	// ==================== COL 6: THAO TÁC ====================
	private JPanel renderThaoTac(boolean isSelected, int row) {
		JPanel p = basePanel(isSelected, row);
		p.setLayout(new FlowLayout(FlowLayout.CENTER, 4, (ROW_HEIGHT - 30) / 2));
		p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));

		p.add(textBtn("Chi tiết", Colors.PRIMARY_LIGHT, Colors.SUCCESS_DARK));
		p.add(textBtn("Sửa", Colors.SECONDARY, Colors.ACCENT));
		p.add(textBtn("Xoá", Colors.SECONDARY, Colors.DANGER));

		return p;
	}

	// ==================== HELPERS ====================

	private JPanel basePanel(boolean isSelected, int row) {
		JPanel p = new JPanel();
		p.setOpaque(true);
		p.setBackground(isSelected ? Colors.PRIMARY_LIGHT : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
		return p;
	}

	private JPanel cellPanel(boolean isSelected, int row) {
		JPanel p = basePanel(isSelected, row);
		p.setLayout(new FlowLayout(FlowLayout.LEFT, 10, (ROW_HEIGHT - 20) / 2));
		p.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));
		return p;
	}

	private JLabel iconBtn(String icon, Color color) {
		JLabel lbl = new JLabel(icon);
		lbl.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		lbl.setForeground(color);
		lbl.setPreferredSize(new Dimension(40, 28));
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
		lbl.setOpaque(true);
		lbl.setBackground(Colors.SECONDARY);
		lbl.setBorder(BorderFactory.createEmptyBorder(4, 6, 4, 6));
		return lbl;
	}

	private JLabel textBtn(String text, Color bg, Color fg) {
		JLabel lbl = new JLabel(text);
		lbl.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		lbl.setForeground(fg);
		lbl.setOpaque(true);
		lbl.setBackground(bg);
		lbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
		lbl.setCursor(new Cursor(Cursor.HAND_CURSOR));
		return lbl;
	}

	// ==================== HEADER RENDERER ====================
	private static class HeaderRenderer extends JLabel implements TableCellRenderer {
		HeaderRenderer() {
			setOpaque(true);
			setBackground(Colors.SECONDARY);
			setForeground(Colors.MUTED);
			setBorder(
					BorderFactory.createCompoundBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, Colors.BORDER_LIGHT),
							BorderFactory.createEmptyBorder(10, 12, 10, 12)));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			setText(value != null ? value.toString() : "");
			setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
			return this;
		}
	}
}
