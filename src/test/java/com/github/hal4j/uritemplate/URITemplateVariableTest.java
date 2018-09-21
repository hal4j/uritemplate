package com.github.hal4j.uritemplate;

import org.junit.jupiter.api.Test;

import static com.github.hal4j.uritemplate.URITemplateModifier.valueOf;
import static com.github.hal4j.uritemplate.URITemplateVariable.template;
import static com.github.hal4j.uritemplate.URIVarComponent.prefix;
import static com.github.hal4j.uritemplate.URIVarComponent.var;
import static java.util.Arrays.stream;
import static org.junit.jupiter.api.Assertions.*;

public class URITemplateVariableTest {

    public static final String SOME_NAME = "name";
    public static final char SOME_VALID_MODIFIER = '/';
    private static final String PREFIXED_NAME = "test";
    private static final int SOME_PREFIX = 3;

    @Test
    public void shouldCorrectlyParseSimpleName() {
        URITemplateVariable var = URITemplateVariable.parse(SOME_NAME);
        assertFalse(var.modifier().isPresent());
        assertEquals(1, var.components().size());
        assertEquals(SOME_NAME, var.components().get(0).name());
    }

    @Test
    public void shouldRejectVarNameStartingWithModifierCharacter() {
        AdvancedAssertions.assertForEach(
                stream(URITemplateModifier.values()).map(URITemplateModifier::modifierChar),
                character -> assertThrows(URITemplateSyntaxException.class,
                        () -> URITemplateVariable.template(character + SOME_NAME),
                        "Should reject name starting with \"" + character + "\""));
    }

    @Test
    public void shouldCorrectlyParseNameWithValidModifier() {
        URITemplateVariable var = URITemplateVariable.parse(SOME_VALID_MODIFIER + SOME_NAME);
        assertTrue(var.modifier().isPresent());
        assertEquals(SOME_VALID_MODIFIER, var.modifier().get().modifierChar());
        assertEquals(1, var.components().size());
        assertEquals(SOME_NAME, var.components().get(0).name());
    }

    @Test
    public void shouldCorrectlyBuildComplexExample() {
        URITemplateModifier mod = valueOf(SOME_VALID_MODIFIER).orElse(null);
        URITemplateVariable var = template(mod, var(SOME_NAME), prefix(PREFIXED_NAME, SOME_PREFIX));
        assertEquals(mod, var.modifier().orElse(null));
        assertEquals(2, var.components().size());
        assertEquals(SOME_NAME, var.components().get(0).name());
        assertEquals(PREFIXED_NAME, var.components().get(1).name());
        assertEquals(SOME_PREFIX, (int) var.components().get(1).prefixLength().orElse(0));
    }

    @Test
    public void shouldCorrectlySerializeComplexExample() {
        URITemplateModifier mod = valueOf(SOME_VALID_MODIFIER).orElse(null);
        URITemplateVariable var = template(mod, var(SOME_NAME), prefix(PREFIXED_NAME, SOME_PREFIX));
        String s = var.toString();
        assertEquals(wrap(SOME_VALID_MODIFIER + SOME_NAME + ',' + PREFIXED_NAME + ':' + SOME_PREFIX), s);
    }

    @Test
    public void shouldCorrectlyParseComplexExample() {
        String s = SOME_VALID_MODIFIER + SOME_NAME + ',' + PREFIXED_NAME + ':' + SOME_PREFIX;
        URITemplateVariable parsed = URITemplateVariable.parse(s);
        assertEquals(valueOf(SOME_VALID_MODIFIER).orElse(null), parsed.modifier().orElse(null));
        assertEquals(2, parsed.components().size());
        assertEquals(SOME_NAME, parsed.components().get(0).name());
        assertEquals(PREFIXED_NAME, parsed.components().get(1).name());
        assertEquals(SOME_PREFIX, (int) parsed.components().get(1).prefixLength().orElse(0));
    }

    @Test
    public void shouldCorrectlyWriteSimpleNameToString() {
        String name = SOME_NAME;
        URITemplateVariable var = URITemplateVariable.parse(name);
        assertEquals(wrap(name), var.toString());
    }

    @Test
    public void shouldCorrectlyWriteNameWithValidModifierToString() {
        String name = SOME_VALID_MODIFIER + SOME_NAME;
        URITemplateVariable var = URITemplateVariable.parse(name);
        assertEquals(wrap(name), var.toString());
    }

    private String wrap(String name) {
        return "{" + name + "}";
    }

}
