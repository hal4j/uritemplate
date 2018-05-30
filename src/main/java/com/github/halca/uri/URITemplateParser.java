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

    public static String parseAndExpand(String value, ParamHolder substitutions) {
        StringBuilder result = new StringBuilder();
        StringBuilder template = new StringBuilder();
        // control flags
        boolean main = true;
        boolean resolve = false;
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            switch (c) {
                case '{':
                    main = false;
                    break;
                case '}':
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
                String resolved = resolveTemplate(tplCode, substitutions);
                result.append(resolved);
                template.setLength(0);
                resolve = false;
                main = true;
            }
        }
        return result.toString();
    }

    private static String resolveTemplate(String tpl, ParamHolder substitutions) {
        char c = tpl.charAt(0);
        boolean operator = URITemplateFormat.MODIFIERS.indexOf(c) >= 0;
        char last = tpl.charAt(tpl.length() - 1);
        boolean explode = last == '*';
        String names = tpl.substring(operator ? 1 : 0, tpl.length() - (explode ? 1 : 0));
        String[] vars = names.split(",");
        return URITemplateFormat.format(operator ? c : null, explode)
                .render(asList(vars), substitutions);
    }

}
