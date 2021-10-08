package com.rmeunier.colormatchapi.service;

import com.rmeunier.colormatchapi.exception.ResourceNotFoundException;
import com.rmeunier.colormatchapi.model.Product;

import java.util.List;

public interface IProductService {
    List<Product> findAll();
    Product findById(String id);
    void saveProduct(Product product);
    boolean saveProduct(String id, String title, String genderId,
                        String composition, String sleeve, String photo, String url);

    void importProductsFromFilePath(String file);

    int[] getDominantColor(Product product);
    int[] getDominantColor(String productId);

    String findDominantColor(Product product) throws ResourceNotFoundException;
    void findDominantColorForAllProducts();
}
