package gui;

import constants.Colors;
import constants.FontStyle;
import entity.KhuyenMai;
import exception.RoundedButton;
import exception.RoundedPanel;
import exception.RoundedTextField;
import exception.StyledTable;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import javax.swing.*;
import service.KhuyenMai_Service;

public class KhuyenMai_GUI extends JPanel implements ActionListener {

    private JPanel pnlTitle, pnlContent, pnlCategory;
    private JLabel lblTitle, lblNote;
    private JPanel pnlHead, pnlButtonAdd;
    private RoundedButton btnAddKM;
    private JPanel pnlSearch;
    private RoundedTextField txtSearch;
    private RoundedButton btnFind, btnAll;
    private JWindow suggestionWindow;
    private DefaultListModel<String> suggestionModel = new DefaultListModel<>();
    private JList<String> lstSuggestions;
    private java.util.List<KhuyenMai> suggestionData = new java.util.ArrayList<>();
    private boolean suppressSuggestion = false;
    private JLabel lblDSKM;
    private ArrayList<KhuyenMai> list;
    private ArrayList<KhuyenMai> fullList = new ArrayList<>();
    private KhuyenMai_Service khuyenMaiSV;
    private StyledTable tblKhuyenMai;
    private JPanel item;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String[] COLUMN_NAMES = {"Chương trình", "Giảm giá", "Thời gian", "Trạng thái", "", ""};

    public KhuyenMai_GUI() {
        list = new ArrayList<>();
        khuyenMaiSV = new KhuyenMai_Service();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Colors.BACKGROUND);

        // ===== HEADER =====
        add(pnlHead = new JPanel(), BorderLayout.NORTH);
        pnlHead.setLayout(new BoxLayout(pnlHead, BoxLayout.X_AXIS));
        pnlHead.setBackground(Colors.BACKGROUND);
        pnlHead.add(pnlTitle = new JPanel());
        pnlHead.add(pnlButtonAdd = new JPanel());

        pnlTitle.setLayout(new BoxLayout(pnlTitle, BoxLayout.Y_AXIS));
        pnlTitle.setPreferredSize(new Dimension(900, 0));
        pnlTitle.setBackground(Colors.BACKGROUND);
        pnlTitle.add(lblTitle = new JLabel("Khuyến mãi"));
        lblTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        pnlTitle.add(lblNote = new JLabel("Quản lý chương trình khuyến mãi"));
        lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));

        pnlButtonAdd.setBackground(Colors.BACKGROUND);
        pnlButtonAdd.add(btnAddKM = new RoundedButton(200, 45, 20, "+ Thêm khuyến mãi", Colors.PRIMARY));

        // ===== CONTENT =====
        add(pnlContent = new JPanel(), BorderLayout.CENTER);
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(Colors.BACKGROUND);

        // Stat cards
        pnlContent.add(pnlCategory = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)));
        pnlCategory.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        pnlCategory.setBackground(Colors.BACKGROUND);
        pnlCategory.add(createPanelCategory(khuyenMaiSV.getSoLuongKhuyenMai(), "Tổng khuyến mãi"));
        pnlCategory.add(createPanelCategory(khuyenMaiSV.getSoLuongDangHoatDong(), "Đang hoạt động"));
        pnlCategory.add(createPanelCategory(khuyenMaiSV.getSoLuongSapDienRa(), "Sắp diễn ra"));
        pnlCategory.add(createPanelCategory(khuyenMaiSV.getSoLuongDaKetThuc(), "Đã kết thúc"));

        // Search bar
        pnlContent.add(pnlSearch = new JPanel());
        pnlSearch.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        pnlSearch.setBackground(Colors.BACKGROUND);
        pnlSearch.add(lblDSKM = new JLabel("Danh sách khuyến mãi"));
        lblDSKM.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblDSKM.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
        pnlSearch.add(Box.createHorizontalGlue());
        pnlSearch.add(txtSearch = new RoundedTextField(500, 40, 20, "Tìm kiếm khuyến mãi..."));
        pnlSearch.add(btnFind = new RoundedButton(130, 40, 20, "Tìm kiếm", Colors.PRIMARY));
        pnlSearch.add(btnAll = new RoundedButton(100, 40, 20, "Tất cả", Colors.SECONDARY));
        btnAll.setForeground(Colors.TEXT_PRIMARY);

        // ===== TABLE =====
        tblKhuyenMai = new StyledTable(COLUMN_NAMES, list);

        tblKhuyenMai.setTwoLineColumn(0, 290,
                v -> ((KhuyenMai) v).getTenKhuyenMai(),
                v -> ((KhuyenMai) v).getMaKhuyenMai());

        tblKhuyenMai.setSingleTextColumn(1, 110,
                v -> {
                    double pct = ((KhuyenMai) v).getPhanTramGG();
                    return pct > 0 ? String.format("%.0f%%", pct) : "—";
                });

        tblKhuyenMai.setTwoLineColumn(2, 190,
                v -> "Từ: " + ((KhuyenMai) v).getNgayBatDau().format(FMT),
                v -> "Đến: " + ((KhuyenMai) v).getNgayKetThuc().format(FMT));

        tblKhuyenMai.setBadgeColumn(3, 140,
                v -> khuyenMaiSV.isDangHoatDong((KhuyenMai) v), "Đang diễn ra", "Không hoạt động");

        tblKhuyenMai.setActionColumn(4, 80);
        tblKhuyenMai.setDeleteButtonColumn(5, 80);

        tblKhuyenMai.setActionColumnListener((row, obj) -> moDialogChiTietKhuyenMai((KhuyenMai) obj));

        tblKhuyenMai.setDeleteColumnListener((row, obj) -> {
            KhuyenMai km = (KhuyenMai) obj;
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Bạn có chắc muốn xóa khuyến mãi \"" + km.getTenKhuyenMai() + "\" không?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                khuyenMaiSV.xoaKhuyenMai(km.getMaKhuyenMai());
                list.remove(row.intValue());
                fullList.remove(km);
                tblKhuyenMai.refresh();
                updateCategory();
            }
        });

        pnlContent.add(tblKhuyenMai);

        // ===== EVENTS =====
        btnAddKM.addActionListener(this);
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
            loadData(khuyenMaiSV.getDSKhuyenMai());
        } catch (Exception e) {
            System.out.println("[KhuyenMai_GUI] Lỗi khi tải dữ liệu:");
            e.printStackTrace();
        }
    }

    public void loadData(ArrayList<KhuyenMai> dsKM) {
        fullList = new ArrayList<>(dsKM);
        list.clear();
        list.addAll(dsKM);
        tblKhuyenMai.refresh();
    }

    private void search() {
        hideSuggestions();
        String keyword = txtSearch.getText().trim();
        ArrayList<KhuyenMai> filtered = khuyenMaiSV.timKiem(fullList, keyword);
        list.clear();
        list.addAll(filtered);
        tblKhuyenMai.refresh();
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
                    KhuyenMai km = suggestionData.get(idx);
                    suppressSuggestion = true;
                    txtSearch.setText(km.getTenKhuyenMai());
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
        ArrayList<KhuyenMai> matches = khuyenMaiSV.timKiem(fullList, kw);
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
            KhuyenMai km = matches.get(i);
            suggestionData.add(km);
            suggestionModel.addElement(km.getTenKhuyenMai() + "   (" + km.getMaKhuyenMai() + ")");
        }
        Point loc = txtSearch.getLocationOnScreen();
        int h = Math.min(max * 32 + 10, 260);
        suggestionWindow.setBounds(loc.x, loc.y + txtSearch.getHeight() + 2, txtSearch.getWidth(), h);
        suggestionWindow.setVisible(true);
    }

    private void updateCategory() {
        pnlCategory.removeAll();
        pnlCategory.add(createPanelCategory(khuyenMaiSV.getSoLuongKhuyenMai(), "Tổng khuyến mãi"));
        pnlCategory.add(createPanelCategory(khuyenMaiSV.getSoLuongDangHoatDong(), "Đang hoạt động"));
        pnlCategory.add(createPanelCategory(khuyenMaiSV.getSoLuongSapDienRa(), "Sắp diễn ra"));
        pnlCategory.add(createPanelCategory(khuyenMaiSV.getSoLuongDaKetThuc(), "Đã kết thúc"));
        pnlCategory.revalidate();
        pnlCategory.repaint();
        pnlContent.revalidate();
        pnlContent.repaint();
    }

    public JPanel createPanelCategory(int number, String title) {
        item = new RoundedPanel(250, 100, 25);
        item.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        item.setBackground(Colors.BACKGROUND);

        RoundedPanel pnlNumber = new RoundedPanel(50, 50, 12);
        pnlNumber.setLayout(new GridBagLayout());
        pnlNumber.setBackground(Colors.PRIMARY_LIGHT);
        JLabel lblNumber = new JLabel(Integer.toString(number));
        lblNumber.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblNumber.setForeground(Colors.SUCCESS);
        pnlNumber.add(lblNumber);

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BoxLayout(pnlInfo, BoxLayout.Y_AXIS));
        pnlInfo.setOpaque(false);

        JLabel lblTitle2 = new JLabel(title);
        lblTitle2.setFont(FontStyle.font(FontStyle.LG, FontStyle.NORMAL));
        lblTitle2.setForeground(new Color(160, 160, 160));

        JLabel lblCount = new JLabel(Integer.toString(number));
        lblCount.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));

        pnlInfo.add(lblTitle2);
        pnlInfo.add(Box.createVerticalStrut(4));
        pnlInfo.add(lblCount);

        item.add(pnlNumber);
        item.add(pnlInfo);
        return item;
    }

    // ===== DIALOGS =====
    private void moDialogChiTietKhuyenMai(KhuyenMai km) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Chi tiết khuyến mãi", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(560, 430);
        dialog.setLocationRelativeTo(null);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BorderLayout());
        pnlMain.setBackground(Colors.BACKGROUND);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        // Header
        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.X_AXIS));
        pnlHeader.setOpaque(false);
        pnlHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        RoundedPanel pnlIcon = new RoundedPanel(60, 60, 30);
        pnlIcon.setOpaque(true);
        pnlIcon.setBackground(Colors.PRIMARY_LIGHT);
        pnlIcon.setLayout(new GridBagLayout());
        pnlIcon.setPreferredSize(new Dimension(60, 60));
        pnlIcon.setMaximumSize(new Dimension(60, 60));
        pnlIcon.setMinimumSize(new Dimension(60, 60));
        JLabel lblIconChar = new JLabel("%");
        lblIconChar.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblIconChar.setForeground(Colors.PRIMARY);
        pnlIcon.add(lblIconChar);

        pnlHeader.add(pnlIcon);
        pnlHeader.add(Box.createHorizontalStrut(15));

        JPanel pnlHeaderInfo = new JPanel();
        pnlHeaderInfo.setLayout(new BoxLayout(pnlHeaderInfo, BoxLayout.Y_AXIS));
        pnlHeaderInfo.setOpaque(false);

        JLabel lblTenKM = new JLabel(km.getTenKhuyenMai());
        lblTenKM.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblTenKM.setForeground(Colors.TEXT_PRIMARY);
        pnlHeaderInfo.add(lblTenKM);

        JLabel lblMaKM = new JLabel(km.getMaKhuyenMai());
        lblMaKM.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblMaKM.setForeground(Colors.TEXT_SECONDARY);
        pnlHeaderInfo.add(lblMaKM);
        pnlHeaderInfo.add(Box.createVerticalStrut(6));

        String ttHienThi = khuyenMaiSV.getTrangThaiHienThi(km);
        Color ttColor = ttHienThi.equals("Đang diễn ra") ? Colors.SUCCESS
                : ttHienThi.equals("Sắp diễn ra") ? Colors.PRIMARY : Color.GRAY;
        JLabel lblTT = new JLabel("● " + ttHienThi);
        lblTT.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblTT.setForeground(ttColor);
        pnlHeaderInfo.add(lblTT);

        pnlHeader.add(pnlHeaderInfo);
        pnlHeader.add(Box.createHorizontalGlue());

        JButton btnEdit = new RoundedButton(120, 40, 15, "Chỉnh sửa", Colors.PRIMARY);
        btnEdit.setPreferredSize(new Dimension(120, 40));
        btnEdit.setMaximumSize(new Dimension(120, 40));
        btnEdit.addActionListener(e -> {
            dialog.dispose();
            moDialogSuaKhuyenMai(km);
        });
        pnlHeader.add(btnEdit);
        pnlMain.add(pnlHeader, BorderLayout.NORTH);

        // Info boxes
        JPanel pnlRow = new JPanel();
        pnlRow.setLayout(new BoxLayout(pnlRow, BoxLayout.X_AXIS));
        pnlRow.setOpaque(false);
        pnlRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnlLeft = createInfoBox("THÔNG TIN KHUYẾN MÃI", 250,
                new String[]{"Mã khuyến mãi", "Phần trăm giảm giá", "Trạng thái"},
                new String[]{km.getMaKhuyenMai(),
                    String.format("%.0f%%", km.getPhanTramGG()),
                    ttHienThi});
        pnlLeft.setPreferredSize(new Dimension(250, 200));
        pnlLeft.setMaximumSize(new Dimension(250, 200));

        JPanel pnlRight = createInfoBox("THỜI GIAN DIỄN RA", 250,
                new String[]{"Ngày bắt đầu", "Ngày kết thúc"},
                new String[]{km.getNgayBatDau().format(FMT), km.getNgayKetThuc().format(FMT)});
        pnlRight.setPreferredSize(new Dimension(250, 200));
        pnlRight.setMaximumSize(new Dimension(250, 200));

        pnlRow.add(pnlLeft);
        pnlRow.add(Box.createHorizontalStrut(15));
        pnlRow.add(pnlRight);
        pnlRow.add(Box.createHorizontalGlue());
        pnlMain.add(pnlRow, BorderLayout.CENTER);

        // Footer
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFooter.setOpaque(false);
        pnlFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnClose = new RoundedButton(100, 40, 15, "Đóng", Colors.SECONDARY);
        btnClose.setForeground(Colors.TEXT_PRIMARY);
        btnClose.addActionListener(e -> dialog.dispose());
        pnlFooter.add(btnClose);
        pnlMain.add(pnlFooter, BorderLayout.SOUTH);

        dialog.add(pnlMain);
        dialog.setVisible(true);
    }

    private JPanel createInfoBox(String title, int width, String[] labels, String[] values) {
        JPanel box = new RoundedPanel(width, 200, 15);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        box.setBackground(Colors.SECONDARY);

        JLabel lblBoxTitle = new JLabel(title);
        lblBoxTitle.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblBoxTitle.setForeground(new Color(100, 110, 120));
        lblBoxTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(lblBoxTitle);
        box.add(Box.createVerticalStrut(12));

        for (int i = 0; i < labels.length; i++) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
            row.setOpaque(false);
            row.setAlignmentX(Component.LEFT_ALIGNMENT);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));

            JLabel lblLabel = new JLabel(labels[i]);
            lblLabel.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
            lblLabel.setForeground(new Color(110, 120, 130));
            lblLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            row.add(lblLabel);
            row.add(Box.createVerticalStrut(2));

            JLabel lblValue = new JLabel(values[i] != null ? values[i] : "");
            lblValue.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            lblValue.setForeground(Colors.TEXT_PRIMARY);
            lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);
            row.add(lblValue);

            box.add(row);
            if (i < labels.length - 1) {
                box.add(Box.createVerticalStrut(8));
            }
        }
        return box;
    }

    private void moDialogThemKhuyenMai() {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Thêm khuyến mãi mới", true);
        dialog.setSize(600, 480);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setBackground(Colors.BACKGROUND);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        JPanel pnlHeader = new JPanel();
        pnlHeader.setLayout(new BoxLayout(pnlHeader, BoxLayout.Y_AXIS));
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        JLabel lblFormTitle = new JLabel("Thêm khuyến mãi mới");
        lblFormTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblFormTitle.setForeground(Colors.TEXT_PRIMARY);
        pnlHeader.add(lblFormTitle);
        JLabel lblFormSub = new JLabel("Điền đầy đủ thông tin rồi nhấn Thêm  (* bắt buộc)");
        lblFormSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblFormSub.setForeground(Colors.TEXT_SECONDARY);
        pnlHeader.add(lblFormSub);
        pnlMain.add(pnlHeader, BorderLayout.NORTH);

        RoundedTextField txtMaKM = new RoundedTextField(240, 35, 10, "");
        txtMaKM.setText(taoMaKhuyenMai());
        txtMaKM.setEnabled(false);
        RoundedTextField txtTenKM = new RoundedTextField(240, 35, 10, "Tên chương trình khuyến mãi");
        RoundedTextField txtPhanTram = new RoundedTextField(240, 35, 10, "0 - 100");
        RoundedTextField txtNgayBatDau = new RoundedTextField(240, 35, 10, "dd/MM/yyyy");
        txtNgayBatDau.setText(LocalDate.now().format(FMT));
        RoundedTextField txtNgayKetThuc = new RoundedTextField(240, 35, 10, "dd/MM/yyyy");
        txtNgayKetThuc.setText(LocalDate.now().plusMonths(1).format(FMT));
        JCheckBox chkTrangThai = new JCheckBox("Kích hoạt khuyến mãi");
        chkTrangThai.setSelected(true);
        chkTrangThai.setOpaque(false);
        chkTrangThai.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));

        JPanel pnlRow = new JPanel();
        pnlRow.setLayout(new BoxLayout(pnlRow, BoxLayout.X_AXIS));
        pnlRow.setOpaque(false);

        RoundedPanel pnlLeft = new RoundedPanel(250, 280, 15);
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlLeft.setBackground(Colors.SECONDARY);
        pnlLeft.setMaximumSize(new Dimension(260, 300));
        JLabel lblInfoTitle = new JLabel("THÔNG TIN KHUYẾN MÃI");
        lblInfoTitle.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblInfoTitle.setForeground(new Color(100, 110, 120));
        lblInfoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlLeft.add(lblInfoTitle);
        pnlLeft.add(Box.createVerticalStrut(12));
        pnlLeft.add(createFormField("Mã khuyến mãi (tự động)", txtMaKM));
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(createFormField("Tên chương trình *", txtTenKM));
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(createFormField("Phần trăm giảm giá (%) *", txtPhanTram));

        RoundedPanel pnlRight = new RoundedPanel(250, 280, 15);
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlRight.setBackground(Colors.SECONDARY);
        pnlRight.setMaximumSize(new Dimension(260, 300));
        JLabel lblTimeTitle = new JLabel("THỜI GIAN");
        lblTimeTitle.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblTimeTitle.setForeground(new Color(100, 110, 120));
        lblTimeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlRight.add(lblTimeTitle);
        pnlRight.add(Box.createVerticalStrut(12));
        pnlRight.add(createFormField("Ngày bắt đầu * (dd/MM/yyyy)", txtNgayBatDau));
        pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(createFormField("Ngày kết thúc * (dd/MM/yyyy)", txtNgayKetThuc));
        pnlRight.add(Box.createVerticalStrut(10));
        JPanel pnlTT = new JPanel();
        pnlTT.setLayout(new BoxLayout(pnlTT, BoxLayout.Y_AXIS));
        pnlTT.setOpaque(false);
        JLabel lblTTLabel = new JLabel("Trạng thái");
        lblTTLabel.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblTTLabel.setForeground(Colors.TEXT_SECONDARY);
        lblTTLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTT.add(lblTTLabel);
        pnlTT.add(Box.createVerticalStrut(5));
        pnlTT.add(chkTrangThai);
        pnlRight.add(pnlTT);

        pnlRow.add(Box.createHorizontalGlue());
        pnlRow.add(pnlLeft);
        pnlRow.add(Box.createHorizontalStrut(15));
        pnlRow.add(pnlRight);
        pnlRow.add(Box.createHorizontalGlue());
        pnlMain.add(pnlRow, BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlFooter.setOpaque(false);
        JButton btnHuy = new RoundedButton(100, 40, 15, "Hủy", Colors.SECONDARY);
        btnHuy.setForeground(Colors.TEXT_PRIMARY);
        JButton btnThem = new RoundedButton(170, 40, 15, "Thêm khuyến mãi", Colors.PRIMARY);
        btnHuy.addActionListener(ev -> dialog.dispose());
        btnThem.addActionListener(ev -> {
            try {
                double pct = Double.parseDouble(txtPhanTram.getText().trim());
                LocalDate ngayBD = LocalDate.parse(txtNgayBatDau.getText().trim(), FMT);
                LocalDate ngayKT = LocalDate.parse(txtNgayKetThuc.getText().trim(), FMT);
                KhuyenMai km = new KhuyenMai(
                        txtMaKM.getText().trim(),
                        txtTenKM.getText().trim(),
                        pct, ngayBD, ngayKT,
                        chkTrangThai.isSelected());
                if (khuyenMaiSV.themKhuyenMai(km)) {
                    list.add(km);
                    fullList.add(km);
                    tblKhuyenMai.refresh();
                    updateCategory();
                    JOptionPane.showMessageDialog(dialog, "Thêm khuyến mãi thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Thêm thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Phần trăm giảm giá phải là số hợp lệ (0-100).", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Ngày không đúng định dạng dd/MM/yyyy.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            }
        });
        pnlFooter.add(btnHuy);
        pnlFooter.add(btnThem);
        pnlMain.add(pnlFooter, BorderLayout.SOUTH);

        dialog.add(pnlMain, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void moDialogSuaKhuyenMai(KhuyenMai km) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Cập nhật khuyến mãi", true);
        dialog.setSize(600, 460);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBackground(Colors.BACKGROUND);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        JLabel lblFormTitle = new JLabel("Cập nhật thông tin khuyến mãi");
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

        RoundedTextField txtMaKM = new RoundedTextField(240, 35, 10, "");
        txtMaKM.setText(km.getMaKhuyenMai());
        txtMaKM.setEnabled(false);
        RoundedTextField txtTenKM = new RoundedTextField(240, 35, 10, "");
        txtTenKM.setText(km.getTenKhuyenMai());
        RoundedTextField txtPhanTram = new RoundedTextField(240, 35, 10, "");
        txtPhanTram.setText(String.format("%.0f", km.getPhanTramGG()));
        RoundedTextField txtNgayBatDau = new RoundedTextField(240, 35, 10, "dd/MM/yyyy");
        txtNgayBatDau.setText(km.getNgayBatDau().format(FMT));
        RoundedTextField txtNgayKetThuc = new RoundedTextField(240, 35, 10, "dd/MM/yyyy");
        txtNgayKetThuc.setText(km.getNgayKetThuc().format(FMT));
        JCheckBox chkTrangThai = new JCheckBox("Kích hoạt khuyến mãi");
        chkTrangThai.setSelected(km.isTrangThai());
        chkTrangThai.setOpaque(false);
        chkTrangThai.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));

        JPanel pnlRow = new JPanel();
        pnlRow.setLayout(new BoxLayout(pnlRow, BoxLayout.X_AXIS));
        pnlRow.setOpaque(false);
        pnlRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedPanel pnlLeft = new RoundedPanel(250, 280, 15);
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlLeft.setBackground(Colors.SECONDARY);
        pnlLeft.setMaximumSize(new Dimension(260, 300));
        JLabel lblInfoTitle = new JLabel("THÔNG TIN KHUYẾN MÃI");
        lblInfoTitle.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblInfoTitle.setForeground(new Color(100, 110, 120));
        lblInfoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlLeft.add(lblInfoTitle);
        pnlLeft.add(Box.createVerticalStrut(12));
        pnlLeft.add(createFormField("Mã khuyến mãi", txtMaKM));
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(createFormField("Tên chương trình *", txtTenKM));
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(createFormField("Phần trăm giảm giá (%) *", txtPhanTram));

        RoundedPanel pnlRight = new RoundedPanel(250, 280, 15);
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlRight.setBackground(Colors.SECONDARY);
        pnlRight.setMaximumSize(new Dimension(260, 300));
        JLabel lblTimeTitle = new JLabel("THỜI GIAN");
        lblTimeTitle.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblTimeTitle.setForeground(new Color(100, 110, 120));
        lblTimeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlRight.add(lblTimeTitle);
        pnlRight.add(Box.createVerticalStrut(12));
        pnlRight.add(createFormField("Ngày bắt đầu * (dd/MM/yyyy)", txtNgayBatDau));
        pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(createFormField("Ngày kết thúc * (dd/MM/yyyy)", txtNgayKetThuc));
        pnlRight.add(Box.createVerticalStrut(10));
        JPanel pnlTT = new JPanel();
        pnlTT.setLayout(new BoxLayout(pnlTT, BoxLayout.Y_AXIS));
        pnlTT.setOpaque(false);
        JLabel lblTTLabel = new JLabel("Trạng thái");
        lblTTLabel.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblTTLabel.setForeground(Colors.TEXT_SECONDARY);
        lblTTLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTT.add(lblTTLabel);
        pnlTT.add(Box.createVerticalStrut(5));
        pnlTT.add(chkTrangThai);
        pnlRight.add(pnlTT);

        pnlRow.add(pnlLeft);
        pnlRow.add(Box.createHorizontalStrut(15));
        pnlRow.add(pnlRight);
        pnlMain.add(pnlRow);
        pnlMain.add(Box.createVerticalStrut(20));

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFooter.setOpaque(false);
        pnlFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnHuy = new RoundedButton(100, 40, 15, "Hủy", Colors.SECONDARY);
        btnHuy.setForeground(Colors.TEXT_PRIMARY);
        JButton btnLuu = new RoundedButton(150, 40, 15, "Lưu thay đổi", Colors.PRIMARY);
        btnHuy.addActionListener(ev -> dialog.dispose());
        btnLuu.addActionListener(ev -> {
            try {
                km.setTenKhuyenMai(txtTenKM.getText().trim());
                km.setPhanTramGG(Double.parseDouble(txtPhanTram.getText().trim()));
                km.setNgayBatDau(LocalDate.parse(txtNgayBatDau.getText().trim(), FMT));
                km.setNgayKetThuc(LocalDate.parse(txtNgayKetThuc.getText().trim(), FMT));
                km.setTrangThai(chkTrangThai.isSelected());
                if (khuyenMaiSV.capNhatKhuyenMai(km)) {
                    tblKhuyenMai.refresh();
                    updateCategory();
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Phần trăm giảm giá phải là số hợp lệ (0-100).", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            } catch (DateTimeParseException ex) {
                JOptionPane.showMessageDialog(dialog, "Ngày không đúng định dạng dd/MM/yyyy.", "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Dữ liệu không hợp lệ", JOptionPane.WARNING_MESSAGE);
            }
        });
        pnlFooter.add(btnHuy);
        pnlFooter.add(btnLuu);
        pnlMain.add(pnlFooter);

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

    private String taoMaKhuyenMai() {
        int maxNum = 0;
        for (KhuyenMai km : fullList) {
            try {
                int num = Integer.parseInt(km.getMaKhuyenMai().replaceAll("[^0-9]", ""));
                if (num > maxNum) {
                    maxNum = num;
                }
            } catch (Exception ignored) {
            }
        }
        return String.format("KM%03d", maxNum + 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddKM) {
            moDialogThemKhuyenMai();
        } else if (e.getSource() == btnFind) {
            search();
        } else if (e.getSource() == btnAll) {
            txtSearch.setText("");
            list.clear();
            list.addAll(fullList);
            tblKhuyenMai.refresh();
        }
    }
}
