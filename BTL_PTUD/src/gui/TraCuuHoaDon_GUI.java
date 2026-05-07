//package gui;
//
//import constants.Colors;
//import constants.FontStyle;
//import entity.HoaDon;
//import exception.RoundedButton;
//import exception.RoundedComboBox;
//import exception.RoundedTextField;
//import exception.StyledTable;
//import java.awt.*;
//import java.text.NumberFormat;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.chrono.ChronoLocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.Locale;
//import javax.swing.*;
//import service.HoaDon_Service;
//
//public class TraCuuHoaDon_GUI extends JPanel {
//
//   // ── Services / Data ────────────────────────────────────────
//   private final HoaDon_Service hoaDonSV = new HoaDon_Service();
//   private final ArrayList<HoaDon> fullList = new ArrayList<>();
//   private final ArrayList<HoaDon> filteredList = new ArrayList<>();
//
//   // ── Filter controls ────────────────────────────────────────
//   private RoundedComboBox<String> cboTimKiemTheo;
//   private RoundedTextField txtKeyword;
//   private JSpinner spnTuNgay;
//   private JSpinner spnDenNgay;
//   private boolean tuNgayActive = false;
//   private boolean denNgayActive = false;
//   private RoundedButton btnTimKiem;
//   private RoundedButton btnXoaLoc;
//
//   // ── Results area ───────────────────────────────────────────
//   private JLabel lblSoLuong;
//   private StyledTable tblHoaDon;
//
//   private static final String[] COLUMN_NAMES = {
//       "Mã hóa đơn", "Khách hàng", "Nhân viên tạo",
//       "Thời gian", "Tổng tiền", "Thao tác"
//   };
//
//   private static final DateTimeFormatter DATE_FMT
//           = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//   private static final NumberFormat MONEY_FMT
//           = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
//
//   // ═══════════════════════════════════════════════════════════
//   public TraCuuHoaDon_GUI() {
//       setLayout(new BorderLayout(0, 16));
//       setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
//       setBackground(Colors.BACKGROUND);
//
//       add(buildTopSection(), BorderLayout.NORTH);
//       add(buildResultsPanel(), BorderLayout.CENTER);
//
//       loadData();
//   }
//
//   // ─────────────────────────────────────────────────────────
//   // TOP SECTION  (header + filter card stacked vertically)
//   // ─────────────────────────────────────────────────────────
//   private JPanel buildTopSection() {
//       JPanel pnl = new JPanel();
//       pnl.setLayout(new BoxLayout(pnl, BoxLayout.Y_AXIS));
//       pnl.setBackground(Colors.BACKGROUND);
//
//       // ── Page header ──
//       JPanel header = new JPanel();
//       header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
//       header.setBackground(Colors.BACKGROUND);
//       header.setBorder(BorderFactory.createEmptyBorder(0, 0, 16, 0));
//       header.setAlignmentX(Component.LEFT_ALIGNMENT);
//
//       JLabel lblTitle = new JLabel("Tra cứu hóa đơn");
//       lblTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
//       lblTitle.setForeground(Colors.TEXT_PRIMARY);
//
//       JLabel lblNote = new JLabel("Tìm kiếm và xem chi tiết hóa đơn trong hệ thống");
//       lblNote.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
//       lblNote.setForeground(Colors.TEXT_SECONDARY);
//
//       header.add(lblTitle);
//       header.add(Box.createVerticalStrut(4));
//       header.add(lblNote);
//
//       pnl.add(header);
//       pnl.add(buildFilterCard());
//       return pnl;
//   }
//
//   // ─────────────────────────────────────────────────────────
//   // FILTER CARD
//   // ─────────────────────────────────────────────────────────
//   private JPanel buildFilterCard() {
//       //====="Tạo card bo góc, chia 3 vùng: NORTH (tiêu đề), CENTER (tìm kiếm), SOUTH (ngày + nút)"=====
//       JPanel card = createCard();
//       card.setLayout(new BorderLayout(0, 0));
//       card.setAlignmentX(Component.LEFT_ALIGNMENT);
//       card.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
//
//       //====="NORTH – Tiêu đề card bộ lọc"=====
//       JPanel pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 12));
//       pnlNorth.setOpaque(false);
//       pnlNorth.add(buildCardTitleRow());
//       card.add(pnlNorth, BorderLayout.NORTH);
//
//       //====="CENTER – Hàng tìm kiếm: pairTimKiem bên trái (WEST), pairTuKhoa giãn rộng (CENTER)"=====
//       JPanel pnlCenter = new JPanel();
//       pnlCenter.setLayout(new FlowLayout(FlowLayout.CENTER, 8, 0));
//       pnlCenter.setOpaque(false);
//       pnlCenter.setBorder(BorderFactory.createEmptyBorder(4, 12, 8, 12));
//
//       // Cặp: label "Tìm kiếm theo" + cboTimKiemTheo – căn trái (WEST)
//       JPanel pairTimKiem = new JPanel();
//       pairTimKiem.setLayout(new BoxLayout(pairTimKiem, BoxLayout.Y_AXIS));
//       pairTimKiem.setOpaque(false);
//       JLabel lblTimKiemTheo = fieldLabel("Tìm kiếm theo");
//       lblTimKiemTheo.setAlignmentX(Component.LEFT_ALIGNMENT);
//       pairTimKiem.add(lblTimKiemTheo);
//       pairTimKiem.add(Box.createVerticalStrut(4));
//       cboTimKiemTheo = new RoundedComboBox<>(10);
//       cboTimKiemTheo.addItem("Mã hóa đơn");
//       cboTimKiemTheo.addItem("Khách hàng");
//       cboTimKiemTheo.addItem("Nhân viên tạo");
//       cboTimKiemTheo.setPreferredSize(new Dimension(180, 42));
//       cboTimKiemTheo.setAlignmentX(Component.LEFT_ALIGNMENT);
//       pairTimKiem.add(cboTimKiemTheo);
//       pnlCenter.add(pairTimKiem);
//       pnlCenter.add(Box.createHorizontalStrut(16));
//
//       // Cặp: label "Từ khóa" + txtKeyword – giãn rộng (CENTER)
//       JPanel pairTuKhoa = new JPanel();
//       pairTuKhoa.setLayout(new BoxLayout(pairTuKhoa, BoxLayout.Y_AXIS));
//       pairTuKhoa.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
//       pairTuKhoa.setOpaque(false);
//       JLabel lblTuKhoa = fieldLabel("Từ khóa");
//       lblTuKhoa.setAlignmentX(Component.LEFT_ALIGNMENT);
//       pairTuKhoa.add(lblTuKhoa);
//       pairTuKhoa.add(Box.createVerticalStrut(4));
//       txtKeyword = new RoundedTextField(500, 42, 10, "Nhập mã hóa đơn...");
//       txtKeyword.setAlignmentX(Component.LEFT_ALIGNMENT);
//       txtKeyword.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
//       pairTuKhoa.add(txtKeyword);
//       pnlCenter.add(pairTuKhoa);
//
//       card.add(pnlCenter, BorderLayout.CENTER);
//
//       //====="SOUTH – Hàng ngày lọc + nút Xóa lọc và Tìm kiếm (nhãn nằm trên spinner)"=====
//       JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
//       pnlSouth.setOpaque(false);
//       pnlSouth.setBorder(BorderFactory.createEmptyBorder(0, 12, 8, 12));
//
//       // Cặp: label "Từ ngày" + spnTuNgay
//       JPanel pairTuNgay = new JPanel();
//       pairTuNgay.setLayout(new BoxLayout(pairTuNgay, BoxLayout.Y_AXIS));
//       pairTuNgay.setOpaque(false);
//       JLabel lblTuNgay = fieldLabel("Từ ngày");
//       lblTuNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
//       pairTuNgay.add(lblTuNgay);
//       pairTuNgay.add(Box.createVerticalStrut(4));
//       spnTuNgay = makeDateSpinner();
//       spnTuNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
//       pairTuNgay.add(spnTuNgay);
//       pnlSouth.add(pairTuNgay);
//
//       // Cặp: label "Đến ngày" + spnDenNgay
//       JPanel pairDenNgay = new JPanel();
//       pairDenNgay.setLayout(new BoxLayout(pairDenNgay, BoxLayout.Y_AXIS));
//       pairDenNgay.setOpaque(false);
//       JLabel lblDenNgay = fieldLabel("Đến ngày");
//       lblDenNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
//       pairDenNgay.add(lblDenNgay);
//       pairDenNgay.add(Box.createVerticalStrut(4));
//       spnDenNgay = makeDateSpinner();
//       spnDenNgay.setAlignmentX(Component.LEFT_ALIGNMENT);
//       pairDenNgay.add(spnDenNgay);
//       pnlSouth.add(pairDenNgay);
//
//       pnlSouth.add(Box.createHorizontalStrut(20));
//
//       btnXoaLoc = new RoundedButton(130, 42, 10, "\u2715  Xóa lọc", Colors.SECONDARY);
//       btnXoaLoc.setForeground(Colors.TEXT_PRIMARY);
//       pnlSouth.add(btnXoaLoc);
//
//       btnTimKiem = new RoundedButton(150, 42, 10, "\uD83D\uDD0D  Tìm kiếm", Colors.PRIMARY);
//       pnlSouth.add(btnTimKiem);
//
//       card.add(pnlSouth, BorderLayout.SOUTH);
//
//       //====="Gắn sự kiện cho các thành phần điều khiển bộ lọc"=====
//       cboTimKiemTheo.addActionListener(e -> updatePlaceholder());
//       txtKeyword.addActionListener(e -> search());
//       btnTimKiem.addActionListener(e -> search());
//       btnXoaLoc.addActionListener(e -> resetFilter());
//       spnTuNgay.addChangeListener(e -> tuNgayActive = true);
//       spnDenNgay.addChangeListener(e -> denNgayActive = true);
//
//       return card;
//   }
//
//   //====="Tạo dòng tiêu đề với icon và nhãn cho card bộ lọc"=====
//   private JPanel buildCardTitleRow() {
//       JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
//       row.setOpaque(false);
//       JLabel icon = new JLabel("\uD83D\uDD0D");
//       icon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
//       icon.setForeground(Colors.PRIMARY);
//       JLabel title = new JLabel("Bộ lọc tìm kiếm");
//       title.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
//       title.setForeground(Colors.PRIMARY);
//       row.add(icon);
//       row.add(title);
//       return row;
//   }
//
//   // ─────────────────────────────────────────────────────────
//   // RESULTS PANEL
//   // ─────────────────────────────────────────────────────────
//   private JPanel buildResultsPanel() {
//       //====="Tạo card kết quả tìm kiếm"=====
//       JPanel card = createCard();
//       card.setLayout(new BorderLayout(0, 0));
//
//       //====="Thanh tiêu đề: tên section + số lượng kết quả"=====
//       JPanel bar = new JPanel(new BorderLayout());
//       bar.setOpaque(false);
//       bar.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
//
//       JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
//       left.setOpaque(false);
//       JLabel iconDoc = new JLabel("\uD83D\uDCC4");
//       iconDoc.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
//       JLabel lblResultTitle = new JLabel("Kết quả tìm kiếm");
//       lblResultTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
//       lblResultTitle.setForeground(Colors.TEXT_PRIMARY);
//       left.add(iconDoc);
//       left.add(lblResultTitle);
//
//       lblSoLuong = new JLabel("Tìm thấy 0 hóa đơn");
//       lblSoLuong.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
//       lblSoLuong.setForeground(Colors.PRIMARY);
//
//       bar.add(left, BorderLayout.WEST);
//       bar.add(lblSoLuong, BorderLayout.EAST);
//       card.add(bar, BorderLayout.NORTH);
//
//       //====="Khởi tạo StyledTable với 6 cột dữ liệu hóa đơn"=====
//       tblHoaDon = new StyledTable(COLUMN_NAMES, filteredList);
//
//       //====="Cột 0 – Mã hóa đơn: hiển thị màu PRIMARY dạng link"=====
//       tblHoaDon.setColumnRenderer(0, (tbl, val, sel, foc, row, col) -> {
//           JLabel lbl = new JLabel();
//           lbl.setOpaque(true);
//           lbl.setText(val instanceof HoaDon ? ((HoaDon) val).getMaHoaDon() : "");
//           lbl.setForeground(Colors.PRIMARY);
//           lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
//           lbl.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
//           lbl.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
//           return lbl;
//       });
//       tblHoaDon.setColumnWidth(0, 130);
//
//       //====="Cột 1 – Khách hàng: dòng 1 = tên KH, dòng 2 = số điện thoại"=====
//       tblHoaDon.setTwoLineColumn(1, 210,
//               v -> safeKHName((HoaDon) v),
//               v -> safeKHPhone((HoaDon) v));
//
//       //====="Cột 2 – Nhân viên tạo: dòng 1 = tên NV, dòng 2 = mã NV"=====
//       tblHoaDon.setTwoLineColumn(2, 190,
//               v -> safeNVName((HoaDon) v),
//               v -> safeNVMa((HoaDon) v));
//
//       //====="Cột 3 – Thời gian: định dạng dd/MM/yyyy từ ngayLap"=====
//       tblHoaDon.setSingleTextColumn(3, 150,
//               v -> {
//                   LocalDateTime d = ((HoaDon) v).getNgayLap();
//                   return d != null ? d.format(DATE_FMT) : "";
//               });
//
//       //====="Cột 4 – Tổng tiền: định dạng tiền VN, in đậm"=====
//       tblHoaDon.setColumnRenderer(4, (tbl, val, sel, foc, row, col) -> {
//           JLabel lbl = new JLabel();
//           lbl.setOpaque(true);
//           lbl.setText(val instanceof HoaDon
//                   ? MONEY_FMT.format(((HoaDon) val).getTongTien()) + " đ" : "");
//           lbl.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
//           lbl.setForeground(Colors.TEXT_PRIMARY);
//           lbl.setBorder(BorderFactory.createEmptyBorder(0, 15, 0, 0));
//           lbl.setBackground(sel ? Colors.PRIMARY_LIGHT : Colors.BACKGROUND);
//           return lbl;
//       });
//       tblHoaDon.setColumnWidth(4, 150);
//
//       //====="Cột 5 – Thao tác: nút Chi tiết mở dialog xem hóa đơn"=====
//       tblHoaDon.setActionColumn(5, 100);
//       tblHoaDon.setActionColumnListener((row, obj) -> moChiTietHoaDon((HoaDon) obj));
//
//       card.add(tblHoaDon, BorderLayout.CENTER);
//       return card;
//   }
//
//   // ─────────────────────────────────────────────────────────
//   // DATA & LOGIC
//   // ─────────────────────────────────────────────────────────
//   //====="Tải toàn bộ hóa đơn từ DB vào fullList và hiển thị lên bảng"=====
//   private void loadData() {
//       try {
//           ArrayList<HoaDon> ds = hoaDonSV.getDSHoaDon();
//           fullList.clear();
//           if (ds != null) {
//               fullList.addAll(ds);
//           }
//           filteredList.clear();
//           filteredList.addAll(fullList);
//           if (tblHoaDon != null) {
//               tblHoaDon.refresh();
//           }
//           updateCountLabel();
//       } catch (Exception e) {
//           e.printStackTrace();
//       }
//   }
//
//   //====="Lọc danh sách hóa đơn theo từ khóa và khoảng ngày rồi refresh bảng"=====
////    private void search() {
////        String loai = (String) cboTimKiemTheo.getSelectedItem();
////        String kw = txtKeyword.getText().trim().toLowerCase();
//////        ChronoLocalDateTime<?> tuNgay = tuNgayActive ? spinnerToDate(spnTuNgay) : null;
//////        ChronoLocalDateTime<?> denNgay = denNgayActive ? spinnerToDate(spnDenNgay) : null;
////
////        filteredList.clear();
////        for (HoaDon hd : fullList) {
////
////            //====="Kiểm tra từ khóa theo loại tìm kiếm đã chọn"=====
////            if (!kw.isEmpty() && !matchKeyword(hd, loai, kw)) {
////                continue;
////            }
////
////            //====="Kiểm tra hóa đơn nằm trong khoảng ngày lọc"=====
//////            LocalDateTime ngay = hd.getNgayLap();
//////            if (ngay != null) {
//////                if (tuNgay != null && ngay.isBefore(tuNgay)) {
//////                    continue;
//////                }
//////                if (denNgay != null && ngay.isAfter(denNgay)) {
//////                    continue;
//////                }
//////            }
//
////            filteredList.add(hd);
////        }
////        tblHoaDon.refresh();
////        updateCountLabel();
////    }
//
//   //====="So khớp từ khóa với trường tương ứng theo loại tìm kiếm"=====
//   private boolean matchKeyword(HoaDon hd, String loai, String kw) {
//       if (loai == null) {
//           return true;
//       }
//       switch (loai) {
//           case "Mã hóa đơn":
//               return hd.getMaHoaDon().toLowerCase().contains(kw);
//           case "Khách hàng":
//               return safeKHName(hd).toLowerCase().contains(kw)
//                       || safeKHPhone(hd).toLowerCase().contains(kw);
//           case "Nhân viên tạo":
//               return safeNVName(hd).toLowerCase().contains(kw)
//                       || safeNVMa(hd).toLowerCase().contains(kw);
//           default:
//               return true;
//       }
//   }
//
//   //====="Xóa toàn bộ điều kiện lọc và hiển thị lại toàn bộ danh sách"=====
//   private void resetFilter() {
//       txtKeyword.setText("");
//       cboTimKiemTheo.setSelectedIndex(0);
//       tuNgayActive = false;
//       denNgayActive = false;
//       spnTuNgay.setValue(new java.util.Date());
//       spnDenNgay.setValue(new java.util.Date());
//       filteredList.clear();
//       filteredList.addAll(fullList);
//       tblHoaDon.refresh();
//       updateCountLabel();
//   }
//
//   //====="Mở dialog hiển thị chi tiết một hóa đơn được chọn"=====
//   private void moChiTietHoaDon(HoaDon hd) {
//       String ngay = hd.getNgayLap() != null ? hd.getNgayLap().format(DATE_FMT) : "";
//       JOptionPane.showMessageDialog(this,
//               "<html><b>Mã hóa đơn:</b> " + hd.getMaHoaDon() + "<br>"
//               + "<b>Ngày lập:</b> " + ngay + "<br>"
//               + "<b>Khách hàng:</b> " + safeKHName(hd) + " – " + safeKHPhone(hd) + "<br>"
//               + "<b>Nhân viên:</b> " + safeNVName(hd) + " (" + safeNVMa(hd) + ")<br>"
//               + "<b>Tổng tiền:</b> " + MONEY_FMT.format(hd.getTongTien()) + " đ</html>",
//               "Chi tiết hóa đơn – " + hd.getMaHoaDon(),
//               JOptionPane.INFORMATION_MESSAGE);
//   }
//
//   // ─────────────────────────────────────────────────────────
//   // HELPERS
//   // ─────────────────────────────────────────────────────────
//   //====="Tạo JPanel bo góc 16px, nền trắng, viền BORDER_LIGHT"=====
//   private JPanel createCard() {
//       JPanel p = new JPanel() {
//           @Override
//           protected void paintComponent(Graphics g) {
//               Graphics2D g2 = (Graphics2D) g.create();
//               g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//               g2.setColor(getBackground());
//               g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
//               g2.setColor(Colors.BORDER_LIGHT);
//               g2.setStroke(new BasicStroke(1f));
//               g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 16, 16);
//               g2.dispose();
//           }
//       };
//       p.setOpaque(false);
//       p.setBackground(Color.WHITE);
//       return p;
//   }
//
//   //====="Tạo JLabel nhãn form in đậm màu TEXT_PRIMARY"=====
//   private JLabel fieldLabel(String text) {
//       JLabel l = new JLabel(text);
//       l.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
//       l.setForeground(Colors.TEXT_PRIMARY);
//       return l;
//   }
//
//   //====="Tạo JSpinner kiểu ngày định dạng dd/MM/yyyy"=====
//   private JSpinner makeDateSpinner() {
//       JSpinner sp = new JSpinner(new SpinnerDateModel());
//       JSpinner.DateEditor ed = new JSpinner.DateEditor(sp, "dd/MM/yyyy");
//       sp.setEditor(ed);
//       sp.setPreferredSize(new Dimension(160, 42));
//       sp.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
//       return sp;
//   }
//
//   //====="Cập nhật placeholder của ô tìm kiếm theo loại được chọn"=====
//   private void updatePlaceholder() {
//       String s = (String) cboTimKiemTheo.getSelectedItem();
//       if (s == null) {
//           return;
//       }
//       switch (s) {
//           case "Mã hóa đơn":
//               txtKeyword.setPlaceholder("Nhập mã hóa đơn...");
//               break;
//           case "Khách hàng":
//               txtKeyword.setPlaceholder("Nhập tên hoặc SĐT khách hàng...");
//               break;
//           case "Nhân viên tạo":
//               txtKeyword.setPlaceholder("Nhập tên hoặc mã nhân viên...");
//               break;
//       }
//   }
//
//   //====="Cập nhật nhãn hiển thị số lượng hóa đơn tìm được"=====
//   private void updateCountLabel() {
//       lblSoLuong.setText("Tìm thấy " + filteredList.size() + " hóa đơn");
//   }
//
//   //====="Chuyển đổi giá trị JSpinner thành LocalDate"=====
//   private LocalDate spinnerToDate(JSpinner sp) {
//       try {
//           java.util.Date d = (java.util.Date) sp.getValue();
//           return d.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();
//       } catch (Exception e) {
//           return null;
//       }
//   }
//
//   //====="Lấy tên khách hàng từ hóa đơn, trả về chuỗi rỗng nếu không có"=====
//   private String safeKHName(HoaDon hd) {
//       return hd.getKhachHang() != null && hd.getKhachHang().getTenKhachHang() != null
//               ? hd.getKhachHang().getTenKhachHang() : "";
//   }
//
//   //====="Lấy số điện thoại khách hàng từ hóa đơn, trả về chuỗi rỗng nếu không có"=====
//   private String safeKHPhone(HoaDon hd) {
//       return hd.getKhachHang() != null && hd.getKhachHang().getSoDienThoai() != null
//               ? hd.getKhachHang().getSoDienThoai() : "";
//   }
//
//   //====="Lấy tên nhân viên tạo hóa đơn, trả về chuỗi rỗng nếu không có"=====
//   private String safeNVName(HoaDon hd) {
//       return hd.getNhanVien() != null && hd.getNhanVien().getTenNhanVien() != null
//               ? hd.getNhanVien().getTenNhanVien() : "";
//   }
//
//   //====="Lấy mã nhân viên tạo hóa đơn, trả về chuỗi rỗng nếu không có"=====
//   private String safeNVMa(HoaDon hd) {
//       return hd.getNhanVien() != null && hd.getNhanVien().getMaNhanVien() != null
//               ? hd.getNhanVien().getMaNhanVien() : "";
//   }
//
//}
