/*
 * @(#)BigDecimalParserJmhBenchmark.java
 * Copyright © 2021. Werner Randelshofer, Switzerland. MIT License.
 */

package ch.randelshofer.fastdoubleparser;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

/**
 * Benchmarks for selected floating point strings.
 * <pre>
 * # JMH version: 1.28
 * # VM version: OpenJDK 64-Bit Server VM, Oracle Corporation, 19+36-2238
 * # Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz
 *
 * Benchmark    Mode     Cnt                 Score   Error   Units   per digit
 * m         1  avgt       2       _   _   _ 13.139          ns/op
 * m        10  avgt       2       _   _   _ 24.150          ns/op
 * m       100  avgt       2       _   _   _496.133          ns/op
 * m      1000  avgt       2       _   _ 14_948.645          ns/op
 * m     10000  avgt       2       _  1_186_259.863          ns/op
 * m    100000  avgt       2       _116_006_218.534          ns/op     1160.06
 * m   1000000  avgt       2     11_915_720_193.000          ns/op    11915.72
 * m  10000000  avgt       1  1_322_734_437_937.000          ns/op   132273.44
 * </pre>
 */

@Fork(value = 1)
@Measurement(iterations = 1)
@Warmup(iterations = 0)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class BigDecimalScalabilityJmh {


    @Param({
            // "1",
            // "10",
            // "100",
            "1000",
            "10000",
            "100000",
            "1000000",
            "10000000",
            //"100000000",
            //"536870919"
    })
    public int digits;
    private String str;

    @Setup(Level.Trial)
    public void setUp() {
        str = "9806543217".repeat((digits + 9) / 10).substring(0, digits);
    }

    @Benchmark
    public BigDecimal m() {
        return new BigDecimal(str);
    }
}





