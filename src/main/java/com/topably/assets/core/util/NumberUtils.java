package com.topably.assets.core.util;

import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.math.RoundingMode;

@UtilityClass
public class NumberUtils {

    public static final BigDecimal ONE_HUNDRED = new BigDecimal(100);

    public BigDecimal calculatePercentage(BigDecimal total, BigDecimal obtained) {
        if (total.compareTo(BigDecimal.ZERO) > 0) {
            return obtained.multiply(ONE_HUNDRED).divide(total, 2, RoundingMode.HALF_EVEN);
        }
        return BigDecimal.ZERO;
    }
}
