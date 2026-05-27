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
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.chart.plot.PiePlot;
import constants.Colors;
import constants.FontStyle;
import exception.RoundedButton;
import exception.RoundedPanel;
import service.SanPham_Service;
import util.AsyncLoader;

@SuppressWarnings("serial")
public class ThongKeSanPham_GUI extends JPanel {

    // ===== SERVICE =====
    private final SanPham_Service sanPhamService = new SanPham_Service();
    private static final NumberFormat VND = constants.Formats.VND;

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
    private DefaultPieDataset<String> pieDataset;
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

        add(createHeaderPanel());

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setOpaque(false);
        body.setAlignmentX(LEFT_ALIGNMENT);
        body.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));
        body.add(createFilterPanel());
        body.add(Box.createVerticalStrut(10));
        body.add(summaryPanel);
        body.add(Box.createVerticalStrut(10));
        body.add(chartsPanel);
        body.add(Box.createVerticalStrut(10));
        body.add(tablePanel);
        add(body);

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

        // Panel khởi tạo xong; dữ liệu sẽ được tải khi tab được mở lần đầu qua refresh()
        // SwingUtilities.invokeLater(() -> performFilter()); // Đã xóa: vẫn chạy trên EDT
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
            if (d <= days) {
                cbNgay.setSelectedItem(currentNgay);
            }
        }
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setAlignmentX(LEFT_ALIGNMENT);
        header.setBorder(BorderFactory.createEmptyBorder(14, 14, 8, 14));

        JLabel lblTitle = new JLabel("Thống kê sản phẩm");
        lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.FOREGROUND);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Báo cáo doanh số và tình trạng tồn kho");
        lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSub.setForeground(Colors.MUTED);
        lblSub.setAlignmentX(LEFT_ALIGNMENT);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(2));
        header.add(lblSub);
        return header;
    }

    // ============================================================
    // FILTER PANEL
    // ============================================================
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 175));
        panel.setLayout(new BorderLayout());

        RoundedPanel filterCard = new RoundedPanel(1200, 170, 18);
        filterCard.setBackground(Colors.BACKGROUND);
        filterCard.setLayout(new BoxLayout(filterCard, BoxLayout.Y_AXIS));
        filterCard.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JPanel headerRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerRow.setOpaque(false);
        headerRow.setAlignmentX(LEFT_ALIGNMENT);
        JLabel lblHeader = new JLabel("Thống kê");
        lblHeader.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblHeader.setForeground(Colors.TEXT_PRIMARY);
        headerRow.add(lblHeader);

        cbKieu = new JComboBox<>(new String[]{"Theo thời gian cụ thể", "Theo quý", "Theo tổng doanh thu"});
        styleControl(cbKieu, 220, 42);
        cbKieu.addActionListener(e -> updateVisibility());

        cbNam = new JComboBox<>();
        cbNam.addItem("Chọn năm");
        int currentYear = java.time.LocalDate.now().getYear();
        for (int y = 2023; y <= currentYear; y++) {
            cbNam.addItem(String.valueOf(y));
        }
        styleControl(cbNam, 120, 42);
        cbNam.addActionListener(e -> updateNgayComboBox());

        cbThang = new JComboBox<>(new String[]{"Chọn tháng", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
        styleControl(cbThang, 130, 42);
        cbThang.addActionListener(e -> updateNgayComboBox());

        cbNgay = new JComboBox<>(new String[]{"Chọn ngày"});
        styleControl(cbNgay, 120, 42);

        cbQuy = new JComboBox<>(new String[]{"Chọn quý", "Quý 1", "Quý 2", "Quý 3", "Quý 4"});
        styleControl(cbQuy, 120, 42);

        cbDoanhThu = new JComboBox<>(new String[]{"Tất cả", "< 5 triệu", "5 - 20 triệu", "> 20 triệu"});
        styleControl(cbDoanhThu, 160, 42);

        leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        leftPanel.setAlignmentX(LEFT_ALIGNMENT);

        btnXem = new RoundedButton(100, 38, 12, "Lọc", Colors.PRIMARY);
        btnXem.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        btnXem.addActionListener(e -> performFilter());

        btnViewTable = new RoundedButton(100, 38, 12, "Bảng", Colors.TEXT_PRIMARY);
        btnViewTable.setForeground(Colors.BACKGROUND);
        btnViewTable.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));

        btnViewChart = new RoundedButton(120, 38, 12, "Biểu đồ", Colors.SECONDARY);
        btnViewChart.setForeground(Colors.TEXT_PRIMARY);
        btnViewChart.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        btnViewChart.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));

        RoundedButton btnExport = new RoundedButton(140, 38, 12, "Xuất CSV", Colors.PRIMARY);
        btnExport.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        btnExport.addActionListener(e -> service.ExcelExporter.xuatTable(this,
                "BÁO CÁO SẢN PHẨM BÁN CHẠY", "SanPham", "BaoCao_SanPham", tableBanChay));

        btnViewChart.addActionListener(e -> {
            isChartView = true;
            updateViewMode();
        });
        btnViewTable.addActionListener(e -> {
            isChartView = false;
            updateViewMode();
        });

        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionRow.setOpaque(false);
        actionRow.setAlignmentX(LEFT_ALIGNMENT);
        actionRow.add(btnViewTable);
        actionRow.add(btnViewChart);
        actionRow.add(btnExport);

        filterCard.add(headerRow);
        filterCard.add(Box.createVerticalStrut(8));
        filterCard.add(leftPanel);
        filterCard.add(Box.createVerticalStrut(8));
        filterCard.add(actionRow);

        panel.add(filterCard, BorderLayout.CENTER);

        updateVisibility();
        return panel;
    }

    private void updateVisibility() {
        if (leftPanel == null) {
            return;
        }
        String kieu = (String) cbKieu.getSelectedItem();
        leftPanel.removeAll();
        leftPanel.add(cbKieu);
        if ("Theo thời gian cụ thể".equals(kieu)) {
            leftPanel.add(cbNam);
            leftPanel.add(cbThang);
            leftPanel.add(cbNgay);
        } else if ("Theo quý".equals(kieu)) {
            leftPanel.add(cbNam);
            leftPanel.add(cbQuy);
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
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.setBackground(Colors.BACKGROUND);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setPreferredSize(new Dimension(9999, 120));
        panel.setMaximumSize(new Dimension(9999, 120));
        panel.setMinimumSize(new Dimension(0, 120));

        Object[] c1 = buildCard("Tổng sản phẩm", "0", Colors.PRIMARY);
        Object[] c2 = buildCard("SP bán chạy", "0", Colors.PRIMARY);
        Object[] c3 = buildCard("Tổng tồn kho", "0", Colors.PRIMARY);
        Object[] c4 = buildCard("Đang khuyến mãi", "0", Colors.PRIMARY);

        lblTongSP = (JLabel) c1[1];
        lblSPBanChay = (JLabel) c2[1];
        lblTonKho = (JLabel) c3[1];
        lblKhuyenMai = (JLabel) c4[1];

        panel.add((JPanel) c1[0]);
        panel.add((JPanel) c2[0]);
        panel.add((JPanel) c3[0]);
        panel.add((JPanel) c4[0]);
        return panel;
    }

    // Card KPI với nền trắng bo góc, thanh accent trên cùng
    private Object[] buildCard(String title, String value, Color accent) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth();
                int h = getHeight();
                java.awt.geom.RoundRectangle2D shape
                        = new java.awt.geom.RoundRectangle2D.Float(0, 0, w - 1, h - 1, 16, 16);
                g2.setClip(shape);
                g2.setColor(Colors.BACKGROUND);
                g2.fillRect(0, 0, w, h);
                g2.setColor(accent);
                g2.fillRect(0, 0, w, 8);
                g2.setClip(null);
                g2.setColor(Colors.BORDER_LIGHT);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, w - 1, h - 1, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 16, 14, 16));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblTitle.setForeground(Colors.TEXT_SECONDARY);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblVal = new JLabel(value);
        lblVal.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblVal.setForeground(Colors.TEXT_PRIMARY);
        lblVal.setAlignmentX(LEFT_ALIGNMENT);

        card.add(lblTitle);
        card.add(Box.createVerticalStrut(8));
        card.add(lblVal);
        card.add(Box.createVerticalGlue());
        return new Object[]{card, lblVal};
    }

    // ============================================================
    // CHARTS PANEL
    // ============================================================
    private JPanel createChartsPanel() {
        JPanel panel = new RoundedPanel(1200, 360, 16);
        panel.setLayout(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 12, 14));
        panel.setBackground(Colors.BACKGROUND);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

        JLabel chartTitle = new JLabel("Biểu đồ phân tích sản phẩm");
        chartTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        chartTitle.setForeground(Colors.TEXT_PRIMARY);

        JPanel chartContainer = new JPanel(new GridLayout(1, 2, 10, 0));
        chartContainer.setOpaque(false);

        pieDataset = new DefaultPieDataset<String>();
        JFreeChart pieChart = ChartFactory.createPieChart("Cơ cấu doanh thu sản phẩm", pieDataset, true, true, false);
        stylePie(pieChart);
        ChartPanel piePanel = new ChartPanel(pieChart);
        piePanel.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));

        barDataset = new DefaultCategoryDataset();
        JFreeChart barChart = ChartFactory.createBarChart("Số lượng bán theo tháng", "Tháng", "Số lượng", barDataset);
        styleBar(barChart, Colors.PRIMARY);
        ChartPanel barPanel = new ChartPanel(barChart);
        barPanel.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));

        chartContainer.add(piePanel);
        chartContainer.add(barPanel);
        panel.add(chartTitle, BorderLayout.NORTH);
        panel.add(chartContainer, BorderLayout.CENTER);
        return panel;
    }

    @SuppressWarnings("unchecked")
    private void stylePie(JFreeChart chart) {
        chart.setBackgroundPaint(Colors.BACKGROUND);
        chart.getTitle().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        PiePlot<String> plot = (PiePlot<String>) chart.getPlot();
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
        JPanel panel = new RoundedPanel(1200, 360, 16);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(Colors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 12, 14));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel title = new JLabel("Top sản phẩm bán chạy");
        title.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        title.setForeground(Colors.TEXT_PRIMARY);

        String[] cols = {"Sản phẩm", "Số lượng", "Doanh thu"};
        modelBanChay = new DefaultTableModel(null, cols) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableBanChay = createStyledTable(modelBanChay);

        tableBanChay.getTableHeader().setReorderingAllowed(false);
        tableBanChay.getTableHeader().setResizingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(tableBanChay);
        scrollPane.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        scrollPane.getViewport().setBackground(Colors.BACKGROUND);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(36);
        table.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        table.getTableHeader().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        table.getTableHeader().setBackground(Colors.SECONDARY);
        table.getTableHeader().setForeground(Colors.TEXT_PRIMARY);
        table.getTableHeader().setPreferredSize(
                new Dimension(table.getTableHeader().getPreferredSize().width, 40));
        table.setSelectionBackground(Colors.SUCCESS_LIGHT);
        table.setGridColor(Colors.BORDER_LIGHT);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        ((javax.swing.table.DefaultTableCellRenderer) table.getDefaultRenderer(Object.class))
                .setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        return table;
    }

    private String[] tinhKhoangNgay(Integer nam, Integer thang, Integer ngay) {
        return constants.Formats.tinhKhoangNgay(nam, thang, ngay);
    }

    private void performFilter() {
        String kieu = (String) cbKieu.getSelectedItem();
        if (kieu == null) {
            return;
        }

        Integer nam = null;
        String doanhThu = null;
        String tuNgay, denNgay;

        switch (kieu) {
            case "Theo thời gian cụ thể": {
                String namStr = (String) cbNam.getSelectedItem();
                String thangStr = (String) cbThang.getSelectedItem();
                String ngayStr = (String) cbNgay.getSelectedItem();
                Integer n = null, t = null, d = null;
                if (namStr != null && !namStr.startsWith("Chọn")) {
                    n = Integer.parseInt(namStr);
                }
                if (thangStr != null && !thangStr.startsWith("Chọn")) {
                    t = Integer.parseInt(thangStr);
                }
                if (ngayStr != null && !ngayStr.startsWith("Chọn")) {
                    d = Integer.parseInt(ngayStr);
                }

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
                if (namQuyStr != null && !namQuyStr.startsWith("Chọn")) {
                    n = Integer.parseInt(namQuyStr);
                }
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
        btnXem.setEnabled(false);
        AsyncLoader.run(
            () -> {
                SanPham_Service.ThongKeSPTongHop tk = sanPhamService.layThongKeTongHop(tuNgay, denNgay);
                ArrayList<Object[]> allRows = sanPhamService.layDanhSachSPBanChay(tuNgay, denNgay);
                LinkedHashMap<String, Integer> slThang = sanPhamService.laySoLuongBanTheoThang(nam);
                return new Object[]{tk, allRows, slThang};
            },
            data -> {
                btnXem.setEnabled(true);
                SanPham_Service.ThongKeSPTongHop tk = (SanPham_Service.ThongKeSPTongHop) data[0];
                @SuppressWarnings("unchecked")
                ArrayList<Object[]> allRows = (ArrayList<Object[]>) data[1];
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Integer> slThang = (LinkedHashMap<String, Integer>) data[2];

                lblTongSP.setText(String.valueOf(tk.tongSP));
                lblSPBanChay.setText(tk.spBanChay);
                lblTonKho.setText(String.valueOf(tk.tonKho));
                lblKhuyenMai.setText(String.valueOf(tk.khuyenMai));

                pieDataset.clear();
                LinkedHashMap<String, Double> phanLoaiMap = new LinkedHashMap<>();
                modelBanChay.setRowCount(0);
                for (Object[] row : allRows) {
                    String tenSP = (String) row[1];
                    String tenLoai = (String) row[2];
                    int soLuong = (int) row[3];
                    double doanhThu = (double) row[4];
                    if (mucDoanhThu != null && !"Tất cả".equals(mucDoanhThu)) {
                        if ("< 5 triệu".equals(mucDoanhThu) && doanhThu >= 5_000_000) continue;
                        if ("5 - 20 triệu".equals(mucDoanhThu) && (doanhThu < 5_000_000 || doanhThu > 20_000_000)) continue;
                        if ("> 20 triệu".equals(mucDoanhThu) && doanhThu <= 20_000_000) continue;
                    }
                    modelBanChay.addRow(new Object[]{tenSP, soLuong, VND.format(doanhThu) + "đ"});
                    phanLoaiMap.merge(tenLoai, doanhThu, Double::sum);
                }
                for (Map.Entry<String, Double> entry : phanLoaiMap.entrySet()) {
                    pieDataset.setValue(entry.getKey(), entry.getValue());
                }
                barDataset.clear();
                for (Map.Entry<String, Integer> entry : slThang.entrySet()) {
                    barDataset.addValue(entry.getValue(), "Số lượng", entry.getKey());
                }
            }
        );
    }

    private void updateViewMode() {
        if (isChartView) {
            btnViewChart.setBackground(Colors.TEXT_PRIMARY);
            btnViewChart.setForeground(Colors.SECONDARY);
            btnViewChart.setBorder(null);
            btnViewTable.setBackground(Colors.SECONDARY);
            btnViewTable.setForeground(Colors.TEXT_PRIMARY);
            btnViewTable.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
            chartsPanel.setVisible(true);
            tablePanel.setVisible(false);
        } else {
            btnViewChart.setBackground(Colors.SECONDARY);
            btnViewChart.setForeground(Colors.TEXT_PRIMARY);
            btnViewChart.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
            btnViewTable.setBackground(Colors.TEXT_PRIMARY);
            btnViewTable.setForeground(Colors.BACKGROUND);
            btnViewTable.setBorder(null);
            chartsPanel.setVisible(false);
            tablePanel.setVisible(true);
        }
        revalidate();
        repaint();
    }

    public JComboBox<String> getCbKieu() {
        return cbKieu;
    }

    public JComboBox<String> getCbNgay() {
        return cbNgay;
    }

    public JComboBox<String> getCbThang() {
        return cbThang;
    }

    public JComboBox<String> getCbNam() {
        return cbNam;
    }

    public JComboBox<String> getCbQuy() {
        return cbQuy;
    }

    public JComboBox<String> getCbDoanhThu() {
        return cbDoanhThu;
    }

    private void styleControl(JComponent control, int width, int height) {
        Dimension size = new Dimension(width, height);
        control.setPreferredSize(size);
        control.setMinimumSize(size);
        control.setMaximumSize(size);
        control.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        control.setBackground(Colors.BACKGROUND);
        control.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(6, 10, 6, 10)));
    }
}
