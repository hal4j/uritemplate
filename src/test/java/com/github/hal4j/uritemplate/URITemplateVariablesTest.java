package com.github.hal4j.uritemplate;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class URITemplateVariablesTest {

    private void assertContainsAll(String uri, URITemplateVariable... vars) {
        List<URITemplateVariable> parsed = new URITemplate(uri).variables();
        List<URITemplateVariable> expected = Arrays.asList(vars);
        assertEquals(expected.size(), parsed.size());
        assertAll("All variables match", () -> {
            for (int i = 0; i < parsed.size(); i++) {
                assertEquals(parsed.get(i), expected.get(i));
            }
        });

    }

    @Test
    public void shouldReturnSimpleVariableFromPath() {
        assertContainsAll("https://www.example.com:8080/a/b/{any}/c?query=hello#fragment",
                URITemplateVariable.template("any"));
    }

    @Test
    public void shouldReturnPathVariableFromPath() {
        assertContainsAll("https://www.example.com:8080/a/b{/any}/c?query=hello#fragment",
                URITemplateVariable.pathVariable("any"));
    }

    @Test
    public void shouldReturnMultipleTemplateVars() {
        assertContainsAll("https://www.{.domain}:8080/a/b/{;any*}{/id}{?query,kind:5}",
                URITemplateVariable.parse(".domain"),
                URITemplateVariable.parse(";any*"),
                URITemplateVariable.parse("/id"),
                URITemplateVariable.parse("?query,kind:5"));
    }

}
