package ch.randelshofer.fastdoubleparser;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

public class JavaBigDecimalFromCharSequenceTest extends AbstractBigDecimalParserTest {


    private void test(BigDecimalTestData d, Function<BigDecimalTestData, BigDecimal> f) {
        BigDecimal actual = f.apply(d);
        BigDecimal expectedValue = d.expectedValue().get();
        if (expectedValue != null) {
            assertEquals(0, expectedValue.compareTo(actual),
                    "expected:" + expectedValue + " <> actual:" + actual);
            assertEquals(expectedValue, actual);
        } else {
            assertNull(actual);
        }
    }

    @TestFactory
    public Stream<DynamicTest> dynamicTestsParseBigDecimalFromCharSequence() {
        return createRegularTestData().stream()
                .filter(t -> t.charLength() == t.input().length()
                        && t.charOffset() == 0)
                .map(t -> dynamicTest(t.title(),
                        () -> test(t, u -> JavaBigDecimalParser.parseBigDecimalOrNull(u.input(), u.charOffset(), u.charLength()))));

    }

}
