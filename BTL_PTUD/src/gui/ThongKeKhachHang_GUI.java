package gui;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PiePlot;
import constants.Colors;
import constants.FontStyle;
import exception.RoundedButton;

public class ThongKeKhachHang_GUI extends JPanel {

    // ===== FILTER =====
    private JComboBox<String> cbKieu, cbNgay, cbThang, cbNam, cbQuy, cbLoaiKH, cbChiTieu;
    private RoundedButton btnXem;
    private JPanel leftPanel;

    // ===== TOGGLE VIEW =====
    private boolean isChartView = false;
    private RoundedButton btnViewChart, btnViewTable;
    private JPanel chartsPanel, tablePanel, summaryPanel;

    // ===== CARD LABELS =====
    private JLabel lblTongKH, lblKHMoi, lblTiLeGiuChan, lblDoanhThuKH;

    // ===== CHART DATASETS =====
    private DefaultPieDataset      pieDataset;
    private DefaultCategoryDataset doanhThuDataset;
    private DefaultCategoryDataset xuHuongDataset;

    // ===== TABLE =====
    private JTable tableKhachHang;
    private DefaultTableModel tableModel;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    public ThongKeKhachHang_GUI() {
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
    // FILTER PANEL — BorderLayout: bộ lọc bên trái, nút cố định bên phải
    // ============================================================
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Colors.SECONDARY);
        panel.setBorder(BorderFactory.createTitledBorder("Thống kê khách hàng"));
        panel.setMaximumSize(new Dimension(9999, 80));
        panel.setPreferredSize(new Dimension(9999, 80));
        panel.setMinimumSize(new Dimension(0, 80));

        cbKieu = new JComboBox<>(new String[]{"Theo thời gian cụ thể", "Theo quý", "Theo loại khách hàng", "Theo tổng chi tiêu"});
        cbKieu.setPreferredSize(new Dimension(190, 30));
        cbKieu.addActionListener(e -> updateVisibility());

        // Năm đầy đủ
        cbNam = new JComboBox<>(new String[]{"Chọn năm", "2023", "2024", "2025", "2026"});
        cbNam.setPreferredSize(new Dimension(100, 30));

        // Tháng đầy đủ
        cbThang = new JComboBox<>(new String[]{"Chọn tháng", "1","2","3","4","5","6","7","8","9","10","11","12"});
        cbThang.setPreferredSize(new Dimension(100, 30));

        // Ngày
        cbNgay = new JComboBox<>(new String[]{"Chọn ngày"});
        cbNgay.setPreferredSize(new Dimension(100, 30));

        // Quý
        cbQuy = new JComboBox<>(new String[]{"Chọn quý", "Quý 1", "Quý 2", "Quý 3", "Quý 4"});
        cbQuy.setPreferredSize(new Dimension(100, 30));

        // Loại KH
        /* 
         * QUY TẮC PHÂN LOẠI KHÁCH HÀNG (Dùng cho DAO):
         * CASE 
         *   WHEN so_don <= 2 AND recency <= 30 THEN 'Khách hàng mới'
         *   WHEN so_don >= 5 AND recency <= 60 THEN 'Thường xuyên'
         *   ELSE 'Tiềm năng'
         * END
         */
        cbLoaiKH = new JComboBox<>(new String[]{"Tất cả", "Khách hàng mới", "Thường xuyên", "Tiềm năng"});
        cbLoaiKH.setPreferredSize(new Dimension(150, 30));

        // Chi tiêu
        cbChiTieu = new JComboBox<>(new String[]{"Tất cả", "< 1 triệu", "1 - 5 triệu", "> 5 triệu"});
        cbChiTieu.setPreferredSize(new Dimension(140, 30));

        // --- Panel bên trái: bộ lọc ---
        leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        leftPanel.setOpaque(false);

        // --- Panel bên phải: nút cố định, KHÔNG bao giờ wrap ---
        btnXem = new RoundedButton(80,  30, 8, "Xem", Colors.PRIMARY);
        btnXem.addActionListener(e -> performFilter());
        RoundedButton btnExport = new RoundedButton(140, 30, 8, "Xuất báo cáo", Colors.PRIMARY);

        btnViewChart = new RoundedButton(100, 30, 15, "Biểu đồ", Colors.BACKGROUND);
        btnViewChart.setForeground(Colors.TEXT_PRIMARY);
        btnViewChart.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        btnViewTable = new RoundedButton(100, 30, 15, "Bảng", Colors.TEXT_PRIMARY);
        btnViewTable.setForeground(Colors.BACKGROUND);

        btnViewChart.addActionListener(e -> {
            isChartView = true;
            updateViewMode();
        });
        btnViewTable.addActionListener(e -> {
            isChartView = false;
            updateViewMode();
        });

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 6));
        rightPanel.setOpaque(false);
        rightPanel.add(btnViewTable);
        rightPanel.add(btnViewChart);
        rightPanel.add(btnExport);

        panel.add(leftPanel,  BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        updateVisibility();
        return panel;
    }

    private void performFilter() {
        String kieu = (String) cbKieu.getSelectedItem();
        if (kieu == null) return;
        
        // Random số liệu trên các thẻ
        long tongKH = Math.round(Math.random() * 500 + 100);
        long khMoi = Math.round(Math.random() * 50 + 10);
        long tiLe = Math.round(Math.random() * 20 + 70); // 70-90%
        long doanhThu = Math.round(Math.random() * 1000000000 + 50000000);
        
        lblTongKH.setText(String.valueOf(tongKH));
        lblKHMoi.setText(String.valueOf(khMoi));
        lblTiLeGiuChan.setText(tiLe + "%");
        lblDoanhThuKH.setText(String.format("%,dđ", doanhThu).replace(",", "."));
        
        // Update Chart
        doanhThuDataset.clear();
        xuHuongDataset.clear();
        
        int n = 6;
        double[] dt = new double[n];
        for (int i = 0; i < n; i++) {
            dt[i] = Math.random() * 100000000 + 10000000;
        }
        java.util.Arrays.sort(dt); // Doanh thu từ thấp đến cao
        
        String[] cats = {"Mốc 1", "Mốc 2", "Mốc 3", "Mốc 4", "Mốc 5", "Mốc 6"};
        for (int i = 0; i < n; i++) {
            doanhThuDataset.addValue(dt[i], "Doanh thu", cats[i]);
            
            xuHuongDataset.addValue((int)(Math.random() * 40 + 10), "Thường xuyên", cats[i]);
            xuHuongDataset.addValue((int)(Math.random() * 20 + 5), "Khách hàng mới", cats[i]);
            xuHuongDataset.addValue((int)(Math.random() * 30 + 10), "Tiềm năng", cats[i]);
        }
        
        pieDataset.clear();
        if ("Theo loại khách hàng".equals(kieu)) {
            String loai = (String) cbLoaiKH.getSelectedItem();
            if ("Tất cả".equals(loai)) {
                pieDataset.setValue("Thường xuyên", Math.random() * 100);
                pieDataset.setValue("Khách hàng mới", Math.random() * 50);
                pieDataset.setValue("Tiềm năng", Math.random() * 80);
            } else {
                pieDataset.setValue(loai, 100); // 100% loại đó
            }
        } else {
            pieDataset.setValue("Thường xuyên", Math.random() * 100);
            pieDataset.setValue("Khách hàng mới", Math.random() * 50);
            pieDataset.setValue("Tiềm năng", Math.random() * 80);
        }
        
        // Update Table
        tableModel.setRowCount(0);
        int rows = (int)(Math.random() * 5 + 3);
        String[] loai = {"Thường xuyên", "Khách hàng mới", "Tiềm năng"};
        for (int i = 1; i <= rows; i++) {
            int randomLoai = (int)(Math.random() * 3);
            tableModel.addRow(new Object[]{
                i, "KH" + (int)(Math.random()*1000 + 1000), 
                "Khách hàng " + i, 
                "0912" + (int)(Math.random()*899999 + 100000), 
                (int)(Math.random()*20 + 1), 
                String.format("%,dđ", (long)(Math.random()*10000000 + 500000)).replace(",", "."), 
                loai[randomLoai]
            });
        }
    }

    private void updateVisibility() {
        if (leftPanel == null) return;
        
        String kieu = (String) cbKieu.getSelectedItem();
        
        leftPanel.removeAll();
        leftPanel.add(cbKieu);
        
        if (kieu != null) {
            switch (kieu) {
                case "Theo thời gian cụ thể":
                    leftPanel.add(cbNam);
                    leftPanel.add(cbThang);
                    leftPanel.add(cbNgay);
                    break;
                case "Theo quý":
                    leftPanel.add(cbNam);
                    leftPanel.add(cbQuy);
                    break;
                case "Theo loại khách hàng":
                    leftPanel.add(cbLoaiKH);
                    break;
                case "Theo tổng chi tiêu":
                    leftPanel.add(cbChiTieu);
                    break;
            }
        }
        
        leftPanel.add(btnXem);
        
        leftPanel.revalidate();
        leftPanel.repaint();
    }

    // ============================================================
    // SUMMARY CARDS — bo góc tròn giống ThongKeDoanhThu_GUI
    // ============================================================
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        panel.setBackground(Colors.BACKGROUND);
        panel.setPreferredSize(new Dimension(9999, 140));
        panel.setMaximumSize(new Dimension(9999, 140));
        panel.setMinimumSize(new Dimension(0, 140));

        Object[] c1 = buildCard("Tổng khách hàng",  "Đang tải...", Colors.PRIMARY);
        Object[] c2 = buildCard("Khách hàng mới",   "Đang tải...", Colors.PRIMARY);
        Object[] c3 = buildCard("Tỉ lệ giữ chân",   "Đang tải...", Colors.PRIMARY);
        Object[] c4 = buildCard("Doanh thu từ KH",   "Đang tải...", Colors.PRIMARY);

        lblTongKH      = (JLabel) c1[1];
        lblKHMoi       = (JLabel) c2[1];
        lblTiLeGiuChan = (JLabel) c3[1];
        lblDoanhThuKH  = (JLabel) c4[1];

        panel.add((JPanel) c1[0]);
        panel.add((JPanel) c2[0]);
        panel.add((JPanel) c3[0]);
        panel.add((JPanel) c4[0]);
        return panel;
    }

    /** Card bo góc tròn — kỹ thuật vẽ tay giống ThongKeDoanhThu_GUI */
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
    // CHARTS PANEL — Pie + Combined (Bar & Line)
    // ============================================================
    private JPanel createChartsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
        panel.setBackground(Colors.BACKGROUND);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // --- Pie chart ---
        pieDataset = new DefaultPieDataset();
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Phân loại khách hàng", pieDataset, true, true, false);
        pieChart.setBackgroundPaint(Colors.BACKGROUND);
        pieChart.getTitle().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        PiePlot piePlot = (PiePlot) pieChart.getPlot();
        piePlot.setBackgroundPaint(Colors.SECONDARY);
        piePlot.setSectionPaint("Thường xuyên",  Colors.TEXT_LOGIN);
        piePlot.setSectionPaint("Khách hàng mới", Colors.SUCCESS);
        piePlot.setSectionPaint("Tiềm năng",      Colors.ACCENT);
        piePlot.setOutlineVisible(false);
        piePlot.setShadowPaint(null);
        ChartPanel piePanel = new ChartPanel(pieChart);
        piePanel.setPreferredSize(new Dimension(380, 280));
        piePanel.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));

        // --- Combined chart (Bar + Line) ---
        doanhThuDataset = new DefaultCategoryDataset();
        xuHuongDataset = new DefaultCategoryDataset();

        CategoryPlot combinedPlot = new CategoryPlot();
        combinedPlot.setBackgroundPaint(Colors.SECONDARY);
        combinedPlot.setRangeGridlinePaint(Colors.BORDER);

        // Bar (Doanh thu)
        BarRenderer barRenderer = new BarRenderer();
        barRenderer.setSeriesPaint(0, Colors.PRIMARY);
        barRenderer.setMaximumBarWidth(0.1);
        barRenderer.setShadowVisible(false);
        combinedPlot.setDataset(0, doanhThuDataset);
        combinedPlot.setRenderer(0, barRenderer);

        CategoryAxis domainAxis = new CategoryAxis("Thời gian / Tiêu chí");
        combinedPlot.setDomainAxis(domainAxis);

        NumberAxis rangeAxis1 = new NumberAxis("Doanh thu");
        combinedPlot.setRangeAxis(0, rangeAxis1);
        combinedPlot.mapDatasetToRangeAxis(0, 0);

        // Line (Xu hướng 3 loại KH)
        org.jfree.chart.renderer.category.LineAndShapeRenderer lineRenderer = new org.jfree.chart.renderer.category.LineAndShapeRenderer();
        lineRenderer.setSeriesPaint(0, Colors.TEXT_LOGIN); // Thường xuyên
        lineRenderer.setSeriesPaint(1, Colors.SUCCESS); // Khách mới
        lineRenderer.setSeriesPaint(2, Colors.ACCENT); // Tiềm năng
        combinedPlot.setDataset(1, xuHuongDataset);
        combinedPlot.setRenderer(1, lineRenderer);

        NumberAxis rangeAxis2 = new NumberAxis("Số lượng KH");
        combinedPlot.setRangeAxis(1, rangeAxis2);
        combinedPlot.mapDatasetToRangeAxis(1, 1);

        combinedPlot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        JFreeChart combinedChart = new JFreeChart(
                "Doanh thu & Xu hướng khách hàng", 
                FontStyle.font(FontStyle.SM, FontStyle.BOLD), 
                combinedPlot, 
                true);
        combinedChart.setBackgroundPaint(Colors.BACKGROUND);

        ChartPanel combinedPanel = new ChartPanel(combinedChart);
        combinedPanel.setPreferredSize(new Dimension(380, 280));
        combinedPanel.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));

        panel.add(piePanel);
        panel.add(combinedPanel);
        return panel;
    }

    // ============================================================
    // TABLE PANEL — cột cố định, không kéo, không resize
    // ============================================================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 10, 10, 10),
                BorderFactory.createTitledBorder("Danh sách khách hàng")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        String[] cols = {"STT", "Mã KH", "Tên khách hàng", "SĐT", "Số đơn", "Tổng chi tiêu (đ)", "Phân loại"};
        tableModel = new DefaultTableModel(null, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        tableKhachHang = new JTable(tableModel);
        tableKhachHang.setRowHeight(26);
        tableKhachHang.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        tableKhachHang.getTableHeader().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        tableKhachHang.getTableHeader().setBackground(Colors.SECONDARY);
        tableKhachHang.setSelectionBackground(Colors.SUCCESS_LIGHT);

        // Cố định: không cho kéo đổi thứ tự cột, không cho resize cột
        tableKhachHang.getTableHeader().setReorderingAllowed(false);
        tableKhachHang.getTableHeader().setResizingAllowed(false);

        int[] widths = {40, 80, 160, 100, 65, 140, 100};
        for (int i = 0; i < widths.length; i++)
            tableKhachHang.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        panel.add(new JScrollPane(tableKhachHang), BorderLayout.CENTER);
        return panel;
    }

    // ============================================================
    // MOCK DATA
    // ============================================================
    private void loadMockData() {
        // Cards
        lblTongKH.setText("158");
        lblKHMoi.setText("38");
        lblTiLeGiuChan.setText("88%");
        lblDoanhThuKH.setText("658.290.000đ");

        // Pie chart
        pieDataset.setValue("Thường xuyên",  75);
        pieDataset.setValue("Khách hàng mới", 38);
        pieDataset.setValue("Tiềm năng",      45);

        // Combined chart
        doanhThuDataset.clear();
        xuHuongDataset.clear();
        double[] dt = {15000000, 22000000, 31000000, 45000000, 58000000, 72000000};
        String[] cats = {"Q1", "Q2", "Q3", "Q4", "Q5", "Q6"};
        for (int i = 0; i < 6; i++) {
            doanhThuDataset.addValue(dt[i], "Doanh thu", cats[i]);
            
            xuHuongDataset.addValue(20 + i*5, "Thường xuyên", cats[i]);
            xuHuongDataset.addValue(10 + i*2, "Khách hàng mới", cats[i]);
            xuHuongDataset.addValue(15 + i*3, "Tiềm năng", cats[i]);
        }

        // Table
        tableModel.setRowCount(0);
        tableModel.addRow(new Object[]{1, "KH000001", "Nguyễn Quốc Nhật", "0912 345 678", 12, "8.950.000đ",  "Thường xuyên"});
        tableModel.addRow(new Object[]{2, "KH000002", "Trần Thị B",       "0987 654 321",  9, "7.500.000đ",  "Thường xuyên"});
        tableModel.addRow(new Object[]{3, "KH000003", "Phạm Văn C",       "0901 234 567",  6, "6.200.000đ",  "Tiềm năng"});
        tableModel.addRow(new Object[]{4, "KH000004", "Lê Thị D",         "0933 111 222",  2, "1.800.000đ",  "Khách hàng mới"});
        tableModel.addRow(new Object[]{5, "KH000005", "Hoàng Văn E",      "0966 333 444",  1, "950.000đ",    "Khách hàng mới"});
    }

    private void updateViewMode() {
        if (isChartView) {
            btnViewChart.setBackground(Colors.TEXT_PRIMARY);
            btnViewChart.setForeground(Colors.BACKGROUND);
            btnViewTable.setBackground(Colors.BACKGROUND);
            btnViewTable.setForeground(Colors.TEXT_PRIMARY);
            
            chartsPanel.setVisible(true);
            tablePanel.setVisible(false);
        } else {
            btnViewChart.setBackground(Colors.BACKGROUND);
            btnViewChart.setForeground(Colors.TEXT_PRIMARY);
            btnViewTable.setBackground(Colors.TEXT_PRIMARY);
            btnViewTable.setForeground(Colors.BACKGROUND);
            
            chartsPanel.setVisible(false);
            tablePanel.setVisible(true);
        }
        
        this.revalidate();
        this.repaint();
    }

    // ============================================================
    // GETTERS (cho Controller)
    // ============================================================
    public JTable getTableKhachHang() { return tableKhachHang; }
    public JComboBox<String> getCbKieu()  { return cbKieu;  }
    public JComboBox<String> getCbNgay()  { return cbNgay;  }
    public JComboBox<String> getCbThang() { return cbThang; }
    public JComboBox<String> getCbNam()   { return cbNam;   }
    public JComboBox<String> getCbQuy()   { return cbQuy;   }
    public JComboBox<String> getCbLoaiKH() { return cbLoaiKH; }
    public JComboBox<String> getCbChiTieu() { return cbChiTieu; }
}
