package com.rmeunier.colormatchapi.exception;

public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String id) {
        super("Product could not be found on id: " + id);
    }
}
