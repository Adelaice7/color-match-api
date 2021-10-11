package com.rmeunier.colormatchapi.controller;

import com.rmeunier.colormatchapi.exception.ColorMissingException;
import com.rmeunier.colormatchapi.exception.ResourceNotFoundException;
import com.rmeunier.colormatchapi.model.Product;
import com.rmeunier.colormatchapi.service.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private IProductService productService;

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productService.findAll();
    }

    @GetMapping("/products/{id}")
    public Product getProduct(@PathVariable("id") String id) {
        return productService.findById(id);
    }

    /**
     * Imports all products from a CSV file of given file path.
     * RequestBody has to be a plain text value of the absolute file path.
     * Upon the Batch Job finishing, the notification listener will return a message.
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
    public int[] getDominantColorForProduct(@PathVariable("id") String id) {
        return productService.getDominantColor(id);
    }

    /**
     * Loads the dominant color using the Google Vision API for a single product of id.
     * It saves the dominant color in the database for a given product.
     *
     * @param id the product's ID for the dominant color to be added to
     * @return the dominant color vector in a String
     */
    @PostMapping("/loadColor/{id}")
    public int[] loadDominantColorForProduct(@PathVariable("id") String id) {
        Product product = productService.findById(id);
        try {
            return productService.findDominantColorAndSave(product);
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

    /**
     * Retrieves an n-element list of products that have the closest color to reference product provided in id.
     * @param id the product ID to reference the color on
     * @param n the number of products to retrieve
     * @return a list of filtered list of Products
     */
    @PostMapping("/getProductsOfColor/{id}/{n}")
    public List<Product> getProductsOfColor(@PathVariable("id") String id, @PathVariable("n") int n) {
        LOGGER.info("Getting products that have a color like product: {}", id);

        Product product = productService.findById(id);

        if (product == null || n <= 0) {
            return new ArrayList<>();
        }

        List<Product> products = new ArrayList<>();

        try {
            products = productService.getProductsOfColorLike(product, n);
        } catch (ColorMissingException e) {
            LOGGER.error("Error during retrieving products of color like product {}", id);
            LOGGER.error("Error message: {}", e.getMessage());
        }

        return products;
    }
}
