/* Lifted pretty much wholesale from Bo Anderson's 
 * article "Enum to Integer and Integer to Enum in Java"
 * https://codingexplained.com/coding/java/enum-to-integer-and-integer-to-enum */
/* May 25, 2020: extended to include map for color entry as well as value 
 * - Peter Thomas */

package com.alphagoat.maven;

import java.awt.Color;

import java.util.Map;
import java.util.HashMap;

public enum GridIdentifier {
    EMPTY_SPACE(0, Color.WHITE),
    COLONY(1, Color.BLUE);

    private int value;
    private Color idColor;
    private static Map valueMap = new HashMap<>();
    private static Map colorMap = new HashMap<>();

    private GridIdentifier(int value, Color idColor) {
        this.value = value;
        this.idColor = idColor;
    }

    static {
        for (GridIdentifier gridIdentifier : GridIdentifier.values()) {
            valueMap.put(gridIdentifier.value, gridIdentifier);
            colorMap.put(gridIdentifier.idColor, gridIdentifier);
        }
    }

    public static GridIdentifier valueOf(int gridIdentifier) {
        return (GridIdentifier) valueMap.get(gridIdentifier);
    }

    public static GridIdentifier colorOf(int gridIdentifier) {
        return (GridIdentifier) colorMap.get(gridIdentifier);
    }

    public int getValue() {
        return value;
    }

    public Color getColor() {
        return idColor;
    }

}
