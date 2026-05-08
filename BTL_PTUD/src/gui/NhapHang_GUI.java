package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.WindowConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import com.toedter.calendar.JDateChooser;

import constants.Colors;
import constants.FontStyle;
import entity.ChiTietPhieuNhap;
import entity.KeSanPham;
import entity.NhaCungCap;
import entity.NhanVien;
import entity.PhieuNhap;
import entity.SanPham;
import exception.QuantityEditor;
import service.LoSanPham_Service;
import service.NhaCungCap_Service;
import service.PhieuNhap_Service;

public class NhapHang_GUI extends JPanel {
	private final PhieuNhap_Service phieuNhapService = new PhieuNhap_Service();
	private final NhaCungCap_Service nhaCungCapService = new NhaCungCap_Service();
	private final LoSanPham_Service loSanPhamService = new LoSanPham_Service();
	private boolean dangCapNhatBang;
	private List<ChiTietPhieuNhap> danhSachChiTiet = new ArrayList<ChiTietPhieuNhap>();
	private File selectedFile;
	
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
	private JLabel errNCC;
	private JLabel lblsubcontentRight1;
	private JLabel lblsubcontentRight2;
	private JDateChooser dtcNgayNhap;
	private JLabel lblsubcontentRight3;
	private JTextArea txaGhiChu;
	private JScrollPane scrGhiChu;
	private JButton btnChinhSuaThongTin;
	private boolean dangChinhSuaThongTin;
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
	private String[] columnNames = {"Sản phẩm", "Số lượng", "Đơn vị", "Giá", "Thành tiền", "Xóa"};
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
		pnlContentTopRight.add(cboNCC = new JComboBox<String>());
		cboNCC.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
		cboNCC.setAlignmentX(Component.LEFT_ALIGNMENT);
		napDanhSachNhaCungCap();
		errNCC = new JLabel();
		errNCC.setFont(FontStyle.font(FontStyle.XS, FontStyle.NORMAL));
		errNCC.setForeground(Colors.DANGER);
		errNCC.setAlignmentX(Component.LEFT_ALIGNMENT);
		errNCC.setVisible(false);
		pnlContentTopRight.add(errNCC);
		cboNCC.addActionListener(e -> { if (errNCC != null) errNCC.setVisible(false); });
		
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
		
		pnlContentTopRight.add(Box.createVerticalStrut(8));
		pnlContentTopRight.add(btnChinhSuaThongTin = new JButton("Chỉnh sửa thông tin file"));
		btnChinhSuaThongTin.setAlignmentX(Component.LEFT_ALIGNMENT);
		btnChinhSuaThongTin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
		btnChinhSuaThongTin.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		btnChinhSuaThongTin.setFocusPainted(false);
		btnChinhSuaThongTin.setOpaque(true);
		btnChinhSuaThongTin.setContentAreaFilled(true);
		btnChinhSuaThongTin.setBorderPainted(false);
		btnChinhSuaThongTin.addActionListener(e -> setCheDoChinhSuaThongTin(!dangChinhSuaThongTin));
		btnChinhSuaThongTin.setEnabled(false);
		setCheDoChinhSuaThongTin(false);
		
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
		btnNhapHang = new JButton("Nhập sản phẩm");
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
		btnNhapHang.addActionListener(e -> xuLyTaoPhieuNhap());

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
		btnXoaTatCa.addActionListener(e -> loaiBoToanBoPhieuNhap());

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
		tblSelected.getColumnModel().getColumn(1).setCellEditor(new QuantityEditor(1, 3, 4));
		tblSelected.getColumnModel().getColumn(5).setPreferredWidth(60);
		tblSelected.getColumnModel().getColumn(5).setMaxWidth(60);
		tblSelected.getColumnModel().getColumn(5).setCellRenderer(new DeleteButtonRenderer());
		tblSelected.getColumnModel().getColumn(5).setCellEditor(new DeleteButtonEditor());
		tableModel.addTableModelListener(e -> {
			if (dangCapNhatBang) {
				return;
			}
			if (e.getType() == TableModelEvent.UPDATE && e.getColumn() == 1 && e.getFirstRow() >= 0) {
				capNhatThanhTien(e.getFirstRow());
			}
			updateSummary();
		});

		return new JScrollPane(tblSelected);
	}

	private void addToRow(ChiTietPhieuNhap chiTietPhieuNhap) {
		if (chiTietPhieuNhap == null) {
			return;
		}
		Object[] rowData = chiTietPhieuNhap.toRowData();
		tableModel.addRow(new Object[] { rowData[0], rowData[1], rowData[2], rowData[3], rowData[4], "Xóa" });
	}

	private class DeleteButtonRenderer implements TableCellRenderer {
		private final JButton btn = new JButton("✕");

		{
			btn.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
			btn.setForeground(Colors.DANGER);
			btn.setBorderPainted(false);
			btn.setContentAreaFilled(false);
			btn.setFocusPainted(false);
			btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			return btn;
		}
	}

	private class DeleteButtonEditor extends AbstractCellEditor implements TableCellEditor {
		private final JButton btn = new JButton("✕");
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
				if (currentRow >= 0 && currentRow < tableModel.getRowCount()) {
					tableModel.removeRow(currentRow);
				}
				if (tableModel.getRowCount() == 0) {
					loaiBoToanBoPhieuNhap();
				}
			});
		}

		@Override
		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
				int column) {
			currentRow = row;
			return btn;
		}

		@Override
		public Object getCellEditorValue() {
			return null;
		}
	}

	private void capNhatThanhTien(int row) {
		if (row < 0 || row >= tableModel.getRowCount()) {
			return;
		}

		int soLuong = docSoNguyen(tableModel.getValueAt(row, 1));
		double gia = docSoThuc(tableModel.getValueAt(row, 3));
		dangCapNhatBang = true;
		try {
			tableModel.setValueAt(soLuong * gia, row, 4);
		} finally {
			dangCapNhatBang = false;
		}
	}

	private int docSoNguyen(Object value) {
		if (value instanceof Number) {
			return ((Number) value).intValue();
		}
		if (value == null) {
			return 0;
		}
		try {
			return Integer.parseInt(value.toString().trim());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private double docSoThuc(Object value) {
		if (value instanceof Number) {
			return ((Number) value).doubleValue();
		}
		if (value == null) {
			return 0;
		}
		try {
			return Double.parseDouble(value.toString().trim());
		} catch (NumberFormatException e) {
			return 0;
		}
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

	private void setCheDoChinhSuaThongTin(boolean choPhepChinhSua) {
		dangChinhSuaThongTin = choPhepChinhSua;
		if (cboNCC != null) {
			cboNCC.setEnabled(choPhepChinhSua);
		}
		if (dtcNgayNhap != null) {
			dtcNgayNhap.setEnabled(choPhepChinhSua);
		}
		if (txaGhiChu != null) {
			txaGhiChu.setEditable(choPhepChinhSua);
			txaGhiChu.setBackground(choPhepChinhSua ? Colors.BACKGROUND : new Color(245, 247, 250));
		}
		if (btnChinhSuaThongTin == null) {
			return;
		}
		if (!btnChinhSuaThongTin.isEnabled()) {
			btnChinhSuaThongTin.setText("Chỉnh sửa thông tin file");
			btnChinhSuaThongTin.setBackground(Colors.BORDER);
			btnChinhSuaThongTin.setForeground(Colors.MUTED);
			return;
		}
		if (choPhepChinhSua) {
			btnChinhSuaThongTin.setText("Khóa chỉnh sửa");
			btnChinhSuaThongTin.setBackground(Colors.PRIMARY);
			btnChinhSuaThongTin.setForeground(Colors.BACKGROUND);
			return;
		}
		btnChinhSuaThongTin.setText("Chỉnh sửa thông tin file");
		btnChinhSuaThongTin.setBackground(new Color(233, 239, 248));
		btnChinhSuaThongTin.setForeground(Colors.PRIMARY);
	}

	private void napDanhSachNhaCungCap() {
		cboNCC.removeAllItems();
		for (String tenNhaCungCap : nhaCungCapService.layDanhSachTenNhaCungCap()) {
			cboNCC.addItem(tenNhaCungCap);
		}
		cboNCC.setSelectedItem(null);
	}

	private void xuLyTaoPhieuNhap() {
		if (tableModel.getRowCount() == 0) {
			return;
		}
		hienThiXacNhanNhapSanPham();
	}

	private void hienThiXacNhanNhapSanPham() {
		Frame owner = JOptionPane.getFrameForComponent(this);
		JDialog dialog = new JDialog(owner, "Xác nhận nhập lô sản phẩm", true);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dialog.setSize(820, 520);
		dialog.setLocationRelativeTo(this);

		JPanel content = new JPanel(new BorderLayout(16, 16));
		content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
		content.setBackground(Colors.BACKGROUND);

		JPanel header = new JPanel();
		header.setOpaque(false);
		header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
		JLabel lblTitleDialog = new JLabel("Xác nhận nhập lô sản phẩm");
		lblTitleDialog.setFont(FontStyle.font(FontStyle.XL, FontStyle.BOLD));
		lblTitleDialog.setAlignmentX(Component.LEFT_ALIGNMENT);
		JLabel lblSubtitleDialog = new JLabel("Kiểm tra lại thông tin lô trước khi xác nhận nhập kho");
		lblSubtitleDialog.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		lblSubtitleDialog.setForeground(Colors.MUTED);
		lblSubtitleDialog.setAlignmentX(Component.LEFT_ALIGNMENT);
		header.add(lblTitleDialog);
		header.add(Box.createVerticalStrut(6));
		header.add(lblSubtitleDialog);

		JPanel thongTinPanel = new JPanel();
		thongTinPanel.setOpaque(false);
		thongTinPanel.setLayout(new BoxLayout(thongTinPanel, BoxLayout.Y_AXIS));
		thongTinPanel.add(taoDongThongTinXacNhan("Nhà cung cấp", docNhaCungCapDangChon()));
		thongTinPanel.add(Box.createVerticalStrut(8));
		thongTinPanel.add(taoDongThongTinXacNhan("Ngày nhập", docNgayNhapDangChon()));
		thongTinPanel.add(Box.createVerticalStrut(8));
		thongTinPanel.add(taoDongThongTinXacNhan("Ghi chú", docGhiChuDangNhap()));

		JTable previewTable = taoBangXacNhanLoSanPham();
		JScrollPane scrollPane = new JScrollPane(previewTable);
		scrollPane.setBorder(BorderFactory.createLineBorder(Colors.BORDER_LIGHT));

		JPanel footer = new JPanel();
		footer.setOpaque(false);
		footer.setLayout(new BoxLayout(footer, BoxLayout.X_AXIS));
		footer.add(Box.createHorizontalGlue());
		JButton btnDong = taoNutTacVuFile("Đóng", Colors.BORDER, Colors.FOREGROUND);
		btnDong.addActionListener(e -> dialog.dispose());
		JButton btnXacNhan = taoNutTacVuFile("Xác nhận nhập", Colors.PRIMARY, Colors.BACKGROUND);
		btnXacNhan.addActionListener(e -> {
			dialog.dispose();
			xuLyLuuPhieuNhap();
		});
		footer.add(btnDong);
		footer.add(Box.createHorizontalStrut(10));
		footer.add(btnXacNhan);

		content.add(header, BorderLayout.NORTH);
		content.add(thongTinPanel, BorderLayout.WEST);
		content.add(scrollPane, BorderLayout.CENTER);
		content.add(footer, BorderLayout.SOUTH);

		dialog.setContentPane(content);
		dialog.setVisible(true);
	}

	private JPanel taoDongThongTinXacNhan(String nhan, String giaTri) {
		JPanel panel = new JPanel(new BorderLayout(8, 0));
		panel.setOpaque(true);
		panel.setBackground(Colors.PRIMARY_LIGHT);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Colors.BORDER_LIGHT),
				BorderFactory.createEmptyBorder(10, 12, 10, 12)));
		panel.setMaximumSize(new Dimension(230, 70));
		panel.setPreferredSize(new Dimension(230, 70));

		JLabel lblNhan = new JLabel(nhan);
		lblNhan.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		lblNhan.setForeground(Colors.TEXT_SECONDARY);
		JLabel lblGiaTri = new JLabel("<html><body style='width:170px'>" + escapeHtml(giaTri) + "</body></html>");
		lblGiaTri.setFont(FontStyle.font(FontStyle.BASE, FontStyle.NORMAL));
		lblGiaTri.setForeground(Colors.TEXT_PRIMARY);

		panel.add(lblNhan, BorderLayout.NORTH);
		panel.add(lblGiaTri, BorderLayout.CENTER);
		return panel;
	}

	private JTable taoBangXacNhanLoSanPham() {
		String[] columns = { "Mã lô", "Sản phẩm", "Số lượng", "Đơn vị", "Giá nhập", "Thành tiền" };
		DefaultTableModel previewModel = new DefaultTableModel(columns, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		for (int i = 0; i < tableModel.getRowCount(); i++) {
			previewModel.addRow(new Object[] {
				taoMaLoTam(i),
				tableModel.getValueAt(i, 0),
				tableModel.getValueAt(i, 1),
				tableModel.getValueAt(i, 2),
				tableModel.getValueAt(i, 3),
				tableModel.getValueAt(i, 4) });
		}

		JTable table = new JTable(previewModel);
		table.setRowHeight(32);
		table.getTableHeader().setReorderingAllowed(false);
		table.setBackground(Colors.BACKGROUND);
		table.setGridColor(Colors.BORDER_LIGHT);
		table.setFont(FontStyle.font(FontStyle.SM, FontStyle.NORMAL));
		table.getTableHeader().setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		table.setShowHorizontalLines(true);
		table.setShowVerticalLines(false);
		return table;
	}

	private String taoMaLoTam(int rowIndex) {
		return new SimpleDateFormat("'LO'yyyyMMdd").format(new java.util.Date()) + String.format("-%02d", rowIndex + 1);
	}

	private String docNhaCungCapDangChon() {
		Object selectedItem = cboNCC == null ? null : cboNCC.getSelectedItem();
		return selectedItem == null ? "Chưa chọn" : selectedItem.toString().trim();
	}

	private String docNgayNhapDangChon() {
		java.util.Date selectedDate = dtcNgayNhap == null ? null : dtcNgayNhap.getDate();
		if (selectedDate == null) {
			return "Chưa có ngày nhập";
		}
		return new SimpleDateFormat("dd/MM/yyyy").format(selectedDate);
	}

	private String docGhiChuDangNhap() {
		String ghiChu = txaGhiChu == null ? "" : txaGhiChu.getText();
		if (ghiChu == null || ghiChu.trim().isEmpty()) {
			return "Không có ghi chú";
		}
		return ghiChu.trim();
	}

	private String escapeHtml(String value) {
		if (value == null || value.isEmpty()) {
			return "";
		}
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
				.replace("\n", "<br>");
	}

	private void loaiBoToanBoPhieuNhap() {
		if (tblSelected != null && tblSelected.isEditing()) {
			TableCellEditor cellEditor = tblSelected.getCellEditor();
			if (cellEditor != null) {
				cellEditor.stopCellEditing();
			}
		}
		tableModel.setRowCount(0);
		selectedFile = null;
		selectedFileName = null;
		xoaThongTinPhieuNhap();
		btnChinhSuaThongTin.setEnabled(false);
		setCheDoChinhSuaThongTin(false);
		duaPanelFileVeTrangThaiBanDau();
	}

	private void xoaThongTinPhieuNhap() {
		if (cboNCC.getItemCount() > 0) {
			cboNCC.setSelectedIndex(-1);
		} else {
			cboNCC.setSelectedItem(null);
		}
		dtcNgayNhap.setDate(null);
		txaGhiChu.setText("");
	}

	private void duaPanelFileVeTrangThaiBanDau() {
		capNhatPanelFile(true);
	}

	private void capNhatPanelFile(boolean trangThaiBanDau) {
		SwingUtilities.invokeLater(() -> {
			int uploadIndex = pnlUpload == null ? -1 : pnlContentTopLeft.getComponentZOrder(pnlUpload);
			if (pnlUpload != null) {
				pnlContentTopLeft.remove(pnlUpload);
			}
			if (uploadIndex < 0 || uploadIndex > pnlContentTopLeft.getComponentCount()) {
				uploadIndex = 4;
			}
			pnlContentTopLeft.add(createUploadPanel(trangThaiBanDau), uploadIndex);
			pnlContentTopLeft.revalidate();
			pnlContentTopLeft.repaint();
		});
	}

	private void updateSummary() {
		int soMatHang = tableModel.getRowCount();
		int tongSoLuong = 0;
		double tongTien = 0;

		for (int i = 0; i < tableModel.getRowCount(); i++) {
			Object qtyObj = tableModel.getValueAt(i, 1);
			Object totalObj = tableModel.getValueAt(i, 4);
			tongSoLuong += docSoNguyen(qtyObj);
			tongTien += docSoThuc(totalObj);
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
			pnlUpload.setPreferredSize(new Dimension(0, 130));
			pnlUpload.add(Box.createVerticalGlue());
			pnlUpload.add(lblSubTitle6 = new JLabel((selectedFileName != null ? selectedFileName : "Đã tải file thành công!")));
			lblSubTitle6.setFont(FontStyle.font(FontStyle.BASE, FontStyle.BOLD));
			lblSubTitle6.setForeground(Colors.SUCCESS);
			lblSubTitle6.setAlignmentX(CENTER_ALIGNMENT);
			pnlUpload.add(Box.createVerticalStrut(8));
			JPanel pnlFileActions = new JPanel();
			pnlFileActions.setOpaque(false);
			pnlFileActions.setLayout(new BoxLayout(pnlFileActions, BoxLayout.X_AXIS));
			pnlFileActions.setAlignmentX(CENTER_ALIGNMENT);
			JButton btnApDungFile = taoNutTacVuFile("Áp dụng file", Colors.PRIMARY, Colors.BACKGROUND);
			btnApDungFile.addActionListener(e -> {
				if (selectedFile != null) {
					xuLyFile(selectedFile);
				}
			});
			JButton btnLoaiBoFile = taoNutTacVuFile("Loại bỏ file", Colors.DANGER, Colors.BACKGROUND);
			btnLoaiBoFile.addActionListener(e -> loaiBoToanBoPhieuNhap());
			pnlFileActions.add(btnApDungFile);
			pnlFileActions.add(Box.createHorizontalStrut(8));
			pnlFileActions.add(btnLoaiBoFile);
			pnlUpload.add(pnlFileActions);
			pnlUpload.add(Box.createVerticalStrut(8));

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

	private JButton taoNutTacVuFile(String text, Color background, Color foreground) {
		JButton button = new JButton(text);
		button.setFont(FontStyle.font(FontStyle.SM, FontStyle.BOLD));
		button.setFocusPainted(false);
		button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
		button.setBackground(background);
		button.setForeground(foreground);
		button.setOpaque(true);
		button.setContentAreaFilled(true);
		button.setBorderPainted(false);
		return button;
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
		try {
			PhieuNhap_Service.DuLieuNhapHang duLieuNhapHang = phieuNhapService.taiDuLieuNhapHang(file);
			selectedFile = file;
			danhSachChiTiet = duLieuNhapHang.getChiTietPhieuNhaps();
			tableModel.setRowCount(0);
			for (ChiTietPhieuNhap ct : danhSachChiTiet) {
				addToRow(ct);
			}
			capNhatThongTinTuFile(duLieuNhapHang);
			btnChinhSuaThongTin.setEnabled(true);
			setCheDoChinhSuaThongTin(false);

			selectedFileName = file.getName();
			capNhatPanelFile(false);
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Lỗi tải file", JOptionPane.ERROR_MESSAGE);
		}
	}

	private void capNhatThongTinTuFile(PhieuNhap_Service.DuLieuNhapHang duLieuNhapHang) {
		capNhatNhaCungCap(duLieuNhapHang.getTenNhaCungCap());
		if (duLieuNhapHang.getNgayNhap() != null) {
			dtcNgayNhap.setDate(Date.valueOf(duLieuNhapHang.getNgayNhap()));
		} else {
			dtcNgayNhap.setDate(null);
		}
		txaGhiChu.setText(duLieuNhapHang.getGhiChu() == null ? "" : duLieuNhapHang.getGhiChu().trim());
	}

	private void capNhatNhaCungCap(String tenNhaCungCap) {
		if (tenNhaCungCap == null || tenNhaCungCap.trim().isEmpty()) {
			if (cboNCC.getItemCount() > 0) {
				cboNCC.setSelectedIndex(-1);
			} else {
				cboNCC.setSelectedItem(null);
			}
			return;
		}

		String tenCanChon = tenNhaCungCap.trim();
		for (int i = 0; i < cboNCC.getItemCount(); i++) {
			String item = cboNCC.getItemAt(i);
			if (item != null && item.equalsIgnoreCase(tenCanChon)) {
				cboNCC.setSelectedIndex(i);
				return;
			}
		}

		cboNCC.addItem(tenCanChon);
		cboNCC.setSelectedItem(tenCanChon);
	}

//	Lấy tên file đã chọn từ bên ngoài class
	public String getSelectedFileName() {
	    return selectedFileName;
	}

	// ==================== LƯU PHIẾU NHẬP VÀO DB ====================

	// Xử lý khi nhấn "Xác nhận nhập":
	// 1. Tạo PhieuNhap → lưu DB
	// 2. Tạo ChiTietPhieuNhap cho mỗi dòng → lưu DB
	// 3. Tạo LoSanPham cho mỗi dòng (quy đổi đơn vị + tính HSD) → lưu DB
	private void xuLyLuuPhieuNhap() {
		try {
			// 1. Đọc thông tin phiếu từ form
			String tenNCC = docNhaCungCapDangChon();
			NhaCungCap ncc = nhaCungCapService.layNhaCungCapTheoTen(tenNCC);
			if (ncc == null) {
				if (errNCC != null) { errNCC.setText("✗ Vui lòng chọn nhà cung cấp."); errNCC.setVisible(true); }
				return;
			}

			LocalDate ngayNhap = docNgayNhapLocalDate();
			if (ngayNhap == null) {
				ngayNhap = LocalDate.now();
			}

			// 2. Sinh mã phiếu nhập tự động
			String maPN = phieuNhapService.sinhMaPhieuNhap(phieuNhapService.sinhPrefixHomNay());

			// 3. Tạo PhieuNhap
			PhieuNhap pn = new PhieuNhap(maPN, ngayNhap, ncc, new NhanVien("NV001"), docGhiChuDangNhap());

			// 4. Lưu chi tiết + tạo lô sản phẩm qua Service
			// Đồng bộ số lượng từ bảng vào danh sách
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				if (i < danhSachChiTiet.size()) {
					int soLuongMoi = docSoNguyen(tableModel.getValueAt(i, 1));
					danhSachChiTiet.get(i).setSoLuong(soLuongMoi);
				}
			}
			KeSanPham keSP = new KeSanPham("KSP2026010"); // Kệ hàng mới nhập
			int soLoTao = phieuNhapService.luuPhieuNhapVaChiTiet(pn, danhSachChiTiet, keSP);
			if (soLoTao < 0) {
				JOptionPane.showMessageDialog(this, "Không thể tạo phiếu nhập.", "Lỗi", JOptionPane.ERROR_MESSAGE);
				return;
			}

			JOptionPane.showMessageDialog(this,
				"Đã lưu phiếu nhập " + maPN + "\n"
				+ "Tạo thành công " + soLoTao + " lô sản phẩm.",
				"Thành công", JOptionPane.INFORMATION_MESSAGE);

			loaiBoToanBoPhieuNhap();

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this,
				"Lỗi khi lưu phiếu nhập: " + ex.getMessage(),
				"Lỗi", JOptionPane.ERROR_MESSAGE);
			ex.printStackTrace();
		}
	}

	private LocalDate docNgayNhapLocalDate() {
		java.util.Date d = dtcNgayNhap == null ? null : dtcNgayNhap.getDate();
		if (d == null) return null;
		return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}
}
