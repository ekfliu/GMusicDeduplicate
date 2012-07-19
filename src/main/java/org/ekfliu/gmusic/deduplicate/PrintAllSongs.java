package org.ekfliu.gmusic.deduplicate;

import gmusic.api.comm.ApacheConnector;

import gmusic.api.impl.GoogleMusicAPI;

import gmusic.api.interfaces.IGoogleMusicAPI;

import gmusic.api.model.Song;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Hello world!
 */
public class PrintAllSongs {
    private static final Comparator<Song> ARTIST_TITLE_COMPARATOR =
        new Comparator<Song>() {
            @Override
            public int compare(final Song aO1, final Song aO2) {
                int result = String.valueOf(aO1.getArtist()).compareTo(aO2.getArtist());

                if (result == 0) {
                    result = String.valueOf(aO1.getTitle()).compareTo(aO2.getTitle());
                }

                return result;
            }
        };

    public static void main(final String[] aArgs) throws Exception {
        if (aArgs.length < 2) {
            System.out.println("usage PrintAllSongs <username> <password>");

            return;
        }

        final String username = aArgs[0];
        final String password = aArgs[1];
        final IGoogleMusicAPI api = new GoogleMusicAPI(new ApacheConnector(), new File("."));
        api.login(username, password);

        final List<Song> songList = new ArrayList<Song>(api.getAllSongs());
        Collections.sort(songList, ARTIST_TITLE_COMPARATOR);
        System.out.println(String.format("%s detected for user %s", songList.size(), username));

        for (Song current : songList) {
            System.out.println(String.format("id \"%s\", title \"%s\", artist \"%s\", created on \"%s\"", current.getId(), current.getTitle(),
                                             current.getArtist(), current.getCreationDate()));
        }
    }
}
