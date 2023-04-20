package com.github.hal4j.uritemplate.bugs;

import org.junit.jupiter.api.Test;

import static com.github.hal4j.uritemplate.URIBuilder.basedOn;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @link https://github.com/hal4j/uritemplate/issues/4
 * @author https://github.com/mancave
 */
public class Issue4Test {

    @Test
    void shouldAppendMultipleQueryParamsAfterExistingWithCorrectDelimiters() {
        String s = basedOn("http://www.example.com?val1=hello1")
                .queryParam("val3", "hello3")
                .queryParam("val2", "hello2")
                .toString();
        assertEquals("http://www.example.com?val1=hello1&val3=hello3&val2=hello2", s);
    }

    @Test
    void shouldAppendMultipleQueryParamsToEmptyQueryWithCorrectDelimiters() {
        String s = basedOn("http://www.example.com")
                .queryParam("val3", "hello3")
                .queryParam("val2", "hello2")
                .toString();
        assertEquals("http://www.example.com?val3=hello3&val2=hello2", s);
    }

}
