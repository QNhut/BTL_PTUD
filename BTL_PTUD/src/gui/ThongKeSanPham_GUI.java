package gui;

import java.awt.*;
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

public class ThongKeSanPham_GUI extends JPanel {

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
    private JTable tableBanChay, tableTonKho;
    private DefaultTableModel modelBanChay, modelTonKho;

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

        loadMockData();
        updateViewMode();
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

        cbNam = new JComboBox<>(new String[]{"Chọn năm", "2023", "2024", "2025"});
        cbNam.setPreferredSize(new Dimension(100, 30));

        cbThang = new JComboBox<>(new String[]{"Chọn tháng", "1","2","3","4","5","6","7","8","9","10","11","12"});
        cbThang.setPreferredSize(new Dimension(100, 30));

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
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        panel.setBackground(Colors.BACKGROUND);

        // Bảng 1: Bán chạy
        JPanel p1 = new JPanel(new BorderLayout());
        p1.setOpaque(false);
        p1.setBorder(BorderFactory.createTitledBorder("Top sản phẩm bán chạy"));
        modelBanChay = new DefaultTableModel(null, new String[]{"Sản phẩm", "Số lượng", "Doanh thu"});
        tableBanChay = createStyledTable(modelBanChay);
        p1.add(new JScrollPane(tableBanChay), BorderLayout.CENTER);

        // Bảng 2: Tồn kho
        JPanel p2 = new JPanel(new BorderLayout());
        p2.setOpaque(false);
        p2.setBorder(BorderFactory.createTitledBorder("Sản phẩm tồn kho thấp"));
        modelTonKho = new DefaultTableModel(null, new String[]{"Sản phẩm", "Tồn kho", "Trạng thái"});
        tableTonKho = createStyledTable(modelTonKho);
        p2.add(new JScrollPane(tableTonKho), BorderLayout.CENTER);

        panel.add(p1);
        panel.add(p2);
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

    private void loadMockData() {
        lblTongSP.setText("342");
        lblSPBanChay.setText("Áo Thun Pro");
        lblTonKho.setText("1.248");
        lblKhuyenMai.setText("8");

        pieDataset.setValue("Áo", 45);
        pieDataset.setValue("Quần", 30);
        pieDataset.setValue("Giày", 15);
        pieDataset.setValue("Phụ kiện", 10);

        for (int i=1; i<=12; i++) barDataset.addValue(Math.random()*100+20, "Bán ra", "T"+i);

        modelBanChay.addRow(new Object[]{"Áo sơ mi nam", 150, "12.500.000đ"});
        modelBanChay.addRow(new Object[]{"Quần Jeans Nữ", 120, "9.800.000đ"});
        modelTonKho.addRow(new Object[]{"Váy công sở", 5, "Sắp hết"});
        modelTonKho.addRow(new Object[]{"Thắt lưng da", 2, "Cảnh báo"});
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

    private void performFilter() {
        // Xử lý lọc dữ liệu (Hiện tại là Mock Data)
        loadMockData(); // Refresh data
        
        // Random hóa số liệu để thấy sự thay đổi
        lblTongSP.setText(String.valueOf((int)(Math.random()*500 + 100)));
        lblSPBanChay.setText(Math.random() > 0.5 ? "Áo Sơ Mi Cao Cấp" : "Quần Tây Âu");
        
        // Thông báo giả lập
        String kieu = (String) cbKieu.getSelectedItem();
        System.out.println("Đang lọc sản phẩm theo: " + kieu);
    }


    public JComboBox<String> getCbKieu()      { return cbKieu; }
    public JComboBox<String> getCbNgay()      { return cbNgay; }
    public JComboBox<String> getCbThang()     { return cbThang; }
    public JComboBox<String> getCbNam()       { return cbNam; }
    public JComboBox<String> getCbQuy()       { return cbQuy; }
    public JComboBox<String> getCbDoanhThu()  { return cbDoanhThu; }
}
