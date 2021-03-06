package org.ekfliu.gmusic.deduplicate;

import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;

public class SHAHashCalculator implements HashCalculator {
    private MessageDigest digest;

    public SHAHashCalculator() {
    }

    @Override
    public void resetInit() throws Exception {
        digest = MessageDigest.getInstance("SHA");
    }
    @Override
    public void updateHash(final String aValue) throws Exception {
        digest.update(aValue.getBytes("UTF8"));
    }
    @Override
    public String finalizeHash() throws Exception {
        return new String(Hex.encodeHex(digest.digest()));
    }
}
