package exception;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import gui.Main_GUI;

public class MenuBarPanel extends JPanel {

    private ArrayList<MenuButton> menuButtons = new ArrayList<>();
    private Main_GUI mainFrame;

    public MenuBarPanel(Main_GUI mainFrame) {
        this.mainFrame = mainFrame;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBackground(Color.WHITE);
        setPreferredSize(new Dimension(300, 0));
        setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        MenuButton danhMuc = createMenu("DANH MỤC");
        danhMuc.addSubMenu("Nhân viên", "data/img/icons/recruitment.png", "NhanVien");
        danhMuc.addSubMenu("Khách Hàng", "data/img/icons/people.png", "KhachHang");
        danhMuc.addSubMenu("Sản Phẩm", "data/img/icons/box.png", "SanPham");
        danhMuc.addSubMenu("Nhà cung cấp", "data/img/icons/vehicle.png", "NhaCungCap");
        danhMuc.addSubMenu("Khuyến mãi", "data/img/icons/tag.png", "KhuyenMai");

        MenuButton xuLy = createMenu("XỬ LÝ");
        xuLy.addSubMenu("Tạo hoá đơn", "data/img/icons/shopping-cart.png", "BanHang");
        xuLy.addSubMenu("Tạo phiếu nhập", "data/img/icons/open-box.png", "NhapHang");
        xuLy.addSubMenu("Đổi hàng", "data/img/icons/commercial.png", "DoiHang");
        xuLy.addSubMenu("Trả hàng", "data/img/icons/exchange.png", "TraHang");

        MenuButton traCuu = createMenu("TRA CỨU");
        traCuu.addSubMenu("Hoá đơn", "data/img/icons/invoice.png", "TraCuuHoaDon");
        traCuu.addSubMenu("Phiếu nhập", "data/img/icons/invoice_1.png", "TraCuuPhieuNhap");
        traCuu.addSubMenu("Đổi hàng", "data/img/icons/commercial.png", "TraCuuDoiHang");
        traCuu.addSubMenu("Trả hàng", "data/img/icons/exchange.png", "TraCuuTraHang");

        MenuButton thongKe = createMenu("THỐNG KÊ");
        thongKe.addSubMenu("Doanh thu", "data/img/icons/up.png", "ThongKeDoanhThu");
        thongKe.addSubMenu("Khách hàng", "data/img/icons/people.png", "ThongKeKhachHang");
        thongKe.addSubMenu("Sản phẩm", "data/img/icons/stats.png", "ThongKeSanPham");


        // ===== CÀI ĐẶT (KHÔNG SUB) =====
        MenuButton caiDat = createSingleMenu("CÀI ĐẶC", "CaiDat");

        addMenu(danhMuc);
        addMenu(xuLy);
        addMenu(traCuu);
        addMenu(thongKe);
        addMenu(caiDat);

        add(Box.createVerticalGlue());
    }

    // ===== MENU CHA =====
    private MenuButton createMenu(String title) {
        MenuButton btn = new MenuButton(title, null);
        btn.setMenuActionListener(this::onMenuSelected);
        return btn;
    }

    // ===== MENU ĐƠN =====
    private MenuButton createSingleMenu(String title, String pageName) {
        MenuButton btn = new MenuButton(title, pageName);
        btn.setMenuActionListener(this::onMenuSelected);
        return btn;
    }

    // ===== ADD MENU =====
    private void addMenu(MenuButton btn) {
        menuButtons.add(btn);
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
        add(btn);
    }

    // ===== EVENT =====
    private void onMenuSelected(MenuButton source, String page) {

        for (MenuButton mb : menuButtons) {
            if (mb != source) {
                mb.collapse();
                mb.resetSubMenuColor();
                mb.setMainSelected(false);
            }
        }

        if (page != null) {
            mainFrame.showPage(page);
        }
    }
}