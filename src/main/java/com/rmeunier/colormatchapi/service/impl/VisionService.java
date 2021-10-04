package com.rmeunier.colormatchapi.service.impl;

import com.google.cloud.vision.v1.*;
import com.google.type.Color;
import com.rmeunier.colormatchapi.service.IVisionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.vision.CloudVisionTemplate;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VisionService implements IVisionService {

    @Autowired
    private CloudVisionTemplate cloudVisionTemplate;

    @Autowired
    private ResourceLoader resourceLoader;

    private static final Logger LOGGER = LoggerFactory.getLogger(VisionService.class);

    public String checkImageForColor(String filePath) {
        Resource imgResource = openFileForProcessing(filePath);
//        AnnotateImageResponse response = this.cloudVisionTemplate
//                .analyzeImage(imgResource, Feature.Type.IMAGE_PROPERTIES);
//
//        ImageProperties imageProperties = response.getImagePropertiesAnnotation();
//        Color dominantColor = getDominantColors(imageProperties);

        //TODO convert dominant color to L*a*b

        return "red";
    }

    private Resource openFileForProcessing(String filePath) {
        return this.resourceLoader.getResource(filePath);
    }

    private Color getDominantColors(ImageProperties imageProperties) {
        DominantColorsAnnotation colors = imageProperties.getDominantColors();
        List<ColorInfo> colorInfos = colors.getColorsList();

        if (colorInfos.size() <= 0) {
            LOGGER.error("Could not obtain any colors from this image!");
            return null;
        }

        return colorInfos.get(0).getColor();
    }
}
