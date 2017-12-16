package com.honeywell.hch.airtouchv3.lib.util;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * The util for encrypt and decrypt using AES arithmetic.
 * Created by nan.liu on 1/16/15.
 */
public class CryptoUtil {

    //region crypto AES
    private final static String HEX = "0123456789ABCDEF";
    private static String seedAES = "com.honeywell.air.app";

    public static void setSeedAES(String seedAES) {
        CryptoUtil.seedAES = seedAES;
    }

    public static String encryptAES(String originalText) throws Exception {
        byte[] rawKey = getRawKey(seedAES.getBytes());
        byte[] result = encryptAES(rawKey, originalText.getBytes());
        return toHex(result);
    }

    public static String decryptAES(String encrypted) throws Exception {
        byte[] rawKey = getRawKey(seedAES.getBytes());
        byte[] enc = toByte(encrypted);
        byte[] result = decryptAES(rawKey, enc);
        return new String(result);
    }

    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
        sr.setSeed(seed);
        keyGenerator.init(128, sr); // 192 and 256 bits may not be available
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] raw = secretKey.getEncoded();
        return raw;
    }

    private static byte[] encryptAES(byte[] raw, byte[] clear) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    private static byte[] decryptAES(byte[] raw, byte[] encrypted)
            throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, new IvParameterSpec(
                new byte[cipher.getBlockSize()]));
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    public static String toHex(String txt) {
        return toHex(txt.getBytes());
    }

    public static String fromHex(String hex) {
        return new String(toByte(hex));
    }

    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).byteValue();
        return result;
    }

    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }
    //endregion

}
