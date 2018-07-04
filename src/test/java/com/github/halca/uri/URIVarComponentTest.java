package com.github.halca.uri;

import org.junit.jupiter.api.Test;

import static com.github.halca.uri.URIVarComponent.PREFIX_SEPARATOR;
import static org.junit.jupiter.api.Assertions.*;

public class URIVarComponentTest {

    public static final String SOME_NAME = "test";
    public static final int SOME_LENGTH = 3;

    @Test
    public void shouldCorrectlyParseSimpleComponent() {
        URIVarComponent component = URIVarComponent.parse(SOME_NAME);
        assertNotNull(component);
        assertEquals(SOME_NAME, component.name());
        assertFalse(component.explode());
        assertFalse(component.prefixLength().isPresent());
    }

    @Test
    public void shouldCorrectlyParseComponentWithLengthPrefix() {
        URIVarComponent component = URIVarComponent.parse(SOME_NAME + PREFIX_SEPARATOR + SOME_LENGTH);
        assertNotNull(component);
        assertEquals(SOME_NAME, component.name());
        assertFalse(component.explode());
        assertTrue(component.prefixLength().isPresent());
        assertEquals(SOME_LENGTH, (int) component.prefixLength().get());
    }

    @Test
    public void shouldCorrectlyParseExplodeComponent() {
        URIVarComponent component = URIVarComponent.parse(SOME_NAME + URIVarComponent.EXPLODE_MODIFIER);
        assertNotNull(component);
        assertEquals(SOME_NAME, component.name());
        assertTrue(component.explode());
        assertFalse(component.prefixLength().isPresent());
    }

}
