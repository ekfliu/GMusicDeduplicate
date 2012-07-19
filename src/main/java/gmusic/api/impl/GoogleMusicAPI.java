/*******************************************************************************
 * Copyright (c) 2012 Jens Kristian Villadsen.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 *
 * Contributors:
 *     Jens Kristian Villadsen - initial API and implementation
 ******************************************************************************/
package gmusic.api.impl;

import gmusic.api.comm.ApacheConnector;
import gmusic.api.comm.FormBuilder;
import gmusic.api.comm.JSON;

import gmusic.api.interfaces.IGoogleHttpClient;
import gmusic.api.interfaces.IGoogleMusicAPI;

import gmusic.api.model.AddPlaylist;
import gmusic.api.model.DeletedPlayList;
import gmusic.api.model.DeletedSongs;
import gmusic.api.model.Playlist;
import gmusic.api.model.Playlists;
import gmusic.api.model.QueryResponse;
import gmusic.api.model.Song;
import gmusic.api.model.SongUrl;

import gmusic.model.Tune;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;

import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import com.google.common.base.Strings;

public class GoogleMusicAPI implements IGoogleMusicAPI {
    private static final String BASE_GOOGLE_SERVICE_URL = "https://play.google.com/music/services/";
    protected IGoogleHttpClient client;
    private final File storageDirectory;

    public GoogleMusicAPI() {
        this(new ApacheConnector(), new File("."));
    }
    public GoogleMusicAPI(final IGoogleHttpClient httpClient, final File file) {
        client = httpClient;
        storageDirectory = file;
    }

    @Override
    public final void login(final String email, final String password)
                     throws ClientProtocolException, IOException, URISyntaxException {
        final Map<String, String> fields = new HashMap<String, String>();
        fields.put("service", "sj");
        fields.put("Email", email);
        fields.put("Passwd", password);

        final FormBuilder form = new FormBuilder();
        form.addFields(fields);
        form.close();
        client.dispatchPost(new URI("https://www.google.com/accounts/ClientLogin"), form);
    }
    @Override
    public final Collection<Song> getAllSongs() throws ClientProtocolException, IOException, URISyntaxException {
        return getSongs("");
    }
    @Override
    public final AddPlaylist addPlaylist(final String playlistName)
                                  throws Exception {
        final Map<String, String> fields = new HashMap<String, String>();
        fields.put("json", "{\"title\":\"" + playlistName + "\"}");

        final FormBuilder form = new FormBuilder();
        form.addFields(fields);
        form.close();

        return JSON.Deserialize(client.dispatchPost(new URI(BASE_GOOGLE_SERVICE_URL + "addplaylist"), form), AddPlaylist.class);
    }
    @Override
    public final Playlists getAllPlaylists() throws ClientProtocolException, IOException, URISyntaxException {
        return JSON.Deserialize(getPlaylistAssist("{}"), Playlists.class);
    }
    @Override
    public final Playlist getPlaylist(final String plID)
                               throws ClientProtocolException, IOException, URISyntaxException {
        return JSON.Deserialize(getPlaylistAssist("{\"id\":\"" + plID + "\"}"), Playlist.class);
    }
    protected final URI getTuneURL(final Tune tune) throws URISyntaxException, ClientProtocolException, IOException {
        return new URI(JSON.Deserialize(client.dispatchGet(new URI(String.format("https://play.google.com/music/play?u=0&songid=%1$s&pt=e", tune.getId()))),
                                        SongUrl.class).getUrl());
    }
    @Override
    public URI getSongURL(final Song song) throws URISyntaxException, ClientProtocolException, IOException {
        return getTuneURL(song);
    }
    @Override
    public final DeletedPlayList deletePlaylist(final String id)
                                         throws Exception {
        final Map<String, String> fields = new HashMap<String, String>();
        fields.put("json", "{\"id\":\"" + id + "\"}");

        final FormBuilder form = new FormBuilder();
        form.addFields(fields);
        form.close();

        return JSON.Deserialize(client.dispatchPost(new URI(BASE_GOOGLE_SERVICE_URL + "deleteplaylist"), form), DeletedPlayList.class);
    }
    @Override
    public void changeSongMetaData(final Song aSong) throws Exception {
        // TODO Auto-generated method stub
    }
    @Override
    public DeletedSongs deleteSongs(final String[] aSongIds)
                             throws Exception {
        final Map<String, String> fields = new HashMap<String, String>();
        fields.put("json", "{ \"songIds\":" + constructJsonArrayList(aSongIds) + ", \"entryIds\":[\"\"], \"listId\": \"all\" }");

        final FormBuilder form = new FormBuilder();
        form.addFields(fields);
        form.close();

        return JSON.Deserialize(client.dispatchPost(new URI(BASE_GOOGLE_SERVICE_URL + "deletesong"), form), DeletedSongs.class);
    }
    private final String constructJsonArrayList(final String[] aStringList) {
        final StringBuilder buffer = new StringBuilder();

        if ((aStringList == null) || (aStringList.length == 0)) {
            buffer.append("[]");
        } else {
            buffer.append("[");

            for (int i = 0; i < aStringList.length; i++) {
                buffer.append("\"" + aStringList[i] + ((i == (aStringList.length - 1)) ? "\"" : "\", "));
            }

            buffer.append("]");
        }

        return buffer.toString();
    }
    private final String getPlaylistAssist(final String jsonString)
                                    throws ClientProtocolException, IOException, URISyntaxException {
        final Map<String, String> fields = new HashMap<String, String>();
        fields.put("json", jsonString);

        final FormBuilder builder = new FormBuilder();
        builder.addFields(fields);
        builder.close();

        return client.dispatchPost(new URI(BASE_GOOGLE_SERVICE_URL + "loadplaylist"), builder);
    }
    private final Collection<Song> getSongs(final String continuationToken)
                                     throws ClientProtocolException, IOException, URISyntaxException {
        final Collection<Song> chunkedCollection = new ArrayList<Song>();
        final Map<String, String> fields = new HashMap<String, String>();
        fields.put("json", "{\"continuationToken\":\"" + continuationToken + "\"}");

        final FormBuilder form = new FormBuilder();
        form.addFields(fields);
        form.close();

        final Playlist chunk = JSON.Deserialize(client.dispatchPost(new URI(BASE_GOOGLE_SERVICE_URL + "loadalltracks"), form), Playlist.class);
        chunkedCollection.addAll(chunk.getPlaylist());

        if (!Strings.isNullOrEmpty(chunk.getContinuationToken())) {
            chunkedCollection.addAll(getSongs(chunk.getContinuationToken()));
        }

        return chunkedCollection;
    }
    @Override
    public Collection<File> downloadSongs(final Collection<Song> songs)
                                   throws MalformedURLException, ClientProtocolException, IOException, URISyntaxException {
        final Collection<File> files = new ArrayList<File>();

        for (final Song song : songs) {
            files.add(downloadSong(song));
        }

        return files;
    }
    @Override
    public File downloadSong(final Song song) throws MalformedURLException, ClientProtocolException, IOException, URISyntaxException {
        return downloadTune(song);
    }
    @Override
    public QueryResponse search(final String query) throws Exception {
        if ((null == query) || query.isEmpty()) {
            throw new IllegalArgumentException("query is null or empty");
        }

        final Map<String, String> fields = new HashMap<String, String>();
        fields.put("json", "{\"q\":\"" + query + "\"}");

        final FormBuilder form = new FormBuilder();
        form.addFields(fields);
        form.close();

        final String response = client.dispatchPost(new URI(BASE_GOOGLE_SERVICE_URL + "search"), form);

        return JSON.Deserialize(response, QueryResponse.class);
    }
    protected File downloadTune(final Tune tune) throws MalformedURLException, ClientProtocolException, IOException, URISyntaxException {
        final File file = new File(storageDirectory.getAbsolutePath() + tune.getId() + ".mp3");

        if (!file.exists()) {
            FileUtils.copyURLToFile(getTuneURL(tune).toURL(), file);
            populateFileWithTuneTags(file, tune);
        }

        return file;
    }
    private void populateFileWithTuneTags(final File file, final Tune tune)
                                   throws IOException {
        try {
            final AudioFile f = AudioFileIO.read(file);
            Tag tag = f.getTag();

            if (tag == null) {
                tag = new ID3v24Tag();
            }

            tag.setField(FieldKey.ARTIST, tune.getAlbumArtist());
            tag.setField(FieldKey.ALBUM, tune.getAlbum());
            tag.setField(FieldKey.TITLE, tune.getTitle());
            f.setTag(tag);
            AudioFileIO.write(f);
        } catch (final Exception e) {
            throw new IOException(e);
        }
    }
}
