package com.rmeunier.colormatchapi.exception;

public class ColorMissingException extends RuntimeException {
    public ColorMissingException() {
        super("Could not get dominant color for product!");
    }
}
