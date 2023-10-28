package com.topably.assets.core.exception;

import org.apache.commons.text.StringSubstitutor;

import java.util.Collections;
import java.util.Map;

public class ApplicationException extends RuntimeException {

    private final String messageTemplate;
    private final Map<String, Object> messageArguments;

    public ApplicationException(String messageTemplate, Map<String, Object> messageArguments) {
        this.messageTemplate = messageTemplate;
        this.messageArguments = messageArguments;
    }

    public ApplicationException(String messageTemplate, Map<String, Object> messageArguments, Throwable cause) {
        super(cause);
        this.messageTemplate = messageTemplate;
        this.messageArguments = messageArguments;
    }

    public ApplicationException(String messageTemplate) {
        this.messageTemplate = messageTemplate;
        this.messageArguments = Collections.emptyMap();
    }

    public ApplicationException(String messageTemplate, Throwable cause) {
        super(cause);
        this.messageTemplate = messageTemplate;
        this.messageArguments = Collections.emptyMap();
    }

    @Override
    public String getMessage() {
        return messageArguments.isEmpty() ? messageTemplate :
            StringSubstitutor.replace(messageTemplate, messageArguments, "{", "}");
    }

}
