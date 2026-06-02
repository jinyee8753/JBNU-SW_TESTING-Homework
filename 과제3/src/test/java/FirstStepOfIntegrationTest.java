import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class FirstStepOfIntegrationTest {

    private RightAngleChecker checker;

    @BeforeEach
    void setUp() {
        checker = mock(RightAngleChecker.class);
        when(checker.isRightAngled(anyInt(), anyInt(), anyInt())).thenAnswer(invocation -> {
            long a = (int) invocation.getArgument(0);
            long b = (int) invocation.getArgument(1);
            long c = (int) invocation.getArgument(2);
            return a * a + b * b == c * c
                || a * a + c * c == b * b
                || b * b + c * c == a * a;
        });
    }

    @ParameterizedTest
    @CsvSource({
        "3, 3, 3, EQUILATERAL",
        "3, 3, 4, ISOSCELES",
        "3, 4, 5, SCALENE|RIGHT_ANGLED",
        "4, 5, 6, SCALENE",
        "5, 3, 4, SCALENE|RIGHT_ANGLED",
        "1, 2, 2, ISOSCELES",
        "1, 2, 3, IMPOSSIBLE",
        "0, 1, 1, IMPOSSIBLE",
        "-1, 2, 2, IMPOSSIBLE"
    })
    void getTypeFlagsWithMockedChecker(int s1, int s2, int s3, String expected) {
        Triangle triangle = new Triangle(s1, s2, s3, checker);

        assertEquals(parseFlags(expected), triangle.getTypeFlags());
    }

    private static EnumSet<TriangleType> parseFlags(String spec) {
        EnumSet<TriangleType> flags = EnumSet.noneOf(TriangleType.class);
        for (String token : spec.split("\\|")) {
            flags.add(TriangleType.valueOf(token.trim()));
        }
        return flags;
    }
}
