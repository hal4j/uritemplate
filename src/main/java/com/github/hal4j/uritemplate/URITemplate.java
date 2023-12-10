package com.github.hal4j.uritemplate;

import java.net.URI;
import java.util.*;

import static com.github.hal4j.uritemplate.ParamHolder.discardMissing;
import static com.github.hal4j.uritemplate.ParamHolder.map;
import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;

/**
 * Immutable representation of a string as an URI template.
 */
public class URITemplate {

    private final String value;

    /**
     * Creates new URI template from the given string. Validation of
     * the template syntax does not happen at this stage and is deferred
     * to the expansion ({@link #expand(ParamHolder)} and termination methods ({@link #toURI()}) .
     * @param string the string supposedly containing an URI template
     */
    public URITemplate(String string) {
        if (string == null) throw new NullPointerException("URI template string cannot be null");
        this.value = string;
    }

    /**
     * Fully expand this template using custom parameter holder
     * @param params the custom parameter holder
     * @return new URI template containing the result of the expansion
     */
    public URITemplate expand(ParamHolder params) {
        return expand(params, false);
    }

    /**
     * Fully or partially expand this template using custom parameter holder
     * @param params the custom parameter holder
     * @param partial indicates whether partial or full expansion must be performed
     * @return new URI template containing the result of the expansion
     */
    public URITemplate expand(ParamHolder params, boolean partial) {
        String expanded = URITemplateParser.parseAndExpand(value, partial, params);
        return new URITemplate(expanded);
    }

    /**
     * Fully expand this template using given map of named substitutions.
     * All missing parameters are removed from template (equivalent of empty list).
     * @param substitutions the map of substituted values
     * @return new URI template containing the result of the expansion
     */
    public URITemplate expand(Map<String, ?> substitutions) {
        return expand(discardMissing(map(substitutions)), false);
    }

    /**
     * Fully expand this template using given substitution values.
     * @see com.github.hal4j.uritemplate.ParamHolder.ParamArray for details.
     * @param substitutions the substituted values
     * @return new URI template containing the result of the expansion
     */
    public URITemplate expand(Object... substitutions) {
        String expanded = URITemplateParser.parseAndExpand(value, false, substitutions);
        return new URITemplate(expanded);
    }

    /**
     * Substitute the value with given name in this template, keeping remaining variables in the resulting template
     * @param name the name of the template parameter
     * @param value the value to substitute
     * @return new URI template containing the result of the expansion
     */
    public URITemplate expandPartial(String name, Object value) {
        return expandPartial(Collections.singletonMap(name, value));
    }

    /**
     * Expand this template using given map of named substitutions and keep variables with missing values in the resulting template.
     * @param substitutions the map of substituted values
     * @return new URI template containing the result of the expansion
     */
    public URITemplate expandPartial(Map<String, ?> substitutions) {
        String expanded = URITemplateParser.parseAndExpand(value, true, substitutions);
        return new URITemplate(expanded);
    }

    /**
     * Expand this template using given substitution values and keep variables with missing values in the resulting template.
     * @see com.github.hal4j.uritemplate.ParamHolder.ParamArray for details.
     * @param substitutions the substituted values
     * @return new URI template containing the result of the expansion
     */
    public URITemplate expandPartial(Object... substitutions) {
        String expanded = URITemplateParser.parseAndExpand(value, true, substitutions);
        return new URITemplate(expanded);
    }

    /**
     * Removes variables with given names from this template
     * @param names the names of the variables
     * @return new URI template without variables with given names
     */
    public URITemplate discard(String... names) {
        return this.discard(asList(names));
    }

    /**
     * Removes variables with given names from this template
     * @param names the names of the variables
     * @return new URI template without variables with given names
     */
    public URITemplate discard(Iterable<String> names) {
        Map<String, Object> map = new HashMap<>();
        names.forEach(name -> map.put(name, URITemplateParser.DISCARDED));
        String expanded = URITemplateParser.parseAndExpand(value, true, map);
        return new URITemplate(expanded);
    }

    /**
     * Checks if this template is fully expanded, i.e. contains no parameters.
     * @return <code>true</code> if template is fully expanded, <code>false</code> otherwise
     */
    public boolean isExpanded() {
        return value.indexOf('{') < 0;
    }

    /**
     * Parses the template and returns list of found template variables in the order of their occurence.
     * @return list of parameters in this template or empty list if template is fully expanded.
     */
    public List<URITemplateVariable> variables() {
        List<URITemplateVariable> vars = new ArrayList<>();
        URITemplateParser.parse(value, new URITemplateParserListener.Adapter() {
            @Override
            public void onVariable(URITemplateVariable var) {
                vars.add(var);
            }
        });
        return vars;
    }

    /**
     * If this template is fully expanded, converts it to URI.
     * @return this template as new URI object
     * @throws IllegalStateException if this template is not fully expanded
     * @throws IllegalArgumentException if this template is not a valid URI
     */
    public URI toURI() {
        if (!isExpanded()) {
            throw new IllegalStateException("Template not expanded: " + value);
        }
        return URI.create(value);
    }

    /**
     * If this template is fully expanded, converts it to URIBuilder (convenience method).
     * @return new URIBuilder(toURI())
     */
    public URIBuilder toBuilder() {
        return new URIBuilder(toURI());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        URITemplate that = (URITemplate) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    /**
     * Returns the original value of this template
     * @return this template as a string
     */
    public String toString() {
        return this.value;
    }

}
