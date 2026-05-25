package gui;

import constants.Colors;
import constants.FontStyle;
import exception.RoundedButton;
import exception.RoundedComboBox;
import exception.RoundedTextField;
import java.awt.*;
import javax.swing.*;

public class TraCuuDoiHang extends JPanel {

    private RoundedComboBox<String> cboTimKiemTheo;
    private RoundedTextField txtKeyword;
    private JSpinner spnTuNgay;
    private JSpinner spnDenNgay;
    private RoundedButton btnTimKiem;
    private RoundedButton btnXoaLoc;
    private JPanel pairButton;

    // ═══════════════════════════════════════════════════════════
    public TraCuuDoiHang() {
        setLayout(new BorderLayout(0, 16));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Colors.BACKGROUND);

        add(buildTopSection(), BorderLayout.NORTH);
        add(buildEmptyState(), BorderLayout.CENTER);
    }

    // ─────────────────────────────────────────────────────────
    // TOP SECTION
    // ─────────────────────────────────────────────────────────
    private JPanel buildTopSection() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Colors.BACKGROUND);

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBackground(Colors.BACKGROUND);
        header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
        header.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Tra c\u1ee9u \u0111\u1ed5i h\u00e0ng");
        lblTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.TEXT_PRIMARY);

        JLabel lblNote = new JLabel("T\u00ecm ki\u1ebfm v\u00e0 xem chi ti\u1ebft phi\u1ebfu \u0111\u1ed5i h\u00e0ng trong h\u1ec7 th\u1ed1ng");
        lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNote.setForeground(Colors.TEXT_SECONDARY);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(4));
        header.add(lblNote);

        pnl.add(header);
        pnl.add(buildFilterCard());
        return pnl;
    }

    // ─────────────────────────────────────────────────────────
    // FILTER CARD
    // ─────────────────────────────────────────────────────────
    private JPanel buildFilterCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 0));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
        pnlNorth.setOpaque(false);
        pnlNorth.add(buildCardTitleRow());
        card.add(pnlNorth, BorderLayout.NORTH);

        // ===Hang tat ca fields: 4 cot deu nhau===
        JPanel pnlFields = new JPanel();
        pnlFields.setLayout(new BoxLayout(pnlFields, BoxLayout.X_AXIS));
        pnlFields.setOpaque(false);
        pnlFields.setBorder(BorderFactory.createEmptyBorder(4, 12, 12, 12));

        JPanel pairTimKiem = new JPanel();
        pairTimKiem.setLayout(new BoxLayout(pairTimKiem, BoxLayout.Y_AXIS));
        pairTimKiem.setOpaque(false);
        pairTimKiem.setAlignmentY(Component.TOP_ALIGNMENT);
        JLabel lblTimKiemTheo = fieldLabel("T\u00ecm ki\u1ebfm theo");
        lblTimKiemTheo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTimKiem.add(lblTimKiemTheo);
        pairTimKiem.add(Box.createVerticalStrut(4));
        cboTimKiemTheo = new RoundedComboBox<>(10);
        cboTimKiemTheo.addItem("M\u00e3 phi\u1ebfu \u0111\u1ed5i");
        cboTimKiemTheo.addItem("Kh\u00e1ch h\u00e0ng");
        cboTimKiemTheo.addItem("Nh\u00e2n vi\u00ean x\u1eed l\u00fd");
        cboTimKiemTheo.setAlignmentX(Component.LEFT_ALIGNMENT);
        cboTimKiemTheo.setMaximumSize(new Dimension(200, 44));
        pairTimKiem.add(cboTimKiemTheo);
        pnlFields.add(pairTimKiem);
        pnlFields.add(Box.createHorizontalStrut(10));

        JPanel pairTuKhoa = new JPanel();
        pairTuKhoa.setLayout(new BoxLayout(pairTuKhoa, BoxLayout.Y_AXIS));
        pairTuKhoa.setOpaque(false);
        pairTuKhoa.setAlignmentY(Component.TOP_ALIGNMENT);
        JLabel lblTuKhoa = fieldLabel("T\u1eeb kh\u00f3a");
        lblTuKhoa.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTuKhoa.add(lblTuKhoa);
        pairTuKhoa.add(Box.createVerticalStrut(4));
        txtKeyword = new RoundedTextField(800, 44, 10, "Nh\u1eadp m\u00e3 phi\u1ebfu \u0111\u1ed5i...");
        txtKeyword.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtKeyword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        pairTuKhoa.add(txtKeyword);
        pnlFields.add(pairTuKhoa);
        pnlFields.add(Box.createHorizontalStrut(10));

        JPanel pairTuNgay = new JPanel();
        pairTuNgay.setLayout(new BoxLayout(pairTuNgay, BoxLayout.Y_AXIS));
        pairTuNgay.setOpaque(false);
        pairTuNgay.setAlignmentY(Component.TOP_ALIGNMENT);
        JLabel lblTuNgay = fieldLabel("T\u1eeb ng\u00e0y");
        lblTuNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairTuNgay.add(lblTuNgay);
        pairTuNgay.add(Box.createVerticalStrut(4));
        spnTuNgay = makeDateSpinner();
        spnTuNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        spnTuNgay.setMaximumSize(new Dimension(200, 44));
        pairTuNgay.add(spnTuNgay);
        pnlFields.add(pairTuNgay);
        pnlFields.add(Box.createHorizontalStrut(10));

        JPanel pairDenNgay = new JPanel();
        pairDenNgay.setLayout(new BoxLayout(pairDenNgay, BoxLayout.Y_AXIS));
        pairDenNgay.setOpaque(false);
        pairDenNgay.setAlignmentY(Component.TOP_ALIGNMENT);
        JLabel lblDenNgay = fieldLabel("\u0110\u1ebfn ng\u00e0y");
        lblDenNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        pairDenNgay.add(lblDenNgay);
        pairDenNgay.add(Box.createVerticalStrut(4));
        spnDenNgay = makeDateSpinner();
        spnDenNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
        spnDenNgay.setMaximumSize(new Dimension(200, 44));
        pairDenNgay.add(spnDenNgay);
        pnlFields.add(pairDenNgay);

        card.add(pnlFields, BorderLayout.CENTER);

        // ===Hang nut bam can phai===
        pairButton = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pairButton.setOpaque(false);
        pairButton.setBorder(BorderFactory.createEmptyBorder(0, 12, 12, 12));
        btnXoaLoc = new RoundedButton(130, 44, 10, "\u2715  X\u00f3a l\u1ecdc", Colors.SECONDARY);
        btnXoaLoc.setForeground(Colors.TEXT_PRIMARY);
        pairButton.add(btnXoaLoc);
        btnTimKiem = new RoundedButton(150, 44, 10, "\uD83D\uDD0D  T\u00ecm ki\u1ebfm", Colors.PRIMARY);
        pairButton.add(btnTimKiem);
        card.add(pairButton, BorderLayout.SOUTH);

        return card;
    }

    private JPanel buildCardTitleRow() {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        row.setOpaque(false);
        JLabel icon = new JLabel("\uD83D\uDD0D");
        icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        icon.setForeground(Colors.PRIMARY);
        JLabel title = new JLabel("B\u1ed9 l\u1ecdc t\u00ecm ki\u1ebfm");
        title.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        title.setForeground(Colors.PRIMARY);
        row.add(icon);
        row.add(title);
        return row;
    }

    // ─────────────────────────────────────────────────────────
    // EMPTY STATE
    // ─────────────────────────────────────────────────────────
    private JPanel buildEmptyState() {
        JPanel card = createCard();
        card.setLayout(new GridBagLayout());

        JPanel inner = new JPanel();
        inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
        inner.setOpaque(false);

        JLabel iconLbl = new JLabel("\uD83D\uDD04");
        iconLbl.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        iconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titleLbl = new JLabel("T\u00ednh n\u0103ng \u0111ang ph\u00e1t tri\u1ec3n");
        titleLbl.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        titleLbl.setForeground(Colors.TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel noteLbl = new JLabel("Ch\u1ee9c n\u0103ng tra c\u1ee9u \u0111\u1ed5i h\u00e0ng s\u1ebd s\u1edbm \u0111\u01b0\u1ee3c c\u1eadp nh\u1eadt.");
        noteLbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        noteLbl.setForeground(Colors.TEXT_SECONDARY);
        noteLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

        inner.add(iconLbl);
        inner.add(Box.createVerticalStrut(12));
        inner.add(titleLbl);
        inner.add(Box.createVerticalStrut(6));
        inner.add(noteLbl);

        card.add(inner);
        return card;
    }

    // ─────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────
    private JPanel createCard() {
        JPanel p = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
                g2.setColor(Colors.BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setBackground(Color.WHITE);
        return p;
    }

    private JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        l.setForeground(Colors.TEXT_PRIMARY);
        l.setHorizontalAlignment(SwingConstants.LEFT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setMaximumSize(new Dimension(Integer.MAX_VALUE, l.getPreferredSize().height));
        return l;
    }

    private JSpinner makeDateSpinner() {
        JSpinner sp = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor ed = new JSpinner.DateEditor(sp, "dd/MM/yyyy");
        sp.setEditor(ed);
        sp.setPreferredSize(new Dimension(160, 42));
        sp.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        return sp;
    }
}
