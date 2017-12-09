/*
 * ExpirationCheck.java
 *
 * Created on July 12, 2001, 11:06 AM
 */

package tjava;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.util.Date;

/**
 *
 * @author  twalker
 * @version
 */
public class ExpirationCheck extends Object {
    
    PublicKey publicKey;
    PrivateKey privateKey;
    
    /** Creates new ExpirationCheck */
    public ExpirationCheck(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
    
    public ExpirationCheck(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }
    
    public boolean verifyExpirationDate(Date expirationDate, byte[] signature) {
        if ( publicKey == null ) return false;
        
        boolean verifies = false;
        try {
        
        String s = expirationDate.toGMTString();
        byte[] data = s.getBytes();
        
        Signature dsa = Signature.getInstance("DSA");
        dsa.initVerify(publicKey);
        
        dsa.update(data);
        verifies = dsa.verify(signature);
        } 
        catch (Exception e) {
            System.err.println(e);
        }
        
        return verifies;
    }
    
    public byte[] generateExpirationDateSignature(Date expirationDate) {
        if ( privateKey == null ) return null;
        
        String s = expirationDate.toGMTString();
        byte[] data = s.getBytes();
        
        byte[] sig = null;
                try {
        Signature dsa = Signature.getInstance("DSA");
        dsa.initSign(privateKey);
        
        dsa.update(data);
        sig = dsa.sign();
                }
                catch (Exception e) {
            System.err.println(e);
        }
        
        return sig;
    }
    
    public PublicKey getPublicKey() {
        return publicKey;
    }
    
    public PrivateKey getPrivateKey() {
        return privateKey;
    }
    
    public void generateNewKeyPair(byte[] userSeed) {
        try  {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        keyGen.initialize(1024, new SecureRandom(userSeed));
        KeyPair pair = keyGen.generateKeyPair();
        
        publicKey = pair.getPublic();
        privateKey = pair.getPrivate();
        }
        catch (Exception e ) {
            
            System.out.println(e);
        }
    }
    
}
