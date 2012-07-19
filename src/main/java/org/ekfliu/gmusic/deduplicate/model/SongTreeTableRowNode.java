package org.ekfliu.gmusic.deduplicate.model;

import java.util.List;

public interface SongTreeTableRowNode {
    void reorder();
    int getChildCount();
    boolean isLeaf();
    SongTreeTableRowNode getParent();
    List<?extends SongTreeTableRowNode> getChildList();
    SongTreeTableRowNode getChild(int i);
    SongTreeTableRowNode[] getPath();
    String getGenre();
    String getAlbum();
    String getId();
    String getComposer();
    String getTitle();
    String getAlbumArtist();
    int getYear();
    String getArtist();
    long getDurationMillis();
    int getPlayCount();
    int getRating();
    String getName();
}
