package org.ekfliu.gmusic.deduplicate.model;

import java.io.Serializable;

public class FindDuplicateOption implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean title;
    private boolean artist;
    private boolean album;
    private boolean composer;
    private boolean year;
    private boolean genre;
    private boolean albumArtist;
    private boolean name;
    private boolean length;
    private HashFunction hashFunction;
    private PreserveFunction preservFunction;

    public FindDuplicateOption() {
    }

    public boolean isTitle() {
        return title;
    }
    public void setTitle(final boolean aTitle) {
        title = aTitle;
    }
    public boolean isArtist() {
        return artist;
    }
    public void setArtist(final boolean aArtist) {
        artist = aArtist;
    }
    public boolean isAlbum() {
        return album;
    }
    public void setAlbum(final boolean aAlbum) {
        album = aAlbum;
    }
    public boolean isComposer() {
        return composer;
    }
    public void setComposer(final boolean aComposer) {
        composer = aComposer;
    }
    public boolean isYear() {
        return year;
    }
    public void setYear(final boolean aYear) {
        year = aYear;
    }
    public boolean isGenre() {
        return genre;
    }
    public void setGenre(final boolean aGenre) {
        genre = aGenre;
    }
    public boolean isAlbumArtist() {
        return albumArtist;
    }
    public void setAlbumArtist(final boolean aAlbumArtist) {
        albumArtist = aAlbumArtist;
    }
    public boolean isName() {
        return name;
    }
    public void setName(final boolean aName) {
        name = aName;
    }
    public HashFunction getHashFunction() {
        return hashFunction;
    }
    public void setHashFunction(final HashFunction aHashFunction) {
        hashFunction = aHashFunction;
    }
    public PreserveFunction getPreservFunction() {
        return preservFunction;
    }
    public void setPreservFunction(final PreserveFunction aPreservFunction) {
        preservFunction = aPreservFunction;
    }
    public boolean isLength() {
        return length;
    }
    public void setLength(final boolean aLength) {
        length = aLength;
    }
}
