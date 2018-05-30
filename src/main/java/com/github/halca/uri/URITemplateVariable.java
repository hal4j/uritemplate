package com.github.halca.uri;

import java.util.Optional;

import static com.github.halca.uri.URITemplateFormat.*;

public class URITemplateVariable {

    private final Character modifier;

    private final String name;

    private final Integer prefixLength;

    private final boolean explode;

    URITemplateVariable(Character modifier, String name, Integer prefixLength, boolean explode) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        if (modifier != null && MODIFIERS.indexOf(modifier) < 0) {
            throw new IllegalArgumentException("Unsupported modifier: '" + modifier + "'");
        }
        if (prefixLength != null && prefixLength < 1) {
            throw new IllegalArgumentException("prefix length must be a positive number");
        }
        this.modifier = modifier;
        this.name = name;
        this.prefixLength = prefixLength;
        this.explode = explode;
    }

    public URITemplateVariable exploded() {
        return new URITemplateVariable(modifier, name, prefixLength, true);
    }

    public URITemplateVariable prefix(int prefix) {
        return new URITemplateVariable(modifier, name, prefix, explode);
    }

    public URITemplateVariable preEncoded() {
        if (modifier != null) {
            throw new IllegalStateException("Pre-encoded modifier cannot be combined with '" + modifier + "'");
        }
        return new URITemplateVariable(PRE_ENCODED, name, prefixLength, explode);
    }

    public Character modifier() {
        return this.modifier;
    }

    public boolean explode() {
        return this.explode;
    }

    public Optional<Integer> prefix() {
        return Optional.ofNullable(prefixLength);
    }

    @Override
    public String toString() {
        return URITemplateFormat
                .format(modifier, explode)
                .renderName((name + (prefixLength != null ? PREFIX_SEPARATOR + prefixLength : "")));
    }

}
