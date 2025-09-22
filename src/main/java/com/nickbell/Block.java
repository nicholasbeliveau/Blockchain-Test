package com.nickbell;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Block {

    public String hash;
    public String previousHash;
    public String merkleRoot;
    public ArrayList< Transaction > transactions = new ArrayList< Transaction >();
    private long timeStamp;
    private int nonce;

    public Block( String data, String previousHash ) {
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.hash = calculateHash();
    }

    public String calculateHash() {

        return StringUtil.applySha256(
                  previousHash +
                        Long.toString(timeStamp) +
                        Integer.toString(nonce) +
                        merkleRoot
        );
    }

    public void mineBlock( int difficulty ) {

        merkleRoot = StringUtil.getMerkleRoot( transactions );
        String target = new String( new char[difficulty]).replace('\0', '0');
        // TODO: Take a look at hte below line.  Its added in the tutorial, but I don't see the funciton in the tutorial
        //String target = StringUtil.getDifficultyString( difficulty );
        long startTime = System.nanoTime();

        while ( !hash.substring( 0, difficulty).equals(target) ) {

            nonce++;
            hash = calculateHash();
        }

        long elapsedTime = System.nanoTime() - startTime;

        System.out.println( "Block Mined : " + hash );
        System.out.println( "Mined in " + TimeUnit.SECONDS.convert( elapsedTime, TimeUnit.NANOSECONDS ) + " seconds." );
    }

    public boolean addTransaction( Transaction transaction ) {

        if ( transaction == null ) return false;

        if ( !Objects.equals( previousHash, "0" ) ) {

            if ( !transaction.processTransaction() ) {
                System.out.println( "Transaction failed to process." );
                return false;
            }
        }

        transactions.add( transaction );
        System.out.println( "Transaction added Successfully." );
        return true;
    }
}
