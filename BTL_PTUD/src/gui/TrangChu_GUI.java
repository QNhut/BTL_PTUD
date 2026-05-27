package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;

import entity.ChiTietHoaDon;
import entity.HoaDon;

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
import service.ChiTietHoaDon_Service;
import service.KhuyenMai_Service;
import service.SanPham_Service;
import util.AsyncLoader;

@SuppressWarnings("serial")
public class TrangChu_GUI extends JPanel {

    private static final NumberFormat VND = constants.Formats.VND;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final String HOME_HERO_BG_PATH = "data/img/home/trangchu-hero-bg.png";
    private static final String HOME_PILL_IMAGE_PATH = "data/img/home/trangchu-pill.png";
    private static final String HOME_PILL_FALLBACK_PATH = "data/img/icons/medical.png";

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
    private final ChiTietHoaDon_Service chiTietHoaDonService = new ChiTietHoaDon_Service();

    private Consumer<String> nav;

    // Containers for dynamic (DB-backed) sections — replaced on each refresh
    private JPanel statRowPlaceholder;
    private JPanel middleRowPlaceholder;
    private JPanel recentPreordersPlaceholder;
    private JPanel wrapper;

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
        private final int soLoai;
        private final String tongTien;
        private final String ngayDat;
        private final String trangThai;
        private final Color badgeBg;
        private final Color badgeFg;

        private RecentPreorder(String maPhieu, String khachHang, int soLoai, String tongTien, String ngayDat,
                String trangThai, Color badgeBg, Color badgeFg) {
            this.maPhieu = maPhieu;
            this.khachHang = khachHang;
            this.soLoai = soLoai;
            this.tongTien = tongTien;
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

        wrapper = new JPanel();
        wrapper.setLayout(new BoxLayout(wrapper, BoxLayout.Y_AXIS));
        wrapper.setBackground(Colors.SECONDARY);
        wrapper.setBorder(BorderFactory.createEmptyBorder(18, 18, 24, 18));

        // Stat row placeholder (fixed height 130)
        statRowPlaceholder = createPlaceholder(130);
        // Middle row placeholder (fixed height 340)
        middleRowPlaceholder = createPlaceholder(340);
        // Recent preorders placeholder (fixed height 330)
        recentPreordersPlaceholder = createPlaceholder(330);

        wrapper.add(buildHeroBanner());
        wrapper.add(Box.createVerticalStrut(18));
        wrapper.add(statRowPlaceholder);
        wrapper.add(Box.createVerticalStrut(18));
        wrapper.add(middleRowPlaceholder);
        wrapper.add(Box.createVerticalStrut(18));
        wrapper.add(recentPreordersPlaceholder);

        JScrollPane scroll = new JScrollPane(wrapper);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(Colors.SECONDARY);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scroll, BorderLayout.CENTER);

        // Load dynamic sections async on first show
        loadDynamicAsync();
    }

    private JPanel createPlaceholder(int height) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        p.setPreferredSize(new Dimension(100, height));
        p.setAlignmentX(LEFT_ALIGNMENT);
        return p;
    }

    public void refresh() {
        loadDynamicAsync();
    }

    private void loadDynamicAsync() {
        AsyncLoader.run(
            () -> {
                JPanel newStat = buildStatRow();
                JPanel newMiddle = buildMiddleRow();
                JPanel newRecent = buildRecentPreordersSection();
                return new Object[]{newStat, newMiddle, newRecent};
            },
            data -> {
                replaceSection(statRowPlaceholder, (JPanel) data[0]);
                statRowPlaceholder = (JPanel) data[0];
                replaceSection(middleRowPlaceholder, (JPanel) data[1]);
                middleRowPlaceholder = (JPanel) data[1];
                replaceSection(recentPreordersPlaceholder, (JPanel) data[2]);
                recentPreordersPlaceholder = (JPanel) data[2];
                wrapper.revalidate();
                wrapper.repaint();
            }
        );
    }

    private void replaceSection(JPanel old, JPanel newPanel) {
        java.awt.Container parent = old.getParent();
        if (parent == null) return;
        int idx = -1;
        for (int i = 0; i < parent.getComponentCount(); i++) {
            if (parent.getComponent(i) == old) { idx = i; break; }
        }
        if (idx >= 0) {
            parent.remove(idx);
            parent.add(newPanel, idx);
        }
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

        BufferedImage pillSource = loadBufferedImage(HOME_PILL_IMAGE_PATH);
        if (pillSource == null) {
            pillSource = loadBufferedImage(HOME_PILL_FALLBACK_PATH);
        }

        ImageIcon pillIcon = null;
        if (pillSource != null) {
            BufferedImage cleaned = makeOuterBackgroundTransparent(pillSource);
            cleaned = removeIsolatedSpeckles(cleaned);
            pillIcon = new ImageIcon(scaleImageHighQuality(cleaned, 92, 92));
        }
        if (pillIcon == null) {
            pillIcon = createPillFallbackIcon(92, 92);
        }
        JLabel lblPill = new JLabel(pillIcon);
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
                    .layThongKeTongHop(homNay.getYear(), homNay.getMonthValue(), homNay.getDayOfMonth(), null, null).doanhThuKy;
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

        int sapHet = 0;
        try {
            List<SanPham> dsSP = sanPhamService.layDanhSachSanPham();
            java.util.Map<String, SanPham_Service.TonKhoInfo> mapTon = sanPhamService.tinhTonKhoTatCa(dsSP);
            SanPham_Service.ThongKe thongKe = sanPhamService.tinhThongKe(dsSP, mapTon);
            sapHet = thongKe.sapHet;
        } catch (Exception ignored) {
        }

        List<HomeStat> stats = new ArrayList<HomeStat>();
        stats.add(new HomeStat("data/img/icons/up.png", new Color(232, 250, 245), Colors.PRIMARY, "+8.2%", SOFT_GREEN, Colors.SUCCESS,
                VND.format((long) doanhThuHomNay) + "đ", "Doanh thu hôm nay"));
        stats.add(new HomeStat("data/img/icons/invoice.png", new Color(255, 241, 234), Colors.ACCENT, "+12", SOFT_GREEN, Colors.SUCCESS,
                String.valueOf(donHomNay), "Số đơn hôm nay"));
        stats.add(new HomeStat("data/img/icons/open-box.png", new Color(255, 244, 199), new Color(202, 138, 4), sapHet + " sắp hết", SOFT_YELLOW,
                new Color(146, 64, 14), String.valueOf(tongSanPham), "Sản phẩm"));
        stats.add(new HomeStat("data/img/icons/people.png", new Color(229, 239, 255), new Color(37, 99, 235), "+3 hôm nay", SOFT_GREEN,
                Colors.SUCCESS, VND.format((long) tongKhachHang), "Khách hàng"));
        return stats;
    }

    private JPanel buildStatCard(HomeStat stat) {
        JPanel card = createCard(260, 150);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
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
        card.add(Box.createVerticalStrut(12));
        card.add(value);
        card.add(Box.createVerticalStrut(3));
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
            java.util.Map<String, SanPham_Service.TonKhoInfo> mapTon = sanPhamService.tinhTonKhoTatCa(ds);
            int n = Math.min(4, ds.size());
            for (int i = 0; i < n; i++) {
                SanPham sp = ds.get(i);
                SanPham_Service.TonKhoInfo info = mapTon.get(sp.getMaSanPham());
                int ton = (info != null) ? info.tonKho : 0;
                boolean lowStock = info != null && !"CON_HANG".equals(info.trangThai);
                card.add(buildFeaturedProductRow(sp, ton, lowStock));
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

        JLabel icon = createRoundIcon(lowStock ? "!" : "*", new Color(234, 245, 241),
                lowStock ? Colors.ACCENT : new Color(59, 130, 246), 42);
        icon.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));

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

        JLabel lblStock = new JLabel((lowStock ? "Cảnh báo tồn: " : "Tồn: ") + tonKho);
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

        card.add(sectionHeader("Đặt trước thuốc gần đây", "Xem tất cả", () -> nav.accept("TraCuuDatThuoc")));
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
        row.add(tableHeaderLabel("Số loại thuốc"));
        row.add(tableHeaderLabel("Tổng tiền"));
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

        JLabel soLoai = new JLabel(rowData.soLoai + " loại");
        soLoai.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
        soLoai.setForeground(Colors.TEXT_PRIMARY);

        JLabel tongTien = new JLabel(rowData.tongTien);
        tongTien.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
        tongTien.setForeground(Colors.PRIMARY);

        JLabel ngay = bodyLabel(rowData.ngayDat);
        JLabel status = createStatusBadge(rowData.trangThai, rowData.badgeBg, rowData.badgeFg);

        row.add(ma);
        row.add(kh);
        row.add(soLoai);
        row.add(tongTien);
        row.add(ngay);
        row.add(wrapLeft(status));
        return row;
    }

    private JLabel createStatusBadge(String text, Color bg, Color fg) {
        JLabel badge = createBadge(text, bg, fg);
        Dimension fixed = new Dimension(170, 36);
        badge.setPreferredSize(fixed);
        badge.setMinimumSize(fixed);
        badge.setMaximumSize(fixed);
        badge.setHorizontalAlignment(SwingConstants.CENTER);
        badge.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        badge.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                nav.accept("TraCuuDatThuoc");
            }
        });
        return badge;
    }

    private List<RecentPreorder> buildSampleRecentPreorders() {
        List<RecentPreorder> rows = new ArrayList<RecentPreorder>();
        try {
            List<HoaDon> dsDatTruoc = hoaDonService.layDSChoThanhToan();
            int limit = Math.min(5, dsDatTruoc.size());
            for (int i = 0; i < limit; i++) {
                HoaDon hd = dsDatTruoc.get(i);
                String maPhieu = hd.getMaHoaDon() != null ? hd.getMaHoaDon() : "---";
                String tenKH = "Khách lẻ";
                if (hd.getKhachHang() != null && hd.getKhachHang().getTenKhachHang() != null
                        && !hd.getKhachHang().getTenKhachHang().isBlank()) {
                    tenKH = hd.getKhachHang().getTenKhachHang();
                }
                int soLoai = 0;
                try {
                    List<ChiTietHoaDon> dsCT = chiTietHoaDonService.getChiTietTheoHoaDon(hd.getMaHoaDon());
                    soLoai = dsCT.size();
                } catch (Exception ignored) {
                }
                String tongTienStr = VND.format((long) hd.getTongTien()) + "đ";
                String ngayDat = hd.getNgayLap() != null
                        ? DATE_FMT.format(hd.getNgayLap().toLocalDate()) : "---";
                rows.add(new RecentPreorder(maPhieu, tenKH, soLoai, tongTienStr,
                        ngayDat, "Chờ thanh toán", SOFT_YELLOW, new Color(161, 98, 7)));
            }
        } catch (Exception ignored) {
        }
        if (rows.isEmpty()) {
            rows.add(new RecentPreorder("---", "---", 0, "---", "---", "Không có dữ liệu", SOFT_BLUE, new Color(37, 99, 235)));
        }
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
            JLabel actionLabel = new JLabel(actionText);
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
        // Thử load file ảnh nếu text có dạng đường dẫn
        ImageIcon loadedIcon = null;
        if (text != null && (text.contains("/") || text.contains("\\"))) {
            try {
                File imgFile = new File(text);
                if (imgFile.exists()) {
                    BufferedImage raw = ImageIO.read(imgFile);
                    if (raw != null) {
                        int iconSize = (int) (size * 0.58);
                        // Vẽ lại với màu fg thông qua tint
                        BufferedImage tinted = new BufferedImage(raw.getWidth(), raw.getHeight(), BufferedImage.TYPE_INT_ARGB);
                        Graphics2D tg = tinted.createGraphics();
                        tg.drawImage(raw, 0, 0, null);
                        tg.setComposite(java.awt.AlphaComposite.SrcIn);
                        tg.setColor(fg);
                        tg.fillRect(0, 0, tinted.getWidth(), tinted.getHeight());
                        tg.dispose();
                        Image scaled = tinted.getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
                        loadedIcon = new ImageIcon(scaled);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        final ImageIcon finalIcon = loadedIcon;
        final String displayText = (loadedIcon == null) ? text : null;

        JLabel label = new JLabel(displayText, finalIcon, SwingConstants.CENTER) {
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
        if (displayText != null) {
            int iconFontSize = displayText.length() >= 2 ? Math.max(12, size / 3) : Math.max(12, size / 2);
            label.setFont(new java.awt.Font("SansSerif", java.awt.Font.BOLD, iconFontSize));
        }
        label.setPreferredSize(new Dimension(size, size));
        label.setMinimumSize(new Dimension(size, size));
        label.setMaximumSize(new Dimension(size, size));
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
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

    private BufferedImage loadBufferedImage(String path) {
        if (path == null || path.trim().isEmpty()) {
            return null;
        }
        File file = new File(path);
        if (!file.exists()) {
            return null;
        }
        try {
            return ImageIO.read(file);
        } catch (Exception e) {
            return null;
        }
    }

    private BufferedImage scaleImageHighQuality(BufferedImage src, int width, int height) {
        BufferedImage out = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(src, 0, 0, width, height, null);
        g2.dispose();
        return out;
    }

    // Xoa nen trung tinh o ria icon (vd checkerboard) de khop mau nen banner.
    private BufferedImage makeOuterBackgroundTransparent(BufferedImage src) {
        if (src == null) {
            return null;
        }

        BufferedImage img = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();

        int w = img.getWidth();
        int h = img.getHeight();
        boolean[] visited = new boolean[w * h];
        Deque<Integer> queue = new ArrayDeque<>();

        for (int x = 0; x < w; x++) {
            tryEnqueueBackground(img, x, 0, w, h, visited, queue);
            tryEnqueueBackground(img, x, h - 1, w, h, visited, queue);
        }
        for (int y = 0; y < h; y++) {
            tryEnqueueBackground(img, 0, y, w, h, visited, queue);
            tryEnqueueBackground(img, w - 1, y, w, h, visited, queue);
        }

        while (!queue.isEmpty()) {
            int idx = queue.removeFirst();
            int x = idx % w;
            int y = idx / w;
            img.setRGB(x, y, 0x00000000);

            tryEnqueueBackground(img, x + 1, y, w, h, visited, queue);
            tryEnqueueBackground(img, x - 1, y, w, h, visited, queue);
            tryEnqueueBackground(img, x, y + 1, w, h, visited, queue);
            tryEnqueueBackground(img, x, y - 1, w, h, visited, queue);
        }

        return img;
    }

    // Xoa cac diem trang/xam bi sot lai sau khi tach nen.
    private BufferedImage removeIsolatedSpeckles(BufferedImage src) {
        if (src == null) {
            return null;
        }

        int w = src.getWidth();
        int h = src.getHeight();
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = out.createGraphics();
        g2.drawImage(src, 0, 0, null);
        g2.dispose();

        for (int y = 1; y < h - 1; y++) {
            for (int x = 1; x < w - 1; x++) {
                int argb = out.getRGB(x, y);
                if (!isLikelyCheckerBackground(argb)) {
                    continue;
                }

                int transparentNeighbors = 0;
                for (int ny = y - 1; ny <= y + 1; ny++) {
                    for (int nx = x - 1; nx <= x + 1; nx++) {
                        if (nx == x && ny == y) {
                            continue;
                        }
                        int a = (out.getRGB(nx, ny) >>> 24) & 0xFF;
                        if (a < 16) {
                            transparentNeighbors++;
                        }
                    }
                }

                if (transparentNeighbors >= 5) {
                    out.setRGB(x, y, 0x00000000);
                }
            }
        }

        return out;
    }

    private void tryEnqueueBackground(BufferedImage img, int x, int y, int w, int h,
            boolean[] visited, Deque<Integer> queue) {
        if (x < 0 || x >= w || y < 0 || y >= h) {
            return;
        }
        int idx = y * w + x;
        if (visited[idx]) {
            return;
        }
        int argb = img.getRGB(x, y);
        if (!isLikelyCheckerBackground(argb)) {
            return;
        }
        visited[idx] = true;
        queue.addLast(idx);
    }

    private boolean isLikelyCheckerBackground(int argb) {
        int a = (argb >>> 24) & 0xFF;
        if (a < 16) {
            return true;
        }

        int r = (argb >>> 16) & 0xFF;
        int g = (argb >>> 8) & 0xFF;
        int b = argb & 0xFF;

        int max = Math.max(r, Math.max(g, b));
        int min = Math.min(r, Math.min(g, b));
        double saturation = max == 0 ? 0 : (double) (max - min) / max;
        double brightness = max / 255.0;

        return brightness >= 0.62 && saturation <= 0.13;
    }

    private ImageIcon createPillFallbackIcon(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int x = 2;
        int y = height / 2 - 16;
        int w = width - 4;
        int h = 32;

        g2.setColor(new Color(245, 251, 248));
        g2.fillRoundRect(x, y, w, h, h, h);

        g2.setColor(new Color(16, 122, 103));
        g2.fillRoundRect(x + w / 2, y, w / 2, h, h, h);

        g2.setColor(new Color(255, 255, 255, 210));
        g2.fillRoundRect(x + 4, y + 4, w - 8, 6, 6, 6);

        g2.setColor(new Color(210, 225, 219));
        g2.drawLine(x + w / 2, y + 2, x + w / 2, y + h - 2);

        g2.dispose();
        return new ImageIcon(image);
    }
}
