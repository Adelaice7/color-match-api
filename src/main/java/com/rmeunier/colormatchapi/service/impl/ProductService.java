package com.rmeunier.colormatchapi.service.impl;

import com.rmeunier.colormatchapi.dao.ProductRepository;
import com.rmeunier.colormatchapi.model.GenderId;
import com.rmeunier.colormatchapi.model.Product;
import com.rmeunier.colormatchapi.service.IProductService;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public void saveProduct(Product product) {
        productRepository.save(product);
    }

    @Override
    public boolean saveProduct(String id, String title, String genderId, String composition, String sleeve, String photo, String url) {
        boolean isGenderIdValid = EnumUtils.isValidEnum(GenderId.class, genderId);

        if (!isGenderIdValid) {
            return false;
        }

        Product product = new Product(id, title, GenderId.valueOf(genderId), composition, sleeve, photo, url);

        saveProduct(product);

        return true;
    }
}
