package com.rmeunier.colormatchapi.config;

import com.rmeunier.colormatchapi.model.Product;
import com.rmeunier.colormatchapi.service.IProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;

public class ProductItemProcessor implements ItemProcessor<Product, Product> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductItemProcessor.class);

    @Autowired
    private IProductService productService;

    @Override
    public Product process(Product product) throws Exception {

        if (product.getDominantColor() != null) {
            return product;
        }

        int[] domColor = productService.findDominantColor(product);
//        LOGGER.info("dom color: {}", Arrays.toString(domColor));
        product.setDominantColor(domColor);

//        LOGGER.info("Product: {}", product.toString());
        //TODO fix not all products getting processed

        return product;
    }
}
