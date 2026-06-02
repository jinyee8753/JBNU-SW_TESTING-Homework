import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.EnumSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SecondStepOfIntegrationTest {

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
        "-1, 2, 2, IMPOSSIBLE",
        "20, 21, 29, SCALENE|RIGHT_ANGLED",
        "30000, 40000, 50000, SCALENE|RIGHT_ANGLED",
        "9, 10, 11, SCALENE"
    })
    void getTypeFlagsWithRealChecker(int s1, int s2, int s3, String expected) {
        Triangle triangle = new Triangle(s1, s2, s3);

        assertEquals(parseFlags(expected), triangle.getTypeFlags());
    }

    @ParameterizedTest
    @CsvSource({
        "3, 4, 5",
        "3, 3, 3",
        "3, 3, 4",
        "4, 5, 6",
        "1, 2, 3",
        "20, 21, 29"
    })
    void getTypeFlagsIsConsistentWithClassifyAndIsRightAngled(int s1, int s2, int s3) {
        Triangle triangle = new Triangle(s1, s2, s3);
        EnumSet<TriangleType> flags = triangle.getTypeFlags();

        if (triangle.isImpossible()) {
            assertEquals(EnumSet.of(TriangleType.IMPOSSIBLE), flags);
        } else {
            assertEquals(triangle.isRightAngled(), flags.contains(TriangleType.RIGHT_ANGLED));
            assertFalse(flags.contains(TriangleType.IMPOSSIBLE));
        }
    }

    private static EnumSet<TriangleType> parseFlags(String spec) {
        EnumSet<TriangleType> flags = EnumSet.noneOf(TriangleType.class);
        for (String token : spec.split("\\|")) {
            flags.add(TriangleType.valueOf(token.trim()));
        }
        return flags;
    }
}
