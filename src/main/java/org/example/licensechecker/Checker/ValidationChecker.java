package org.example.licensechecker.Checker;

import org.example.licensechecker.Checker.Handlers.LicenseValidator;
import org.example.licensechecker.DTO.LicenseBody;
import org.example.licensechecker.Vaildator.LicenseSignatureChecker;

import java.util.ArrayList;
import java.util.List;

public class ValidationChecker {
    private final LicenseSignatureChecker licenseSignatureChecker;
    private final List<LicenseValidator> validators;

    // Builder라 프라이빗으로
    private ValidationChecker(LicenseSignatureChecker licenseSignatureChecker, List<LicenseValidator> validators) {
        this.licenseSignatureChecker = licenseSignatureChecker;
        this.validators = validators;
    }


    public boolean check(String licenseSirial) {
        try {
            // RSA
            LicenseBody license = licenseSignatureChecker.decodeLicenseKey(licenseSirial);
            if (license == null) return false;

            // 하드웨어와 대조
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


        private final List<LicenseValidator> ruleValidators = new ArrayList<>();


        public Builder addRule(LicenseValidator validator) {
            this.ruleValidators.add(validator);
            return this;
        }

        public ValidationChecker build() {
            LicenseSignatureChecker cryptoValidator = new LicenseSignatureChecker();
            return new ValidationChecker(cryptoValidator, new ArrayList<>(ruleValidators));
        }
    }
}

