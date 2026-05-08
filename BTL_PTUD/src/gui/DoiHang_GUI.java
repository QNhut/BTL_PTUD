package gui;

import constants.Colors;
import constants.FontStyle;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.LoaiSanPham_DAO;
import dao.SanPham_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.LoaiSanPham;
import entity.SanPham;
import exception.RoundedButton;
import exception.RoundedPanel;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class DoiHang_GUI extends JPanel {

    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
    private final SanPham_DAO sanPhamDAO = new SanPham_DAO();
    private final LoaiSanPham_DAO loaiSanPhamDAO = new LoaiSanPham_DAO();

    private List<SanPham> allProducts = new ArrayList<>();
    private List<LoaiSanPham> categories = new ArrayList<>();
    private List<ReturnItem> currentInvoiceItems = new ArrayList<>();
    private List<OrderItem> newOrderItems = new ArrayList<>();
    
    private final dao.LoSanPham_DAO loSanPhamDAO = new dao.LoSanPham_DAO();
    private final service.LoSanPham_Service loSanPhamService = new service.LoSanPham_Service();
    private final service.DoiHang_Service doiHangService = new service.DoiHang_Service();

    private JTextField txtSearchMaHD, txtSearchInv;
    private JTable tblOldOrder, tblInventory, tblNewOrder, tblReason;
    private DefaultTableModel modelOldOrder, modelInventory, modelNewOrder, modelReason;
    private JComboBox<String> cbCategories;
    private JLabel lblOldTotal, lblNewTotal, lblDiffAmount, lblSummaryOldTotal;
    
    private final DecimalFormat df = new DecimalFormat("#,###");
    private double oldOrderTotal = 0;
    private double newOrderTotal = 0;

    public DoiHang_GUI() {
        setLayout(new BorderLayout());
        setBackground(Colors.BACKGROUND);
        loadData();
        initComponents();
    }

    private void loadData() {
        try {
            // Lấy danh sách sản phẩm kèm số lượng tồn từ các lô hàng
            allProducts = sanPhamDAO.getDSSanPham();
            categories = loaiSanPhamDAO.getDSLoaiSanPham();
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void initComponents() {
        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Colors.PRIMARY_BUTTON);
        pnlHeader.setBorder(new EmptyBorder(30, 25, 25, 25));
        
        JLabel lblTitle = new JLabel("Quản Lý Đổi Hàng");
        lblTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        
        JLabel lblSub = new JLabel("Tìm hóa đơn cũ để chọn sản phẩm cần đổi và chọn sản phẩm mới từ tồn kho");
        lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSub.setForeground(new Color(220, 220, 220));
        pnlHeader.add(lblSub, BorderLayout.SOUTH);
        add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new GridLayout(1, 2, 25, 0));
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(new EmptyBorder(20, 25, 25, 25));

        pnlCenter.add(createLeftPanel());
        pnlCenter.add(createRightPanel());
        add(pnlCenter, BorderLayout.CENTER);
        
        refreshInventoryTable();
    }

    private JPanel createLeftPanel() {
        JPanel container = new JPanel(new BorderLayout(0, 20));
        container.setOpaque(false);

        // --- SECTION 1: OLD ORDER ---
        RoundedPanel pnlOld = new RoundedPanel(0, 0, 12);
        pnlOld.setBackground(Color.WHITE);
        pnlOld.setLayout(new BorderLayout(0, 15));
        pnlOld.setBorder(new EmptyBorder(25, 20, 25, 20));

        JPanel pnlSearch = new JPanel(new BorderLayout(10, 8));
        pnlSearch.setOpaque(false);
        JLabel lblSearch = new JLabel("📋 Đơn Hàng Cũ");
        lblSearch.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblSearch.setForeground(Colors.TEXT_PRIMARY);
        pnlSearch.add(lblSearch, BorderLayout.NORTH);

        JLabel lblSearchHint = new JLabel("Nhập mã hóa đơn để tìm kiếm");
        lblSearchHint.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSearchHint.setForeground(new Color(120, 120, 120));

        txtSearchMaHD = new JTextField();
        txtSearchMaHD.setPreferredSize(new Dimension(0, 38));
        txtSearchMaHD.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        
        RoundedButton btnSearch = new RoundedButton(110, 38, 8, "🔍 Tìm", Colors.PRIMARY_BUTTON);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> searchInvoice());
        
        JPanel pnlInput = new JPanel(new BorderLayout(10, 0));
        pnlInput.setOpaque(false);
        pnlInput.add(lblSearchHint, BorderLayout.NORTH);
        JPanel pnlInputField = new JPanel(new BorderLayout(10, 0));
        pnlInputField.setOpaque(false);
        pnlInputField.add(txtSearchMaHD, BorderLayout.CENTER);
        pnlInputField.add(btnSearch, BorderLayout.EAST);
        pnlInput.add(pnlInputField, BorderLayout.CENTER);
        pnlSearch.add(pnlInput, BorderLayout.CENTER);
        pnlOld.add(pnlSearch, BorderLayout.NORTH);

        String[] cols = {"Mã SP", "Tên SP", "Giá", "Đã Mua", "SL Đổi", "Tổng"};
        modelOldOrder = new DefaultTableModel(cols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblOldOrder = new JTable(modelOldOrder);
        tblOldOrder.setRowHeight(32);
        tblOldOrder.getTableHeader().setBackground(new Color(245, 245, 245));
        tblOldOrder.getTableHeader().setForeground(Colors.TEXT_PRIMARY);
        tblOldOrder.setGridColor(new Color(230, 230, 230));
        
        JPanel pnlTable = new JPanel(new BorderLayout(0, 10));
        pnlTable.setOpaque(false);
        pnlTable.add(new JScrollPane(tblOldOrder), BorderLayout.CENTER);
        
        RoundedButton btnReturn = new RoundedButton(140, 35, 6, "↩️ Chọn đổi hàng", new Color(66, 133, 244));
        btnReturn.setForeground(Color.WHITE);
        btnReturn.addActionListener(e -> selectReturnQuantity());
        pnlTable.add(btnReturn, BorderLayout.NORTH);
        pnlOld.add(pnlTable, BorderLayout.CENTER);

        JPanel pnlSummary = new JPanel(new BorderLayout());
        pnlSummary.setOpaque(false);
        pnlSummary.setBorder(new EmptyBorder(10, 0, 0, 0));
        lblOldTotal = new JLabel("💰 Tổng hàng cũ: 0 đ");
        lblOldTotal.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblOldTotal.setForeground(new Color(229, 57, 53));
        pnlSummary.add(lblOldTotal, BorderLayout.EAST);
        pnlOld.add(pnlSummary, BorderLayout.SOUTH);

        // --- SECTION 2: EXCHANGE REASONS ---
        RoundedPanel pnlReason = new RoundedPanel(0, 200, 12);
        pnlReason.setBackground(Color.WHITE);
        pnlReason.setLayout(new BorderLayout(0, 10));
        pnlReason.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblReasonTitle = new JLabel("📝 Lý Do Đổi Hàng");
        lblReasonTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblReasonTitle.setForeground(Colors.TEXT_PRIMARY);
        pnlReason.add(lblReasonTitle, BorderLayout.NORTH);

        String[] reasonCols = {"Mã SP", "Tên SP", "SL Đổi", "Lý Do"};
        modelReason = new DefaultTableModel(reasonCols, 0) {
            public boolean isCellEditable(int r, int c) { return c == 3; }
        };
        modelReason.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 3) {
                int row = e.getFirstRow();
                String maSP = modelReason.getValueAt(row, 0).toString();
                String reason = modelReason.getValueAt(row, 3).toString();
                for (ReturnItem i : currentInvoiceItems) {
                    if (i.product.getMaSP().trim().equals(maSP.trim())) {
                        i.reason = reason;
                        break;
                    }
                }
            }
        });
        
        tblReason = new JTable(modelReason);
        tblReason.setRowHeight(32);
        tblReason.getTableHeader().setBackground(new Color(245, 245, 245));
        tblReason.getTableHeader().setForeground(Colors.TEXT_PRIMARY);
        tblReason.setGridColor(new Color(230, 230, 230));
        
        // --- THÊM Ô CHỌN LÝ DO CÓ SẴN ---
        JComboBox<String> cbReasons = new JComboBox<>(new String[]{
            "Sản phẩm lỗi",
            "Không vừa kích cỡ",
            "Đổi màu sắc",
            "Sản phẩm không giống mô tả",
            "Đổi ý / Không thích nữa",
            "Khác"
        });
        tblReason.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(cbReasons));
        
        pnlReason.add(new JScrollPane(tblReason), BorderLayout.CENTER);

        container.add(pnlOld, BorderLayout.CENTER);
        container.add(pnlReason, BorderLayout.SOUTH);

        return container;
    }

    private JPanel createRightPanel() {
        JPanel pnl = new JPanel(new BorderLayout(0, 20));
        pnl.setOpaque(false);

        // Inventory panel - cố định chiều cao
        JPanel pnlInvWrapper = new JPanel(new BorderLayout());
        pnlInvWrapper.setOpaque(false);
        pnlInvWrapper.setPreferredSize(new Dimension(0, 280));
        pnlInvWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 280));

        // Inventory
        RoundedPanel pnlInv = new RoundedPanel(0, 0, 12);
        pnlInv.setBackground(Color.WHITE);
        pnlInv.setLayout(new BorderLayout(0, 12));
        pnlInv.setBorder(new EmptyBorder(25, 20, 25, 20));

        JPanel pnlInvHeader = new JPanel(new BorderLayout());
        pnlInvHeader.setOpaque(false);
        JLabel lblInv = new JLabel("📦 Tồn Kho - Hàng Mới");
        lblInv.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblInv.setForeground(Colors.TEXT_PRIMARY);
        pnlInvHeader.add(lblInv, BorderLayout.WEST);

        JPanel pnlFilters = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        pnlFilters.setOpaque(false);
        
        txtSearchInv = new JTextField();
        txtSearchInv.setPreferredSize(new Dimension(120, 32));
        txtSearchInv.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        txtSearchInv.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) { refreshInventoryTable(); }
        });
        JLabel lblSearch = new JLabel("Tìm:");
        lblSearch.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        pnlFilters.add(lblSearch);
        pnlFilters.add(txtSearchInv);

        cbCategories = new JComboBox<>();
        cbCategories.setPreferredSize(new Dimension(130, 32));
        cbCategories.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        cbCategories.addItem("Tất cả");
        for (LoaiSanPham l : categories) cbCategories.addItem(l.getTenLoaiSP());
        cbCategories.addActionListener(e -> refreshInventoryTable());
        JLabel lblCategory = new JLabel("Loại:");
        lblCategory.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        pnlFilters.add(lblCategory);
        pnlFilters.add(cbCategories);
        
        pnlInvHeader.add(pnlFilters, BorderLayout.EAST);
        pnlInv.add(pnlInvHeader, BorderLayout.NORTH);

        String[] invCols = {"Mã SP", "Tên SP", "Giá", "Tồn"};
        modelInventory = new DefaultTableModel(invCols, 0) { public boolean isCellEditable(int r, int c) { return false; } };
        tblInventory = new JTable(modelInventory);
        tblInventory.setRowHeight(32);
        tblInventory.getTableHeader().setBackground(new Color(245, 245, 245));
        tblInventory.getTableHeader().setForeground(Colors.TEXT_PRIMARY);
        tblInventory.setGridColor(new Color(230, 230, 230));
        tblInventory.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Chuyển sang Click đúp (2 lần) để tránh lỗi click nhầm/nhạy quá mức
                if (e.getClickCount() == 2) {
                    addNewItemFromInventory();
                }
            }
        });
        pnlInv.add(new JScrollPane(tblInventory), BorderLayout.CENTER);
        
        // Nút thêm sản phẩm từ kho
        RoundedButton btnAdd = new RoundedButton(160, 35, 6, "➕ Thêm sản phẩm", new Color(66, 133, 244));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.addActionListener(e -> {
            if (tblInventory.getSelectedRow() < 0) {
                JOptionPane.showMessageDialog(this, "⚠️ Vui lòng chọn một sản phẩm từ kho!");
                return;
            }
            addNewItemFromInventory();
        });
        JPanel pnlInvFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        pnlInvFooter.setOpaque(false);
        pnlInvFooter.setBorder(new EmptyBorder(10, 0, 0, 0));
        pnlInvFooter.add(btnAdd);
        pnlInv.add(pnlInvFooter, BorderLayout.SOUTH);
        
        pnlInvWrapper.add(pnlInv);
        pnl.add(pnlInvWrapper, BorderLayout.NORTH);


        // New Order
        RoundedPanel pnlNew = new RoundedPanel(0, 0, 12);
        pnlNew.setBackground(new Color(240, 255, 245));
        pnlNew.setLayout(new BorderLayout(0, 12));
        pnlNew.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblNew = new JLabel("✅ Đơn Hàng Mới");
        lblNew.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblNew.setForeground(new Color(27, 155, 65));
        pnlNew.add(lblNew, BorderLayout.NORTH);

        String[] newCols = {"Mã SP", "Tên SP", "Giá", "SL", "Tổng"};
        modelNewOrder = new DefaultTableModel(newCols, 0) { 
            public boolean isCellEditable(int r, int c) { return c == 3; } 
        };
        modelNewOrder.addTableModelListener(e -> {
            if (isRefreshing) return; // CHẶN VÒNG LẶP
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 3) {
                int row = e.getFirstRow();
                try {
                    int newQty = Integer.parseInt(modelNewOrder.getValueAt(row, 3).toString());
                    if (newQty > 0) {
                        newOrderItems.get(row).quantity = newQty;
                        refreshNewOrderTable();
                    }
                } catch (Exception ex) {}
            }
        });
        tblNewOrder = new JTable(modelNewOrder);
        tblNewOrder.setRowHeight(32);
        tblNewOrder.getTableHeader().setBackground(new Color(245, 245, 245));
        tblNewOrder.getTableHeader().setForeground(Colors.TEXT_PRIMARY);
        tblNewOrder.setGridColor(new Color(230, 230, 230));
        JScrollPane scrollNew = new JScrollPane(tblNewOrder);
        scrollNew.setPreferredSize(new Dimension(0, 160));
        pnlNew.add(scrollNew, BorderLayout.CENTER);

        JPanel pnlNewActions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        pnlNewActions.setOpaque(false);
        RoundedButton btnRemove = new RoundedButton(90, 32, 6, "🗑️ Xóa", new Color(244, 67, 54));
        RoundedButton btnClear = new RoundedButton(130, 32, 6, "🧹 Xóa tất cả", new Color(158, 158, 158));
        btnRemove.setForeground(Color.WHITE);
        btnClear.setForeground(Color.WHITE);
        btnRemove.addActionListener(e -> {
            int row = tblNewOrder.getSelectedRow();
            if (row >= 0) { newOrderItems.remove(row); refreshNewOrderTable(); }
        });
        btnClear.addActionListener(e -> { newOrderItems.clear(); refreshNewOrderTable(); });
        pnlNewActions.add(btnRemove);
        pnlNewActions.add(btnClear);

        JPanel pnlBottom = new JPanel(new BorderLayout(0, 12));
        pnlBottom.setOpaque(false);
        pnlBottom.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        JPanel pnlStats = new JPanel(new GridLayout(3, 1, 0, 6));
        pnlStats.setOpaque(false);
        lblNewTotal = new JLabel("💵 Hàng mới: 0 đ");
        lblNewTotal.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        lblSummaryOldTotal = new JLabel("💰 Hàng cũ: 0 đ");
        lblSummaryOldTotal.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        lblDiffAmount = new JLabel("💳 Thanh toán: 0 đ");
        lblDiffAmount.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblDiffAmount.setForeground(new Color(27, 155, 65));
        pnlStats.add(lblNewTotal); 
        pnlStats.add(lblSummaryOldTotal); 
        pnlStats.add(lblDiffAmount);
        
        JPanel pnlCombinedSummary = new JPanel(new BorderLayout());
        pnlCombinedSummary.setOpaque(false);
        pnlCombinedSummary.add(pnlNewActions, BorderLayout.NORTH);
        pnlCombinedSummary.add(pnlStats, BorderLayout.CENTER);
        
        pnlBottom.add(pnlCombinedSummary, BorderLayout.CENTER);

        RoundedButton btnConfirm = new RoundedButton(0, 45, 10, "✨ Xác Nhận Đổi Hàng", new Color(27, 155, 65));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        btnConfirm.addActionListener(e -> confirmExchange());
        pnlBottom.add(btnConfirm, BorderLayout.SOUTH);
        pnlNew.add(pnlBottom, BorderLayout.SOUTH);

        pnl.add(pnlNew, BorderLayout.CENTER);
        return pnl;
    }

    private void searchInvoice() {
        String ma = txtSearchMaHD.getText().trim();
        if (ma.isEmpty()) return;

        HoaDon hd = doiHangService.getHoaDonByMa(ma);
        if (hd == null) { 
            JOptionPane.showMessageDialog(this, "❌ Không tìm thấy hóa đơn: " + ma); 
            return; 
        }

        // Kiểm tra điều kiện đổi trả (ví dụ: trong vòng 7 ngày)
        String checkMsg = doiHangService.checkDieuKienDoiHang(hd);
        if (!checkMsg.equals("OK")) {
            JOptionPane.showMessageDialog(this, "⚠️ " + checkMsg);
            // Có thể chọn chặn hoặc cảnh báo tùy yêu cầu
        }

        List<ChiTietHoaDon> details = doiHangService.getChiTietHoaDon(ma);
        currentInvoiceItems.clear();
        for (ChiTietHoaDon ct : details) {
            currentInvoiceItems.add(new ReturnItem(ct.getSanPham(), ct.getSoLuong(), ct.getDonGia()));
        }
        refreshOldOrderTable();
    }

    private void refreshOldOrderTable() {
        modelOldOrder.setRowCount(0); 
        modelReason.setRowCount(0);
        oldOrderTotal = 0;
        for (ReturnItem i : currentInvoiceItems) {
            double total = i.returnQty * i.price; oldOrderTotal += total;
            modelOldOrder.addRow(new Object[]{i.product.getMaSP(), i.product.getTenSP(), df.format(i.price), i.boughtQty, i.returnQty, df.format(total)});
            
            if (i.returnQty > 0) {
                if (i.reason == null || i.reason.isEmpty()) i.reason = "Sản phẩm lỗi";
                modelReason.addRow(new Object[]{i.product.getMaSP(), i.product.getTenSP(), i.returnQty, i.reason});
            }
        }
        lblOldTotal.setText("Tổng hàng cũ: " + df.format(oldOrderTotal) + " đ");
        lblSummaryOldTotal.setText("Hàng cũ: " + df.format(oldOrderTotal) + " đ");
        updateDiff();
    }

    private void selectReturnQuantity() {
        int r = tblOldOrder.getSelectedRow(); if (r < 0) return;
        ReturnItem i = currentInvoiceItems.get(r);
        String v = JOptionPane.showInputDialog(this, "Nhập SL cần đổi cho " + i.product.getTenSP(), i.returnQty);
        if (v != null) {
            try {
                int q = Integer.parseInt(v);
                if (q >= 0 && q <= i.boughtQty) { 
                    i.returnQty = q; 
                    refreshOldOrderTable(); 
                } else {
                    JOptionPane.showMessageDialog(this, "⚠️ Số lượng không hợp lệ! (Lớn hơn số lượng đã mua hoặc nhỏ hơn 0)", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "⚠️ Vui lòng nhập một số nguyên hợp lệ!", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void refreshInventoryTable() {
        modelInventory.setRowCount(0);
        String cat = (String) cbCategories.getSelectedItem();
        String search = txtSearchInv.getText().toLowerCase().trim();
        
        for (SanPham s : allProducts) {
            boolean matchesCat = cat.equals("Tất cả") || s.getLoaiSP().getTenLoaiSP().equals(cat);
            boolean matchesSearch = s.getTenSP().toLowerCase().contains(search) || s.getMaSP().toLowerCase().contains(search);
            
            if (matchesCat && matchesSearch) {
                int tonKho = loSanPhamDAO.layTongSoLuongTonTheoMaSanPham(s.getMaSP());
                modelInventory.addRow(new Object[]{s.getMaSP(), s.getTenSP(), df.format(s.getGiaBan()), tonKho});
            }
        }
    }

    private boolean isProcessingAdd = false;
    private boolean isRefreshing = false;
    private void addNewItemFromInventory() {
        if (isProcessingAdd) return;
        int r = tblInventory.getSelectedRow();
        if (r < 0) return;
        String m = tblInventory.getValueAt(r, 0).toString().trim();

        isProcessingAdd = true;
        try {
            SanPham s = allProducts.stream()
                    .filter(p -> p.getMaSP().trim().equalsIgnoreCase(m))
                    .findFirst().orElse(null);
            if (s == null) return;

            // Tìm sản phẩm trong danh sách
            boolean found = false;
            for (OrderItem item : newOrderItems) {
                if (item.product.getMaSP().trim().equalsIgnoreCase(m)) {
                    item.quantity += 1; // Tự động +1
                    found = true;
                    break;
                }
            }
            if (!found) {
                newOrderItems.add(new OrderItem(s, 1));
            }
            // Luôn gọi refresh — hàm này có deduplication nên KHÔNG BAO GIờ có trùng
            refreshNewOrderTable();
        } finally {
            isProcessingAdd = false;
        }
    }

    private void refreshNewOrderTable() {
        // PHÒNG THỦ TUYỆT ĐỐI: Gộp mọi sản phẩm trùng trước khi render
        java.util.LinkedHashMap<String, OrderItem> deduped = new java.util.LinkedHashMap<>();
        for (OrderItem item : newOrderItems) {
            String key = item.product.getMaSP().trim().toLowerCase();
            if (deduped.containsKey(key)) {
                deduped.get(key).quantity += item.quantity;
            } else {
                deduped.put(key, new OrderItem(item.product, item.quantity));
            }
        }
        newOrderItems.clear();
        newOrderItems.addAll(deduped.values());

        isRefreshing = true;
        try {
            modelNewOrder.setRowCount(0);
            newOrderTotal = 0;
            for (OrderItem i : newOrderItems) {
                double t = i.product.getGiaBan() * i.quantity;
                newOrderTotal += t;
                modelNewOrder.addRow(new Object[]{
                    i.product.getMaSP(), i.product.getTenSP(),
                    df.format(i.product.getGiaBan()), i.quantity, df.format(t)
                });
            }
        } finally {
            isRefreshing = false;
        }
        lblNewTotal.setText("Hàng mới: " + df.format(newOrderTotal) + " đ");
        updateDiff();
    }

    private void updateDiff() {
        double d = newOrderTotal - oldOrderTotal;
        if (d > 0) {
            lblDiffAmount.setText("💳 Khách trả thêm: " + df.format(d) + " đ");
            lblDiffAmount.setForeground(new Color(229, 57, 53)); // Danger Red
        } else if (d < 0) {
            lblDiffAmount.setText("💳 Hoàn khách: " + df.format(Math.abs(d)) + " đ");
            lblDiffAmount.setForeground(new Color(27, 155, 65)); // Success Green
        } else {
            lblDiffAmount.setText("💳 Thanh toán: 0 đ");
            lblDiffAmount.setForeground(Colors.TEXT_PRIMARY);
        }
    }

    private void confirmExchange() {
        if (currentInvoiceItems.stream().noneMatch(i -> i.returnQty > 0)) {
            JOptionPane.showMessageDialog(this, "⚠️ Vui lòng chọn ít nhất một sản phẩm cần đổi!");
            return;
        }
        if (newOrderItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Vui lòng chọn sản phẩm mới muốn đổi!");
            return;
        }

        double diff = newOrderTotal - oldOrderTotal;
        String msg = String.format("Xác nhận đổi hàng?\n- Tổng hàng cũ đổi: %s đ\n- Tổng hàng mới lấy: %s đ\n- %s", 
                df.format(oldOrderTotal), df.format(newOrderTotal), 
                diff > 0 ? "Khách cần trả thêm: " + df.format(diff) + " đ" : "Cửa hàng hoàn lại: " + df.format(Math.abs(diff)) + " đ");

        int confirm = JOptionPane.showConfirmDialog(this, msg, "Xác nhận giao dịch", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) return;

        try {
            // Chuẩn bị dữ liệu để truyền vào Service
            List<ChiTietHoaDon> returns = new ArrayList<>();
            for (ReturnItem i : currentInvoiceItems) {
                if (i.returnQty > 0) {
                    returns.add(new ChiTietHoaDon(null, i.product, i.returnQty, i.price));
                }
            }

            List<ChiTietHoaDon> news = new ArrayList<>();
            for (OrderItem i : newOrderItems) {
                news.add(new ChiTietHoaDon(null, i.product, i.quantity, i.product.getGiaBan()));
            }

            // Gọi Service để xử lý kho và logic nghiệp vụ
            String mainReason = currentInvoiceItems.stream()
                                    .filter(i -> i.returnQty > 0)
                                    .map(i -> i.reason)
                                    .findFirst().orElse("Đổi hàng");
                                    
            String currentMaHD = txtSearchMaHD.getText().trim();
            boolean success = doiHangService.thucHienGiaoDichDoiHang(currentMaHD, returns, news, mainReason);

            if (success) {
                // Hiển thị dialog in hóa đơn đổi
                String[] cols = {"Tên SP", "SL", "Đơn giá"};
                DefaultTableModel inModel = new DefaultTableModel(cols, 0);
                for (OrderItem item : newOrderItems) {
                    inModel.addRow(new Object[]{item.product.getTenSP(), item.quantity, (int)item.product.getGiaBan()});
                }
                
                String maHD = txtSearchMaHD.getText().trim();
                HoaDon oldHD = hoaDonDAO.layHDTheoMa(maHD);
                String tenKH = "Khách hàng";
                String sdt = "";
                String tenNV = "";
                if (oldHD != null && oldHD.getKhachHang() != null) {
                    tenKH = oldHD.getKhachHang().getMaKhachHang();
                }
                if (oldHD != null && oldHD.getNhanVien() != null) {
                    tenNV = oldHD.getNhanVien().getMaNhanVien();
                }
                
                java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                String thoiGian = java.time.LocalDateTime.now().format(dtf);
                
                HoaDonPreviewDialog previewDialog = new HoaDonPreviewDialog(
                        (javax.swing.JFrame) SwingUtilities.getWindowAncestor(this),
                        tenKH, sdt, tenNV, thoiGian, inModel);
                previewDialog.setVisible(true);

                // 3. Thông báo thành công
                JOptionPane.showMessageDialog(this, "✅ Giao dịch đổi hàng đã được xử lý thành công!\n(Dữ liệu kho đã được cập nhật)");
                
                // 4. Reset giao diện
                currentInvoiceItems.clear();
                newOrderItems.clear();
                txtSearchMaHD.setText("");
                refreshOldOrderTable();
                refreshNewOrderTable();
                loadData(); // Tải lại tồn kho mới
                refreshInventoryTable();
            } else {
                throw new Exception("Lỗi khi cập nhật dữ liệu kho.");
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Có lỗi xảy ra: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private class ReturnItem {
        SanPham product; int boughtQty; int returnQty = 0; double price; String reason = "";
        ReturnItem(SanPham p, int b, double pr) { product = p; boughtQty = b; price = pr; }
    }
    private class OrderItem {
        SanPham product; int quantity;
        OrderItem(SanPham p, int q) { product = p; quantity = q; }
    }
}
