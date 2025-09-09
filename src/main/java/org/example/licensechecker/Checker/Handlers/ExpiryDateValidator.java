package org.example.licensechecker.Checker.Handlers;

import org.example.licensechecker.DTO.LicenseBody;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;


public class ExpiryDateValidator implements LicenseValidator {
    @Override
    public boolean validate(LicenseBody licenseBody) {
        String expiryDateStr = licenseBody.expiration();

        // 만료일이 설정되지 않았다면, 영구 라이선스로 간주하고 통과
        if (expiryDateStr == null || expiryDateStr.isEmpty()) {
            return true;
        }
        try {
            // "YYYY-MM-DD" 형식의 날짜 문자열을 파싱
            LocalDate expiryDate = LocalDate.parse(expiryDateStr);
            LocalDate today = LocalDate.now();
            System.out.println("today: " + today+" expiryDate: " + expiryDate);
            // 오늘 날짜가 만료일 이후가 아니어야 함
            return !today.isAfter(expiryDate);
        } catch (DateTimeParseException e) {
            // 날짜 형식이 잘못된 경우 유효하지 않은 것으로 처리
            System.err.println("잘못된 날짜 형식입니다: " + expiryDateStr);
            return false;
        }
    }

    @Override
    public String getErrorMessage() {
        return "라이선스 기간이 만료되었습니다.";
    }
}
