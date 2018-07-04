package com.github.halca.uri;

public interface URITemplateParserListener {

    void onTextFragment(String text);

    void onVariable(URITemplateVariable var);

    void onCompleted();

    void onSyntaxError(String value, int position);

    class Adapter implements URITemplateParserListener {
        @Override
        public void onTextFragment(String text) {
        }
        @Override
        public void onVariable(URITemplateVariable var) {

        }
        @Override
        public void onCompleted() {
        }

        @Override
        public void onSyntaxError(String value, int position) {
            throw new URITemplateSyntaxException(value + " (" + position + ")");
        }
    }

}
