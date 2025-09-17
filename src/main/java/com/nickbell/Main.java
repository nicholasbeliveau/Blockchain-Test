package com.nickbell;

public class Main {
    public static void main(String[] args) {

        Block blockOne = new Block( "Initial Block", "0" );
        System.out.println( "Block 1 : " + blockOne.hash );

        Block blockTwo = new Block( "This is the second block", blockOne.hash );
        System.out.println( "Block 2 : " + blockTwo.hash );

        Block blockThree = new Block( "Initial Block", blockTwo.hash );
        System.out.println( "Block 3 : " + blockThree.hash );
    }
}