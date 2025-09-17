package com.nickbell;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class Block {

    public String hash;
    public String previousHash;
    private String data;    // This could be any data
    private long timeStamp;
    private int nonce;

    public Block( String data, String previousHash ) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {

        return StringUtil.applySha256(
                  previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        data
        );
    }

    public void mineBlock( int difficulty ) {

        String target = new String( new char[difficulty]).replace('\0', '0');
        long startTime = System.nanoTime();

        while ( !hash.substring( 0, difficulty).equals(target) ) {

            nonce++;
            hash = calculateHash();
        }

        long elapsedTime = System.nanoTime() - startTime;

        System.out.println( "Block Mined : " + hash );
        System.out.println( "Mined in " + TimeUnit.SECONDS.convert( elapsedTime, TimeUnit.NANOSECONDS ) + " seconds." );
    }


}
