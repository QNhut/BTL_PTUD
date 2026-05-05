package gui;

import constants.Colors;
import constants.FontStyle;
import dao.NhanVien_DAO;
import entity.NhanVien;
import exception.RoundedButton;
import exception.RoundedPanel;
import exception.RoundedTextField;
import exception.StyledTable;
import service.NhanVien_Service;

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

        // Phần tiêu đề
        add(pnlHead = new JPanel(), BorderLayout.NORTH);
        pnlHead.setLayout(new BoxLayout(pnlHead, BoxLayout.X_AXIS));
        pnlHead.setBackground(Colors.BACKGROUND);
        pnlHead.add(pnlTitle = new JPanel());
        pnlHead.add(pnlButtonAddNV = new JPanel());

        pnlTitle.setLayout(new BoxLayout(pnlTitle, BoxLayout.Y_AXIS));
        pnlTitle.setPreferredSize(new Dimension(900, 0));
        pnlTitle.setBackground(Colors.BACKGROUND);
        pnlTitle.add(lblTitle = new JLabel("Nhân viên"));
        lblTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));

        pnlTitle.add(lblNote = new JLabel("Quản lý nhân viên trong hệ thống"));
        lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.BASE));

        pnlButtonAddNV.setBackground(Colors.BACKGROUND);
        pnlButtonAddNV.add(btnAddNV = new RoundedButton(160, 40, 20, "+Thêm nhân viên", Colors.PRIMARY));

        // Phần nội dung
        add(pnlContent = new JPanel(), BorderLayout.CENTER);
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(Colors.BACKGROUND);

        // Thông tin chung: Tổng NV, Trạng thái,...
        pnlContent.add(pnlCategory = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)));
        pnlCategory.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        pnlCategory.setBackground(Colors.BACKGROUND);
        pnlCategory.add(createPanelCategory(nhanVienSV.getSoLuongNhanVien(), "Tổng nhân viên"));
        pnlCategory.add(createPanelCategory(nhanVienSV.getSoLuongNhanVienOnline(), "Đang hoạt động"));
        pnlCategory.add(createPanelCategory(nhanVienSV.getSoLuongNhanVienOffline(), "Offline"));

        // Thanh tìm kiếm + nút tìm kiếm
        pnlContent.add(pnlSearch = new JPanel());
        pnlSearch.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        pnlSearch.setBackground(Colors.BACKGROUND);

        pnlSearch.add(lblDSNV = new JLabel("Danh sách nhân viên"));
        lblDSNV.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblDSNV.setBorder(BorderFactory.createEmptyBorder(0, 50, 0, 0));
        pnlSearch.add(Box.createHorizontalGlue());
        pnlSearch.add(txtSearch = new RoundedTextField(500, 40, 20, "Tìm kiếm nhân viên "));
        pnlSearch.add(btnFind = new RoundedButton(150, 40, 20, "Tìm kiếm", Colors.PRIMARY));
        pnlSearch.add(btnAll = new RoundedButton(100, 40, 20, "Tất cả", Colors.SECONDARY));
        btnAll.setForeground(Colors.TEXT_PRIMARY);

        // Bảng danh sách nhân viên
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

        // Xử lý click nút "Chi tiết"
        tblNhanVien.setActionColumnListener((row, obj) -> {
            NhanVien nhanVien = (NhanVien) obj;
            moDialogChiTietNhanVien(nhanVien);
        });

        // Xử lý click nút "Xóa"
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

        pnlContent.add(tblNhanVien);

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
        pnlCategory.add(createPanelCategory(nhanVienSV.getSoLuongNhanVien(), "Tổng nhân viên"));
        pnlCategory.add(createPanelCategory(nhanVienSV.getSoLuongNhanVienOnline(), "Đang hoạt động"));
        pnlCategory.add(createPanelCategory(nhanVienSV.getSoLuongNhanVienOffline(), "Offline"));
        pnlCategory.revalidate();
        pnlCategory.repaint();
        pnlContent.revalidate();
        pnlContent.repaint();
    }

    public JPanel createPanelCategory(int number, String title) {
        item = new RoundedPanel(300, 100, 25);
        item.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 15));
        item.setBackground(Colors.BACKGROUND);

        // Icon số bên trái — hình vuông bo góc nền xanh đậm
        RoundedPanel pnlNumber = new RoundedPanel(50, 50, 12);
        pnlNumber.setLayout(new GridBagLayout());
        pnlNumber.setBackground(Colors.PRIMARY_LIGHT);
        JLabel lblNumber = new JLabel(Integer.toString(number));
        lblNumber.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblNumber.setForeground(Colors.SUCCESS);
        pnlNumber.add(lblNumber);

        // Phần text bên phải
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

    private void moDialogChiTietNhanVien(NhanVien nhanVien) {
        JDialog dialog = new JDialog((java.awt.Frame) null, "Chi tiết nhân viên", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setSize(650, 700);
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

        final String[] currentImagePath = {nhanVien.getHinhAnh()};
        final boolean[] editMode = {false};

        JLabel lblAvatar = new JLabel();
        lblAvatar.setHorizontalAlignment(SwingConstants.CENTER);
        lblAvatar.setVerticalAlignment(SwingConstants.CENTER);
        lblAvatar.setIcon(createAvatarIcon(currentImagePath[0], nhanVien.getTenNhanVien(), 80));
        lblAvatar.setPreferredSize(new Dimension(80, 80));
        lblAvatar.setMaximumSize(new Dimension(80, 80));
        lblAvatar.setMinimumSize(new Dimension(80, 80));

        lblAvatar.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (!editMode[0]) {
                    return;
                }
                JFileChooser chooser = new JFileChooser();
                chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                        "Ảnh (jpg, png, gif, bmp)", "jpg", "jpeg", "png", "gif", "bmp"));
                chooser.setDialogTitle("Chọn ảnh đại diện");
                if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                    File selected = chooser.getSelectedFile();
                    try {
                        String fname = selected.getName();
                        String ext = fname.lastIndexOf('.') >= 0
                                ? fname.substring(fname.lastIndexOf('.')) : ".png";
                        String destFileName = nhanVien.getMaNhanVien() + ext;
                        File destDir = new File("data/img/users");
                        if (!destDir.exists()) {
                            destDir.mkdirs();
                        }
                        File dest = new File(destDir, destFileName);
                        Files.copy(selected.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        currentImagePath[0] = dest.getPath().replace("\\", "/");
                        lblAvatar.setIcon(createAvatarIcon(currentImagePath[0], nhanVien.getTenNhanVien(), 80));
                    } catch (IOException ex) {
                        JOptionPane.showMessageDialog(dialog, "Không thể tải ảnh: " + ex.getMessage(),
                                "Lỗi", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        pnlHeader.add(lblAvatar);
        pnlHeader.add(Box.createHorizontalStrut(15));

        JPanel pnlHeaderInfo = new JPanel();
        pnlHeaderInfo.setLayout(new BoxLayout(pnlHeaderInfo, BoxLayout.Y_AXIS));
        pnlHeaderInfo.setOpaque(false);

        JLabel lblTenNV = new JLabel(nhanVien.getTenNhanVien());
        lblTenNV.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblTenNV.setForeground(Colors.TEXT_PRIMARY);
        pnlHeaderInfo.add(lblTenNV);

        JLabel lblChucVu = new JLabel(nhanVien.getChucVu() != null ? nhanVien.getChucVu().getTenChucVu() : "");
        lblChucVu.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblChucVu.setForeground(Colors.TEXT_SECONDARY);
        pnlHeaderInfo.add(lblChucVu);

        pnlHeaderInfo.add(Box.createVerticalStrut(10));

        JLabel lblTrangThai = new JLabel(nhanVien.isTrangThai() ? "● Đang làm" : "● Nghỉ việc");
        lblTrangThai.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblTrangThai.setForeground(nhanVien.isTrangThai() ? Colors.SUCCESS : Color.GRAY);
        pnlHeaderInfo.add(lblTrangThai);

        pnlHeader.add(pnlHeaderInfo);
        pnlHeader.add(Box.createHorizontalGlue());

        JButton btnEdit = new RoundedButton(120, 40, 15, "Chỉnh sửa", Colors.PRIMARY);
        btnEdit.setPreferredSize(new Dimension(120, 40));
        btnEdit.setMaximumSize(new Dimension(120, 40));
        pnlHeader.add(btnEdit);

        pnlMain.add(pnlHeader);

        // ===== EDITABLE FIELDS =====
        // -- THÔNG TIN CÁ NHÂN --
        JComboBox<String> cboGioiTinh = new JComboBox<>(new String[]{"Nam", "Nữ"});
        cboGioiTinh.setSelectedItem(nhanVien.isGioiTinh() ? "Nam" : "Nữ");
        cboGioiTinh.setEnabled(false);
        cboGioiTinh.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JTextField txtCCCD = new JTextField(nhanVien.getCCCD() != null ? nhanVien.getCCCD() : "");
        styleDetailField(txtCCCD);

        // -- THÔNG TIN LIÊN HỆ --
        JTextField txtSDT = new JTextField(nhanVien.getSoDienThoai() != null ? nhanVien.getSoDienThoai() : "");
        styleDetailField(txtSDT);

        JTextField txtEmail = new JTextField(nhanVien.getEmail() != null ? nhanVien.getEmail() : "");
        styleDetailField(txtEmail);

        JTextField txtDiaChi = new JTextField(nhanVien.getDiaChi() != null ? nhanVien.getDiaChi() : "");
        styleDetailField(txtDiaChi);

        // -- THÔNG TIN CÔNG VIỆC --
        java.util.ArrayList<entity.ChucVu> dsCV = nhanVienSV.getDSChucVu();
        JComboBox<entity.ChucVu> cboChucVu = new JComboBox<>();
        for (entity.ChucVu cv : dsCV) {
            cboChucVu.addItem(cv);
        }
        if (nhanVien.getChucVu() != null) {
            cboChucVu.setSelectedItem(nhanVien.getChucVu());
        }
        cboChucVu.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lst, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(lst, value, index, isSelected, cellHasFocus);
                setText(value == null ? "" : ((entity.ChucVu) value).getTenChucVu());
                return this;
            }
        });
        cboChucVu.setEnabled(false);
        cboChucVu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JLabel lblMoTaValue = new JLabel(nhanVien.getChucVu() != null ? nhanVien.getChucVu().getMoTa() : "");
        lblMoTaValue.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblMoTaValue.setForeground(Colors.TEXT_PRIMARY);
        lblMoTaValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        cboChucVu.addActionListener(ev -> {
            entity.ChucVu cv = (entity.ChucVu) cboChucVu.getSelectedItem();
            lblMoTaValue.setText(cv != null ? cv.getMoTa() : "");
            lblChucVu.setText(cv != null ? cv.getTenChucVu() : "");
        });

        // ===== CONTENT LAYOUT =====
        JPanel pnlInfoContent = new JPanel();
        pnlInfoContent.setLayout(new BoxLayout(pnlInfoContent, BoxLayout.Y_AXIS));
        pnlInfoContent.setBackground(Colors.BACKGROUND);
        pnlInfoContent.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Row 1: 2 columns
        JPanel pnlRow1 = new JPanel();
        pnlRow1.setLayout(new BoxLayout(pnlRow1, BoxLayout.X_AXIS));
        pnlRow1.setOpaque(false);
        pnlRow1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));

        // Left box
        JPanel pnlInfoLeft = new RoundedPanel(280, 240, 15);
        pnlInfoLeft.setLayout(new BoxLayout(pnlInfoLeft, BoxLayout.Y_AXIS));
        pnlInfoLeft.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlInfoLeft.setBackground(Colors.SECONDARY);
        pnlInfoLeft.setPreferredSize(new Dimension(280, 240));
        pnlInfoLeft.setMaximumSize(new Dimension(280, 240));

        JLabel lblTitlePersonal = new JLabel("THÔNG TIN CÁ NHÂN");
        lblTitlePersonal.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblTitlePersonal.setForeground(new Color(100, 110, 120));
        lblTitlePersonal.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlInfoLeft.add(lblTitlePersonal);
        pnlInfoLeft.add(Box.createVerticalStrut(10));
        pnlInfoLeft.add(buildReadonlyRow("Mã nhân viên", nhanVien.getMaNhanVien()));
        pnlInfoLeft.add(Box.createVerticalStrut(8));
        pnlInfoLeft.add(buildFieldRow("Giới tính", cboGioiTinh));
        pnlInfoLeft.add(Box.createVerticalStrut(8));
        pnlInfoLeft.add(buildFieldRow("CCCD", txtCCCD));
        pnlInfoLeft.add(Box.createVerticalStrut(8));
        pnlInfoLeft.add(buildReadonlyRow("Ngày vào làm", "01/03/2024"));

        // Right box
        JPanel pnlInfoRight = new RoundedPanel(280, 240, 15);
        pnlInfoRight.setLayout(new BoxLayout(pnlInfoRight, BoxLayout.Y_AXIS));
        pnlInfoRight.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlInfoRight.setBackground(Colors.SECONDARY);
        pnlInfoRight.setPreferredSize(new Dimension(280, 240));
        pnlInfoRight.setMaximumSize(new Dimension(280, 240));

        JLabel lblTitleContact = new JLabel("THÔNG TIN LIÊN HỆ");
        lblTitleContact.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblTitleContact.setForeground(new Color(100, 110, 120));
        lblTitleContact.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlInfoRight.add(lblTitleContact);
        pnlInfoRight.add(Box.createVerticalStrut(10));
        pnlInfoRight.add(buildIconFieldRow("\u260E", "Số điện thoại", txtSDT));
        pnlInfoRight.add(Box.createVerticalStrut(6));
        JSeparator sep1 = new JSeparator();
        sep1.setForeground(new Color(50, 55, 60));
        sep1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnlInfoRight.add(sep1);
        pnlInfoRight.add(Box.createVerticalStrut(6));
        pnlInfoRight.add(buildIconFieldRow("\u2709", "Email", txtEmail));
        pnlInfoRight.add(Box.createVerticalStrut(6));
        JSeparator sep2 = new JSeparator();
        sep2.setForeground(new Color(50, 55, 60));
        sep2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        pnlInfoRight.add(sep2);
        pnlInfoRight.add(Box.createVerticalStrut(6));
        pnlInfoRight.add(buildIconFieldRow("\uD83D\uDCCD", "Địa chỉ", txtDiaChi));

        pnlRow1.add(pnlInfoLeft);
        pnlRow1.add(Box.createHorizontalStrut(15));
        pnlRow1.add(pnlInfoRight);
        pnlRow1.add(Box.createHorizontalGlue());
        pnlInfoContent.add(pnlRow1);
        pnlInfoContent.add(Box.createVerticalStrut(15));

        // Row 2: Full width
        JPanel pnlRow2 = new JPanel();
        pnlRow2.setLayout(new BoxLayout(pnlRow2, BoxLayout.X_AXIS));
        pnlRow2.setOpaque(false);
        pnlRow2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JPanel pnlInfoWork = new RoundedPanel(580, 140, 15);
        pnlInfoWork.setLayout(new BoxLayout(pnlInfoWork, BoxLayout.Y_AXIS));
        pnlInfoWork.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlInfoWork.setBackground(Colors.SECONDARY);
        pnlInfoWork.setPreferredSize(new Dimension(580, 150));
        pnlInfoWork.setMaximumSize(new Dimension(580, 150));

        JLabel lblTitleWork = new JLabel("THÔNG TIN CÔNG VIỆC");
        lblTitleWork.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblTitleWork.setForeground(new Color(100, 110, 120));
        lblTitleWork.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlInfoWork.add(lblTitleWork);
        pnlInfoWork.add(Box.createVerticalStrut(10));

        JPanel pnlWorkRow = new JPanel();
        pnlWorkRow.setLayout(new BoxLayout(pnlWorkRow, BoxLayout.X_AXIS));
        pnlWorkRow.setOpaque(false);
        pnlWorkRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlWorkRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JPanel colChucVu = new JPanel();
        colChucVu.setLayout(new BoxLayout(colChucVu, BoxLayout.Y_AXIS));
        colChucVu.setOpaque(false);
        JLabel lblCVLabel = new JLabel("Chức vụ");
        lblCVLabel.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lblCVLabel.setForeground(new Color(110, 120, 130));
        lblCVLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        colChucVu.add(lblCVLabel);
        colChucVu.add(Box.createVerticalStrut(4));
        cboChucVu.setAlignmentX(Component.LEFT_ALIGNMENT);
        cboChucVu.setPreferredSize(new Dimension(200, 32));
        colChucVu.add(cboChucVu);

        JPanel colMoTa = new JPanel();
        colMoTa.setLayout(new BoxLayout(colMoTa, BoxLayout.Y_AXIS));
        colMoTa.setOpaque(false);
        JLabel lblMTLabel = new JLabel("Mô tả công việc");
        lblMTLabel.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lblMTLabel.setForeground(new Color(110, 120, 130));
        lblMTLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        colMoTa.add(lblMTLabel);
        colMoTa.add(Box.createVerticalStrut(4));
        colMoTa.add(lblMoTaValue);

        pnlWorkRow.add(colChucVu);
        pnlWorkRow.add(Box.createHorizontalStrut(30));
        pnlWorkRow.add(colMoTa);
        pnlWorkRow.add(Box.createHorizontalGlue());
        pnlInfoWork.add(pnlWorkRow);

        pnlRow2.add(pnlInfoWork);
        pnlRow2.add(Box.createHorizontalGlue());
        pnlInfoContent.add(pnlRow2);

        pnlMain.add(pnlInfoContent);
        pnlMain.add(Box.createVerticalGlue());

        // ===== FOOTER =====
        JPanel pnlFooter = new JPanel();
        pnlFooter.setLayout(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        pnlFooter.setBackground(Colors.BACKGROUND);
        pnlFooter.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        pnlFooter.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));

        JButton btnSave = new RoundedButton(150, 40, 15, "Lưu thay đổi", Colors.PRIMARY);
        btnSave.setEnabled(false);
        btnSave.setForeground(Colors.BACKGROUND);

        JButton btnClose = new RoundedButton(100, 40, 15, "Đóng", Colors.SECONDARY);
        btnClose.setForeground(Colors.FOREGROUND);
        btnClose.addActionListener(e -> dialog.dispose());

        // ===== LISTENERS =====
        btnEdit.addActionListener(e -> {
            editMode[0] = true;
            lblAvatar.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            lblAvatar.setToolTipText("Click để thay đổi ảnh đại diện");
            cboGioiTinh.setEnabled(true);
            txtCCCD.setEnabled(true);
            enableDetailField(txtCCCD);
            txtSDT.setEnabled(true);
            enableDetailField(txtSDT);
            txtEmail.setEnabled(true);
            enableDetailField(txtEmail);
            txtDiaChi.setEnabled(true);
            enableDetailField(txtDiaChi);
            cboChucVu.setEnabled(true);
            btnSave.setEnabled(true);
            btnEdit.setEnabled(false);
            btnEdit.setText("Đang sửa...");
        });

        btnSave.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog,
                    "Bạn có chắc muốn lưu thay đổi thông tin nhân viên?",
                    "Xác nhận lưu", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    nhanVien.setGioiTinh("Nam".equals(cboGioiTinh.getSelectedItem()));
                    nhanVien.setCCCD(txtCCCD.getText().trim());
                    nhanVien.setSoDienThoai(txtSDT.getText().trim());
                    nhanVien.setEmail(txtEmail.getText().trim());
                    nhanVien.setDiaChi(txtDiaChi.getText().trim());
                    nhanVien.setChucVu((entity.ChucVu) cboChucVu.getSelectedItem());
                    nhanVien.setHinhAnh(currentImagePath[0]);

                    if (nhanVienSV.updateNhanVien(nhanVien)) {
                        tblNhanVien.refresh();
                        updateCategory();
                        JOptionPane.showMessageDialog(dialog, "Cập nhật thành công!", "Thành công",
                                JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Cập nhật thất bại. Vui lòng thử lại.", "Lỗi",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (IllegalArgumentException ex) {
                    JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Dữ liệu không hợp lệ",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        pnlFooter.add(btnSave);
        pnlFooter.add(btnClose);
        pnlMain.add(pnlFooter);

        JScrollPane scrollPane = new JScrollPane(pnlMain);
        scrollPane.setBorder(null);
        dialog.add(scrollPane);
        dialog.setVisible(true);
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
                File f = new File(imagePath);
                if (f.exists()) {
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
        dialog.setSize(680, 640);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BoxLayout(pnlMain, BoxLayout.Y_AXIS));
        pnlMain.setBackground(Colors.BACKGROUND);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        // ===== HEADER =====
        JLabel lblFormTitle = new JLabel("Thêm nhân viên mới");
        lblFormTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblFormTitle.setForeground(Colors.TEXT_PRIMARY);
        lblFormTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMain.add(lblFormTitle);

        JLabel lblFormSub = new JLabel("Điền đầy đủ thông tin rồi nhấn Thêm nhân viên  (* bắt buộc)");
        lblFormSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblFormSub.setForeground(Colors.TEXT_SECONDARY);
        lblFormSub.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlMain.add(lblFormSub);
        pnlMain.add(Box.createVerticalStrut(20));

        // ===== FORM FIELDS =====
        RoundedTextField txtMaNV = new RoundedTextField(270, 38, 10, "");
        txtMaNV.setText(taoMaNhanVien());
        txtMaNV.setEnabled(false);

        RoundedTextField txtTenNV = new RoundedTextField(270, 38, 10, "Họ và tên đầy đủ");
        RoundedTextField txtCCCD = new RoundedTextField(270, 38, 10, "12 chữ số");
        RoundedTextField txtSDT = new RoundedTextField(270, 38, 10, "10 chữ số");
        RoundedTextField txtEmail2 = new RoundedTextField(270, 38, 10, "example@email.com");
        RoundedTextField txtDiaChi = new RoundedTextField(270, 38, 10, "Địa chỉ (tùy chọn)");

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

        java.util.ArrayList<entity.ChucVu> dsCV = nhanVienSV.getDSChucVu();
        JComboBox<entity.ChucVu> cboChucVu = new JComboBox<>();
        cboChucVu.addItem(null);
        for (entity.ChucVu cv : dsCV) {
            cboChucVu.addItem(cv);
        }
        cboChucVu.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lst, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(lst, value, index, isSelected, cellHasFocus);
                setText(value == null ? "-- Chọn chức vụ --" : ((entity.ChucVu) value).getTenChucVu());
                return this;
            }
        });
        cboChucVu.setPreferredSize(new Dimension(250, 38));
        cboChucVu.setMaximumSize(new Dimension(250, 38));

        JCheckBox chkTrangThai = new JCheckBox("Đang làm việc");
        chkTrangThai.setSelected(true);
        chkTrangThai.setOpaque(false);
        chkTrangThai.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));

        // ===== ROW 1: 2 CỘT =====
        JPanel pnlRow1 = new JPanel();
        pnlRow1.setLayout(new BoxLayout(pnlRow1, BoxLayout.X_AXIS));
        pnlRow1.setOpaque(false);
        pnlRow1.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Trái — Thông tin cá nhân
        RoundedPanel pnlLeft = new RoundedPanel(300, 260, 15);
        pnlLeft.setLayout(new BoxLayout(pnlLeft, BoxLayout.Y_AXIS));
        pnlLeft.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlLeft.setBackground(Colors.SECONDARY);
        pnlLeft.setMaximumSize(new Dimension(300, 300));

        JLabel lblPersonal = new JLabel("THÔNG TIN CÁ NHÂN");
        lblPersonal.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblPersonal.setForeground(new Color(100, 110, 120));
        lblPersonal.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlLeft.add(lblPersonal);
        pnlLeft.add(Box.createVerticalStrut(12));
        pnlLeft.add(createFormField("Mã nhân viên (tự động)", txtMaNV));
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(createFormField("Họ và tên *", txtTenNV));
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(createFormField("Giới tính *", pnlGioiTinh));
        pnlLeft.add(Box.createVerticalStrut(10));
        pnlLeft.add(createFormField("CCCD *", txtCCCD));

        // Phải — Thông tin liên hệ
        RoundedPanel pnlRight = new RoundedPanel(300, 260, 15);
        pnlRight.setLayout(new BoxLayout(pnlRight, BoxLayout.Y_AXIS));
        pnlRight.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlRight.setBackground(Colors.SECONDARY);
        pnlRight.setMaximumSize(new Dimension(300, 300));

        JLabel lblContact = new JLabel("THÔNG TIN LIÊN HỆ");
        lblContact.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblContact.setForeground(new Color(100, 110, 120));
        lblContact.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlRight.add(lblContact);
        pnlRight.add(Box.createVerticalStrut(12));
        pnlRight.add(createFormField("Số điện thoại *", txtSDT));
        pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(createFormField("Email", txtEmail2));
        pnlRight.add(Box.createVerticalStrut(10));
        pnlRight.add(createFormField("Địa chỉ", txtDiaChi));

        pnlRow1.add(pnlLeft);
        pnlRow1.add(Box.createHorizontalStrut(15));
        pnlRow1.add(pnlRight);
        pnlMain.add(pnlRow1);
        pnlMain.add(Box.createVerticalStrut(15));

        // ===== ROW 2: FULL WIDTH — THÔNG TIN CÔNG VIỆC =====
        RoundedPanel pnlWork = new RoundedPanel(615, 120, 15);
        pnlWork.setLayout(new BoxLayout(pnlWork, BoxLayout.Y_AXIS));
        pnlWork.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        pnlWork.setBackground(Colors.SECONDARY);
        pnlWork.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlWork.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));

        JLabel lblWork = new JLabel("THÔNG TIN CÔNG VIỆC");
        lblWork.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblWork.setForeground(new Color(100, 110, 120));
        lblWork.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlWork.add(lblWork);
        pnlWork.add(Box.createVerticalStrut(12));

        JPanel pnlWorkRow = new JPanel();
        pnlWorkRow.setLayout(new BoxLayout(pnlWorkRow, BoxLayout.X_AXIS));
        pnlWorkRow.setOpaque(false);
        pnlWorkRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlWorkRow.add(createFormField("Chức vụ", cboChucVu));
        pnlWorkRow.add(Box.createHorizontalStrut(30));

        JPanel pnlTrangThai = new JPanel();
        pnlTrangThai.setLayout(new BoxLayout(pnlTrangThai, BoxLayout.Y_AXIS));
        pnlTrangThai.setOpaque(false);
        JLabel lblTT = new JLabel("Trạng thái");
        lblTT.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblTT.setForeground(Colors.TEXT_SECONDARY);
        lblTT.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTrangThai.add(lblTT);
        pnlTrangThai.add(Box.createVerticalStrut(8));
        pnlTrangThai.add(chkTrangThai);
        pnlWorkRow.add(pnlTrangThai);

        pnlWork.add(pnlWorkRow);
        pnlMain.add(pnlWork);
        pnlMain.add(Box.createVerticalStrut(20));

        // ===== FOOTER =====
        JPanel pnlFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFooter.setOpaque(false);
        pnlFooter.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton btnHuy = new RoundedButton(100, 40, 15, "Hủy", Colors.SECONDARY);
        JButton btnThem = new RoundedButton(160, 40, 15, "Thêm nhân viên", Colors.PRIMARY);

        btnHuy.addActionListener(ev -> dialog.dispose());
        btnThem.addActionListener(ev -> {
            try {
                entity.ChucVu selectedCV = (entity.ChucVu) cboChucVu.getSelectedItem();
                NhanVien nv = new NhanVien(
                        txtMaNV.getText().trim(),
                        txtTenNV.getText().trim(),
                        rdoNam.isSelected(),
                        txtSDT.getText().trim(),
                        txtDiaChi.getText().trim(),
                        txtEmail2.getText().trim(),
                        txtCCCD.getText().trim(),
                        selectedCV,
                        null,
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
                JOptionPane.showMessageDialog(dialog,
                        ex.getMessage(), "Dữ liệu không hợp lệ",
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        pnlFooter.add(btnHuy);
        pnlFooter.add(btnThem);
        pnlMain.add(pnlFooter);

        JScrollPane scroll = new JScrollPane(pnlMain);
        scroll.setBorder(null);
        dialog.add(scroll);
        dialog.setVisible(true);
    }

    // Tạo 1 hàng label + field dọc dùng trong form thêm/sửa
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

    // Tự sinh mã nhân viên dạng NV<năm><số thứ tự 3 chữ số>
    private String taoMaNhanVien() {
        int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        int maxNum = 0;
        for (NhanVien nv : list) {
            try {
                String ma = nv.getMaNhanVien();
                int num = Integer.parseInt(ma.substring(ma.length() - 3));
                if (num > maxNum) {
                    maxNum = num;
                }
            } catch (Exception ignored) {
            }
        }
        return String.format("NV%d%03d", year, maxNum + 1);
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
