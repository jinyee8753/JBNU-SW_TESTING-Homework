import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.EnumSet;
import org.junit.jupiter.api.Test;

class TriangleGetTypeFlagsUnitTest {

    @Test
    void getTypeFlagsReturnsScaleneAndRightAngledWhenMockSaysRight() {
        RightAngleChecker checker = mock(RightAngleChecker.class);
        when(checker.isRightAngled(3, 4, 5)).thenReturn(true);

        Triangle triangle = new Triangle(3, 4, 5, checker);

        assertEquals(EnumSet.of(TriangleType.SCALENE, TriangleType.RIGHT_ANGLED),
                triangle.getTypeFlags());
        verify(checker).isRightAngled(3, 4, 5);
    }

    @Test
    void getTypeFlagsReturnsScaleneOnlyWhenMockSaysNotRight() {
        RightAngleChecker checker = mock(RightAngleChecker.class);
        when(checker.isRightAngled(3, 4, 5)).thenReturn(false);

        Triangle triangle = new Triangle(3, 4, 5, checker);

        assertEquals(EnumSet.of(TriangleType.SCALENE), triangle.getTypeFlags());
        verify(checker).isRightAngled(3, 4, 5);
    }

    @Test
    void getTypeFlagsReturnsEquilateralIndependentlyOfMockedChecker() {
        RightAngleChecker checker = mock(RightAngleChecker.class);
        when(checker.isRightAngled(5, 5, 5)).thenReturn(false);

        Triangle triangle = new Triangle(5, 5, 5, checker);

        assertEquals(EnumSet.of(TriangleType.EQUILATERAL), triangle.getTypeFlags());
        verify(checker).isRightAngled(5, 5, 5);
    }

    @Test
    void getTypeFlagsReturnsIsoscelesForExactlyTwoEqualSides() {
        RightAngleChecker checker = mock(RightAngleChecker.class);
        when(checker.isRightAngled(3, 3, 4)).thenReturn(false);

        Triangle triangle = new Triangle(3, 3, 4, checker);

        assertEquals(EnumSet.of(TriangleType.ISOSCELES), triangle.getTypeFlags());
        verify(checker).isRightAngled(3, 3, 4);
    }

    @Test
    void getTypeFlagsCombinesIsoscelesAndRightAngledWhenMockSaysRight() {
        RightAngleChecker checker = mock(RightAngleChecker.class);
        when(checker.isRightAngled(3, 3, 4)).thenReturn(true);

        Triangle triangle = new Triangle(3, 3, 4, checker);

        assertEquals(EnumSet.of(TriangleType.ISOSCELES, TriangleType.RIGHT_ANGLED),
                triangle.getTypeFlags());
        verify(checker).isRightAngled(3, 3, 4);
    }

    @Test
    void getTypeFlagsReturnsImpossibleAndNeverCallsCheckerForTriangleInequality() {
        RightAngleChecker checker = mock(RightAngleChecker.class);

        Triangle triangle = new Triangle(1, 2, 3, checker);

        assertEquals(EnumSet.of(TriangleType.IMPOSSIBLE), triangle.getTypeFlags());
        verify(checker, never()).isRightAngled(anyInt(), anyInt(), anyInt());
    }

    @Test
    void getTypeFlagsReturnsImpossibleAndNeverCallsCheckerForZeroSide() {
        RightAngleChecker checker = mock(RightAngleChecker.class);

        Triangle triangle = new Triangle(0, 1, 1, checker);

        assertEquals(EnumSet.of(TriangleType.IMPOSSIBLE), triangle.getTypeFlags());
        verify(checker, never()).isRightAngled(anyInt(), anyInt(), anyInt());
    }

    @Test
    void getTypeFlagsReturnsImpossibleAndNeverCallsCheckerForNegativeSide() {
        RightAngleChecker checker = mock(RightAngleChecker.class);

        Triangle triangle = new Triangle(-1, 2, 2, checker);

        assertEquals(EnumSet.of(TriangleType.IMPOSSIBLE), triangle.getTypeFlags());
        verify(checker, never()).isRightAngled(anyInt(), anyInt(), anyInt());
    }

    @Test
    void getTypeFlagsReturnsIsoscelesAtJustValidBoundary() {
        RightAngleChecker checker = mock(RightAngleChecker.class);
        when(checker.isRightAngled(1, 2, 2)).thenReturn(false);

        Triangle triangle = new Triangle(1, 2, 2, checker);

        assertEquals(EnumSet.of(TriangleType.ISOSCELES), triangle.getTypeFlags());
        verify(checker).isRightAngled(1, 2, 2);
    }
}
