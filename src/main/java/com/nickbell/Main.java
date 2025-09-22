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
    public static float minimumTransaction = 0.1f;
    public static Wallet walletOne;
    public static Wallet walletTwo;
    public static Transaction genesisTransaction;

    public static Boolean isChainValid() {

        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String( new char[ difficulty ] ).replace( '\0', '0' );
        HashMap< String, TransactionOutput > tempUTXOs = new HashMap< String, TransactionOutput >();
        tempUTXOs.put( genesisTransaction.outputs.get( 0 ).id, genesisTransaction.outputs.get( 0 ) );

        for ( int i=1; i < blockChain.size(); i++ ) {

            currentBlock = blockChain.get(i);
            previousBlock = blockChain.get(i-1);

            if ( !currentBlock.hash.equals( currentBlock.calculateHash() ) ) {
                System.out.println( "Currenthashes not equal" );
                return false;
            }

            if (!previousBlock.hash.equals( currentBlock.previousHash ) ) {
                System.out.println( "Previous hashes not equal" );
                return false;
            }

            if ( !currentBlock.hash.substring( 0, difficulty ).equals( hashTarget ) ) {
                System.out.println( "This block hasn't been mined");
                return false;
            }

            TransactionOutput tempOutput;
            for ( int t = 0; t < currentBlock.transactions.size(); t++ ) {
                Transaction currentTransaction = currentBlock.transactions.get( t );

                if ( !currentTransaction.verifySignature() ) {
                    System.out.println( "Signature verification failed" );
                    return false;
                }

                if ( currentTransaction.getInputsValue() != currentTransaction.getOutputsValue() ) {
                    System.out.println( "Inputs are not equal to outputs" );
                    return false;
                }

                for( TransactionInput input: currentTransaction.inputs ) {
                    tempOutput = tempUTXOs.get( input.transactionOutputId );

                    if ( tempOutput == null ) {
                        System.out.println( "Referenced input on Transaction(" + t + ") is Missing" );
                        return false;
                    }

                    if ( input.UTXO.value != tempOutput.value ) {
                        System.out.println( "Referenced input Transaction(" + t + ") value is invalid" );
                        return false;
                    }

                    tempUTXOs.remove( input.transactionOutputId );
                }

                for ( TransactionOutput output: currentTransaction.outputs ) {
                    tempUTXOs.put( output.id, output );
                }

                if ( currentTransaction.outputs.get( 0 ).recipient != currentTransaction.recipient ) {
                    System.out.println( "Transaction (" + t + ") output recipient is not who it should be" );
                    return false;
                }

                if ( currentTransaction.outputs.get( 1 ).recipient != currentTransaction.sender ) {
                    System.out.println( "Transaction (" + t + ") output 'change' is not sender." );
                    return false;
                }
            }
        }
        System.out.println( "Blockchain is valid" );
        return true;
    }

    // Old function.  Use isChainValid().  Keeping it here just for learning purposes.
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

    public static void addBlock( Block newBlock ) {
        newBlock.mineBlock( difficulty );
        blockChain.add( newBlock );
    }

    public static void main(String[] args) {

        Security.addProvider( new org.bouncycastle.jce.provider.BouncyCastleProvider() );

        walletOne = new Wallet();
        walletTwo = new Wallet();
        Wallet coinbase = new Wallet();

        // Genesis transaction.  100 coins to walletOne
        genesisTransaction = new Transaction( coinbase.publicKey, walletOne.publicKey, 100f, null );
        genesisTransaction.generateSignature( coinbase.privateKey );
        genesisTransaction.transactionId = "0";
        genesisTransaction.outputs.add( new TransactionOutput(genesisTransaction.recipient, genesisTransaction.value, genesisTransaction.transactionId ) );
        UTXOs.put( genesisTransaction.outputs.get( 0 ).id, genesisTransaction.outputs.get( 0 ) );

        System.out.println( "Creating and Mining Genesis Block" );
        Block genesis = new Block( "0" );
        genesis.addTransaction( genesisTransaction );
        addBlock( genesis );

        Block block1 = new Block( genesis.hash );
        System.out.println( "\nWalletOne's balance is : " + walletOne.getBalance() );
        System.out.println( "WalletOne attempting to send funds (40) to WalletTwo" );
        block1.addTransaction( walletOne.sendFunds( walletTwo.publicKey, 40f ) );
        addBlock( block1 );
        System.out.println( "\nWalletOne's balance is : " + walletOne.getBalance() );
        System.out.println( "WalletTwo's balance is : " + walletTwo.getBalance() );

        Block block2 = new Block( block1.hash );
        System.out.println( "\nWalletOne attempting ot send more funds (1000) than it has" );
        block2.addTransaction( walletOne.sendFunds( walletTwo.publicKey, 1000f ) );
        addBlock( block2 );
        System.out.println( "\nWalletOne's balance is : " + walletOne.getBalance() );
        System.out.println( "WalletTwo's balance is : " + walletTwo.getBalance() );

        Block block3 = new Block( block2.hash );
        System.out.println( "\nWalletTwo is attempting ot send funds (20) to WalletOne" );
        block3.addTransaction( walletTwo.sendFunds(walletOne.publicKey, 20 ) );
        System.out.println( "\nWalletOne's balance is : " + walletOne.getBalance() );
        System.out.println( "WalletTwo's balance is : " + walletTwo.getBalance() );

        isChainValid();
    }
}