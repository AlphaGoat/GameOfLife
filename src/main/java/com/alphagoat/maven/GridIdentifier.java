/* Lifted pretty much wholesale from Bo Anderson's 
 * article "Enum to Integer and Integer to Enum in Java"
 * https://codingexplained.com/coding/java/enum-to-integer-and-integer-to-enum */

package com.alphagoat.maven;

import java.util.Map;
import java.util.HashMap;

public enum GridIdentifier {
    EMPTY_SPACE(0),
    COLONY(1);

    private int value;
    private static Map map = new HashMap<>();

    private GridIdentifier(int value) {
        this.value = value;
    }

    static {
        for (GridIdentifier gridIdentifier : GridIdentifier.values()) {
            map.put(gridIdentifier.value, gridIdentifier);
        }
    }

    public static GridIdentifier valueOf(int gridIdentifier) {
        return (GridIdentifier) map.get(gridIdentifier);
    }

    public int getValue() {
        return value;
    }

}
