package com.github.halca.uri;

import org.junit.jupiter.api.function.Executable;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;

public final class AdvancedAssertions {

    private AdvancedAssertions() {}

    public static <T> void assertForEach(Stream<T> values, Consumer<T> assertion) {
        assertAll(values.map(value -> (Executable) (() -> assertion.accept(value))));
    }

}
