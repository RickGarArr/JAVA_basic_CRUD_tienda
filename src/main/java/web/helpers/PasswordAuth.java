package web.helpers;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public abstract class PasswordAuth {
    
    public static String generateHash(String password)
            throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        //numero de interacion s para el hash
        int iterations = 77;
        // string se convierte a un arreglo de chars
        char[] chars = password.toCharArray();
        // se obtiene el salt
        byte[] salt = getSalt();
        
        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 16 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        SecretKey sk = skf.generateSecret(spec);
        byte[] hash = sk.getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }
     
    private static byte[] getSalt() throws NoSuchAlgorithmException
    {
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        // se crea un arreglo de bytes, todos con valor de 0
        byte[] salt = new byte[16];
        // se rellena el arreglo con bytes aleatorios
        sr.nextBytes(salt);
        //return direccion del objeto salt:: Los arreglos son objetos
        return salt;
    }
     
    private static String toHex(byte[] array) throws NoSuchAlgorithmException
    {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if(paddingLength > 0) {
            return String.format("%0"  + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }
    
    public static boolean validateString(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);
         
        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();
        
        int diff = hash.length ^ testHash.length;
        for(int i = 0; i < hash.length && i < testHash.length; i++)
        {
            diff = diff | hash[i] ^ testHash[i];
        }
        return diff == 0;
    }
    
    private static byte[] fromHex(String hex) throws NoSuchAlgorithmException {
        byte[] bytes = new byte[hex.length() / 2];
        for(int i = 0; i<bytes.length ;i++)
        {
            bytes[i] = (byte)Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }
    
}



