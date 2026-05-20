# TDD 워크플로우 로그

## 개요

이 문서는 과제2에서 수행한 TDD 워크플로우를 요약하고, 과제2-재수행에서 추가한 정적분석 단계를 기록한다.

## 1. 요구사항 분석 (과제2에서 수행)

과제1 코드에서 확인한 결함:
- `isImpossible()`이 삼각형 부등식을 검사하지 않음
- `classify()`가 `side1 == side2`만 이등변으로 검사
- 이등변 반환 문자열 `isossceles` 오타
- `isRightAngled()`가 세 번째 입력만 빗변으로 가정
- `getArea()`가 불가능한 삼각형에서 NaN 반환

## 2. Red 단계 (과제2에서 수행)

- JUnit 5 테스트 33개 작성
- 과제1 원본 코드로 실행: 17개 통과, 16개 실패

## 3. Green 단계 (과제2에서 수행)

수정 내용:
- `isosceles` 오타 수정
- 이등변 조건을 모든 변 쌍으로 확장
- 삼각형 부등식 검사 추가
- 직각 판정을 모든 빗변 후보로 확장
- `long` 사용으로 큰 수 overflow 위험 감소

결과: 33개 테스트 모두 통과

## 4. Refactor 단계 (과제2에서 수행)

- `square(int value)` 헬퍼 메소드 분리
- `semiPerimeter` 변수 분리
- 기존 인터페이스 유지

결과: 33개 테스트 모두 통과

## 5. 정적분석 단계 (과제2-재수행에서 추가)

### 5.1 사용한 도구

| 도구 | 버전 | 용도 |
|------|------|------|
| PMD | 7.12.0 | 잠재 결함, 코드 스타일, 설계 문제 탐지 |
| CheckStyle | 10.25.0 | 코딩 표준 준수 여부 검사 |

### 5.2 PMD 실행 결과

적용한 규칙 세트: bestpractices, codestyle, design, errorprone, performance, security

수정 전 위반: 41건

주요 경고 분류:

| 분류 | 규칙명 | 건수 | 판정 |
|------|--------|------|------|
| 잠재 결함 | UnusedAssignment | 1 | 수정 |
| 잠재 결함 | AvoidCatchingGenericException | 1 | 수정 |
| 코드 스타일 | ShortVariable | 9 | false alarm |
| 코드 스타일 | MethodArgumentCouldBeFinal | 8 | false alarm |
| 코드 스타일 | LocalVariableCouldBeFinal | 7 | false alarm |
| 코드 스타일 | OnlyOneReturn | 7 | false alarm |
| 코드 스타일 | SystemPrintln | 5 | false alarm |
| 코드 스타일 | NoPackage | 1 | false alarm |
| 코드 스타일 | LinguisticNaming | 1 | false alarm |

수정 후 위반: 39건

### 5.3 CheckStyle 실행 결과

적용한 규칙: Sun Checks (기본 제공)

수정 전 위반: 48건 → 수정 후: 46건

MultipleVariableDeclarations(1건)과 RegexpSingleline(3건)을 수정하여 4건 감소했으나, 변수 분리로 JavadocVariable이 2건 증가하여 최종 46건이다.

주요 경고 분류 (수정 후 46건 기준):

| 분류 | 규칙명 | 건수 | 판정 |
|------|--------|------|------|
| 중괄호 스타일 | LeftCurly | 14 | false alarm |
| 매개변수 | FinalParameters | 8 | false alarm |
| Javadoc | JavadocVariable, JavadocPackage, JavadocMethod, JavadocStyle | 11 | false alarm |
| 줄 길이 | LineLength | 5 | false alarm |
| 기타 | FileTabCharacter, RightCurly 등 | 8 | false alarm |

### 5.4 수정 적용 내용

1. `private int side1, side2, side3;` → 한 줄에 한 변수씩 선언으로 분리
2. `Triangle triangle = null;` → `= null` 불필요 초기화 제거
3. `catch (Exception e)` → `catch (NumberFormatException | ArrayIndexOutOfBoundsException e)` 구체적 예외 처리
4. 메소드 사이 불필요한 빈 줄과 trailing space 제거

### 5.5 수정 후 회귀 테스트

정적분석 수정 직후: 33개 테스트 모두 통과. 정적분석 수정이 기존 기능을 변경하지 않음을 확인했다.
커버리지 개선 테스트 추가 후: 40개 테스트 모두 통과.

## 6. 커버리지 측정 단계 (과제2-재수행에서 추가)

### 6.1 사용한 도구

| 도구 | 버전 | 용도 |
|------|------|------|
| JaCoCo | 0.8.12 | Java 바이트코드 기반 코드 커버리지 측정 |

### 6.2 실행 방법

JaCoCo agent를 JVM 옵션으로 연결하여 JUnit 테스트 실행 시 커버리지 데이터를 수집했다.

```powershell
# JaCoCo agent를 붙여서 JUnit 테스트 실행
java -javaagent:tools\jacoco\lib\jacocoagent.jar=destfile=build\jacoco.exec -jar tools\junit-platform-console-standalone-1.10.2.jar execute --class-path "build\classes;build\test-classes" --scan-class-path

# HTML 리포트 생성
java -jar tools\jacoco\lib\jacococli.jar report build\jacoco.exec --classfiles build\classes --sourcefiles src --html evidence\jacoco-report

# CSV 리포트 생성
java -jar tools\jacoco\lib\jacococli.jar report build\jacoco.exec --classfiles build\classes --sourcefiles src --csv evidence\jacoco_coverage.csv
```

### 6.3 초기 커버리지 결과 (33개 테스트)

| 지표 | 커버된 수 / 전체 | 커버리지 |
|------|-----------------|----------|
| Instruction | 185 / 249 | 74% |
| Branch | 33 / 36 | 91% |
| Line | 31 / 50 | 62% |
| Method | 7 / 10 | 70% |

### 6.4 AI agent 분석 및 테스트 추가

- 핵심 비즈니스 로직은 충분히 커버됨
- 미커버 메소드: `main()` (의도적 제외), `setSideLengths()` (0%), `getSideLengths()` (0%)
- 판단: `setSideLengths()`, `getSideLengths()`는 공개 메소드이므로 테스트 추가 필요
- 7개 테스트 추가 (33개 → 40개)

### 6.5 최종 커버리지 결과 (40개 테스트)

| 지표 | 이전 | 이후 | 변화 |
|------|------|------|------|
| Instruction | 74% | 83% | +9% |
| Branch | 91% | 94% | +3% |
| Line | 62% | 74% | +12% |
| Method | 70% | 90% | +20% |

미커버: `main()`만 남음 (의도적 제외). main() 제외 시 Line 100%.
상세 분석: `evidence/jacoco_coverage_result.txt`
