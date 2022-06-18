package com.megaport;

import org.apache.commons.lang3.StringUtils;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.megaport.MainTest.loadResourceAsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SortNamesSessionTest {
    @ParameterizedTest
    @CsvSource({
        "/test1/input1.txt,     /test1/output.txt",
        "/test1/recoverable-input2.txt, /test1/output.txt",
        "/test1/input3.txt,     /test1/output.txt",
        "/test1/input4.txt,     /test1/output.txt"
    })
    public void canSortNames(String input, String output, @TempDir File tempDir) throws IOException {
        File inputFile = new File(tempDir, "input.txt");
        File outputFile = new File(tempDir, "output.txt");
        Files.write(inputFile.toPath(), loadResourceAsString(input).getBytes(StandardCharsets.UTF_8));
        new SortNamesSession(inputFile, outputFile).process();

        List<String> outputLines = Files.readAllLines(outputFile.toPath()).stream().filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        List<String> expectedOutputLines = Stream.of(loadResourceAsString(output).split("\\n")).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        assertEquals(expectedOutputLines, outputLines);
    }

    @ParameterizedTest
    @CsvSource(delimiter = ';',
        value = {
            "/bad-input/bad-input1.txt; Invalid line at line 1: ,",
            "/bad-input/bad-input2.txt; Invalid line at line 4: SMITH FREDRICK"
        })
    public void complainOnBadInput(String input, String expectedErrorMessage, @TempDir File tempDir) throws IOException {
        Exception exception = assertThrows(Exception.class, () -> {
            File inputFile = new File(tempDir, "test.txt");
            File outputFile = new File(tempDir, "test-output.txt");
            Files.write(inputFile.toPath(), loadResourceAsString(input).getBytes(StandardCharsets.UTF_8));
            new SortNamesSession(inputFile, outputFile).process();
        });
        assertThat(exception.getMessage(), CoreMatchers.containsString(expectedErrorMessage));
    }
}
