package gui;

import constants.Colors;
import constants.FontStyle;
import java.awt.*;
import java.text.NumberFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * Dialog hiển thị chi tiết phiếu/hóa đơn dạng card với: - Header: icon + tiêu
 * đề + nút đóng - 2 card thông tin trái-phải (LinkedHashMap label → value) -
 * Bảng danh sách sản phẩm - Khu vực tổng kết tiền (label, value, isHighlight)
 *
 * Dùng chung cho TraCuuHoaDon, TraCuuPhieuNhap, TraCuuDoiHang, TraCuuTraHang.
 */
public class ChiTietDialog extends JDialog {

    public static final NumberFormat MONEY = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    public static class SummaryRow {

        public final String label;
        public final String value;
        public final Color valueColor;
        public final boolean bold;

        public SummaryRow(String label, String value, Color valueColor, boolean bold) {
            this.label = label;
            this.value = value;
            this.valueColor = valueColor;
            this.bold = bold;
        }
    }

    public ChiTietDialog(Window parent,
            String iconEmoji,
            String title,
            String leftCardIcon, String leftCardTitle, LinkedHashMap<String, String> leftInfo,
            String rightCardIcon, String rightCardTitle, LinkedHashMap<String, String> rightInfo,
            String productSectionTitle,
            String[] columnNames,
            List<Object[]> rows,
            int[] rightAlignColumns,
            List<SummaryRow> summary) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        setLayout(new BorderLayout());
        getContentPane().setBackground(Color.WHITE);

        add(buildHeader(iconEmoji, title), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBackground(Color.WHITE);
        body.setBorder(new EmptyBorder(0, 20, 20, 20));

        // Hàng 2 card thông tin
        JPanel infoRow = new JPanel(new GridLayout(1, 2, 16, 0));
        infoRow.setOpaque(false);
        infoRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoRow.add(buildInfoCard(leftCardIcon, leftCardTitle, leftInfo));
        infoRow.add(buildInfoCard(rightCardIcon, rightCardTitle, rightInfo));
        body.add(infoRow);
        body.add(Box.createVerticalStrut(16));

        // Tiêu đề bảng sản phẩm
        if (productSectionTitle != null) {
            JLabel lblSection = new JLabel(productSectionTitle);
            lblSection.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
            lblSection.setForeground(Colors.TEXT_PRIMARY);
            lblSection.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(lblSection);
            body.add(Box.createVerticalStrut(8));
        }

        // Bảng sản phẩm
        if (columnNames != null && rows != null) {
            JComponent table = buildProductTable(columnNames, rows, rightAlignColumns);
            table.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(table);
            body.add(Box.createVerticalStrut(16));
        }

        // Tổng kết
        if (summary != null && !summary.isEmpty()) {
            JPanel summaryCard = buildSummaryCard(summary);
            summaryCard.setAlignmentX(Component.LEFT_ALIGNMENT);
            body.add(summaryCard);
        }

        JScrollPane scroll = new JScrollPane(body,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.getViewport().setBackground(Color.WHITE);
        add(scroll, BorderLayout.CENTER);

        setSize(900, 720);
        setMinimumSize(new Dimension(820, 560));
        setLocationRelativeTo(parent);
    }

    // ─── Header ──────────────────────────────────────────────
    private JPanel buildHeader(String iconEmoji, String title) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setBorder(new EmptyBorder(16, 20, 16, 16));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        left.setOpaque(false);
        JLabel icon = new JLabel(iconEmoji);
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 22));
        icon.setForeground(Colors.PRIMARY);
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblTitle.setForeground(Colors.TEXT_PRIMARY);
        left.add(icon);
        left.add(lblTitle);
        header.add(left, BorderLayout.WEST);

        JButton btnClose = new JButton("\u2715");
        btnClose.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnClose.setForeground(Colors.TEXT_SECONDARY);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setFocusPainted(false);
        btnClose.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> dispose());
        header.add(btnClose, BorderLayout.EAST);
        return header;
    }

    // ─── Card thông tin (label : value) ──────────────────────
    private JPanel buildInfoCard(String iconEmoji, String title, LinkedHashMap<String, String> info) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 247, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        // Tiêu đề card
        JPanel titleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        titleRow.setOpaque(false);
        titleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel ic = new JLabel(iconEmoji);
        ic.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        ic.setForeground(Colors.PRIMARY);
        JLabel lblT = new JLabel(title);
        lblT.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblT.setForeground(Colors.TEXT_PRIMARY);
        titleRow.add(ic);
        titleRow.add(lblT);
        card.add(titleRow);
        card.add(Box.createVerticalStrut(10));

        // Các dòng label : value
        for (var entry : info.entrySet()) {
            JPanel row = new JPanel(new BorderLayout(8, 0));
            row.setOpaque(false);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            row.setBorder(new EmptyBorder(2, 0, 2, 0));

            JLabel lblK = new JLabel(entry.getKey() + ":");
            lblK.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            lblK.setForeground(Colors.TEXT_SECONDARY);
            lblK.setPreferredSize(new Dimension(95, 22));

            String raw = entry.getValue() == null ? "" : entry.getValue();
            // Nếu value có chứa thẻ HTML thì giữ nguyên, ngược lại escape an toàn
            String html = raw.contains("<") && raw.contains(">")
                    ? "<html>" + raw + "</html>"
                    : "<html>" + escape(raw) + "</html>";
            JLabel lblV = new JLabel(html);
            lblV.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            lblV.setForeground(Colors.TEXT_PRIMARY);

            row.add(lblK, BorderLayout.WEST);
            row.add(lblV, BorderLayout.CENTER);
            card.add(row);
        }
        return card;
    }

    // ─── Bảng sản phẩm ────────────────────────────────────────
    private JComponent buildProductTable(String[] cols, List<Object[]> rows, int[] rightCols) {
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        for (Object[] r : rows) {
            model.addRow(r);
        }

        JTable table = new JTable(model);
        table.setRowHeight(34);
        table.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFillsViewportHeight(true);
        table.setBackground(Color.WHITE);

        JTableHeader header = table.getTableHeader();
        header.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        header.setForeground(Colors.TEXT_PRIMARY);
        header.setBackground(new Color(248, 249, 251));
        header.setPreferredSize(new Dimension(0, 36));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                setHorizontalAlignment(SwingConstants.RIGHT);
                setBorder(new EmptyBorder(0, 8, 0, 12));
                return comp;
            }
        };
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean s, boolean f, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, s, f, r, c);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(new EmptyBorder(0, 12, 0, 8));
                return comp;
            }
        };
        for (int i = 0; i < cols.length; i++) {
            boolean isRight = false;
            if (rightCols != null) {
                for (int rc : rightCols) {
                    if (rc == i) {
                        isRight = true;
                        break;
                    }
                }
            }
            table.getColumnModel().getColumn(i).setCellRenderer(isRight ? rightRenderer : leftRenderer);
        }

        JScrollPane sp = new JScrollPane(table);
        sp.setBorder(BorderFactory.createLineBorder(new Color(230, 233, 238), 1));
        int h = Math.min(rows.size(), 8) * 34 + 38;
        sp.setPreferredSize(new Dimension(0, Math.max(120, h)));
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(120, h)));
        return sp;
    }

    // ─── Card tổng kết ────────────────────────────────────────
    private JPanel buildSummaryCard(List<SummaryRow> rows) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(245, 247, 250));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(14, 16, 14, 16));

        for (SummaryRow r : rows) {
            JPanel row = new JPanel(new BorderLayout());
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(4, 0, 4, 0));
            JLabel lblK = new JLabel(r.label + ":");
            lblK.setFont(FontStyle.font(FontStyle.SM, r.bold ? FontStyle.BOLD : FontStyle.NORMAL));
            lblK.setForeground(r.bold ? Colors.TEXT_PRIMARY : Colors.TEXT_SECONDARY);

            JLabel lblV = new JLabel(r.value);
            lblV.setHorizontalAlignment(SwingConstants.RIGHT);
            lblV.setFont(FontStyle.font(r.bold ? FontStyle.LG : FontStyle.SM, FontStyle.BOLD));
            lblV.setForeground(r.valueColor != null ? r.valueColor : Colors.TEXT_PRIMARY);

            row.add(lblK, BorderLayout.WEST);
            row.add(lblV, BorderLayout.EAST);
            card.add(row);
        }
        return card;
    }

    private String escape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
