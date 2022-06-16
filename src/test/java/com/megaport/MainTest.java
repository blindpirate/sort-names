package com.megaport;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MainTest {
    @Test
    public void canSortNames(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("test.txt");
        Files.write(tempDir.resolve("test.txt"), Arrays.asList("BAKER, THEODORE", "SMITH, ANDREW", "KENT, MADISON", "SMITH, FREDRICK"));
        Main.main(new String[]{input.toAbsolutePath().toString()});

        List<String> outputLines = Files.readAllLines(tempDir.resolve("test-sorted.txt")).stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        Assertions.assertEquals(Arrays.asList("BAKER, THEODORE", "KENT, MADISON", "SMITH, ANDREW", "SMITH, FREDRICK"), outputLines);
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
        MatcherAssert.assertThat(exception.getMessage(), CoreMatchers.containsString("Can't read file"));
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
}
