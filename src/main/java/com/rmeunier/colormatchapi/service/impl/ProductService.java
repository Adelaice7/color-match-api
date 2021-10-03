package com.rmeunier.colormatchapi.service.impl;

import com.rmeunier.colormatchapi.dao.ProductRepository;
import com.rmeunier.colormatchapi.exception.ProductNotFoundException;
import com.rmeunier.colormatchapi.model.GenderId;
import com.rmeunier.colormatchapi.model.Product;
import com.rmeunier.colormatchapi.service.IProductService;
import com.rmeunier.colormatchapi.utils.FileLoaderUtils;
import com.rmeunier.colormatchapi.utils.FilePathLoader;
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

import java.util.List;

@Service
public class ProductService implements IProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private FileLoaderUtils loaderUtils;

    @Value("${file.delim:,}")
    private String DELIM;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job importProductJob;

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
    //TODO batch processing
    @Override
    public void importProductsFromFilePath(String filePath) {
        LOGGER.info("Starting import of CSV file from filepath: {}...", filePath);

        loaderUtils = new FilePathLoader();

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

        if (parsedString.length <= 0) {
            return null;
        }

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
        product.setDominantColor(color);
        productRepository.save(product);
    }

    @Override
    public String getDominantColor(Product product) {
        String photo = product.getPhoto();

        String domColor = "bleu";
        addDomColorToDb(product, domColor);
        return domColor;
    }

}
