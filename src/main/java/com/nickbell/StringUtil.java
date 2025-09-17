package com.nickbell;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

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
}
