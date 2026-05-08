package exception;

import gui.Main_GUI;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

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
        danhMuc.addSubMenu("Thuế", "data/img/icons/taxes.png", "Thue");

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
        MenuButton heThong = createSingleMenu("HỆ THỐNG", "HeThong");
        heThong.addSubMenu("Tài khoản", "data/img/icons/gear.png", "TaiKhoan");
        heThong.addSubMenu("Trang chủ", "data/img/icons/home.png", "TrangChu");
        heThong.addSubMenu("Trợ giúp", "data/img/icons/question.png", "TroGiup");
        heThong.addSubMenu("Thoát", "data/img/icons/logout.png", "Thoat");

        addMenu(danhMuc);
        addMenu(xuLy);
        addMenu(traCuu);
        addMenu(thongKe);
        addMenu(heThong);

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
