package org.ekfliu.gmusic.deduplicate;

import gmusic.api.model.Song;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.ekfliu.gmusic.deduplicate.model.PreserveFunction;

public class CreationDatePreserveCalculator implements PreserveCalculator {
    private static final Comparator<Song> CREATE_DATE_COMPARATOR =
        new Comparator<Song>() {
            @Override
            public int compare(final Song aO1, final Song aO2) {
                return Long.valueOf(aO1.getCreationDate()).compareTo(Long.valueOf(aO2.getCreationDate()));
            }
        };

    private PreserveFunction preserveFunction;

    public CreationDatePreserveCalculator(final PreserveFunction aPreserveFunction) {
        super();
        preserveFunction = aPreserveFunction;
    }

    @Override
    public void processDuplicateList(final List<Song> aSongList) {
        if (preserveFunction == PreserveFunction.Newest) {
            Collections.sort(aSongList, CREATE_DATE_COMPARATOR);
        } else if (preserveFunction == PreserveFunction.Oldest) {
            Collections.sort(aSongList, Collections.reverseOrder(CREATE_DATE_COMPARATOR));
        }
    }
}
