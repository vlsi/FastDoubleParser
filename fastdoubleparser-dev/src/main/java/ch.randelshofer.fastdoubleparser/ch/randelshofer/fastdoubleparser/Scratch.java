/*
 * @(#)Scratch.java
 * Copyright © 2023 Werner Randelshofer, Switzerland. MIT License.
 */
package ch.randelshofer.fastdoubleparser;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

public class Scratch {
    public static String repeat(String str, int count) {
        StringBuilder b = new StringBuilder(str.length() * count);
        while (count-- > 0) {
            b.append(str);
        }
        return b.toString();
    }

    public static void main(String... args) {
        int digits = 646456993;
        /*
        String str =
                "-"
                        + repeat("0", Math.max(0, digits - 646456993))
                        + repeat("1234567890", (Math.min(646456993, digits) + 9) / 10).substring(0, Math.min(digits, 646456993));
        byte[]decLiteral = str.getBytes(StandardCharsets.ISO_8859_1);
        BigInteger b = JavaBigIntegerParser.parseBigInteger(decLiteral);
        System.out.println(b.bitLength());
        */
        String hexStr =
                "-"
                        + repeat("0", Math.max(0, digits - 536870912))
                        + repeat("12b4c6d7e0", (Math.min(536870912, digits) + 9) / 10).substring(0, Math.min(digits, 536870912));

        byte[] hexLiteral = hexStr.getBytes(StandardCharsets.ISO_8859_1);

        BigInteger b = JavaBigIntegerParser.parseBigInteger(hexLiteral, 16);
        System.out.println(b.bitLength());
    }
}
