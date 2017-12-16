package com.honeywell.hch.airtouchv2.framework.enrollment.utils;

import android.util.Base64;

import com.honeywell.hch.airtouchv2.framework.enrollment.models.http.WAPIKeyResponse;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;

public class PasswordUtil {

    public static String encodeString(String data) {
        byte[] byteData = data.getBytes();
        return Base64.encodeToString(byteData, Base64.NO_WRAP);
    }

    public static String decodeString(String data) {
        return new String(Base64.decode(data, Base64.DEFAULT));
    }

    public static String encryptString(String data, WAPIKeyResponse wapiKeyResponse) throws
            NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeySpecException, NoSuchPaddingException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, InvalidKeyException {
        Key publicKey = decodeKey(wapiKeyResponse);
        return encryptString(publicKey, data);

    }

    private static Key decodeKey(WAPIKeyResponse wapiKeyResponse) throws NoSuchAlgorithmException,
            InvalidKeySpecException, UnsupportedEncodingException {
        byte[] modulus = Base64.decode(wapiKeyResponse.getModulus().getBytes("US-ASCII"), Base64.NO_WRAP);
        byte[] exponent = Base64.decode(wapiKeyResponse.getExponent().getBytes("US-ASCII"), Base64.NO_WRAP);
        BigInteger modulusInteger = new BigInteger(1, modulus);
        BigInteger exponentInteger = new BigInteger(1, exponent);

        RSAPublicKeySpec rsaPublicKeySpec = new RSAPublicKeySpec(modulusInteger, exponentInteger);
        return KeyFactory.getInstance("RSA").generatePublic(rsaPublicKeySpec);
    }

    public static String encryptString(Key publicKey, String data) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchProviderException, UnsupportedEncodingException, InvalidAlgorithmParameterException {

        byte[] byteData = data.getBytes("UTF-8"); // convert string to byte array
        Cipher cipher = Cipher.getInstance("RSA/NONE/OAEPWithSHA1AndMGF1Padding", "BC");

        cipher.init(Cipher.ENCRYPT_MODE, publicKey, OAEPParameterSpec.DEFAULT);
        cipher.update(byteData);
        byte[] encryptedByteData = cipher.doFinal();

        // convert encrypted byte array to string and return it
        return Base64.encodeToString(encryptedByteData, Base64.NO_WRAP);
    }
}
