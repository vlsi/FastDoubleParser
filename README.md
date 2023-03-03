[![Maven Central](https://maven-badges.herokuapp.com/maven-central/ch.randelshofer/fastdoubleparser/badge.svg)](https://maven-badges.herokuapp.com/maven-central/ch.randelshofer/fastdoubleparser)

# FastDoubleParser

This is a Java port of Daniel Lemire's [fast_float](https://github.com/fastfloat/fast_float) project.

This project provides parsers for `double`, `float`, `BigDecimal` and `BigInteger` values.
The parsers are optimised for speed for the most common inputs.

The code in this project contains optimised versions for Java SE 1.8, 11, 17, 19 and 20-ea.
The code is released in a single multi-release jar, which contains the code for all these versions
except 20-ea.

Usage:

```java
module MyModule {
  requires ch.randelshofer.fastdoubleparser;
}
```

```java
import ch.randelshofer.fastdoubleparser.JavaDoubleParser;
import ch.randelshofer.fastdoubleparser.JavaFloatParser;
import ch.randelshofer.fastdoubleparser.JavaBigDecimalParser;
import ch.randelshofer.fastdoubleparser.JavaBigIntegerParser;
import ch.randelshofer.fastdoubleparser.JsonDoubleParser;

class MyMain {
  public static void main(String... args) {
    double d = JavaDoubleParser.parseDouble("1.2345e135");
    float f = JavaFloatParser.parseFloat("1.2345f");
      BigDecimal bd = JavaBigDecimalParser.parseBigDecimal("1.2345");
      BigInteger bi = JavaBigIntegerParser.parseBigInteger("12345");
      double jsonD = JsonDoubleParser.parseDouble("1.2345e85");
  }
}
```

The `parse...()`-methods take a `CharacterSequence`. a `char`-array or a `byte`-array as argument. This way. you can
parse from a `StringBuffer` or an array without having to convert your input to a `String`. Parsing from an array is
faster. because the parser can process multiple characters at once using SIMD instructions.

## Performance Tuning

The JVM does not reliably inline `String.charAt(int)`. This may negativily impact the
`parse...()`-methods that take a `CharacterSequence` as an argument.

To ensure optimal performance, you can use the following java command line option:

    -XX:CompileCommand=inline,java/lang/String.charAt

## Performance Characteristics

### `float` and `double` parsers

On common input data, the fast `double` and `float` parsers are about 4 times faster than
`java.lang.Double.valueOf(String)` and `java.lang.Float.valueOf(String)`.

For less common inputs, the fast parsers can be slower than their `java.lang` counterparts.

A `double` value can always be specified exactly with up to 17 digits in the significand.
A `float` only needs up to 8 digits.
Therefore, inputs with more than 19 digits in the significand are considered less common.
Such inputs are expected to occur if the input data was created with more precision, and needs to be narrowed down
to the precision of a `double` or a `float`.

### `BigDecimal` and `BigInteger` parsers

On common input data, the fast `BigDecimal` and `BigInteger` parsers are slightly faster than
`java.math.BigDecimal(String)` and `java.math.BigInteger(String)`.

For less common inputs with many digits, the fast parsers can be a lot faster than their `java.math` counterparts.
The fast parsers can convert even the longest supported inputs in less than 6 minutes, whereas
their `java.math` counterparts need months!

The fast parsers convert digit characters from base 10 to a bit sequence in base 2
using a divide-and-conquer algorithm. Small sequences of digits are converted
individually to bit sequences and then gradually combined to the final bit sequence.
This algorithm needs to perform multiplications of very long bit sequences.
The multiplications are performed in the frequency domain using a discrete fourier transform.
The multiplications in the frequency domain can be performed in `O(Nlog N (log log N))` time,
where `N` is the number of digits.
In contrast, conventional multiplication algorithms in the time domain need `O(N²)` time.


### Memory usage and computation time

The memory usage depends on the result type and the maximal supported input character length.

The computation times are given for a Mac mini 2018 with Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz.

| Parser               |Result Type          | Maximal<br/>input length | Memory usage<br/>JVM -Xmx | Computation<br/>Time |
|----------------------|---------------------|---------------------:|--------------------------:|---------------------:|
| JavaDoubleParser     |java.lang.Double     |             2^31 - 5 |              10 gigabytes |              < 5 sec |
| JavaFloatParser      |java.lang.Float      |             2^31 - 5 |              10 gigabytes |              < 5 sec |
| JavaBigIntegerParser |java.math.BigInteger |        1,292,782,622 |              16 gigabytes |              < 6 min |
| JavaBigDecimalParser |java.math.BigDecimal |        1,292,782,635 |              16 gigabytes |              < 6 min |

## Performance measurements

On my Mac mini (2018) I get the results shown below.

### Random double numbers in the range from 0 to 1

Most input lines look like this: `0.4011441469603171`.

|Method                     | MB/s  |stdev|Mfloats/s| ns/f   | speedup | JDK    |
|---------------------------|------:|-----:|------:|--------:|--------:|--------|
|java.lang.Double           |  91.73|10.1 %|   5.26|   189.95|     1.00|20      |
|java.lang.Float            |  92.93| 6.1 %|   5.33|   187.50|     1.00|20      |
|java.math.BigDecimal       | 172.14| 7.2 %|   9.88|   101.22|     1.00|20      |
|JavaDoubleParser String    | 554.66| 4.0 %|  31.83|    31.41|     6.05|20      |
|JavaDoubleParser char[]    | 592.95|13.4 %|  34.03|    29.39|     6.46|20      |
|JavaDoubleParser byte[]    | 642.69| 3.6 %|  36.88|    27.11|     7.01|20      |
|JsonDoubleParser String    | 551.93|17.4 %|  31.68|    31.57|     6.02|20      |
|JsonDoubleParser char[]    | 612.85|14.2 %|  35.17|    28.43|     6.68|20      |
|JsonDoubleParser byte[]    | 642.12| 4.6 %|  36.85|    27.14|     7.00|20      |
|JavaFloatParser  String    | 519.72|16.3 %|  29.83|    33.53|     5.59|20      |
|JavaFloatParser  char[]    | 599.44| 5.6 %|  34.40|    29.07|     6.45|20      |
|JavaFloatParser  byte[]    | 621.22|11.2 %|  35.65|    28.05|     6.68|20      |
|JavaBigDecimalParser String| 513.70|17.4 %|  29.48|    33.92|     2.98|20      |
|JavaBigDecimalParser char[]| 617.66|25.9 %|  35.45|    28.21|     3.59|20      |
|JavaBigDecimalParser byte[]| 670.74| 5.8 %|  38.49|    25.98|     3.90|20      |

### The data file `canada.txt`

This file contains numbers in the range from -128 to +128.
Most input lines look like this: `52.038048000000117`.

|Method                     | MB/s  |stdev|Mfloats/s| ns/f   | speedup | JDK    |
|---------------------------|------:|-----:|------:|--------:|--------:|--------|
|java.lang.Double           |  80.33| 5.2 %|   4.62|   216.62|     1.00|20      |
|java.lang.Float            |  94.04| 4.7 %|   5.40|   185.04|     1.00|20      |
|java.math.BigDecimal       | 298.46|11.9 %|  17.15|    58.30|     1.00|20      |
|JavaDoubleParser String    | 366.14|13.9 %|  21.04|    47.53|     4.56|20      |
|JavaDoubleParser char[]    | 590.31| 4.8 %|  33.92|    29.48|     7.35|20      |
|JavaDoubleParser byte[]    | 548.01|12.2 %|  31.49|    31.75|     6.82|20      |
|JsonDoubleParser String    | 404.25|14.8 %|  23.23|    43.05|     5.03|20      |
|JsonDoubleParser char[]    | 575.44| 3.5 %|  33.07|    30.24|     7.16|20      |
|JsonDoubleParser byte[]    | 576.21| 3.2 %|  33.11|    30.20|     7.17|20      |
|JavaFloatParser  String    | 342.41|14.5 %|  19.68|    50.82|     3.64|20      |
|JavaFloatParser  char[]    | 576.07|16.1 %|  33.10|    30.21|     6.13|20      |
|JavaFloatParser  byte[]    | 592.73|12.2 %|  34.06|    29.36|     6.30|20      |
|JavaBigDecimalParser String| 420.41|15.4 %|  24.16|    41.39|     1.41|20      |
|JavaBigDecimalParser char[]| 643.92|18.4 %|  37.00|    27.02|     2.16|20      |
|JavaBigDecimalParser byte[]| 686.99| 5.8 %|  39.48|    25.33|     2.30|20      |

### The data file `mesh.txt`

This file contains input lines like `1749`, and `0.539081215858`.

|Method                     | MB/s  |stdev|Mfloats/s| ns/f   | speedup | JDK    |
|---------------------------|------:|-----:|------:|--------:|--------:|--------|
|java.lang.Double           | 167.28|22.7 %|  22.79|    43.88|     1.00|20-ea   |
|java.lang.Float            |  94.41|11.0 %|  12.86|    77.75|     1.00|20-ea   |
|java.math.BigDecimal       | 181.11|24.2 %|  24.67|    40.53|     1.00|20-ea   |
|JavaDoubleParser String    | 230.94|22.0 %|  31.46|    31.79|     1.38|20-ea   |
|JavaDoubleParser char[]    | 330.35|23.2 %|  45.00|    22.22|     1.97|20-ea   |
|JavaDoubleParser byte[]    | 353.26|20.2 %|  48.12|    20.78|     2.11|20-ea   |
|JsonDoubleParser String    | 230.26|19.0 %|  31.37|    31.88|     1.38|20-ea   |
|JsonDoubleParser char[]    | 321.63|23.1 %|  43.81|    22.82|     1.92|20-ea   |
|JsonDoubleParser byte[]    | 374.07|24.2 %|  50.96|    19.62|     2.24|20-ea   |
|JavaFloatParser  String    | 199.30|22.5 %|  27.15|    36.83|     2.11|20-ea   |
|JavaFloatParser  char[]    | 245.89|33.6 %|  33.50|    29.85|     2.60|20-ea   |
|JavaFloatParser  byte[]    | 287.94|33.6 %|  39.22|    25.49|     3.05|20-ea   |
|JavaBigDecimalParser String| 211.58|36.2 %|  28.82|    34.70|     1.17|20-ea   |
|JavaBigDecimalParser char[]| 319.68|39.6 %|  43.55|    22.96|     1.77|20-ea   |
|JavaBigDecimalParser byte[]| 337.29|36.4 %|  45.95|    21.76|     1.86|20-ea   |

### The data file `canada_hex.txt`

This file contains numbers in the range from -128 to +128 in hexadecimal notation.
Most input lines look like this: `-0x1.09219008205fcp6`.

|Method                     | MB/s  |stdev|Mfloats/s| ns/f   | speedup | JDK    |
|---------------------------|------:|-----:|------:|--------:|--------:|--------|
|java.lang.Double           |  36.57| 2.4 %|   2.01|   498.71|     1.00|20      |
|java.lang.Float            |  36.53| 2.4 %|   2.00|   499.29|     1.00|20      |
|JavaDoubleParser String    | 314.91|11.1 %|  17.27|    57.92|     8.61|20      |
|JavaDoubleParser char[]    | 450.63|15.9 %|  24.71|    40.47|    12.32|20      |
|JavaDoubleParser byte[]    | 553.66|15.0 %|  30.36|    32.94|    15.14|20      |
|JavaFloatParser  String    | 322.78|12.3 %|  17.70|    56.50|     8.84|20      |
|JavaFloatParser  char[]    | 454.16|16.1 %|  24.90|    40.16|    12.43|20      |
|JavaFloatParser  byte[]    | 561.10|16.1 %|  30.77|    32.50|    15.36|20      |

### Comparison with C version

For comparison. here are the test results
of [simple_fastfloat_benchmark](https://github.com/lemire/simple_fastfloat_benchmark)  
on the same computer:

    version: Thu Mar 31 10:18:12 2022 -0400 f2082bf747eabc0873f2fdceb05f9451931b96dc

    Intel(R) Core(TM) i7-8700B CPU @ 3.20GHz SIMD-256

    $ ./build/benchmarks/benchmark
    # parsing random numbers
    available models (-m): uniform one_over_rand32 simple_uniform32 simple_int32 int_e_int simple_int64 bigint_int_dot_int big_ints 
    model: generate random numbers uniformly in the interval [0.0.1.0]
    volume: 100000 floats
    volume = 2.09808 MB 
    netlib                                  :   317.31 MB/s (+/- 6.0 %)    15.12 Mfloat/s      66.12 ns/f 
    doubleconversion                        :   263.89 MB/s (+/- 4.2 %)    12.58 Mfloat/s      79.51 ns/f 
    strtod                                  :    86.13 MB/s (+/- 3.7 %)     4.10 Mfloat/s     243.61 ns/f 
    abseil                                  :   467.27 MB/s (+/- 9.0 %)    22.27 Mfloat/s      44.90 ns/f 
    fastfloat                               :   880.79 MB/s (+/- 6.6 %)    41.98 Mfloat/s      23.82 ns/f 

    OpenJDK 20-ea+22-1594
    java.lang.Double                        :    89.59 MB/s (+/- 6.0 %)     5.14 Mfloat/s     194.44 ns/f
    JavaDoubleParser String                 :   485.97 MB/s (+/-13.8 %)    27.90 Mfloat/s      35.85 ns/f
    JavaDoubleParser char[]                 :   562.55 MB/s (+/-10.0 %)    32.29 Mfloat/s      30.97 ns/f
    JavaDoubleParser byte[]                 :   644.65 MB/s (+/- 8.7 %)    37.01 Mfloat/s      27.02 ns/f

'

    $ ./build/benchmarks/benchmark -f data/canada.txt
    # read 111126 lines 
    volume = 1.93374 MB 
    netlib                                  :   337.79 MB/s (+/- 5.8 %)    19.41 Mfloat/s      51.52 ns/f 
    doubleconversion                        :   254.22 MB/s (+/- 6.0 %)    14.61 Mfloat/s      68.45 ns/f 
    strtod                                  :    73.33 MB/s (+/- 7.1 %)     4.21 Mfloat/s     237.31 ns/f 
    abseil                                  :   411.11 MB/s (+/- 7.3 %)    23.63 Mfloat/s      42.33 ns/f 
    fastfloat                               :   741.32 MB/s (+/- 5.3 %)    42.60 Mfloat/s      23.47 ns/f 

    OpenJDK 20-ea+29-2280
    java.lang.Double            :    77.84 MB/s (+/- 4.1 %)     4.47 Mfloat/s     223.54 ns/f     1.00 speedup
    JavaDoubleParser String     :   329.79 MB/s (+/-13.4 %)    18.95 Mfloat/s      52.77 ns/f     4.24 speedup
    JavaDoubleParser char[]     :   521.30 MB/s (+/-15.2 %)    29.96 Mfloat/s      33.38 ns/f     6.70 speedup
    JavaDoubleParser byte[]     :   560.48 MB/s (+/-12.7 %)    32.21 Mfloat/s      31.05 ns/f     7.20 speedup

# Building and running the code

When you clone the code repository from github. you can choose from the following branches:

- `main` Aims to contain only working code.
- `dev` This code may or may not work. This code uses the experimental Vector API, and the Foreign Memory Access API,
  that are included in Java 20.


## Command sequence with Java SE 20 on macOS:

```shell
git clone https://github.com/wrandelshofer/FastDoubleParser.git
cd FastDoubleParser 
javac --enable-preview -source 20 -d out -encoding utf8 --module-source-path fastdoubleparser-dev/src/main/java --module ch.randelshofer.fastdoubleparser    
javac --enable-preview -source 20 -d out -encoding utf8 -p out --module-source-path fastdoubleparserdemo-dev/src/main/java --module ch.randelshofer.fastdoubleparserdemo
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main  
java -XX:CompileCommand=inline,java/lang/String.charAt --enable-preview -p out -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main FastDoubleParserDemo/data/canada.txt   
```

## Command sequence with Java SE 8, 11, 17, 19 and 20 and Maven 3.8.6 on macOS:

```shell
git clone https://github.com/wrandelshofer/FastDoubleParser.git
cd FastDoubleParser
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-20.jdk/Contents/Home 
mvn clean
mvn package
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-20.jdk/Contents/Home 
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-19.jdk/Contents/Home 
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-17.jdk/Contents/Home 
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.8.jdk/Contents/Home
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/mesh.txt
java -XX:CompileCommand=inline,java/lang/String.charAt -p fastdoubleparser/target:fastdoubleparserdemo/target -m ch.randelshofer.fastdoubleparserdemo/ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada_hex.txt
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_281.jdk/Contents/Home
java -XX:CompileCommand=inline,java/lang/String.charAt -cp "fastdoubleparser/target/*:fastdoubleparserdemo/target/*" ch.randelshofer.fastdoubleparserdemo.Main --markdown
java -XX:CompileCommand=inline,java/lang/String.charAt -cp "fastdoubleparser/target/*:fastdoubleparserdemo/target/*" ch.randelshofer.fastdoubleparserdemo.Main --markdown FastDoubleParserDemo/data/canada.txt
```

## IntelliJ IDEA with Java SE 8, 11, 17, 19 and 20 on macOS

Prerequisites:

1. Install the following Java SDKs: 8, 11, 17, 19 and 20.
   _If you do not need to edit the code, you only need to install the Java 20 SDK._
2. Install IntelliJ IDEA

Steps:

1. Start IntelliJ IDEA
2. From the main menu, choose **Git > Clone...**
3. In the dialog that opens, enter the URL https://github.com/wrandelshofer/FastDoubleParser.git,
   specify the directory in which you want to save the project and click **Clone**.
4. Intellij IDEA will now clone the repository and open a new project window.
   However, the project modules are not yet configured correctly.
5. From the main menu of the new project window, choose **View > Tool Windows > Maven**
6. In the Maven tool window, run the Maven build **Parent project for FastDoubleParser > Lifecycle > compile**
7. In the toolbar of the Maven tool window, click **Reload All Maven Projects**
8. Intellij IDEA knows now for each module, where the **source**,
   **generated source**,  **test source**, and **generated test source** folders are.
   However, the project modules have still incorrect JDK dependencies.
9. _You can skip this step, if you do not want to edit the code._
   From the main menu, choose **File > Project Structure...**
10. _You can skip this step, if you do not want to edit the code._
    In the dialog that opens, select in the navigation bar **Project Settings > Modules**
11. _You can skip this step, if you do not want to edit the code._
    For each module in the right pane of the dialog, select the **Dependencies** tab.
    Specify the corresponding **Module SDK** for modules which have a name that ends in
    **-Java8**, **-Java11**, **-Java17**, **-Java19**.
    Do not change modules with other name endings - they must stay on the Java 20 SDK.

12. From the main menu, choose **Build > Build Project**
    Intellij IDEA will now properly build the project.

## Editing the code

The majority of the code is located in the module named **fastdoubleparser-dev**,
and **fastdoubleparserdemo-dev**.
The code in these modules uses early access features of the Java 20 SDK.

Modules which have a name that ends in **-Java8**, **-Java11**, **-Java17**, **-Java19**
contain deltas of the **-dev** modules.

The delta code is located in the **source** and **test** folders of the module.
Code from the **-dev** module is located in the **generated source** and
**generated test source** folders.

The Maven POM of a module contains **maven-resources-plugin** elements that copy code
from the **-dev** module to the delta modules.
