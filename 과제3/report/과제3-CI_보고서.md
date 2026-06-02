# 과제3 – CI: getTypeFlags + Mocking 단위시험 + 통합시험 + GitHub Actions

## 1. 사용한 AI agent 또는 도구

### 사용한 도구

| 도구                      | 버전                   | 역할                         |
| ----------------------- | -------------------- | -------------------------- |
| Claude Code             | Opus-4.8             | 오케스트레이터, 코드 구현, 보고서 작성     |
| Hermes                  | GPT-5.5              | cross-family 설계 리뷰 및 최종 검수 |
| Codex                   | GPT-5.5              | 구현 코드 리뷰                   |
| Claude Code             | Sonnet-4.6           | 보조 문서 작성                   |
| Gradle                  | 8.10.2               | 빌드 도구 (Gradle Wrapper)     |
| JUnit 5                 | junit-jupiter 5.11.3 | 테스트 프레임워크                  |
| JUnit Platform Suite    | —                    | `@Suite` 기반 통합 테스트 집계      |
| Mockito                 | 5.18.0               | Mock 객체 생성 (단위시험 격리)       |
| GitHub Actions          | —                    | CI (push/PR 시 자동 빌드·테스트)   |
| Eclipse Temurin OpenJDK | 17                   | Java 런타임                   |

### 사용 목적

| 목적 | 강의 연계 |
|------|-----------|
| 기존 `classify()`에 더해 복수 속성을 동시에 반환하는 `getTypeFlags()` 추가 | 13주차 |
| Mockito mock을 활용한 단위시험 격리 | 11주차 Test Double |
| `@Suite` 기반 단위→통합 단계 분리 | 12·13주차 Integration Testing |
| GitHub Actions로 CI 파이프라인 구축, push/PR 시 build success 확인 | 13주차 CI |

### AI agent 역할 분담

| Agent | 부여한 역할 |
|-------|-------------|
| Claude Code (Opus 4.8) | 지시사항·강의자료(11/12/13주차) 분석, `getTypeFlags()`·mocking 구조 설계, 단위·통합 테스트 및 Gradle/CI 구현, 보고서 작성 |
| Hermes (GPT-5.5) | cross-family 설계 검수 (인터페이스 주입·반환형·통합 구조·대면검사 방어력) |
| Codex (GPT-5.5) | 구현 코드 리뷰 (Mockito 사용·빌드 설정·엣지케이스) |
| Claude Code (Sonnet 4.6) | README·AI 활용 기록 문서 작성 |
| 엄격 평가 에이전트 | 지시사항 요구·강의 기준 대비 최종 감사 |

## 2. 개발 대상 및 과제2-재수행과의 관계

개발 대상:
- 삼각형 유형 판정 프로그램 (`Triangle.java`)

과제2-재수행에서 가져온 산출물:
- `Triangle.java`: TDD와 정적분석을 거친 삼각형 판정 코드 (`classify()`, `isRightAngled()`, `isImpossible()`, `getArea()`, `getPerimeter()` 등)
- `TriangleTest.java`: 40개 JUnit 5 테스트

과제3에서 추가한 것:
- **`getTypeFlags()`**: 기존 `classify()`는 대표 유형 하나만 문자열로 반환한다. `getTypeFlags()`는 삼각형의 복수 속성(모양 + 직각 여부)을 `EnumSet<TriangleType>`로 함께 반환한다.
- **Mocking 기반 단위시험**: `getTypeFlags()`의 직각 판정 의존성을 인터페이스로 분리하고, Mockito mock으로 대체하여 격리 검증한다.
- **통합시험**: Mock을 실제 구현으로 교체하는 단계별 통합 테스트를 `@Suite`로 구성한다.
- **CI**: Gradle + GitHub Actions로 push/PR 시 빌드·테스트를 자동 수행한다.

지시사항이 요구한 4가지 예시:

| 입력 | 기대 결과 |
|------|-----------|
| (3, 3, 3) | equilateral |
| (3, 3, 4) | isosceles |
| (3, 4, 5) | right-angled, scalene |
| (4, 5, 6) | scalene |

## 3. getTypeFlags() 설계

### 3.1 반환형 — EnumSet<TriangleType>

`getTypeFlags()`는 `EnumSet<TriangleType>`을 반환한다. `TriangleType`은 다음 enum이다.

```java
public enum TriangleType {
    EQUILATERAL, ISOSCELES, SCALENE, RIGHT_ANGLED, IMPOSSIBLE
}
```

문자열 집합(`Set<String>`) 대신 enum을 선택한 이유:
- 타입 안전성: 오타가 컴파일 단계에서 걸린다.
- 중복 불가: `EnumSet`은 같은 값을 두 번 담지 않는다.
- 비교 용이: `assertEquals(EnumSet.of(...), ...)`로 테스트가 명료하다.

### 3.2 판정 규칙

- **impossible**: 삼각형이 성립하지 않으면 `{IMPOSSIBLE}`만 반환한다. 모양·직각 flag를 붙이지 않는다. (기존 `classify()`의 impossible 의미 보존)
- **모양 flag (상호 배타, 정확히 하나)**:
  - 세 변이 모두 같으면 `EQUILATERAL`
  - 두 변만 같으면 `ISOSCELES`
  - 모두 다르면 `SCALENE`
- **직각 flag (독립 추가)**: 직각삼각형이면 `RIGHT_ANGLED`를 추가한다.

4가지 예시 검증:

| 입력 | getTypeFlags() | 지시사항 |
|------|----------------|----------|
| (3, 3, 3) | {EQUILATERAL} | equilateral |
| (3, 3, 4) | {ISOSCELES} | isosceles |
| (3, 4, 5) | {SCALENE, RIGHT_ANGLED} | right-angled, scalene |
| (4, 5, 6) | {SCALENE} | scalene |

`equilateral`을 `isosceles`로 중복 표기하지 않는 것은 기존 `classify()` 로직(정삼각형이면 equilateral만 반환)과 일관성을 유지하기 위함이다.

## 4. Mocking 기반 단위시험 (강의 11주차)

### 4.1 mocking을 위한 의존성 분리

강의 11주차(Test Levels — Unit Testing)에서 단위 테스트는 **테스트 대상 단위를 격리하여** 수행하며, SUT가 의존하는 외부 구성요소(DOC, Depended-on Component)는 **Test Double(Stub/Mock/Fake)**로 대체한다고 배웠다. 강의의 Mockito 예시는 다음과 같았다.

```java
// 강의 예시 — interface DogFood를 Dog가 의존, mock으로 격리
DogFood mockedDogFood = mock(DogFood.class);
when(mockedDogFood.eat()).thenReturn(13);
int returnVal = d.eatDinner(mockedDogFood);
```

이 패턴을 그대로 적용하기 위해, 직각 판정 로직을 인터페이스로 분리했다.

```java
public interface RightAngleChecker {
    boolean isRightAngled(int s1, int s2, int s3);
}

public class RealRightAngleChecker implements RightAngleChecker {
    public boolean isRightAngled(int s1, int s2, int s3) {
        long a = (long) s1 * s1, b = (long) s2 * s2, c = (long) s3 * s3;
        return a + b == c || a + c == b || b + c == a;
    }
}
```

`Triangle`은 `RightAngleChecker`를 **생성자로 주입**받는다.

```java
public Triangle(int s1, int s2, int s3) {            // 실제 사용 — 진짜 구현
    this(s1, s2, s3, new RealRightAngleChecker());
}

Triangle(int s1, int s2, int s3, RightAngleChecker checker) {  // 시험용(package-private) — mock 주입
    ...
    this.rightAngleChecker = checker;
}
```

이는 강의 13주차 Calculator 예시에서 `Divider(IMultiplier multiplier, ISubstractor subtractor)`가 의존성을 생성자로 주입받아 단위시험에서 mock으로 대체한 구조와 동일하다.

### 4.2 단위시험 — TriangleGetTypeFlagsUnitTest

`RightAngleChecker`를 Mockito mock으로 대체하여, `getTypeFlags()`의 **flag 조합 로직만 격리 검증**한다.

```java
@Test
void getTypeFlagsReturnsScaleneAndRightAngledWhenMockSaysRight() {
    RightAngleChecker checker = mock(RightAngleChecker.class);
    when(checker.isRightAngled(3, 4, 5)).thenReturn(true);

    Triangle triangle = new Triangle(3, 4, 5, checker);

    assertEquals(EnumSet.of(TriangleType.SCALENE, TriangleType.RIGHT_ANGLED),
            triangle.getTypeFlags());
    verify(checker).isRightAngled(3, 4, 5);
}
```

핵심은 mock이 **실제 기하 계산과 무관하게** 직각 여부를 통제한다는 점이다. 예를 들어 실제로 직각삼각형인 (3,4,5)에 대해 mock이 `false`를 반환하도록 stub하면 결과는 `{SCALENE}`이 된다. 이는 `getTypeFlags()`의 직각 flag가 (실제 계산이 아니라) 주입된 의존성으로부터 온다는 것을 격리 검증한다 — 즉 단위시험이 `RealRightAngleChecker`의 정확성에 의존하지 않는다.

단위시험 케이스 (총 9개):

| 케이스 | mock stub | 기대 flags | 검증 목적 |
|--------|-----------|-----------|-----------|
| (3,4,5) | right=true | {SCALENE, RIGHT_ANGLED} | 직각+부등변 조합 |
| (3,4,5) | right=false | {SCALENE} | 격리(실제 직각이어도 mock 따름) |
| (5,5,5) | right=false | {EQUILATERAL} | 정삼각형 |
| (3,3,4) | right=false | {ISOSCELES} | 이등변 |
| (3,3,4) | right=true | {ISOSCELES, RIGHT_ANGLED} | 조합 로직 격리 |
| (1,2,3) | — | {IMPOSSIBLE} | 불가능 + checker 미호출(`verify(never())`) |
| (0,1,1) | — | {IMPOSSIBLE} | 경계: 변=0 |
| (-1,2,2) | — | {IMPOSSIBLE} | 경계: 음수 변 |
| (1,2,2) | right=false | {ISOSCELES} | 경계: 막 성립하는 삼각형 |

불가능한 삼각형의 경우 `getTypeFlags()`가 일찍 반환하여 checker를 호출하지 않음을 `verify(checker, never()).isRightAngled(...)`로 검증한다. 이는 "직각 판정은 유효한 삼각형에만 의미가 있다"는 설계 의도를 시험으로 고정한 것이다.

### 4.3 leaf 단위시험 — RealRightAngleCheckerTest

강의 13주차 Calculator 예시에서 의존성이 없는 leaf 단위(`Adder`, `Flipper`)는 mock 없이 실제 객체로 직접 테스트했다.

```java
class AdderTest {   // 강의 예시
    private IAdder adder = new Adder();
    ...
}
```

`RealRightAngleChecker`도 의존성이 없는 leaf 단위이므로 실제 객체로 직접 단위 테스트한다 (피타고라스 판정, 변 순서 무관, 큰 값 오버플로 안전성 포함, 9개 케이스).

## 5. 통합시험 (강의 12·13주차)

### 5.1 통합시험 설계 — 단위→통합 단계 분리

강의 12주차(Integration Testing)에서 통합 시 진행 사항은 다음과 같았다.

> 1. Test Double들이 실제 모듈로 대체된다.
> 2. 인터페이스 관련 테스트 케이스들이 추가되고 실행된다.

강의 13주차(Lec 07-3)는 이를 `@Suite`와 `@SelectClasses`로 단계화했다. Calculator 예시는 Step 1(자식 전부 mock) → Step 2(한 층 real로 교체) → Step 3(전부 real)로 진행하며, "테스트 케이스는 단위 테스트와 동일"하게 유지했다.

본 과제의 단위는 `Triangle`(SUT)과 `RightAngleChecker`(DOC) 두 개이므로 두 단계로 매핑한다.

```java
@Suite
@SelectClasses({
    FirstStepOfIntegrationTest.class,
    SecondStepOfIntegrationTest.class
})
class TriangleIntegrationTest {
}
```

- **FirstStepOfIntegrationTest**: `Triangle` + **mock** `RightAngleChecker`. mock은 `thenAnswer`로 실제 피타고라스 동작을 흉내낸다 (강의에서 mock `IMultiplier`가 `thenAnswer`로 `arg1*arg2`를 반환한 것과 동일). 테스트 케이스는 단위시험과 동일.
- **SecondStepOfIntegrationTest**: `Triangle` + **실제** `RealRightAngleChecker` (공개 생성자). 단위 케이스 + **통합 전용 추가 케이스**.

### 5.2 단위/통합 분리

강의 13주차는 build.gradle에서 통합 단계 클래스를 default test 태스크에서 제외하여 단위시험과 통합시험을 분리했다.

```gradle
test {
    useJUnitPlatform()
    exclude '**/FirstStepOfIntegrationTest.class'
    exclude '**/SecondStepOfIntegrationTest.class'
}
```

이렇게 하면 단위시험 클래스는 정상적으로 실행되고, 통합 단계 클래스는 `@Suite`(`TriangleIntegrationTest`)를 통해서만 실행되어 중복 실행이 방지된다. 실제로 `./gradlew test --tests TriangleIntegrationTest` 실행 시 suite가 First/Second 단계를 실행함을 확인했다.

### 5.3 통합 전용 추가 케이스

지시사항은 "단위시험 케이스 + 통합용 추가 케이스"를 요구한다. SecondStep(실제 구현)에만 다음 추가 케이스를 둔다 — 이들은 **실제 `RealRightAngleChecker`의 계산이 동작해야만** 통과한다.

| 추가 케이스 | 기대 | 통합 검증 의미 |
|-------------|------|----------------|
| (20, 21, 29) | {SCALENE, RIGHT_ANGLED} | 실제 피타고라스 판정 |
| (30000, 40000, 50000) | {SCALENE, RIGHT_ANGLED} | 큰 값 오버플로 안전성(long 연산) |
| (9, 10, 11) | {SCALENE} | 직각이 아닌 부등변 |
| 일관성 검증 | — | `getTypeFlags()`의 RIGHT_ANGLED ⇔ 실제 `isRightAngled()`, impossible ⇔ `isImpossible()` |

일관성 검증 테스트는 `getTypeFlags()`가 실제 `classify()`/`isRightAngled()`/`isImpossible()` 파이프라인과 모순 없이 통합됨을 확인한다 (강의 12주차의 "명세와의 모순을 보이는 것"이 통합시험 목표).

## 6. CI 구축 (강의 13주차)

### 6.1 Gradle 프로젝트

강의 13주차의 "해야 할 일"은 다음과 같았다.
- Gradle/Maven 프로젝트 생성
- JUnit 5 설정
- GitHub Actions workflow 생성
- push/PR 시 build, test 실행하도록 설정

강의 예시가 Gradle 기반(build.gradle, gradle.yml)이므로 Gradle을 선택했다. 의존성은 강의 예시를 따른다.

```gradle
dependencies {
    testImplementation platform('org.junit:junit-bom:5.11.3')
    testImplementation 'org.junit.jupiter:junit-jupiter'
    testImplementation 'org.junit.platform:junit-platform-suite'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
    testImplementation 'org.mockito:mockito-core:5.18.0'
}
```

### 6.2 GitHub Actions workflow

`.github/workflows/gradle.yml` — main 브랜치 push 혹은 pull request 시 빌드·테스트를 자동 수행한다.

```yaml
name: Java CI with Gradle
on:
  push:
    branches: [ "main" ]
  pull_request:
    branches: [ "main" ]
jobs:
  build:
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: "과제3"
    steps:
    - uses: actions/checkout@v4
    - name: Set up JDK 17
      uses: actions/setup-java@v4
      with:
        java-version: '17'
        distribution: 'temurin'
    - name: Setup Gradle
      uses: gradle/actions/setup-gradle@v4
    - name: Build with Gradle Wrapper
      run: ./gradlew build
```

저장소 루트에 과제2-재수행과 과제3 폴더가 함께 있으므로, `defaults.run.working-directory: 과제3`으로 `./gradlew build`를 과제3 폴더에서 실행한다. (GitHub Actions는 workflow를 저장소 루트의 `.github/workflows/`에서만 읽으므로, gradle.yml은 과제3 폴더가 아니라 루트에 둔다. 과제3 폴더에는 `ci/gradle.yml`로 동일 파일의 참조 사본을 둔다.)

테스트가 모두 통과하면 빌드가 성공(build success)하고, 하나라도 실패하면 빌드가 실패한다. 이는 강의 13주차에서 "테스트 통과로 인한 빌드 성공 / 테스트 실패로 인한 빌드 에러"로 설명한 CI의 핵심 동작이다.

## 7. 강의 내용과의 연관

| 강의 | 적용 내용 |
|------|-----------|
| 11주차 — Unit Testing | Test Double(Mock), Mockito `mock()/when()/verify()`, SUT-DOC 격리. `RightAngleChecker` mock으로 `getTypeFlags()` 격리 검증 |
| 11주차 — Test Isolation | 시험 대상이 아닌 상태(실제 기하 계산)가 결과에 영향을 주지 않도록 mock 사용 |
| 12주차 — Integration Testing | Test Double을 실제 모듈로 교체 + 인터페이스 케이스 추가. 점진적(incremental) 통합 |
| 13주차 — CI | `@Suite`/`@SelectClasses` 단위·통합 분리, Gradle, GitHub Actions, push/PR build success |
| 13주차 — Calculator 예시 | interface+impl, 생성자 의존성 주입, leaf 단위는 real, mock은 상위 단위 — 구조 그대로 적용 |

## 8. 최종 테스트 결과

테스트 실행 환경:

| 항목 | 값 |
|------|-----|
| Java | Eclipse Temurin OpenJDK 17 |
| 빌드 | Gradle 8.10.2 (Gradle Wrapper) |
| 프레임워크 | JUnit 5 (junit-jupiter 5.11.3), JUnit Platform Suite |
| Mock | Mockito 5.18.0 |

테스트 구성:

| 분류 | 테스트 클래스 | 개수 | 설명 |
|------|---------------|------|------|
| 단위 | TriangleGetTypeFlagsUnitTest | 9 | `getTypeFlags()` — RightAngleChecker mock 격리 |
| 단위 | RealRightAngleCheckerTest | 9 | leaf 단위 — 실제 피타고라스 판정 |
| 단위(회귀) | TriangleTest | 40 | 과제2-재수행 기존 테스트 (classify 등) |
| 통합 Step1 | FirstStepOfIntegrationTest | 9 | Triangle + mock checker |
| 통합 Step2 | SecondStepOfIntegrationTest | 18 | Triangle + 실제 checker + 추가/일관성 |
| 통합 집계 | TriangleIntegrationTest | (@Suite) | Step1·Step2 집계 |

최종 결과:
- 전체 테스트: **85개**
- 통과: **85개**
- 실패: **0개**
- 빌드: **BUILD SUCCESSFUL**
- 증빙: `evidence/gradle_build_result.txt`, `evidence/test-report/`

## 9. 남은 한계 또는 개선 가능성

| 범주 | 한계 / 개선 가능성 |
|------|--------------------|
| 설계 | 본 과제의 단위는 `Triangle`과 `RightAngleChecker` 둘뿐이라, 강의 Calculator 예시의 3단계 통합을 2단계로 매핑했다. 의존성이 더 많았다면 mock을 점진적으로 교체하는 단계가 늘어났을 것이다. |
| 설계 | `getTypeFlags()`는 직각 판정만 의존성으로 분리했다. 모양 판정(equilateral/isosceles/scalene)까지 분리하면 mocking 범위가 넓어지지만, 단순 정수 비교를 인터페이스로 추상화하는 것은 과설계로 판단했다. |
| Mocking | 직각이등변삼각형은 정수 변으로 존재하지 않으므로(빗변이 무리수), `{ISOSCELES, RIGHT_ANGLED}` 조합은 mock으로만 단위 검증했다. 이는 mocking이 실제로는 불가능한 입력 조합까지 flag 조합 로직을 격리 검증할 수 있다는 장점을 보여준다. |
| CI | 단일 모듈이라 빌드가 빠르지만, 대규모 프로젝트에서는 캐싱·병렬화·테스트 분할이 추가로 필요하다. |
| CI | 커버리지 게이트(JaCoCo)나 정적분석을 CI에 추가하면 품질 게이트를 자동화할 수 있다 (과제2-재수행 도구와 연계 가능). |
