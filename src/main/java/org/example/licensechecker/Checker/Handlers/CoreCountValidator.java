package org.example.licensechecker.Checker.Handlers;

import org.example.licensechecker.DTO.LicenseBody;


public class CoreCountValidator implements LicenseValidator {

    @Override
    public boolean validate(LicenseBody licenseBody) {
        //int licensedCores = licenseBody.coreCount(); //<- 있다면

       /*
        if (licensedCores <= 0) {
            return false;
        }
        // 실제 시스템의 코어 수를 가져옴
        int systemCores = Runtime.getRuntime().availableProcessors();
        System.out.println(systemCores +", "+ licensedCores);
        // 시스템의 코어 수가 라이선스에 허용된 코어 수보다 작거나 같아야 함
        return systemCores <= licensedCores;
    */
        return false;
    }

    @Override
    public String getErrorMessage() {
        return "예시로 작성된 헨들러입니다, 구현되지 않았습니다.";
    }
}
