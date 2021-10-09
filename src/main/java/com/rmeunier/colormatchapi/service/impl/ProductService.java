package com.rmeunier.colormatchapi.service.impl;

import com.rmeunier.colormatchapi.dao.ProductRepository;
import com.rmeunier.colormatchapi.exception.ProductNotFoundException;
import com.rmeunier.colormatchapi.exception.ResourceNotFoundException;
import com.rmeunier.colormatchapi.model.GenderId;
import com.rmeunier.colormatchapi.model.Product;
import com.rmeunier.colormatchapi.model.Schema;
import com.rmeunier.colormatchapi.service.IProductService;
import com.rmeunier.colormatchapi.service.IVisionService;
import org.apache.commons.lang3.EnumUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductService implements IProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    @Value("${file.delim:,}")
    private String DELIM;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private IVisionService visionService;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importProductJob;

    @Autowired
    private Job domColorJob;

    @Override
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Override
    public Product findById(String id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
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
    @Override
    public void importProductsFromFilePath(String filePath) {
        LOGGER.info("Starting import of CSV file from filepath: {}...", filePath);

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("batchJobId", System.currentTimeMillis())
                    .addString("filePath", filePath)
                    .toJobParameters();
            jobLauncher.run(importProductJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            LOGGER.error("Batch import could not be started! Error: {}", e.getMessage());
        }
    }

    /**
     * Finds the dominant color of a provided product and parses it into an int array.
     *
     * @param product the Product to find the dominant color of.
     * @return the dominant color's RGB vector
     */
    public int[] getDominantColor(Product product) {
        return product.getDominantColor();
    }

    /**
     * Finds a product based on provided productId and then finds the dominant color of this product.
     *
     * @param productId the ID of the product for finding its dominant color
     * @return the dominant color's RGB vector
     */
    public int[] getDominantColor(String productId) {
        Product product = findById(productId);
        return getDominantColor(product);
    }

    /**
     * Saves the RGB color vector for the dominant color of a product.
     *
     * @param product the product to save
     * @param color   the dominant color vector to save
     */
    private void addDomColorToDb(Product product, int[] color) {
        product.setDominantColor(color);
        productRepository.save(product);
    }

    /**
     * Get dominant color for a single product.
     * Checks first if dominant color exists first for current product.
     *
     * @param product the product to get the color for
     * @return the String value of the product's dominant color
     */
    @Override
    public int[] findDominantColor(Product product) throws ResourceNotFoundException {
        if (domColorExists(product)) {
            LOGGER.info("Dominant color already exists for this product: {}", product.getId());
            return product.getDominantColor();
        } else {
            LOGGER.info("Starting Vision API to find dominant color for product: {}", product.getId());
            String photoPath = product.getPhoto();
            return visionService.loadDominantColorForImage(photoPath, Schema.HTTPS);
        }
    }

    public void findDominantColorForAllProducts() {
        LOGGER.info("Starting to load dominant colors for all products...");

        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("domColorBatchJobId", System.currentTimeMillis())
                    .toJobParameters();
            jobLauncher.run(domColorJob, jobParameters);
        } catch (JobExecutionAlreadyRunningException | JobRestartException
                | JobInstanceAlreadyCompleteException | JobParametersInvalidException e) {
            LOGGER.error("Batch processing dominant colors could not be started! Error: {}", e.getMessage());
        }
    }

    /**
     * Checks if dominant color is stored in the database or not.
     * It is null if it does not exist.
     *
     * @param product the product to check for
     * @return true if exists, false if null
     */
    private boolean domColorExists(Product product) {
        return product.getDominantColor() != null;
    }

}
