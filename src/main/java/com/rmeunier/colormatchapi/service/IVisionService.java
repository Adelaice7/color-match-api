package com.rmeunier.colormatchapi.service;

import com.rmeunier.colormatchapi.exception.ResourceNotFoundException;
import com.rmeunier.colormatchapi.model.Schema;

public interface IVisionService {
    int[] loadDominantColorForImage(String filePath, Schema schema) throws ResourceNotFoundException;
}
