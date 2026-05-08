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
import java.awt.*;
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

    private List<ReturnItem> currentInvoiceItems = new ArrayList<>();
    private JTextField txtSearchMaHD;
    private JTable tblItems;
    private DefaultTableModel modelItems;
    private JLabel lblRefundTotal, lblCustomerName, lblInvoiceDate;
    // Bổ sung: mã HĐ, SĐT KH, phương thức thanh toán, tổng tiền của hóa đơn
    private JLabel lblMaHoaDon, lblPhone, lblPaymentMethod, lblInvoiceTotal;
    private JRadioButton rdoLyDoHetHan, rdoLyDoLoi;
    private ButtonGroup grpLyDo;

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
        pnlHeader.setBackground(Colors.PRIMARY_BUTTON);
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

        // Main Center Panel with 2 columns like DoiHang_GUI
        JPanel pnlCenter = new JPanel(new GridBagLayout());
        pnlCenter.setOpaque(false);
        pnlCenter.setBorder(new EmptyBorder(20, 25, 25, 25));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 1.0;

        // --- LEFT PANEL: Search & Info ---
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        pnlCenter.add(createLeftPanel(), gbc);

        // --- RIGHT PANEL: Items & Process ---
        gbc.gridx = 1;
        gbc.weightx = 0.7;
        gbc.insets = new Insets(0, 25, 0, 0);
        pnlCenter.add(createRightPanel(), gbc);

        add(pnlCenter, BorderLayout.CENTER);
    }

    private JPanel createLeftPanel() {
        RoundedPanel pnl = new RoundedPanel(0, 0, 12);
        pnl.setBackground(Color.WHITE);
        pnl.setLayout(new BorderLayout(0, 20));
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

        RoundedButton btnSearch = new RoundedButton(0, 38, 8, "🔎 Tìm hóa đơn", Colors.PRIMARY_BUTTON);
        btnSearch.setForeground(Color.WHITE);
        btnSearch.addActionListener(e -> searchInvoice());

        JPanel pnlInputField = new JPanel(new BorderLayout(0, 5));
        pnlInputField.setOpaque(false);
        pnlInputField.add(lblSearchHint, BorderLayout.NORTH);
        pnlInputField.add(txtSearchMaHD, BorderLayout.CENTER);
        pnlSearch.add(pnlInputField, BorderLayout.CENTER);
        pnlSearch.add(btnSearch, BorderLayout.SOUTH);
        pnl.add(pnlSearch, BorderLayout.NORTH);

        // Info Section
        JPanel pnlInfo = new JPanel(new BorderLayout(0, 12));
        pnlInfo.setOpaque(false);
        pnlInfo.setBorder(new EmptyBorder(15, 0, 15, 0));

        JLabel lblInfoTitle = new JLabel("📋 Thông Tin Hóa Đơn");
        lblInfoTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblInfoTitle.setForeground(Colors.TEXT_PRIMARY);
        pnlInfo.add(lblInfoTitle, BorderLayout.NORTH);

        JPanel pnlInfoDetails = new JPanel(new GridLayout(6, 1, 0, 8));
        pnlInfoDetails.setOpaque(false);
        lblMaHoaDon = new JLabel("🧾 Mã hóa đơn: --");
        lblMaHoaDon.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblMaHoaDon.setForeground(Colors.PRIMARY);
        lblCustomerName = new JLabel("👤 Khách hàng: --");
        lblCustomerName.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        lblPhone = new JLabel("📞 SĐT: --");
        lblPhone.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        lblInvoiceDate = new JLabel("📅 Ngày lập: --");
        lblInvoiceDate.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        lblPaymentMethod = new JLabel("💳 Phương thức TT: --");
        lblPaymentMethod.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        lblInvoiceTotal = new JLabel("💰 Tổng tiền HĐ: --");
        lblInvoiceTotal.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblInvoiceTotal.setForeground(new Color(229, 57, 53));
        pnlInfoDetails.add(lblMaHoaDon);
        pnlInfoDetails.add(lblCustomerName);
        pnlInfoDetails.add(lblPhone);
        pnlInfoDetails.add(lblInvoiceDate);
        pnlInfoDetails.add(lblPaymentMethod);
        pnlInfoDetails.add(lblInvoiceTotal);
        pnlInfo.add(pnlInfoDetails, BorderLayout.CENTER);
        pnl.add(pnlInfo, BorderLayout.CENTER);

        // Reason Section
        JPanel pnlReason = new JPanel(new BorderLayout(0, 8));
        pnlReason.setOpaque(false);
        pnlReason.setBorder(new EmptyBorder(10, 0, 0, 0));
        JLabel lblReasonTitle = new JLabel("📝 Lý Do Trả Hàng");
        lblReasonTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblReasonTitle.setForeground(Colors.TEXT_PRIMARY);
        pnlReason.add(lblReasonTitle, BorderLayout.NORTH);

        JPanel pnlReasonOptions = new JPanel(new GridLayout(2, 1, 0, 8));
        pnlReasonOptions.setOpaque(false);
        pnlReasonOptions.setBorder(new EmptyBorder(8, 4, 0, 0));
        rdoLyDoHetHan = new JRadioButton("Hết hạn sử dụng");
        rdoLyDoHetHan.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        rdoLyDoHetHan.setOpaque(false);
        rdoLyDoLoi = new JRadioButton("Sản phẩm lỗi");
        rdoLyDoLoi.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        rdoLyDoLoi.setOpaque(false);
        grpLyDo = new ButtonGroup();
        grpLyDo.add(rdoLyDoHetHan);
        grpLyDo.add(rdoLyDoLoi);
        rdoLyDoHetHan.setSelected(true);
        pnlReasonOptions.add(rdoLyDoHetHan);
        pnlReasonOptions.add(rdoLyDoLoi);
        pnlReason.add(pnlReasonOptions, BorderLayout.CENTER);
        pnl.add(pnlReason, BorderLayout.SOUTH);

        return pnl;
    }

    private JPanel createRightPanel() {
        RoundedPanel pnl = new RoundedPanel(0, 0, 12);
        pnl.setBackground(Color.WHITE);
        pnl.setLayout(new BorderLayout(0, 15));
        pnl.setBorder(new EmptyBorder(25, 20, 25, 20));

        JLabel lblTableTitle = new JLabel("📦 Danh Sách Sản Phẩm Trả");
        lblTableTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblTableTitle.setForeground(Colors.TEXT_PRIMARY);
        pnl.add(lblTableTitle, BorderLayout.NORTH);

        String[] cols = {"Mã SP", "Tên sản phẩm", "Đơn giá", "SL Mua", "SL Trả", "Hoàn tiền"};
        modelItems = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return c == 4;
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
        tblItems = new JTable(modelItems);
        tblItems.setRowHeight(34);
        tblItems.getTableHeader().setBackground(new Color(245, 245, 245));
        tblItems.getTableHeader().setForeground(Colors.TEXT_PRIMARY);
        tblItems.setGridColor(new Color(230, 230, 230));
        tblItems.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

        // Editor JSpinner cho cột SL Trả với max = SL Mua của từng dòng
        tblItems.getColumnModel().getColumn(4).setCellEditor(new SpinnerCellEditor());
        // Căn giữa cho các cột số
        javax.swing.table.DefaultTableCellRenderer center = new javax.swing.table.DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        tblItems.getColumnModel().getColumn(3).setCellRenderer(center);
        tblItems.getColumnModel().getColumn(4).setCellRenderer(center);
        pnl.add(new JScrollPane(tblItems), BorderLayout.CENTER);

        // Summary Footer
        JPanel pnlFooter = new JPanel(new BorderLayout(20, 0));
        pnlFooter.setOpaque(false);
        pnlFooter.setBorder(new EmptyBorder(15, 0, 0, 0));

        lblRefundTotal = new JLabel("💰 TỔNG TIỀN HOÀN: 0 đ");
        lblRefundTotal.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblRefundTotal.setForeground(new Color(229, 57, 53));

        RoundedButton btnConfirm = new RoundedButton(220, 45, 10, "✅ Xác Nhận Hoàn Tiền", new Color(27, 155, 65));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        btnConfirm.addActionListener(e -> processReturn());

        pnlFooter.add(lblRefundTotal, BorderLayout.CENTER);
        pnlFooter.add(btnConfirm, BorderLayout.EAST);
        pnl.add(pnlFooter, BorderLayout.SOUTH);

        return pnl;
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

        // Lấy thông tin đầy đủ của khách hàng (mapHoaDon chỉ set mã KH)
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

        // Lấy tên phương thức thanh toán từ mã
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
                "0 đ"
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
                return; // setValueAt sẽ kích hoạt lại handleQtyChange
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
        // Commit giá trị đang chỉnh trong cell trước khi xử lý
        if (tblItems.isEditing()) {
            tblItems.getCellEditor().stopCellEditing();
        }
        if (refundTotal <= 0) {
            JOptionPane.showMessageDialog(this, "⚠️ Vui lòng nhập số lượng trả!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Xác nhận trả hàng và hoàn tiền " + df.format(refundTotal) + " đ cho khách?", "Xác nhận", 0);
        if (confirm == 0) {
            String maHD = txtSearchMaHD.getText().trim();
            String reason = rdoLyDoLoi.isSelected() ? "Sản phẩm lỗi" : "Hết hạn sử dụng";

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
                modelItems.setRowCount(0);
                currentInvoiceItems.clear();
                updateRefundTotal();
                txtSearchMaHD.setText("");
                rdoLyDoHetHan.setSelected(true);
                lblMaHoaDon.setText("🧾 Mã hóa đơn: --");
                lblCustomerName.setText("👤 Khách hàng: --");
                lblPhone.setText("📞 SĐT: --");
                lblInvoiceDate.setText("📅 Ngày lập: --");
                lblPaymentMethod.setText("💳 Phương thức TT: --");
                lblInvoiceTotal.setText("💰 Tổng tiền HĐ: --");
            } else {
                JOptionPane.showMessageDialog(this, "❌ Có lỗi xảy ra khi cập nhật hệ thống!", "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

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

    /**
     * Cell editor dùng JSpinner cho cột SL Trả, max thay đổi theo SL Mua của
     * từng dòng.
     */
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
            }else if (value != null) {
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
}
