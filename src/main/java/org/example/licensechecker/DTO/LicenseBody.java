package org.example.licensechecker.DTO;

public record LicenseBody(
        String expiration,
        String uuid,
        String customerid,
        String solution,
        String publicKey
)
{}
