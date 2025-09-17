package com.nickbell;

import java.util.Date;

public class Block {

    public String hash;
    public String previousHash;
    private String data;    // This could be any data
    private long timeStamp;

    public Block( String data, String previousHash ) {
        this.data = data;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
    }
}
