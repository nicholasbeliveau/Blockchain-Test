package com.nickbell;

import com.google.gson.GsonBuilder;

import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

public class Main {

    public static ArrayList<Block> blockChain = new ArrayList<Block>();
    public static HashMap< String, TransactionOutput > UTXOs = new HashMap< String, TransactionOutput>();
    // Keeping this at 5.  Tested up to 7 and saw results anywhere from 13 seconds to 143 seconds.  5 works quickly
    public static int difficulty = 5;
    public static Wallet walletOne;
    public static Wallet walletTwo;

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

    public void miningTest() {
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

    public static void transactionTest() {
        Security.addProvider( new org.bouncycastle.jce.provider.BouncyCastleProvider() );

        walletOne = new Wallet();
        walletTwo = new Wallet();

        // Test the keys
        System.out.println( "Private and public keys:" );
        System.out.println( StringUtil.getStringFromKey( walletOne.privateKey ) );
        System.out.println( StringUtil.getStringFromKey( walletTwo.publicKey ) );

        // Create test transaction
        Transaction transaction = new Transaction( walletOne.publicKey, walletTwo.publicKey, 5, null );
        transaction.generateSignature( walletOne.privateKey );

        // Verify the signature
        System.out.println( "Is signature verified" );
        System.out.println( transaction.verifySignature() );
    }

    public static void main(String[] args) {

        transactionTest();
    }
}