import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
    FirstStepOfIntegrationTest.class,
    SecondStepOfIntegrationTest.class
})
class TriangleIntegrationTest {
}
