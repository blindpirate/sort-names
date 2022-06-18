package com.megaport;

import java.io.File;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        File inputFile = parseCommandLineArgs(args);
        new SortNamesSession(inputFile, determineOutputFile(inputFile)).process();
    }

    private static File parseCommandLineArgs(String[] args) {
        if (args.length != 1) {
            throw new IllegalArgumentException("We expect exact 1 parameters, but it's " + Arrays.toString(args));
        }
        return new File(args[0]);
    }

    private static File determineOutputFile(File inputFile) {
        File parentFile = inputFile.getParentFile();
        String fileName = inputFile.getName().replaceFirst("\\.", "-sorted.");
        return new File(parentFile, fileName);
    }
}
