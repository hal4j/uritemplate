package com.github.hal4j.uritemplate.test;

import com.github.hal4j.uritemplate.URITemplateFormat;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class URITemplateFormatTest {

    @Test
    void shouldCorrectlyRenderQueryStart() {
        String result = URITemplateFormat.format(URITemplateFormat.QUERY_START, false)
                .renderName("name");
        assertEquals("{?name}", result);
    }

    @Test
    void shouldCorrectlyRenderQueryStartMultiple() {
        String result = URITemplateFormat.format(URITemplateFormat.QUERY_START, false)
                .renderName("name,var");
        assertEquals("{?name,var}", result);
    }

    @Test
    void shouldCorrectlyRenderQueryParam() {
        String result = URITemplateFormat.format(URITemplateFormat.QUERY_SEPARATOR, false)
                .renderName("name");
        assertEquals("{&name}", result);
    }

    @Test
    void shouldCorrectlyRenderEmptyModifier() {
        String result = URITemplateFormat.format(null, false)
                .renderName("name");
        assertEquals("{name}", result);
    }

}
