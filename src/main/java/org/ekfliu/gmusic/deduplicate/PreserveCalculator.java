package org.ekfliu.gmusic.deduplicate;

import gmusic.api.model.Song;

import java.util.List;

public interface PreserveCalculator {
    void processDuplicateList(List<Song> songList);
}
