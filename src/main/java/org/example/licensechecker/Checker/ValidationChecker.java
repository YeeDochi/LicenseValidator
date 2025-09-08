package org.example.licensechecker.Checker;

import com.example.License.Proto.LicenseProtos;
import org.example.licensechecker.Checker.Handlers.LicenseValidator;
import org.example.licensechecker.Vaildator.LicenseSignatureChecker;

import java.util.ArrayList;
import java.util.List;

public class ValidationChecker {
    private final LicenseSignatureChecker licenseSignatureChecker;
    private final List<LicenseValidator> validators;

    // 생성자는 private으로 막아서 Builder를 통해서만 생성하도록 강제
    private ValidationChecker(LicenseSignatureChecker licenseSignatureChecker, List<LicenseValidator> validators) {
        this.licenseSignatureChecker = licenseSignatureChecker;
        this.validators = validators;
    }

    // 실제 검증을 수행하는 메소드
    public boolean check(String licenseSirial) {
        try {
            // 1. 암호학적 검증 (위변조 확인)
            LicenseProtos.License license = licenseSignatureChecker.decodeLicenseKey(licenseSirial);
            if (license == null) return false;

            // 2. 비즈니스 규칙 검증
            for (LicenseValidator validator : validators) {
                if (!validator.validate(license)) {
                    System.err.println("라이선스 규칙 위반: " + validator.getErrorMessage());
                    return false;
                }
            }

            System.out.println("모든 라이선스 검증을 통과했습니다.");
            return true;

        } catch (Exception e) {
            System.err.println("라이선스 키 처리 중 오류 발생: " + e.getMessage());
            return false;
        }
    }

    // --- Builder 클래스 ---
    public static class Builder {
        private String publicKey;
        private final List<LicenseValidator> ruleValidators = new ArrayList<>();

        public Builder withPublicKey(String publicKey) {
            this.publicKey = publicKey;
            return this;
        }

        public Builder addRule(LicenseValidator validator) {
            this.ruleValidators.add(validator);
            return this;
        }

        public ValidationChecker build() {
            if (publicKey == null || publicKey.isEmpty()) {
                throw new IllegalStateException("공개키는 반드시 필요합니다.");
            }
            LicenseSignatureChecker cryptoValidator = new LicenseSignatureChecker(publicKey);
            return new ValidationChecker(cryptoValidator, new ArrayList<>(ruleValidators));
        }
    }
}

