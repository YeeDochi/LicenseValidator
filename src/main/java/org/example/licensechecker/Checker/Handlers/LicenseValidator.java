package org.example.licensechecker.Checker.Handlers;


import org.example.licensechecker.DTO.LicenseBody;

public interface LicenseValidator {
    boolean validate(LicenseBody licenseBody);
    String getErrorMessage();
}