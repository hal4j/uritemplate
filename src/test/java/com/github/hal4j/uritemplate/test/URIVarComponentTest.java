package com.github.hal4j.uritemplate.test;

import com.github.hal4j.uritemplate.URIVarComponent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class URIVarComponentTest {

    private static final String SOME_NAME = "test";
    private static final int SOME_LENGTH = 3;

    @Test
    void shouldCorrectlyParseSimpleComponent() {
        URIVarComponent component = URIVarComponent.parse(SOME_NAME);
        assertNotNull(component);
        assertEquals(SOME_NAME, component.name());
        assertFalse(component.explode());
        assertFalse(component.prefixLength().isPresent());
    }

    @Test
    void shouldCorrectlyParseComponentWithLengthPrefix() {
        URIVarComponent component = URIVarComponent.parse(SOME_NAME + URIVarComponent.PREFIX_SEPARATOR + SOME_LENGTH);
        assertNotNull(component);
        assertEquals(SOME_NAME, component.name());
        assertFalse(component.explode());
        assertTrue(component.prefixLength().isPresent());
        assertEquals(SOME_LENGTH, (int) component.prefixLength().get());
    }

    @Test
    void shouldCorrectlyParseExplodeComponent() {
        URIVarComponent component = URIVarComponent.parse(SOME_NAME + URIVarComponent.EXPLODE_MODIFIER);
        assertNotNull(component);
        assertEquals(SOME_NAME, component.name());
        assertTrue(component.explode());
        assertFalse(component.prefixLength().isPresent());
    }

}
