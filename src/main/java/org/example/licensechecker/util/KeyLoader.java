package org.example.licensechecker.util;


import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;


import java.util.Base64;

// String 으로 저장되어있는 키를 객체로 변환하는 클래스

public class KeyLoader {


    private String PUBLIC_KEY_STRING;

    public KeyLoader(String PUBLIC_KEY_STRING) {
        this.PUBLIC_KEY_STRING=PUBLIC_KEY_STRING;
    }

    public PublicKey loadPublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
        String publicKeyPem = PUBLIC_KEY_STRING;

        publicKeyPem = publicKeyPem.replaceAll("\\s", "");
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyPem);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);


        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }
}