/*
 * @(#)JavaBigIntegerFromCharSequence.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.util.Map;

import static ch.randelshofer.fastdoubleparser.FastIntegerMath.fillPowersOf10Floor16;
import static ch.randelshofer.fastdoubleparser.ParseDigitsTaskCharSequence.parseDigits;

class JavaBigIntegerFromCharSequence extends AbstractNumberParser {
    public final static int MAX_INPUT_LENGTH = 1_292_782_622;
    /**
     * The resulting value must fit into {@code 2^31 - 1} bits.
     * The decimal representation of {@code 2^31 - 1} has 646,456,993 digits.
     */
    private static final int MAX_DECIMAL_DIGITS = 646_456_993;


    /**
     * Parses a {@code BigIntegerLiteral} as specified in {@link JavaBigIntegerParser}.
     *
     * @return result (always non-null)
     * @throws NumberFormatException if parsing fails
     */
    public BigInteger parseBigIntegerLiteral(CharSequence str, int offset, int length, int radix)
            throws NumberFormatException {
        try {
            final int endIndex = offset + length;
            if (offset < 0 || endIndex < offset || endIndex > str.length() || length > MAX_INPUT_LENGTH) {
                throw new IllegalArgumentException(ILLEGAL_OFFSET_OR_ILLEGAL_LENGTH);
            }
            // Parse optional sign
            // -------------------
            int index = offset;
            char ch = str.charAt(index);
            final boolean isNegative = ch == '-';
            if (isNegative || ch == '+') {
                ch = charAt(str, ++index, endIndex);
                if (ch == 0) {
                    throw new NumberFormatException(SYNTAX_ERROR);
                }
            }

            switch (radix) {
            case 10:
                return parseDecDigits(str, index, endIndex, isNegative);
            case 16:
                return parseHexDigits(str, index, endIndex, isNegative);
            default:
                return new BigInteger(str.subSequence(offset, length).toString(), radix);
            }
        } catch (ArithmeticException e) {
            NumberFormatException nfe = new NumberFormatException(VALUE_EXCEEDS_LIMITS);
            nfe.initCause(e);
            throw nfe;
        }
    }

    private BigInteger parseDecDigits(CharSequence str, int from, int to, boolean isNegative) {
        int numDigits = to - from;
        if (numDigits > 18) {
            return parseManyDecDigits(str, from, to, isNegative);
        }
        int preroll = from + (numDigits & 7);
        long significand = FastDoubleSwar.tryToParseUpTo7Digits(str, from, preroll);
        boolean success = significand >= 0;
        for (from = preroll; from < to; from += 8) {
            int addend = FastDoubleSwar.tryToParseEightDigits(str, from);
            success &= addend >= 0;
            significand = significand * 100_000_000L + addend;
        }
        if (!success) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }
        return BigInteger.valueOf(isNegative ? -significand : significand);
    }

    private BigInteger parseHexDigits(CharSequence str, int from, int to, boolean isNegative) {
        from = skipZeroes(str, from, to);
        int numDigits = to - from;
        if (numDigits <= 0) {
            return BigInteger.ZERO;
        }
        byte[] bytes = new byte[((numDigits + 1) >> 1) + 1];
        int index = 1;
        boolean illegalDigits = false;
        if ((numDigits & 1) != 0) {
            char chLow = str.charAt(from++);
            int valueLow = lookupHex(chLow);
            bytes[index++] = (byte) valueLow;
            illegalDigits = valueLow < 0;
        }
        int prerollLimit = from + ((to - from) & 7);
        for (; from < prerollLimit; from += 2) {
            char chHigh = str.charAt(from);
            char chLow = str.charAt(from + 1);
            int valueHigh = lookupHex(chHigh);
            int valueLow = lookupHex(chLow);
            bytes[index++] = (byte) (valueHigh << 4 | valueLow);
            illegalDigits |= valueLow < 0 || valueHigh < 0;
        }
        for (; from < to; from += 8, index += 4) {
            long value = FastDoubleSwar.tryToParseEightHexDigits(str, from);
            FastDoubleSwar.writeIntBE(bytes, index, (int) value);
            illegalDigits |= value < 0;
        }
        if (illegalDigits) {
            throw new NumberFormatException(SYNTAX_ERROR);
        }
        BigInteger result = new BigInteger(bytes);
        return isNegative ? result.negate() : result;
    }

    private BigInteger parseManyDecDigits(CharSequence str, int from, int to, boolean isNegative) {
        from = skipZeroes(str, from, to);
        int numDigits = to - from;
        if (numDigits > MAX_DECIMAL_DIGITS) {
            throw new NumberFormatException(VALUE_EXCEEDS_LIMITS);
        }
        Map<Integer, BigInteger> powersOfTen = fillPowersOf10Floor16(from, to);
        BigInteger result = parseDigits(str, from, to, powersOfTen);
        return isNegative ? result.negate() : result;
    }

    private int skipZeroes(CharSequence str, int from, int to) {
        while (from < to && str.charAt(from) == '0') from++;
        return from;
    }

}
