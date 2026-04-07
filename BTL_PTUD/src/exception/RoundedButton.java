package exception;

import java.awt.*;
import javax.swing.*;

import constants.Colors;

//======CÁCH DÙNG=======
// Gọi JButton btnA = new RoundedButton(int width, int height, int radius, String text, Color bgColor)
//Ví dụ: JButton btnSave = new RoundedButton(200, 30, 20, "Lưu", Colors.BackGround)

public class RoundedButton extends JButton {

    private int radius;
    private int width;
    private int height;

    private Color backgroundColor;
    private Color foregroundColor;
    private Color hoverColor;
    private Color pressedColor;

    private boolean isHover = false;
    private boolean isPressed = false;

    public RoundedButton(int width, int height, int radius, String text, Color bgColor) {
        super(text);

        this.width = width;
        this.height = height;
        this.radius = radius;

        this.backgroundColor = bgColor;
        this.foregroundColor = Colors.BACKGROUND;

        this.hoverColor = bgColor.brighter();
        this.pressedColor = bgColor.darker();

        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setFont(new Font("Arial", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(foregroundColor);

        // 👉 Hover + Press effect
        addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                isHover = true;
                repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                isHover = false;
                isPressed = false;
                repaint();
            }

            public void mousePressed(java.awt.event.MouseEvent evt) {
                isPressed = true;
                repaint();
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                isPressed = false;
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

        // 🎨 Xác định màu theo trạng thái
        if (isPressed) {
            g2.setColor(pressedColor);
        } else if (isHover) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(backgroundColor);
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

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        this.hoverColor = color.brighter();
        this.pressedColor = color.darker();
        repaint();
    }
}