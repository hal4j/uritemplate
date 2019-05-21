package com.github.hal4j.uritemplate.test;

import com.github.hal4j.uritemplate.URITemplate;
import com.github.hal4j.uritemplate.URITemplateVariable;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class URITemplateVariablesTest {

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
    void shouldReturnSimpleVariableFromPath() {
        assertContainsAll("https://www.example.com:8080/a/b/{any}/c?query=hello#fragment",
                URITemplateVariable.template("any"));
    }

    @Test
    void shouldReturnPathVariableFromPath() {
        assertContainsAll("https://www.example.com:8080/a/b{/any}/c?query=hello#fragment",
                URITemplateVariable.pathVariable("any"));
    }

    @Test
    void shouldReturnMultipleTemplateVars() {
        assertContainsAll("https://www.{.domain}:8080/a/b/{;any*}{/id}{?query,kind:5}",
                URITemplateVariable.parse(".domain"),
                URITemplateVariable.parse(";any*"),
                URITemplateVariable.parse("/id"),
                URITemplateVariable.parse("?query,kind:5"));
    }

}
