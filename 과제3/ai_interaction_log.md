# AI Agent 활용 기록

## 1. 사용한 AI agent

| Agent         | 모델                | 역할                                 |
| ------------- | ----------------- | ---------------------------------- |
| Claude Opus   | Claude Opus 4.8   | 오케스트레이터, 코드 구현, 메인 보고서 작성          |
| Hermes        | GPT-5.5-medium    | cross-family 설계 리뷰, 최종 검수          |
| Codex         | GPT-5.5-low       | 구현 리뷰                              |
| Claude Sonnet | Claude Sonnet 4.6 | 문서 작성 (README, ai_interaction_log) |

## 2. 역할 분담

| 담당 | 세부 역할 |
|------|-----------|
| Opus (오케스트레이터) | 지시사항 분석, 강의 자료 교차검증, 전체 작업 지시, 코드 구현, 메인 보고서 작성 |
| Hermes (cross-family 리뷰어) | 설계 합의 (RightAngleChecker 인터페이스·주입 방식), 최종 cross-family 검수 |
| Codex (구현 리뷰어) | 구현 코드 리뷰 |
| Sonnet (문서 담당) | README.md, ai_interaction_log.md 초안 작성 |

## 3. 워크플로우

1. **지시사항 분석** — Opus가 과제3 지시사항(`과제3-지시사항.docx`)을 읽고 요구사항을 정리했다.
2. **강의 자료 교차검증** — 11, 12, 13주차 강의 자료를 기준으로 mock 단위시험, @Suite 통합시험, CI 구성 요건을 검증했다.
3. **Hermes 설계 합의** — `RightAngleChecker` 인터페이스와 생성자 주입 방식에 대해 Hermes와 합의했다. 교수님의 `Divider(IMultiplier, ISubstractor)` 패턴을 참고했다.
4. **Opus 구현** — `Triangle`, `TriangleType`, `RightAngleChecker`, `RealRightAngleChecker`, 6개 테스트 클래스, `build.gradle`, Gradle Wrapper, GitHub Actions 워크플로우를 구현했다.
5. **로컬 Gradle 빌드 검증** — `./gradlew build`로 85 tests, 0 failures, BUILD SUCCESSFUL을 확인했다.
6. **3중 리뷰** — Hermes, Codex, strict-evaluator 역할의 3중 리뷰를 수행했다.
7. **GitHub push + CI 확인** — 저장소의 `과제3` 폴더로 push하고, 저장소 루트 `.github/workflows/gradle.yml`을 통해 GitHub Actions에서 빌드 성공(build success)을 확인했다 (워크플로 "Java CI with Gradle", conclusion: success).

## 4. 핵심 설계 결정

### RightAngleChecker 인터페이스 + 생성자 주입

`Triangle` 클래스가 직각 판정 로직에 직접 의존하지 않고 `RightAngleChecker` 인터페이스에 의존하도록 생성자 주입 방식을 채택했다. 이를 통해 단위시험에서 Mockito mock으로 `RightAngleChecker`를 대체할 수 있다. 교수님의 `Divider(IMultiplier, ISubstractor)` 의존성 주입 패턴과 동일한 구조다.

### EnumSet\<TriangleType\> 반환

`getTypeFlags()`는 `TriangleType` 열거형의 `EnumSet`을 반환한다. 삼각형은 동시에 여러 속성(예: 이등변삼각형이면서 직각삼각형)을 가질 수 있으므로, 단일 값 반환인 `classify()`와 달리 복수 속성을 동시에 표현할 수 있는 구조다.

### @Suite 기반 통합시험

JUnit 5 `@Suite`로 `FirstStepOfIntegrationTest`와 `SecondStepOfIntegrationTest`를 `TriangleIntegrationTest`에 묶었다. `FirstStepOfIntegrationTest`는 mock 협력자로 통합 흐름을 검증하고, `SecondStepOfIntegrationTest`는 실제 구현체로 종단간 동작을 검증한다. `build.gradle`의 `test { exclude }` 설정으로 통합 단계 클래스가 단위 테스트 태스크에서 직접 실행되지 않고 `@Suite`를 통해서만 실행되도록 분리한다.

## 5. AI 활용 검증 체크리스트

| 검증 항목 | 결과 |
|-----------|------|
| Mockito mock 단위시험이 강의(11주차 Test Double) 기준에 부합하는가 | 확인 — 생성자 주입 + `when().thenReturn()` 패턴 적용 |
| @Suite 통합시험 구조가 강의(13주차) 기준에 부합하는가 | 확인 — FirstStep(mock)→SecondStep(real) 2단계 구성 |
| 기존 40개 회귀 테스트가 모두 통과하는가 | 확인 — 85 tests, 0 failures |
| GitHub Actions CI가 실제로 통과했는가 | 확인 — ubuntu+temurin17, `./gradlew build`, conclusion: success |
| AI 제안 설계(인터페이스+주입)가 강의 패턴과 일치하는가 | 확인 — 교수님 Divider 패턴과 동일 구조 |
| 테스트 수치와 도구 버전이 실제 빌드 결과와 일치하는가 | 확인 — 85 tests / Java 17 / Gradle 8.10.2 / Mockito 5.18.0 |
