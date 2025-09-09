package org.example.licensechecker.Vaildator;

import org.example.licensechecker.DTO.LicenseBody;
import org.example.licensechecker.DTO.LicenseHeader;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class LicenseSignatureChecker {


    private final String ASYMMETRIC_SIGNATURE_ALGORITHM = "SHA256withRSA";

    private final ObjectMapper objectMapper = new ObjectMapper();
    public LicenseSignatureChecker() {
    }

    public LicenseBody decodeLicenseKey(String licenseKey) throws Exception {
        String[] parts = licenseKey.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid license key format. It must contain 3 parts separated by dots.");
        }

        String headerBase64 = parts[0];
        String bodyBase64 = parts[1];
        String signatureBase64 = parts[2];

        // 2. Header와 Body를 디코딩하여 JSON 문자열로 변환
        String headerJson = new String(Base64.getUrlDecoder().decode(headerBase64), StandardCharsets.UTF_8);
        String bodyJson = new String(Base64.getUrlDecoder().decode(bodyBase64), StandardCharsets.UTF_8);

        // JSON을 객체로 변환
        LicenseHeader header = objectMapper.readValue(headerJson, LicenseHeader.class);
        LicenseBody body = objectMapper.readValue(bodyJson, LicenseBody.class);

        // 3. Body에 포함된 공개키를 사용하여 서명 검증 준비
        String publicKeyString = body.publicKey();
        byte[] keyBytes = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(spec);

        // 4. 서명 검증 수행
        // 서명은 인코딩된 'Header.Body' 부분을 원본으로 하여 생성되었으므로, 해당 부분을 사용
        String headerAndBody = headerBase64 + "." + bodyBase64;
        byte[] signatureBytes = Base64.getUrlDecoder().decode(signatureBase64);

        Signature signature = Signature.getInstance(ASYMMETRIC_SIGNATURE_ALGORITHM);
        signature.initVerify(publicKey);
        signature.update(headerAndBody.getBytes(StandardCharsets.UTF_8));

        boolean isValid = signature.verify(signatureBytes);

        // 5. 결과 반환
        if (isValid) {
            // 서명이 유효하면 라이선스 본문 내용 반환
            return body;
        } else {
            // 서명이 유효하지 않으면 예외 발생
            throw new SecurityException("License signature verification failed. The key may be tampered or invalid.");
        }
    }
}
