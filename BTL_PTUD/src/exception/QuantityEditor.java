package exception;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.TableCellEditor;

public class QuantityEditor extends AbstractCellEditor implements TableCellEditor {
    private JPanel panel = new JPanel();
    private JButton btnMinus = new JButton("-");
    private JTextField txtValue = new JTextField("0", 3); 
    private JButton btnPlus = new JButton("+");

    private int value;
    private JTable table;
    private int row;

    public QuantityEditor() {
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 3, 0));

//      Định dạng txtValue
        txtValue.setHorizontalAlignment(JTextField.CENTER);
        txtValue.setPreferredSize(new Dimension(40, 24));
        txtValue.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        panel.add(btnMinus);
        panel.add(txtValue);
        panel.add(btnPlus);

//      Nút -
        btnMinus.addActionListener(e -> {
            syncFromField();
            if (value > 1) {
                value--;
                updateValue();
            }
        });

//      Nút +
        btnPlus.addActionListener(e -> {
            syncFromField();
            value++;
            updateValue();
        });

        txtValue.addActionListener(e -> {
            syncFromField();
            updateValue();
        });

        txtValue.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                syncFromField();
                updateValue();
            }
        });
    }

    private void syncFromField() {
        try {
            int v = Integer.parseInt(txtValue.getText().trim());
            value = Math.max(1, v);
        } catch (NumberFormatException ex) {
            value = 1;
        }
    }

    private void updateValue() {
        txtValue.setText(String.valueOf(value));

        if (table != null) {
            table.setValueAt(value, row, 1);

            Object priceObj = table.getValueAt(row, 2);
            if (priceObj instanceof Integer) {
                int price = (Integer) priceObj;
                table.setValueAt(price * value, row, 3);
            }
        }
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        this.table = table;
        this.row = row;
        this.value = (value instanceof Integer) ? (Integer) value : 1;
        txtValue.setText(String.valueOf(this.value));
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        syncFromField();
        return value;
    }
}
