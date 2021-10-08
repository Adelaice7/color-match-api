package com.rmeunier.colormatchapi.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String url) {
        super("Image resource could not be found on url: " + url);
    }
}
