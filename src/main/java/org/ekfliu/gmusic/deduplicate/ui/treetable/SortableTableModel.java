package org.ekfliu.gmusic.deduplicate.ui.treetable;

public interface SortableTableModel {
    public boolean isDescend();
    public int getSortColumn();
    public boolean isColumnSortable(int columnIndex);
}
