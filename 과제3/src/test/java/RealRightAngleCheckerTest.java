import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class RealRightAngleCheckerTest {

    private final RightAngleChecker checker = new RealRightAngleChecker();

    @ParameterizedTest
    @CsvSource({
        "3, 4, 5, true",
        "5, 3, 4, true",
        "5, 4, 3, true",
        "20, 21, 29, true",
        "30000, 40000, 50000, true",
        "3, 3, 3, false",
        "4, 5, 6, false",
        "1, 2, 2, false",
        "9, 10, 11, false"
    })
    void isRightAngledDetectsPythagoreanTriplesRegardlessOfOrder(int s1, int s2, int s3, boolean expected) {
        assertEquals(expected, checker.isRightAngled(s1, s2, s3));
    }
}
