package com.rmeunier.colormatchapi.service;

import com.rmeunier.colormatchapi.model.Product;

import java.util.List;

public interface IProductService {
    List<Product> findAll();
    void saveProduct(Product product);
    boolean saveProduct(String id, String title, String genderId,
                        String composition, String sleeve, String photo, String url);
}
