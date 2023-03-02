package me.gatogamer.nekotops.hologram;

import lombok.Getter;

/**
 * This code has been created by
 * gatogamer#6666 A.K.A. gatogamer.
 * If you want to use my code, please
 * ask first, and give me the credits.
 * Arigato! n.n
 */
@Getter
public enum TopType {
    DAY("day"),
    WEEK("week"),
    MONTH("month"),
    ALL_TIME("allTime");

    private final String endpointName;

    TopType(String endpointName) {
        this.endpointName = endpointName;
    }

    public TopType next() {
        try {
            return values()[ordinal() + 1];
        } catch (Exception e) {
            return values()[0];
        }
    }

    public TopType previous() {
        try {
            return values()[ordinal() - 1];
        } catch (Exception e) {
            return values()[values().length - 1];
        }
    }
}