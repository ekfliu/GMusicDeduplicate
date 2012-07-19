package org.ekfliu.gmusic.deduplicate;

public interface HashCalculator {
    void resetInit() throws Exception;
    void updateHash(String aValue) throws Exception;
    String finalizeHash() throws Exception;
}
