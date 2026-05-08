package gui;

import constants.Colors;
import constants.FontStyle;
import entity.KhuyenMai;
import entity.SanPham;
import exception.FormValidator;
import exception.RoundedButton;
import exception.RoundedComboBox;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import service.KhuyenMai_Service;
import service.SanPham_Service;
import service.Validators;

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
    private SanPham_Service sanPhamSV;
    private StyledTable tblKhuyenMai;
    private JPanel item;
    private java.util.Map<String, Integer> spCountMap = new java.util.HashMap<>();

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String[] COLUMN_NAMES = {"Chương trình", "Giảm giá", "Thời gian", "Trạng thái", "Sản phẩm", "", "", ""};

    public KhuyenMai_GUI() {
        list = new ArrayList<>();
        khuyenMaiSV = new KhuyenMai_Service();
        sanPhamSV = new SanPham_Service();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Colors.BACKGROUND);

        add(taoHeader(), BorderLayout.NORTH);
        add(taoContent(), BorderLayout.CENTER);
        wireEvents();
        loadDataSafe();
    }

    // ===== KHỐI: HEADER =====
    private JPanel taoHeader() {
        pnlHead = new JPanel();
        pnlHead.setLayout(new BoxLayout(pnlHead, BoxLayout.X_AXIS));
        pnlHead.setBackground(Colors.BACKGROUND);

        pnlTitle = new JPanel();
        pnlTitle.setLayout(new BoxLayout(pnlTitle, BoxLayout.Y_AXIS));
        pnlTitle.setPreferredSize(new Dimension(900, 0));
        pnlTitle.setBackground(Colors.BACKGROUND);
        pnlTitle.add(lblTitle = new JLabel("Khuyến mãi"));
        lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.FOREGROUND);
        pnlTitle.add(lblNote = new JLabel("Quản lý chương trình khuyến mãi"));
        lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNote.setForeground(Colors.MUTED);

        pnlButtonAdd = new JPanel();
        pnlButtonAdd.setBackground(Colors.BACKGROUND);
        pnlButtonAdd.add(btnAddKM = new RoundedButton(170, 40, 10, "+ Thêm khuyến mãi", Colors.PRIMARY));

        pnlHead.add(pnlTitle);
        pnlHead.add(pnlButtonAdd);
        return pnlHead;
    }

    // ===== KHỐI: NỘI DUNG =====
    private JPanel taoContent() {
        pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(Colors.BACKGROUND);
        pnlContent.add(taoStatCards());
        pnlContent.add(taoSearchBar());
        pnlContent.add(setupTable());
        return pnlContent;
    }

    // ===== KHỐI: THỐNG KÊ =====
    private JPanel taoStatCards() {
        pnlCategory = new JPanel(new GridLayout(1, 4, 16, 0));
        pnlCategory.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        pnlCategory.setBackground(Colors.BACKGROUND);
        pnlCategory.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        pnlCategory.setPreferredSize(new Dimension(0, 110));
        try {
            pnlCategory.add(statCard("Tổng khuyến mãi", khuyenMaiSV.getSoLuongKhuyenMai(), Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
            pnlCategory.add(statCard("Đang hoạt động", khuyenMaiSV.getSoLuongDangHoatDong(), Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
            pnlCategory.add(statCard("Sắp diễn ra", khuyenMaiSV.getSoLuongSapDienRa(), Colors.BROWN_HOVER, Colors.ACCENT, Colors.ACCENT));
            pnlCategory.add(statCard("Đã kết thúc", khuyenMaiSV.getSoLuongDaKetThuc(), Colors.SECONDARY, Colors.DANGER, Colors.DANGER));
        } catch (Exception e) {
            System.err.println("[KhuyenMai_GUI] Không tải được thống kê: " + e.getMessage());
            pnlCategory.add(statCard("Tổng khuyến mãi", 0, Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
            pnlCategory.add(statCard("Đang hoạt động", 0, Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
            pnlCategory.add(statCard("Sắp diễn ra", 0, Colors.BROWN_HOVER, Colors.ACCENT, Colors.ACCENT));
            pnlCategory.add(statCard("Đã kết thúc", 0, Colors.SECONDARY, Colors.DANGER, Colors.DANGER));
        }
        return pnlCategory;
    }

    // ===== KHỐI: THANH TÌM KIẾM =====
    private JPanel taoSearchBar() {
        pnlSearch = new JPanel();
        pnlSearch.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        pnlSearch.setBackground(Colors.BACKGROUND);
        pnlSearch.add(lblDSKM = new JLabel("Danh sách khuyến mãi"));
        lblDSKM.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblDSKM.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        pnlSearch.add(Box.createHorizontalGlue());
        pnlSearch.add(txtSearch = new RoundedTextField(500, 40, 20, "Tìm kiếm khuyến mãi..."));
        pnlSearch.add(btnFind = new RoundedButton(130, 40, 20, "Tìm kiếm", Colors.PRIMARY));
        pnlSearch.add(btnAll = new RoundedButton(100, 40, 20, "Tất cả", Colors.SECONDARY));
        pnlSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        btnAll.setForeground(Colors.TEXT_PRIMARY);
        return pnlSearch;
    }

    // ===== KHỐI: BẢNG =====
    private StyledTable setupTable() {
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

        // Cột Sản phẩm: hiển thị số lượng SP đang áp dụng
        tblKhuyenMai.setColumnRenderer(4, (table, value, isSelected, hasFocus, row, col) -> {
            KhuyenMai km = (KhuyenMai) value;
            Integer cnt = spCountMap.get(km.getMaKhuyenMai());
            int count = cnt != null ? cnt : 0;
            JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            pnl.setOpaque(true);
            pnl.setBackground(isSelected ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            if (count > 0) {
                JLabel lbl = new JLabel(count + " sản phẩm");
                lbl.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
                lbl.setForeground(Colors.SUCCESS_DARK);
                lbl.setOpaque(true);
                lbl.setBackground(Colors.SUCCESS_LIGHT);
                lbl.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
                pnl.add(lbl);
            } else {
                JLabel lbl = new JLabel("Chưa áp dụng");
                lbl.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
                lbl.setForeground(Colors.MUTED);
                pnl.add(lbl);
            }
            return pnl;
        });
        tblKhuyenMai.setColumnWidth(4, 140);

        // Cột Áp dụng (col 5)
        tblKhuyenMai.setColumnRenderer(5, new TableCellRenderer() {
            private final JPanel pnl = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            private final RoundedButton btn = new RoundedButton(80, 32, 8, "Áp dụng", Colors.PRIMARY);
            {
                btn.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
                btn.setForeground(Color.WHITE);
                pnl.add(btn);
                pnl.setOpaque(true);
                pnl.setBorder(BorderFactory.createEmptyBorder(21, 5, 21, 5));
            }
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                pnl.setBackground(isSelected ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
                return pnl;
            }
        });
        tblKhuyenMai.setColumnWidth(5, 120);

        tblKhuyenMai.setActionColumn(6, 80);
        tblKhuyenMai.setDeleteButtonColumn(7, 80);

        tblKhuyenMai.setActionColumnListener((row, obj) -> moDialogChiTietKhuyenMai((KhuyenMai) obj));

        // Click "Áp dụng" ở col 5
        tblKhuyenMai.getTable().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int row = tblKhuyenMai.getTable().rowAtPoint(e.getPoint());
                int col = tblKhuyenMai.getTable().columnAtPoint(e.getPoint());
                if (row >= 0 && col == 5) {
                    moDialogApDungKhuyenMai(list.get(row));
                }
            }
        });

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

        return tblKhuyenMai;
    }

    // ===== KHỐI: SỰ KIỆN =====
    private void wireEvents() {
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
    }

    // ===== KHỐI: TẢI DỮ LIỆU =====
    public void refresh() {
        loadDataSafe();
    }

    private void loadDataSafe() {
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
        refreshSpCountMap();
        tblKhuyenMai.refresh();
    }

    private void refreshSpCountMap() {
        spCountMap = khuyenMaiSV.getDemSanPhamTheoKM();
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
        pnlCategory.add(statCard("Tổng khuyến mãi", khuyenMaiSV.getSoLuongKhuyenMai(), Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
        pnlCategory.add(statCard("Đang hoạt động", khuyenMaiSV.getSoLuongDangHoatDong(), Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
        pnlCategory.add(statCard("Sắp diễn ra", khuyenMaiSV.getSoLuongSapDienRa(), Colors.BROWN_HOVER, Colors.ACCENT, Colors.ACCENT));
        pnlCategory.add(statCard("Đã kết thúc", khuyenMaiSV.getSoLuongDaKetThuc(), Colors.SECONDARY, Colors.DANGER, Colors.DANGER));
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

    // ===== DIALOGS =====
    private void moDialogApDungKhuyenMai(KhuyenMai km) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Áp dụng khuyến mãi", true);
        dialog.setSize(780, 600);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(Colors.BACKGROUND);

        // Load tất cả sản phẩm
        final java.util.List<SanPham> allProducts = sanPhamSV.layDanhSachSanPham();
        final java.util.List<SanPham> filtered = new java.util.ArrayList<>(allProducts);
        final java.util.Set<String> checkedSet = new java.util.HashSet<>();
        for (SanPham sp : allProducts) {
            if (sp.getKhuyenMai() != null && km.getMaKhuyenMai().equals(sp.getKhuyenMai().getMaKhuyenMai())) {
                checkedSet.add(sp.getMaSanPham());
            }
        }
        // Lưu lại trạng thái ban đầu để tính diff khi áp dụng
        final java.util.Set<String> initialSet = new java.util.HashSet<>(checkedSet);

        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setBackground(Colors.BACKGROUND);

        // ===== HEADER =====
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Colors.BACKGROUND);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(14, 20, 14, 20)));
        JPanel hdInfo = new JPanel();
        hdInfo.setLayout(new BoxLayout(hdInfo, BoxLayout.Y_AXIS));
        hdInfo.setOpaque(false);
        JLabel lblHdTitle = new JLabel("Áp dụng khuyến mãi");
        lblHdTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblHdTitle.setForeground(Colors.TEXT_PRIMARY);
        hdInfo.add(lblHdTitle);
        JLabel lblHdSub = new JLabel(km.getTenKhuyenMai() + "  ·  "
                + String.format("%.0f%%", km.getPhanTramGG()) + "  ·  " + km.getMaKhuyenMai());
        lblHdSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblHdSub.setForeground(Colors.PRIMARY);
        hdInfo.add(lblHdSub);
        pnlHeader.add(hdInfo, BorderLayout.CENTER);
        JButton btnX = new JButton("×");
        btnX.setFont(new Font("SansSerif", Font.PLAIN, 22));
        btnX.setForeground(Colors.MUTED);
        btnX.setFocusPainted(false);
        btnX.setBorderPainted(false);
        btnX.setContentAreaFilled(false);
        btnX.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnX.addActionListener(e -> dialog.dispose());
        pnlHeader.add(btnX, BorderLayout.EAST);
        pnlMain.add(pnlHeader, BorderLayout.NORTH);

        // ===== TABLE MODEL =====
        final String[] colNames = {"", "Mã SP", "Tên sản phẩm", "Loại", "Đơn vị", "Giá bán"};
        AbstractTableModel tblModel = new AbstractTableModel() {
            @Override public int getRowCount() { return filtered.size(); }
            @Override public int getColumnCount() { return colNames.length; }
            @Override public String getColumnName(int col) { return colNames[col]; }
            @Override public Class<?> getColumnClass(int col) { return col == 0 ? Boolean.class : String.class; }
            @Override public boolean isCellEditable(int row, int col) { return col == 0; }
            @Override
            public Object getValueAt(int row, int col) {
                SanPham sp = filtered.get(row);
                switch (col) {
                    case 0: return checkedSet.contains(sp.getMaSanPham());
                    case 1: return sp.getMaSanPham();
                    case 2: return sp.getTenSanPham();
                    case 3: return sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoaiSanPham() : "";
                    case 4: return sp.getDonViTinh() != null ? sp.getDonViTinh() : "";
                    case 5: return String.format("%,.0f đ", sp.getGiaThanh());
                    default: return "";
                }
            }
            @Override
            public void setValueAt(Object val, int row, int col) {
                if (col == 0 && row < filtered.size()) {
                    String maSP = filtered.get(row).getMaSanPham();
                    if (Boolean.TRUE.equals(val)) checkedSet.add(maSP);
                    else checkedSet.remove(maSP);
                    fireTableCellUpdated(row, 0);
                }
            }
        };

        JTable tblSP = new JTable(tblModel);
        tblSP.setRowHeight(52);
        tblSP.setShowGrid(false);
        tblSP.setIntercellSpacing(new Dimension(0, 0));
        tblSP.setBackground(Colors.BACKGROUND);
        tblSP.setSelectionBackground(Colors.PRIMARY_LIGHT);
        tblSP.setFillsViewportHeight(true);
        tblSP.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));

        tblSP.getColumnModel().getColumn(0).setMaxWidth(44);
        tblSP.getColumnModel().getColumn(0).setMinWidth(44);
        tblSP.getColumnModel().getColumn(1).setPreferredWidth(80);
        tblSP.getColumnModel().getColumn(2).setPreferredWidth(200);
        tblSP.getColumnModel().getColumn(3).setPreferredWidth(140);
        tblSP.getColumnModel().getColumn(4).setPreferredWidth(70);
        tblSP.getColumnModel().getColumn(5).setPreferredWidth(90);

        // Header style
        JTableHeader tblHdr = tblSP.getTableHeader();
        tblHdr.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        tblHdr.setBackground(Colors.SECONDARY);
        tblHdr.setForeground(Colors.TEXT_SECONDARY);
        tblHdr.setReorderingAllowed(false);
        tblHdr.setPreferredSize(new Dimension(0, 44));
        tblHdr.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int r, int c) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, focus, r, c);
                lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
                lbl.setForeground(Colors.TEXT_SECONDARY);
                lbl.setBackground(Colors.SECONDARY);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
                        BorderFactory.createEmptyBorder(0, 10, 0, 0)));
                return lbl;
            }
        });

        // Checkbox renderer
        tblSP.getColumnModel().getColumn(0).setCellRenderer((t, val, sel, focus, row, col) -> {
            JCheckBox chk = new JCheckBox();
            chk.setSelected(Boolean.TRUE.equals(val));
            chk.setOpaque(true);
            chk.setHorizontalAlignment(SwingConstants.CENTER);
            boolean checked = row < filtered.size() && checkedSet.contains(filtered.get(row).getMaSanPham());
            chk.setBackground(checked ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            return chk;
        });

        // Default row renderer (highlight checked rows)
        DefaultTableCellRenderer rowRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                boolean checked = row < filtered.size() && checkedSet.contains(filtered.get(row).getMaSanPham());
                lbl.setBackground(checked ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                lbl.setForeground(Colors.TEXT_PRIMARY);
                return lbl;
            }
        };
        for (int c = 1; c < colNames.length; c++) tblSP.getColumnModel().getColumn(c).setCellRenderer(rowRenderer);

        // Badge renderer cho cột Loại (col 3)
        tblSP.getColumnModel().getColumn(3).setCellRenderer((t, val, sel, focus, row, col) -> {
            boolean checked = row < filtered.size() && checkedSet.contains(filtered.get(row).getMaSanPham());
            JPanel pnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
            pnl.setBackground(checked ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            pnl.setOpaque(true);
            String loaiText = val != null ? val.toString() : "";
            if (!loaiText.isEmpty()) {
                JLabel badge = new JLabel(loaiText);
                badge.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
                badge.setForeground(Colors.PRIMARY);
                badge.setOpaque(true);
                badge.setBackground(Colors.PRIMARY_LIGHT);
                badge.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
                pnl.add(badge);
            }
            return pnl;
        });

        // Renderer Giá bán (col 5) - căn phải
        tblSP.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean focus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(t, v, sel, focus, row, col);
                boolean checked = row < filtered.size() && checkedSet.contains(filtered.get(row).getMaSanPham());
                lbl.setBackground(checked ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
                lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 12));
                lbl.setForeground(Colors.TEXT_PRIMARY);
                lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
                lbl.setHorizontalAlignment(SwingConstants.RIGHT);
                return lbl;
            }
        });

        JScrollPane scrollSP = new JScrollPane(tblSP);
        scrollSP.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        scrollSP.getViewport().setBackground(Colors.BACKGROUND);

        // ===== FOOTER LABELS =====
        JLabel lblSelectedCount = new JLabel("Đã chọn " + checkedSet.size() + " sản phẩm");
        lblSelectedCount.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSelectedCount.setForeground(Colors.TEXT_SECONDARY);

        RoundedButton btnApDungNow = new RoundedButton(170, 38, 10,
                "Cập nhật (" + checkedSet.size() + ")", Colors.PRIMARY);
        btnApDungNow.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnApDungNow.setForeground(Color.WHITE);

        // Cập nhật count khi checkbox thay đổi
        tblModel.addTableModelListener(e -> {
            int cnt = checkedSet.size();
            lblSelectedCount.setText("Đã chọn " + cnt + " sản phẩm");
            btnApDungNow.setText("Cập nhật (" + cnt + ")");
        });

        // ===== CATEGORY COMBO =====
        java.util.Map<String, String> loaiMap = new java.util.LinkedHashMap<>();
        loaiMap.put("ALL", "Tất cả loại");
        for (SanPham sp : allProducts) {
            if (sp.getLoaiSanPham() != null && sp.getLoaiSanPham().getMaLoaiSanPham() != null) {
                loaiMap.putIfAbsent(sp.getLoaiSanPham().getMaLoaiSanPham(), sp.getLoaiSanPham().getTenLoaiSanPham());
            }
        }
        java.util.List<String> loaiKeys = new java.util.ArrayList<>(loaiMap.keySet());

        final String[] activeLoai = {"ALL"};
        final String[] activeKw = {""};

        RoundedComboBox<String> cboLoai = new RoundedComboBox<>(10);
        cboLoai.setPreferredSize(new Dimension(180, 38));
        cboLoai.setMaximumSize(new Dimension(180, 38));
        cboLoai.setMinimumSize(new Dimension(180, 38));
        for (String tenLoai : loaiMap.values()) cboLoai.addItem(tenLoai);

        Runnable filterTable = () -> {
            filtered.clear();
            String kw = activeKw[0];
            for (SanPham sp : allProducts) {
                boolean matchKw = kw.isEmpty()
                        || sp.getMaSanPham().toLowerCase().contains(kw)
                        || sp.getTenSanPham().toLowerCase().contains(kw);
                boolean matchLoai = "ALL".equals(activeLoai[0])
                        || (sp.getLoaiSanPham() != null && activeLoai[0].equals(sp.getLoaiSanPham().getMaLoaiSanPham()));
                if (matchKw && matchLoai) filtered.add(sp);
            }
            tblModel.fireTableDataChanged();
        };

        cboLoai.addActionListener(ev -> {
            int idx = cboLoai.getSelectedIndex();
            if (idx >= 0 && idx < loaiKeys.size()) {
                activeLoai[0] = loaiKeys.get(idx);
                filterTable.run();
            }
        });

        // ===== CENTER CONTENT =====
        JPanel pnlCenter = new JPanel(new BorderLayout(0, 6));
        pnlCenter.setBackground(Colors.BACKGROUND);
        pnlCenter.setBorder(BorderFactory.createEmptyBorder(12, 20, 0, 20));

        // Search row: [txtSearch] [cboLoai]
        JPanel searchRow = new JPanel();
        searchRow.setLayout(new BoxLayout(searchRow, BoxLayout.X_AXIS));
        searchRow.setOpaque(false);
        RoundedTextField txtSearchSP = new RoundedTextField(500, 38, 20, "Tìm theo tên hoặc mã sản phẩm...");
        txtSearchSP.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        searchRow.add(txtSearchSP);
        searchRow.add(Box.createHorizontalStrut(8));
        searchRow.add(cboLoai);

        Runnable doSearch = () -> {
            activeKw[0] = txtSearchSP.getText().trim().toLowerCase();
            filterTable.run();
        };
        txtSearchSP.addActionListener(ev -> doSearch.run());

        // Nút Chọn tất cả / Bỏ chọn tất cả
        RoundedButton btnChonTatCa = new RoundedButton(140, 32, 8, "Chọn tất cả", Colors.SECONDARY);
        btnChonTatCa.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        btnChonTatCa.setForeground(Colors.TEXT_PRIMARY);
        btnChonTatCa.addActionListener(ev -> {
            boolean allChecked = !filtered.isEmpty()
                    && filtered.stream().allMatch(sp -> checkedSet.contains(sp.getMaSanPham()));
            if (allChecked) {
                filtered.forEach(sp -> checkedSet.remove(sp.getMaSanPham()));
            } else {
                filtered.forEach(sp -> checkedSet.add(sp.getMaSanPham()));
            }
            tblModel.fireTableDataChanged();
        });

        // Cập nhật text nút khi dữ liệu bảng thay đổi
        tblModel.addTableModelListener(ev -> {
            boolean allChecked = !filtered.isEmpty()
                    && filtered.stream().allMatch(sp -> checkedSet.contains(sp.getMaSanPham()));
            btnChonTatCa.setText(allChecked ? "Bỏ chọn tất cả" : "Chọn tất cả");
        });

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 2));
        actionRow.setOpaque(false);
        actionRow.add(btnChonTatCa);

        JPanel topSection = new JPanel();
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.setOpaque(false);
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        actionRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        topSection.add(searchRow);
        topSection.add(actionRow);

        pnlCenter.add(topSection, BorderLayout.NORTH);
        pnlCenter.add(scrollSP, BorderLayout.CENTER);
        pnlMain.add(pnlCenter, BorderLayout.CENTER);

        // ===== FOOTER =====
        JPanel pnlFooter = new JPanel(new BorderLayout());
        pnlFooter.setBackground(Colors.BACKGROUND);
        pnlFooter.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        pnlFooter.add(lblSelectedCount, BorderLayout.WEST);

        JPanel pnlFooterBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlFooterBtns.setOpaque(false);
        RoundedButton btnDungAll = new RoundedButton(150, 38, 10, "Dừng khuyến mãi", Colors.SECONDARY);
        btnDungAll.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnDungAll.setForeground(Colors.DANGER);
        RoundedButton btnHuyAD = new RoundedButton(80, 38, 10, "Hủy", Colors.SECONDARY);
        btnHuyAD.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnHuyAD.setForeground(Colors.TEXT_PRIMARY);
        btnHuyAD.addActionListener(e -> dialog.dispose());
        pnlFooterBtns.add(btnDungAll);
        pnlFooterBtns.add(btnHuyAD);
        pnlFooterBtns.add(btnApDungNow);
        pnlFooter.add(pnlFooterBtns, BorderLayout.EAST);
        pnlMain.add(pnlFooter, BorderLayout.SOUTH);

        // ===== DỪNG TẤT CẢ =====
        btnDungAll.addActionListener(e -> {
            if (initialSet.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Khuyến mãi này hiện chưa áp dụng cho sản phẩm nào.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int kq = JOptionPane.showConfirmDialog(dialog,
                    "Dừng khuyến mãi \"" + km.getTenKhuyenMai() + "\" cho TẤT CẢ "
                    + initialSet.size() + " sản phẩm đang áp dụng?",
                    "Xác nhận dừng khuyến mãi",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (kq != JOptionPane.YES_OPTION) return;
            int n = khuyenMaiSV.goKhuyenMaiKhoiTatCa(km.getMaKhuyenMai());
            refreshSpCountMap();
            tblKhuyenMai.refresh();
            JOptionPane.showMessageDialog(dialog,
                    "Đã dừng khuyến mãi cho " + n + " sản phẩm!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        // ===== APPLY LOGIC (diff: thêm + gỡ) =====
        btnApDungNow.addActionListener(e -> {
            // Tính diff giữa initialSet và checkedSet
            java.util.Set<String> toAdd = new java.util.HashSet<>(checkedSet);
            toAdd.removeAll(initialSet);
            java.util.Set<String> toRemove = new java.util.HashSet<>(initialSet);
            toRemove.removeAll(checkedSet);

            if (toAdd.isEmpty() && toRemove.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Không có thay đổi nào để cập nhật.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int added = 0, removed = 0;
            for (String maSP : toAdd) {
                if (khuyenMaiSV.apDungChoSanPham(maSP, km.getMaKhuyenMai())) added++;
            }
            for (String maSP : toRemove) {
                if (khuyenMaiSV.goKhuyenMaiKhoiSanPham(maSP)) removed++;
            }
            refreshSpCountMap();
            tblKhuyenMai.refresh();
            JOptionPane.showMessageDialog(dialog,
                    "Đã thêm khuyến mãi cho " + added + " sản phẩm.\n"
                    + "Đã gỡ khuyến mãi khỏi " + removed + " sản phẩm.",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        dialog.add(pnlMain);
        dialog.setVisible(true);
    }

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

        // Error labels cho từng ô cần validate
        JLabel errTen = FormValidator.errorLabel();
        JLabel errPct = FormValidator.errorLabel();
        JLabel errNgayBD = FormValidator.errorLabel();
        JLabel errNgayKT = FormValidator.errorLabel();

        JPanel pnlRow = new JPanel();
        pnlRow.setLayout(new BoxLayout(pnlRow, BoxLayout.X_AXIS));
        pnlRow.setOpaque(false);

        RoundedPanel pnlLeft = new RoundedPanel(250, 280, 15);
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlLeft.setBackground(Colors.SECONDARY);
        pnlLeft.setMaximumSize(new Dimension(260, 320));
        JLabel lblInfoTitle = new JLabel("THÔNG TIN KHUYẾN MÃI");
        lblInfoTitle.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblInfoTitle.setForeground(new Color(100, 110, 120));
        lblInfoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlLeft.add(lblInfoTitle);
        pnlLeft.add(Box.createVerticalStrut(12));
        pnlLeft.add(createFormField("Mã khuyến mãi (tự động)", txtMaKM));
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(FormValidator.fieldWithError("Tên chương trình *", txtTenKM, errTen));
        pnlLeft.add(Box.createVerticalStrut(8));
        pnlLeft.add(FormValidator.fieldWithError("Phần trăm giảm giá (%) *", txtPhanTram, errPct));

        RoundedPanel pnlRight = new RoundedPanel(250, 280, 15);
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlRight.setBackground(Colors.SECONDARY);
        pnlRight.setMaximumSize(new Dimension(260, 320));
        JLabel lblTimeTitle = new JLabel("THỜI GIAN");
        lblTimeTitle.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblTimeTitle.setForeground(new Color(100, 110, 120));
        lblTimeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlRight.add(lblTimeTitle);
        pnlRight.add(Box.createVerticalStrut(12));
        pnlRight.add(FormValidator.fieldWithError("Ngày bắt đầu * (dd/MM/yyyy)", txtNgayBatDau, errNgayBD));
        pnlRight.add(Box.createVerticalStrut(8));
        pnlRight.add(FormValidator.fieldWithError("Ngày kết thúc * (dd/MM/yyyy)", txtNgayKetThuc, errNgayKT));
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

        // Validator
        FormValidator fv = new FormValidator()
                .add(txtTenKM, errTen, Validators::tenNguoi)
                .add(txtPhanTram, errPct, Validators::phanTram)
                .add(txtNgayBatDau, errNgayBD, Validators::ngay)
                .add(txtNgayKetThuc, errNgayKT, Validators::ngay);

        JPanel pnlFooter = new JPanel();
        pnlFooter.setLayout(new BoxLayout(pnlFooter, BoxLayout.Y_AXIS));
        pnlFooter.setOpaque(false);
        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        pnlBtns.setOpaque(false);
        JButton btnHuy = new RoundedButton(100, 40, 15, "Hủy", Colors.SECONDARY);
        btnHuy.setForeground(Colors.TEXT_PRIMARY);
        JButton btnThem = new RoundedButton(170, 40, 15, "Thêm khuyến mãi", Colors.PRIMARY);
        btnHuy.addActionListener(ev -> dialog.dispose());
        btnThem.addActionListener(ev -> {
            if (!fv.validateAll()) return;
            // Kiểm tra ngày kết thúc > ngày bắt đầu
            LocalDate ngayBD = LocalDate.parse(txtNgayBatDau.getText().trim(), FMT);
            LocalDate ngayKT = LocalDate.parse(txtNgayKetThuc.getText().trim(), FMT);
            if (!ngayKT.isAfter(ngayBD)) {
                errNgayKT.setText("Ngày kết thúc phải sau ngày bắt đầu");
                errNgayKT.setVisible(true);
                txtNgayKetThuc.setInvalid(true);
                txtNgayKetThuc.requestFocusInWindow();
                return;
            }
            try {
                double pct = Double.parseDouble(txtPhanTram.getText().trim());
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
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        pnlBtns.add(btnHuy);
        pnlBtns.add(btnThem);
        pnlFooter.add(pnlBtns);
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

        // Error labels
        JLabel errTen = FormValidator.errorLabel();
        JLabel errPct = FormValidator.errorLabel();
        JLabel errNgayBD = FormValidator.errorLabel();
        JLabel errNgayKT = FormValidator.errorLabel();

        JPanel pnlRow = new JPanel();
        pnlRow.setLayout(new BoxLayout(pnlRow, BoxLayout.X_AXIS));
        pnlRow.setOpaque(false);
        pnlRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedPanel pnlLeft = new RoundedPanel(250, 280, 15);
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlLeft.setBackground(Colors.SECONDARY);
        pnlLeft.setMaximumSize(new Dimension(260, 320));
        JLabel lblInfoTitle = new JLabel("THÔNG TIN KHUYẾN MÃI");
        lblInfoTitle.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblInfoTitle.setForeground(new Color(100, 110, 120));
        lblInfoTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlLeft.add(lblInfoTitle);
        pnlLeft.add(Box.createVerticalStrut(12));
        pnlLeft.add(createFormField("Mã khuyến mãi", txtMaKM));
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(FormValidator.fieldWithError("Tên chương trình *", txtTenKM, errTen));
        pnlLeft.add(Box.createVerticalStrut(8));
        pnlLeft.add(FormValidator.fieldWithError("Phần trăm giảm giá (%) *", txtPhanTram, errPct));

        RoundedPanel pnlRight = new RoundedPanel(250, 280, 15);
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlRight.setBackground(Colors.SECONDARY);
        pnlRight.setMaximumSize(new Dimension(260, 320));
        JLabel lblTimeTitle = new JLabel("THỜI GIAN");
        lblTimeTitle.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblTimeTitle.setForeground(new Color(100, 110, 120));
        lblTimeTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlRight.add(lblTimeTitle);
        pnlRight.add(Box.createVerticalStrut(12));
        pnlRight.add(FormValidator.fieldWithError("Ngày bắt đầu * (dd/MM/yyyy)", txtNgayBatDau, errNgayBD));
        pnlRight.add(Box.createVerticalStrut(8));
        pnlRight.add(FormValidator.fieldWithError("Ngày kết thúc * (dd/MM/yyyy)", txtNgayKetThuc, errNgayKT));
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

        FormValidator fv = new FormValidator()
                .add(txtTenKM, errTen, Validators::tenNguoi)
                .add(txtPhanTram, errPct, Validators::phanTram)
                .add(txtNgayBatDau, errNgayBD, Validators::ngay)
                .add(txtNgayKetThuc, errNgayKT, Validators::ngay);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFooter.setOpaque(false);
        pnlFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        JButton btnHuy = new RoundedButton(100, 40, 15, "Hủy", Colors.SECONDARY);
        btnHuy.setForeground(Colors.TEXT_PRIMARY);
        JButton btnLuu = new RoundedButton(150, 40, 15, "Lưu thay đổi", Colors.PRIMARY);
        btnHuy.addActionListener(ev -> dialog.dispose());
        btnLuu.addActionListener(ev -> {
            if (!fv.validateAll()) return;
            LocalDate ngayBD = LocalDate.parse(txtNgayBatDau.getText().trim(), FMT);
            LocalDate ngayKT = LocalDate.parse(txtNgayKetThuc.getText().trim(), FMT);
            if (!ngayKT.isAfter(ngayBD)) {
                errNgayKT.setText("Ngày kết thúc phải sau ngày bắt đầu");
                errNgayKT.setVisible(true);
                txtNgayKetThuc.setInvalid(true);
                txtNgayKetThuc.requestFocusInWindow();
                return;
            }
            try {
                km.setTenKhuyenMai(txtTenKM.getText().trim());
                km.setPhanTramGG(Double.parseDouble(txtPhanTram.getText().trim()));
                km.setNgayBatDau(ngayBD);
                km.setNgayKetThuc(ngayKT);
                km.setTrangThai(chkTrangThai.isSelected());
                if (khuyenMaiSV.capNhatKhuyenMai(km)) {
                    tblKhuyenMai.refresh();
                    updateCategory();
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Cập nhật thất bại. Vui lòng thử lại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
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
        return khuyenMaiSV.sinhMaKhuyenMai();
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
