package gui;

import constants.Colors;
import constants.FontStyle;
import entity.LoaiSanPham;
import entity.Thue;
import exception.FormValidator;
import exception.RoundedButton;
import exception.RoundedPanel;
import exception.RoundedTextField;
import exception.StyledTable;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.*;

import service.LoaiSanPham_Service;
import service.Thue_Service;
import service.Validators;

// Quản lý thuế. Áp dụng thuế theo Loại sản phẩm (batch update SanPham).
public class Thue_GUI extends JPanel implements ActionListener {

    private JPanel pnlTitle, pnlContent, pnlCategory;
    private JLabel lblTitle, lblNote;
    private JPanel pnlHead, pnlButtonAdd;
    private RoundedButton btnAddThue;
    private JPanel pnlSearch;
    private RoundedTextField txtSearch;
    private RoundedButton btnFind, btnAll;
    private JLabel lblDSThue;
    private ArrayList<Thue> list;
    private ArrayList<Thue> fullList = new ArrayList<>();
    private Thue_Service thueSV;
    private LoaiSanPham_Service loaiSV;
    private StyledTable tblThue;
    private Map<String, Integer> spCountMap = new java.util.HashMap<>();

    private static final String[] COLUMN_NAMES = {"Mức thuế", "Phần trăm", "Mô tả", "Loại SP áp dụng", "Áp dụng", "", ""};

    public Thue_GUI() {
        list = new ArrayList<>();
        thueSV = new Thue_Service();
        loaiSV = new LoaiSanPham_Service();

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
        pnlTitle.add(lblTitle = new JLabel("Thuế"));
        lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.FOREGROUND);
        pnlTitle.add(lblNote = new JLabel("Quản lý mức thuế và áp dụng theo loại sản phẩm"));
        lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNote.setForeground(Colors.MUTED);

        pnlButtonAdd = new JPanel();
        pnlButtonAdd.setBackground(Colors.BACKGROUND);
        pnlButtonAdd.add(btnAddThue = new RoundedButton(170, 40, 10, "+ Thêm thuế", Colors.PRIMARY));

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
        pnlCategory = new JPanel(new GridLayout(1, 3, 16, 0));
        pnlCategory.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        pnlCategory.setBackground(Colors.BACKGROUND);
        pnlCategory.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));
        pnlCategory.setPreferredSize(new Dimension(0, 110));
        try {
            capNhatStat();
        } catch (Exception e) {
            System.err.println("[Thue_GUI] Không tải được thống kê: " + e.getMessage());
            pnlCategory.add(statCard("Tổng mức thuế", 0, Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
            pnlCategory.add(statCard("Đang áp dụng", 0, Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
            pnlCategory.add(statCard("SP gắn thuế", 0, Colors.BROWN_HOVER, Colors.ACCENT, Colors.ACCENT));
        }
        return pnlCategory;
    }

    // ===== KHỐI: THANH TÌM KIẾM =====
    private JPanel taoSearchBar() {
        pnlSearch = new JPanel();
        pnlSearch.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        pnlSearch.setBackground(Colors.BACKGROUND);
        pnlSearch.add(lblDSThue = new JLabel("Danh sách thuế"));
        lblDSThue.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblDSThue.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        pnlSearch.add(Box.createHorizontalGlue());
        pnlSearch.add(txtSearch = new RoundedTextField(500, 40, 20, "Tìm kiếm theo mã, tên thuế..."));
        pnlSearch.add(btnFind = new RoundedButton(130, 40, 20, "Tìm kiếm", Colors.PRIMARY));
        pnlSearch.add(btnAll = new RoundedButton(100, 40, 20, "Tất cả", Colors.SECONDARY));
        pnlSearch.setMaximumSize(new Dimension(Integer.MAX_VALUE, 56));
        btnAll.setForeground(Colors.TEXT_PRIMARY);
        return pnlSearch;
    }

    // ===== KHỐI: BẢNG =====
    private StyledTable setupTable() {
        tblThue = new StyledTable(COLUMN_NAMES, list);

        tblThue.setTwoLineColumn(0, 240,
                v -> ((Thue) v).getTenThue(),
                v -> ((Thue) v).getMaThue());

        tblThue.setSingleTextColumn(1, 110,
                v -> String.format("%.1f%%", ((Thue) v).getPhanTramThue()));

        tblThue.setSingleTextColumn(2, 260,
                v -> {
                    String mt = ((Thue) v).getMoTa();
                    return (mt == null || mt.isBlank()) ? "—" : mt;
                });

        tblThue.setColumnRenderer(3, (table, value, isSelected, hasFocus, row, col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
            p.setBackground(isSelected ? Colors.PRIMARY_LIGHT : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
            int cnt = value instanceof Thue ? spCountMap.getOrDefault(((Thue) value).getMaThue(), 0) : 0;
            JLabel lbl = new JLabel(cnt + " sản phẩm");
            lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            lbl.setForeground(cnt > 0 ? Colors.SUCCESS_DARK : Colors.MUTED);
            lbl.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
            p.add(lbl);
            return p;
        });
        tblThue.setColumnWidth(3, 140);

        tblThue.setColumnRenderer(4, (table, value, isSelected, hasFocus, row, col) -> {
            JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            p.setBackground(isSelected ? Colors.PRIMARY_LIGHT : (row % 2 == 0 ? Colors.BACKGROUND : new Color(0xFAFAFA)));
            JLabel lbl = new JLabel("Áp dụng");
            lbl.setOpaque(true);
            lbl.setBackground(Colors.PRIMARY_LIGHT);
            lbl.setForeground(Colors.PRIMARY);
            lbl.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
            lbl.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            p.add(lbl);
            return p;
        });
        tblThue.setColumnWidth(4, 110);

        tblThue.setActionColumn(5, 80);
        tblThue.setDeleteButtonColumn(6, 80);

        tblThue.setActionColumnListener((row, obj) -> moDialogSuaThue((Thue) obj));
        tblThue.setDeleteColumnListener((row, obj) -> {
            Thue t = (Thue) obj;
            int cnt = spCountMap.getOrDefault(t.getMaThue(), 0);
            String warn = cnt > 0 ? "\nMức thuế đang áp dụng cho " + cnt + " sản phẩm." : "";
            int kq = JOptionPane.showConfirmDialog(this,
                    "Xóa thuế \"" + t.getTenThue() + "\"?" + warn,
                    "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (kq == JOptionPane.YES_OPTION) {
                if (thueSV.xoaThue(t.getMaThue())) {
                    list.remove(t);
                    fullList.remove(t);
                    refreshSpCount();
                    tblThue.refresh();
                    capNhatStat();
                } else {
                    JOptionPane.showMessageDialog(this, "Không thể xóa (có ràng buộc).", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Click "Áp dụng" col 4
        tblThue.getTable().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int col = tblThue.getTable().columnAtPoint(e.getPoint());
                int row = tblThue.getTable().rowAtPoint(e.getPoint());
                if (col == 4 && row >= 0 && row < list.size()) {
                    moDialogApDungThue(list.get(row));
                }
            }
        });

        return tblThue;
    }

    // ===== KHỐI: SỰ KIỆN =====
    private void wireEvents() {
        btnAddThue.addActionListener(this);
        btnFind.addActionListener(this);
        btnAll.addActionListener(this);
        txtSearch.addActionListener(e -> search());
    }

    // ===== KHỐI: TẢI DỮ LIỆU =====
    public void refresh() {
        loadDataSafe();
    }

    private void loadDataSafe() {
        try {
            loadData(thueSV.getDSThue());
        } catch (Exception e) {
            System.err.println("[Thue_GUI] Lỗi khi tải dữ liệu:");
            e.printStackTrace();
        }
    }

    public void loadData(ArrayList<Thue> ds) {
        fullList = new ArrayList<>(ds);
        list.clear();
        list.addAll(ds);
        refreshSpCount();
        tblThue.refresh();
    }

    private void refreshSpCount() {
        spCountMap = thueSV.getDemSanPhamTheoThue();
    }

    private void search() {
        String kw = txtSearch.getText().trim();
        ArrayList<Thue> filtered = thueSV.timKiem(fullList, kw);
        list.clear();
        list.addAll(filtered);
        tblThue.refresh();
    }

    private void capNhatStat() {
        pnlCategory.removeAll();
        int tong = thueSV.getSoLuongThue();
        int dangApDung = (int) spCountMap.values().stream().filter(v -> v > 0).count();
        int tongSP = spCountMap.values().stream().mapToInt(Integer::intValue).sum();
        pnlCategory.add(statCard("Tổng mức thuế", tong, Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
        pnlCategory.add(statCard("Đang áp dụng", dangApDung, Colors.SUCCESS_LIGHT, Colors.SUCCESS_DARK, Colors.SUCCESS));
        pnlCategory.add(statCard("SP gắn thuế", tongSP, Colors.BROWN_HOVER, Colors.ACCENT, Colors.ACCENT));
        pnlCategory.revalidate();
        pnlCategory.repaint();
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

    // ========== DIALOG THÊM ==========
    private void moDialogThemThue() {
        moDialogThueForm(null);
    }

    private void moDialogSuaThue(Thue cu) {
        moDialogThueForm(cu);
    }

    private void moDialogThueForm(Thue cu) {
        boolean isEdit = cu != null;
        JDialog dialog = new JDialog((java.awt.Frame) null, isEdit ? "Sửa thuế" : "Thêm thuế mới", true);
        dialog.setSize(520, 420);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel pnlMain = new JPanel(new BorderLayout());
        pnlMain.setBackground(Colors.BACKGROUND);
        pnlMain.setBorder(BorderFactory.createEmptyBorder(25, 25, 20, 25));

        JLabel lblFormTitle = new JLabel(isEdit ? "Sửa thuế" : "Thêm thuế mới");
        lblFormTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblFormTitle.setForeground(Colors.TEXT_PRIMARY);
        pnlMain.add(lblFormTitle, BorderLayout.NORTH);

        RoundedTextField txtMa = new RoundedTextField(400, 35, 10, "");
        txtMa.setText(isEdit ? cu.getMaThue() : thueSV.sinhMaThue());
        txtMa.setEnabled(false);
        RoundedTextField txtTen = new RoundedTextField(400, 35, 10, "VD: VAT 10%");
        if (isEdit) txtTen.setText(cu.getTenThue());
        RoundedTextField txtPct = new RoundedTextField(400, 35, 10, "0 - 100");
        if (isEdit) txtPct.setText(String.valueOf(cu.getPhanTramThue()));
        RoundedTextField txtMoTa = new RoundedTextField(400, 35, 10, "Mô tả (tùy chọn)");
        if (isEdit && cu.getMoTa() != null) txtMoTa.setText(cu.getMoTa());

        JLabel errTen = FormValidator.errorLabel();
        JLabel errPct = FormValidator.errorLabel();

        RoundedPanel form = new RoundedPanel(440, 280, 15);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        form.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        form.setBackground(Colors.SECONDARY);
        form.add(createFormField("Mã thuế (tự động)", txtMa));
        form.add(Box.createVerticalStrut(10));
        form.add(FormValidator.fieldWithError("Tên thuế *", txtTen, errTen));
        form.add(Box.createVerticalStrut(8));
        form.add(FormValidator.fieldWithError("Phần trăm thuế (%) *", txtPct, errPct));
        form.add(Box.createVerticalStrut(10));
        form.add(createFormField("Mô tả", txtMoTa));
        pnlMain.add(form, BorderLayout.CENTER);

        FormValidator fv = new FormValidator()
                .add(txtTen, errTen, Validators::required)
                .add(txtPct, errPct, Validators::phanTram);

        JPanel pnlBtns = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        pnlBtns.setOpaque(false);
        JButton btnHuy = new RoundedButton(100, 40, 15, "Hủy", Colors.SECONDARY);
        btnHuy.setForeground(Colors.TEXT_PRIMARY);
        JButton btnLuu = new RoundedButton(150, 40, 15, isEdit ? "Lưu" : "Thêm thuế", Colors.PRIMARY);
        btnHuy.addActionListener(ev -> dialog.dispose());
        btnLuu.addActionListener(ev -> {
            if (!fv.validateAll()) return;
            try {
                double pct = Double.parseDouble(txtPct.getText().trim());
                String moTa = txtMoTa.getText().trim();
                Thue t = isEdit
                        ? new Thue(cu.getMaThue(), txtTen.getText().trim(), pct, moTa)
                        : new Thue(txtMa.getText().trim(), txtTen.getText().trim(), pct, moTa);
                boolean ok = isEdit ? thueSV.capNhatThue(t) : thueSV.themThue(t);
                if (ok) {
                    if (isEdit) {
                        int idx = list.indexOf(cu);
                        if (idx >= 0) list.set(idx, t);
                        idx = fullList.indexOf(cu);
                        if (idx >= 0) fullList.set(idx, t);
                    } else {
                        list.add(t);
                        fullList.add(t);
                    }
                    tblThue.refresh();
                    capNhatStat();
                    JOptionPane.showMessageDialog(dialog, "Lưu thành công!", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Lưu thất bại.", "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        });
        pnlBtns.add(btnHuy);
        pnlBtns.add(btnLuu);
        pnlMain.add(pnlBtns, BorderLayout.SOUTH);

        dialog.add(pnlMain);
        dialog.setVisible(true);
    }

    // ========== DIALOG ÁP DỤNG THUẾ THEO LOẠI SP ==========
    private void moDialogApDungThue(Thue thue) {
        JDialog dialog = new JDialog((java.awt.Frame) null,
                "Áp dụng thuế: " + thue.getTenThue(), true);
        dialog.setSize(560, 560);
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel main = new JPanel(new BorderLayout(0, 10));
        main.setBackground(Colors.BACKGROUND);
        main.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Header
        JPanel head = new JPanel();
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.setOpaque(false);
        JLabel lblT = new JLabel(thue.getTenThue() + "  (" + String.format("%.1f%%", thue.getPhanTramThue()) + ")");
        lblT.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblT.setForeground(Colors.TEXT_PRIMARY);
        JLabel lblS = new JLabel("Chọn các loại sản phẩm để áp dụng mức thuế này.");
        lblS.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblS.setForeground(Colors.TEXT_SECONDARY);
        head.add(lblT);
        head.add(lblS);
        main.add(head, BorderLayout.NORTH);

        // Danh sách loại SP
        List<LoaiSanPham> dsLoai = loaiSV.layDanhSachLoaiSanPham();
        Set<String> dangApDung = new HashSet<>(thueSV.layMaLoaiDangApDung(thue.getMaThue()));

        DefaultListModel<LoaiSanPham> model = new DefaultListModel<>();
        for (LoaiSanPham l : dsLoai) model.addElement(l);

        JList<LoaiSanPham> lst = new JList<>(model);
        lst.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        lst.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lst.setFixedCellHeight(34);
        lst.setCellRenderer((list1, value, index, isSel, focus) -> {
            JPanel p = new JPanel(new BorderLayout());
            p.setBackground(isSel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
            p.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
            JLabel lb = new JLabel(value.getTenLoaiSanPham());
            lb.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            lb.setForeground(Colors.TEXT_PRIMARY);
            p.add(lb, BorderLayout.WEST);
            if (dangApDung.contains(value.getMaLoaiSanPham())) {
                JLabel tag = new JLabel("đang áp dụng");
                tag.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
                tag.setForeground(Colors.SUCCESS_DARK);
                p.add(tag, BorderLayout.EAST);
            }
            return p;
        });

        // Pre-select các loại đang áp dụng
        ArrayList<Integer> selIdx = new ArrayList<>();
        for (int i = 0; i < dsLoai.size(); i++) {
            if (dangApDung.contains(dsLoai.get(i).getMaLoaiSanPham())) selIdx.add(i);
        }
        int[] sel = selIdx.stream().mapToInt(Integer::intValue).toArray();
        lst.setSelectedIndices(sel);

        JScrollPane sc = new JScrollPane(lst);
        sc.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        main.add(sc, BorderLayout.CENTER);

        // Footer
        JPanel foot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        foot.setOpaque(false);
        JButton btnGo = new RoundedButton(120, 38, 15, "Gỡ tất cả", Colors.SECONDARY);
        btnGo.setForeground(Colors.DANGER);
        JButton btnHuy = new RoundedButton(100, 38, 15, "Hủy", Colors.SECONDARY);
        btnHuy.setForeground(Colors.TEXT_PRIMARY);
        JButton btnAp = new RoundedButton(150, 38, 15, "Áp dụng", Colors.PRIMARY);

        btnHuy.addActionListener(ev -> dialog.dispose());

        btnGo.addActionListener(ev -> {
            if (dangApDung.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Không có loại SP nào đang áp dụng.", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            int kq = JOptionPane.showConfirmDialog(dialog,
                    "Gỡ thuế khỏi " + dangApDung.size() + " loại SP đang áp dụng?",
                    "Xác nhận", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (kq == JOptionPane.YES_OPTION) {
                int n = thueSV.apDungChoLoaiSanPham(null, new ArrayList<>(dangApDung));
                JOptionPane.showMessageDialog(dialog, "Đã gỡ thuế khỏi " + n + " sản phẩm.", "Thành công", JOptionPane.INFORMATION_MESSAGE);
                refreshSpCount();
                tblThue.refresh();
                capNhatStat();
                dialog.dispose();
            }
        });

        btnAp.addActionListener(ev -> {
            List<LoaiSanPham> chon = lst.getSelectedValuesList();
            // Tính diff: xoá các loại đã bỏ chọn, thêm các loại mới chọn
            Set<String> moiChon = new HashSet<>();
            for (LoaiSanPham l : chon) moiChon.add(l.getMaLoaiSanPham());

            List<String> canGo = new ArrayList<>();
            for (String ma : dangApDung) if (!moiChon.contains(ma)) canGo.add(ma);
            List<String> canThem = new ArrayList<>();
            for (String ma : moiChon) if (!dangApDung.contains(ma)) canThem.add(ma);

            if (canGo.isEmpty() && canThem.isEmpty()) {
                JOptionPane.showMessageDialog(dialog,
                        "Không có thay đổi nào để cập nhật.",
                        "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int spGo = canGo.isEmpty() ? 0 : thueSV.apDungChoLoaiSanPham(null, canGo);
            int spThem = canThem.isEmpty() ? 0 : thueSV.apDungChoLoaiSanPham(thue.getMaThue(), canThem);

            JOptionPane.showMessageDialog(dialog,
                    "Đã áp dụng thuế cho " + spThem + " sản phẩm mới.\n"
                    + "Đã gỡ thuế khỏi " + spGo + " sản phẩm cũ.",
                    "Hoàn tất", JOptionPane.INFORMATION_MESSAGE);

            refreshSpCount();
            tblThue.refresh();
            capNhatStat();
            dialog.dispose();
        });

        foot.add(btnGo);
        foot.add(btnHuy);
        foot.add(btnAp);
        main.add(foot, BorderLayout.SOUTH);

        dialog.add(main);
        dialog.setVisible(true);
    }

    private JPanel createFormField(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel lbl = new JLabel(labelText);
        lbl.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lbl.setForeground(Colors.TEXT_SECONDARY);
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(4));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(field);
        return p;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if (src == btnAddThue) moDialogThemThue();
        else if (src == btnFind) search();
        else if (src == btnAll) {
            txtSearch.setText("");
            list.clear();
            list.addAll(fullList);
            tblThue.refresh();
        }
    }
}
