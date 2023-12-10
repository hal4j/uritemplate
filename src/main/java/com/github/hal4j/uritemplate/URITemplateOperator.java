package com.github.hal4j.uritemplate;

import java.util.Optional;

import static com.github.hal4j.uritemplate.ExpansionBehavior.expand;
import static com.github.hal4j.uritemplate.ExpansionBehavior.expandAs;
import static java.util.Optional.empty;
import static java.util.Optional.of;

public enum URITemplateOperator {

    NONE(expand()),
    PRE_ENCODED('+', expand().separator(',').allowReserved()),
    DOMAIN('.', true, expandAs('.')),
    PATH('/', true, expandAs('/')),
    MATRIX(';', true, expandAs(';').named()),
    QUERY_START('?', expandAs('?').separator('&').named().ifEmptyExplodeWith('=')),
    QUERY('&', expandAs('&').named().ifEmptyExplodeWith('=')),
    FRAGMENT('#', expandAs('#').separator(',').allowReserved()),

    /**
     * Non-standard extension to be used in opaque URIs like URNs
     */
    NAMESPACE(':', expandAs(':').allowReserved());

    private final Character operatorChar;
    private final ExpansionBehavior behavior;
    private final boolean hierarchical;

    URITemplateOperator(ExpansionBehavior behavior) {
        this.operatorChar = null;
        this.hierarchical = false;
        this.behavior = behavior;
    }

    URITemplateOperator(char c, ExpansionBehavior behavior) {
        this.operatorChar = c;
        this.hierarchical = false;
        this.behavior = behavior;
    }

    URITemplateOperator(char c, boolean hierarchical, ExpansionBehavior behavior) {
        this.operatorChar = c;
        this.hierarchical = hierarchical;
        this.behavior = behavior;
    }

    public Character operatorChar() {
        return operatorChar;
    }

    public ExpansionBehavior behavior() {
        return this.behavior;
    }

    public static Optional<URITemplateOperator> valueOf(char c) {
        for (URITemplateOperator modifier : values()) {
            if (modifier.operatorChar != null && modifier.operatorChar == c) {
                return of(modifier);
            }
        }
        return empty();
    }

    public boolean isHierarchical() {
        return hierarchical;
    }

}
