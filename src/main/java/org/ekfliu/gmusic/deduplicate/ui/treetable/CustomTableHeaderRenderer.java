package org.ekfliu.gmusic.deduplicate.ui.treetable;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.plaf.UIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class CustomTableHeaderRenderer extends DefaultTableCellRenderer implements UIResource {
    private static final long serialVersionUID = 1L;
    private Border raisedBorder;
    private Border loweredBorder;
    private int currentColumn = -1;
    private Icon sortUpArrow;
    private Icon sortDownArrow;
    private SortableTableModel model;

    public int getCurrentColumn() {
        return currentColumn;
    }
    public void setCurrentColumn(int currentColumn) {
        this.currentColumn = currentColumn;
    }

    public CustomTableHeaderRenderer(SortableTableModel model, Icon sortUp, Icon sortDown) {
        raisedBorder = BorderFactory.createBevelBorder(BevelBorder.RAISED);
        loweredBorder = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
        setHorizontalAlignment(JLabel.CENTER);
        this.sortDownArrow = sortDown;
        this.sortUpArrow = sortUp;
        this.model = model;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        if (table != null) {
            JTableHeader header = table.getTableHeader();

            if (header != null) {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
            }

            if (model.getSortColumn() == column) {
                if (model.isDescend()) {
                    setIcon(sortUpArrow);
                } else {
                    setIcon(sortDownArrow);
                }
            } else {
                setIcon(null);
            }
        }

        setText((value == null) ? "" : value.toString());

        // setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        if (currentColumn == column) {
            setBorder(loweredBorder);
        } else {
            setBorder(raisedBorder);
        }

        return this;
    }
}
