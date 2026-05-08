package exception;

import java.awt.*;
import javax.swing.*;

import constants.Colors;



//Gọi JToggleButton btnA = new RoundedToggleButton(int width, int height, int radius, String text, Color bgColor)
//Ví dụ: JToggleButton btnSave = new RoundedToggleButton(200, 30, 20, "Lưu", Colors.BackGround)


public class RoundedToggleButton extends JToggleButton {

    private int radius;
    private int width;
    private int height;

    private Color primary;
    private Color secondary;
    private Color foreground;
    private Color background;

    private Color hoverColor;
    private boolean isHover = false;

    public RoundedToggleButton(int width, int height, int radius, String text, Color primary) {
        super(text);

        this.width = width;
        this.height = height;
        this.radius = radius;

        this.primary = primary;
        this.secondary = Colors.SECONDARY;
        this.foreground = Colors.FOREGROUND;
        this.background = Colors.BACKGROUND;

        this.hoverColor = primary.brighter();

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Arial", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 👉 Hover effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHover = true;
                repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHover = false;
                repaint();
            }
        });
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        // 🎨 Xác định màu
        if (getModel().isSelected()) {
            g2.setColor(primary);
            setForeground(background);
        } else if (isHover) {
            g2.setColor(hoverColor);
            setForeground(background);
        } else {
            g2.setColor(secondary);
            setForeground(foreground);
        }

        // 🎨 Vẽ nền
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.dispose();

        super.paintComponent(g); // vẽ text
    }

    @Override
    protected void paintBorder(Graphics g) {
        // không vẽ border
    }

    // ================== SETTER ==================

    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    public void setPrimaryColor(Color color) {
        this.primary = color;
        this.hoverColor = color.brighter();
        repaint();
    }
}