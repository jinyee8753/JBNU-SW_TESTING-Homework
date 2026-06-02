# 과제3 – CI (getTypeFlags + Mocking 단위시험 + 통합시험 + GitHub Actions CI)

## 개요

과제2-재수행의 Triangle 프로그램에 `getTypeFlags()` 메서드를 추가하고, Mockito 기반 mock 단위시험, JUnit 5 `@Suite` 기반 통합시험, Gradle + GitHub Actions CI 파이프라인을 구축했다.

## 과제2-재수행과의 관계

과제2-재수행에서 구현한 `classify()` 메서드는 삼각형의 대표 유형 1개를 반환한다. 과제3에서는 이에 더해 복수의 속성(정삼각형, 이등변삼각형, 직각삼각형 등)을 동시에 반환하는 `getTypeFlags()`를 추가했다. 반환 타입은 `EnumSet<TriangleType>`이다.

기존 40개의 테스트는 회귀 테스트로 그대로 유지하며, 신규 테스트가 추가되어 총 85개의 테스트를 구성한다.

## 폴더 구조

```
과제3/
├── src/
│   ├── main/java/
│   │   ├── Triangle.java              # 핵심 구현 (classify, getTypeFlags)
│   │   ├── TriangleType.java          # 삼각형 유형 enum
│   │   ├── RightAngleChecker.java     # 직각 판정 인터페이스
│   │   └── RealRightAngleChecker.java # RightAngleChecker 실제 구현
│   └── test/java/
│       ├── TriangleTest.java                  # 기존 40개 회귀 테스트
│       ├── TriangleGetTypeFlagsUnitTest.java  # getTypeFlags 단위시험 (mock 기반)
│       ├── RealRightAngleCheckerTest.java     # leaf 클래스 단위시험 (real)
│       ├── FirstStepOfIntegrationTest.java    # 통합시험 1단계 (mock 사용)
│       ├── SecondStepOfIntegrationTest.java   # 통합시험 2단계 (real 사용)
│       └── TriangleIntegrationTest.java       # @Suite 통합시험 진입점
├── ci/
│   └── gradle.yml                    # CI 워크플로 참조본 (실제 활성본은 저장소 루트 .github/workflows/gradle.yml)
├── gradle/wrapper/                   # Gradle Wrapper
├── build.gradle
├── settings.gradle
├── gradlew / gradlew.bat
├── .gitignore
├── 지시사항.md                       # 과제3 지시사항
├── README.md
├── ai_interaction_log.md            # AI agent 활용 기록
├── evidence/                         # 빌드·테스트 결과 증빙
└── report/                           # 제출 보고서
```

## 빌드 및 실행

JDK 17 이상이 필요하다.

```bash
# 컴파일 + 전체 테스트 실행
./gradlew build

# 테스트만 실행
./gradlew test

# 삼각형 판정 실행 (예: 3 4 5)
./gradlew run --args="3 4 5"
```

## 테스트 구성

| 분류 | 클래스 | 방식 | 설명 |
|------|--------|------|------|
| 단위시험 | `TriangleGetTypeFlagsUnitTest` | Mockito mock | `RightAngleChecker`를 mock으로 주입, `getTypeFlags()` 검증 |
| 단위시험 | `RealRightAngleCheckerTest` | real (leaf) | 의존성 없는 leaf 클래스 직접 테스트 |
| 단위시험 | `TriangleTest` | real | 기존 40개 회귀 테스트 |
| 통합시험 | `FirstStepOfIntegrationTest` (via `@Suite`) | mock | 1단계: mock 협력자로 통합 흐름 검증 |
| 통합시험 | `SecondStepOfIntegrationTest` (via `@Suite`) | real | 2단계: 실제 구현체로 종단간 검증 |

`build.gradle`의 `test { exclude }` 설정으로 통합시험 단계 클래스(`FirstStepOfIntegrationTest`, `SecondStepOfIntegrationTest`)가 단위 테스트 태스크에서 직접 실행되지 않고 `@Suite`(`TriangleIntegrationTest`)를 통해서만 실행되도록 분리한다.

## CI 구성

`ci/gradle.yml`은 실제 GitHub Actions 구성의 참조본이며, 실제 작동하는 워크플로우는 저장소 루트 `.github/workflows/gradle.yml`에 위치한다.

- 트리거: `main` 브랜치 push 및 Pull Request
- 환경: Ubuntu + Eclipse Temurin JDK 17
- 실행: `./gradlew build` (컴파일 + 전체 테스트)
- 테스트 전체 통과 = BUILD SUCCESSFUL

## 최종 결과

| 항목 | 값 |
|------|-----|
| 전체 테스트 | 85개 |
| 성공 | 85개 |
| 실패 | 0개 |
| 빌드 결과 | BUILD SUCCESSFUL |
| Java | 17 (Eclipse Temurin) |
| Gradle | 8.10.2 |
| JUnit | 5 |
| Mockito | 5.18.0 |
