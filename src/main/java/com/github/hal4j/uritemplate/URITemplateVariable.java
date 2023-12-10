package com.github.hal4j.uritemplate;

import java.util.*;

import static com.github.hal4j.uritemplate.URITemplateOperator.*;
import static com.github.hal4j.uritemplate.URIVarComponent.var;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

public class URITemplateVariable {

    private final URITemplateOperator modifier;

    private final List<URIVarComponent> components;

    public static URITemplateVariable parse(String template) {
        if ((template.charAt(0) == '{') && (template.charAt(template.length() - 1) == '}')) {
            template = template.substring(1, template.length() - 1);
        }
        if (template.isEmpty()) {
            throw new URITemplateSyntaxException("URI template cannot be empty: {}");
        }
        Optional<URITemplateOperator> modifier = URITemplateOperator.valueOf(template.charAt(0));
        String specs = template;
        if (modifier.isPresent()) {
            specs = template.substring(1);
            if (specs.isEmpty()) {
                throw new URITemplateSyntaxException("Name not specified: {" + modifier.get().operatorChar() + "}");
            }
        }
        List<URIVarComponent> components = stream(specs.split(","))
                .map(URIVarComponent::parse)
                .collect(toList());
        return new URITemplateVariable(modifier.orElse(NONE), components);
    }

    public static URITemplateVariable template(String name) {
        return template(var(name));
    }

    public static URITemplateVariable template(URIVarComponent... components) {
        return new URITemplateVariable(NONE, asList(components));
    }

    public static URITemplateVariable template(URITemplateOperator modifier,
                                               URIVarComponent... components) {
        return new URITemplateVariable(modifier, asList(components));
    }

    public static URITemplateVariable fragment(String name) {
        return template(URITemplateOperator.FRAGMENT, var(name));
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

    private URITemplateVariable(URITemplateOperator modifier, List<URIVarComponent> components) {
        this.modifier = modifier;
        if (components == null || components.isEmpty()) {
            throw new URITemplateSyntaxException("At least one component required for template variable");
        }
        this.components = Collections.unmodifiableList(components);
    }

    public Optional<URITemplateOperator> modifier() {
        return Optional.ofNullable(this.modifier).filter(m -> m != NONE);
    }

    public List<URIVarComponent> components() {
        return this.components;
    }

    public void expandTo(ParamHolder substitutions, boolean partial, StringBuilder result) {
        if (partial) {
            expandPartialTo(substitutions, result);
        } else {
            boolean isFirst = true;
            for (URIVarComponent component : this.components) {
                boolean expanded = component.expandTo(substitutions, result, isFirst, modifier);
                isFirst = isFirst && !expanded;
            }
        }
    }

    private void expandPartialTo(ParamHolder substitutions, StringBuilder result) {
        int pos;
        boolean isFirst = true;
        URITemplateOperator modifier = this.modifier;
        boolean flush = modifier.isHierarchical();
        List<URIVarComponent> unresolved = new ArrayList<>();
        for (URIVarComponent component : this.components) {
            if (substitutions.ignore(component.name())) {
                continue;
            }
            pos = result.length();
            boolean expanded = component.expandTo(substitutions, result, isFirst, modifier);
            isFirst = isFirst && !(expanded || flush);
            if (expanded) {
                if (flush && !unresolved.isEmpty()) {
                    result.insert(pos, new URITemplateVariable(modifier, unresolved));
                    unresolved.clear();
                }
                if (modifier == QUERY_START) modifier = QUERY;
            } else {
                unresolved.add(component);
            }
        }
        if (!unresolved.isEmpty()) {
            result.append(new URITemplateVariable(modifier, unresolved));
        }
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
        if (modifier != null && modifier.operatorChar() != null) {
            builder.append(modifier.operatorChar());
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
