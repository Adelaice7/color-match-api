package com.rmeunier.colormatchapi.utils;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Component
@Qualifier("filePathLoader")
public class FilePathLoader implements FileLoaderUtils {

    private File file;

    private void setFile(String filePath) {
        this.file = new File(filePath);
    }

    @Override
    public InputStream loadFile(String filePath) throws IOException {
        setFile(filePath);
        return new FileInputStream(this.file.getPath());
    }
}
