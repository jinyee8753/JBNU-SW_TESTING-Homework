# 태스크 분할 및 워크플로우 정의

## 1. 요구사항 원문

과제2-재수행 지시사항:
1. 과제2의 TDD 코드에 정적분석 단계를 추가한다.
2. 정적분석 결과를 AI agent가 분석하고, 직접 검증한다.
3. 수정 적용 후 JUnit 회귀 테스트를 재실행한다.

강의에서 추가로 요구한 사항 (10주차 목, 11주차 화/목):
- 요구사항을 태스크로 분할하고 직접 분할을 검증할 것
- AI agent에게 워크플로우를 마크다운으로 정의시킬 것
- JaCoCo 커버리지 측정을 포함할 것
- JaCoCo 리포트를 AI agent가 분석할 것
- Git repo 연결, PR 생성, merge, pull 후 실행 흐름 (10주차 목에서 언급)

### PR/merge 흐름에 대한 범위 판단

10주차 목요일 강의에서 교수님께서 repo/PR/merge/pull 기반 통합 흐름을 언급했다. 그러나 이번 재수행에서는 로컬 단일 폴더 구조로 진행했으며, 별도의 Git 원격 저장소나 PR/merge 워크플로우는 적용하지 않았다. 그 이유는 다음과 같다:

1. 과제2-재수행의 핵심 범위는 "기존 TDD 코드에 정적분석 + 커버리지 측정을 추가"하는 것이다
2. 원본 과제2 코드가 로컬 프로젝트 구조이므로, PR/merge 흐름보다는 정적분석·커버리지 단계 자체에 집중했다
3. 대신 태스크별 산출물(증빙 파일, 테스트 결과, 커버리지 리포트)을 `evidence/` 디렉토리에 저장하여 통합 흐름의 추적성을 확보했다
4. 코드 변경 이력은 각 태스크의 수정 내용과 회귀 테스트 결과로 증빙한다

## 2. AI agent의 태스크 분할 결과

초기에는 정적분석만 포함하는 프롬프트로 태스크 분할을 시작했다. 이후 11주차 목 강의에서 JaCoCo 커버리지 측정이 추가 요구되어, 검증 단계에서 T6~T8을 보강했다. 아래는 보강 후 최종 프롬프트이다:

```
새로운 과제를 부여받았어. 레포트/과제2 를 재수행 해야하는 과제야(과제 이름은 '과제2-재수행'). 지시 사항은 레포트/과제2-재수행 폴더에 문서로 정리해놨어. 원본인 '과제2'를 보고 '과제2-재수행'폴더에서 과제를 진행하면 돼.

과제2-재수행의 요구사항을 태스크로 분할해줘. 각 태스크는 독립적으로 실행·검증 가능해야 하고,
TDD의 Red-Green-Refactor 사이클과 정적분석·커버리지 측정 단계를 포함해야 해.
```

AI agent가 분할한 태스크 목록 (보강 후 최종):

| 태스크 ID | 태스크명 | 입력 | 산출물 | 완료 기준 |
|-----------|----------|------|--------|-----------|
| T1 | 과제2 코드 이관 | 과제2 src/tests | 과제2-재수행 src/tests | 33개 테스트 통과 |
| T2 | 정적분석 환경 구축 | - | PMD, CheckStyle 설치 | 도구 실행 가능 |
| T3 | PMD 실행 및 분석 | src/Triangle.java | PMD 결과, 경고 분류 | 모든 경고에 판정 부여 |
| T4 | CheckStyle 실행 및 분석 | src/Triangle.java | CheckStyle 결과, 경고 분류 | 모든 경고에 판정 부여 |
| T5 | 실질 결함 수정 | PMD/CheckStyle 분석 결과 | 수정된 Triangle.java | 수정 후 테스트 통과 |
| T6 | 커버리지 환경 구축 | - | JaCoCo 설치 | 도구 실행 가능 |
| T7 | 커버리지 측정 및 분석 | tests/TriangleTest.java | JaCoCo 리포트, 분석 결과 | 메소드별 커버리지 확인 |
| T8 | 커버리지 개선 테스트 추가 | JaCoCo 분석 결과 | 추가 테스트, 재측정 결과 | 공개 메소드 커버리지 향상 |
| T9 | 최종 회귀 테스트 | 수정된 코드 + 전체 테스트 | 최종 테스트 결과 | 전체 테스트 통과 |
| T10 | 보고서 및 증빙 작성 | 모든 태스크 결과 | 보고서, 로그, 증빙 | 모든 증빙 파일 최신 상태 |

## 3. 태스크 분할 검증

AI agent의 태스크 분할을 직접 검토한 결과:

| 검증 항목 | 판단 | 근거 |
|-----------|------|------|
| 태스크가 독립 실행 가능한가 | 적절 | 각 태스크는 명확한 입력과 산출물이 있음 |
| 누락된 단계가 있는가 | 수정 필요 | 초기 분할에서 JaCoCo 관련 태스크(T6~T8)가 없었음. 11주차 목 강의 검토 후 추가 |
| 순서가 적절한가 | 적절 | T1→T2→T3/T4→T5→T6→T7→T8→T9→T10 순서가 논리적 |
| 완료 기준이 명확한가 | 적절 | 각 태스크에 측정 가능한 완료 기준이 있음 |

수정한 사항:
- T6~T8을 추가: 강의에서 JaCoCo 커버리지 측정을 명시적으로 요구했으나 AI agent의 초기 분할에 없었음
- T5의 범위를 명확히 함: "실질적 결함만 수정하고, 스타일 경고는 false alarm으로 유지"라는 기준을 추가

## 4. 워크플로우 정의

### 4.1 전체 워크플로우

```
[요구사항] → [태스크 분할] → [검증]
                                  ↓
              [T1] 코드 이관 → JUnit 33개 통과 확인
                                  ↓
              [T2] 정적분석 환경 구축 (PMD + CheckStyle)
                                  ↓
              [T3] PMD 실행 → AI agent 분석 → 검증
                                  ↓
              [T4] CheckStyle 실행 → AI agent 분석 → 검증
                                  ↓
              [T5] 실질 결함 수정 → JUnit 회귀 테스트 → PMD 재실행
                                  ↓
              [T6] 커버리지 환경 구축 (JaCoCo)
                                  ↓
              [T7] JaCoCo 실행 → AI agent 분석 → 검증
                                  ↓
              [T8] 미커버 공개 메소드 테스트 추가 → JaCoCo 재측정
                                  ↓
              [T9] 최종 회귀 테스트 (40개 통과 확인)
                                  ↓
              [T10] 보고서 + 증빙 정리
```

### 4.2 각 태스크의 실행 규칙

태스크별 공통 규칙:
- AI agent가 실행하고, 사람이 결과를 검증한다
- 각 태스크 완료 후 산출물을 확인하고 다음 태스크로 진행한다
- 테스트가 포함된 태스크에서는 반드시 JUnit 실행 결과를 증빙으로 저장한다

정적분석 판정 규칙:
- PMD/CheckStyle 경고 각각에 대해 AI agent가 원인 분석을 수행한다
- 각 경고를 "수정 필요(실질 결함)" 또는 "false alarm(스타일 경고)"로 분류한다
- 사람이 강의(10주차) 인스펙션 체크리스트 기준으로 교차 검증한다
- 수정 판정을 받은 경고만 코드를 수정하고, 수정 후 회귀 테스트를 실행한다

커버리지 분석 규칙:
- JaCoCo 리포트를 AI agent가 메소드별로 분석한다
- 미커버 공개 메소드에 대해 테스트 추가 여부를 판단한다
- 의도적 제외(예: main())는 근거를 명시한다

## 5. 태스크별 실행 결과 요약

### T1: 과제2 코드 이관

- 입력: 과제2의 `src/Triangle.java`, `tests/TriangleTest.java`
- 결과: 과제2-재수행 디렉토리에 이관 완료
- 검증: JUnit 33개 테스트 모두 통과

### T2: 정적분석 환경 구축

- PMD 7.12.0 다운로드 및 설치 (GitHub Releases)
- CheckStyle 10.25.0 다운로드 및 설치 (GitHub Releases)
- 검증: 각 도구가 Triangle.java에 대해 실행 가능함을 확인

### T3: PMD 실행 및 분석

- 적용 규칙: bestpractices, codestyle, design, errorprone, performance, security
- 결과: 41건 경고 발견
- AI agent 분류: 2건 실질 결함 + 39건 false alarm
- 검증: 동의 (인스펙션 체크리스트 대조 완료)
- 증빙: `evidence/pmd_full_result.txt`

### T4: CheckStyle 실행 및 분석

- 적용 규칙: Sun Checks
- 수정 전 결과: 48건 → 수정 후: 46건
- AI agent 분류: 스타일 경고 (실질 결함 없음), 수정 적용 가능 항목 2건(MultipleVariableDeclarations, trailing space)
- 검증: 동의 (Allman vs K&R 등 스타일 차이는 원본 유지)
- 증빙: `evidence/checkstyle_result_before_fix.txt` (수정 전 48건), `evidence/checkstyle_result.txt` (수정 후 46건)

### T5: 실질 결함 수정

수정한 항목:

| # | 규칙 | 수정 내용 | 근거 |
|---|------|-----------|------|
| 1 | UnusedAssignment | `Triangle triangle = null;` → `Triangle triangle;` | 불필요한 null 초기화 |
| 2 | AvoidCatchingGenericException | `catch (Exception e)` → `catch (NumberFormatException \| ArrayIndexOutOfBoundsException e)` | 프로그래밍 오류 은폐 방지 |
| 3 | OneDeclarationPerLine | `private int side1, side2, side3;` → 3줄로 분리 | 가독성, diff 추적성 |
| 4 | RegexpSingleline | trailing space 제거 | 코드 정리 |

- 회귀 테스트: 33개 모두 통과
- PMD 재실행: 41건 → 39건

### T6: 커버리지 환경 구축

- JaCoCo 0.8.12 다운로드 및 설치 (Maven Central)
- 검증: JaCoCo agent가 JUnit 실행에 연결 가능함을 확인

### T7: 커버리지 측정 및 분석

초기 측정 결과 (33개 테스트):

| 지표 | 커버리지 |
|------|----------|
| Instruction | 74% |
| Branch | 91% |
| Line | 62% |
| Method | 70% (3개 미커버) |

AI agent 분석:
- `main()` (0%): 의도적 제외 — Javadoc에 테스트 불요 명시
- `setSideLengths()` (0%): 공개 API이나 테스트 미작성
- `getSideLengths()` (0%): 공개 API이나 테스트 미작성
- `isRightAngled()` Branch 87%: guard 분기 미커버
- `isImpossible()` Branch 83%: 단축 평가로 인한 미커버

검증: 동의하되, `setSideLengths()`와 `getSideLengths()`는 공개 메소드이므로 테스트 추가가 필요하다고 판단

### T8: 커버리지 개선 테스트 추가

추가한 테스트 7개:

| # | 테스트명 | 목적 |
|---|----------|------|
| 1 | setSideLengthsUpdatesClassification | setSideLengths() 후 classify() 정상 동작 확인 |
| 2 | setSideLengthsReturnsSameInstance | fluent API 패턴(return this) 검증 |
| 3 | getSideLengthsReturnsCommaSeparatedValues | 반환 형식 검증 |
| 4 | getSideLengthsReflectsSetSideLengths | setSideLengths() 후 getSideLengths() 연동 확인 |
| 5 | getPerimeterReturnsSum | 기본 둘레 계산 검증 |
| 6 | getPerimeterForImpossibleTriangleReturnsSum | 불가능한 삼각형에서도 합을 반환하는 동작 검증 |
| 7 | isRightAngledReturnsFalseForImpossibleTriangle | isRightAngled() guard 분기 커버 |

재측정 결과 (40개 테스트):

| 지표 | 이전 | 이후 | 변화 |
|------|------|------|------|
| Instruction | 74% | 83% | +9% |
| Branch | 91% | 94% | +3% |
| Line | 62% | 74% | +12% |
| Method | 70% | 90% | +20% |

미커버: `main()`만 남음 (의도적 제외). main() 제외 시 Line 100%.

### T9: 최종 회귀 테스트

- 전체 테스트: 40개
- 통과: 40개
- 실패: 0개
- 증빙: `evidence/final_test_result.txt`

### T10: 보고서 및 증빙 정리

- 보고서: `report/과제2-재수행_TDD_정적분석_보고서.md`
- 태스크 분할: `TASK_BREAKDOWN.md` (이 문서)
- 워크플로우 로그: `tdd_workflow_log.md`
- 정적분석 로그: `static_analysis_log.md`
- AI 활용 기록: `ai_interaction_log.md`
- 증빙: `evidence/` 디렉토리

## 6. getPerimeter() Javadoc 불일치 수정

`getPerimeter()`의 원본 Javadoc에는 "@return -1 if input values are invalid, otherwise the perimeter"라고 명시되어 있었지만, 실제 코드는 항상 `side1 + side2 + side3`을 반환했다. 이는 Javadoc과 구현의 불일치(documentation-implementation mismatch)이다.

수정 내용:
1. Javadoc을 현재 구현에 맞게 수정: "@return the perimeter (side1 + side2 + side3)." — 불가능한 삼각형에서도 합을 반환한다는 동작을 정확히 기술
2. 테스트 `getPerimeterForImpossibleTriangleReturnsSum()`으로 이 동작을 검증

코드 동작을 변경하지 않고 Javadoc을 수정한 이유:
1. `getPerimeter()`가 항상 합을 반환하는 현재 동작은 `getArea()`에서 `semiPerimeter` 계산에 사용되고 있어, 동작 변경 시 `getArea()`의 내부 로직에 영향을 줄 수 있다
2. Javadoc 수정은 문서 정확성 개선이며 과제2-재수행의 범위 내에서 안전하게 수행 가능하다
3. 이 불일치를 발견한 것 자체가 AI agent 코드 리뷰와 커버리지 분석의 가치를 보여준다
4. 정적분석 도구(PMD, CheckStyle)로는 탐지되지 않았으며, 이는 정적분석의 한계에 해당한다
