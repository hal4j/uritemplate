package com.github.hal4j.uritemplate;

import java.util.Map;

public class URITemplateParser {

    static final Object DISCARDED = new Object();

    public static String parseAndExpand(String value,
                                        boolean partial,
                                        Map<String, ?> substitutions) {
        return parseAndExpand(value, partial, new ParamHolder.ParamMap(substitutions));
    }

    public static String parseAndExpand(String value,
                                        boolean partial,
                                        Object... substitutions) {
        return parseAndExpand(value, partial, new ParamHolder.ParamArray(substitutions));
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

    public static String parseAndExpand(String value,
                                        boolean partial,
                                        ParamHolder substitutions) {
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
                resolveTemplate(value, partial, tplCode, substitutions, result);
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

    private static void resolveTemplate(String value, boolean partial, String tpl, ParamHolder substitutions, StringBuilder result) {
        URITemplateVariable template = URITemplateVariable.parse(tpl);
        template.expandTo(substitutions, partial, result);
    }

}
