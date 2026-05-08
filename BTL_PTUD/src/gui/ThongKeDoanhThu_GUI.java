package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.DefaultCategoryDataset;

import com.toedter.calendar.JDateChooser;

import constants.Colors;
import constants.FontStyle;
import exception.RoundedButton;
import service.HoaDon_Service;

public class ThongKeDoanhThu_GUI extends JPanel implements ActionListener {

    // ===== SERVICE =====
    private final HoaDon_Service hoaDonService = new HoaDon_Service();

    // ===== FILTER =====
    private JComboBox<String> cbKieu, cbNgay, cbThang, cbNam;
    private RoundedButton btnLoc;
    private JDateChooser dateFrom, dateTo;
    private JLabel lblFromDate, lblToDate;
    private JPanel leftPanel;

    // ===== TOGGLE VIEW =====
    private boolean isChartView = false;
    private RoundedButton btnViewChart, btnViewTable;
    private JPanel chartsPanel, tablePanel, summaryPanel;

    // ===== CARD LABELS =====
    private JLabel lblDoanhThuKy, lblTongDT, lblSoGD, lblDTTB;

    // ===== CHART DATASETS & CHARTS =====
    private DefaultCategoryDataset barDataset, lineDataset;
    private JFreeChart barChart, lineChart;

    // ===== TABLE =====
    private DefaultTableModel tableModel;
    private JTable tableHoaDon;

    private static final NumberFormat VND = constants.Formats.VND;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    public ThongKeDoanhThu_GUI() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Colors.BACKGROUND);

        summaryPanel = createSummaryPanel();
        chartsPanel = createChartsPanel();
        tablePanel = createTablePanel();

        add(createFilterPanel());
        add(summaryPanel);
        add(chartsPanel);
        add(tablePanel);

        // Default: set combobox về ngày hiện tại và load dữ liệu
        LocalDate today = LocalDate.now();
        cbNam.setSelectedItem(String.valueOf(today.getYear()));
        cbThang.setSelectedItem(String.valueOf(today.getMonthValue()));
        // fillNgay sẽ được trigger bởi onThangChanged, nhưng cần fill thủ công ở đây
        fillNgay(today.getMonthValue(), today.getYear());
        cbNgay.setSelectedItem(String.valueOf(today.getDayOfMonth()));
        loadAll(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), null, null);
        
        updateViewMode();
    }

    // Được gọi từ Main_GUI khi chuyển sang tab này để reload data mới nhất
    public void refresh() {
        LocalDate today = LocalDate.now();
        cbNam.setSelectedItem(String.valueOf(today.getYear()));
        cbThang.setSelectedItem(String.valueOf(today.getMonthValue()));
        fillNgay(today.getMonthValue(), today.getYear());
        cbNgay.setSelectedItem(String.valueOf(today.getDayOfMonth()));
        loadAll(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), null, null);
    }

    // FILTER PANEL
    private JPanel createFilterPanel() {
        // Outer panel: BorderLayout đảm bảo hai nút bên phải KHÔNG bao giờ bị wrap xuống
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBackground(Colors.SECONDARY);
        panel.setBorder(BorderFactory.createTitledBorder("Thống kê"));
        panel.setPreferredSize(new Dimension(9999, 80));
        panel.setMaximumSize(new Dimension(9999, 80));
        panel.setMinimumSize(new Dimension(0, 80));

        cbKieu = new JComboBox<>(new String[]{"Theo thời gian cụ thể", "Theo khoảng thời gian"});
        cbKieu.setPreferredSize(new Dimension(190, 30));
        cbKieu.addActionListener(e -> updateVisibility());

        cbNam = new JComboBox<>(new String[]{"Chọn năm","2023","2024","2025","2026"});
        cbNam.setPreferredSize(new Dimension(100, 30));
        cbNam.addActionListener(e -> onNamChanged());

        cbThang = new JComboBox<>(new String[]{"Chọn tháng","1","2","3","4","5","6","7","8","9","10","11","12"});
        cbThang.setPreferredSize(new Dimension(110, 30));
        cbThang.addActionListener(e -> onThangChanged());

        cbNgay = new JComboBox<>(new String[]{"Chọn ngày"});
        cbNgay.setPreferredSize(new Dimension(100, 30));

        // Mặc định Từ ngày = đầu tháng hiện tại, Đến ngày = hôm nay
        java.util.Date hoNay = new java.util.Date();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(hoNay);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        java.util.Date dauThang = cal.getTime();

        dateFrom = new JDateChooser();
        dateFrom.setDateFormatString("dd/MM/yyyy");
        dateFrom.setPreferredSize(new Dimension(120, 30));
        dateFrom.setDate(dauThang);
        // Không cho chọn ngày tương lai cho "Từ ngày"
        dateFrom.setMaxSelectableDate(hoNay);

        dateTo = new JDateChooser();
        dateTo.setDateFormatString("dd/MM/yyyy");
        dateTo.setPreferredSize(new Dimension(120, 30));
        dateTo.setDate(hoNay);
        dateTo.setMaxSelectableDate(hoNay);
        dateTo.setMinSelectableDate(dauThang);

        // Quy tắc: Từ ngày thay đổi → cập nhật giới hạn min của Đến ngày
        dateFrom.addPropertyChangeListener("date", evt -> {
            java.util.Date f = dateFrom.getDate();
            if (f != null) {
                dateTo.setMinSelectableDate(f);
                java.util.Date t = dateTo.getDate();
                if (t != null && t.before(f)) {
                    dateTo.setDate(f);
                }
            }
        });
        // Đến ngày thay đổi → cập nhật giới hạn max của Từ ngày
        dateTo.addPropertyChangeListener("date", evt -> {
            java.util.Date t = dateTo.getDate();
            if (t != null) {
                java.util.Date capMax = t.after(hoNay) ? hoNay : t;
                dateFrom.setMaxSelectableDate(capMax);
            }
        });

        lblFromDate = new JLabel("Từ ngày:");
        lblToDate   = new JLabel("Đến ngày:");

        // --- Panel bên trái: chứa các bộ lọc, dùng FlowLayout bình thường ---
        leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        leftPanel.setOpaque(false);

        // --- Panel bên phải: cố định, chứa Lọc + Xuất PDF, không bao giờ bị wrap ---
        btnLoc = new RoundedButton(70, 30, 8, "Lọc", Colors.PRIMARY);
        btnLoc.addActionListener(this);
        RoundedButton btnXuat = new RoundedButton(140, 30, 8, "Xuất CSV", Colors.PRIMARY);
        btnXuat.addActionListener(e -> service.ExcelExporter.xuatTable(this,
                "BÁO CÁO DOANH THU", "DoanhThu", "BaoCao_DoanhThu", tableHoaDon));

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
        rightPanel.add(btnXuat);

        panel.add(leftPanel,  BorderLayout.CENTER);
        panel.add(rightPanel, BorderLayout.EAST);

        updateVisibility();
        return panel;
    }

    // ============================================================
    // SUMMARY CARDS — bo góc tròn (vẽ tương tự RoundedButton)
    // ============================================================
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 6, 10));
        panel.setBackground(Colors.BACKGROUND);
        panel.setPreferredSize(new Dimension(9999, 140));
        panel.setMaximumSize(new Dimension(9999, 140));
        panel.setMinimumSize(new Dimension(0, 140));

        Object[] c1 = buildCard("Doanh thu kỳ lọc",  "Đang tải...", Colors.PRIMARY);
        Object[] c2 = buildCard("Tổng doanh thu",     "Đang tải...", Colors.PRIMARY);
        Object[] c3 = buildCard("Số giao dịch",       "Đang tải...", Colors.PRIMARY);
        Object[] c4 = buildCard("Doanh thu TB / HĐ",  "Đang tải...", Colors.PRIMARY);

        lblDoanhThuKy = (JLabel) c1[1];
        lblTongDT     = (JLabel) c2[1];
        lblSoGD       = (JLabel) c3[1];
        lblDTTB       = (JLabel) c4[1];

        panel.add((JPanel) c1[0]);
        panel.add((JPanel) c2[0]);
        panel.add((JPanel) c3[0]);
        panel.add((JPanel) c4[0]);
        return panel;
    }

    // Card với góc tròn vẽ thủ công, tương tự kỹ thuật trong RoundedButton
    private Object[] buildCard(String title, String value, Color accent) {
        JPanel card = new JPanel() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
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
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        barDataset  = new DefaultCategoryDataset();
        lineDataset = new DefaultCategoryDataset();

        barChart = ChartFactory.createBarChart(
                "Doanh thu theo tháng", "Tháng", "Số tiền (triệu đ)", barDataset);
        styleBar(barChart, Colors.TEXT_LOGIN);
        ChartPanel barPanel = new ChartPanel(barChart);
        barPanel.setPreferredSize(new Dimension(400, 280));
        barPanel.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));

        lineChart = ChartFactory.createLineChart(
                "Xu hướng doanh thu", "Thời gian", "Số tiền (triệu đ)", lineDataset);
        styleLine(lineChart, Colors.SUCCESS);
        ChartPanel linePanel = new ChartPanel(lineChart);
        linePanel.setPreferredSize(new Dimension(400, 280));
        linePanel.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));

        panel.add(barPanel);
        panel.add(linePanel);
        return panel;
    }

    private void styleBar(JFreeChart chart, Color color) {
        chart.setBackgroundPaint(Colors.BACKGROUND);
        chart.getTitle().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Colors.SECONDARY);
        plot.setRangeGridlinePaint(Colors.BORDER);
        BarRenderer r = (BarRenderer) plot.getRenderer();
        r.setSeriesPaint(0, color); r.setMaximumBarWidth(0.06); r.setShadowVisible(false);
        NumberAxis ax = (NumberAxis) plot.getRangeAxis();
        ax.setNumberFormatOverride(VND); ax.setAutoRangeIncludesZero(true);
    }

    private void styleLine(JFreeChart chart, Color color) {
        chart.setBackgroundPaint(Colors.BACKGROUND);
        chart.getTitle().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Colors.SECONDARY);
        plot.setRangeGridlinePaint(Colors.BORDER);
        LineAndShapeRenderer r = (LineAndShapeRenderer) plot.getRenderer();
        r.setSeriesPaint(0, color);
        r.setSeriesStroke(0, new BasicStroke(2.0f));
        r.setSeriesShapesVisible(0, true);
        NumberAxis ax = (NumberAxis) plot.getRangeAxis();
        ax.setNumberFormatOverride(VND); ax.setAutoRangeIncludesZero(true);
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    }

    // ============================================================
    // TABLE PANEL
    // ============================================================
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Colors.BACKGROUND);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(4, 10, 10, 10),
                BorderFactory.createTitledBorder("Danh sách hóa đơn")));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        String[] cols = {"Mã HĐ", "Ngày lập", "Tên nhân viên", "Tên khách hàng", "Số lượng", "Đơn giá (đ)", "PTTT", "Tổng tiền (đ)"};
        tableModel = new DefaultTableModel(null, cols) {
            public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(tableModel);
        tableHoaDon = table;
        table.setRowHeight(26);
        table.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
        table.getTableHeader().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        table.getTableHeader().setBackground(Colors.SECONDARY);
        table.setSelectionBackground(Colors.SUCCESS_LIGHT);

        // Cố định: không cho kéo đổi thứ tự cột, không cho resize cột
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        int[] widths = {80, 90, 130, 130, 60, 90, 85, 100};
        for (int i = 0; i < widths.length; i++)
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    // ============================================================
    // LOAD — Gọi Service lấy dữ liệu, GUI chỉ hiển thị
    // ============================================================
    private void loadAll(Integer nam, Integer thang, Integer ngay,
                         java.util.Date tuNgayUtil, java.util.Date denNgayUtil) {

        LocalDate tuNgay  = tuNgayUtil  != null
                ? tuNgayUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()  : null;
        LocalDate denNgay = denNgayUtil != null
                ? denNgayUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        // --- Summary Cards (Service tính toán, GUI chỉ hiển thị) ---
        HoaDon_Service.ThongKeTongHop tk = hoaDonService.layThongKeTongHop(nam, thang, ngay, tuNgay, denNgay);
        lblDoanhThuKy.setText(VND.format((long) tk.doanhThuKy) + "đ");
        lblTongDT.setText(VND.format((long) tk.tongDoanhThu)   + "đ");
        lblSoGD.setText(String.valueOf(tk.soGiaoDich));
        lblDTTB.setText(VND.format((long) tk.doanhThuTrungBinh) + "đ");

        // --- Bar chart (3 mức: năm→tháng, tháng→ngày, ngày→giờ) ---
        barDataset.clear();
        LinkedHashMap<String, Double> barMap = hoaDonService.layDuLieuBieuDoCot(nam, thang, ngay);
        String barLabel, barTitle, lineTitle, xAxisLabel, yAxisLabel;
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (tuNgay != null && denNgay != null) {
            // Chế độ khoảng thời gian
            barLabel = "Doanh thu (triệu đ)";
            barTitle = "Doanh thu — " + tuNgay.format(fmt) + " đến " + denNgay.format(fmt);
            lineTitle = "Xu hướng doanh thu — " + tuNgay.format(fmt) + " đến " + denNgay.format(fmt);
            xAxisLabel = "Ngày";
            yAxisLabel = "Số tiền (triệu đ)";
        } else if (ngay != null && thang != null && nam != null) {
            barLabel = "Doanh thu (đ)";
            barTitle = String.format("Doanh thu theo giờ — %02d/%02d/%d", ngay, thang, nam);
            lineTitle = String.format("Xu hướng doanh thu — %02d/%02d/%d", ngay, thang, nam);
            xAxisLabel = "Giờ";
            yAxisLabel = "Số tiền (đ)";
        } else if (thang != null && nam != null) {
            barLabel = "Doanh thu (triệu đ)";
            barTitle = String.format("Doanh thu theo ngày — Tháng %d/%d", thang, nam);
            lineTitle = String.format("Xu hướng doanh thu — Tháng %d/%d", thang, nam);
            xAxisLabel = "Ngày";
            yAxisLabel = "Số tiền (triệu đ)";
        } else if (nam != null) {
            barLabel = "Doanh thu (triệu đ)";
            barTitle = "Doanh thu theo tháng — Năm " + nam;
            lineTitle = "Xu hướng doanh thu — Năm " + nam;
            xAxisLabel = "Tháng";
            yAxisLabel = "Số tiền (triệu đ)";
        } else {
            barLabel = "Doanh thu (triệu đ)";
            int y = LocalDate.now().getYear();
            barTitle = "Doanh thu theo tháng — Năm " + y;
            lineTitle = "Xu hướng doanh thu — Năm " + y;
            xAxisLabel = "Tháng";
            yAxisLabel = "Số tiền (triệu đ)";
        }
        boolean isDayLevel = (ngay != null && thang != null && nam != null);
        for (Map.Entry<String, Double> entry : barMap.entrySet()) {
            double val = isDayLevel ? entry.getValue() : entry.getValue() / 1_000_000.0;
            barDataset.addValue(val, barLabel, entry.getKey());
        }
        barChart.setTitle(barTitle);
        barChart.getCategoryPlot().getDomainAxis().setLabel(xAxisLabel);
        barChart.getCategoryPlot().getRangeAxis().setLabel(yAxisLabel);

        // --- Line chart (xu hướng, cùng mức với bar) ---
        lineDataset.clear();
        LinkedHashMap<String, Double> lineMap = hoaDonService.layDuLieuXuHuong(nam, thang, ngay, tuNgay, denNgay);
        for (Map.Entry<String, Double> entry : lineMap.entrySet()) {
            double val = isDayLevel ? entry.getValue() : entry.getValue() / 1_000_000.0;
            lineDataset.addValue(val, barLabel, entry.getKey());
        }
        lineChart.setTitle(lineTitle);
        lineChart.getCategoryPlot().getDomainAxis().setLabel(xAxisLabel);
        lineChart.getCategoryPlot().getRangeAxis().setLabel(yAxisLabel);

        // --- Table (Service trả về data đầy đủ, GUI chỉ format tiền VND) ---
        tableModel.setRowCount(0);
        ArrayList<Object[]> rows = hoaDonService.layDanhSachTheoKy(nam, thang, ngay, tuNgay, denNgay);
        for (Object[] row : rows) {
            if (row[5] instanceof Number) {
                row[5] = VND.format(((Number) row[5]).longValue()) + "đ";
            }
            if (row[7] instanceof Number) {
                row[7] = VND.format(((Number) row[7]).longValue()) + "đ";
            }
            tableModel.addRow(row);
        }
    }

    // ============================================================
    // ACTION
    // ============================================================
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() != btnLoc) return;

        boolean isSpecific = "Theo thời gian cụ thể".equals(cbKieu.getSelectedItem());

        if (isSpecific) {
            String namStr   = (String) cbNam.getSelectedItem();
            String thangStr = (String) cbThang.getSelectedItem();
            String ngayStr  = (String) cbNgay.getSelectedItem();

            boolean hasNam   = namStr   != null && !namStr.startsWith("Chọn");
            boolean hasThang = thangStr != null && !thangStr.startsWith("Chọn");
            boolean hasNgay  = ngayStr  != null && !ngayStr.startsWith("Chọn");

            if (!hasNam && !hasThang && !hasNgay) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thời gian thống kê!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Integer nam   = hasNam   ? Integer.parseInt(namStr)   : null;
            Integer thang = hasThang ? Integer.parseInt(thangStr) : null;
            Integer ngay  = hasNgay  ? Integer.parseInt(ngayStr)  : null;
            loadAll(nam, thang, ngay, null, null);
        } else {
            java.util.Date from = dateFrom.getDate();
            java.util.Date to   = dateTo.getDate();
            if (from == null || to == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ Từ ngày và Đến ngày!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            java.util.Date hoNay = new java.util.Date();
            if (from.after(hoNay) || to.after(hoNay)) {
                JOptionPane.showMessageDialog(this, "Không được chọn ngày trong tương lai!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (from.after(to)) {
                JOptionPane.showMessageDialog(this, "Từ ngày phải trước hoặc bằng Đến ngày!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            loadAll(null, null, null, from, to);
        }
    }

    // ============================================================
    // FILTER HELPERS
    // ============================================================
    private void updateVisibility() {
        if (leftPanel == null) return;
        
        boolean sp = "Theo thời gian cụ thể".equals(cbKieu.getSelectedItem());
        
        leftPanel.removeAll();
        leftPanel.add(cbKieu);
        
        if (sp) {
            leftPanel.add(cbNam);
            leftPanel.add(cbThang);
            leftPanel.add(cbNgay);
        } else {
            leftPanel.add(lblFromDate);
            leftPanel.add(dateFrom);
            leftPanel.add(lblToDate);
            leftPanel.add(dateTo);
        }
        
        leftPanel.add(btnLoc);
        
        leftPanel.revalidate();
        leftPanel.repaint();
    }

    private void onThangChanged() {
        String t = (String) cbThang.getSelectedItem();
        String n = (String) cbNam.getSelectedItem();
        if (t == null || t.startsWith("Chọn") || n == null || n.startsWith("Chọn")) return;
        try { fillNgay(Integer.parseInt(t), Integer.parseInt(n)); } catch (NumberFormatException ignored) {}
    }

    private void onNamChanged() {
        String t = (String) cbThang.getSelectedItem();
        String n = (String) cbNam.getSelectedItem();
        if (t == null || t.startsWith("Chọn") || n == null || n.startsWith("Chọn")) return;
        try { fillNgay(Integer.parseInt(t), Integer.parseInt(n)); } catch (NumberFormatException ignored) {}
    }

    private void fillNgay(int thang, int nam) {
        cbNgay.removeAllItems();
        cbNgay.addItem("Chọn ngày");
        int max;
        switch (thang) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12: max = 31; break;
            case 4: case 6: case 9: case 11: max = 30; break;
            case 2: max = ((nam%4==0 && nam%100!=0) || nam%400==0) ? 29 : 28; break;
            default: max = 31;
        }
        for (int i = 1; i <= max; i++) cbNgay.addItem(String.valueOf(i));
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

}

