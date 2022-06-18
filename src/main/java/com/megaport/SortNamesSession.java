package com.megaport;

import com.google.common.collect.Streams;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

public class SortNamesSession {
    private final File inputFile;
    private final File outputFile;

    public SortNamesSession(File inputFile, File outputFile) {
        this.inputFile = inputFile;
        this.outputFile = outputFile;
    }

    public void process() {
        Stream<String> lines = readLines().stream()
            .map(String::trim)
            .filter(StringUtils::isNotEmpty);
        try (Writer outputWriter = new BufferedWriter(new FileWriter(outputFile))) {
            Streams.mapWithIndex(lines, Name::parseLine)
                .sorted(Name.COMPARATOR)
                .map(Name::toOutputLine)
                .peek(System.out::println)
                .forEach(line -> {
                    try {
                        outputWriter.append(line).append("\n");
                    } catch (IOException e) {
                        throw new UncheckedIOException(e);
                    }
                });
        } catch (IOException | UncheckedIOException e) {
            throw new IllegalStateException("Can't write to output file", e);
        }

        System.out.println("Finished: created " + outputFile.getAbsolutePath());
    }

    private List<String> readLines() {
        try {
            return Files.readAllLines(inputFile.toPath());
        } catch (IOException e) {
            throw new IllegalStateException("Can't read file " + inputFile.getAbsolutePath(), e);
        }
    }
}
