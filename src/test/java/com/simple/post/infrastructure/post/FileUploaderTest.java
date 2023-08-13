package com.simple.post.infrastructure.post;

import com.simple.post.domain.post.service.FileUploader;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FileUploaderTest {
    private final FileUploader fileUploader;

    @Autowired
    public FileUploaderTest(FileUploader fileUploader) {
        this.fileUploader = fileUploader;
    }

    @AfterEach
    public void deleteFile() throws IOException {
        String path = "files/testCompany/";
        File folder = new File(path);

        Path uploadPath = Paths.get(path);
        Files.createDirectories(uploadPath);
        File[] deleteFolderList = folder.listFiles();

        for (File f : deleteFolderList) {
            f.delete();
        }

        folder.delete();
    }

    @Test
    void 파일_업로드_정상작동() throws IOException {
        File file = new File("test.jpg");
        file.createNewFile();

        String path = "files/testCompany/";
        File savedFileFolder = new File(path);

        fileUploader.upload("testCompany", file);

        assertEquals(1, savedFileFolder.listFiles().length);
    }
}