package exception;

import constants.Colors;
import constants.FontStyle;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.basic.ComboPopup;

import java.awt.*;

// ComboBox bo tròn, đồng bộ với RoundedTextField.
public class RoundedComboBox<E> extends JComboBox<E> {

	private final int radius;

	public RoundedComboBox(int radius) {
		this.radius = radius;
		setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		setBackground(Colors.BACKGROUND);
		setForeground(Colors.TEXT_PRIMARY);
		setOpaque(false);
		setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 8));
		setUI(new RoundedComboUI());
		setRenderer(new RoundedListRenderer());
	}

	public RoundedComboBox() {
		this(10);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(getBackground());
		g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
		super.paintComponent(g2);
		g2.dispose();
	}

	@Override
	protected void paintBorder(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Colors.BORDER_LIGHT);
		g2.setStroke(new BasicStroke(1.2f));
		g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
		g2.dispose();
	}

	// ── Custom UI ──
	private class RoundedComboUI extends BasicComboBoxUI {
		@Override
		protected JButton createArrowButton() {
			JButton btn = new JButton() {
				@Override
				public void paintComponent(Graphics g) {
					Graphics2D g2 = (Graphics2D) g.create();
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setColor(getParent().getBackground());
					g2.fillRect(0, 0, getWidth(), getHeight());
					// Vẽ mũi tên
					g2.setColor(Colors.MUTED);
					int cx = getWidth() / 2, cy = getHeight() / 2;
					int[] xp = { cx - 5, cx + 5, cx };
					int[] yp = { cy - 2, cy - 2, cy + 4 };
					g2.fillPolygon(xp, yp, 3);
					g2.dispose();
				}
			};
			btn.setBorder(BorderFactory.createEmptyBorder());
			btn.setPreferredSize(new Dimension(28, 28));
			btn.setFocusPainted(false);
			btn.setContentAreaFilled(false);
			return btn;
		}

		@Override
		protected ComboPopup createPopup() {
			BasicComboPopup popup = (BasicComboPopup) super.createPopup();
			popup.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1));
			return popup;
		}
	}

	// ── Custom list renderer ──
	private class RoundedListRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList<?> list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
			setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
			if (isSelected) {
				setBackground(Colors.PRIMARY_LIGHT);
				setForeground(Colors.TEXT_PRIMARY);
			} else {
				setBackground(Colors.BACKGROUND);
				setForeground(Colors.TEXT_PRIMARY);
			}
			return this;
		}
	}
}
