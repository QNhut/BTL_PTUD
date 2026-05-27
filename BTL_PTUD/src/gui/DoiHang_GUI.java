package gui;

import constants.Colors;
import constants.FontStyle;
import dao.HoaDon_DAO;
import dao.LoaiSanPham_DAO;
import dao.SanPham_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.SanPham;
import exception.RoundedButton;
import exception.RoundedPanel;
import exception.RoundedToggleButton;

import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

@SuppressWarnings("serial")
public class DoiHang_GUI extends JPanel {

    // ===DAOs & Services===
    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private final SanPham_DAO sanPhamDAO = new SanPham_DAO();
    private final LoaiSanPham_DAO loaiSanPhamDAO = new LoaiSanPham_DAO();
    private final dao.LoSanPham_DAO loSanPhamDAO = new dao.LoSanPham_DAO();
    private final service.DoiHang_Service doiHangService = new service.DoiHang_Service();

    // ===Dữ liệu nghiệp vụ===
    private List<SanPham> allProducts = new ArrayList<>();
    private List<ReturnItem> currentInvoiceItems = new ArrayList<>();
    private List<OrderItem> newOrderItems = new ArrayList<>();

    // ===UI Components===
    private JTextField txtSearchMaHD;
    private JTable tblOldOrder, tblNewOrder;
    private DefaultTableModel modelOldOrder, modelNewOrder;
    private JTextArea txtNote;
    private JLabel lblOldTotal, lblNewTotal, lblDiff;
    private RoundedToggleButton btnModeList, btnModeManual;
    private JPanel pnlReasonListPanel, pnlReasonManualPanel;
    private JComboBox<String> cbReasonPreset;
    private JTextField txtReasonCustom;
    private JComboBox<entity.PhuongThucThanhToan> cbPhuongThuc;
    private JPanel pnlPhuongThucRow;
    private final dao.PhuongThucThanhToan_DAO ptttDAO = new dao.PhuongThucThanhToan_DAO();

    // ===Trạng thái===
    private final DecimalFormat df = new DecimalFormat("#,###");
    private double oldOrderTotal = 0;
    private double newOrderTotal = 0;

    public DoiHang_GUI() {
        setLayout(new BorderLayout());
        setBackground(Colors.BACKGROUND);
        loadData();
        initComponents();
    }

    // ===Tải danh sách sản phẩm từ DB===
    private void loadData() {
        try {
            allProducts = sanPhamDAO.getDSSanPham();
            loaiSanPhamDAO.getDSLoaiSanPham();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ===Dựng layout tổng thể: header + 2 cột===
    private void initComponents() {
        add(createHeader(), BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(new EmptyBorder(20, 20, 20, 20));
        pnlCenter.add(createLeftPanel());
        pnlCenter.add(createRightPanel());
        add(pnlCenter, BorderLayout.CENTER);
    }

    // ===Header tiêu đề màn hình===
    private JPanel createHeader() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setBackground(Colors.BACKGROUND);
        pnl.setBorder(new EmptyBorder(20, 25, 0, 25));

        JLabel lblTitle = new JLabel("Đổi hàng");
        lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.FOREGROUND);

        JLabel lblSub = new JLabel("Tìm hóa đơn cũ để chọn sản phẩm cần đổi và chọn sản phẩm mới từ tồn kho");
        lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSub.setForeground(Colors.MUTED);

        pnl.add(lblTitle);
        pnl.add(lblSub);
        return pnl;
    }

    // ===Panel trái: ô tìm kiếm + bảng SP cần đổi + chế độ nhập lý do + ghi chú===
    private JPanel createLeftPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 15));
        pnl.setOpaque(false);
        pnl.add(createSearchSection(), BorderLayout.NORTH);
        pnl.add(createOldOrderSection(), BorderLayout.CENTER);
        return pnl;
    }

    // ===Card tìm kiếm hóa đơn theo mã===
    private JPanel createSearchSection() {
        RoundedPanel pnl = new RoundedPanel(0, 130, 12);
        pnl.setBackground(Color.WHITE);
        pnl.setLayout(new BorderLayout(0, 8));
        pnl.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel lblTitle = new JLabel("Đơn Hàng Cũ");
        lblTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblTitle.setForeground(Colors.TEXT_PRIMARY);

        JLabel lblHint = new JLabel("Nhập mã hóa đơn để tìm kiếm");
        lblHint.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblHint.setForeground(Colors.TEXT_SECONDARY);

        txtSearchMaHD = new JTextField();
        txtSearchMaHD.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        txtSearchMaHD.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.INPUT_NORMAL_BORDER, 1, true),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtSearchMaHD.addActionListener(e -> searchInvoice());

        RoundedButton btnSearch = new RoundedButton(100, 45, 8, "Tìm", Colors.PRIMARY);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> searchInvoice());

        JPanel pnlField = new JPanel(new BorderLayout(8, 0));
        pnlField.setOpaque(false);
        pnlField.add(txtSearchMaHD, BorderLayout.CENTER);
        pnlField.add(btnSearch, BorderLayout.EAST);

        JPanel pnlBody = new JPanel(new BorderLayout(0, 5));
        pnlBody.setOpaque(false);
        pnlBody.add(lblHint, BorderLayout.NORTH);
        pnlBody.add(pnlField, BorderLayout.CENTER);

        pnl.add(lblTitle, BorderLayout.NORTH);
        pnl.add(pnlBody, BorderLayout.CENTER);
        return pnl;
    }

    // ===Card bảng SP cần đổi + khu vực nhập lý do + ghi chú===
    private JPanel createOldOrderSection() {
        RoundedPanel pnl = new RoundedPanel(0, 0, 12);
        pnl.setBackground(Color.WHITE);
        pnl.setLayout(new BorderLayout(0, 10));
        pnl.setBorder(new EmptyBorder(18, 20, 18, 20));

        // Header: tiêu đề + nút chọn số lượng
        JPanel pnlHeader = new JPanel(new BorderLayout(0, 6));
        pnlHeader.setOpaque(false);
        pnlHeader.setBorder(new EmptyBorder(0, 0, 8, 0));

        JLabel lblTitle = new JLabel("Danh sách sản phẩm trong hóa đơn");
        lblTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblTitle.setForeground(Colors.TEXT_PRIMARY);

        JPanel pnlBtnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlBtnRow.setOpaque(false);
        RoundedButton btnSelectQty = new RoundedButton(160, 32, 6, "Chọn SL Đổi", Colors.PRIMARY);
        btnSelectQty.setForeground(Color.WHITE);
        btnSelectQty.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnSelectQty.addActionListener(e -> selectReturnQuantity());
        pnlBtnRow.add(btnSelectQty);

        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        pnlHeader.add(pnlBtnRow, BorderLayout.SOUTH);
        pnl.add(pnlHeader, BorderLayout.NORTH);

        // Bảng SP cũ: Mã SP | Tên SP | SL Đổi
        String[] cols = {"Mã SP", "Tên SP", "SL Đổi"};
        modelOldOrder = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        tblOldOrder = new JTable(modelOldOrder);
        tblOldOrder.setRowHeight(32);
        tblOldOrder.getTableHeader().setBackground(Colors.SECONDARY);
        tblOldOrder.getTableHeader().setForeground(Colors.TEXT_PRIMARY);
        tblOldOrder.setGridColor(Colors.BORDER);
        tblOldOrder.setSelectionBackground(Colors.PRIMARY_LIGHT);
        tblOldOrder.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));

        // Click hàng → mở dialog chọn SP thay thế
        tblOldOrder.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblOldOrder.getSelectedRow();
                if (row >= 0) {
                    onOldOrderRowClicked(row);
                }
            }
        });

        JScrollPane scroll = new JScrollPane(tblOldOrder);
        scroll.setPreferredSize(new Dimension(0, 140));
        scroll.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        pnl.add(scroll, BorderLayout.CENTER);
        pnl.add(createInputModeSection(), BorderLayout.SOUTH);
        return pnl;
    }

    // ===Khu vực chế độ nhập lý do đổi hàng + ghi chú===
    private JPanel createInputModeSection() {
        JPanel pnl = new JPanel();
        pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
        pnl.setOpaque(false);

        // --- Hàng "Chế độ nhập:" + 2 toggle tab-buttons ---
        JPanel pnlToggleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlToggleRow.setOpaque(false);
        pnlToggleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlToggleRow.setBorder(new EmptyBorder(10, 0, 8, 0));

        JLabel lblMode = new JLabel("Chế độ nhập:  ");
        lblMode.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblMode.setForeground(Colors.TEXT_SECONDARY);

        ButtonGroup modeGroup = new ButtonGroup();
        btnModeList = new RoundedToggleButton(190, 34, 12, "≡ Chọn từ danh sách", Colors.PRIMARY);
        btnModeManual = new RoundedToggleButton(160, 34, 12, "Nhập thủ công", Colors.PRIMARY);
        btnModeList.setSelected(true);
        modeGroup.add(btnModeList);
        modeGroup.add(btnModeManual);
        btnModeList.addActionListener(e -> switchMode(false));
        btnModeManual.addActionListener(e -> switchMode(true));

        pnlToggleRow.add(lblMode);
        pnlToggleRow.add(btnModeList);
        pnlToggleRow.add(Box.createHorizontalStrut(6));
        pnlToggleRow.add(btnModeManual);
        pnl.add(pnlToggleRow);

        // --- Label "Lý do đổi hàng *" ---
        JLabel lblReasonTitle = new JLabel("Lý do đổi hàng *");
        lblReasonTitle.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblReasonTitle.setForeground(Colors.TEXT_PRIMARY);
        lblReasonTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnl.add(lblReasonTitle);
        pnl.add(Box.createVerticalStrut(4));

        // --- Panel mode 1: JComboBox danh sách lý do (hiện mặc định) ---
        pnlReasonListPanel = new JPanel(new BorderLayout());
        pnlReasonListPanel.setOpaque(false);
        pnlReasonListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        cbReasonPreset = new JComboBox<>(new String[]{
            "Chọn lý do đổi hàng...",
            "Sản phẩm lỗi kỹ thuật",
            "Sai kích thước / màu sắc",
            "Khách đổi ý",
            "Sản phẩm không giống mô tả",
            "Sản phẩm hết hạn / hỏng",
            "Sản phẩm thiếu phụ kiện",
            "Khác"
        });
        cbReasonPreset.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        cbReasonPreset.setBackground(Color.WHITE);
        pnlReasonListPanel.add(cbReasonPreset, BorderLayout.CENTER);
        pnl.add(pnlReasonListPanel);

        // --- Panel mode 2: JTextField nhập thủ công (ẩn mặc định) ---
        pnlReasonManualPanel = new JPanel(new BorderLayout());
        pnlReasonManualPanel.setOpaque(false);
        pnlReasonManualPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlReasonManualPanel.setVisible(false);

        txtReasonCustom = new JTextField();
        txtReasonCustom.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        txtReasonCustom.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.INPUT_NORMAL_BORDER, 1, true),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
        txtReasonCustom.setForeground(Colors.TEXT_SECONDARY);
        txtReasonCustom.setText("Nhập lý do đổi hàng...");
        txtReasonCustom.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtReasonCustom.getText().equals("Nhập lý do đổi hàng...")) {
                    txtReasonCustom.setText("");
                    txtReasonCustom.setForeground(Colors.TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtReasonCustom.getText().isEmpty()) {
                    txtReasonCustom.setText("Nhập lý do đổi hàng...");
                    txtReasonCustom.setForeground(Colors.TEXT_SECONDARY);
                }
            }
        });
        pnlReasonManualPanel.add(txtReasonCustom, BorderLayout.CENTER);
        pnl.add(pnlReasonManualPanel);

        // --- Textarea ghi chú (luôn hiện) ---
        JPanel pnlNote = new JPanel(new BorderLayout(0, 4));
        pnlNote.setOpaque(false);
        pnlNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlNote.setBorder(new EmptyBorder(12, 0, 0, 0));

        JLabel lblNote = new JLabel("Ghi chú (không bắt buộc)");
        lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNote.setForeground(Colors.TEXT_PRIMARY);

        txtNote = new JTextArea(3, 0);
        txtNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.INPUT_NORMAL_BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
        txtNote.setForeground(Colors.TEXT_SECONDARY);
        txtNote.setText("Nhập ghi chú thêm nếu cần...");
        txtNote.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (txtNote.getText().equals("Nhập ghi chú thêm nếu cần...")) {
                    txtNote.setText("");
                    txtNote.setForeground(Colors.TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (txtNote.getText().isEmpty()) {
                    txtNote.setText("Nhập ghi chú thêm nếu cần...");
                    txtNote.setForeground(Colors.TEXT_SECONDARY);
                }
            }
        });

        pnlNote.add(lblNote, BorderLayout.NORTH);
        pnlNote.add(new JScrollPane(txtNote), BorderLayout.CENTER);
        pnl.add(pnlNote);

        return pnl;
    }

    // ===Panel phải: bảng SP mới + tổng kết + xác nhận===
    private JPanel createRightPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 15));
        pnl.setOpaque(false);
        pnl.add(createSelectedProductsSection(), BorderLayout.CENTER);
        pnl.add(createSummarySection(), BorderLayout.SOUTH);
        return pnl;
    }

    // ===Card bảng SP mới được chọn đổi===
    private JPanel createSelectedProductsSection() {
        RoundedPanel pnl = new RoundedPanel(0, 0, 12);
        pnl.setBackground(Color.WHITE);
        pnl.setLayout(new BorderLayout(0, 10));
        pnl.setBorder(new EmptyBorder(18, 20, 18, 20));

        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setOpaque(false);
        JLabel lblTitle = new JLabel("Sản Phẩm Đã Chọn Đổi");
        lblTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblTitle.setForeground(Colors.TEXT_PRIMARY);
        JLabel lblSub = new JLabel("Danh sách sản phẩm được chọn đổi từ hóa đơn cũ");
        lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSub.setForeground(Colors.TEXT_SECONDARY);
        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        pnlHeader.add(lblSub, BorderLayout.SOUTH);
        pnl.add(pnlHeader, BorderLayout.NORTH);

        // Bảng SP mới: Mã SP | Tên SP | Giá | SL | Tổng (read-only)
        String[] cols = {"Mã SP", "Tên SP", "Giá", "SL", "Tổng"};
        modelNewOrder = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tblNewOrder = new JTable(modelNewOrder);
        tblNewOrder.setRowHeight(32);
        tblNewOrder.getTableHeader().setBackground(Colors.SECONDARY);
        tblNewOrder.getTableHeader().setForeground(Colors.TEXT_PRIMARY);
        tblNewOrder.setGridColor(Colors.BORDER);
        tblNewOrder.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        tblNewOrder.setSelectionBackground(Colors.SUCCESS_LIGHT);

        pnl.add(new JScrollPane(tblNewOrder), BorderLayout.CENTER);

        // Nút Xóa / Xóa tất cả
        JPanel pnlActions = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pnlActions.setOpaque(false);
        pnlActions.setBorder(new EmptyBorder(6, 0, 0, 0));

        RoundedButton btnRemove = new RoundedButton(90, 34, 6, "Xóa", Colors.DANGER);
        RoundedButton btnClear = new RoundedButton(110, 34, 6, "Xóa tất cả", Colors.BORDER);
        btnRemove.setForeground(Color.WHITE);
        btnClear.setForeground(Colors.TEXT_PRIMARY);
        btnRemove.addActionListener(e -> removeSelectedNewItem());
        btnClear.addActionListener(e -> clearNewOrder());

        pnlActions.add(btnRemove);
        pnlActions.add(btnClear);
        pnl.add(pnlActions, BorderLayout.SOUTH);
        return pnl;
    }

    // ===Card tổng kết tiền + nút xác nhận===
    private JPanel createSummarySection() {
        RoundedPanel pnl = new RoundedPanel(0, 200, 12);
        pnl.setBackground(Color.WHITE);
        pnl.setLayout(new BorderLayout(0, 10));
        pnl.setBorder(new EmptyBorder(18, 20, 18, 20));

        JLabel lblTitle = new JLabel("Tổng Kết Đổi Hàng");
        lblTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblTitle.setForeground(Colors.TEXT_PRIMARY);
        pnl.add(lblTitle, BorderLayout.NORTH);

        // 3 hàng tổng kết
        JPanel pnlStats = new JPanel(new GridLayout(3, 1, 0, 8));
        pnlStats.setOpaque(false);
        pnlStats.setBorder(new EmptyBorder(6, 0, 8, 0));

        lblOldTotal = new JLabel();
        lblNewTotal = new JLabel();
        lblDiff = new JLabel();
        lblOldTotal.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNewTotal.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblDiff.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));

        pnlStats.add(wrapLabel(lblOldTotal));
        pnlStats.add(wrapLabel(lblNewTotal));
        pnlStats.add(wrapLabel(lblDiff));
        pnl.add(pnlStats, BorderLayout.CENTER);

        updateSummaryLabels();

        RoundedButton btnConfirm = new RoundedButton(0, 44, 10, "Xác Nhận Đổi Hàng", Colors.PRIMARY);
        setButtonIcon(btnConfirm, "commercial.png", 16, 16);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        btnConfirm.addActionListener(e -> confirmExchange());

        // Hàng chọn hình thức thanh toán (chỉ hiện khi khách cần trả thêm)
        pnlPhuongThucRow = new JPanel(new BorderLayout(0, 4));
        pnlPhuongThucRow.setOpaque(false);
        pnlPhuongThucRow.setBorder(new EmptyBorder(0, 0, 8, 0));
        pnlPhuongThucRow.setVisible(false);

        JLabel lblPTTT = new JLabel("Hình thức thanh toán:");
        lblPTTT.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblPTTT.setForeground(Colors.TEXT_PRIMARY);

        cbPhuongThuc = new JComboBox<>();
        for (entity.PhuongThucThanhToan pt : ptttDAO.getDSPhuongThuc()) {
            cbPhuongThuc.addItem(pt);
        }
        cbPhuongThuc.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel(value != null ? value.getTenPTTT() : "");
            lbl.setOpaque(true);
            lbl.setBackground(isSelected ? Colors.PRIMARY_LIGHT : Color.WHITE);
            lbl.setForeground(Colors.TEXT_PRIMARY);
            lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            return lbl;
        });
        cbPhuongThuc.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        pnlPhuongThucRow.add(lblPTTT, BorderLayout.NORTH);
        pnlPhuongThucRow.add(cbPhuongThuc, BorderLayout.CENTER);

        JPanel pnlSouthWrapper = new JPanel(new BorderLayout(0, 0));
        pnlSouthWrapper.setOpaque(false);
        pnlSouthWrapper.add(pnlPhuongThucRow, BorderLayout.NORTH);
        pnlSouthWrapper.add(btnConfirm, BorderLayout.CENTER);
        pnl.add(pnlSouthWrapper, BorderLayout.SOUTH);
        return pnl;
    }

    // ===Bọc label vào JPanel để fill width===
    private JPanel wrapLabel(JLabel lbl) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.add(lbl, BorderLayout.CENTER);
        return row;
    }

    // ===Tìm kiếm hóa đơn và nạp SP vào bảng trái===
    private void searchInvoice() {
        String ma = txtSearchMaHD.getText().trim();
        if (ma.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập mã hóa đơn!");
            return;
        }

        HoaDon hd = doiHangService.getHoaDonByMa(ma);
        if (hd == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn: " + ma);
            clearOldOrder();
            return;
        }

        String check = doiHangService.checkDieuKienDoiHang(hd);
        if (!check.equals("OK")) {
            JOptionPane.showMessageDialog(this,
                    check,
                    "Không thể đổi hàng", JOptionPane.WARNING_MESSAGE);
            clearOldOrder();
            clearNewOrder();
            return;
        }

        List<ChiTietHoaDon> details = doiHangService.getChiTietHoaDon(ma);
        currentInvoiceItems.clear();
        for (ChiTietHoaDon ct : details) {
            currentInvoiceItems.add(new ReturnItem(ct.getSanPham(), ct.getSoLuong(), ct.getDonGia()));
        }
        refreshOldOrderTable();
    }

    // ===Click vào dòng bảng SP cũ → mở dialog chọn SP thay thế===
    private void onOldOrderRowClicked(int row) {
        if (row < 0 || row >= currentInvoiceItems.size()) {
            return;
        }
        showProductSelectionDialog(currentInvoiceItems.get(row));
    }

    // ===Mở dialog chọn SP thay thế cùng loại từ tồn kho===
    private void showProductSelectionDialog(ReturnItem returnItem) {
        String loaiId = (returnItem.product.getLoaiSP() != null)
                ? returnItem.product.getLoaiSP().getMaLoaiSP() : null;

        List<SanPham> candidates = new ArrayList<>();
        for (SanPham s : allProducts) {
            if (!s.isTrangThai()) {
                continue;
            }
            boolean sameType = loaiId == null || (s.getLoaiSP() != null
                    && s.getLoaiSP().getMaLoaiSP().equalsIgnoreCase(loaiId));
            if (!sameType) {
                continue;
            }
            if (loSanPhamDAO.layTongSoLuongTonTheoMaSanPham(s.getMaSP()) > 0) {
                candidates.add(s);
            }
        }

        if (candidates.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Không có sản phẩm cùng loại còn hàng trong kho!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog(
                (Frame) SwingUtilities.getWindowAncestor(this),
                "Chọn sản phẩm đổi — " + returnItem.product.getTenSP(), true);
        dialog.setSize(560, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout(0, 8));

        String[] cols = {"Mã SP", "Tên SP", "Giá", "Tồn kho"};
        DefaultTableModel mdl = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        for (SanPham s : candidates) {
            int ton = loSanPhamDAO.layTongSoLuongTonTheoMaSanPham(s.getMaSP());
            mdl.addRow(new Object[]{s.getMaSP(), s.getTenSP(), df.format(s.getGiaBan()), ton});
        }

        JTable tbl = new JTable(mdl);
        tbl.setRowHeight(30);
        tbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tbl.getTableHeader().setBackground(Colors.SECONDARY);

        RoundedButton btnAdd = new RoundedButton(120, 36, 6, "Thêm", Colors.PRIMARY);
        setButtonIcon(btnAdd, "open-box.png", 14, 14);
        RoundedButton btnCancel = new RoundedButton(90, 36, 6, "Hủy", Colors.TEXT_SECONDARY);
        btnAdd.setForeground(Color.WHITE);
        btnCancel.setForeground(Color.WHITE);

        btnAdd.addActionListener(e -> {
            int sel = tbl.getSelectedRow();
            if (sel < 0) {
                JOptionPane.showMessageDialog(dialog, "Vui lòng chọn sản phẩm!");
                return;
            }
            String maSP = mdl.getValueAt(sel, 0).toString();
            SanPham chosen = candidates.stream()
                    .filter(s -> s.getMaSP().equalsIgnoreCase(maSP)).findFirst().orElse(null);
            if (chosen != null) {
                // Xóa SP thay thế cũ nếu đã chọn trước đó
                if (returnItem.linkedOrderItem != null) {
                    newOrderItems.remove(returnItem.linkedOrderItem);
                }
                // Giữ returnQty đã đặt qua "Chọn SL Đổi"; nếu chưa đặt thì mặc định toàn bộ
                if (returnItem.returnQty <= 0) {
                    returnItem.returnQty = returnItem.boughtQty;
                }
                OrderItem newItem = new OrderItem(chosen, returnItem.returnQty);
                newOrderItems.add(newItem);
                returnItem.linkedOrderItem = newItem;
                refreshOldOrderTable();
                refreshNewOrderTable();
            }
            dialog.dispose();
        });

        tbl.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    btnAdd.doClick();
                }
            }
        });

        btnCancel.addActionListener(e -> dialog.dispose());

        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        pnlBottom.add(btnAdd);
        pnlBottom.add(btnCancel);

        JLabel lblHint = new JLabel("  Chọn sản phẩm thay thế (double-click để thêm nhanh):");
        lblHint.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        dialog.add(lblHint, BorderLayout.NORTH);
        dialog.add(new JScrollPane(tbl), BorderLayout.CENTER);
        dialog.add(pnlBottom, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ===Chuyển đổi chế độ nhập lý do: danh sách ↔ thủ công===
    private void switchMode(boolean isManual) {
        pnlReasonListPanel.setVisible(!isManual);
        pnlReasonManualPanel.setVisible(isManual);
        revalidate();
        repaint();
    }

    // ===Làm mới bảng SP cũ từ currentInvoiceItems===
    private void refreshOldOrderTable() {
        modelOldOrder.setRowCount(0);
        oldOrderTotal = 0;
        for (ReturnItem i : currentInvoiceItems) {
            oldOrderTotal += i.returnQty * i.price;
            modelOldOrder.addRow(new Object[]{
                i.product.getMaSP(), i.product.getTenSP(), i.returnQty
            });
        }
        updateSummaryLabels();
    }

    // ===Làm mới bảng SP mới từ newOrderItems===
    private void refreshNewOrderTable() {
        modelNewOrder.setRowCount(0);
        newOrderTotal = 0;
        for (OrderItem i : newOrderItems) {
            double total = i.product.getGiaBan() * i.quantity;
            newOrderTotal += total;
            modelNewOrder.addRow(new Object[]{
                i.product.getMaSP(), i.product.getTenSP(),
                df.format(i.product.getGiaBan()), i.quantity, df.format(total)
            });
        }
        updateSummaryLabels();
    }

    // ===Cập nhật 3 nhãn tổng kết với màu và căn chỉnh HTML===
    private void updateSummaryLabels() {
        lblOldTotal.setText("<html><table width='100%'><tr>"
                + "<td>Tổng hàng cũ (trả lại):</td>"
                + "<td align='right'><font color='#F44725'>" + df.format(oldOrderTotal) + " đ</font></td>"
                + "</tr></table></html>");

        lblNewTotal.setText("<html><table width='100%'><tr>"
                + "<td>Tổng hàng đổi:</td>"
                + "<td align='right'><font color='#F44725'>" + df.format(newOrderTotal) + " đ</font></td>"
                + "</tr></table></html>");

        double diff = newOrderTotal - oldOrderTotal;
        String diffLabel, diffColor, diffValue;
        if (diff > 0) {
            diffLabel = "Khách thanh toán thêm:";
            diffColor = "#F44725";
            diffValue = df.format(diff) + " đ";
        } else if (diff < 0) {
            diffLabel = "Cửa hàng hoàn lại:";
            diffColor = "#16A34A";
            diffValue = df.format(Math.abs(diff)) + " đ";
        } else {
            diffLabel = "Khách thanh toán thêm:";
            diffColor = "#16A34A";
            diffValue = "0 đ";
        }
        lblDiff.setText("<html><table width='100%'><tr>"
                + "<td><b>" + diffLabel + "</b></td>"
                + "<td align='right'><b><font color='" + diffColor + "'>" + diffValue + "</font></b></td>"
                + "</tr></table></html>");

        // Hiển thị hình thức thanh toán chỉ khi khách cần trả thêm
        if (pnlPhuongThucRow != null) {
            pnlPhuongThucRow.setVisible(diff > 0);
        }
    }

    // ===Xóa dòng được chọn khỏi đơn hàng mới===
    private void removeSelectedNewItem() {
        int row = tblNewOrder.getSelectedRow();
        if (row >= 0 && row < newOrderItems.size()) {
            OrderItem removed = newOrderItems.remove(row);
            for (ReturnItem ri : currentInvoiceItems) {
                if (ri.linkedOrderItem == removed) {
                    ri.linkedOrderItem = null;
                    break;
                }
            }
            refreshNewOrderTable();
        }
    }

    // ===Xóa toàn bộ đơn hàng mới===
    private void clearNewOrder() {
        newOrderItems.clear();
        for (ReturnItem ri : currentInvoiceItems) {
            ri.linkedOrderItem = null;
        }
        refreshNewOrderTable();
    }

    // ===Chọn số lượng SP cần đổi từ dòng được chọn trong bảng cũ===
    private void selectReturnQuantity() {
        int row = tblOldOrder.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn sản phẩm cần đổi từ bảng!");
            return;
        }
        ReturnItem item = currentInvoiceItems.get(row);
        String input = JOptionPane.showInputDialog(this,
                "Nhập số lượng cần đổi cho: " + item.product.getTenSP()
                + "\n(Tối đa: " + item.boughtQty + ")",
                String.valueOf(item.returnQty));
        if (input == null) {
            return;
        }
        try {
            int qty = Integer.parseInt(input.trim());
            if (qty < 0 || qty > item.boughtQty) {
                JOptionPane.showMessageDialog(this, "Số lượng phải từ 0 đến " + item.boughtQty);
                return;
            }
            item.returnQty = qty;
            // Đồng bộ SL sang bảng SP đã chọn đổi
            if (item.linkedOrderItem != null) {
                if (qty == 0) {
                    newOrderItems.remove(item.linkedOrderItem);
                    item.linkedOrderItem = null;
                } else {
                    item.linkedOrderItem.quantity = qty;
                    refreshNewOrderTable();
                }
            }
            refreshOldOrderTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Số lượng không hợp lệ!");
        }
    }

    // ===Xóa bảng SP cũ===
    private void clearOldOrder() {
        currentInvoiceItems.clear();
        modelOldOrder.setRowCount(0);
        oldOrderTotal = 0;
        updateSummaryLabels();
    }

    // ===Validate và thực hiện giao dịch đổi hàng===
    private void confirmExchange() {
        if (currentInvoiceItems.stream().noneMatch(i -> i.returnQty > 0)) {
            JOptionPane.showMessageDialog(this, "Vui lòng tìm hóa đơn và chọn sản phẩm cần đổi!");
            return;
        }
        if (newOrderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một sản phẩm mới!");
            return;
        }

        // Validate lý do đổi hàng theo chế độ đang chọn
        String lyDo;
        if (btnModeList.isSelected()) {
            if (cbReasonPreset.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn lý do đổi hàng!");
                return;
            }
            lyDo = cbReasonPreset.getSelectedItem().toString();
        } else {
            String custom = txtReasonCustom.getText().trim();
            if (custom.isEmpty() || custom.equals("Nhập lý do đổi hàng...")) {
                JOptionPane.showMessageDialog(this, "Vui lòng nhập lý do đổi hàng!");
                return;
            }
            lyDo = custom;
        }

        // Nếu khách cần thanh toán thêm → bắt buộc chọn hình thức thanh toán
        String maPTTT = null;
        if (newOrderTotal > oldOrderTotal) {
            entity.PhuongThucThanhToan selectedPT = (entity.PhuongThucThanhToan) cbPhuongThuc.getSelectedItem();
            if (selectedPT == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn hình thức thanh toán!");
                return;
            }
            maPTTT = selectedPT.getMaPTTT();
        }

        try {
            List<ChiTietHoaDon> returns = buildReturnList();
            List<ChiTietHoaDon> news = buildNewList();
            String maHD = txtSearchMaHD.getText().trim();

            boolean ok = doiHangService.thucHienGiaoDichDoiHang(maHD, returns, news, lyDo, maPTTT);
            if (ok) {
                HoaDon_GUI.stockDirty = true;
                showPreviewDialog(maPTTT);
                resetForm();
            } else {
                JOptionPane.showMessageDialog(this, "Lỗi khi cập nhật dữ liệu kho.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Lỗi: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ===Tạo danh sách SP cũ cần trả (returnQty > 0)===
    private List<ChiTietHoaDon> buildReturnList() {
        List<ChiTietHoaDon> list = new ArrayList<>();
        for (ReturnItem i : currentInvoiceItems) {
            if (i.returnQty > 0) {
                list.add(new ChiTietHoaDon(null, i.product, i.returnQty, i.price));
            }
        }
        return list;
    }

    // ===Tạo danh sách SP mới được chọn đổi===
    private List<ChiTietHoaDon> buildNewList() {
        List<ChiTietHoaDon> list = new ArrayList<>();
        for (OrderItem i : newOrderItems) {
            list.add(new ChiTietHoaDon(null, i.product, i.quantity, i.product.getGiaBan()));
        }
        return list;
    }

    // ===Hiện dialog preview hóa đơn đổi hàng===
    private void showPreviewDialog(String maPTTT) {
        try {
            String maHD = txtSearchMaHD.getText().trim();
            HoaDon oldHD = hoaDonDAO.layHDTheoMa(maHD);
            String tenKH = (oldHD != null && oldHD.getKhachHang() != null)
                    ? oldHD.getKhachHang().getMaKhachHang() : "Khách hàng";
            String tenNV = (oldHD != null && oldHD.getNhanVien() != null)
                    ? oldHD.getNhanVien().getMaNhanVien() : "";

            String[] cols = {"Tên SP", "SL", "Đơn giá"};
            DefaultTableModel inModel = new DefaultTableModel(cols, 0);
            for (OrderItem item : newOrderItems) {
                inModel.addRow(new Object[]{item.product.getTenSP(), item.quantity, (int) item.product.getGiaBan()});
            }

            String thoiGian = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

            HoaDonPreviewDialog preview;
            if (maPTTT != null) {
                entity.PhuongThucThanhToan selectedPT = (entity.PhuongThucThanhToan) cbPhuongThuc.getSelectedItem();
                String tenPTTT = (selectedPT != null) ? selectedPT.getTenPTTT() : maPTTT;
                long soTienTraThem = Math.round(newOrderTotal - oldOrderTotal);
                preview = new HoaDonPreviewDialog(
                        (javax.swing.JFrame) SwingUtilities.getWindowAncestor(this),
                        tenKH, "", tenNV, thoiGian, inModel, tenPTTT, soTienTraThem);
            } else {
                preview = new HoaDonPreviewDialog(
                        (javax.swing.JFrame) SwingUtilities.getWindowAncestor(this),
                        tenKH, "", tenNV, thoiGian, inModel);
            }
            preview.setVisible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void refresh() {
        resetForm();
    }

    // ===Reset toàn bộ form sau khi đổi hàng thành công===
    private void resetForm() {
        currentInvoiceItems.clear();
        newOrderItems.clear();
        txtSearchMaHD.setText("");
        txtNote.setText("Nhập ghi chú thêm nếu cần...");
        txtNote.setForeground(Colors.TEXT_SECONDARY);
        cbReasonPreset.setSelectedIndex(0);
        txtReasonCustom.setText("Nhập lý do đổi hàng...");
        txtReasonCustom.setForeground(Colors.TEXT_SECONDARY);
        btnModeList.setSelected(true);
        switchMode(false);
        oldOrderTotal = 0;
        newOrderTotal = 0;
        modelOldOrder.setRowCount(0);
        modelNewOrder.setRowCount(0);
        updateSummaryLabels();
        loadData();
    }

    private void setButtonIcon(AbstractButton button, String iconFile, int w, int h) {
        ImageIcon icon = loadUiIcon(iconFile, w, h);
        if (icon != null) {
            button.setIcon(icon);
            button.setIconTextGap(6);
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
        }
    }

    private ImageIcon loadUiIcon(String iconFile, int w, int h) {
        ImageIcon raw = new ImageIcon("data/img/icons/" + iconFile);
        if (raw.getIconWidth() <= 0 || raw.getIconHeight() <= 0) {
            return null;
        }
        Image scaled = raw.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    // ===Model SP cũ trong hóa đơn (cần trả lại)===
    private class ReturnItem {

        SanPham product;
        int boughtQty;
        int returnQty = 0;
        double price;
        OrderItem linkedOrderItem = null;

        ReturnItem(SanPham p, int b, double pr) {
            product = p;
            boughtQty = b;
            price = pr;
        }
    }

    // ===Model SP mới được chọn để đổi===
    private class OrderItem {

        SanPham product;
        int quantity;

        OrderItem(SanPham p, int q) {
            product = p;
            quantity = q;
        }
    }
}
