package com.github.hal4j.uritemplate.test;

import com.github.hal4j.uritemplate.URITemplateModifier;
import com.github.hal4j.uritemplate.URITemplateSyntaxException;
import com.github.hal4j.uritemplate.URITemplateVariable;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static com.github.hal4j.uritemplate.URITemplateModifier.valueOf;
import static com.github.hal4j.uritemplate.URITemplateVariable.template;
import static com.github.hal4j.uritemplate.URIVarComponent.prefix;
import static com.github.hal4j.uritemplate.URIVarComponent.var;
import static org.junit.jupiter.api.Assertions.*;

class URITemplateVariableTest {

    private static final String SOME_NAME = "name";
    private static final char SOME_VALID_MODIFIER = '/';
    private static final String PREFIXED_NAME = "test";
    private static final int SOME_PREFIX = 3;

    @Test
    void shouldCorrectlyParseShortName() {
        URITemplateVariable var = URITemplateVariable.parse("id");
        assertFalse(var.modifier().isPresent());
        assertEquals(1, var.components().size());
        assertEquals("id", var.components().get(0).name());
    }

    @Test
    void shouldCorrectlyParseWrappedName() {
        URITemplateVariable var = URITemplateVariable.parse("{?id,name*}");
        assertTrue(var.modifier().isPresent());
        assertEquals('?', var.modifier().get().modifierChar());
        assertEquals(2, var.components().size());
        assertEquals("id", var.components().get(0).name());
        assertEquals("name", var.components().get(1).name());
    }

    @Test
    void shouldCorrectlyParseSimpleName() {
        URITemplateVariable var = URITemplateVariable.parse(SOME_NAME);
        assertFalse(var.modifier().isPresent());
        assertEquals(1, var.components().size());
        assertEquals(SOME_NAME, var.components().get(0).name());
    }

    @Test
    void shouldRejectVarNameStartingWithModifierCharacter() {
        AdvancedAssertions.assertForEach(
                Arrays.stream(URITemplateModifier.values()).map(URITemplateModifier::modifierChar),
                character -> Assertions.assertThrows(URITemplateSyntaxException.class,
                        () -> URITemplateVariable.template(character + SOME_NAME),
                        "Should reject name starting with \"" + character + "\""));
    }

    @Test
    void shouldCorrectlyParseNameWithValidModifier() {
        URITemplateVariable var = URITemplateVariable.parse(SOME_VALID_MODIFIER + SOME_NAME);
        assertTrue(var.modifier().isPresent());
        assertEquals(SOME_VALID_MODIFIER, var.modifier().get().modifierChar());
        assertEquals(1, var.components().size());
        assertEquals(SOME_NAME, var.components().get(0).name());
    }

    @Test
    void shouldCorrectlyBuildComplexExample() {
        URITemplateModifier mod = valueOf(SOME_VALID_MODIFIER).orElse(null);
        URITemplateVariable var = template(mod, var(SOME_NAME), prefix(PREFIXED_NAME, SOME_PREFIX));
        assertEquals(mod, var.modifier().orElse(null));
        assertEquals(2, var.components().size());
        assertEquals(SOME_NAME, var.components().get(0).name());
        assertEquals(PREFIXED_NAME, var.components().get(1).name());
        assertEquals(SOME_PREFIX, (int) var.components().get(1).prefixLength().orElse(0));
    }

    @Test
    void shouldCorrectlySerializeComplexExample() {
        URITemplateModifier mod = valueOf(SOME_VALID_MODIFIER).orElse(null);
        URITemplateVariable var = template(mod, var(SOME_NAME), prefix(PREFIXED_NAME, SOME_PREFIX));
        String s = var.toString();
        assertEquals(wrap(SOME_VALID_MODIFIER + SOME_NAME + ',' + PREFIXED_NAME + ':' + SOME_PREFIX), s);
    }

    @Test
    void shouldCorrectlyParseComplexExample() {
        String s = SOME_VALID_MODIFIER + SOME_NAME + ',' + PREFIXED_NAME + ':' + SOME_PREFIX;
        URITemplateVariable parsed = URITemplateVariable.parse(s);
        assertEquals(valueOf(SOME_VALID_MODIFIER).orElse(null), parsed.modifier().orElse(null));
        assertEquals(2, parsed.components().size());
        assertEquals(SOME_NAME, parsed.components().get(0).name());
        assertEquals(PREFIXED_NAME, parsed.components().get(1).name());
        assertEquals(SOME_PREFIX, (int) parsed.components().get(1).prefixLength().orElse(0));
    }

    @Test
    void shouldCorrectlyWriteSimpleNameToString() {
        String name = SOME_NAME;
        URITemplateVariable var = URITemplateVariable.parse(name);
        assertEquals(wrap(name), var.toString());
    }

    @Test
    void shouldCorrectlyWriteNameWithValidModifierToString() {
        String name = SOME_VALID_MODIFIER + SOME_NAME;
        URITemplateVariable var = URITemplateVariable.parse(name);
        assertEquals(wrap(name), var.toString());
    }

    private String wrap(String name) {
        return "{" + name + "}";
    }

}
