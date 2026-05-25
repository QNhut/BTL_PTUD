package gui;

import constants.Colors;
import constants.FontStyle;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.PhuongThucThanhToan_DAO;
import dao.SanPham_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.PhuongThucThanhToan;
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

public class TraHang_GUI extends JPanel {

    private final HoaDon_DAO hoaDonDAO = new HoaDon_DAO();
    private final ChiTietHoaDon_DAO chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
    private final SanPham_DAO sanPhamDAO = new SanPham_DAO();
    private final KhachHang_DAO khachHangDAO = new KhachHang_DAO();
    private final PhuongThucThanhToan_DAO pttDAO = new PhuongThucThanhToan_DAO();
    private final service.TraHang_Service traHangService = new service.TraHang_Service();

    private final List<ReturnItem> currentInvoiceItems = new ArrayList<>();
    private JTextField txtSearchMaHD;
    private JTable tblItems;
    private DefaultTableModel modelItems;
    private JLabel lblRefundTotal, lblCustomerName, lblInvoiceDate;
    private JLabel lblMaHoaDon, lblPhone, lblPaymentMethod, lblInvoiceTotal;
    private RoundedToggleButton btnModeList, btnModeManual;
    private JPanel pnlReasonListPanel, pnlReasonManualPanel;
    private JComboBox<String> cbReasonPreset;
    private JTextField txtReasonCustom;
    private JTextArea txtNote;

    private final DecimalFormat df = new DecimalFormat("#,###");
    private double refundTotal = 0;

    public TraHang_GUI() {
        setLayout(new BorderLayout());
        setBackground(Colors.BACKGROUND);
        initComponents();
    }

    private void initComponents() {
        // Header
        JPanel pnlHeader = new JPanel(new BorderLayout());
        pnlHeader.setBackground(Colors.PRIMARY);
        pnlHeader.setBorder(new EmptyBorder(30, 25, 25, 25));
        JLabel lblTitle = new JLabel("Quản Lý Trả Hàng");
        lblTitle.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblTitle.setForeground(Color.WHITE);
        pnlHeader.add(lblTitle, BorderLayout.NORTH);
        JLabel lblSub = new JLabel("Tìm kiếm hóa đơn và xử lý hoàn tiền cho khách hàng");
        lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSub.setForeground(new Color(220, 220, 220));
        pnlHeader.add(lblSub, BorderLayout.SOUTH);
        add(pnlHeader, BorderLayout.NORTH);

        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(new EmptyBorder(20, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        pnlCenter.add(createLeftPanel(), gbc);
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 25, 0, 0);
        pnlCenter.add(createRightPanel(), gbc);
        add(pnlCenter, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        RoundedPanel pnl = new RoundedPanel(0, 0, 12);
        pnl.setBackground(Color.WHITE);
        pnl.setLayout(new BorderLayout(0, 0));
        pnl.setBorder(new EmptyBorder(25, 20, 25, 20));

        // Search Section
        JPanel pnlSearch = new JPanel(new BorderLayout(0, 10));
        pnlSearch.setOpaque(false);
        JLabel lblSearch = new JLabel("🔍 Tìm Kiếm Hóa Đơn");
        lblSearch.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblSearch.setForeground(Colors.TEXT_PRIMARY);
        pnlSearch.add(lblSearch, BorderLayout.NORTH);
        JLabel lblSearchHint = new JLabel("Nhập mã hóa đơn để tìm kiếm");
        lblSearchHint.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSearchHint.setForeground(new Color(120, 120, 120));
        txtSearchMaHD = new JTextField();
        txtSearchMaHD.setPreferredSize(new Dimension(0, 38));
        txtSearchMaHD.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        RoundedButton btnSearch = new RoundedButton(0, 38, 8, "🔎 Tìm hóa đơn", Colors.PRIMARY);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> searchInvoice());
        JPanel pnlInputField = new JPanel(new BorderLayout(0, 5));
        pnlInputField.setOpaque(false);
        pnlInputField.add(lblSearchHint, BorderLayout.NORTH);
        pnlInputField.add(txtSearchMaHD, BorderLayout.CENTER);
        pnlSearch.add(pnlInputField, BorderLayout.CENTER);
        pnlSearch.add(btnSearch, BorderLayout.SOUTH);

        JPanel pnlTop = new JPanel();
        pnlTop.setLayout(new BoxLayout(pnlTop, BoxLayout.Y_AXIS));
        pnlTop.setOpaque(false);
        pnlSearch.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTop.add(pnlSearch);

        // Info Section
        JPanel pnlInfo = new JPanel(new BorderLayout(0, 6));
        pnlInfo.setOpaque(false);
        pnlInfo.setBorder(new EmptyBorder(10, 0, 8, 0));
        JLabel lblInfoTitle = new JLabel("📋 Thông Tin Hóa Đơn");
        lblInfoTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblInfoTitle.setForeground(Colors.TEXT_PRIMARY);
        pnlInfo.add(lblInfoTitle, BorderLayout.NORTH);
        JPanel pnlInfoDetails = new JPanel();
        pnlInfoDetails.setLayout(new BoxLayout(pnlInfoDetails, BoxLayout.Y_AXIS));
        pnlInfoDetails.setOpaque(false);
        lblMaHoaDon = new JLabel("🧾 Mã hóa đơn: --");
        lblMaHoaDon.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
        lblMaHoaDon.setForeground(Colors.PRIMARY);
        lblCustomerName = new JLabel("👤 Khách hàng: --");
        lblCustomerName.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lblPhone = new JLabel("📞 SĐT: --");
        lblPhone.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lblInvoiceDate = new JLabel("📅 Ngày lập: --");
        lblInvoiceDate.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lblPaymentMethod = new JLabel("💳 Phương thức TT: --");
        lblPaymentMethod.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        lblInvoiceTotal = new JLabel("💰 Tổng tiền HĐ: --");
        lblInvoiceTotal.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblInvoiceTotal.setForeground(new Color(229, 57, 53));
        pnlInfoDetails.add(lblMaHoaDon);
        pnlInfoDetails.add(Box.createVerticalStrut(10));
        pnlInfoDetails.add(lblCustomerName);
        pnlInfoDetails.add(Box.createVerticalStrut(10));
        pnlInfoDetails.add(lblPhone);
        pnlInfoDetails.add(Box.createVerticalStrut(10));
        pnlInfoDetails.add(lblInvoiceDate);
        pnlInfoDetails.add(Box.createVerticalStrut(10));
        pnlInfoDetails.add(lblPaymentMethod);
        pnlInfoDetails.add(Box.createVerticalStrut(10));
        pnlInfoDetails.add(lblInvoiceTotal);
        pnlInfo.add(pnlInfoDetails, BorderLayout.CENTER);
        pnlInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setOpaque(false);
        pnlContent.add(pnlInfo);

        // Reason Section
        JPanel pnlReason = new JPanel();
        pnlReason.setLayout(new BoxLayout(pnlReason, BoxLayout.Y_AXIS));
        pnlReason.setOpaque(false);
        pnlReason.setBorder(new EmptyBorder(30, 0, 0, 0));

        JLabel lblReasonTitle = new JLabel("⚠ Lý Do Trả Hàng");
        lblReasonTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblReasonTitle.setForeground(Colors.TEXT_PRIMARY);
        lblReasonTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlReason.add(lblReasonTitle);
        pnlReason.add(Box.createVerticalStrut(8));

        JPanel pnlToggleRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlToggleRow.setOpaque(false);
        pnlToggleRow.setAlignmentX(Component.LEFT_ALIGNMENT);
//        JLabel lblMode = new JLabel("Chế độ nhập:  ");
//        lblMode.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
//        lblMode.setForeground(Colors.TEXT_SECONDARY);
        ButtonGroup modeGroup = new ButtonGroup();
        btnModeList = new RoundedToggleButton(100, 32, 12, "≡ Chọn", Colors.PRIMARY);
        btnModeManual = new RoundedToggleButton(90, 32, 12, "✏ Nhập", Colors.TEXT_SECONDARY);
        btnModeList.setForeground(Color.WHITE);
        btnModeManual.setForeground(Color.WHITE);
        modeGroup.add(btnModeList);
        modeGroup.add(btnModeManual);
        btnModeList.setSelected(true);
        btnModeList.addActionListener(e -> switchMode(false));
        btnModeManual.addActionListener(e -> switchMode(true));
//        pnlToggleRow.add(lblMode);
        pnlToggleRow.add(btnModeList);
        pnlToggleRow.add(Box.createHorizontalStrut(6));
        pnlToggleRow.add(btnModeManual);
        pnlReason.add(pnlToggleRow);
        pnlReason.add(Box.createVerticalStrut(8));

        JLabel lblReasonField = new JLabel("Lý do trả hàng *");
        lblReasonField.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblReasonField.setForeground(Colors.TEXT_SECONDARY);
        lblReasonField.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlReason.add(lblReasonField);
        pnlReason.add(Box.createVerticalStrut(4));

        pnlReasonListPanel = new JPanel(new BorderLayout());
        pnlReasonListPanel.setOpaque(false);
        pnlReasonListPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        cbReasonPreset = new JComboBox<>(new String[]{
            "Chọn lý do trả hàng...", "Hết hạn sử dụng", "Sản phẩm lỗi/hư hỏng",
            "Sản phẩm không đúng mô tả", "Sản phẩm không đúng loại", "Khách hàng đổi ý"
        });
        cbReasonPreset.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        pnlReasonListPanel.add(cbReasonPreset, BorderLayout.CENTER);
        pnlReason.add(pnlReasonListPanel);

        pnlReasonManualPanel = new JPanel(new BorderLayout());
        pnlReasonManualPanel.setOpaque(false);
        pnlReasonManualPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlReasonManualPanel.setVisible(false);
        txtReasonCustom = new JTextField();
        txtReasonCustom.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        txtReasonCustom.setPreferredSize(new Dimension(0, 30));
        txtReasonCustom.setText("Nhập lý do trả hàng...");
        txtReasonCustom.setForeground(Colors.TEXT_SECONDARY);
        txtReasonCustom.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent fe) {
                if (txtReasonCustom.getText().equals("Nhập lý do trả hàng...")) {
                    txtReasonCustom.setText("");
                    txtReasonCustom.setForeground(Colors.TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent fe) {
                if (txtReasonCustom.getText().trim().isEmpty()) {
                    txtReasonCustom.setText("Nhập lý do trả hàng...");
                    txtReasonCustom.setForeground(Colors.TEXT_SECONDARY);
                }
            }
        });
        pnlReasonManualPanel.add(txtReasonCustom, BorderLayout.CENTER);
        pnlReason.add(pnlReasonManualPanel);

        // Note Section
        pnlReason.add(Box.createVerticalStrut(10));
        JLabel lblNote = new JLabel("📝 Ghi chú (không bắt buộc)");
        lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNote.setForeground(Colors.TEXT_SECONDARY);
        lblNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlReason.add(lblNote);
        pnlReason.add(Box.createVerticalStrut(4));
        txtNote = new JTextArea(5, 0);
        txtNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        txtNote.setLineWrap(true);
        txtNote.setWrapStyleWord(true);
        txtNote.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER, 1),
                new EmptyBorder(4, 8, 4, 8)
        ));
        txtNote.setText("Nhập ghi chú...");
        txtNote.setForeground(Colors.TEXT_SECONDARY);
        txtNote.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent fe) {
                if (txtNote.getText().equals("Nhập ghi chú...")) {
                    txtNote.setText("");
                    txtNote.setForeground(Colors.TEXT_PRIMARY);
                }
            }

            @Override
            public void focusLost(FocusEvent fe) {
                if (txtNote.getText().trim().isEmpty()) {
                    txtNote.setText("Nhập ghi chú...");
                    txtNote.setForeground(Colors.TEXT_SECONDARY);
                }
            }
        });
        JScrollPane spNote = new JScrollPane(txtNote);
        spNote.setAlignmentX(Component.LEFT_ALIGNMENT);
        spNote.setBorder(BorderFactory.createEmptyBorder());
        pnlReason.add(spNote);

        pnlContent.add(pnlReason);
        pnlContent.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTop.add(Box.createVerticalStrut(12));
        pnlTop.add(pnlContent);
        pnl.add(pnlTop, BorderLayout.NORTH);
        return pnl;
    }

    private JPanel createRightPanel() {
        RoundedPanel pnl = new RoundedPanel(0, 0, 12);
        pnl.setBackground(Color.WHITE);
        pnl.setLayout(new BorderLayout(0, 15));
        pnl.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblTableTitle = new JLabel("← Danh Sách Sản Phẩm Trả");
        lblTableTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblTableTitle.setForeground(Colors.PRIMARY);
        pnl.add(lblTableTitle, BorderLayout.NORTH);

        String[] cols = {"Mã SP", "Tên sản phẩm", "Đơn giá", "SL Mua", "SL Trả", "Hoàn tiền", "Thao tác"};
        modelItems = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 4 || c == 6;
            }

            @Override
            public Class<?> getColumnClass(int c) {
                return (c == 3 || c == 4) ? Integer.class : String.class;
            }
        };
        modelItems.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 4) {
                handleQtyChange(e.getFirstRow());
            }
        });

        tblItems = new JTable(modelItems) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getRowCount() == 0) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Colors.TEXT_SECONDARY);
                    g2.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
                    String text = "Tìm hóa đơn để hiển thị danh sách sản phẩm";
                    FontMetrics fm = g2.getFontMetrics();
                    int x = (getWidth() - fm.stringWidth(text)) / 2;
                    int y = getHeight() / 2;
                    g2.drawString(text, x, y);
                }
            }
        };
        tblItems.setRowHeight(34);
        tblItems.getTableHeader().setBackground(new Color(245, 245, 245));
        tblItems.getTableHeader().setForeground(Colors.TEXT_PRIMARY);
        tblItems.setGridColor(new Color(230, 230, 230));
        tblItems.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        tblItems.getColumnModel().getColumn(4).setCellEditor(new SpinnerCellEditor());

        javax.swing.table.DefaultTableCellRenderer center = new javax.swing.table.DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblItems.getColumnModel().getColumn(3).setCellRenderer(center);
        tblItems.getColumnModel().getColumn(4).setCellRenderer(center);

        tblItems.getColumnModel().getColumn(6).setCellRenderer((table, value, isSelected, hasFocus, row, col) -> {
            JButton btn = new JButton("Xoa");
            btn.setBackground(Colors.DANGER);
            btn.setForeground(Color.WHITE);
            btn.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            return btn;
        });
        tblItems.getColumnModel().getColumn(6).setCellEditor(new DeleteButtonEditor());
        tblItems.getColumnModel().getColumn(6).setPreferredWidth(70);
        tblItems.getColumnModel().getColumn(6).setMaxWidth(80);

        pnl.add(new JScrollPane(tblItems), BorderLayout.CENTER);

        JPanel pnlFooter = new JPanel(new BorderLayout(20, 0));
        pnlFooter.setOpaque(false);
        pnlFooter.setBorder(new EmptyBorder(15, 0, 0, 0));
        lblRefundTotal = new JLabel("TỔNG TIỀN HOÀN: 0 đ");
        lblRefundTotal.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblRefundTotal.setForeground(Colors.PRIMARY);
        RoundedButton btnConfirm = new RoundedButton(220, 45, 10, "✅ Xác Nhận Hoàn Tiền", Colors.PRIMARY);
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        btnConfirm.addActionListener(e -> processReturn());
        pnlFooter.add(lblRefundTotal, BorderLayout.CENTER);
        pnlFooter.add(btnConfirm, BorderLayout.EAST);
        pnl.add(pnlFooter, BorderLayout.SOUTH);

        return pnl;
    }

    private void switchMode(boolean isManual) {
        pnlReasonListPanel.setVisible(!isManual);
        pnlReasonManualPanel.setVisible(isManual);
        revalidate();
        repaint();
    }

    private void removeRow(int row) {
        if (row >= 0 && row < currentInvoiceItems.size()) {
            currentInvoiceItems.remove(row);
            modelItems.removeRow(row);
            updateRefundTotal();
        }
    }

    private void searchInvoice() {
        String ma = txtSearchMaHD.getText().trim();
        if (ma.isEmpty()) {
            return;
        }
        HoaDon hd = hoaDonDAO.layHDTheoMa(ma);
        if (hd == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!");
            return;
        }
        String tenKH = "Khách lẻ";
        String sdtKH = "---";
        if (hd.getKhachHang() != null && hd.getKhachHang().getMaKhachHang() != null) {
            KhachHang khFull = khachHangDAO.layKHTheoMa(hd.getKhachHang().getMaKhachHang());
            if (khFull != null) {
                if (khFull.getTenKhachHang() != null && !khFull.getTenKhachHang().isBlank()) {
                    tenKH = khFull.getTenKhachHang();
                }
                if (khFull.getSoDienThoai() != null && !khFull.getSoDienThoai().isBlank()) {
                    sdtKH = khFull.getSoDienThoai();
                }
            }
        }
        String tenPTTT = hd.getMaPTTT() != null ? hd.getMaPTTT() : "---";
        if (hd.getMaPTTT() != null) {
            try {
                PhuongThucThanhToan ptt = pttDAO.layTheoMa(hd.getMaPTTT());
                if (ptt != null && ptt.getTenPTTT() != null) {
                    tenPTTT = ptt.getTenPTTT();
                }
            } catch (Exception ignore) {
            }
        }
        double tongTienHD = hd.getThanhTien() > 0 ? hd.getThanhTien() : hd.getTongTien();
        lblMaHoaDon.setText("🧾 Mã hóa đơn: " + hd.getMaHoaDon());
        lblCustomerName.setText("👤 Khách hàng: " + tenKH);
        lblPhone.setText("📞 SĐT: " + sdtKH);
        lblInvoiceDate.setText("📅 Ngày lập: "
                + (hd.getNgayLap() != null ? hd.getNgayLap().toString() : "--"));
        lblPaymentMethod.setText("💳 Phương thức TT: " + tenPTTT);
        lblInvoiceTotal.setText("💰 Tổng tiền HĐ: " + df.format(tongTienHD) + " đ");
        List<ChiTietHoaDon> details = chiTietHoaDonDAO.getDSTheoHoaDon(ma);
        currentInvoiceItems.clear();
        modelItems.setRowCount(0);
        for (ChiTietHoaDon ct : details) {
            SanPham fullSP = sanPhamDAO.laySanPhamTheoMa(ct.getSanPham().getMaSP());
            if (fullSP != null) {
                ct.setSanPham(fullSP);
            }
            currentInvoiceItems.add(new ReturnItem(ct.getSanPham(), ct.getSoLuong(), ct.getDonGia()));
            modelItems.addRow(new Object[]{
                ct.getSanPham().getMaSP(),
                ct.getSanPham().getTenSP(),
                df.format(ct.getDonGia()),
                Integer.valueOf(ct.getSoLuong()),
                Integer.valueOf(0),
                "0 đ",
                "Xóa"
            });
        }
        updateRefundTotal();
    }

    private void handleQtyChange(int row) {
        if (row < 0 || row >= currentInvoiceItems.size()) {
            return;
        }
        try {
            Object v = modelItems.getValueAt(row, 4);
            int qty = (v instanceof Integer) ? (Integer) v : Integer.parseInt(v.toString());
            ReturnItem item = currentInvoiceItems.get(row);
            if (qty < 0) {
                qty = 0;
            }
            if (qty > item.boughtQty) {
                JOptionPane.showMessageDialog(this,
                        "Số lượng trả không được vượt quá SL Mua (" + item.boughtQty + ").",
                        "Không hợp lệ", JOptionPane.WARNING_MESSAGE);
                qty = item.boughtQty;
                modelItems.setValueAt(qty, row, 4);
                return;
            }
            item.returnQty = qty;
            modelItems.setValueAt(df.format(qty * item.price) + " đ", row, 5);
            updateRefundTotal();
        } catch (Exception e) {
            modelItems.setValueAt(0, row, 4);
        }
    }

    private void updateRefundTotal() {
        refundTotal = 0;
        for (ReturnItem i : currentInvoiceItems) {
            refundTotal += i.returnQty * i.price;
        }
        lblRefundTotal.setText("TỔNG TIỀN HOÀN: " + df.format(refundTotal) + " đ");
    }

    private void processReturn() {
        if (tblItems.isEditing()) {
            tblItems.getCellEditor().stopCellEditing();
        }
        if (currentInvoiceItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng tìm hóa đơn trước!");
            return;
        }
        if (refundTotal <= 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng nhập số lượng trả!");
            return;
        }
        String reason;
        if (btnModeList.isSelected()) {
            if (cbReasonPreset.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "⚠️ Vui lòng chọn lý do trả hàng!");
                return;
            }
            reason = cbReasonPreset.getSelectedItem().toString();
        } else {
            String custom = txtReasonCustom.getText().trim();
            if (custom.isEmpty() || custom.equals("Nhập lý do trả hàng...")) {
                JOptionPane.showMessageDialog(this, "⚠️ Vui lòng nhập lý do trả hàng!");
                return;
            }
            reason = custom;
        }
        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận trả hàng và hoàn tiền " + df.format(refundTotal) + " đ cho khách?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        String maHD = txtSearchMaHD.getText().trim();
        List<ChiTietHoaDon> returns = new ArrayList<>();
        for (ReturnItem i : currentInvoiceItems) {
            if (i.returnQty > 0) {
                returns.add(new ChiTietHoaDon(null, i.product, i.returnQty, i.price));
            }
        }
        boolean success = traHangService.thucHienTraHang(maHD, returns, reason);
        if (success) {
            HoaDon_GUI.stockDirty = true;
            JOptionPane.showMessageDialog(this, "✅ Xử lý trả hàng thành công! Kho và hóa đơn đã được cập nhật.");
            resetForm();
        } else {
            JOptionPane.showMessageDialog(this, "❌ Có lỗi xảy ra khi cập nhật hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetForm() {
        modelItems.setRowCount(0);
        currentInvoiceItems.clear();
        updateRefundTotal();
        txtSearchMaHD.setText("");
        cbReasonPreset.setSelectedIndex(0);
        btnModeList.setSelected(true);
        switchMode(false);
        txtReasonCustom.setText("Nhập lý do trả hàng...");
        txtReasonCustom.setForeground(Colors.TEXT_SECONDARY);
        txtNote.setText("Nhập ghi chú...");
        txtNote.setForeground(Colors.TEXT_SECONDARY);
        lblMaHoaDon.setText("🧾 Mã hóa đơn: --");
        lblCustomerName.setText("👤 Khách hàng: --");
        lblPhone.setText("📞 SĐT: --");
        lblInvoiceDate.setText("📅 Ngày lập: --");
        lblPaymentMethod.setText("💳 Phương thức TT: --");
        lblInvoiceTotal.setText("💰 Tổng tiền HĐ: --");
    }

    // === Inner classes ===
    private class ReturnItem {

        SanPham product;
        int boughtQty;
        int returnQty = 0;
        double price;

        ReturnItem(SanPham p, int b, double pr) {
            product = p;
            boughtQty = b;
            price = pr;
        }
    }

    private class SpinnerCellEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor {

        private final JSpinner spinner = new JSpinner(new SpinnerNumberModel(0, 0, 9999, 1));

        SpinnerCellEditor() {
            JComponent ed = spinner.getEditor();
            if (ed instanceof JSpinner.DefaultEditor) {
                ((JSpinner.DefaultEditor) ed).getTextField().setHorizontalAlignment(JTextField.CENTER);
            }
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            int max = (row >= 0 && row < currentInvoiceItems.size()) ? currentInvoiceItems.get(row).boughtQty : 9999;
            int cur = 0;
            if (value instanceof Integer) {
                cur = (Integer) value;
            } else if (value != null) {
                try {
                    cur = Integer.parseInt(value.toString());
                } catch (NumberFormatException ignored) {
                }
            }
            if (cur < 0) {
                cur = 0;
            }
            if (cur > max) {
                cur = max;
            }
            spinner.setModel(new SpinnerNumberModel(cur, 0, max, 1));
            return spinner;
        }

        @Override
        public Object getCellEditorValue() {
            try {
                spinner.commitEdit();
            } catch (java.text.ParseException ignored) {
            }
            return spinner.getValue();
        }
    }

    private class DeleteButtonEditor extends javax.swing.AbstractCellEditor implements javax.swing.table.TableCellEditor {

        private final JButton btn = new JButton("Xóa");
        private int editingRow;

        DeleteButtonEditor() {
            btn.setBackground(Colors.DANGER);
            btn.setForeground(Color.WHITE);
            btn.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            btn.setFocusPainted(false);
            btn.setBorderPainted(false);
            btn.setOpaque(true);
            btn.addActionListener(e -> {
                fireEditingStopped();
                removeRow(editingRow);
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            editingRow = row;
            return btn;
        }

        @Override
        public Object getCellEditorValue() {
            return "Xóa";
        }
    }
}
