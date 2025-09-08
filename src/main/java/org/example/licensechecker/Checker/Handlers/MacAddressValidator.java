package org.example.licensechecker.Checker.Handlers;

import com.example.License.Proto.LicenseProtos.License;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class MacAddressValidator implements LicenseValidator {

    private final Set<String> systemMacAddresses;

    public MacAddressValidator() {
        this.systemMacAddresses = getSystemMacAddresses();
    }

    @Override
    public boolean validate(License license) {
        String licenseMac = license.getMacAddress().replaceAll("[-:]", "").toUpperCase();

        // 라이선스에 MAC 주소가 지정되지 않았다면, 장비 제한이 없는 것으로 간주하고 통과
        if (licenseMac.isEmpty()) {
            return true;
        }

        // 시스템의 MAC 주소 중 라이선스에 명시된 주소가 있는지 확인
        return systemMacAddresses.contains(licenseMac);
    }

    @Override
    public String getErrorMessage() {
        return "라이선스가 허가된 장비의 MAC 주소와 일치하지 않습니다.";
    }

    /**
     * 현재 시스템의 모든 유효한 MAC 주소를 가져옵니다.
     * @return MAC 주소 Set
     */
    private Set<String> getSystemMacAddresses() {
        Set<String> macs = new HashSet<>();
        try {
            for (NetworkInterface ni : Collections.list(NetworkInterface.getNetworkInterfaces())) {
                // 가상 인터페이스나 루프백 인터페이스는 제외하고, 물리적인 하드웨어 주소만 필터링
                if (ni.isLoopback() || ni.isVirtual() || !ni.isUp()) {
                    continue;
                }
                byte[] hardwareAddress = ni.getHardwareAddress();
                if (hardwareAddress != null) {
                    StringBuilder sb = new StringBuilder();
                    for (byte b : hardwareAddress) {
                        sb.append(String.format("%02X", b));
                    }
                    macs.add(sb.toString());
                }
            }
        } catch (Exception e) {
            System.err.println("MAC 주소를 가져오는 데 실패했습니다: " + e.getMessage());
        }
        return macs;
    }
}
