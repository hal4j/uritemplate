package com.github.halca.uri;

import java.util.Objects;
import java.util.Optional;

import static java.lang.String.format;

public class URIVarComponent {

    public static final char PREFIX_SEPARATOR = ':';
    public static final char EXPLODE_MODIFIER = '*';

    private final String name;

    private final Integer prefixLength;

    private final boolean explode;

    public static URIVarComponent parse(String spec) {
        boolean explode = spec.charAt(spec.length() - 1) == EXPLODE_MODIFIER;
        int idx = spec.indexOf(':');
        if (idx == 0) {
            throw new URITemplateSyntaxException(format("Name not specified in varspec:\"%s\"", spec));
        } else if (idx == spec.length() - 1) {
            throw new URITemplateSyntaxException(format("Prefix length not specified in varspec:\"%s\"", spec));
        } else if (idx > 0 && explode) {
            throw new URITemplateSyntaxException(format("Explode flag and prefix canot be specified in same varspec:\"%s\"", spec));
        }
        if (explode) {
            return new URIVarComponent(spec.substring(0, spec.length() - 1), null, true);
        } else if (idx > 0) {
            try {
                int length = Integer.parseInt(spec.substring(idx + 1));
                return new URIVarComponent(spec.substring(0, idx), length, false);
            } catch (NumberFormatException e) {
                throw new URITemplateSyntaxException(format("Cannot parse prefix length: %s", spec));
            }
        }
        return new URIVarComponent(spec, null, false);
    }

    public static URIVarComponent var(String name) {
        return new URIVarComponent(name, null, false);
    }

    public static URIVarComponent exploded(String name) {
        return new URIVarComponent(name, null, true);
    }

    public static URIVarComponent prefix(String name, int length) {
        return new URIVarComponent(name, length, false);
    }

    private URIVarComponent(String name, Integer prefixLength, boolean explode) {
        if (name == null) {
            throw new NullPointerException("name");
        }
        // TODO: add name validation
        this.name = name;
        if (prefixLength != null && (prefixLength <= 0 || prefixLength > 10000)) {
            throw new URITemplateSyntaxException(format("Prefix length (%d) must be an integer number between 1 and 10000 inclusive", prefixLength));
        }
        this.prefixLength = prefixLength;
        this.explode = explode;
    }

    public final String name() {
        return this.name;
    }

    public final Optional<Integer> prefixLength() {
        return Optional.ofNullable(this.prefixLength);
    }

    public final boolean explode() {
        return explode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URIVarComponent that = (URIVarComponent) o;
        return Objects.equals(name, that.name) &&
                Objects.equals(prefixLength, that.prefixLength)
                && (explode == that.explode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, prefixLength);
    }

    @Override
    public String toString() {
        if (prefixLength != null) {
            return name + PREFIX_SEPARATOR + prefixLength;
        } else if (explode) {
            return name + EXPLODE_MODIFIER;
        } else {
            return name;
        }
    }


}
