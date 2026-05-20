# 정적분석 워크플로우 기록

## 1. 정적분석 도구 선택

강의 자료(10주차 Lec 06 - Static & Exploratory Testing)에서 다룬 정적분석 도구 중 오픈소스 도구를 선택했다.

- **PMD 7.12.0**: Java 코드의 잠재 결함, 불필요 코드, 코드 스타일 문제를 탐지하는 정적분석 도구
- **CheckStyle 10.25.0**: Java 코딩 표준(Sun Checks) 준수 여부를 검사하는 도구

강의 자료에서 언급된 정적 분석 도구 목록:
- 오픈소스: xLint, PMD, CheckStyle, FindBugs
- 상용: Coverity, SonarQube, Klocwork

이 중 PMD와 CheckStyle은 설치가 간편하고 Java 단일 클래스에도 적용 가능하여 선택했다.

## 2. PMD 실행 및 결과

### 실행 명령

```powershell
pmd.bat check -d src\Triangle.java -R "category/java/bestpractices.xml,category/java/codestyle.xml,category/java/design.xml,category/java/errorprone.xml,category/java/performance.xml,category/java/security.xml" -f text --no-cache
```

### 수정 전 결과: 41건

전체 결과는 `evidence/pmd_full_result.txt`에 저장했다.

## 3. CheckStyle 실행 및 결과

### 실행 명령

```powershell
java -jar checkstyle.jar -c /sun_checks.xml src\Triangle.java
```

### 수정 전 결과: 48건 → 수정 후: 46건

수정 전 결과는 `evidence/checkstyle_result_before_fix.txt`, 수정 후 결과는 `evidence/checkstyle_result.txt`에 저장했다.

## 4. AI agent 분석: 경고별 원인과 수정 필요성

### 4.1 수정이 필요한 실질적 결함 (2건)

#### (1) UnusedAssignment — `triangle = null` (line 178)

- **원인**: `Triangle triangle = null;`로 선언 후 바로 try 블록에서 재할당한다. `= null` 초기화는 사용되지 않는다.
- **수정 필요성**: 높음. 불필요한 null 할당은 코드 가독성을 해치고, null 사용을 유발할 수 있다.
- **false alarm 가능성**: 없음. 실제로 사용되지 않는 할당이다.
- **수정**: `Triangle triangle;`로 변경

#### (2) AvoidCatchingGenericException — `catch (Exception e)` (line 186)

- **원인**: `main()`에서 `Integer.parseInt()`와 배열 접근 시 발생할 수 있는 예외를 `Exception`으로 포괄적으로 잡고 있다.
- **수정 필요성**: 높음. `Exception`을 잡으면 프로그래밍 오류(예: NullPointerException)까지 숨길 수 있다. 실제로 발생 가능한 예외만 명시해야 한다.
- **false alarm 가능성**: 없음. 명확한 안티패턴이다.
- **수정**: `catch (NumberFormatException | ArrayIndexOutOfBoundsException e)`로 변경

### 4.2 스타일 경고 — false alarm으로 판단 (나머지 39건)

#### ShortVariable (9건) — `s1`, `s2`, `s3`

- **원인**: PMD 기본 설정에서 3자 미만 변수명을 경고한다.
- **수정 필요성**: 없음. 삼각형의 세 변을 나타내는 `s1`, `s2`, `s3`은 수학적 관례에 부합하며, Javadoc에도 매개변수 설명이 있어 의미가 명확하다.
- **false alarm 가능성**: 높음. 도메인에서 관례적으로 사용하는 짧은 변수명이다.

#### MethodArgumentCouldBeFinal / LocalVariableCouldBeFinal (15건)

- **원인**: 재할당되지 않는 매개변수와 지역 변수에 `final`을 선언하지 않았다.
- **수정 필요성**: 낮음. `final` 사용은 팀의 코딩 스타일 선택이지 결함이 아니다. 이 프로젝트에서는 원본 코드의 스타일을 유지하는 것이 우선이다.
- **false alarm 가능성**: 높음. 스타일 선호도 차이일 뿐 실질적 문제가 아니다.

#### OnlyOneReturn (7건)

- **원인**: `classify()`, `getArea()`, `isRightAngled()`, `isImpossible()`, `main()`에서 여러 개의 return 문을 사용한다.
- **수정 필요성**: 없음. guard clause 패턴(조건 불만족 시 조기 반환)은 현대 Java에서 권장되는 가독성 향상 기법이다.
- **false alarm 가능성**: 높음. 이 규칙 자체가 논쟁적이며, PMD 커뮤니티에서도 비활성화를 권장하는 규칙이다.

#### SystemPrintln (5건)

- **원인**: `main()` 메소드에서 `System.out.println()`을 사용한다.
- **수정 필요성**: 없음. `main()`은 CLI 진입점이므로 표준 출력 사용이 적절하다. 라이브러리 코드에서의 `System.out` 사용과는 다르다.
- **false alarm 가능성**: 높음. 규칙의 의도(라이브러리에서 System.out 금지)와 적용 대상(CLI main)이 다르다.

#### NoPackage (1건)

- **원인**: 패키지 선언 없이 기본 패키지에 클래스가 존재한다.
- **수정 필요성**: 없음. 과제용 단일 클래스이며, 패키지 추가 시 테스트 코드도 동시에 수정해야 한다. 원본 인터페이스 유지가 과제 요구사항이다.
- **false alarm 가능성**: 높음. 이 맥락에서는 해당 없다.

#### LinguisticNaming (1건)

- **원인**: `setSideLengths()`가 `Triangle`을 반환하는데, setter 이름은 보통 void를 반환해야 한다.
- **수정 필요성**: 없음. 이것은 fluent API(메소드 체이닝) 패턴으로, 원본 코드의 의도적인 설계이다.
- **false alarm 가능성**: 높음. fluent API 패턴은 이 규칙의 예외에 해당한다.

### 4.3 CheckStyle 경고 분석

CheckStyle의 수정 전 48건은 Sun Checks 코딩 표준과의 스타일 차이다. MultipleVariableDeclarations(1건)과 RegexpSingleline(3건) 수정 후 46건.

- **LeftCurly (14건)**: 코드가 Allman 스타일(중괄호 다음 줄)을 사용하고, Sun Checks는 K&R 스타일(중괄호 같은 줄)을 요구한다. 이것은 스타일 선택이다.
- **FinalParameters (8건)**: PMD의 MethodArgumentCouldBeFinal과 동일한 이유로 false alarm이다.
- **Javadoc 관련 (8건)**: private 필드에 대한 Javadoc은 과제 수준에서 불필요하다. 상수 필드의 의미는 이름과 값으로 자명하다.
- **LineLength (5건)**: 80자 제한을 약간 초과하는 줄이 있다. 현대적 코딩 환경에서 80자 제한은 과도하게 엄격하다.
- **MultipleVariableDeclarations (1건)**: PMD의 OneDeclarationPerLine과 동일. 수정 적용했다.
- **RegexpSingleline (3건)**: trailing space. 수정 적용했다.

## 5. 최종 판단 요약

| 분류 | 건수 | 조치 |
|------|------|------|
| 실질적 결함 (수정) | 2건 | UnusedAssignment, AvoidCatchingGenericException |
| 코드 정리 (수정) | 2건 | MultipleVariableDeclarations, trailing space |
| 스타일 경고 (유지) | 85건 | 원본 코드 스타일 존중, 과제 맥락에서 적절 |

수정 후 PMD: 41건 → 39건
수정 후 CheckStyle: 48건 → 46건
JUnit 회귀 테스트: 40개 모두 통과 (기존 33개 + 커버리지 개선 7개)

## 6. JaCoCo 커버리지 측정

### 실행 명령

```powershell
# JaCoCo agent를 붙여 JUnit 실행
java -javaagent:tools\jacoco\lib\jacocoagent.jar=destfile=build\jacoco.exec -jar tools\junit-platform-console-standalone-1.10.2.jar execute --class-path "build\classes;build\test-classes" --scan-class-path

# 리포트 생성
java -jar tools\jacoco\lib\jacococli.jar report build\jacoco.exec --classfiles build\classes --sourcefiles src --html evidence\jacoco-report
java -jar tools\jacoco\lib\jacococli.jar report build\jacoco.exec --classfiles build\classes --sourcefiles src --csv evidence\jacoco_coverage.csv
```

### 결과

전체 결과는 `evidence/jacoco_coverage_result.txt`에 저장했다.
HTML 리포트는 `evidence/jacoco-report/index.html`에서 확인할 수 있다.

초기 결과 (33개 테스트):

| 지표 | 커버리지 |
|------|----------|
| Instruction | 74% (185/249) |
| Branch | 91% (33/36) |
| Line | 62% (31/50) |
| Method | 70% (7/10) |

테스트 추가 후 최종 결과 (40개 테스트):

| 지표 | 이전 | 이후 | 변화 |
|------|------|------|------|
| Instruction | 74% | 83% | +9% |
| Branch | 91% | 94% | +3% |
| Line | 62% | 74% | +12% |
| Method | 70% | 90% | +20% |

미커버 메소드: `main()`만 남음 (의도적 제외). main() 제외 시 Line 100%.
부분 커버: `isImpossible()` Branch 83% (단축 평가로 인한 미커버)
