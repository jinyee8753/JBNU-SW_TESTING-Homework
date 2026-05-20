import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TriangleTest {
    @Test
    void classifyEquilateralTriangle() {
        assertEquals("equilateral", new Triangle(3, 3, 3).classify());
    }

    @ParameterizedTest
    @CsvSource({
        "3, 3, 5",
        "5, 3, 5",
        "3, 5, 5",
        "1000000, 1000000, 1999999"
    })
    void classifyIsoscelesTriangleForAnyEqualSidePair(int side1, int side2, int side3) {
        assertEquals("isosceles", new Triangle(side1, side2, side3).classify());
    }

    @ParameterizedTest
    @CsvSource({
        "4, 5, 6",
        "9, 10, 11"
    })
    void classifyScaleneTriangle(int side1, int side2, int side3) {
        assertEquals("scalene", new Triangle(side1, side2, side3).classify());
    }

    @ParameterizedTest
    @CsvSource({
        "3, 4, 5",
        "5, 3, 4",
        "20, 21, 29"
    })
    void classifyRightAngledTriangleRegardlessOfInputOrder(int side1, int side2, int side3) {
        assertEquals("right-angled", new Triangle(side1, side2, side3).classify());
    }

    @ParameterizedTest
    @CsvSource({
        "0, 3, 4",
        "3, 0, 4",
        "3, 4, 0",
        "-1, 3, 4",
        "1, 2, 3",
        "1, 2, 10"
    })
    void classifyImpossibleTriangle(int side1, int side2, int side3) {
        assertEquals("impossible", new Triangle(side1, side2, side3).classify());
    }

    @ParameterizedTest
    @CsvSource({
        "0, 3, 4",
        "3, 0, 4",
        "3, 4, 0",
        "-1, 3, 4",
        "1, 2, 3",
        "1, 2, 10"
    })
    void impossibleIncludesNonPositiveAndTriangleInequalityCases(int side1, int side2, int side3) {
        assertTrue(new Triangle(side1, side2, side3).isImpossible());
    }

    @ParameterizedTest
    @CsvSource({
        "3, 4, 5",
        "4, 5, 6",
        "1000000, 1000000, 1999999"
    })
    void possibleTriangleIsNotImpossible(int side1, int side2, int side3) {
        assertFalse(new Triangle(side1, side2, side3).isImpossible());
    }

    @Test
    void triangleInequalityBoundaryIsImpossible() {
        Triangle triangle = new Triangle(1, 2, 3);

        assertTrue(triangle.isImpossible());
        assertEquals("impossible", triangle.classify());
    }

    @Test
    void getAreaReturnsMinusOneForImpossibleTriangle() {
        assertEquals(-1.0, new Triangle(1, 2, 10).getArea(), 0.000001);
    }

    @Test
    void getAreaReturnsExpectedValueForThreeFourFiveTriangle() {
        assertEquals(6.0, new Triangle(3, 4, 5).getArea(), 0.000001);
    }

    @ParameterizedTest
    @CsvSource({
        "5, 3, 5, isosceles",
        "3, 5, 5, isosceles",
        "5, 3, 4, right-angled",
        "4, 5, 3, right-angled",
        "2, 1, 10, impossible"
    })
    void inputOrderDoesNotChangeClassification(int side1, int side2, int side3, String expected) {
        assertEquals(expected, new Triangle(side1, side2, side3).classify());
    }

    @Test
    void setSideLengthsUpdatesClassification() {
        Triangle triangle = new Triangle(3, 3, 3);
        assertEquals("equilateral", triangle.classify());

        triangle.setSideLengths(3, 4, 5);
        assertEquals("right-angled", triangle.classify());
    }

    @Test
    void setSideLengthsReturnsSameInstance() {
        Triangle triangle = new Triangle(1, 1, 1);
        Triangle returned = triangle.setSideLengths(3, 4, 5);
        assertTrue(triangle == returned);
    }

    @Test
    void getSideLengthsReturnsCommaSeparatedValues() {
        assertEquals("3,4,5", new Triangle(3, 4, 5).getSideLengths());
    }

    @Test
    void getSideLengthsReflectsSetSideLengths() {
        Triangle triangle = new Triangle(1, 1, 1);
        triangle.setSideLengths(7, 8, 9);
        assertEquals("7,8,9", triangle.getSideLengths());
    }

    @Test
    void getPerimeterReturnsSum() {
        assertEquals(12, new Triangle(3, 4, 5).getPerimeter());
    }

    @Test
    void getPerimeterForImpossibleTriangleReturnsSum() {
        Triangle impossible = new Triangle(1, 2, 10);
        assertTrue(impossible.isImpossible());
        assertEquals(13, impossible.getPerimeter());
    }

    @Test
    void isRightAngledReturnsFalseForImpossibleTriangle() {
        assertFalse(new Triangle(1, 2, 10).isRightAngled());
    }
}
