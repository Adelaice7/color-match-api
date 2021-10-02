package com.rmeunier.colormatchapi.utils;

import java.io.IOException;
import java.io.InputStream;

public interface FileLoaderUtils {

    InputStream loadFile(String filePath) throws IOException;
}
