package org.example.licensechecker.Vaildator;

import com.example.License.Proto.LicenseProtos;
import org.apache.commons.codec.binary.Base32;
import org.example.licensechecker.util.KeyLoader;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.security.Signature;

public class LicenseSignatureChecker {

    private final String publickey;
    private final String secretKey; // 대칭키 추가
    private final int gcmIvLength;
    private final int gcmTagLength;

    private final String ASYMMETRIC_SIGNATURE_ALGORITHM = "SHA256withRSA";
    private final String SYMMETRIC_ALGORITHM = "AES/GCM/NoPadding";
    private KeyLoader keyLoader;

    public LicenseSignatureChecker(String publickey, String secretKey, int gcmIvLength, int gcmTagLength) {
        this.publickey = publickey;
        this.secretKey = secretKey;
        this.gcmIvLength = gcmIvLength;
        this.gcmTagLength = gcmTagLength;
        keyLoader = new KeyLoader(publickey);
    }

    public LicenseProtos.License decodeLicenseKey(String formattedKey) throws Exception {
        String base32Encoded = formattedKey.replace("-", "").toUpperCase().replaceAll("=", "");
        String restoredBase32 = base32Encoded.replace('0', '=');
        byte[] finalBytes = new Base32().decode(restoredBase32);

        // 1. [iv]와 [암호화된 데이터] 분리
        ByteBuffer byteBuffer = ByteBuffer.wrap(finalBytes);
        byte[] iv = new byte[gcmIvLength];
        byteBuffer.get(iv);
        byte[] encryptedData = new byte[byteBuffer.remaining()];
        byteBuffer.get(encryptedData);

        // 2. 데이터 복호화 (AES/GCM)
        SecretKeySpec keySpec = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(gcmTagLength, iv);
        Cipher cipher = Cipher.getInstance(SYMMETRIC_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec);
        // 복호화 결과물: [rawData 길이] + [rawData] + [signature]
        byte[] decryptedBytes = cipher.doFinal(encryptedData);

        // 3. [rawData]와 [signature] 분리
        ByteBuffer decryptedBuffer = ByteBuffer.wrap(decryptedBytes);
        short rawDataLength = decryptedBuffer.getShort();
        byte[] rawData = new byte[rawDataLength];
        decryptedBuffer.get(rawData);
        byte[] signature = new byte[decryptedBuffer.remaining()];
        decryptedBuffer.get(signature);

        // 4. 서명 검증 (RSA)
        PublicKey publicKey = keyLoader.loadPublicKey();
        Signature ecdsaVerify = Signature.getInstance(ASYMMETRIC_SIGNATURE_ALGORITHM);
        ecdsaVerify.initVerify(publicKey);
        // 원본 데이터(rawData)로 서명을 검증
        ecdsaVerify.update(rawData);
        boolean isValid = ecdsaVerify.verify(signature);

        if (isValid) {
            // 검증 성공 시 원본 데이터를 Protobuf 객체로 변환하여 반환
            return LicenseProtos.License.parseFrom(rawData);
        } else {
            // 서명 검증 실패 시 null 반환
            return null;
        }
    }
}
