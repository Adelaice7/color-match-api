package com.rmeunier.colormatchapi.service.impl;

import com.rmeunier.colormatchapi.dao.ProductRepository;
import com.rmeunier.colormatchapi.model.GenderId;
import com.rmeunier.colormatchapi.model.Product;
import com.rmeunier.colormatchapi.service.IProductService;
import com.rmeunier.colormatchapi.utils.FileLoaderUtils;
import com.rmeunier.colormatchapi.utils.FilePathLoader;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Service
public class ProductService implements IProductService {

    private static Logger logger = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileLoaderUtils loaderUtils;

    @Value("${file.delim:,}")
    private static String DELIM;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(String id) {
        Optional<Product> prod = productRepository.findById(id);

        if (prod.isEmpty()) {
            return null;
        }
        return prod.get();
    }

    @Override
    public List<Product> findByName(String name) {
        return null;
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

    /**
     * Import products from a CSV file of a given file path.
     * As the processing of the file happens line by line using the Scanner,
     * it does not store the whole file in the memory, therefore memory usage will be limited.
     *
     * @param filePath the path to the products CSV file.
     */
    //TODO batch processing
    @Override
    public void importProductsFromFilePath(String filePath) {
        logger.info("Starting import of CSV file from filepath: {}...", filePath);

        loaderUtils = new FilePathLoader();

        try (FileInputStream inputStream = (FileInputStream) loaderUtils.loadFile(filePath);
             Scanner sc = new Scanner(inputStream, StandardCharsets.UTF_8)) {

            // skipping first line for header row
            if (sc.hasNextLine()) {
                sc.nextLine();
            }

            while (sc.hasNextLine()) {
                String line = sc.nextLine();

                Product product = parseStringIntoProduct(line);

                // skip product that could not be parsed
                if (product == null) {
                    //TODO which product?
                    logger.error("Could not parse product!");
                    continue;
                }

                saveProduct(product);
            }

            if (sc.ioException() != null) {
                throw sc.ioException();
            }
        } catch (IOException e) {
            logger.error("Could not load file to import! Error: {}", e.getMessage());
        }
    }

    /**
     * Parses a single line of String from the file into a Product object.
     *
     * @param str a single line of the CSV file
     * @return the Product object
     */
    private Product parseStringIntoProduct(String str) {
        if (str.length() <= 0) {
            return null;
        }

        String[] parsedString = str.split(DELIM);

        String id = parsedString[0];
        String title = parsedString[1];
        String genderId = parsedString[2];
        String composition = parsedString[3];
        String sleeve = parsedString[4];
        String path = parsedString[5];
        String url = parsedString[6];

        return new Product(id, title, GenderId.valueOf(genderId), composition, sleeve, path, url);
    }

    private void addDomColorToDb(Product product, String color) {

    }

    @Override
    public String getDominantColor(Product product) {
        return "blue";
    }

}
