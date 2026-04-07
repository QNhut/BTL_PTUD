package exception;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import constants.Colors;

public class MenuButton extends JPanel {

    private RoundedButton mainButton;
    private JPanel subPanel;
    private boolean expanded = false;
    private MenuActionListener listener;
    private String mainPageName;

    private ArrayList<RoundedButton> subButtons = new ArrayList<>();

    // ===== COLOR =====
    private static final Color MAIN_COLOR = Colors.PRIMARY;          // Xanh chính
    private static final Color SUB_NORMAL = Colors.BACKGROUND;       // Trắng
    private static final Color SUB_SELECTED = Colors.ACCENT;         // Cam highlight
    private static final Color TEXT_MAIN = Colors.BACKGROUND;        // Text màu trắng
    private static final Color TEXT_SUB_NORMAL = Colors.TEXT_PRIMARY;   // Text tối
    private static final Color TEXT_SUB_SELECTED = Colors.BACKGROUND;   // Text trắng

    private JLabel arrowLabel;

    // ===== CONSTRUCTOR =====
    public MenuButton(String text, String pageName) {

        this.mainPageName = pageName;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE); // nền ngoài trắng
        setAlignmentX(Component.LEFT_ALIGNMENT);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0)); // Khoảng cách giữa các button cha

        // ===== MAIN BUTTON =====
        mainButton = new RoundedButton(400, 50, 40, "", MAIN_COLOR);
        mainButton.setLayout(new BorderLayout());
        mainButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        mainButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        mainButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));

        // TEXT LABEL
        JLabel textLabel = new JLabel(text);
        textLabel.setForeground(TEXT_MAIN);
        textLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        // ARROW ICON (không thêm ngay, chỉ thêm khi có sub-menu)
        arrowLabel = new JLabel();
        arrowLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        mainButton.add(textLabel, BorderLayout.CENTER);
        mainButton.add(arrowLabel, BorderLayout.EAST);

        // Tắt hiệu ứng hover màu sáng cho button cha
        mainButton.setEnableHover(false);

        add(mainButton);

        // ===== SUB PANEL =====
        subPanel = new JPanel();
        subPanel.setLayout(new BoxLayout(subPanel, BoxLayout.Y_AXIS));
        subPanel.setBackground(Color.WHITE);
        subPanel.setBorder(BorderFactory.createEmptyBorder(4, 10, 0, 10)); // Khoảng cách với button cha
        subPanel.setVisible(false);

        add(subPanel);

        // ===== EVENT =====
        mainButton.addActionListener(e -> {

            if (!subButtons.isEmpty()) {
                toggleMenu();
            } else if (listener != null && mainPageName != null) {

                setMainSelected(true);
                listener.onMenuSelected(this, mainPageName);
            }
        });
    }

    // ===== LOAD ICON =====
    private ImageIcon loadIcon(String path, int w, int h) {

        ImageIcon icon = new ImageIcon(path);

        if (icon.getIconWidth() > 0) {
            Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            System.out.println("Không tìm thấy icon: " + path);
        }

        return null;
    }

    // ===== TOGGLE =====
    private void toggleMenu() {
        expanded = !expanded;
        subPanel.setVisible(expanded);

        if (arrowLabel != null) {
            arrowLabel.setIcon(loadIcon(
                    expanded ? "data/img/icons/up-chevron.png"
                             : "data/img/icons/down-chevron.png",
                    18, 18
            ));
        }

        revalidate();
        repaint();
    }

    public void collapse() {
        expanded = false;
        subPanel.setVisible(false);

        if (arrowLabel != null && !subButtons.isEmpty()) {
            arrowLabel.setIcon(loadIcon("data/img/icons/down-chevron.png", 18, 18));
        }
    }

    // ===== ADD SUB MENU =====
    public void addSubMenu(String title, String iconPath, String pageName) {

        if (subButtons.isEmpty()) {
            // Thêm arrow icon khi có sub-menu đầu tiên
            arrowLabel.setIcon(loadIcon("data/img/icons/down-chevron.png", 18, 18));
        }

        RoundedButton subBtn = new RoundedButton(350, 40, 30, "", SUB_NORMAL);
        subBtn.setLayout(new BorderLayout());

        // ICON
        JLabel iconLabel = new JLabel(loadIcon(iconPath, 18, 18));
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 15));

        // TEXT
        JLabel textLabel = new JLabel(title);
        textLabel.setForeground(TEXT_SUB_NORMAL);
        textLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));

        subBtn.add(iconLabel, BorderLayout.WEST);
        subBtn.add(textLabel, BorderLayout.CENTER);

        subBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        subBtn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        subBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // ===== CLICK =====
        subBtn.addActionListener(e -> {

            resetSubMenuColor();

            subBtn.setBackground(SUB_SELECTED);
            textLabel.setForeground(TEXT_SUB_SELECTED);

            // Giữ lại trạng thái arrow khi click sub-button
            if (arrowLabel != null && arrowLabel.getIcon() == null) {
                arrowLabel.setIcon(loadIcon(expanded ? "data/img/icons/up-chevron.png" : "data/img/icons/down-chevron.png", 18, 18));
            }

            if (listener != null) {
                listener.onMenuSelected(this, pageName);
            }
        });

        subButtons.add(subBtn);
        subPanel.add(subBtn);
    }

    // ===== RESET =====
    public void resetSubMenuColor() {
        for (RoundedButton btn : subButtons) {
            btn.setBackground(SUB_NORMAL);

            for (Component c : btn.getComponents()) {
                if (c instanceof JLabel) {
                    ((JLabel) c).setForeground(TEXT_SUB_NORMAL);
                }
            }
        }
    }

    // ===== LISTENER =====
    public void setMenuActionListener(MenuActionListener listener) {
        this.listener = listener;
    }

    public interface MenuActionListener {
        void onMenuSelected(MenuButton source, String pageName);
    }

    // ===== SELECT MAIN =====
    public void setMainSelected(boolean selected) {
        if (subButtons.isEmpty()) {
            mainButton.setBackground(selected ? SUB_SELECTED : MAIN_COLOR);
        }
    }
}