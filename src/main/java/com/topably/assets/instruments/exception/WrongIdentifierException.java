package com.topably.assets.instruments.exception;

public class WrongIdentifierException extends RuntimeException {

    public WrongIdentifierException(String identifier) {
        super("Wrong identifier %s was provided".formatted(identifier));
    }
}
