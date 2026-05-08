package exception;

import constants.Colors;
import java.awt.*;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

//======CÁCH DÙNG=======
//Gọi JTextField txtA = new RoundedTextField(int width, int height, int radius, String placeholder)
//Ví dụ: JTextField txtA = new RoundedTextField(200, 30, 20, "Tìm kiếm")
public class RoundedTextField extends JTextField {

    private int radius;
    private int width;
    private int height;
    private String placeholder;

    private Color borderColor = Colors.INPUT_NORMAL_BORDER;
    private Color focusBorderColor = Colors.INPUT_FOCUS_BORDER;
    private Color backgroundColor = Color.WHITE;
    private Color placeholderColor = new Color(150, 150, 150);

    private boolean invalid = false;
    private Color savedBorderColor;
    private Color savedBgColor;

    public RoundedTextField(int width, int height, int radius, String placeholder) {
        this.width = width;
        this.height = height;
        this.radius = radius;
        this.placeholder = placeholder;

        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Khi user g\u00f5 lai: t\u1ef1 \u0111\u1ed9ng x\u00f3a tr\u1ea1ng th\u00e1i invalid
        getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { clearInvalidIfTyping(); }
            @Override public void removeUpdate(DocumentEvent e) { clearInvalidIfTyping(); }
            @Override public void changedUpdate(DocumentEvent e) {}
        });
    }

    private void clearInvalidIfTyping() {
        if (invalid) {
            setInvalid(false);
        }
    }

    // \u0110\u00e1nh d\u1ea5u \u00f4 nh\u1eadp \u0111ang sai (vi\u1ec1n \u0111\u1ecf + n\u1ec1n h\u1ed3ng nh\u1ea1t).
    public void setInvalid(boolean invalid) {
        if (this.invalid == invalid) return;
        if (invalid) {
            this.savedBorderColor = this.borderColor;
            this.savedBgColor = this.backgroundColor;
            this.borderColor = Colors.INPUT_INVALID_BORDER;
            this.backgroundColor = Colors.INPUT_INVALID_BG;
        } else {
            if (savedBorderColor != null) this.borderColor = savedBorderColor;
            if (savedBgColor != null) this.backgroundColor = savedBgColor;
        }
        this.invalid = invalid;
        repaint();
    }

    public boolean isInvalid() {
        return invalid;
    }

    // 👉 Cho layout biết size
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int inset = 1;
        int w = getWidth() - 2 * inset;
        int h = getHeight() - 2 * inset;

        // 🎨 Background
        g2.setColor(backgroundColor);
        g2.fillRoundRect(inset, inset, w, h, radius, radius);

        super.paintComponent(g);

        // 🎯 Placeholder (canh giữa chuẩn)
        if (getText().isEmpty() && !isFocusOwner()) {
            g2.setColor(placeholderColor);

            FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            g2.drawString(placeholder, x, y);
        }

        g2.dispose();
    }

    @Override
    protected void paintBorder(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // 🎨 Border khi focus
        if (hasFocus()) {
            g2.setColor(focusBorderColor);
        } else {
            g2.setColor(borderColor);
        }

        int inset = 1;
        int w = getWidth() - 2 * inset;
        int h = getHeight() - 2 * inset;
        g2.drawRoundRect(inset, inset, w - 1, h - 1, radius, radius);

        g2.dispose();
    }

    // ================== SETTER ==================
    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    public void setBorderColor(Color color) {
        this.borderColor = color;
        repaint();
    }

    public void setFocusBorderColor(Color color) {
        this.focusBorderColor = color;
        repaint();
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }
}
