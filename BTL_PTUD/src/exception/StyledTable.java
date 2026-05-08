package exception;

import constants.Colors;
import constants.FontStyle;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.MatteBorder;
import javax.swing.table.*;

// Bảng styled có thể tái sử dụng cho bất kỳ entity nào.
// <p>
// Cấu trúc file:</p>
// <ol>
// <li>Fields</li>
// <li>Constructor</li>
// <li>Khởi tạo nội bộ (initTable, styleHeader, installTableClickListener)</li>
// <li>Public API – Cấu hình cơ bản (getTable, setRowHeight, setColumnWidth,
// setColumnRenderer, refresh)</li>
// <li>Public API – Loại cột dựng sẵn (setAvatarColumn, setTwoLineColumn,
// ...)</li>
// <li>Public API – Xử lý sự kiện click (setActionColumnListener,
// setDeleteColumnListener)</li>
// <li>Tiện ích nội bộ (AVATAR_COLORS, getInitials, getAvatarColor)</li>
// <li>Renderer nội bộ (các inner class)</li>
// </ol>
public class StyledTable extends JScrollPane {

    // =========================================================================
    // 1. FIELDS
    // =========================================================================
    private final String[] columnNames; // Tên các cột hiển thị trên header
    private final List<?> data;         // Nguồn dữ liệu; mỗi phần tử = 1 dòng
    private JTable table;
    private AbstractTableModel model;

    // Chỉ số cột thao tác – được gán khi gọi setActionColumn / setDeleteButtonColumn
    private int actionColumnIndex = -1;
    private int deleteColumnIndex = -1;

    // Callback xử lý click từng loại cột thao tác
    private java.util.function.BiConsumer<Integer, Object> actionListener;
    private java.util.function.BiConsumer<Integer, Object> deleteListener;

    // Cờ đảm bảo chỉ đăng ký 1 MouseListener duy nhất
    private boolean clickListenerInstalled = false;

    // =========================================================================
    // 2. CONSTRUCTOR
    // =========================================================================
    // Khởi tạo bảng với tên cột và nguồn dữ liệu.
    // @param columnNames Mảng tên cột hiển thị trên header.
    // @param data List dữ liệu; mỗi phần tử tương ứng một dòng.
    public StyledTable(String[] columnNames, List<?> data) {
        this.columnNames = columnNames;
        this.data = data;
        initTable();
    }

    // =========================================================================
    // 3. KHỞI TẠO NỘI BỘ
    // =========================================================================
    // Tạo JTable, gán model, áp style cho header và đăng ký mouse listener.
    // Được gọi duy nhất từ constructor.
    private void initTable() {
        model = new AbstractTableModel() {
            @Override
            public int getRowCount() {
                return data.size();
            }

            @Override
            public int getColumnCount() {
                return columnNames.length;
            }

            @Override
            public String getColumnName(int col) {
                return columnNames[col];
            }

            @Override
            public Object getValueAt(int row, int col) {
                return data.get(row);
            }

            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        table = new JTable(model);
        table.setRowHeight(75);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Colors.BACKGROUND);
        table.setSelectionBackground(Colors.PRIMARY_LIGHT);
        table.setSelectionForeground(Colors.TEXT_PRIMARY);
        table.setFocusable(false);
        table.setFillsViewportHeight(true);

        styleHeader();

        setViewportView(table);
        setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        getViewport().setBackground(Colors.BACKGROUND);

        installTableClickListener();
    }

    // Áp dụng font, màu sắc và border cho phần header của bảng.
    private void styleHeader() {
        JTableHeader header = table.getTableHeader();
        header.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        header.setForeground(Colors.TEXT_SECONDARY);
        header.setBackground(Colors.SECONDARY);
        header.setBorder(new MatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 45));
        header.setReorderingAllowed(false);

        header.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
                lbl.setForeground(Colors.TEXT_SECONDARY);
                lbl.setBackground(Colors.SECONDARY);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        new MatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
                        BorderFactory.createEmptyBorder(0, 15, 0, 0)));
                lbl.setHorizontalAlignment(SwingConstants.LEFT);
                return lbl;
            }
        });
    }

    // Đăng ký một MouseListener duy nhất để điều phối click đến đúng callback.
    // Nhờ cờ {@code clickListenerInstalled}, phương thức này an toàn khi gọi
    // nhiều lần.
    private void installTableClickListener() {
        if (clickListenerInstalled) {
            return;
        }
        clickListenerInstalled = true;

        table.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row < 0 || col < 0) {
                    return;
                }

                Object rowData = data.get(row);
                if (col == actionColumnIndex && actionListener != null) {
                    actionListener.accept(row, rowData);
                } else if (col == deleteColumnIndex && deleteListener != null) {
                    deleteListener.accept(row, rowData);
                }
            }
        });
    }

    // =========================================================================
    // 4. PUBLIC API – CẤU HÌNH CƠ BẢN
    // =========================================================================
    // Trả về đối tượng JTable gốc để tùy biến thêm khi cần (thêm sorter,
    // listener, v.v.).
    public JTable getTable() {
        return table;
    }

    // Thiết lập chiều cao (pixel) cho tất cả các dòng.
    public void setRowHeight(int height) {
        table.setRowHeight(height);
    }

    // Thiết lập độ rộng ưu tiên cho một cột theo chỉ số.
    public void setColumnWidth(int colIndex, int width) {
        table.getColumnModel().getColumn(colIndex).setPreferredWidth(width);
    }

    // Gán renderer tùy chỉnh bất kỳ cho một cột.
    public void setColumnRenderer(int colIndex, TableCellRenderer renderer) {
        table.getColumnModel().getColumn(colIndex).setCellRenderer(renderer);
    }

    // Buộc bảng vẽ lại toàn bộ sau khi dữ liệu trong list thay đổi. Phải gọi
    // mỗi khi thêm / xóa / sửa phần tử trong list nguồn.
    public void refresh() {
        model.fireTableDataChanged();
    }

    // =========================================================================
    // 5. PUBLIC API – LOẠI CỘT DỰNG SẴN
    // =========================================================================
    // Cột avatar hình tròn (initials) kèm 2 dòng text: tên chính và thông tin
    // phụ.
    // @param nameFunc Lambda lấy tên chính từ object dòng (hiển thị in đậm).
    // @param subFunc Lambda lấy thông tin phụ (mã, email...) từ object dòng.
    public void setAvatarColumn(int colIndex, int width,
            Function<Object, String> nameFunc, Function<Object, String> subFunc) {
        setColumnWidth(colIndex, width);
        setColumnRenderer(colIndex, new AvatarTwoLineRenderer(nameFunc, subFunc, null));
    }

    // Cột avatar hình tròn có thể hiển thị ảnh đại diện (nếu có), nếu không sẽ
    // fallback về initials + màu nền.
    // @param imagePathFunc Lambda lấy đường dẫn ảnh từ object dòng.
    public void setAvatarColumn(int colIndex, int width,
            Function<Object, String> nameFunc,
            Function<Object, String> subFunc,
            Function<Object, String> imagePathFunc) {
        setColumnWidth(colIndex, width);
        setColumnRenderer(colIndex, new AvatarTwoLineRenderer(nameFunc, subFunc, imagePathFunc));
    }

    // Cột 2 dòng chữ: dòng 1 in đậm (tên / tiêu đề), dòng 2 nhỏ hơn (mô tả
    // phụ).
    public void setTwoLineColumn(int colIndex, int width,
            Function<Object, String> line1Func, Function<Object, String> line2Func) {
        setColumnWidth(colIndex, width);
        setColumnRenderer(colIndex, new TwoLineRenderer(line1Func, line2Func));
    }

    // Cột 2 dòng, mỗi dòng có ký tự icon đầu dòng (ví dụ: ✉ email, ☎ sđt).
    // @param icon1 Ký tự icon dòng 1 (unicode hoặc emoji).
    // @param text1Func Lambda lấy text dòng 1.
    // @param icon2 Ký tự icon dòng 2.
    // @param text2Func Lambda lấy text dòng 2.
    public void setIconTwoLineColumn(int colIndex, int width,
            String icon1, Function<Object, String> text1Func,
            String icon2, Function<Object, String> text2Func) {
        setColumnWidth(colIndex, width);
        setColumnRenderer(colIndex, new IconTwoLineRenderer(icon1, text1Func, icon2, text2Func));
    }

    // Cột đơn giản với 1 dòng chữ căn giữa theo chiều dọc.
    public void setSingleTextColumn(int colIndex, int width,
            Function<Object, String> textFunc) {
        setColumnWidth(colIndex, width);
        setColumnRenderer(colIndex, new SingleTextRenderer(textFunc));
    }

    // Cột badge trạng thái: chấm màu + nhãn, màu thay đổi theo trạng thái
    // active/inactive.
    // @param activeFunc Lambda trả về {@code true} nếu đang hoạt động.
    // @param activeText Nhãn khi đang hoạt động (ví dụ: "Đang làm").
    // @param inactiveText Nhãn khi không hoạt động (ví dụ: "Nghỉ việc").
    public void setBadgeColumn(int colIndex, int width,
            Function<Object, Boolean> activeFunc,
            String activeText, String inactiveText) {
        setColumnWidth(colIndex, width);
        setColumnRenderer(colIndex, new BadgeRenderer(activeFunc, activeText, inactiveText));
    }

    // Cột nút "Chi tiết" — khi click sẽ kích hoạt callback đã đăng ký qua
    // {@link #setActionColumnListener}.
    public void setActionColumn(int colIndex, int width) {
        actionColumnIndex = colIndex;
        setColumnWidth(colIndex, width);
        setColumnRenderer(colIndex, new ActionDotsRenderer());
    }

    // Cột nút "Xóa" màu đỏ — khi click sẽ kích hoạt callback đã đăng ký qua
    // {@link #setDeleteColumnListener}.
    public void setDeleteButtonColumn(int colIndex, int width) {
        deleteColumnIndex = colIndex;
        setColumnWidth(colIndex, width);
        setColumnRenderer(colIndex, new DeleteButtonRenderer());
    }

    // =========================================================================
    // 6. PUBLIC API – XỬ LÝ SỰ KIỆN CLICK
    // =========================================================================
    // Đăng ký callback được gọi khi người dùng click vào cột "Chi tiết".
    // @param listener BiConsumer nhận (chỉ số dòng, object dữ liệu dòng đó).
    public void setActionColumnListener(java.util.function.BiConsumer<Integer, Object> listener) {
        this.actionListener = listener;
    }

    // Đăng ký callback được gọi khi người dùng click vào cột "Xóa".
    // @param listener BiConsumer nhận (chỉ số dòng, object dữ liệu dòng đó).
    public void setDeleteColumnListener(java.util.function.BiConsumer<Integer, Object> listener) {
        this.deleteListener = listener;
    }

    // =========================================================================
    // 7. TIỆN ÍCH NỘI BỘ (chỉ dùng bởi các renderer bên dưới)
    // =========================================================================
    // Bảng màu cố định dùng cho avatar, xoay vòng theo hashCode của tên
    private static final Color[] AVATAR_COLORS = {
        new Color(76, 175, 80), new Color(33, 150, 243),
        new Color(255, 152, 0), new Color(156, 39, 176),
        new Color(0, 188, 212), new Color(244, 67, 54),};

    // Trích chữ cái đầu của từ đầu và từ cuối trong tên đầy đủ. Ví dụ: "Nguyễn
    // Văn An" → "NA", "Admin" → "A".
    private static String getInitials(String name) {
        if (name == null || name.isEmpty()) {
            return "?";
        }
        String[] parts = name.trim().split("\\s+");
        if (parts.length >= 2) {
            return ("" + parts[0].charAt(0) + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
        return ("" + parts[0].charAt(0)).toUpperCase();
    }

    // Chọn màu avatar dựa trên hashCode của tên. Cùng tên → cùng màu, đảm bảo
    // nhất quán khi bảng được vẽ lại.
    private static Color getAvatarColor(String name) {
        if (name == null) {
            return AVATAR_COLORS[0];
        }
        return AVATAR_COLORS[Math.abs(name.hashCode()) % AVATAR_COLORS.length];
    }

    // =========================================================================
    // 8. RENDERER NỘI BỘ
    // =========================================================================
    // Vẽ avatar tròn (initials) + tên in đậm + dòng phụ nhỏ hơn.
    private static class AvatarTwoLineRenderer extends JPanel implements TableCellRenderer {

        private final Function<Object, String> nameFunc;
        private final Function<Object, String> subFunc;
        private final Function<Object, String> imagePathFunc;
        private String name, sub, initials;
        private Color avatarColor;
        private String imagePath;
        private static final java.util.Map<String, Image> AVATAR_IMAGE_CACHE = new java.util.HashMap<>();

        AvatarTwoLineRenderer(Function<Object, String> nameFunc,
                Function<Object, String> subFunc,
                Function<Object, String> imagePathFunc) {
            this.nameFunc = nameFunc;
            this.subFunc = subFunc;
            this.imagePathFunc = imagePathFunc;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            this.name = nameFunc.apply(value);
            this.sub = subFunc.apply(value);
            this.initials = getInitials(name);
            this.avatarColor = getAvatarColor(name);
            this.imagePath = imagePathFunc != null ? imagePathFunc.apply(value) : null;
            setBackground(isSelected ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            return this;
        }

        private Image getCachedAvatarImage(String path, int size) {
            if (path == null || path.trim().isEmpty()) {
                return null;
            }

            String cacheKey = path + "#" + size;
            if (AVATAR_IMAGE_CACHE.containsKey(cacheKey)) {
                return AVATAR_IMAGE_CACHE.get(cacheKey);
            }

            try {
                File f = new File(path);
                if (!f.exists()) {
                    return null;
                }
                BufferedImage raw = ImageIO.read(f);
                if (raw == null) {
                    return null;
                }
                Image scaled = raw.getScaledInstance(size, size, Image.SCALE_SMOOTH);
                AVATAR_IMAGE_CACHE.put(cacheKey, scaled);
                return scaled;
            } catch (IOException ignored) {
                return null;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            int avatarSize = 40, x = 15, y = (getHeight() - avatarSize) / 2;
            Shape oldClip = g2.getClip();
            g2.setClip(new java.awt.geom.Ellipse2D.Float(x, y, avatarSize, avatarSize));

            Image avatarImage = getCachedAvatarImage(imagePath, avatarSize);
            if (avatarImage != null) {
                g2.drawImage(avatarImage, x, y, avatarSize, avatarSize, null);
            } else {
                g2.setColor(avatarColor);
                g2.fillOval(x, y, avatarSize, avatarSize);

                g2.setClip(oldClip);
                g2.setColor(Color.WHITE);
                g2.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(initials,
                        x + (avatarSize - fm.stringWidth(initials)) / 2,
                        y + (avatarSize + fm.getAscent() - fm.getDescent()) / 2);
                g2.setClip(new java.awt.geom.Ellipse2D.Float(x, y, avatarSize, avatarSize));
            }
            g2.setClip(oldClip);

            int infoX = x + avatarSize + 12;
            g2.setColor(Colors.TEXT_PRIMARY);
            g2.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            g2.drawString(name != null ? name : "", infoX, getHeight() / 2 - 4);

            g2.setColor(Colors.TEXT_SECONDARY);
            g2.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
            g2.drawString(sub != null ? sub : "", infoX, getHeight() / 2 + 14);

            g2.setColor(Colors.BORDER_LIGHT);
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            g2.dispose();
        }
    }

    // Vẽ 2 dòng chữ: dòng 1 in đậm (tên), dòng 2 nhỏ màu phụ (mô tả).
    private static class TwoLineRenderer extends JPanel implements TableCellRenderer {

        private final Function<Object, String> line1Func;
        private final Function<Object, String> line2Func;
        private String line1, line2;

        TwoLineRenderer(Function<Object, String> line1Func, Function<Object, String> line2Func) {
            this.line1Func = line1Func;
            this.line2Func = line2Func;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            this.line1 = line1Func.apply(value);
            this.line2 = line2Func.apply(value);
            setBackground(isSelected ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            int x = 15;
            g2.setColor(Colors.TEXT_PRIMARY);
            g2.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            g2.drawString(line1 != null ? line1 : "", x, getHeight() / 2 - 4);
            g2.setColor(Colors.TEXT_SECONDARY);
            g2.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
            g2.drawString(line2 != null ? line2 : "", x, getHeight() / 2 + 14);
            g2.setColor(Colors.BORDER_LIGHT);
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            g2.dispose();
        }
    }

    // Vẽ 2 dòng, mỗi dòng bắt đầu bằng ký tự icon (unicode/emoji).
    private static class IconTwoLineRenderer extends JPanel implements TableCellRenderer {

        private final String icon1, icon2;
        private final Function<Object, String> text1Func, text2Func;
        private String text1, text2;

        IconTwoLineRenderer(String icon1, Function<Object, String> text1Func,
                String icon2, Function<Object, String> text2Func) {
            this.icon1 = icon1;
            this.text1Func = text1Func;
            this.icon2 = icon2;
            this.text2Func = text2Func;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            this.text1 = text1Func.apply(value);
            this.text2 = text2Func.apply(value);
            setBackground(isSelected ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            int x = 15;
            g2.setColor(Colors.TEXT_SECONDARY);
            g2.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            g2.drawString(icon1, x, getHeight() / 2 - 4);
            g2.setColor(Colors.TEXT_PRIMARY);
            g2.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
            g2.drawString(text1 != null ? text1 : "\u2014", x + 20, getHeight() / 2 - 4);
            g2.setColor(Colors.TEXT_SECONDARY);
            g2.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            g2.drawString(icon2, x, getHeight() / 2 + 14);
            g2.setColor(Colors.TEXT_PRIMARY);
            g2.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
            g2.drawString(text2 != null ? text2 : "\u2014", x + 20, getHeight() / 2 + 14);
            g2.setColor(Colors.BORDER_LIGHT);
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            g2.dispose();
        }
    }

    // Vẽ 1 dòng chữ căn giữa theo chiều dọc.
    private static class SingleTextRenderer extends JPanel implements TableCellRenderer {

        private final Function<Object, String> textFunc;
        private String text;

        SingleTextRenderer(Function<Object, String> textFunc) {
            this.textFunc = textFunc;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            this.text = textFunc.apply(value);
            setBackground(isSelected ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
            g2.setColor(Colors.TEXT_PRIMARY);
            g2.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text != null ? text : "", 15,
                    (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            g2.setColor(Colors.BORDER_LIGHT);
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            g2.dispose();
        }
    }

    // Vẽ badge bo góc: chấm màu + nhãn, màu khác nhau tùy trạng thái.
    private static class BadgeRenderer extends JPanel implements TableCellRenderer {

        private final Function<Object, Boolean> activeFunc;
        private final String activeText, inactiveText;
        private boolean isActive;

        BadgeRenderer(Function<Object, Boolean> activeFunc, String activeText, String inactiveText) {
            this.activeFunc = activeFunc;
            this.activeText = activeText;
            this.inactiveText = inactiveText;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            this.isActive = activeFunc.apply(value);
            setBackground(isSelected ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            return this;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);

            String text = isActive ? activeText : inactiveText;
            Color bgColor = isActive ? Colors.SUCCESS_LIGHT : Colors.SECONDARY;
            Color fgColor = isActive ? Colors.SUCCESS_DARK : Colors.TEXT_SECONDARY;

            g2.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
            FontMetrics fm = g2.getFontMetrics();
            int dotSize = 7, gap = 6, padX = 12, padY = 4;
            int badgeW = padX + dotSize + gap + fm.stringWidth(text) + padX;
            int badgeH = fm.getHeight() + padY * 2;
            int badgeX = 10, badgeY = (getHeight() - badgeH) / 2;

            g2.setColor(bgColor);
            g2.fillRoundRect(badgeX, badgeY, badgeW, badgeH, badgeH, badgeH);
            g2.setColor(fgColor);
            int dotX = badgeX + padX, dotY = badgeY + (badgeH - dotSize) / 2;
            g2.fillOval(dotX, dotY, dotSize, dotSize);
            g2.drawString(text, dotX + dotSize + gap, badgeY + padY + fm.getAscent());

            g2.setColor(Colors.BORDER_LIGHT);
            g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);
            g2.dispose();
        }
    }

    // Hiển thị nút "Chi tiết". Click được xử lý bởi MouseListener qua
    // actionListener.
    private static class ActionDotsRenderer extends JPanel implements TableCellRenderer {

        private final RoundedButton btnDetail;

        ActionDotsRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            btnDetail = new RoundedButton(80, 45, 20, "Chi tiết", Colors.SECONDARY);
            btnDetail.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
            btnDetail.setForeground(Colors.FOREGROUND);
            btnDetail.setFocusPainted(false);
            btnDetail.setPreferredSize(new Dimension(30, 20));
            add(btnDetail);
            setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            btnDetail.setEnabled(true);
            return this;
        }
    }

    // Hiển thị nút "Xóa" màu đỏ. Click được xử lý bởi MouseListener qua
    // deleteListener.
    private static class DeleteButtonRenderer extends JPanel implements TableCellRenderer {

        private final RoundedButton btnDelete;

        DeleteButtonRenderer() {
            setOpaque(true);
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            btnDelete = new RoundedButton(80, 45, 20, "Xóa", Colors.DANGER);
            btnDelete.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
            btnDelete.setForeground(Colors.BACKGROUND);
            btnDelete.setFocusPainted(false);
            btnDelete.setPreferredSize(new Dimension(30, 20));
            add(btnDelete);
            setBorder(BorderFactory.createEmptyBorder(15, 10, 5, 10));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            setBackground(isSelected ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            btnDelete.setEnabled(true);
            return this;
        }
    }
}
