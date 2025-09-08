import com.google.protobuf.gradle.*

plugins {
    `java-library` // 스프링 부트 대신 'java-library' 플러그인 사용
    id("com.google.protobuf") version "0.9.4"

}

group = "com.example.licenseChecker"
version = "1.0.0" // 라이브러리 버전

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // API 의존성 (이 라이브러리를 사용하는 프로젝트에게 전파됨)
    api("com.google.protobuf:protobuf-java:4.27.2")
    api("commons-codec:commons-codec:1.16.0")

    // 테스트용 의존성
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.25.3"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}