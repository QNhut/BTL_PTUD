package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
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

    private static final NumberFormat VND =
            NumberFormat.getNumberInstance(new java.util.Locale("vi", "VN"));

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

        // TODO: Bỏ comment dòng dưới sau khi fix DB
        loadAll(null, null, null, null, null);
        // loadMockData(); // Dùng tạm data mẫu để test GUI
        
        updateViewMode();
    }

    // ============================================================
    // FILTER PANEL
    // ============================================================
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

        dateFrom = new JDateChooser(); dateFrom.setDateFormatString("dd/MM/yyyy");
        dateFrom.setPreferredSize(new Dimension(100, 30));
        dateTo   = new JDateChooser(); dateTo.setDateFormatString("dd/MM/yyyy");
        dateTo.setPreferredSize(new Dimension(100, 30));

        lblFromDate = new JLabel("Từ ngày:");
        lblToDate   = new JLabel("Đến ngày:");

        // --- Panel bên trái: chứa các bộ lọc, dùng FlowLayout bình thường ---
        leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        leftPanel.setOpaque(false);

        // --- Panel bên phải: cố định, chứa Lọc + Xuất PDF, không bao giờ bị wrap ---
        btnLoc = new RoundedButton(70, 30, 8, "Lọc", Colors.PRIMARY);
        btnLoc.addActionListener(this);
        RoundedButton btnXuat = new RoundedButton(140, 30, 8, "Xuất file PDF", Colors.PRIMARY);

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

    /** Card với góc tròn vẽ thủ công, tương tự kỹ thuật trong RoundedButton */
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
    // LOAD — Gọi Service, không viết SQL ở đây
    // ============================================================
    private void loadAll(Integer nam, Integer thang, Integer ngay,
                         java.util.Date tuNgayUtil, java.util.Date denNgayUtil) {

        LocalDate tuNgay  = tuNgayUtil  != null
                ? tuNgayUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()  : null;
        LocalDate denNgay = denNgayUtil != null
                ? denNgayUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        // --- Cards ---
        double dtKy   = hoaDonService.tinhDoanhThuKy(nam, thang, ngay, tuNgay, denNgay);
        double tongDT = hoaDonService.tinhTongDoanhThu();
        int    soGD   = hoaDonService.demSoGiaoDich(nam, thang, ngay, tuNgay, denNgay);
        double dtTB   = soGD > 0 ? dtKy / soGD : 0;

        lblDoanhThuKy.setText(VND.format((long) dtKy) + "đ");
        lblTongDT.setText(VND.format((long) tongDT)   + "đ");
        lblSoGD.setText(String.valueOf(soGD));
        lblDTTB.setText(VND.format((long) dtTB)       + "đ");

        // --- Bar chart ---
        barDataset.clear();
        LinkedHashMap<String, Double> barMap;
        if (thang != null && nam != null) {
            barMap = hoaDonService.thongKeTheoNgay(nam, thang);
        } else {
            int barNam = nam != null ? nam : LocalDate.now().getYear();
            barMap = hoaDonService.thongKeTheoThang(barNam);
        }
        for (Map.Entry<String, Double> entry : barMap.entrySet())
            barDataset.addValue(entry.getValue() / 1_000_000.0, "Doanh thu (triệu đ)", entry.getKey());

        // --- Line chart ---
        lineDataset.clear();
        LocalDate lineFrom = tuNgay;
        LocalDate lineTo   = denNgay;
        if (lineFrom == null || lineTo == null) {
            // Compute range from nam/thang/ngay
            int y = nam != null ? nam : LocalDate.now().getYear();
            if (thang != null) {
                lineFrom = LocalDate.of(y, thang, 1);
                lineTo   = lineFrom.withDayOfMonth(lineFrom.lengthOfMonth());
            } else {
                lineFrom = LocalDate.of(y, 1, 1);
                lineTo   = LocalDate.of(y, 12, 31);
            }
        }
        LinkedHashMap<String, Double> lineMap = hoaDonService.xuHuongTheoNgay(lineFrom, lineTo);
        if (lineMap.isEmpty()) {
            // Fallback: theo tháng
            int y = nam != null ? nam : LocalDate.now().getYear();
            lineMap = hoaDonService.thongKeTheoThang(y);
        }
        for (Map.Entry<String, Double> entry : lineMap.entrySet())
            lineDataset.addValue(entry.getValue() / 1_000_000.0, "Doanh thu (triệu đ)", entry.getKey());

        // --- Table ---
        tableModel.setRowCount(0);
        ArrayList<Object[]> rows = hoaDonService.layDanhSachTheoKy(nam, thang, ngay, tuNgay, denNgay);
        for (Object[] row : rows) {
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

    // ============================================================
    // (Removed: generateMockDataForFilter — replaced by loadAll)
    // ============================================================
    @Deprecated private void generateMockDataForFilter() {
        boolean isSpecific = "Theo thời gian cụ thể".equals(cbKieu.getSelectedItem());
        
        barDataset.clear();
        lineDataset.clear();
        tableModel.setRowCount(0);
        
        String chartTitle = "Doanh thu";
        String xAxisLabel = "";
        java.util.List<String> labels = new ArrayList<>();
        java.util.List<Double> values = new ArrayList<>();

        if (isSpecific) {
            String namStr = (String) cbNam.getSelectedItem();
            String thangStr = (String) cbThang.getSelectedItem();
            String ngayStr = (String) cbNgay.getSelectedItem();
            
            boolean hasNam = namStr != null && !namStr.startsWith("Chọn");
            boolean hasThang = thangStr != null && !thangStr.startsWith("Chọn");
            boolean hasNgay = ngayStr != null && !ngayStr.startsWith("Chọn");

            if (!hasNam && !hasThang && !hasNgay) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn thời gian thống kê!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }

            if (hasNgay) {
                // Biểu đồ trống
                chartTitle = "Doanh thu ngày " + ngayStr + "/" + (hasThang ? thangStr : "") + "/" + namStr;
                xAxisLabel = "Giờ";
                // Không thêm data vào biểu đồ để trống
            } else if (hasThang) {
                chartTitle = "Doanh thu tháng " + thangStr + "/" + namStr;
                xAxisLabel = "Ngày";
                int maxDays = 30; 
                try {
                    int t = Integer.parseInt(thangStr);
                    int n = Integer.parseInt(namStr);
                    if (t==2) maxDays = ((n%4==0 && n%100!=0) || n%400==0) ? 29 : 28;
                    else if (t==4||t==6||t==9||t==11) maxDays = 30;
                    else maxDays = 31;
                } catch(Exception ex) {}
                for (int i = 1; i <= maxDays; i++) {
                    labels.add(String.valueOf(i));
                    values.add(Math.random() * 50 + 10);
                }
            } else if (hasNam) {
                chartTitle = "Doanh thu năm " + namStr;
                xAxisLabel = "Tháng";
                for (int i = 1; i <= 12; i++) {
                    labels.add("T" + i);
                    values.add(Math.random() * 200 + 50);
                }
            }
        } else {
            java.util.Date from = dateFrom.getDate();
            java.util.Date to = dateTo.getDate();
            if (from == null || to == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ Từ ngày và Đến ngày!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (from.after(to)) {
                JOptionPane.showMessageDialog(this, "Từ ngày phải trước hoặc bằng Đến ngày!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            long diffInMillis = to.getTime() - from.getTime();
            long diffDays = diffInMillis / (1000 * 60 * 60 * 24);
            
            if (diffDays <= 31) {
                chartTitle = "Doanh thu (" + (diffDays + 1) + " ngày)";
                xAxisLabel = "Ngày";
                for (int i = 1; i <= diffDays + 1; i++) {
                    labels.add("N" + i);
                    values.add(Math.random() * 50 + 10);
                }
            } else if (diffDays <= 90) {
                chartTitle = "Doanh thu (" + (diffDays + 1) + " ngày)";
                xAxisLabel = "Tuần";
                int weeks = (int) Math.ceil((double)(diffDays + 1) / 7);
                for (int i = 1; i <= weeks; i++) {
                    labels.add("Tuần " + i);
                    values.add(Math.random() * 300 + 100);
                }
            } else {
                chartTitle = "Doanh thu (" + (diffDays + 1) + " ngày)";
                xAxisLabel = "Tháng";
                int months = (int) Math.ceil((double)(diffDays + 1) / 30);
                for (int i = 1; i <= months; i++) {
                    labels.add("T" + i);
                    values.add(Math.random() * 1000 + 300);
                }
            }
        }

        // Cập nhật cấu hình biểu đồ
        barChart.setTitle(chartTitle);
        barChart.getCategoryPlot().getDomainAxis().setLabel(xAxisLabel);
        lineChart.setTitle("Xu hướng " + chartTitle.toLowerCase());
        lineChart.getCategoryPlot().getDomainAxis().setLabel(xAxisLabel);

        double tongDoanhThu = 0;
        int tongGD = 0;
        
        for (int i = 0; i < labels.size(); i++) {
            barDataset.addValue(values.get(i), "Doanh thu (triệu đ)", labels.get(i));
            lineDataset.addValue(values.get(i) * (0.9 + Math.random()*0.2), "Doanh thu (triệu đ)", labels.get(i));
            tongDoanhThu += values.get(i);
        }

        // Tạo dữ liệu bảng
        int numRows = (int) (Math.random() * 25 + 10); // 10 to 35 rows
        if (labels.isEmpty()) {
            numRows = (int) (Math.random() * 15 + 5); // Có giao dịch nhưng biểu đồ ngày trống theo yêu cầu
            tongDoanhThu = 0; // Tính lại từ table
        }

        String[] nhanViens = {"Nguyễn Văn A", "Trần Thị B", "Lê Hoàng C", "Phạm Văn D", "Vũ Minh Tuấn", "Phạm Thái Bình"};
        String[] khachHangs = {"Khách lẻ", "Nguyễn Quốc Nhật", "Hoàng Văn E", "Lý Thị F", "Lê Phương Thảo", "Trương Vô Kỵ"};
        String[] pttts = {"Tiền mặt", "Chuyển khoản", "Quẹt thẻ", "Momo", "VNPay"};

        for (int i = 1; i <= numRows; i++) {
            String maHD = String.format("HD%06d", (int)(Math.random() * 999999));
            String ngayLap = "2025-10-" + String.format("%02d", (int)(Math.random() * 28 + 1));
            String nv = nhanViens[(int)(Math.random() * nhanViens.length)];
            String kh = khachHangs[(int)(Math.random() * khachHangs.length)];
            int sl = (int)(Math.random() * 5 + 1);
            long donGia = (long)(Math.random() * 500 + 50) * 1000;
            String pttt = pttts[(int)(Math.random() * pttts.length)];
            long tongTien = sl * donGia;

            if (labels.isEmpty()) { // Chỉ lấy doanh thu từ table nếu là ngày cụ thể
                tongDoanhThu += (tongTien / 1_000_000.0);
            }

            tableModel.addRow(new Object[]{
                maHD, ngayLap, nv, kh, sl, 
                VND.format(donGia) + "đ", pttt, VND.format(tongTien) + "đ"
            });
        }
        
        tongGD = numRows;
        
        // Cập nhật card summary
        lblDoanhThuKy.setText(VND.format((long)(tongDoanhThu * 1_000_000)) + "đ");
        lblTongDT.setText(VND.format(2500000000L + (long)(Math.random()*500000000L)) + "đ"); 
        lblSoGD.setText(String.valueOf(tongGD));
        lblDTTB.setText(tongGD > 0 ? VND.format((long)((tongDoanhThu * 1_000_000) / tongGD)) + "đ" : "0đ");
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

    private void loadMockData() {
        // Khởi tạo bảng trống khi chưa có dữ liệu (fallback)
        tableModel.setRowCount(0);
        barDataset.clear();
        lineDataset.clear();
        lblDoanhThuKy.setText("Đang tải...");
        lblTongDT.setText("Đang tải...");
        lblSoGD.setText("Đang tải...");
        lblDTTB.setText("Đang tải...");
    }
}
