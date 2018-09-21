package com.github.hal4j.uritemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.github.hal4j.uritemplate.URITemplateModifier.*;
import static com.github.hal4j.uritemplate.URIVarComponent.var;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;

public class URITemplateVariable {

    private final URITemplateModifier modifier;

    private final List<URIVarComponent> components;

    public static URITemplateVariable parse(String template) {
        if (template.length() < 3) {
            throw new URITemplateSyntaxException("URI template cannot be empty: {}");
        }
        Optional<URITemplateModifier> modifier = URITemplateModifier.valueOf(template.charAt(0));
        String specs = template;
        if (modifier.isPresent()) {
            specs = template.substring(1);
            if (specs.length() == 0) {
                throw new URITemplateSyntaxException("Name not specified: {" + modifier.get().modifierChar() + "}");
            }
        }
        List<URIVarComponent> components = stream(specs.split(","))
                .map(URIVarComponent::parse)
                .collect(toList());
        return new URITemplateVariable(modifier.orElse(null), components);
    }

    public static URITemplateVariable template(String name) {
        return template(var(name));
    }

    public static URITemplateVariable template(URIVarComponent... components) {
        return new URITemplateVariable(null, asList(components));
    }

    public static URITemplateVariable template(URITemplateModifier modifier,
                                               URIVarComponent... components) {
        return new URITemplateVariable(modifier, asList(components));
    }

    public static URITemplateVariable fragment(String name) {
        return template(URITemplateModifier.FRAGMENT, var(name));
    }

    public static URITemplateVariable queryStart(String name) {
        return template(QUERY_START, var(name));
    }

    public static URITemplateVariable queryParam(String name) {
        return template(QUERY, var(name));
    }

    public static URITemplateVariable pathVariable(String name) {
        return template(PATH, var(name));
    }

    public static URITemplateVariable preEncoded(String name) {
        return template(PRE_ENCODED, var(name));
    }

    private URITemplateVariable(URITemplateModifier modifier, List<URIVarComponent> components) {
        this.modifier = modifier;
        if (components == null || components.isEmpty()) {
            throw new URITemplateSyntaxException("At least one component required for template variable");
        }
        this.components = Collections.unmodifiableList(components);
    }

    public Optional<URITemplateModifier> modifier() {
        return Optional.ofNullable(this.modifier);
    }

    public List<URIVarComponent> components() {
        return this.components;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URITemplateVariable variable = (URITemplateVariable) o;
        if (modifier != variable.modifier) return false;
        if (components.size() != variable.components.size()) return false;
        for (int i = 0; i < components.size(); i++) {
            if (!components.get(i).equals(variable.components.get(i))) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(modifier, components);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("{");
        if (modifier != null) {
            builder.append(modifier.modifierChar());
        }
        boolean first = true;
        for (URIVarComponent component : components) {
            if (!first) {
                builder.append(',');
            } else {
                first = false;
            }
            builder.append(component);
        }
        return builder.append('}').toString();
    }

}
