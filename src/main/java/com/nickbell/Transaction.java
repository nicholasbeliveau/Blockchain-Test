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
}
