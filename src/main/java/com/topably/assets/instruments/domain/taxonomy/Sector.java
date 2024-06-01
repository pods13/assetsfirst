package com.topably.assets.instruments.domain.taxonomy;

import java.util.EnumSet;

import lombok.Getter;


public enum Sector {

    ENERGY("Energy", "Энергетика");

    private final String name;
    @Getter
    private final String ruLocale;

    Sector(String name, String ruLocale) {
        this.name = name;
        this.ruLocale = ruLocale;
    }

    public static Sector findByName(String name) {
        return EnumSet.allOf(Sector.class).stream()
            .filter(s -> s.name.equals(name))
            .findFirst()
            .orElseThrow(() -> new IllegalStateException(String.format("Unsupported Type %s", name)));
    }

}
