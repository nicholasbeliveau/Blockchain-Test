package com.nickbell;

import java.security.*;
import java.util.ArrayList;

public class Transaction {

    public String transactionId;
    public PublicKey sender, recipient;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    private static int sequence = 0; // count of number of transactions generated

    public Transaction( PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs ) {
        this.sender = from;
        this.recipient = to;
        this.value = value;
        this.inputs = inputs;
    }

    // Calculate teh transaction hash to use as Id
    private String calculateHash() {
        sequence++;
        return StringUtil.applySha256(
                StringUtil.getStringFromKey( sender ) +
                        StringUtil.getStringFromKey( recipient ) +
                        Float.toString( value ) +
                        sequence
        );
    }

    public void generateSignature( PrivateKey privateKey ) {
        String data = StringUtil.getStringFromKey( sender ) + StringUtil.getStringFromKey( recipient ) + Float.toString( value );
        signature = StringUtil.applyECDSASig( privateKey, data );
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey( sender ) + StringUtil.getStringFromKey( recipient ) + Float.toString( value );
        return StringUtil.verifyECDSASig( sender, data, signature );
    }

    public boolean processTransaction() {

        if ( !verifySignature() ) {
            System.out.println( "Invalid signature" );
            return false;
        }

        // Gather transaction input and make sure they're unspent
        for ( TransactionInput input : inputs ) {
            input.UTXO = Main.UTXOs.get( input.transactionOutputId );
        }

        // Check if transaction is valid
        if ( getInputsValue() < Main.minimumTransaction ) {
            System.out.println( "Transaction Inputs too small: " + getInputsValue() );
            return false;
        }

        // Generate Transaction outputs
        float leftOver = getInputsValue() - value;
        transactionId = calculateHash();
        outputs.add( new TransactionOutput( this.recipient, value, transactionId ) ); // Send value to recipient
        outputs.add( new TransactionOutput( this.sender, leftOver, transactionId ) ); // Sent the left over 'change' back to sender

        // Add outputs to unspent list
        for ( TransactionOutput output : outputs ) {
            Main.UTXOs.put( output.id, output );
        }

        // Remove transaction inputs from UTXO lists as spent
        for ( TransactionInput input : inputs ) {
            if ( input.UTXO == null ) continue;
            Main.UTXOs.remove( input.UTXO.id );
        }

        return true;
    }

    // Returns sum of inputs
    public float getInputsValue() {
        float total = 0;
        for ( TransactionInput input : inputs ) {
            if ( input.UTXO == null ) continue;
            total += input.UTXO.value;
        }
        return total;
    }

    // Returns sum of outputs
    public float getOutputsValue() {
        float total = 0;
        for(  TransactionOutput output : outputs ) {
            total += output.value;
        }

        return total;
    }
}
