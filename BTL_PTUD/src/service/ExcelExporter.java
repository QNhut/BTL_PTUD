package service;

import java.awt.Component;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.TableModel;

// Xuất nội dung JTable đang hiển thị ra file CSV (mở được bằng Excel).
public class ExcelExporter {

    private static String escapeCsv(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    public static void xuatTable(Component parent, String tieuDe, String tenSheet,
                                 String tenFileGoiY, JTable table) {
        if (table == null) return;
        TableModel model = table.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parent, "Không có dữ liệu để xuất.",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu báo cáo CSV");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV (*.csv)", "csv"));
        chooser.setSelectedFile(new File(tenFileGoiY + ".csv"));
        if (chooser.showSaveDialog(parent) != JFileChooser.APPROVE_OPTION) return;
        File f = chooser.getSelectedFile();
        if (!f.getName().toLowerCase().endsWith(".csv")) {
            f = new File(f.getParentFile(), f.getName() + ".csv");
        }

        try (PrintWriter pw = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(f), StandardCharsets.UTF_8))) {
            // BOM để Excel nhận đúng UTF-8
            pw.print('\uFEFF');

            int colCount = model.getColumnCount();

            // Tiêu đề
            if (tieuDe != null && !tieuDe.isEmpty()) {
                pw.println(escapeCsv(tieuDe));
                pw.println();
            }

            // Header
            StringBuilder header = new StringBuilder();
            for (int c = 0; c < colCount; c++) {
                if (c > 0) header.append(',');
                header.append(escapeCsv(model.getColumnName(c)));
            }
            pw.println(header);

            // Data rows
            for (int r = 0; r < model.getRowCount(); r++) {
                StringBuilder row = new StringBuilder();
                for (int c = 0; c < colCount; c++) {
                    if (c > 0) row.append(',');
                    Object v = model.getValueAt(r, c);
                    row.append(escapeCsv(v == null ? "" : v.toString()));
                }
                pw.println(row);
            }

            JOptionPane.showMessageDialog(parent,
                    "Đã xuất báo cáo:\n" + f.getAbsolutePath(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(parent,
                    "Lỗi khi xuất CSV: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }
}
