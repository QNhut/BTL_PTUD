package gui;

import constants.Colors;
import constants.FontStyle;
import entity.NhanVien;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JRadioButton;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import service.HoaDon_Service;
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
    private final HoaDon_Service hoaDonService = new HoaDon_Service();
    private final SanPham_Service sanPhamService = new SanPham_Service();
    private final service.KhachHang_Service khachHangService = new service.KhachHang_Service();
    private final service.LoaiSanPham_Service loaiSanPhamService = new service.LoaiSanPham_Service();
    private NhanVien nhanVien;
    private JLabel lblTenKHError;
    private JLabel lblSDTStatus;

    // ==== Member badge + diem su dung ====
    // KH tìm được qua SĐT (nếu có); null = chưa tra hoặc khách lẻ.
    private entity.KhachHang khachHangHienTai;
    private JLabel lblLoaiKH;          // “✓ Thành viên: ... (X điểm)” hoặc “Khách lẻ”
    private JLabel lblDiemLabel;        // “Điểm sử dụng (tối đa: X)”
    private JTextField txtDiemSuDung;
    private JLabel lblDiemError;

    // ==== Breakdown summary rows (5) ====
    private JLabel lblValTamTinh;
    private JLabel lblValThue;
    private JLabel lblValGiamKM;
    private JLabel lblValGiamDiem;
    private JLabel lblValThanhTien;

    // ==== Customer type toggle + panels ====
    private JRadioButton rdoKhachLe;
    private JRadioButton rdoCoThongTin;
    private JPanel pnlKHInfo;
    private JPanel pnlPoints;
    private JCheckBox chkDungDiem;
    @SuppressWarnings("rawtypes")
    private JComboBox cmbPTTT;
    private java.util.List<entity.PhuongThucThanhToan> dsPTTT = new java.util.ArrayList<>();


    public HoaDon_GUI() {
        this(null);
    }

    public HoaDon_GUI(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(Colors.BACKGROUND);
        // Gọi constructor phụ trước khi gọi setLayout

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

        txtSearch = new RoundedTextField(400, 35, 15, "Tìm kiếm sản phẩm...");
        txtSearch.setMaximumSize(new Dimension(400, 35));
        pnlSearchRow.add(txtSearch);
        pnlSearchRow.add(Box.createHorizontalStrut(10));

        JButton btnSearch = new RoundedButton(100, 35, 15, "Tìm", Colors.PRIMARY);
        btnSearch.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnSearch.setMaximumSize(new Dimension(100, 35));
        pnlSearchRow.add(btnSearch);
        pnlSearchRow.add(Box.createHorizontalStrut(10));

//      Nút sổ xuống chọn công dụng — tải từ DB
        java.util.List<entity.LoaiSanPham> dsLoai = loaiSanPhamService.layDanhSachLoaiSanPham();
        JPopupMenu popupCongDung = new JPopupMenu();
        popupCongDung.setBackground(Colors.BACKGROUND);
        popupCongDung.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Colors.BORDER_LIGHT, 1),
                BorderFactory.createEmptyBorder(6, 0, 6, 0)
        ));

        // Mục "Tất cả"
        JMenuItem itemTatCa = new JMenuItem("Tất cả");
        itemTatCa.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        itemTatCa.setForeground(Colors.TEXT_PRIMARY);
        itemTatCa.setBackground(Colors.BACKGROUND);
        itemTatCa.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 30));
        itemTatCa.setCursor(new Cursor(Cursor.HAND_CURSOR));
        itemTatCa.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                itemTatCa.setBackground(Colors.PRIMARY_LIGHT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                itemTatCa.setBackground(Colors.BACKGROUND);
            }
        });
        itemTatCa.addActionListener(ev -> {
            btnCongDung.setText("▼ Tất cả");
            loadProducts(currentList);
        });
        popupCongDung.add(itemTatCa);

        for (entity.LoaiSanPham loai : dsLoai) {
            JMenuItem item = new JMenuItem(loai.getTenLoaiSP());
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
                btnCongDung.setText("▼ " + loai.getTenLoaiSP());
                // Lọc sản phẩm theo loại
                List<SanPham> filtered = new ArrayList<>();
                for (SanPham sp : currentList) {
                    if (sp.getLoaiSP() != null
                            && loai.getMaLoaiSP().equals(sp.getLoaiSP().getMaLoaiSP())) {
                        filtered.add(sp);
                    }
                }
                Map<String, SanPham_Service.TonKhoInfo> tonKhoMap = sanPhamService.tinhTonKhoTatCa(filtered);
                pnlProductList.removeAll();
                for (SanPham sp : filtered) {
                    SanPham_Service.TonKhoInfo info = tonKhoMap.get(sp.getMaSanPham());
                    int tonKho = (info != null) ? info.tonKho : 0;
                    pnlProductList.add(createProductCard(sp, selectedList.contains(sp), tonKho));
                }
                updateLayout(filtered.size());
            });
            popupCongDung.add(item);
        }

        btnCongDung = new RoundedButton(150, 35, 15, "▼ Công dụng", Colors.SECONDARY);
        btnCongDung.setForeground(Colors.TEXT_PRIMARY);
        btnCongDung.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        btnCongDung.setMaximumSize(new Dimension(150, 35));
        btnCongDung.addActionListener(e -> {
            popupCongDung.setPreferredSize(new Dimension(180, popupCongDung.getPreferredSize().height));
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

        // Tải danh sách sản phẩm từ DB
        List<SanPham> list = loadProductsFromDB();
        loadProducts(list);
        pnlContentTopLeft.revalidate();
        pnlContentTopLeft.repaint();

        // Sự kiện tìm kiếm sản phẩm theo tên/mã
        btnSearch.addActionListener(ev -> {
            String kw = txtSearch.getText().trim();
            List<SanPham> filtered = sanPhamService.timKiem(currentList, kw);
            Map<String, SanPham_Service.TonKhoInfo> tonKhoMap = sanPhamService.tinhTonKhoTatCa(filtered);
            pnlProductList.removeAll();
            for (SanPham sp : filtered) {
                SanPham_Service.TonKhoInfo info = tonKhoMap.get(sp.getMaSanPham());
                int tonKho = (info != null) ? info.tonKho : 0;
                pnlProductList.add(createProductCard(sp, selectedList.contains(sp), tonKho));
            }
            updateLayout(filtered.size());
        });
        txtSearch.addActionListener(ev -> btnSearch.doClick());

        pnlContentTop.add(Box.createHorizontalStrut(20));

//		Bên phải: thông tin hóa đơn (BorderLayout: CENTER = form cuộn, SOUTH = summary cố định)
        pnlContentTop.add(pnlContentTopRight = new RoundedPanel(350, 470, 15));
        pnlContentTopRight.setBackground(Colors.BACKGROUND);
        pnlContentTopRight.setLayout(new BorderLayout(0, 0));
        pnlContentTopRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // -------- Form (phần cuộn) --------
        JPanel pnlForm = new JPanel();
        pnlForm.setLayout(new BoxLayout(pnlForm, BoxLayout.Y_AXIS));
        pnlForm.setOpaque(false);
        pnlForm.setBorder(BorderFactory.createEmptyBorder(0, 0, 4, 0));

        pnlForm.add(lblcontentRight = new JLabel("Thông tin hóa đơn"));
        lblcontentRight.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblcontentRight.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Nhân viên + thời gian (tách 2 dòng để tránh bị mất khi scrollbar xuất hiện)
        pnlForm.add(Box.createVerticalStrut(5));
        String tenNV = (nhanVien != null) ? nhanVien.getTenNhanVien() : "--";
        String thoiGianTao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        lblNhanVienValue = new JLabel("NV: " + tenNV);
        lblNhanVienValue.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblNhanVienValue.setForeground(Colors.TEXT_SECONDARY);
        lblNhanVienValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblNhanVienValue.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        pnlForm.add(lblNhanVienValue);

        lblThoiGianValue = new JLabel("🕒 " + thoiGianTao);
        lblThoiGianValue.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblThoiGianValue.setForeground(Colors.TEXT_SECONDARY);
        lblThoiGianValue.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblThoiGianValue.setMaximumSize(new Dimension(Integer.MAX_VALUE, 18));
        pnlForm.add(lblThoiGianValue);

        pnlForm.add(Box.createVerticalStrut(8));
        JPanel divTop = new JPanel();
        divTop.setOpaque(true);
        divTop.setBackground(Colors.BORDER_LIGHT);
        divTop.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divTop.setPreferredSize(new Dimension(0, 1));
        divTop.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(divTop);
        pnlForm.add(Box.createVerticalStrut(8));

        // ---- Toggle Khách lẻ / Có thông tin ----
        JLabel lblLoaiKHTitle = new JLabel("Khách hàng");
        lblLoaiKHTitle.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblLoaiKHTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(lblLoaiKHTitle);
        pnlForm.add(Box.createVerticalStrut(4));

        rdoKhachLe = new JRadioButton("Khách lẻ");
        rdoKhachLe.setSelected(true);
        rdoKhachLe.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        rdoKhachLe.setOpaque(false);
        rdoCoThongTin = new JRadioButton("Có thông tin KH");
        rdoCoThongTin.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        rdoCoThongTin.setOpaque(false);
        ButtonGroup grpLoaiKH = new ButtonGroup();
        grpLoaiKH.add(rdoKhachLe);
        grpLoaiKH.add(rdoCoThongTin);
        JPanel pnlToggle = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        pnlToggle.setOpaque(false);
        pnlToggle.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlToggle.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        pnlToggle.add(rdoKhachLe);
        pnlToggle.add(rdoCoThongTin);
        pnlForm.add(pnlToggle);
        pnlForm.add(Box.createVerticalStrut(6));

        // ---- PTTT combobox (luôn hiển thị để user không bỏ lỡ) ----
        JLabel lblPTTTMain = new JLabel("Phương thức thanh toán");
        lblPTTTMain.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblPTTTMain.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(lblPTTTMain);
        pnlForm.add(Box.createVerticalStrut(4));

        dsPTTT = hoaDonService.getDSPhuongThucThanhToan();
        @SuppressWarnings("unchecked")
        JComboBox<entity.PhuongThucThanhToan> cmbPTTTTyped = new JComboBox<>();
        cmbPTTT = cmbPTTTTyped;
        for (entity.PhuongThucThanhToan pt : dsPTTT) cmbPTTTTyped.addItem(pt);
        cmbPTTTTyped.setRenderer((lst, value, index, isSel, hasFocus) -> {
            JLabel lbl = new JLabel(value != null ? value.getTenPTTT() : "");
            lbl.setOpaque(true);
            lbl.setBackground(isSel ? Colors.PRIMARY_LIGHT : java.awt.Color.WHITE);
            lbl.setForeground(Colors.TEXT_PRIMARY);
            lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            lbl.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            return lbl;
        });
        cmbPTTTTyped.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        cmbPTTTTyped.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbPTTTTyped.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        pnlForm.add(cmbPTTTTyped);
        pnlForm.add(Box.createVerticalStrut(8));

        // ---- Divider trước khu KH info ----
        JPanel divKH = new JPanel();
        divKH.setOpaque(true);
        divKH.setBackground(Colors.BORDER_LIGHT);
        divKH.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divKH.setPreferredSize(new Dimension(0, 1));
        divKH.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlForm.add(divKH);
        pnlForm.add(Box.createVerticalStrut(6));

        // ---- Panel thông tin KH (ẩn mặc định) ----
        pnlKHInfo = new JPanel();
        pnlKHInfo.setLayout(new BoxLayout(pnlKHInfo, BoxLayout.Y_AXIS));
        pnlKHInfo.setOpaque(false);
        pnlKHInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlKHInfo.setVisible(false);

        pnlKHInfo.add(lblsubcontentRight1 = new JLabel("Tên khách hàng *"));
        lblsubcontentRight1.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblsubcontentRight1.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlKHInfo.add(Box.createVerticalStrut(4));
        pnlKHInfo.add(txtTenKH = new RoundedTextField(Integer.MAX_VALUE, 34, 12, "Nhập tên khách hàng"));
        txtTenKH.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblTenKHError = new JLabel();
        lblTenKHError.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblTenKHError.setForeground(Colors.DANGER);
        lblTenKHError.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblTenKHError.setVisible(false);
        pnlKHInfo.add(lblTenKHError);
        txtTenKH.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { lblTenKHError.setVisible(false); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { lblTenKHError.setVisible(false); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });

        pnlKHInfo.add(Box.createVerticalStrut(6));
        pnlKHInfo.add(lblsubcontentRight2 = new JLabel("Số điện thoại"));
        lblsubcontentRight2.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblsubcontentRight2.setAlignmentX(Component.LEFT_ALIGNMENT);

        pnlKHInfo.add(Box.createVerticalStrut(4));
        pnlKHInfo.add(txtSDT = new RoundedTextField(Integer.MAX_VALUE, 34, 12, "Nhập số điện thoại (10 số)"));
        txtSDT.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblSDTStatus = new JLabel();
        lblSDTStatus.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSDTStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblSDTStatus.setVisible(false);
        pnlKHInfo.add(lblSDTStatus);
        txtSDT.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { lblSDTStatus.setVisible(false); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { lblSDTStatus.setVisible(false); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });
        txtSDT.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override public void focusLost(java.awt.event.FocusEvent e) { timKhachHangTheoSDT(); }
        });
        txtSDT.addActionListener(e -> timKhachHangTheoSDT());

        pnlKHInfo.add(Box.createVerticalStrut(5));
        lblLoaiKH = new JLabel();
        lblLoaiKH.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblLoaiKH.setForeground(Colors.SUCCESS_DARK);
        lblLoaiKH.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblLoaiKH.setVisible(false);
        pnlKHInfo.add(lblLoaiKH);

        pnlForm.add(pnlKHInfo);

        // ---- Panel điểm (ẩn đến khi thành viên có điểm) ----
        pnlPoints = new JPanel();
        pnlPoints.setLayout(new BoxLayout(pnlPoints, BoxLayout.Y_AXIS));
        pnlPoints.setOpaque(false);
        pnlPoints.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlPoints.setVisible(false);
        pnlPoints.setBorder(BorderFactory.createEmptyBorder(4, 0, 0, 0));

        chkDungDiem = new JCheckBox("Sử dụng điểm tích lũy");
        chkDungDiem.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        chkDungDiem.setOpaque(false);
        chkDungDiem.setAlignmentX(Component.LEFT_ALIGNMENT);
        chkDungDiem.setEnabled(false);
        chkDungDiem.addActionListener(e -> {
            boolean on = chkDungDiem.isSelected();
            txtDiemSuDung.setEnabled(on);
            lblDiemLabel.setVisible(on);
            if (!on) txtDiemSuDung.setText("0");
            updateSummary();
        });
        pnlPoints.add(chkDungDiem);

        pnlPoints.add(Box.createVerticalStrut(3));
        lblDiemLabel = new JLabel("Điểm muốn dùng — 1 điểm = 1.000đ");
        lblDiemLabel.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblDiemLabel.setForeground(Colors.TEXT_SECONDARY);
        lblDiemLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDiemLabel.setVisible(false);
        pnlPoints.add(lblDiemLabel);

        pnlPoints.add(Box.createVerticalStrut(3));
        txtDiemSuDung = new RoundedTextField(Integer.MAX_VALUE, 32, 12, "0");
        txtDiemSuDung.setAlignmentX(Component.LEFT_ALIGNMENT);
        txtDiemSuDung.setEnabled(false);
        pnlPoints.add(txtDiemSuDung);

        lblDiemError = new JLabel();
        lblDiemError.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblDiemError.setForeground(Colors.DANGER);
        lblDiemError.setAlignmentX(Component.LEFT_ALIGNMENT);
        lblDiemError.setVisible(false);
        pnlPoints.add(lblDiemError);

        txtDiemSuDung.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { updateSummary(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { updateSummary(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) {}
        });
        pnlForm.add(pnlPoints);

        // ---- Toggle action listeners ----
        rdoKhachLe.addActionListener(e -> {
            pnlKHInfo.setVisible(false);
            pnlPoints.setVisible(false);
            txtTenKH.setText("");
            txtSDT.setText("");
            lblSDTStatus.setVisible(false);
            khachHangHienTai = null;
            lblDiemError.setVisible(false);
            updateSummary();
            pnlForm.revalidate();
            pnlForm.repaint();
        });
        rdoCoThongTin.addActionListener(e -> {
            pnlKHInfo.setVisible(true);
            pnlForm.revalidate();
            pnlForm.repaint();
        });

        // Scroll pane cho form (luôn hiển thị scrollbar để width ổn định)
        JScrollPane scrollForm = new JScrollPane(pnlForm,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        scrollForm.getViewport().setBackground(Colors.BACKGROUND);
        scrollForm.getVerticalScrollBar().setUnitIncrement(12);
        pnlContentTopRight.add(scrollForm, BorderLayout.CENTER);

        // -------- Fixed bottom: summary + button --------
        JPanel pnlFixed = new JPanel();
        pnlFixed.setLayout(new BoxLayout(pnlFixed, BoxLayout.Y_AXIS));
        pnlFixed.setOpaque(false);
        pnlFixed.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, Colors.BORDER_LIGHT));
        pnlContentTopRight.add(pnlFixed, BorderLayout.SOUTH);

        pnlFixed.add(pnlContentBottomRight = new RoundedPanel(330, 170, 15));
        pnlContentBottomRight.setLayout(new BoxLayout(pnlContentBottomRight, BoxLayout.Y_AXIS));
        pnlContentBottomRight.setBorder(BorderFactory.createEmptyBorder(8, 10, 6, 10));
        pnlContentBottomRight.setMaximumSize(new Dimension(Integer.MAX_VALUE, 170));
        pnlContentBottomRight.setBackground(Colors.SUCCESS_LIGHT);
        pnlContentBottomRight.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Row: Tạm tính
        pnlContentBottomRight.add(row1 = makeSummaryRow("Tạm tính (sau KM)", "0đ", false));
        pnlContentBottomRight.add(Box.createVerticalStrut(3));
        // Row: Thuế
        pnlContentBottomRight.add(row2 = makeSummaryRow("Thuế (+)", "0đ", false));
        pnlContentBottomRight.add(Box.createVerticalStrut(3));
        // Row: Giảm KM
        JPanel rowGiamKM = makeSummaryRow("Giảm giá KM (-)", "0đ", false);
        pnlContentBottomRight.add(rowGiamKM);
        pnlContentBottomRight.add(Box.createVerticalStrut(3));
        // Row: Giảm điểm
        JPanel rowGiamDiem = makeSummaryRow("Điểm sử dụng (-)", "0đ", false);
        pnlContentBottomRight.add(rowGiamDiem);
        pnlContentBottomRight.add(Box.createVerticalStrut(5));

        lblValTamTinh  = (JLabel) ((JPanel) row1).getComponent(1);
        lblValThue     = (JLabel) ((JPanel) row2).getComponent(1);
        lblValGiamKM   = (JLabel) rowGiamKM.getComponent(1);
        lblValGiamDiem = (JLabel) rowGiamDiem.getComponent(1);

        JPanel divider = new JPanel();
        divider.setOpaque(true);
        divider.setBackground(new Color(200, 220, 210));
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setPreferredSize(new Dimension(0, 1));
        pnlContentBottomRight.add(divider);
        pnlContentBottomRight.add(Box.createVerticalStrut(5));

        row3 = makeSummaryRow("THÀNH TIỀN", "0đ", true);
        pnlContentBottomRight.add(row3);
        lblValThanhTien = (JLabel) ((JPanel) row3).getComponent(1);

        // Compat refs
        lblLeft1 = (JLabel) ((JPanel) row1).getComponent(0);  lblRight1 = lblValTamTinh;
        lblLeft2 = (JLabel) ((JPanel) row2).getComponent(0);  lblRight2 = lblValThue;
        lblLeft3 = (JLabel) ((JPanel) row3).getComponent(0);  lblRight3 = lblValThanhTien;

        pnlFixed.add(Box.createVerticalStrut(6));

//		Nút "Thanh toán"
        btnLapHoaDon = new RoundedButton(Integer.MAX_VALUE, 40, 20, "Thanh toán", Colors.PRIMARY);
        btnLapHoaDon.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnLapHoaDon.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        btnLapHoaDon.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));

        setButtonState(false);
        btnLapHoaDon.addActionListener(e -> moXemTruocHoaDon());
        pnlFixed.add(btnLapHoaDon);

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
                if (col == 1) {
                    return Integer.class;   // Số lượng

                }
                if (col == 2 || col == 3) {
                    return Double.class; // Giá / Thành tiền

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
        double gia = sp.getGiaSauKM();
        tableModel.addRow(new Object[]{
            sp.getTenSP(),
            1,
            gia,
            gia,
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
        // Build cart items "lite" for breakdown computation
        List<HoaDon_Service.CartItem> items = buildCartItems();
        int diem = parseDiemSuDung();
        HoaDon_Service.HoaDonSummary sum = hoaDonService.tinhTongKet(items, diem);

        if (lblValTamTinh != null) {
            lblValTamTinh.setText(formatVND(sum.tienHang));
            lblValThue.setText(formatVND(sum.tienThue));
            lblValGiamKM.setText("- " + formatVND(sum.tienGiamGia));
            lblValGiamDiem.setText("- " + formatVND(sum.tienGiamTuDiem)
                    + (sum.diemSuDung > 0 ? " (" + sum.diemSuDung + " đ)" : ""));
            lblValThanhTien.setText(formatVND(sum.thanhTien));
        }
        if (lblTotalPrice != null) {
            lblTotalPrice.setText(formatVND(sum.thanhTien));
        }
        // Cập nhật label "X sản phẩm đã chọn"
        if (lblsubTitleBottom != null) {
            lblsubTitleBottom.setText(String.format("%d sản phẩm đã chọn", items.size()));
        }
        setButtonState(!items.isEmpty());
    }

    private static String formatVND(double v) {
        return String.format("%,.0fđ", v);
    }

    // Lấy số điểm sử dụng từ ô nhập (0 nếu trống/khách lẻ).
    private int parseDiemSuDung() {
        if (txtDiemSuDung == null || !txtDiemSuDung.isEnabled()) return 0;
        String s = txtDiemSuDung.getText().trim();
        if (s.isEmpty()) return 0;
        try {
            int v = Integer.parseInt(s);
            return Math.max(0, v);
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    // Tạo 1 hàng "label : value" cho summary panel. Component(0)=label, Component(1)=value.
    private JPanel makeSummaryRow(String label, String value, boolean bold) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, bold ? 24 : 20));
        JLabel lblL = new JLabel(label);
        JLabel lblR = new JLabel(value);
        if (bold) {
            lblL.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
            lblR.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
            lblR.setForeground(Colors.PRIMARY);
        } else {
            lblL.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
            lblR.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        }
        row.add(lblL, BorderLayout.WEST);
        row.add(lblR, BorderLayout.EAST);
        return row;
    }

    private JPanel createProductCard(SanPham sp, boolean isSelected, int tonKho) {
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

        int lblMaxWidth = isSelected ? CARD_WIDTH - 100 : CARD_WIDTH - 30;
        JLabel lblTen = new JLabel("<html><div style='width:" + lblMaxWidth + "px'>" + sp.getTenSP() + "</div></html>");
        lblTen.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblTen.setForeground(Colors.TEXT_PRIMARY);
        top.add(lblTen, BorderLayout.CENTER);

        if (isSelected) {
            JLabel badge = new JLabel("✓ Đã thêm");
            badge.setOpaque(true);
            badge.setBackground(Colors.SUCCESS_LIGHT);
            badge.setForeground(Colors.SUCCESS_DARK);
            badge.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
            badge.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
            top.add(badge, BorderLayout.EAST);
        }

        String loai = (sp.getLoaiSP() != null) ? sp.getLoaiSP().getTenLoaiSP() : "Không rõ";
        JLabel lblLoai = new JLabel(loai);
        lblLoai.setForeground(Colors.TEXT_SECONDARY);
        lblLoai.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblLoai.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel priceRow = new JPanel(new BorderLayout());
        priceRow.setOpaque(false);
        priceRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        String giaHtml;
        if (sp.coKhuyenMai()) {
            giaHtml = "<html><span style='color:#ED5A2D; font-weight:bold;'>"
                    + String.format("%,.0fđ", sp.getGiaSauKM()) + "</span><br>"
                    + "<span style='color:#9CA3AF; text-decoration:line-through; font-size:9px;'>"
                    + String.format("%,.0fđ", sp.getGiaThanh()) + "</span></html>";
        } else {
            giaHtml = String.format("%,.0fđ", sp.getGiaThanh());
        }
        JLabel lblGia = new JLabel(giaHtml);
        lblGia.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblGia.setForeground(Colors.TEXT_PRIMARY);

        JLabel lblTon = new JLabel("Tồn: " + tonKho);
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

    private int getTonKhoHienTai(SanPham sanPham) {
        if (sanPham == null) {
            return 0;
        }
        return sanPhamService.layTonKho(sanPham.getMaSanPham());
    }

    //===="Tải danh sách sản phẩm đang hoạt động từ DB"=====
    private List<SanPham> loadProductsFromDB() {
        try {
            List<SanPham> all = sanPhamService.layDanhSachSanPham();
            List<SanPham> active = new ArrayList<>();
            for (SanPham sp : all) {
                if (sp.isTrangThai()) {
                    active.add(sp);
                }
            }
            return active;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //===="Xây dựng danh sách CartItem từ bảng đã chọn và selectedList"=====
    private List<HoaDon_Service.CartItem> buildCartItems() {
        Map<String, SanPham> spMap = new HashMap<>();
        for (SanPham sp : selectedList) {
            spMap.put(sp.getTenSP(), sp);
        }
        List<HoaDon_Service.CartItem> items = new ArrayList<>();
        for (int i = 0; i < tableModel.getRowCount(); i++) {
            String ten = (String) tableModel.getValueAt(i, 0);
            int sl = ((Number) tableModel.getValueAt(i, 1)).intValue();
            double gia = ((Number) tableModel.getValueAt(i, 2)).doubleValue();
            SanPham sp = spMap.get(ten);
            if (sp != null && sl > 0) {
                items.add(new HoaDon_Service.CartItem(sp, sl, gia));
            }
        }
        return items;
    }

    //=="=="Tra cứu KH theo số điện thoại, tự điền tên nếu tìm thấy"====="
    private void timKhachHangTheoSDT() {
        String sdt = txtSDT.getText().trim();
        if (sdt.isEmpty()) {
            lblSDTStatus.setVisible(false);
            setMemberMode(null);
            return;
        }
        if (!sdt.matches("\\d{10}")) {
            lblSDTStatus.setText("✗ Số điện thoại phải có đúng 10 chữ số");
            lblSDTStatus.setForeground(Colors.DANGER);
            lblSDTStatus.setVisible(true);
            setMemberMode(null);
            return;
        }
        entity.KhachHang kh = khachHangService.layKHTheoSDT(sdt);
        if (kh != null && !service.HoaDon_Service.SDT_KHACH_LE.equals(kh.getSoDienThoai())) {
            txtTenKH.setText(kh.getTenKhachHang());
            lblSDTStatus.setText("✓ Đã tìm thấy: " + kh.getTenKhachHang());
            lblSDTStatus.setForeground(Colors.SUCCESS_DARK);
            setMemberMode(kh);
        } else {
            lblSDTStatus.setText("ℹ Số mới – sẽ tạo khách hàng khi thanh toán");
            lblSDTStatus.setForeground(Colors.TEXT_SECONDARY);
            setMemberMode(null);
        }
        lblSDTStatus.setVisible(true);
    }

    // Cập nhật UI badge thành viên + hiển thị ô nhập điểm.
    private void setMemberMode(entity.KhachHang kh) {
        khachHangHienTai = kh;
        if (kh != null) {
            String diemText = kh.getDiemTichLuy() > 0 ? " (" + kh.getDiemTichLuy() + " điểm)" : " (0 điểm)";
            lblLoaiKH.setText("✓ Thành viên: " + kh.getTenKhachHang() + diemText);
            lblLoaiKH.setForeground(Colors.SUCCESS_DARK);
            lblLoaiKH.setVisible(true);
            if (kh.getDiemTichLuy() > 0) {
                chkDungDiem.setText("Sử dụng điểm tích lũy (" + kh.getDiemTichLuy() + " điểm)");
                chkDungDiem.setEnabled(true);
                lblDiemLabel.setText("Điểm muốn dùng — 1 điểm = 1.000đ (tối đa " + kh.getDiemTichLuy() + ")");
                pnlPoints.setVisible(true);
            } else {
                pnlPoints.setVisible(false);
            }
        } else {
            lblLoaiKH.setVisible(false);
            lblLoaiKH.setText("");
            pnlPoints.setVisible(false);
            chkDungDiem.setSelected(false);
            chkDungDiem.setEnabled(false);
            txtDiemSuDung.setText("0");
            txtDiemSuDung.setEnabled(false);
            lblDiemLabel.setVisible(false);
            lblDiemError.setVisible(false);
        }
        updateSummary();
        pnlContentTopRight.revalidate();
        pnlContentTopRight.repaint();
    }

    //=="=="Mở dialog xem trước hóa đơn trước khi xác nhận thanh toán"====="
    private void moXemTruocHoaDon() {
        String tenKH = null, sdt = null;

        if (rdoCoThongTin != null && rdoCoThongTin.isSelected()) {
            tenKH = txtTenKH.getText().trim();
            sdt = txtSDT.getText().trim();
            boolean hasError = false;

            if (tenKH.isEmpty()) {
                lblTenKHError.setText("✗ Vui lòng nhập tên khách hàng");
                lblTenKHError.setVisible(true);
                hasError = true;
            } else {
                lblTenKHError.setVisible(false);
            }

            if (!sdt.isEmpty() && !sdt.matches("\\d{10}")) {
                lblSDTStatus.setText("✗ Số điện thoại phải có đúng 10 chữ số");
                lblSDTStatus.setForeground(Colors.DANGER);
                lblSDTStatus.setVisible(true);
                hasError = true;
            }

            if (hasError) return;
        }

        List<HoaDon_Service.CartItem> items = buildCartItems();
        if (items.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn ít nhất một sản phẩm.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Validate điểm sử dụng
        int diemSuDung = 0;
        if (chkDungDiem != null && chkDungDiem.isSelected() && chkDungDiem.isEnabled()) {
            diemSuDung = parseDiemSuDung();
            if (diemSuDung > 0 && khachHangHienTai != null && diemSuDung > khachHangHienTai.getDiemTichLuy()) {
                lblDiemError.setText("✗ Vượt quá điểm hiện có (" + khachHangHienTai.getDiemTichLuy() + ")");
                lblDiemError.setVisible(true);
                return;
            }
        }
        lblDiemError.setVisible(false);

        // Kiểm tra tồn kho trước khi mở dialog
        String loi = hoaDonService.kiemTraTonKho(items);
        if (loi != null) {
            JOptionPane.showMessageDialog(this, loi, "Không đủ tồn kho", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Lấy PTTT đã chọn
        entity.PhuongThucThanhToan pttChon = (entity.PhuongThucThanhToan) cmbPTTT.getSelectedItem();
        String maPTTTChon = (pttChon != null) ? pttChon.getMaPTTT() : null;

        String maHD = hoaDonService.sinhMaHoaDon();
        String thoiGian = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        String tenNVStr = (nhanVien != null) ? nhanVien.getTenNhanVien() : "--";

        Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
        HoaDonPreviewDialog dialog = new HoaDonPreviewDialog(
                parentFrame,
                tenKH,
                sdt,
                tenNVStr,
                thoiGian,
                tableModel,
                maHD,
                hoaDonService,
                nhanVien,
                items,
                diemSuDung,
                maPTTTChon,
                this::resetSauThanhToan
        );
        dialog.setVisible(true);
    }

    //===="Reset toàn bộ form về trạng thái ban đầu sau khi thanh toán thành công"=====
    public void resetSauThanhToan() {
        selectedList.clear();
        tableModel.setRowCount(0);
        txtTenKH.setText("");
        txtSDT.setText("");
        txtSearch.setText("");
        if (rdoKhachLe != null) rdoKhachLe.setSelected(true);
        if (pnlKHInfo != null) pnlKHInfo.setVisible(false);
        if (pnlPoints != null) pnlPoints.setVisible(false);
        khachHangHienTai = null;
        setMemberMode(null);
        lblsubTitleBottom.setText("0 sản phẩm đã chọn");
        String thoiGianMoi = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));
        lblThoiGianValue.setText(thoiGianMoi);
        List<SanPham> freshList = loadProductsFromDB();
        loadProducts(freshList);
        updateSummary();
    }

    private void loadProducts(List<SanPham> list) {
        currentList = list;
        Map<String, SanPham_Service.TonKhoInfo> tonKhoMap = sanPhamService.tinhTonKhoTatCa(list);
        pnlProductList.removeAll();
        for (SanPham sp : list) {
            SanPham_Service.TonKhoInfo info = tonKhoMap.get(sp.getMaSanPham());
            int tonKho = (info != null) ? info.tonKho : 0;
            pnlProductList.add(createProductCard(sp, selectedList.contains(sp), tonKho));
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

}
