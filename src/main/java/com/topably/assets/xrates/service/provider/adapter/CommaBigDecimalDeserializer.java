package com.topably.assets.xrates.service.provider.adapter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Optional;

public class CommaBigDecimalDeserializer extends NumberDeserializers.BigDecimalDeserializer {

    @Override
    public BigDecimal deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        var stringValue = p.getText();
        return Optional.ofNullable(stringValue)
            .map(value -> new BigDecimal(value.replace(',', '.')))
            .orElse(null);
    }
}
