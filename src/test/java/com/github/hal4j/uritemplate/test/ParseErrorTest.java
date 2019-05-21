package com.github.hal4j.uritemplate.test;

import com.github.hal4j.uritemplate.URITemplateSyntaxException;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static com.github.hal4j.uritemplate.URITemplateParser.parseAndExpand;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ParseErrorTest {

    private static final Map<String, Object> ANY = emptyMap();

    @Test
    void shouldThrowNPEOnNull() {
        assertThrows(NullPointerException.class,
                () -> parseAndExpand(null, ANY));
    }

    @Test
    void shouldThrowSyntaxErrorOnVarStartCharacter() {
        assertThrows(URITemplateSyntaxException.class,
                () -> parseAndExpand("{", ANY));
    }

    @Test
    void shouldThrowSyntaxErrorOnVarEndCharacter() {
        assertThrows(URITemplateSyntaxException.class,
                () -> parseAndExpand("}", ANY));
    }

    @Test
    void shouldThrowSyntaxErrorOnEmptyVar() {
        assertThrows(URITemplateSyntaxException.class,
                () -> parseAndExpand("{}", ANY));
    }

    @Test
    void shouldThrowSyntaxErrorOnEmptyVarInURI() {
        assertThrows(URITemplateSyntaxException.class,
                () -> parseAndExpand("http://www.example.com/{}", ANY));
    }

    @Test
    void shouldThrowSyntaxErrorOnVarStartInURI() {
        assertThrows(URITemplateSyntaxException.class,
                () -> parseAndExpand("http://www.example.com/{", ANY));
    }

    @Test
    void shouldThrowSyntaxErrorOnNotClosedVar() {
        assertThrows(URITemplateSyntaxException.class,
                () -> parseAndExpand("http://www.example.com/{something", ANY));
    }

    @Test
    void shouldThrowSyntaxErrorOnEmptyVarNameWithComma() {
        assertThrows(URITemplateSyntaxException.class,
                () -> parseAndExpand("http://www.example.com/{a,}", ANY));
    }

    @Test
    void shouldThrowSyntaxErrorOnEmptyVarNameWithComma2() {
        assertThrows(URITemplateSyntaxException.class,
                () -> parseAndExpand("http://www.example.com/{a,,b}", ANY));
    }

    @Test
    void shouldThrowSyntaxErrorOnDoubleOpeningInVar() {
        assertThrows(URITemplateSyntaxException.class,
                () -> parseAndExpand("http://www.example.com/{{var}", ANY));
    }

    @Test
    void shouldThrowSyntaxErrorOnDoubleCloseCharacter() {
        assertThrows(URITemplateSyntaxException.class,
                () -> parseAndExpand("http://www.example.com/{var}}", ANY));
    }

}
