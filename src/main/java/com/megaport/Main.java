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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        String fileNameToBeSorted = parseCommandLineArgs(args);
        Stream<String> lines = readLines(fileNameToBeSorted).stream()
            .map(String::trim)
            .filter(StringUtils::isNotEmpty);

        File outputFile = determineOutputFile(fileNameToBeSorted);
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

    private static File determineOutputFile(String inputFileName) {
        File inputFile = new File(inputFileName);
        File parentFile = inputFile.getParentFile();
        String fileName = inputFile.getName().replaceFirst("\\.", "-sorted.");
        return new File(parentFile, fileName);
    }

    private static List<String> readLines(String fileName) {
        try {
            return Files.readAllLines(new File(fileName).toPath());
        } catch (IOException e) {
            throw new IllegalStateException("Can't read file " + fileName, e);
        }
    }

    private static String parseCommandLineArgs(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("We expect exact 1 parameters, but it's " + Arrays.toString(args));
        }
        return args[0];
    }
}
