package com.rmeunier.colormatchapi.controller;

import com.rmeunier.colormatchapi.model.Product;
import com.rmeunier.colormatchapi.service.IProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductController {

    @Autowired
    private IProductService productService;

    @GetMapping("/products")
    public List<Product> getProducts() {
        return productService.findAll();
    }

    @PostMapping(value = "/importProducts", consumes = MediaType.TEXT_PLAIN_VALUE)
    public void importProducts(@RequestBody String csvFilePath) {
        productService.importProductsFromFilePath(csvFilePath);
    }

    @GetMapping("/getColor/{id}")
    public void getDominantColorForProduct(@PathVariable("id") String id) {
        Product product = productService.findById(id);
        productService.getDominantColor(product);
    }
}
