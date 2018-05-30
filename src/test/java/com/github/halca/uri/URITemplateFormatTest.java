package com.github.halca.uri;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class URITemplateFormatTest {

    @Test
    public void shouldCorrectlyRenderQueryStart() {
        String result = URITemplateFormat.format(URITemplateFormat.QUERY_START, false)
                .renderName("name");
        assertEquals("{?name}", result);
    }

    @Test
    public void shouldCorrectlyRenderQueryStartMultiple() {
        String result = URITemplateFormat.format(URITemplateFormat.QUERY_START, false)
                .renderName("name,var");
        assertEquals("{?name,var}", result);
    }

    @Test
    public void shouldCorrectlyRenderQueryParam() {
        String result = URITemplateFormat.format(URITemplateFormat.QUERY_SEPARATOR, false)
                .renderName("name");
        assertEquals("{&name}", result);
    }

    @Test
    public void shouldCorrectlyRenderEmptyModifier() {
        String result = URITemplateFormat.format(null, false)
                .renderName("name");
        assertEquals("{name}", result);
    }

}
