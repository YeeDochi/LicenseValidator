# **라이선스 검증 라이브러리 (License Validator Library)**

## **1. 개요**

이 프로젝트는 프로토콜 버퍼(Protocol Buffers)와 비대칭 키 암호화(RSA)를 기반으로 하는 Java용 소프트웨어 라이선스 키 검증 라이브러리입니다. 


## **2. 주요 기능**

* 프로토콜 버퍼를 사용하여 라이선스 정보를 작고 효율적인 바이너리 형식으로 직렬화합니다.
* `BusinessRuleValidator` 인터페이스를 통해 만료일, 하드웨어 정보(MAC 주소, 보드 시리얼), CPU 코어 수 등 다양한 비즈니스 규칙을 조합하여 사용할 수 있습니다.
*  `BusinessRuleValidator` 인터페이스를 직접 구현하여, 프로젝트에 필요한 완전히 새로운 커스텀 검증 규칙을 라이브러리 수정 없이 쉽게 추가할 수 있습니다.

## **3. 설치 및 설정**

### **3.1. 의존성 추가**
이 라이브러리는 현재 로컬 `.jar` 파일을 직접 참조하는 방식으로 설정합니다.

1.  프로젝트의 `libs` 폴더를 생성하고, 이 라이브러리의 `.jar` 파일(예: `licenseChecker-1.0.0.jar`)을 복사합니다.
2.  사용할 프로젝트의 `build.gradle.kts` 파일에 아래 내용을 추가합니다.

~~~kotlin
dependencies {
    // 1. 라이브러리 JAR 파일 직접 참조
    implementation(files("libs/licenseChecker-1.0.0.jar"))

    // 2. 라이브러리가 필요로 하는 의존성들을 직접 추가
    // (추후 JitPack 또는 사내 Maven 저장소로 이전 시 이 부분은 불필요해짐)
    implementation("commons-codec:commons-codec:1.16.0")
    implementation("com.google.protobuf:protobuf-java:4.27.2")
}
~~~

### **3.2. (스프링 부트 환경) Bean으로 등록**
스프링 부트 환경에서는 `@Configuration` 클래스를 통해 `ValidationChecker`를 Bean으로 등록하여 편리하게 사용할 수 있습니다.

~~~java
@Configuration
public class LicenseCheckerConfig {

    @Value("${license.public-key}")
    private String publicKey;

    @Bean
    public ValidationChecker licenseChecker() {
        return new ValidationChecker.Builder()
                .withPublicKey(publicKey)
                .addRule(new ExpiryDateValidator())
                .addRule(new CoreCountValidator())
                .addRule(new MacAddressValidator())
                .build();
    }
}
~~~

## **4. 사용법**

`ValidationChecker.Builder`를 사용하여 필요한 공개키와 검증 규칙들을 설정한 후, `check()` 메소드에 라이선스 키 문자열을 전달하여 유효성을 검증합니다.

~~~java
// 스프링 환경이 아닐 경우
String publicKey = "MIIB..."; // 파일 등에서 읽어온 공개키
String licenseKey = "XXXX-XXXX-XXXX-...."; // 검증할 라이선스 키

// 1. 빌더를 통해 검증기 생성 및 설정
ValidationChecker checker = new ValidationChecker.Builder()
        .withPublicKey(publicKey)
        .addRule(new ExpiryDateValidator())
        .addRule(new MacAddressValidator())
        .build();

// 2. 라이선스 키 검증 실행
boolean isValid = checker.check(licenseKey);

if (isValid) {
    System.out.println("라이선스가 유효합니다.");
} else {
    System.out.println("유효하지 않은 라이선스입니다.");
}
~~~

## **5. 기본 제공 검증기 (Validators)**

* **`ExpiryDateValidator`**: 라이선스의 만료일을 현재 날짜와 비교하여 유효 기간을 검증합니다.
```java
public class ExpiryDateValidator implements LicenseValidator {
    @Override
    public boolean validate(LicenseProtos.License license) {
        String expiryDateStr = license.getExpireDate();

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
```
  
* **`CoreCountValidator`**: 시스템의 CPU 코어 수가 라이선스에 명시된 허용치를 초과하는지 검증합니다.
```java
public class CoreCountValidator implements LicenseValidator {

    @Override
    public boolean validate(LicenseProtos.License license) {
        int licensedCores = license.getCoreCount();

        // 체크해야할 항목이 0이라면 뭔가 잘못된것
        if (licensedCores <= 0) {
            return false;
        }
        // 실제 시스템의 코어 수를 가져옴
        int systemCores = Runtime.getRuntime().availableProcessors();
        System.out.println(systemCores +", "+ licensedCores);
        // 시스템의 코어 수가 라이선스에 허용된 코어 수보다 작거나 같아야 함
        return systemCores <= licensedCores;
    }

    @Override
    public String getErrorMessage() {
        return "시스템의 CPU 코어 수가 라이선스 허용치를 초과합니다.";
    }
}

```
  
* **`MacAddressValidator`**: 시스템의 MAC 주소가 라이선스에 귀속된 주소와 일치하는지 검증합니다.
```java
public class MacAddressValidator implements LicenseValidator {

    private final Set<String> systemMacAddresses;

    public MacAddressValidator() {
        this.systemMacAddresses = getSystemMacAddresses();
    }

    @Override
    public boolean validate(License license) {
        String licenseMac = license.getMacAddress().replaceAll("[-:]", "").toUpperCase();


        if (licenseMac.isEmpty()) {
            return true;
        }
        return systemMacAddresses.contains(licenseMac);
    }

    @Override
    public String getErrorMessage() {
        return "라이선스가 허가된 장비의 MAC 주소와 일치하지 않습니다.";
    }

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
```
  
* 해당 검증기들은 개발이 더 필요합니다.

## **6. 커스텀 검증기 만들기**

`LicenseValidator` 인터페이스를 구현하여 프로젝트에 특화된 자신만의 검증 규칙을 만들 수 있습니다.

~~~java
// 예시: 특정 제품 이름이 포함되어 있는지 검증하는 커스텀 Validator
public class ProductNameValidator implements LicenseValidator {
    
    @Override
    public boolean validate(License license) {
        // .proto에 productName 필드가 있다고 가정
        return "MyAwesomeApp".equals(license.getProductName());
    }

    @Override
    public String getErrorMessage() {
        return "라이선스가 이 제품을 위한 것이 아닙니다.";
    }
}

// 사용법
ValidationChecker customChecker = new ValidationChecker.Builder()
        .withPublicKey(publicKey)
        .addRule(new ProductNameValidator()) // 빌더에 직접 만든 검증기 추가
        .build();
~~~
