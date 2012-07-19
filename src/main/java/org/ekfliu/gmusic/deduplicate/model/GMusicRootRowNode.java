package org.ekfliu.gmusic.deduplicate.model;

import gmusic.api.model.Song;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GMusicRootRowNode extends AbstractTreeTableRowNode implements SongTreeTableRowNode {
    private List<SongTreeTableRowNode> childList = new ArrayList<SongTreeTableRowNode>();

    public GMusicRootRowNode() {
    }
    public GMusicRootRowNode(final Map<String, List<Song>> aDuplicateMap) {
        updateNewDuplicateMap(aDuplicateMap);
    }

    public void updateNewDuplicateMap(final Map<String, List<Song>> aDuplicateMap) {
        childList.clear();

        for (List<Song> duplicateList : aDuplicateMap.values()) {
            final Song keep = duplicateList.get(duplicateList.size() - 1);
            final List<Song> deleteList = duplicateList.subList(0, duplicateList.size() - 1);
            final GMusicTableRowNode keepNode = new GMusicTableRowNode(this, keep, deleteList);
            childList.add(keepNode);
        }
    }
    @Override
    public void reorder() {
        // TODO Auto-generated method stub
    }
    @Override
    public SongTreeTableRowNode getParent() {
        return null;
    }
    @Override
    public List<?extends SongTreeTableRowNode> getChildList() {
        return childList;
    }
    @Override
    public String getGenre() {
        return null;
    }
    @Override
    public String getAlbum() {
        return null;
    }
    @Override
    public String getId() {
        return null;
    }
    @Override
    public String getComposer() {
        return null;
    }
    @Override
    public String getTitle() {
        return null;
    }
    @Override
    public String getAlbumArtist() {
        return null;
    }
    @Override
    public int getYear() {
        return 0;
    }
    @Override
    public String getArtist() {
        return null;
    }
    @Override
    public long getDurationMillis() {
        return 0;
    }
    @Override
    public int getPlayCount() {
        return 0;
    }
    @Override
    public int getRating() {
        return 0;
    }
    @Override
    public String getName() {
        return null;
    }
    @Override
    public String toString() {
        return "Root";
    }
}
