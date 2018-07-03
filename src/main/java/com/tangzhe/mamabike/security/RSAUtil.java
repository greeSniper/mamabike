package com.tangzhe.mamabike.security;

import javax.crypto.Cipher;
import java.io.InputStream;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * Created by JackWangon[www.coder520.com] 2017/8/10.
 */
public class RSAUtil {

    public static final String KEY_ALGORITHM = "RSA";

    /**
     * 公钥字符串
     */
    private static String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCkMzmWUaJ9Xm+qsE+PJ79J MPrjxTZirU1QaIVTjKXzw3YskkRQ6Wh7KgewBINR+H0QoGTVW8mhBF1ZDxI7 +aqqFgD3mOB4Ct1GTwt5a8Qf4n/auLhjXlt31h6qkI2HZFwuIO/c9xJ2d9Hs Ozjl+ZT+N13fd0/bwVxWVizRWjgJMQIDAQAB";

    public static String getPublicKey() {
        return PUBLIC_KEY;
    }

    /**
     * 私钥字符串
     */
    private static String PRIVATE_KEY = "";

    public static String getPrivateKey() {
        return PRIVATE_KEY;
    }

    /**
     * 读取密钥字符串
     * @throws Exception
     */
    public static void convert() throws Exception {
        byte[] data = null;

        try {
            InputStream is = RSAUtil.class.getResourceAsStream("/enc_pri");
            int length = is.available();
            data = new byte[length];
            is.read(data);
        } catch (Exception e) {
        }

        String dataStr = new String(data);
        try {
            PRIVATE_KEY = dataStr;
        } catch (Exception e) {
        }

        if (PRIVATE_KEY == null) {
            throw new Exception("Fail to retrieve key");
        }
    }

    /**
     * 私钥解密
     * @param data
     * @return
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data) throws Exception {
        convert();
        byte[] keyBytes = Base64Util.decode(PRIVATE_KEY);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key privateKey = keyFactory.generatePrivate(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     * @param data
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String key) throws Exception {
        byte[] keyBytes = Base64Util.decode(key);
        X509EncodedKeySpec pkcs8KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        Key publicKey = keyFactory.generatePublic(pkcs8KeySpec);

        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    public static void main(String[] args) throws Exception {
//        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
//        keyPairGenerator.initialize(1024);
//        KeyPair keyPair = keyPairGenerator.generateKeyPair();
//        PublicKey publicKey = keyPair.getPublic();
//        PrivateKey privateKey = keyPair.getPrivate();
//        System.out.println(Base64Util.encode(publicKey.getEncoded()));
//        System.out.println(Base64Util.encode(privateKey.getEncoded()));

        //测试RSA加密解密
        String data = "老王来了、、、";
        byte[] enResult = encryptByPublicKey(data.getBytes("UTF-8"),PUBLIC_KEY);
        System.out.println(enResult.toString());
        byte[] deResult = decryptByPrivateKey(enResult);
        System.out.println(new String(deResult,"UTF-8"));

    }
}
