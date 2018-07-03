package com.tangzhe.mamabike.security;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by JackWangon[www.coder520.com] 2017/8/10.
 */
public class AESUtil {

    public static final String KEY_ALGORITHM = "AES";
    public static final String KEY_ALGORITHM_MODE = "AES/CBC/PKCS5Padding";

    /**
     *  AES对称加密
     * @param data
     * @param key   16位
     * @return
     */
    public  static String encrypt(String data,String key){
        try {
            SecretKeySpec spec = new SecretKeySpec(key.getBytes("UTF-8"),KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE);
            cipher.init(Cipher.ENCRYPT_MODE , spec,new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] bs = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64Util.encode(bs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }

    /**
     *  AES对称解密
     * @param data
     * @param key   16位
     * @return
     */
    public static String decrypt(String data, String key) {
        try {
            SecretKeySpec spec = new SecretKeySpec(key.getBytes("UTF-8"), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM_MODE);
            cipher.init(Cipher.DECRYPT_MODE , spec , new IvParameterSpec(new byte[cipher.getBlockSize()]));
            byte[] originBytes = Base64Util.decode(data);
            byte[] result = cipher.doFinal(originBytes);
            return new String(result,"UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }


    public static void main(String[] args) throws Exception {
//        //AES加密数据
//        String key = "123456789abcdfgt";
//        String encryptResult = encrypt("老王来了。。。", key);
//        System.out.println(encryptResult);
//        //AES解密数据
//        String decryptResult = decrypt(encryptResult, key);
//        System.out.println(decryptResult);

        //安卓端AES加密数据
        String key = "123456789abcdfgt"; //数据加密的密钥
        String data = "{'mobile':'18980840843','code':'6666','platform':'android'}"; //数据的明文
        String enResult = encrypt(data, key); //用于传输的数据的密文
        System.out.println(enResult); //加密的数据
        //安卓端RSA加密AES的密钥key
        byte[] enKeyBytes = RSAUtil.encryptByPublicKey(key.getBytes("UTF-8"), RSAUtil.getPublicKey());
        //base64避免乱码，妨碍传输
        String enKey = Base64Util.encode(enKeyBytes);
        System.out.println(enKey); //用于传输的通过publicKey RSA加密的密钥
//
//        //服务端RSA解密AES的密钥key
//        byte[] deKeyBytes = Base64Util.decode(enKey);//避免乱码
//        byte[] deKeyBytesBytes = RSAUtil.decryptByPrivateKey(deKeyBytes);
//        String deKey = new String(deKeyBytesBytes, "UTF-8"); //通过privateKey RSA解密的密钥
//        //System.out.println(deKey);
//        String deResult = decrypt(enResult, deKey);
//        System.out.println(deResult); //解密的数据

//        /**AES加密数据**/
//        String key = "123456789abcdfgt";
//        String dataToEn = "{'mobile':'18980840843','code':'6666','platform':'android'}";
//        String enResult = encrypt(dataToEn,key);
//        System.out.println(enResult);
//
//        /**RSA 加密AES的密钥**/
//        byte[] enKey = RSAUtil.encryptByPublicKey(key.getBytes("UTF-8"),"MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCkMzmWUaJ9Xm+qsE+PJ79J MPrjxTZirU1QaIVTjKXzw3YskkRQ6Wh7KgewBINR+H0QoGTVW8mhBF1ZDxI7 +aqqFgD3mOB4Ct1GTwt5a8Qf4n/auLhjXlt31h6qkI2HZFwuIO/c9xJ2d9Hs Ozjl+ZT+N13fd0/bwVxWVizRWjgJMQIDAQAB");
//        String baseKey = Base64Util.encode(enKey);
//        System.out.println(baseKey);
//        //服务端RSA解密AES的key
//        byte[] de = Base64Util.decode(baseKey);
//        byte[] deKeyResult = RSAUtil.decryptByPrivateKey(de);
//        System.out.println(new String(deKeyResult,"UTF-8"));
//
//        String deResult = decrypt(enResult,key);
//        System.out.println(deResult);
    }


}
