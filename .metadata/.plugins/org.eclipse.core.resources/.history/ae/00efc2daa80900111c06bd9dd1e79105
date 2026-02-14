package exception;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class MenuButton extends JPanel {

    private JButton mainButton;
    private JPopupMenu subMenu;
    private boolean selected = false;
    private MenuActionListener listener;

    private static final Color COLOR_NORMAL = Color.WHITE;
    private static final Color COLOR_HOVER = new Color(220, 230, 245);
    private static final Color COLOR_SELECTED = new Color(124, 163, 206);

    private static final Color SUB_NORMAL = Color.WHITE;
    private static final Color SUB_HOVER = new Color(200, 220, 245);

    private static final Font FONT_MAIN = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font FONT_MAIN_SELECTED = new Font("Segoe UI", Font.BOLD, 14);
    private static final Font FONT_SUB = new Font("Segoe UI", Font.PLAIN, 13);

    public MenuButton(String text, String iconPath) {

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        ImageIcon icon = null;
        if (iconPath != null && !iconPath.isEmpty()) {
            Image img = new ImageIcon(iconPath)
                    .getImage()
                    .getScaledInstance(22, 22, Image.SCALE_SMOOTH);
            icon = new ImageIcon(img);
        }

        mainButton = new JButton(text, icon);
        mainButton.setHorizontalAlignment(SwingConstants.LEFT);
        mainButton.setFocusPainted(false);
        mainButton.setBackground(COLOR_NORMAL);
        mainButton.setFont(FONT_MAIN);
        mainButton.setBorder(BorderFactory.createEmptyBorder(12, 10, 12, 10));
        mainButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mainButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

        add(mainButton, BorderLayout.CENTER);

        subMenu = new JPopupMenu();
        subMenu.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        mainButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                if (!selected) {
                    mainButton.setBackground(COLOR_HOVER);
                }
                if (subMenu.getComponentCount() > 0) {
                    // ===== ĐỔI SANG XỔ SANG PHẢI =====
                    subMenu.show(mainButton, mainButton.getWidth(), 0);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (!selected) {
                    mainButton.setBackground(COLOR_NORMAL);
                }
            }
        });
    }

    public void addSubMenu(String title, String pageName) {

        JMenuItem item = new JMenuItem(title);
        item.setFont(FONT_SUB);
        item.setOpaque(true);
        item.setBackground(SUB_NORMAL);
        item.setBorder(BorderFactory.createEmptyBorder(8, 30, 8, 20));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        item.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                item.setBackground(SUB_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                item.setBackground(SUB_NORMAL);
            }
        });

        item.addActionListener(e -> {
            if (listener != null) {
                listener.onMenuSelected(this, pageName);
            }
            subMenu.setVisible(false);
        });

        subMenu.add(item);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;

        mainButton.setBackground(
                selected ? COLOR_SELECTED : COLOR_NORMAL
        );
        mainButton.setFont(
                selected ? FONT_MAIN_SELECTED : FONT_MAIN
        );
    }

    public JButton getMainButton() {
        return mainButton;
    }

    public void setMenuActionListener(MenuActionListener listener) {
        this.listener = listener;
    }

    public interface MenuActionListener {
        void onMenuSelected(MenuButton source, String pageName);
    }
}
