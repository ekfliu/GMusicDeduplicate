package org.ekfliu.gmusic.deduplicate.model;

public abstract class AbstractTreeTableRowNode implements SongTreeTableRowNode {
    public SongTreeTableRowNode[] getPath() {
        return getPathToRoot(this, 0);
    }
    protected SongTreeTableRowNode[] getPathToRoot(SongTreeTableRowNode aNode, int depth) {
        final SongTreeTableRowNode[] retNodes;

        if (aNode == null) {
            if (depth == 0) {
                retNodes = null;
            } else {
                retNodes = new SongTreeTableRowNode[depth];
            }
        } else {
            depth++;
            retNodes = getPathToRoot(aNode.getParent(), depth);
            retNodes[retNodes.length - depth] = aNode;
        }

        return retNodes;
    }
    @Override
    public int getChildCount() {
        return getChildList().size();
    }
    @Override
    public boolean isLeaf() {
        return getChildList().isEmpty();
    }
    @Override
    public SongTreeTableRowNode getChild(final int aI) {
        return getChildList().get(aI);
    }
}
