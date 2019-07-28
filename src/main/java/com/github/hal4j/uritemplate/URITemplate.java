package com.github.hal4j.uritemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        this.value = string;
    }

    /**
     * Substitute the value with given name in this template
     * @param name the name of the template parameter
     * @param value the value to substitute
     * @return new URI template containing the result of the expansion
     */
    public URITemplate expand(String name, Object value) {
        return expand(Collections.singletonMap(name, value));
    }

    /**
     * Expand this template using given map of named substitutions
     * @param substitutions the map of substituted values
     * @return new URI template containing the result of the expansion
     */
    public URITemplate expand(Map<String, ?> substitutions) {
        String expanded = URITemplateParser.parseAndExpand(value, substitutions);
        return new URITemplate(expanded);
    }

    /**
     * Expand this template using given substitution values.
     * @see com.github.hal4j.uritemplate.ParamHolder.ParamArray for details.
     * @param substitutions the substituted values
     * @return new URI template containing the result of the expansion
     */
    public URITemplate expand(Object... substitutions) {
        String expanded = URITemplateParser.parseAndExpand(value, substitutions);
        return new URITemplate(expanded);
    }

    /**
     * Expand this template using custom parameter holder
     * @param params the custom parameter holder
     * @return new URI template containing the result of the expansion
     */
    public URITemplate expand(ParamHolder params) {
        String expanded = URITemplateParser.parseAndExpand(value, params);
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
     * Returns the original value of this template
     * @return this template as a string
     */
    public String toString() {
        return this.value;
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

}
