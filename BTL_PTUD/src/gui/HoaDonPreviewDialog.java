package gui;

import constants.Colors;
import constants.FontStyle;
import entity.HoaDon;
import entity.NhanVien;
import exception.RoundedButton;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import service.HoaDon_Service;

/**
 * Dialog xem trước và in / xuất PDF hóa đơn. Không phụ thuộc thư viện ngoài –
 * PDF được tạo bằng cách nhúng ảnh JPEG.
 */
public class HoaDonPreviewDialog extends JDialog {

    // --- Kích thước trang hóa đơn (px, dùng cho Graphics2D) ---
    static final int PAGE_W = 240;

    static final int PAGE_PAD = 12;

    private final String tenKhachHang;
    private final String soDienThoai;
    private final String tenNhanVien;
    private final String thoiGian;
    private final DefaultTableModel tableModel;
    private final String maHoaDon;

    // Trường bổ sung cho luồng xác nhận thanh toán
    private final HoaDon_Service hoaDonService;
    private final NhanVien nhanVienEntity;
    private final String tenKHRaw;
    private final String sdtRaw;
    private final List<HoaDon_Service.CartItem> cartItems;
    private final Runnable onThanhToanThanhCong;

    private InvoicePanel invoicePanel;
    private JPanel pnlSouth; // thanh dưới – thay thế sau khi thanh toán

    // =========================================================
    // Constructor cũ: chỉ xem trước, không có nút thanh toán
    // =========================================================
    public HoaDonPreviewDialog(Frame parent,
            String tenKH, String sdt,
            String nhanVien, String thoiGian,
            DefaultTableModel model) {
        super(parent, "Hóa đơn bán hàng", true);
        this.tenKhachHang = (tenKH == null || tenKH.isBlank()) ? "Khách lẻ" : tenKH;
        this.soDienThoai = (sdt == null || sdt.isBlank()) ? "---" : sdt;
        this.tenNhanVien = (nhanVien == null || nhanVien.isBlank()) ? "---" : nhanVien;
        this.thoiGian = thoiGian;
        this.tableModel = model;
        this.maHoaDon = "HD" + System.currentTimeMillis();
        this.hoaDonService = null;
        this.nhanVienEntity = null;
        this.tenKHRaw = tenKH;
        this.sdtRaw = sdt;
        this.cartItems = null;
        this.onThanhToanThanhCong = null;
        initUI();
    }

    // =========================================================
    //==="Constructor đầy đủ: hiển thị hóa đơn + có nút xác nhận thanh toán"====
    // =========================================================
    public HoaDonPreviewDialog(Frame parent,
            String tenKH, String sdt,
            String tenNV, String thoiGian,
            DefaultTableModel model,
            String maHD,
            HoaDon_Service service,
            NhanVien nhanVien,
            List<HoaDon_Service.CartItem> items,
            Runnable onSuccess) {
        super(parent, "Hóa đơn bán hàng", true);
        this.tenKhachHang = (tenKH == null || tenKH.isBlank()) ? "Khách lẻ" : tenKH;
        this.soDienThoai = (sdt == null || sdt.isBlank()) ? "---" : sdt;
        this.tenNhanVien = (tenNV == null || tenNV.isBlank()) ? "---" : tenNV;
        this.thoiGian = thoiGian;
        this.tableModel = model;
        this.maHoaDon = maHD;
        this.hoaDonService = service;
        this.nhanVienEntity = nhanVien;
        this.tenKHRaw = tenKH;
        this.sdtRaw = sdt;
        this.cartItems = items;
        this.onThanhToanThanhCong = onSuccess;
        initUI();
    }

    // =========================================================
    //  UI
    // =========================================================
    private void initUI() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(210, 210, 210));

        invoicePanel = new InvoicePanel();
        JScrollPane scroll = new JScrollPane(invoicePanel);
        scroll.getViewport().setBackground(new Color(210, 210, 210));
        scroll.setBorder(null);
        add(scroll, BorderLayout.CENTER);

        // ---- Bottom bar (payment mode → action bar after success) ----
        pnlSouth = new JPanel(new BorderLayout());
        pnlSouth.setBackground(Colors.BACKGROUND);
        pnlSouth.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT));

        if (hoaDonService != null && cartItems != null) {
            pnlSouth.add(buildPaymentBar(), BorderLayout.CENTER);
        } else {
            pnlSouth.add(buildActionBar(false), BorderLayout.CENTER);
        }

        add(pnlSouth, BorderLayout.SOUTH);

        // --- Kích thước tự điều chỉnh theo màn hình ---
        java.awt.Rectangle screen = java.awt.GraphicsEnvironment
                .getLocalGraphicsEnvironment().getMaximumWindowBounds();
        int dlgW = Math.min(400, screen.width - 40);
        int dlgH = Math.min(800, screen.height - 40);
        setSize(dlgW, dlgH);

        // Căn giữa và đảm bảo hoàn toàn nằm trong màn hình
        setLocationRelativeTo(getParent());
        java.awt.Point loc = getLocation();
        int x = Math.max(screen.x, Math.min(loc.x, screen.x + screen.width - dlgW));
        int y = Math.max(screen.y, Math.min(loc.y, screen.y + screen.height - dlgH));
        setLocation(x, y);
        setResizable(true);
    }

    // =========================================================
    //  Inner class – vẽ hóa đơn bằng Graphics2D
    // =========================================================
    class InvoicePanel extends JPanel {

        InvoicePanel() {
            setBackground(new Color(210, 210, 210));
        }

        /**
         * Tính chiều cao thực tế của nội dung hóa đơn dựa trên số dòng.
         */
        int computePageHeight() {
            int rows = (tableModel != null) ? tableModel.getRowCount() : 0;
            // base: 58 header + nội dung cố định ~252 + mỗi dòng 20 + padding 30
            return 310 + rows * 20;
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(PAGE_W + 80, computePageHeight() + 60);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            applyHints(g2);

            int pageH = computePageHeight();
            int panelW = Math.max(getWidth(), PAGE_W + 40);
            int cardX = (panelW - PAGE_W) / 2;
            int cardY = 20;

            // Shadow
            g2.setColor(new Color(0, 0, 0, 35));
            g2.fillRoundRect(cardX + 3, cardY + 3, PAGE_W, pageH, 12, 12);

            // White page
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(cardX, cardY, PAGE_W, pageH, 12, 12);

            g2.translate(cardX, cardY);
            drawInvoice(g2, PAGE_W, pageH, PAGE_PAD);
            g2.dispose();
        }

        /**
         * Vẽ toàn bộ nội dung hóa đơn vào Graphics2D đã cho.
         */
        void drawInvoice(Graphics2D g2, int width, int height, int pad) {
            applyHints(g2);
            NumberFormat nf = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
            int contentW = width - pad * 2;
            int x = pad;

            // ---- Header (màu nền xanh) ----
            g2.setColor(Colors.PRIMARY);
            g2.fillRect(0, 0, width, 58);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 14));
            drawCentered(g2, "NHÀ THUỐC HTT", width, 20);
            g2.setFont(new Font("Arial", Font.PLAIN, 8));
            drawCentered(g2, "123 Đường ABC, Quận 1, TP.HCM", width, 34);
            drawCentered(g2, "Điện thoại: 0901 234 567", width, 46);

            int y = 66;

            // ---- Tiêu đề hóa đơn ----
            g2.setColor(Colors.TEXT_PRIMARY);
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            drawCentered(g2, "HÓA ĐƠN BÁN HÀNG", width, y + 12);
            y += 22;

            // ---- Mã & ngày ----
            g2.setFont(new Font("Arial", Font.PLAIN, 8));
            g2.setColor(Colors.TEXT_SECONDARY);
            drawCentered(g2, "Mã HĐ: " + maHoaDon, width, y);
            y += 12;
            drawCentered(g2, "Ngày: " + thoiGian, width, y);
            y += 12;

            // ---- Divider ----
            drawDivider(g2, x, y, contentW);
            y += 10;

            // ---- Thông tin khách hàng / nhân viên ----
            drawLabel(g2, "KH:", x, y);
            drawValue(g2, tenKhachHang, x + 24, y);
            y += 14;
            drawLabel(g2, "SĐT:", x, y);
            drawValue(g2, soDienThoai, x + 28, y);
            drawLabel(g2, "NV:", x + contentW / 2 + 6, y);
            drawValue(g2, tenNhanVien, x + contentW / 2 + 26, y);
            y += 14;

            // ---- Divider ----
            drawDivider(g2, x, y, contentW);
            y += 10;

            // ---- Bảng sản phẩm ----
            // colW: STT | Tên sản phẩm | SL | Thành tiền
            int[] colW = {22, contentW - 22 - 28 - 70, 28, 70};
            String[] headers = {"STT", "Tên sản phẩm", "SL", "Thành tiền"};
            int rowH = 20;

            // Header
            g2.setColor(Colors.PRIMARY);
            g2.fillRect(x, y, contentW, rowH);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 9));
            drawTableRow(g2, headers, colW, x, y, rowH, RowAlign.HEADER);
            y += rowH;

            // Rows
            long grandTotal = 0;
            for (int r = 0; r < tableModel.getRowCount(); r++) {
                Color bg = (r % 2 == 0) ? Color.WHITE : new Color(248, 250, 252);
                g2.setColor(bg);
                g2.fillRect(x, y, contentW, rowH);

                g2.setColor(Colors.BORDER_LIGHT);
                g2.setStroke(new BasicStroke(0.7f));
                g2.drawLine(x, y + rowH, x + contentW, y + rowH);

                String tenSP = String.valueOf(tableModel.getValueAt(r, 0));
                int qty = toInt(tableModel.getValueAt(r, 1));
                int donGia = toInt(tableModel.getValueAt(r, 2));
                long thanhTien = (long) qty * donGia;
                grandTotal += thanhTien;

                String[] vals = {
                    String.valueOf(r + 1),
                    tenSP,
                    String.valueOf(qty),
                    nf.format(thanhTien) + "đ"
                };

                g2.setFont(new Font("Arial", Font.PLAIN, 9));
                g2.setColor(Colors.TEXT_PRIMARY);
                drawTableRow(g2, vals, colW, x, y, rowH, RowAlign.DATA);
                y += rowH;
            }

            // ---- Tổng tiền ----
            y += 6;
            g2.setColor(Colors.BORDER);
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(x, y, x + contentW, y);
            y += 12;

            String totalStr = "TỔNG TIỀN: " + nf.format(grandTotal) + "đ";
            g2.setFont(new Font("Arial", Font.BOLD, 11));
            g2.setColor(Colors.PRIMARY);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(totalStr, x + contentW - fm.stringWidth(totalStr), y + 12);
            y += 30;

            // ---- Footer ----
            drawDivider(g2, x, y, contentW);
            y += 12;
            g2.setFont(new Font("Arial", Font.ITALIC, 9));
            g2.setColor(Colors.TEXT_SECONDARY);
            drawCentered(g2, "Cảm ơn quý khách đã mua hàng tại nhà thuốc HTT!", width, y);
            y += 26;

            // ---- Chữ ký ----
            g2.setFont(new Font("Arial", Font.BOLD, 9));
            g2.setColor(Colors.TEXT_PRIMARY);
            g2.drawString("Khách hàng", x + 10, y);
            fm = g2.getFontMetrics();
            String sigNV = "Nhân viên bán hàng";
            g2.drawString(sigNV, x + contentW - fm.stringWidth(sigNV) - 10, y);
            y += 10;

            g2.setFont(new Font("Arial", Font.ITALIC, 8));
            g2.setColor(Colors.TEXT_SECONDARY);
            String sigNote = "(Ký và ghi rõ họ tên)";
            fm = g2.getFontMetrics();
            g2.drawString(sigNote, x + 6, y);
            g2.drawString(sigNote, x + contentW - fm.stringWidth(sigNote) - 6, y);
        }

        // ----- helpers vẽ -----
        private void drawCentered(Graphics2D g2, String text, int width, int y) {
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(text, (width - fm.stringWidth(text)) / 2, y);
        }

        private void drawDivider(Graphics2D g2, int x, int y, int w) {
            g2.setColor(Colors.BORDER_LIGHT);
            g2.setStroke(new BasicStroke(1f));
            g2.drawLine(x, y, x + w, y);
        }

        private void drawLabel(Graphics2D g2, String text, int x, int y) {
            g2.setFont(new Font("Arial", Font.BOLD, 9));
            g2.setColor(Colors.TEXT_PRIMARY);
            g2.drawString(text, x, y);
        }

        private void drawValue(Graphics2D g2, String text, int x, int y) {
            g2.setFont(new Font("Arial", Font.PLAIN, 9));
            g2.setColor(Colors.TEXT_SECONDARY);
            g2.drawString(text, x, y);
        }

        private enum RowAlign {
            HEADER, DATA
        }

        private void drawTableRow(Graphics2D g2, String[] vals, int[] colW,
                int x, int y, int unusedRowH, RowAlign mode) {
            FontMetrics fm = g2.getFontMetrics();
            int cx = x + 5;
            for (int i = 0; i < vals.length; i++) {
                String v = vals[i];
                int cellRight = cx + colW[i] - 6;
                if (i == 3) {
                    // right-align numeric column
                    g2.drawString(v, cellRight - fm.stringWidth(v), y + 14);
                } else if (i == 2) {
                    // center quantity
                    g2.drawString(v, cx + (colW[i] - fm.stringWidth(v)) / 2, y + 14);
                } else if (i == 1 && mode == RowAlign.DATA) {
                    g2.drawString(clipText(g2, v, colW[i] - 6), cx, y + 14);
                } else {
                    g2.drawString(v, cx, y + 14);
                }
                cx += colW[i];
            }
        }

        private String clipText(Graphics2D g2, String text, int maxW) {
            FontMetrics fm = g2.getFontMetrics();
            if (fm.stringWidth(text) <= maxW) {
                return text;
            }
            while (!text.isEmpty() && fm.stringWidth(text + "...") > maxW) {
                text = text.substring(0, text.length() - 1);
            }
            return text + "...";
        }
    }

    // =========================================================
    //===="Thanh chọn phương thức thanh toán + nút xác nhận (trước khi thanh toán)"=====
    // =========================================================
    @SuppressWarnings("unchecked")
    private JPanel buildPaymentBar() {
        java.util.List<entity.PhuongThucThanhToan> dsPTTT = hoaDonService.getDSPhuongThucThanhToan();
        JComboBox<entity.PhuongThucThanhToan> cmbPTTT = new JComboBox<>();
        for (entity.PhuongThucThanhToan pt : dsPTTT) {
            cmbPTTT.addItem(pt);
        }
        cmbPTTT.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
            JLabel lbl = new JLabel(value != null ? value.getTenPTTT() : "");
            lbl.setOpaque(true);
            lbl.setBackground(isSelected ? Colors.PRIMARY_LIGHT : Color.WHITE);
            lbl.setForeground(Colors.TEXT_PRIMARY);
            lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            return lbl;
        });
        cmbPTTT.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        cmbPTTT.setPreferredSize(new Dimension(140, 30));

        JButton btnThanhToan = new RoundedButton(155, 34, 17, "✓ Xác nhận", Colors.SUCCESS);
        btnThanhToan.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.addActionListener(e -> {
            entity.PhuongThucThanhToan selected
                    = (entity.PhuongThucThanhToan) cmbPTTT.getSelectedItem();
            String maPTTT = (selected != null) ? selected.getMaPTTT() : null;
            xuLyThanhToan(btnThanhToan, maPTTT);
        });

        JButton btnHuy = new RoundedButton(70, 34, 17, "Hủy", Colors.SECONDARY);
        btnHuy.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnHuy.setForeground(Colors.TEXT_PRIMARY);
        btnHuy.addActionListener(e -> dispose());

        JLabel lblPTTT = new JLabel("Phương thức thanh toán:");
        lblPTTT.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblPTTT.setForeground(Colors.TEXT_PRIMARY);

        // Hàng 1: label + combobox
        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 6));
        row1.setBackground(Colors.BACKGROUND);
        row1.add(lblPTTT);
        row1.add(cmbPTTT);

        // Hàng 2: nút xác nhận + hủy
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 4));
        row2.setBackground(Colors.BACKGROUND);
        row2.add(btnThanhToan);
        row2.add(btnHuy);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Colors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        panel.add(row1);
        panel.add(row2);
        return panel;
    }

    // =========================================================
    //===="Thanh hành động (Xuất PDF / In / Đóng) sau khi thanh toán thành công"=====
    // =========================================================
    private JPanel buildActionBar(boolean callbackOnClose) {
        JButton btnPDF = new RoundedButton(110, 34, 17, "Xuất PDF", Colors.PRIMARY);
        JButton btnPrint = new RoundedButton(80, 34, 17, "In", Colors.SUCCESS);
        JButton btnClose = new RoundedButton(80, 34, 17, callbackOnClose ? "Đóng" : "Hủy", Colors.SECONDARY);

        btnPDF.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnPDF.setForeground(Color.WHITE);
        btnPrint.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnPrint.setForeground(Color.WHITE);
        btnClose.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnClose.setForeground(Colors.TEXT_PRIMARY);

        btnPDF.addActionListener(e -> exportPDF());
        btnPrint.addActionListener(e -> printInvoice());
        btnClose.addActionListener(e -> {
            dispose();
            if (callbackOnClose && onThanhToanThanhCong != null) {
                onThanhToanThanhCong.run();
            }
        });

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
        panel.setBackground(Colors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 0));
        panel.add(btnPDF);
        panel.add(btnPrint);
        panel.add(btnClose);
        return panel;
    }

    // =========================================================
    //===="Xử lý thanh toán: lưu hóa đơn vào DB, cập nhật tồn kho, đổi sang thanh hành động"=====
    // =========================================================
    private void xuLyThanhToan(JButton btnThanhToan, String maPTTT) {
        btnThanhToan.setEnabled(false);
        btnThanhToan.setText("Đang xử lý...");
        SwingWorker<HoaDon, Void> worker = new SwingWorker<>() {
            @Override
            protected HoaDon doInBackground() {
                return hoaDonService.taoHoaDon(
                        maHoaDon, tenKHRaw, sdtRaw, nhanVienEntity, cartItems, maPTTT);
            }

            @Override
            protected void done() {
                try {
                    HoaDon hd = get();
                    JOptionPane.showMessageDialog(HoaDonPreviewDialog.this,
                            "Thanh toán thành công!\nMã hóa đơn: " + hd.getMaHoaDon(),
                            "Thành công", JOptionPane.INFORMATION_MESSAGE);
                    // Chuyển sang thanh hành động (Xuất PDF / In / Đóng)
                    pnlSouth.removeAll();
                    pnlSouth.add(buildActionBar(true), BorderLayout.CENTER);
                    pnlSouth.revalidate();
                    pnlSouth.repaint();
                } catch (Exception ex) {
                    btnThanhToan.setEnabled(true);
                    btnThanhToan.setText("✓ Xác nhận");
                    Throwable cause = ex.getCause();
                    String msg = (cause != null) ? cause.getMessage() : ex.getMessage();
                    JOptionPane.showMessageDialog(HoaDonPreviewDialog.this,
                            "Lỗi khi thanh toán: " + msg,
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }

    // =========================================================
    //  In hóa đơn
    // =========================================================
    private void printInvoice() {
        PrinterJob job = PrinterJob.getPrinterJob();
        job.setJobName("Hoa don - " + maHoaDon);
        job.setPrintable((graphics, pageFormat, pageIndex) -> {
            if (pageIndex > 0) {
                return Printable.NO_SUCH_PAGE;
            }
            Graphics2D g2 = (Graphics2D) graphics;
            g2.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
            int dynH = invoicePanel.computePageHeight();
            double sx = pageFormat.getImageableWidth() / PAGE_W;
            double sy = pageFormat.getImageableHeight() / dynH;
            double scale = Math.min(sx, sy);
            g2.scale(scale, scale);
            invoicePanel.drawInvoice(g2, PAGE_W, dynH, PAGE_PAD);
            return Printable.PAGE_EXISTS;
        });
        if (job.printDialog()) {
            try {
                job.print();
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi khi in: " + ex.getMessage(), "Lỗi in",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================
    //  Xuất PDF (không cần thư viện ngoài – nhúng ảnh JPEG)
    // =========================================================
    private void exportPDF() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu hóa đơn dưới dạng PDF");
        chooser.setFileFilter(new FileNameExtensionFilter("PDF file (*.pdf)", "pdf"));
        chooser.setSelectedFile(new java.io.File(maHoaDon + ".pdf"));
        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) {
            return;
        }

        java.io.File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new java.io.File(file.getAbsolutePath() + ".pdf");
        }
        try {
            BufferedImage img = renderToImage();
            writePDF(img, file);
            JOptionPane.showMessageDialog(this,
                    "Đã xuất PDF thành công!\n" + file.getAbsolutePath(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất PDF: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Render nội dung hóa đơn vào BufferedImage (2× để sắc nét).
     */
    private BufferedImage renderToImage() {
        final int SCALE = 2;
        int dynH = invoicePanel.computePageHeight();
        int w = PAGE_W * SCALE;
        int h = dynH * SCALE;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        applyHints(g2);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        g2.scale(SCALE, SCALE);
        invoicePanel.drawInvoice(g2, PAGE_W, dynH, PAGE_PAD);
        g2.dispose();
        return img;
    }

    /**
     * Tạo file PDF tối giản: nhúng ảnh JPEG dùng DCTDecode. Không cần thư viện
     * ngoài, hỗ trợ tiếng Việt đầy đủ.
     */
    private void writePDF(BufferedImage img, java.io.File outFile) throws IOException {
        // --- Mã hóa ảnh sang JPEG ---
        ByteArrayOutputStream jpegBuf = new ByteArrayOutputStream();
        ImageIO.write(img, "JPEG", jpegBuf);
        byte[] jpegBytes = jpegBuf.toByteArray();

        // --- Fit ảnh vào A4 (595×842 pt) ---
        int pageW = 595, pageH = 842;
        double scale = Math.min((double) pageW / img.getWidth(),
                (double) pageH / img.getHeight()) * 0.96;
        int drawW = (int) (img.getWidth() * scale);
        int drawH = (int) (img.getHeight() * scale);
        int drawX = (pageW - drawW) / 2;
        int drawY = (pageH - drawH) / 2;

        // --- Content stream đặt ảnh ---
        String cs = "q\n" + drawW + " 0 0 " + drawH + " " + drawX + " " + drawY + " cm\n/Img Do\nQ\n";
        byte[] csb = cs.getBytes("ISO-8859-1");

        // --- Tạo các PDF object ---
        byte[] o1 = "1 0 obj\n<< /Type /Catalog /Pages 2 0 R >>\nendobj\n"
                .getBytes("ISO-8859-1");
        byte[] o2 = "2 0 obj\n<< /Type /Pages /Kids [3 0 R] /Count 1 >>\nendobj\n"
                .getBytes("ISO-8859-1");
        byte[] o3 = ("3 0 obj\n<< /Type /Page /Parent 2 0 R"
                + " /MediaBox [0 0 " + pageW + " " + pageH + "]"
                + "\n/Contents 4 0 R /Resources << /XObject << /Img 5 0 R >> >> >>\nendobj\n")
                .getBytes("ISO-8859-1");
        byte[] o4h = ("4 0 obj\n<< /Length " + csb.length + " >>\nstream\n")
                .getBytes("ISO-8859-1");
        byte[] o4e = "\nendstream\nendobj\n".getBytes("ISO-8859-1");
        byte[] o5h = ("5 0 obj\n<< /Type /XObject /Subtype /Image"
                + "\n/Width " + img.getWidth() + " /Height " + img.getHeight()
                + "\n/ColorSpace /DeviceRGB /BitsPerComponent 8"
                + "\n/Filter /DCTDecode /Length " + jpegBytes.length + " >>\nstream\n")
                .getBytes("ISO-8859-1");
        byte[] o5e = "\nendstream\nendobj\n".getBytes("ISO-8859-1");

        // PDF binary header (4 bytes >127 → đánh dấu file nhị phân)
        byte[] pdfHdr = "%PDF-1.4\n%\u00e2\u00e3\u00cf\u00d3\n".getBytes("ISO-8859-1");

        // --- Tính byte offset từng object để xref đúng ---
        int[] off = new int[5];
        int pos = pdfHdr.length;
        off[0] = pos;
        pos += o1.length;
        off[1] = pos;
        pos += o2.length;
        off[2] = pos;
        pos += o3.length;
        off[3] = pos;
        pos += o4h.length + csb.length + o4e.length;
        off[4] = pos;
        int xrefStart = pos + o5h.length + jpegBytes.length + o5e.length;

        // --- xref table ---
        StringBuilder xb = new StringBuilder();
        xb.append("xref\n0 6\n");
        xb.append("0000000000 65535 f \n");
        for (int o : off) {
            xb.append(String.format("%010d 00000 n \n", o));
        }
        xb.append("trailer\n<< /Size 6 /Root 1 0 R >>\n");
        xb.append("startxref\n").append(xrefStart).append("\n%%EOF\n");
        byte[] xrefBytes = xb.toString().getBytes("ISO-8859-1");

        // --- Ghi file ---
        try (FileOutputStream fos = new FileOutputStream(outFile)) {
            fos.write(pdfHdr);
            fos.write(o1);
            fos.write(o2);
            fos.write(o3);
            fos.write(o4h);
            fos.write(csb);
            fos.write(o4e);
            fos.write(o5h);
            fos.write(jpegBytes);
            fos.write(o5e);
            fos.write(xrefBytes);
        }
    }

    // =========================================================
    //  Utilities
    // =========================================================
    private static void applyHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
    }

    private static int toInt(Object val) {
        if (val instanceof Number) {
            return ((Number) val).intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(val));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
