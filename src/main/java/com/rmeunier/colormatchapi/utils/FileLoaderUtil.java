package com.rmeunier.colormatchapi.utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class FileLoaderUtil implements LoaderUtils {

    //TODO return line by line
    public void loadFile(File file) {
        try (LineIterator it = FileUtils.lineIterator(file, StandardCharsets.UTF_8.name())) {
            while (it.hasNext()) {
                String line = it.nextLine();
                System.out.println(line);
            }

        } catch (IOException e) {
            System.err.println("The file could not be opened.");
        }
    }
}
