package com.megaport;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.aggregator.AggregateWith;
import org.junit.jupiter.params.aggregator.ArgumentsAccessor;
import org.junit.jupiter.params.aggregator.ArgumentsAggregationException;
import org.junit.jupiter.params.aggregator.ArgumentsAggregator;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTest {
    @Test
    public void canSortNames(@TempDir Path tempDir) throws IOException {
        Path inputFile = tempDir.resolve("test.txt");
        Files.write(tempDir.resolve("test.txt"), loadResourceAsString("/test1/input1.txt").getBytes(StandardCharsets.UTF_8));
        Main.main(new String[]{inputFile.toAbsolutePath().toString()});

        List<String> outputLines = Files.readAllLines(tempDir.resolve("test-sorted.txt")).stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        List<String> expectedOutputLines = Stream.of(loadResourceAsString("/test1/output.txt").split("\\n")).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        assertEquals(expectedOutputLines, outputLines);
    }

    @Test
    public void throwExceptionIfArgumentIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Main.main(new String[0]));
    }

    @ParameterizedTest
    @CsvSource({"1,2", "3,4,5"})
    public void throwExceptionIfArgumentIsWrong(@AggregateWith(VarargsAggregator.class) String... args) {
        assertThrows(IllegalArgumentException.class, () -> Main.main(args));
    }

    @Test
    public void throwExceptionIfInputFileCantBeRead(@TempDir Path tempDir) {
        Exception exception = assertThrows(Exception.class, () -> Main.main(new String[]{tempDir.resolve("not-exist.txt").toAbsolutePath().toString()}));
        assertThat(exception.getMessage(), CoreMatchers.containsString("Can't read file"));
    }

    // https://github.com/junit-team/junit5/issues/2256
    static class VarargsAggregator implements ArgumentsAggregator {
        @Override
        public Object aggregateArguments(ArgumentsAccessor accessor, ParameterContext context) throws ArgumentsAggregationException {
            return accessor.toList().stream()
                .skip(context.getIndex())
                .map(String::valueOf)
                .toArray(String[]::new);
        }
    }

    public static String loadResourceAsString(String resourceName) {
        try (InputStream is = Main.class.getResourceAsStream(resourceName)) {
            return CharStreams.toString(new InputStreamReader(is, Charsets.UTF_8));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
