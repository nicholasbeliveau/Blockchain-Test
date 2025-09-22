package com.nickbell;

import java.security.KeyPairGenerator;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Wallet {

    public PrivateKey privateKey;
    public PublicKey publicKey;

    public HashMap< String, TransactionOutput > UTXOs = new HashMap< String, TransactionOutput >();

    public Wallet() {
        generateKeyPair();
    }


    // This function uses Elliptic Curve Key Pair to set Public and Private keys
    // TODO: Look into Elliptic Curve
    public void generateKeyPair() {
        try {

            // TODO: Look into these and get a deeper understanding of the algorithms and providers
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("ECDSA","BC");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("prime192v1");

            // Initialize the key generator nad generate a KeyPair
            keyGen.initialize(ecSpec, random);
            KeyPair keyPair = keyGen.generateKeyPair();

            // Set the keys
            privateKey = keyPair.getPrivate();
            publicKey = keyPair.getPublic();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public float getBalance() {

        float total = 0;

        // Loop through UTXOs and add any that are the users to the total.
        for ( Map.Entry< String, TransactionOutput > item: Main.UTXOs.entrySet() ) {
            TransactionOutput UTXO = item.getValue();

            if ( UTXO.isMine( publicKey ) ) {
                UTXOs.put( UTXO.id, UTXO );
                total += UTXO.value;
            }
        }

        return total;
    }

    public Transaction sendFunds( PublicKey _recipient, float value ) {

        float total = 0;

        if ( getBalance() < value ) {
            System.out.println( "Not enough funds to send the transaction.");
            System.out.println( "Current balance is : " + getBalance() );
            System.out.println( "Attempted Transaction : " + value );
            return null;
        }

        ArrayList< TransactionInput > inputs = new ArrayList< TransactionInput >();

        for ( Map.Entry< String, TransactionOutput > item : UTXOs.entrySet() ) {

            TransactionOutput UTXO = item.getValue();
            total += UTXO.value;
            inputs.add( new TransactionInput( UTXO.id ) );
            if ( total > value ) break;
        }

        Transaction transaction = new Transaction( publicKey, _recipient, value, inputs );
        transaction.generateSignature( privateKey );

        for( TransactionInput input: inputs ) {

            UTXOs.remove( input.transactionOutputId );
        }

        return transaction;
    }
}
