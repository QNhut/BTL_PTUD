package exception;

import constants.Colors;
import constants.FontStyle;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

// Helper gắn validator cho các ô nhập trong dialog.
// <p>
// Dùng như sau:
// <pre>
// FormValidator fv = new FormValidator();
// JLabel errTen = FormValidator.errorLabel();
// pnl.add(FormValidator.fieldWithError("Tên *", txtTen, errTen));
// fv.add(txtTen, errTen, Validators::tenNguoi);
// ...
// if (!fv.validateAll()) return; // đã tự động focus + tô ô sai
// </pre>
public final class FormValidator {

    private final List<Entry> entries = new ArrayList<>();

    public FormValidator add(JTextComponent field, JLabel errorLabel,
            Function<String, String> validator) {
        entries.add(new Entry(field, errorLabel, validator));
        return this;
    }

    // Chạy toàn bộ validator. Nếu có ô sai: tô đỏ, hiện lỗi dưới ô,
    // focus vào ô sai đầu tiên và trả về false.
    public boolean validateAll() {
        boolean ok = true;
        JTextComponent firstInvalid = null;
        for (Entry e : entries) {
            String value = e.field.getText();
            String err = e.validator.apply(value);
            if (err == null) {
                e.markValid();
            } else {
                ok = false;
                e.markInvalid(err);
                if (firstInvalid == null) firstInvalid = e.field;
            }
        }
        if (firstInvalid != null) {
            firstInvalid.requestFocusInWindow();
            firstInvalid.selectAll();
        }
        return ok;
    }

    // Xóa trạng thái lỗi trên tất cả ô.
    public void clearAll() {
        for (Entry e : entries) e.markValid();
    }

    // Tạo nhãn lỗi (mặc định ẩn, chữ đỏ nhỏ).
    public static JLabel errorLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lbl.setForeground(Colors.DANGER);
        lbl.setVisible(false);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // Tạo một row form field theo cấu trúc:
    // <pre>
    // [Label]
    // [Input]
    // [Error label]
    // </pre>
    public static JPanel fieldWithError(String labelText, JTextField field, JLabel errorLabel) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lbl.setForeground(Colors.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(lbl);
        row.add(Box.createVerticalStrut(4));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(field);

        if (errorLabel != null) {
            row.add(Box.createVerticalStrut(2));
            errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            errorLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 0));
            row.add(errorLabel);
        }
        return row;
    }

    // ===== INTERNAL =====
    private static final class Entry {
        final JTextComponent field;
        final JLabel errorLabel;
        final Function<String, String> validator;

        Entry(JTextComponent field, JLabel errorLabel, Function<String, String> validator) {
            this.field = field;
            this.errorLabel = errorLabel;
            this.validator = validator;
        }

        void markInvalid(String msg) {
            if (field instanceof RoundedTextField) {
                ((RoundedTextField) field).setInvalid(true);
            }
            if (errorLabel != null) {
                errorLabel.setText(msg);
                errorLabel.setVisible(true);
                errorLabel.setPreferredSize(null);
                errorLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
            }
        }

        void markValid() {
            if (field instanceof RoundedTextField) {
                ((RoundedTextField) field).setInvalid(false);
            }
            if (errorLabel != null) {
                errorLabel.setText(" ");
                errorLabel.setVisible(false);
            }
        }
    }
}
