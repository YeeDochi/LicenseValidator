import org.example.licensechecker.Checker.Handlers.ExpiryDateValidator;
import org.example.licensechecker.Checker.ValidationChecker;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ValidationCheckerTest {


    private ValidationChecker checker;

    @BeforeEach
    void setUp() {
        checker = new ValidationChecker.Builder()
                .addRule(new ExpiryDateValidator()) // 만료
                .build();
    }

    @Test
    @DisplayName("유효한 라이선스 키는 검증에 성공해야 한다 (true 반환)")
    void check_WithValidLicenseKey_ShouldReturnTrue() {

        String validKey = "eyJsaWNlbnNlX3YiOiIxLjAifQ.eyJleHBpcmF0aW9uIjoiMjAyNS0wOS0xMSIsInV1aWQiOiJiMmZkYjYzNi0xOTEwLTQzODUtODVkYy0wMjhmZDI5NjllMjQiLCJjdXN0b21lcmlkIjoiYXNkIiwic29sdXRpb24iOiJPUElUIiwicHVibGljS2V5IjoiTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFydjlGNk93Y2dhcC9xbzh1VzlXSk15SlE1T1VjaGFxUzZVYTFxV0xFTndZN05SQWd0U0lEaFE1Umt5c0RxN3UvaVliWTZXUXVIYUxOTHQ4cG1mN3BGRGoyMGUzUUREcm5CRHZCcXpMVnZPUEp4TEtreVl2NkNDNnpUUEVnL3FrbVJ0dndUKzRvMWJ3STJuQzEyWHZiWTREWHJTamVweUd2UDh1RXh2K2xOSXczb083aHhVeTkvNWF4VWNpeisxaWJkRjNuTENYZUhCbWtCWkY1SE5BTHljQzlGeENzY0I0clBWcUNaYnVpNVhkRkJ3eEFKTGRuVVVsV3Z6UHcvYlpqV3I3UGUzTHRnZFJCakI5RXR6UlJGVVlydGZjUTJXY1d6Y3BKY0hVRjIvUHMzZnB2Wk5aY0xHYjNKT0lKUjdLeUpXWFowMXJGSnZrVHhTTkRJTFpkTXdJREFRQUIifQ.oH2xe9b_3aKVZtoWtGuVpTZG45FM9-2edjDqF0kU4FF5ke8yzcxDBns99AKK94cxm9pdLSf5N_umpNR4arYtjj2wmpX7nvLJ9B7uM9C1P9eCJTY-u7N8zDlH_I_RFZPacUxjJve-skmvEtl7JNN-3QXg5FPN8sEvPvdVhBxxccsGfFug1aBXql_4BHgFuhHdgVQzblLly8EzfIyIYmiB1WN7Din5_u4Dxnv_uPFoIr851cotI-IMog5trSl_VJWpIaUsEKDgs9RUUpf6AtYwTKySLyTLolsgeI-E03q6o73ACixP6UZH2SzPQIqq7q-gLSn_q2YA842Kt74FAxPjPg";
        boolean result = checker.check(validKey);
        
        assertTrue(result, "유효한 라이선스 키가 검증에 실패했습니다.");
    }

    @Test
    @DisplayName("위변조된 라이선스 키는 검증에 실패해야 한다 (false 반환)")
    void check_WithTamperedLicenseKey_ShouldReturnFalse() {
        
        String erasedKey = "eyJsaWNlbnNlX3YiOiIxLjAifQ.eyJleHBpcmF0aW9uIjoiMjAyNS0wOSasdaasdnV1aWQiOiJiMmZkYjYzNi0xOTEwLTQzODUtODVkYy0wMjhmZDI5NjllMjQiLCJjdXN0b21lcmlkIjoiYXNkIiwic29sdXRpb24iOiJPUElUIiwicHVibGljS2V5IjoiTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUE0Y1JOZytvMTBOR2JsQ1NkWUR5V3Jha0ZoQzZ4eWhRZmhIWlIzT0VlYlBobStGWUM2cVQvU0ZMdGRRaXp4S0pzMVlZWWVFNnEyNEVWVWlUeTF5TW9STThzN3FKaUo3MjlUOFRKbWc5R3NXVVd0bkJyczVZUTNPZTROZUxkMXNHK1RJRm03WHFieDVpbldCeXNFZk9IUmZ4eFhrOHo2UWVHS2lKeGg1UVJ0TVdScnBuSVVrUDFJSWNuckxBcTVmNmFqMnF2Zk1sYWVNQzBmM2E4TUtnN25aZlBsTGdWemp5a24rb0xNYm54UWVNV25xVk5qNTdIdDBDRFNOVkVPbmdVbVFoQ2dCSjJ0V29XQ0I3Rmltb2JxaFd3L2xXWE4vNTVuSENncWhOTnI2cHRxa2RKd3d2WVljYys3N05DR3ltZEQ1OGVFS0phdzA0U0dSZ1doWXFPY3dJREFRQUIifQ.klziBEUktQtwYJheT6g_YqV00t7wa2lrlKw4834HW6HuBwYOHjxN5UVMeM3BSepkVD9Xcgpj3DV0CIBK9RlAavBTZ1vRTNgQCdvu9e0CVCCgg4lC_pAbeRyzr5wC9ZhqrFnxZPYakNBbPNnILufSA4SZw-e4CP1Yy71a6sxPMiblGOj3qOcdOB_w3pA9JoQnZZDiYu2_M3A0j95-3RzaXmSOxspaPoQdSWsF_imT-swtld1ykDRVI0OBwdnpFrSePW4C2mVC_fGGgwBR-Zp0U6MM8keV3clvWHsA70KpY9TC43l2OgWIRumenzrxUmsGduTohxTd2RhiAfViUDo6mg";
        //String tamperedKey = validKey.substring(0, validKey.length() - 1) + "Z";

        boolean result = checker.check(erasedKey);
        
        assertFalse(result, "위변조된 라이선스 키가 검증에 성공했습니다.");
    }

    @Test
    @DisplayName("만료된 라이선스 키는 검증에 실패해야 한다 (false 반환)")
    void check_WithExpiredLicenseKey_ShouldReturnFalse() {

        String expiredKey = "eyJsaWNlbnNlX3YiOiIxLjAifQ.eyJleHBpcmF0aW9uIjoiMjAyNS0wOC0zMSIsInV1aWQiOiJiMmZkYjYzNi0xOTEwLTQzODUtODVkYy0wMjhmZDI5NjllMjQiLCJjdXN0b21lcmlkIjoiYXNkIiwic29sdXRpb24iOiJPUElUIiwicHVibGljS2V5IjoiTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUFqcGgrVlhPdmMxYzFObE9sdERaRW5BbEJhNFI0dVA2RGhza0l0WHQvc3laNlZkRUlUYW9UajRhZkRkV0dBRHVianlGY3dZZnBBMnd6RzhSbWlOZDBKbU0wNW5Ga3IwUTlWMkVMK0p4cHJESDZBS1ltYkRySlQ1TVhVdHdiek9zSW9hOWJXZnZ5RmRDM2ljZ0VsWkliZTVpWlllWkRHL2JldHA1WWp6c3lld3pBMGVBOUZMdTRURGZ2VzNmZ1ZRVzRsZGtzbytqcXBFcGFyTW1rRHBYSUZrMkNtcnJhK2xvZTdaRjNzRTV1R0oxUjZoSDhscHhJMjhJOWN6elhaRlVrKzdjOERQV1Vua3dvdm1HWE5McWJxbXNrTWN4SCt1N2hqZ2crc2Q3VGEzdmUwR2FGRURWYTNWZlVVNVhTRU9DbWRjeTFnUDk3TXNXZ0JidlZpeTVPM1FJREFRQUIifQ.Aby1pmc7ceYpnfAu2tpxhxMuw4ZCCnpWkEu3ge5UIbduui5F3i7reAphJuBl-QBAulKMPTVVCgnc479pPxZP7WWodaVLNzll95-THeYUadSrT2efCk_GZUIftAOTsE-Xzavm_pVHonUJZodG8xgMifckueZaXZsYUoim4hd9wcc-89llPBiHaCWo8d3Sisi0H_whgdhESYyaEO17pzOz8V8ar2rFcHfeulLQtfPZ_V8k0AP-tISZel8YMOn-PcgH_YNBDzFUzCB_JrFnKGLBZMl6BzhQMCjdNOssrarmvusCRTuMvsbfvqzrjvOpszUBS8HYlm0bTy3kVNJHHlZwYg";
        boolean result = checker.check(expiredKey);

        assertFalse(result, "만료된 라이선스 키가 검증에 성공했습니다.");
    }

    @Test
    @DisplayName("라이센스 버전을 불러올 수 있다.")
    void check_IsItCanGetVirsion() throws Exception {

        String virsionLicense1 = "eyJsaWNlbnNlX3YiOiIxLjAifQ.eyJleHBpcmF0aW9uIjoiMjAyNS0wOS0xOSIsInV1aWQiOiJiMmZkYjYzNi0xOTEwLTQzODUtODVkYy0wMjhmZDI5NjllMjQiLCJjdXN0b21lcmlkIjoiYXNkIiwic29sdXRpb24iOiJPUElUIiwicHVibGljS2V5IjoiTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUE0Y1JOZytvMTBOR2JsQ1NkWUR5V3Jha0ZoQzZ4eWhRZmhIWlIzT0VlYlBobStGWUM2cVQvU0ZMdGRRaXp4S0pzMVlZWWVFNnEyNEVWVWlUeTF5TW9STThzN3FKaUo3MjlUOFRKbWc5R3NXVVd0bkJyczVZUTNPZTROZUxkMXNHK1RJRm03WHFieDVpbldCeXNFZk9IUmZ4eFhrOHo2UWVHS2lKeGg1UVJ0TVdScnBuSVVrUDFJSWNuckxBcTVmNmFqMnF2Zk1sYWVNQzBmM2E4TUtnN25aZlBsTGdWemp5a24rb0xNYm54UWVNV25xVk5qNTdIdDBDRFNOVkVPbmdVbVFoQ2dCSjJ0V29XQ0I3Rmltb2JxaFd3L2xXWE4vNTVuSENncWhOTnI2cHRxa2RKd3d2WVljYys3N05DR3ltZEQ1OGVFS0phdzA0U0dSZ1doWXFPY3dJREFRQUIifQ.klziBEUktQtwYJheT6g_YqV00t7wa2lrlKw4834HW6HuBwYOHjxN5UVMeM3BSepkVD9Xcgpj3DV0CIBK9RlAavBTZ1vRTNgQCdvu9e0CVCCgg4lC_pAbeRyzr5wC9ZhqrFnxZPYakNBbPNnILufSA4SZw-e4CP1Yy71a6sxPMiblGOj3qOcdOB_w3pA9JoQnZZDiYu2_M3A0j95-3RzaXmSOxspaPoQdSWsF_imT-swtld1ykDRVI0OBwdnpFrSePW4C2mVC_fGGgwBR-Zp0U6MM8keV3clvWHsA70KpY9TC43l2OgWIRumenzrxUmsGduTohxTd2RhiAfViUDo6mg";
        String virsionLicense2 = "eyJsaWNlbnNlX3YiOiIyLjAifQ.eyJleHBpcmF0aW9uIjoiMjAyNS0wOS0xOSIsInV1aWQiOiI0ZjdhYmFjMy1kMDcyLTQxOGItYWRlOS0wYWYzY2MwNTY2NGIiLCJjdXN0b21lcmlkIjoiYXNkIiwic29sdXRpb24iOiJPUElUIiwicHVibGljS2V5IjoiTUlJQklqQU5CZ2txaGtpRzl3MEJBUUVGQUFPQ0FROEFNSUlCQ2dLQ0FRRUF6VlNPNmJmSTVpYzNJaXpvU0JUVmNrZEdPWjRqVldDT2EyZW10bGpmanVCL0FXRTIvQVpXTy83aUxUY3lqcWxDekxxVjFaSW1SbTBHNHJZMWNqOVJibFpqNk5DZHN5QkpmNHkyamVCY3ppMEZERkJFSWxVZEwycS94SUhBMG85SWY5QnpYSUNMb2luYUhCWUUrVkprSy8xTXFvT3ZOY3ZJMFExVFhjeHJlVFVNOFZBSGxDRFVOTFVXdUs4K2VOMm5VNXF4MlRtU290eFByNnlqQkZUaUdyTzZLY0lsYWthb0lieENwZTh1Tk1tTVJQdmhFeGd4UkVwSmVBOHV3TFRoQkpnYzVJTEZmV1BXQUNyMzB5b0tXNEhyK2Zmb1oyNkFsLzlIR1BZcjMyOWloOHZYQlRtd2RXS3FyR1MwRUZVdGViTHJ5a0lxNWlRVGNTTU5RZXFwK1FJREFRQUIifQ.ZUrVsOcu-GX8vfiC_G3MKCkvjOnD-Z232nNIPXqgfbD0Bn_C4AaXE9zhar9SmxFy7OSX5NH5kqvdNAFsJNMznB8ZszjN8T0q6JuUGgc6eyWAFmKQxZyB4cQRf6u-t3Li5qgt9ccjlT8mok6FBktT0qkDMamF6SOkkHoDAIL7X3XEghR2NFMeefDAkDN4H-Ec5iD6LFkoLP0TR2RQ_HmkvkuQH-p6A4VQy2vII1jyQNybr2CUMpWtN3Z_VZmwmcWNi82avNG85JxPiff8NybFyh3QsnG7PnK8lBrrqTYAYredr7zWslZ-etvVuy6j--sETcxiJpHlJ8tMAjq1BCJnlw";
        String virsion1 = checker.getVirsion(virsionLicense1);
        String virsion2 = checker.getVirsion(virsionLicense2);
        assertTrue(virsion1.equals("1.0"), "올바른 버전(1.0)을 불러왔습니다.");
        assertTrue(virsion2.equals("2.0"), "올바른 버전(2.0)을 불러왔습니다.");
    }

    @Test
    @DisplayName("빈 문자열이나 null 키는 검증에 실패해야 한다 (false 반환)")
    void check_WithEmptyOrNullKey_ShouldReturnFalse() {
        // --- 실행 및 검증 (Act & Assert) ---
        assertFalse(checker.check(""), "빈 문자열 키가 검증에 성공했습니다.");
        assertFalse(checker.check(null), "null 키가 검증에 성공했습니다.");
    }
}
