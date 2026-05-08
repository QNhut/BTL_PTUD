package gui;

import constants.Colors;
import constants.FontStyle;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

/**
 * Popup gợi ý từ khóa cho ô tìm kiếm (autocomplete dropdown).
 *
 * Cách dùng: SearchSuggestionPopup<KhachHang> popup = new
 * SearchSuggestionPopup<>(txtKeyword); popup.setSource(() -> dsKhachHang, //
 * nguồn dữ liệu (lazy) kh -> kh.getTenKhachHang(), // text hiển thị chính kh ->
 * kh.getMaKhachHang(), // text phụ (mã) (kh, kw) ->
 * kh.getTenKhachHang().toLowerCase().contains(kw) ||
 * kh.getSoDienThoai().contains(kw)); popup.setOnSelect(kh -> {
 * txtKeyword.setText(kh.getTenKhachHang()); search(); });
 * popup.setEnabled(true); // bật/tắt theo loại tìm kiếm
 */
public class SearchSuggestionPopup<T> {

    public interface MatchFn<T> {

        boolean matches(T item, String keywordLower);
    }

    private final JTextComponent input;
    private JWindow window;
    private final DefaultListModel<String> model = new DefaultListModel<>();
    private final JList<String> list = new JList<>(model);
    private final List<T> currentItems = new ArrayList<>();
    private boolean suppress = false;
    private boolean enabled = true;
    private int maxItems = 8;

    private java.util.function.Supplier<? extends Iterable<T>> source;
    private Function<T, String> primaryText;
    private Function<T, String> secondaryText;
    private MatchFn<T> matcher;
    private java.util.function.Consumer<T> onSelect;

    public SearchSuggestionPopup(JTextComponent input) {
        this.input = input;

        list.setBackground(Colors.BACKGROUND);
        list.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        list.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int idx, boolean sel, boolean focus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(l, v, idx, sel, focus);
                lbl.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
                lbl.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
                lbl.setForeground(Colors.TEXT_PRIMARY);
                return lbl;
            }
        });
        list.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int idx = list.locationToIndex(e.getPoint());
                if (idx >= 0 && idx < currentItems.size()) {
                    T item = currentItems.get(idx);
                    suppress = true;
                    if (onSelect != null) {
                        onSelect.accept(item);
                    }
                    suppress = false;
                    hide();
                }
            }
        });

        input.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                refresh();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
        input.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                javax.swing.Timer t = new javax.swing.Timer(150, ev -> hide());
                t.setRepeats(false);
                t.start();
            }
        });
    }

    public void setSource(java.util.function.Supplier<? extends Iterable<T>> source,
            Function<T, String> primaryText,
            Function<T, String> secondaryText,
            MatchFn<T> matcher) {
        this.source = source;
        this.primaryText = primaryText;
        this.secondaryText = secondaryText;
        this.matcher = matcher;
    }

    public void setOnSelect(java.util.function.Consumer<T> onSelect) {
        this.onSelect = onSelect;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        if (!enabled) {
            hide();
        }
    }

    public void setMaxItems(int n) {
        this.maxItems = n;
    }

    public void hide() {
        if (window != null) {
            window.setVisible(false);
        }
    }

    private void initWindow() {
        if (window != null) {
            return;
        }
        Window parent = SwingUtilities.getWindowAncestor(input);
        window = new JWindow(parent);
        window.setFocusableWindowState(false);
        JScrollPane sp = new JScrollPane(list);
        sp.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));
        sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        window.add(sp);
    }

    private void refresh() {
        if (suppress || !enabled || source == null || matcher == null) {
            hide();
            return;
        }
        String kw = input.getText().trim().toLowerCase();
        if (kw.isEmpty()) {
            hide();
            return;
        }

        // Gom và lọc trùng (theo primaryText + secondaryText)
        java.util.LinkedHashMap<String, T> uniq = new java.util.LinkedHashMap<>();
        for (T it : source.get()) {
            if (it == null) {
                continue;
            }
            if (!matcher.matches(it, kw)) {
                continue;
            }
            String key = safe(primaryText.apply(it)) + "|" + safe(secondaryText == null ? "" : secondaryText.apply(it));
            uniq.putIfAbsent(key, it);
            if (uniq.size() >= maxItems) {
                break;
            }
        }
        if (uniq.isEmpty()) {
            hide();
            return;
        }
        if (!input.isShowing()) {
            return;
        }

        initWindow();
        model.clear();
        currentItems.clear();
        for (T it : uniq.values()) {
            currentItems.add(it);
            String main = safe(primaryText.apply(it));
            String sub = secondaryText != null ? safe(secondaryText.apply(it)) : "";
            model.addElement(sub.isEmpty() ? main : main + "   (" + sub + ")");
        }
        Point loc = input.getLocationOnScreen();
        int h = Math.min(currentItems.size() * 32 + 10, 260);
        window.setBounds(loc.x, loc.y + input.getHeight() + 2, input.getWidth(), h);
        window.setVisible(true);
    }

    /**
     * Cập nhật giá trị text mà không kích hoạt popup (dùng khi click chọn từ
     * list).
     */
    public void setTextSilently(String text) {
        suppress = true;
        input.setText(text);
        suppress = false;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }
}
