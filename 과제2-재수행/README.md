# 과제2-재수행: TDD + 정적분석 기반 삼각형 유형 판정

## 개요

과제2의 TDD 기반 삼각형 유형 판정 코드를 그대로 가져온 뒤, 정적분석(Static Analysis) 기반 코드 품질 검토 단계를 추가했다. PMD와 CheckStyle을 실행하여 잠재 결함, 코드 품질 문제, 스타일 위반을 확인하고, AI agent가 각 경고를 분석한 뒤 최종 판단하여 수정을 적용했다.

## 과제2와의 차이점

- 과제2: TDD 워크플로우(Red-Green-Refactor)만 수행
- 과제2-재수행: 과제2의 리팩터링 완료 코드에 **정적분석 및 커버리지 측정 단계를 추가**
  - PMD 7.12.0 (bestpractices, codestyle, design, errorprone, performance, security)
  - CheckStyle 10.25.0 (Sun Checks)
  - JaCoCo 0.8.12 (코드 커버리지 측정)
  - AI agent에 의한 경고 분석, 커버리지 분석, 수정 판단
  - 수정 후 JUnit 회귀 테스트

## 실행 방법

### 테스트 실행 (Windows PowerShell)

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot"
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

javac -encoding UTF-8 -d build\classes src\Triangle.java
javac -encoding UTF-8 -cp "tools\junit-platform-console-standalone-1.10.2.jar;build\classes" -d build\test-classes tests\TriangleTest.java
java -jar tools\junit-platform-console-standalone-1.10.2.jar execute --disable-ansi-colors --details-theme=ascii --class-path "build\classes;build\test-classes" --scan-class-path
```

### PMD 실행

```powershell
tools\pmd\pmd-bin-7.12.0\bin\pmd.bat check -d src\Triangle.java -R "category/java/bestpractices.xml,category/java/codestyle.xml,category/java/design.xml,category/java/errorprone.xml,category/java/performance.xml,category/java/security.xml" -f text --no-cache
```

### CheckStyle 실행

```powershell
java -jar tools\checkstyle\checkstyle.jar -c /sun_checks.xml src\Triangle.java
```

### JaCoCo 커버리지 측정

```powershell
# JaCoCo agent를 붙여 JUnit 테스트 실행
java -javaagent:tools\jacoco\lib\jacocoagent.jar=destfile=build\jacoco.exec -jar tools\junit-platform-console-standalone-1.10.2.jar execute --class-path "build\classes;build\test-classes" --scan-class-path

# HTML 리포트 생성
java -jar tools\jacoco\lib\jacococli.jar report build\jacoco.exec --classfiles build\classes --sourcefiles src --html evidence\jacoco-report
```

## 폴더 구조

- `src/`: 삼각형 유형 판정 구현 코드
- `tests/`: JUnit 5 테스트 코드
- `evidence/`: 정적분석 결과, 커버리지 리포트, 테스트 결과 증빙
- `report/`: 제출 및 발표용 보고서
- `tools/`: PMD, CheckStyle, JaCoCo, JUnit Console jar
- `TASK_BREAKDOWN.md`: 태스크 분할, 검증, 워크플로우 정의
- `tdd_workflow_log.md`: TDD 진행 기록
- `static_analysis_log.md`: 정적분석 워크플로우 기록
- `ai_interaction_log.md`: AI agent 활용 기록 및 검증 체크리스트

## 테스트 환경

- 언어: Java
- 테스트 프레임워크: JUnit 5
- Java 런타임: Eclipse Temurin OpenJDK 17.0.19
- 정적분석 도구: PMD 7.12.0, CheckStyle 10.25.0
- 커버리지 도구: JaCoCo 0.8.12

## 최종 결과

- 전체 테스트: 40개 (기존 33개 + 커버리지 개선 7개)
- 성공: 40개
- 실패: 0개
- PMD 경고: 수정 전 41건 → 수정 후 39건 (2건 실질 결함 수정, 39건 스타일 경고 유지)
- CheckStyle 경고: 수정 전 48건 → 수정 후 46건 (2건 코드 정리, 46건 스타일 경고 유지)
- JaCoCo 커버리지: Instruction 83%, Branch 94%, Line 74%, Method 90% (main() 제외 시 Line 100%)
