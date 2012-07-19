package org.ekfliu.gmusic.deduplicate;

import gmusic.api.comm.ApacheConnector;

import gmusic.api.impl.GoogleMusicAPI;

import gmusic.api.interfaces.IGoogleMusicAPI;

import java.io.File;

public class DeleteSongs {
    public static void main(final String[] aArgs) throws Exception {
        if (aArgs.length < 2) {
            System.out.println("usage DeleteSongs <username> <password>");

            return;
        }

        final String username = aArgs[0];
        final String password = aArgs[1];
        final IGoogleMusicAPI api = new GoogleMusicAPI(new ApacheConnector(), new File("."));
        api.login(username, password);

        final String[] deleteSongIds = new String[aArgs.length - 2];
        System.arraycopy(aArgs, 2, deleteSongIds, 0, deleteSongIds.length);
        api.deleteSongs(deleteSongIds);
    }
}
