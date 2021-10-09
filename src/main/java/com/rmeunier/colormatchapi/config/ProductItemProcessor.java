package com.rmeunier.colormatchapi.config;

import com.rmeunier.colormatchapi.model.Product;
import com.rmeunier.colormatchapi.service.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

public class ProductItemProcessor implements ItemProcessor<Product, Product> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductItemProcessor.class);

    @Autowired
    private IProductService productService;

    @Override
    public Product process(Product product) {
        // filtering existing dominant color records to be skipped
        if (product.getDominantColor() != null) {
            return null;
        }

        int[] domColor = productService.findDominantColor(product);
        product.setDominantColor(domColor);

        return product;
    }
}
