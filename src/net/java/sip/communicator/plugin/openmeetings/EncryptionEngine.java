package net.java.sip.communicator.plugin.openmeetings;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.Cipher;

public class EncryptionEngine
{

    // private static byte[] iv =
    // { 0x0a, 0x01, 0x02, 0x03, 0x04, 0x0b, 0x0c, 0x0d };

    public static IvParameterSpec iv = new IvParameterSpec(new byte[]
    { 1, 2, 3, 4, 5, 6, 7, 8 });

    private static String xform = "DES/CBC/PKCS5Padding";

    // private static SecretKey key;

    public static SecretKey key = new SecretKeySpec(new byte[]
    { 1, 1, 1, 1, 1, 1, 1, 1 }, "DES");

    private static SecretKey getKey() throws NoSuchAlgorithmException
    {

        KeyGenerator kg = KeyGenerator.getInstance("DES");
        kg.init(56); // 56 is the keysize. Fixed for DES
        // key = kg.generateKey();
        //
        // SecretKeyFactory kf =
        // SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        // SecretKey key = kf.generateSecret(keySpec);

        return key;
    }

    public String encrypt(String message) throws Exception
    {
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword =
            md.digest("HG58YZ3CR9".getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
        for (int j = 0, k = 16; j < 8;)
        {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher cipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);

        final byte[] plainTextBytes = message.getBytes("utf-8");
        final byte[] cipherText = cipher.doFinal(plainTextBytes);

        final String encodedCipherText =
            new sun.misc.BASE64Encoder().encode(cipherText);

        return encodedCipherText;
    }

    public String decrypt(String messageStr) throws Exception
    {
        byte[] message = new sun.misc.BASE64Decoder().decodeBuffer(messageStr);
        final MessageDigest md = MessageDigest.getInstance("md5");
        final byte[] digestOfPassword =
            md.digest("HG58YZ3CR9".getBytes("utf-8"));
        final byte[] keyBytes = Arrays.copyOf(digestOfPassword, 24);
        for (int j = 0, k = 16; j < 8;)
        {
            keyBytes[k++] = keyBytes[j++];
        }

        final SecretKey key = new SecretKeySpec(keyBytes, "DESede");
        final IvParameterSpec iv = new IvParameterSpec(new byte[8]);
        final Cipher decipher = Cipher.getInstance("DESede/CBC/PKCS5Padding");
        decipher.init(Cipher.DECRYPT_MODE, key, iv);

        // final byte[] encData = new
        // sun.misc.BASE64Decoder().decodeBuffer(message);
        final byte[] plainText = decipher.doFinal(message);

        return new String(plainText, "UTF-8");
    }

    // public static String encrypt( String input ) throws Exception {
    // byte[] inpBytes = input.getBytes();
    // Cipher cipher = Cipher.getInstance(xform);
    // // IvParameterSpec ips = new IvParameterSpec(iv);
    // cipher.init(Cipher.ENCRYPT_MODE, key, iv);
    // String out = cipher.doFinal(inpBytes).toString();
    // return out;
    // }
    // public static String decrypt(String input ) throws Exception {
    // byte[] inpBytes = input.getBytes();
    // Cipher cipher = Cipher.getInstance(xform);
    // // IvParameterSpec ips = new IvParameterSpec(iv);
    // cipher.init(Cipher.DECRYPT_MODE, key, iv);
    // byte[] outb = cipher.doFinal(inpBytes);
    // String out = cipher.doFinal(inpBytes).toString();
    // return out;
    // }

}
