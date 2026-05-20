# 과제2-재수행: TDD + 정적분석 기반 삼각형 유형 판정

## 1. 사용한 AI agent 또는 도구

사용한 도구명:
- Claude Code (Claude Opus 4.6)
- PMD 7.12.0
- CheckStyle 10.25.0
- JaCoCo 0.8.12
- JUnit 5 Console Launcher 1.10.2
- Eclipse Temurin OpenJDK 17.0.19

사용 목적:
- 과제2 코드에 정적분석 단계를 추가하여 잠재 결함 발견
- 정적분석 결과의 AI agent 기반 자동 분류 (실질적 결함 vs false alarm)
- JaCoCo 커버리지 측정으로 테스트 충분성 확인
- 수정 후 JUnit 회귀 테스트로 기능 유지 확인
- 워크플로우 기록 및 보고서 작성 보조

AI agent에게 부여한 주요 역할:
- 정적분석 도구 설치 및 실행
- 경고별 원인 분석 및 수정 판단
- 코드 수정 적용
- JaCoCo 커버리지 측정 및 리포트 분석
- 회귀 테스트 실행
- 보고서 작성 보조

## 2. 개발 대상 및 과제2와의 관계

개발 대상:
- 삼각형 유형 판정 프로그램 (`Triangle.java`)

과제2에서 가져온 산출물:
- `src/Triangle.java`: TDD Red-Green-Refactor를 거쳐 33개 테스트를 모두 통과한 코드
- `tests/TriangleTest.java`: 정삼각형, 이등변, 부등변, 직각, 불가능한 삼각형 등 33개 JUnit 5 테스트

과제2-재수행에서 추가한 단계:
- 리팩터링 완료 코드에 대해 정적분석 도구(PMD, CheckStyle)를 실행
- AI agent가 정적분석 결과를 검토하여 각 경고의 원인, 수정 필요성, false alarm 가능성을 분석
- AI agent의 분석 결과를 직접 판단하고 수정 적용
- JUnit 테스트를 다시 실행하여 기존 기능 유지 확인

## 3. 정적분석 워크플로우 설명

### 3.1 정적분석 도구 선택 근거

강의 10주차(Static & Exploratory Testing)에서 소개된 정적 분석 도구 중 오픈소스 도구를 선택했다.

| 도구 | 버전 | 분석 초점 |
|------|------|-----------|
| PMD | 7.12.0 | 잠재 결함, 설계 문제, 불필요 코드 |
| CheckStyle | 10.25.0 | 코딩 표준 준수 (Sun Checks) |

선택 이유:
- 강의 자료에서 PMD, CheckStyle을 오픈소스 정적 분석 도구로 명시했다.
- Java 단일 클래스에도 명령줄로 간편하게 적용할 수 있다.
- PMD는 잠재 결함과 코드 설계 문제를, CheckStyle은 코딩 표준 위반을 각각 다른 관점에서 검사한다.

### 3.2 PMD 실행 결과

적용 규칙 세트: bestpractices, codestyle, design, errorprone, performance, security

**수정 전 결과: 41건**

경고 분류 요약:

| 규칙명 | 건수 | 카테고리 | 판정 |
|--------|------|----------|------|
| ShortVariable | 9 | codestyle | false alarm |
| MethodArgumentCouldBeFinal | 8 | codestyle | false alarm |
| OnlyOneReturn | 7 | codestyle | false alarm |
| LocalVariableCouldBeFinal | 7 | codestyle | false alarm |
| SystemPrintln | 5 | bestpractices | false alarm |
| UnusedAssignment | 1 | bestpractices | **수정** |
| AvoidCatchingGenericException | 1 | design | **수정** |
| NoPackage | 1 | codestyle | false alarm |
| LinguisticNaming | 1 | codestyle | false alarm |
| OneDeclarationPerLine | 1 | codestyle | **수정** |

### 3.3 CheckStyle 실행 결과

적용 규칙: Sun Checks (기본 제공)

**수정 전 결과: 48건 → 수정 후: 46건**

수정 전 48건에서 MultipleVariableDeclarations(1건)과 RegexpSingleline(3건)을 수정하여 4건 감소했으나, 변수 분리로 JavadocVariable이 2건 증가하여 최종 46건이다.

경고 분류 요약 (수정 후 46건 기준):

| 규칙명 | 건수 | 판정 |
|--------|------|------|
| LeftCurly (중괄호 스타일) | 14 | false alarm |
| JavadocVariable | 8 | false alarm |
| FinalParameters | 8 | false alarm |
| LineLength | 5 | false alarm |
| RightCurly | 1 | false alarm |
| FileTabCharacter | 1 | false alarm |
| JavadocPackage | 1 | false alarm |
| JavadocStyle | 1 | false alarm |
| JavadocMethod | 1 | false alarm |
| 기타 | 6 | false alarm |

### 3.4 AI agent 분석 및 판단

#### 수정이 필요한 실질적 결함

**1. UnusedAssignment — `Triangle triangle = null;`**

- AI agent 분석: `= null` 초기화는 try 블록에서 바로 재할당되므로 사용되지 않는다.
- 판단: 동의. 강의의 인스펙션 체크리스트에서 '초기화되지 않은 변수' 관련 항목과 연관된다. 불필요한 null 할당은 코드를 혼란스럽게 하므로 제거한다.
- 적용: `Triangle triangle;`로 변경

**2. AvoidCatchingGenericException — `catch (Exception e)`**

- AI agent 분석: `Exception`을 포괄적으로 잡으면 프로그래밍 오류(NullPointerException 등)까지 숨길 수 있다.
- 판단: 동의. 강의의 인스펙션 체크리스트에서 '제어 흐름 오류' 범주에 해당한다. `main()`에서 실제로 발생 가능한 예외는 `NumberFormatException`(잘못된 입력)과 `ArrayIndexOutOfBoundsException`(인자 부족)뿐이다.
- 적용: `catch (NumberFormatException | ArrayIndexOutOfBoundsException e)`로 변경

**3. OneDeclarationPerLine / MultipleVariableDeclarations**

- AI agent 분석: `private int side1, side2, side3;`는 한 줄에 여러 변수를 선언하여 가독성과 diff 추적을 해친다.
- 판단: 동의. 한 줄에 한 변수씩 선언하면 변경 이력 추적이 명확해진다.
- 적용: 세 줄로 분리

**4. Trailing space 제거**

- 적용: 메소드 사이 불필요한 공백 줄과 trailing space를 정리

#### false alarm으로 판단한 스타일 경고

**1. ShortVariable (`s1`, `s2`, `s3`)** — 삼각형 변의 수학적 관례로 도메인에서 충분히 명확하다. Javadoc에 매개변수 설명이 있다.

**2. OnlyOneReturn** — guard clause 패턴은 현대 Java에서 권장되는 가독성 향상 기법이다. `classify()` 메소드에서 조기 반환을 제거하면 중첩이 깊어져 오히려 가독성이 나빠진다.

**3. SystemPrintln** — `main()`은 CLI 진입점이므로 표준 출력 사용이 적절하다.

**4. LeftCurly (CheckStyle)** — 코드가 Allman 스타일을 일관되게 사용한다. Sun Checks는 K&R을 요구하지만 이것은 스타일 선택이지 결함이 아니다.

**5. MethodArgumentCouldBeFinal / FinalParameters** — `final` 사용은 팀 코딩 규약의 문제이다. 원본 코드 스타일 유지가 우선이다.

**6. NoPackage** — 과제용 단일 클래스에서 패키지 선언은 불필요하다.

**7. LinguisticNaming** — `setSideLengths()`의 `Triangle` 반환은 fluent API(메소드 체이닝) 패턴이다.

### 3.5 수정 후 회귀 테스트

수정 적용 후 JUnit 테스트를 다시 실행했다.

정적분석 수정 직후 결과 (기존 33개 테스트):
- 전체 테스트: 33개
- 통과: 33개
- 실패: 0개

정적분석 기반 수정이 기존 기능을 변경하지 않음을 확인했다.

수정 후 PMD 재실행:
- 수정 전: 41건
- 수정 후: 39건 (UnusedAssignment, AvoidCatchingGenericException 해결)

커버리지 개선 테스트 추가 후 최종 결과 (40개 테스트):
- 전체 테스트: 40개 (기존 33개 + 신규 7개)
- 통과: 40개
- 실패: 0개

### 3.6 JaCoCo 커버리지 측정

#### 도구 선택 근거

강의 11주차(목)에서 교수님이 커버리지 측정 도구로 JaCoCo를 명시했다.

> "커버리지 측정 도구를 설치하고 커버리지 측정되는 것까지는 일단 확인을 해야 돼요"
> "자코코가 결과를 내면 리포트를 내보내거든요. 그 리포트를 에이전트가 받아 가지고 현재 커버리지가 얼마고 어디가 커버되지 않았는지에 대한 체크를 한다"

| 도구 | 버전 | 용도 |
|------|------|------|
| JaCoCo | 0.8.12 | Java 바이트코드 기반 커버리지 측정 |

#### 실행 방법

JaCoCo agent를 JUnit 테스트 실행 시 `-javaagent` 옵션으로 연결하여 커버리지 데이터를 수집했다.

```
java -javaagent:jacocoagent.jar=destfile=jacoco.exec -jar junit-platform-console-standalone.jar ...
java -jar jacococli.jar report jacoco.exec --classfiles build/classes --sourcefiles src --html evidence/jacoco-report
```

#### 초기 커버리지 결과 (33개 테스트)

| 지표 | 커버된 수 / 전체 | 커버리지 |
|------|-----------------|----------|
| Instruction | 185 / 249 | **74%** |
| Branch | 33 / 36 | **91%** |
| Line | 31 / 50 | **62%** |
| Complexity | 22 / 28 | **78%** |
| Method | 7 / 10 | **70%** |

#### AI agent의 초기 커버리지 분석

- `main()` (0%): Javadoc에 "no unit tests need to be written for this method" 명시. 의도적 제외.
- `setSideLengths()` (0%): 공개 API이지만 테스트 미작성. 생성자만으로 삼각형을 생성하고 있어 검증되지 않은 상태.
- `getSideLengths()` (0%): `main()`에서만 호출되며 `main()` 자체가 미테스트.
- `isRightAngled()` Branch 87%: `if (isImpossible())` guard의 true 분기가 미실행.
- `isImpossible()` Branch 83%: 단축 평가(short-circuit)로 인해 2개 분기가 미실행.

#### 판단 및 테스트 추가

AI agent의 분석을 검토한 결과, `setSideLengths()`와 `getSideLengths()`는 공개 메소드이므로 테스트 추가가 필요하다고 판단했다. `isRightAngled()`의 guard 분기도 직접 테스트하기로 결정했다.

추가한 테스트 7개:

| # | 테스트명 | 목적 |
|---|----------|------|
| 1 | `setSideLengthsUpdatesClassification` | `setSideLengths()` 후 `classify()` 정상 동작 확인 |
| 2 | `setSideLengthsReturnsSameInstance` | fluent API 패턴(return this) 검증 |
| 3 | `getSideLengthsReturnsCommaSeparatedValues` | 반환 형식 검증 |
| 4 | `getSideLengthsReflectsSetSideLengths` | `setSideLengths()` 후 `getSideLengths()` 연동 확인 |
| 5 | `getPerimeterReturnsSum` | 기본 둘레 계산 검증 |
| 6 | `getPerimeterForImpossibleTriangleReturnsSum` | 불가능한 삼각형에서도 합을 반환하는 동작 검증 |
| 7 | `isRightAngledReturnsFalseForImpossibleTriangle` | `isRightAngled()` guard 분기 커버 |

#### 최종 커버리지 결과 (40개 테스트)

| 지표 | 이전 (33개) | 이후 (40개) | 변화 |
|------|------------|------------|------|
| Instruction | 74% (185/249) | **83%** (206/249) | +9% |
| Branch | 91% (33/36) | **94%** (34/36) | +3% |
| Line | 62% (31/50) | **74%** (37/50) | +12% |
| Method | 70% (7/10) | **90%** (9/10) | +20% |

#### 최종 메소드별 상세

| 메소드 | Instruction | Branch | 상태 |
|--------|-------------|--------|------|
| `classify()` | 100% | 100% | 완전 커버 |
| `getArea()` | 100% | 100% | 완전 커버 |
| `Triangle(int,int,int)` | 100% | n/a | 완전 커버 |
| `getPerimeter()` | 100% | n/a | 완전 커버 |
| `square(int)` | 100% | n/a | 완전 커버 |
| `setSideLengths()` | 100% | n/a | 완전 커버 (신규 테스트) |
| `getSideLengths()` | 100% | n/a | 완전 커버 (신규 테스트) |
| `isRightAngled()` | 100% | 100% | 완전 커버 (신규 테스트) |
| `isImpossible()` | 100% | 83% | 부분 커버 (단축 평가) |
| `main()` | 0% | n/a | 미커버 (의도적 제외) |

미커버 분석:
- `main()` (0%): 의도적 제외. `main()` 제외 시 Line 커버리지는 37/(50-13) = 100%.
- `isImpossible()` Branch 83%: 단축 평가로 인한 미커버. 기능적으로 모든 불가능 케이스가 정확히 판별되므로 위험은 낮음.

## 4. 강의 내용과의 연관

### 정적 테스팅의 이점 (강의 10주차)

이번 과제에서 정적분석이 제공한 이점:

1. **결함 조기 발견**: `AvoidCatchingGenericException`은 동적 테스트(JUnit)로는 발견하기 어려운 잠재 결함이다. 테스트가 모두 통과하더라도 예외 처리가 너무 포괄적이면 추후 예상치 못한 오류를 숨길 수 있다.

2. **코드 품질 직접 발견**: `UnusedAssignment`과 `MultipleVariableDeclarations`는 실행 결과에 영향을 주지 않지만 코드 품질과 유지보수성을 해친다.

3. **개발 생산성 향상**: AI agent가 89건의 정적분석 경고를 분류하는 데 소요된 시간은 수동 코드 리뷰보다 훨씬 짧았다.

### 커버리지 측정의 역할 (강의 11주차)

교수님이 11주차(목)에서 설명한 커버리지 측정의 목적:

1. **테스트 충분성 판단**: JaCoCo 커버리지 리포트를 통해 테스트가 핵심 비즈니스 로직을 충분히 검증하고 있음을 수치로 확인했다. 초기 33개 테스트에서 Branch 91%였으며, 테스트 추가 후 40개 테스트에서 Branch 94%로 향상되었다.

2. **미테스트 영역 인식**: `setSideLengths()`, `getSideLengths()`가 테스트되지 않고 있다는 사실을 커버리지 측정으로 발견했다. 이는 수동 코드 리뷰로도 발견할 수 있지만, JaCoCo가 자동으로 시각화해준다.

3. **AI agent와의 연동**: 교수님이 언급한 대로 JaCoCo 리포트를 AI agent에게 전달하여, 현재 커버리지 수치와 미커버 영역에 대한 분석을 받았다. AI agent는 미커버 메소드 중 `main()` 제외가 적절하다는 판단과, `isImpossible()`의 단축 평가로 인한 Branch 미커버 원인을 정확히 분석했다.

### 인스펙션 오류 체크리스트와의 대응 (강의 10주차)

| 체크리스트 카테고리 | 발견된 경고 | 수정 여부 |
|---------------------|-------------|-----------|
| 데이터 참조 오류 | UnusedAssignment | 수정 |
| 데이터 선언 오류 | MultipleVariableDeclarations | 수정 |
| 제어 흐름 오류 | AvoidCatchingGenericException | 수정 |
| 연산 오류 | 해당 없음 | - |
| 비교 오류 | 해당 없음 | - |
| 인터페이스 오류 | 해당 없음 | - |

## 5. 최종 테스트 결과

테스트 실행 환경:
- Java: Eclipse Temurin OpenJDK 17.0.19
- 테스트 프레임워크: JUnit 5
- 테스트 파일: `tests/TriangleTest.java`

최종 결과:
- 전체 테스트 개수: 40개 (기존 33개 + 커버리지 개선 7개)
- 통과한 테스트 개수: 40개
- 실패한 테스트 개수: 0개
- 전체 테스트 통과 여부: 통과
- 최종 결과 파일: `evidence/final_test_result.txt`

정적분석 최종 결과:
- PMD: 수정 전 41건 → 수정 후 39건 (실질 결함 2건 수정)
- CheckStyle: 수정 전 48건 → 수정 후 46건 (MultipleVariableDeclarations, trailing space 수정)

커버리지 측정 결과 (JaCoCo 0.8.12, 40개 테스트 기준):
- Instruction 커버리지: 83% (206/249)
- Branch 커버리지: 94% (34/36)
- Line 커버리지: 74% (37/50), main() 제외 시 100%
- Method 커버리지: 90% (9/10), main() 제외 시 100%
- 커버리지 리포트: `evidence/jacoco-report/index.html`

## 6. 남아 있는 한계 또는 개선 가능성

정적분석 도구 관련:
- PMD와 CheckStyle의 기본 규칙 세트는 대규모 프로젝트 기준이므로 과제 수준에서는 false alarm이 많이 발생한다.
- 실제 프로젝트에서는 커스텀 규칙 파일을 정의하여 프로젝트 맥락에 맞는 규칙만 적용해야 한다.
- SpotBugs(FindBugs 후속)나 Error Prone 같은 추가 도구를 병행하면 더 깊은 결함을 발견할 수 있다.

AI agent 활용 관련:
- AI agent의 경고 분류가 대부분 타당했으나, 모든 판단을 그대로 수용하는 것은 위험하다. 강의에서 배운 인스펙션 체크리스트 기준으로 교차 검증이 필요하다.
- AI agent는 코드의 의도와 도메인 맥락을 설명하면 false alarm 판별 정확도가 높아진다.

코드 자체 관련:
- `getPerimeter()`의 원본 Javadoc이 "invalid input이면 -1 반환"이라고 기술했지만 실제 코드는 항상 합을 반환하는 불일치가 있었다. 이번 과제에서 Javadoc을 현재 구현에 맞게 수정했다. 이 불일치는 정적분석 도구(PMD, CheckStyle)로는 탐지되지 않았으며, AI agent의 코드 리뷰와 커버리지 분석 과정에서 발견되었다.
- `Integer.MAX_VALUE` 근처 입력에 대한 `getPerimeter()` overflow 가능성은 기존 인터페이스(`int` 반환)의 구조적 한계이다.

## 7. 발표용 캡처 또는 증빙 자료 목록

저장된 증빙 자료:
- `evidence/pmd_full_result.txt`: PMD 수정 전 전체 결과 (41건)
- `evidence/pmd_after_fix_result.txt`: PMD 수정 후 전체 결과 (39건)
- `evidence/checkstyle_result_before_fix.txt`: CheckStyle 수정 전 결과 (48건)
- `evidence/checkstyle_result.txt`: CheckStyle 수정 후 결과 (46건)
- `evidence/final_test_result.txt`: 최종 JUnit 테스트 결과
- `evidence/jacoco_coverage.csv`: JaCoCo 커버리지 CSV 데이터
- `evidence/jacoco_coverage_result.txt`: JaCoCo 커버리지 분석 결과
- `evidence/jacoco-report/`: JaCoCo HTML 리포트 (소스코드 라인별 커버리지)
- `TASK_BREAKDOWN.md`: 태스크 분할, 검증, 워크플로우 정의
- `static_analysis_log.md`: 정적분석 워크플로우 상세 기록
- `ai_interaction_log.md`: AI agent 활용 기록 및 검증 체크리스트
- `tdd_workflow_log.md`: TDD + 정적분석 전체 워크플로우 기록
