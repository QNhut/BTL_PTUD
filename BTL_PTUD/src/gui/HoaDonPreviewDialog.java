package gui;

import constants.Colors;
import constants.FontStyle;
import dao.PhuongThucThanhToan_DAO;
import entity.PhuongThucThanhToan;
import exception.RoundedButton;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.*;
import java.io.*;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

/**
 * Dialog xem trước và in / xuất PDF hóa đơn. Không phụ thuộc thư viện ngoài –
 * PDF được tạo bằng cách nhúng ảnh JPEG.
 */
public class HoaDonPreviewDialog extends JDialog {

    // --- Kích thước trang hóa đơn (px, dùng cho Graphics2D / in / PDF) ---
    static final int PAGE_W = 559;   // A5 ngang: 148mm @ 96dpi
    static final int PAGE_H = 794;   // A5 dọc: 210mm @ 96dpi
    static final int PAGE_PAD = 28;
    // Tỷ lệ hiển thị trong dialog (không ảnh hưởng PDF/in)
    static final double DISPLAY_SCALE = 0.82;

    private final String tenKhachHang;
    private final String soDienThoai;
    private final String tenNhanVien;
    private final String thoiGian;
    private final DefaultTableModel tableModel;
    private final String maHoaDon;

    private InvoicePanel invoicePanel;

    // --- Payment phase state ---
    private ArrayList<PhuongThucThanhToan> dsPhuongThuc;
    private PhuongThucThanhToan selectedPTTT = null;
    private RoundedButton btnDropdownPTTT;   // dropdown button hiển thị PTTT đã chọn
    private CardLayout cardLayout;
    private JPanel southPanel;
    private static final String PHASE_PAYMENT = "payment";
    private static final String PHASE_PRINT = "print";

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
        this.dsPhuongThuc = new PhuongThucThanhToan_DAO().getDSPhuongThuc();
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

        // ---- South: 2-phase panel via CardLayout ----
        cardLayout = new CardLayout();
        southPanel = new JPanel(cardLayout);
        southPanel.setBackground(Colors.BACKGROUND);
        southPanel.add(buildPaymentPhase(), PHASE_PAYMENT);
        southPanel.add(buildPrintPhase(), PHASE_PRINT);
        cardLayout.show(southPanel, PHASE_PAYMENT);
        add(southPanel, BorderLayout.SOUTH);

        setSize((int) (PAGE_W * DISPLAY_SCALE) + 80,
                (int) (PAGE_H * DISPLAY_SCALE) + 200);
        setLocationRelativeTo(getParent());
        setResizable(true);
    }

    // =========================================================
    //  Phase 1 – Chọn phương thức thanh toán
    // =========================================================
    private JPanel buildPaymentPhase() {
        JPanel pnl = new JPanel(new BorderLayout(0, 8));
        pnl.setBackground(Colors.BACKGROUND);
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        // Popup menu chứa các PTTT
        JPopupMenu popupPTTT = new JPopupMenu();
        popupPTTT.setBackground(Colors.BACKGROUND);
        popupPTTT.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(6, 0, 6, 0)));

        for (PhuongThucThanhToan pt : dsPhuongThuc) {
            if (!pt.isTrangThai()) {
                continue;
            }
            JMenuItem item = new JMenuItem(pt.getTenPTTT());
            item.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            item.setForeground(Colors.TEXT_PRIMARY);
            item.setBackground(Colors.BACKGROUND);
            item.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 30));
            item.setCursor(new Cursor(Cursor.HAND_CURSOR));
            item.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    item.setBackground(Colors.PRIMARY_LIGHT);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    item.setBackground(Colors.BACKGROUND);
                }
            });
            item.addActionListener(ev -> {
                selectedPTTT = pt;
                btnDropdownPTTT.setText("▼ " + pt.getTenPTTT());
                btnDropdownPTTT.setBackground(Colors.PRIMARY);
                btnDropdownPTTT.setForeground(Color.WHITE);
            });
            popupPTTT.add(item);
        }

        // Hàng chứa label + dropdown
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setBackground(Colors.BACKGROUND);

        JLabel lbl = new JLabel("Phương thức:");
        lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lbl.setForeground(Colors.TEXT_PRIMARY);
        row.add(lbl);

        btnDropdownPTTT = new RoundedButton(200, 38, 15, "▼ Chọn hình thức", Colors.SECONDARY);
        btnDropdownPTTT.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnDropdownPTTT.setForeground(Colors.TEXT_PRIMARY);
        btnDropdownPTTT.addActionListener(e -> {
            popupPTTT.setPreferredSize(new Dimension(btnDropdownPTTT.getWidth(),
                    popupPTTT.getPreferredSize().height));
            popupPTTT.show(btnDropdownPTTT, 0, btnDropdownPTTT.getHeight() + 4);
        });
        row.add(btnDropdownPTTT);
        pnl.add(row, BorderLayout.CENTER);

        // Bottom buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnRow.setBackground(Colors.BACKGROUND);

        JButton btnCancel = new RoundedButton(110, 40, 20, "Hủy", Colors.SECONDARY);
        btnCancel.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnCancel.setForeground(Colors.TEXT_PRIMARY);
        btnCancel.addActionListener(e -> dispose());

        JButton btnConfirm = new RoundedButton(190, 40, 20, "Xác nhận thanh toán", Colors.PRIMARY);
        btnConfirm.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnConfirm.setForeground(Color.WHITE);
        btnConfirm.addActionListener(e -> confirmPayment());

        btnRow.add(btnCancel);
        btnRow.add(btnConfirm);
        pnl.add(btnRow, BorderLayout.SOUTH);

        return pnl;
    }

    // =========================================================
    //  Phase 2 – Thanh toán thành công → in / xuất PDF
    // =========================================================
    private JPanel buildPrintPhase() {
        JPanel pnl = new JPanel(new BorderLayout(0, 10));
        pnl.setBackground(Colors.BACKGROUND);
        pnl.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(14, 16, 14, 16)));

        JLabel lblOk = new JLabel("✓  Thanh toán thành công!");
        lblOk.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblOk.setForeground(Colors.SUCCESS_DARK);
        pnl.add(lblOk, BorderLayout.NORTH);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 0));
        btnRow.setBackground(Colors.BACKGROUND);

        JButton btnPDF = new RoundedButton(160, 42, 21, "Xuất PDF", Colors.PRIMARY);
        JButton btnPrint = new RoundedButton(120, 42, 21, "In", Colors.SUCCESS);
        JButton btnClose = new RoundedButton(120, 42, 21, "Đóng", Colors.SECONDARY);

        btnPDF.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnPDF.setForeground(Color.WHITE);
        btnPrint.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnPrint.setForeground(Color.WHITE);
        btnClose.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnClose.setForeground(Colors.TEXT_PRIMARY);

        btnPDF.addActionListener(e -> exportPDF());
        btnPrint.addActionListener(e -> printInvoice());
        btnClose.addActionListener(e -> dispose());

        btnRow.add(btnPDF);
        btnRow.add(btnPrint);
        btnRow.add(btnClose);
        pnl.add(btnRow, BorderLayout.SOUTH);

        return pnl;
    }

    // =========================================================
    //  Xử lý chọn PTTT và xác nhận thanh toán
    // =========================================================
    private void confirmPayment() {
        if (selectedPTTT == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn phương thức thanh toán trước khi xác nhận.",
                    "Chưa chọn phương thức", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // TODO: lưu hóa đơn vào database
        cardLayout.show(southPanel, PHASE_PRINT);
        invoicePanel.repaint();
    }

    // =========================================================
    //  Inner class – vẽ hóa đơn bằng Graphics2D
    // =========================================================
    class InvoicePanel extends JPanel {

        InvoicePanel() {
            int dw = (int) (PAGE_W * DISPLAY_SCALE) + 40;
            int dh = (int) (PAGE_H * DISPLAY_SCALE) + 40;
            setPreferredSize(new Dimension(dw, dh));
            setBackground(new Color(210, 210, 210));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            applyHints(g2);
            g2.scale(DISPLAY_SCALE, DISPLAY_SCALE);

            // Shadow
            g2.setColor(new Color(0, 0, 0, 35));
            int margin = (int) (20 / DISPLAY_SCALE * DISPLAY_SCALE); // =20
            g2.fillRoundRect(23, 23, PAGE_W + 4, PAGE_H + 4, 12, 12);

            // White page
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(20, 20, PAGE_W, PAGE_H, 12, 12);

            g2.translate(20, 20);
            drawInvoice(g2, PAGE_W, PAGE_H, PAGE_PAD);
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
            g2.fillRect(0, 0, width, 78);

            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 22));
            drawCentered(g2, "NHÀ THUỐC HTT", width, 30);
            g2.setFont(new Font("Arial", Font.PLAIN, 11));
            drawCentered(g2, "123 Đường ABC, Quận 1, TP.HCM   |   ĐT: 0901 234 567", width, 54);

            int y = 92;

            // ---- Tiêu đề hóa đơn ----
            g2.setColor(Colors.TEXT_PRIMARY);
            g2.setFont(new Font("Arial", Font.BOLD, 19));
            drawCentered(g2, "HÓA ĐƠN BÁN HÀNG", width, y + 18);
            y += 38;

            // ---- Mã & ngày ----
            g2.setFont(new Font("Arial", Font.PLAIN, 12));
            g2.setColor(Colors.TEXT_SECONDARY);
            drawCentered(g2, "Mã HĐ: " + maHoaDon + "     Ngày: " + thoiGian, width, y);
            y += 22;

            // ---- Divider ----
            drawDivider(g2, x, y, contentW);
            y += 16;

            // ---- Thông tin khách hàng / nhân viên ----
            int col2 = x + contentW / 2 + 10;

            drawLabel(g2, "Khách hàng:", x, y);
            drawValue(g2, tenKhachHang, x + 100, y);
            drawLabel(g2, "SĐT:", col2, y);
            drawValue(g2, soDienThoai, col2 + 38, y);
            y += 22;

            drawLabel(g2, "Nhân viên:", x, y);
            drawValue(g2, tenNhanVien, x + 85, y);
            y += 20;

            // ---- Divider ----
            drawDivider(g2, x, y, contentW);
            y += 14;

            // ---- Bảng sản phẩm ----
            // colW: STT | Tên sản phẩm | SL | Đơn giá | Thành tiền
            int[] colW = {36, contentW - 36 - 55 - 115 - 115, 55, 115, 115};
            String[] headers = {"STT", "Tên sản phẩm", "SL", "Đơn giá", "Thành tiền"};
            int rowH = 30;

            // Header
            g2.setColor(Colors.PRIMARY);
            g2.fillRect(x, y, contentW, rowH);
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 12));
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
                    nf.format(donGia) + "đ",
                    nf.format(thanhTien) + "đ"
                };

                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                g2.setColor(Colors.TEXT_PRIMARY);
                drawTableRow(g2, vals, colW, x, y, rowH, RowAlign.DATA);
                y += rowH;
            }

            // ---- Tổng tiền ----
            y += 10;
            g2.setColor(Colors.BORDER);
            g2.setStroke(new BasicStroke(1.2f));
            g2.drawLine(x, y, x + contentW, y);
            y += 18;

            String totalStr = "TỔNG TIỀN: " + nf.format(grandTotal) + "đ";
            g2.setFont(new Font("Arial", Font.BOLD, 15));
            g2.setColor(Colors.PRIMARY);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(totalStr, x + contentW - fm.stringWidth(totalStr), y + 18);
            y += 28;

            // ---- Phương thức thanh toán ----
            if (selectedPTTT != null) {
                g2.setFont(new Font("Arial", Font.PLAIN, 12));
                g2.setColor(Colors.TEXT_SECONDARY);
                String ptttStr = "Phương thức thanh toán: " + selectedPTTT.getTenPTTT();
                fm = g2.getFontMetrics();
                g2.drawString(ptttStr, x + contentW - fm.stringWidth(ptttStr), y + 16);
                y += 16;
            }
            y += 16;

            // ---- Footer ----
            drawDivider(g2, x, y, contentW);
            y += 18;
            g2.setFont(new Font("Arial", Font.ITALIC, 12));
            g2.setColor(Colors.TEXT_SECONDARY);
            drawCentered(g2, "Cảm ơn quý khách đã mua hàng tại nhà thuốc HTT. Hẹn gặp lại!", width, y);
            y += 42;

            // ---- Chữ ký ----
            g2.setFont(new Font("Arial", Font.BOLD, 12));
            g2.setColor(Colors.TEXT_PRIMARY);
            g2.drawString("Khách hàng", x + 20, y);
            fm = g2.getFontMetrics();
            String sigNV = "Nhân viên bán hàng";
            g2.drawString(sigNV, x + contentW - fm.stringWidth(sigNV) - 20, y);
            y += 14;

            g2.setFont(new Font("Arial", Font.ITALIC, 11));
            g2.setColor(Colors.TEXT_SECONDARY);
            String sigNote = "(Ký và ghi rõ họ tên)";
            fm = g2.getFontMetrics();
            g2.drawString(sigNote, x + 10, y);
            g2.drawString(sigNote, x + contentW - fm.stringWidth(sigNote) - 10, y);
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
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.setColor(Colors.TEXT_PRIMARY);
            g2.drawString(text, x, y);
        }

        private void drawValue(Graphics2D g2, String text, int x, int y) {
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
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
                if (i == 3 || i == 4) {
                    // right-align numeric columns
                    g2.drawString(v, cellRight - fm.stringWidth(v), y + 20);
                } else if (i == 2) {
                    // center quantity
                    g2.drawString(v, cx + (colW[i] - fm.stringWidth(v)) / 2, y + 20);
                } else if (i == 1 && mode == RowAlign.DATA) {
                    g2.drawString(clipText(g2, v, colW[i] - 10), cx, y + 20);
                } else {
                    g2.drawString(v, cx, y + 20);
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
            double sx = pageFormat.getImageableWidth() / PAGE_W;
            double sy = pageFormat.getImageableHeight() / PAGE_H;
            double scale = Math.min(sx, sy);
            g2.scale(scale, scale);
            invoicePanel.drawInvoice(g2, PAGE_W, PAGE_H, PAGE_PAD);
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
        int w = PAGE_W * SCALE;
        int h = PAGE_H * SCALE;
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        applyHints(g2);
        g2.setColor(Color.WHITE);
        g2.fillRect(0, 0, w, h);
        g2.scale(SCALE, SCALE);
        invoicePanel.drawInvoice(g2, PAGE_W, PAGE_H, PAGE_PAD);
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
