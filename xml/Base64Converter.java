/*
 * Base64Converter.java
 *
 * Created on August 13, 2006, 3:49 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package xml;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 *
 * @author 1425
 */
public class Base64Converter {
    
    public static byte[] fromBase64(String input) {
        return fromBase64(input.toCharArray());
    }
    public static byte[] fromBase64(char[] input) {
        
        int length = 0;
        for(int i = 0; i < input.length; i++) {
            if ( getBinaryFromASCII(input[i]) != -1 ) {
                length++;
            }
        }
        length = (int)(length * 6.0 / 8.0);
        
        byte[] output = new byte[length];
        
        int buffer;
        int oindex = 0;
        
        for(int i = 0; i < input.length; ) {
            buffer = 0;
            for(int j = 0; j < 4; j++,i++) {
                int value = -1;
                while(i < input.length && (value = getBinaryFromASCII(input[i])) == -1) {
                    i++;
                }
                if ( value != -1 ) {
                    buffer += getBinaryFromASCII(input[i]);
                    
                }
                if ( j < 3 ) buffer = buffer << 6;
            }
            
            for(int j=2; j>=0; j--) {
                byte aByte = (byte)(buffer & 0x000000FF);
                if ( oindex+j < length ) {
                    output[oindex+j] = aByte;
                }
                buffer = buffer >> 8;
            }
            oindex += 3;
        }
        
        return output;
    }
    
    public static int getBinaryFromASCII(char aChar) {
        
        int aByte;
        
        if ( aChar >= 'A' && aChar <= 'Z' ) {
            aByte = aChar - 'A';
        }
        else if ( aChar >= 'a' && aChar <= 'z' ) {
            aByte = aChar - 'a' + 26;
        }
        else if ( aChar >= '0' && aChar <= '9' ) {
            aByte = aChar - '0' + 52;
        }
        else if ( aChar == '+') {
            aByte = 62;
        }
        else if ( aChar == '/') {
            aByte = 63;
        }
        else {
            return -1;
        }
        
        return aByte;
    }
    
    public static void main(String[] args) throws IOException {
        
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        
        StringBuffer sb = new StringBuffer();
        String s;
        while( ( s = br.readLine()) != null && s.equals("end") ) {
            sb.append(s);
        }
        byte[] output = fromBase64(s.toCharArray());
        
        for(int i = 0; i < output.length; i++ ) {
            System.out.print( (char) output[i]);
        }
    }
    
}
