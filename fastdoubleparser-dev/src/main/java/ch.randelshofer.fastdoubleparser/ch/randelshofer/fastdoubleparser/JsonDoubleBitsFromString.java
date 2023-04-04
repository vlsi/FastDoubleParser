/*
 * @(#)JsonDoubleBitsFromString.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

/**
 * Parses a {@code double} from a {@link String}.
 */
final class JsonDoubleBitsFromString extends AbstractJsonFloatingPointBitsFromString {

    /**
     * Creates a new instance.
     */
    public JsonDoubleBitsFromString() {

    }

    @Override
    long valueOfFloatLiteral(String str, int startIndex, int endIndex, boolean isNegative,
                             long significand, int exponent, boolean isSignificandTruncated,
                             int exponentOfTruncatedSignificand) {
        double d = FastDoubleMath.tryDecFloatToDoubleTruncated(isNegative, significand, exponent, isSignificandTruncated,
                exponentOfTruncatedSignificand);
        return Double.doubleToRawLongBits(Double.isNaN(d) ? Double.parseDouble(str.subSequence(startIndex, endIndex).toString()) : d);
    }
}