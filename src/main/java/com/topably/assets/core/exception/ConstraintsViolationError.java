package com.topably.assets.core.exception;

public record ConstraintsViolationError(String property, String message) {
}
