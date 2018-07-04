package com.github.halca.uri;

import org.junit.jupiter.api.Test;

import static com.github.halca.uri.URITemplateParser.parseAndExpand;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParseErrorTest {

    @Test
    public void shouldThrowNPEOnNull() {
        assertThrows(NullPointerException.class, () -> {
            parseAndExpand(null, emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnVarStartCharacter() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            parseAndExpand("{", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnVarEndCharacter() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            parseAndExpand("}", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnEmptyVar() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            parseAndExpand("{}", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnEmptyVarInURI() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            parseAndExpand("http://www.example.com/{}", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnVarStartInURI() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            parseAndExpand("http://www.example.com/{", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnNotClosedVar() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            parseAndExpand("http://www.example.com/{something", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnEmptyVarNameWithComma() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            parseAndExpand("http://www.example.com/{a,}", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnEmptyVarNameWithComma2() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            parseAndExpand("http://www.example.com/{a,,b}", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnDoubleOpeningInVar() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            parseAndExpand("http://www.example.com/{{var}", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnDoubleCloseCharacter() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            parseAndExpand("http://www.example.com/{var}}", emptyMap());
        });
    }

}
