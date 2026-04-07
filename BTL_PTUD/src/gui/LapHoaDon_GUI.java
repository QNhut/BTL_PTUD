package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.ScrollPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.DimensionUIResource;

import constants.Colors;
import constants.FontStyle;
import entity.CardItem;
import entity.LoaiSanPham;
import entity.NhaCungCap;
import entity.SanPham;
import exception.MenuButton.MenuActionListener;
import exception.RoundedButton;
import exception.RoundedPanel;
import exception.RoundedTextField;
import exception.RoundedToggleButton;

public class LapHoaDon_GUI extends JPanel implements ActionListener, MouseListener {
	private JPanel pHeader, pCenter, pEarth, pList, pInforKH, pGioHang, pKH, pListButton, pListProduct, pThanhToan,
	pCardContent;
	private JLabel lblTitle, lblKhachHang, lblTen, lblSDT, lblIconKH, lblTongTien;
	private RoundedToggleButton btnAll, btnHaSot, btnKhangSinh, btnVitamin, btnTieuHoa, btnDiUng, btnHo;
	private ButtonGroup group;
	private FontStyle fontStyle;
	private JScrollPane scroll, scrollGH;
	private RoundedTextField txtFind, txtTenKH, txtSDT;
	private ArrayList<SanPham> list = new ArrayList<SanPham>();
	private ArrayList<SanPham> l = new ArrayList<SanPham>();
	private ArrayList<CardItem> gioHang = new ArrayList<CardItem>();
	private JButton btnHuy, btnThanhToan;
	private JPanel pButton;
	private JPanel pTongTien;
	
	public LapHoaDon_GUI() {
		setPreferredSize(new Dimension(1250, 900));
		setLayout(new BorderLayout());
		
		// =======HEADER===========
		pHeader = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pHeader.setBackground(Colors.PRIMARY);
		pHeader.setPreferredSize(new Dimension(0, 90));
		pHeader.add(lblTitle = new JLabel("Tạo Hóa đơn"));
		lblTitle.setFont(new Font(fontStyle.FONT_FAMILY, Font.BOLD, 40));
		lblTitle.setForeground(Colors.BACKGROUND);
		lblTitle.setBorder(new EmptyBorder(10, 0, 0, 0));
		add(pHeader, BorderLayout.NORTH);
		
		//CENTER
		pCenter = new JPanel();
		pCenter = new JPanel(new BorderLayout());
		pCenter.setBackground(Colors.BACKGROUND);
		
		pList = new JPanel();
		pList.setLayout(new BoxLayout(pList, BoxLayout.Y_AXIS));
		pList.setBackground(Colors.BACKGROUND);
		pList.add(Box.createVerticalStrut(20));
		txtFind = new RoundedTextField(600, 50, 20, "Tìm kiếm thuốc...");
		txtFind.setPreferredSize(new Dimension(600, 40));
		txtFind.setMaximumSize(new Dimension(600, 40));
		pList.add(txtFind);
		
		scroll = new JScrollPane(pList);
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);;
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setBorder(null);
		scroll.setViewportBorder(null);
		pCenter.add(scroll, BorderLayout.CENTER);
		add(pCenter, BorderLayout.CENTER);
		
		group = new ButtonGroup();
		btnAll = new RoundedToggleButton(100, 50, 20, "Tất cả", Colors.PRIMARY);
		btnDiUng = new RoundedToggleButton(100, 50, 20,"Dị ứng", Colors.PRIMARY);
		btnHaSot = new RoundedToggleButton(100, 50, 20,"Hạ sốt", Colors.PRIMARY);
		btnHo = new RoundedToggleButton(100, 50, 20,"Ho", Colors.PRIMARY);
		btnKhangSinh = new RoundedToggleButton(100, 50, 20,"Kháng sinh", Colors.PRIMARY);
		btnTieuHoa = new RoundedToggleButton(100, 50, 20,"Tiêu hóa", Colors.PRIMARY);
		btnVitamin = new RoundedToggleButton(100, 50, 20,"Vitamin", Colors.PRIMARY);
		

		group.add(btnAll);
		group.add(btnKhangSinh);
		group.add(btnVitamin);
		group.add(btnDiUng);
		group.add(btnHaSot);
		group.add(btnHo);
		group.add(btnTieuHoa);
		
		pListButton = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pListButton.setBackground(Colors.BACKGROUND);
		pListButton.add(btnAll);
		pListButton.add(btnHo);
		pListButton.add(btnHaSot);
		pListButton.add(btnDiUng);
		pListButton.add(btnVitamin);
		pListButton.add(btnKhangSinh);
		pListButton.add(btnTieuHoa);
		
		pList.add(Box.createVerticalStrut(10));
		pList.add(pListButton);
		
		pListProduct = new JPanel(new GridBagLayout());
		pListProduct.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		pListProduct.setBackground(Colors.BACKGROUND);
		list.add(new SanPham("sp01", "Paracetamol 500mg", new NhaCungCap(), new LoaiSanPham("sp01", "Ha sot", "ha sot"), 2000, 302, "Vien", "2/3/2027", "img1.jpg"));
		list.add(new SanPham("sp02", "miếng dán giảm đau Salonpas", new NhaCungCap(), new LoaiSanPham("sp01", "Giam dau", "ha sot"), 430200, 252, "Vien", "2/3/2027", "img1.jpg"));
		list.add(new SanPham("sp03", "Berberin", new NhaCungCap(), new LoaiSanPham(), 2000, 302, "Vien", "2/3/2027", "img1.jpg"));
		list.add(new SanPham("sp04", "thuốc kháng acid dạ dày(Antacid, Omeprazole)", new NhaCungCap(), new LoaiSanPham("sp01", "Da day", "ha sot"), 212000, 324, "Vien", "2/3/2027", "img1.jpg"));
		list.add(new SanPham("sp05", "Thuốc ho(ho khan,Acetylcystein)", new NhaCungCap(), new LoaiSanPham("sp01", "Thuoc ho", "ha sot"), 42547, 1673, "Vien", "2/3/2027", "img1.jpg"));
		list.add(new SanPham("sp06", "Diphenhydramine(kem bôi)", new NhaCungCap(), new LoaiSanPham("sp01", "Ngoai da", "ha sot"), 32450, 219, "Vien", "2/3/2027", "img1.jpg"));
		list.add(new SanPham("sp07", "băng gạc", new NhaCungCap(), new LoaiSanPham("sp01", "Vat tu y te", "ha sot"), 2000, 302, "Vien", "2/3/2027", "img1.jpg"));
		
		int n = 0;
		for(SanPham i : list) {
			addProductCard(createProductCard(i), n);
			n++;
		}
		
		
		pList.add(Box.createVerticalStrut(10));
		pList.add(pListProduct);
		
		
		//EARTH
		pEarth = new JPanel(new BorderLayout());
		pEarth.setPreferredSize(new Dimension(300, 800));
		add(pEarth, BorderLayout.EAST);
		
		//Thong tin KH
		pInforKH = new JPanel();
		pInforKH.setPreferredSize(new Dimension(300, 250));
		pInforKH.setLayout(new BoxLayout(pInforKH, BoxLayout.Y_AXIS));
		pInforKH.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
		pKH = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pKH.add(lblIconKH = new JLabel(loadIcon("data/img/icons/people.png", 30, 30)));
		pKH.add(lblKhachHang = new JLabel("Khách hàng"));
		
		lblKhachHang.setFont(new Font("Arial", Font.BOLD, 20));
		lblTen = new JLabel("Tên*: ");
		lblSDT = new JLabel("Số Điện thoại: ");
		txtTenKH = new  RoundedTextField(250, 30, 20, "Nhập tên khách hàng");
		txtTenKH.setMaximumSize(new Dimension(220, 20));
		txtSDT = new RoundedTextField(100, 30, 20, "Nhập số điên thoại khách hàng");
		txtSDT.setMaximumSize(new Dimension(250, 20));
		
		lblTen.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblSDT.setAlignmentX(Component.LEFT_ALIGNMENT);
		txtTenKH.setAlignmentX(Component.LEFT_ALIGNMENT);
		txtSDT.setAlignmentX(Component.LEFT_ALIGNMENT);
		pKH.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		pInforKH.add(pKH);
		pInforKH.add(lblTen);
		pInforKH.add(Box.createVerticalStrut(10));
		pInforKH.add(txtTenKH);
		pInforKH.add(Box.createVerticalStrut(20));
		pInforKH.add(lblSDT);
		pInforKH.add(Box.createVerticalStrut(10));
		pInforKH.add(txtSDT);
		pInforKH.add(Box.createVerticalStrut(10));
		
		//Gio Hang
		pGioHang = new JPanel(new BorderLayout());
		pGioHang.setBorder(BorderFactory.createLineBorder(Colors.BORDER));
		
		pCardContent = new JPanel();
		pCardContent.setLayout(new BoxLayout(pCardContent, BoxLayout.Y_AXIS));
		
		scrollGH = new JScrollPane(pCardContent);
		scrollGH.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollGH.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		pGioHang.add(scrollGH);
		updateCart(gioHang);
		
		pThanhToan = new JPanel();
		pThanhToan.setMinimumSize(new Dimension(300, 300));		
		pThanhToan.setPreferredSize(new Dimension(300, 150));
		pThanhToan.setBorder(BorderFactory.createEmptyBorder(10,0, 0,0));
		pThanhToan.setBackground(Colors.BACKGROUND);
		pThanhToan.setLayout(new BorderLayout());
		
		pTongTien = new JPanel();
		lblTongTien = new JLabel("Tổng tiền: "+ Double.toString(tinhTongHoaDon()) + "đ");
		pTongTien.add(lblTongTien);
		pTongTien.setBackground(Colors.BACKGROUND);
		pTongTien.setMinimumSize(new Dimension(0, 200));
	
		pButton = new JPanel();
		pButton.setLayout(new FlowLayout());
		pButton.add(btnThanhToan = new RoundedButton(100, 40, 20, "Thanh Toán", Colors.PRIMARY));
		pButton.add(btnHuy = new RoundedButton(100, 40, 20, "Hủy", Colors.SECONDARY));
		pButton.setBackground(Colors.BACKGROUND);
		
		btnHuy.setBackground(Colors.BACKGROUND);
		btnHuy.setPreferredSize(new Dimension(100, 40));
		btnHuy.setMaximumSize(new Dimension(100, 40));
		btnHuy.setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));
		btnHuy.setFont(new Font("Arial", Font.BOLD, 15));
		btnHuy.setForeground(Colors.FOREGROUND);
		
		btnThanhToan.setBackground(Colors.PRIMARY);
		btnThanhToan.setPreferredSize(new Dimension(100, 40));
		btnThanhToan.setMaximumSize(new Dimension(100, 40));
		btnThanhToan.setBorder(BorderFactory.createLineBorder(Colors.BORDER, 1));
		btnThanhToan.setFont(new Font("Arial", Font.BOLD, 15));
		btnThanhToan.setForeground(Colors.BACKGROUND);
		
		pThanhToan.add(pTongTien, BorderLayout.NORTH);
		pThanhToan.add(pButton, BorderLayout.CENTER);
		
		pEarth.add(pInforKH, BorderLayout.NORTH);
		pEarth.add(pGioHang, BorderLayout.CENTER);
		pEarth.add(pThanhToan, BorderLayout.SOUTH);
		
	}
	
	
	//Tạo thẻ sản phẩm 
	public JPanel createProductCard(SanPham sp) {
		JPanel card = new RoundedPanel(350, 200, 30);
		card.setLayout(new BorderLayout());
		card.setBackground(Colors.BACKGROUND);
	    card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
		card.setPreferredSize(new Dimension(350, 150));
		card.setMinimumSize(new Dimension(350, 150));
		
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		infoPanel.setOpaque(false);
		
	    JLabel lblName = new JLabel(sp.getTenSP());
	    lblName.setFont(new Font("Segoe UI", Font.BOLD, 16));
		
		JLabel lblCategory = new JLabel(sp.getLoaiSP().getTenLoaiSP());
	    lblCategory.setForeground(Colors.FOREGROUND);

	    JLabel lblPrice = new JLabel(String.valueOf(sp.getGiaBan()));
	    lblPrice.setForeground(Colors.PRIMARY);
	    lblPrice.setFont(new Font("Segoe UI", Font.BOLD, 18));

	    infoPanel.add(lblName);
	    infoPanel.add(lblCategory);
	    infoPanel.add(Box.createVerticalStrut(10));
	    infoPanel.add(lblPrice);

	    // ===== Button =====
	    JButton btnAdd = new RoundedButton(70, 40, 20,"+ Thêm", Colors.PRIMARY);
	    btnAdd.setBackground(Colors.PRIMARY);
	    btnAdd.setForeground(Colors.BACKGROUND);
	    btnAdd.setFocusPainted(false);

	    card.add(infoPanel, BorderLayout.CENTER);
	    card.add(btnAdd, BorderLayout.SOUTH);
	    btnAdd.addActionListener(e ->{
	    	boolean found = false;
	    	for(CardItem i : gioHang) {
	    		if(i.getSp().getMaSP().equals(sp.getMaSP())) {
	    			i.setSoLuong(i.getSoLuong()+1);
	    			found = true;
	    		}
	    	}
	    	
	    	if(!found) gioHang.add(new CardItem(sp, 1));
	    	updateCart(gioHang);
	    	updateTongTien();
	    });
	    
	    return card;
	}
	
	
	//Thêm thẻ sản phẩm vào danh sách
	private void addProductCard(JPanel card, int index) {
	    GridBagConstraints gbc = new GridBagConstraints();

	    gbc.gridx = index % 2;   // 0 hoặc 1 (2 cột)
	    gbc.gridy = index / 2;   // tự xuống hàng
	    gbc.insets = new Insets(10, 10, 10, 10);
	    gbc.anchor = GridBagConstraints.NORTH;
	    gbc.fill = GridBagConstraints.NONE;  // QUAN TRỌNG

	    gbc.weightx = 0;  // không cho giãn ngang
	    gbc.weighty = 0;  // không cho giãn dọc

	    pListProduct.add(card, gbc);
	}
	
	
	//Tạo icon
    private ImageIcon loadIcon(String path, int w, int h) {

        ImageIcon icon = new ImageIcon(path);

        if (icon.getIconWidth() > 0) {
            Image img = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
            return new ImageIcon(img);
        } else {
            System.out.println("Không tìm thấy icon: " + path);
        }

        return null;
    }
	
    
    //Tạo giỏ hàng trống
    private JPanel createEmptyCartPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setOpaque(false);

        JLabel icon = new JLabel(loadIcon("data/img/cart.png", 80, 80));
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel text = new JLabel("Giỏ hàng trống");
        text.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(Box.createVerticalGlue());
        panel.add(icon);
        panel.add(Box.createVerticalStrut(10));
        panel.add(text);
        panel.add(Box.createVerticalGlue());

        return panel;
    }
    
    //Tạo sản phẩm thanh toán trong giỏ hàng
    private JPanel createCardItem(CardItem card, JPanel parent) {
        JPanel item = new RoundedPanel(100, 120, 30);
        item.setLayout(new BorderLayout(10, 5));
        item.setBackground(Colors.BACKGROUND);
        item.setMaximumSize(new Dimension(300, 150));
        item.setPreferredSize(new Dimension(100, 110));
        item.setBorder(new EmptyBorder(10, 10, 10, 10));

        // ====== TOP: tên + giá + nút xoá ======
        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);

        JLabel lblInfo = new JLabel(
            "<html><div style='width:150px'><b>" + card.getSp().getTenSP() + "</b><br>" +
            "<span style='color:#00A86B'>" + card.getSp().getGiaBan() + "đ</span><div></html>"
        );

        JButton btnDelete = new JButton("✖");
        btnDelete.setForeground(Color.RED);
        btnDelete.setBorderPainted(false);
        btnDelete.setContentAreaFilled(false);
        btnDelete.setFocusPainted(false);
        btnDelete.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 👉 Xoá item khỏi UI
        btnDelete.addActionListener(e -> {
            parent.remove(item);
            gioHang.remove(card);
            parent.revalidate();
            parent.repaint();
            updateTongTien();
        });

        top.add(lblInfo, BorderLayout.WEST);
        top.add(btnDelete, BorderLayout.EAST);

        // ====== CENTER: số lượng ======
        JPanel center = new JPanel();
        center.setOpaque(false);

        JButton btnMinus = new JButton("-");
        JButton btnPlus = new JButton("+");
        
        btnMinus.setPreferredSize(new DimensionUIResource(40, 20));
        btnPlus.setPreferredSize(new DimensionUIResource(50, 20));

        JSpinner spinner = new JSpinner(new SpinnerNumberModel(card.getSoLuong(), 1, 100, 1));
        spinner.setPreferredSize(new Dimension(100, 25));

        btnMinus.addActionListener(e -> {
            int value = (int) spinner.getValue();
            if (value > 1) spinner.setValue(value - 1);
        });

        btnPlus.addActionListener(e -> {
            int value = (int) spinner.getValue();
            spinner.setValue(value + 1);
        });
        
        spinner.addChangeListener(e -> card.setSoLuong((int) spinner.getValue()));

        center.add(btnMinus);
        center.add(spinner);
        center.add(btnPlus);

        // ====== BOTTOM: tổng tiền ======
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setOpaque(false);

        JLabel lblTotal = new JLabel();
        lblTotal.setFont(new Font("Arial", Font.BOLD, 13));

        // cập nhật tổng tiền
        Runnable updateTotal = () -> {
            int qty = (int) spinner.getValue();
            int total = (int) (qty * card.getSp().getGiaBan());
            lblTotal.setText("Tổng: " + total + "đ");
        };

        spinner.addChangeListener(e -> updateTotal.run());
        updateTotal.run();

        bottom.add(lblTotal);

        // ====== ADD vào item ======
        item.add(top, BorderLayout.NORTH);
        item.add(center, BorderLayout.CENTER);
        item.add(bottom, BorderLayout.SOUTH);

        return item;
    }
    
    private void updateCart(ArrayList<CardItem> list) {
    	pCardContent.removeAll();
    	if(list.isEmpty()) {
    		pCardContent.add(createEmptyCartPanel());
    	}
    	else {
    		double tongTien = 0;
    		for(CardItem i : list) {
    			pCardContent.add(createCardItem(i, pCardContent));
    			pCardContent.add(Box.createVerticalStrut(10));
    		}
    	}
        pCardContent.revalidate();
        pCardContent.repaint();
    }
    
    private double tinhTongHoaDon() {
        double tong = 0;

        for (CardItem item : gioHang) {
            tong += item.getSp().getGiaBan() * item.getSoLuong();
        }

        return tong;
    }
    
    private void updateTongTien() {
    	lblTongTien.setText("Tổng tiền: " + Double.toString(tinhTongHoaDon()) + "đ");
    }


	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
	}
	
	public static void main(String[] args) {
		new LapHoaDon_GUI().setVisible(true);
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}