package org.jfree.chart.ui;

/**
 * Used to indicate either the foreground or background layer.
*/
public enum Layer {
    /** Foreground. */
    FOREGROUND("Layer.FOREGROUND"),

    /** Background. */
    BACKGROUND("Layer.BACKGROUND");

    /** The name. */
    private String name;

    /**
     * Private constructor.
     *
     * @param name  the name.
     */
    private Layer(final String name) {
        this.name = name;
    }

    /**
     * Returns a string representing the object.
     *
     * @return The string.
     */
    @Override
    public String toString() {
        return name;
    }
}