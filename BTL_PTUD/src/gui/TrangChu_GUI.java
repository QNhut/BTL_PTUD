package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Insets;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import constants.Colors;
import constants.FontStyle;
import entity.KhuyenMai;
import entity.SanPham;
import exception.RoundedButton;
import exception.RoundedPanel;
import service.HoaDon_Service;
import service.KhachHang_Service;
import service.KhuyenMai_Service;
import service.SanPham_Service;

public class TrangChu_GUI extends JPanel {

    private static final NumberFormat VND = constants.Formats.VND;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String HOME_HERO_BG_PATH = "data/img/home/trangchu-hero-bg.png";
    private static final String HOME_PILL_IMAGE_PATH = "data/img/home/trangchu-pill.png";

    private static final Color HERO_BG = new Color(2, 31, 31);
    private static final Color HERO_GLOW = new Color(5, 74, 64, 110);
    private static final Color SOFT_GREEN = new Color(220, 252, 231);
    private static final Color SOFT_YELLOW = new Color(254, 249, 195);
    private static final Color SOFT_BLUE = new Color(219, 234, 254);
    private static final Color SOFT_RED = new Color(254, 226, 226);

    private final HoaDon_Service hoaDonService = new HoaDon_Service();
    private final SanPham_Service sanPhamService = new SanPham_Service();
    private final KhachHang_Service khachHangService = new KhachHang_Service();
    private final KhuyenMai_Service khuyenMaiService = new KhuyenMai_Service();

    private Consumer<String> nav;

    private static final class HomeStat {

        private final String icon;
        private final Color iconBg;
        private final Color iconFg;
        private final String badge;
        private final Color badgeBg;
        private final Color badgeFg;
        private final String value;
        private final String label;

        private HomeStat(String icon, Color iconBg, Color iconFg, String badge, Color badgeBg, Color badgeFg,
                String value, String label) {
            this.icon = icon;
            this.iconBg = iconBg;
            this.iconFg = iconFg;
            this.badge = badge;
            this.badgeBg = badgeBg;
            this.badgeFg = badgeFg;
            this.value = value;
            this.label = label;
        }
    }

    private static final class RecentPreorder {

        private final String maPhieu;
        private final String khachHang;
        private final String thuocDat;
        private final int soLuong;
        private final String ngayDat;
        private final String trangThai;
        private final Color badgeBg;
        private final Color badgeFg;

        private RecentPreorder(String maPhieu, String khachHang, String thuocDat, int soLuong, String ngayDat,
                String trangThai, Color badgeBg, Color badgeFg) {
            this.maPhieu = maPhieu;
            this.khachHang = khachHang;
            this.thuocDat = thuocDat;
            this.soLuong = soLuong;
            this.ngayDat = ngayDat;
            this.trangThai = trangThai;
            this.badgeBg = badgeBg;
            this.badgeFg = badgeFg;
        }
    }

    public TrangChu_GUI() {
        this(page -> {
        });
    }

    public TrangChu_GUI(Consumer<String> nav) {
        this.nav = nav;
        setLayout(new BorderLayout());
        setBackground(Colors.SECONDARY);

        JPanel wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Colors.SECONDARY);
        wrapper.setBorder(BorderFactory.createEmptyBorder(18, 18, 24, 18));

        wrapper.add(buildHeroBanner());
        wrapper.add(Box.createVerticalStrut(18));
        wrapper.add(buildStatRow());
        wrapper.add(Box.createVerticalStrut(18));
        wrapper.add(buildMiddleRow());
        wrapper.add(Box.createVerticalStrut(18));
        wrapper.add(buildRecentPreordersSection());

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Colors.SECONDARY);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);
    }

    private JPanel buildHeroBanner() {
        RoundedPanel banner = new RoundedPanel(800, 235, 24) {
            private final Image background = loadImage(HOME_HERO_BG_PATH);

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setClip(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 24, 24));
                g2.setColor(HERO_BG);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 24, 24);
                if (background != null) {
                    g2.drawImage(background, 0, 0, getWidth(), getHeight(), this);
                }
                g2.setColor(HERO_GLOW);
                g2.fillOval(getWidth() - 260, 30, 180, 180);
                g2.fillOval(getWidth() - 360, -20, 160, 160);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        banner.setBackground(Colors.BACKGROUND_HOME);
        banner.setLayout(new BorderLayout(24, 0));
        banner.setOpaque(false);
        banner.setMaximumSize(new Dimension(Integer.MAX_VALUE, 235));
        banner.setAlignmentX(LEFT_ALIGNMENT);
        banner.setBorder(BorderFactory.createEmptyBorder(30, 34, 30, 34));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

        JLabel lblEyebrow = new JLabel("HAPPY HEALTH PHARMACY");
        lblEyebrow.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        lblEyebrow.setForeground(Colors.PRIMARY);
        lblEyebrow.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblTitle = new JLabel("Sức khoẻ là ưu tiên hàng đầu");
        lblTitle.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setAlignmentX(LEFT_ALIGNMENT);

        JLabel lblDesc = new JLabel(
                "<html><div style='width:520px'>Hệ thống quản lý bán hàng nhà thuốc thông minh - quản lý kho, hóa đơn, và đặt trước thuốc dễ dàng.</div></html>");
        lblDesc.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        lblDesc.setForeground(Colors.opacity(Color.WHITE, 0.68f));
        lblDesc.setAlignmentX(LEFT_ALIGNMENT);

        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        buttonRow.setOpaque(false);
        buttonRow.setAlignmentX(LEFT_ALIGNMENT);

        RoundedButton btnHoaDon = new RoundedButton(132, 40, 14, "Tạo hoá đơn", Colors.PRIMARY);
        btnHoaDon.setForeground(Color.WHITE);
        btnHoaDon.addActionListener(e -> nav.accept("BanHang"));

        RoundedButton btnDatTruoc = new RoundedButton(156, 40, 14, "Đặt trước thuốc", Colors.ACCENT);
        btnDatTruoc.setForeground(Color.WHITE);
        btnDatTruoc.addActionListener(e -> nav.accept("DatTruoc"));

        buttonRow.add(btnHoaDon);
        buttonRow.add(btnDatTruoc);

        left.add(lblEyebrow);
        left.add(Box.createVerticalStrut(8));
        left.add(lblTitle);
        left.add(Box.createVerticalStrut(12));
        left.add(lblDesc);
        left.add(Box.createVerticalStrut(18));
        left.add(buttonRow);

        JPanel right = new JPanel(new GridBagLayout());
        right.setOpaque(false);
        right.setPreferredSize(new Dimension(280, 0));

        RoundedPanel iconCircle = new RoundedPanel(170, 170, 85);
        iconCircle.setOpaque(false);
        iconCircle.setBackground(new Color(3, 66, 58));
        iconCircle.setLayout(new GridBagLayout());

        ImageIcon pillIcon = loadScaledIcon(HOME_PILL_IMAGE_PATH, 92, 92);
        JLabel lblPill = pillIcon != null ? new JLabel(pillIcon) : new JLabel("💊");
        lblPill.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
        lblPill.setForeground(Color.WHITE);
        iconCircle.add(lblPill);

        right.add(iconCircle);

        banner.add(left, BorderLayout.WEST);
        banner.add(right, BorderLayout.EAST);
        return banner;
    }

    private JPanel buildStatRow() {
        JPanel row = new JPanel(new GridLayout(1, 4, 10, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 130));

        for (HomeStat stat : loadStats()) {
            row.add(buildStatCard(stat));
        }
        return row;
    }

    private List<HomeStat> loadStats() {
        double doanhThuHomNay = 0;
        int donHomNay = 0;
        int tongSanPham = 0;
        int tongKhachHang = 0;
        try {
            LocalDate homNay = LocalDate.now();
            doanhThuHomNay = hoaDonService
                    .layThongKeTongHop(homNay.getYear(), homNay.getMonthValue(), homNay.getDayOfMonth(), null, null).tongDoanhThu;
            donHomNay = hoaDonService
                    .layThongKeTongHop(homNay.getYear(), homNay.getMonthValue(), homNay.getDayOfMonth(), null, null).soGiaoDich;
        } catch (Exception ignored) {
        }
        try {
            tongSanPham = sanPhamService.layDanhSachSanPham().size();
        } catch (Exception ignored) {
        }
        try {
            tongKhachHang = khachHangService.getSoLuongKhachHang();
        } catch (Exception ignored) {
        }

        List<HomeStat> stats = new ArrayList<HomeStat>();
        stats.add(new HomeStat("↗", new Color(232, 250, 245), Colors.PRIMARY, "+8.2%", SOFT_GREEN, Colors.SUCCESS,
                VND.format((long) doanhThuHomNay) + "đ", "Doanh thu hôm nay"));
        stats.add(new HomeStat("▢", new Color(255, 241, 234), Colors.ACCENT, "+12", SOFT_GREEN, Colors.SUCCESS,
                String.valueOf(donHomNay), "Số đơn hôm nay"));
        stats.add(new HomeStat("◫", new Color(255, 244, 199), new Color(202, 138, 4), "12 sắp hết", SOFT_YELLOW,
                new Color(146, 64, 14), String.valueOf(tongSanPham), "Sản phẩm"));
        stats.add(new HomeStat("◌", new Color(229, 239, 255), new Color(37, 99, 235), "+3 hôm nay", SOFT_GREEN,
                Colors.SUCCESS, VND.format((long) tongKhachHang), "Khách hàng"));
        return stats;
    }

    private JPanel buildStatCard(HomeStat stat) {
        JPanel card = createCard(260, 130);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(18, 20, 18, 20));
        card.setAlignmentX(LEFT_ALIGNMENT);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setAlignmentX(LEFT_ALIGNMENT);

        JLabel icon = createRoundIcon(stat.icon, stat.iconBg, stat.iconFg, 40);
        JLabel badge = createBadge(stat.badge, stat.badgeBg, stat.badgeFg);

        top.add(icon, BorderLayout.WEST);
        top.add(badge, BorderLayout.EAST);

        JLabel value = new JLabel(stat.value);
        value.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        value.setForeground(Colors.TEXT_PRIMARY);
        value.setAlignmentX(LEFT_ALIGNMENT);
        value.setHorizontalAlignment(SwingConstants.LEFT);
        value.setMaximumSize(new Dimension(Integer.MAX_VALUE, value.getPreferredSize().height));

        JLabel label = new JLabel(stat.label);
        label.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        label.setForeground(Colors.TEXT_SECONDARY);
        label.setAlignmentX(LEFT_ALIGNMENT);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setMaximumSize(new Dimension(Integer.MAX_VALUE, label.getPreferredSize().height));

        card.add(top);
        card.add(Box.createVerticalStrut(20));
        card.add(value);
        card.add(Box.createVerticalStrut(6));
        card.add(label);
        return card;
    }

    private JPanel buildMiddleRow() {
        JPanel row = new JPanel(new GridLayout(1, 2, 16, 0));
        row.setOpaque(false);
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
        row.add(buildFeaturedProductsCard());
        row.add(buildPromotionCard());
        return row;
    }

    private JPanel buildFeaturedProductsCard() {
        JPanel card = createCard(400, 340);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 22, 18, 22));

        card.add(sectionHeader("Sản phẩm nổi bật", "Xem tất cả", () -> nav.accept("SanPham")));
        card.add(Box.createVerticalStrut(18));

        try {
            List<SanPham> ds = sanPhamService.layDanhSachSanPham();
            int[] tonMinhHoa = {240, 88, 320, 15};
            int n = Math.min(4, ds.size());
            for (int i = 0; i < n; i++) {
                card.add(buildFeaturedProductRow(ds.get(i), tonMinhHoa[i % tonMinhHoa.length], i == 3));
                if (i < n - 1) {
                    card.add(Box.createVerticalStrut(12));
                }
            }
            if (n == 0) {
                card.add(createEmptyLabel("Chưa có sản phẩm để hiển thị"));
            }
        } catch (Exception ex) {
            card.add(createEmptyLabel("Không tải được danh sách sản phẩm"));
        }

        return card;
    }

    private JPanel buildFeaturedProductRow(SanPham sp, int tonKho, boolean lowStock) {
        JPanel row = new JPanel(new BorderLayout(14, 0));
        row.setOpaque(false);

        JPanel left = new JPanel(new BorderLayout(12, 0));
        left.setOpaque(false);

        JLabel icon = createRoundIcon(lowStock ? "💊" : "●", new Color(234, 245, 241),
                lowStock ? Colors.ACCENT : new Color(59, 130, 246), 42);
        icon.setFont(lowStock ? new Font("Segoe UI Emoji", Font.PLAIN, 22)
                : FontStyle.font(FontStyle.BASE, FontStyle.BOLD));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel lblName = new JLabel(sp.getTenSanPham());
        lblName.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblName.setForeground(Colors.TEXT_PRIMARY);

        String tenLoai = sp.getLoaiSanPham() != null && sp.getLoaiSanPham().getTenLoaiSanPham() != null
                ? sp.getLoaiSanPham().getTenLoaiSanPham()
                : "Sản phẩm";
        JLabel lblType = new JLabel(tenLoai);
        lblType.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblType.setForeground(Colors.TEXT_SECONDARY);

        text.add(lblName);
        text.add(Box.createVerticalStrut(4));
        text.add(lblType);

        left.add(icon, BorderLayout.WEST);
        left.add(text, BorderLayout.CENTER);

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));

        JLabel lblPrice = new JLabel(VND.format((long) sp.getGiaThanh()) + "đ");
        lblPrice.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        lblPrice.setForeground(Colors.PRIMARY);
        lblPrice.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel lblStock = new JLabel((lowStock ? "⚠ Tồn: " : "Tồn: ") + tonKho);
        lblStock.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        lblStock.setForeground(lowStock ? Colors.DANGER : Colors.TEXT_SECONDARY);
        lblStock.setAlignmentX(Component.RIGHT_ALIGNMENT);

        right.add(lblPrice);
        right.add(Box.createVerticalStrut(4));
        right.add(lblStock);

        row.add(left, BorderLayout.CENTER);
        row.add(right, BorderLayout.EAST);
        return row;
    }

    private JPanel buildPromotionCard() {
        JPanel card = createCard(320, 340);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(22, 22, 18, 22));

        card.add(sectionHeader("Khuyến mãi hiện hành", "Xem tất cả", () -> nav.accept("KhuyenMai")));
        card.add(Box.createVerticalStrut(14));

        boolean hasPromotion = false;
        try {
            List<KhuyenMai> ds = khuyenMaiService.getDSKhuyenMai();
            int count = 0;
            for (KhuyenMai km : ds) {
                if (!khuyenMaiService.isDangHoatDong(km)) {
                    continue;
                }
                if (hasPromotion) {
                    card.add(Box.createVerticalStrut(14));
                }
                card.add(buildPromotionItem(km.getTenKhuyenMai(),
                        km.getNgayKetThuc() != null ? "HSD: " + km.getNgayKetThuc() : "Đang áp dụng", "Đang chạy",
                        SOFT_GREEN, Colors.SUCCESS));
                hasPromotion = true;
                count++;
                if (count >= 3) {
                    break;
                }
            }
        } catch (Exception ignored) {
        }

        if (!hasPromotion) {
            card.add(buildPromotionItem("Mua 2 tặng 1 Vitamin C", "HSD: 31/05/2026", "Đang chạy", SOFT_GREEN,
                    Colors.SUCCESS));
            card.add(Box.createVerticalStrut(14));
            card.add(buildPromotionItem("Giảm 15% thuốc kháng sinh", "HSD: 15/06/2026", "Đang chạy", SOFT_GREEN,
                    Colors.SUCCESS));
            card.add(Box.createVerticalStrut(14));
            card.add(buildPromotionItem("Combo sức khoẻ mùa hè", "HSD: 30/06/2026", "Sắp tới", SOFT_BLUE,
                    new Color(37, 99, 235)));
        }
        return card;
    }

    private JPanel buildPromotionItem(String title, String subtitle, String status, Color badgeBg, Color badgeFg) {
        JPanel item = new RoundedPanel(250, 75, 16);
        item.setLayout(new BorderLayout(12, 0));
        item.setOpaque(false);
        item.setBackground(new Color(246, 251, 249));
//        item.setAlignmentX(LEFT_ALIGNMENT);
        item.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        item.setMinimumSize(new Dimension(0, 70));
        item.setPreferredSize(new Dimension(250, 75));
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 75));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));

        JLabel lblTitle = new JLabel("<html><div style='width:170px'>" + title + "</div></html>");
        lblTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        lblTitle.setForeground(Colors.TEXT_PRIMARY);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
        lblSub.setForeground(Colors.TEXT_SECONDARY);

        text.add(lblTitle);
        text.add(Box.createVerticalStrut(8));
        text.add(lblSub);

        JLabel badge = createBadge(status, badgeBg, badgeFg);
        item.add(text, BorderLayout.CENTER);
        item.add(badge, BorderLayout.EAST);
        return item;
    }

    private JPanel buildRecentPreordersSection() {
        JPanel card = createCard(1000, 330);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setAlignmentX(LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 330));
        card.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT),
                BorderFactory.createEmptyBorder(18, 22, 14, 22)));

        card.add(sectionHeader("🗓 Đặt trước thuốc gần đây", "Xem tất cả", () -> nav.accept("TraCuuDatThuoc")));
        card.add(Box.createVerticalStrut(18));
        card.add(buildPreorderTableHeader());
        card.add(Box.createVerticalStrut(4));

        List<RecentPreorder> rows = buildSampleRecentPreorders();
        for (int i = 0; i < rows.size(); i++) {
            card.add(buildPreorderRow(rows.get(i)));
            if (i < rows.size() - 1) {
                card.add(createDivider());
            }
        }
        return card;
    }

    private JPanel buildPreorderTableHeader() {
        JPanel row = new JPanel(new GridLayout(1, 6, 0, 0));
        row.setOpaque(false);
        row.add(tableHeaderLabel("Mã phiếu"));
        row.add(tableHeaderLabel("Khách hàng"));
        row.add(tableHeaderLabel("Thuốc đặt"));
        row.add(tableHeaderLabel("SL"));
        row.add(tableHeaderLabel("Ngày đặt"));
        row.add(tableHeaderLabel("Trạng thái"));
        return row;
    }

    private JPanel buildPreorderRow(RecentPreorder rowData) {
        JPanel row = new JPanel(new GridLayout(1, 6, 0, 0));
        row.setOpaque(false);
        row.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        JLabel ma = new JLabel(rowData.maPhieu);
        ma.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        ma.setForeground(Colors.PRIMARY);

        JLabel kh = bodyLabel(rowData.khachHang);
        JLabel thuoc = bodyLabel(rowData.thuocDat);

        JLabel sl = new JLabel(String.valueOf(rowData.soLuong));
        sl.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        sl.setForeground(Colors.TEXT_PRIMARY);

        JLabel ngay = bodyLabel(rowData.ngayDat);
        JLabel status = createBadge(rowData.trangThai, rowData.badgeBg, rowData.badgeFg);

        row.add(ma);
        row.add(kh);
        row.add(thuoc);
        row.add(sl);
        row.add(ngay);
        row.add(wrapLeft(status));
        return row;
    }

    private List<RecentPreorder> buildSampleRecentPreorders() {
        List<RecentPreorder> rows = new ArrayList<RecentPreorder>();
        rows.add(new RecentPreorder("DT-0045", "Trần Thị B", "Omeprazole 20mg", 2,
                DATE_FMT.format(LocalDate.now()), "◔ Chờ xác nhận", SOFT_YELLOW, new Color(161, 98, 7)));
        rows.add(new RecentPreorder("DT-0044", "Lê Văn C", "Vitamin C 1000mg", 5,
                DATE_FMT.format(LocalDate.now()), "◉ Đã xác nhận", SOFT_BLUE, new Color(37, 99, 235)));
        rows.add(new RecentPreorder("DT-0043", "Phạm Thị D", "Amoxicillin 500mg", 1,
                DATE_FMT.format(LocalDate.now().minusDays(1)), "◉ Hoàn thành", SOFT_GREEN, Colors.SUCCESS));
        rows.add(new RecentPreorder("DT-0042", "Nguyễn Văn E", "Paracetamol 500mg", 3,
                DATE_FMT.format(LocalDate.now().minusDays(1)), "◉ Đã huỷ", SOFT_RED, Colors.DANGER));
        return rows;
    }

    private JPanel sectionHeader(String title, String actionText, Runnable action) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
        lblTitle.setForeground(Colors.TEXT_PRIMARY);

        row.add(lblTitle, BorderLayout.WEST);

        if (actionText != null && !actionText.isEmpty()) {
            JLabel actionLabel = new JLabel(actionText + "  →");
            actionLabel.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
            actionLabel.setForeground(Colors.PRIMARY);
            if (action != null) {
                actionLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
                actionLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                    @Override
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        action.run();
                    }
                });
            }
            row.add(actionLabel, BorderLayout.EAST);
        }

        return row;
    }

    private JLabel tableHeaderLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        label.setForeground(Colors.TEXT_SECONDARY);
        return label;
    }

    private JLabel bodyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontStyle.font(FontStyle.LG, FontStyle.NORMAL));
        label.setForeground(Colors.TEXT_PRIMARY);
        return label;
    }

    private JLabel createRoundIcon(String text, Color bg, Color fg, int size) {
        JLabel label = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        label.setOpaque(false);
        label.setBackground(bg);
        label.setForeground(fg);
        label.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
        label.setPreferredSize(new Dimension(size, size));
        label.setMinimumSize(new Dimension(size, size));
        label.setMaximumSize(new Dimension(size, size));
        label.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return label;
    }

    private JLabel createBadge(String text, Color bg, Color fg) {
        JLabel badge = new JLabel(text, SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        badge.setOpaque(false);
        badge.setBackground(bg);
        badge.setForeground(fg);
        badge.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
        badge.setBorder(BorderFactory.createEmptyBorder(6, 15, 6, 15));
        return badge;
    }

    private JComponent wrapLeft(JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setOpaque(false);
        panel.add(component);
        return panel;
    }

    private JLabel createEmptyLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        label.setForeground(Colors.TEXT_SECONDARY);
        label.setAlignmentX(LEFT_ALIGNMENT);
        return label;
    }

    private JPanel createDivider() {
        JPanel divider = new JPanel();
        divider.setBackground(Colors.BORDER_LIGHT);
        divider.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        divider.setPreferredSize(new Dimension(1, 1));
        divider.setMinimumSize(new Dimension(1, 1));
        return divider;
    }

    private JPanel createCard(int width, int height) {
        RoundedPanel card = new RoundedPanel(width, height, 18);
        card.setBackground(Colors.BACKGROUND);
        card.setAlignmentX(LEFT_ALIGNMENT);
        Dimension size = new Dimension(width, height);
        card.setPreferredSize(size);
        card.setMinimumSize(size);
        card.setMaximumSize(size);
        return card;
    }

    private Image loadImage(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        return new ImageIcon(path).getImage();
    }

    private ImageIcon loadScaledIcon(String path, int width, int height) {
        Image image = loadImage(path);
        if (image == null) {
            return null;
        }
        return new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }
}
