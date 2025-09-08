package org.example.licensechecker.Vaildator;

import com.example.License.Proto.LicenseProtos;
import org.apache.commons.codec.binary.Base32;
import org.example.licensechecker.util.KeyLoader;

import java.nio.ByteBuffer;
import java.security.PublicKey;
import java.security.Signature;

public class LicenseSignatureChecker {

    private String publickey;
    private final String ASYMMETRIC_SIGNATURE_ALGORITHM = "SHA256withRSA";
    private KeyLoader keyLoader;
    public LicenseSignatureChecker(String publickey) {
        this.publickey = publickey;
        keyLoader = new KeyLoader(publickey);
    }

    public LicenseProtos.License decodeLicenseKey(String formattedKey) throws Exception {
        String base32Encoded = formattedKey.replace("-", "").toUpperCase().replaceAll("=", "");
        String restoredBase32 = base32Encoded.replace('0', '=');
        byte[] finalBytes = new Base32().decode(restoredBase32);
        ByteBuffer byteBuffer = ByteBuffer.wrap(finalBytes);

        short dataLength = byteBuffer.getShort();
        byte[] rawData = new byte[dataLength];
        byteBuffer.get(rawData);
        byte[] signature = new byte[byteBuffer.remaining()];
        byteBuffer.get(signature);
        PublicKey publicKey = keyLoader.loadPublicKey();
        Signature ecdsaVerify = Signature.getInstance(ASYMMETRIC_SIGNATURE_ALGORITHM);
        ecdsaVerify.initVerify(publicKey);
        ecdsaVerify.update(rawData);
        boolean isValid = ecdsaVerify.verify(signature);

        if (isValid) {
            System.out.println("유효한 라이선스입니다.");
            return LicenseProtos.License.parseFrom(rawData);
        } else {
            System.out.println("위조된 라이선스입니다!");
            throw new SecurityException("Invalid license key signature.");
        }
    }
}
