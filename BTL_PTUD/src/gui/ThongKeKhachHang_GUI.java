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
import service.KhachHang_Service;

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
    private DefaultPieDataset      pieDataset;
    private DefaultCategoryDataset doanhThuDataset;
    private DefaultCategoryDataset xuHuongDataset;

    // ===== TABLE =====
    private JTable tableKhachHang;
    private DefaultTableModel tableModel;

    private static final NumberFormat VND =
            NumberFormat.getNumberInstance(new java.util.Locale("vi", "VN"));

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

        // Khởi tạo mặc định là ngày hiện tại
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
        
        // Đảm bảo UI đã render xong rồi mới lọc (giống nhấn "Xem" 1 lần)
        SwingUtilities.invokeLater(() -> performFilter());
    }

    /** Được gọi từ Main_GUI khi chuyển sang tab này để reload data mới nhất */
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
        int currentYear = LocalDate.now().getYear();
        cbNam = new JComboBox<>();
        cbNam.addItem("Chọn năm");
        for (int y = currentYear; y >= 2020; y--) cbNam.addItem(String.valueOf(y));
        cbNam.setPreferredSize(new Dimension(100, 30));
        cbNam.addActionListener(e -> updateNgayComboBox());

        // Tháng đầy đủ
        cbThang = new JComboBox<>(new String[]{"Chọn tháng", "1","2","3","4","5","6","7","8","9","10","11","12"});
        cbThang.setPreferredSize(new Dimension(100, 30));
        cbThang.addActionListener(e -> updateNgayComboBox());

        // Ngày
        cbNgay = new JComboBox<>(new String[]{"Chọn ngày"});
        cbNgay.setPreferredSize(new Dimension(100, 30));

        // Quý
        cbQuy = new JComboBox<>(new String[]{"Chọn quý", "Quý 1", "Quý 2", "Quý 3", "Quý 4"});
        cbQuy.setPreferredSize(new Dimension(100, 30));

        // Loại KH
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

    // ============================================================
    // LOAD — DB → Table → Summary Cards
    // ============================================================
    private void loadAll(String tuNgay, String denNgay, Integer nam, String loaiKH, String chiTieu) {

        // 1. Lấy dữ liệu từ Service
        ArrayList<Object[]> allRows = khachHangService.layDanhSachKHThongKe(tuNgay, denNgay);

        // 2. Lọc theo loại KH / chi tiêu (nếu có)
        ArrayList<Object[]> filteredRows = new ArrayList<>();
        for (Object[] row : allRows) {
            String phanLoai = (String) row[6]; // cột PhanLoai
            double tongChi = (row[5] instanceof Number) ? ((Number) row[5]).doubleValue() : 0;

            if (loaiKH != null && !"Tất cả".equals(loaiKH) && !loaiKH.equals(phanLoai))
                continue;
            if (chiTieu != null && !"Tất cả".equals(chiTieu)) {
                if ("< 1 triệu".equals(chiTieu) && tongChi >= 1_000_000) continue;
                if ("1 - 5 triệu".equals(chiTieu) && (tongChi < 1_000_000 || tongChi > 5_000_000)) continue;
                if ("> 5 triệu".equals(chiTieu) && tongChi <= 5_000_000) continue;
            }
            filteredRows.add(row);
        }

        // 3. Đổ dữ liệu vào bảng
        tableModel.setRowCount(0);
        for (Object[] row : filteredRows) {
            Object[] displayRow = row.clone();
            if (displayRow[5] instanceof Number) {
                displayRow[5] = VND.format(((Number) displayRow[5]).longValue()) + "đ";
            }
            tableModel.addRow(displayRow);
        }

        // 4. Tính summary cards TỪ dữ liệu đã lọc
        int tongKH = filteredRows.size();
        int khMoi = 0;
        double doanhThu = 0;
        int khQuayLai = 0;
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

        // 5. Pie chart — tính từ dữ liệu bảng
        pieDataset.clear();
        LinkedHashMap<String, Integer> phanLoaiMap = new LinkedHashMap<>();
        for (Object[] row : filteredRows) {
            String pl = (String) row[6];
            phanLoaiMap.merge(pl, 1, Integer::sum);
        }
        for (Map.Entry<String, Integer> entry : phanLoaiMap.entrySet()) {
            pieDataset.setValue(entry.getKey(), entry.getValue());
        }

        // 6. Bar chart (doanh thu theo tháng) + Line chart (xu hướng KH)
        doanhThuDataset.clear();
        LinkedHashMap<String, Double> dtThang = khachHangService.layDoanhThuTheoThang(nam);
        for (Map.Entry<String, Double> entry : dtThang.entrySet()) {
            doanhThuDataset.addValue(entry.getValue() / 1_000_000.0, "Doanh thu (triệu đ)", entry.getKey());
        }

        xuHuongDataset.clear();
        LinkedHashMap<String, int[]> xuHuong = khachHangService.layXuHuongKH(nam);
        for (Map.Entry<String, int[]> entry : xuHuong.entrySet()) {
            int[] vals = entry.getValue();
            xuHuongDataset.addValue(vals[0], "Thường xuyên", entry.getKey());
            xuHuongDataset.addValue(vals[1], "Khách hàng mới", entry.getKey());
            xuHuongDataset.addValue(vals[2], "Tiềm năng", entry.getKey());
        }
    }

    /** Tính khoảng ngày từ các tham số lọc */
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

    // ============================================================
    // ACTION — Nút "Xem" lọc dữ liệu
    // ============================================================
    private void performFilter() {
        String kieu = (String) cbKieu.getSelectedItem();
        if (kieu == null) return;

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
                loadAll(tuNgay, denNgay, nam, null, null);
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
        if (cbNam == null || cbThang == null || cbNgay == null) return;
        
        String selNam = (String) cbNam.getSelectedItem();
        String selThang = (String) cbThang.getSelectedItem();
        
        Object oldNgay = cbNgay.getSelectedItem();
        cbNgay.removeAllItems();
        cbNgay.addItem("Chọn ngày");
        
        if (selNam != null && !selNam.startsWith("Chọn") && selThang != null && !selThang.startsWith("Chọn")) {
            int y = Integer.parseInt(selNam);
            int m = Integer.parseInt(selThang);
            int days = LocalDate.of(y, m, 1).lengthOfMonth();
            for (int d = 1; d <= days; d++) cbNgay.addItem(String.valueOf(d));
        }
        
        if (oldNgay != null) cbNgay.setSelectedItem(oldNgay);
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

        String[] cols = {"Mã KH", "Tên khách hàng", "SĐT", "HĐ gần nhất", "Số đơn", "Tổng chi tiêu (đ)", "Phân loại"};
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

        int[] widths = {90, 160, 100, 130, 55, 130, 110};
        for (int i = 0; i < widths.length; i++)
            tableKhachHang.getColumnModel().getColumn(i).setPreferredWidth(widths[i]);

        panel.add(new JScrollPane(tableKhachHang), BorderLayout.CENTER);
        return panel;
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
