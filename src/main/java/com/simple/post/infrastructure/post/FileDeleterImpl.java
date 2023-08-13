package com.simple.post.infrastructure.post;

import com.simple.post.domain.post.service.FileDeleter;
import com.simple.post.exception.NotFoundFileException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

@Component
public class FileDeleterImpl implements FileDeleter {

    @Override
    public void delete(String filePath) throws IOException {
        filePath = filePath.replaceFirst("^/", "");
        try {
            Files.delete(Path.of(filePath));
        } catch (NoSuchFileException ex) {
            throw new NotFoundFileException("Not Found File");
        }
    }
}