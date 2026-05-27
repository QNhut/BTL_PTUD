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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.*;
import org.jfree.data.category.DefaultCategoryDataset;

import com.toedter.calendar.JDateChooser;

import constants.Colors;
import constants.FontStyle;
import exception.RoundedButton;
import exception.RoundedPanel;
import service.HoaDon_Service;
import util.AsyncLoader;

@SuppressWarnings("serial")
public class ThongKeDoanhThu_GUI extends JPanel implements ActionListener {

    // ===== SERVICE =====
    private final HoaDon_Service hoaDonService = new HoaDon_Service();

    // ===== FILTER =====
    private JComboBox<String> cbKieu, cbNgay, cbThang, cbNam;
    private RoundedButton btnLoc;
    private JDateChooser dateFrom, dateTo;
    private JLabel lblFromDate, lblToDate;
    private JPanel leftPanel;
    private RoundedPanel filterCard;

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
//        setBorder(BorderFactory.createEmptyBorder(14, 14, 14, 14));

        summaryPanel = createSummaryPanel();
        chartsPanel = createChartsPanel();
        tablePanel = createTablePanel();

        add(createHeaderPanel());

        // Panel chứa các phần còn lại — có padding ngang
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

        // Default: set combobox về ngày hiện tại và load dữ liệu
        LocalDate today = LocalDate.now();
        cbNam.setSelectedItem(String.valueOf(today.getYear()));
        cbThang.setSelectedItem(String.valueOf(today.getMonthValue()));
        // fillNgay sẽ được trigger bởi onThangChanged, nhưng cần fill thủ công ở đây
        fillNgay(today.getMonthValue(), today.getYear());
        cbNgay.setSelectedItem(String.valueOf(today.getDayOfMonth()));
        loadAllAsync(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), null, null);

        updateViewMode();
    }

    // Được gọi từ Main_GUI khi chuyển sang tab này để reload data mới nhất
    public void refresh() {
        LocalDate today = LocalDate.now();
        cbNam.setSelectedItem(String.valueOf(today.getYear()));
        cbThang.setSelectedItem(String.valueOf(today.getMonthValue()));
        fillNgay(today.getMonthValue(), today.getYear());
        cbNgay.setSelectedItem(String.valueOf(today.getDayOfMonth()));
        loadAllAsync(today.getYear(), today.getMonthValue(), today.getDayOfMonth(), null, null);
    }

    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setAlignmentX(LEFT_ALIGNMENT);
        header.setBorder(BorderFactory.createEmptyBorder(14, 14, 8, 14));

        JLabel lblTitle = new JLabel("Thống kê doanh thu");
        lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.FOREGROUND);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Thống kê và báo cáo doanh thu theo thời gian");
        lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSub.setForeground(Colors.MUTED);
        lblSub.setAlignmentX(LEFT_ALIGNMENT);

        header.add(lblTitle);
        header.add(Box.createVerticalStrut(2));
        header.add(lblSub);
        return header;
    }

    // FILTER PANEL
    private JPanel createFilterPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 175));
        panel.setLayout(new BorderLayout());

        filterCard = new RoundedPanel(1200, 170, 18);
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

        cbKieu = new JComboBox<>(new String[]{"Theo thời gian cụ thể", "Theo khoảng thời gian"});
        styleControl(cbKieu, 220, 42);
        cbKieu.addActionListener(e -> updateVisibility());

        cbNam = new JComboBox<>();
        cbNam.addItem("Chọn năm");
        int currentYear = java.time.LocalDate.now().getYear();
        for (int y = 2023; y <= currentYear; y++) {
            cbNam.addItem(String.valueOf(y));
        }
        styleControl(cbNam, 120, 42);
        cbNam.addActionListener(e -> onNamChanged());

        cbThang = new JComboBox<>(new String[]{"Chọn tháng", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12"});
        styleControl(cbThang, 130, 42);
        cbThang.addActionListener(e -> onThangChanged());

        cbNgay = new JComboBox<>(new String[]{"Chọn ngày"});
        styleControl(cbNgay, 120, 42);

        // Mặc định Từ ngày = đầu tháng hiện tại, Đến ngày = hôm nay
        java.util.Date hoNay = new java.util.Date();
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(hoNay);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        java.util.Date dauThang = cal.getTime();

        dateFrom = new JDateChooser();
        dateFrom.setDateFormatString("dd/MM/yyyy");
        styleDateChooser(dateFrom, 180, 42);
        dateFrom.setDate(dauThang);
        // Không cho chọn ngày tương lai cho "Từ ngày"
        dateFrom.setMaxSelectableDate(hoNay);

        dateTo = new JDateChooser();
        dateTo.setDateFormatString("dd/MM/yyyy");
        styleDateChooser(dateTo, 180, 42);
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
        lblFromDate.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblFromDate.setForeground(Colors.TEXT_SECONDARY);
        lblToDate = new JLabel("Đến ngày:");
        lblToDate.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblToDate.setForeground(Colors.TEXT_SECONDARY);

        leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setOpaque(false);
        leftPanel.setAlignmentX(LEFT_ALIGNMENT);

        btnLoc = new RoundedButton(100, 38, 12, "Lọc", Colors.PRIMARY);
        btnLoc.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        btnLoc.addActionListener(this);

        btnViewTable = new RoundedButton(100, 38, 12, "Bảng", Colors.TEXT_PRIMARY);
        btnViewTable.setForeground(Colors.BACKGROUND);
        btnViewTable.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));

        btnViewChart = new RoundedButton(120, 38, 12, "Biểu đồ", Colors.SECONDARY);
        btnViewChart.setForeground(Colors.TEXT_PRIMARY);
        btnViewChart.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        btnViewChart.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));

        RoundedButton btnXuat = new RoundedButton(140, 38, 12, "Xuất CSV", Colors.PRIMARY);
        btnXuat.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        btnXuat.addActionListener(e -> service.ExcelExporter.xuatTable(this,
                "BÁO CÁO DOANH THU", "DoanhThu", "BaoCao_DoanhThu", tableHoaDon));

        btnViewChart.addActionListener(e -> {
            isChartView = true;
            updateViewMode();
        });
        btnViewTable.addActionListener(e -> {
            isChartView = false;
            updateViewMode();
        });

        // Hàng 2: Bảng | Biểu đồ | Xuất CSV — căn trái, cùng hàng với nhau
        JPanel actionRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionRow.setOpaque(false);
        actionRow.setAlignmentX(LEFT_ALIGNMENT);
        actionRow.add(btnViewTable);
        actionRow.add(btnViewChart);
        actionRow.add(btnXuat);

        filterCard.add(headerRow);
        filterCard.add(Box.createVerticalStrut(8));
        filterCard.add(leftPanel);
        filterCard.add(Box.createVerticalStrut(8));
        filterCard.add(actionRow);

        panel.add(filterCard, BorderLayout.CENTER);

        updateVisibility();
        return panel;
    }

    // ============================================================
    // SUMMARY CARDS — bo góc tròn (vẽ tương tự RoundedButton)
    // ============================================================
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.setBackground(Colors.BACKGROUND);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setPreferredSize(new Dimension(9999, 120));
        panel.setMaximumSize(new Dimension(9999, 120));
        panel.setMinimumSize(new Dimension(0, 120));

        Object[] c1 = buildCard("Doanh thu kỳ lọc", "Đang tải...", Colors.PRIMARY);
        Object[] c2 = buildCard("Tổng doanh thu", "Đang tải...", Colors.PRIMARY);
        Object[] c3 = buildCard("Số giao dịch", "Đang tải...", Colors.PRIMARY);
        Object[] c4 = buildCard("Doanh thu TB / HĐ", "Đang tải...", Colors.PRIMARY);

        lblDoanhThuKy = (JLabel) c1[1];
        lblTongDT = (JLabel) c2[1];
        lblSoGD = (JLabel) c3[1];
        lblDTTB = (JLabel) c4[1];

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
                // Clip toàn bộ paint theo hình bo góc — tránh góc vuông lộ ra
                java.awt.geom.RoundRectangle2D shape
                        = new java.awt.geom.RoundRectangle2D.Float(0, 0, w - 1, h - 1, 16, 16);
                g2.setClip(shape);
                // Nền trắng
                g2.setColor(Colors.BACKGROUND);
                g2.fillRect(0, 0, w, h);
                // Thanh accent trên đầu
                g2.setColor(accent);
                g2.fillRect(0, 0, w, 8);
                // Bỏ clip để vẽ viền không bị cắt
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

        barDataset = new DefaultCategoryDataset();
        lineDataset = new DefaultCategoryDataset();

        barChart = ChartFactory.createBarChart(
                "Doanh thu theo tháng", "Tháng", "Số tiền (triệu đ)", barDataset);
        styleBar(barChart, Colors.TEXT_LOGIN);
        ChartPanel barPanel = new ChartPanel(barChart);
        barPanel.setPreferredSize(new Dimension(400, 300));
        barPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        barPanel.setBackground(Colors.BACKGROUND);

        lineChart = ChartFactory.createLineChart(
                "Xu hướng doanh thu", "Thời gian", "Số tiền (triệu đ)", lineDataset);
        styleLine(lineChart, Colors.SUCCESS);

        JLabel title = new JLabel("Biểu đồ doanh thu theo tháng");
        title.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        title.setForeground(Colors.TEXT_PRIMARY);

        panel.add(title, BorderLayout.NORTH);
        panel.add(barPanel, BorderLayout.CENTER);
        return panel;
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
        NumberAxis ax = (NumberAxis) plot.getRangeAxis();
        ax.setNumberFormatOverride(VND);
        ax.setAutoRangeIncludesZero(true);
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
        ax.setNumberFormatOverride(VND);
        ax.setAutoRangeIncludesZero(true);
        plot.getDomainAxis().setCategoryLabelPositions(CategoryLabelPositions.UP_45);
    }

    // ============================================================
    // TABLE PANEL
    // ============================================================
    private JPanel createTablePanel() {
        JPanel panel = new RoundedPanel(1200, 360, 16);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(Colors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 12, 14));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel title = new JLabel("Danh sách hóa đơn");
        title.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        title.setForeground(Colors.TEXT_PRIMARY);

        String[] cols = {"Mã HĐ", "Ngày lập", "Tên nhân viên", "Tên khách hàng", "Số lượng", "Đơn giá (đ)", "PTTT", "Tổng tiền (đ)"};
        tableModel = new DefaultTableModel(null, cols) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };
        JTable table = new JTable(tableModel);
        tableHoaDon = table;
        table.setRowHeight(36);
        table.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        table.getTableHeader().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        table.getTableHeader().setBackground(Colors.SECONDARY);
        table.getTableHeader().setForeground(Colors.TEXT_PRIMARY);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getPreferredSize().width, 40));
        table.setSelectionBackground(Colors.SUCCESS_LIGHT);
        table.setGridColor(Colors.BORDER_LIGHT);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        ((DefaultTableCellRenderer) table.getDefaultRenderer(Object.class)).setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        int[] widths = {90, 100, 140, 140, 60, 100, 130, 110};
        for (int i = 0; i < widths.length; i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        table.getColumnModel().getColumn(0).setCellRenderer(new AccentTextRenderer(Colors.PRIMARY, SwingConstants.LEFT));
        table.getColumnModel().getColumn(4).setCellRenderer(new AccentTextRenderer(Colors.TEXT_PRIMARY, SwingConstants.CENTER));
        table.getColumnModel().getColumn(6).setCellRenderer(new PaymentBadgeRenderer());
        table.getColumnModel().getColumn(7).setCellRenderer(new AccentTextRenderer(Colors.PRIMARY, SwingConstants.RIGHT));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        scrollPane.getViewport().setBackground(Colors.BACKGROUND);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    // ============================================================
    // LOAD — Gọi Service lấy dữ liệu, GUI chỉ hiển thị
    // ============================================================
    /** Gọi async để không chặn EDT */
    private void loadAllAsync(Integer nam, Integer thang, Integer ngay,
            java.util.Date tuNgayUtil, java.util.Date denNgayUtil) {

        final LocalDate tuNgay = tuNgayUtil != null
                ? tuNgayUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        final LocalDate denNgay = denNgayUtil != null
                ? denNgayUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        btnLoc.setEnabled(false);
        AsyncLoader.run(
            () -> {
                // --- Tất cả DB calls chạy trong background thread ---
                HoaDon_Service.ThongKeTongHop tk = hoaDonService.layThongKeTongHop(nam, thang, ngay, tuNgay, denNgay);
                LinkedHashMap<String, Double> barMap = hoaDonService.layDuLieuBieuDoCot(nam, thang, ngay);
                LinkedHashMap<String, Double> lineMap = hoaDonService.layDuLieuXuHuong(nam, thang, ngay, tuNgay, denNgay);
                ArrayList<Object[]> rows = hoaDonService.layDanhSachTheoKy(nam, thang, ngay, tuNgay, denNgay);
                return new Object[]{tk, barMap, lineMap, rows};
            },
            data -> {
                // --- Tất cả Swing updates chạy trên EDT ---
                btnLoc.setEnabled(true);
                HoaDon_Service.ThongKeTongHop tk = (HoaDon_Service.ThongKeTongHop) data[0];
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Double> barMap = (LinkedHashMap<String, Double>) data[1];
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Double> lineMap = (LinkedHashMap<String, Double>) data[2];
                @SuppressWarnings("unchecked")
                ArrayList<Object[]> rows = (ArrayList<Object[]>) data[3];
                applyLoadAll(tk, barMap, lineMap, rows, nam, thang, ngay, tuNgay, denNgay);
            }
        );
    }

    private void applyLoadAll(HoaDon_Service.ThongKeTongHop tk,
            LinkedHashMap<String, Double> barMap, LinkedHashMap<String, Double> lineMap,
            ArrayList<Object[]> rows, Integer nam, Integer thang, Integer ngay,
            LocalDate tuNgay, LocalDate denNgay) {

        lblDoanhThuKy.setText(VND.format((long) tk.doanhThuKy) + "đ");
        lblTongDT.setText(VND.format((long) tk.tongDoanhThu) + "đ");
        lblSoGD.setText(String.valueOf(tk.soGiaoDich));
        lblDTTB.setText(VND.format((long) tk.doanhThuTrungBinh) + "đ");

        barDataset.clear();
        String barLabel, barTitle, lineTitle, xAxisLabel, yAxisLabel;
        java.time.format.DateTimeFormatter fmt = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy");
        if (tuNgay != null && denNgay != null) {
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

        if (lineDataset != null && lineChart != null) {
            lineDataset.clear();
            for (Map.Entry<String, Double> entry : lineMap.entrySet()) {
                double val = isDayLevel ? entry.getValue() : entry.getValue() / 1_000_000.0;
                lineDataset.addValue(val, barLabel, entry.getKey());
            }
            lineChart.setTitle(lineTitle);
            lineChart.getCategoryPlot().getDomainAxis().setLabel(xAxisLabel);
            lineChart.getCategoryPlot().getRangeAxis().setLabel(yAxisLabel);
        }

        tableModel.setRowCount(0);
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

    private void loadAll(Integer nam, Integer thang, Integer ngay,
            java.util.Date tuNgayUtil, java.util.Date denNgayUtil) {

        LocalDate tuNgay = tuNgayUtil != null
                ? tuNgayUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;
        LocalDate denNgay = denNgayUtil != null
                ? denNgayUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate() : null;

        // --- Summary Cards (Service tính toán, GUI chỉ hiển thị) ---
        HoaDon_Service.ThongKeTongHop tk = hoaDonService.layThongKeTongHop(nam, thang, ngay, tuNgay, denNgay);
        lblDoanhThuKy.setText(VND.format((long) tk.doanhThuKy) + "đ");
        lblTongDT.setText(VND.format((long) tk.tongDoanhThu) + "đ");
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
        if (lineDataset != null && lineChart != null) {
            lineDataset.clear();
            LinkedHashMap<String, Double> lineMap = hoaDonService.layDuLieuXuHuong(nam, thang, ngay, tuNgay, denNgay);
            for (Map.Entry<String, Double> entry : lineMap.entrySet()) {
                double val = isDayLevel ? entry.getValue() : entry.getValue() / 1_000_000.0;
                lineDataset.addValue(val, barLabel, entry.getKey());
            }
            lineChart.setTitle(lineTitle);
            lineChart.getCategoryPlot().getDomainAxis().setLabel(xAxisLabel);
            lineChart.getCategoryPlot().getRangeAxis().setLabel(yAxisLabel);
        }

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
        if (e.getSource() != btnLoc) {
            return;
        }

        boolean isSpecific = "Theo thời gian cụ thể".equals(cbKieu.getSelectedItem());

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

            Integer nam = hasNam ? Integer.parseInt(namStr) : null;
            Integer thang = hasThang ? Integer.parseInt(thangStr) : null;
            Integer ngay = hasNgay ? Integer.parseInt(ngayStr) : null;
            loadAllAsync(nam, thang, ngay, null, null);
        } else {
            java.util.Date from = dateFrom.getDate();
            java.util.Date to = dateTo.getDate();
            if (from == null || to == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn đầy đủ Từ ngày và Đến ngày!", "Cảnh báo", JOptionPane.WARNING_MESSAGE);
                return;
            }
            java.time.LocalDate fromLocal = from.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            java.time.LocalDate toLocal = to.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
            java.time.LocalDate today = java.time.LocalDate.now();
            if (fromLocal.isAfter(today) || toLocal.isAfter(today)) {
                JOptionPane.showMessageDialog(this, "Không được chọn ngày trong tương lai!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (fromLocal.isAfter(toLocal)) {
                JOptionPane.showMessageDialog(this, "Từ ngày phải trước hoặc bằng Đến ngày!", "Lỗi", JOptionPane.ERROR_MESSAGE);
                return;
            }
            loadAllAsync(null, null, null, from, to);
        }
    }

    // ============================================================
    // FILTER HELPERS
    // ============================================================
    private void updateVisibility() {
        if (leftPanel == null) {
            return;
        }

        boolean sp = "Theo thời gian cụ thể".equals(cbKieu.getSelectedItem());

        leftPanel.removeAll();
        leftPanel.add(cbKieu);

        if (sp) {
            leftPanel.add(cbNam);
            leftPanel.add(cbThang);
            leftPanel.add(cbNgay);
            leftPanel.add(btnLoc);
        } else {
            leftPanel.add(lblFromDate);
            leftPanel.add(dateFrom);
            leftPanel.add(lblToDate);
            leftPanel.add(dateTo);
            leftPanel.add(btnLoc);
        }

        leftPanel.revalidate();
        leftPanel.repaint();
    }

    private void onThangChanged() {
        String t = (String) cbThang.getSelectedItem();
        String n = (String) cbNam.getSelectedItem();
        if (t == null || t.startsWith("Chọn") || n == null || n.startsWith("Chọn")) {
            return;
        }
        try {
            fillNgay(Integer.parseInt(t), Integer.parseInt(n));
        } catch (NumberFormatException ignored) {
        }
    }

    private void onNamChanged() {
        String t = (String) cbThang.getSelectedItem();
        String n = (String) cbNam.getSelectedItem();
        if (t == null || t.startsWith("Chọn") || n == null || n.startsWith("Chọn")) {
            return;
        }
        try {
            fillNgay(Integer.parseInt(t), Integer.parseInt(n));
        } catch (NumberFormatException ignored) {
        }
    }

    private void fillNgay(int thang, int nam) {
        cbNgay.removeAllItems();
        cbNgay.addItem("Chọn ngày");
        int max;
        switch (thang) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                max = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                max = 30;
                break;
            case 2:
                max = ((nam % 4 == 0 && nam % 100 != 0) || nam % 400 == 0) ? 29 : 28;
                break;
            default:
                max = 31;
        }
        for (int i = 1; i <= max; i++) {
            cbNgay.addItem(String.valueOf(i));
        }
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

        this.revalidate();
        this.repaint();
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

    private void styleDateChooser(JDateChooser chooser, int width, int height) {
        Dimension size = new Dimension(width, height);
        chooser.setPreferredSize(size);
        chooser.setMinimumSize(size);
        chooser.setMaximumSize(size);
        chooser.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        chooser.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        JComponent editor = chooser.getDateEditor().getUiComponent();
        if (editor != null) {
            editor.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
            editor.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
        }
    }

    @SuppressWarnings("serial")
    private static class AccentTextRenderer extends DefaultTableCellRenderer {

        private final Color color;
        private final int align;

        private AccentTextRenderer(Color color, int align) {
            this.color = color;
            this.align = align;
            setOpaque(true);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(align);
            setForeground(isSelected ? Colors.TEXT_PRIMARY : color);
            setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
            setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            setBackground(isSelected ? Colors.SUCCESS_LIGHT : Colors.BACKGROUND);
            return this;
        }
    }

    private static class PaymentBadgeRenderer implements TableCellRenderer {

        private static final Dimension BADGE = new Dimension(120, 24);

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            String text = value == null ? "" : value.toString();
            Color[] colors = badgeColors(text);
            Color bg = isSelected ? Colors.SUCCESS_LIGHT : colors[0];
            Color fg = isSelected ? Colors.TEXT_PRIMARY : colors[1];

            // Outer cell — nền đồng nhất với hàng
            JPanel cell = new JPanel(new GridBagLayout());
            cell.setOpaque(true);
            cell.setBackground(isSelected ? Colors.SUCCESS_LIGHT : Colors.BACKGROUND);

            // Badge — pill bo tròn, vẽ nền bằng paintComponent
            JLabel badge = new JLabel(text, SwingConstants.CENTER) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(bg);
                    // arc = height để thành hình pill
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), getHeight(), getHeight());
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            badge.setOpaque(false);
            badge.setForeground(fg);
            badge.setFont(FontStyle.font(FontStyle.XS, FontStyle.BOLD));
            badge.setPreferredSize(BADGE);
            badge.setMinimumSize(BADGE);
            badge.setMaximumSize(BADGE);
            badge.setHorizontalAlignment(SwingConstants.CENTER);

            cell.add(badge);
            return cell;
        }

        private static Color[] badgeColors(String text) {
            String l = text.toLowerCase();
            if (l.contains("tiền mặt") || l.contains("tien mat")) {
                return new Color[]{new Color(220, 252, 231), new Color(22, 163, 74)};
            }
            if (l.contains("thẻ") || l.contains("the") || l.contains("atm")) {
                return new Color[]{new Color(219, 234, 254), new Color(37, 99, 235)};
            }
            if (l.contains("momo") || l.contains("ví")) {
                return new Color[]{new Color(252, 231, 243), new Color(219, 39, 119)};
            }
            if (l.contains("chuyển khoản") || l.contains("chuyen khoan")) {
                return new Color[]{new Color(243, 232, 255), new Color(147, 51, 234)};
            }
            if (l.contains("qr")) {
                return new Color[]{new Color(204, 251, 241), new Color(15, 118, 110)};
            }
            return new Color[]{Colors.SECONDARY, Colors.TEXT_SECONDARY};
        }
    }

}
