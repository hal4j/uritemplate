package com.github.hal4j.uritemplate.test;

import com.github.hal4j.uritemplate.ParamHolder;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ParamHolderTest {

    @Test
    void prefixedHolderShouldConfirmPresenceOfMatchingParameter() {
        Map<String, String> params = Collections.singletonMap("k", "1");
        ParamHolder holder = ParamHolder.prefixed("p", params);
        assertTrue(holder.containsKey("p.k"));
    }

    @Test
    void prefixedHolderShouldDenyPresenceOfNonMatchingParameter() {
        Map<String, String> params = Collections.singletonMap("k", "1");
        ParamHolder holder = ParamHolder.prefixed("p", params);
        assertFalse(holder.containsKey("p.x"));
    }

    @Test
    void prefixedHolderShouldReturnValueOfMatchingParameter() {
        Map<String, String> params = Collections.singletonMap("k", "1");
        ParamHolder holder = ParamHolder.prefixed("p", params);
        assertEquals("1", holder.get("p.k"));
    }

    @Test
    void prefixedHolderShouldReturnNullForNonMatchingParameter() {
        Map<String, String> params = Collections.singletonMap("k", "1");
        ParamHolder holder = ParamHolder.prefixed("p", params);
        assertNull(holder.get("p.x"));
    }

}
