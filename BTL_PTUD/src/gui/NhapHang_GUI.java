package gui;

import java.awt.BorderLayout;
import java.awt.Color;
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
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.filechooser.FileNameExtensionFilter;

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
	private JPanel pnlSubTitle;
	private JLabel lblSubTitle;
	private JLabel lblSubTitle2;
	private JPanel pnlUpload;
	private JLabel lblSubTitle3;
	private JLabel lblSubTitle4;
	private JLabel lblSubTitle5;
	private JLabel lblSubTitle6;

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
		pnlContentLeft.add(pnlContentTopLeft = new JPanel(), BorderLayout.NORTH);
		
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
		pnlContentTopLeft.add(Box.createVerticalStrut(20));
		pnlContentTopLeft.add(createUploadPanel(true));
		
		
//		Phần nội dung bên phải chứ thông tin phiếu nhập hàng
		pnlContent.add(pnlContentRight = new JPanel(), BorderLayout.EAST);
		pnlContentRight.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Colors.MUTED, 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)
		));
		
		pnlContentRight.setPreferredSize(new Dimension(350, 0));
		pnlContentRight.setMinimumSize(new Dimension(350, 0));
		pnlContentRight.setMaximumSize(new Dimension(450, Integer.MAX_VALUE));
		
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
			pnlUpload.setPreferredSize(new Dimension(0, 200));
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
