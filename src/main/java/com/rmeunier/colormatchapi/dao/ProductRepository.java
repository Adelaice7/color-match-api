package com.rmeunier.colormatchapi.dao;

import com.rmeunier.colormatchapi.model.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends CrudRepository<Product, String> {

    List<Product> findAll();
    Product findByName(String productName);
}
