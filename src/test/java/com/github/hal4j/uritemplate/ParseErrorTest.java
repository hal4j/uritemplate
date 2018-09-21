package com.github.hal4j.uritemplate;

import org.junit.jupiter.api.Test;

import static com.github.hal4j.uritemplate.URITemplateParser.parseAndExpand;
import static java.util.Collections.emptyMap;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ParseErrorTest {

    @Test
    public void shouldThrowNPEOnNull() {
        assertThrows(NullPointerException.class, () -> {
            URITemplateParser.parseAndExpand(null, emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnVarStartCharacter() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            URITemplateParser.parseAndExpand("{", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnVarEndCharacter() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            URITemplateParser.parseAndExpand("}", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnEmptyVar() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            URITemplateParser.parseAndExpand("{}", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnEmptyVarInURI() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            URITemplateParser.parseAndExpand("http://www.example.com/{}", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnVarStartInURI() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            URITemplateParser.parseAndExpand("http://www.example.com/{", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnNotClosedVar() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            URITemplateParser.parseAndExpand("http://www.example.com/{something", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnEmptyVarNameWithComma() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            URITemplateParser.parseAndExpand("http://www.example.com/{a,}", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnEmptyVarNameWithComma2() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            URITemplateParser.parseAndExpand("http://www.example.com/{a,,b}", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnDoubleOpeningInVar() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            URITemplateParser.parseAndExpand("http://www.example.com/{{var}", emptyMap());
        });
    }

    @Test
    public void shouldThrowSyntaxErrorOnDoubleCloseCharacter() {
        assertThrows(URITemplateSyntaxException.class, () -> {
            URITemplateParser.parseAndExpand("http://www.example.com/{var}}", emptyMap());
        });
    }

}
