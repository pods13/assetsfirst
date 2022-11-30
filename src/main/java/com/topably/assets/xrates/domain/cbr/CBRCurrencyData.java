package com.topably.assets.xrates.domain.cbr;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.topably.assets.xrates.service.provider.adapter.CommaBigDecimalDeserializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CBRCurrencyData {

    @JacksonXmlProperty(localName = "ID")
    private String id;

    @JacksonXmlProperty(localName = "NumCode")
    private Integer numCode;

    @JacksonXmlProperty(localName = "CharCode")
    private String charCode;

    @JacksonXmlProperty(localName = "Nominal")
    private Integer nominal;

    @JacksonXmlProperty(localName = "Name")
    private String name;

    @JacksonXmlProperty(localName = "Value")
    @JsonDeserialize(using = CommaBigDecimalDeserializer.class)
    private BigDecimal value;

}
