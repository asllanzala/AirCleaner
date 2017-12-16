package com.honeywell.hch.airtouchv2.lib.util;

import android.content.Context;

import com.honeywell.hch.airtouchv2.ATApplication;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * Created by Jin Qian on 6/3/2015.
 */
public class TripleDES {
    public static final String KEY_ALGORITHM = "DESede";
    public static final String CIPHER_ALGORITHM_ECB = "DESede/ECB/PKCS5Padding";
    public static final String CIPHER_ALGORITHM_CBC = "DESede/CBC/PKCS5Padding";

    private KeyGenerator keyGen;
    private SecretKey secretKey; // the key should be saved in local.
    private SecretKey secretKey2;
    private Cipher cipher;
    private static byte[] encryptData;

    public TripleDES(String mode) throws Exception {
        if ("ECB".equals(mode)) {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM_ECB);
            secretKey = (SecretKey)fileRead("key.out");
            if (secretKey == null) {
                keyGen = KeyGenerator.getInstance(KEY_ALGORITHM);
                secretKey = keyGen.generateKey();
                fileSave(secretKey, "key.out");
            }

        } else if ("CBC".equals(mode)) {
            cipher = Cipher.getInstance(CIPHER_ALGORITHM_CBC);
            secretKey2 = (SecretKey)fileRead("key.out");
            if (secretKey2 == null) {
                keyGen = KeyGenerator.getInstance(KEY_ALGORITHM);
                DESedeKeySpec spec = new DESedeKeySpec(keyGen.generateKey().getEncoded());
                secretKey2 = SecretKeyFactory.getInstance(KEY_ALGORITHM).generateSecret(spec);
                fileSave(secretKey2, "key.out");
            }

        }
    }

    /**
     * @param str
     * @return
     * @throws Exception
     */
    public byte[] encrypt(String str) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return encryptData = cipher.doFinal(str.getBytes());
    }

    public void savePassword(byte[] pass) {
        fileSave(pass, "pass.out");
    }

    public byte[] loadPassword() {
        return (byte[])fileRead("pass.out");
    }

    /**
     * @param encrypt
     * @return
     * @throws Exception
     */
    public byte[] decrypt(byte[] encrypt) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return encryptData = cipher.doFinal(encrypt);
    }

    byte[] getIV() {
        return "administ".getBytes();
    }

    /**
     * @param str
     * @return
     * @throws Exception
     */
    public byte[] encrypt2(String str) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey2, new IvParameterSpec(getIV()));
        return encryptData = cipher.doFinal(str.getBytes());
    }

    /**
     * @param encrypt
     * @return
     * @throws Exception
     */
    public byte[] decrypt2(byte[] encrypt) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey2, new IvParameterSpec(getIV()));
        return encryptData = cipher.doFinal(encrypt);
    }

    public void fileSave(Object key, String fileName) {
        try {
            FileOutputStream fos = ATApplication.getInstance().
                    getApplicationContext().openFileOutput(fileName, Context.MODE_WORLD_READABLE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(key);
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Object fileRead(String fileName) {
        Object key = null;
        try {
            FileInputStream fis = ATApplication.getInstance().
                    getApplicationContext().openFileInput(fileName);
            ObjectInputStream ois = new ObjectInputStream(fis);
            key = (Object) ois.readObject();

        } catch (StreamCorruptedException e) {
            e.printStackTrace();
        } catch (OptionalDataException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return key;
    }


}
