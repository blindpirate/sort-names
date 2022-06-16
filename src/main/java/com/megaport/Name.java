package com.megaport;

import org.apache.commons.lang3.StringUtils;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Name {
    public static final Comparator<Name> COMPARATOR =
        Comparator.comparing(Name::getFirstName).thenComparing(Name::getLastName);
    private final String firstName;
    private final String lastName;

    public static Name parseLine(String line, long index) {
        String[] splitByComma = line.split(",");
        List<String> sanitized = Stream.of(splitByComma)
            .map(String::trim)
            .filter(StringUtils::isNotEmpty)
            .collect(Collectors.toList());
        if (sanitized.size() != 2) {
            throw new IllegalArgumentException("Invalid line at line " + (index + 1) + ": " + line);
        }
        return new Name(sanitized.get(0), sanitized.get(1));
    }

    private Name(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String toOutputLine() {
        return firstName + ", " + lastName;
    }
}
