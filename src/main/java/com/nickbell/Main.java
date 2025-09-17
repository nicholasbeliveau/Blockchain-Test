package com.nickbell;

import com.google.gson.GsonBuilder;

import java.util.ArrayList;

public class Main {

    public static ArrayList<Block> blockChain = new ArrayList<Block>();

    public static void main(String[] args) {

        blockChain.add( new Block("Initial Block", "0") );
        blockChain.add( new Block("This is the second block", blockChain.get(blockChain.size()-1).hash) );
        blockChain.add( new Block("Third block", blockChain.get(blockChain.size()-1).hash) );

        String blockChainJson = new GsonBuilder().setPrettyPrinting().create().toJson(blockChain);
        System.out.println(blockChainJson);
    }
}