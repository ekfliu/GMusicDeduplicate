package org.ekfliu.gmusic.deduplicate.ui.treetable;

import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

public class ResizeableTableHeader extends JTableHeader implements TableModelListener {
    private static final long serialVersionUID = 1L;
    protected boolean autoResizingEnabled;
    protected boolean includeHeaderWidth;
    protected boolean resizeVisibleOnly;

    public ResizeableTableHeader() {
        this(null);
    }
    public ResizeableTableHeader(final TableColumnModel cm) {
        super(cm);
        autoResizingEnabled = false;
        includeHeaderWidth = false;
        resizeVisibleOnly = false;
        addMouseListener(new ResizingMouseAdapter());
        addMouseListener(new TableHeaderListener());
    }

    @Override
    public void setTable(final JTable table) {
        JTable old = this.table;

        if (table != old) {
            if ((old != null) && (old.getModel() != null)) {
                old.getModel().removeTableModelListener(this);
            }

            if ((table != null) && (table.getModel() != null)) {
                table.getModel().addTableModelListener(this);
            }
        }

        this.table = table;
        firePropertyChange("table", old, table);
    }
    public void setAutoResizingEnabled(final boolean autoResizingEnabled) {
        boolean old = this.autoResizingEnabled;
        this.autoResizingEnabled = autoResizingEnabled;
        firePropertyChange("autoResizingEnabled", old, autoResizingEnabled);
    }
    public boolean getAutoResizingEnabled() {
        return autoResizingEnabled;
    }
    public void setIncludeHeaderWidth(final boolean includeHeaderWidth) {
        boolean old = this.includeHeaderWidth;
        this.includeHeaderWidth = includeHeaderWidth;
        firePropertyChange("includeHeaderWidth", old, includeHeaderWidth);
    }
    public boolean isResizeVisibleOnly() {
        return resizeVisibleOnly;
    }
    public void setResizeVisibleOnly(final boolean resizeVisibleOnly) {
        boolean old = resizeVisibleOnly;
        this.resizeVisibleOnly = resizeVisibleOnly;
        firePropertyChange("resizeVisibleOnly", old, resizeVisibleOnly);
    }
    public boolean getIncludeHeaderWidth() {
        return includeHeaderWidth;
    }
    public void resizeColumn(final TableColumn column) {
        if (column != null) {
            adjustColumnWidth(column);
        }
    }
    public void resizeAllColumns() {
        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);
            adjustColumnWidth(column);
        }
    }
    public void setColumnWidths(final TableColumn column, final int preferredWidth, final int minWidth, final int maxWidth) {
        if (column != null) {
            if (preferredWidth != -1) {
                column.setPreferredWidth(preferredWidth);
            }

            if (minWidth != -1) {
                column.setMinWidth(minWidth);
            }

            if (maxWidth != -1) {
                column.setMaxWidth(maxWidth);
            }
        }
    }
    public void setAllColumnWidths(final int preferredWidth, final int minWidth, final int maxWidth) {
        for (int col = 0; col < table.getColumnCount(); col++) {
            TableColumn column = table.getColumnModel().getColumn(col);
            setColumnWidths(column, preferredWidth, minWidth, maxWidth);
        }
    }
    @Override
    public void tableChanged(final TableModelEvent e) {
        if (getAutoResizingEnabled()) {
            if (e.getType() == TableModelEvent.DELETE) {
                resizeAllColumns();
            } else {
                // for performance reason only adjust width, if
                // one of the new cell is greater than the column width.
                for (int col = 0; col < table.getColumnCount(); col++) {
                    TableColumn column = table.getColumnModel().getColumn(col);

                    if (canResize(column)) {
                        int width = column.getPreferredWidth();

                        for (int row = e.getFirstRow(); (row <= e.getLastRow()) && (row < table.getRowCount()) && (row != -1); row++) {
                            TableCellRenderer renderer = table.getCellRenderer(row, col);
                            Component comp = renderer.getTableCellRendererComponent(table, table.getValueAt(row, col), false, false, row, col);
                            width = Math.max(width, comp.getPreferredSize().width + table.getColumnModel().getColumnMargin());
                        }

                        column.setPreferredWidth(width);
                    }
                }
            }
        }
    }
    private void adjustColumnWidth(final TableColumn column) {
        int width = 0;
        int col = table.convertColumnIndexToView(column.getModelIndex());

        // Determine width of header.
        if (includeHeaderWidth) {
            TableCellRenderer headerRenderer = column.getHeaderRenderer();

            if (headerRenderer == null) {
                headerRenderer = table.getTableHeader().getDefaultRenderer();
            }

            Component headerComp = headerRenderer.getTableCellRendererComponent(table, column.getHeaderValue(), false, false, 0, col);
            width = Math.max(width, headerComp.getPreferredSize().width);
        }

        int startIndex = 0;
        int endIndex = table.getRowCount();

        if (resizeVisibleOnly) {
            Rectangle visible = table.getVisibleRect();
            int offset = table.getY();
            Point start = new Point(1, 1 - offset);
            Point end = new Point(1, (int) visible.getHeight() - 1 - offset);
            startIndex = table.rowAtPoint(start);
            endIndex = table.rowAtPoint(end) + 1;

            if (endIndex == 0) {
                endIndex = -1;
            }
        }

        // Determine max width of cells.
        for (int row = startIndex; row < endIndex; row++) {
            TableCellRenderer renderer = table.getCellRenderer(row, col);
            Component comp = renderer.getTableCellRendererComponent(table, table.getValueAt(row, col), false, false, row, col);
            width = Math.max(width, comp.getPreferredSize().width);
        }

        // add columnmargins
        width += table.getColumnModel().getColumnMargin();
        column.setPreferredWidth(width);
    }
    private boolean canResize(final TableColumn column) {
        return (column != null) && getResizingAllowed() && column.getResizable();
    }
    private TableColumn getResizingColumn(final Point p) {
        return getResizingColumn(p, columnAtPoint(p));
    }
    private TableColumn getResizingColumn(final Point p, final int column) {
        if (column == -1) {
            return null;
        }

        Rectangle r = getHeaderRect(column);
        r.grow(-3, 0);

        if (r.contains(p)) {
            return null;
        }

        int midPoint = r.x + (r.width / 2);
        int columnIndex;

        if (getComponentOrientation().isLeftToRight()) {
            columnIndex = (p.x < midPoint) ? (column - 1) : column;
        } else {
            columnIndex = (p.x < midPoint) ? column : (column - 1);
        }

        if (columnIndex == -1) {
            return null;
        }

        return getColumnModel().getColumn(columnIndex);
    }

    private class ResizingMouseAdapter extends MouseAdapter {
        @Override
        public void mouseClicked(final MouseEvent me) {
            if ((me.getClickCount() == 2) && (me.getButton() == MouseEvent.BUTTON1)) {
                TableColumn resizingColumn = getResizingColumn(me.getPoint());

                if (canResize(resizingColumn)) {
                    adjustColumnWidth(resizingColumn);
                }
            }
        }
    }

    private int getSortingColumn(final Point point) {
        int columnIndex = columnAtPoint(point);
        Rectangle r = getHeaderRect(columnIndex);
        r.grow(-3, 0);

        if (r.contains(point)) {
            return columnIndex;
        } else {
            return -1;
        }
    }

    private class TableHeaderListener extends MouseAdapter {
        private int downColumn;
        private int upColumn;

        @Override
        public void mousePressed(final MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                if (SortableTableModel.class.isAssignableFrom(getTable().getModel().getClass())) {
                    Point where = event.getPoint();
                    CustomTableHeaderRenderer renderer = (CustomTableHeaderRenderer) getDefaultRenderer();
                    downColumn = getSortingColumn(where);

                    if (((SortableTableModel) getTable().getModel()).isColumnSortable(downColumn)) {
                        renderer.setCurrentColumn(downColumn);
                        repaint();
                    }
                }
            }
        }
        @Override
        public void mouseReleased(final MouseEvent event) {
            if (event.getButton() == MouseEvent.BUTTON1) {
                if (SortableTableModel.class.isAssignableFrom(getTable().getModel().getClass())) {
                    Point where = event.getPoint();
                    CustomTableHeaderRenderer renderer = (CustomTableHeaderRenderer) getDefaultRenderer();
                    upColumn = getSortingColumn(where);

                    if (((SortableTableModel) getTable().getModel()).isColumnSortable(downColumn)) {
                        if (downColumn == upColumn) {
                            firePropertyChange("sortColumn", -1, upColumn);
                        }

                        renderer.setCurrentColumn(-1);
                        repaint();
                    }
                }
            }
        }
    }
}
