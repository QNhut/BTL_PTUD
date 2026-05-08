package gui;

import constants.Colors;
import constants.FontStyle;
import entity.NhanVien;
import exception.FormValidator;
import exception.RoundedButton;
import exception.RoundedPanel;
import exception.RoundedTextField;
import exception.StyledTable;
import service.NhanVien_Service;
import service.Validators;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import javax.imageio.ImageIO;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import javax.swing.*;

public class NhanVien_GUI extends JPanel implements ActionListener {

    private JPanel pnlTitle, pnlContent, pnlCategory;
    private JLabel lblTitle;
    private JLabel lblNote;
    private JPanel pnlHead;
    private JPanel pnlButtonAddNV;
    private RoundedButton btnAddNV;
    private JPanel pnlSearch;
    private RoundedTextField txtSearch;
    private RoundedButton btnFind;
    private RoundedButton btnAll;
    private JWindow suggestionWindow;
    private javax.swing.DefaultListModel<String> suggestionModel = new javax.swing.DefaultListModel<>();
    private javax.swing.JList<String> lstSuggestions;
    private java.util.List<NhanVien> suggestionData = new java.util.ArrayList<>();
    private boolean suppressSuggestion = false;
    private JLabel lblDSNV;
    private ArrayList<NhanVien> list;
    private ArrayList<NhanVien> fullList = new ArrayList<>();
    private NhanVien_Service nhanVienSV;
    private StyledTable tblNhanVien;
    private JPanel item;

    private static final String[] COLUMN_NAMES = {"Nhân viên", "Chức vụ", "Liên hệ", "Giới tính", "Trạng thái", "", ""};

    public NhanVien_GUI() {
        list = new ArrayList<NhanVien>();

        nhanVienSV = new NhanVien_Service();

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
        pnlTitle.add(lblTitle = new JLabel("Nhân viên"));
        lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.FOREGROUND);
        pnlTitle.add(lblNote = new JLabel("Quản lý nhân viên trong hệ thống"));
        lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNote.setForeground(Colors.MUTED);

        pnlButtonAddNV = new JPanel();
        pnlButtonAddNV.setBackground(Colors.BACKGROUND);
        pnlButtonAddNV.add(btnAddNV = new RoundedButton(170, 40, 10, "+ Thêm nhân viên", Colors.PRIMARY));

        pnlHead.add(pnlTitle);
        pnlHead.add(pnlButtonAddNV);
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
        pnlCategory = new JPanel(new GridLayout(1, 3, 16, 0));
        pnlCategory.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        pnlCategory.setBackground(Colors.BACKGROUND);
        pnlCategory.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        pnlCategory.setPreferredSize(new Dimension(0, 110));
        try {
            pnlCategory.add(statCard("Tổng nhân viên", nhanVienSV.getSoLuongNhanVien(), Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
            pnlCategory.add(statCard("Đang hoạt động", nhanVienSV.getSoLuongNhanVienOnline(), Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
            pnlCategory.add(statCard("Nghỉ việc", nhanVienSV.getSoLuongNhanVienOffline(), Colors.SECONDARY, Colors.DANGER, Colors.DANGER));
        } catch (Exception e) {
            System.err.println("[NhanVien_GUI] Không tải được thống kê: " + e.getMessage());
            pnlCategory.add(statCard("Tổng nhân viên", 0, Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
            pnlCategory.add(statCard("Đang hoạt động", 0, Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
            pnlCategory.add(statCard("Nghỉ việc", 0, Colors.SECONDARY, Colors.DANGER, Colors.DANGER));
        }
        return pnlCategory;
    }

    // ===== KHỐI: THANH TÌM KIẾM =====
    private JPanel taoSearchBar() {
        pnlSearch = new JPanel();
        pnlSearch.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 8));
        pnlSearch.setBackground(Colors.BACKGROUND);
        pnlSearch.add(lblDSNV = new JLabel("Danh sách nhân viên"));
        lblDSNV.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblDSNV.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));
        pnlSearch.add(Box.createHorizontalGlue());
        pnlSearch.add(txtSearch = new RoundedTextField(500, 40, 20, "Tìm kiếm nhân viên "));
        pnlSearch.add(btnFind = new RoundedButton(150, 40, 20, "Tìm kiếm", Colors.PRIMARY));
        pnlSearch.add(btnAll = new RoundedButton(100, 40, 20, "Tất cả", Colors.SECONDARY));
        pnlSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        btnAll.setForeground(Colors.TEXT_PRIMARY);
        return pnlSearch;
    }

    // ===== KHỐI: BẢNG =====
    private StyledTable setupTable() {
        tblNhanVien = new StyledTable(COLUMN_NAMES, list);

        tblNhanVien.setAvatarColumn(0, 220,
                v -> ((NhanVien) v).getTenNhanVien(),
                v -> ((NhanVien) v).getMaNhanVien());

        tblNhanVien.setTwoLineColumn(1, 180,
                v -> ((NhanVien) v).getChucVu() != null ? ((NhanVien) v).getChucVu().getTenChucVu() : "",
                v -> ((NhanVien) v).getEmail());

        tblNhanVien.setIconTwoLineColumn(2, 220,
                "\u2709", v -> ((NhanVien) v).getEmail(),
                "\u260E", v -> ((NhanVien) v).getSoDienThoai());

        tblNhanVien.setSingleTextColumn(3, 100,
                v -> ((NhanVien) v).isGioiTinh() ? "Nam" : "Nữ");

        tblNhanVien.setBadgeColumn(4, 130,
                v -> ((NhanVien) v).isTrangThai(), "Đang làm", "Nghỉ việc");

        tblNhanVien.setActionColumn(5, 80);
        tblNhanVien.setDeleteButtonColumn(6, 80);

        tblNhanVien.setActionColumnListener((row, obj) -> {
            NhanVien nhanVien = (NhanVien) obj;
            moDialogChiTietNhanVien(nhanVien);
        });

        tblNhanVien.setDeleteColumnListener((row, obj) -> {
            NhanVien nhanVien = (NhanVien) obj;
            int confirm = JOptionPane.showConfirmDialog(null,
                    "Bạn có chắc muốn xóa nhân viên \"" + nhanVien.getTenNhanVien() + "\" không?",
                    "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                nhanVienSV.xoaNhanVien(nhanVien.getMaNhanVien());
                list.remove(row.intValue());
                tblNhanVien.refresh();
                updateCategory();
            }
        });

        return tblNhanVien;
    }

    // ===== KHỐI: SỰ KIỆN =====
    private void wireEvents() {
        btnAddNV.addActionListener(this);
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
            ArrayList<NhanVien> dsNV = nhanVienSV.getDSNhanVien();
            if (dsNV == null || dsNV.isEmpty()) {
                System.out.println("[NhanVien_GUI] Cảnh báo: Danh sách nhân viên rỗng");
            }
            loadData(dsNV);
        } catch (Exception e) {
            System.out.println("[NhanVien_GUI] Lỗi khi tải dữ liệu nhân viên:");
            e.printStackTrace();
        }
    }

    public void loadData(ArrayList<NhanVien> dsNV) {
        fullList = new ArrayList<>(dsNV);
        list.clear();
        list.addAll(dsNV);
        tblNhanVien.refresh();
    }

    private void search() {
        hideSuggestions();
        String keyword = txtSearch.getText().trim();
        ArrayList<NhanVien> filtered = nhanVienSV.timKiem(fullList, keyword);
        list.clear();
        list.addAll(filtered);
        tblNhanVien.refresh();
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
                    NhanVien nv = suggestionData.get(idx);
                    suppressSuggestion = true;
                    txtSearch.setText(nv.getTenNhanVien());
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
        ArrayList<NhanVien> matches = nhanVienSV.timKiem(fullList, kw);
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
            NhanVien nv = matches.get(i);
            suggestionData.add(nv);
            suggestionModel.addElement(nv.getTenNhanVien() + "   (" + nv.getMaNhanVien() + ")");
        }
        Point loc = txtSearch.getLocationOnScreen();
        int h = Math.min(max * 32 + 10, 260);
        suggestionWindow.setBounds(loc.x, loc.y + txtSearch.getHeight() + 2, txtSearch.getWidth(), h);
        suggestionWindow.setVisible(true);
    }

    private void updateCategory() {
        pnlCategory.removeAll();
        pnlCategory.add(statCard("Tổng nhân viên", nhanVienSV.getSoLuongNhanVien(), Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
        pnlCategory.add(statCard("Đang hoạt động", nhanVienSV.getSoLuongNhanVienOnline(), Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
        pnlCategory.add(statCard("Nghỉ việc", nhanVienSV.getSoLuongNhanVienOffline(), Colors.SECONDARY, Colors.DANGER, Colors.DANGER));
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

    private void moDialogChiTietNhanVien(NhanVien nhanVien) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Chi tiết nhân viên", true);
        dialog.setSize(680, 680);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(Colors.BACKGROUND);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBackground(Colors.BACKGROUND);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        final String[] currentImagePath = {nhanVien.getHinhAnh()};
        final boolean[] editMode = {false};

        // ===== HEADER =====
        JPanel pnlHeader = new JPanel(new BorderLayout(12, 0));
        pnlHeader.setBackground(Colors.BACKGROUND);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(0, 0, 16, 0)));
        pnlHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        pnlHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Icon tròn (avatar nhân viên)
        JLabel iconLbl = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Colors.PRIMARY_LIGHT != null ? Colors.PRIMARY_LIGHT : new Color(220, 235, 255));
                g2.fillOval(0, 0, 48, 48);
                g2.setColor(Colors.PRIMARY);
                g2.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
                FontMetrics fm = g2.getFontMetrics();
                String init = getInitials(nhanVien.getTenNhanVien());
                g2.drawString(init, (48 - fm.stringWidth(init)) / 2, (48 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        iconLbl.setPreferredSize(new Dimension(48, 48));
        pnlHeader.add(iconLbl, BorderLayout.WEST);

        JPanel headerInfo = new JPanel();
        headerInfo.setLayout(new BoxLayout(headerInfo, BoxLayout.Y_AXIS));
        headerInfo.setOpaque(false);
        JLabel lblFormTitle = new JLabel("Chi tiết nhân viên");
        lblFormTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblFormTitle.setForeground(Colors.TEXT_PRIMARY);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerInfo.add(lblFormTitle);
        headerInfo.add(Box.createVerticalStrut(4));
        JLabel lblFormSub = new JLabel("Xem và chỉnh sửa thông tin nhân viên");
        lblFormSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblFormSub.setForeground(Colors.MUTED);
        lblFormSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerInfo.add(lblFormSub);
        headerInfo.add(Box.createVerticalStrut(6));
        JLabel lblMaBadge = new JLabel("Mã: " + nhanVien.getMaNhanVien());
        lblMaBadge.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblMaBadge.setForeground(Colors.PRIMARY);
        lblMaBadge.setOpaque(true);
        lblMaBadge.setBackground(Colors.SUCCESS_LIGHT);
        lblMaBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        lblMaBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerInfo.add(lblMaBadge);
        pnlHeader.add(headerInfo, BorderLayout.CENTER);

        // Nút đóng
        JButton btnCloseX = new JButton("x");
        btnCloseX.setFont(new Font("SansSerif", Font.PLAIN, 22));
        btnCloseX.setForeground(Colors.DANGER);
        btnCloseX.setFocusPainted(false);
        btnCloseX.setBorderPainted(false);
        btnCloseX.setContentAreaFilled(false);
        btnCloseX.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCloseX.setPreferredSize(new Dimension(48, 48));
        btnCloseX.addActionListener(e -> dialog.dispose());
        JPanel closeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closeWrap.setOpaque(false);
        closeWrap.add(btnCloseX);
        pnlHeader.add(closeWrap, BorderLayout.EAST);
        pnlMain.add(pnlHeader);
        pnlMain.add(Box.createVerticalStrut(20));

        // ===== TOP ROW: ẢNH + INFO =====
        JPanel topRow = new JPanel();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ẢNH
        JLabel lblAnhPreview = new JLabel("Tải ảnh", SwingConstants.CENTER);
        lblAnhPreview.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lblAnhPreview.setForeground(Colors.MUTED);
        lblAnhPreview.setOpaque(true);
        lblAnhPreview.setBackground(Colors.SECONDARY);
        lblAnhPreview.setPreferredSize(new Dimension(150, 200));
        lblAnhPreview.setMaximumSize(new Dimension(150, 200));
        lblAnhPreview.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        // Load ảnh hiện tại nếu có
        if (currentImagePath[0] != null && !currentImagePath[0].isEmpty()) {
            try {
                File f = resolveImageFile(currentImagePath[0]);
                System.out.println("[NhanVien] Load ảnh: path='" + currentImagePath[0] + "' → " + (f != null ? f.getAbsolutePath() : "null") + " exists=" + (f != null && f.exists()));
                if (f != null && f.exists()) {
                    BufferedImage raw = ImageIO.read(f);
                    if (raw != null) {
                        Image scaled = raw.getScaledInstance(146, 196, Image.SCALE_SMOOTH);
                        lblAnhPreview.setIcon(new ImageIcon(scaled));
                        lblAnhPreview.setText(null);
                    }
                }
            } catch (Exception ex) { System.out.println("[NhanVien] Exception loading image: " + ex); }
        }
        lblAnhPreview.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!editMode[0]) return;
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "Ảnh (PNG, JPG, JPEG)", "png", "jpg", "jpeg", "gif"));
                if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File sel = chooser.getSelectedFile();
                        String ext = sel.getName().contains(".") ? sel.getName().substring(sel.getName().lastIndexOf('.')) : ".png";
                        String destName = nhanVien.getMaNhanVien().toLowerCase() + ext;
                        File destDir = new File("data/img/users");
                        if (!destDir.exists()) destDir.mkdirs();
                        File dest = new File(destDir, destName);
                        Files.copy(sel.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        currentImagePath[0] = "users/" + destName;
                        BufferedImage raw = ImageIO.read(dest);
                        if (raw != null) {
                            Image scaled = raw.getScaledInstance(146, 196, Image.SCALE_SMOOTH);
                            lblAnhPreview.setIcon(new ImageIcon(scaled));
                            lblAnhPreview.setText(null);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Không đọc được ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        topRow.add(lblAnhPreview);
        topRow.add(Box.createRigidArea(new Dimension(20, 0)));

        // INFO bên phải ảnh
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedTextField txtTenNV = new RoundedTextField(270, 38, 10, "Họ và tên đầy đủ");
        txtTenNV.setText(nhanVien.getTenNhanVien() != null ? nhanVien.getTenNhanVien() : "");
        txtTenNV.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtTenNV.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtTenNV.setEnabled(false);
        JLabel errTen = FormValidator.errorLabel();
        info.add(fieldLabelNV("Họ và tên *"));
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(txtTenNV);
        info.add(Box.createRigidArea(new Dimension(0, 2)));
        info.add(errTen);
        info.add(Box.createRigidArea(new Dimension(0, 8)));

        JRadioButton rdoNam = new JRadioButton("Nam");
        JRadioButton rdoNu = new JRadioButton("Nữ");
        rdoNam.setSelected(nhanVien.isGioiTinh());
        rdoNu.setSelected(!nhanVien.isGioiTinh());
        rdoNam.setOpaque(false);
        rdoNu.setOpaque(false);
        rdoNam.setEnabled(false);
        rdoNu.setEnabled(false);
        rdoNam.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        rdoNu.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        ButtonGroup bgGT = new ButtonGroup();
        bgGT.add(rdoNam);
        bgGT.add(rdoNu);
        JPanel pnlGT = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlGT.setOpaque(false);
        pnlGT.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlGT.add(rdoNam);
        pnlGT.add(rdoNu);
        info.add(fieldLabelNV("Giới tính *"));
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(pnlGT);
        info.add(Box.createRigidArea(new Dimension(0, 12)));

        RoundedTextField txtCCCD = new RoundedTextField(270, 38, 10, "12 chữ số");
        txtCCCD.setText(nhanVien.getCCCD() != null ? nhanVien.getCCCD() : "");
        txtCCCD.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtCCCD.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtCCCD.setEnabled(false);
        JLabel errCCCD = FormValidator.errorLabel();
        info.add(fieldLabelNV("CCCD *"));
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(txtCCCD);
        info.add(Box.createRigidArea(new Dimension(0, 2)));
        info.add(errCCCD);
        info.add(Box.createRigidArea(new Dimension(0, 8)));

        RoundedTextField txtSDT = new RoundedTextField(270, 38, 10, "10 chữ số");
        txtSDT.setText(nhanVien.getSoDienThoai() != null ? nhanVien.getSoDienThoai() : "");
        txtSDT.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtSDT.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtSDT.setEnabled(false);
        JLabel errSDT = FormValidator.errorLabel();
        info.add(fieldLabelNV("Số điện thoại *"));
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(txtSDT);
        info.add(Box.createRigidArea(new Dimension(0, 2)));
        info.add(errSDT);

        topRow.add(info);
        pnlMain.add(topRow);
        pnlMain.add(Box.createVerticalStrut(16));

        // ===== THÔNG TIN BỔ SUNG =====
        RoundedTextField txtEmail = new RoundedTextField(300, 38, 10, "example@email.com");
        txtEmail.setText(nhanVien.getEmail() != null ? nhanVien.getEmail() : "");
        txtEmail.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtEmail.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtEmail.setEnabled(false);
        JLabel errEmail = FormValidator.errorLabel();
        pnlMain.add(fieldLabelNV("Email"));
        pnlMain.add(Box.createRigidArea(new Dimension(0, 4)));
        pnlMain.add(txtEmail);
        pnlMain.add(Box.createRigidArea(new Dimension(0, 2)));
        pnlMain.add(errEmail);
        pnlMain.add(Box.createRigidArea(new Dimension(0, 8)));

        RoundedTextField txtDiaChi = new RoundedTextField(300, 38, 10, "Địa chỉ (tùy chọn)");
        txtDiaChi.setText(nhanVien.getDiaChi() != null ? nhanVien.getDiaChi() : "");
        txtDiaChi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtDiaChi.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDiaChi.setEnabled(false);
        pnlMain.add(fieldLabelNV("Địa chỉ"));
        pnlMain.add(Box.createRigidArea(new Dimension(0, 4)));
        pnlMain.add(txtDiaChi);
        pnlMain.add(Box.createRigidArea(new Dimension(0, 12)));

        // Chức vụ + trạng thái
        java.util.ArrayList<entity.ChucVu> dsCV = nhanVienSV.getDSChucVu();
        JComboBox<entity.ChucVu> cboChucVu = new JComboBox<>();
        cboChucVu.addItem(null);
        for (entity.ChucVu cv : dsCV) cboChucVu.addItem(cv);
        if (nhanVien.getChucVu() != null) cboChucVu.setSelectedItem(nhanVien.getChucVu());
        cboChucVu.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lst, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(lst, value, index, isSelected, cellHasFocus);
                setText(value == null ? "-- Chọn chức vụ --" : ((entity.ChucVu) value).getTenChucVu());
                return this;
            }
        });
        cboChucVu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cboChucVu.setAlignmentX(Component.LEFT_ALIGNMENT);
        cboChucVu.setEnabled(false);
        pnlMain.add(fieldLabelNV("Chức vụ"));
        pnlMain.add(Box.createRigidArea(new Dimension(0, 4)));
        pnlMain.add(cboChucVu);
        pnlMain.add(Box.createRigidArea(new Dimension(0, 12)));

        JCheckBox chkTrangThai = new JCheckBox("Đang làm việc");
        chkTrangThai.setSelected(nhanVien.isTrangThai());
        chkTrangThai.setOpaque(false);
        chkTrangThai.setEnabled(false);
        chkTrangThai.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        chkTrangThai.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMain.add(chkTrangThai);
        pnlMain.add(Box.createVerticalGlue());

        // ===== VALIDATOR + FOOTER =====
        FormValidator fv = new FormValidator()
                .add(txtTenNV, errTen, Validators::tenNguoi)
                .add(txtCCCD, errCCCD, Validators::cccd)
                .add(txtSDT, errSDT, Validators::soDienThoai)
                .add(txtEmail, errEmail, Validators::emailOptional);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        pnlFooter.setOpaque(false);
        pnlFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT));

        RoundedButton btnClose = new RoundedButton(90, 38, 10, "Đóng", Colors.SECONDARY);
        btnClose.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnClose.setForeground(Colors.TEXT_PRIMARY);
        btnClose.addActionListener(e -> dialog.dispose());

        RoundedButton btnEdit = new RoundedButton(120, 38, 10, "Chỉnh sửa", Colors.PRIMARY);
        btnEdit.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnEdit.setForeground(Colors.BACKGROUND);

        RoundedButton btnSave = new RoundedButton(130, 38, 10, "Lưu thay đổi", Colors.PRIMARY);
        btnSave.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnSave.setForeground(Colors.BACKGROUND);
        btnSave.setVisible(false);

        btnEdit.addActionListener(e -> {
            editMode[0] = true;
            lblAnhPreview.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblAnhPreview.setToolTipText("Click để thay đổi ảnh đại diện");
            txtTenNV.setEnabled(true);
            rdoNam.setEnabled(true);
            rdoNu.setEnabled(true);
            txtCCCD.setEnabled(true);
            txtSDT.setEnabled(true);
            txtEmail.setEnabled(true);
            txtDiaChi.setEnabled(true);
            cboChucVu.setEnabled(true);
            chkTrangThai.setEnabled(true);
            btnEdit.setVisible(false);
            btnSave.setVisible(true);
        });

        btnSave.addActionListener(e -> {
            if (!fv.validateAll()) return;
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Bạn có chắc muốn lưu thay đổi?", "Xác nhận", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    nhanVien.setTenNhanVien(txtTenNV.getText().trim());
                    nhanVien.setGioiTinh(rdoNam.isSelected());
                    nhanVien.setCCCD(txtCCCD.getText().trim());
                    nhanVien.setSoDienThoai(txtSDT.getText().trim());
                    nhanVien.setEmail(txtEmail.getText().trim());
                    nhanVien.setDiaChi(txtDiaChi.getText().trim());
                    nhanVien.setChucVu((entity.ChucVu) cboChucVu.getSelectedItem());
                    nhanVien.setTrangThai(chkTrangThai.isSelected());
                    nhanVien.setHinhAnh(currentImagePath[0]);
                    if (nhanVienSV.updateNhanVien(nhanVien)) {
                        tblNhanVien.refresh();
                        updateCategory();
                        JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Cập nhật thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        pnlFooter.add(btnClose);
        pnlFooter.add(btnEdit);
        pnlFooter.add(btnSave);
        pnlMain.add(pnlFooter);

        JScrollPane scrollPane = new JScrollPane(pnlMain);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }


    // Thử tìm file ảnh theo nhiều prefix khác nhau
    private File resolveImageFile(String path) {
        if (path == null || path.isEmpty()) return null;
        // 1. Absolute hoặc relative so với working dir
        File f = new File(path);
        if (f.exists()) return f;
        // 2. Relative so với working dir + data/img/
        f = new File("data/img/" + path);
        if (f.exists()) return f;
        // 3. Tìm theo tên file trong users/
        f = new File("data/img/users/" + new File(path).getName());
        if (f.exists()) return f;
        // 4. Dùng project root qua class location (phòng khi working dir khác)
        try {
            File jar = new File(NhanVien_GUI.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            // jar là bin/ hoặc .jar — lấy thư mục chứa nó = project root
            File root = jar.isDirectory() ? jar.getParentFile() : jar.getParentFile();
            f = new File(root, "data/img/" + path);
            if (f.exists()) return f;
            f = new File(root, "data/img/users/" + new File(path).getName());
            if (f.exists()) return f;
        } catch (Exception ignored) {}
        return null;
    }

    private Icon createAvatarIcon(String imagePath, String name, int size) {
        BufferedImage bi = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        java.awt.geom.Ellipse2D.Float circle = new java.awt.geom.Ellipse2D.Float(0, 0, size, size);
        boolean imageLoaded = false;
        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                File f = resolveImageFile(imagePath);
                if (f != null && f.exists()) {
                    BufferedImage raw = ImageIO.read(f);
                    if (raw != null) {
                        g2.setClip(circle);
                        g2.drawImage(raw, 0, 0, size, size, null);
                        imageLoaded = true;
                    }
                }
            } catch (Exception ignored) {
            }
        }
        if (!imageLoaded) {
            g2.setClip(null);
            g2.setColor(Colors.PRIMARY);
            g2.fill(circle);
            g2.setClip(circle);
            g2.setColor(Color.WHITE);
            String initials = getInitials(name);
            g2.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
            FontMetrics fm = g2.getFontMetrics();
            int tx = (size - fm.stringWidth(initials)) / 2;
            int ty = (size - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(initials, tx, ty);
        }
        g2.dispose();
        return new ImageIcon(bi);
    }

    // Row chỉ đọc: label nhỏ trên + value in đậm dưới
    private JPanel buildReadonlyRow(String label, String value) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        JLabel lbl = new JLabel(label);
        lbl.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lbl.setForeground(new Color(110, 120, 130));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel val = new JLabel(value != null ? value : "");
        val.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        val.setForeground(Colors.TEXT_PRIMARY);
        val.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(lbl);
        row.add(Box.createVerticalStrut(2));
        row.add(val);
        return row;
    }

    // Row có input field: label nhỏ trên + component dưới
    private JPanel buildFieldRow(String label, JComponent field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        JLabel lbl = new JLabel(label);
        lbl.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lbl.setForeground(new Color(110, 120, 130));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(lbl);
        row.add(Box.createVerticalStrut(2));
        row.add(field);
        return row;
    }

    // Row có icon bên trái + label + field
    private JPanel buildIconFieldRow(String icon, String label, JTextField field) {
        JPanel row = new JPanel();
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblIcon.setForeground(Colors.PRIMARY);
        lblIcon.setPreferredSize(new Dimension(28, 40));
        lblIcon.setVerticalAlignment(SwingConstants.CENTER);
        row.add(lblIcon);
        row.add(Box.createHorizontalStrut(4));
        JPanel col = new JPanel();
        col.setLayout(new BoxLayout(col, BoxLayout.Y_AXIS));
        col.setOpaque(false);
        JLabel lbl = new JLabel(label);
        lbl.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lbl.setForeground(new Color(110, 120, 130));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        col.add(lbl);
        col.add(Box.createVerticalStrut(2));
        col.add(field);
        row.add(col);
        row.add(Box.createHorizontalGlue());
        return row;
    }

    // Áp style cho JTextField ở chế độ xem (ẩn border, nền trong suốt)
    private void styleDetailField(JTextField field) {
        field.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        field.setForeground(Colors.TEXT_PRIMARY);
        field.setOpaque(false);
        field.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        field.setEnabled(false);
        field.setDisabledTextColor(Colors.TEXT_PRIMARY);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
    }

    // Khi chuyển sang chế độ sửa: hiện border cho field
    private void enableDetailField(JTextField field) {
        field.setOpaque(true);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.PRIMARY, 1, true),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
    }

    private JPanel createInfoBox(String title, int width, String[] labels, String[] values, String[] icons) {
        JPanel box = new RoundedPanel(width, 230, 15);
        box.setLayout(new BoxLayout(box, BoxLayout.Y_AXIS));
        box.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        box.setBackground(Colors.SECONDARY); // Dark bg

        // Title
        JLabel lblBoxTitle = new JLabel(title);
        lblBoxTitle.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblBoxTitle.setForeground(new Color(100, 110, 120));
        lblBoxTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        box.add(lblBoxTitle);
        box.add(Box.createVerticalStrut(15));

        // Items
        for (int i = 0; i < labels.length; i++) {
            JPanel row = new JPanel();
            row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
            row.setOpaque(false);
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
            row.setAlignmentX(Component.LEFT_ALIGNMENT);

            // Icon (if exists)
            if (!icons[i].isEmpty()) {
                JLabel lblIcon = new JLabel(icons[i]);
                lblIcon.setFont(new Font("Segoe UI", Font.BOLD, 18));
                lblIcon.setForeground(Colors.PRIMARY);
                lblIcon.setPreferredSize(new Dimension(30, 35));
                lblIcon.setVerticalAlignment(SwingConstants.TOP);
                row.add(lblIcon);
                row.add(Box.createHorizontalStrut(5));
            }

            // Label + Value
            JPanel colInfo = new JPanel();
            colInfo.setLayout(new BoxLayout(colInfo, BoxLayout.Y_AXIS));
            colInfo.setOpaque(false);

            JLabel lblLabel = new JLabel(labels[i]);
            lblLabel.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
            lblLabel.setForeground(new Color(110, 120, 130));
            lblLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            colInfo.add(lblLabel);
            colInfo.add(Box.createVerticalStrut(2));

            JLabel lblValue;
            if (labels[i].equals("Địa chỉ") && values[i] != null && values[i].length() > 20) {
                // Format địa chỉ với text wrap
                lblValue = new JLabel("<html>" + values[i].replace(", ", ",<br>") + "</html>");
            } else {
                lblValue = new JLabel(values[i] != null ? values[i] : "");
            }
            lblValue.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            lblValue.setForeground(Colors.TEXT_PRIMARY);
            lblValue.setAlignmentX(Component.LEFT_ALIGNMENT);
            colInfo.add(lblValue);

            row.add(colInfo);
            row.add(Box.createHorizontalGlue());
            box.add(row);

            // Divider - chỉ show khi có icon (Thông tin Liên hệ)
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

    private String getInitials(String fullName) {
        if (fullName == null || fullName.isEmpty()) {
            return "NV";
        }
        String[] parts = fullName.trim().split("\\s+");
        if (parts.length >= 2) {
            return (parts[parts.length - 2].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
        }
        return fullName.substring(0, Math.min(2, fullName.length())).toUpperCase();
    }

    private void moDialogThemNhanVien() {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Thêm nhân viên mới", true);
        dialog.setSize(680, 680);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.getContentPane().setBackground(Colors.BACKGROUND);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBackground(Colors.BACKGROUND);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(20, 24, 20, 24));

        // ===== HEADER =====
        JPanel pnlHeader = new JPanel(new BorderLayout(12, 0));
        pnlHeader.setBackground(Colors.BACKGROUND);
        pnlHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Colors.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(0, 0, 16, 0)));
        pnlHeader.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        pnlHeader.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Icon tròn
        JLabel iconLbl = new JLabel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Colors.SUCCESS_LIGHT);
                g2.fillOval(0, 0, 48, 48);
                g2.setColor(Colors.SUCCESS);
                g2.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString("+", (48 - fm.stringWidth("+")) / 2, (48 + fm.getAscent() - fm.getDescent()) / 2);
                g2.dispose();
            }
        };
        iconLbl.setPreferredSize(new Dimension(48, 48));
        pnlHeader.add(iconLbl, BorderLayout.WEST);

        JPanel headerInfo = new JPanel();
        headerInfo.setLayout(new BoxLayout(headerInfo, BoxLayout.Y_AXIS));
        headerInfo.setOpaque(false);
        JLabel lblFormTitle = new JLabel("Thêm nhân viên mới");
        lblFormTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblFormTitle.setForeground(Colors.TEXT_PRIMARY);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerInfo.add(lblFormTitle);
        headerInfo.add(Box.createVerticalStrut(4));
        JLabel lblFormSub = new JLabel("Điền đầy đủ thông tin rồi nhấn Thêm nhân viên (* bắt buộc)");
        lblFormSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblFormSub.setForeground(Colors.MUTED);
        lblFormSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerInfo.add(lblFormSub);
        headerInfo.add(Box.createVerticalStrut(6));
        // Badge mã NV
        String maNV = taoMaNhanVien();
        JLabel lblMaBadge = new JLabel("Mã: " + maNV);
        lblMaBadge.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblMaBadge.setForeground(Colors.PRIMARY);
        lblMaBadge.setOpaque(true);
        lblMaBadge.setBackground(Colors.SUCCESS_LIGHT);
        lblMaBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(3, 8, 3, 8)));
        lblMaBadge.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerInfo.add(lblMaBadge);
        pnlHeader.add(headerInfo, BorderLayout.CENTER);

        // Nút đóng
        JButton btnClose = new JButton("x");
        btnClose.setFont(new Font("SansSerif", Font.PLAIN, 22));
        btnClose.setForeground(Colors.DANGER);
        btnClose.setFocusPainted(false);
        btnClose.setBorderPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.setPreferredSize(new Dimension(48, 48));
        btnClose.addActionListener(e -> dialog.dispose());
        JPanel closeWrap = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        closeWrap.setOpaque(false);
        closeWrap.add(btnClose);
        pnlHeader.add(closeWrap, BorderLayout.EAST);
        pnlMain.add(pnlHeader);
        pnlMain.add(Box.createVerticalStrut(20));

        // ===== TOP ROW: ẢNH + INFO =====
        JPanel topRow = new JPanel();
        topRow.setLayout(new BoxLayout(topRow, BoxLayout.X_AXIS));
        topRow.setOpaque(false);
        topRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        // ẢNH
        final String[] selectedImagePath = {null};
        JLabel lblAnhPreview = new JLabel("Tải ảnh", SwingConstants.CENTER);
        lblAnhPreview.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lblAnhPreview.setForeground(Colors.MUTED);
        lblAnhPreview.setOpaque(true);
        lblAnhPreview.setBackground(Colors.SECONDARY);
        lblAnhPreview.setPreferredSize(new Dimension(150, 200));
        lblAnhPreview.setMaximumSize(new Dimension(150, 200));
        lblAnhPreview.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        lblAnhPreview.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblAnhPreview.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent e) {
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "Ảnh (PNG, JPG, JPEG)", "png", "jpg", "jpeg", "gif"));
                if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                    try {
                        File sel = chooser.getSelectedFile();
                        String ext = sel.getName().contains(".") ? sel.getName().substring(sel.getName().lastIndexOf('.')) : ".png";
                        String destName = maNV.toLowerCase() + ext;
                        File destDir = new File("data/img/users");
                        if (!destDir.exists()) destDir.mkdirs();
                        File dest = new File(destDir, destName);
                        Files.copy(sel.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        selectedImagePath[0] = "users/" + destName;
                        BufferedImage raw = ImageIO.read(dest);
                        if (raw != null) {
                            Image scaled = raw.getScaledInstance(146, 196, Image.SCALE_SMOOTH);
                            lblAnhPreview.setIcon(new ImageIcon(scaled));
                            lblAnhPreview.setText(null);
                        }
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, "Không đọc được ảnh: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        topRow.add(lblAnhPreview);
        topRow.add(Box.createRigidArea(new Dimension(20, 0)));

        // INFO bên phải ảnh
        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));
        info.setOpaque(false);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedTextField txtTenNV = new RoundedTextField(270, 38, 10, "Họ và tên đầy đủ");
        txtTenNV.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtTenNV.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel errTen = FormValidator.errorLabel();
        info.add(fieldLabelNV("Họ và tên *"));
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(txtTenNV);
        info.add(Box.createRigidArea(new Dimension(0, 2)));
        info.add(errTen);
        info.add(Box.createRigidArea(new Dimension(0, 8)));

        JRadioButton rdoNam = new JRadioButton("Nam");
        JRadioButton rdoNu = new JRadioButton("Nữ");
        rdoNam.setSelected(true);
        rdoNam.setOpaque(false);
        rdoNu.setOpaque(false);
        rdoNam.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        rdoNu.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        ButtonGroup bgGT = new ButtonGroup();
        bgGT.add(rdoNam);
        bgGT.add(rdoNu);
        JPanel pnlGT = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlGT.setOpaque(false);
        pnlGT.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlGT.add(rdoNam);
        pnlGT.add(rdoNu);
        info.add(fieldLabelNV("Giới tính *"));
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(pnlGT);
        info.add(Box.createRigidArea(new Dimension(0, 12)));

        RoundedTextField txtCCCD = new RoundedTextField(270, 38, 10, "12 chữ số");
        txtCCCD.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtCCCD.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel errCCCD = FormValidator.errorLabel();
        info.add(fieldLabelNV("CCCD *"));
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(txtCCCD);
        info.add(Box.createRigidArea(new Dimension(0, 2)));
        info.add(errCCCD);
        info.add(Box.createRigidArea(new Dimension(0, 8)));

        RoundedTextField txtSDT = new RoundedTextField(270, 38, 10, "10 chữ số");
        txtSDT.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtSDT.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel errSDT = FormValidator.errorLabel();
        info.add(fieldLabelNV("Số điện thoại *"));
        info.add(Box.createRigidArea(new Dimension(0, 4)));
        info.add(txtSDT);
        info.add(Box.createRigidArea(new Dimension(0, 2)));
        info.add(errSDT);

        topRow.add(info);
        pnlMain.add(topRow);
        pnlMain.add(Box.createVerticalStrut(16));

        // ===== THÔNG TIN BỔ SUNG =====
        RoundedTextField txtEmail2 = new RoundedTextField(300, 38, 10, "example@email.com");
        txtEmail2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtEmail2.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel errEmail = FormValidator.errorLabel();
        pnlMain.add(fieldLabelNV("Email"));
        pnlMain.add(Box.createRigidArea(new Dimension(0, 4)));
        pnlMain.add(txtEmail2);
        pnlMain.add(Box.createRigidArea(new Dimension(0, 2)));
        pnlMain.add(errEmail);
        pnlMain.add(Box.createRigidArea(new Dimension(0, 8)));

        RoundedTextField txtDiaChi = new RoundedTextField(300, 38, 10, "Địa chỉ (tùy chọn)");
        txtDiaChi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtDiaChi.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMain.add(fieldLabelNV("Địa chỉ"));
        pnlMain.add(Box.createRigidArea(new Dimension(0, 4)));
        pnlMain.add(txtDiaChi);
        pnlMain.add(Box.createRigidArea(new Dimension(0, 12)));

        // Chức vụ + trạng thái
        java.util.ArrayList<entity.ChucVu> dsCV = nhanVienSV.getDSChucVu();
        JComboBox<entity.ChucVu> cboChucVu = new JComboBox<>();
        cboChucVu.addItem(null);
        for (entity.ChucVu cv : dsCV) cboChucVu.addItem(cv);
        cboChucVu.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lst, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(lst, value, index, isSelected, cellHasFocus);
                setText(value == null ? "-- Chọn chức vụ --" : ((entity.ChucVu) value).getTenChucVu());
                return this;
            }
        });
        cboChucVu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cboChucVu.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMain.add(fieldLabelNV("Chức vụ"));
        pnlMain.add(Box.createRigidArea(new Dimension(0, 4)));
        pnlMain.add(cboChucVu);
        pnlMain.add(Box.createRigidArea(new Dimension(0, 12)));

        JCheckBox chkTrangThai = new JCheckBox("Đang làm việc");
        chkTrangThai.setSelected(true);
        chkTrangThai.setOpaque(false);
        chkTrangThai.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        chkTrangThai.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMain.add(chkTrangThai);
        pnlMain.add(Box.createVerticalGlue());

        // ===== VALIDATOR + FOOTER =====
        FormValidator fv = new FormValidator()
                .add(txtTenNV, errTen, Validators::tenNguoi)
                .add(txtCCCD, errCCCD, Validators::cccd)
                .add(txtSDT, errSDT, Validators::soDienThoai)
                .add(txtEmail2, errEmail, Validators::emailOptional);

        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        pnlFooter.setOpaque(false);
        pnlFooter.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlFooter.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT));

        RoundedButton btnHuy = new RoundedButton(90, 38, 10, "Hủy", Colors.SECONDARY);
        btnHuy.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnHuy.setForeground(Colors.TEXT_PRIMARY);
        RoundedButton btnThem = new RoundedButton(150, 38, 10, "Thêm nhân viên", Colors.PRIMARY);
        btnThem.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnThem.setForeground(Colors.BACKGROUND);

        btnHuy.addActionListener(ev -> dialog.dispose());
        btnThem.addActionListener(ev -> {
            if (!fv.validateAll()) return;
            try {
                entity.ChucVu selectedCV = (entity.ChucVu) cboChucVu.getSelectedItem();
                NhanVien nv = new NhanVien(
                        maNV,
                        txtTenNV.getText().trim(),
                        rdoNam.isSelected(),
                        txtSDT.getText().trim(),
                        txtDiaChi.getText().trim(),
                        txtEmail2.getText().trim(),
                        txtCCCD.getText().trim(),
                        selectedCV,
                        selectedImagePath[0],
                        chkTrangThai.isSelected()
                );
                if (nhanVienSV.themNhanVien(nv)) {
                    list.add(nv);
                    tblNhanVien.refresh();
                    updateCategory();
                    JOptionPane.showMessageDialog(dialog,
                            "Thêm nhân viên thành công!", "Thành công",
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog,
                            "Thêm thất bại. Vui lòng thử lại.", "Lỗi",
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });

        pnlFooter.add(btnHuy);
        pnlFooter.add(btnThem);
        pnlMain.add(pnlFooter);

        JScrollPane scroll = new JScrollPane(pnlMain);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        dialog.add(scroll);
        dialog.setVisible(true);
    }

    private JLabel fieldLabelNV(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        l.setForeground(Colors.TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private String taoMaNhanVien() {
        return nhanVienSV.sinhMaNhanVien();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnAddNV) {
            moDialogThemNhanVien();
        } else if (e.getSource() == btnFind) {
            search();
        } else if (e.getSource() == btnAll) {
            txtSearch.setText("");
            list.clear();
            list.addAll(fullList);
            tblNhanVien.refresh();
        }
    }
}