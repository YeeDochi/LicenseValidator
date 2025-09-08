package org.example.licensechecker.Checker.Handlers;

import com.example.License.Proto.LicenseProtos;

public interface LicenseValidator {
    boolean validate(LicenseProtos.License license);
    String getErrorMessage();
}