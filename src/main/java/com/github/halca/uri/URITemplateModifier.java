package com.github.halca.uri;

import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.of;

public enum URITemplateModifier {

    PATH('/'),
    QUERY_START('?'),
    QUERY('&'),
    FRAGMENT('#'),
    DOMAIN('.'),
    PRE_ENCODED('+'),
    MATRIX(';');

    private final char modifierChar;

    URITemplateModifier(char c) {
        this.modifierChar = c;
    }

    char modifierChar() {
        return modifierChar;
    }

    public static Optional<URITemplateModifier> valueOf(char c) {
        for (URITemplateModifier modifier : values()) {
            if (modifier.modifierChar == c) {
                return of(modifier);
            }
        }
        return empty();
    }

}
