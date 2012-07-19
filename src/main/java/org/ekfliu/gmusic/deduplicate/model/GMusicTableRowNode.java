package org.ekfliu.gmusic.deduplicate.model;

import gmusic.api.model.Song;

import java.util.ArrayList;
import java.util.List;

public class GMusicTableRowNode extends AbstractTreeTableRowNode implements SongTreeTableRowNode {
    private SongTreeTableRowNode parent;
    private List<SongTreeTableRowNode> childList = new ArrayList<SongTreeTableRowNode>();
    private Song songData;

    public GMusicTableRowNode(final SongTreeTableRowNode aParent, final Song aKeepSong, final List<Song> aDeleteList) {
        parent = aParent;
        songData = aKeepSong;

        if (aDeleteList != null) {
            for (Song current : aDeleteList) {
                childList.add(new GMusicTableRowNode(this, current, null));
            }
        }
    }

    @Override
    public void reorder() {
    }
    @Override
    public SongTreeTableRowNode getParent() {
        return parent;
    }
    @Override
    public List<?extends SongTreeTableRowNode> getChildList() {
        return childList;
    }
    @Override
    public String getGenre() {
        return songData.getGenre();
    }
    @Override
    public String getAlbum() {
        return songData.getAlbum();
    }
    @Override
    public String getId() {
        return songData.getId();
    }
    @Override
    public String getComposer() {
        return songData.getComposer();
    }
    @Override
    public String getTitle() {
        return songData.getTitle();
    }
    @Override
    public String getAlbumArtist() {
        return songData.getAlbumArtist();
    }
    @Override
    public int getYear() {
        return songData.getYear();
    }
    @Override
    public String getArtist() {
        return songData.getArtist();
    }
    @Override
    public long getDurationMillis() {
        return songData.getDurationMillis();
    }
    @Override
    public int getPlayCount() {
        return songData.getPlaycount();
    }
    @Override
    public int getRating() {
        return Integer.parseInt(songData.getRating());
    }
    @Override
    public String getName() {
        return songData.getName();
    }
    @Override
    public String toString() {
        return getTitle();
    }
}
