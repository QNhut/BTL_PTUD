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

// Helper g\u1eafn validator cho c\u00e1c \u00f4 nh\u1eadp trong dialog.
// <p>
// D\u00f9ng nh\u01b0 sau:
// <pre>
// FormValidator fv = new FormValidator();
// JLabel errTen = FormValidator.errorLabel();
// pnl.add(FormValidator.fieldWithError("T\u00ean *", txtTen, errTen));
// fv.add(txtTen, errTen, Validators::tenNguoi);
// ...
// if (!fv.validateAll()) return; // \u0111\u00e3 t\u1ef1 \u0111\u1ed9ng focus + t\u00f4 \u00f4 sai
// </pre>
public final class FormValidator {

    private final List<Entry> entries = new ArrayList<>();

    public FormValidator add(JTextComponent field, JLabel errorLabel,
            Function<String, String> validator) {
        entries.add(new Entry(field, errorLabel, validator));
        return this;
    }

    // Ch\u1ea1y to\u00e0n b\u1ed9 validator. N\u1ebfu c\u00f3 \u00f4 sai: t\u00f4 \u0111\u1ecf, hi\u1ec7n l\u1ed7i d\u01b0\u1edbi \u00f4,
    // focus v\u00e0o \u00f4 sai \u0111\u1ea7u ti\u00ean v\u00e0 tr\u1ea3 v\u1ec1 false.
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

    // X\u00f3a tr\u1ea1ng th\u00e1i l\u1ed7i tr\u00ean t\u1ea5t c\u1ea3 \u00f4.
    public void clearAll() {
        for (Entry e : entries) e.markValid();
    }

    // T\u1ea1o nh\u00e3n l\u1ed7i (m\u1eb7c \u0111\u1ecbnh \u1ea9n, ch\u1eef \u0111\u1ecf nh\u1ecf).
    public static JLabel errorLabel() {
        JLabel lbl = new JLabel(" ");
        lbl.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lbl.setForeground(Colors.DANGER);
        lbl.setVisible(false);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        return lbl;
    }

    // T\u1ea1o m\u1ed9t row form field theo c\u1ea5u tr\u00fac:
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
