package gui;

import constants.Colors;
import constants.FontStyle;
import entity.KhachHang;
import exception.RoundedButton;
import exception.RoundedPanel;
import exception.RoundedTextField;
import exception.StyledTable;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import service.KhachHang_Service;

public class KhachHang_GUI extends JPanel implements ActionListener {

    private JPanel pnlTitle, pnlContent, pnlCategory;
    private JLabel lblTitle;
    private JLabel lblNote;
    private JPanel pnlHead;
    private JPanel pnlButtonAddKH;
    private RoundedButton btnAddKH;
    private JPanel pnlSearch;
    private RoundedTextField txtSearch;
    private RoundedButton btnFind;
    private RoundedButton btnAll;
    private JWindow suggestionWindow;
    private javax.swing.DefaultListModel<String> suggestionModel = new javax.swing.DefaultListModel<>();
    private javax.swing.JList<String> lstSuggestions;
    private java.util.List<KhachHang> suggestionData = new java.util.ArrayList<>();
    private boolean suppressSuggestion = false;
    private JLabel lblDSKH;
    private ArrayList<KhachHang> list;
    private ArrayList<KhachHang> fullList = new ArrayList<>();
    private KhachHang_Service khachHangSV;
    private StyledTable tblKhachHang;
    private JPanel item;

    private static final String[] COLUMN_NAMES = {"Khách hàng", "Liên hệ", "Giới tính", "Điểm tích lũy", "Trạng thái", "", ""};

    public KhachHang_GUI() {
        list = new ArrayList<>();
        khachHangSV = new KhachHang_Service();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Colors.BACKGROUND);

        // ===== HEADER =====
        add(pnlHead = new JPanel(), BorderLayout.NORTH);
        pnlHead.setLayout(new BoxLayout(pnlHead, BoxLayout.X_AXIS));
        pnlHead.setBackground(Colors.BACKGROUND);
        pnlHead.add(pnlTitle = new JPanel());
        pnlHead.add(pnlButtonAddKH = new JPanel());

        pnlTitle.setLayout(new BoxLayout(pnlTitle, BoxLayout.Y_AXIS));
        pnlTitle.setPreferredSize(new Dimension(900, 0));
        pnlTitle.setBackground(Colors.BACKGROUND);
        pnlTitle.add(lblTitle = new JLabel("Khách hàng"));
        lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.FOREGROUND);

        pnlTitle.add(lblNote = new JLabel("Quản lý khách hàng trong hệ thống"));
        lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNote.setForeground(Colors.MUTED);

        pnlButtonAddKH.setBackground(Colors.BACKGROUND);
        pnlButtonAddKH.add(btnAddKH = new RoundedButton(170, 40, 10, "+ Thêm khách hàng", Colors.PRIMARY));

        // ===== CONTENT =====
        add(pnlContent = new JPanel(), BorderLayout.CENTER);
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(Colors.BACKGROUND);

        // Thống kê tổng quan
        pnlContent.add(pnlCategory = new JPanel(new GridLayout(1, 3, 16, 0)));
        pnlCategory.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        pnlCategory.setBackground(Colors.BACKGROUND);
        pnlCategory.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        pnlCategory.setPreferredSize(new Dimension(0,110));
        pnlCategory.add(statCard("Tổng khách hàng", khachHangSV.getSoLuongKhachHang(), Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
        pnlCategory.add(statCard("Đang hoạt động", khachHangSV.getSoLuongKhachHangHoatDong(), Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
        pnlCategory.add(statCard("Ngừng hoạt động", khachHangSV.getSoLuongKhachHangNgungHoatDong(), Colors.SECONDARY, Colors.DANGER, Colors.DANGER));

        // Thanh tìm kiếm
        pnlContent.add(pnlSearch = new JPanel());
        pnlSearch.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        pnlSearch.setBackground(Colors.BACKGROUND);

        pnlSearch.add(lblDSKH = new JLabel("Danh sách khách hàng"));
        lblDSKH.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblDSKH.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        pnlSearch.add(Box.createHorizontalGlue());
        pnlSearch.add(txtSearch = new RoundedTextField(500, 40, 20, "Tìm kiếm khách hàng "));
        pnlSearch.add(btnFind = new RoundedButton(130, 40, 20, "Tìm kiếm", Colors.PRIMARY));
        pnlSearch.add(btnAll = new RoundedButton(100, 40, 20, "Tất cả", Colors.SECONDARY));
        pnlSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        btnAll.setForeground(Colors.TEXT_PRIMARY);

        // ===== BẢNG =====
        tblKhachHang = new StyledTable(COLUMN_NAMES, list);

        tblKhachHang.setAvatarColumn(0, 220,
                v -> ((KhachHang) v).getTenKhachHang(),
                v -> ((KhachHang) v).getMaKhachHang());

        tblKhachHang.setIconTwoLineColumn(1, 220,
                "\u2709", v -> ((KhachHang) v).getEmail(),
                "\u260E", v -> ((KhachHang) v).getSoDienThoai());

        tblKhachHang.setSingleTextColumn(2, 100,
                v -> ((KhachHang) v).isGioiTinh() ? "Nam" : "Nữ");

        tblKhachHang.setSingleTextColumn(3, 130,
                v -> String.valueOf(((KhachHang) v).getDiemTichLuy()));

        tblKhachHang.setBadgeColumn(4, 130,
                v -> ((KhachHang) v).isTrangThai(), "Hoạt động", "Ngừng HĐ");

        tblKhachHang.setActionColumn(5, 80);
        tblKhachHang.setDeleteButtonColumn(6, 80);

        tblKhachHang.setActionColumnListener((row, obj) -> {
            KhachHang khachHang = (KhachHang) obj;
            moDialogChiTietKhachHang(khachHang);
        });

        tblKhachHang.setDeleteColumnListener((row, obj) -> {
            KhachHang khachHang = (KhachHang) obj;
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Bạn có chắc muốn xóa khách hàng \"" + khachHang.getTenKhachHang() + "\" không?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                khachHangSV.xoaKhachHang(khachHang.getMaKhachHang());
                list.remove(row.intValue());
                tblKhachHang.refresh();
                updateCategory();
            }
        });

        pnlContent.add(tblKhachHang);

        btnAddKH.addActionListener(this);
        btnFind.addActionListener(this);
        btnAll.addActionListener(this);
        txtSearch.addActionListener(e -> search());
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateSuggestions();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
            }
        });
        txtSearch.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                javax.swing.Timer t = new javax.swing.Timer(150, ev -> hideSuggestions());
                t.setRepeats(false);
                t.start();
            }
        });

        try {
            ArrayList<KhachHang> dsKH = khachHangSV.getDSKhachHang();
            if (dsKH == null || dsKH.isEmpty()) {
                System.out.println("[KhachHang_GUI] Cảnh báo: Danh sách khách hàng rỗng");
            }
            loadData(dsKH);
        } catch (Exception e) {
            System.out.println("[KhachHang_GUI] Lỗi khi tải dữ liệu khách hàng:");
            e.printStackTrace();
        }
    }

    public void loadData(ArrayList<KhachHang> dsKH) {
        fullList = new ArrayList<>(dsKH);
        list.clear();
        list.addAll(dsKH);
        tblKhachHang.refresh();
    }

    private void search() {
        hideSuggestions();
        String keyword = txtSearch.getText().trim();
        ArrayList<KhachHang> filtered = khachHangSV.timKiem(fullList, keyword);
        list.clear();
        list.addAll(filtered);
        tblKhachHang.refresh();
    }

    private void hideSuggestions() {
        if (suggestionWindow != null) {
            suggestionWindow.setVisible(false);
        }
    }

    private void initSuggestionWindow() {
        if (suggestionWindow != null) {
            return;
        }
        Window parent = SwingUtilities.getWindowAncestor(txtSearch);
        suggestionWindow = new JWindow(parent);
        suggestionWindow.setFocusableWindowState(false);
        lstSuggestions = new JList<>(suggestionModel);
        lstSuggestions.setBackground(Colors.BACKGROUND);
        lstSuggestions.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lstSuggestions.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> l, Object v, int idx, boolean sel, boolean focus) {
                JLabel lbl = (JLabel) super.getListCellRendererComponent(l, v, idx, sel, focus);
                lbl.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
                lbl.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
                lbl.setForeground(Colors.TEXT_PRIMARY);
                return lbl;
            }
        });
        lstSuggestions.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int idx = lstSuggestions.locationToIndex(e.getPoint());
                if (idx >= 0 && idx < suggestionData.size()) {
                    KhachHang kh = suggestionData.get(idx);
                    suppressSuggestion = true;
                    txtSearch.setText(kh.getTenKhachHang());
                    suppressSuggestion = false;
                    hideSuggestions();
                    search();
                }
            }
        });
        JScrollPane scroll = new JScrollPane(lstSuggestions);
        scroll.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(4, 0, 4, 0)));
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        suggestionWindow.add(scroll);
    }

    private void updateSuggestions() {
        if (suppressSuggestion) {
            return;
        }
        String kw = txtSearch.getText().trim();
        if (kw.isEmpty()) {
            hideSuggestions();
            return;
        }
        ArrayList<KhachHang> matches = khachHangSV.timKiem(fullList, kw);
        if (matches.isEmpty()) {
            hideSuggestions();
            return;
        }
        if (!txtSearch.isShowing()) {
            return;
        }
        initSuggestionWindow();
        suggestionModel.clear();
        suggestionData.clear();
        int max = Math.min(8, matches.size());
        for (int i = 0; i < max; i++) {
            KhachHang kh = matches.get(i);
            suggestionData.add(kh);
            suggestionModel.addElement(kh.getTenKhachHang() + "   (" + kh.getMaKhachHang() + ")");
        }
        Point loc = txtSearch.getLocationOnScreen();
        int h = Math.min(max * 32 + 10, 260);
        suggestionWindow.setBounds(loc.x, loc.y + txtSearch.getHeight() + 2, txtSearch.getWidth(), h);
        suggestionWindow.setVisible(true);
    }

    private void updateCategory() {
        pnlCategory.removeAll();
        pnlCategory.add(statCard("Tổng khách hàng", khachHangSV.getSoLuongKhachHang(), Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
        pnlCategory.add(statCard("Đang hoạt động", khachHangSV.getSoLuongKhachHangHoatDong(), Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
        pnlCategory.add(statCard("Ngừng hoạt động", khachHangSV.getSoLuongKhachHangNgungHoatDong(), Colors.SECONDARY, Colors.DANGER, Colors.DANGER));
        pnlCategory.revalidate();
        pnlCategory.repaint();
        pnlContent.revalidate();
        pnlContent.repaint();
    }

    private JPanel statCard(String title, int value, Color bg, Color valColor, Color titleColor) {
        RoundedPanel c = new RoundedPanel(200, 90, 16);
        c.setBackground(bg);
        c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
        c.setBorder(BorderFactory.createEmptyBorder(14, 18, 14, 18));
        JLabel lblT = new JLabel(title);
        lblT.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblT.setForeground(titleColor);
        lblT.setAlignmentX(LEFT_ALIGNMENT);
        c.add(lblT);
        c.add(Box.createVerticalStrut(6));
        JLabel lblV = new JLabel(String.valueOf(value));
        lblV.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblV.setForeground(valColor);
        lblV.setAlignmentX(LEFT_ALIGNMENT);
        c.add(lblV);
        return c;
    }

    private void moDialogChiTietKhachHang(KhachHang khachHang) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Chi tiết khách hàng", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 560);
        dialog.setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBackground(Colors.BACKGROUND);

        // ===== HEADER =====
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.X_AXIS));
        pnlHeader.setBackground(Colors.BACKGROUND);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        pnlHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // Avatar
        JPanel pnlAvatar = new RoundedPanel(80, 80, 50);
        pnlAvatar.setOpaque(true);
        pnlAvatar.setBackground(Colors.PRIMARY);
        pnlAvatar.setLayout(new BorderLayout());
        pnlAvatar.setPreferredSize(new Dimension(80, 80));
        pnlAvatar.setMaximumSize(new Dimension(80, 80));
        pnlAvatar.setMinimumSize(new Dimension(80, 80));
        JLabel lblInitials = new JLabel(getInitials(khachHang.getTenKhachHang()));
        lblInitials.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        lblInitials.setForeground(Color.WHITE);
        lblInitials.setHorizontalAlignment(SwingConstants.CENTER);
        lblInitials.setVerticalAlignment(SwingConstants.CENTER);
        pnlAvatar.add(lblInitials);

        pnlHeader.add(pnlAvatar);
        pnlHeader.add(Box.createHorizontalStrut(15));

        // Info header
        JPanel pnlHeaderInfo = new JPanel();
        pnlHeaderInfo.setLayout(new BoxLayout(pnlHeaderInfo, BoxLayout.Y_AXIS));
        pnlHeaderInfo.setOpaque(false);

        JLabel lblTenKH = new JLabel(khachHang.getTenKhachHang());
        lblTenKH.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblTenKH.setForeground(Colors.TEXT_PRIMARY);
        pnlHeaderInfo.add(lblTenKH);

        JLabel lblMaKH = new JLabel(khachHang.getMaKhachHang());
        lblMaKH.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblMaKH.setForeground(Colors.TEXT_SECONDARY);
        pnlHeaderInfo.add(lblMaKH);

        pnlHeaderInfo.add(Box.createVerticalStrut(10));

        JLabel lblTrangThai = new JLabel(khachHang.isTrangThai() ? "● Hoạt động" : "● Ngừng hoạt động");
        lblTrangThai.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblTrangThai.setForeground(khachHang.isTrangThai() ? Colors.SUCCESS : Color.GRAY);
        pnlHeaderInfo.add(lblTrangThai);

        pnlHeader.add(pnlHeaderInfo);
        pnlHeader.add(Box.createHorizontalGlue());

        JButton btnEdit = new RoundedButton(120, 40, 15, "Chỉnh sửa", Colors.PRIMARY);
        btnEdit.setPreferredSize(new Dimension(120, 40));
        btnEdit.setMaximumSize(new Dimension(120, 40));
        btnEdit.addActionListener(e -> {
            dialog.dispose();
            moDialogSuaKhachHang(khachHang);
        });
        pnlHeader.add(btnEdit);

        pnlMain.add(pnlHeader);

        // ===== CONTENT =====
        JPanel pnlInfoContent = new JPanel();
        pnlInfoContent.setLayout(new BoxLayout(pnlInfoContent, BoxLayout.Y_AXIS));
        pnlInfoContent.setBackground(Colors.BACKGROUND);
        pnlInfoContent.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Row 1: 2 cột
        JPanel pnlRow1 = new JPanel();
        pnlRow1.setLayout(new BoxLayout(pnlRow1, BoxLayout.X_AXIS));
        pnlRow1.setOpaque(false);
        pnlRow1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        JPanel pnlInfoLeft = createInfoBox("THÔNG TIN CÁ NHÂN", 260,
                new String[]{"Mã khách hàng", "Giới tính", "Điểm tích lũy"},
                new String[]{khachHang.getMaKhachHang(),
                    khachHang.isGioiTinh() ? "Nam" : "Nữ",
                    String.valueOf(khachHang.getDiemTichLuy())},
                new String[]{"", "", ""});
        pnlInfoLeft.setPreferredSize(new Dimension(260, 200));
        pnlInfoLeft.setMaximumSize(new Dimension(260, 200));

        JPanel pnlInfoRight = createInfoBox("THÔNG TIN LIÊN HỆ", 260,
                new String[]{"Số điện thoại", "Email"},
                new String[]{khachHang.getSoDienThoai(),
                    khachHang.getEmail() != null ? khachHang.getEmail() : ""},
                new String[]{"\u260E", "\u2709"});
        pnlInfoRight.setPreferredSize(new Dimension(260, 200));
        pnlInfoRight.setMaximumSize(new Dimension(260, 200));

        pnlRow1.add(pnlInfoLeft);
        pnlRow1.add(Box.createHorizontalStrut(15));
        pnlRow1.add(pnlInfoRight);
        pnlRow1.add(Box.createHorizontalGlue());

        pnlInfoContent.add(pnlRow1);
        pnlMain.add(pnlInfoContent);
        pnlMain.add(Box.createVerticalGlue());

        // ===== FOOTER =====
        JPanel pnlFooter = new JPanel();
        pnlFooter.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlFooter.setBackground(Colors.BACKGROUND);
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        pnlFooter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JButton btnSave = new RoundedButton(150, 40, 15, "Chỉnh sửa", Colors.PRIMARY);
        btnSave.addActionListener(e -> {
            dialog.dispose();
            moDialogSuaKhachHang(khachHang);
        });

        JButton btnClose = new RoundedButton(100, 40, 15, "Đóng", Colors.SECONDARY);
        btnClose.addActionListener(e -> dialog.dispose());

        pnlFooter.add(btnSave);
        pnlFooter.add(btnClose);
        pnlMain.add(pnlFooter);

        JScrollPane scrollPane = new JScrollPane(pnlMain);
        scrollPane.setBorder(null);
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    private JPanel createInfoBox(String title, int width, String[] labels, String[] values, String[] icons) {
        JPanel box = new RoundedPanel(width, 230, 15);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        box.setBackground(Colors.SECONDARY);

        JLabel lblBoxTitle = new JLabel(title);
        lblBoxTitle.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblBoxTitle.setForeground(new Color(100, 110, 120));
        lblBoxTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(lblBoxTitle);
        box.add(Box.createVerticalStrut(15));

        for (int i = 0; i < labels.length; i++) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            if (!icons[i].isEmpty()) {
                JLabel lblIcon = new JLabel(icons[i]);
                lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 18));
                lblIcon.setForeground(Colors.PRIMARY);
                lblIcon.setPreferredSize(new Dimension(30, 35));
                lblIcon.setVerticalAlignment(SwingConstants.TOP);
                row.add(lblIcon);
                row.add(Box.createHorizontalStrut(5));
            }

            JPanel colInfo = new JPanel();
            colInfo.setLayout(new BoxLayout(colInfo, BoxLayout.Y_AXIS));
            colInfo.setOpaque(false);

            JLabel lblLabel = new JLabel(labels[i]);
            lblLabel.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
            lblLabel.setForeground(new Color(110, 120, 130));
            lblLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            colInfo.add(lblLabel);
            colInfo.add(Box.createVerticalStrut(2));

            JLabel lblValue = new JLabel(values[i] != null ? values[i] : "");
            lblValue.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            lblValue.setForeground(Colors.TEXT_PRIMARY);
            lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);
            colInfo.add(lblValue);

            row.add(colInfo);
            row.add(Box.createHorizontalGlue());
            box.add(row);

            if (i < labels.length - 1 && !icons[i].isEmpty()) {
                box.add(Box.createVerticalStrut(8));
                JSeparator sep = new JSeparator();
                sep.setForeground(new Color(50, 55, 60));
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                box.add(sep);
                box.add(Box.createVerticalStrut(8));
            } else if (i < labels.length - 1) {
                box.add(Box.createVerticalStrut(6));
            }
        }

        return box;
    }

    private void moDialogThemKhachHang() {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Thêm khách hàng mới", true);
        dialog.setSize(650, 550);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BorderLayout());
        pnlMain.setBackground(Colors.BACKGROUND);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        // ===== HEADER =====
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
        // pnlHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblFormTitle = new JLabel("Thêm khách hàng mới");
        lblFormTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblFormTitle.setForeground(Colors.TEXT_PRIMARY);
        lblFormTitle.setHorizontalAlignment(SwingConstants.LEFT);
        pnlHeader.add(lblFormTitle);

        JLabel lblFormSub = new JLabel("Điền đầy đủ thông tin rồi nhấn Thêm khách hàng  (* bắt buộc)");
        lblFormSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblFormSub.setForeground(Colors.TEXT_SECONDARY);
        lblFormSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblFormSub.setHorizontalAlignment(SwingConstants.LEFT);
        pnlHeader.add(lblFormSub);

        pnlMain.add(pnlHeader, BorderLayout.NORTH);

        // ===== FORM FIELDS =====
        RoundedTextField txtMaKH = new RoundedTextField(240, 25, 10, "");
        txtMaKH.setText(taoMaKhachHang());
        txtMaKH.setEnabled(false);

        RoundedTextField txtTenKH = new RoundedTextField(240, 25, 10, "Họ và tên đầy đủ");
        RoundedTextField txtSDT = new RoundedTextField(240, 25, 10, "10 chữ số");
        RoundedTextField txtEmail = new RoundedTextField(240, 25, 10, "example@email.com");

        JRadioButton rdoNam = new JRadioButton("Nam");
        JRadioButton rdoNu = new JRadioButton("Nữ");
        rdoNam.setSelected(true);
        rdoNam.setOpaque(false);
        rdoNu.setOpaque(false);
        rdoNam.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        rdoNu.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        ButtonGroup bgGioiTinh = new ButtonGroup();
        bgGioiTinh.add(rdoNam);
        bgGioiTinh.add(rdoNu);
        JPanel pnlGioiTinh = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlGioiTinh.setOpaque(false);
        pnlGioiTinh.add(rdoNam);
        pnlGioiTinh.add(rdoNu);

        JCheckBox chkTrangThai = new JCheckBox("Đang hoạt động");
        chkTrangThai.setSelected(true);
        chkTrangThai.setOpaque(false);
        chkTrangThai.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));

        // ===== ROW 1: 2 CỘT =====
        JPanel pnlRow1 = new JPanel();
        pnlRow1.setLayout(new BoxLayout(pnlRow1, BoxLayout.X_AXIS));
        pnlRow1.setOpaque(false);
        pnlRow1.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlRow1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 300));

        // Trái — Thông tin cá nhân
        RoundedPanel pnlLeft = new RoundedPanel(260, 260, 15);
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlLeft.setBackground(Colors.SECONDARY);
        pnlLeft.setMaximumSize(new Dimension(260, 280));

        JLabel lblPersonal = new JLabel("THÔNG TIN CÁ NHÂN");
        lblPersonal.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblPersonal.setForeground(new Color(100, 110, 120));
        lblPersonal.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlLeft.add(lblPersonal);
        pnlLeft.add(Box.createVerticalStrut(12));
        pnlLeft.add(createFormField("Mã khách hàng (tự động)", txtMaKH));
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(createFormField("Họ và tên *", txtTenKH));
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(createFormField("Giới tính *", pnlGioiTinh));

        // Phải — Thông tin liên hệ
        RoundedPanel pnlRight = new RoundedPanel(260, 260, 15);
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlRight.setBackground(Colors.SECONDARY);
        pnlRight.setMaximumSize(new Dimension(260, 280));

        JLabel lblContact = new JLabel("THÔNG TIN LIÊN HỆ");
        lblContact.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblContact.setForeground(new Color(100, 110, 120));
        lblContact.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlRight.add(lblContact);
        pnlRight.add(Box.createVerticalStrut(12));
        pnlRight.add(createFormField("Số điện thoại *", txtSDT));
        pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(createFormField("Email", txtEmail));
        pnlRight.add(Box.createVerticalStrut(10));

        JPanel pnlTrangThai = new JPanel();
        pnlTrangThai.setLayout(new BoxLayout(pnlTrangThai, BoxLayout.Y_AXIS));
        pnlTrangThai.setOpaque(false);
        JLabel lblTT = new JLabel("Trạng thái");
        lblTT.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblTT.setForeground(Colors.TEXT_SECONDARY);
        lblTT.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTrangThai.add(lblTT);
        pnlTrangThai.add(Box.createVerticalStrut(5));
        pnlTrangThai.add(chkTrangThai);
        pnlRight.add(pnlTrangThai);

        pnlRow1.add(Box.createHorizontalGlue());
        pnlRow1.add(pnlLeft);
        pnlRow1.add(Box.createHorizontalStrut(15));
        pnlRow1.add(pnlRight);
        pnlRow1.add(Box.createHorizontalGlue());
        pnlMain.add(pnlRow1, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlFooter.setOpaque(false);
        pnlFooter.setAlignmentX(Component.CENTER_ALIGNMENT);
        pnlFooter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));

        JButton btnHuy = new RoundedButton(100, 40, 15, "Hủy", Colors.SECONDARY);
        JButton btnThem = new RoundedButton(160, 40, 15, "Thêm khách hàng", Colors.PRIMARY);

        btnHuy.addActionListener(ev -> dialog.dispose());
        btnThem.addActionListener(ev -> {
            try {
                KhachHang kh = new KhachHang(
                        txtMaKH.getText().trim(),
                        txtTenKH.getText().trim(),
                        txtSDT.getText().trim(),
                        txtEmail.getText().trim(),
                        rdoNam.isSelected(),
                        0,
                        chkTrangThai.isSelected()
                );
                if (khachHangSV.themKhachHang(kh)) {
                    list.add(kh);
                    tblKhachHang.refresh();
                    updateCategory();
                    JOptionPane.showMessageDialog(dialog,
                            "Thêm khách hàng thành công!", "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Thêm thất bại. Vui lòng thử lại.", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(), "Dữ liệu không hợp lệ",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        pnlFooter.add(btnHuy);
        pnlFooter.add(btnThem);
        pnlMain.add(pnlFooter, BorderLayout.SOUTH);

        dialog.add(pnlMain, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void moDialogSuaKhachHang(KhachHang khachHang) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Cập nhật khách hàng", true);
        dialog.setSize(560, 500);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBackground(Colors.BACKGROUND);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        // ===== HEADER =====
        JLabel lblFormTitle = new JLabel("Cập nhật thông tin khách hàng");
        lblFormTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblFormTitle.setForeground(Colors.TEXT_PRIMARY);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMain.add(lblFormTitle);

        JLabel lblFormSub = new JLabel("Chỉnh sửa thông tin rồi nhấn Lưu thay đổi  (* bắt buộc)");
        lblFormSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblFormSub.setForeground(Colors.TEXT_SECONDARY);
        lblFormSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMain.add(lblFormSub);
        pnlMain.add(Box.createVerticalStrut(20));

        // ===== FORM FIELDS (pre-filled) =====
        RoundedTextField txtMaKH = new RoundedTextField(240, 38, 10, "");
        txtMaKH.setText(khachHang.getMaKhachHang());
        txtMaKH.setEnabled(false);

        RoundedTextField txtTenKH = new RoundedTextField(240, 38, 10, "Họ và tên đầy đủ");
        txtTenKH.setText(khachHang.getTenKhachHang());

        RoundedTextField txtSDT = new RoundedTextField(240, 38, 10, "10 chữ số");
        txtSDT.setText(khachHang.getSoDienThoai() != null ? khachHang.getSoDienThoai() : "");

        RoundedTextField txtEmail = new RoundedTextField(240, 38, 10, "example@email.com");
        txtEmail.setText(khachHang.getEmail() != null ? khachHang.getEmail() : "");

        JRadioButton rdoNam = new JRadioButton("Nam");
        JRadioButton rdoNu = new JRadioButton("Nữ");
        rdoNam.setSelected(khachHang.isGioiTinh());
        rdoNu.setSelected(!khachHang.isGioiTinh());
        rdoNam.setOpaque(false);
        rdoNu.setOpaque(false);
        rdoNam.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        rdoNu.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        ButtonGroup bgGioiTinh = new ButtonGroup();
        bgGioiTinh.add(rdoNam);
        bgGioiTinh.add(rdoNu);
        JPanel pnlGioiTinh = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlGioiTinh.setOpaque(false);
        pnlGioiTinh.add(rdoNam);
        pnlGioiTinh.add(rdoNu);

        JCheckBox chkTrangThai = new JCheckBox("Đang hoạt động");
        chkTrangThai.setSelected(khachHang.isTrangThai());
        chkTrangThai.setOpaque(false);
        chkTrangThai.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));

        // ===== ROW 1: 2 CỘT =====
        JPanel pnlRow1 = new JPanel();
        pnlRow1.setLayout(new BoxLayout(pnlRow1, BoxLayout.X_AXIS));
        pnlRow1.setOpaque(false);
        pnlRow1.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedPanel pnlLeft = new RoundedPanel(260, 260, 15);
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlLeft.setBackground(Colors.SECONDARY);
        pnlLeft.setMaximumSize(new Dimension(260, 280));

        JLabel lblPersonal = new JLabel("THÔNG TIN CÁ NHÂN");
        lblPersonal.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblPersonal.setForeground(new Color(100, 110, 120));
        lblPersonal.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlLeft.add(lblPersonal);
        pnlLeft.add(Box.createVerticalStrut(12));
        pnlLeft.add(createFormField("Mã khách hàng", txtMaKH));
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(createFormField("Họ và tên *", txtTenKH));
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(createFormField("Giới tính *", pnlGioiTinh));

        RoundedPanel pnlRight = new RoundedPanel(260, 260, 15);
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlRight.setBackground(Colors.SECONDARY);
        pnlRight.setMaximumSize(new Dimension(260, 280));

        JLabel lblContact = new JLabel("THÔNG TIN LIÊN HỆ");
        lblContact.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblContact.setForeground(new Color(100, 110, 120));
        lblContact.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlRight.add(lblContact);
        pnlRight.add(Box.createVerticalStrut(12));
        pnlRight.add(createFormField("Số điện thoại *", txtSDT));
        pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(createFormField("Email", txtEmail));
        pnlRight.add(Box.createVerticalStrut(10));

        JPanel pnlTrangThai = new JPanel();
        pnlTrangThai.setLayout(new BoxLayout(pnlTrangThai, BoxLayout.Y_AXIS));
        pnlTrangThai.setOpaque(false);
        JLabel lblTT = new JLabel("Trạng thái");
        lblTT.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblTT.setForeground(Colors.TEXT_SECONDARY);
        lblTT.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTrangThai.add(lblTT);
        pnlTrangThai.add(Box.createVerticalStrut(5));
        pnlTrangThai.add(chkTrangThai);
        pnlRight.add(pnlTrangThai);

        pnlRow1.add(pnlLeft);
        pnlRow1.add(Box.createHorizontalStrut(15));
        pnlRow1.add(pnlRight);
        pnlMain.add(pnlRow1);
        pnlMain.add(Box.createVerticalStrut(20));

        // ===== FOOTER =====
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFooter.setOpaque(false);
        pnlFooter.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnHuy = new RoundedButton(100, 40, 15, "Hủy", Colors.SECONDARY);
        JButton btnLuu = new RoundedButton(160, 40, 15, "Lưu thay đổi", Colors.PRIMARY);

        btnHuy.addActionListener(ev -> dialog.dispose());
        btnLuu.addActionListener(ev -> {
            try {
                khachHang.setTenKhachHang(txtTenKH.getText().trim());
                khachHang.setSoDienThoai(txtSDT.getText().trim());
                khachHang.setEmail(txtEmail.getText().trim());
                khachHang.setGioiTinh(rdoNam.isSelected());
                khachHang.setTrangThai(chkTrangThai.isSelected());

                if (khachHangSV.updateKhachHang(khachHang)) {
                    tblKhachHang.refresh();
                    updateCategory();
                    JOptionPane.showMessageDialog(dialog,
                            "Cập nhật thành công!", "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Cập nhật thất bại. Vui lòng thử lại.", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(), "Dữ liệu không hợp lệ",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        pnlFooter.add(btnHuy);
        pnlFooter.add(btnLuu);
        pnlMain.add(pnlFooter);

        JScrollPane scroll = new JScrollPane(pnlMain);
        scroll.setBorder(null);
        dialog.add(pnlMain, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private JPanel createFormField(String labelText, JComponent field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lbl.setForeground(Colors.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(lbl);
        row.add(Box.createVerticalStrut(5));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(field);
        return row;
    }

    private String taoMaKhachHang() {
        int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        int maxNum = 0;
        for (KhachHang kh : list) {
            try {
                String ma = kh.getMaKhachHang();
                int num = Integer.parseInt(ma.substring(ma.length() - 3));
                if (num > maxNum) {
                    maxNum = num;
                }
            } catch (Exception ignored) {
            }
        }
        return String.format("KH%d%03d", year, maxNum + 1);
    }

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "KH";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[parts.length - 2].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
        return fullName.substring(0, Math.min(2, fullName.length())).toUpperCase();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddKH) {
            moDialogThemKhachHang();
        } else if (e.getSource() == btnFind) {
            search();
        } else if (e.getSource() == btnAll) {
            txtSearch.setText("");
            list.clear();
            list.addAll(fullList);
            tblKhachHang.refresh();
        }
    }
}