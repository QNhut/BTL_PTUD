package gui;

import constants.Colors;
import constants.FontStyle;
import entity.NhaCungCap;
import exception.RoundedButton;
import exception.RoundedPanel;
import exception.RoundedTextField;
import exception.StyledTable;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;
import service.NhaCungCap_Service;

public class NhaCungCap_GUI extends JPanel implements ActionListener {

    private JPanel pnlTitle, pnlContent, pnlCategory;
    private JLabel lblTitle, lblNote;
    private JPanel pnlHead, pnlButtonAdd;
    private RoundedButton btnAddNCC;
    private JPanel pnlSearch;
    private RoundedTextField txtSearch;
    private RoundedButton btnFind, btnAll;
    private JWindow suggestionWindow;
    private DefaultListModel<String> suggestionModel = new DefaultListModel<>();
    private JList<String> lstSuggestions;
    private java.util.List<NhaCungCap> suggestionData = new java.util.ArrayList<>();
    private boolean suppressSuggestion = false;
    private JLabel lblDSNCC;
    private ArrayList<NhaCungCap> list;
    private ArrayList<NhaCungCap> fullList = new ArrayList<>();
    private NhaCungCap_Service nhaCungCapSV;
    private StyledTable tblNhaCungCap;
    private JPanel item;

    private static final String[] COLUMN_NAMES = {"Nhà cung cấp", "Liên hệ", "Địa chỉ", "Trạng thái", "", ""};

    public NhaCungCap_GUI() {
        list = new ArrayList<>();
        nhaCungCapSV = new NhaCungCap_Service();

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
        pnlTitle.add(lblTitle = new JLabel("Nhà cung cấp"));
        lblTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        pnlTitle.add(lblNote = new JLabel("Quản lý danh sách nhà cung cấp và đối tác"));
        lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNote.setForeground(Colors.TEXT_SECONDARY);

        pnlButtonAdd.setBackground(Colors.BACKGROUND);
        pnlButtonAdd.add(btnAddNCC = new RoundedButton(210, 45, 20, "+ Thêm nhà cung cấp", Colors.PRIMARY));

        // ===== CONTENT =====
        add(pnlContent = new JPanel(), BorderLayout.CENTER);
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(Colors.BACKGROUND);

        // Stat cards
        pnlContent.add(pnlCategory = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)));
        pnlCategory.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        pnlCategory.setBackground(Colors.BACKGROUND);
        pnlCategory.add(createPanelCategory(nhaCungCapSV.getSoLuongNCC(), "Tổng NCC"));
        pnlCategory.add(createPanelCategory(nhaCungCapSV.getSoLuongDangHopTac(), "Đang hợp tác"));
        pnlCategory.add(createPanelCategory(nhaCungCapSV.getSoLuongNgungHopTac(), "Ngừng hợp tác"));

        // Search bar
        pnlContent.add(pnlSearch = new JPanel());
        pnlSearch.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        pnlSearch.setBackground(Colors.BACKGROUND);
        pnlSearch.add(lblDSNCC = new JLabel("Danh sách nhà cung cấp"));
        lblDSNCC.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblDSNCC.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
        pnlSearch.add(Box.createHorizontalGlue());
        pnlSearch.add(txtSearch = new RoundedTextField(500, 40, 20, "Tìm theo tên công ty, mã NCC, SĐT..."));
        pnlSearch.add(btnFind = new RoundedButton(130, 40, 20, "Tìm kiếm", Colors.PRIMARY));
        pnlSearch.add(btnAll = new RoundedButton(100, 40, 20, "Tất cả", Colors.SECONDARY));
        btnAll.setForeground(Colors.TEXT_PRIMARY);

        // ===== TABLE =====
        tblNhaCungCap = new StyledTable(COLUMN_NAMES, list);
        tblNhaCungCap.getTable().setRowHeight(60);

        // Col 0: Tên NCC + Mã
        tblNhaCungCap.setTwoLineColumn(0, 280,
                v -> ((NhaCungCap) v).getTenNhaCungCap(),
                v -> ((NhaCungCap) v).getMaNhaCungCap());

        // Col 1: SĐT + Email
        tblNhaCungCap.setTwoLineColumn(1, 220,
                v -> ((NhaCungCap) v).getSoDienThoai() != null ? ((NhaCungCap) v).getSoDienThoai() : "",
                v -> ((NhaCungCap) v).getEmail() != null ? ((NhaCungCap) v).getEmail() : "");

        // Col 2: Địa chỉ
        tblNhaCungCap.setSingleTextColumn(2, 180,
                v -> ((NhaCungCap) v).getDiaChi() != null ? ((NhaCungCap) v).getDiaChi() : "—");

        // Col 3: Trạng thái badge
        tblNhaCungCap.setBadgeColumn(3, 150,
                v -> ((NhaCungCap) v).isTrangThai(), "Đang hợp tác", "Ngừng hợp tác");

        tblNhaCungCap.setActionColumn(4, 80);
        tblNhaCungCap.setDeleteButtonColumn(5, 80);

        tblNhaCungCap.setActionColumnListener((row, obj) -> moDialogChiTietNCC((NhaCungCap) obj));
        tblNhaCungCap.setDeleteColumnListener((row, obj) -> {
            NhaCungCap ncc = (NhaCungCap) obj;
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Bạn có chắc muốn xóa nhà cung cấp \"" + ncc.getTenNhaCungCap() + "\" không?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                nhaCungCapSV.xoaNCC(ncc.getMaNhaCungCap());
                list.remove(row.intValue());
                fullList.remove(ncc);
                tblNhaCungCap.refresh();
                updateCategory();
            }
        });

        pnlContent.add(tblNhaCungCap);

        // ===== EVENTS =====
        btnAddNCC.addActionListener(this);
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
            loadData(nhaCungCapSV.getDSNhaCungCap());
        } catch (Exception e) {
            System.out.println("[NhaCungCap_GUI] Lỗi khi tải dữ liệu:");
            e.printStackTrace();
        }
    }

    public void loadData(ArrayList<NhaCungCap> dsNCC) {
        fullList = new ArrayList<>(dsNCC);
        list.clear();
        list.addAll(dsNCC);
        tblNhaCungCap.refresh();
    }

    private void search() {
        hideSuggestions();
        String keyword = txtSearch.getText().trim();
        ArrayList<NhaCungCap> filtered = nhaCungCapSV.timKiem(fullList, keyword);
        list.clear();
        list.addAll(filtered);
        tblNhaCungCap.refresh();
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
                    NhaCungCap ncc = suggestionData.get(idx);
                    suppressSuggestion = true;
                    txtSearch.setText(ncc.getTenNhaCungCap());
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
        ArrayList<NhaCungCap> matches = nhaCungCapSV.timKiem(fullList, kw);
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
            NhaCungCap ncc = matches.get(i);
            suggestionData.add(ncc);
            suggestionModel.addElement(ncc.getTenNhaCungCap() + "   (" + ncc.getMaNhaCungCap() + ")");
        }
        Point loc = txtSearch.getLocationOnScreen();
        int h = Math.min(max * 32 + 10, 260);
        suggestionWindow.setBounds(loc.x, loc.y + txtSearch.getHeight() + 2, txtSearch.getWidth(), h);
        suggestionWindow.setVisible(true);
    }

    private void updateCategory() {
        pnlCategory.removeAll();
        pnlCategory.add(createPanelCategory(nhaCungCapSV.getSoLuongNCC(), "Tổng NCC"));
        pnlCategory.add(createPanelCategory(nhaCungCapSV.getSoLuongDangHopTac(), "Đang hợp tác"));
        pnlCategory.add(createPanelCategory(nhaCungCapSV.getSoLuongNgungHopTac(), "Ngừng hợp tác"));
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
    private void moDialogChiTietNCC(NhaCungCap ncc) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Chi tiết nhà cung cấp", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(600, 460);
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
        JLabel lblIconChar = new JLabel(ncc.getTenNhaCungCap().substring(0, 1).toUpperCase());
        lblIconChar.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblIconChar.setForeground(Colors.PRIMARY);
        pnlIcon.add(lblIconChar);
        pnlHeader.add(pnlIcon);
        pnlHeader.add(Box.createHorizontalStrut(15));

        JPanel pnlHeaderInfo = new JPanel();
        pnlHeaderInfo.setLayout(new BoxLayout(pnlHeaderInfo, BoxLayout.Y_AXIS));
        pnlHeaderInfo.setOpaque(false);
        JLabel lblTenNCC = new JLabel(ncc.getTenNhaCungCap());
        lblTenNCC.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblTenNCC.setForeground(Colors.TEXT_PRIMARY);
        pnlHeaderInfo.add(lblTenNCC);
        JLabel lblMaNCC = new JLabel(ncc.getMaNhaCungCap());
        lblMaNCC.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblMaNCC.setForeground(Colors.TEXT_SECONDARY);
        pnlHeaderInfo.add(lblMaNCC);
        pnlHeaderInfo.add(Box.createVerticalStrut(6));
        Color ttColor = ncc.isTrangThai() ? Colors.SUCCESS : Colors.MUTED;
        String ttText = ncc.isTrangThai() ? "Đang hợp tác" : "Ngừng hợp tác";
        JLabel lblTT = new JLabel("● " + ttText);
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
            moDialogSuaNCC(ncc);
        });
        pnlHeader.add(btnEdit);

        pnlMain.add(pnlHeader, BorderLayout.NORTH);

        // Info boxes
        JPanel pnlRow = new JPanel();
        pnlRow.setLayout(new BoxLayout(pnlRow, BoxLayout.X_AXIS));
        pnlRow.setOpaque(false);
        pnlRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnlLeft = createInfoBox("THÔNG TIN LIÊN HỆ", 255,
                new String[]{"Mã NCC", "Số điện thoại", "Email"},
                new String[]{ncc.getMaNhaCungCap(), ncc.getSoDienThoai(), ncc.getEmail()});
        pnlLeft.setPreferredSize(new Dimension(255, 200));
        pnlLeft.setMaximumSize(new Dimension(255, 200));

        JPanel pnlRight = createInfoBox("THÔNG TIN KHÁC", 255,
                new String[]{"Địa chỉ", "Mô tả", "Trạng thái"},
                new String[]{
                    ncc.getDiaChi() != null ? ncc.getDiaChi() : "—",
                    ncc.getMoTa() != null ? ncc.getMoTa() : "—",
                    ttText
                });
        pnlRight.setPreferredSize(new Dimension(255, 200));
        pnlRight.setMaximumSize(new Dimension(255, 200));

        pnlRow.add(pnlLeft);
        pnlRow.add(Box.createHorizontalStrut(15));
        pnlRow.add(pnlRight);
        pnlRow.add(Box.createHorizontalGlue());
        pnlMain.add(pnlRow, BorderLayout.CENTER);

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

    private void moDialogThemNCC() {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Thêm nhà cung cấp mới", true);
        dialog.setSize(520, 540);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBackground(Colors.BACKGROUND);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(25, 30, 20, 30));

        JLabel lblFormTitle = new JLabel("Thêm nhà cung cấp mới");
        lblFormTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblFormTitle.setForeground(Colors.TEXT_PRIMARY);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMain.add(lblFormTitle);
        JLabel lblSub = new JLabel("Điền thông tin rồi nhấn Thêm  (* bắt buộc)");
        lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSub.setForeground(Colors.TEXT_SECONDARY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMain.add(lblSub);
        pnlMain.add(Box.createVerticalStrut(20));

        RoundedTextField txtMaNCC = new RoundedTextField(450, 38, 10, "");
        txtMaNCC.setText(taoMaNCC());
        txtMaNCC.setEnabled(false);
        RoundedTextField txtTenNCC = new RoundedTextField(450, 38, 10, "Tên công ty / nhà cung cấp...");
        RoundedTextField txtSDT = new RoundedTextField(450, 38, 10, "Vd: 0281234567");
        RoundedTextField txtEmail = new RoundedTextField(450, 38, 10, "Vd: contact@company.com");
        RoundedTextField txtDiaChi = new RoundedTextField(450, 38, 10, "Địa chỉ công ty...");
        RoundedTextField txtMoTa = new RoundedTextField(450, 38, 10, "Ghi chú...");
        JCheckBox chkTrangThai = new JCheckBox("Đang hợp tác");
        chkTrangThai.setSelected(true);
        chkTrangThai.setOpaque(false);
        chkTrangThai.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        chkTrangThai.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlMain.add(createFormField("Mã NCC (tự động)", txtMaNCC));
        pnlMain.add(Box.createVerticalStrut(10));
        pnlMain.add(createFormField("Tên nhà cung cấp *", txtTenNCC));
        JLabel errNCC = errLabel();
        pnlMain.add(errNCC);
        pnlMain.add(Box.createVerticalStrut(10));
        pnlMain.add(createFormField("Số điện thoại *", txtSDT));
        pnlMain.add(Box.createVerticalStrut(10));
        pnlMain.add(createFormField("Email *", txtEmail));
        pnlMain.add(Box.createVerticalStrut(10));
        pnlMain.add(createFormField("Địa chỉ", txtDiaChi));
        pnlMain.add(Box.createVerticalStrut(10));
        pnlMain.add(createFormField("Mô tả", txtMoTa));
        pnlMain.add(Box.createVerticalStrut(10));
        pnlMain.add(chkTrangThai);
        pnlMain.add(Box.createVerticalGlue());

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlFooter.setOpaque(false);
        pnlFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnHuy = new RoundedButton(100, 40, 15, "Hủy", Colors.SECONDARY);
        JButton btnThem = new RoundedButton(180, 40, 15, "+ Thêm nhà cung cấp", Colors.PRIMARY);
        btnHuy.setForeground(Colors.TEXT_PRIMARY);
        btnHuy.addActionListener(ev -> dialog.dispose());
        btnThem.addActionListener(ev -> {
            try {
                String ten = txtTenNCC.getText().trim();
                String sdt = txtSDT.getText().trim();
                String mail = txtEmail.getText().trim();
                String dc = txtDiaChi.getText().trim();
                String mo = txtMoTa.getText().trim();
                NhaCungCap ncc = new NhaCungCap(
                        txtMaNCC.getText().trim(), ten,
                        dc.isEmpty() ? null : dc,
                        mail, sdt,
                        mo.isEmpty() ? null : mo,
                        chkTrangThai.isSelected());
                if (nhaCungCapSV.themNCC(ncc)) {
                    list.add(ncc);
                    fullList.add(ncc);
                    tblNhaCungCap.refresh();
                    updateCategory();
                    JOptionPane.showMessageDialog(dialog, "Thêm nhà cung cấp thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Thêm thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                errNCC.setText("✗ " + ex.getMessage());
                errNCC.setVisible(true);
            }
        });
        pnlFooter.add(btnHuy);
        pnlFooter.add(btnThem);
        pnlMain.add(pnlFooter);
        dialog.add(pnlMain, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void moDialogSuaNCC(NhaCungCap ncc) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Cập nhật nhà cung cấp", true);
        dialog.setSize(520, 540);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBackground(Colors.BACKGROUND);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(25, 30, 20, 30));

        JLabel lblFormTitle = new JLabel("Cập nhật nhà cung cấp");
        lblFormTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblFormTitle.setForeground(Colors.TEXT_PRIMARY);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMain.add(lblFormTitle);
        JLabel lblSub = new JLabel("Chỉnh sửa rồi nhấn Lưu thay đổi  (* bắt buộc)");
        lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSub.setForeground(Colors.TEXT_SECONDARY);
        lblSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMain.add(lblSub);
        pnlMain.add(Box.createVerticalStrut(20));

        RoundedTextField txtMaNCC = new RoundedTextField(450, 38, 10, "");
        txtMaNCC.setText(ncc.getMaNhaCungCap());
        txtMaNCC.setEnabled(false);
        RoundedTextField txtTenNCC = new RoundedTextField(450, 38, 10, "");
        txtTenNCC.setText(ncc.getTenNhaCungCap());
        RoundedTextField txtSDT = new RoundedTextField(450, 38, 10, "");
        txtSDT.setText(ncc.getSoDienThoai() != null ? ncc.getSoDienThoai() : "");
        RoundedTextField txtEmail = new RoundedTextField(450, 38, 10, "");
        txtEmail.setText(ncc.getEmail() != null ? ncc.getEmail() : "");
        RoundedTextField txtDiaChi = new RoundedTextField(450, 38, 10, "");
        txtDiaChi.setText(ncc.getDiaChi() != null ? ncc.getDiaChi() : "");
        RoundedTextField txtMoTa = new RoundedTextField(450, 38, 10, "");
        txtMoTa.setText(ncc.getMoTa() != null ? ncc.getMoTa() : "");
        JCheckBox chkTrangThai = new JCheckBox("Đang hợp tác");
        chkTrangThai.setSelected(ncc.isTrangThai());
        chkTrangThai.setOpaque(false);
        chkTrangThai.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        chkTrangThai.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlMain.add(createFormField("Mã NCC", txtMaNCC));
        pnlMain.add(Box.createVerticalStrut(10));
        pnlMain.add(createFormField("Tên nhà cung cấp *", txtTenNCC));
        JLabel errNCC = errLabel();
        pnlMain.add(errNCC);
        pnlMain.add(Box.createVerticalStrut(10));
        pnlMain.add(createFormField("Số điện thoại *", txtSDT));
        pnlMain.add(Box.createVerticalStrut(10));
        pnlMain.add(createFormField("Email *", txtEmail));
        pnlMain.add(Box.createVerticalStrut(10));
        pnlMain.add(createFormField("Địa chỉ", txtDiaChi));
        pnlMain.add(Box.createVerticalStrut(10));
        pnlMain.add(createFormField("Mô tả", txtMoTa));
        pnlMain.add(Box.createVerticalStrut(10));
        pnlMain.add(chkTrangThai);
        pnlMain.add(Box.createVerticalGlue());

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        pnlFooter.setOpaque(false);
        pnlFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnHuy = new RoundedButton(100, 40, 15, "Hủy", Colors.SECONDARY);
        JButton btnLuu = new RoundedButton(160, 40, 15, "Lưu thay đổi", Colors.PRIMARY);
        btnHuy.setForeground(Colors.TEXT_PRIMARY);
        btnHuy.addActionListener(ev -> dialog.dispose());
        btnLuu.addActionListener(ev -> {
            try {
                String tenMoi = txtTenNCC.getText().trim();
                String sdtMoi = txtSDT.getText().trim();
                String mailMoi = txtEmail.getText().trim();
                String dcMoi = txtDiaChi.getText().trim();
                String moMoi = txtMoTa.getText().trim();
                boolean ttMoi = chkTrangThai.isSelected();
                // Validate bằng cách tạo temp object — entity tự throw nếu sai
                NhaCungCap temp = new NhaCungCap(
                        ncc.getMaNhaCungCap(), tenMoi,
                        dcMoi.isEmpty() ? null : dcMoi,
                        mailMoi, sdtMoi,
                        moMoi.isEmpty() ? null : moMoi,
                        ttMoi);
                if (nhaCungCapSV.capNhatNCC(temp)) {
                    ncc.setTenNhaCungCap(tenMoi);
                    ncc.setSoDienThoai(sdtMoi);
                    ncc.setEmail(mailMoi);
                    ncc.setDiaChi(dcMoi.isEmpty() ? null : dcMoi);
                    ncc.setMoTa(moMoi.isEmpty() ? null : moMoi);
                    ncc.setTrangThai(ttMoi);
                    tblNhaCungCap.refresh();
                    updateCategory();
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                errNCC.setText("✗ " + ex.getMessage());
                errNCC.setVisible(true);
            }
        });
        pnlFooter.add(btnHuy);
        pnlFooter.add(btnLuu);
        pnlMain.add(pnlFooter);
        dialog.add(pnlMain, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    // ===== HELPERS =====
    private JLabel errLabel() {
        JLabel l = new JLabel();
        l.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        l.setForeground(Colors.DANGER);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setVisible(false);
        return l;
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

    private String taoMaNCC() {
        int maxNum = 0;
        for (NhaCungCap ncc : fullList) {
            try {
                int n = Integer.parseInt(ncc.getMaNhaCungCap().replaceAll("[^0-9]", ""));
                if (n > maxNum) {
                    maxNum = n;
                }
            } catch (Exception ignored) {
            }
        }
        return String.format("NCC%03d", maxNum + 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddNCC) {
            moDialogThemNCC();
        } else if (e.getSource() == btnFind) {
            search();
        } else if (e.getSource() == btnAll) {
            txtSearch.setText("");
            list.clear();
            list.addAll(fullList);
            tblNhaCungCap.refresh();
        }
    }
}
