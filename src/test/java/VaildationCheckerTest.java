import org.example.licensechecker.Checker.Handlers.CoreCountValidator;
import org.example.licensechecker.Checker.Handlers.ExpiryDateValidator;
import org.example.licensechecker.Checker.ValidationChecker;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ValidationCheckerTest {

    private final String TEST_PUBLIC_KEY = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlZ60iu0ucDxPVnP85UI/8OlUE2IUsZUzagOJsA0Rp7/K5UI/A04oMv3uqVTS4uZcHQGlt3vwbPNWjpKQhLDPY+buISr0950iUdKVKkx3SlknGxFhSfZ5yNBzRwgaSf8emGVzZP3fBtzma4g3HJ7TItK0yZiXKisN6L7f+GvjEy6luIhyUbgy8rzrboZx0lPx1hkhs/PSda/gSjHeEdN7eM1/mSRv4i+iIAYhkHtMTcTA4jx0gM2aWL+5kUoa2dK/Y/FEEJY3xQmxa6xEqZc/C7dFw0RFInBL/BbmivVCFjWsUcoa3hOkPWiOVBFlSItyXtaDkvdLF7ntXej6Hn/v0QIDAQAB\n";
    private final String SECRET_KEY ="adfajbdjkbqawoielbaksjdbvlilbaas";
    private ValidationChecker checker;

    @BeforeEach
    void setUp() {
        checker = new ValidationChecker.Builder()
                .withPublicKey(TEST_PUBLIC_KEY)
                .withSecretKey(SECRET_KEY)
                .addRule(new ExpiryDateValidator()) // 만료
                .addRule(new CoreCountValidator()) // 코어수
                .build();
    }

    @Test
    @DisplayName("유효한 라이선스 키는 검증에 성공해야 한다 (true 반환)")
    void check_WithValidLicenseKey_ShouldReturnTrue() {

        String validKey = "6UYFTV3F-4RI463IY-AT2DBMMF-IXDWDU42-4PY4GQVG-IY7AJYFV-GT36UWZ2-QPTYKPAJ-66YU42HH-6D4VKT5S-2TLAAEWP-T5GZUA53-6XGCBV3U-7PA66UYK-6GSO45WR-AHLT2OH2-HH5VAUXL-2VCBVI6A-OBD66EF3-J4APGIZ4-GPEBRRPQ-EIDHKLAF-YSPP3BV5-EOB3FDLT-AIAB6ZEB-EPDDRJWN-PX3K4W2Y-K7IYJYPN-VE7YEEHB-6YTSMPGF-GZBVKQNN-VJ6YKSN4-NGDEM62O-OXS5APIQ-U3FDSQF6-6MWL5UIH-FBFRNBHE-T3IAT5DJ-CFVWRFOD-EXP2NNJ3-HDJXXNAP-3TUKWOZV-3KP25IOV-PPEFWVZY-XGAXJ2TI-K5V7PPTV-R43PCPCL-MNKF6DDJ-RQNX4DUF-XYLUS6NJ-MV25HTQC-VPAAC2FP-KCOXEFRT-ENAPOSKI-HCIIAKXY-UZV5ZCVO-QOMFW2RN-7W6VJLQ4-CEG4JUKH-IQXV6JX7-CRCQVUB6-34UHLYFG-ISWDK7OJ-EMLJ4QHY-E7SGEEVG-43DC7MY0";
        
        boolean result = checker.check(validKey);
        
        assertTrue(result, "유효한 라이선스 키가 검증에 실패했습니다.");
    }

    @Test
    @DisplayName("위변조된 라이선스 키는 검증에 실패해야 한다 (false 반환)")
    void check_WithTamperedLicenseKey_ShouldReturnFalse() {
        
        String validKey = "6UYFTV3F-4RI463IY-AT2DBMMF-IXDWDU42-4PY4GQVG-IY7AJYFV-GT36UWZ2-QPTYKPAJ-66YU42HH-6D4VKT5S-2TLAAEWP-T5GZUA53-6XGCBV3U-7PA66UYK-6GSO45WR-AHLT2OH2-HH5VAUXL-2VCBVI6A-OBD66EF3-J4APGIZ4-GPEBRRPQ-EIDHKLAF-YSPP3BV5-EOB3FDLT-AIAB6ZEB-EPDDRJWN-PX3K4W2Y-K7IYJYPN-VE7YEEHB-6YTSMPGF-GZBVKQNN-VJ6YKSN4-NGDEM62O-OXS5APIQ-U3FDSQF6-6MWL5UIH-FBFRNBHE-T3IAT5DJ-CFVWRFOD-EXP2NNJ3-HDJXXNAP-3TUKWOZV-3KP25IOV-PPEFWVZY-XGAXJ2TI-K5V7PPTV-R43PCPCL-MNKF6DDJ-RQNX4DUF-XYLUS6NJ-MV25HTQC-VPAAC2FP-KCOXEFRT-ENAPOSKI-HCIIAKXY-UZV5ZCVO-QOMFW2RN-7W6VJLQ4-CEG4JUKH-IQXV6JX7-CRCQVUB6-34UHLYFG-ISWDK7OJ-EMLJ4QHY-E7SGEEVG-43DC7MY0";  String tamperedKey = validKey.substring(0, validKey.length() - 1) + "Z";
        
        boolean result = checker.check(tamperedKey);
        
        assertFalse(result, "위변조된 라이선스 키가 검증에 성공했습니다.");
    }

    @Test
    @DisplayName("만료된 라이선스 키는 검증에 실패해야 한다 (false 반환)")
    void check_WithExpiredLicenseKey_ShouldReturnFalse() {
        
        String expiredKey = "FL2KMXSY-GCQ3YBKI-FFWSGA7O-7LAQANAN-6TYLHTGR-WDWNIRMC-TBYPFGQ2-CHEQ6MO3-RXFTNKQ5-NCXYV2XS-66C4COYK-M4VOOROU-BKNIQI25-GEVZTKQ4-XVXKRXWD-LSZK3BR2-YGVQGB3N-XGNVGHK5-DSFRXXPD-ZC5R2BCY-YDT7YMAG-VEVK3Z67-VLYWHGKW-DQMNDSJU-2GTF3CWM-YG4V7HT3-VFLIJYUT-PPTEF5XU-4SYBXC2N-UKXD22AE-PK55NXIP-OGPELE5M-4ZFXNAWK-SZS23KA6-K4RIVGIA-KUQGRCFV-KZGEYQQB-3T7VBQZQ-DEOJ4TJI-NHQALEDZ-MLEPBM53-7ZKDIIR7-ZRYU4WYP-LFZ2J3MR-3TCSHK73-QHLOF3EH-BHQYVAUS-D6DR3YP4-3Q3XPT22-L6MOYKPH-N3IXDMLN-PJ5GFQFT-ILN3WTB7-LDEYM6EK-67GYJIYQ-GDW7CSLZ-CKVHIGLK-WQFJ7VSE-2CQNQ732-XL24YBDK-VIZ6RWBE-4EVTZV2Q-35ISK65V-6E4FM6SZ-DIT5I3QD-QO6LXBI0";

        boolean result = checker.check(expiredKey);

        assertFalse(result, "만료된 라이선스 키가 검증에 성공했습니다.");
    }

    @Test
    @DisplayName("코어 수가 시스템 사양을 초과하는 라이선스는 실패해야 한다 (false 반환)")
    void check_WithExceededCoreCountLicense_ShouldReturnFalse() {
        String exceededCoreKey = "6T7XUP3Y-BKJAPHNY-7J32RD2J-FUWIPUPO-JU7A3AGF-LQHLYXJD-RU6BQF6P-V6V6GLYB-2ZXIXVEG-QB2Q4HOW-PYXJSV6Y-4XXMT6XV-LUGFI6TJ-JFFNLOJH-N62DGOQU-EMPGFRGN-JAMBX5JJ-MHPDRC7E-HNAH2FTY-YYGJVE55-NZTC2D3H-PT2WJLU4-DNIU5RHR-ORYEFTQF-63MEMVBO-EOHPGLCI-ALPLHO3Q-BCBDWXFS-UYIXFV2G-ZF4EVUB4-ZWVG4PCK-DJBH74YY-JBWCNIOO-NEU5ERIU-TG2TAEAM-66DFRNBG-R5EOCHSO-QBE34UTQ-YHELA7DW-6ACAT2KT-VISD2ETC-TESPBHXG-HVY3MCU3-EAUWPQW7-YBK2JJXS-SNXF2LVJ-OLHCWNDB-CVIUQZMM-YJNX3ACD-RZQKLVCP-B6QOALMT-IPJLFZRX-QPDS5Q44-3FLRHBJJ-FYX2EYCS-SC5HO5GQ-4KOMYRYQ-TL4FWS5A-CAE7CG56-OOYMOE35-EYUOEBT6-2W5ZTJI3-IT46KXQV-DO3I6X4E-7VZ3LFHC-JMWKUZA0";
      
        boolean result = checker.check(exceededCoreKey);
        
        assertFalse(result, "코어 수 초과 라이선스가 검증에 성공했습니다.");
    }

    @Test
    @DisplayName("빈 문자열이나 null 키는 검증에 실패해야 한다 (false 반환)")
    void check_WithEmptyOrNullKey_ShouldReturnFalse() {
        // --- 실행 및 검증 (Act & Assert) ---
        assertFalse(checker.check(""), "빈 문자열 키가 검증에 성공했습니다.");
        assertFalse(checker.check(null), "null 키가 검증에 성공했습니다.");
    }
}
