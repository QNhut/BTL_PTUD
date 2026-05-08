package exception;

import java.awt.*;
import javax.swing.*;

//======CÁCH DÙNG=======
//Gọi JTextField txtA = new RoundedTextField(int width, int height, int radius, String placeholder)
//Ví dụ: JTextField txtA = new RoundedTextField(200, 30, 20, "Tìm kiếm")
public class RoundedTextField extends JTextField {

    private int radius;
    private int width;
    private int height;
    private String placeholder;

    private Color borderColor = new Color(200, 200, 200);
    private Color focusBorderColor = new Color(100, 150, 255);
    private Color backgroundColor = Color.WHITE;
    private Color placeholderColor = new Color(150, 150, 150);

    public RoundedTextField(int width, int height, int radius, String placeholder) {
        this.width = width;
        this.height = height;
        this.radius = radius;
        this.placeholder = placeholder;

        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        setFont(new Font("Segoe UI", Font.PLAIN, 14));
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
