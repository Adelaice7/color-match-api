package com.rmeunier.colormatchapi.model;

public enum Schema {
    HTTP ("http:"),
    HTTPS ("https:"),
    FILE ("file:");

    public final String label;

    private Schema(String label) {
        this.label = label;
    }
}
