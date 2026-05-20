# AI Agent 활용 기록

## 1. 사용한 AI agent 또는 도구

사용 도구:
- Claude Code (Claude Opus 4.6)
- PMD 7.12.0
- CheckStyle 10.25.0
- JaCoCo 0.8.12
- JUnit 5 Console Launcher 1.10.2
- Eclipse Temurin OpenJDK 17.0.19

사용 환경:
- Windows 11 환경에서 Claude Code가 정적분석 도구 다운로드, 실행, 결과 분석, 코드 수정, 회귀 테스트를 수행했다.
- Java가 시스템 PATH에 없어 winget으로 Eclipse Temurin JDK 17을 설치했다.
- PMD와 CheckStyle은 공식 GitHub 릴리스에서 다운로드했다.

## 2. AI agent에게 부여한 주요 역할

- 과제2 코드의 정적분석 환경 구축
- PMD 및 CheckStyle 실행
- 정적분석 결과의 경고별 원인 분석
- 각 경고의 수정 필요성 판단 (실질적 결함 vs false alarm)
- 실질적 결함에 대한 코드 수정 제안 및 적용
- JaCoCo 커버리지 측정 환경 구축 및 실행
- JaCoCo 리포트 분석 (메소드별 커버리지, 미커버 영역 원인 분석)
- 수정 후 JUnit 회귀 테스트 실행
- 워크플로우 기록 및 보고서 작성 보조

## 3. 사용한 프롬프트 또는 명령 예시

과제 시작 시 부여한 지시:

```text
새로운 과제를 부여받았어. 레포트/과제2 를 재수행 해야하는 과제야(과제 이름은 '과제2-재수행').
지시 사항은 레포트/과제2-재수행 폴더에 적혀있어. 7주차부터 수업시간에 언급하신 내용들에 새로운 과제에 대한 내용들이 있고 원본인 '과제2'를 보고 '과제2-재수행'폴더에서 과제를 진행하면 돼.
```

JaCoCo 커버리지 추가 시 부여한 지시:

```text
교수님이 11주차 목에서 JaCoCo 커버리지 측정을 설치하고 확인하라고 했는데, 이것을 과제2-재수행에 추가하자.
```

AI agent가 수행한 주요 명령:

```powershell
# PMD 정적분석 실행
pmd.bat check -d src\Triangle.java -R "category/java/bestpractices.xml,category/java/codestyle.xml,category/java/design.xml,category/java/errorprone.xml,category/java/performance.xml,category/java/security.xml" -f text --no-cache

# CheckStyle 실행
java -jar checkstyle.jar -c /sun_checks.xml src\Triangle.java

# JaCoCo 커버리지 측정 (JUnit 테스트에 JaCoCo agent 연결)
java -javaagent:tools\jacoco\lib\jacocoagent.jar=destfile=build\jacoco.exec -jar tools\junit-platform-console-standalone-1.10.2.jar execute --class-path "build\classes;build\test-classes" --scan-class-path

# JaCoCo HTML/CSV 리포트 생성
java -jar tools\jacoco\lib\jacococli.jar report build\jacoco.exec --classfiles build\classes --sourcefiles src --html evidence\jacoco-report
java -jar tools\jacoco\lib\jacococli.jar report build\jacoco.exec --classfiles build\classes --sourcefiles src --csv evidence\jacoco_coverage.csv

# JUnit 테스트 실행
javac -encoding UTF-8 -d build\classes src\Triangle.java
javac -encoding UTF-8 -cp "junit-platform-console-standalone-1.10.2.jar;build\classes" -d build\test-classes tests\TriangleTest.java
java -jar junit-platform-console-standalone-1.10.2.jar execute --disable-ansi-colors --details-theme=ascii --class-path "build\classes;build\test-classes" --scan-class-path
```

## 4. AI agent의 분석 결과 검토

### AI agent가 수정을 제안한 항목 (수락)

1. **UnusedAssignment (`triangle = null`)**: AI agent가 `= null` 초기화가 불필요하다고 분석했다. 실제로 try 블록에서 바로 재할당하므로 정확한 분석이다. 수정을 수락했다.

2. **AvoidCatchingGenericException (`catch (Exception e)`)**: AI agent가 `Exception` 대신 실제 발생 가능한 `NumberFormatException | ArrayIndexOutOfBoundsException`을 잡아야 한다고 분석했다. 프로그래밍 오류를 숨길 위험이 있으므로 수정을 수락했다.

3. **MultipleVariableDeclarations**: AI agent가 `private int side1, side2, side3;`을 한 줄에 한 변수씩 분리하도록 제안했다. 가독성 향상과 diff 명확성을 위해 수정을 수락했다.

### AI agent가 false alarm으로 판단한 항목 (동의)

1. **ShortVariable (`s1`, `s2`, `s3`)**: 삼각형 변의 수학적 관례라는 분석에 동의한다. Javadoc에 매개변수 설명이 있으므로 충분히 명확하다.

2. **OnlyOneReturn**: guard clause 패턴이 더 가독성이 좋다는 분석에 동의한다. `classify()` 메소드에서 조기 반환을 제거하면 오히려 중첩이 깊어져 가독성이 나빠진다.

3. **SystemPrintln**: `main()`은 CLI 진입점이므로 `System.out.println()` 사용이 적절하다는 분석에 동의한다.

4. **LeftCurly (CheckStyle)**: Allman vs K&R 스타일은 팀 규약의 문제이지 결함이 아니라는 분석에 동의한다. 원본 코드의 일관된 Allman 스타일을 유지하는 것이 맞다.

### AI agent의 분석을 그대로 쓰지 않고 수정한 사례

- AI agent는 모든 `MethodArgumentCouldBeFinal` 경고를 일괄적으로 false alarm 처리했으나, `final` 키워드 추가가 코드 의도를 명확히 하는 경우도 있을 수 있다. 다만 이 과제에서는 원본 코드 스타일 유지가 우선이므로 AI agent의 판단을 수용했다.
- CheckStyle의 `LineLength` 경고(80자 초과)에 대해 AI agent는 현대적 환경에서 과도하다고 분석했다. 120자 기준이라면 모두 통과하므로 동의하지만, 기업 환경에서는 80자 제한을 따르는 곳도 있음을 참고한다.

## 5. 검토 내용

정적분석에서 수정한 부분:
- `UnusedAssignment`과 `AvoidCatchingGenericException`은 강의(10주차)에서 다룬 인스펙션 오류 체크리스트의 '데이터 참조 오류'와 '제어 흐름 오류'에 해당한다. 불필요한 null 초기화는 초기화 관련 결함 가능성을, 포괄적 예외 처리는 제어 흐름 오류 은폐 가능성을 높인다.
- `MultipleVariableDeclarations`는 인스펙션 체크리스트의 '데이터 선언 오류' 범주에서 가독성과 유지보수성을 해치는 패턴으로 확인된다.

JaCoCo 커버리지 분석에서 확인한 부분:
- AI agent가 JaCoCo 리포트를 받아 메소드별 커버리지를 분석했다. 핵심 비즈니스 로직(`classify`, `getArea`, `isImpossible`, `isRightAngled`)이 Instruction 95~100%로 충분히 커버되어 있다는 분석에 동의한다.
- `main()` 미커버가 적절하다는 판단(Javadoc에 "no unit tests need to be written" 명시)에 동의한다.
- `isImpossible()`의 Branch 83% 원인이 Java의 단축 평가(short-circuit evaluation)라는 분석이 정확했다. `(1,2,3)` 입력 시 `s1+s2<=s3`에서 바로 true가 되어 뒤 조건이 평가되지 않는다.
- 교수님이 11주차 목에서 "자코코가 결과를 내면 리포트를 내보내거든요. 그 리포트를 에이전트가 받아 가지고 현재 커버리지가 얼마고 어디가 커버되지 않았는지에 대한 체크를 한다"라고 언급한 워크플로우를 실제로 수행했다.

정적분석에서 유지한 부분:
- 대부분의 스타일 경고는 프로젝트의 코딩 규약 차이에서 발생한다. PMD와 CheckStyle의 기본 규칙 세트는 대규모 프로젝트를 대상으로 하므로, 과제 수준의 단일 클래스에는 과도한 경고가 발생할 수 있다.
- 실제 기업 환경에서는 프로젝트에 맞는 커스텀 규칙 세트를 정의하여 false alarm을 줄인다.

정적분석이 TDD 워크플로우에 기여한 점:
- TDD의 Red-Green-Refactor만으로는 발견하기 어려운 코드 품질 문제(불필요 초기화, 포괄적 예외 처리)를 정적분석이 추가로 발견했다.
- 정적분석 결과를 AI agent에게 검토시킴으로써 각 경고의 맥락을 파악하고 수정 여부를 효율적으로 판단할 수 있었다.
- 수정 후 JUnit 회귀 테스트가 모두 통과하여 기능 유지를 확인했다.

## 6. 검증 체크리스트

### PMD 경고별 검증 (41건 → 39건)

| 규칙명 | 건수 | AI agent 판단 | 최종 판단 | 근거 |
|--------|------|---------------|---------------|------|
| ShortVariable | 9 | false alarm | **동의** | s1,s2,s3은 삼각형 변의 수학적 관례. Javadoc에 설명 있음 |
| MethodArgumentCouldBeFinal | 8 | false alarm | **동의** | final 사용은 팀 규약. 원본 스타일 유지 우선 |
| OnlyOneReturn | 7 | false alarm | **동의** | guard clause 패턴은 현대 Java 권장 기법 |
| LocalVariableCouldBeFinal | 7 | false alarm | **동의** | MethodArgumentCouldBeFinal과 동일 근거 |
| SystemPrintln | 5 | false alarm | **동의** | main()은 CLI 진입점. 라이브러리 코드가 아님 |
| UnusedAssignment | 1 | **수정** | **동의** | try 블록에서 바로 재할당. 인스펙션 체크리스트 '데이터 참조 오류' |
| AvoidCatchingGenericException | 1 | **수정** | **동의** | NullPointerException 등 프로그래밍 오류 은폐 위험. 인스펙션 체크리스트 '제어 흐름 오류' |
| NoPackage | 1 | false alarm | **동의** | 과제용 단일 클래스. 패키지 추가 시 테스트도 수정 필요 |
| LinguisticNaming | 1 | false alarm | **동의** | fluent API 패턴(return this)은 의도적 설계 |
| OneDeclarationPerLine | 1 | **수정** | **동의** | 한 줄에 한 변수. diff 추적 명확성. 인스펙션 체크리스트 '데이터 선언 오류' |

### CheckStyle 경고별 검증 (48건 → 46건)

| 규칙명 | 건수 | AI agent 판단 | 최종 판단 | 근거 |
|--------|------|---------------|---------------|------|
| LeftCurly | 14 | false alarm | **동의** | Allman 스타일 일관 사용. 스타일 선택이지 결함 아님 |
| FinalParameters | 8 | false alarm | **동의** | PMD의 MethodArgumentCouldBeFinal과 동일 |
| JavadocVariable | 8 | false alarm | **동의** | private 필드/상수에 Javadoc 불요. 이름과 값으로 의미 자명 |
| LineLength | 5 | false alarm | **동의** | 80자 제한은 과도. 현대 환경에서 120자 기준 적용 가능 |
| RegexpSingleline | 3→0 | **수정** | **동의** | trailing space 제거. 코드 정리 |
| MultipleVariableDeclarations | 1→0 | **수정** | **동의** | PMD OneDeclarationPerLine과 동일 |
| FileTabCharacter | 1 | false alarm | **동의** | 원본 코드가 탭 사용. 전체 변환은 범위 밖 |
| RightCurly | 1 | false alarm | **동의** | Allman 스타일의 try-catch 형식. LeftCurly와 동일 근거 |
| JavadocPackage | 1 | false alarm | **동의** | NoPackage와 동일 근거 |
| JavadocStyle | 1 | false alarm | **동의** | Javadoc 마침표 누락은 원본 유지 |
| JavadocMethod | 1 | false alarm | **동의** | main()의 @param args 누락은 원본 유지 |

### JaCoCo 커버리지 검증

| 메소드 | AI agent 판단 | 최종 판단 | 조치 |
|--------|---------------|---------------|------|
| classify() 100% | 완전 커버 | **동의** | 유지 |
| getArea() 100% | 완전 커버 | **동의** | 유지 |
| isImpossible() Branch 83% | 단축 평가 원인, 기능 위험 낮음 | **동의** | 유지 |
| isRightAngled() Branch 87% | guard 분기 미커버, 위험 낮음 | **일부 수정** | 테스트 추가하여 100% 달성 |
| setSideLengths() 0% | 공개 API이나 테스트 없음 | **수정** | 테스트 2개 추가 |
| getSideLengths() 0% | main()에서만 호출 | **수정** | 테스트 2개 추가 |
| getPerimeter() 100% | 완전 커버 | **보완** | 불가능한 삼각형 동작 검증 테스트 추가, Javadoc을 현재 동작에 맞게 수정 |
| main() 0% | 의도적 제외 | **동의** | Javadoc에 테스트 불요 명시 |
