/*
 * @(#)TreeTableModelAdapter.java        1.2 98/10/27
 *
 * Copyright 1997, 1998 by Sun Microsystems, Inc.,
 * 901 San Antonio Road, Palo Alto, California, 94303, U.S.A.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of Sun Microsystems, Inc. ("Confidential Information").  You
 * shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with Sun.
 */
package org.ekfliu.gmusic.deduplicate.ui.treetable;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

/**
 * This is a wrapper class takes a TreeTableModel and implements the table model
 * interface. The implementation is trivial, with all of the event dispatching
 * support provided by the superclass: the AbstractTableModel.
 *
 * @version 1.2 10/27/98
 * @author Philip Milne
 * @author Scott Violet
 */
public class TreeTableModelAdapter extends AbstractTableModel implements SortableTableModel {
    private static final long serialVersionUID = 1L;
    private JTree tree;
    private TreeTableModel treeTableModel;

    public TreeTableModelAdapter(final TreeTableModel treeTableModel, final JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;
        tree.addTreeExpansionListener(new TreeExpansionListener() {
                // Don't use fireTableRowsInserted() here; the selection model
                // would get updated twice.
                @Override
                public void treeExpanded(final TreeExpansionEvent event) {
                    fireTableDataChanged();
                }
                @Override
                public void treeCollapsed(final TreeExpansionEvent event) {
                    fireTableDataChanged();
                }
            });
        // Install a TreeModelListener that can update the table when
        // tree changes. We use delayedFireTableDataChanged as we can
        // not be guaranteed the tree will have finished processing
        // the event before us.
        treeTableModel.addTreeModelListener(new TreeModelListener() {
                @Override
                public void treeNodesChanged(final TreeModelEvent e) {
                    delayedFireTableDataChanged();
                }
                @Override
                public void treeNodesInserted(final TreeModelEvent e) {
                    delayedFireTableDataChanged();
                }
                @Override
                public void treeNodesRemoved(final TreeModelEvent e) {
                    delayedFireTableDataChanged();
                }
                @Override
                public void treeStructureChanged(final TreeModelEvent e) {
                    delayedFireTableDataChanged();
                }
            });
    }

    // Wrappers, implementing TableModel interface.
    @Override
    public int getColumnCount() {
        return treeTableModel.getColumnCount();
    }
    @Override
    public String getColumnName(final int column) {
        return treeTableModel.getColumnName(column);
    }
    @Override
    public Class getColumnClass(final int column) {
        return treeTableModel.getColumnClass(column);
    }
    @Override
    public int getRowCount() {
        return tree.getRowCount();
    }
    protected Object nodeForRow(final int row) {
        TreePath treePath = tree.getPathForRow(row);

        return treePath.getLastPathComponent();
    }
    @Override
    public Object getValueAt(final int row, final int column) {
        return treeTableModel.getValueAt(nodeForRow(row), column);
    }
    @Override
    public boolean isCellEditable(final int row, final int column) {
        return treeTableModel.isCellEditable(nodeForRow(row), column);
    }
    @Override
    public void setValueAt(final Object value, final int row, final int column) {
        treeTableModel.setValueAt(value, nodeForRow(row), column);
    }
    /**
     * Invokes fireTableDataChanged after all the pending events have been
     * processed. SwingUtilities.invokeLater is used to handle this.
     */
    protected void delayedFireTableDataChanged() {
        SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    fireTableDataChanged();
                }
            });
    }
    @Override
    public boolean isDescend() {
        if (treeTableModel instanceof SortableTableModel) {
            return ((SortableTableModel) treeTableModel).isDescend();
        } else {
            return false;
        }
    }
    @Override
    public int getSortColumn() {
        if (treeTableModel instanceof SortableTableModel) {
            return ((SortableTableModel) treeTableModel).getSortColumn();
        } else {
            return -1;
        }
    }
    @Override
    public boolean isColumnSortable(final int aColumnIndex) {
        if (treeTableModel instanceof SortableTableModel) {
            return ((SortableTableModel) treeTableModel).isColumnSortable(aColumnIndex);
        } else {
            return false;
        }
    }
}
