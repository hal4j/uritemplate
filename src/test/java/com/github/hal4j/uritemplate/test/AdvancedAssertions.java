package com.github.hal4j.uritemplate.test;

import org.junit.jupiter.api.function.Executable;

import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;

final class AdvancedAssertions {

    private AdvancedAssertions() {}

    static <T> void assertForEach(Stream<T> values, Consumer<T> assertion) {
        assertAll(values.map(value -> (Executable) (() -> assertion.accept(value))));
    }

}
