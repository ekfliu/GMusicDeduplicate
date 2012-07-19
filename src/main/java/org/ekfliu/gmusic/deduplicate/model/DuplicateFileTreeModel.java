package org.ekfliu.gmusic.deduplicate.model;

import gmusic.api.model.Song;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Map;

import javax.swing.tree.TreeSelectionModel;

import org.ekfliu.gmusic.deduplicate.ui.treetable.AbstractTreeTableModel;
import org.ekfliu.gmusic.deduplicate.ui.treetable.SortableTableModel;
import org.ekfliu.gmusic.deduplicate.ui.treetable.TreeTableModel;

public class DuplicateFileTreeModel extends AbstractTreeTableModel implements TreeTableModel, SortableTableModel, PropertyChangeListener {
    protected static String[] cNames =
        {
            "Title",
            "Artist",
            "Album",
            "Album Artist",
            "Genre",
            "Composer",
            "Year",
            "Rating",
            "Play Count",
            "Duration",
            "Name",
            "Id"
        };
    protected static Class[] cTypes =
        {
            TreeTableModel.class,
            String.class,
            String.class,
            String.class,
            String.class,
            String.class,
            Integer.class,
            Integer.class,
            Integer.class,
            Long.class,
            String.class,
            String.class
        };
    protected TreeSelectionModel selectionModel;
    public static final Integer ZERO = new Integer(0);
    private int sortColumn = -1;
    private boolean descend = false;

    public DuplicateFileTreeModel() {
        super(new GMusicRootRowNode());
    }

    public void updateNewDuplicateMap(final Map<String, List<Song>> aDuplicateMap) {
        ((GMusicRootRowNode) getRoot()).updateNewDuplicateMap(aDuplicateMap);

        SongTreeTableRowNode root = (SongTreeTableRowNode) getRoot();
        fireTreeStructureChanged(root, root.getPath(), null, null);
    }
    @Override
    public Object getChild(final Object aParent, final int aIndex) {
        return ((SongTreeTableRowNode) aParent).getChild(aIndex);
    }
    @Override
    public int getChildCount(final Object aParent) {
        return ((SongTreeTableRowNode) aParent).getChildCount();
    }
    @Override
    public void propertyChange(final PropertyChangeEvent aEvt) {
        String propName = aEvt.getPropertyName();

        if ("sortColumn".equals(propName)) {
            Integer newValue = (Integer) aEvt.getNewValue();

            if (newValue.intValue() == sortColumn) {
                descend = !descend;
            }

            sortColumn = newValue.intValue();

            final SongTreeTableRowNode root = (SongTreeTableRowNode) getRoot();

            switch (sortColumn) {
                /*
                 * case 0: UtilHelper.sortTreeTableRowNode(root.getChild(),
                 * UtilHelper.Type.Path, descend); for (TreeTableRowNode eachChild :
                 * root.getChild()) {
                 * UtilHelper.sortTreeTableRowNode(eachChild.getChild(),
                 * UtilHelper.Type.Path, descend); } break; case 1:
                 * UtilHelper.sortTreeTableRowNode(root.getChild(),
                 * UtilHelper.Type.Name, descend); for (TreeTableRowNode eachChild :
                 * root.getChild()) {
                 * UtilHelper.sortTreeTableRowNode(eachChild.getChild(),
                 * UtilHelper.Type.Name, descend); } break; case 2:
                 * UtilHelper.sortTreeTableRowNode(root.getChild(),
                 * UtilHelper.Type.Size, descend); for (TreeTableRowNode eachChild :
                 * root.getChild()) {
                 * UtilHelper.sortTreeTableRowNode(eachChild.getChild(),
                 * UtilHelper.Type.Size, descend); } break; case 4:
                 * UtilHelper.sortTreeTableRowNode(root.getChild(),
                 * UtilHelper.Type.Comments, descend); for (TreeTableRowNode
                 * eachChild : root.getChild()) {
                 * UtilHelper.sortTreeTableRowNode(eachChild.getChild(),
                 * UtilHelper.Type.Comments, descend); } break; default: return;
                 */
            }

            root.reorder();
            fireCascadeTreeNodesChanged(root);
        }
    }
    protected void fireCascadeTreeNodesChanged(final SongTreeTableRowNode aNode) {
        final int size = aNode.getChildCount();

        if (size == 0) {
            return;
        }

        final int[] indices = new int[size];

        for (int i = 0; i < size; i++) {
            indices[i] = i;
        }

        final SongTreeTableRowNode[] elements = aNode.getChildList().toArray(new SongTreeTableRowNode[size]);
        fireTreeNodesChanged(DuplicateFileTreeModel.this, aNode.getPath(), indices, elements);

        for (SongTreeTableRowNode eachChild : elements) {
            fireCascadeTreeNodesChanged(eachChild);
        }
    }
    @Override
    public boolean isDescend() {
        return descend;
    }
    @Override
    public int getSortColumn() {
        return sortColumn;
    }
    @Override
    public boolean isColumnSortable(final int aColumnIndex) {
        return false;
    }
    @Override
    public int getColumnCount() {
        return cNames.length;
    }
    @Override
    public String getColumnName(final int aColumn) {
        return cNames[aColumn];
    }
    @Override
    public Class getColumnClass(final int aColumn) {
        return cTypes[aColumn];
    }
    @Override
    public Object getValueAt(final Object aNode, final int aColumn) {
        final SongTreeTableRowNode item = (SongTreeTableRowNode) aNode;

        switch (aColumn) {
            case 0:
                // this is not used, see toString() method for actual object
                return "";

            case 1:
                return item.getArtist();

            case 2:
                return item.getAlbum();

            case 3:
                return item.getAlbumArtist();

            case 4:
                return item.getGenre();

            case 5:
                return item.getComposer();

            case 6:
                return item.getYear();

            case 7:
                return item.getRating();

            case 8:
                return item.getPlayCount();

            case 9:
                return item.getDurationMillis();

            case 10:
                return item.getName();

            case 11:
                return item.getId();
        }

        return null;
    }
    @Override
    public void setSelectionModel(final TreeSelectionModel aSelectionModel) {
        selectionModel = aSelectionModel;
    }
}
