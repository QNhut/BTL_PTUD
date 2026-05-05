package exception;

import java.awt.*;
import java.awt.event.*;
import java.util.function.BiConsumer;

import javax.swing.*;
import javax.swing.table.*;

import constants.Colors;
import constants.FontStyle;

/**
 * Cell Editor cho cột "Thao tác" trong bảng sản phẩm. Hiển thị 4 nút: Xem | Chi
 * tiết | Sửa | Xóa
 * 
 * Cách dùng: table.getColumnModel().getColumn(6).setCellEditor( new
 * ProductActionEditor(table, (action, row) -> { switch (action) { case "XEM":
 * xemChiTiet(row); break; case "CHI_TIET": moChiTiet(row); break; case "SUA":
 * suaSanPham(row); break; case "XOA": xoaSanPham(row); break; } }) );
 */
public class ProductActionEditor extends AbstractCellEditor implements TableCellEditor {

	private final JPanel panel;
	private final JTable table;
	private final BiConsumer<String, Integer> handler;
	private int currentRow;

	private static final int ROW_HEIGHT = 80;

	public ProductActionEditor(JTable table, BiConsumer<String, Integer> handler) {
		this.table = table;
		this.handler = handler;
		this.panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, (ROW_HEIGHT - 30) / 2));
		panel.setOpaque(true);
		panel.setBackground(Colors.BACKGROUND);

		panel.add(textBtn("Chi tiết", Colors.PRIMARY_LIGHT, Colors.SUCCESS_DARK, "CHI_TIET"));
		panel.add(textBtn("Sửa", Colors.SECONDARY, Colors.ACCENT, "SUA"));
		panel.add(textBtn("Xoá", Colors.SECONDARY, Colors.DANGER, "XOA"));
	}

	private JButton iconBtn(String icon, Color color, String action) {
		JButton btn = new JButton(icon);
		btn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
		btn.setForeground(color);
		btn.setPreferredSize(new Dimension(30, 28));
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(true);
		btn.setBackground(Colors.SECONDARY);
		btn.setOpaque(true);
		btn.addActionListener(e -> {
			fireEditingStopped();
			if (handler != null)
				handler.accept(action, currentRow);
		});
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(Colors.BORDER_LIGHT);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(Colors.SECONDARY);
			}
		});
		return btn;
	}

	private JButton textBtn(String text, Color bg, Color fg, String action) {
		JButton btn = new JButton(text);
		btn.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
		btn.setForeground(fg);
		btn.setBackground(bg);
		btn.setOpaque(true);
		btn.setFocusPainted(false);
		btn.setBorderPainted(false);
		btn.setContentAreaFilled(true);
		btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		btn.setPreferredSize(new Dimension(52, 28));
		btn.addActionListener(e -> {
			fireEditingStopped();
			if (handler != null)
				handler.accept(action, currentRow);
		});
		btn.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				btn.setBackground(Colors.GREEN_HOVER);
			}

			@Override
			public void mouseExited(MouseEvent e) {
				btn.setBackground(bg);
			}
		});
		return btn;
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		this.currentRow = row;
		panel.setBackground(isSelected ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
		return panel;
	}

	@Override
	public Object getCellEditorValue() {
		return null;
	}
}
