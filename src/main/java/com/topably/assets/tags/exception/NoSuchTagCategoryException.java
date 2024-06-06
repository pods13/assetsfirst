package com.topably.assets.tags.exception;

import java.util.Map;

import com.topably.assets.core.exception.ApplicationException;


public class NoSuchTagCategoryException extends ApplicationException {

    public NoSuchTagCategoryException(Long userId, String code) {
        super("no_such_tag_category", Map.of("userId", userId, "code", code));

    }

}
