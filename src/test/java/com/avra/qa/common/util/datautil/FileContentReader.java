package com.avra.qa.common.util.datautil;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;

@Component
public class FileContentReader {

    public String getFileFromPath(String path) {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(Objects.requireNonNull(classLoader.getResource(path)).getPath());
        return file.getPath();
    }

    public String getFileContent(String classpathFileLocation) throws IOException {
        Resource contentFile = new ClassPathResource(classpathFileLocation);
        return FileCopyUtils.copyToString(new FileReader(contentFile.getFile()));
    }

    public byte[] getPdfFileContent(String classpathFileLocation) throws IOException {
        Resource contentFile = new ClassPathResource(classpathFileLocation);
        return FileCopyUtils.copyToByteArray(contentFile.getFile());
    }
}
