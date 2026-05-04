package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JDateChooser;

import constants.Colors;
import constants.FontStyle;

public class NhapHang_GUI extends JPanel {
	
	private JPanel pnlTitle;
	private JLabel lblTitle;
	private JLabel lblsubTitle;
	private JPanel pnlContent;
	private JPanel pnlContentLeft;
	private JPanel pnlContentRight;
	private JPanel pnlContentTopLeft;
	private JPanel pnlContentTopRight;
	private JPanel pnlContentBottom;
	private JLabel lblSubTitle;
	private JLabel lblSubTitle2;
	private JPanel pnlUpload;
	private JLabel lblSubTitle3;
	private JLabel lblSubTitle4;
	private JLabel lblSubTitle5;
	private JLabel lblSubTitle6;
	private JLabel lblcontentRight;
	private JComboBox<String> cboNCC;
	private JLabel lblsubcontentRight1;
	private JLabel lblsubcontentRight2;
	private JDateChooser dtcNgayNhap;
	private JLabel lblsubcontentRight3;
	private JTextArea txaGhiChu;
	private JScrollPane scrGhiChu;
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
	private JButton btnNhapHang;
	private JPanel pnlTitleBottom;
	private JPanel pnlsubTitleBottom;
	private JLabel lblTitleBottom1;
	private JLabel lblsubTitleBottom;
	private JButton btnXoaTatCa;
	private JPanel pnlTotelPrice;
	private JLabel lblTotal;
	private JLabel lblTotalPrice;
	private String[] columnNames = {"Sản phẩm", "Số lượng", "Đơn vị", "Giá", "Thành tiền", "Ghi chú"};
	private DefaultTableModel tableModel;
	private JTable tblSelected;

//	Lưu tên file đã chọn để dùng ở nơi khác
	private String selectedFileName = null;

	public NhapHang_GUI() {
		setLayout(new BorderLayout(10,10));
		setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		
//		Phần tiêu đề
		add(pnlTitle = new JPanel(), BorderLayout.NORTH);
		pnlTitle.setLayout(new BoxLayout(pnlTitle, BoxLayout.Y_AXIS));
		
		pnlTitle.add(lblTitle = new JLabel("Nhập hàng"));
		lblTitle.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
		pnlTitle.add(Box.createVerticalStrut(10));
		
		pnlTitle.add(lblsubTitle = new JLabel("Tạo phiếu nhập hàng vào kho"));
		lblsubTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
		
//		Phần nội dung chính
		add(pnlContent = new JPanel(), BorderLayout.CENTER);
		pnlContent.setLayout(new BorderLayout());
//		pnlContent.setBackground(Colors.MUTED);
		
//		Phần nội dung bên trái phần center
		pnlContent.add(pnlContentLeft = new JPanel(), BorderLayout.CENTER);
		pnlContentLeft.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Colors.MUTED, 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)
		));
		pnlContentLeft.setLayout(new BorderLayout());
		
//		Phần yêu cầu nhập file dữ liệu phiếu nhập hàng
		pnlContentLeft.add(pnlContentTopLeft = new JPanel(), BorderLayout.CENTER);
		
		pnlContentTopLeft.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Colors.MUTED, 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)
		));
		
		pnlContentTopLeft.setAlignmentX(LEFT_ALIGNMENT);
		pnlContentTopLeft.setLayout(new BoxLayout(pnlContentTopLeft, BoxLayout.Y_AXIS));
		pnlContentTopLeft.add(lblSubTitle = new JLabel("File dữ liệu phiếu nhập hàng"));
		lblSubTitle.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
		lblSubTitle.setAlignmentX(LEFT_ALIGNMENT);
		pnlContentTopLeft.add(Box.createVerticalStrut(10));
		pnlContentTopLeft.add(lblSubTitle2 = new JLabel("Tải lên file Excel hoặc CSV chứa danh sách sản phẩm cần nhập hàng"));
		lblSubTitle2.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lblSubTitle2.setAlignmentX(LEFT_ALIGNMENT);
		
//		Tải file lên — ban đầu true = chưa có file, hiện ô kéo thả
		pnlContentTopLeft.add(Box.createVerticalStrut(14));
		pnlContentTopLeft.add(createUploadPanel(true));
		pnlContentTopLeft.add(Box.createVerticalStrut(10));
		pnlContentTopLeft.add(Box.createVerticalGlue());
		
		
//		Phần nội dung bên phải chứ thông tin phiếu nhập hàng
		pnlContent.add(pnlContentRight = new JPanel(), BorderLayout.EAST);
		pnlContentRight.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Colors.MUTED, 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)
		));
		
		pnlContentRight.setPreferredSize(new Dimension(350, 0));
		pnlContentRight.setMinimumSize(new Dimension(350, 0));
		pnlContentRight.setMaximumSize(new Dimension(450, Integer.MAX_VALUE));
		
//		Bên phải: thông tin phiếu nhập + tóm tắt + nút tạo phiếu
		pnlContentRight.setLayout(new BoxLayout(pnlContentRight, BoxLayout.Y_AXIS));
		pnlContentRight.add(pnlContentTopRight = new JPanel());
//		pnlContentTopRight.setBackground(Colors.SUCCESS);
		pnlContentTopRight.setLayout(new BoxLayout(pnlContentTopRight, BoxLayout.Y_AXIS));
		pnlContentTopRight.setBorder(BorderFactory.createCompoundBorder(
		        BorderFactory.createLineBorder(Colors.MUTED, 1), 
		        BorderFactory.createEmptyBorder(10, 10, 10, 10)  
		    ));
		pnlContentTopRight.setPreferredSize(new Dimension(350, 0));
		pnlContentTopRight.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		pnlContentTopRight.add(lblcontentRight = new JLabel("Thông tin phiếu nhập hàng"));
		lblcontentRight.setFont(FontStyle.font(FontStyle.LG, FontStyle.BOLD));
		lblcontentRight.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		pnlContentTopRight.add(Box.createVerticalStrut(10));
		
		pnlContentTopRight.add(lblsubcontentRight1 = new JLabel("Nhà cung cấp"));
		lblsubcontentRight1.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblsubcontentRight1.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		pnlContentTopRight.add(Box.createVerticalStrut(5));
		pnlContentTopRight.add(cboNCC = new JComboBox<>(new String[] {"Nhà cung cấp A", "Nhà cung cấp B", "Nhà cung cấp C"}));
		cboNCC.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		cboNCC.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		pnlContentTopRight.add(Box.createVerticalStrut(10));
		pnlContentTopRight.add(lblsubcontentRight2 = new JLabel("Ngày nhập hàng"));
		lblsubcontentRight2.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblsubcontentRight2.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		pnlContentTopRight.add(Box.createVerticalStrut(5));
		pnlContentTopRight.add(dtcNgayNhap = new JDateChooser());
//		dtcNgayNhap.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		
		dtcNgayNhap.setAlignmentX(Component.LEFT_ALIGNMENT);
		dtcNgayNhap.setDateFormatString("dd/MM/yyyy");
		
		pnlContentTopRight.add(Box.createVerticalStrut(10));
		pnlContentTopRight.add(lblsubcontentRight3 = new JLabel("Ghi chú"));
		lblsubcontentRight3.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblsubcontentRight3.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		pnlContentTopRight.add(Box.createVerticalStrut(5));
		txaGhiChu = new JTextArea(5, 10);
		txaGhiChu.setLineWrap(true);
		txaGhiChu.setWrapStyleWord(true);
		pnlContentTopRight.add(scrGhiChu = new JScrollPane(txaGhiChu));
		scrGhiChu.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
		scrGhiChu.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		pnlContentTopRight.add(Box.createVerticalStrut(10));
		
//		Khung tóm tắt + nút
		pnlContentTopRight.add(pnlContentBottomRight = new JPanel());
		pnlContentBottomRight.setLayout(new BoxLayout(pnlContentBottomRight, BoxLayout.Y_AXIS));
		pnlContentBottomRight.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		pnlContentBottomRight.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
		pnlContentBottomRight.setBackground(Colors.SUCCESS_LIGHT);
		pnlContentBottomRight.setAlignmentX(Component.LEFT_ALIGNMENT);

//		Row 1: Số mặt hàng
		pnlContentBottomRight.add(row1 = new JPanel(new BorderLayout()));
		row1.setOpaque(false);
		row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
		row1.add(lblLeft1 = new JLabel("Số mặt hàng"), BorderLayout.WEST);
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
		row3.add(lblLeft3 = new JLabel("Tổng tiền nhập"), BorderLayout.WEST);
		row3.add(lblRight3 = new JLabel("0đ"), BorderLayout.EAST);
		lblLeft3.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
		lblRight3.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
		lblRight3.setForeground(Colors.PRIMARY);

		pnlContentBottomRight.add(Box.createVerticalStrut(14));

//		Nút "Tạo phiếu nhập" 
		btnNhapHang = new JButton("Tạo phiếu nhập");
		btnNhapHang.setAlignmentX(Component.CENTER_ALIGNMENT);
		btnNhapHang.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		btnNhapHang.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
		btnNhapHang.setOpaque(true);
		btnNhapHang.setContentAreaFilled(true);
		btnNhapHang.setBorderPainted(false);
		btnNhapHang.setFocusPainted(false);

//		Trạng thái ban đầu: disabled
		setButtonState(false);
		pnlContentBottomRight.add(btnNhapHang);

		btnNhapHang.addMouseListener(new MouseAdapter() {
		    @Override
		    public void mouseEntered(MouseEvent evt) {
		        if (btnNhapHang.isEnabled()) {
		            btnNhapHang.setBackground(Colors.PRIMARY_BUTTON);
		        }
		    }
		    @Override
		    public void mouseExited(MouseEvent evt) {
		        if (btnNhapHang.isEnabled()) {
		            btnNhapHang.setBackground(Colors.PRIMARY);
		        }
		    }
		});

		pnlContent.add(pnlContentBottom = new JPanel(new BorderLayout()), BorderLayout.SOUTH);
		pnlContentBottom.setBorder(BorderFactory.createCompoundBorder(
		        BorderFactory.createLineBorder(Colors.MUTED, 1), 
		        BorderFactory.createEmptyBorder(10, 10, 10, 10)
		));
		pnlContentBottom.setPreferredSize(new Dimension(0, 320));

		pnlContentBottom.add(pnlTitleBottom = new JPanel(new BorderLayout()), BorderLayout.NORTH);
		pnlTitleBottom.setOpaque(false);
		pnlTitleBottom.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

		pnlTitleBottom.add(pnlsubTitleBottom = new JPanel(), BorderLayout.WEST);
		pnlsubTitleBottom.setOpaque(false);
		pnlsubTitleBottom.setLayout(new BoxLayout(pnlsubTitleBottom, BoxLayout.Y_AXIS));
		pnlsubTitleBottom.add(lblTitleBottom1 = new JLabel("Danh sách nhập hàng"));
		lblTitleBottom1.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
		pnlsubTitleBottom.add(Box.createVerticalStrut(5));
		pnlsubTitleBottom.add(lblsubTitleBottom = new JLabel("0 sản phẩm đã chọn"));

		pnlTitleBottom.add(btnXoaTatCa = new JButton("Xóa tất cả"), BorderLayout.EAST);
		btnXoaTatCa.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		btnXoaTatCa.setFocusPainted(false);
		btnXoaTatCa.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
		btnXoaTatCa.setOpaque(true);
		btnXoaTatCa.setBackground(Colors.DANGER);
		btnXoaTatCa.setForeground(Colors.BACKGROUND);
		btnXoaTatCa.addActionListener(e -> {
			tableModel.setRowCount(0);
			updateSummary();
		});

		pnlContentBottom.add(createSelectedListPanel(), BorderLayout.CENTER);

		pnlContentBottom.add(pnlTotelPrice = new JPanel(), BorderLayout.SOUTH);
		pnlTotelPrice.setOpaque(false);
		pnlTotelPrice.setLayout(new BoxLayout(pnlTotelPrice, BoxLayout.X_AXIS));
		pnlTotelPrice.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
		pnlTotelPrice.add(lblTotal = new JLabel("Tổng tiền: "));
		pnlTotelPrice.add(Box.createHorizontalStrut(5));
		pnlTotelPrice.add(lblTotalPrice = new JLabel("0đ"));
		lblTotal.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
		lblTotalPrice.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
		lblTotalPrice.setForeground(Colors.PRIMARY);

		
	}

	private JScrollPane createSelectedListPanel() {
		tableModel = new DefaultTableModel(columnNames, 0) {
			@Override
			public boolean isCellEditable(int row, int col) {
				return col == 1 || col == 5;
			}
		};

		tblSelected = new JTable(tableModel);
		tblSelected.setRowHeight(32);
		tblSelected.getTableHeader().setReorderingAllowed(false);
		tblSelected.setBackground(Colors.BACKGROUND);
		tblSelected.setGridColor(Colors.BORDER_LIGHT);
		tblSelected.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		tblSelected.getTableHeader().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		tblSelected.setShowHorizontalLines(true);
		tblSelected.setShowVerticalLines(false);
		tblSelected.getColumnModel().getColumn(0).setPreferredWidth(220);
		tblSelected.getColumnModel().getColumn(1).setPreferredWidth(110);
		tblSelected.getColumnModel().getColumn(2).setPreferredWidth(110);
		tblSelected.getColumnModel().getColumn(3).setPreferredWidth(130);
		tblSelected.getColumnModel().getColumn(4).setPreferredWidth(140);
		tblSelected.getColumnModel().getColumn(5).setPreferredWidth(220);

		tableModel.addTableModelListener(e -> updateSummary());

		return new JScrollPane(tblSelected);
	}

	private void setButtonState(boolean enabled) {
		if (enabled) {
			btnNhapHang.setEnabled(true);
			btnNhapHang.setBackground(Colors.PRIMARY);
			btnNhapHang.setForeground(Colors.BACKGROUND);
			btnNhapHang.setCursor(new Cursor(Cursor.HAND_CURSOR));
		} else {
			btnNhapHang.setEnabled(false);
			btnNhapHang.setBackground(Colors.BORDER);
			btnNhapHang.setForeground(Colors.MUTED);
			btnNhapHang.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	private void updateSummary() {
		int soMatHang = tableModel.getRowCount();
		int tongSoLuong = 0;
		double tongTien = 0;

		for (int i = 0; i < tableModel.getRowCount(); i++) {
			Object qtyObj = tableModel.getValueAt(i, 1);
			Object totalObj = tableModel.getValueAt(i, 4);
			if (qtyObj instanceof Number) {
				tongSoLuong += ((Number) qtyObj).intValue();
			}
			if (totalObj instanceof Number) {
				tongTien += ((Number) totalObj).doubleValue();
			}
		}

		lblsubTitleBottom.setText(String.format("%d sản phẩm đã chọn", soMatHang));
		lblRight1.setText(soMatHang + " sản phẩm");
		lblRight2.setText(tongSoLuong + " đơn vị");
		lblRight3.setText(String.format("%.0fđ", tongTien));
		lblTotalPrice.setText(String.format("%.0fđ", tongTien));
		setButtonState(soMatHang > 0);
	}
	
	private JPanel createUploadPanel(Boolean status) {
		pnlUpload = new JPanel();
		pnlUpload.setLayout(new BoxLayout(pnlUpload, BoxLayout.Y_AXIS));
		pnlUpload.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Colors.MUTED, 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)
		));
		pnlUpload.setAlignmentX(LEFT_ALIGNMENT);
		if(status) {
//			status = true: chưa có file — hiển thị ô kéo thả / chọn file
			pnlUpload.setPreferredSize(new Dimension(0, 240));
			pnlUpload.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
//			pnlUpload.setMaximumSize(new Dimension(600, 380));
//			pnlUpload.setMinimumSize(new Dimension(400, 180));
			pnlUpload.add(Box.createVerticalGlue());
			Image icon = new ImageIcon("data/img/icons/upload.png").getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
			pnlUpload.add(lblSubTitle3 = new JLabel(new ImageIcon(icon)));
			lblSubTitle3.setFont(FontStyle.font(FontStyle.XXL, FontStyle.BOLD));
			lblSubTitle3.setAlignmentX(CENTER_ALIGNMENT);
			pnlUpload.add(Box.createVerticalStrut(10));
			pnlUpload.add(lblSubTitle4 = new JLabel("Kéo thả hoặc nhấn để chọn file"));
			lblSubTitle4.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
			lblSubTitle4.setAlignmentX(CENTER_ALIGNMENT);
			pnlUpload.add(Box.createVerticalStrut(5));
			pnlUpload.add(lblSubTitle5 = new JLabel("Hỗ trợ file Excel (.xlsx, .xls) và CSV (.csv)"));
			lblSubTitle5.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
			lblSubTitle5.setAlignmentX(CENTER_ALIGNMENT);
			pnlUpload.add(Box.createVerticalGlue());

//			Nhấn để mở JFileChooser
			pnlUpload.setCursor(new Cursor(Cursor.HAND_CURSOR));
			pnlUpload.addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) {
			        chonFile();
			    }
			});

//			Kéo thả file vào panel
			pnlUpload.setTransferHandler(new TransferHandler() {
			    @Override
			    public boolean canImport(TransferSupport support) {
			        return support.isDataFlavorSupported(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
			    }
			    @Override
			    public boolean importData(TransferSupport support) {
			        try {
			            @SuppressWarnings("unchecked")
			            java.util.List<File> files = (java.util.List<File>)
			                support.getTransferable().getTransferData(java.awt.datatransfer.DataFlavor.javaFileListFlavor);
			            if (!files.isEmpty()) {
			                xuLyFile(files.get(0));
			                return true;
			            }
			        } catch (Exception ex) {
			            ex.printStackTrace();
			        }
			        return false;
			    }
			});

		}else {
//			status = false: đã có file — hiển thị tên file và nút đổi file
			pnlUpload.setPreferredSize(new Dimension(0, 100));
			pnlUpload.add(Box.createVerticalGlue());
			pnlUpload.add(lblSubTitle6 = new JLabel((selectedFileName != null ? selectedFileName : "Đã tải file thành công!")));
			lblSubTitle6.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
			lblSubTitle6.setForeground(Colors.SUCCESS);
			lblSubTitle6.setAlignmentX(CENTER_ALIGNMENT);
			pnlUpload.add(Box.createVerticalStrut(8));
//			Nút đổi file
			JLabel lblDoiFile = new JLabel("Chọn file khác");
			lblDoiFile.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
			lblDoiFile.setForeground(Colors.PRIMARY);
			lblDoiFile.setAlignmentX(CENTER_ALIGNMENT);
			lblDoiFile.setCursor(new Cursor(Cursor.HAND_CURSOR));
			lblDoiFile.addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseClicked(MouseEvent e) {
			        chonFile();
			    }
			});
			pnlUpload.add(lblDoiFile);
			pnlUpload.add(Box.createVerticalGlue());
		}
		return pnlUpload;
	}

//	Mở JFileChooser và xử lý file được chọn
	private void chonFile() {
	    JFileChooser fc = new JFileChooser();
	    fc.setFileFilter(new FileNameExtensionFilter("Excel & CSV (*.xlsx, *.xls, *.csv)", "xlsx", "xls", "csv"));
	    fc.setAcceptAllFileFilterUsed(false);
	    int result = fc.showOpenDialog(this);
	    if (result == JFileChooser.APPROVE_OPTION) {
	        xuLyFile(fc.getSelectedFile());
	    }
	}

//	Nhận file, lưu tên, cập nhật panel sang trạng thái đã có file
	private void xuLyFile(File file) {
	    selectedFileName = file.getName();
	    SwingUtilities.invokeLater(() -> {
	        pnlContentTopLeft.remove(pnlUpload);
	        pnlContentTopLeft.add(createUploadPanel(false));
	        pnlContentTopLeft.revalidate();
	        pnlContentTopLeft.repaint();
	    });
	}

//	Lấy tên file đã chọn từ bên ngoài class
	public String getSelectedFileName() {
	    return selectedFileName;
	}

}
