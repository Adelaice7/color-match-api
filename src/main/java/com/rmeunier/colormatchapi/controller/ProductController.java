package com.rmeunier.colormatchapi.controller;

import com.rmeunier.colormatchapi.exception.ResourceNotFoundException;
import com.rmeunier.colormatchapi.model.Product;
import com.rmeunier.colormatchapi.service.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private IProductService productService;

    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable("id") String id) {
        return productService.findById(id);
    }

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productService.findAll();
    }

    /**
     * Imports all products from a CSV file of given file path.
     * RequestBody has to be a plain text value of the absolute file path.
     * @param csvFilePath the absolute path String for the CSV file to be imported.
     */
    @PostMapping(value = "/importProducts", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void importProducts(@RequestBody String csvFilePath) {
        productService.importProductsFromFilePath(csvFilePath);
    }

    /**
     * Retrieves the dominant color from the database for a product.
     *
     * @param id the product id.
     * @return the dominant color vector in a String.
     */
    @GetMapping("/getColor/{id}")
    public String getDominantColorForProduct(@PathVariable("id") String id) {
        int[] domColor = productService.getDominantColor(id);
        return Arrays.toString(domColor);
    }

    /**
     * Loads the dominant color using the Google Vision API for a single product of id.
     * It saves the dominant color in the database for a given product.
     *
     * @param id the product's ID for the dominant color to be added to
     * @return the dominant color vector in a String
     */
    @PostMapping("/loadColor/{id}")
    public String loadDominantColorForProduct(@PathVariable("id") String id) {
        Product product = productService.findById(id);
        try {
            int[] domColor = productService.findDominantColor(product);
            return Arrays.toString(domColor);
        } catch (ResourceNotFoundException e) {
            LOGGER.error("An error occurred trying to get the dominant color of product: {}", id);
            LOGGER.error("Error message: {}", e.getMessage());
        }

        return null;
    }

    /**
     * Loads dominant colors for all products in database.
     */
    @PostMapping(value = "/loadColorForAllProducts")
    public void loadDominantColorForAllProducts() {
        productService.findDominantColorForAllProducts();
    }

    @PostMapping("/getProductsOfColor")
    public void getProductsOfColor(@PathVariable String id, int n) {
        LOGGER.info("This is good.");
    }
}
