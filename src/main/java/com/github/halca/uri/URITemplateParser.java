package com.github.halca.uri;

import java.util.Map;

import static java.util.Arrays.asList;

public class URITemplateParser {

    public static String parseAndExpand(String value, Map<String, ?> substitutions) {
        return parseAndExpand(value, new ParamHolder.ParamMap(substitutions));
    }

    public static String parseAndExpand(String value, Object... substitutions) {
        return parseAndExpand(value, new ParamHolder.ParamArray(substitutions));
    }

    public static void parse(String value, URITemplateParserListener listener) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        StringBuilder current = new StringBuilder();
        // control flags
        boolean main = true;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '{':
                    if (!main) {
                        listener.onSyntaxError(value, i);
                    }
                    listener.onTextFragment(current.toString());
                    current.setLength(0);
                    main = false;
                    break;
                case '}':
                    if (main) {
                        listener.onSyntaxError(value, i);
                    }
                    listener.onVariable(URITemplateVariable.parse(current.toString()));
                    current.setLength(0);
                    main = true;
                    break;
                default:
                    current.append(c);
            }
        }
        if (!main) {
            listener.onSyntaxError(value, value.length() - 1);
        } else {
            listener.onTextFragment(current.toString());
        }
        listener.onCompleted();
    }

    public static String parseAndExpand(String value, ParamHolder substitutions) {
        if (value == null) {
            throw new NullPointerException("value");
        }
        StringBuilder result = new StringBuilder();
        StringBuilder template = new StringBuilder();
        // control flags
        boolean main = true;
        boolean resolve = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '{':
                    if (!main) {
                        throw new URITemplateSyntaxException(value);
                    }
                    main = false;
                    break;
                case '}':
                    if (main) {
                        throw new URITemplateSyntaxException(value);
                    }
                    resolve = true;
                    break;
            }
            if (main) {
                result.append(c);
            } else {
                template.append(c);
            }
            if (resolve) {
                String tplCode = template.substring(1, template.length() - 1);
                if (tplCode.isEmpty()) {
                    throw new URITemplateSyntaxException(value);
                }
                String resolved = resolveTemplate(value, tplCode, substitutions);
                result.append(resolved);
                template.setLength(0);
                resolve = false;
                main = true;
            }
        }
        if (!main) {
            throw new URITemplateSyntaxException(value);
        }
        return result.toString();
    }

    private static String resolveTemplate(String value, String tpl, ParamHolder substitutions) {
        char c = tpl.charAt(0);
        boolean operator = URITemplateFormat.MODIFIERS.indexOf(c) >= 0;
        char last = tpl.charAt(tpl.length() - 1);
        boolean explode = last == '*';
        String names = tpl.substring(operator ? 1 : 0, tpl.length() - (explode ? 1 : 0));
        if (names.charAt(names.length() - 1) == ',') {
            throw new URITemplateSyntaxException(value);
        }
        String[] vars = names.split(",");
        for (int i = 0; i < vars.length; i++) {
            if (vars[i].isEmpty()) throw new URITemplateSyntaxException(value);
        }
        return URITemplateFormat.format(operator ? c : null, explode)
                .render(asList(vars), substitutions);
    }

}
