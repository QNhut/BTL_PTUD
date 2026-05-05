package gui;

import constants.Colors;
import constants.FontStyle;
import entity.SanPham;
import exception.QuantityEditor;
import exception.RoundedButton;
import exception.RoundedPanel;
import exception.RoundedTextField;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import service.SanPham_Service;

public class HoaDon_GUI extends JPanel {
    // === Dùng làm GUI Lập Hóa Đơn ===

    private JPanel pnlTitle;
    private JLabel lblTitle;
    private JLabel lblsubTitle;
    private JPanel pnlContent;
    private JPanel pnlContentTop;
    private JPanel pnlContentBottom;
    private JPanel pnlContentTopLeft;
    private JPanel pnlContentTopRight;
    private JPanel pnlTopLeft;
    private JLabel lblcontentTopLeft;
    private JTextField txtSearch;
    private JPanel pnlProductList;
    private Set<SanPham> selectedList = new HashSet<>();
    private List<SanPham> currentList;
    private JLabel lblcontentRight;

    private static final int CARD_WIDTH = 210;
    private static final int CARD_HEIGHT = 155;

    private JLabel lblsubcontentRight1;
    private JLabel lblsubcontentRight2;
    private JTextField txtTenKH;
    private JTextField txtSDT;
    private JLabel lblNhanVienLabel;
    private JLabel lblNhanVienValue;
    private JLabel lblThoiGianLabel;
    private JLabel lblThoiGianValue;
    private JScrollPane scrProduct;
    private JPanel pnlContentBottomRight;
    private JPanel row1;
    private JLabel lblLeft1;
    private JLabel lblRight1;
    private JPanel row2;
    private JLabel lblLeft2;
    private JLabel lblRight2;
    private JPanel row3;
    private JLabel lblLeft3;
    private JLabel lblRight3;
    private JButton btnLapHoaDon;
    private JPanel pnlTitleBottom;
    private JPanel pnlsubTitleBottom;
    private JLabel lblTitleBottom1;
    private JLabel lblsubTitleBottom;
    private JButton btnXoaTatCa;
    private JButton btnCongDung;

    private String[] columnNames = {"Sản phẩm", "Số lượng", "Giá", "Thành tiền", ""};
    private DefaultTableModel tableModel;
    private JTable tblSelected;
    private JPanel pnlTotelPrice;
    private double totalPrice = 0;
    private JLabel lblTotal;
    private JLabel lblTotalPrice;
    private SanPham_Service sanPhamService;
    private List<SanPham> fullList = new ArrayList<>();
    private String currentCongDung = "Tất cả";

    public HoaDon_GUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Colors.BACKGROUND);

//		Phần tiêu đề
        add(pnlTitle = new JPanel(), BorderLayout.NORTH);
        pnlTitle.setLayout(new BoxLayout(pnlTitle, BoxLayout.Y_AXIS));
        pnlTitle.setBackground(Colors.BACKGROUND);

        pnlTitle.add(lblTitle = new JLabel("Lập hóa đơn"));
        lblTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        pnlTitle.setBackground(Colors.BACKGROUND);

//		Phần nội dung chính
        add(pnlContent = new JPanel(), BorderLayout.CENTER);
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(Colors.BACKGROUND);

//		Phần trên: chọn sản phẩm + thông tin phiếu nhập
        pnlContent.add(pnlContentTop = new JPanel());
        pnlContentTop.setLayout(new BoxLayout(pnlContentTop, BoxLayout.X_AXIS));
        pnlContentTop.setBackground(Colors.BACKGROUND);
        pnlContentTop.setPreferredSize(new Dimension(0, 470));
        pnlContentTop.setMaximumSize(new Dimension(Integer.MAX_VALUE, 500));
        pnlContentTop.setMinimumSize(new Dimension(0, 200));

//		Bên trái: danh sách sản phẩm
        pnlContentTop.add(pnlContentTopLeft = new RoundedPanel(700, 470, 15));
        pnlContentTopLeft.setBackground(Colors.BACKGROUND);
        pnlContentTopLeft.setLayout(new BorderLayout(10, 10));
        pnlContentTopLeft.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pnlContentTopLeft.add(pnlTopLeft = new JPanel(), BorderLayout.NORTH);
        pnlTopLeft.setLayout(new BoxLayout(pnlTopLeft, BoxLayout.Y_AXIS));
        pnlTopLeft.setOpaque(false);

        pnlTopLeft.add(lblcontentTopLeft = new JLabel("Chọn sản phẩm bán"));
        lblcontentTopLeft.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblcontentTopLeft.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlTopLeft.add(Box.createVerticalStrut(10));

//      Hàng tìm kiếm: TextField + nút tìm
        JPanel pnlSearchRow = new JPanel();
        pnlSearchRow.setLayout(new BoxLayout(pnlSearchRow, BoxLayout.X_AXIS));
        pnlSearchRow.setOpaque(false);
        pnlSearchRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlSearchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        txtSearch = new RoundedTextField(450, 35, 15, "Tìm kiếm sản phẩm...");
        txtSearch.setMaximumSize(new Dimension(450, 35));
        pnlSearchRow.add(txtSearch);
        pnlSearchRow.add(Box.createHorizontalStrut(10));

        JButton btnSearch = new RoundedButton(100, 35, 15, "Tìm", Colors.PRIMARY);
        btnSearch.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnSearch.setMaximumSize(new Dimension(100, 35));
        btnSearch.addActionListener(e -> filterAndLoad());
        txtSearch.addActionListener(e -> filterAndLoad()); // Enter cũng tìm
        pnlSearchRow.add(btnSearch);
        pnlSearchRow.add(Box.createHorizontalStrut(10));

//      Nút sổ xuống chọn loại sản phẩm
        String[] congDungList = {
            "Tất cả",
            // Thực phẩm chức năng
            "Hỗ trợ sức khỏe", "Dinh dưỡng", "Làm đẹp",
            // Dược mỹ phẩm
            "Chăm sóc làn da", "Chăm sóc tóc", "Mỹ phẩm tổng hợp",
            "Chăm sóc vùng mắt", "Sản phẩm tự nhiên",
            // Thuốc
            "Thuốc hô hấp - Tai mũi họng", "Thuốc tiêu hóa - Gan mật",
            "Thuốc tim mạch", "Thuốc thần kinh", "Thuốc cơ xương khớp",
            "Thuốc da liễu", "Khác",
            // Chăm sóc cá nhân
            "Thực phẩm - Đồ uống", "Vệ sinh cá nhân", "Chăm sóc răng miệng",
            "Đồ dùng gia đình", "Hàng tổng hợp", "Tinh dầu các loại", "Thiết bị làm đẹp"
        };
        JPopupMenu popupCongDung = new JPopupMenu();
        popupCongDung.setBackground(Colors.BACKGROUND);
        popupCongDung.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(6, 0, 6, 0)
        ));

        for (String cd : congDungList) {
            JMenuItem item = new JMenuItem(cd);
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
                String label = cd.equals("Tất cả") ? "▼ Danh mục" : "▼ " + (cd.length() > 16 ? cd.substring(0, 14) + "…" : cd);
                btnCongDung.setText(label);
                currentCongDung = cd;
                filterAndLoad();
            });
            popupCongDung.add(item);
        }

        btnCongDung = new RoundedButton(170, 35, 15, "▼ Danh mục", Colors.SECONDARY);
        btnCongDung.setForeground(Colors.TEXT_PRIMARY);
        btnCongDung.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnCongDung.setMaximumSize(new Dimension(170, 35));
        btnCongDung.addActionListener(e -> {
            popupCongDung.setPreferredSize(new Dimension(260, popupCongDung.getPreferredSize().height));
            popupCongDung.show(btnCongDung, 0, btnCongDung.getHeight() + 4);
        });
        pnlSearchRow.add(btnCongDung);

        pnlTopLeft.add(pnlSearchRow);

        pnlProductList = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        pnlProductList.setBackground(Colors.BACKGROUND);

        pnlContentTopLeft.add(scrProduct = new JScrollPane(pnlProductList), BorderLayout.CENTER);
        scrProduct.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrProduct.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
        scrProduct.setBorder(null);

        // Lấy sản phẩm từ data
        sanPhamService = new SanPham_Service();
        fullList = sanPhamService.getDSSanPham();
        loadProducts(fullList);
        pnlContentTopLeft.revalidate();
        pnlContentTopLeft.repaint();

        pnlContentTop.add(Box.createHorizontalStrut(20));

//		Bên phải: thông tin phiếu nhập + tóm tắt + nút tạo phiếu
        pnlContentTop.add(pnlContentTopRight = new RoundedPanel(350, 470, 15));
        pnlContentTopRight.setBackground(Colors.BACKGROUND);
        pnlContentTopRight.setLayout(new BoxLayout(pnlContentTopRight, BoxLayout.Y_AXIS));
        pnlContentTopRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        pnlContentTopRight.add(lblcontentRight = new JLabel("Thông tin hóa đơn"));
        lblcontentRight.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblcontentRight.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlContentTopRight.add(Box.createVerticalStrut(10));

        pnlContentTopRight.add(lblsubcontentRight1 = new JLabel("Tên khách hàng *"));
        lblsubcontentRight1.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblsubcontentRight1.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlContentTopRight.add(Box.createVerticalStrut(5));
        pnlContentTopRight.add(txtTenKH = new RoundedTextField(Integer.MAX_VALUE, 35, 15, "Nhập tên khách hàng"));
        txtTenKH.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlContentTopRight.add(Box.createVerticalStrut(10));
        pnlContentTopRight.add(lblsubcontentRight2 = new JLabel("Số điện thoại"));
        lblsubcontentRight2.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblsubcontentRight2.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlContentTopRight.add(Box.createVerticalStrut(5));
        pnlContentTopRight.add(txtSDT = new RoundedTextField(Integer.MAX_VALUE, 35, 15, "Nhập số điện thoại"));
        txtSDT.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlContentTopRight.add(Box.createVerticalStrut(10));

//      Nhân viên lập hóa đơn
        pnlContentTopRight.add(lblNhanVienLabel = new JLabel("Nhân viên"));
        lblNhanVienLabel.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblNhanVienLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlContentTopRight.add(Box.createVerticalStrut(5));
        pnlContentTopRight.add(lblNhanVienValue = new JLabel("--"));
        lblNhanVienValue.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNhanVienValue.setForeground(Colors.TEXT_SECONDARY);
        lblNhanVienValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlContentTopRight.add(Box.createVerticalStrut(10));

//      Thời gian tạo
        pnlContentTopRight.add(lblThoiGianLabel = new JLabel("Thời gian tạo"));
        lblThoiGianLabel.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblThoiGianLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlContentTopRight.add(Box.createVerticalStrut(5));
        String thoiGianTao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        pnlContentTopRight.add(lblThoiGianValue = new JLabel(thoiGianTao));
        lblThoiGianValue.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblThoiGianValue.setForeground(Colors.TEXT_SECONDARY);
        lblThoiGianValue.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlContentTopRight.add(Box.createVerticalStrut(10));

//		Khung tóm tắt + nút
        pnlContentTopRight.add(pnlContentBottomRight = new RoundedPanel(330, 160, 15));
        pnlContentBottomRight.setLayout(new BoxLayout(pnlContentBottomRight, BoxLayout.Y_AXIS));
        pnlContentBottomRight.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        pnlContentBottomRight.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        pnlContentBottomRight.setBackground(Colors.SUCCESS_LIGHT);
        pnlContentBottomRight.setAlignmentX(Component.LEFT_ALIGNMENT);

//		Row 1: Số mặt hàng
        pnlContentBottomRight.add(row1 = new JPanel(new BorderLayout()));
        row1.setOpaque(false);
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        row1.add(lblLeft1 = new JLabel("Số sản phẩm"), BorderLayout.WEST);
        row1.add(lblRight1 = new JLabel("0 sản phẩm"), BorderLayout.EAST);
        lblLeft1.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblRight1.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));

        pnlContentBottomRight.add(Box.createVerticalStrut(6));

//		Row 2: Tổng số lượng
        pnlContentBottomRight.add(row2 = new JPanel(new BorderLayout()));
        row2.setOpaque(false);
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        row2.add(lblLeft2 = new JLabel("Tổng số lượng"), BorderLayout.WEST);
        row2.add(lblRight2 = new JLabel("0 đơn vị"), BorderLayout.EAST);
        lblLeft2.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblRight2.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));

        pnlContentBottomRight.add(Box.createVerticalStrut(10));

//		Đường kẻ phân cách
        JPanel divider = new JPanel();
        divider.setOpaque(true);
        divider.setBackground(new Color(200, 220, 210));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setPreferredSize(new Dimension(0, 1));
        pnlContentBottomRight.add(divider);

        pnlContentBottomRight.add(Box.createVerticalStrut(10));

//		Row 3: Tổng tiền (in đậm, nổi bật)
        pnlContentBottomRight.add(row3 = new JPanel(new BorderLayout()));
        row3.setOpaque(false);
        row3.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));
        row3.add(lblLeft3 = new JLabel("Tổng tiền"), BorderLayout.WEST);
        row3.add(lblRight3 = new JLabel("0đ"), BorderLayout.EAST);
        lblLeft3.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblRight3.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblRight3.setForeground(Colors.PRIMARY);

        pnlContentBottomRight.add(Box.createVerticalStrut(14));

//		Nút "Thanh toán" 
        btnLapHoaDon = new RoundedButton(Integer.MAX_VALUE, 40, 20, "Thanh toán", Colors.PRIMARY);
        btnLapHoaDon.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLapHoaDon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLapHoaDon.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));

//		Trạng thái ban đầu: disabled
        setButtonState(false);

        btnLapHoaDon.addActionListener(e -> {

            Frame parent = (Frame) SwingUtilities.getWindowAncestor(HoaDon_GUI.this);
            String tenKH = txtTenKH.getText().trim();
            String sdt = txtSDT.getText().trim();
            String nhanVien = lblNhanVienValue.getText();
            String thoiGian = lblThoiGianValue.getText();
            HoaDonPreviewDialog dialog = new HoaDonPreviewDialog(
                    parent, tenKH, sdt, nhanVien, thoiGian, tableModel);
            dialog.setVisible(true);
        });

        pnlContentBottomRight.add(btnLapHoaDon);
        pnlContent.add(Box.createVerticalStrut(15));

//		Phần dưới: danh sách sản phẩm đã chọn
        pnlContentBottom = new RoundedPanel(1500, 270, 15);
        pnlContentBottom.setLayout(new BorderLayout());
        pnlContent.add(pnlContentBottom);
        pnlContentBottom.setBackground(Colors.BACKGROUND);
        pnlContentBottom.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//		Giới hạn chiều cao để không chiếm hết màn hình
        pnlContentBottom.setMinimumSize(new Dimension(0, 270));
        pnlContentBottom.setMaximumSize(new Dimension(Integer.MAX_VALUE, 320));

//		Phần thông tin bản
        pnlContentBottom.add(pnlTitleBottom = new JPanel(new BorderLayout()), BorderLayout.NORTH);
        pnlTitleBottom.setOpaque(false);
        pnlTitleBottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        pnlsubTitleBottom = new JPanel();
        pnlsubTitleBottom.setOpaque(false);
        pnlTitleBottom.add(pnlsubTitleBottom, BorderLayout.WEST);
        pnlsubTitleBottom.setLayout(new BoxLayout(pnlsubTitleBottom, BoxLayout.Y_AXIS));

        pnlsubTitleBottom.add(lblTitleBottom1 = new JLabel("Danh sách sản phẩm đã chọn"));
        lblTitleBottom1.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        pnlsubTitleBottom.add(Box.createVerticalStrut(5));
        pnlsubTitleBottom.add(lblsubTitleBottom = new JLabel(String.format("%d sản phẩm đã chọn", selectedList.size())));
//		 Phần nút xoá tất cả
        pnlTitleBottom.add(btnXoaTatCa = new RoundedButton(120, 35, 15, "Xóa tất cả", Colors.DANGER), BorderLayout.EAST);
        btnXoaTatCa.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));

        btnXoaTatCa.addActionListener(e -> {
            selectedList.clear();
            refreshSelectedList();
            loadProducts(currentList);
            updateSummary();
        });

//		Phần table danh sách sản phẩm đã chọn
        pnlContentBottom.add(createSelectedListPanel(), BorderLayout.CENTER);

//		Phần tổng tiền
        pnlContentBottom.add(pnlTotelPrice = new JPanel(), BorderLayout.SOUTH);
        pnlTotelPrice.setOpaque(false);
        pnlTotelPrice.setLayout(new BoxLayout(pnlTotelPrice, BoxLayout.X_AXIS));
        pnlTotelPrice.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        pnlTotelPrice.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlTotelPrice.add(lblTotal = new JLabel("Tổng tiền: "));
        pnlTotelPrice.add(Box.createHorizontalStrut(5));
        pnlTotelPrice.add(lblTotalPrice = new JLabel(String.format("%.0fđ", totalPrice)));

        lblTotal.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblTotalPrice.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblTotalPrice.setForeground(Colors.PRIMARY);
    }

    private JScrollPane createSelectedListPanel() {
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 1 || col == 4;
            }

            @Override
            public Class<?> getColumnClass(int col) {
                if (col == 1 || col == 2 || col == 3) {
                    return Integer.class;
                }
                return String.class;
            }
        };

        tblSelected = new JTable(tableModel);
        tblSelected.setRowHeight(32);
        tblSelected.getTableHeader().setReorderingAllowed(false);
        tblSelected.setFillsViewportHeight(false);
        tblSelected.setBackground(Colors.BACKGROUND);
        tblSelected.setGridColor(Colors.BORDER_LIGHT);
        tblSelected.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        tblSelected.getTableHeader().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        tblSelected.setShowHorizontalLines(true);
        tblSelected.setShowVerticalLines(false);

//		Cột Số lượng dùng QuantityEditor
        tblSelected.getColumnModel().getColumn(1).setCellEditor(new QuantityEditor());

//		Cột Xóa — width nhỏ cố định, nút ✕ đỏ
        tblSelected.getColumnModel().getColumn(4).setPreferredWidth(60);
        tblSelected.getColumnModel().getColumn(4).setMaxWidth(60);
        tblSelected.getColumnModel().getColumn(4).setCellRenderer(new DeleteButtonRenderer());
        tblSelected.getColumnModel().getColumn(4).setCellEditor(new DeleteButtonEditor());

        tableModel.addTableModelListener(e -> updateSummary());

        return new JScrollPane(tblSelected);
    }

//	Renderer: vẽ nút ✕ trong ô
    private class DeleteButtonRenderer implements TableCellRenderer {

        private final JButton btn = new JButton("x");

        {
            btn.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            btn.setForeground(Colors.DANGER);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return btn;
        }
    }

//	Editor: xử lý click nút ✕ — xóa hàng và cập nhật selectedList
    private class DeleteButtonEditor extends AbstractCellEditor implements TableCellEditor {

        private final JButton btn = new JButton("X");
        private int currentRow;

        {
            btn.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            btn.setForeground(Colors.DANGER);
            btn.setBorderPainted(false);
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btn.addActionListener(e -> {
                fireEditingStopped();
                String tenSP = (String) tableModel.getValueAt(currentRow, 0);
                tableModel.removeRow(currentRow);
                selectedList.removeIf(sp -> sp.getTenSP().equals(tenSP));
                lblsubTitleBottom.setText(String.format("%d sản phẩm đã chọn", selectedList.size()));
                loadProducts(currentList);
                updateSummary();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            return btn;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }
    }

//	Thêm một hàng vào table cho sản phẩm sp
    private void addRowToTable(SanPham sp) {
        tableModel.addRow(new Object[]{
            sp.getTenSP(),
            1,
            (int) sp.getGiaBan(),
            (int) sp.getGiaBan(),
            "✕"
        });
    }

//	Xóa hàng của sản phẩm sp khỏi table
    private void removeRowFromTable(SanPham sp) {
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            if (tableModel.getValueAt(i, 0).equals(sp.getTenSP())) {
                tableModel.removeRow(i);
                break;
            }
        }
    }

    private void refreshSelectedList() {
        tableModel.setRowCount(0);
        for (SanPham sp : selectedList) {
            addRowToTable(sp);
        }
        lblsubTitleBottom.setText(String.format("%d sản phẩm đã chọn", selectedList.size()));
    }

    // Bật/tắt nút và đổi màu theo trạng thái
    private void setButtonState(boolean enabled) {
        if (enabled) {
            btnLapHoaDon.setEnabled(true);
            btnLapHoaDon.setBackground(Colors.PRIMARY);
            btnLapHoaDon.setForeground(Colors.BACKGROUND);
            btnLapHoaDon.setCursor(new Cursor(Cursor.HAND_CURSOR));
            ((RoundedButton) btnLapHoaDon).setEnableHover(true);
        } else {
            btnLapHoaDon.setEnabled(false);
            btnLapHoaDon.setBackground(Colors.BORDER);
            btnLapHoaDon.setForeground(Colors.MUTED);
            btnLapHoaDon.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            ((RoundedButton) btnLapHoaDon).setEnableHover(false);
        }
    }

    // Cập nhật tóm tắt sau mỗi lần chọn sản phẩm
    private void updateSummary() {
        int soMatHang = selectedList.size();

        int tongSoLuong = 0;
        double tongTien = 0;

        if (tableModel != null) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int qty = (int) tableModel.getValueAt(i, 1);
                int total = (int) tableModel.getValueAt(i, 3);
                tongSoLuong += qty;
                tongTien += total;
            }
        }

        lblRight1.setText(soMatHang + " sản phẩm");
        lblRight2.setText(tongSoLuong + " đơn vị");
        lblRight3.setText(String.format("%.0fđ", tongTien));
        lblTotalPrice.setText(String.format("%.0fđ", tongTien));

        setButtonState(soMatHang > 0);
    }

    private JPanel createProductCard(SanPham sp, boolean isSelected) {
        JPanel card = new RoundedPanel(200, 170, 20);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(CARD_WIDTH, CARD_HEIGHT));

        if (isSelected) {
            card.setBackground(Colors.PRIMARY_LIGHT);
            // card.setBorder(BorderFactory.createLineBorder(Colors.PRIMARY, 1));
        } else {
            card.setBackground(Colors.BACKGROUND);
            // card.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1));
        }

        card.setBorder(BorderFactory.createCompoundBorder(
                card.getBorder(),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setAlignmentX(Component.LEFT_ALIGNMENT);

        int lblMaxWidth = isSelected ? CARD_WIDTH - 130 : CARD_WIDTH - 40;
        JLabel lblTen = new JLabel("<html>" + sp.getTenSP() + "</html>");
        lblTen.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblTen.setForeground(Colors.TEXT_PRIMARY);
        lblTen.setPreferredSize(new Dimension(lblMaxWidth, 40));
        lblTen.setVerticalAlignment(JLabel.TOP);
        top.add(lblTen, BorderLayout.CENTER);

        if (isSelected) {
            JLabel badge = new JLabel("✓ Đã thêm");
            badge.setOpaque(true);
            badge.setBackground(Colors.SUCCESS_LIGHT);
            badge.setForeground(Colors.SUCCESS_DARK);
            badge.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            badge.setBorder(BorderFactory.createEmptyBorder(1, 4, 1, 4));
            top.add(badge, BorderLayout.EAST);
        }

        String loai = (sp.getLoaiSP().getTenLoaiSP() != null) ? sp.getLoaiSP().getTenLoaiSP().toString() : "Không rõ";
        JLabel lblLoai = new JLabel(loai);
        lblLoai.setForeground(Colors.TEXT_SECONDARY);
        lblLoai.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblLoai.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel priceRow = new JPanel(new BorderLayout());
        priceRow.setOpaque(false);
        priceRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblGia = new JLabel(String.format("%.0fđ", sp.getGiaBan()));
        lblGia.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblGia.setForeground(Colors.TEXT_PRIMARY);

        JLabel lblTon = new JLabel("Tồn: " + sp.getSoLuong());
        lblTon.setForeground(Colors.TEXT_SECONDARY);
        lblTon.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));

        priceRow.add(lblGia, BorderLayout.WEST);
        priceRow.add(lblTon, BorderLayout.EAST);

        Color btnBg = isSelected ? Colors.SUCCESS_LIGHT : Colors.PRIMARY;
        Color btnFg = isSelected ? Colors.SUCCESS_DARK : Colors.BACKGROUND;
        String btnText = isSelected ? "+ Thêm lần nữa" : "+ Thêm vào đơn";

        JButton btn = new RoundedButton(Integer.MAX_VALUE, 35, 20, btnText, btnBg);
        btn.setForeground(btnFg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);

        btn.addActionListener(e -> {
            if (selectedList.contains(sp)) {
                selectedList.remove(sp);
                removeRowFromTable(sp);
            } else {
                selectedList.add(sp);
                addRowToTable(sp);
            }
            lblsubTitleBottom.setText(String.format("%d sản phẩm đã chọn", selectedList.size()));
            loadProducts(currentList);
            updateSummary(); // cập nhật tóm tắt và trạng thái nút
        });

        card.add(top);
        card.add(Box.createVerticalStrut(10));
        card.add(lblLoai);
        card.add(Box.createVerticalStrut(10));
        card.add(priceRow);
        card.add(Box.createVerticalGlue());
        card.add(btn);

        return card;
    }

    private void loadProducts(List<SanPham> list) {
        currentList = list;
        pnlProductList.removeAll();
        for (SanPham sp : list) {
            pnlProductList.add(createProductCard(sp, selectedList.contains(sp)));
        }
        updateLayout(list.size());
    }

    private void updateLayout(int total) {
        int rows = 2;
        int hgap = 15;
        int vgap = 15;
        int cols = (int) Math.ceil((double) total / rows);
        int width = cols * (CARD_WIDTH + hgap) + hgap;
        int height = rows * (CARD_HEIGHT + vgap) + vgap;
        pnlProductList.setPreferredSize(new Dimension(width, height));
        pnlProductList.revalidate();
        pnlProductList.repaint();
    }

    private void filterAndLoad() {
        // Bước 1: lọc theo công dụng (O(n))
        List<SanPham> filtered = sanPhamService.locTheoCongDung(fullList, currentCongDung);
        // Bước 2: tìm kiếm theo từ khóa — hỗ trợ cả tên và mã sản phẩm
        // Nếu keyword bắt đầu "SP" → binary search O(log n + k), ngược lại linear O(n)
        String keyword = txtSearch.getText().trim();
        if (!keyword.isEmpty()) {
            filtered = sanPhamService.timKiem(filtered, keyword);
        }
        loadProducts(filtered);
    }

}
