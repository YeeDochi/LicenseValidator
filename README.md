# Java 라이선스 검증 라이브러리 

## ✨ 주요 기능

-   **자체 검증 가능한 키 구조**: 라이선스 키는 `헤더.본문.서명`의 세 부분으로 구성됩니다. 서명 검증에 필요한 **공개키는 본문(Body) 내에 포함**되어 있어, 라이선스 키 하나만으로 무결성 검증이 가능합니다.
-   **디지털 서명 검증**: RSA 및 SHA256 알고리즘을 사용하여 라이선스 키가 변조되지 않았는지 확인합니다.
-   **유연한 검증 규칙**: 빌더(Builder) 패턴을 통해 만료일 검사 등 필요한 검증 규칙을 동적으로 추가할 수 있습니다.
-   **쉬운 통합**: 간단한 API를 통해 모든 Java 프로젝트에 쉽게 통합하여 사용할 수 있습니다.

---

## 🏗️ 프로젝트 구조

-   **`DTO`**: 라이선스 키의 데이터 구조를 정의하는 Record 클래스들을 포함합니다.
    -   `LicenseHeader`: 라이선스 버전(`license_v`) 정보를 담습니다.
    -   `LicenseBody`: 라이선스 만료일, 고객 정보, 그리고 **공개키** 등 핵심 데이터를 담습니다.
-   **`Checker`**: 전체 검증 프로세스를 관리하고 구성합니다.
    -   `LicenseSignatureChecker`: 라이선스의 암호화 서명을 해독하고 유효성을 검사하는 핵심 로직을 담당합니다.
    -   `ValidationChecker`: 빌더를 통해 생성되며, 서명 검증 후 추가적인 규칙(핸들러)들을 순차적으로 실행합니다.
-   **`Checker.Handlers`**: 개별 검증 규칙을 정의하는 인터페이스와 구현체들을 포함합니다.
    -   `LicenseValidator`: 모든 검증 핸들러가 구현해야 하는 공통 인터페이스입니다.
    -   `ExpiryDateValidator`: 라이선스의 만료일을 검증하는 구현체입니다.

---

## 🛠️ 작동 방식

1.  **빌더(Builder) 생성**: `ValidationChecker.Builder`를 생성합니다. 이 버전에서는 **별도로 공개키를 전달할 필요가 없습니다.**
2.  **검증 규칙 추가**: 필요한 검증 규칙(예: `ExpiryDateValidator`)을 빌더에 추가합니다.
3.  **`ValidationChecker` 생성**: `build()` 메서드를 호출하여 `ValidationChecker` 인스턴스를 생성합니다.
4.  **라이선스 검증**: `check()` 메서드에 검증할 라이선스 키(시리얼)를 전달합니다.
5.  **내부 처리 과정**:
    -   `LicenseSignatureChecker`가 라이선스 키를 점(`.`) 기준으로 헤더, 본문, 서명 세 부분으로 분리합니다.
    -   Base64Url로 인코딩된 본문을 디코딩하여 JSON 데이터를 객체(`LicenseBody`)로 변환합니다.
    -   `LicenseBody` 객체 안에서 **공개키 문자열을 추출**합니다.
    -   추출된 공개키를 사용하여 서명이 유효한지 검증합니다. 서명이 유효하지 않으면 즉시 예외가 발생하며 실패 처리됩니다.
    -   서명이 유효하면, 빌더에 추가된 각 `LicenseValidator` 핸들러들이 순서대로 실행됩니다. 예를 들어 `ExpiryDateValidator`는 현재 날짜와 라이선스의 만료일을 비교합니다.
    -   하나의 규칙이라도 실패하면 전체 검증은 실패로 간주됩니다.
6.  **결과 반환**: 모든 서명 검증과 규칙 검사를 통과하면 `true`를, 그렇지 않으면 `false`를 반환합니다.

---

## 🚀 사용 예시

라이브러리를 사용하여 라이선스 키를 검증하는 방법은 다음과 같습니다.

~~~java
import org.example.licensechecker.Checker.Handlers.ExpiryDateValidator;
import org.example.licensechecker.Checker.ValidationChecker;

public class Main {
    public static void main(String[] args) {
        // 1. 검증할 라이선스 키
        // 이 키의 Body 부분에는 서명 검증에 필요한 공개키가 포함되어 있습니다.
        String licenseKey = "eyJsaWNlbnNlX3YiOiIxLjAifQ.eyJleHBpcmF0aW9uIjoiMjA5OS0xMi0zMSIsInV1aWQiOiJ0ZXN0LXV1aWQiLCJjdXN0b21lcmlkIjoidGVzdC1jdXN0b21lciIsInNvbHV0aW9uIjoiTXlBcHAiLCJwdWJsaWNLZXkiOiJNSUlCSWpBTkJna3Foa2lHOXcwQkFRRUZBQU9DQVE4QU1JSUJDZ0tDQVFFQT...In0.signature-part";

        // 2. 빌더를 사용하여 검증기 생성 및 규칙 추가
        // 별도의 공개키를 제공할 필요가 없습니다.
        ValidationChecker checker = new ValidationChecker.Builder()
                .addRule(new ExpiryDateValidator()) // 만료일 검증 규칙 추가
                // .addRule(new YourCustomValidator()) // 필요시 다른 검증 규칙을 직접 구현하여 추가할 수 있습니다.
                .build();

        // 3. 라이선스 키 검증 실행
        boolean isValid = checker.check(licenseKey);

        // 4. 결과 확인
        if (isValid) {
            System.out.println("라이선스가 유효합니다. 애플리케이션을 시작합니다.");
        } else {
            System.out.println("유효하지 않은 라이선스입니다. 애플리케이션을 종료합니다.");
        }

        // 버전 정보 가져오기 (선택 사항)
        try {
            String version = checker.getVirsion(licenseKey);
            System.out.println("라이선스 버전: " + version);
        } catch (Exception e) {
            System.err.println("버전 정보를 가져오는 데 실패했습니다.");
        }
    }
}
~~~