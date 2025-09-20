package com.nickbell;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;

public class StringUtil {

    public static String applySha256( String input ) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hast = digest.digest( input.getBytes( "UTF-8" ) ); // TODO: Address this warning.
            StringBuffer hexString = new StringBuffer();    // Contains the hash as hexadecimal

            // TODO: Revisit warning
            for ( int i =0; i < hast.length; i++ ) {
                String hex = Integer.toHexString(0xff & hast[i]);

                if ( hex.length() == 1 ) hexString.append('0');

                hexString.append( hex );
            }

            return hexString.toString();
        }
        catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public static byte[] applyECDSASig(PrivateKey privateKey, String input ) {
        Signature dsa;
        byte[] output = new byte[0];

        try {
            dsa = Signature.getInstance("ECDSA", "BC" );
            dsa.initSign( privateKey );
            byte[] strByte = input.getBytes();
            dsa.update( strByte );
            byte[] realSig = dsa.sign();
            output = realSig;
        }
        catch ( Exception e ) {
            throw new RuntimeException( e );
        }

        return output;
    }

    public static boolean verifyECDSASig( PublicKey publicKey, String data, byte[] signature ) {

        try {
            Signature ecdsaVerify = Signature.getInstance( "ECDSA", "BC" );
            ecdsaVerify.initVerify( publicKey );
            ecdsaVerify.update( data.getBytes() );
            return ecdsaVerify.verify( signature );
        }
        catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    public static String getStringFromKey( Key key ) {
        return Base64.getEncoder().encodeToString( key.getEncoded() );
    }
}
