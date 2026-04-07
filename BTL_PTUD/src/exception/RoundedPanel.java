package exception;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

//======CÁCH DÙNG=======
//Gọi JPanel jplA = new RoundedPanel(int width, int height, int radius)
//Ví dụ: JPanel jplA = new RoundedPanel(200, 30, 20)

public class RoundedPanel extends JPanel {
    private int cornerRadius;
    private int width;
    private int height;
    private Color borderColor = new Color(220, 220, 220);

    public RoundedPanel(int width, int height, int radius) {
        this.width = width;
        this.height = height;
        this.cornerRadius = radius;

        setOpaque(false);   // QUAN TRỌNG
        setBorder(null);
    }

    // 👉 Cho layout biết kích thước mong muốn
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(width, height);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);

        // Vẽ nền bo góc
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(),
                         cornerRadius, cornerRadius);

        // Vẽ viền
        g2.setColor(borderColor);
        g2.setStroke(new BasicStroke(1));
        g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1,
                         cornerRadius, cornerRadius);

        g2.dispose();
    }
}