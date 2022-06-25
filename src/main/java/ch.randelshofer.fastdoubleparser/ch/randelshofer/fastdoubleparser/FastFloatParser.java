/*
 * @(#)FastFloatParser2.java
 * Copyright © 2022. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

/**
 * Provides static method for parsing a {@code float} from a
 * {@link CharSequence}, {@code char} array or {@code byte} array.
 */
public class FastFloatParser {

    /**
     * Don't let anyone instantiate this class.
     */
    private FastFloatParser() {

    }

    /**
     * Convenience method for calling {@link #parseFloat(CharSequence, int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static float parseFloat(CharSequence str) throws NumberFormatException {
        return parseFloat(str, 0, str.length());
    }

    /**
     * Parses a {@code FloatValue} from a {@link CharSequence} and converts it
     * into a {@code float} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     *
     * @param str    the string to be parsed
     * @param offset the start offset of the {@code FloatValue} in {@code str}
     * @param length the length of {@code FloatValue} in {@code str}
     * @return the parsed value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static float parseFloat(CharSequence str, int offset, int length) throws NumberFormatException {
        long bitPattern = new FloatBitsFromCharSequence().parseFloatValue(str, offset, length);
        if (bitPattern == AbstractFloatValueParser.PARSE_ERROR) {
            throw new NumberFormatException("Illegal input");
        }
        return Float.intBitsToFloat((int) bitPattern);
    }

    /**
     * Convenience method for calling {@link #parseFloat(byte[], int, int)}.
     *
     * @param str the string to be parsed, a byte array with characters
     *            in ISO-8859-1, ASCII or UTF-8 encoding
     * @return the parsed value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static float parseFloat(byte[] str) throws NumberFormatException {
        return parseFloat(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatValue} from a {@code byte}-Array and converts it
     * into a {@code float} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first byte to parse
     * @param length The number of bytes to parse
     * @return the parsed value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static float parseFloat(byte[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = new FloatBitsFromByteArray().parseFloatValue(str, offset, length);
        if (bitPattern == AbstractFloatValueParser.PARSE_ERROR) {
            throw new NumberFormatException("Illegal input");
        }
        return Float.intBitsToFloat((int) bitPattern);
    }


    /**
     * Convenience method for calling {@link #parseFloat(char[], int, int)}.
     *
     * @param str the string to be parsed
     * @return the parsed value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static float parseFloat(char[] str) throws NumberFormatException {
        return parseFloat(str, 0, str.length);
    }

    /**
     * Parses a {@code FloatValue} from a {@code byte}-Array and converts it
     * into a {@code float} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first character to parse
     * @param length The number of characters to parse
     * @return the parsed value
     * @throws NumberFormatException if the string can not be parsed
     */
    public static float parseFloat(char[] str, int offset, int length) throws NumberFormatException {
        long bitPattern = new FloatBitsFromCharArray().parseFloatValue(str, offset, length);
        if (bitPattern == AbstractFloatValueParser.PARSE_ERROR) {
            throw new NumberFormatException("Illegal input");
        }
        return Float.intBitsToFloat((int) bitPattern);
    }

    /**
     * Parses a {@code FloatValue} from a {@link CharSequence} and converts it
     * into a bit pattern that encodes a {@code float} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     * <p>
     * Usage example:
     * <pre>
     *     long bitPattern = parseFloatBits("3.14", 0, 4);
     *     if (bitPattern == -1L) {
     *         ...handle parse error...
     *     } else {
     *         double d = Double.longBitsToDouble(bitPattern);
     *     }
     * </pre>
     *
     * @param str    the string to be parsed
     * @param offset the start offset of the {@code FloatValue} in {@code str}
     * @param length the length of {@code FloatValue} in {@code str}
     * @return the bit pattern of the parsed value, if the input is legal;
     * otherwise, {@code -1L}.
     */
    public static long parseFloatBits(CharSequence str, int offset, int length) {
        return new FloatBitsFromCharSequence().parseFloatValue(str, offset, length);
    }

    /**
     * Parses a {@code FloatValue} from a {@code byte}-Array and converts it
     * into a bit pattern that encodes a {@code float} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     * <p>
     * See {@link #parseFloatBits(CharSequence, int, int)} for a usage example.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first byte to parse
     * @param length The number of bytes to parse
     * @return the bit pattern of the parsed value, if the input is legal;
     * otherwise, {@code -1L}.
     */
    public static long parseFloatBits(byte[] str, int offset, int length) {
        return new FloatBitsFromByteArray().parseFloatValue(str, offset, length);
    }

    /**
     * Parses a {@code FloatValue} from a {@code byte}-Array and converts it
     * into a bit pattern that encodes a {@code float} value.
     * <p>
     * See {@link ch.randelshofer.fastdoubleparser} for the syntax of {@code FloatValue}.
     * <p>
     * See {@link #parseFloatBits(CharSequence, int, int)} for a usage example.
     *
     * @param str    the string to be parsed, a byte array with characters
     *               in ISO-8859-1, ASCII or UTF-8 encoding
     * @param offset The index of the first character to parse
     * @param length The number of characters to parse
     * @return the bit pattern of the parsed value, if the input is legal;
     * otherwise, {@code -1L}.
     */
    public static long parseFloatBits(char[] str, int offset, int length) {
        return new FloatBitsFromCharArray().parseFloatValue(str, offset, length);
    }
}