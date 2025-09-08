package org.example.licensechecker.Checker.Handlers;

import com.example.License.Proto.LicenseProtos;

import javax.xml.validation.Validator;

public class SocketCountValidator implements LicenseValidator{
    @Override
    public boolean validate(LicenseProtos.License license) {
        int licenseSocketCount = license.getSocketCount();

        // 체크해야할 항목이 0이라면 뭔가 잘못된것
        if (licenseSocketCount <= 0) {
            return false;
        }
        // 실제 시스템의 코어 수를 가져옴
        int systemCores = Runtime.getRuntime().availableProcessors();

        // 시스템의 코어 수가 라이선스에 허용된 코어 수보다 작거나 같아야 함
        return systemCores <= licenseSocketCount;
    }

    @Override
    public String getErrorMessage() {
        return "시스템의 CPU 소켓 수가 라이선스 허용치를 초과합니다.";
    }
}
