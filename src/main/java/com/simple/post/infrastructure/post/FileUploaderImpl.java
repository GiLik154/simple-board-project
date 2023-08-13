package com.simple.post.infrastructure.post;

import com.simple.post.domain.post.service.FileUploader;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class FileUploaderImpl implements FileUploader { // DiskFileUploader implements FileUploader

    @Override
    public String upload(String company, File file) throws IOException {
        String fileExtension = FilenameUtils.getExtension(file.getName());
        String fileName = StringUtils.cleanPath(creatFileName()) + "." + fileExtension;
        String uploadDir = "files/" + company + "/";
        Path uploadPath = Paths.get(uploadDir);

        createDirectoryIfNotExist(uploadPath);

        createFile(file, fileName, uploadPath);

        return "/" + uploadDir + fileName;
    }

    private String creatFileName() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSSSSSSSS");
        return now.format(formatter);
    }

    private void createDirectoryIfNotExist(Path uploadPath) throws IOException {
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }

    private void createFile(File file, String fileName, Path uploadPath) throws IOException {
        try (InputStream inputStream = new FileInputStream(file)) {
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);

            file.delete();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}