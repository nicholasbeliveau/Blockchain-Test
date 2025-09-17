package com.nickbell;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class Main {

    public static ArrayList<Block> blockChain = new ArrayList<Block>();
    public static int difficulty = 5;

    public static Boolean validChain() {

        Block currentBlock;
        Block previousBlock;

        for ( int i=1; i < blockChain.size(); i++ ) {

            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i-1);

            // First check to make sure the has is the correct calculated hash
            if ( !currentBlock.hash.equals( currentBlock.calculateHash() ) ) {
                return false;
            }

            // Next, compare previous block's hash with what the current one has registered as previous
            if( !previousBlock.hash.equals( currentBlock.previousHash ) ) {
                return false;
            }
        }

        return true;
    }

    public static void testValidChain() {
        System.out.println( "Should be true : " + validChain());
        blockChain.get( 1 ).hash = "this is different";
        System.out.println( "Should be false : " + validChain() );
    }

    public static void main(String[] args) {

        blockChain.add( new Block("Initial Block", "0") );
        System.out.println( "Mining block 1" );
        blockChain.get(0).mineBlock(difficulty);

        blockChain.add( new Block("This is the second block", blockChain.get(blockChain.size()-1).hash) );
        System.out.println( "Mining block 2" );
        blockChain.get(1).mineBlock(difficulty);

        blockChain.add( new Block("Third block", blockChain.get(blockChain.size()-1).hash) );
        System.out.println( "Mining block 3" );
        blockChain.get(2).mineBlock(difficulty);

        String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
        System.out.println(blockChainJson);

        if( validChain() ) {
            System.out.println( "Chain is Valid" );
        }
        else {
            System.out.println( "Chain is Invalid" );
        }
    }
}