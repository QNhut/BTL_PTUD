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
import constants.Colors;
import constants.FontStyle;
import exception.RoundedButton;
import exception.RoundedPanel;
import service.KhachHang_Service;
import util.AsyncLoader;
import util.MaskUtil;

@SuppressWarnings("serial")
public class ThongKeKhachHang_GUI extends JPanel {

    // ===== SERVICE =====
    private final KhachHang_Service khachHangService = new KhachHang_Service();

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
    private DefaultPieDataset<String> pieDataset;
    private DefaultCategoryDataset doanhThuDataset;
    private DefaultCategoryDataset xuHuongDataset;

    // ===== TABLE =====
    private JTable tableKhachHang;
    private DefaultTableModel tableModel;

    private static final NumberFormat VND = constants.Formats.VND;

    // ============================================================
    // CONSTRUCTOR
    // ============================================================
    public ThongKeKhachHang_GUI() {
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

    // Được gọi từ Main_GUI khi chuyển sang tab này để reload data mới nhất
    public void refresh() {
        LocalDate now = LocalDate.now();
        cbKieu.setSelectedItem("Theo thời gian cụ thể");
        cbNam.setSelectedItem(String.valueOf(now.getYear()));
        cbThang.setSelectedItem(String.valueOf(now.getMonthValue()));
        updateNgayComboBox();
        cbNgay.setSelectedItem(String.valueOf(now.getDayOfMonth()));

        performFilter();
    }

    // ============================================================
    private JPanel createHeaderPanel() {
        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setAlignmentX(LEFT_ALIGNMENT);
        header.setBorder(BorderFactory.createEmptyBorder(14, 14, 8, 14));

        JLabel lblTitle = new JLabel("Thống kê khách hàng");
        lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.FOREGROUND);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblSub = new JLabel("Phân tích và theo dõi hành vi khách hàng");
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

        cbKieu = new JComboBox<>(new String[]{"Theo thời gian cụ thể", "Theo quý", "Theo loại khách hàng", "Theo tổng chi tiêu"});
        styleControl(cbKieu, 220, 42);
        cbKieu.addActionListener(e -> updateVisibility());

        int currentYear = LocalDate.now().getYear();
        cbNam = new JComboBox<>();
        cbNam.addItem("Chọn năm");
        for (int y = currentYear; y >= 2020; y--) {
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

        cbLoaiKH = new JComboBox<>(new String[]{"Tất cả", "Khách hàng mới", "Thường xuyên", "Tiềm năng"});
        styleControl(cbLoaiKH, 170, 42);

        cbChiTieu = new JComboBox<>(new String[]{"Tất cả", "< 1 triệu", "1 - 5 triệu", "> 5 triệu"});
        styleControl(cbChiTieu, 160, 42);

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
                "BÁO CÁO KHÁCH HÀNG", "KhachHang", "BaoCao_KhachHang", tableKhachHang));

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

    // ============================================================
    // LOAD — DB → Table → Summary Cards
    // ============================================================
    private void loadAll(String tuNgay, String denNgay, Integer nam, String loaiKH, String chiTieu) {

        btnXem.setEnabled(false);
        AsyncLoader.run(
            () -> {
                // 1. Lấy và lọc dữ liệu — chạy trong background thread
                ArrayList<Object[]> allRows = khachHangService.layDanhSachKHThongKe(tuNgay, denNgay);
                ArrayList<Object[]> filteredRows = new ArrayList<>();
                for (Object[] row : allRows) {
                    String phanLoai = (String) row[6];
                    double tongChi = (row[5] instanceof Number) ? ((Number) row[5]).doubleValue() : 0;
                    if (loaiKH != null && !"Tất cả".equals(loaiKH) && !loaiKH.equals(phanLoai)) continue;
                    if (chiTieu != null && !"Tất cả".equals(chiTieu)) {
                        if ("< 1 triệu".equals(chiTieu) && tongChi >= 1_000_000) continue;
                        if ("1 - 5 triệu".equals(chiTieu) && (tongChi < 1_000_000 || tongChi > 5_000_000)) continue;
                        if ("> 5 triệu".equals(chiTieu) && tongChi <= 5_000_000) continue;
                    }
                    filteredRows.add(row);
                }
                LinkedHashMap<String, Double> dtThang = khachHangService.layDoanhThuTheoThang(nam);
                LinkedHashMap<String, int[]> xuHuong = khachHangService.layXuHuongKH(nam);
                return new Object[]{filteredRows, dtThang, xuHuong};
            },
            data -> {
                btnXem.setEnabled(true);
                @SuppressWarnings("unchecked")
                ArrayList<Object[]> filteredRows = (ArrayList<Object[]>) data[0];
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, Double> dtThang = (LinkedHashMap<String, Double>) data[1];
                @SuppressWarnings("unchecked")
                LinkedHashMap<String, int[]> xuHuong = (LinkedHashMap<String, int[]>) data[2];

                // 3. Đổ dữ liệu vào bảng
                tableModel.setRowCount(0);
                for (Object[] row : filteredRows) {
                    Object[] displayRow = row.clone();
                    if (displayRow[5] instanceof Number) {
                        displayRow[5] = VND.format(((Number) displayRow[5]).longValue()) + "đ";
                    }
                    if (displayRow[2] instanceof String) {
                        displayRow[2] = MaskUtil.phone((String) displayRow[2]);
                    }
                    tableModel.addRow(displayRow);
                }

                // 4. Summary cards
                int tongKH = filteredRows.size(), khMoi = 0, khQuayLai = 0;
                double doanhThu = 0;
                for (Object[] row : filteredRows) {
                    String pl = (String) row[6];
                    if ("Khách hàng mới".equals(pl)) khMoi++;
                    doanhThu += ((Number) row[5]).doubleValue();
                    if (((Number) row[4]).intValue() >= 2) khQuayLai++;
                }
                double tiLeGiuChan = (tongKH > 0) ? (khQuayLai * 100.0 / tongKH) : 0;
                lblTongKH.setText(String.valueOf(tongKH));
                lblKHMoi.setText(String.valueOf(khMoi));
                lblTiLeGiuChan.setText(String.format("%.0f%%", tiLeGiuChan));
                lblDoanhThuKH.setText(VND.format((long) doanhThu) + "đ");

                // 5. Pie chart
                pieDataset.clear();
                LinkedHashMap<String, Integer> phanLoaiMap = new LinkedHashMap<>();
                for (Object[] row : filteredRows) {
                    phanLoaiMap.merge((String) row[6], 1, Integer::sum);
                }
                for (Map.Entry<String, Integer> entry : phanLoaiMap.entrySet()) {
                    pieDataset.setValue(entry.getKey(), entry.getValue());
                }

                // 6. Bar + line charts
                doanhThuDataset.clear();
                for (Map.Entry<String, Double> entry : dtThang.entrySet()) {
                    doanhThuDataset.addValue(entry.getValue() / 1_000_000.0, "Doanh thu (triệu đ)", entry.getKey());
                }
                xuHuongDataset.clear();
                for (Map.Entry<String, int[]> entry : xuHuong.entrySet()) {
                    int[] vals = entry.getValue();
                    xuHuongDataset.addValue(vals[0], "Thường xuyên", entry.getKey());
                    xuHuongDataset.addValue(vals[1], "Khách hàng mới", entry.getKey());
                    xuHuongDataset.addValue(vals[2], "Tiềm năng", entry.getKey());
                }
            }
        );
    }

    // Tính khoảng ngày từ các tham số lọc
    private String[] tinhKhoangNgay(Integer nam, Integer thang, Integer ngay) {
        return constants.Formats.tinhKhoangNgay(nam, thang, ngay);
    }

    // ============================================================
    // ACTION — Nút "Xem" lọc dữ liệu
    // ============================================================
    private void performFilter() {
        String kieu = (String) cbKieu.getSelectedItem();
        if (kieu == null) {
            return;
        }

        Integer nam = null;
        String loaiKH = null;
        String chiTieu = null;
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
                loadAll(tuNgay, denNgay, nam, null, null);
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
                loadAll(tuNgay, denNgay, nam, null, null);
                return;
            }
            case "Theo loại khách hàng":
                loaiKH = (String) cbLoaiKH.getSelectedItem();
                break;
            case "Theo tổng chi tiêu":
                chiTieu = (String) cbChiTieu.getSelectedItem();
                break;
        }

        // Cho các filter không theo thời gian, dùng toàn bộ năm hiện tại
        int y = LocalDate.now().getYear();
        tuNgay = y + "-01-01";
        denNgay = y + "-12-31";
        loadAll(tuNgay, denNgay, y, loaiKH, chiTieu);
    }

    private void updateNgayComboBox() {
        if (cbNam == null || cbThang == null || cbNgay == null) {
            return;
        }

        String selNam = (String) cbNam.getSelectedItem();
        String selThang = (String) cbThang.getSelectedItem();

        Object oldNgay = cbNgay.getSelectedItem();
        cbNgay.removeAllItems();
        cbNgay.addItem("Chọn ngày");

        if (selNam != null && !selNam.startsWith("Chọn") && selThang != null && !selThang.startsWith("Chọn")) {
            int y = Integer.parseInt(selNam);
            int m = Integer.parseInt(selThang);
            int days = LocalDate.of(y, m, 1).lengthOfMonth();
            for (int d = 1; d <= days; d++) {
                cbNgay.addItem(String.valueOf(d));
            }
        }

        if (oldNgay != null) {
            cbNgay.setSelectedItem(oldNgay);
        }
    }

    private void updateVisibility() {
        if (leftPanel == null) {
            return;
        }

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
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.setBackground(Colors.BACKGROUND);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setPreferredSize(new Dimension(9999, 120));
        panel.setMaximumSize(new Dimension(9999, 120));
        panel.setMinimumSize(new Dimension(0, 120));

        Object[] c1 = buildCard("Tổng khách hàng", "Đang tải...", Colors.PRIMARY);
        Object[] c2 = buildCard("Khách hàng mới", "Đang tải...", Colors.PRIMARY);
        Object[] c3 = buildCard("Tỉ lệ giữ chân", "Đang tải...", Colors.PRIMARY);
        Object[] c4 = buildCard("Doanh thu từ KH", "Đang tải...", Colors.PRIMARY);

        lblTongKH = (JLabel) c1[1];
        lblKHMoi = (JLabel) c2[1];
        lblTiLeGiuChan = (JLabel) c3[1];
        lblDoanhThuKH = (JLabel) c4[1];

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
    // CHARTS PANEL — Pie + Combined (Bar & Line)
    // ============================================================
    private JPanel createChartsPanel() {
        JPanel panel = new RoundedPanel(1200, 360, 16);
        panel.setLayout(new BorderLayout(0, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 12, 14));
        panel.setBackground(Colors.BACKGROUND);
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

        JLabel chartTitle = new JLabel("Biểu đồ phân tích khách hàng");
        chartTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        chartTitle.setForeground(Colors.TEXT_PRIMARY);

        JPanel chartContainer = new JPanel(new GridLayout(1, 2, 10, 0));
        chartContainer.setOpaque(false);

        // --- Pie chart ---
        pieDataset = new DefaultPieDataset<String>();
        JFreeChart pieChart = ChartFactory.createPieChart(
                "Phân loại khách hàng", pieDataset, true, true, false);
        pieChart.setBackgroundPaint(Colors.BACKGROUND);
        pieChart.getTitle().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        @SuppressWarnings("unchecked")
        PiePlot<String> piePlot = (PiePlot<String>) pieChart.getPlot();
        piePlot.setBackgroundPaint(Colors.SECONDARY);
        piePlot.setSectionPaint("Thường xuyên", Colors.TEXT_LOGIN);
        piePlot.setSectionPaint("Khách hàng mới", Colors.SUCCESS);
        piePlot.setSectionPaint("Tiềm năng", Colors.ACCENT);
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

        CategoryAxis domainAxis = new CategoryAxis("Tháng");
        combinedPlot.setDomainAxis(domainAxis);

        NumberAxis rangeAxis1 = new NumberAxis("Doanh thu (triệu đ)");
        combinedPlot.setRangeAxis(0, rangeAxis1);
        combinedPlot.mapDatasetToRangeAxis(0, 0);

        // Line (Xu hướng 3 loại KH)
        LineAndShapeRenderer lineRenderer = new LineAndShapeRenderer();
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

        chartContainer.add(piePanel);
        chartContainer.add(combinedPanel);
        panel.add(chartTitle, BorderLayout.NORTH);
        panel.add(chartContainer, BorderLayout.CENTER);
        return panel;
    }

    // ============================================================
    // TABLE PANEL — cột cố định, không kéo, không resize
    // ============================================================
    private JPanel createTablePanel() {
        JPanel panel = new RoundedPanel(1200, 360, 16);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBackground(Colors.BACKGROUND);
        panel.setBorder(BorderFactory.createEmptyBorder(14, 14, 12, 14));
        panel.setAlignmentX(LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        JLabel title = new JLabel("Danh sách khách hàng");
        title.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        title.setForeground(Colors.TEXT_PRIMARY);

        String[] cols = {"Mã KH", "Tên khách hàng", "SĐT", "HĐ gần nhất", "Số đơn", "Tổng chi tiêu (đ)", "Phân loại"};
        tableModel = new DefaultTableModel(null, cols) {
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        tableKhachHang = new JTable(tableModel);
        tableKhachHang.setRowHeight(36);
        tableKhachHang.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        tableKhachHang.getTableHeader().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        tableKhachHang.getTableHeader().setBackground(Colors.SECONDARY);
        tableKhachHang.getTableHeader().setForeground(Colors.TEXT_PRIMARY);
        tableKhachHang.getTableHeader().setReorderingAllowed(false);
        tableKhachHang.getTableHeader().setResizingAllowed(false);
        tableKhachHang.getTableHeader().setPreferredSize(
                new Dimension(tableKhachHang.getTableHeader().getPreferredSize().width, 40));
        tableKhachHang.setSelectionBackground(Colors.SUCCESS_LIGHT);
        tableKhachHang.setGridColor(Colors.BORDER_LIGHT);
        tableKhachHang.setShowVerticalLines(false);
        tableKhachHang.setShowHorizontalLines(true);
        ((javax.swing.table.DefaultTableCellRenderer) tableKhachHang.getDefaultRenderer(Object.class))
                .setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));

        int[] widths = {90, 160, 100, 130, 55, 130, 110};
        for (int i = 0; i < widths.length; i++) {
            tableKhachHang.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(tableKhachHang);
        scrollPane.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));
        scrollPane.getViewport().setBackground(Colors.BACKGROUND);

        panel.add(title, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
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

    // ============================================================
    // GETTERS (cho Controller)
    // ============================================================
    public JTable getTableKhachHang() {
        return tableKhachHang;
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

    public JComboBox<String> getCbLoaiKH() {
        return cbLoaiKH;
    }

    public JComboBox<String> getCbChiTieu() {
        return cbChiTieu;
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
