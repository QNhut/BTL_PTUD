package gui;

import java.awt.*;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PiePlot;
import constants.Colors;
import constants.FontStyle;
import exception.RoundedButton;
import service.SanPham_Service;

public class ThongKeSanPham_GUI extends JPanel {

    // ===== SERVICE =====
    private final SanPham_Service sanPhamService = new SanPham_Service();
    private static final NumberFormat VND = NumberFormat.getNumberInstance(new java.util.Locale("vi", "VN"));

    // ===== FILTER =====
    private JComboBox<String> cbKieu, cbNgay, cbThang, cbNam, cbQuy, cbDoanhThu;
    private RoundedButton btnXem;
    private JPanel leftPanel;

    // ===== TOGGLE VIEW =====
    private boolean isChartView = false;
    private RoundedButton btnViewChart, btnViewTable;
    private JPanel chartsPanel, tablePanel, summaryPanel;

    // ===== CARD LABELS =====
    private JLabel lblTongSP, lblSPBanChay, lblTonKho, lblKhuyenMai;

    // ===== CHART DATASETS =====
    private DefaultPieDataset      pieDataset;
    private DefaultCategoryDataset barDataset;

    // ===== TABLES =====
    private JTable tableBanChay;
    private DefaultTableModel modelBanChay;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    public ThongKeSanPham_GUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Colors.BACKGROUND);

        summaryPanel = createSummaryPanel();
        chartsPanel = createChartsPanel();
        tablePanel = createTablePanel();

        add(createFilterPanel());
        add(summaryPanel);
        add(chartsPanel);
        add(tablePanel);

        initializeDefaultFilter();
        updateViewMode();
    }

    private void initializeDefaultFilter() {
        LocalDate now = LocalDate.now();
        cbKieu.setSelectedItem("Theo thời gian cụ thể");
        cbNam.setSelectedItem(String.valueOf(now.getYear()));
        cbThang.setSelectedItem(String.valueOf(now.getMonthValue()));
        updateNgayComboBox();
        cbNgay.setSelectedItem(String.valueOf(now.getDayOfMonth()));
        
        // Đảm bảo UI đã render xong rồi mới lọc
        SwingUtilities.invokeLater(() -> performFilter());
    }

    public void refresh() {
        LocalDate now = LocalDate.now();
        cbKieu.setSelectedItem("Theo thời gian cụ thể");
        cbNam.setSelectedItem(String.valueOf(now.getYear()));
        cbThang.setSelectedItem(String.valueOf(now.getMonthValue()));
        updateNgayComboBox();
        cbNgay.setSelectedItem(String.valueOf(now.getDayOfMonth()));
        
        performFilter();
    }

    private void updateNgayComboBox() {
        String thangStr = (String) cbThang.getSelectedItem();
        String namStr = (String) cbNam.getSelectedItem();
        if (thangStr == null || namStr == null || thangStr.startsWith("Chọn") || namStr.startsWith("Chọn")) {
            return;
        }
        int thang = Integer.parseInt(thangStr);
        int nam = Integer.parseInt(namStr);
        int days = LocalDate.of(nam, thang, 1).lengthOfMonth();
        String currentNgay = (String) cbNgay.getSelectedItem();
        cbNgay.removeAllItems();
        cbNgay.addItem("Chọn ngày");
        for (int i = 1; i <= days; i++) {
            cbNgay.addItem(String.valueOf(i));
        }
        if (currentNgay != null && !currentNgay.startsWith("Chọn")) {
            int d = Integer.parseInt(currentNgay);
            if (d <= days) cbNgay.setSelectedItem(currentNgay);
        }
    }

    // ============================================================
    // FILTER PANEL — Bộ lọc và nút chuyển đổi
    // ============================================================
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Colors.SECONDARY);
        panel.setBorder(BorderFactory.createTitledBorder("Thống kê sản phẩm"));
        panel.setMaximumSize(new Dimension(9999, 80));
        panel.setPreferredSize(new Dimension(9999, 80));
        panel.setMinimumSize(new Dimension(0, 80));

        cbKieu = new JComboBox<>(new String[]{"Theo thời gian cụ thể", "Theo quý", "Theo tổng doanh thu"});
        cbKieu.setPreferredSize(new Dimension(190, 30));
        cbKieu.addActionListener(e -> updateVisibility());

        cbNam = new JComboBox<>(new String[]{"Chọn năm", "2023", "2024", "2025", "2026"});
        cbNam.setPreferredSize(new Dimension(100, 30));
        cbNam.addActionListener(e -> updateNgayComboBox());

        cbThang = new JComboBox<>(new String[]{"Chọn tháng", "1","2","3","4","5","6","7","8","9","10","11","12"});
        cbThang.setPreferredSize(new Dimension(100, 30));
        cbThang.addActionListener(e -> updateNgayComboBox());

        cbNgay = new JComboBox<>(new String[]{"Chọn ngày"});
        cbNgay.setPreferredSize(new Dimension(100, 30));

        cbQuy = new JComboBox<>(new String[]{"Chọn quý", "Quý 1", "Quý 2", "Quý 3", "Quý 4"});
        cbQuy.setPreferredSize(new Dimension(100, 30));

        cbDoanhThu = new JComboBox<>(new String[]{"Tất cả", "< 5 triệu", "5 - 20 triệu", "> 20 triệu"});
        cbDoanhThu.setPreferredSize(new Dimension(150, 30));

        leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        leftPanel.setOpaque(false);

        // --- Panel bên phải: Chuyển đổi + Xuất file ---
        btnXem = new RoundedButton(80,  30, 8, "Xem", Colors.PRIMARY);
        btnXem.addActionListener(e -> performFilter());
        RoundedButton btnExport = new RoundedButton(140, 30, 8, "Xuất báo cáo", Colors.PRIMARY);

        btnViewChart = new RoundedButton(100, 30, 15, "Biểu đồ", Colors.BACKGROUND);
        btnViewChart.setForeground(Colors.TEXT_PRIMARY);
        btnViewChart.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        
        btnViewTable = new RoundedButton(100, 30, 15, "Bảng", Colors.TEXT_PRIMARY);
        btnViewTable.setForeground(Colors.BACKGROUND);

        btnViewChart.addActionListener(e -> { isChartView = true; updateViewMode(); });
        btnViewTable.addActionListener(e -> { isChartView = false; updateViewMode(); });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        rightPanel.setOpaque(false);
        rightPanel.add(btnViewChart);
        rightPanel.add(btnViewTable);
        rightPanel.add(btnExport);

        panel.add(leftPanel,  BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        updateVisibility();
        return panel;
    }

    private void updateVisibility() {
        if (leftPanel == null) return;
        String kieu = (String) cbKieu.getSelectedItem();
        leftPanel.removeAll();
        leftPanel.add(cbKieu);
        if ("Theo thời gian cụ thể".equals(kieu)) {
            leftPanel.add(cbNam); leftPanel.add(cbThang); leftPanel.add(cbNgay);
        } else if ("Theo quý".equals(kieu)) {
            leftPanel.add(cbNam); leftPanel.add(cbQuy);
        } else if ("Theo tổng doanh thu".equals(kieu)) {
            leftPanel.add(cbDoanhThu);
        }
        leftPanel.add(btnXem);
        leftPanel.revalidate();
        leftPanel.repaint();
    }

    // ============================================================
    // SUMMARY CARDS
    // ============================================================
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        panel.setBackground(Colors.BACKGROUND);
        panel.setPreferredSize(new Dimension(9999, 140));
        panel.setMaximumSize(new Dimension(9999, 140));

        Object[] c1 = buildCard("Tổng sản phẩm",   "0", Colors.PRIMARY);
        Object[] c2 = buildCard("SP bán chạy",     "0", Colors.PRIMARY);
        Object[] c3 = buildCard("Tổng tồn kho",    "0", Colors.PRIMARY);
        Object[] c4 = buildCard("Đang khuyến mãi", "0", Colors.PRIMARY);

        lblTongSP     = (JLabel) c1[1];
        lblSPBanChay  = (JLabel) c2[1];
        lblTonKho     = (JLabel) c3[1];
        lblKhuyenMai  = (JLabel) c4[1];

        panel.add((JPanel) c1[0]);
        panel.add((JPanel) c2[0]);
        panel.add((JPanel) c3[0]);
        panel.add((JPanel) c4[0]);
        return panel;
    }

    private Object[] buildCard(String title, String value, Color accent) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Colors.BACKGROUND);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 8, 14, 14);
                g2.fillRect(0, 4, getWidth(), 8);
                g2.setColor(Colors.BORDER);
                g2.setStroke(new BasicStroke(1.2f));
                g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 14, 14);
                g2.dispose();
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(16, 14, 10, 14));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblTitle.setForeground(Colors.TEXT_SECONDARY);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblVal.setForeground(Colors.TEXT_PRIMARY);
        lblVal.setAlignmentX(LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(6));
        card.add(lblVal);
        return new Object[]{card, lblVal};
    }

    // ============================================================
    // CHARTS PANEL
    // ============================================================
    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        panel.setBackground(Colors.BACKGROUND);

        pieDataset = new DefaultPieDataset();
        JFreeChart pieChart = ChartFactory.createPieChart("Cơ cấu doanh thu sản phẩm", pieDataset, true, true, false);
        stylePie(pieChart);
        ChartPanel piePanel = new ChartPanel(pieChart);
        piePanel.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));

        barDataset = new DefaultCategoryDataset();
        JFreeChart barChart = ChartFactory.createBarChart("Số lượng bán theo tháng", "Tháng", "Số lượng", barDataset);
        styleBar(barChart, Colors.PRIMARY);
        ChartPanel barPanel = new ChartPanel(barChart);
        barPanel.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));

        panel.add(piePanel);
        panel.add(barPanel);
        return panel;
    }

    private void stylePie(JFreeChart chart) {
        chart.setBackgroundPaint(Colors.BACKGROUND);
        chart.getTitle().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setBackgroundPaint(Colors.SECONDARY);
        plot.setOutlineVisible(false);
        plot.setShadowPaint(null);
    }

    private void styleBar(JFreeChart chart, Color color) {
        chart.setBackgroundPaint(Colors.BACKGROUND);
        chart.getTitle().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Colors.SECONDARY);
        plot.setRangeGridlinePaint(Colors.BORDER);
        BarRenderer r = (BarRenderer) plot.getRenderer();
        r.setSeriesPaint(0, color);
        r.setMaximumBarWidth(0.06);
        r.setShadowVisible(false);
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        panel.setBackground(Colors.BACKGROUND);

        JPanel p1 = new JPanel(new BorderLayout());
        p1.setOpaque(false);
        p1.setBorder(BorderFactory.createTitledBorder("Top sản phẩm bán chạy"));
        
        String[] cols = {"Sản phẩm", "Số lượng", "Doanh thu"};
        modelBanChay = new DefaultTableModel(null, cols) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBanChay = createStyledTable(modelBanChay);
        
        // Cố định không cho kéo đổi cột và khóa resize
        tableBanChay.getTableHeader().setReorderingAllowed(false);
        tableBanChay.getTableHeader().setResizingAllowed(false);
        
        p1.add(new JScrollPane(tableBanChay), BorderLayout.CENTER);

        panel.add(p1, BorderLayout.CENTER);
        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        table.getTableHeader().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        table.getTableHeader().setBackground(Colors.SECONDARY);
        table.setSelectionBackground(Colors.SUCCESS_LIGHT);
        return table;
    }

    private String[] tinhKhoangNgay(Integer nam, Integer thang, Integer ngay) {
        int y = (nam != null) ? nam : LocalDate.now().getYear();
        String tuNgay, denNgay;
        if (ngay != null && thang != null) {
            LocalDate d = LocalDate.of(y, thang, ngay);
            tuNgay = d.toString();
            denNgay = d.toString();
        } else if (thang != null) {
            LocalDate first = LocalDate.of(y, thang, 1);
            tuNgay = first.toString();
            denNgay = first.withDayOfMonth(first.lengthOfMonth()).toString();
        } else {
            tuNgay = y + "-01-01";
            denNgay = y + "-12-31";
        }
        return new String[]{tuNgay, denNgay};
    }

    private void performFilter() {
        String kieu = (String) cbKieu.getSelectedItem();
        if (kieu == null) return;

        Integer nam = null;
        String doanhThu = null;
        String tuNgay, denNgay;

        switch (kieu) {
            case "Theo thời gian cụ thể": {
                String namStr = (String) cbNam.getSelectedItem();
                String thangStr = (String) cbThang.getSelectedItem();
                String ngayStr = (String) cbNgay.getSelectedItem();
                Integer n = null, t = null, d = null;
                if (namStr != null && !namStr.startsWith("Chọn")) n = Integer.parseInt(namStr);
                if (thangStr != null && !thangStr.startsWith("Chọn")) t = Integer.parseInt(thangStr);
                if (ngayStr != null && !ngayStr.startsWith("Chọn")) d = Integer.parseInt(ngayStr);
                
                if (n == null) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn năm!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                nam = n;
                String[] range = tinhKhoangNgay(n, t, d);
                tuNgay = range[0];
                denNgay = range[1];
                loadAll(tuNgay, denNgay, nam, null);
                return;
            }
            case "Theo quý": {
                String namQuyStr = (String) cbNam.getSelectedItem();
                String quyStr = (String) cbQuy.getSelectedItem();
                Integer n = null;
                if (namQuyStr != null && !namQuyStr.startsWith("Chọn")) n = Integer.parseInt(namQuyStr);
                if (n == null || quyStr == null || quyStr.startsWith("Chọn")) {
                    JOptionPane.showMessageDialog(this, "Vui lòng chọn năm và quý!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                nam = n;
                int quy = Integer.parseInt(quyStr.replace("Quý ", ""));
                int thangBD = (quy - 1) * 3 + 1;
                int thangKT = quy * 3;
                tuNgay = String.format("%d-%02d-01", nam, thangBD);
                LocalDate lastDay = LocalDate.of(nam, thangKT, 1);
                denNgay = lastDay.withDayOfMonth(lastDay.lengthOfMonth()).toString();
                loadAll(tuNgay, denNgay, nam, null);
                return;
            }
            case "Theo tổng doanh thu":
                doanhThu = (String) cbDoanhThu.getSelectedItem();
                nam = LocalDate.now().getYear();
                String[] range = tinhKhoangNgay(nam, null, null);
                tuNgay = range[0];
                denNgay = range[1];
                loadAll(tuNgay, denNgay, nam, doanhThu);
                break;
        }
    }

    private void loadAll(String tuNgay, String denNgay, Integer nam, String mucDoanhThu) {
        // --- Summary Cards ---
        SanPham_Service.ThongKeSPTongHop tk = sanPhamService.layThongKeTongHop(tuNgay, denNgay);
        lblTongSP.setText(String.valueOf(tk.tongSP));
        lblSPBanChay.setText(tk.spBanChay);
        lblTonKho.setText(String.valueOf(tk.tonKho));
        lblKhuyenMai.setText(String.valueOf(tk.khuyenMai));

        // --- Table & Pie Chart Data ---
        ArrayList<Object[]> allRows = sanPhamService.layDanhSachSPBanChay(tuNgay, denNgay);
        ArrayList<Object[]> filteredRows = new ArrayList<>();
        
        pieDataset.clear();
        LinkedHashMap<String, Double> phanLoaiMap = new LinkedHashMap<>();

        modelBanChay.setRowCount(0);

        for (Object[] row : allRows) {
            String tenSP = (String) row[1];
            String tenLoai = (String) row[2];
            int soLuong = (int) row[3];
            double doanhThu = (double) row[4];

            // Lọc theo tổng doanh thu (nếu có)
            if (mucDoanhThu != null && !"Tất cả".equals(mucDoanhThu)) {
                if ("< 5 triệu".equals(mucDoanhThu) && doanhThu >= 5_000_000) continue;
                if ("5 - 20 triệu".equals(mucDoanhThu) && (doanhThu < 5_000_000 || doanhThu > 20_000_000)) continue;
                if ("> 20 triệu".equals(mucDoanhThu) && doanhThu <= 20_000_000) continue;
            }
            
            filteredRows.add(row);
            
            modelBanChay.addRow(new Object[]{
                tenSP, soLuong, VND.format(doanhThu) + "đ"
            });

            phanLoaiMap.merge(tenLoai, doanhThu, Double::sum);
        }

        // --- Pie chart ---
        for (Map.Entry<String, Double> entry : phanLoaiMap.entrySet()) {
            pieDataset.setValue(entry.getKey(), entry.getValue());
        }

        // --- Bar chart ---
        barDataset.clear();
        LinkedHashMap<String, Integer> slThang = sanPhamService.laySoLuongBanTheoThang(nam);
        for (Map.Entry<String, Integer> entry : slThang.entrySet()) {
            barDataset.addValue(entry.getValue(), "Số lượng", entry.getKey());
        }

    }
    private void updateViewMode() {
        if (isChartView) {
            btnViewChart.setBackground(Colors.TEXT_PRIMARY); btnViewChart.setForeground(Colors.BACKGROUND);
            btnViewTable.setBackground(Colors.BACKGROUND);   btnViewTable.setForeground(Colors.TEXT_PRIMARY);
            chartsPanel.setVisible(true); tablePanel.setVisible(false);
        } else {
            btnViewChart.setBackground(Colors.BACKGROUND);   btnViewChart.setForeground(Colors.TEXT_PRIMARY);
            btnViewTable.setBackground(Colors.TEXT_PRIMARY); btnViewTable.setForeground(Colors.BACKGROUND);
            chartsPanel.setVisible(false); tablePanel.setVisible(true);
        }
        revalidate(); repaint();
    }

    public JComboBox<String> getCbKieu()      { return cbKieu; }
    public JComboBox<String> getCbNgay()      { return cbNgay; }
    public JComboBox<String> getCbThang()     { return cbThang; }
    public JComboBox<String> getCbNam()       { return cbNam; }
    public JComboBox<String> getCbQuy()       { return cbQuy; }
    public JComboBox<String> getCbDoanhThu()  { return cbDoanhThu; }
}
