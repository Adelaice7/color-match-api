package com.rmeunier.colormatchapi.service.impl;

import com.rmeunier.colormatchapi.dao.ProductRepository;
import com.rmeunier.colormatchapi.model.GenderId;
import com.rmeunier.colormatchapi.model.Product;
import com.rmeunier.colormatchapi.service.IProductService;
import com.rmeunier.colormatchapi.utils.FileLoaderUtils;
import com.rmeunier.colormatchapi.utils.FilePathLoader;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

@Service
public class ProductService implements IProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileLoaderUtils loaderUtils;

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

    @Override
    public void importProductsFromFilePath(String filePath) {
        loaderUtils = new FilePathLoader();
        System.out.println(filePath);

        try (FileInputStream inputStream = (FileInputStream) loaderUtils.loadFile(filePath);
             Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {

            // skipping first line for data type row
            if (sc.hasNextLine()) {
                sc.nextLine();
            }

            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                System.out.println(line);
            }

            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException e) {
            System.err.println("Could not load file to import!");
            System.err.println(e.getMessage());
        }
    }
}
